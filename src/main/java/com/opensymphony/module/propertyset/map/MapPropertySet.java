/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.module.propertyset.map;

import java.util.*;

import com.opensymphony.module.propertyset.AbstractPropertySet;
import com.opensymphony.module.propertyset.PropertyException;

/**
 * The MapPropertySet is an UNTYPED PropertySet implementation that
 * acts as a wrapper around a standard {@link java.util.Map} .
 * <p/>
 * <p>Because Map's will only store the value but not the type, this
 * is untyped. See {@link com.opensymphony.module.propertyset.PropertySet}
 * for explanation.</p>
 * <p/>
 * <b>Optional Args</b>
 * <ul>
 * <li><b>map</b> - the map that will back this PropertySet</li>
 * </ul>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 144 $
 * @see com.opensymphony.module.propertyset.PropertySet
 */
public class MapPropertySet extends AbstractPropertySet {
  //~ Instance fields ////////////////////////////////////////////////////////

  /**
   * Underlying Map storing properties.
   */
  protected Map<String, Object> map;

  //~ Methods ////////////////////////////////////////////////////////////////

  /**
   * The type parameter is ignored.
   */
  public synchronized Collection<String> getKeys(String prefix, int type) {
    Iterator<String> keys = map.keySet().iterator();
    List<String> result = new ArrayList<String>();

    while(keys.hasNext()) {
      String key = keys.next();

      if((prefix == null) || key.startsWith(prefix)) {
        result.add(key);
      }
    }

    Collections.sort(result);

    return result;
  }

  /**
   * Set underlying map.
   */
  public synchronized void setMap(Map<String, Object> map) {
    if(map == null) {
      throw new NullPointerException("Map cannot be null.");
    }

    this.map = map;
  }

  /**
   * Retrieve underlying map.
   */
  public synchronized Map<String, Object> getMap() {
    return map;
  }

  /**
   * This is an untyped PropertySet implementation so this method will always
   * throw {@link java.lang.UnsupportedOperationException} .
   */
  public int getType(String key) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("PropertySet does not support types");
  }

  public synchronized boolean exists(String key) {
    return map.containsKey(key);
  }

  public void init(Map<String, String> config, Map<String, Object> args) {
    map = (Map<String, Object>)args.get("map");

    if(map == null) {
      map = new HashMap<String, Object>();
    }
  }

  public synchronized void remove(String key) {
    map.remove(key);
  }

  public void remove() throws PropertyException {
    map.clear();
  }

  /**
   * Returns false.
   */
  public boolean supportsType(int type) {
    return false;
  }

  /**
   * Returns false.
   */
  public boolean supportsTypes() {
    return false;
  }

  /**
   * The type parameter is ignored.
   */
  protected synchronized void setImpl(int type, String key, Object value) {
    map.put(key, value);
  }

  /**
   * The type parameter is ignored.
   */
  protected synchronized Object get(int type, String key) {
    return exists(key) ? map.get(key) : null;
  }
}
