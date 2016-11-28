package avro.client;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificResponder;

import avro.proto.connect;
import avro.server.SysServer;


public class User extends Client {
	
	private String username;
	
	User(int port, String username){
		
		super(port,"User");
		this.username = username;
	}

	public static void main(String[] args) {
		
		User Bob = new User(6789,"Bobby");
		



	}

}
