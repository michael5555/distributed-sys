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
import org.apache.avro.ipc.CallFuture;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.ipc.Transceiver;

import avro.proto.serverproto;
import avro.proto.userproto;
import avro.proto.Fridgestate;



import avro.proto.Lightinfo;
import avro.proto.TSinfo;
import avro.proto.Userinfo;
import avro.proto.Clientinfo;
import avro.proto.Fridgeinfo;


import avro.proto.lightproto;

import avro.proto.fridgeproto;

import avro.server.Controller;



import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import asg.cliche.*;

public class User extends Controller implements userproto {
	
	private int id;
	private boolean fridgeTime;
	private Fridgeinfo fridge;
	private String address;
	
	private int controllerport = 5000;

	User(int id, String username, String conaddr,String addr){
		super(conaddr);
		this.address = addr;
		this.id = id;
		fridgeTime = false;
		fridge = new Fridgeinfo(0,"");
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getAddress(){
		return this.address;
	}
	
	public void run() {
		if ( fridge.getId() != 0){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(fridge.getAddress().toString()),fridge.getId()));
			}catch( IOException e){
				fridgeTime = false;
				fridge.setId(0);
				System.out.println("Connection to fridge lost");
			}
		}
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
	
	@Override
	public synchronized int syncClients(List<Clientinfo> clients) {
		
		this.clients = clients;
		return 0;
	}
	
	@Override
	public synchronized int syncUsers(List<Userinfo> users) {
		
		this.users = users;
		return 0;
	}
	
	@Override
	public synchronized int syncLights(List<Lightinfo> lights) {
		
		this.lights = lights;
		return 0;
	}
	
	@Override
	public synchronized int syncMeasurements(List<List<TSinfo>> measurements) {
		
		this.measurements = measurements;
		return 0;
	}
	
	@Override
	public int election(int id) {
		
		System.out.println("our election has commenced");
		
		int me = 0;
		int next = 0;
		String type = "";
		
		for(int i = 0; i < clients.size(); i++) {
			
			if( clients.get(i).getId() == this.id ){
				
				me = i;
				break;
			}
		}
		
		for(int i = me + 1; i <= clients.size(); i++) {
			
			if (i >= clients.size() - 1){
				
				i = 0;
			}
			
			if (clients.get(i).getType().toString().equals("User") || clients.get(i).getType().toString().equals("Fridge") ){
				
				if (clients.get(i).getType().toString().equals("User")) {
					
					type = "User";
				}
				else {
					type = "Fridge";
				}
				next = i;
				break;
			}
		}
		
		if(id > this.id) {
			
			//forward election
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(clients.get(next).getAddress().toString()),clients.get(next).getId()));
				if(type == "User"){
					Thread t1 = new Thread(new Runnable() {
					     public void run() {
					    	 try{
								userproto.Callback proxy = SpecificRequestor.getClient(userproto.Callback.class, client);
								CallFuture<Integer> future = new CallFuture<Integer>();
								proxy.election(id, future);
							}catch(IOException e){}
					     }
					});  
					t1.start();


				}
				else {
					Thread t1 = new Thread(new Runnable() {
					     public void run() {
					    	 try{
								userproto.Callback proxy = SpecificRequestor.getClient(userproto.Callback.class, client);
								CallFuture<Integer> future = new CallFuture<Integer>();
								proxy.election(id, future);
							}catch(IOException e){}
					     }
					});  
					t1.start();
				}
			}catch(IOException e){}

		}
		
		if (id < this.id ) {
			int myid = this.id;
			//forward election with my id
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(clients.get(next).getAddress().toString()),clients.get(next).getId()));
				if(type == "User"){
					Thread t1 = new Thread(new Runnable() {
					     public void run() {
					    	 try{
								userproto.Callback proxy = SpecificRequestor.getClient(userproto.Callback.class, client);
								CallFuture<Integer> future = new CallFuture<Integer>();
								proxy.election(myid, future);
							}catch(IOException e){}
					     }
					});  
					t1.start();				}
				else {
					Thread t1 = new Thread(new Runnable() {
					     public void run() {
					    	 try{
								fridgeproto.Callback proxy = SpecificRequestor.getClient(fridgeproto.Callback.class, client);
								CallFuture<Integer> future = new CallFuture<Integer>();
								proxy.election(myid, future);
							}catch(IOException e){}
					     }
					});  
					t1.start();	
				}
			}catch(IOException e){}
		}
		
		if (id == this.id){
			
			System.out.println("Yay");
			
			//send elected
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(clients.get(next).getAddress().toString()),clients.get(next).getId()));
				if(type == "User"){
					Thread t1 = new Thread(new Runnable() {
					     public void run() {
					    	 try{
								userproto.Callback proxy = SpecificRequestor.getClient(userproto.Callback.class, client);
								CallFuture<Integer> future = new CallFuture<Integer>();
								proxy.election(id, future);
							}catch(IOException e){}
					     }
					});  
					t1.start();	
				}
				else {
					Thread t1 = new Thread(new Runnable() {
					     public void run() {
					    	 try{
								fridgeproto.Callback proxy = SpecificRequestor.getClient(fridgeproto.Callback.class, client);
								CallFuture<Integer> future = new CallFuture<Integer>();
								proxy.election(id, future);
							}catch(IOException e){}
					     }
					});  
					t1.start();	

				}
			}catch(IOException e){}
		}
		
		return 0;
	}
	
	@Override
	public int elected(int id) {
		
		int me = 0;
		int next = 0;
		String type = "";
		
		for(int i = 0; i < clients.size(); i++) {
			
			if( clients.get(i).getId() == this.id ){
				
				me = i;
				break;
			}
		}
		
		for(int i = me + 1; i <= clients.size(); i++) {
			
			if (i >= clients.size() - 1){
				
				i = 0;
			}
			
			if (clients.get(i).getType().toString().equals("User") || clients.get(i).getType().toString().equals("Fridge") ){
				
				if (clients.get(i).getType().toString().equals("User")) {
					
					type = "User";
				}
				else {
					type = "Fridge";
				}
				next = i;
				break;
			}
		}
		
		if (id != this.id){
			
			//forward elected
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(clients.get(next).getAddress().toString()),clients.get(next).getId()));
				if(type == "User"){
					Thread t1 = new Thread(new Runnable() {
					     public void run() {
					    	 try{
								userproto.Callback proxy = SpecificRequestor.getClient(userproto.Callback.class, client);
								CallFuture<Integer> future = new CallFuture<Integer>();
								proxy.election(id, future);
							}catch(IOException e){}
					     }
					});  
					t1.start();	
				}
				else {
					
					Thread t1 = new Thread(new Runnable() {
					     public void run() {
					    	 try{
								fridgeproto.Callback proxy = SpecificRequestor.getClient(fridgeproto.Callback.class, client);
								CallFuture<Integer> future = new CallFuture<Integer>();
								proxy.election(id, future);
							}catch(IOException e){}
					     }
					});  
					t1.start();	
				}
			}catch(IOException e){}

		}
		
		return 0;
	}
	
	@Command
	public void  sendElection() {
		
		System.out.println("we started election");
		
		int me = 0;
		int next = 0;
		String type = "";
		
		for(int i = 0; i < clients.size(); i++) {
			
			if( clients.get(i).getId() == this.id ){
				
				me = i;
				break;
			}
		}
		
		System.out.println("my id is : " +  clients.get(me).getId());

		
		for(int i = me + 1; i <= clients.size(); i++) {
			
			if (i > clients.size() - 1){
				
				i = 0;
			}
			
			if (clients.get(i).getType().toString().equals("User") || clients.get(i).getType().toString().equals("Fridge") ){
				
				if (clients.get(i).getType().toString().equals("User")) {
					
					type = "User";
				}
				else {
					type = "Fridge";
				}
				next = i;
				break;
			}
		}
		System.out.println("next candidate id is : " +  clients.get(next).getId());

		try{
			System.out.println(clients.get(next).getId());
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(clients.get(next).getAddress().toString()),clients.get(next).getId()));
			if(type == "User"){
				userproto.Callback proxy = SpecificRequestor.getClient(userproto.Callback.class, client);
				CallFuture<Integer> future = new CallFuture<Integer>();
				proxy.election(this.id, future);
			}
			else {
				
				fridgeproto.Callback proxy = SpecificRequestor.getClient(fridgeproto.Callback.class, client);
				CallFuture<Integer> future = new CallFuture<Integer>();
				proxy.election(this.id, future);
			}
		}catch(IOException e){}
	}

	
	@Command
	public void printLights(){
		if(!fridgeTime){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(this.getControllerAddress()),controllerport));
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
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(this.getControllerAddress()),controllerport));
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
	public void changeLight(int id){
		if(!fridgeTime){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(this.getControllerAddress()),controllerport));
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
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(this.getControllerAddress()),controllerport));
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
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(this.getControllerAddress()),controllerport));
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
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(this.getControllerAddress()),controllerport));
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
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(this.getControllerAddress()),controllerport));
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
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(this.getControllerAddress()),controllerport));
				serverproto proxy = (serverproto) SpecificRequestor.getClient(serverproto.class, client);
				Fridgestate s = proxy.openAFridge(id,this.id);
				CharSequence addr = s.getAddress();
				int open = s.getId();
				if(open == 0){
					System.out.println("Fridge with id: " + id + " has been opened.");
					fridgeTime = true;
					fridge.setId(id);
					fridge.setAddress(addr);
				}
				else if(open == -2){
					System.out.println("Fridge with id: " + id + " is already being used.");
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
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(fridge.getAddress().toString()),id));
				fridgeproto proxy = (fridgeproto) SpecificRequestor.getClient(fridgeproto.class, client);
				if(proxy.addItem(id, item) == 0){
					System.out.println("Added item: " + item + " to fridge with id: "  + id);
				}
				client.close();
			}catch (IOException e){
				
			}
		}
		else{
			System.out.println("Right now you are connected to a controller");
		}
	}
	
	@Command
	public void removeItemfromFridge(int id,CharSequence item){
		if(fridgeTime){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(fridge.getAddress().toString()),id));
				fridgeproto proxy = (fridgeproto) SpecificRequestor.getClient(fridgeproto.class, client);
				int removed = proxy.removeItem(id, item);
				if(removed == 0){
					System.out.println("removed item: " + item + " from fridge with id: "  + id);
				}
				else {
					System.out.println("The item " + item + " was not in the fridge");
				}
				client.close();
			}catch (IOException e){
				
			}
		}
		else{
			System.out.println("Right now you are connected to a controller");
		}
	}
	
	@Command
	public void closeFridge(int id){
		if(fridgeTime){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(fridge.getAddress().toString()),id));
				fridgeproto proxy = (fridgeproto) SpecificRequestor.getClient(fridgeproto.class, client);
				int status = proxy.closeFridge(id);
				if(status  == 0){
					fridgeTime = false;
					System.out.println("fridge with id: "  + id +  " has been closed");
					fridge.setId(0);
				}
				else if (status > 0){
					System.out.println("fridge with id: "  + id +  " is still in use");
					fridgeTime = false;
				}
				client.close();
			}catch (IOException e){
				
			}
		}
		else{
			System.out.println("Right now you are connected to a controller");
		}
	}
	
	@Command
	public void printbackup(){
		
		for(Clientinfo temp : clients){
			
			System.out.println("We have a client of type: " + temp.getType() + " with id: " +  temp.getId());
		}
		for(Lightinfo temp : lights){
			
			System.out.println("We have a light with id: " +  temp.getId());
		}
		
		for(Userinfo temp : users){
			
			System.out.println("We have a user with id: " +  temp.getId());
		}
		
	}


	public static void main(String[] args) {
		Server server = null;
		try {
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(args[0]),5000));
			serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
			
			int id = proxy.connect("User",args[1]);
			User Bob = new User(id,"Bobby", args[0],args[1]);

			client.close();

			server = new SaslSocketServer(new SpecificResponder(userproto.class, Bob), new InetSocketAddress(InetAddress.getByName(Bob.getAddress()),Bob.getId()));
			server.start();
			
	        Timer timer = new Timer();
	        timer.schedule(new TimerTask() {
	            @Override
	            public void run() {
	                Bob.run();
	            }
	        }, 0, 5000);
			ShellFactory.createConsoleShell("user", "", Bob).commandLoop();
			System.exit(1);
		} catch(IOException e){
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);
		}
		try {
			server.join();
		}	catch ( InterruptedException e) { 
			
		}
	}
}
