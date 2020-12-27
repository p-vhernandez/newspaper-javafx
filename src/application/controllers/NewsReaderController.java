/**
 * 
 */
package application.controllers;

import java.io.File;
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
import application.utils.exceptions.ErrorMalFormedArticle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

	private static final String INFO_FILE_EXTENSION = "*.news";
	private static final String INFO_ACCEPTED_FILES = "NEWS files (*.news)";
	private static final String ALERT_ERROR_GENERIC = "There has been an error";
	private static final String ALERT_ERROR_NULL_FILE_PATH = "An error ocurred while trying to access the file path: it is null.";
	private static final String ALERT_ERROR_GO_TO_NEW_ARTICLE = "An error ocurred while trying to open the article creation screen, please try again later.";
	private static final String ALERT_ERROR_EMPTY_JSON_DATA = "The article data could not be retrieved from the file path chosen.";
	private static final String ALERT_ERROR_LOAD_ARTICLE_DATA = "An error ocurred while trying to load the article's data, please try again later.";
	private static final String ALERT_EDIT_TITLE = "Want to edit an article?";
	private static final String ALERT_EDIT_CONTENT_TEXT = "An article from the list must be selected in order to edit it.";
	private static final String ALERT_DELETE_TITLE = "Want to delete an article?";
	private static final String ALERT_DELETE_CONTENT_TEXT = "An article from the list must be selected in order to delete it.";
	private static final String ALERT_DELETE_CONFIRMATION = "Are you sure you want to delete the selected article?";

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
	private Article selectedArticle;

	private Main main;
	private NewsReaderModel newsReaderModel = new NewsReaderModel();

	private Pane root;

	public void setContent(Pane root) {
		this.root = root;
	}

	public Pane getContent() {
		return root;
	}

	/**
	 * This method is called after the screen (FXML file) has been loaded.
	 */
	@FXML
	void initialize() {
		disableReadMore();
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
		disableReadMore();
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
			Button eventOrigin = (Button) event.getSource();
			Scene parentScene = eventOrigin.getScene();

			FXMLLoader loader = new FXMLLoader (getClass().getResource(AppScenes.NEWS_DETAILS.getFxmlFile()));
			Pane articleRoot = loader.load();
			parentScene.setRoot(articleRoot);

			ArticleDetailsController detailsController = loader.<ArticleDetailsController>getController(); 
			detailsController.setContent(articleRoot);
			detailsController.setNewsReaderController(this);
			detailsController.setUsr(usr);
			detailsController.setArticle(getFullArticle());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Download full article's data since the actual
	 * data only has the thumbnail_image instead of the image
	 * 
	 * @return selected article obj
	 */
	private Article getFullArticle() {
		try {
			JsonObject jsonFullArticle = newsReaderModel.getConnectionManager().downloadFullArticle(selectedArticle.getIdArticle());
			return JsonArticle.jsonToArticle(jsonFullArticle);
		} catch (ErrorMalFormedArticle e) {
			e.printStackTrace();
			return null;
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
			Button eventOrigin = (Button) event.getSource();
			Scene parentScene = eventOrigin.getScene();

			FXMLLoader loader = new FXMLLoader (getClass().getResource(AppScenes.LOGIN.getFxmlFile()));
			Pane loginRoot = loader.load();
			parentScene.setRoot(loginRoot);

			LoginController loginController = loader.<LoginController>getController();
			loginController.setNewsReaderController(this);
			loginController.setConnectionManager(newsReaderModel.getConnectionManager());

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
		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(INFO_ACCEPTED_FILES, INFO_FILE_EXTENSION);
		fileChooser.getExtensionFilters().add(extensionFilter);

		Stage stage = (Stage) root.getScene().getWindow();
		File file = fileChooser.showOpenDialog(stage);

		JsonObject jsonArticle = null;
		if (file != null) {
			jsonArticle = JsonArticle.readFile(file.getPath());
		} else {
			showInformativeAlert(ALERT_ERROR_GENERIC, ALERT_ERROR_NULL_FILE_PATH);
		}

		if (jsonArticle != null) {
			Article articleToLoad;
			try {
				articleToLoad = JsonArticle.jsonToArticle(jsonArticle);
				removeMenuChild();

				MenuItem eventOrigin = (MenuItem) event.getSource();
				ContextMenu contextMenu = eventOrigin.getParentPopup();
				Scene parentScene = contextMenu.getOwnerWindow().getScene();

				FXMLLoader loader = new FXMLLoader (getClass().getResource(AppScenes.EDITOR.getFxmlFile()));
				Pane editorRoot = loader.load();
				parentScene.setRoot(editorRoot);

				ArticleEditController editController = loader.<ArticleEditController>getController();
				editController.setContent(editorRoot);
				editController.setNewsReaderController(this);
				editController.setUsr(usr);
				editController.setArticle(articleToLoad);
				editController.setConnectionMannager(newsReaderModel.getConnectionManager());
			} catch (Exception e) {
				e.printStackTrace();
				showInformativeAlert(ALERT_ERROR_GENERIC, ALERT_ERROR_LOAD_ARTICLE_DATA);
			}
		} else {
			showInformativeAlert(ALERT_ERROR_GENERIC, ALERT_ERROR_EMPTY_JSON_DATA);
		}
	}

	@FXML
	private void btnNewArticleClicked(ActionEvent event) {
		try {
			removeMenuChild();

			MenuItem eventOrigin = (MenuItem) event.getSource();
			ContextMenu contextMenu = eventOrigin.getParentPopup();
			Scene parentScene = contextMenu.getOwnerWindow().getScene();

			FXMLLoader loader = new FXMLLoader (getClass().getResource(AppScenes.EDITOR.getFxmlFile()));
			Pane editorRoot = loader.load();
			parentScene.setRoot(editorRoot);

			ArticleEditController editController = loader.<ArticleEditController>getController();
			editController.setContent(editorRoot);
			editController.setNewsReaderController(this);
			editController.setUsr(usr);
			editController.setArticle(null);
			editController.setConnectionMannager(newsReaderModel.getConnectionManager());
		} catch (Exception e) {
			e.printStackTrace();
			showInformativeAlert(ALERT_ERROR_GENERIC, ALERT_ERROR_GO_TO_NEW_ARTICLE);
		}
	}

	@FXML
	private void btnEditArticleClicked(ActionEvent event) {
		if (selectedArticle != null) {
			try {
				removeMenuChild();

				MenuItem eventOrigin = (MenuItem) event.getSource();
				ContextMenu contextMenu = eventOrigin.getParentPopup();
				Scene parentScene = contextMenu.getOwnerWindow().getScene();

				FXMLLoader loader = new FXMLLoader (getClass().getResource(AppScenes.EDITOR.getFxmlFile()));
				Pane editorRoot = loader.load();
				parentScene.setRoot(editorRoot);

				ArticleEditController editController = loader.<ArticleEditController>getController();
				editController.setContent(editorRoot);
				editController.setNewsReaderController(this);
				editController.setUsr(usr);
				editController.setArticle(getFullArticle());
				editController.setConnectionMannager(newsReaderModel.getConnectionManager());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			showInformativeAlert(ALERT_EDIT_TITLE, ALERT_EDIT_CONTENT_TEXT);
		}
	}

	@FXML
	private void btnDeleteArticleClicked() {
		if (selectedArticle != null) {
			try {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle(ALERT_DELETE_TITLE);
				alert.setHeaderText(null);
				alert.setContentText(ALERT_DELETE_CONFIRMATION);
				
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
			showInformativeAlert(ALERT_DELETE_TITLE, ALERT_DELETE_CONTENT_TEXT);
		}
	}

	private void showInformativeAlert(String title, String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

	/**
	 * Function that allows the user to log out
	 * Sets the user to null and retrieves all articles
	 * @param event
	 */
	@FXML
	private void btnLogoutClicked(ActionEvent event) {
		clearArticleSelection();
		setUsr(null);
		getData();
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
	
	private void disableReadMore() {
		btnReadMore.setDisable(true);
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

	private void removeMenuChild() {
		btnMenu.hide();
	}

	public void addMenuChild() {
		btnMenu.show();
	}

}
