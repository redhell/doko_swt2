package doko.entities;

import java.util.List;

public class Player extends User {

	private int team;

	Player(String username, Socket socket, User user) {
		super(username, socket);
	}

	public void getCards() {

	}

	public void removeCard(Card card) {

	}

	public void setCards(List cards) {

	}

}
