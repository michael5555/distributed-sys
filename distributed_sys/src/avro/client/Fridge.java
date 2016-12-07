package avro.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.ipc.SaslSocketServer;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;

import avro.proto.serverproto;

import avro.proto.fridgeproto;
import avro.proto.lightproto;

public class Fridge implements fridgeproto  {
	
	private List<CharSequence> items;
	private int id;


	public Fridge(int id) {
		items = new ArrayList<CharSequence>();
		items.add("Eggs");
		items.add("Milk");

		this.id = id;

		
	}
	
	public int getId(){
		
		return this.id;
	}
	
	public List<CharSequence> getItems(){
		
		return items;
	}
	
	@Override
	public List<CharSequence> sendItems(int id){
				
		return items;
		
		
	}
	
	

	public static void main(String[] args) {
		Server server = null;
		try {
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),6789));
			serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
			int id = proxy.connect("Fridge");
			Fridge kastje = new Fridge(id);
			server = new SaslSocketServer(new SpecificResponder(fridgeproto.class, kastje), new InetSocketAddress(InetAddress.getLocalHost(),6790 + kastje.getId()));


		} catch(IOException e){
			
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);

		}
		
		server.start();
		
		try {
			server.join();
		}	catch ( InterruptedException e) { }
	}

}
