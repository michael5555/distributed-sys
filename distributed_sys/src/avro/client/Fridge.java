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
import avro.proto.tsproto;
import avro.proto.userproto;
import avro.proto.Clientinfo;
import avro.proto.Controllerinfo;
import avro.proto.Lightinfo;
import avro.proto.TSinfo;
import avro.proto.Userinfo;
import avro.proto.fridgeproto;
import avro.proto.lightproto;
import avro.server.Controller;


public class Fridge extends Controller implements fridgeproto,serverproto  {
	
	private List<CharSequence> items;
	private int id;
	private int conid;

	private boolean open;
	private Userinfo connected;
	private String address;
	private boolean participant = false;
	private int controllerport = 5000;
	private Controllerinfo oldcontroller;

	
	public Fridge(int id, String conaddr, String addr) {
		super(conaddr);
		this.oldcontroller =  new Controllerinfo(5000,conaddr);

		this.address = addr;
		items = new ArrayList<CharSequence>();
		this.id = id;
		this.conid = id - 2000;
		open = false;
		connected = new Userinfo(0,true,"");
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getConId() {
		return this.conid;
	}
	
	public String getAddress() {
		return this.address;
	}
	public int getControllerPort() {
		return this.controllerport;
	}
	
	public void checkuser() {
		if (connected.getId() != 0) {
			try {
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(connected.getAddress().toString()),connected.getId()));
				client.close();
			} catch (IOException e) {
				open = false;
				System.out.println("Connection to user with id " + connected.getId() + " lost");
				connected.setId(0);
			}
		}	
	}
	
	public synchronized void checkcontroller(){
		if (controllerport != this.conid) {
			try{
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(conaddress,controllerport));
				serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
				proxy.reconnect("Fridge", address, id);
				client.close();
			}catch(IOException e){
				sendElection();
			}
		}
	}
	
	public List<CharSequence> getItems() {
		return items;
	}
	
	@Override
	public List<CharSequence> sendItems(int id) {
		return items;
	}
	
	@Override
	public int openFridge(int id, int userid,CharSequence useraddr) {
		if (open) {	
			return -2;
		}
		if (id == this.id) {
			open = true;
			connected.setId(userid);
			connected.setAddress(useraddr);
			return 0;
		}
		return -1;
	}
	
	@Override
	public int addItem(int id, CharSequence item) {
		if ((id == this.id) && (open )) {
			items.add(item);
			return 0;
		}
		return -1;
	}
	
	@Override
	public int removeItem(int id, CharSequence item) {
		if (id == this.id && open) {
			for (CharSequence temp : items) {
				if (temp.equals(item)) {
					items.remove(item);
					if (items.size() == 0) {
						try {
							Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(this.getControllerAddress()),controllerport));
							serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
							proxy.FridgeEmptyMessage(this.id);
						} catch (IOException e ){
							this.sendElection();
						}
					}
					return 0;
				}
			}
		}
		return -1;
	}
	
	@Override
	public int closeFridge(int id) {
		if (id == this.id) {
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
	
	
	public int getNextIdAndType() {
		int me = 0;
		
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getId() == this.id) {
				me = i;
				break;
			}
		}
		for (int i = me + 1; i <= clients.size(); i++) {
			if (i > clients.size() - 1) {
				i = 0;
			}
			if (clients.get(i).getType().toString().equals("User") || clients.get(i).getType().toString().equals("Fridge")) {
				int next = i;
				return next;
			}
		}
		return -1;
	}
	
	public void sendElectionMessage(int next,String type,int id) {
		try {
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(clients.get(next).getAddress().toString()),clients.get(next).getId()));
			if (type == "User") {
				Thread t1 = new Thread(new Runnable() {
				     public void run() {
				    	 try {
							userproto.Callback proxy = SpecificRequestor.getClient(userproto.Callback.class, client);
							CallFuture<Integer> future = new CallFuture<Integer>();
							proxy.election(id, future);
						} catch (IOException e) {
							
						}
				     }
				});  
				t1.start();
			}
			else {
				Thread t1 = new Thread(new Runnable() {
				     public void run() {
				    	 try {
							fridgeproto.Callback proxy = SpecificRequestor.getClient(fridgeproto.Callback.class, client);
							CallFuture<Integer> future = new CallFuture<Integer>();
							proxy.election(id, future);
						} catch (IOException e) {
							
						}
				     }
				});  
				t1.start();
			}
		}catch(IOException e){
			
		}
	}
	
	public void sendElectedMessage(int next,String type,int id) {
		try {
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(clients.get(next).getAddress().toString()),clients.get(next).getId()));
			if (type == "User") {
				Thread t1 = new Thread(new Runnable() {
				     public void run() {
				    	 try {
							userproto.Callback proxy = SpecificRequestor.getClient(userproto.Callback.class, client);
							CallFuture<Integer> future = new CallFuture<Integer>();
							proxy.elected(id, future);
						} catch (IOException e) {
							
						}
				     }
				});  
				t1.start();	
			}
			else {
				Thread t1 = new Thread(new Runnable() {
				     public void run() {
				    	 try {
							fridgeproto.Callback proxy = SpecificRequestor.getClient(fridgeproto.Callback.class, client);
							CallFuture<Integer> future = new CallFuture<Integer>();
							proxy.elected(id, future);
						} catch (IOException e) {
							
						}
				     }
				});  
				t1.start();	
			}
		}catch(IOException e){
			
		}
	}

	
	@Override
	public int election(int id) {
		int next = getNextIdAndType();
		String type = clients.get(next).getType().toString();
		
		if (id > this.id) {
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
		if (id == this.id) {
			participant = false;
			System.out.println("I have been elected controller. My id: " + this.id);
			
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
	
	public void  sendElection() {
		System.out.println("original controller is offline, we have started election");
		int next = getNextIdAndType();
		String type = clients.get(next).getType().toString();
		
		participant = true;
		sendElectionMessage(next, type, this.id);
	}
	
	public void ControllerHandOff() {
		for (Clientinfo temp : clients) {
			if (temp.getType().toString().equals("User")) {
				try {
					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
					userproto proxy = (userproto) SpecificRequestor.getClient(userproto.class, client);
					proxy.setcontrollerinfo(this.conid, this.address);
				} catch (IOException e) {
					
				}
			}
			else if (temp.getType().toString().equals("Light")) {
				try {
					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
					lightproto proxy = (lightproto) SpecificRequestor.getClient(lightproto.class, client);
					proxy.setcontrollerinfo(this.conid, this.address);
				} catch (IOException e) {
					
				}
			}
			else if (temp.getType().toString().equals("Fridge")) {
				try {
					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
					fridgeproto proxy = (fridgeproto) SpecificRequestor.getClient(fridgeproto.class, client);
					proxy.setcontrollerinfo(this.conid, this.address);
				} catch(IOException e) {
					
				}
			}
			else if (temp.getType().toString().equals("TS")) {
				try {
					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
					tsproto proxy = (tsproto) SpecificRequestor.getClient(tsproto.class, client);
					proxy.setcontrollerinfo(this.conid, this.address);
				} catch (IOException e) {
					
				}
			}
		}
		
		for(int i = 0; i < clients.size();i++) {
			if (clients.get(i).getId() == this.id) {
				clients.remove(i);
				break;
			}
		}
		
		Server server2 = null;
		try {
			server2 = new SaslSocketServer(new SpecificResponder(serverproto.class, this), new InetSocketAddress(InetAddress.getByName(this.getAddress()),this.getConId()));
			server2.start();
		} catch (IOException e) {
			
		}
		
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
                if(pollOldController(oldcontroller) == 0){
                	
            		clients.add(new Clientinfo(id,"Fridge",address));
            		
            		System.out.println("Old controller has reconnected.");
            		
            		try{
            			
            			
            			
    					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(oldcontroller.getAddress().toString()),oldcontroller.getId()));
    					serverproto proxy = (serverproto) SpecificRequestor.getClient(serverproto.class, client);
    					proxy.resyncClients(clients);
    					proxy.resyncUsers(users);
    					proxy.resyncLights(lights);
    					proxy.resyncMeasurements(measurements);

    					

            		}catch(IOException e) {}
            		
            		timer.cancel();
                }
            }
        }, 0, 5000);
        
        
		try {
			server2.join();
		} catch (InterruptedException e) {
			
		}
	}
	
	@Override
	public int setcontrollerinfo(int port, CharSequence address){
		conaddress = address.toString();
		controllerport = port;
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
			
			proxy.update();

			client.close();
			
	        Timer timer = new Timer();
	        timer.schedule(new TimerTask() {
	        	@Override
	            public void run() {
	                kastje.checkuser();
	            }
	        }, 0, 5000);
	        
	        Timer timer2 = new Timer();
	        timer2.schedule(new TimerTask() {
	        	@Override
	            public void run() {
	        		kastje.checkcontroller();
	            }
	        }, 0, 5000);
		} catch(IOException e){
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);
		}
		try {
			server.join();

		} catch (InterruptedException e) {
			
		}
	}
}
