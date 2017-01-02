/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package avro.proto;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class Clientinfo extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Clientinfo\",\"namespace\":\"avro.proto\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"type\",\"type\":\"string\"},{\"name\":\"address\",\"type\":\"string\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public int id;
  @Deprecated public java.lang.CharSequence type;
  @Deprecated public java.lang.CharSequence address;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public Clientinfo() {}

  /**
   * All-args constructor.
   */
  public Clientinfo(java.lang.Integer id, java.lang.CharSequence type, java.lang.CharSequence address) {
    this.id = id;
    this.type = type;
    this.address = address;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return id;
    case 1: return type;
    case 2: return address;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: id = (java.lang.Integer)value$; break;
    case 1: type = (java.lang.CharSequence)value$; break;
    case 2: address = (java.lang.CharSequence)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'id' field.
   */
  public java.lang.Integer getId() {
    return id;
  }

  /**
   * Sets the value of the 'id' field.
   * @param value the value to set.
   */
  public void setId(java.lang.Integer value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'type' field.
   */
  public java.lang.CharSequence getType() {
    return type;
  }

  /**
   * Sets the value of the 'type' field.
   * @param value the value to set.
   */
  public void setType(java.lang.CharSequence value) {
    this.type = value;
  }

  /**
   * Gets the value of the 'address' field.
   */
  public java.lang.CharSequence getAddress() {
    return address;
  }

  /**
   * Sets the value of the 'address' field.
   * @param value the value to set.
   */
  public void setAddress(java.lang.CharSequence value) {
    this.address = value;
  }

  /** Creates a new Clientinfo RecordBuilder */
  public static avro.proto.Clientinfo.Builder newBuilder() {
    return new avro.proto.Clientinfo.Builder();
  }
  
  /** Creates a new Clientinfo RecordBuilder by copying an existing Builder */
  public static avro.proto.Clientinfo.Builder newBuilder(avro.proto.Clientinfo.Builder other) {
    return new avro.proto.Clientinfo.Builder(other);
  }
  
  /** Creates a new Clientinfo RecordBuilder by copying an existing Clientinfo instance */
  public static avro.proto.Clientinfo.Builder newBuilder(avro.proto.Clientinfo other) {
    return new avro.proto.Clientinfo.Builder(other);
  }
  
  /**
   * RecordBuilder for Clientinfo instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Clientinfo>
    implements org.apache.avro.data.RecordBuilder<Clientinfo> {

    private int id;
    private java.lang.CharSequence type;
    private java.lang.CharSequence address;

    /** Creates a new Builder */
    private Builder() {
      super(avro.proto.Clientinfo.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(avro.proto.Clientinfo.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.type)) {
        this.type = data().deepCopy(fields()[1].schema(), other.type);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.address)) {
        this.address = data().deepCopy(fields()[2].schema(), other.address);
        fieldSetFlags()[2] = true;
      }
    }
    
    /** Creates a Builder by copying an existing Clientinfo instance */
    private Builder(avro.proto.Clientinfo other) {
            super(avro.proto.Clientinfo.SCHEMA$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.type)) {
        this.type = data().deepCopy(fields()[1].schema(), other.type);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.address)) {
        this.address = data().deepCopy(fields()[2].schema(), other.address);
        fieldSetFlags()[2] = true;
      }
    }

    /** Gets the value of the 'id' field */
    public java.lang.Integer getId() {
      return id;
    }
    
    /** Sets the value of the 'id' field */
    public avro.proto.Clientinfo.Builder setId(int value) {
      validate(fields()[0], value);
      this.id = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'id' field has been set */
    public boolean hasId() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'id' field */
    public avro.proto.Clientinfo.Builder clearId() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'type' field */
    public java.lang.CharSequence getType() {
      return type;
    }
    
    /** Sets the value of the 'type' field */
    public avro.proto.Clientinfo.Builder setType(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.type = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'type' field has been set */
    public boolean hasType() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'type' field */
    public avro.proto.Clientinfo.Builder clearType() {
      type = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'address' field */
    public java.lang.CharSequence getAddress() {
      return address;
    }
    
    /** Sets the value of the 'address' field */
    public avro.proto.Clientinfo.Builder setAddress(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.address = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'address' field has been set */
    public boolean hasAddress() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'address' field */
    public avro.proto.Clientinfo.Builder clearAddress() {
      address = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    public Clientinfo build() {
      try {
        Clientinfo record = new Clientinfo();
        record.id = fieldSetFlags()[0] ? this.id : (java.lang.Integer) defaultValue(fields()[0]);
        record.type = fieldSetFlags()[1] ? this.type : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.address = fieldSetFlags()[2] ? this.address : (java.lang.CharSequence) defaultValue(fields()[2]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
