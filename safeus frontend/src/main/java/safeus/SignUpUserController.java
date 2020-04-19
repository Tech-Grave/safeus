package safeus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SignUpUserController {
	
	@FXML
	Button back;
	
	@FXML
	TextField name;
	
	@FXML
	TextField address;
	
	@FXML
	TextField city;
	
	@FXML
	TextField pinCode;
	
	@FXML
	TextField phone;
	
	@FXML
	PasswordField password;
	
	@FXML
	PasswordField confirmPassword;
	
	@FXML
	Button submit;
	
	@FXML
	Button verification;
	
	@FXML
	TextField code;
	
	@FXML
	Button confirm;
	
	@FXML
	TextField email;
	
	@FXML
	public void initialize() {
		verification.setDisable(true);
		confirm.setDisable(true);
		
		submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	try {
            		if(name.getText().trim().equalsIgnoreCase("") != true && 
            				address.getText().trim().equalsIgnoreCase("") != true &&
            				city.getText().trim().equalsIgnoreCase("") != true &&
            				pinCode.getText().trim().equalsIgnoreCase("") != true &&
            				address.getText().trim().equalsIgnoreCase("") != true &&
            				phone.getText().trim().equalsIgnoreCase("") != true &&
            				password.getText().trim().equalsIgnoreCase("") != true &&
            				confirmPassword.getText().trim().equalsIgnoreCase("") != true) {
            			
            			try {
                    		//connecting
            				URL url = new URL("http://127.0.0.1:5000" + 
            						"/signup/user/" + name.getText().replace(" ", "%20") + "/" + address.getText().replace(" ", "%20") + "/" 
            						+ city.getText().replace(" ", "%20") + "/" + pinCode.getText().replace(" ", "%20") +
            						"/" + phone.getText().replace(" ", "%20") + "/" + email.getText().replace(" ", "%20") + "/" + 
            						password.getText().replace(" ", "%20") + "/" + confirmPassword.getText().replace(" ", "%20"));
            				
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
            					
            					verification.setDisable(false);
            					System.out.println("Account Created.");
            					
            					verification.setOnAction(new EventHandler<ActionEvent>() {
            			            @Override
            			            public void handle(ActionEvent event) {
            			            	confirm.setDisable(false);
            			            	
            			            	try {
            			            		//connect
            			            		URL url = new URL("http://127.0.0.1:5000" + 
            	            						"/sendOTP/" + phone.getText().replace(" ", "%20") + "/" + email.getText().replace(" ", "%20"));
            	            				
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
            			            		
            	            				if(jsonObject.get("message").toString().toLowerCase().equalsIgnoreCase("True")) {
            	            					System.out.println(jsonObject.get("message").toString());
            	            					//check OTP
            	            					confirm.setOnAction(new EventHandler<ActionEvent>() {
            	            			            @Override
            	            			            public void handle(ActionEvent event) {
            	            			            	
            	            			            	URL url;
														try {
															url = new URL("http://127.0.0.1:5000" + 
																	"/verifyotp/user/" + email.getText().replace(" ", "%20")
																	+ "/" + code.getText().trim());
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
	                        	            					//verified
	                        	            					Alert correct = new Alert(AlertType.INFORMATION);
	                        	            					correct.setTitle("Successful");
	                        	            					correct.setHeaderText("Account Verified");
	                        	            					correct.setContentText("Your account has been verified. Enter your password to login. Press OK to continue."
	                        	            							+ "\n\n\n");
	                        	            					Optional<ButtonType> verified = correct.showAndWait();
	                        	            					if(verified.get() == ButtonType.OK) {
	                        	            						
	                        	            						//go to login page
	                        	            						try {
	                        	            		    				System.out.println("go to login page");
	                        	            		    				Parent loader = FXMLLoader.load(getClass().getResource("/fxml/login_page.fxml"));
	                        	            							
	                        	            							Scene scene = back.getScene();
	                        	            							Window window = scene.getWindow();
	                        	            							Stage stage = (Stage) window;
	                        	            							
	                        	            							back.getScene().setRoot(loader);
	                        	            		    			}catch(Exception ee) {
	                        	            		    				System.out.println("going to login page: " + ee);
	                        	            		    			}
	                        	            						
	                        	            					}
	                        	            				}else if(jsonObject.get("message").toString().equalsIgnoreCase("False")){
	                        	            					//incorrect otp
	                        	            					Alert incorrect = new Alert(AlertType.ERROR);
	                        	            					incorrect.setTitle("Failed");
	                        	            					incorrect.setHeaderText("Incorect Verification Code");
	                        	            					incorrect.setContentText("Entered verification code is incorrect. Please check your email and try again."
	                        	            							+ "\n\n\n");
	                        	            					incorrect.show();
	                        	            				}
														} catch (Exception exx) {
															System.out.println("exx: " + exx);
														}
                        	            				
                        	            				
            	            			            	
            	            			            }});
            	            				}
            	            				
            			            	}catch(Exception ex) {
            			            		System.out.println("SendingOTP: " + ex);
            			            	}
            			            	
            			            	
            			            }});
            					
            				}else {
            					Alert wrongCredentials = new Alert(AlertType.ERROR);
            					wrongCredentials.setTitle("Message");
            					wrongCredentials.setHeaderText("Signup Failed");
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
            			
            		}else {
            					System.out.println("Field(s) found empty.");
            				}
            	}catch(Exception e) {
            		System.out.println("Submit:" + e);
            	}
            }});
		
		//back
		try {
		FileInputStream inputBackImage = new FileInputStream("images/back.png");
		Image backImage = new Image(inputBackImage);
		back.setGraphic(new ImageView(backImage));
		}catch(Exception e) {}
		
		back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
					try {
	    				System.out.println("go to login page");
	    				Parent loader = FXMLLoader.load(getClass().getResource("/fxml/login_page.fxml"));
						
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
