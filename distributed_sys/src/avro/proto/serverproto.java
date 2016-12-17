/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package avro.proto;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public interface serverproto {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"serverproto\",\"namespace\":\"avro.proto\",\"types\":[{\"type\":\"record\",\"name\":\"Lightinfo\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"status\",\"type\":\"boolean\"}]},{\"type\":\"record\",\"name\":\"Userinfo\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"athome\",\"type\":\"boolean\"}]},{\"type\":\"record\",\"name\":\"Clientinfo\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"type\",\"type\":\"string\"}]},{\"type\":\"record\",\"name\":\"TSinfo\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"measurement\",\"type\":\"double\"}]},{\"type\":\"record\",\"name\":\"Fridgeinfo\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"}]}],\"messages\":{\"connect\":{\"request\":[{\"name\":\"type2\",\"type\":\"string\"}],\"response\":\"int\"},\"sendLights\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"}],\"response\":{\"type\":\"array\",\"items\":\"Lightinfo\"}},\"sendClients\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"}],\"response\":{\"type\":\"array\",\"items\":\"Clientinfo\"}},\"changeLightStatus\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"}],\"response\":\"int\"},\"changeHomeStatus\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"}],\"response\":\"int\"},\"sendFridgeItems\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"}],\"response\":{\"type\":\"array\",\"items\":\"string\"}},\"sendTSMeasurement\":{\"request\":[{\"name\":\"measurement\",\"type\":\"double\"},{\"name\":\"id\",\"type\":\"int\"}],\"response\":\"int\"},\"getCurrentTemperature\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"}],\"response\":\"double\"},\"getTemperatureHistory\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"}],\"response\":{\"type\":\"array\",\"items\":\"double\"}},\"openAFridge\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"userid\",\"type\":\"int\"}],\"response\":\"int\"},\"FridgeEmptyMessage\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"}],\"response\":\"int\"},\"deleteClient\":{\"request\":[{\"name\":\"id\",\"type\":\"int\"}],\"response\":\"int\"},\"syncClients\":{\"request\":[{\"name\":\"clients\",\"type\":{\"type\":\"array\",\"items\":\"Clientinfo\"}}],\"response\":\"int\"},\"syncUsers\":{\"request\":[{\"name\":\"users\",\"type\":{\"type\":\"array\",\"items\":\"Userinfo\"}}],\"response\":\"int\"},\"syncLights\":{\"request\":[{\"name\":\"lights\",\"type\":{\"type\":\"array\",\"items\":\"Lightinfo\"}}],\"response\":\"int\"},\"syncMeasurements\":{\"request\":[{\"name\":\"measurements\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"array\",\"items\":\"TSinfo\"}}}],\"response\":\"int\"}}}");
  int connect(java.lang.CharSequence type2) throws org.apache.avro.AvroRemoteException;
  java.util.List<avro.proto.Lightinfo> sendLights(int id) throws org.apache.avro.AvroRemoteException;
  java.util.List<avro.proto.Clientinfo> sendClients(int id) throws org.apache.avro.AvroRemoteException;
  int changeLightStatus(int id) throws org.apache.avro.AvroRemoteException;
  int changeHomeStatus(int id) throws org.apache.avro.AvroRemoteException;
  java.util.List<java.lang.CharSequence> sendFridgeItems(int id) throws org.apache.avro.AvroRemoteException;
  int sendTSMeasurement(double measurement, int id) throws org.apache.avro.AvroRemoteException;
  double getCurrentTemperature(int id) throws org.apache.avro.AvroRemoteException;
  java.util.List<java.lang.Double> getTemperatureHistory(int id) throws org.apache.avro.AvroRemoteException;
  int openAFridge(int id, int userid) throws org.apache.avro.AvroRemoteException;
  int FridgeEmptyMessage(int id) throws org.apache.avro.AvroRemoteException;
  int deleteClient(int id) throws org.apache.avro.AvroRemoteException;
  int syncClients(java.util.List<avro.proto.Clientinfo> clients) throws org.apache.avro.AvroRemoteException;
  int syncUsers(java.util.List<avro.proto.Userinfo> users) throws org.apache.avro.AvroRemoteException;
  int syncLights(java.util.List<avro.proto.Lightinfo> lights) throws org.apache.avro.AvroRemoteException;
  int syncMeasurements(java.util.List<java.util.List<avro.proto.TSinfo>> measurements) throws org.apache.avro.AvroRemoteException;

  @SuppressWarnings("all")
  public interface Callback extends serverproto {
    public static final org.apache.avro.Protocol PROTOCOL = avro.proto.serverproto.PROTOCOL;
    void connect(java.lang.CharSequence type2, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void sendLights(int id, org.apache.avro.ipc.Callback<java.util.List<avro.proto.Lightinfo>> callback) throws java.io.IOException;
    void sendClients(int id, org.apache.avro.ipc.Callback<java.util.List<avro.proto.Clientinfo>> callback) throws java.io.IOException;
    void changeLightStatus(int id, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void changeHomeStatus(int id, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void sendFridgeItems(int id, org.apache.avro.ipc.Callback<java.util.List<java.lang.CharSequence>> callback) throws java.io.IOException;
    void sendTSMeasurement(double measurement, int id, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void getCurrentTemperature(int id, org.apache.avro.ipc.Callback<java.lang.Double> callback) throws java.io.IOException;
    void getTemperatureHistory(int id, org.apache.avro.ipc.Callback<java.util.List<java.lang.Double>> callback) throws java.io.IOException;
    void openAFridge(int id, int userid, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void FridgeEmptyMessage(int id, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void deleteClient(int id, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void syncClients(java.util.List<avro.proto.Clientinfo> clients, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void syncUsers(java.util.List<avro.proto.Userinfo> users, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void syncLights(java.util.List<avro.proto.Lightinfo> lights, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
    void syncMeasurements(java.util.List<java.util.List<avro.proto.TSinfo>> measurements, org.apache.avro.ipc.Callback<java.lang.Integer> callback) throws java.io.IOException;
  }
}