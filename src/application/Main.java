package application;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.controllers.NewsReaderController;

//import org.omg.CORBA.portable.InputStream;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import serverConection.ConnectionManager;
import serverConection.exceptions.AuthenticationError;
import serverConection.exceptions.ServerCommunicationError;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {

	private ConnectionManager connection;
	private NewsReaderController newsReaderController;

	@Override
	public void start(Stage primaryStage) {		
		newsReaderController = new NewsReaderController();
		Pane root = newsReaderController.getContent();
		// setConnection();

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
			connection.setAnonymousAPIKey("DEV_TEAM_3553");
			defaultLogin();
			newsReaderController.setConnectionManager(connection);	
		} catch(AuthenticationError e) {
			Logger.getGlobal().log(Level.SEVERE, "Error in loging process");
			e.printStackTrace();
		} 
	}

	public void defaultLogin() {
		try {
			// Login without login form:
			connection.login("Reader2", "reader2");
			User user = new User("Reader2", Integer.parseInt(connection.getIdUser()));
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
