package application.model;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SectionModel {
	private Tab Section;
	private VBox vbox;
	private Text texte;
	private TextArea texteVideo;
	private Text aide;
	private TextArea aideVideo;
	
	
	public SectionModel(TabPane parent,String texteCache,int nbtab) {
		Section = new Tab("Section "+nbtab);
		vbox = new VBox();
		texte = new Text("texte");
		Section.setContent(vbox);
		texteVideo = new TextArea(texteCache) {
        	@Override
        	public void copy() {
        		System.out.println("tu va pas triché quand meme !");
        	}
        };
        
        texteVideo.setEditable(false);
		parent.getTabs().add(Section);
		vbox.getChildren().addAll(texte,texteVideo);
	}
	
	public TextArea getTextvideo() {
		return texteVideo;
	}
	
	public String getAide() {
		return aideVideo.getText();
	}
}
