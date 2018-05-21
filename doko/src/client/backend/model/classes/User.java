package client.backend.model.classes;

import org.json.JSONArray;
import org.json.JSONObject;

public class User {

	private String username;
	private int score;
	private JSONArray karten;
	
	public User(String username) {
		this.username=username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getScore() {
		return score;
	}
	
	public JSONArray getKarten() {
		return karten;
	}
	
	public void setKarten(JSONArray karten) {
		this.karten = karten;
	}
	
	public void entferneKarte(int index) {
		karten.remove(index);
	}
	
	public void entferneKarte(String symbol, String wertigkeit) {
		for(int i=0; i<karten.length(); i++) {
			JSONObject json = karten.getJSONObject(i);
			if(symbol.equals(json.getString("symbol")) && wertigkeit.equals(json.getString(wertigkeit))) {
				karten.remove(i);
				break;
			}
		}
	}
	
	public void entferneKarte(JSONObject job) {
		for(int i=0; i<karten.length(); i++) {
			JSONObject json = karten.getJSONObject(i);
			if(json.equals(job)) {
				karten.remove(i);
				break;
			}
		}
	}
}
