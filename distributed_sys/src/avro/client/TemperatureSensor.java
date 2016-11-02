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
		
		double newmeasure = gen.nextGaussian();
		if (newmeasure > 1){
			newmeasure = 1;
		}
		else if (newmeasure < -1){
			newmeasure = -1;
		}
		measurement = measurement + newmeasure;
		
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
