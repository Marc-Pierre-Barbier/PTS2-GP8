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
		SecModl.sectionLoad(aideVideoSTR, texteVideoSTR, SectionTimeCode);
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
		return SecModl.getTimecode().toString();
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
