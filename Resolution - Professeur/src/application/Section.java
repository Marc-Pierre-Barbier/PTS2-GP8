package application;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
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
	
	//time
	private Tab limiteTempsTab;
	private TextArea limiteTempsH;
	private TextArea limiteTempsM;
	private HBox boxtime;
	private final int TAILLE_CHAMP_TEXTE_TIMER = 30;

	public Section(TabPane parent, TabPane sectionsTimeCodePane,String aideVideoSTR,String texteVideoSTR,String SectionTimeCode) {
		this(parent,sectionsTimeCodePane);
		texteVideo.setText(texteVideoSTR);
		aideVideo.setText(aideVideoSTR);
		System.out.println(SectionTimeCode);
		limiteTempsH.setText(SectionTimeCode.charAt(0) +""+ SectionTimeCode.charAt(1) + "");
		limiteTempsM.setText(SectionTimeCode.charAt(3) +""+ SectionTimeCode.charAt(4) + "");
	}
	
	
	public Section(TabPane parent, TabPane sectionsTimeCodePane) {
		nbtab++;
		idTab=nbtab;
		Section = new Tab("Section "+nbtab);
		vbox = new VBox();
		texte = new Text("texte");
		texteVideo = new TextArea();
		aide = new Text("aide");
		aideVideo = new TextArea();
		aideVideo.setMaxHeight(100);
		Section.setContent(vbox);
		vbox.getChildren().addAll(texte,texteVideo,aide,aideVideo);
		parent.getTabs().add(Section);
		
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

	public int getidTab(){
		return idTab;
	}

	public String getText() {
		return texteVideo.getText();
	}
	
	public String getAide() {
		return aideVideo.getText();
	}
	
	public String getTimeCode() {
		if(limiteTempsH.getText().length() == 1)limiteTempsH.setText("0"+limiteTempsH.getText());
		if(limiteTempsM.getText().length() == 1)limiteTempsM.setText("0"+limiteTempsM.getText());
		if(limiteTempsH.getText().length() == 0)limiteTempsH.setText("00");
		if(limiteTempsM.getText().length() == 0)limiteTempsM.setText("00");
		if(limiteTempsH.getText().length() > 2) {
			limiteTempsM.setStyle("-fx-text-inner-color: red;"); 
			return "ABORT";
		}
		if(limiteTempsM.getText().length() > 2) {
			limiteTempsM.setStyle("-fx-text-inner-color: red;");  
			return "ABORT";
		}
		
		return "00:"+limiteTempsM.getText() + ":" + limiteTempsM.getText();
		
	}
	/*public Section(TabPane tabPane,TabPane sectionsTimeCodePane) {
		this(tabPane,sectionsTimeCodePane);
	}*/
	public static void reset() {
		nbtab=0;
	}
}
