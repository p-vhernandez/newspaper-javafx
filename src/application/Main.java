package application;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import application.controllers.NewsReaderController;
import application.news.User;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import serverConection.ConnectionManager;
import serverConection.exceptions.AuthenticationError;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class Main extends Application {

	private ConnectionManager connection;
	private NewsReaderController newsReaderController;

	@Override
	public void start(Stage primaryStage) {		
		newsReaderController = new NewsReaderController();
		newsReaderController.setMainController(this);
		Pane root = newsReaderController.getContent();
		setConnection();

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("css/application.css").toExternalForm());
		primaryStage.setTitle("Newspaper");
		primaryStage.initStyle(StageStyle.DECORATED);
		primaryStage.setScene(scene);
		primaryStage.show();
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
		
		/* For http & https proxy 
		prop.setProperty(ConnectionManager.ATTR_PROXY_HOST, "http://proxy.fi.upm.es");
		prop.setProperty(ConnectionManager.ATTR_PROXY_PORT, "80");
		*/
		
		/* For proxy or apache password auth 
		prop.setProperty(ConnectionManager.ATTR_PROXY_USER, "...");
		prop.setProperty(ConnectionManager.ATTR_PROXY_PASS, "...");
		*/

		return prop;
	}
	
}
