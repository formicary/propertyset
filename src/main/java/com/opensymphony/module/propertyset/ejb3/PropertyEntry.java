package com.opensymphony.module.propertyset.ejb3;

import java.io.*;
import java.util.Date;
import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

/**
 * @author Hani Suleiman
 *         Date: Nov 8, 2005
 *         Time: 4:57:44 PM
 */
@Entity
@Table(name = "OS_PROPERTIES")
@NamedQueries({@NamedQuery(name = "entries", query = "select p from PropertyEntry p where p.primaryKey.entityName=:entityName and p.primaryKey.entityId=:entityId"), @NamedQuery(name = "keys", query = "select p.primaryKey.key from PropertyEntry p where p.primaryKey.entityName=:entityName and p.primaryKey.entityId=:entityId"), @NamedQuery(name = "keys.prefix", query = "select p.primaryKey.key from PropertyEntry p where p.primaryKey.entityName=:entityName and p.primaryKey.entityId=:entityId and p.primaryKey.key like :prefix"), @NamedQuery(name = "keys.type", query = "select p.primaryKey.key from PropertyEntry p where p.primaryKey.entityName=:entityName and p.primaryKey.entityId=:entityId and p.type=:type"), @NamedQuery(name = "keys.prefixAndType", query = "select p.primaryKey.key from PropertyEntry p where p.primaryKey.entityName=:entityName and p.primaryKey.entityId=:entityId and p.type=:type and p.primaryKey.key like :prefix")})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.hibernate.annotations.Table(appliesTo = "OS_PROPERTIES", indexes = {@Index(name = "os_PropertyEntry_allidx", columnNames = {"entityName", "entityId"})})
public class PropertyEntry {
  @EmbeddedId
  private EntryPK primaryKey;
  private boolean boolValue;
  private int intValue;
  private long longValue;
  private double doubleValue;
  private String stringValue;

  @Lob @org.hibernate.annotations.Type(type = "org.hibernate.type.StringClobType")
  private String textValue;
  private Date dateValue;
  private int type;

  public EntryPK getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(EntryPK primaryKey) {
    this.primaryKey = primaryKey;
  }

  public boolean getBoolValue() {
    return boolValue;
  }

  public void setBoolValue(boolean boolValue) {
    this.boolValue = boolValue;
  }

  public int getIntValue() {
    return intValue;
  }

  public void setIntValue(int intValue) {
    this.intValue = intValue;
  }

  public long getLongValue() {
    return longValue;
  }

  public void setLongValue(long longValue) {
    this.longValue = longValue;
  }

  public double getDoubleValue() {
    return doubleValue;
  }

  public void setDoubleValue(double doubleValue) {
    this.doubleValue = doubleValue;
  }

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }

  public String getTextValue() {
    return textValue;
  }

  public void setTextValue(String textValue) {
    this.textValue = textValue;
  }

  public Date getDateValue() {
    return dateValue;
  }

  public void setDateValue(Date dateValue) {
    this.dateValue = dateValue;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }
}
