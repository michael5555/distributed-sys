package avro.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;

import avro.proto.serverproto;
import avro.proto.userproto;
import avro.proto.tsproto;




public class TemperatureSensor implements tsproto  {
	
	private double measurement;
	private Random gen = new Random();
	private int id;
	private String conaddress;
	private String address;
	private int controllerport = 5000;


	public TemperatureSensor(int id, String conaddr, String addr) {
		this.conaddress = conaddr;
		this.address = addr;
		this.id = id;
		measurement = gen.nextGaussian() + 20;
	}
	
	public synchronized void checkcontroller(){
		try{
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(conaddress,controllerport));
			serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
			proxy.reconnect("TS", address, id);
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
	
	double getMeasurement() {
		return measurement;
	}
	
	public int getId() {
		return this.id;
	}
	
	double nextMeasurement() {
		double newmeasure = gen.nextGaussian();
		if (newmeasure > 1) {
			newmeasure = 1;
		}
		else if (newmeasure < -1) {
			newmeasure = -1;
		}
		measurement = measurement + newmeasure;
		return measurement;
	}
	
	@Override
	public int setcontrollerinfo(int port, CharSequence address) {
		controllerport = port;
		conaddress = address.toString();
		return 0;
	}
	
	public static void main(String[] args) {
		Server server = null;
		try {
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(args[0]),5000));
			serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
			
			int id = proxy.connect("TS",args[1]);
			TemperatureSensor s = new TemperatureSensor(id,args[0],args[1]);
			
			client.close();
			
			server = new SaslSocketServer(new SpecificResponder(userproto.class, s), new InetSocketAddress(InetAddress.getByName(s.getAddress()),s.getId()));
			server.start();

			Timer timer = new Timer();
			System.out.println(s.getMeasurement());
			timer.schedule(new TimerTask(){	
				@Override
				public void run() {
					try {
						Transceiver client2 = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(s.getControllerAddress()),s.getControllerPort()));
						serverproto proxy2 =  (serverproto) SpecificRequestor.getClient(serverproto.class, client2);

						proxy2.sendTSMeasurement(s.nextMeasurement(),id);
						client2.close();
						System.out.println(s.getMeasurement());
					} catch (IOException e) {
						//TODO
					}
				}
			},0,10000);
			
	        Timer timer2 = new Timer();
	        timer2.schedule(new TimerTask() {
	        	@Override
	            public void run() {
	        		s.checkcontroller();
	            }
	        }, 0, 5000);
		} catch(IOException e) {
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);
		}
		try {
			server.join();
		} catch (InterruptedException e) {
			//TODO
		}
	}
}
