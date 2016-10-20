package avro.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificResponder;

import avro.proto.Hello;
import avro.client.*;

import java.util.Vector;

public class SysServer implements Hello {

	private int id;
	private Vector<Client> clients;
	
	public SysServer(){
		
		this.id = 0;
	}
	

	
	@Override
	public int connect() throws AvroRemoteException
	{
		System.out.println(" Client connected: Bob (number: " + id + " )");
		return this.id++;
	}




	public static void main( String[] args){
		
		Server server = null;
		try {
			server = new SaslSocketServer(new SpecificResponder(Hello.class, new SysServer()), new InetSocketAddress(6789));
		} catch (IOException e) {
			System.err.println(" error Failed to start server");
			e.printStackTrace(System.err);
			System.exit(1);
		}
		server.start();
		try {
			server.join();
		}	catch ( InterruptedException e) { }
			
	}
	
}