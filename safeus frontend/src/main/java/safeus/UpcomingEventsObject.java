package safeus;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class UpcomingEventsObject {
	
	AnchorPane booking;
	Label shopName;
	Label shopType;
	Label shopId;
	Label incomingTime;
	Label outgoingTime;
	Label date;
	Label status;
	Button viewDetails;
	
	String strOwnerName;
	String strAddress;
	String strCity;
	String strPinCode;
	String strPhone;
	String strEmail;
	String strNote;
	
	Label shopIdLabel;
	Label incomingTimeLabel;
	Label outgoingTimeLabel;
	Label statusLabel;
	
	UpcomingEventsObject() {
		
		booking = new AnchorPane();
		
		
		booking.setPrefSize(798.0, 165.0);
		booking.setMinSize(798.0, 165.0);
		booking.setMaxSize(798.0, 165.0);
		
		shopName = new Label();
		shopType = new Label();
		shopId = new Label();
		incomingTime = new Label();
		outgoingTime = new Label();
		date = new Label();
		status = new Label();
		
		viewDetails = new Button("View Details");
		viewDetails.setPrefSize(115.0, 4.0);
		
		shopIdLabel = new Label("Shop ID:");
		incomingTimeLabel = new Label("Incoming Time:");
		outgoingTimeLabel = new Label("Outgoing Time:");
		statusLabel = new Label("Status:");
		
		booking.getChildren().addAll(shopName, shopType, shopId, incomingTime, outgoingTime, date, 
				status, viewDetails, shopIdLabel, incomingTimeLabel, outgoingTimeLabel, statusLabel);
	
		viewDetails.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	try {
            		UpcomingEventsPageController.ownerName.setText(strOwnerName);
            		UpcomingEventsPageController.address.setText(strAddress);
            		UpcomingEventsPageController.city.setText(strCity);
            		UpcomingEventsPageController.pin.setText(strPinCode);
            		UpcomingEventsPageController.phone.setText(strPhone);
            		UpcomingEventsPageController.email.setText(strEmail);
            		UpcomingEventsPageController.note.setText(strNote);
            	}catch(Exception e) {
            		System.out.println("Loading details: " + e);
            	}
            	
            }});
		
		//shopName
		AnchorPane.setTopAnchor(shopName, 13.0);
		AnchorPane.setLeftAnchor(shopName, 22.0);
		//shopType
		AnchorPane.setTopAnchor(shopType, 38.0);
		AnchorPane.setLeftAnchor(shopType, 22.0);
		//shopId
		AnchorPane.setTopAnchor(shopId, 7.0);
		AnchorPane.setLeftAnchor(shopId, 720.0);
		//incomingTime
		AnchorPane.setTopAnchor(incomingTime, 71.0);
		AnchorPane.setLeftAnchor(incomingTime, 130.0);
		//outgoingTime
		AnchorPane.setTopAnchor(outgoingTime, 71.0);
		AnchorPane.setLeftAnchor(outgoingTime, 430.0);
		//date
		AnchorPane.setTopAnchor(date, 101.0);
		AnchorPane.setLeftAnchor(date, 22.0);
		//status
		AnchorPane.setTopAnchor(status, 144.0);
		AnchorPane.setLeftAnchor(status, 73.0);
		
		//shopIdLabel
		AnchorPane.setTopAnchor(shopIdLabel, 7.0);
		AnchorPane.setLeftAnchor(shopIdLabel, 662.0);
		//incomingTimeLabel
		AnchorPane.setTopAnchor(incomingTimeLabel, 72.0);
		AnchorPane.setLeftAnchor(incomingTimeLabel, 22.0);
		//outgoingTimeLabel
		AnchorPane.setTopAnchor(outgoingTimeLabel, 72.0);
		AnchorPane.setLeftAnchor(outgoingTimeLabel, 321.0);
		//statusLabel
		AnchorPane.setTopAnchor(statusLabel, 144.0);
		AnchorPane.setLeftAnchor(statusLabel, 22.0);
		
		//viewDetails
		AnchorPane.setTopAnchor(viewDetails, 136.0);
		AnchorPane.setLeftAnchor(viewDetails, 673.0);
		
		//css
		shopName.setStyle("-fx-text-fill: '#0e005e';" + "-fx-font-size: 22px;" + "" + "-fx-font-family: 'Arial Rounded MT Bold';");
		shopType.setStyle("-fx-text-fill: '#5e5e5e';" + "-fx-font-size: 13px;");
		shopId.setStyle("-fx-text-fill: '#3000a1';" + "-fx-font-size: 14px;");
		incomingTime.setStyle("-fx-text-fill: '#dd0000';" + "-fx-font-size: 14px;" + "-fx-font-weight: bold;");
		outgoingTime.setStyle("-fx-text-fill: '#dd0000';" + "-fx-font-size: 14px;" + "-fx-font-weight: bold;");
		date.setStyle("-fx-text-fill: '#c68b00';" + "-fx-font-size: 17px;" + "" + "-fx-font-family: 'Rockwell';");
		status.setStyle("-fx-text-fill: '#bf000d';" + "-fx-font-size: 16px;" + "-fx-font-family: 'Copperplate Gothic Bold';" + "");
		
		shopIdLabel.setStyle("-fx-text-fill: '#1c00a6';" + "-fx-font-size: 14px;");
		incomingTimeLabel.setStyle("-fx-text-fill: '#007c04';" + "-fx-font-size: 13px;" + "-fx-font-weight: bold;");
		outgoingTimeLabel.setStyle("-fx-text-fill: '#007c04';" + "-fx-font-size: 13px;" + "-fx-font-weight: bold;");
		statusLabel.setStyle("-fx-text-fill: '#000000';" + "-fx-font-size: 14px;" + "-fx-font-family: 'Nirmala UI';");
		
		viewDetails.setStyle("-fx-background-color: '#e6b800';" + "-fx-text-fill: '#ffffff';");
		
		
	}
}
