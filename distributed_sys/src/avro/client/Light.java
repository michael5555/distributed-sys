package avro.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;

import avro.proto.serverproto;
import avro.proto.lightproto;

public class Light implements lightproto  {
	
	private Boolean state;
	private int id;

	public Light(int id) {
		state = false;
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}
	
	public Boolean getState() {
		return state;
	}
	
	public int changeStatus(int id, boolean lightstatus){
		if(id == this.id){
			this.state = lightstatus;
			if(this.state){
				System.out.println("light with id: " + this.id + " has been turned on");
			}
			else{
				System.out.println("light with id: " + this.id + " has been turned off");
			}
			return 0;
		}
		return -1;
	}

	public static void main(String[] args) {
		Server server = null;
		try {
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
			serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
			
			int id = proxy.connect("Light");
			client.close();
			Light lampje = new Light(id);
			
			server = new SaslSocketServer(new SpecificResponder(lightproto.class, lampje), new InetSocketAddress(InetAddress.getLocalHost(),lampje.getId()));
			server.start();
		} catch(IOException e){
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);
		}
		
		try {
			server.join();

		}	catch ( Exception e) {
			
		}
	}
}
