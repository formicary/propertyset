/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.module.propertyset;

import java.util.*;

/**
 * Base implementation of PropertySet.
 * <p/>
 * <p>Performs necessary casting for get???/set??? methods which wrap around the
 * following 2 methods which are declared <code>protected abstract</code> and need
 * to be implemented by subclasses:</p>
 * <p/>
 * <ul>
 * <li> {@link #get(int, java.lang.String)} </li>
 * <li> {@link #setImpl(int, java.lang.String, java.lang.Object)} </li>
 * </ul>
 * <p/>
 * <p>The following methods are declared <code>public abstract</code> and are the
 * remainder of the methods that need to be implemented at the very least:</p>
 * <p/>
 * <ul>
 * <li> {@link #exists(java.lang.String)} </li>
 * <li> {@link #remove(java.lang.String)} </li>
 * <li> {@link #getType(java.lang.String)} </li>
 * <li> {@link #getKeys(java.lang.String, int)} </li>
 * </ul>
 * <p/>
 * <p>The <code>supports???</code> methods are implemented and all return true by default.
 * Override if necessary.</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @author <a href="mailto:hani@fate.demon.co.uk">Hani Suleiman</a>
 * @version $Revision: 151 $
 */
public abstract class AbstractPropertySet implements PropertySet {
  //~ Methods ////////////////////////////////////////////////////////////////

  public void setAsActualType(String key, Object value) throws PropertyException {
    int type;

    if(value instanceof Boolean) {
      type = BOOLEAN;
    } else if(value instanceof Integer) {
      type = INT;
    } else if(value instanceof Long) {
      type = LONG;
    } else if(value instanceof Double) {
      type = DOUBLE;
    } else if(value instanceof String) {
      if(value.toString().length() > 255) {
        type = TEXT;
      } else {
        type = STRING;
      }
    } else if(value instanceof Date) {
      type = DATE;
    } else {
      type = OBJECT;
    }

    set(type, key, value);
  }

  public Object getAsActualType(String key) throws PropertyException {
    int type = getType(key);
    Object value = null;

    switch(type) {
      case BOOLEAN:
        value = getBoolean(key);

        break;

      case INT:
        value = getInt(key);

        break;

      case LONG:
        value = getLong(key);

        break;

      case DOUBLE:
        value = getDouble(key);

        break;

      case STRING:
        value = getString(key);

        break;

      case TEXT:
        value = getText(key);

        break;

      case DATE:
        value = getDate(key);

        break;

      case OBJECT:
        value = getObject(key);

        break;
    }

    return value;
  }

  public void setBoolean(String key, boolean value) {
    set(BOOLEAN, key, value ? Boolean.TRUE : Boolean.FALSE);
  }

  public boolean getBoolean(String key) {
    try {
      return (Boolean)get(BOOLEAN, key);
    } catch(NullPointerException e) {
      return false;
    }
  }

  public void setDate(String key, Date value) {
    set(DATE, key, value);
  }

  public Date getDate(String key) {
    try {
      return (Date)get(DATE, key);
    } catch(NullPointerException e) {
      return null;
    }
  }

  public void setDouble(String key, double value) {
    set(DOUBLE, key, value);
  }

  public double getDouble(String key) {
    try {
      return (Double)get(DOUBLE, key);
    } catch(NullPointerException e) {
      return 0.0;
    }
  }

  public void setInt(String key, int value) {
    set(INT, key, value);
  }

  public int getInt(String key) {
    try {
      return (Integer)get(INT, key);
    } catch(NullPointerException e) {
      return 0;
    }
  }

  /**
   * Calls <code>getKeys(null,0)</code>
   */
  public Collection<String> getKeys() throws PropertyException {
    return getKeys(null, 0);
  }

  /**
   * Calls <code>getKeys(null,type)</code>
   */
  public Collection<String> getKeys(int type) throws PropertyException {
    return getKeys(null, type);
  }

  /**
   * Calls <code>getKeys(prefix,0)</code>
   */
  public Collection<String> getKeys(String prefix) throws PropertyException {
    return getKeys(prefix, 0);
  }

  public void setLong(String key, long value) {
    set(LONG, key, value);
  }

  public long getLong(String key) {
    try {
      return (Long)get(LONG, key);
    } catch(NullPointerException e) {
      return 0L;
    }
  }

  public void setObject(String key, Object value) {
    set(OBJECT, key, value);
  }

  public Object getObject(String key) {
    try {
      return get(OBJECT, key);
    } catch(NullPointerException e) {
      return null;
    }
  }

  /**
   * Returns true.
   */
  public boolean isSettable(String property) {
    return true;
  }

  /**
   * Throws IllegalPropertyException if value length greater than 255.
   */
  public void setString(String key, String value) {
    if((value != null) && (value.length() > 255)) {
      throw new IllegalPropertyException("String exceeds 255 characters.");
    }

    set(STRING, key, value);
  }

  public String getString(String key) {
    try {
      return (String)get(STRING, key);
    } catch(NullPointerException e) {
      return null;
    }
  }

  public void setText(String key, String value) {
    set(TEXT, key, value);
  }

  public String getText(String key) {
    try {
      return (String)get(TEXT, key);
    } catch(NullPointerException e) {
      return null;
    }
  }

  public void init(Map<String, String> config, Map<String, Object> args) {
    // nothing
  }

  /**
   * Returns true.
   */
  public boolean supportsType(int type) {
    return true;
  }

  /**
   * Returns true.
   */
  public boolean supportsTypes() {
    return true;
  }

  /**
   * Simple human readable representation of contents of PropertySet.
   */
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append(getClass().getName());
    result.append(" {\n");

    try {

      for(String key : getKeys()) {
        int type = getType(key);

        if(type > 0) {
          result.append('\t');
          result.append(key);
          result.append(" = ");
          result.append(get(type, key));
          result.append('\n');
        }
      }
    } catch(PropertyException e) {
      // toString should never throw an exception.
    }

    result.append("}\n");

    return result.toString();
  }

  protected abstract void setImpl(int type, String key, Object value) throws PropertyException;

  protected abstract Object get(int type, String key) throws PropertyException;

  protected String type(int type) {
    switch(type) {
      case PropertySet.BOOLEAN:
        return "boolean";

      case PropertySet.INT:
        return "int";

      case PropertySet.LONG:
        return "long";

      case PropertySet.DOUBLE:
        return "double";

      case PropertySet.STRING:
        return "string";

      case PropertySet.TEXT:
        return "text";

      case PropertySet.DATE:
        return "date";

      case PropertySet.OBJECT:
        return "object";

      default:
        return null;
    }
  }

  protected int type(String type) {
    if(type == null) {
      return 0;
    }

    type = type.toLowerCase();

    if(type.equals("boolean")) {
      return PropertySet.BOOLEAN;
    }

    if(type.equals("int")) {
      return PropertySet.INT;
    }

    if(type.equals("long")) {
      return PropertySet.LONG;
    }

    if(type.equals("double")) {
      return PropertySet.DOUBLE;
    }

    if(type.equals("string")) {
      return PropertySet.STRING;
    }

    if(type.equals("text")) {
      return PropertySet.TEXT;
    }

    if(type.equals("date")) {
      return PropertySet.DATE;
    }

    if(type.equals("object")) {
      return PropertySet.OBJECT;
    }

    return 0;
  }

  private void set(int type, String key, Object value) throws PropertyException {

    //we're ok this far, so call the actual setter.
    setImpl(type, key, value);
  }
}
