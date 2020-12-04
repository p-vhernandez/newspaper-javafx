/**
 * 
 */
package application.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Observable;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import application.AppScenes;
import application.models.NewsReaderModel;
import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedArticle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;
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

	@FXML
	private void btnLoginClicked(ActionEvent event) {
		try {
			LoginController loginController = new LoginController(this);

			Button eventOrigin = (Button) event.getSource();
			eventOrigin.getScene().setRoot(loginController.getContent());
		} catch (Exception e) {
			e.printStackTrace();
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
			alert.setContentText("An article must be selected in order to edit it.");
			alert.showAndWait();
		}
	}

	@FXML
	private void btnDeleteArticleClicked() {
		System.out.println("DELETE ARTICLE CLICKED");
		// TODO: deletion functionality
	}

	@FXML
	private void btnExitClicked() {
		System.out.println("EXIT CLICKED");
	}

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
		this.newsReaderModel.setDummyData(false); //System is connected so dummy data are not needed
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

	private void disableReadMore() {
		btnReadMore.setDisable(true);
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

}
