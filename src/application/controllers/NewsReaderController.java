/**
 * 
 */
package application.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;

import javax.json.JsonObject;

import application.AppScenes;
import application.Main;
import application.models.NewsReaderModel;
import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import serverConection.ConnectionManager;
import serverConection.exceptions.ServerCommunicationError;

/**
 * @author ÁngelLucas
 *
 */
public class NewsReaderController {

	@FXML
	private ListView<Article> listArticles;

	@FXML
	private ComboBox<Categories> selectorCategory;

	@FXML
	private ImageView articleImage;

	@FXML
	private WebView articleAbstract;

	@FXML
	private Button btnReadMore;

	@FXML
	private Button btnLogin;

	@FXML
	private MenuButton btnMenu;

	@FXML
	private MenuItem btnLoadArticle;

	@FXML
	private MenuItem btnNewArticle;

	@FXML
	private MenuItem btnEditArticle;

	@FXML
	private MenuItem btnDeleteArticle;

	@FXML
	private MenuItem btnLogout;

	@FXML
	private Label lblUser;

	private User usr;
	private Pane root;
	private Article selectedArticle;

	private Main main;
	private NewsReaderModel newsReaderModel = new NewsReaderModel();

	public NewsReaderController() {
		newsReaderModel.setDummyData(true);

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.READER.getFxmlFile()));
			loader.setController(this);
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Pane getContent() {
		return root;
	}

	/**
	 * This method is called after the screen (FXML file) has been loaded.
	 */
	@FXML
	void initialize() {
		hideWelcomeMessage();
		lblUser.managedProperty().bind(lblUser.visibleProperty());

		getData();
		setListeners();
		checkMenuItems();
	}

	void getData() {
		newsReaderModel.retrieveData();

		listArticles.setItems(newsReaderModel.getArticles());
		selectorCategory.setItems(newsReaderModel.getCategories());
		selectorCategory.getSelectionModel().select(Categories.ALL);
	}

	public void clearArticleSelection() {
		listArticles.getSelectionModel().clearSelection();

		articleImage.setImage(null);
		WebEngine engine = articleAbstract.getEngine();
		engine.loadContent("");
	}

	/**
	 * Set the listeners to show changes in the screen when an element is clicked
	 */
	private void setListeners() {
		listArticles.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			selectedArticle = newValue;
			showArticleData();
			enableReadMore();
		});

		selectorCategory.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			ObservableList<Article> allArticles = newsReaderModel.getArticles();

			if (newValue.equals(Categories.ALL)) {
				listArticles.setItems(allArticles);
			} else {
				Predicate<Article> byCategory = article -> article.getCategory().equals(newValue.toString());
				listArticles.setItems(allArticles.filtered(byCategory));
			}
		});
	}

	/**
	 * Display a new screen with the article's details.
	 * 
	 * @param event
	 */
	@FXML
	private void readMore(ActionEvent event) {
		try {
			ArticleDetailsController detailsController = new ArticleDetailsController(this);
			detailsController.setUsr(usr);
			detailsController.setArticle(selectedArticle);

			Button eventOrigin = (Button) event.getSource();
			eventOrigin.getScene().setRoot(detailsController.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method: Login Can be done automatically (just one user) or displaying a new
	 * for to allow more than one user to enter with their credentials.
	 * 
	 * @param event
	 */
	@FXML
	private void btnLoginClicked(ActionEvent event) {
		try {
			LoginController loginController = new LoginController(this);
			loginController.setConnectionManager(newsReaderModel.getConnectionManager());

			Button eventOrigin = (Button) event.getSource();
			eventOrigin.getScene().setRoot(loginController.getContent());

			// autoLogin();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void autoLogin() {
		if (main != null) {
			main.defaultLogin();
		}
	}

	@FXML
	private void btnLoadArticleClicked(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("NEWS files (*.news)", "*.news");
		fileChooser.getExtensionFilters().add(extensionFilter);

		Stage stage = (Stage) root.getScene().getWindow();
		File file = fileChooser.showOpenDialog(stage);

		JsonObject jsonArticle = null;
		if (file != null) {
			jsonArticle = JsonArticle.readFile(file.getPath());
		} else {
			// TODO: show error
		}

		if (jsonArticle != null) {
			Article articleToLoad;
			try {
				articleToLoad = JsonArticle.jsonToArticle(jsonArticle);

				ArticleEditController editController = new ArticleEditController(this);
				editController.setUsr(usr);
				editController.setArticle(articleToLoad);
				editController.setConnectionMannager(newsReaderModel.getConnectionManager());

				MenuItem eventOrigin = (MenuItem) event.getSource();
				ContextMenu contextMenu = eventOrigin.getParentPopup();
				contextMenu.getOwnerWindow().getScene().setRoot(editController.getContent());
			} catch (Exception e) {
				// TODO: show error
				e.printStackTrace();
			}
		} else {
			// TODO: show error
		}
	}

	@FXML
	private void btnNewArticleClicked(ActionEvent event) {
		try {
			ArticleEditController editController = new ArticleEditController(this);
			editController.setUsr(usr);
			editController.setArticle(null);
			editController.setConnectionMannager(newsReaderModel.getConnectionManager());

			MenuItem eventOrigin = (MenuItem) event.getSource();
			ContextMenu contextMenu = eventOrigin.getParentPopup();
			contextMenu.getOwnerWindow().getScene().setRoot(editController.getContent());
		} catch (Exception e) {
			// TODO: show error
			e.printStackTrace();
		}
	}

	@FXML
	private void btnEditArticleClicked(ActionEvent event) {
		if (selectedArticle != null) {
			try {
				ArticleEditController editController = new ArticleEditController(this);
				editController.setArticle(selectedArticle);
				editController.setUsr(usr);
				editController.setConnectionMannager(newsReaderModel.getConnectionManager());

				MenuItem eventOrigin = (MenuItem) event.getSource();
				ContextMenu contextMenu = eventOrigin.getParentPopup();
				contextMenu.getOwnerWindow().getScene().setRoot(editController.getContent());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Want to edit an article?");
			alert.setHeaderText(null);
			alert.setContentText("An article from the list must be selected in order to edit it.");
			alert.showAndWait();
		}
	}

	@FXML
	private void btnDeleteArticleClicked() {
		if (selectedArticle != null) {
			try {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Delete article");
				alert.setHeaderText(null);
				alert.setContentText("Are you sure you want to delete the selected article?");
				
				Optional<ButtonType> result = alert.showAndWait();
				if (result.isPresent() && result.get() == ButtonType.OK) {
					newsReaderModel.getConnectionManager().deleteArticle(selectedArticle.getIdArticle());
					clearArticleSelection();
					getData();
				}
			} catch (ServerCommunicationError e) {
				e.printStackTrace();
			}
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Want to delete an article?");
			alert.setHeaderText(null);
			alert.setContentText("An article from the list must be selected in order to delete it.");
			alert.showAndWait();
		}
	}

	/**
	 * Function that allows the user to log out
	 * Sets the user to null and retrieves all articles
	 * @param event
	 */
	@FXML
	private void btnLogoutClicked(ActionEvent event) {
		setUsr(null);
	}

	/**
	 * Function that closes the applciation.
	 * @param event
	 */
	@FXML
	private void btnExitClicked(ActionEvent event) {
		Platform.exit();
	}

	/**
	 * Show the selected article's data
	 * on the screen. 
	 */
	private void showArticleData() {
		if (selectedArticle != null) {
			if (selectedArticle.getImageData() != null) {
				articleImage.setImage(selectedArticle.getImageData());
			} else {
				articleImage.setImage(new Image(getClass().getResourceAsStream("../../images/ic_news.png")));
			}

			WebEngine engine = articleAbstract.getEngine();
			engine.loadContent(selectedArticle.getAbstractText());
		}
	}

	public void setConnectionManager(ConnectionManager connection){
		this.newsReaderModel.setDummyData(false); // System is connected so dummy data are not needed
		this.newsReaderModel.setConnectionManager(connection);
		this.getData();
	}

	private void checkMenuItems() {
		if (this.usr != null) {
			btnLoadArticle.setDisable(false);
			btnNewArticle.setDisable(false);
			btnEditArticle.setDisable(false);
			btnDeleteArticle.setDisable(false);
			btnLogout.setDisable(false);
			btnLogin.setVisible(false);
		} else {
			btnLoadArticle.setDisable(false);
			btnNewArticle.setDisable(false);
			btnEditArticle.setDisable(true);
			btnDeleteArticle.setDisable(true);
			btnLogout.setDisable(true);
			btnLogin.setVisible(true);
		}
	}

	private void enableReadMore() {
		btnReadMore.setDisable(false);
	}
	
	/**
	 * @param usr the usr to set
	 */
	public void setUsr(User usr) {
		this.usr = usr;

		if (this.usr != null) {
			lblUser.setText("Welcome back, " + this.usr.getLogin() + "!");
			lblUser.setVisible(true);
		} else {
			hideWelcomeMessage();
		}

		getData();
		checkMenuItems();
	}

	private void hideWelcomeMessage() {
		lblUser.setText("");
		lblUser.setVisible(false);
	}

	/**
	 * @return the usr
	 */
	public User getUsr() {
		return usr;
	}

	/**
	 * Sets the main controller in order to
	 * perform the actions needed.
	 * @param main
	 */
	public void setMainController(Main main) {
		this.main = main;
	}

}
