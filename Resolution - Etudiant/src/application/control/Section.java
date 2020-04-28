package application.control;

import application.model.SectionModel;
import javafx.scene.control.TabPane;

public class Section {
	public static int nbtab = 0;
	private int idTab;
	private String sectionTimeCode;
	private String sectionText; //le texte non caché
	private String texteCache;  //le texte avec la reponse de l'éleve + char masqué
	private String sectionAide;
	private SectionModel secMod;
	private boolean locked = false;
	private boolean help = false;
	
	public Section(TabPane parent,String sectionAide,String sectionText,String sectionTimeCode) {
		nbtab++;
		idTab=nbtab;
		this.sectionTimeCode = sectionTimeCode;
		this.sectionText=sectionText;
		this.sectionAide=sectionAide;
		System.out.println(sectionAide);
		texteCache="";
        for (int i = 0; i < sectionText.length(); i++) {
        	if(!ApplicationController.CARACTERE_NON_OCULTER.contains(sectionText.charAt(i)+"")) {
        		texteCache += ApplicationController.CARACTERE_OCULTATION;
        	}else {
        		texteCache += sectionText.charAt(i);
        	}
		}
        
        secMod = new SectionModel(parent,texteCache, nbtab);
        System.out.println(texteCache);
        
	}

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
		return secMod.getAide();
	}
	
	public String getTimeCode() {
		return sectionTimeCode;
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
}
