package application.model;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Option {
	
	protected Parent OptionRoot;
	protected Scene OptionScene; 
	public void optionMenu() throws Exception {
		new Task<Integer>() {
			@Override
			protected Integer call() throws Exception {
				Stage sta = new Stage();
				OptionRoot = FXMLLoader.load(getClass().getResource("/application/vue/MenuHandicap.fxml"));
				OptionScene = new Scene(OptionRoot, 1142, 656);
				sta.setScene(OptionScene);
				sta.show();
				return null;
			}
		}.call();
	}
}
