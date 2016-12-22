package avro.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.avro.ipc.CallFuture;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;

import avro.proto.serverproto;
import avro.proto.userproto;
import avro.proto.Clientinfo;
import avro.proto.Lightinfo;
import avro.proto.TSinfo;
import avro.proto.Userinfo;
import avro.proto.fridgeproto;

import avro.server.Controller;


public class Fridge extends Controller implements fridgeproto  {
	
	private List<CharSequence> items;
	private int id;
	private boolean open;
	private Userinfo connected;
	private String address;


	public Fridge(int id, String conaddr, String addr) {
		super(conaddr);
		this.address = addr;
		items = new ArrayList<CharSequence>();
		this.id = id;
		open = false;
		connected = new Userinfo(0,true,"");
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getAddress(){
		return this.address;
	}
	
	public void run() {
		if ( connected.getId() != 0){
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(connected.getAddress().toString()),connected.getId()));
			}catch( IOException e){
				open = false;
				System.out.println("Connection to user with id " + connected.getId() + " lost");
				connected.setId(0);
			}
		}	
	}
	
	public List<CharSequence> getItems(){
		return items;
	}
	
	@Override
	public List<CharSequence> sendItems(int id){
		return items;
	}
	
	@Override
	public int openFridge(int id, int userid,CharSequence useraddr){
		if(open){	
			return -2;
		}
		if(id == this.id){
			open = true;
			connected.setId(userid);
			connected.setAddress(useraddr);
			return 0;
		}
		return -1;
	}
	
	@Override
	public int addItem(int id, CharSequence item){
		if((id == this.id) && (open )){
			items.add(item);
			return 0;
		}
		return -1;
	}
	
	@Override
	public int removeItem(int id, CharSequence item){
		if(id == this.id && open){
			for(CharSequence temp : items){
				if (temp.equals(item)){
					items.remove(item);
					if(items.size() == 0){
						try{
							Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(this.getControllerAddress()),5000));
							serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
							proxy.FridgeEmptyMessage(this.id);
						}catch(IOException e){}
					}
					return 0;
				}
			}
		}
		return -1;
	}
	
	@Override
	public int closeFridge(int id){
		if(id == this.id){
			open = false;
			connected.setId(0);
			connected.setAddress("");

			return 0;
		}
		return -1;
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
		
		int me = 0;
		int next = 0;
		String type = "";
		
		for(int i = 0; i < clients.size(); i++) {
			
			if( clients.get(i).getId() == this.id ){
				
				me = i;
				break;
			}
		}
		
		for(int i = me + 1; i < clients.size(); i++) {
			
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
					userproto.Callback proxy = SpecificRequestor.getClient(userproto.Callback.class, client);
					CallFuture<Integer> future = new CallFuture<Integer>();
					proxy.election(id, future);
				}
				else {
					
					fridgeproto.Callback proxy = SpecificRequestor.getClient(fridgeproto.Callback.class, client);
					CallFuture<Integer> future = new CallFuture<Integer>();
					proxy.election(id, future);
				}
			}catch(IOException e){}

		}
		
		if (id < this.id ) {
			
			//forward election with my id
			try{
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
		
		if (id == this.id){
			
			//send elected
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(clients.get(next).getAddress().toString()),clients.get(next).getId()));
				if(type == "User"){
					userproto.Callback proxy = SpecificRequestor.getClient(userproto.Callback.class, client);
					CallFuture<Integer> future = new CallFuture<Integer>();
					proxy.elected(this.id, future);
				}
				else {
					
					fridgeproto.Callback proxy = SpecificRequestor.getClient(fridgeproto.Callback.class, client);
					CallFuture<Integer> future = new CallFuture<Integer>();
					proxy.elected(this.id, future);
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
		
		for(int i = me + 1; i < clients.size(); i++) {
			
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
					userproto.Callback proxy = SpecificRequestor.getClient(userproto.Callback.class, client);
					CallFuture<Integer> future = new CallFuture<Integer>();
					proxy.elected(id, future);
				}
				else {
					
					fridgeproto.Callback proxy = SpecificRequestor.getClient(fridgeproto.Callback.class, client);
					CallFuture<Integer> future = new CallFuture<Integer>();
					proxy.elected(id, future);
				}
			}catch(IOException e){}

		}
		
		return 0;
	}

	public static void main(String[] args) {
		Server server = null;
		try {
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(args[0]),5000));
			serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
			
			int id = proxy.connect("Fridge",args[1]);
			Fridge kastje = new Fridge(id,args[0],args[1]);
			
			server = new SaslSocketServer(new SpecificResponder(fridgeproto.class, kastje), new InetSocketAddress(InetAddress.getByName(kastje.getAddress()),kastje.getId()));
			server.start();
			
	        Timer timer = new Timer();
	        timer.schedule(new TimerTask() {
	        	@Override
	            public void run() {
	                kastje.run();
	            }
	        }, 0, 5000);
		} catch(IOException e){
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);
		}
		try {
			server.join();
		} catch ( InterruptedException e) {
			
		}
	}
}
