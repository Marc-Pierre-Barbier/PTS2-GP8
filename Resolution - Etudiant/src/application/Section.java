package application;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class Section {
	public static int nbtab = 0;
	private int idTab;
	private Tab Section;
	private VBox vbox;
	private Text texte;
	private TextArea texteVideo;
	private Text aide;
	private TextArea aideVideo;
	private String SectionTimeCode;
	private String SectionText = "";
	private String texteCache = "";
	
	
	public Section(TabPane parent,String SectionAide,String SectionText,String SectionTimeCode) {
		nbtab++;
		idTab=nbtab;
		Section = new Tab("Section "+nbtab);
		vbox = new VBox();
		texte = new Text("texte");
		
		aide = new Text("aide");
		aideVideo = new TextArea(SectionAide);
		aideVideo.setMaxHeight(100);
		Section.setContent(vbox);
		
		parent.getTabs().add(Section);
		
		this.SectionTimeCode = SectionTimeCode;
		this.SectionText=SectionText;
		
        for (int i = 0; i < SectionText.length(); i++) {
        	if(!ApplicationController.CARACTERE_NON_OCULTER.contains(SectionText.charAt(i)+"")) {
        		texteCache += ApplicationController.CARACTERE_OCULTATION;
        	}else {
        		texteCache += SectionText.charAt(i);
        	}
		}
        System.out.println(texteCache);
        texteVideo = new TextArea(texteCache);
        vbox.getChildren().addAll(texte,texteVideo,aide,aideVideo);
	}

	public int getidTab(){
		return idTab;
	}
	
	public TextArea getTextvideo() {
		return texteVideo;
	}
	
	public String getTextATrouver() {
		return SectionText;
	}
	
	public String getAide() {
		return aideVideo.getText();
	}
	
	public String getTimeCode() {
		return SectionTimeCode;
	}
	/*public Section(TabPane tabPane,TabPane sectionsTimeCodePane) {
		this(tabPane,sectionsTimeCodePane);
	}*/
}
