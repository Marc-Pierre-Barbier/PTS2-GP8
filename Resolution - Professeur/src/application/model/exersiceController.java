package application.model;

import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;

public class exersiceController {
	
	private TabPane exersicePane;
	private Node emptyTabExersice;
	
	public void onNewExersice() {
		emptyTabExersice = exersicePane.getTabs().get(0).getContent();
	}
	
	public void onAddSection() {
		TextInputDialog dialog = new TextInputDialog(Lang.ENTRER_TITRE);
		dialog.setTitle(Lang.TITRE_SECTION);
		dialog.setHeaderText(Lang.NOUV_SECTION);
		dialog.setContentText(Lang.ENTRER_TITRE_SECTION);

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
		    if(exersicePane != null) {
		    	exersicePane.getTabs().add(new Tab(result.get(), emptyTabExersice));
		    }else {
		    	System.out.println("MERCI DE DEFINIR LE TABPANE");
		    }
		}else{
			System.out.println("CANCELLED");
		}
	}
	
	public void onRemoveSection() {
		
	}
	
	public void setExersicePane(TabPane t) {
		exersicePane = t;
	}
	
	public TabPane getExersicePane() {
		return exersicePane;
	}

}
