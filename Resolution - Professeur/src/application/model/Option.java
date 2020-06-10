package application.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.control.ApplicationController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Option {

	public static final String POLICE_COMBO_BOX_SIZE_ID = "policeSize";
	public static final String POLICE_CHECK_BOX_ID = "policeChbx";
	public static final String DALTONIEN_CHECK_BOX_ID = "daltonChbx";
	
	@SuppressWarnings("unchecked")
	public Option() throws IOException {
		Stage sta = new Stage();
		BorderPane OptionRoot = FXMLLoader.load(getClass().getResource("/application/view/MenuHandicap.fxml"));
		Scene OptionScene = new Scene(OptionRoot);
		sta.setScene(OptionScene);
		sta.show();
		for (Node e : getFinalChildren(OptionRoot)) {
			if (e.getId() != null)
				switch (e.getId()) {
				case POLICE_CHECK_BOX_ID:
					((CheckBox) e).setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							for (Node pol :  getFinalChildren(OptionRoot)) {
								if (pol.getId().contentEquals(POLICE_COMBO_BOX_SIZE_ID)) {
									pol.setDisable(!pol.isDisable());
								}
							}
						}
					});
					break;
				case POLICE_COMBO_BOX_SIZE_ID:
					for (int i = 13; i <= 20; i++)
						((ComboBox<String>) e).getItems().add(i + "");
					((ComboBox<String>) e).getSelectionModel().selectFirst();
					break;
				case "anulerOption":
					((Button) e).setOnAction(event -> sta.close());
					break;
				case "validerOption":
					((Button) e).setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							for (Node node : getFinalChildren(OptionRoot)) {
								if (node.getId().equals(POLICE_CHECK_BOX_ID) && ((CheckBox) node).isSelected()) {
									for (Node pol : getFinalChildren(OptionRoot)) {
										if (pol.getId().equals(POLICE_COMBO_BOX_SIZE_ID)) {
											ApplicationController.changePoliceSize(((ComboBox<String>) pol).getSelectionModel().getSelectedItem());
											ApplicationController.changeResolutionFromPolice(((ComboBox<String>) pol).getSelectionModel().getSelectedItem());
										}
									}
								}
								if (node.getId() == DALTONIEN_CHECK_BOX_ID && ((CheckBox) node).isSelected()) {
									//TODO DALTONINEN
								}
							}
							sta.close();
						}
					});
					break;
				case DALTONIEN_CHECK_BOX_ID:
					// TODO mode daltonien
					break;
				default:
					break;
				}

		}

	}

	// algo recusif
	public static List<Node> getFinalChildren(Node node) {
		List<Node> list = new ArrayList<>();
		if (!(node instanceof Pane)) {
			list.add(node);
			return list;
		}
		for (Node e : ((Pane) node).getChildren()) {
			list.addAll(getFinalChildren(e));
		}

		return list;
	}

}
