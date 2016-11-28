package avro.client;

import java.io.IOException;

import org.apache.avro.ipc.specific.SpecificRequestor;

import avro.proto.connect;
import avro.proto.getlights;


public class Light extends Client {
	
	private Boolean state;

	public Light(int port) {
		super(port,"Light");
		state = false;
	}
	
	public void changeState(){
		
		state = !state;
		System.out.println(" you changed your state to: " + state);
	}
	
	public Boolean getState() {
		return state;
	}

	public static void main(String[] args) {
		Light lampje = new Light(6789);
		lampje.changeState();
		lampje.changeState();
		try{
			getlights proxy =  (getlights) SpecificRequestor.getClient(getlights.class, lampje.client);
			int blabla = proxy.getlights(lampje.getID(),lampje.getState());
		} catch(IOException e){
		
		System.err.println("Error connecting to server ...");
		e.printStackTrace(System.err);
		System.exit(1);

		}

	}

}
