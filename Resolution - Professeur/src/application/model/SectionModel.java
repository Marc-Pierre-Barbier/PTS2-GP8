package application.model;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SectionModel {
	
	private final int TAILLE_CHAMP_TEXTE_TIMER = 30;
	private Tab section;
	private VBox vbox;
	private Text texte;
	private TextArea texteVideo;
	private Text aide;
	private TextArea aideVideo;
	
	//time
	private Tab limiteTempsTab;
	private TextArea limiteTempsH;
	private TextArea limiteTempsM;
	private HBox boxtime;
	
	public SectionModel(TabPane parent,TabPane sectionsTimeCodePane,int nbtab) {
		section = new Tab("Section "+nbtab);
		vbox = new VBox();
		texte = new Text("texte");
		texteVideo = new TextArea();
		aide = new Text("aide");
		aideVideo = new TextArea();
		aideVideo.setMaxHeight(100);
		section.setContent(vbox);
		vbox.getChildren().addAll(texte,texteVideo,aide,aideVideo);
		parent.getTabs().add(section);
		
		limiteTempsTab = new Tab("Section "+nbtab);
		limiteTempsH = new TextArea("00");
		limiteTempsH.setMinHeight(TAILLE_CHAMP_TEXTE_TIMER);
		limiteTempsH.setMaxHeight(TAILLE_CHAMP_TEXTE_TIMER);
		limiteTempsM = new TextArea("00");
		limiteTempsM.setMinHeight(TAILLE_CHAMP_TEXTE_TIMER);
		limiteTempsM.setMaxHeight(TAILLE_CHAMP_TEXTE_TIMER);
		boxtime= new HBox();
		boxtime.getChildren().addAll(new Text("Limite de temps :"),limiteTempsH,new Text("h"),limiteTempsM,new Text("min"));
		limiteTempsTab.setContent(boxtime);
		sectionsTimeCodePane.getTabs().add(limiteTempsTab);
	}
	
	public void SectionLoad(String aideVideoSTR,String texteVideoSTR,String SectionTimeCode) {
		texteVideo.setText(texteVideoSTR);
		aideVideo.setText(aideVideoSTR);
		System.out.println(SectionTimeCode);
		limiteTempsH.setText(SectionTimeCode.charAt(0) +""+ SectionTimeCode.charAt(1) + "");
		limiteTempsM.setText(SectionTimeCode.charAt(3) +""+ SectionTimeCode.charAt(4) + "");
	}
	
	public String getText() {
		return texteVideo.getText();
	}
	
	public String getAide() {
		return aideVideo.getText();
	}

	public String getlimiteTempsH() {
		return limiteTempsH.getText();
	}

	public String getlimiteTempsM() {
		return limiteTempsM.getText();
	}

	public void setLimiteTempsH(String string) {
		limiteTempsH.setText(string);
		
	}
	
	public void setLimiteTempsM(String string) {
		limiteTempsM.setText(string);
		
	}
	
	public void setStyleLimiteTempsH(String string) {
		limiteTempsH.setText(string);
		
	}
	
	public void setStyleLimiteTempsM(String string) {
		limiteTempsM.setText(string);
		
	}

	public void disableAide() {
		aide.setDisable(true);
		aideVideo.setDisable(true);
	}

	public void enableAide() {
		aide.setDisable(false);
		aideVideo.setDisable(false);
	}
}
