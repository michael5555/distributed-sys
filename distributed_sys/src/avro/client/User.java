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
import avro.proto.Clientinfo;

import avro.proto.lightproto;

import avro.proto.fridgeproto;


import java.util.List;

import asg.cliche.*;



public class User implements userproto {
	
	private int id;
	private boolean fridgeTime;

	
	User(int id, String username){
		this.id = id;
		fridgeTime = false;
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
	
	@Override
	public int reportFridgeEmpty(int id) {
		
		System.out.println("Fridge with id: " + id + " is empty");
		return 0;
	}
	
	@Command
	public void printLights(){
		if(!fridgeTime){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
				serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
	
				List<Lightinfo> lights = proxy.sendLights(this.id);
				client.close();
				if (lights.size() == 0){
					System.out.println("There are no lights");
				}
				for(Lightinfo temp : lights){
					
					System.out.println("We have a light with id: " + temp.getId() + " ,its status is currently: " +  temp.getStatus());
				}
			}catch(IOException e){}
		}
		else{
			System.out.println("Right now you are connected to a Fridge");
		}
	}
	
	@Command
	public void printClients(){
		if(!fridgeTime){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
				serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
				List<Clientinfo> clients = proxy.sendClients(this.id);
				client.close();
				for(Clientinfo temp : clients){

					System.out.println("We have a client with id: " + temp.getId() + " ,its type is: " +  temp.getType());
				}
			}catch(IOException e){}
		}
		else{
			System.out.println("Right now you are connected to a Fridge");
		}
	}
	@Command
	public void changeLightStatus(int id){
		if(!fridgeTime){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
				serverproto proxy = (serverproto) SpecificRequestor.getClient(serverproto.class, client);
	
				proxy.changeLightStatus(id);
				client.close();
			}catch(IOException e){}
		}		
		else{
			System.out.println("Right now you are connected to a Fridge");
		}
	}
	

	@Command
	public void changeHomeStatus(){
		if(!fridgeTime){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
				serverproto proxy = (serverproto) SpecificRequestor.getClient(serverproto.class, client);
				
				proxy.changeHomeStatus(this.id);
				client.close();

			}catch(IOException e){}
		}
		else{
			System.out.println("Right now you are connected to a Fridge");
		}
	}
	
	@Command
	public void printFridgeItems(int id){
		if(!fridgeTime){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
				serverproto proxy = (serverproto) SpecificRequestor.getClient(serverproto.class, client);
				
				List<CharSequence> items = proxy.sendFridgeItems(id);
				client.close();

				if (items.size() == 0){
					System.out.println("Fridge with id: " + id + " is empty");
				}
				
				for(CharSequence temp : items){
					System.out.println("Fridge has : " + temp);
				}
		
			}catch(IOException e){}
		}
		else{
			System.out.println("Right now you are connected to a Fridge");
		}
	}
	
	@Command
	public void printCurrentTemperature(){
		if(!fridgeTime){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
				serverproto proxy = (serverproto) SpecificRequestor.getClient(serverproto.class, client);
				
				double value = proxy.getCurrentTemperature(this.id);
				client.close();

				System.out.println("Current temperature: "  + value);
		
			}catch(IOException e){}
		}		
		else{
			System.out.println("Right now you are connected to a Fridge");
		}
	}
	
	@Command
	public void printTemperatureHistory(int id){
		if(!fridgeTime){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
				serverproto proxy = (serverproto) SpecificRequestor.getClient(serverproto.class, client);
				
				List<Double> temps = proxy.getTemperatureHistory(id);
				client.close();
				System.out.println("Temperature History : ");
				for(Double temp : temps){
					System.out.print( temp + ", ");
				}
		
			}catch(IOException e){}
		}
		else{
			System.out.println("Right now you are connected to a Fridge");
		}
	}
	
	@Command
	public void openFridge(int id){
		if(!fridgeTime){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
				serverproto proxy = (serverproto) SpecificRequestor.getClient(serverproto.class, client);
				int open = proxy.openFridge(id);
				if(open == 0){
					
					System.out.println("Fridge with id: " + id + " has been opened.");
					fridgeTime = true;
				}
				else if (open == 1){
					System.out.println("Fridge with id: " + id + " is already opened.");
					fridgeTime = true;

				}
				client.close();

			}catch(IOException e){}
		}
		else{
			System.out.println("Right now you are connected to a Fridge");
		}
	}
	
	@Command
	public void addItemtoFridge(int id,CharSequence item){
		
		if(fridgeTime){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),id));
				fridgeproto proxy = (fridgeproto) SpecificRequestor.getClient(fridgeproto.class, client);
				if(proxy.addItem(id, item) == 0){
					System.out.println("Added item: " + item + " to fridge with id: "  + id);

				}
				client.close();


			}catch (IOException e){}
			
		}
		else{
			System.out.println("Right now you are connected to a controller");

		}
	}
	@Command
	public void removeItemfromFridge(int id,CharSequence item){
		
		if(fridgeTime){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),id));
				fridgeproto proxy = (fridgeproto) SpecificRequestor.getClient(fridgeproto.class, client);
				if(proxy.removeItem(id, item) == 0){
					System.out.println("removed item: " + item + " from fridge with id: "  + id);

				}
				client.close();

			}catch (IOException e){}
			
		}
		else{
			System.out.println("Right now you are connected to a controller");

		}
	}
	
	@Command
	public void closeFridge(int id){
		
		if(fridgeTime){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),id));
				fridgeproto proxy = (fridgeproto) SpecificRequestor.getClient(fridgeproto.class, client);
				int status = proxy.closeFridge(id);
				if(status  == 0){
					fridgeTime = false;
					System.out.println("fridge with id: "  + id +  " has been closed");
				}
				else if (status > 0){
					System.out.println("fridge with id: "  + id +  " is still in use");

					fridgeTime = false;
					
				}
				client.close();


			}catch (IOException e){}
			
		}
		else{
			System.out.println("Right now you are connected to a controller");

		}
	}


	public static void main(String[] args) {
		Server server = null;
		try {

			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
			serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
			int id = proxy.connect("User");
			User Bob = new User(id,"Bobby");

			client.close();

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
