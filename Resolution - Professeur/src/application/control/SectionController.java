package application.control;

import java.util.List;

import application.model.Section;
import application.model.timeCodeTab;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class SectionController {
	@FXML
	TabPane tabPane;
	
	public void run(Scene scene, List<Section> sections,int dureVideo){
		Stage stga = new Stage();
		stga.setScene(scene);
		stga.show();
		for(Section s : sections) {
			tabPane.getTabs().add(new timeCodeTab(s,dureVideo));
		}
	}

}
