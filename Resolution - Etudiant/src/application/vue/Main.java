package application.vue;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

	public static Stage stage;
	public static GridPane root;

	@Override
	public void start(Stage primaryStage) {
		try {
			root = FXMLLoader.load(getClass().getResource("../model/Home.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Résolution V1.0 G8 - Etudiant");
			primaryStage.setScene(scene);
			primaryStage.show();
			stage = primaryStage;
			stage.setResizable(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public Stage getStage() {
		return stage;
	}
	

	


}
