/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package avro.proto;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public interface fridgeproto {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"fridgeproto\",\"namespace\":\"avro.proto\",\"types\":[],\"messages\":{\"sendItems\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"}],\"response\":{\"type\":\"array\",\"items\":\"string\"}}}}");
  java.util.List<java.lang.CharSequence> sendItems(int id) throws org.apache.avro.AvroRemoteException;

  @SuppressWarnings("all")
  public interface Callback extends fridgeproto {
    public static final org.apache.avro.Protocol PROTOCOL = avro.proto.fridgeproto.PROTOCOL;
    void sendItems(int id, org.apache.avro.ipc.Callback<java.util.List<java.lang.CharSequence>> callback) throws java.io.IOException;
  }
}