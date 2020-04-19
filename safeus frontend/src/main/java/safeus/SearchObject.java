package safeus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;

public class SearchObject {
	
	public AnchorPane searchRoot;
	
	public Label shopId;
	public Label shopName;
	public Label ownerName;
	public Label shopType;
	public Label openingTime;
	public Label closingTime;
	public Label timeReqPerUser;
	public Label address;
	public Label cityPinCode;	//concatenated as one Label
	public Label phoneEmail;
	public Label isOpen;
	
	public Label ownerNameLabel;
	public Label openingTimeLabel;
	public Label closingTimeLabel;
	public Label timeReqPerUserLabel;
	
	Button bookSlot;
	
	Tooltip tip0 = new Tooltip("Shop Owner is not taking any booking right now.");
	Tooltip tip1 = new Tooltip("Shop Owner is taking bookings. Click this button to booking a slot right now.");
	
	SearchObject(){
		searchRoot = new AnchorPane();
		
		shopId = new Label();
		shopName = new Label();
		ownerName = new Label();
		shopType = new Label();
		openingTime = new Label();
		closingTime = new Label();
		timeReqPerUser = new Label();
		address = new Label();
		cityPinCode = new Label();
		phoneEmail = new Label();
		isOpen = new Label();
		
		searchRoot.setPrefSize(535, 150);
		searchRoot.setMinSize(535, 150);
		searchRoot.setMaxSize(535, 150);
		
		openingTimeLabel = new Label("Opening at ");
		closingTimeLabel = new Label("Closing at");
		timeReqPerUserLabel = new Label("Time alloted:");
		ownerNameLabel = new Label("Owner:");
		
		bookSlot = new Button("Book Slot");
		bookSlot.setPrefWidth(120.0);
		
		// css
		bookSlot.setStyle("-fx-background-color: '#33cc33';" + "-fx-text-fill: '#ffffff';");
		shopId.setStyle("-fx-text-fill: '#000099';" + "-fx-font-size: 12px;" + "-fx-font-family: 'Microsoft JhengHei UI';");
		shopName.setStyle("-fx-text-fill: '#000066';" + "-fx-font-size: 15px;" + "-fx-font-weight: bold;" + "-fx-font-family: 'Microsoft JhengHei UI';");
		shopType.setStyle("-fx-font-family: 'Tahoma';" + "-fx-text-fill: '#737373';" + "-fx-font-size: 11.5px;");
		ownerNameLabel.setStyle("-fx-text-fill: '#660066';" + "-fx-font-size: 13px;");
		ownerName.setStyle("-fx-text-fill: '#ffa31a';" + "-fx-font-size: 13.5px;" + "-fx-font-weight: bold;");
		openingTimeLabel.setStyle("-fx-text-fill: '#666699';" + "-fx-font-size: 13px;" + "-fx-font-family: 'Arial Rounded MT Bold';");
		closingTimeLabel.setStyle("-fx-text-fill: '#666699';" + "-fx-font-size: 13px;" + "-fx-font-family: 'Arial Rounded MT Bold';");
		timeReqPerUserLabel.setStyle("-fx-text-fill: '#666699';" + "-fx-font-size: 13px;" + "-fx-font-family: 'Arial Rounded MT Bold';");
		openingTime.setStyle("-fx-font-family: 'Arial Rounded MT Bold';" + "-fx-text-fill: '#ff0000';" + "-fx-font-size: 13.5px;");
		closingTime.setStyle("-fx-font-family: 'Arial Rounded MT Bold';" + "-fx-text-fill: '#ff0000';" + "-fx-font-size: 13.5px;");
		timeReqPerUser.setStyle("-fx-font-family: 'Arial Rounded MT Bold';" + "-fx-text-fill: '#ff0000';" + "-fx-font-size: 13.5px;");
		address.setStyle("-fx-text-fill: '#996633';" + "-fx-font-family: 'Arial Rounded MT Bold';" + "-fx-font-size: 12px;");
		cityPinCode.setStyle("-fx-text-fill: '#8a8a5c';" + "-fx-font-family: 'Rockwell';" + "-fx-font-size: 12px;");
		isOpen.setStyle("" + "-fx-font-size: 12px;" + "-fx-text-fill: '#800000';" + "-fx-font-style: italic;");
		
		searchRoot.setStyle("-fx-border-style: solid none none none; -fx-border-width: 1; -fx-border-color: '#8c8c8c';");
		
		//bookSlot
		AnchorPane.setRightAnchor(bookSlot, 5.0);
		AnchorPane.setBottomAnchor(bookSlot, 3.0);
		//isOpen
		AnchorPane.setLeftAnchor(isOpen, 322.0);
		AnchorPane.setBottomAnchor(isOpen, 4.0);
		//shopId
		AnchorPane.setTopAnchor(shopId, 10.0);
		AnchorPane.setRightAnchor(shopId, 5.0);
		//shopName
		AnchorPane.setTopAnchor(shopName, 5.0);
		AnchorPane.setLeftAnchor(shopName, 10.0);
		//shopType
		AnchorPane.setTopAnchor(shopType, 25.0);
		AnchorPane.setLeftAnchor(shopType, 10.0);
		//ownerNameLabel
		AnchorPane.setTopAnchor(ownerNameLabel, 45.0);
		AnchorPane.setLeftAnchor(ownerNameLabel, 10.0);
		//ownerName
		AnchorPane.setTopAnchor(ownerName, 44.0);
		AnchorPane.setLeftAnchor(ownerName, 60.0);
		//openingTimeLabel
		AnchorPane.setTopAnchor(openingTimeLabel, 65.0);
		AnchorPane.setLeftAnchor(openingTimeLabel, 10.0);
		//openingTime
		AnchorPane.setTopAnchor(openingTime, 64.5);
		AnchorPane.setLeftAnchor(openingTime, 85.0);
		//closingTimeLabel
		AnchorPane.setTopAnchor(closingTimeLabel, 65.0);
		AnchorPane.setLeftAnchor(closingTimeLabel, 190.0 - 25.0);
		//timeReqPerUserLabel
		AnchorPane.setTopAnchor(timeReqPerUserLabel, 65.0);
		AnchorPane.setLeftAnchor(timeReqPerUserLabel, 320.0);
		//closingTime
		AnchorPane.setTopAnchor(closingTime, 64.5);
		AnchorPane.setLeftAnchor(closingTime, 260.0 - 25.0);
		//timeReqPerUser
		AnchorPane.setTopAnchor(timeReqPerUser, 64.5);
		AnchorPane.setLeftAnchor(timeReqPerUser, 410.0);
		//address
		AnchorPane.setTopAnchor(address, 100.0 - 5.0);
		AnchorPane.setLeftAnchor(address, 10.0);
		//cityPinCode
		AnchorPane.setTopAnchor(cityPinCode, 118.0 - 8.0);
		AnchorPane.setLeftAnchor(cityPinCode, 10.0);
		//phoneEmail
		AnchorPane.setLeftAnchor(phoneEmail, 10.0);
		AnchorPane.setBottomAnchor(phoneEmail, 1.0);
		
		searchRoot.getChildren().addAll(shopId);
		searchRoot.getChildren().addAll(shopName);
		searchRoot.getChildren().addAll(ownerName);
		searchRoot.getChildren().addAll(ownerNameLabel);
		searchRoot.getChildren().addAll(shopType);
		searchRoot.getChildren().addAll(openingTime);
		searchRoot.getChildren().addAll(closingTime);
		searchRoot.getChildren().addAll(timeReqPerUser);
		searchRoot.getChildren().addAll(address);
		searchRoot.getChildren().addAll(cityPinCode);
		searchRoot.getChildren().addAll(phoneEmail);
		searchRoot.getChildren().addAll(isOpen);
		searchRoot.getChildren().addAll(openingTimeLabel);
		searchRoot.getChildren().addAll(closingTimeLabel);
		searchRoot.getChildren().addAll(timeReqPerUserLabel);
		searchRoot.getChildren().addAll(bookSlot);
		
		bookSlot.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	
            	//first enter a note in alert
            	TextInputDialog td = new TextInputDialog("None");

            	td.setTitle("Book Slot");
            	td.setHeaderText("Enter your requirements");
            	
            	Optional<String> noteTaken = td.showAndWait();
            	
            	noteTaken.ifPresent(e -> {
            		System.out.println(noteTaken.get().toLowerCase());
            		
            		try {
            			
            			//first get booking details
            			//connecting
                		URL url = new URL("http://127.0.0.1:5000/bookslot/" + SafeusDriver.connectedEmail.trim().toLowerCase() + "/"  + shopId.getText().trim().substring(9) + "/" + td.getEditor().getText().toString().replace(" ", "%20") + "/0");
        				
        				
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
        				
        				System.out.println("Server Output: " + result);
        				
        				JSONParser parse = new JSONParser(); 
        				JSONObject jsonObject = (JSONObject)parse.parse(result);
        				
        				Alert confirm = new Alert(AlertType.CONFIRMATION);
        				confirm.setTitle("Book Slot Dialog");
        				confirm.setHeaderText("Slot Details");
        				confirm.setContentText(jsonObject.get("message").toString());
        				Optional<ButtonType> con = confirm.showAndWait();
        				
        				if(con.get() == ButtonType.OK) {
        					System.out.println("Booking slot");
        					//book slot
        					url = new URL("http://127.0.0.1:5000/bookslot/" + SafeusDriver.connectedEmail.trim().toLowerCase() + "/" + shopId.getText().trim().substring(9, shopId.getText().length()) + "/" + td.getEditor().getText().toString().replace(" ", "%20") + "/1");
            				
            				
            				conn = (HttpURLConnection) url.openConnection();
            				conn.setRequestMethod("GET");
            				conn.setRequestProperty("Accept", "JSON");
            				
            				//storing JSON object in a string
            				br = new BufferedReader(new InputStreamReader(
            						(conn.getInputStream())));
            				output = "";
            				result = "";
            				while ((output = br.readLine()) != null) {
            					result += output.trim();
            				}
            				
            				System.out.println("Server Output: " + result);
            				
            				parse = new JSONParser(); 
            				jsonObject = (JSONObject)parse.parse(result);
            				
            				if(jsonObject.get("message").toString().equalsIgnoreCase("True")) {
            					Alert done = new Alert(AlertType.INFORMATION);
            					done.setTitle("Successful");
            					done.setHeaderText("Slot Booked");
            					done.setContentText("Your slot has been booked. Please check your email for details.\n\n\n");
            					done.show();
            				}else {
            					Alert done = new Alert(AlertType.ERROR);
            					done.setTitle("Failed");
            					done.setHeaderText("Slot Not Booked");
            					done.setContentText(jsonObject.get("message") + "\n\n\n");
            					done.show();
            				}
        				}
        				
            		}catch(Exception eq) {
            			System.out.println("Slot:" + eq);
            		}
            		
            	});
            	
            	
            	
            	
            	
				
				
            }
        });
		
	}
}
