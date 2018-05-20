package game;

import client.backend.model.classes.ConnectionSocket;
import client.backend.model.classes.StateE;

public class Lobby {

	private ConnectionSocket connectionSocket;
	private Game game;

	public Lobby(ConnectionSocket connectionSocket) {
		this.connectionSocket = connectionSocket;
	}

	public boolean connectToServer() {
		return connectionSocket.connectToServer();
	}

	public StateE queueForGame() {
		if (connectionSocket.isConnected()) {
			System.out.println("send queue-request, waiting for response");
			return connectionSocket.queueForGame();

		} else {
			System.out.println("Client could not connect");
			return StateE.CONNECTION_FAILED;
		}
	}

}
