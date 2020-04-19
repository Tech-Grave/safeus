package safeus;

import javafx.application.Application;

import java.io.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;

import java.util.regex.Pattern;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.*;
import javafx.scene.effect.*;
import javafx.scene.control.Alert.AlertType;

import java.text.DateFormat;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javafx.geometry.Orientation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.awt.Desktop;
import java.net.URI;


public class SafeusDriver extends Application{

	static final int width = 1134 - 10;
	static final int height = 595 - 10;
	
	static AnchorPane root;
	static Scene scene;
	static Stage stage;
	
	static String connectedAs;
	static String connectedEmail;

	@Override
	public void start(Stage s) throws Exception {
		//root = FXMLLoader.load(getClass().getResource("/fxml/user_home_page.fxml"));
		root = FXMLLoader.load(getClass().getResource("/fxml/login_page.fxml"));
		
		scene = new Scene(root, width, height);
		s.setTitle("Safeus Application");
        s.setResizable(false);
		s.setScene(scene);
        s.show();
	}
	
	public static void main(String[] args) {
		launch(args);

	}

}
