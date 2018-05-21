package doko.entities;

public class Card implements Comparable {

	public SymbolE symbol;
	public WertigkeitE wertigkeit;
	public boolean trumpf;

	Card(SymbolE symbol, WertigkeitE wertigkeit, Card c) {
		this.symbol = symbol;
		this.wertigkeit = wertigkeit;
	}

	@Override
	public int compareTo(Object o) {
		Card c = (Card) o;

		if (this.trumpf && !c.trumpf) {
			return 1;
		} else if (!this.trumpf && c.trumpf) {
			return -1;
		} else if (this.trumpf && c.trumpf) {
			if (this.symbol.compareTo(c.symbol) < 0) {
				if (this.wertigkeit.compareTo(c.wertigkeit) < 0) {
					return -1;
				} else if (this.wertigkeit.compareTo(c.wertigkeit) > 0) {
					return 1;
				} else {
					return 0;
				}
			} else if (this.symbol.compareTo(c.symbol) > 0) {
				if (this.wertigkeit.compareTo(c.wertigkeit) < 0) {
					return -1;
				} else if (this.wertigkeit.compareTo(c.wertigkeit) > 0) {
					return 1;
				} else {
					return 0;
				}
			} else if (this.wertigkeit.compareTo(c.wertigkeit) < 0) {
				return -1;
			} else if (this.wertigkeit.compareTo(c.wertigkeit) > 0) {
				return 1;
			} else {
				return 0;
			}
		} else {// Vergleich des Symbols bei nicht-Trumpf nötig? Man muss sowieso die richtige
				// Farbe bedienen, ansonsten ist es weniger Wert. Irgendwo muss noch die erste
				// gespielte Farbe abgespeichert werden
			if (this.wertigkeit.compareTo(c.wertigkeit) < 0) {
				return -1;
			} else if (this.wertigkeit.compareTo(c.wertigkeit) > 0) {
				return 1;
			} else {
				return 0;
			}
		}
	}

}
