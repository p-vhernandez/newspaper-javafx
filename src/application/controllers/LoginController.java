package application.controllers;

import application.news.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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

	@FXML
	TextField usernameField;

	@FXML
	TextField passwordField;

	public void setContent(Pane root) {
		this.root = root;
	}

	public Pane getContent() {
		return root;
	}

	public void setNewsReaderController(NewsReaderController newsReaderController) {
		this.newsReaderController = newsReaderController;
	}
	
	public User getLoggedUsr() {
		return loggedUsr;
		
	}
		
	public void setConnectionManager (ConnectionManager connection) {
		this.loginModel.setConnectionManager(connection);
	}

	@FXML
	private void btnLoginClicked(ActionEvent event) {
		String user = usernameField.getText();
		String pass = passwordField.getText();

		User loggedUser = loginModel.validateUser(user, pass);
		if (loggedUser != null) {
			newsReaderController.setUsr(loggedUser);

			Button eventOrigin = (Button) event.getSource();
			eventOrigin.getScene().setRoot(newsReaderController.getContent());
		} else {
			// TODO: show error
		}
	}

	@FXML
	private void btnBackClicked(ActionEvent event) {
		Button eventOrigin = (Button) event.getSource();
		eventOrigin.getScene().setRoot(newsReaderController.getContent());
	}
	
}