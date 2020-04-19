package safeus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.event.ActionEvent;

public class LoginPageController {
	
	@FXML
	RadioButton asUser;
	
	@FXML
	RadioButton asOwner;
	
	@FXML
	TextField email;
	
	@FXML
	PasswordField password;
	
	@FXML
	Button login;
	
	@FXML
	Button forgotPassword;
	
	@FXML
	Button signUp;
	
	@FXML
	ToggleGroup as = new ToggleGroup();
	
	@FXML
	public void initialize() {
		
		signUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	
            	if(asUser.isSelected()) {
            		try {
	    				System.out.println("go to signup user's page");
	    				Parent loader = FXMLLoader.load(getClass().getResource("/fxml/signup_user.fxml"));
						
						Scene scene = signUp.getScene();
						Window window = scene.getWindow();
						Stage stage = (Stage) window;
						
						signUp.getScene().setRoot(loader);
	    			}catch(Exception ee) {
	    				System.out.println("going to home page: " + ee);
	    			}
            	}else {
            		//pass
            	}
            }
        });
		
		login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	
            	// check for empty fields
            	if(email.getText().trim().equalsIgnoreCase("") || password.getText().equalsIgnoreCase("")) {
            		
            		Alert emptyFields = new Alert(AlertType.WARNING);
            		emptyFields.setTitle("Message");
            		emptyFields.setHeaderText("Field(s) found empty");
            		emptyFields.setContentText("Email ID or password is not entered. Please fill in the"
							+ "required details and try again.\n\n\n");
            		emptyFields.show();
            	}else {
            		try {
                		String selectedAs = "";
                		if(asUser.isSelected()) {
                			selectedAs = "user";
                			SafeusDriver.connectedAs = "user";
                		}else {
                			selectedAs = "owner";
                			SafeusDriver.connectedAs = "owner";
                		}
        				System.out.println("Selected As: " + selectedAs);
        				
        				//connecting
        				URL url = new URL("http://127.0.0.1:5000/login/" + selectedAs + "/" + email.getText().trim() + 
        						"/" + password.getText());
        				
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
        				
        				if(jsonObject.get("message").toString().equalsIgnoreCase("True")) {
        					
        					System.out.println("Logged In");
        					SafeusDriver.connectedEmail = email.getText().toLowerCase().trim();
        					//System.out.println(SafeusDriver.connectedEmail);
        					// go to HomePageUser
        					try {
        						Parent loader = FXMLLoader.load(getClass().getResource("/fxml/user_home_page.fxml"));
        						
        						Scene scene = login.getScene();
        						Window window = scene.getWindow();
        						Stage stage = (Stage) window;
        						
        						login.getScene().setRoot(loader);
        						
        					}catch(Exception exp) {
        						System.out.println("Going to HomePageUser: " + exp);
        					}
        					
        				}else {
        					Alert wrongCredentials = new Alert(AlertType.ERROR);
        					wrongCredentials.setTitle("Message");
        					wrongCredentials.setHeaderText("Login Failed");
        					wrongCredentials.setContentText(jsonObject.get("message").toString()
    								+ "\n\n\n");
        					wrongCredentials.show();
        				}
        			}catch(Exception err) {
        				System.out.println("LoginPageController: " + err);
        				
        				Alert wrongCredentials = new Alert(AlertType.ERROR);
    					wrongCredentials.setTitle("Error");
    					wrongCredentials.setHeaderText("Sorry. Failed!");
    					wrongCredentials.setContentText("Cannot connect to the server. Please try again "
    							+ "after some time.\n\n\n");
    					wrongCredentials.show();
        			}
            	}//else closes
            }
        });
		
	}
}
