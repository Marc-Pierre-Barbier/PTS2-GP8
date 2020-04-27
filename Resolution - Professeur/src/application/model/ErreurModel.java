package application.model;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ErreurModel {
	public static void erreur(String entete, String contenu) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Erreur");
		alert.setHeaderText(entete);
		alert.setContentText(contenu);
		alert.showAndWait();
	}
	
	public static Alert warn(String titre,String entete, String contenu) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(titre);
		alert.setHeaderText(entete);
		alert.setContentText(contenu);
		return alert;
	}
}
