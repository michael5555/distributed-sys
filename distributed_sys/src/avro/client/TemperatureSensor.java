package avro.client;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Random;

import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import avro.proto.sysserver;
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
	
	public int getID(){
		
		return this.id;
	}
	
	void nextMeasurement() {
		
		double newmeasure = gen.nextGaussian();
		if(newmeasure > 1){
			newmeasure = 1;
		}
		else if (newmeasure < -1){
			newmeasure = -1;
		}
		
		measurement = measurement + newmeasure;
		
	}
	


	public static void main(String[] args) {
		try {
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(6789));
			sysserver proxy =  (sysserver) SpecificRequestor.getClient(sysserver.class, client);
			int id = proxy.connect("TS");
			TemperatureSensor s = new TemperatureSensor(id);
			System.out.println(s.getMeasurement());
			while(true){
				s.nextMeasurement();
				System.out.println(s.getMeasurement());
		        Thread.sleep(3000);
	
			}



		} catch(IOException e){
			
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);

		}
		 catch(InterruptedException e){ }
		

	}
}
