package avro.client;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import avro.proto.sysserver;


public class Client {
	
	private int id;
	Transceiver client;
	
	public Client(int port,CharSequence type){
		
		
		try {
			this.client = new SaslSocketTransceiver(new InetSocketAddress(port));
			sysserver proxy =  (sysserver) SpecificRequestor.getClient(sysserver.class, client);
			this.id = proxy.connect(type);
			System.out.println(id);
			//client.close();
		} catch(IOException e){
			
			System.err.println("Error connecting to server ...");
			e.printStackTrace(System.err);
			System.exit(1);

		}

		
		
	}
	
	public int getID(){
		
		return this.id;
	}
	


	public static void main(String[] args) {
		Client myclient =  new Client(6789,"client");

	}

}
