package avro.server;

import java.io.IOException;
import java.net.InetAddress;
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
import avro.proto.userproto;
import avro.proto.fridgeproto;

import java.util.Timer;
import java.util.TimerTask;






import avro.proto.Lightinfo;
import avro.proto.Clientinfo;
import avro.proto.Userinfo;
import avro.proto.TSinfo;





import java.util.List;
import java.util.ArrayList;

public class Controller  implements serverproto {

	private final int  port = 5000;
	private int id;
	private List<Clientinfo> clients;
	private List<Userinfo> users;
	private List<Lightinfo> lights;
	private List<List<TSinfo> > measurements;
	
	public Controller(){
		this.id = 1;
		clients = new ArrayList<Clientinfo>();
		users = new ArrayList<Userinfo>();
		lights = new ArrayList<Lightinfo>();
		measurements = new ArrayList<List<TSinfo>>();
	}
	
	public int getPort(){
		return this.port;
	}
	
	public void run() {
		for(int i  = 0; i < clients.size();i++){
			try {
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),clients.get(i).getId()));
			} catch(IOException e){
				deleteClient(clients.get(i).getId());
				i--;
			}	
		}
	}
	
	@Override
	public int connect(CharSequence type2) throws AvroRemoteException
	{
		if(type2.toString().equals("User")){
			users.add( new Userinfo(port + id,true) );
		}
		else if(type2.toString().equals("Light")){
			lights.add( new Lightinfo(port + id,false) );
		}
		else if (type2.toString().equals("TS")){
			List<TSinfo> newlist = new ArrayList<TSinfo>();
			newlist.add(new TSinfo( port + id,0.0));
			measurements.add(newlist);
		}
		clients.add(new Clientinfo(port + id,type2));
		System.out.println(" Client connected: " + type2 + " (number: " +  (port + id) + " )");
		return  port + this.id++;
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
				return 0;
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
	public List<Clientinfo> sendClients (int id) throws AvroRemoteException 
	{
		for(Userinfo temp : users){
			if(temp.getId() == id){
				return clients;
			}
		}
			return new ArrayList<Clientinfo>();
	}
	
	@Override
	public int changeLightStatus(int id){
		for(Lightinfo temp : lights){
			if( id == temp.getId()){
				try {
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),id));
				lightproto proxy =  (lightproto) SpecificRequestor.getClient(lightproto.class, client);
				proxy.changeStatus(id,!temp.getStatus());
				temp.setStatus(!temp.getStatus());
				client.close();
				} catch(IOException e){
					System.err.println("Error connecting to light ...");
					e.printStackTrace(System.err);
					System.exit(1);
				}
			}
		}
		return 0;
	}
	
	public boolean nobodyAtHome(){
		for(Userinfo temp: users){
			if(temp.getAthome() == true){
				return false;
			}
		}
		return true;
	}

	@Override
	public int changeHomeStatus(int id){
		boolean homestatus = false;
		for(Userinfo temp: users){
			if(id == temp.getId()){
				temp.setAthome(!temp.getAthome());
				homestatus = temp.getAthome();
				break;
			}
		}
		for(Userinfo temp: users){
			if(id != temp.getId()){
				try {
					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),temp.getId()));
					userproto prox =  (userproto) SpecificRequestor.getClient(userproto.class, client);
					prox.reportUserStatus(id,homestatus);
					client.close();
				}catch(IOException e){
					System.err.println("Error connecting to user ...");
					e.printStackTrace(System.err);
					System.exit(1);
				}
			}
		}
		if(nobodyAtHome()){
			for(Lightinfo temp : lights){
				try {
					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),temp.getId()));
					lightproto proxy =  (lightproto) SpecificRequestor.getClient(lightproto.class, client);
					proxy.changeStatus(temp.getId(),false);
					client.close();
					} catch(IOException e){
						System.err.println("Error connecting to light ...");
						e.printStackTrace(System.err);
						System.exit(1);
					}
			}
		}
		else{
			for(Lightinfo temp : lights){
				try {
					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),temp.getId()));
					lightproto proxy =  (lightproto) SpecificRequestor.getClient(lightproto.class, client);
					proxy.changeStatus(temp.getId(),temp.getStatus());
					client.close();
				} catch(IOException e){
					System.err.println("Error connecting to light ...");
					e.printStackTrace(System.err);
					System.exit(1);
				}
			}
		}
		return 0;
	}
	
	@Override
	public List<CharSequence> sendFridgeItems(int id){
		for(Clientinfo temp : clients){
			if (temp.getId() == id && temp.getType().toString().equals("Fridge")){
				try {
					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),temp.getId()));
					fridgeproto proxy =  (fridgeproto) SpecificRequestor.getClient(fridgeproto.class, client);
					List<CharSequence> items = proxy.sendItems(temp.getId());
					client.close();
					return items;
				} catch(IOException e){
					System.err.println("Error connecting to fridge ...");
					e.printStackTrace(System.err);
					System.exit(1);
				}
			}
		}
		return new ArrayList<CharSequence>();
	}
	
	@Override
	public int sendTSMeasurement(double measurement, int id){	
		for(List<TSinfo> temp : measurements){
			if( temp.get(0).getId() == id){
				if(temp.size() == 1 && temp.get(0).getMeasurement() == 0.0){
					temp.remove(0);
				}
				temp.add(new TSinfo(id,measurement));
				if (temp.size() > 10){
					temp.remove(0);
				}
				break;
			}
		}
		return 0;
	}
	
	@Override
	public double getCurrentTemperature(int id){
		for(Userinfo temp : users){
			if (temp.getId() == id){				
				double value = 0.0;
				for(List<TSinfo> templist : measurements) {
					value += templist.get(templist.size() - 1).getMeasurement();
				}
				return value / measurements.size();		
			}
		}
		return -1;
	}
	
	@Override
	public List<Double> getTemperatureHistory(int id){
		for(Userinfo temp : users){
			if (temp.getId() == id){
				List<Double> newlist = new ArrayList<Double>();
				for(int i = 0; i < measurements.get(0).size();i++){
					newlist.add(0.0);
				}
				for(List<TSinfo> templist : measurements){
					for(int i = 0; i < templist.size();i++){
						double d = newlist.get(i);
						d += templist.get(i).getMeasurement();
						newlist.set(i,d);
					}
				}
				for(int i = 0; i < newlist.size(); i++){
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
	public int openFridge(int id, int userid){
		for(Clientinfo temp : clients){
			if(id == temp.getId() && temp.getType().toString().equals("Fridge")){
				try{
					Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),temp.getId()));
					fridgeproto proxy =  (fridgeproto) SpecificRequestor.getClient(fridgeproto.class, client);

					int i =  proxy.openFridge(id,userid);
					client.close();
					return i;
				}catch(IOException e){}
			}
		}
		return -1;
	}
	
	@Override
	public int FridgeEmptyMessage(int id){
		for(Userinfo temp: users){
			try {
				Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),temp.getId()));
				userproto prox =  (userproto) SpecificRequestor.getClient(userproto.class, client);
				prox.reportFridgeEmpty(id);
				client.close();
			}catch(IOException e){
				System.err.println("Error connecting to user ...");
				e.printStackTrace(System.err);
				System.exit(1);
			}
		}
		return 0;
	}
	
	@Override
	public int deleteClient(int id) {
		for(Clientinfo temp : clients){
			if (id == temp.getId()){
				if( temp.getType().toString().equals("User")) {
					for(Userinfo temp2 : users){
						if( id == temp2.getId()){
							users.remove(temp2);
							break;
						}
					}
				}
				else if (temp.getType().toString().equals("Light")) {
					for(Lightinfo temp2 : lights){
						if( id == temp2.getId()){
							lights.remove(temp2);
							break;
						}
					}
				}
				else if (temp.getType().toString().equals("TS")) {
					for(List<TSinfo> temp2 : measurements){
						if( id == temp2.get(0).getId()){
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

	public static void main( String[] args){
		Server server = null;
		Controller controller = new Controller();
		
		try {
			server = new SaslSocketServer(new SpecificResponder(serverproto.class, controller), new InetSocketAddress(InetAddress.getLocalHost(),controller.getPort()));
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
                controller.run();
            }
        }, 0, 5000);
		try {
			server.join();
		}	catch ( InterruptedException e) {
			
		}	
	}
}