package application.view;

import application.model.Lang;
import application.model.timeCode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SectionModel {
	
	private static final int TAILLE_CHAMP_TEXTE_TIMER = 30;
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
	private timeCode timecode;
	
	public SectionModel(TabPane parent,TabPane sectionsTimeCodePane,int nbtab) {
		timecode = new timeCode();
		section = new Tab(Lang.SECTION + nbtab);
		vbox = new VBox();
		texte = new Text(Lang.TEXTE);
		texteVideo = new TextArea();
		aide = new Text(Lang.AIDE);
		aideVideo = new TextArea();
		aideVideo.setMaxHeight(100);
		section.setContent(vbox);
		vbox.getChildren().addAll(texte,texteVideo,aide,aideVideo);
		parent.getTabs().add(section);
		
		limiteTempsTab = new Tab("Section "+nbtab);
		limiteTempsH = new TextArea("00");
		limiteTempsH.setMinHeight(TAILLE_CHAMP_TEXTE_TIMER);
		limiteTempsH.setMaxHeight(TAILLE_CHAMP_TEXTE_TIMER);
		limiteTempsH.setOnKeyReleased(e -> timecode.setHours(limiteTempsH.getText()));
		limiteTempsM = new TextArea("00");
		limiteTempsM.setMinHeight(TAILLE_CHAMP_TEXTE_TIMER);
		limiteTempsM.setMaxHeight(TAILLE_CHAMP_TEXTE_TIMER);
		limiteTempsM.setOnKeyReleased(e -> timecode.setMinutes(limiteTempsM.getText()));
		boxtime= new HBox();
		boxtime.getChildren().addAll(new Text(Lang.LIMITE_TEMP),limiteTempsH,new Text("h"),limiteTempsM,new Text("min"));
		limiteTempsTab.setContent(boxtime);
		sectionsTimeCodePane.getTabs().add(limiteTempsTab);
	}
	
	public void sectionLoad(String aideVideoSTR,String texteVideoSTR,String SectionTimeCode) {
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

	public timeCode getTimecode() {
		return timecode;
	}

	public void setLimiteTempsH(String string) {
		limiteTempsH.setText(string);
		
	}
	
	public void setLimiteTempsM(String string) {
		limiteTempsM.setText(string);
		
	}
	
	public void setStyleLimiteTempsH(String string) {
		limiteTempsH.setStyle(string);
		
	}
	
	public void setStyleLimiteTempsM(String string) {
		limiteTempsM.setStyle(string);
		
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
