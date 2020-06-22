package application.view;

import application.model.Lang;
import application.model.Section;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SectionModel {
	private Tab sectionTab;
	private VBox vbox;
	private Text texte;
	private TextArea texteVideo;
	private Section section;
	//private TextArea aideVideo;
	
	
	public SectionModel(TabPane parent,String texteCache,int nbtab, application.model.Section section) {
		sectionTab = new Tab(Lang.SECTION +nbtab);
		sectionTab.setOnSelectionChanged(e -> timeHandle(sectionTab));
		this.section=section;
		vbox = new VBox();
		texte = new Text("texte");
		sectionTab.setContent(vbox);
		texteVideo = new TextArea(texteCache) {
        	@Override
        	public void copy() {
        		System.out.println("tu va pas triché quand meme !");
        	}
        };
        
        texteVideo.setEditable(false);
		parent.getTabs().add(sectionTab);
		vbox.getChildren().addAll(texte,texteVideo);
	}
	
	private Object timeHandle(Tab sectionTab) {
		if(sectionTab.isSelected()) {
			section.startTimer();
		}else {
			section.stopTimer();
		}
		return null;
	}

	public TextArea getTextvideo() {
		return texteVideo;
	}
	
	/*public String getAide() {
		return aideVideo.getText();
	}*/
}
