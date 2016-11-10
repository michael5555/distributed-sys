package avro.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificResponder;

import avro.proto.connect;
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
public class SysServer implements connect {

	private int id;
	private Vector<ClientInfo> clients;
	private Vector<UserInfo> users;

	
	public SysServer(){
		
		this.id = 0;
		clients = new Vector<ClientInfo>();
		users = new Vector<UserInfo>();
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




	public static void main( String[] args){
		
		Server server = null;
		try {
			server = new SaslSocketServer(new SpecificResponder(connect.class, new SysServer()), new InetSocketAddress(6789));
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