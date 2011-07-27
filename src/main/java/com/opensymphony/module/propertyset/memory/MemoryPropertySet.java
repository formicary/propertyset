/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.module.propertyset.memory;

import com.opensymphony.module.propertyset.*;

import java.io.Serializable;

import java.util.*;


/**
 * The MemoryPropertySet is a PropertySet implementation that
 * will store any primitive or object in an internal Map
 * that is stored in memory.
 *
 * <p>An alternative to MemoryPropertySet is SerializablePropertySet
 * which can be Serialized to/from a stream.</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 144 $
 *
 * @see com.opensymphony.module.propertyset.PropertySet
 */
public class MemoryPropertySet extends AbstractPropertySet {
    //~ Instance fields ////////////////////////////////////////////////////////

    private HashMap<String, Object> map;

    //~ Methods ////////////////////////////////////////////////////////////////

    public synchronized Collection<String> getKeys(String prefix, int type) {
        Iterator keys = getMap().keySet().iterator();
        List<String> result = new ArrayList<String>();

        while (keys.hasNext()) {
            String key = (String) keys.next();

            if ((prefix == null) || key.startsWith(prefix)) {
                if (type == 0) {
                    result.add(key);
                } else {
                    ValueEntry v = (ValueEntry) getMap().get(key);

                    if (v.type == type) {
                        result.add(key);
                    }
                }
            }
        }

        Collections.sort(result);

        return result;
    }

    public synchronized int getType(String key) {
        if (getMap().containsKey(key)) {
            return ((ValueEntry) getMap().get(key)).type;
        } else {
            return 0;
        }
    }

    public synchronized boolean exists(String key) {
        return getType(key) > 0;
    }

  public void init(Map<String, String> config, Map<String, Object> args) {
        map = new HashMap<String, Object>();
    }

    public synchronized void remove(String key) {
        getMap().remove(key);
    }

    public void remove() throws PropertyException {
        map.clear();
    }

    protected synchronized void setImpl(int type, String key, Object value) throws DuplicatePropertyKeyException {
        if (exists(key)) {
            ValueEntry v = (ValueEntry) getMap().get(key);

            if (v.type != type) {
                throw new DuplicatePropertyKeyException();
            }

            v.value = value;
        } else {
            getMap().put(key, new ValueEntry(type, value));
        }

    }

    protected Map<String, Object> getMap() {
        return map;
    }

    protected synchronized Object get(int type, String key) throws InvalidPropertyTypeException {
        if (exists(key)) {
            ValueEntry v = (ValueEntry) getMap().get(key);

            if (v.type != type) {
                throw new InvalidPropertyTypeException();
            }

            return v.value;
        } else {
            return null;
        }
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    public static final class ValueEntry implements Serializable {
        Object value;
        int type;

        public ValueEntry() {
        }

        public ValueEntry(int type, Object value) {
            this.type = type;
            this.value = value;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }
    }
}
