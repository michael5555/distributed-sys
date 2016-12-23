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
import avro.proto.tsproto;

import avro.proto.Fridgestate;

import avro.proto.userserverproto;


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

public class User extends Controller implements userproto,serverproto {
	
	private int id;
	private int conid;

	private boolean fridgeTime;
	private Fridgeinfo fridge;
	private String address;
	private boolean participant = false;
	
	private int controllerport = 5000;

	User(int id, String username, String conaddr,String addr){
		super(conaddr);
		this.address = addr;
		this.id = id;
		this.conid = id - 2000;
		fridgeTime = false;
		fridge = new Fridgeinfo(0,"");
	}
	
	public int getId(){
		return this.id;
	}
	public int getConId(){
		return this.conid;
	}
	
	public String getAddress(){
		return this.address;
	}
	
	public void checkfridge() {
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
	
	public int getNextIdAndType(){
		int me = 0;
		
		for(int i = 0; i < clients.size(); i++) {
			
			if( clients.get(i).getId() == this.id ){
				
				me = i;
				break;
			}
		}
		for(int i = me + 1; i <= clients.size(); i++) {
			
			if (i > clients.size() - 1){
				
				i = 0;
			}
			
			if (clients.get(i).getType().toString().equals("User") || clients.get(i).getType().toString().equals("Fridge") ){

				int next = i;
				return next;
			}
		}
		return -1;
	}
	
	public void  sendElectionMessage(int next,String type,int id) {
		
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
	
	public void  sendElectedMessage(int next,String type,int id) {
		
		try{
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(clients.get(next).getAddress().toString()),clients.get(next).getId()));
			if(type == "User"){
				Thread t1 = new Thread(new Runnable() {
				     public void run() {
				    	 try{
							userproto.Callback proxy = SpecificRequestor.getClient(userproto.Callback.class, client);
							CallFuture<Integer> future = new CallFuture<Integer>();
							proxy.elected(id, future);
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
							proxy.elected(id, future);
						}catch(IOException e){}
				     }
				});  
				t1.start();	

			}
		}catch(IOException e){}
	}

	
	@Override
	public int election(int id) {
		
		
		int next = getNextIdAndType();
		
		String type = clients.get(next).getType().toString();
		

		
		if(id > this.id) {
			//forward election
			
			sendElectionMessage(next, type, id);
			participant = true;



		}
		
		if (id < this.id ) {
			//forward election with my id
			if(participant == false){
				sendElectionMessage(next, type, this.id);
				participant = true;

			}
		}
		
		if (id == this.id){
			participant = false;
			System.out.println("Yay");
			
			//send elected
			sendElectedMessage(next, type, id);
			ControllerHandOff();


		}
		
		return 0;
	}
	
	@Override
	public int elected(int id) {
		
		int next = getNextIdAndType();
		
		String type = clients.get(next).getType().toString();
		
		if (id != this.id){
			participant = false;
			//forward elected
			sendElectedMessage(next, type, id);

		}

		
		return 0;
	}
	
	@Command
	public void  sendElection() {
		
		System.out.println("we started election");
		
		int next = getNextIdAndType();
		
		String type = clients.get(next).getType().toString();
		
		
		participant = true;
		sendElectionMessage(next, type, this.id);
	}
	
	@Override
	public int setcontrollerinfo(int port, CharSequence address){
		
		controllerport = port;
		conaddress = address.toString();
		
		return 0;
	}
	
	public void ControllerHandOff() {
		
		for(int i = 0; i < clients.size();i++) {
			
			if( clients.get(i).getId() == this.id) {
				
				clients.remove(i);
				break;
			}
		}
		
		for(Userinfo temp : users) {
			
			if( temp.getId() == this.id) {
				
				users.remove(temp);
			}
		}
		
		for(Clientinfo temp : clients) {
			
			if(temp.getType().toString().equals("User")) {
				try{

					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
					userproto proxy = (userproto) SpecificRequestor.getClient(userproto.class, client);
					proxy.setcontrollerinfo(this.conid, this.address);
				}catch(IOException e) {}

				
			}
			else if(temp.getType().toString().equals("Light")) {
				try{

					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
					lightproto proxy = (lightproto) SpecificRequestor.getClient(lightproto.class, client);
					proxy.setcontrollerinfo(this.conid, this.address);
				}catch(IOException e) {}

				
			}
			else if(temp.getType().toString().equals("Fridge")) {
				try{

					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
					fridgeproto proxy = (fridgeproto) SpecificRequestor.getClient(fridgeproto.class, client);
					proxy.setcontrollerinfo(this.conid, this.address);
				}catch(IOException e) {}

				
			}
			else if(temp.getType().toString().equals("TS")) {
				try{

					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
					tsproto proxy = (tsproto) SpecificRequestor.getClient(tsproto.class, client);
					proxy.setcontrollerinfo(this.conid, this.address);
				}catch(IOException e) {}

				
			}

		}
		
        /*Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                this.run();
            }
        }, 0, 5000);*/
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
			}catch(IOException e){
				sendElection();
			}
		}
		else{
			System.out.println("Right now you are connected to a Fridge");
		}
	}
	
	@Command
	public void printClients(){
		if(!fridgeTime){
			try{
				System.out.println(this.getControllerAddress());
				System.out.println("controller port: " + controllerport);

				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(this.getControllerAddress()),controllerport));
				serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
				List<Clientinfo> clients = proxy.sendClients(this.id);
				client.close();
				for(Clientinfo temp : clients){
					System.out.println("We have a client with id: " + temp.getId() + " ,its type is: " +  temp.getType());
				}
			}catch(IOException e){
				sendElection();

			}
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
			}catch(IOException e){
				sendElection();

			}
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

			}catch(IOException e){
				sendElection();

			}
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
		
			}catch(IOException e){
				sendElection();

			}
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
		
			}catch(IOException e){
				sendElection();

			}
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
			}catch(IOException e){
				sendElection();

			}
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
			}catch(IOException e){
				sendElection();

			}
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
		Server server2 = null;

		try {
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(args[0]),5000));
			serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
			
			int id = proxy.connect("User",args[1]);
			User Bob = new User(id,"Bobby", args[0],args[1]);

			client.close();

			server = new SaslSocketServer(new SpecificResponder(userproto.class, Bob), new InetSocketAddress(InetAddress.getByName(Bob.getAddress()),Bob.getId()));
			server2 = new SaslSocketServer(new SpecificResponder(serverproto.class, Bob), new InetSocketAddress(InetAddress.getByName(Bob.getAddress()),Bob.getConId()));

			server.start();
			server2.start();

			
	        Timer timer = new Timer();
	        timer.schedule(new TimerTask() {
	            @Override
	            public void run() {
	                Bob.checkfridge();
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
			server2.join();

		}	catch ( InterruptedException e) { 
			
		}
	}
}
