package application.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

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

	public static void erreurStack(IOException e) {
		Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur inconu");
        alert.setHeaderText("une erreur inconu c'est produite");
 
        VBox dialogPaneContent = new VBox();
 
        Label label = new Label("Stack Trace:");
 
        String stackTrace = getStackTrace(e);
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setText(stackTrace);
 
        dialogPaneContent.getChildren().addAll(label, textArea);
 
        // Set content for Dialog Pane
        alert.getDialogPane().setContent(dialogPaneContent);
 
        alert.showAndWait();
		
	}
	
    private static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
