package avro.client;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;

import avro.proto.sysserver;
import avro.proto.lightproto;



public class Light implements lightproto  {
	
	private Boolean state;
	private int id;


	public Light(int id) {
		state = false;
		this.id = id;
	}
	
	public int getID(){
		
		return this.id;
	}
	
	public void changeState(sysserver proxy){
		
		state = !state;
		try{
			proxy.getlights(this.id, this.state);
		}catch(IOException e){}
		System.out.println(" you changed your state to: " + state);
	}
	
	public Boolean getState() {
		return state;
	}
	
	public int changestatus(int id){
		
		if(id == this.id){
			
			this.state = !this.state;
			return 0;
		}
		
		return -1;
	}

	public static void main(String[] args) {
		Server server = null;
		try {
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(6789));
			sysserver proxy =  (sysserver) SpecificRequestor.getClient(sysserver.class, client);
			int id = proxy.connect("Light");
			Light lampje = new Light(id);
			server = new SaslSocketServer(new SpecificResponder(lightproto.class, lampje), new InetSocketAddress(6790 + lampje.getID()));


			proxy.getlights(lampje.getID(), lampje.getState());
			

		} catch(IOException e){
			
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);

		}
		
		server.start();
		
		try {
			server.join();
		}	catch ( InterruptedException e) { }

		
		

	}

}
