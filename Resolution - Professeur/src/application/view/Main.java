package application.view;


import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
	protected static Stage stage;
	protected static AnchorPane pagePrincipale; //le static es essenciel sinon sa print une erreur sur chargerUnePage
	protected AnchorPane pane;
	@Override
	public void start(Stage primaryStage) {
		try {
			System.out.println(getClass().getResource("/application/model/MenuPrincipal.fxml"));
			pagePrincipale = FXMLLoader.load(getClass().getResource("/application/model/MenuPrincipal.fxml"));
			
			//pagePrincipale = FXMLLoader.load(getClass().getResource("../model/MenuPrincipal.fxml"));
			Scene scene = new Scene(pagePrincipale, 450, 450);
			primaryStage.setTitle("Résolution V1.0 G8 - Professeur");
			primaryStage.setScene(scene);
			primaryStage.show();
			stage = primaryStage;
			stage.setResizable(false);
			
		} catch (Exception e) {
			System.out.println("Au revoir.");
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
		pane = FXMLLoader.load(getClass().getResource(nomPage));
		pagePrincipale.getChildren().setAll(pane);
	}

	public void setHauteur(int i) {
		stage.setHeight(i);
	}
	
	public void setLargeur(int i) {
		stage.setWidth(i);
	}


}
