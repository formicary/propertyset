package com.opensymphony.module.propertyset.ejb3;

import java.util.Date;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertySet;

import static com.opensymphony.module.propertyset.PropertySet.*;
/**
 * @author hani
 *         Date: 7/27/11
 *         Time: 10:40 PM
 */
@TransactionAttribute
public class PropertySetService {

  @PersistenceContext(unitName="pu")
  private EntityManager entityManager;

  public <T> T get(String entityName, long id, String key, Class<T> type) {
    EntryPK pk = new EntryPK(entityName, id, key);
    PropertyEntry entry = entityManager.find(PropertyEntry.class, pk);
    if(type == Integer.class) {
      return (T)new Integer(entry.getIntValue());
    }
    if(type == Long.class) {
      return (T)new Long(entry.getLongValue());
    }
    if(type == Double.class) {
      return (T)new Double(entry.getDoubleValue());
    }
    if(type == Date.class) {
      return (T)entry.getDateValue();
    }
    if(type == String.class) {
      if(entry.getType() == PropertySet.STRING) {
        return (T)entry.getStringValue();
      } else if(entry.getType() == PropertySet.TEXT) {
        return (T)entry.getTextValue();
      } else {
        throw new IllegalArgumentException("Entry " + entityName + "#" + id + " key " + key + " is of type " + entry.getType() + ", not a string");
      }
    }
    if(type == Boolean.class) {
      return (T)Boolean.valueOf(entry.getBoolValue());
    }
    throw new IllegalArgumentException("Unsupported datatype " + type + " for entity " + entityName + " key " + key);
  }

  public void set(String entityName, long id, String key, Object value) {
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
      throw new IllegalArgumentException("Unsupported datatype " + value.getClass().getName() + " for entity " + entityName + "#" + id + " key " + key);
    }

    EntryPK pk = new EntryPK(entityName, id, key);
    PropertyEntry item;

    item = entityManager.find(PropertyEntry.class, pk);

    if(item == null) {
      item = new PropertyEntry();
      item.setPrimaryKey(pk);
      item.setType(type);
    } else if(item.getType() != type) {
      throw new PropertyException("Existing key '" + key + "' does not have matching type of " + type(type));
    }

    switch(type) {
      case BOOLEAN:
        item.setBoolValue((Boolean)value);
        break;

      case INT:
        item.setIntValue(((Number)value).intValue());
        break;

      case LONG:
        item.setLongValue(((Number)value).longValue());
        break;

      case DOUBLE:
        item.setDoubleValue(((Number)value).doubleValue());
        break;

      case STRING:
        item.setStringValue((String)value);
        break;

      case TEXT:
        item.setTextValue((String)value);
        break;

      case DATE:
        item.setDateValue((Date)value);
        break;

      default:
        throw new PropertyException("type " + type + " not supported");
    }

    entityManager.persist(item);
  }

  private String type(int type) {
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
}
