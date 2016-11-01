package avro.client;


import java.util.Random;
public class TemperatureSensor extends Client {
	
	 private double measurement;
	 private Random gen = new Random();

	public TemperatureSensor(int port) {
		super(port);
		measurement = gen.nextGaussian() + 20;
	}
	
	double getMeasurement() {
		
		return measurement;
	}
	
	void nextMeasurement() {
		
		measurement = measurement + gen.nextGaussian();
		
	}
	


	public static void main(String[] args) {
		TemperatureSensor s = new TemperatureSensor(6789);
		System.out.println(s.getMeasurement());
		
		try{
		
			while(true){
				s.nextMeasurement();
				System.out.println(s.getMeasurement());
		        Thread.sleep(3000);
	
			}

			
		} catch(InterruptedException e){}
		
	}

}
