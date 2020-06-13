package application.control;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Option {

	public static final String POLICE_CHECK_BOX_ID = "policeChbx";
	public static final String DALTONIEN_CHECK_BOX_ID = "daltonChbx";
	
	@FXML
	private CheckBox policeChbx;
	
	@FXML
	private ComboBox<String> policeSize;
	@FXML
	private Button anulerOption;
	@FXML
	private Button validerOption;
	
	
	public void run(Stage sta) {
		policeChbx.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				policeSize.setDisable(!policeSize.isDisable());
			}
		});
		for (int i = 13; i <= 20; i++)
			policeSize.getItems().add(i + "");
		
		policeSize.getSelectionModel().selectFirst();
		
		anulerOption.setOnAction(event -> sta.close());
		validerOption.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if(policeChbx.isSelected()) {
					ApplicationController.changePoliceSize(policeSize.getSelectionModel().getSelectedItem());
					ApplicationController.changeResolutionFromPolice(policeSize.getSelectionModel().getSelectedItem());
				}
				sta.close();
			}
		});
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
