package application.model;

import java.time.LocalTime;

import application.control.ApplicationController;
import application.vue.ErreurModel;
import application.vue.SectionModel;
import javafx.scene.control.TabPane;

public class Section {
	public static int nbtab = 0;
	private int idTab;
	private String sectionTimeLimiteCode;
	private String sectionText; //le texte non caché
	private String texteCache;  //le texte avec la reponse de l'éleve + char masqué
	private String sectionAide;
	private SectionModel secMod;
	private boolean locked = false;
	private boolean help = false;
	private CustomSectionPausableTime timer;
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
	
	public Section(TabPane parent,String sectionAide,String sectionText,String sectionTimeCode,String timeStart,String timeStop) {
		timer = new CustomSectionPausableTime();
		nbtab++;
		idTab=nbtab;
		this.sectionTimeLimiteCode = sectionTimeCode;
		this.sectionText=sectionText;
		this.sectionAide=sectionAide;
		texteCache="";
        for (int i = 0; i < sectionText.length(); i++) {
        	if(!ApplicationController.CARACTERE_NON_OCULTER.contains(sectionText.charAt(i)+"")) {
        		texteCache += ApplicationController.CARACTERE_OCULTATION;
        	}else {
        		texteCache += sectionText.charAt(i);
        	}
		}
        
        secMod = new SectionModel(parent,texteCache, nbtab,this);
        System.out.println(texteCache);
        this.timeStart=timeStart;
		this.timeStop=timeStop;
        
	}

	/*public Section(TabPane parent,String sectionText,String sectionTimeCode) {
		//this(parent,"",sectionText,sectionTimeCode);
		//TODO a faire correctement
	}*/

	public static void reset() {
		nbtab=0;
	}
	
	public int getidTab(){
		return idTab;
	}
	/**
	 * retourne le texte crypté
	 * 
	 * @return
	 */
	
	public String getTextATrouver() {
		return sectionText;
	}
	
	public String getAide() {
		return sectionAide;
	}
	
	public String getTimeLimiteCode() {
		return sectionTimeLimiteCode;
	}

	public String getTexteCache() {
		return texteCache;
	}
	
	public void setTexteCache(String texteCache) {
		this.texteCache = texteCache;
		if(!help && !locked)secMod.getTextvideo().setText(texteCache);
	}
	
	public void switchHelpStatus() {
		if(!locked) {
			System.out.println("help ed");
			help=!help;
			if(help) {
				secMod.getTextvideo().setText(sectionAide);
			}else {
				secMod.getTextvideo().setText(texteCache);
			}
		}
	}
	
	public boolean isHelp() {
		return help;
	}
	
	
	/**
	 * empeche toute modification des champs pour l'affichage de la solution
	 */
	public void lock() {
		locked = true;
		secMod.getTextvideo().setText(sectionText);
	}
	
	public boolean islocked() {
		return locked;
	}
	
	public String getTimeStart() {
		return timeStart;
	}
	public String getTimeStop() {
		return timeStop;
	}

	public long getTimeLeft() {
		LocalTime t = LocalTime.parse(sectionTimeLimiteCode);
		//t = t.minusNanos(timer.getTimeElipsed());
		return localTimeToMSecs(t) - timer.getTimeElipsed();
	}
	
	private long localTimeToMSecs(LocalTime t) {
		return t.getMinute()*60*1000 + t.getSecond()*1000;
	}
	
	public void startTimer() {
		try {
			timer.start();
		} catch (AlredyRunningExeption e) {
			ErreurModel.erreurStack(e);
		}
	}

	public void stopTimer() {
		try {
			timer.pause();
		} catch (NotRunningExeption e) {
			ErreurModel.erreurStack(e);
		}
	}
}
