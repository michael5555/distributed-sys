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

import avro.proto.serverproto;
import avro.proto.lightproto;


import avro.client.*;

import avro.proto.Lightinfo;
import avro.proto.Clientinfo;
import avro.proto.Userinfo;




import java.util.List;
import java.util.ArrayList;


public class Controller implements serverproto {

	private int id;
	private List<Clientinfo> clients;
	private List<Userinfo> users;
	public List<Lightinfo> lights;


	
	public Controller(){
		
		this.id = 0;
		clients = new ArrayList<Clientinfo>();
		users = new ArrayList<Userinfo>();
		lights = new ArrayList<Lightinfo>();
	}
	

	
	@Override
	public int connect(CharSequence type2) throws AvroRemoteException
	{
		
		if(type2.toString().equals("User")){
			users.add( new Userinfo(id,true) );
		}
		
		else {
			
			clients.add(new Clientinfo(id,type2));
		}
		System.out.println(" Client connected: " + type2 + " (number: " + id + " )");
		return this.id++;
	}
	
	@Override
	public int getLights (int id, boolean status) throws AvroRemoteException 
	{
		for(Lightinfo temp : lights){
			
			if(temp.getId() == id){
				
				if(temp.getStatus() != status){
					
					temp.setStatus(status);
					return 0;
					
				}
			}
		}
		lights.add(new Lightinfo(id, status));
		System.out.println(" Light connected: " + id + " (status: " + status + " )");


		return 0;
	}

	
	@Override
	public List<Lightinfo> sendLights (int id) throws AvroRemoteException 
	{
		for(Userinfo temp : users){
			
			if(temp.getId() == id){
				
				return lights;
			}
		}
			return new ArrayList<Lightinfo>();
	}
	
	@Override
	public int changeLightStatus(int id){
		
		for(Lightinfo temp : lights){
			
			if( id == temp.getId()){
				try {

				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(6790 + id));
				lightproto proxy =  (lightproto) SpecificRequestor.getClient(lightproto.class, client);
				proxy.changeStatus(id);
				temp.setStatus(!temp.getStatus());
				} catch(IOException e){
					
					System.err.println("Error connecting to light ...");
					e.printStackTrace(System.err);
					System.exit(1);

				}

			}
		}
		
		return 0;
	}



	public static void main( String[] args){
		
		Server server = null;
		Controller controller = new Controller();

		try {
			server = new SaslSocketServer(new SpecificResponder(serverproto.class, controller), new InetSocketAddress(6789));
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