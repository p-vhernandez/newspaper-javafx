package application.controllers;

import application.news.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.awt.event.MouseEvent;
import java.io.IOException;

import application.AppScenes;
import application.models.LoginModel;

import serverConection.ConnectionManager;

public class LoginController {
	
	private LoginModel loginModel = new LoginModel();
	private User loggedUsr = null;

	private Pane root;

	private NewsReaderController newsReaderController;

	@FXML
	TextField usernameField;

	@FXML
	TextField passwordField;

	public LoginController(NewsReaderController newsReaderController) {
		this.newsReaderController = newsReaderController;
		loginModel.setDummyData(false);

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.LOGIN.getFxmlFile()));
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

	@FXML
	private void btnLoginClicked(ActionEvent event) {
		String user = usernameField.getText();
		String pass = passwordField.getText();
		// newsReaderController.setUsr(loginModel.validateUser(user,pass));
		Button eventOrigin = (Button) event.getSource();
		eventOrigin.getScene().setRoot(newsReaderController.getContent());
	}

	@FXML
	private void btnBackClicked(ActionEvent event) {
		Button eventOrigin = (Button) event.getSource();
		eventOrigin.getScene().setRoot(newsReaderController.getContent());
	}
	
}