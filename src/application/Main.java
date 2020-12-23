package application;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import application.controllers.NewsReaderController;
import application.news.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import serverConection.ConnectionManager;
import serverConection.exceptions.AuthenticationError;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Main extends Application {

	private ConnectionManager connection;
	private NewsReaderController newsReaderController;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader (getClass().getResource(AppScenes.READER.getFxmlFile()));
			Pane root = loader.load();
			newsReaderController = loader.<NewsReaderController>getController();
			newsReaderController.setContent(root);
			setConnection();	

			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("css/application.css").toExternalForm());
			primaryStage.setTitle("Newspaper");
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("../images/ic_news.png")));
			primaryStage.initStyle(StageStyle.DECORATED);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setConnection() {
		try {
			// Create properties for server connection
			Properties prop = buildServerProperties();
			connection = new ConnectionManager(prop);

			// Connecting as public (anonymous) for your group
			connection.setAnonymousAPIKey("");
			// defaultLogin();
			newsReaderController.setConnectionManager(connection);	
		} catch(AuthenticationError e) {
			Logger.getGlobal().log(Level.SEVERE, "Error in loging process");
			e.printStackTrace();
		} 
	}

	public void defaultLogin() {
		try {
			// Set the API Key to retrieve the group's articles
			connection.setAnonymousAPIKey("DEV_TEAM_3553");
			// Login without login form:
			connection.login("us_3_3", "3553");
			User user = new User("us_3_3", Integer.parseInt(connection.getIdUser()));
			newsReaderController.setUsr(user);
		} catch (AuthenticationError e) {
			e.printStackTrace();
		} 
	}
	
	final static Properties buildServerProperties() {
		Properties prop = new Properties();
		prop.setProperty(ConnectionManager.ATTR_SERVICE_URL, "https://sanger.dia.fi.upm.es/pui-rest-news/");
		prop.setProperty(ConnectionManager.ATTR_REQUIRE_SELF_CERT, "TRUE");

		return prop;
	} 
	
}
