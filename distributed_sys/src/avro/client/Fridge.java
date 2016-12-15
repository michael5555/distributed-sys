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
	private int open;


	public Fridge(int id) {
		items = new ArrayList<CharSequence>();

		this.id = id;
		open = 0;

		
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
	
	@Override
	public int openFridge(int id){
		
		if(id == this.id){
			open += 1;
			if(open > 1){
				return 1;
			}
			return 0;
		}
		
		return -1;
	}
	
	@Override
	public int addItem(int id, CharSequence item){
		
		if((id == this.id) && (open != 0)){
			items.add(item);
			return 0;
		}
		
		return -1;
	}
	
	@Override
	public int removeItem(int id, CharSequence item){
		if(id == this.id && open != 0){

			for(CharSequence temp : items){

				if (temp.equals(item)){

					items.remove(item);
					if(items.size() == 0){
						
						try{
							Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
							serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
							proxy.FridgeEmptyMessage(this.id);
						}catch(IOException e){}

					}
					return 0;
				}
			}
		}
		
		return -1;
	}
	
	@Override
	public int closeFridge(int id){
		
		if(id == this.id){
			open -= 1;
			return open;
		}
		
		return -1;
	}

	public static void main(String[] args) {
		Server server = null;
		try {
			Transceiver client = new SaslSocketTransceiver(new InetSocketAddress(InetAddress.getLocalHost(),5000));
			serverproto proxy =  (serverproto) SpecificRequestor.getClient(serverproto.class, client);
			int id = proxy.connect("Fridge");
			Fridge kastje = new Fridge(id);
			server = new SaslSocketServer(new SpecificResponder(fridgeproto.class, kastje), new InetSocketAddress(InetAddress.getLocalHost(),kastje.getId()));


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
