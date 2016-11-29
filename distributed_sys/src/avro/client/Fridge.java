package avro.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Vector;

import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import avro.proto.sysserver;
public class Fridge  {
	
	private Vector<String> items;
	private int id;


	public Fridge(int id) {
		items = new Vector<String>();
		this.id = id;

		
	}
	
	public int getID(){
		
		return this.id;
	}
	
	Vector<String> getItems(){
		
		return items;
	}

	public static void main(String[] args) {
		try {
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(6789));
			sysserver proxy =  (sysserver) SpecificRequestor.getClient(sysserver.class, client);
			int id = proxy.connect("Fridge");
			Fridge kastje = new Fridge(id);

		} catch(IOException e){
			
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);

		}
	}

}
