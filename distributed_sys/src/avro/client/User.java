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

import avro.proto.sysserver;
import avro.proto.user;

import avro.server.Controller;
import avro.proto.Lightinfo;
import java.util.List;



public class User implements user  {
	
	private String username;
	private int id;

	
	User(int id, String username){
		this.id = id;
		this.username = username;
	}
	public int getID(){
		
		return this.id;
	}
	
	public int recievelights (List<Lightinfo> lights) throws AvroRemoteException 
	{
		for(Lightinfo temp : lights){
			
			System.out.println("controller has a light with id " + temp.getId() + ", and this lights current state is: " + temp.getStatus());
		}
		return 0;
	}

	public static void main(String[] args) {
		try {
			Server userserver = null;

			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(6789));
			sysserver proxy =  (sysserver) SpecificRequestor.getClient(sysserver.class, client);
			int id = proxy.connect("User");
			User Bob = new User(id,"Bobby");
			List<Lightinfo> lights = proxy.recievelights(Bob.getID());
			
			for (Lightinfo temp : lights){
				
				System.out.println("we have a light with id: " + temp.getId() + " ,its state is currently: " + temp.getStatus());
			}
			userserver = new SaslSocketServer(new SpecificResponder(user.class, Bob), new InetSocketAddress(6790));

			//client.close();
		} catch(IOException e){
			
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);

		}
		
		



	}

}
