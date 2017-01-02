package avro.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.ArrayList;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;

import avro.proto.serverproto;
import avro.proto.lightproto;
import avro.proto.userproto;
import avro.proto.fridgeproto;
import avro.proto.tsproto;

import avro.proto.Fridgestate;
import avro.proto.Lightinfo;
import avro.proto.Clientinfo;
import avro.proto.Controllerinfo;

import avro.proto.Userinfo;
import avro.proto.TSinfo;


public class Controller implements serverproto {

	private final int port = 5000;
	private int id;
	protected String conaddress;
	
	protected List<Clientinfo> clients;
	protected List<Userinfo> users;
	protected List<Lightinfo> lights;
	protected List<List<TSinfo> > measurements;
	
	
	public Controller(String addr){
		this.id = 1;
		conaddress = addr;
		clients = new ArrayList<Clientinfo>();
		users = new ArrayList<Userinfo>();
		lights = new ArrayList<Lightinfo>();
		measurements = new ArrayList<List<TSinfo>>();
	}
	
	public String getControllerAddress() {
		return this.conaddress;
	}
	
	public int getPort() {
		return this.port;
	}
	
	@Override
	public synchronized int update() {
		for (int i  = 0; i < clients.size();i++) {
			try {
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(clients.get(i).getAddress().toString()),clients.get(i).getId()));
				if (clients.get(i).getType().toString().equals("User")) {
					userproto proxy =  (userproto) SpecificRequestor.getClient(userproto.class, client);
					proxy.syncClients(this.clients);
					proxy.syncLights(this.lights);
					proxy.syncMeasurements(this.measurements);
					proxy.syncUsers(this.users);
				}
				else if (clients.get(i).getType().toString().equals("Fridge")) {
					fridgeproto proxy =  (fridgeproto) SpecificRequestor.getClient(fridgeproto.class, client);
					proxy.syncClients(this.clients);
					proxy.syncLights(this.lights);
					proxy.syncMeasurements(this.measurements);
					proxy.syncUsers(this.users);
				}
			} catch(IOException e) {
				deleteClient(clients.get(i).getId());
				i--;
			}	
		}
		return 0;
	}
	
	@Override
	public synchronized int resyncClients(List<Clientinfo> clients) {
		this.clients = clients;
		return 0;
	}
	
	@Override
	public synchronized int resyncUsers(List<Userinfo> users) {
		this.users = users;
		return 0;
	}
	
	@Override
	public synchronized int resyncLights(List<Lightinfo> lights) {
		this.lights = lights;
		return 0;
	}
	
	@Override
	public synchronized int resyncMeasurements(List<List<TSinfo>> measurements) {
		this.measurements = measurements;
		return 0;
	}
	
	public synchronized int pollOldController(Controllerinfo oldcontroller){
		
    	try {
    		int oldid = oldcontroller.getId();
    		String oldaddress = oldcontroller.getAddress().toString();
    		Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(oldaddress),oldid));
    		for(Clientinfo temp : clients) {
    			
    			if(temp.getType().toString().equals("User")) {
    				try{

    					Transceiver client2 = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
    					userproto proxy = (userproto) SpecificRequestor.getClient(userproto.class, client2);
    					proxy.setcontrollerinfo(oldid, oldaddress);
    				}catch(IOException e) {}

    				
    			}
    			else if(temp.getType().toString().equals("Light")) {
    				try{

    					Transceiver client2 = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
    					lightproto proxy = (lightproto) SpecificRequestor.getClient(lightproto.class, client2);
    					proxy.setcontrollerinfo(oldid, oldaddress);
    				}catch(IOException e) {}

    				
    			}
    			else if(temp.getType().toString().equals("Fridge")) {
    				try{

    					Transceiver client2 = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
    					fridgeproto proxy = (fridgeproto) SpecificRequestor.getClient(fridgeproto.class, client2);
    					proxy.setcontrollerinfo(oldid, oldaddress);
    				}catch(IOException e) {}

    				
    			}
    			else if(temp.getType().toString().equals("TS")) {
    				try{

    					Transceiver client2 = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
    					tsproto proxy = (tsproto) SpecificRequestor.getClient(tsproto.class, client2);
    					proxy.setcontrollerinfo(oldid, oldaddress);
    				}catch(IOException e) {}

    				
    			}
    		}

    	}catch(IOException e){
    		return -1;
    	}
    	
		return 0;
	}
	
	@Override
	public synchronized int connect(CharSequence type2,CharSequence address) throws AvroRemoteException {
		if (type2.toString().equals("User")) {
			users.add(new Userinfo(port + id,true,address));
		}
		else if (type2.toString().equals("Light")) {
			lights.add(new Lightinfo(port + id,false,address));
		}
		else if (type2.toString().equals("TS")) {
			List<TSinfo> newlist = new ArrayList<TSinfo>();
			newlist.add(new TSinfo(port + id,0.0,address));
			measurements.add(newlist);
		}
		clients.add(new Clientinfo(port + id,type2,address));
		System.out.println(" Client connected: " + type2 + " (number: " +  (port + id) + " )");
		return port + this.id++;
	}
	
	@Override
	public synchronized int reconnect(CharSequence type2,CharSequence address, int id) throws AvroRemoteException {
		
		for(Clientinfo temp : clients){
			
			if(id == temp.getId()){
				
				return 0;
			}
		}
		if (type2.toString().equals("User")) {
			users.add(new Userinfo(id,true,address));
		}
		else if (type2.toString().equals("Light")) {
			lights.add(new Lightinfo(id,false,address));
		}
		else if (type2.toString().equals("TS")) {
			List<TSinfo> newlist = new ArrayList<TSinfo>();
			newlist.add(new TSinfo(id,0.0,address));
			measurements.add(newlist);
		}
		clients.add(new Clientinfo(id,type2,address));
		System.out.println(" Client reconnected: " + type2 + " (number: " +  id + " )");
		return 0;
	}
	
	@Override
	public List<Lightinfo> sendLights (int id) throws AvroRemoteException {
		for (Userinfo temp : users) {
			if (temp.getId() == id) {
				return lights;
			}
		}
		return new ArrayList<Lightinfo>();
	}
	
	@Override
	public List<Clientinfo> sendClients (int id) throws AvroRemoteException {
		for (Userinfo temp : users) {
			if (temp.getId() == id) {
				return clients;
			}
		}
		return new ArrayList<Clientinfo>();
	}
	
	@Override
	public synchronized int changeLightStatus(int id) {
		for (Lightinfo temp : lights) {
			if (id == temp.getId()) {
				try {
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),id));
				lightproto proxy =  (lightproto) SpecificRequestor.getClient(lightproto.class, client);
				proxy.changeStatus(id,!temp.getStatus());
				temp.setStatus(!temp.getStatus());
				client.close();
				} catch (IOException e) {
					System.err.println("Error connecting to light ...");
					e.printStackTrace(System.err);
					System.exit(1);
				}
			}
		}
		return 0;
	}
	
	public boolean nobodyAtHome() {
		for (Userinfo temp: users) {
			if (temp.getAthome() == true) {
				return false;
			}
		}
		return true;
	}

	@Override
	public synchronized int changeHomeStatus(int id) {
		boolean homestatus = false;
		for (Userinfo temp: users) {
			if (id == temp.getId()) {
				temp.setAthome(!temp.getAthome());
				homestatus = temp.getAthome();
				break;
			}
		}
		for (Userinfo temp: users) {
			if (id != temp.getId()) {
				try {
					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
					userproto prox =  (userproto) SpecificRequestor.getClient(userproto.class, client);
					prox.reportUserStatus(id,homestatus);
					client.close();
				} catch (IOException e) {
					System.err.println("Error connecting to user ...");
					e.printStackTrace(System.err);
					System.exit(1);
				}
			}
		}
		if (nobodyAtHome()) {
			for (Lightinfo temp : lights) {
				try {
					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
					lightproto proxy =  (lightproto) SpecificRequestor.getClient(lightproto.class, client);
					proxy.changeStatus(temp.getId(),false);
					client.close();
				} catch (IOException e) {
					System.err.println("Error connecting to light ...");
					e.printStackTrace(System.err);
					System.exit(1);
				}
			}
		}
		else {
			for (Lightinfo temp : lights) {
				try {
					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
					lightproto proxy =  (lightproto) SpecificRequestor.getClient(lightproto.class, client);
					proxy.changeStatus(temp.getId(),temp.getStatus());
					client.close();
				} catch (IOException e) {
					System.err.println("Error connecting to light ...");
					e.printStackTrace(System.err);
					System.exit(1);
				}
			}
		}
		return 0;
	}
	
	@Override
	public List<CharSequence> sendFridgeItems(int id) {
		for (Clientinfo temp : clients) {
			if (temp.getId() == id && temp.getType().toString().equals("Fridge")) {
				try {
					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
					fridgeproto proxy =  (fridgeproto) SpecificRequestor.getClient(fridgeproto.class, client);
					List<CharSequence> items = proxy.sendItems(temp.getId());
					client.close();
					return items;
				} catch (IOException e) {
					System.err.println("Error connecting to fridge ...");
					e.printStackTrace(System.err);
					System.exit(1);
				}
			}
		}
		return new ArrayList<CharSequence>();
	}
	
	@Override
	public synchronized int sendTSMeasurement(double measurement, int id) {	
		for (List<TSinfo> temp : measurements) {
			if (temp.get(0).getId() == id) {
				CharSequence addr = temp.get(0).getAddress();
				if (temp.size() == 1 && temp.get(0).getMeasurement() == 0.0) {
					temp.remove(0);
				}
				temp.add(new TSinfo(id,measurement,addr));
				if (temp.size() > 10) {
					temp.remove(0);
				}
				break;
			}
		}
		return 0;
	}
	
	@Override
	public double getCurrentTemperature(int id) {
		for (Userinfo temp : users) {
			if (temp.getId() == id) {				
				double value = 0.0;
				for (List<TSinfo> templist : measurements) {
					value += templist.get(templist.size() - 1).getMeasurement();
				}
				return value / measurements.size();		
			}
		}
		return -1;
	}
	
	@Override
	public List<Double> getTemperatureHistory(int id) {
		for (Userinfo temp : users) {
			if (temp.getId() == id) {
				List<Double> newlist = new ArrayList<Double>();
				for (int i = 0; i < measurements.get(0).size();i++) {
					newlist.add(0.0);
				}
				for (List<TSinfo> templist : measurements) {
					for (int i = 0; i < templist.size();i++) {
						double d = newlist.get(i);
						d += templist.get(i).getMeasurement();
						newlist.set(i,d);
					}
				}
				for (int i = 0; i < newlist.size(); i++) {
					double d = newlist.get(i);
					d = d/measurements.size();
					newlist.set(i, d);
				}
				return newlist;
			}
		}
		return new ArrayList<Double>();
	}
	
	@Override
	public Fridgestate openAFridge(int id, int userid) {
		CharSequence addr = "";
		CharSequence useraddr = "";
		for(Userinfo temp: users){
			if (temp.getId() == userid) {
				useraddr = temp.getAddress();
			}
		}
		for (Clientinfo temp : clients) {
			if (id == temp.getId() && temp.getType().toString().equals("Fridge")) {
				try {
					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
					fridgeproto proxy =  (fridgeproto) SpecificRequestor.getClient(fridgeproto.class, client);
					int i =  proxy.openFridge(id,userid,useraddr);
					addr = temp.getAddress();
					client.close();
					return new Fridgestate(i,addr);
				} catch (IOException e) {
					
				}
			}
		}
		return new Fridgestate(-1,addr);
	}
	
	@Override
	public int FridgeEmptyMessage(int id) {
		for (Userinfo temp: users) {
			try {
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(temp.getAddress().toString()),temp.getId()));
				userproto prox =  (userproto) SpecificRequestor.getClient(userproto.class, client);
				prox.reportFridgeEmpty(id);
				client.close();
			} catch (IOException e) {
				System.err.println("Error connecting to user ...");
				e.printStackTrace(System.err);
				System.exit(1);
			}
		}
		return 0;
	}
	
	@Override
	public synchronized int deleteClient(int id) {
		for (int temp = 0;temp < clients.size(); temp++) {
			if (id == clients.get(temp).getId()) {
				if (clients.get(temp).getType().toString().equals("User")) {
					for (int temp2 = 0; temp2 < users.size();temp2++) {
						if (id == users.get(temp2).getId()) {
							users.remove(temp2);
							break;
						}
					}
				}
				else if (clients.get(temp).getType().toString().equals("Light")) {
					for (int temp2 = 0; temp2 < lights.size();temp2++) {
						if (id == lights.get(temp2).getId()) {
							lights.remove(temp2);
							break;
						}
					}
				}
				else if (clients.get(temp).getType().toString().equals("TS")) {
					for (int temp2 = 0; temp2 < measurements.size();temp2++) {
						if (id == measurements.get(temp2).get(0).getId()) {
							measurements.remove(temp2);
							break;
						}
					}
				}
				clients.remove(temp);
				break;
			}		
		}
		return 0;
	}

	public static void main( String[] args) {
		Server server = null;
		Controller controller = new Controller(args[0]);
		
		try {
			server = new SaslSocketServer(new SpecificResponder(serverproto.class, controller), new InetSocketAddress(InetAddress.getByName(args[0]),controller.getPort()));
		} catch (IOException e) {
			System.err.println(" error Failed to start server");
			e.printStackTrace(System.err);
			System.exit(1);
		}
		server.start();
		
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                controller.update();
            }
        }, 0, 5000);
        
		try {
			server.join();
		}	catch (InterruptedException e) {
			
		}
	}
}