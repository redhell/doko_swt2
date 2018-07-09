package entities;

import entities.SymbolE;
import entities.WertigkeitE;

public class Card implements Comparable<Card> {

	private SymbolE symbol;
	private WertigkeitE wertigkeit;
	private boolean trumpf;
	private boolean schweinchen;

	public Card(WertigkeitE wertigkeit, SymbolE symbol, boolean trumpf) {
		this.wertigkeit = wertigkeit;
		this.symbol = symbol;
		this.trumpf = trumpf;
	}

	public SymbolE getSymbol() {
		return symbol;
	}

	public void setSymbol(SymbolE symbol) {
		this.symbol = symbol;
	}

	public WertigkeitE getWertigkeit() {
		return wertigkeit;
	}

	public void setWertigkeit(WertigkeitE wertigkeit) {
		this.wertigkeit = wertigkeit;
	}

	public boolean isTrumpf() {
		return trumpf;
	}

	public void setTrumpf(boolean trumpf) {
		this.trumpf = trumpf;
	}

	@Override
	public int compareTo(Card o) {
		if (this.getWertigkeit().compareTo(o.getWertigkeit()) > 0) {
			return 1;
		} else if (this.getWertigkeit().compareTo(o.getWertigkeit()) < 0) {
			return -1;
		}
		return 0;
	}

	@Override
	public String toString() {
		return this.getWertigkeit() + "_" + this.getSymbol();
	}

	public String getPath() {
		return "assets/" + this.toString() + ".png";
	}

	@Override
	public boolean equals(Object obj) {
		Card otherCard = (Card) obj;
		return this.symbol == otherCard.symbol && this.wertigkeit == otherCard.wertigkeit;
	}

	public boolean isSchweinchen() {
		return schweinchen;
	}

	public void setSchweinchen(boolean schweinchen) {
		this.schweinchen = schweinchen;
	}
	
}
