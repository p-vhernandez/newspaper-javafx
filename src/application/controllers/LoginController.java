package application.controllers;

import application.news.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;

import application.models.LoginModel;

import serverConection.ConnectionManager;

public class LoginController {

	private static final String ALERT_ERROR_GENERIC = "There has been an error";
	private static final String ALERT_ERROR_USER_VALIDATION = "The user credentials could not be validated.";

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
			newsReaderController.clearArticleSelection();

			Button eventOrigin = (Button) event.getSource();
			eventOrigin.getScene().setRoot(newsReaderController.getContent());
		} else {
			showInformativeAlert(ALERT_ERROR_GENERIC, ALERT_ERROR_USER_VALIDATION);
		}
	}

	@FXML
	private void btnBackClicked(ActionEvent event) {
		Button eventOrigin = (Button) event.getSource();
		newsReaderController.clearArticleSelection();
		eventOrigin.getScene().setRoot(newsReaderController.getContent());
	}

	private void showInformativeAlert(String title, String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(contentText);
		alert.showAndWait();
	}
	
}