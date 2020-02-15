package application.Etudiant;

import java.io.File;

import application.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HomeController extends Main{
	
	@FXML
	MediaPlayer mediaPlayer;

	@FXML
	public void onOpenExercice(ActionEvent e) {
		Stage s = super.getStage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All", "*"),
				new FileChooser.ExtensionFilter("*.mp3", "*.mp3"), new FileChooser.ExtensionFilter("*.mp4", "*.mp4"));
		fileChooser.setTitle("Open Resource File");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File selectedFile = fileChooser.showOpenDialog(s);
		if (selectedFile != null) {
			System.out.println(selectedFile.getAbsolutePath());
			selectedFile.getAbsolutePath();
		}

	}

	@FXML
	public void onQuit(ActionEvent e) {
		Alert alert = new Alert(AlertType.INFORMATION, "Êtes-vous sur de vouloir quitter ? ", ButtonType.YES,
				ButtonType.CANCEL);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.YES) {
			System.exit(0);
		}
	}
	
	public void c() {
		mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {

			@Override
			public void changed(ObservableValue<? extends Duration> arg0, Duration arg1, Duration arg2) {
				// TODO Auto-generated method stub
				
			}
			
		}
	}

}
