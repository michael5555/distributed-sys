package avro.client;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Random;

import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import avro.proto.serverproto;

import java.util.Timer;
import java.util.TimerTask;

public class TemperatureSensor  {
	
	private double measurement;
	private Random gen = new Random();
	private int id;


	public TemperatureSensor(int id) {
		this.id = id;
		measurement = gen.nextGaussian() + 20;
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
		try {
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
			serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
			int id = proxy.connect("TS");
			TemperatureSensor s = new TemperatureSensor(id);
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
				},0,1000);
			


		} catch(IOException e){
			
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);

		}
		

	}
}
