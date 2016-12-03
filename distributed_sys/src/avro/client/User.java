package avro.client;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.ipc.Transceiver;

import avro.proto.serverproto;

import avro.proto.Lightinfo;
import java.util.List;



public class User {
	
	private String username;
	private int id;

	
	User(int id, String username){
		this.id = id;
		this.username = username;
	}
	public int getID(){
		
		return this.id;
	}
	


	public static void main(String[] args) {
		try {

			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(6789));
			serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
			int id = proxy.connect("User");
			User Bob = new User(id,"Bobby");
			List<Lightinfo> lights = proxy.sendLights(Bob.getID());
			
			for (Lightinfo temp : lights){
				
				System.out.println("we have a light with id: " + temp.getId() + " ,its state is currently: " + temp.getStatus());
			}
			proxy.changeLightStatus(1);
			lights = proxy.sendLights(Bob.getID());
			for (Lightinfo temp : lights){
				
				System.out.println("we have a light with id: " + temp.getId() + " ,its state is currently: " + temp.getStatus());
			}

			//client.close();
		} catch(IOException e){
			
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);

		}
		
		



	}

}
