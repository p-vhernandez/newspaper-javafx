/**
 * 
 */
package application.controllers;

import java.io.IOException;
import java.util.function.Predicate;

import application.AppScenes;
import application.Main;
import application.models.NewsReaderModel;
import application.news.Article;
import application.news.Categories;
import application.news.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import serverConection.ConnectionManager;

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
	 * This method is called after the 
	 * screen (FXML file) has been loaded.
	 */
	@FXML
	void initialize() {
		getData();
		setListeners();
		checkMenuItems();
	}

	private void getData() {
		newsReaderModel.retrieveData();
		
		listArticles.setItems(newsReaderModel.getArticles());
		selectorCategory.setItems(newsReaderModel.getCategories());
	}

	/**
	 * Set the listeners to show changes
	 * in the screen when an element is clicked
	 */ 
	private void setListeners() {
		listArticles.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Article>() {
			@Override
			public void changed(ObservableValue<? extends Article> observable, Article oldValue, Article newValue) {
				selectedArticle = newValue;
				showArticleData();
				enableReadMore();
			}
		});

		selectorCategory.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Categories>() {

			@Override
			public void changed(ObservableValue<? extends Categories> observable, Categories oldValue, Categories newValue) {
				ObservableList<Article> allArticles = newsReaderModel.getArticles();

				if (newValue.equals(Categories.ALL)) {
					listArticles.setItems(allArticles);
				} else {
					Predicate<Article> byCategory = article -> article.getCategory().equals(newValue.toString());
					listArticles.setItems(allArticles.filtered(byCategory));
				}
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
			detailsController.setArticle(selectedArticle);

			Button eventOrigin = (Button) event.getSource();
			eventOrigin.getScene().setRoot(detailsController.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method: Login
	 * Can be done automatically (just one user)
	 * or displaying a new for to allow more than one
	 * user to enter with their credentials.
	 * 
	 * @param event
	 */
	@FXML
	private void btnLoginClicked(ActionEvent event) {
		try {
			LoginController loginController = new LoginController(this);

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
	private void btnLoadArticleClicked() {
		System.out.println("LOAD ARTICLE CLICKED");
	}

	@FXML
	private void btnNewArticleClicked() {
		System.out.println("NEW ARTICLE CLICKED");
	}

	@FXML
	private void btnEditArticleClicked(ActionEvent event) {
		if (selectedArticle != null) {
			try {
				ArticleEditController editController = new ArticleEditController(this);
				editController.setArticle(selectedArticle);

				Button eventOrigin = (Button) event.getSource();
				eventOrigin.getScene().setRoot(editController.getContent());
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
			// TODO: deletion functionality
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Want to delete an article?");
			alert.setHeaderText(null);
			alert.setContentText("An article from the list must be selected in order to delete it.");
			alert.showAndWait();
		}
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
			}

			WebEngine engine = articleAbstract.getEngine();
			engine.loadContent(selectedArticle.getAbstractText());
		}
	}

	public void setConnectionManager (ConnectionManager connection){
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
			btnLogin.setVisible(false);
		} else {
			btnLoadArticle.setDisable(false);
			btnNewArticle.setDisable(false);
			btnEditArticle.setDisable(true);
			btnDeleteArticle.setDisable(true);
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

		// Reload articles
		this.getData();
		checkMenuItems();
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
