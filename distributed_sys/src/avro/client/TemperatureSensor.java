package avro.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Random;

import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;

import avro.proto.serverproto;
import avro.proto.userproto;

import java.util.Timer;
import java.util.TimerTask;

public class TemperatureSensor  {
	
	private double measurement;
	private Random gen = new Random();
	private int id;
	private String conaddress;
	private String address;


	public TemperatureSensor(int id, String conaddr, String addr) {
		this.conaddress = conaddr;
		this.address = addr;
		this.id = id;
		measurement = gen.nextGaussian() + 20;
	}
	
	public String getControllerAddress() {
		return conaddress;
	}
	
	public String getAddress() {
		return address;
	}
	
	double getMeasurement() {
		return measurement;
	}
	
	public int getId(){
		return this.id;
	}
	
	double nextMeasurement() {
		double newmeasure = gen.nextGaussian();
		if(newmeasure > 1){
			newmeasure = 1;
		}
		else if (newmeasure < -1){
			newmeasure = -1;
		}
		measurement = measurement + newmeasure;
		return measurement;
	}
	


	public static void main(String[] args) {
		Server server = null;
		try {
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getByName(args[0]),5000));
			serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
			
			int id = proxy.connect("TS",args[1]);
			TemperatureSensor s = new TemperatureSensor(id,args[0],args[1]);
			
			server = new SaslSocketServer(new SpecificResponder(userproto.class, s), new InetSocketAddress(InetAddress.getByName(s.getAddress()),s.getId()));
			server.start();

			Timer timer = new Timer();
			System.out.println(s.getMeasurement());
			timer.schedule(new TimerTask(){	
				@Override
				public void run(){
					try {
						proxy.sendTSMeasurement(s.nextMeasurement(),id);
						System.out.println(s.getMeasurement());
					}catch(IOException e){}
				}
			},0,10000);
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
