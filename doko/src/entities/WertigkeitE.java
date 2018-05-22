package entities;



public enum WertigkeitE {

	ASS,KOENIG,DAME,BUBE,ZEHN;
	
	public static String[] getValues(){
		return new String[]{WertigkeitE.ASS.name(),WertigkeitE.KOENIG.name(),WertigkeitE.DAME.name(),WertigkeitE.BUBE.name(),WertigkeitE.ZEHN.name()};
	}
	
}
