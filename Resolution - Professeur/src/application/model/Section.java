package application.model;

import application.view.SectionModel;
import javafx.scene.control.TabPane;

public class Section {
	public static int nbtab = 0;
	private int idTab;
	SectionModel SecModl;
	/**
	 * le temps est exprimer en hh:mm:ss depuis le debut de la video
	 * @return
	 */
	private String timeStart;
	/**
	 * le temps est exprimer en hh:mm:ss depuis le debut de la video
	 * @return
	 */
	private String timeStop;

	public Section(TabPane parent, TabPane sectionsTimeCodePane,String aideVideoSTR,String texteVideoSTR,String SectionTimeCode,String timeStart,String timeStop) {
		this(parent,sectionsTimeCodePane);
		SecModl.SectionLoad(aideVideoSTR, texteVideoSTR, SectionTimeCode);
		this.timeStart=timeStart;
		this.timeStop=timeStop;
	}
	
	
	public Section(TabPane parent, TabPane sectionsTimeCodePane) {
		nbtab++;
		idTab=nbtab;
		SecModl = new SectionModel(parent, sectionsTimeCodePane,nbtab);
		timeStart="00:00:00";
		timeStop="00:00:00";
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
	
	public String getTimeLimitCode() {
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
	
	public void setTimeStop(String timeStop) {
		this.timeStop = timeStop;
	}
	
	public String getTimeStop() {
		return timeStop;
	}
	
	public void setTimeStart(String string) {
		this.timeStart = string;
	}
	
	public String getTimeStart() {
		return timeStart;
	}
}
