/**
 * 
 */
package application.controllers;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.Predicate;

import application.models.NewsReaderModel;
import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedArticle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

	private NewsReaderModel newsReaderModel = new NewsReaderModel();

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
	private MenuButton btnMenu;

	private User usr;
	private Article selectedArticle;

	public NewsReaderController() {
		// TODO
		// Uncomment next sentence to use data from server instead dummy data
		newsReaderModel.setDummyData(true);
	}

	@FXML
	void initialize() {
		getData();
		setListeners();
		showArticleData();
	}

	private void getData() {
		newsReaderModel.retrieveData();

		selectedArticle = newsReaderModel.getArticles().get(0);
		
		listArticles.setItems(newsReaderModel.getArticles());
		listArticles.getSelectionModel().select(0);

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
				// TODO: show article's details in the page
				selectedArticle = newValue;
				showArticleData();
			}
		});

		selectorCategory.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Categories>() {

			@Override
			public void changed(ObservableValue<? extends Categories> arg0, Categories arg1, Categories arg2) {
				// TODO: filter articles by category
			}
		});

		btnMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// TODO: differenciate actions and add functionalities
			}
		});
	}

	@FXML
	private void readMore() {
		System.out.println("READ MORE CLICKED");
	}

	@FXML
	private void btnMenuClicked() {
		System.out.println("MENU CLICKED");
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
	private void btnEditArticleClicked() {
		System.out.println("EDIT ARTICLE CLICKED");
	}

	@FXML
	private void btnDeleteArticleClicked() {
		System.out.println("DELETE ARTICLE CLICKED");
	}

	@FXML
	private void btnExitClicked() {
		System.out.println("EXIT CLICKED");
	}

	private void showArticleData() {
		if (selectedArticle.getImageData() != null) {
			articleImage.setImage(selectedArticle.getImageData());
		}

		WebEngine engine = articleAbstract.getEngine();
		engine.loadContent(selectedArticle.getAbstractText());
	}

	/**
	 * @return the usr
	 */
	public User getUsr() {
		return usr;
	}

	public void setConnectionManager (ConnectionManager connection){
		this.newsReaderModel.setDummyData(false); //System is connected so dummy data are not needed
		this.newsReaderModel.setConnectionManager(connection);
		this.getData();
	}
	
	/**
	 * @param usr the usr to set
	 */
	public void setUsr(User usr) {
		
		this.usr = usr;
		//Reload articles
		this.getData();
		//TODO Update UI
	}

}
