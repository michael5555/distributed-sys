package avro.client;

public class User extends Client {
	
	private String username;
	
	User(int port, String username){
		
		super(port);
		this.username = username;
	}

	public static void main(String[] args) {
		
		User Bob = new User(6789,"Bobby");

	}

}
