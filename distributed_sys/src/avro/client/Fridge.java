package avro.client;

import java.util.Vector;
public class Fridge extends Client {
	
	private Vector<String> items;

	public Fridge(int port) {
		super(port,"Fridge");
	}
	
	Vector<String> getItems(){
		
		return items;
	}

	public static void main(String[] args) {
		Fridge kastje = new Fridge(6789);
	}

}
