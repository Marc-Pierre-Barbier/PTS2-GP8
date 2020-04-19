package application;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Base64;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static Stage stage;

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Résolution V1.0 G8 - Etudiant");
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
	
	public void JSONCreation() {
		System.out.println("Lancement");
		JSONObject obj = new JSONObject();
        obj.put("name", "mkyong.com");
        obj.put("age", 100);

        JSONArray list = new JSONArray();
        list.add("msg 1");
        list.add("msg 2");
        list.add("msg 3");

        obj.put("messages", list);
        
        String originalInput = "test input";
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
        obj.put("Test", encodedString);

        try (FileWriter file = new FileWriter("c:\\project\\test.json")) {
            file.write(obj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print(obj);
	}
	
	public void JSONReader() throws ParseException {
		JSONParser parser = new JSONParser();
		System.out.println("Lancement du décodeur");
        try (Reader reader = new FileReader("c:\\project\\test.json")) {

            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            System.out.println(jsonObject);
            
            String Test = (String) jsonObject.get("Test");
            
            byte[] decodedBytes = Base64.getDecoder().decode(Test);
            String decodedString = new String(decodedBytes);
            
            System.out.println("Fichier décodé : " + decodedString);
        }catch(IOException e){
        	
        }
	}

}
