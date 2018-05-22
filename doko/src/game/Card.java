package game;

import entities.SymbolE;
import entities.WertigkeitE;

public class Card implements Comparable<Card>{

	private SymbolE symbol;
	private WertigkeitE wertigkeit;
	private boolean trumpf;
	
	public Card(SymbolE symbol,WertigkeitE wertigkeit) {
		this.symbol=symbol;
		this.wertigkeit=wertigkeit;
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
		if(this.getWertigkeit().compareTo(o.getWertigkeit())>0){
			return 1;
		}else if(this.getWertigkeit().compareTo(o.getWertigkeit())<0){
			return -1;
		}
		return 0;
	}

	@Override
	public String toString(){
		return this.getWertigkeit() + " " + this.getSymbol();
	}
	
	
	
}
