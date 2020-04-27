package application.control;

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
		TextInputDialog dialog = new TextInputDialog("Entrez un titre");
		dialog.setTitle("Nouvelle Section");
		dialog.setHeaderText("Ajouter une nouvelle section");
		dialog.setContentText("Entrez le titre de la section : ");

		// Traditional way to get the response value.
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
