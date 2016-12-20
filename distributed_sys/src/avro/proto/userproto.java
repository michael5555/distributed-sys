/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package avro.proto;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public interface userproto {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"userproto\",\"namespace\":\"avro.proto\",\"types\":[{\"type\":\"record\",\"name\":\"Lightinfo\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"status\",\"type\":\"boolean\"},{\"name\":\"address\",\"type\":\"string\"}]},{\"type\":\"record\",\"name\":\"Userinfo\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"athome\",\"type\":\"boolean\"},{\"name\":\"address\",\"type\":\"string\"}]},{\"type\":\"record\",\"name\":\"Clientinfo\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"type\",\"type\":\"string\"},{\"name\":\"address\",\"type\":\"string\"}]},{\"type\":\"record\",\"name\":\"TSinfo\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"measurement\",\"type\":\"double\"},{\"name\":\"address\",\"type\":\"string\"}]},{\"type\":\"record\",\"name\":\"Fridgeinfo\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"address\",\"type\":\"string\"}]}],\"messages\":{\"reportUserStatus\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"athome\",\"type\":\"boolean\"}],\"response\":\"int\"},\"reportFridgeEmpty\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"}],\"response\":\"int\"},\"syncClients\":{\"request\":[{\"name\":\"clients\",\"type\":{\"type\":\"array\",\"items\":\"Clientinfo\"}}],\"response\":\"int\"},\"syncUsers\":{\"request\":[{\"name\":\"users\",\"type\":{\"type\":\"array\",\"items\":\"Userinfo\"}}],\"response\":\"int\"},\"syncLights\":{\"request\":[{\"name\":\"lights\",\"type\":{\"type\":\"array\",\"items\":\"Lightinfo\"}}],\"response\":\"int\"},\"syncMeasurements\":{\"request\":[{\"name\":\"measurements\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"array\",\"items\":\"TSinfo\"}}}],\"response\":\"int\"}}}");
  int reportUserStatus(int id, boolean athome) throws org.apache.avro.AvroRemoteException;
  int reportFridgeEmpty(int id) throws org.apache.avro.AvroRemoteException;
  int syncClients(java.util.List<avro.proto.Clientinfo> clients) throws org.apache.avro.AvroRemoteException;
  int syncUsers(java.util.List<avro.proto.Userinfo> users) throws org.apache.avro.AvroRemoteException;
  int syncLights(java.util.List<avro.proto.Lightinfo> lights) throws org.apache.avro.AvroRemoteException;
  int syncMeasurements(java.util.List<java.util.List<avro.proto.TSinfo>> measurements) throws org.apache.avro.AvroRemoteException;

  @SuppressWarnings("all")
  public interface Callback extends userproto {
    public static final org.apache.avro.Protocol PROTOCOL = avro.proto.userproto.PROTOCOL;
    void reportUserStatus(int id, boolean athome, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void reportFridgeEmpty(int id, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void syncClients(java.util.List<avro.proto.Clientinfo> clients, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void syncUsers(java.util.List<avro.proto.Userinfo> users, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void syncLights(java.util.List<avro.proto.Lightinfo> lights, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void syncMeasurements(java.util.List<java.util.List<avro.proto.TSinfo>> measurements, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
  }
}