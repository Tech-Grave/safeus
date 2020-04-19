package safeus;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class EarlierEventsObject {
	
	AnchorPane booking;
	
	Label shopName;
	Label shopType;
	Label shopId;
	Label incomingTime;
	Label outgoingTime;
	Label date;
	Label status;
	
	Label incomingTimeLabel;
	Label outgoingTimeLabel;
	
	Button viewDetails;
	
	String strShopId;
	String strShopName;
	String strOwnerName;
	String strShopType;
	String strAddress;
	String strCity;
	String strPinCode;
	String strPhone;
	String strEmail;
	String strEventDay;
	String strIncomingTime;
	String strOutGoingTime;
	String strStatus;
	String strNote;
	
	EarlierEventsObject(){
		booking = new AnchorPane();
		
		booking.setPrefSize(279.0, 108.0 + 5);
		booking.setMaxSize(279.0, 108.0 + 5);
		booking.setMinSize(279.0, 108.0 + 5);
		
		shopName = new Label();
		shopType = new Label();
		shopId = new Label();
		incomingTime = new Label();
		outgoingTime = new Label();
		date = new Label();
		status = new Label();
		
		incomingTimeLabel = new Label("Incoming Time:");
		outgoingTimeLabel = new Label("Outgoing Time:");
		
		viewDetails = new Button("View Details");
		viewDetails.setPrefWidth(100.0);
		
		//shopName
		AnchorPane.setTopAnchor(shopName, 2.0);
		AnchorPane.setLeftAnchor(shopName, 5.0);
		//shopId
		AnchorPane.setTopAnchor(shopId, 2.0);
		AnchorPane.setRightAnchor(shopId, 10.0);
		//shopType
		AnchorPane.setTopAnchor(shopType, 20.0);
		AnchorPane.setLeftAnchor(shopType, 5.0);
		//incomingTime
		AnchorPane.setTopAnchor(incomingTime, 40.0);
		AnchorPane.setLeftAnchor(incomingTime, 100.0);
		//outgoingTime
		AnchorPane.setTopAnchor(outgoingTime, 57.0);
		AnchorPane.setLeftAnchor(outgoingTime, 100.0);
		//date
		AnchorPane.setLeftAnchor(date, 5.0);
		AnchorPane.setBottomAnchor(date, 17.0);
		//status
		AnchorPane.setLeftAnchor(status, 5.0);
		AnchorPane.setBottomAnchor(status, 0.0);
		//viewDetails
		AnchorPane.setRightAnchor(viewDetails, 10.0);
		AnchorPane.setBottomAnchor(viewDetails, 0.0);
		//incomingTimeLabel
		AnchorPane.setTopAnchor(incomingTimeLabel, 40.0);
		AnchorPane.setLeftAnchor(incomingTimeLabel, 5.0);
		//outgoingTimeLabel
		AnchorPane.setTopAnchor(outgoingTimeLabel, 57.0);
		AnchorPane.setLeftAnchor(outgoingTimeLabel, 5.0);
		
		// css
		viewDetails.setStyle("-fx-text-fill: '#ffffff';" + "-fx-background-color: '#0000cc';");
		shopName.setStyle("-fx-text-fill: '#000066';" + "-fx-font-size: 13px;" + "-fx-font-weight: bold;");
		shopType.setStyle("-fx-font-family: 'Tahoma';" + "-fx-text-fill: '#737373';" + "-fx-font-size: 10px;");
		shopId.setStyle("-fx-font-size: 10.5px;" + "-fx-text-fill: '#000099';");
		incomingTimeLabel.setStyle("-fx-text-fill: '#3973ac';" + "-fx-font-family: 'Arial Rounded MT Bold';" + "-fx-font-size: 12px;");
		outgoingTimeLabel.setStyle("-fx-text-fill: '#3973ac';" + "-fx-font-family: 'Arial Rounded MT Bold';" + "-fx-font-size: 12px;");
		incomingTime.setStyle("-fx-font-family: 'Rockwell';" + "-fx-text-fill: '#008000';" + "-fx-font-size: 12.5px;" + "-fx-font-weight: bold;");
		outgoingTime.setStyle("-fx-font-family: 'Rockwell';" + "-fx-text-fill: '#008000';" + "-fx-font-size: 12.5px;" + "-fx-font-weight: bold;");
		date.setStyle("-fx-font-family: 'Arial Rounded MT Bold';" + "-fx-text-fill: '#e63900';" + "-fx-font-size: 12px;");
		status.setStyle("-fx-text-fill: '#996600';" + "-fx-font-size: 11px;");
		
		booking.setStyle("-fx-border-style: solid none none none; -fx-border-width: 0.5; -fx-border-color: '#a6a6a6';");
		
		booking.getChildren().add(shopName);
		booking.getChildren().add(shopId);
		booking.getChildren().add(shopType);
		booking.getChildren().add(incomingTime);
		booking.getChildren().add(outgoingTime);
		booking.getChildren().add(incomingTimeLabel);
		booking.getChildren().add(outgoingTimeLabel);
		booking.getChildren().add(date);
		booking.getChildren().add(status);
		booking.getChildren().add(viewDetails);
		
		viewDetails.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	try {
            		Alert wrongCredentials = new Alert(AlertType.INFORMATION);
        			wrongCredentials.setTitle("Details");
        			wrongCredentials.setHeaderText(strShopName);
        			wrongCredentials.setContentText(
        							"Shop ID: " + strShopId+ "\n\n" + 
        							"Incoming Time: " + strIncomingTime + "\n" + 
        							"Outgoing Time: " +strOutGoingTime + "\n" + 
        							"Date: " + strEventDay + "\n\n" + 
        							"Note: " + strNote + "\n\n" + 
        							"Owner: " + strOwnerName+ "\n" + 
        							"Address: " + strAddress+ "\n" + 
        							"City: " + strCity + "\n" + 
        							"PIN Code: " + strPinCode + "\n\n" + 
        							"Phone: " + strPhone+ "\n" + 
        							"Email: " + strEmail+ "\n\n" + 
        							"Status: " + strStatus + "\n");
        			wrongCredentials.show();
            	}catch(Exception err) {
            		System.out.println("Going to details page: " + err);
            	}
            }});
	}
	
}
