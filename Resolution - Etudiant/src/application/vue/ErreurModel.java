package application.vue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import application.model.Lang;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class ErreurModel {
	public static void erreur(String entete, String contenu) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(Lang.ERREUR);
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

	public static void erreurStack(Exception e) {
		Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(Lang.ERREUR + Lang.INCONU);
        alert.setHeaderText(Lang.UNKNOWN_ERR);
 
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
	
    public static boolean confirmDialog(String titre,String entete,String contenu) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setHeaderText(entete);
        alert.setContentText(contenu);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;

    }
	
	private static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

	public static void infoDialog(String str,String titre) {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle(titre);
    	alert.setHeaderText(null);
    	alert.setContentText(str);
    	//TODO IMAGE
    	alert.setGraphic(new ImageView());
    	alert.showAndWait();
    }
    
    
}
