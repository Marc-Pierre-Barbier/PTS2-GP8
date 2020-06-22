package application;


import application.model.Lang;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

	public static Stage stage;
	public static GridPane root;

	private static Pane pagePrincipale;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			root = FXMLLoader.load(getClass().getResource("/application/view/Home.fxml"));
			Scene scene = new Scene(root, 1142, 660);
			pagePrincipale=root;
			primaryStage.setTitle(Lang.TITRE);
			primaryStage.setScene(scene);
			primaryStage.show();
			stage = primaryStage;
			stage.getIcons().add(new Image("/application/favicon.png"));
			//stage.setResizable(false);
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
	
	public static Pane getRoot(){
		return pagePrincipale;
	}
	
	public static void setHauteur(double i) {
		stage.setHeight(i);
	}
	
	public static void setLargeur(double i) {
		stage.setWidth(i);
	}
	


}
