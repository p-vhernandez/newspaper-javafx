package application.controllers;

import application.news.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

import application.AppScenes;
import application.models.LoginModel;

import serverConection.ConnectionManager;

public class LoginController {
	
	private LoginModel loginModel = new LoginModel();
	private User loggedUsr = null;

	private Pane root;

	private NewsReaderController newsReaderController;

	public LoginController(NewsReaderController newsReaderController) {
		this.newsReaderController = newsReaderController;
		loginModel.setDummyData(true);

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.NEWS_DETAILS.getFxmlFile()));
			loader.setController(this);
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Pane getContent() {
		return root;
	}
	
	User getLoggedUsr() {
		return loggedUsr;
		
	}
		
	void setConnectionManager (ConnectionManager connection) {
		this.loginModel.setConnectionManager(connection);
	}
	
}