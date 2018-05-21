package doko.entities;
import java.io.BufferedReader;
import java.io.PrintWriter;


public class ClientWorker {

	private BufferedReader in = null;
	private PrintWriter out = null;
	private ClientStateE state;
	private User user;
	
	ClientWorker(User user, ServerThread st) {
		this.user = user;
		
	}
	
	public void run() {
		
	}
	
	public void changeState(ClientStateE state) {
		this.state = state;
	}
}
