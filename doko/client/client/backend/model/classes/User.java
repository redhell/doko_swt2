package client.backend.model.classes;

public abstract class User {

	private String username;
	private int score;
	private ConnectionSocket cs;
	
	public String getUsername() {
		return username;
	}
	
	public int getScore() {
		return score;
	}
}
