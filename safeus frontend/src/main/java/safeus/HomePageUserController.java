package safeus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.Window;

public class HomePageUserController {
	
	@FXML
	Button shopsInMyCity;
	
	@FXML
	Button shopsNearMe;
	
	@FXML
	ComboBox category;
	
	@FXML
	TextField searchBox;
	
	@FXML
	Button go;
	
	@FXML
	Button deactivateAccount;
	
	@FXML
	Button logout;
	
	@FXML
	Button viewCurrentBookings;
	
	@FXML
	TextField userName;
	
	@FXML
	TextField address;
	
	@FXML
	TextField city;
	
	@FXML
	TextField pinCode;
	
	@FXML
	TextField phone;
	
	@FXML
	TextField email;
	
	@FXML
	Button save;
	
	@FXML
	Button aboutApp;
	
	@FXML
	VBox searchResultPane;
	
	@FXML
	VBox earlierBookingsPane;
	
	@SuppressWarnings("deprecation")
	public void loadUserDetails() {
		try {
			//connecting
			URL url = new URL("http://127.0.0.1:5000/information/" + SafeusDriver.connectedAs + "/" 
					+ SafeusDriver.connectedEmail);
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
			
			//loading user details
			userName.setText(WordUtils.capitalizeFully(jsonObject.get("user_name").toString(), delimiters));
			address.setText(WordUtils.capitalizeFully(jsonObject.get("address").toString(), delimiters));
			city.setText(WordUtils.capitalizeFully(jsonObject.get("city").toString(), delimiters));
			pinCode.setText(jsonObject.get("pin_code").toString());
			phone.setText(jsonObject.get("phone").toString());
			email.setText(jsonObject.get("email").toString());
			
		}catch(Exception err) {
			System.out.println("LoadingHomepage: " + err);
			
			Alert wrongCredentials = new Alert(AlertType.ERROR);
			wrongCredentials.setTitle("Error");
			wrongCredentials.setHeaderText("Sorry. Failed!");
			wrongCredentials.setContentText("Cannot connect to the server. Please try again "
					+ "after some time.\n\n\n");
			wrongCredentials.show();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void fetchSearchFromShopsInMyCity(String myCity) {
		// this function send search request to the Server to search shops in my city.
				searchResultPane.getChildren().clear();
				try {
					//connecting
					URL url = new URL("http://127.0.0.1:5000/search/city/" + myCity);
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
					
					Label heading = new Label("  Search results for " + myCity + ":");
					heading.setStyle("-fx-font-size: 15px;" + "-fx-font-style: italic;");
					searchResultPane.getChildren().add(heading);
					//Get data for Results array
					for(int i = 0 ; i < searchResultArray.size() ; i++)
					{
						Label sepLabel = new Label();
						
						JSONObject arrayElement =  (JSONObject)searchResultArray.get(i);
						SearchObject element = new SearchObject();
						
						element.shopId.setText("Shop ID: " + arrayElement.get("shop_id").toString());
						element.shopName.setText(WordUtils.capitalizeFully(arrayElement.get("shop_name").toString(), delimiters));
						element.ownerName.setText(WordUtils.capitalizeFully(arrayElement.get("owner_name").toString(), delimiters));
						element.shopType.setText(arrayElement.get("shop_type").toString());
						element.openingTime.setText(arrayElement.get("opening_time").toString());
						element.closingTime.setText(arrayElement.get("closing_time").toString());
						element.timeReqPerUser.setText(arrayElement.get("time_req_per_user").toString());
						element.address.setText(WordUtils.capitalizeFully(arrayElement.get("address").toString(), delimiters));
						element.cityPinCode.setText(WordUtils.capitalizeFully(arrayElement.get("city") + ", " + arrayElement.get("pin_code").toString(), delimiters));
						element.phoneEmail.setText(arrayElement.get("phone").toString() + ", " + arrayElement.get("email").toString());
						element.isOpen.setText(arrayElement.get("is_open").toString());
						
						searchResultPane.getChildren().addAll(element.searchRoot, sepLabel);
						
						if(element.isOpen.getText().trim().equalsIgnoreCase("1") == true) {
							element.isOpen.setText("");
							element.bookSlot.setTooltip(element.tip1);
							element.bookSlot.setDisable(false);
						} else {
							element.isOpen.setText("Booking Closed.");
							element.bookSlot.setDisable(true);
						}
					}
				}catch(Exception err) {
					System.out.println("ShopsInMyCity: " + err);
				}
	}
	
	@SuppressWarnings("deprecation")
	public void fetchShopsNearMe() {
		// this function is used to fetch shops in the same pincode as of the user's.
		searchResultPane.getChildren().clear();
		try {
			//connecting
			URL url = new URL("http://127.0.0.1:5000/search/nearby/" + pinCode.getText().trim());
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
			
			Label heading = new Label("  Search results for shops near me:");
			heading.setStyle("-fx-font-size: 15px;" + "-fx-font-style: italic;");
			searchResultPane.getChildren().add(heading);
			//Get data for Results array
			for(int i = 0 ; i < searchResultArray.size() ; i++)
			{
				Label sepLabel = new Label();
				
				JSONObject arrayElement =  (JSONObject)searchResultArray.get(i);
				SearchObject element = new SearchObject();
				
				element.shopId.setText("Shop ID: " + arrayElement.get("shop_id").toString());
				element.shopName.setText(WordUtils.capitalizeFully(arrayElement.get("shop_name").toString(), delimiters));
				element.ownerName.setText(WordUtils.capitalizeFully(arrayElement.get("owner_name").toString(), delimiters));
				element.shopType.setText(arrayElement.get("shop_type").toString());
				element.openingTime.setText(arrayElement.get("opening_time").toString());
				element.closingTime.setText(arrayElement.get("closing_time").toString());
				element.timeReqPerUser.setText(arrayElement.get("time_req_per_user").toString());
				element.address.setText(WordUtils.capitalizeFully(arrayElement.get("address").toString(), delimiters));
				element.cityPinCode.setText(WordUtils.capitalizeFully(arrayElement.get("city") + ", " + arrayElement.get("pin_code").toString(), delimiters));
				element.phoneEmail.setText(arrayElement.get("phone").toString() + ", " + arrayElement.get("email").toString());
				element.isOpen.setText(arrayElement.get("is_open").toString());
				
				System.out.println(element.ownerName.getText() + ": " + element.isOpen.getText());
				
				if(element.isOpen.getText().trim().equalsIgnoreCase("1") == true) {
					element.isOpen.setText("");
					element.bookSlot.setTooltip(element.tip1);
					element.bookSlot.setDisable(false);
				} else {
					element.isOpen.setText("Booking Closed.");
					element.bookSlot.setDisable(true);
				}
				
				searchResultPane.getChildren().addAll(element.searchRoot, sepLabel);
			}
		}catch(Exception err) {
			System.out.println("ShopsNearMe" + err);
		}
	}
	
	final char[] delimiters = { ' ', '_' };
	
	@SuppressWarnings("deprecation")
	public void fetchSearchFromSearchBox(String searchElement) {
		// this function send search request to the Server when 'Go' button is pressed.
		searchResultPane.getChildren().clear();
		try {
			//connecting
			URL url = new URL("http://127.0.0.1:5000/search/box/" + searchElement);
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
			
			Label heading = new Label("  Search results for " + searchElement + ":");
			heading.setStyle("-fx-font-size: 15px;" + "-fx-font-style: italic;");
			searchResultPane.getChildren().add(heading);
			//Get data for Results array
			for(int i = 0 ; i < searchResultArray.size() ; i++)
			{
				Label sepLabel = new Label();
				
				JSONObject arrayElement =  (JSONObject)searchResultArray.get(i);
				SearchObject element = new SearchObject();
				
				element.shopId.setText("Shop ID: " + arrayElement.get("shop_id").toString());
				element.shopName.setText(WordUtils.capitalizeFully(arrayElement.get("shop_name").toString(), delimiters));
				element.ownerName.setText(WordUtils.capitalizeFully(arrayElement.get("owner_name").toString(), delimiters));
				element.shopType.setText(arrayElement.get("shop_type").toString());
				element.openingTime.setText(arrayElement.get("opening_time").toString());
				element.closingTime.setText(arrayElement.get("closing_time").toString());
				element.timeReqPerUser.setText(arrayElement.get("time_req_per_user").toString());
				element.address.setText(WordUtils.capitalizeFully(arrayElement.get("address").toString(), delimiters));
				element.cityPinCode.setText(WordUtils.capitalizeFully(arrayElement.get("city") + ", " + arrayElement.get("pin_code").toString(), delimiters));
				element.phoneEmail.setText(arrayElement.get("phone").toString() + ", " + arrayElement.get("email").toString());
				element.isOpen.setText(arrayElement.get("is_open").toString());
				
				System.out.println(element.ownerName.getText() + ": " + element.isOpen.getText());
				
				searchResultPane.getChildren().addAll(element.searchRoot, sepLabel);
				
				if(element.isOpen.getText().trim().equalsIgnoreCase("1") == true) {
					element.bookSlot.setTooltip(element.tip1);
					element.isOpen.setText("");
					element.bookSlot.setDisable(false);
				} else {
					element.isOpen.setText("Booking Closed.");
					element.bookSlot.setDisable(true);
				}
			}
		}catch(Exception err) {
			System.out.println("SearchBox" + err);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void loadEarlierBookingsHistory() {
		// this function loads previous events in the Application
		earlierBookingsPane.getChildren().clear();
		System.out.println("in function");
		try {
			//connecting
			URL url = new URL("http://127.0.0.1:5000/events/earlier/" + email.getText().toLowerCase().trim());
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
				Label sepLabel = new Label();
				
				JSONObject arrayElement =  (JSONObject)searchResultArray.get(i);
				EarlierEventsObject pastElement = new EarlierEventsObject();
				
				pastElement.strShopId = arrayElement.get("shop_id").toString();
				pastElement.strShopName = WordUtils.capitalizeFully(arrayElement.get("shop_name").toString(), delimiters);
				pastElement.strOwnerName = WordUtils.capitalizeFully(arrayElement.get("owner_name").toString(), delimiters);
				pastElement.strShopType = WordUtils.capitalizeFully(arrayElement.get("shop_type").toString(), delimiters);
				pastElement.strAddress = WordUtils.capitalizeFully(arrayElement.get("address").toString(), delimiters);
				pastElement.strCity = WordUtils.capitalizeFully(arrayElement.get("city").toString(), delimiters);
				pastElement.strPinCode = arrayElement.get("pin_code").toString();
				pastElement.strPhone = arrayElement.get("phone").toString();
				pastElement.strEmail = arrayElement.get("email").toString();
				pastElement.strEventDay = arrayElement.get("event_day").toString();
				pastElement.strIncomingTime = arrayElement.get("incoming_time").toString();
				pastElement.strOutGoingTime = arrayElement.get("outgoing_time").toString();
				pastElement.strStatus = WordUtils.capitalizeFully(arrayElement.get("status").toString(), delimiters);
				pastElement.strNote = arrayElement.get("note").toString();
				
				pastElement.shopName.setText(WordUtils.capitalizeFully(arrayElement.get("shop_name").toString(), delimiters));
				pastElement.shopId.setText("Shop ID: " + arrayElement.get("shop_id").toString());
				pastElement.shopType.setText(arrayElement.get("shop_type").toString());
				pastElement.date.setText(arrayElement.get("event_day").toString());
				pastElement.incomingTime.setText(arrayElement.get("incoming_time").toString());
				pastElement.outgoingTime.setText(arrayElement.get("outgoing_time").toString());
				pastElement.status.setText(WordUtils.capitalizeFully("Status: " + arrayElement.get("status").toString(), delimiters));
				
				earlierBookingsPane.getChildren().addAll(pastElement.booking, sepLabel);
				System.out.println("in loop");
			}
		}catch(Exception err) {
			System.out.println("Loading Previous Bookings: " + err);
		}
	}
	
	public void updateInfo(String userName, String address, String city, String pinCode, String phone, String email) {
		// used to update user information and fetch user-latest information.
		
		userName = userName.trim().replace(" ", "%20");
		address = address.trim().replace(" ", "%20");
		city = city.trim().replace(" ", "%20");
		
		try {
			//connection
			URL url = new URL("http://127.0.0.1:5000/update/user/"+ userName + "/" + address + "/" + 
							city + "/" + pinCode + "/" + phone + "/" + email);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
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
			
			String isDone = jsonObject.get("message").toString();
			if(isDone.equalsIgnoreCase("True")) {
				Alert wrongCredentials = new Alert(AlertType.INFORMATION);
    			wrongCredentials.setTitle("Successful");
    			wrongCredentials.setHeaderText("Account Updated");
    			wrongCredentials.setContentText(
    							"Your account details has been updated. Press OK to continue." + 
    							"\n\n\n");
    			wrongCredentials.show();
			}else {
				Alert wrongCredentials = new Alert(AlertType.ERROR);
    			wrongCredentials.setTitle("Filed");
    			wrongCredentials.setHeaderText("Account not updated");
    			wrongCredentials.setContentText(
    					isDone + 
    					"\n\n\n");
    			wrongCredentials.show();
			}
			
		}catch(Exception error) {
			System.out.println("updateInfo: " + error);
		}finally {
			//fetch latest user-information
			loadUserDetails();
		}
	}
	
	public void aboutApp() {
		try {
			//connection
			URL url = new URL("http://127.0.0.1:5000/about");
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
			result = result.replace("*", "\n");
			JSONParser parse = new JSONParser(); 
			JSONObject jsonObject = (JSONObject)parse.parse(result);
			
			String aboutData = jsonObject.get("message").toString();
			Alert about = new Alert(AlertType.INFORMATION);
			
			
			about.setTitle("About App");
			about.setHeaderText("Safeus");
			about.setContentText(
					aboutData);
			about.getDialogPane().setMinWidth(800.0);
			about.show();
			
			
		}catch(Exception e) {
			System.out.println("aboutApp: " + e);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void fetchShopsAccordingCategory(String selectedCategory) {
		// this function fetches shops according to the category selected. 
		searchResultPane.getChildren().clear();
		try {
			//connecting
			URL url = new URL("http://127.0.0.1:5000/search/category/" + selectedCategory.replace(" ", "%20"));
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
			
			Label heading = new Label("  Search results for " + selectedCategory + ":");
			heading.setStyle("-fx-font-size: 15px;" + "-fx-font-style: italic;");
			searchResultPane.getChildren().add(heading);
			//Get data for Results array
			for(int i = 0 ; i < searchResultArray.size() ; i++)
			{
				Label sepLabel = new Label();
				
				JSONObject arrayElement =  (JSONObject)searchResultArray.get(i);
				SearchObject element = new SearchObject();
				
				element.shopId.setText("Shop ID: " + arrayElement.get("shop_id").toString());
				element.shopName.setText(WordUtils.capitalizeFully(arrayElement.get("shop_name").toString(), delimiters));
				element.ownerName.setText(WordUtils.capitalizeFully(arrayElement.get("owner_name").toString(), delimiters));
				element.shopType.setText(arrayElement.get("shop_type").toString());
				element.openingTime.setText(arrayElement.get("opening_time").toString());
				element.closingTime.setText(arrayElement.get("closing_time").toString());
				element.timeReqPerUser.setText(arrayElement.get("time_req_per_user").toString());
				element.address.setText(WordUtils.capitalizeFully(arrayElement.get("address").toString(), delimiters));
				element.cityPinCode.setText(WordUtils.capitalizeFully(arrayElement.get("city") + ", " + arrayElement.get("pin_code").toString(), delimiters));
				element.phoneEmail.setText(arrayElement.get("phone").toString() + ", " + arrayElement.get("email").toString());
				element.isOpen.setText(arrayElement.get("is_open").toString());
				
				System.out.println(element.ownerName.getText() + ": " + element.isOpen.getText());
				
				searchResultPane.getChildren().addAll(element.searchRoot, sepLabel);
				
				if(element.isOpen.getText().trim().equalsIgnoreCase("1") == true) {
					element.bookSlot.setTooltip(element.tip1);
					element.isOpen.setText("");
					element.bookSlot.setDisable(false);
				} else {
					element.isOpen.setText("Booking Closed.");
					element.bookSlot.setDisable(true);
				}
			}
		}catch(Exception err) {
			System.out.println("Shops according category: " + err);
		}
	}
	
	public void deactivateUserAccount(String userEmail) {
		try {
			Alert ask = new Alert(AlertType.CONFIRMATION);
    		ask.setTitle("Confirmation");
    		ask.setHeaderText("Deactivate Account?");
    		ask.setContentText("Are you sure? If yes then press OK else press Cancel.\n\n\n");
    		Optional<ButtonType> what = ask.showAndWait();
    		System.out.println("ask");
    		if(what.get() == ButtonType.OK) {
        		System.out.println("ok");
    			//deactivate account
    			//connecting
    			URL url = new URL("http://127.0.0.1:5000/deleteaccount/user/" + SafeusDriver.connectedEmail);
    			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    			conn.setRequestMethod("POST");
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
    			
    			String whatHappened = jsonObject.get("message").toString();
    			System.out.println("output:" + whatHappened);
    			if(whatHappened.trim().toLowerCase().equalsIgnoreCase("True")) {
    				System.out.println("True");
    				Alert done = new Alert(AlertType.INFORMATION);
    				done.setTitle("Successful");
    				done.setHeaderText("Account Deactivated");
    				done.setContentText("Your account has been successfully deactivated. Login again to activate your account.\n\n\n");
    	    		Optional<ButtonType> ok = done.showAndWait();
    	    		System.out.println("show info");
    	    		if(ok.get() == ButtonType.OK) {
    	    			//go to login page
    	    			try {
    	    				System.out.println("go to login page");
    	    				Parent loader = FXMLLoader.load(getClass().getResource("/fxml/login_page.fxml"));
    						
    						Scene scene = deactivateAccount.getScene();
    						Window window = scene.getWindow();
    						Stage stage = (Stage) window;
    						
    						deactivateAccount.getScene().setRoot(loader);
    	    			}catch(Exception ee) {
    	    				System.out.println("going to login page: " + ee);
    	    			}
    	    		}
    	    		
    			}else {
    				
    				Alert error = new Alert(AlertType.ERROR);
    				error.setTitle("Error");
    				error.setHeaderText("Something Went Wrong");
    				error.setContentText(whatHappened + "\n\n\n");
    				error.show();
    			}
    		}
		}catch(Exception err) {
			System.out.println("Deactivate user account: " + err);
		}
	}
	
	@FXML
	public void initialize() {
		
		
		System.out.println("connectedAs: " + SafeusDriver.connectedAs);
		System.out.println("connectedEmail: " + SafeusDriver.connectedEmail);
		
		category.getItems().addAll(
	            "Pharmacy", "Ration Shop", "Ration Office", "Mail Booth", "Drinking Water", 
	            "Bakery", "Doctor Clinic", "Hospital Department", "Grocery", "Local Store"
	        );
		
		//load details from Server
		loadUserDetails();
		loadEarlierBookingsHistory();
		
		go.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if(searchBox.getText().trim().toLowerCase().equalsIgnoreCase("") != true) {
            		fetchSearchFromSearchBox(searchBox.getText().trim().toLowerCase().replace(" ", "%20"));
            	}
            }});
		
		save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if(userName.getText().trim().toLowerCase().equalsIgnoreCase("") != true && address.getText().trim().toLowerCase().equalsIgnoreCase("") != true && city.getText().trim().toLowerCase().equalsIgnoreCase("") != true && pinCode.getText().trim().toLowerCase().equalsIgnoreCase("") != true && phone.getText().trim().toLowerCase().equalsIgnoreCase("") != true && email.getText().trim().toLowerCase().equalsIgnoreCase("") != true) {
            		
            		//confirmation
            		Alert ask = new Alert(AlertType.CONFIRMATION);
            		ask.setTitle("Confirmation");
            		ask.setHeaderText("Update Account?");
            		ask.setContentText("Are you sure? If yes then press OK else press Cancel.\n\n\n");
            		Optional<ButtonType> what = ask.showAndWait();
            		if(what.get() == ButtonType.OK) {
                		updateInfo(userName.getText(), address.getText(), city.getText(), pinCode.getText(), phone.getText(), email.getText());
            		}else {
            			//update saved info
            			loadUserDetails();
            		}
            	}else {
            		Alert empty = new Alert(AlertType.ERROR);
            		empty.setTitle("Error");
            		empty.setHeaderText("Cannot Update!");
            		empty.setContentText("Some field(s) is/are found empty. "
            				+ "Please enter all the fields and try again.\n\n\n");
            		empty.show();
            	}
            }});
		
