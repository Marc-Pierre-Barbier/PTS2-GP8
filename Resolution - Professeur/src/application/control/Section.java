package application.control;

import application.model.SectionModel;
import javafx.scene.control.TabPane;

public class Section {
	public static int nbtab = 0;
	private int idTab;
	SectionModel SecModl;
	

	public Section(TabPane parent, TabPane sectionsTimeCodePane,String aideVideoSTR,String texteVideoSTR,String SectionTimeCode) {
		this(parent,sectionsTimeCodePane);
		SecModl.SectionLoad(aideVideoSTR, texteVideoSTR, SectionTimeCode);
	}
	
	
	public Section(TabPane parent, TabPane sectionsTimeCodePane) {
		nbtab++;
		idTab=nbtab;
		SecModl = new SectionModel(parent, sectionsTimeCodePane,nbtab);
	}

	public int getidTab(){
		return idTab;
	}

	public String getText() {
		return SecModl.getText();
	}
	
	public String getAide() {
		return SecModl.getAide();
	}
	
	public String getTimeCode() {
		String limiteTempsH = SecModl.getlimiteTempsH();
		String limiteTempsM = SecModl.getlimiteTempsM();
		if(limiteTempsH.length() == 1)SecModl.setLimiteTempsH("0"+limiteTempsH);
		if(limiteTempsM.length() == 1)SecModl.setLimiteTempsM("0"+limiteTempsM);
		if(limiteTempsH.length() == 0)SecModl.setLimiteTempsH("00");
		if(limiteTempsM.length() == 0)SecModl.setLimiteTempsM("00");
		if(limiteTempsH.length() > 2) {
			SecModl.setStyleLimiteTempsH("-fx-text-inner-color: red;"); 
			return "ABORT"; // une erreur est detecter correction par l'utilisateur requise
		}
		if(limiteTempsM.length() > 2) {
			SecModl.setStyleLimiteTempsM("-fx-text-inner-color: red;");  
			return "ABORT";
		}
		try {
			Integer.parseInt(limiteTempsH);
			Integer.parseInt(limiteTempsM);
		}catch (NumberFormatException e) {
			return "ABORT"; //le temps contien lettres / charatéres speciaux
		}
		
		return "00:"+limiteTempsM + ":" + limiteTempsM;
		
	}

	public static void reset() {
		nbtab=0;
	}


	public void disableAide() {
		SecModl.disableAide();
	}


	public void enableAide() {
		SecModl.enableAide();
		
	}
}
