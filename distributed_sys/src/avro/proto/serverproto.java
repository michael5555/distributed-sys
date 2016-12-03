/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package avro.proto;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public interface serverproto {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"serverproto\",\"namespace\":\"avro.proto\",\"types\":[{\"type\":\"record\",\"name\":\"Lightinfo\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"status\",\"type\":\"boolean\"}]},{\"type\":\"record\",\"name\":\"Userinfo\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"athome\",\"type\":\"boolean\"}]},{\"type\":\"record\",\"name\":\"Clientinfo\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"type\",\"type\":\"string\"}]}],\"messages\":{\"connect\":{\"request\":[{\"name\":\"type2\",\"type\":\"string\"}],\"response\":\"int\"},\"getLights\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"status\",\"type\":\"boolean\"}],\"response\":\"int\"},\"sendLights\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"}],\"response\":{\"type\":\"array\",\"items\":\"Lightinfo\"}},\"changeLightStatus\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"}],\"response\":\"int\"}}}");
  int connect(java.lang.CharSequence type2) throws org.apache.avro.AvroRemoteException;
  int getLights(int id, boolean status) throws org.apache.avro.AvroRemoteException;
  java.util.List<avro.proto.Lightinfo> sendLights(int id) throws org.apache.avro.AvroRemoteException;
  int changeLightStatus(int id) throws org.apache.avro.AvroRemoteException;

  @SuppressWarnings("all")
  public interface Callback extends serverproto {
    public static final org.apache.avro.Protocol PROTOCOL = avro.proto.serverproto.PROTOCOL;
    void connect(java.lang.CharSequence type2, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void getLights(int id, boolean status, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void sendLights(int id, org.apache.avro.ipc.Callback<java.util.List<avro.proto.Lightinfo>> callback) throws java.io.IOException;
    void changeLightStatus(int id, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
  }
}