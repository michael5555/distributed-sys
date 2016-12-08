package avro.client;

import java.io.IOException;
import java.net.InetAddress;
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
import avro.proto.userproto;


import avro.proto.Lightinfo;
import avro.proto.lightproto;

import java.util.List;

import asg.cliche.*;



public class User implements userproto {
	
	private String username;
	private int id;

	
	User(int id, String username){
		this.id = id;
		this.username = username;
	}
	public int getId(){
		
		return this.id;
	}
	
	@Override
	public int reportUserStatus(int id, boolean athome){
		
		if(athome){
		
			System.out.println("User with id: " + id + " has  entered the house.");
		}
		else{
			System.out.println("User with id: " + id + " has  left the house.");

		}
		return 0;
	}
	
	@Command
	public void printLights(){
		
		try{
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
			serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);

			List<Lightinfo> lights = proxy.sendLights(this.id);
			for(Lightinfo temp : lights){
				
				System.out.println("We have a light with id: " + temp.getId() + " ,its status is currently: " +  temp.getStatus());
			}
		}catch(IOException e){}
	}
	@Command
	public void changeLightStatus(int id){
		
		try{
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
			serverproto proxy = (serverproto) SpecificRequestor.getClient(serverproto.class, client);

			proxy.changeLightStatus(id);

		}catch(IOException e){}
	}
	

	@Command
	public void changeHomeStatus(){
		
		try{
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
			serverproto proxy = (serverproto) SpecificRequestor.getClient(serverproto.class, client);
			
			proxy.changeHomeStatus(this.id);
	
		}catch(IOException e){}
	}
	


	public static void main(String[] args) {
		Server server = null;
		try {

			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
			serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
			int id = proxy.connect("User");
			User Bob = new User(id,"Bobby");


			server = new SaslSocketServer(new SpecificResponder(userproto.class, Bob), new InetSocketAddress(InetAddress.getLocalHost(),Bob.getId()));
			
			server.start();
			ShellFactory.createConsoleShell("user", "", Bob).commandLoop(); // and three.

		} catch(IOException e){
			
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);

		}
		

		
		try {
			server.join();
		}	catch ( InterruptedException e) { }



	}

}
