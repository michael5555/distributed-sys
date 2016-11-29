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


public class User  {
	
	private String username;
	private int id;

	
	User(int id, String username){
		this.id = id;
		this.username = username;
		System.out.println(this.id);
	}
	public int getID(){
		
		return this.id;
	}

	public static void main(String[] args) {
		try {
			Server userserver = null;

			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(6789));
			sysserver proxy =  (sysserver) SpecificRequestor.getClient(sysserver.class, client);
			int id = proxy.connect("User");
			User Bob = new User(id,"Bobby");
			userserver = new SaslSocketServer(new SpecificResponder(user.class, Bob), new InetSocketAddress(6790));

			//client.close();
		} catch(IOException e){
			
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);

		}
		
		



	}

}
