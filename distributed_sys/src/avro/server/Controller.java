package avro.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;

import avro.proto.sysserver;

import avro.client.*;

import java.util.Vector;


class ClientInfo {
	
	private int id;
	private CharSequence type;
	
	public ClientInfo(int i, CharSequence t){
		
		id = i;
		type = t;
	}
	
	public int getID() {
		
		return id;
	}
	
	public CharSequence getType() {
		
		return type;
	}
}

class LightInfo {
	
	private int id;
	private Boolean status;
	
	public LightInfo(int i, Boolean b){
		
		id = i;
		status = b;
	}
	
	public int getID() {
		
		return id;
	}
	
	public Boolean getType() {
		
		return status;
	}
}

class UserInfo {
	
	private int id;
	private Boolean home;
	
	public UserInfo(int i, Boolean h){
		
		id = i;
		home = h;
	}
	
	public int getID() {
		
		return id;
	}
	
	public Boolean getType() {
		
		return home;
	}
}
public class Controller implements sysserver {

	private int id;
	private Vector<ClientInfo> clients;
	private Vector<UserInfo> users;
	public Vector<LightInfo> lights;


	
	public Controller(){
		
		this.id = 0;
		clients = new Vector<ClientInfo>();
		users = new Vector<UserInfo>();
		lights = new Vector<LightInfo>();
	}
	

	
	@Override
	public int connect(CharSequence type2) throws AvroRemoteException
	{
		
		if(type2.toString().equals("User")){
			users.add( new UserInfo(id,true) );
		}
		
		else {
			
			clients.add(new ClientInfo(id,type2));
		}
		System.out.println(" Client connected: " + type2 + " (number: " + id + " )");
		return this.id++;
	}
	
	@Override
	public int getlights (int id, boolean status) throws AvroRemoteException 
	{
		lights.add(new LightInfo(id, status));
		System.out.println(" Light connected: " + id + " (status: " + status + " )");

		return 0;
	}




	public static void main( String[] args){
		
		Server server = null;
		Controller controller = new Controller();

		try {
			server = new SaslSocketServer(new SpecificResponder(sysserver.class, controller), new InetSocketAddress(6789));
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