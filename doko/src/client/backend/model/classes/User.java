package client.backend.model.classes;

import java.net.Socket;

public class User {

	private String username;
	private int score;
	
	public User(String username) {
		this.username=username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getScore() {
		return score;
	}
}
