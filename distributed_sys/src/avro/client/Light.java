package avro.client;

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
	}

}
