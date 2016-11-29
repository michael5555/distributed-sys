package avro.client;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import avro.proto.sysserver;


public class Light  {
	
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

	public static void main(String[] args) {
		try {
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(6789));
			sysserver proxy =  (sysserver) SpecificRequestor.getClient(sysserver.class, client);
			int id = proxy.connect("Light");
			Light lampje = new Light(id);
			int blabla = proxy.getlights(lampje.getID(), lampje.getState());
			lampje.changeState(proxy);

			//client.close();
		} catch(IOException e){
			
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);

		}

	}

}
