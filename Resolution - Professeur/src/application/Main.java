package application;


import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

	public static Stage stage;
	public static AnchorPane pagePrincipale;

	@Override
	public void start(Stage primaryStage) {
		try {
			pagePrincipale = FXMLLoader.load(getClass().getResource("MenuPrincipal.fxml"));
			Scene scene = new Scene(pagePrincipale, 450, 450);
			primaryStage.setTitle("Résolution V1.0 G8 - Professeur");
			primaryStage.setScene(scene);
			primaryStage.show();
			stage = primaryStage;
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
	
	public void chargerUnePage(String nomPage) throws IOException {
		AnchorPane pane = FXMLLoader.load(getClass().getResource(nomPage));
		pagePrincipale.getChildren().setAll(pane);
	}

	public void setHauteur(int i) {
		stage.setHeight(i);
	}
	
	public void setLargeur(int i) {
		stage.setWidth(i);
	}


}
