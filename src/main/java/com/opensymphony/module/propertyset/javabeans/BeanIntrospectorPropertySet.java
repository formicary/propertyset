/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.module.propertyset.javabeans;

import java.beans.*;
import java.util.*;

import com.opensymphony.module.propertyset.*;

/**
 * PropertySet wrapper for any javabean.
 * Dynamically looks up all bean properties (those exposed by get/setXXX) and invokes
 * them on the getXXX/setXXX propertyset methods.
 * <p/>
 * <p/>
 * <b>Required Args</b>
 * <ul>
 * <li><b>bean</b> - any Object that can be introspected</li>
 * </ul>
 * Date: Dec 16, 2001
 * Time: 6:20:00 PM
 *
 * @author Hani Suleiman
 */
public class BeanIntrospectorPropertySet extends AbstractPropertySet {
  //~ Instance fields ////////////////////////////////////////////////////////

  private Map<String, PropertyDescriptor> descriptors = new HashMap<String, PropertyDescriptor>();
  private Object bean = null;

  //~ Methods ////////////////////////////////////////////////////////////////

  public void setBean(Object bean) throws PropertyImplementationException {
    this.bean = bean;

    try {
      BeanInfo info = Introspector.getBeanInfo(bean.getClass());
      PropertyDescriptor[] beanDescriptors = info.getPropertyDescriptors();

      for(PropertyDescriptor beanDescriptor : beanDescriptors) {
        descriptors.put(beanDescriptor.getName(), beanDescriptor);
      }
    } catch(IntrospectionException e) {
      throw new PropertyImplementationException("Object is not a bean", e);
    }
  }

  public Collection<String> getKeys(String prefix, int type) throws PropertyException {
    Collection<String> keys = new ArrayList<String>();

    for(PropertyDescriptor descriptor : descriptors.values()) {
      if(((prefix == null) || descriptor.getName().startsWith(prefix)) && ((type == 0) || (getType(descriptor.getName()) == type))) {
        keys.add(descriptor.getName());
      }
    }

    return keys;
  }

  public boolean isSettable(String property) {
    PropertyDescriptor descriptor = descriptors.get(property);

    return (descriptor != null) && (descriptor.getWriteMethod() != null);
  }

  public int getType(String key) throws PropertyException {
    PropertyDescriptor descriptor = descriptors.get(key);

    if(descriptor == null) {
      throw new PropertyException("No key " + key + " found");
    }

    Class c = descriptor.getPropertyType();

    if((c == Integer.TYPE) || (c == Integer.class)) {
      return PropertySet.INT;
    }

    if((c == Long.TYPE) || (c == Long.class)) {
      return PropertySet.LONG;
    }

    if((c == Double.TYPE) || (c == Double.class)) {
      return PropertySet.DOUBLE;
    }

    //XXX Shouldn't this be TEXT?
    if(c == String.class) {
      return PropertySet.STRING;
    }

    if((c == Boolean.TYPE) || (c == Boolean.class)) {
      return PropertySet.BOOLEAN;
    }

    if(java.util.Date.class.isAssignableFrom(c)) {
      return PropertySet.DATE;
    }

    return PropertySet.OBJECT;
  }

  public boolean exists(String key) throws PropertyException {
    return descriptors.get(key) != null;
  }

  public void init(Map<String, String> config, Map<String, Object> args) {
    Object bean = args.get("bean");
    setBean(bean);
  }

  public void remove() throws PropertyException {
    //no-op, doesn't make sense to remove bean properties
  }

  public void remove(String key) throws PropertyException {
    throw new PropertyImplementationException("Remove not supported in BeanIntrospectorPropertySet, use setXXX(null) instead");
  }

  protected void setImpl(int type, String key, Object value) throws PropertyException {
    if(getType(key) != type) {
      throw new InvalidPropertyTypeException(key + " is not of type " + type);
    }

    PropertyDescriptor descriptor = descriptors.get(key);

    try {
      descriptor.getWriteMethod().invoke(bean, value);
    }
    //pretty lame way of doing this, but I'm lazy
    catch(NullPointerException ex) {
      throw new PropertyImplementationException("Property " + key + " is read-only");
    } catch(Exception ex) {
      throw new PropertyImplementationException("Cannot invoke write method for key " + key, ex);
    }
  }

  protected Object get(int type, String key) throws PropertyException {
    if(getType(key) != type) {
      throw new InvalidPropertyTypeException(key + " is not of type " + type);
    }

    PropertyDescriptor descriptor = descriptors.get(key);

    try {

      return descriptor.getReadMethod().invoke(bean);
    }
    //pretty lame way of doing this, but I'm lazy
    catch(NullPointerException ex) {
      throw new PropertyImplementationException("Property " + key + " is write-only");
    } catch(Exception ex) {
      throw new PropertyImplementationException("Cannot invoke read method for key " + key, ex);
    }
  }
}