		shopsInMyCity.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	fetchSearchFromShopsInMyCity(city.getText().toLowerCase().trim().replace(" ", "%20"));
            }});
		
		shopsNearMe.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	fetchShopsNearMe();
            }});
		
		category.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	fetchShopsAccordingCategory(category.getSelectionModel().getSelectedItem().toString().toLowerCase());
            }});
		
		deactivateAccount.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	System.out.println("deactivate account");
            	deactivateUserAccount(email.getText().trim().toLowerCase());
            }});
		
		viewCurrentBookings.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
					try {
	    				System.out.println("go to upcoming events page");
	    				Parent loader = FXMLLoader.load(getClass().getResource("/fxml/upcoming_events_page.fxml"));
						
						Scene scene = viewCurrentBookings.getScene();
						Window window = scene.getWindow();
						Stage stage = (Stage) window;
						
						viewCurrentBookings.getScene().setRoot(loader);
	    			}catch(Exception ee) {
	    				System.out.println("going to upcoming events page: " + ee);
	    			}
            	}
            });
		
		logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
	            // go to login page
            	Alert ask = new Alert(AlertType.CONFIRMATION);
            	ask.setTitle("Confirmation");
            	ask.setHeaderText("Do you want to logout?");
            	ask.setContentText("Are you sure? Press OK to continue."
						+ "\n\n\n");
            	Optional<ButtonType> answer = ask.showAndWait();
            	if(answer.get() == ButtonType.OK) {
            		try {
    					Parent loader = FXMLLoader.load(getClass().getResource("/fxml/login_page.fxml"));
    					
    					Scene scene = logout.getScene();
    					Window window = scene.getWindow();
    					Stage stage = (Stage) window;
    					
    					logout.getScene().setRoot(loader);
    					
    				}catch(Exception exp) {
    					System.out.println("Going to LoginPage: " + exp);
    				}
            	}
            }
        });
		
		aboutApp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	aboutApp();
            }});
		
	}
}
