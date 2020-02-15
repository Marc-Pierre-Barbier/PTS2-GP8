package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class FXMLDocumentController implements Initializable{
	
	@FXML
	private AnchorPane rootPane;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@FXML
	public void loadSecond(ActionEvent e) throws IOException {
		AnchorPane pane = FXMLLoader.load(getClass().getResource("Exercice.fxml"));
		rootPane.getChildren().setAll(pane);
	}

	
}
