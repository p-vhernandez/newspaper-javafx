package application.controllers;

import application.news.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

	public LoginController(NewsReaderController newsReaderController) {
		this.newsReaderController = newsReaderController;
		loginModel.setDummyData(true);

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.LOGIN.getFxmlFile()));
			loader.setController(this);
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void onLoginCLicked(MouseEvent event) {

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
	private void btnLoginClicked() {System.out.println("Hola muy buenas. LoginClicked");}

	@FXML
	private void btnBackClicked() {System.out.println("Hola muy buenas. BackClicked");}
	
}