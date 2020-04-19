package safeus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.text.WordUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class UpcomingEventsPageController {
	
	@FXML
	AnchorPane root;
	
	@FXML
	Button back;
	
	@FXML
	VBox eventsPane;
	
	static TextField ownerName = new TextField();
	static TextField address = new TextField();
	static TextField city = new TextField();
	static TextField pin = new TextField();
	static TextField phone = new TextField();
	static TextField email = new TextField();
	static TextArea note = new TextArea();
	
	final char[] delimiters = { ' ', '_' };
	
	@SuppressWarnings("deprecation")
	public void loadUpcomingEvents(String userEmail) {
		// this function loads upcoming events in the Application
		
		eventsPane.getChildren().clear();
		System.out.println("in function");
		
		try {
			//connecting
			URL url = new URL("http://127.0.0.1:5000/events/upcoming/" + userEmail);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "JSON");
		
			//storing JSON object in a string
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			String output,result = "";
			while ((output = br.readLine()) != null) {
				result += output.trim();
			}
			result = "{\"results\" : " + result + " }";
			System.out.println("Server Output: " + result);
			
			JSONParser parse = new JSONParser(); 
			JSONObject jsonObject = (JSONObject)parse.parse(result);
			JSONArray searchResultArray = (JSONArray) jsonObject.get("results");
			
			if(result.length() < 20) {
				System.out.println("History is empty.");
				return;
			}
			
			//Get data for Results array
			for(int i = 0 ; i < searchResultArray.size() ; i++)
			{
				Label sepLabel = new Label("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t");
				sepLabel.setUnderline(true);
				
				JSONObject arrayElement =  (JSONObject)searchResultArray.get(i);
				UpcomingEventsObject upcomingElement = new UpcomingEventsObject();
				
				upcomingElement.strOwnerName = WordUtils.capitalizeFully(arrayElement.get("owner_name").toString(), delimiters);
				upcomingElement.strAddress = WordUtils.capitalizeFully(arrayElement.get("address").toString(), delimiters);
				upcomingElement.strCity = WordUtils.capitalizeFully(arrayElement.get("city").toString(), delimiters);
				upcomingElement.strPinCode = arrayElement.get("pin_code").toString();
				upcomingElement.strPhone = arrayElement.get("phone").toString();
				upcomingElement.strEmail = arrayElement.get("email").toString();
				upcomingElement.strNote = arrayElement.get("note").toString();
				
				upcomingElement.shopName.setText(WordUtils.capitalizeFully(arrayElement.get("shop_name").toString(), delimiters));
				upcomingElement.shopId.setText(arrayElement.get("shop_id").toString());
				upcomingElement.shopType.setText(arrayElement.get("shop_type").toString());
				upcomingElement.date.setText(arrayElement.get("event_day").toString());
				upcomingElement.incomingTime.setText(arrayElement.get("incoming_time").toString());
				upcomingElement.outgoingTime.setText(arrayElement.get("outgoing_time").toString());
				upcomingElement.status.setText(arrayElement.get("status").toString());
				
				eventsPane.getChildren().addAll(upcomingElement.booking, sepLabel);
				System.out.println("in loop");
			}
		}catch(Exception err) {
			System.out.println("Loading upcoming Bookings: " + err);
		}
	}
	
	@FXML
	public void initialize() {
		
		root.getChildren().addAll(ownerName, address, city, pin, phone, email, note);
		//ownerName
		AnchorPane.setLeftAnchor(ownerName, 840.0);
		AnchorPane.setTopAnchor(ownerName, 181.0);
		//address
		AnchorPane.setLeftAnchor(address, 840.0);
		AnchorPane.setTopAnchor(address, 228.0);
		//city
		AnchorPane.setLeftAnchor(city, 840.0);
		AnchorPane.setTopAnchor(city, 277.0);
		//pin
		AnchorPane.setLeftAnchor(pin, 840.0);
		AnchorPane.setTopAnchor(pin, 326.0);
		//phone
		AnchorPane.setLeftAnchor(phone, 840.0);
		AnchorPane.setTopAnchor(phone, 377.0);
		//email
		AnchorPane.setLeftAnchor(email, 840.0);
		AnchorPane.setTopAnchor(email, 425.0);
		//note
		AnchorPane.setLeftAnchor(note, 840.0);
		AnchorPane.setTopAnchor(note, 469.0);
		
		ownerName.setPrefWidth(283);
		address.setPrefWidth(283);
		city.setPrefWidth(283);
		pin.setPrefWidth(283);
		phone.setPrefWidth(283);
		email.setPrefWidth(283);
		
		note.setPrefSize(283, 113);
		
		ownerName.setEditable(false);
		address.setEditable(false);
		city.setEditable(false);
		pin.setEditable(false);
		phone.setEditable(false);
		email.setEditable(false);
		note.setEditable(false);
		
		ownerName.setText("");
		address.setText("");
		city.setText("");
		pin.setText("");
		phone.setText("");
		email.setText("");
		note.setText("");
		
		
		ownerName.setPromptText("Owner");
		address.setPromptText("Address");
		city.setPromptText("City");
		pin.setPromptText("PIN Code");
		phone.setPromptText("Phone");
		email.setPromptText("Email ID");
		note.setPromptText("Note");
		
		//back
		try {
		FileInputStream inputBackImage = new FileInputStream("images/back.png");
		Image backImage = new Image(inputBackImage);
		back.setGraphic(new ImageView(backImage));
		}catch(Exception e) {}
		
		//load upcoming events
		loadUpcomingEvents(SafeusDriver.connectedEmail.trim().toLowerCase());
		
		back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
					try {
	    				System.out.println("go to upcoming events page");
	    				Parent loader = FXMLLoader.load(getClass().getResource("/fxml/user_home_page.fxml"));
						
						Scene scene = back.getScene();
						Window window = scene.getWindow();
						Stage stage = (Stage) window;
						
						back.getScene().setRoot(loader);
	    			}catch(Exception ee) {
	    				System.out.println("going to home page: " + ee);
	    			}
            	}
            });
		
	}
}
