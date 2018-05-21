package doko.entities;

public abstract class User {

	protected String username;
	protected int score;
	protected Socket st;
	private ClientWorker cw;
	
	User(String username, Socket socket) {
		st = socket;
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
	 this.username = username;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	
}
