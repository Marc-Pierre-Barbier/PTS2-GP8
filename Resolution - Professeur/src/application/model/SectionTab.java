package application.model;

import java.util.List;

import application.control.Section;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class SectionTab {
	private static Tab sectionsTabNEW;
	private static Button btnNewSection;
	
	public static void newSectionTab(TabPane sectionsTabPane,TabPane sectionsTimeCodePane,List<Section> sections) {
		sectionsTabNEW = new Tab("+");
		btnNewSection = new Button("nouvelle section");
		
		sectionsTimeCodePane.getTabs().clear();
		sectionsTabPane.getTabs().add(sectionsTabNEW);
		
		sectionsTabNEW.setContent(btnNewSection);
		btnNewSection.setOnAction(e -> sections.add(new Section(sectionsTabPane, sectionsTimeCodePane)));
	}
}
