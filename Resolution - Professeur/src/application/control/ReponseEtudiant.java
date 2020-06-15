package application.control;

import java.util.Base64;

import org.jdom2.Element;

import application.model.Lang;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ReponseEtudiant {
	private Stage stage;
	
	@FXML
	private TabPane tabs;
	@FXML
	private Text reponse;
	
	public void run() {
		String rep="";
		for(Tab t : tabs.getTabs()) {
			rep+=((TextField)t.getContent()).getText()+" ";
		}
		rep = rep.trim();
		rep += " ";
		int nbMot = count(rep,' ');
		reponse.setText(countMotTrouve(rep,'*')+"/"+nbMot);

		stage.show();
	}

	/**
	 * retourne le nombre de mot qui ne contienne pas le caratére invalide
	 * @param rep
	 * @param invalide
	 * @return nom de mot trouve
	 */
	private int countMotTrouve(String rep, char invalide) {
		int i=0;
		rep += " ";
		String str="";
		for(char cursor : rep.toCharArray()) {
			if(cursor != ' ') {
				str += cursor;
			}else {
				if(!str.contains("*")) {
					i++;
				}
			}
		}
		return i;
	}

	private int count(String rep, char c) {
		int i=0;
		for(char cursor : rep.toCharArray()) {
			if(cursor == c) {
				i++;
			}
		}
		return i;
	}

	public void init(Stage sta) {
		stage = sta;
		
	}
	
	public void addTab(Element elem) {
		Tab t = new Tab(Lang.SECTION +(tabs.getTabs().size() + 1 ));
		byte[] raw = Base64.getDecoder().decode(elem.getChild("reponseEtudiant").getValue());
		TextField content = new TextField(new String(raw));
		content.setEditable(false);
		t.setContent(content);
		tabs.getTabs().add(t);
	}
}
