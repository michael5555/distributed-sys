package avro.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;

import avro.proto.serverproto;
import avro.proto.lightproto;


public class Light implements lightproto  {
	
	private Boolean state;
	private int id;
	private String conaddress;
	private String address;
	private int controllerport = 5000;


	public Light(int id,String conaddr, String addr) {

		this.conaddress = conaddr;
		this.address = addr;
		state = false;
		this.id = id;
	}
	
	public synchronized void checkcontroller(){
		try{
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(conaddress,controllerport));
			serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
			proxy.reconnect("Light", address, id);
			client.close();
		}catch(IOException e){}
	}
	
	public String getControllerAddress() {
		return conaddress;
	}
	
	public int getControllerPort() {
		return controllerport;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getId() {
		return this.id;
	}
	
	public Boolean getState() {
		return state;
	}
	
	public int changeStatus(int id, boolean lightstatus) {
		if (id == this.id) {
			if (this.state == lightstatus){
				return 0;
			}
			this.state = lightstatus;
			if (this.state) {
				System.out.println("light with id: " + this.id + " has been turned on");
			}
			else {
				System.out.println("light with id: " + this.id + " has been turned off");
			}
			return 0;
		}
		return -1;
	}
	
	@Override
	public int setcontrollerinfo(int port, CharSequence address) {
		conaddress = address.toString();
		controllerport = port;
		return 0;
	}

	public static void main(String[] args) {
		Server server = null;
		try {
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(args[0]),5000));
			serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
			
			int id = proxy.connect("Light",args[1]);
			Light lampje = new Light(id,args[0],args[1]);
			
			server = new SaslSocketServer(new SpecificResponder(lightproto.class, lampje), new InetSocketAddress(InetAddress.getByName(lampje.getAddress()),lampje.getId()));
			server.start();
			
			proxy.update();
			
			client.close();

	        Timer timer2 = new Timer();
	        timer2.schedule(new TimerTask() {
	        	@Override
	            public void run() {
	        		lampje.checkcontroller();
	            }
	        }, 0, 5000);
		} catch(IOException e) {
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);
		}
		
		try {
			server.join();
		}	catch (Exception e) {
			
		}
	}
}
