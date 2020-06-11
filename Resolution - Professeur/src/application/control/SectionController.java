package application.control;

import java.util.List;

import application.model.Section;
import application.model.timeCodeTab;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class SectionController {
	@FXML
	TabPane tabPane;
	@FXML
	Button newbtn;
	
	public void run(Scene scene, List<Section> sections,int dureVideo,TabPane sectionsTabPane, TabPane sectionsTimeCodePane){
		Stage stga = new Stage();
		stga.setScene(scene);
		stga.show();
		for(Section s : sections) {
			tabPane.getTabs().add(new timeCodeTab(s,dureVideo));
			
		}
		
		newbtn.setOnAction(e -> newTab(sections, dureVideo, sectionsTabPane, sectionsTimeCodePane));
	}
	
	private void newTab(List<Section> sections,int dureVideo,TabPane sectionsTabPane, TabPane sectionsTimeCodePane) {
		Section n = new Section(sectionsTabPane, sectionsTimeCodePane);
		sections.add(n);
		tabPane.getTabs().add(new timeCodeTab(n,dureVideo));
	}

}
