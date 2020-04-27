package application.control;

import application.model.SectionModel;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;

public class Section {
	public static int nbtab = 0;
	private int idTab;
	private String SectionTimeCode;
	private String SectionText = "";
	private String texteCache = "";
	private SectionModel secMod;
	
	public Section(TabPane parent,String sectionAide,String SectionText,String SectionTimeCode) {
		//TODO l'aide doit se superposer au texe et la solution doit bloquer la section
		nbtab++;
		idTab=nbtab;
		this.SectionTimeCode = SectionTimeCode;
		this.SectionText=SectionText;
		
        for (int i = 0; i < SectionText.length(); i++) {
        	if(!ApplicationController.CARACTERE_NON_OCULTER.contains(SectionText.charAt(i)+"")) {
        		texteCache += ApplicationController.CARACTERE_OCULTATION;
        	}else {
        		texteCache += SectionText.charAt(i);
        	}
		}
        
        secMod = new SectionModel(parent,texteCache, nbtab, sectionAide);
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
	public TextArea getTextvideo() {
		return secMod.getTextvideo();
	}
	
	public String getTextATrouver() {
		return SectionText;
	}
	
	public String getAide() {
		return secMod.getAide();
	}
	
	public String getTimeCode() {
		return SectionTimeCode;
	}
}
