package application.view;


import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
	protected static Stage stage;
	protected static Pane pagePrincipale; //le static es essenciel sinon sa print une erreur sur chargerUnePage
	private static Scene scene;
	protected GridPane pane;
	@Override
	public void start(Stage primaryStage) {
		try {
			System.out.println(getClass().getResource("/application/model/MenuPrincipal.fxml"));
			pagePrincipale = FXMLLoader.load(getClass().getResource("/application/model/MenuPrincipal.fxml"));
			scene = new Scene(pagePrincipale);//, 450, 450
			primaryStage.setTitle("Résolution V1.0 G8 - Professeur");
			primaryStage.setScene(scene);
			primaryStage.show();
			stage = primaryStage;
			//stage.setResizable(false);
			stage.getIcons().add(new Image("/application/favicon.png"));
			
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
		pagePrincipale=pane;
		scene.setRoot(pane);
		pagePrincipale = pane;
		setHauteur(pane.getPrefHeight()+40);
		setLargeur(pane.getPrefWidth());
		stage.setMinHeight(pane.getMinHeight());
		stage.setMinWidth(pane.getMinWidth());
	}

	public static void setHauteur(double i) {
		stage.setHeight(i);
	}
	
	public static void setLargeur(double i) {
		stage.setWidth(i);
	}
	public static Pane getRoot(){
		return pagePrincipale;
	}

}
