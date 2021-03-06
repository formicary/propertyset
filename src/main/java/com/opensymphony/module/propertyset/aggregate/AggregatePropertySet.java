/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.module.propertyset.aggregate;

import java.io.Serializable;
import java.util.*;

import com.opensymphony.module.propertyset.AbstractPropertySet;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertySet;

/**
 * PropertySet composed of a collection of other propertysets.
 * Tried each of the propertysets to find a value, tries to be
 * as fault tolerant as possible, in that when any error occurs,
 * it simply tries the operation on the next set.
 * <p/>
 * <p/>
 * <b>Optional Args</b>
 * <ul>
 * <li><b>PropertySets</b> - a List of PropertySet</li>
 * </ul>
 * <p/>
 * Date: Dec 16, 2001
 * Time: 11:28:06 PM
 *
 * @author Hani Suleiman
 */
public class AggregatePropertySet extends AbstractPropertySet implements Serializable {

  private List<PropertySet> propertySets;

  //~ Methods ////////////////////////////////////////////////////////////////

  public Collection<String> getKeys(String prefix, int type) throws PropertyException {
    Iterator i = propertySets.iterator();
    Collection<String> keys = new ArrayList<String>();

    while(i.hasNext()) {
      PropertySet set = (PropertySet)i.next();

      try {
        keys.addAll(set.getKeys(prefix, type));
      } catch(PropertyException ex) {
        //we don't really care about these here
      }
    }

    return keys;
  }

  public boolean isSettable(String property) {

    for(PropertySet set : propertySets) {
      if(set.isSettable(property)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks all propertysets for the specified property.
   * If a match is found, the type for the match is returned.
   * Note that the first match is what is checked,
   * other propertysets might also define this key, but
   * they would not be checked.
   */
  public int getType(String key) throws PropertyException {

    for(PropertySet set : propertySets) {
      try {
        return set.getType(key);
      } catch(PropertyException ex) {
        //we don't really care about these
      }
    }

    throw new PropertyException("No key " + key + " found");
  }

  public void addPropertySet(PropertySet propertySet) {
    propertySets.add(propertySet);
  }

  public boolean exists(String key) throws PropertyException {

    for(PropertySet set : propertySets) {
      try {
        if(set.exists(key)) {
          return true;
        }
      } catch(PropertyException ex) {
      }
    }

    return false;
  }

  public void init(Map<String, String> config, Map<String, Object> args) {
    // TODO document configuration
    propertySets = (List<PropertySet>)args.get("PropertySets");

    if(propertySets == null) {
      propertySets = new ArrayList<PropertySet>();
    }
  }

  public void remove() throws PropertyException {
    if(propertySets != null) {

      for(Object propertySet : propertySets) {
        PropertySet ps = (PropertySet)propertySet;
        ps.remove();
      }
    }
  }

  public void remove(String key) throws PropertyException {

    for(PropertySet set : propertySets) {
      try {
        set.remove(key);
      } catch(PropertyException ex) {
        //we don't really care about these
      }
    }
  }

  /**
   * Attempts to set a property in one of the propertysets.
   * Note that this method returns at the FIRST successful set call,
   * rather than setting the same property on all the propertysets.
   */
  protected void setImpl(int type, String key, Object value) throws PropertyException {

    for(PropertySet set : propertySets) {
      try {
        if(set.isSettable(key)) {
          switch(type) {
            case BOOLEAN:
              set.setBoolean(key, (Boolean)value);

              return;

            case INT:
              set.setInt(key, ((Number)value).intValue());

              return;

            case LONG:
              set.setLong(key, ((Number)value).longValue());

              return;

            case DOUBLE:
              set.setDouble(key, ((Number)value).doubleValue());

              return;

            case STRING:
              set.setString(key, (String)value);

              return;

            case TEXT:
              set.setText(key, (String)value);

              return;

            case DATE:
              set.setDate(key, (Date)value);

              return;

            case OBJECT:
              set.setObject(key, value);

              return;

          }
        }
      } catch(PropertyException ex) {
        //we don't care about these here, sadly
      }
    }
  }

  protected Object get(int type, String key) throws PropertyException {

    for(PropertySet set : propertySets) {
      try {
        //poo, since set.get() is protected, we have to double back
        //on ourselves and call getXXX(), which in turn will call get
        switch(type) {
          case BOOLEAN:

            boolean bool = set.getBoolean(key);

            if(bool) {
              return Boolean.TRUE;
            }

            //If we have false, we need to check if it's missing
            //property or if it's actually false
            if(set.exists(key)) {
              return Boolean.FALSE;
            }

            break;

          case INT:

            int maybeInt = set.getInt(key);

            if(maybeInt != 0) {
              return maybeInt;
            }

            break;

          case LONG:

            long maybeLong = set.getLong(key);

            if(maybeLong != 0) {
              return maybeLong;
            }

            break;

          case DOUBLE:

            double maybeDouble = set.getDouble(key);

            if(maybeDouble != 0) {
              return maybeDouble;
            }

            break;

          case STRING:

            String string = set.getString(key);

            if(string != null) {
              return string;
            }

            break;

          case TEXT:

            String text = set.getText(key);

            if(text != null) {
              return text;
            }

            break;

          case DATE:

            Date date = set.getDate(key);

            if(date != null) {
              return date;
            }

            break;

          case OBJECT:

            Object obj = set.getObject(key);

            if(obj != null) {
              return obj;
            }

            break;

        }
      } catch(PropertyException ex) {
        //we don't really care about these here
      }
    }

    return null;
  }
}
