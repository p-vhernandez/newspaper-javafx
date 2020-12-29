package application.controllers;

import java.io.FileWriter;
import java.io.IOException;

import javax.json.JsonObject;

import com.jfoenix.controls.JFXTextField;

import application.AppScenes;
import application.models.ArticleEditModel;
import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import serverConection.ConnectionManager;
import serverConection.exceptions.ServerCommunicationError;

/**
 * @author ÁngelLucas
 *
 */
public class ArticleEditController {

	@FXML
	private JFXTextField txtTitle;

	@FXML
	private JFXTextField txtSubtitle;

	@FXML
	private ImageView imgArticle;

	@FXML
	private ComboBox<Categories> categorySelector;

	@FXML 
	private HTMLEditor abstractEditor;

	@FXML 
	private HTMLEditor bodyEditor;

	@FXML
	private VBox vBoxAbstract;

	@FXML 
	private VBox vBoxBody;

	@FXML 
	private VBox fullContent;

	@FXML
	private Button btnSend;

	@FXML
	private Button btnChange;

	@FXML
	private Button btnBack;

	@FXML
	private Label lblUser;

    private ConnectionManager connection;
	private ArticleEditModel editingArticle;
	private User usr;

	private NewsReaderController newsReaderController;
	private Pane root;

	private ObservableList<Categories> categories;

	private boolean editAbstract = true;
	private boolean articleCreation = false;

	public void setContent(Pane root) {
		this.root = root;
	}

	public void setNewsReaderController(NewsReaderController newsReaderController) {
		this.newsReaderController = newsReaderController;
	}

	public Pane getContent() {
		return root;
	}

	/**
	 * Differenciate if the user wants to create an article
	 * or edit an already existing article.
	 * 
	 * @param creation - boolean
	 */
	public void setArticleCreation(boolean creation) {
		this.articleCreation = creation;
		checkCreation();
	}

	/**
	 * This method is called after the 
	 * screen (FXML file) has been loaded.
	 */
	@FXML
	void initialize() {		
		categories = FXCollections.observableArrayList();

		vBoxAbstract.managedProperty().bind(vBoxAbstract.visibleProperty());
		vBoxBody.managedProperty().bind(vBoxBody.visibleProperty());
		
		setListeners();
	}

	/**
	 * Set the listeners to show changes in the 
	 * screen when an element is clicked.
	 */
	private void setListeners() {
		txtTitle.textProperty().addListener((observable, oldValue, newValue) ->
			editingArticle.setTitle(newValue));

		txtSubtitle.textProperty().addListener((observable, oldValue, newValue) -> 
			editingArticle.setSubtitle(newValue));

		categorySelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> 
			editingArticle.setCategory(newValue));
	}

	/**
	 * Enable or disable the title edition 
	 * depending on the article being created
	 * or edited.
	 */
	private void checkCreation() {
		if (articleCreation) {
			txtTitle.setEditable(true);
			txtTitle.setFocusTraversable(true);
		} else {
			txtTitle.setEditable(false);
			txtTitle.setFocusTraversable(false);
		}
	}

	/**
	 * Show the selected article's data
	 * on the screen if the user is editing
	 * an already existing article. 
	 */
	private void showArticleDetails() {
		if (editingArticle.getArticleOriginal() != null) {
			txtTitle.setText(editingArticle.getTitle());
			txtSubtitle.setText(editingArticle.getSubtitle());
			categorySelector.getSelectionModel().select(editingArticle.getCategory());
			abstractEditor.setHtmlText(editingArticle.getAbstractText());
			bodyEditor.setHtmlText(editingArticle.getBodyText());

			showArticleImage();
		} else {
			showGenericImage();
		}
	}

	/**
	 * If the article has an image, show it.
	 * If not, show the generic one used in the main screen.
	 */
	private void showArticleImage() {
		if (editingArticle.getImage() != null) {
			imgArticle.setImage(editingArticle.getImage());
		} else {
			showGenericImage();
		}
	}

	/**
	 * Load and show the generic image of the project.
	 */
	private void showGenericImage() {
		imgArticle.setImage(new Image(getClass().getResourceAsStream("../../images/ic_news.png")));
	}

	/**
	 * Load categories.
	 */
	private void configureCategoriesData() {
		if (!categories.isEmpty()) {
			categories.clear();
		}

		for (Categories cat : Categories.values()) {
			if (cat != Categories.ALL) {
				this.categories.add(cat);
			}
		}
	}

	/**
	 * Set the categories into the selector.
	 */
	private void configureCategorySelector() {
		categorySelector.setItems(categories);
	}

	/**
	 * Show the image picker. 
	 * 
	 * @param event - button event that triggered the action
	 */
	@FXML
	private void onImageClicked(MouseEvent event) {
		if (event.getClickCount() >= 2) {
			Scene parentScene = ((Node) event.getSource()).getScene();
			FXMLLoader loader = null;

			try {
				loader = new FXMLLoader(getClass().getResource(AppScenes.IMAGE_PICKER.getFxmlFile()));
				Pane pickerRoot = loader.load();
				Scene scene = new Scene(pickerRoot);
				scene.getStylesheets().add(getClass().getResource("../css/application.css").toExternalForm());
				Window parentStage = parentScene.getWindow();
				Stage stage = new Stage();

				stage.initOwner(parentStage);
				stage.setScene(scene);
				stage.initStyle(StageStyle.UNDECORATED);
				stage.initModality(Modality.WINDOW_MODAL);
				stage.showAndWait();

				ImagePickerController controller = loader.<ImagePickerController>getController();
				Image image = controller.getImage();
				
				if (image != null) {
					editingArticle.setImage(image);
					showArticleImage();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Save the article to the server. 
	 * 
	 * @param event - button event that triggered the action
	 */
	@FXML
	private void btnSendClicked(ActionEvent event) {
		getArticleNewData();
		editingArticle.commit();
		
		if (send()) {
			newsReaderController.clearArticleSelection();
			newsReaderController.getData();
			btnBackClicked(event);
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("An error ocurred");
			alert.setHeaderText(null);

			if (articleCreation) {
				alert.setContentText("The articule could not be created.");
			} else {
				alert.setContentText("The articule could not be edited.");
			}
			
			alert.showAndWait();
		}
	}

	/**
	 * Save the article locally.
	 * 
	 * @param event - button event that triggered the action
	 */
	@FXML
	private void btnSaveClicked(ActionEvent event) {
		getArticleNewData();
		write();
	}

	/**
	 * Show or hide the editors' visibility.
	 * 
	 * @param event - button event that triggered the action
	 */
	@FXML
	private void btnChangeClicked(ActionEvent event) {
		if (editAbstract) {
			vBoxAbstract.setVisible(false);
			vBoxBody.setVisible(true);
			changeBtnChangeText("Write abstract");
		} else {
			vBoxAbstract.setVisible(true);
			vBoxBody.setVisible(false);
			changeBtnChangeText("Write body");
		}

		editAbstract = !editAbstract;
	}

	/**
	 * Back to main screen.
	 * 
	 * @param event - button event that triggered the action
	 */
	@FXML
	private void btnBackClicked(ActionEvent event) {
		Button eventOrigin = (Button) event.getSource();
		eventOrigin.getScene().setRoot(newsReaderController.getContent());
	}

	/**
	 * Change the button text.
	 * 
	 * @param text - button event that triggered the action
	 */
	private void changeBtnChangeText(String text) {
		btnChange.setText(text);
	}

	/**
	 * Get HTML editors' content.
	 */
	private void getArticleNewData() {
		editingArticle.setAbstractText(abstractEditor.getHtmlText());
		editingArticle.setBodyText(bodyEditor.getHtmlText());
	}
	
	/**
	 * Send and article to server,
	 * Title and category must be defined and category must be different to ALL
	 * @return true if the article has been saved
	 */
	private boolean send() {
		String titleText = editingArticle.getTitle(); 
		Categories category = editingArticle.getCategory(); 

		if (titleText == null || category == null || 
				titleText.equals("") || category == Categories.ALL) {
			Alert alert = new Alert(AlertType.ERROR, "Imposible send the article!! Title and categoy are mandatory", ButtonType.OK);
			alert.showAndWait();

			return false;
		}

		try {
			connection.saveArticle(editingArticle.getArticleOriginal());
		} catch (ServerCommunicationError e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * This method is used to set the connection manager which is
	 * needed to save a news 
	 * @param connection connection manager
	 */
	public void setConnectionMannager(ConnectionManager connection) {
		this.connection = connection;
		enableBackBtn();
	}

	private void enableSendBtn() {
		btnSend.setDisable(false);
	}

	private void disableSendBtn() {
		btnSend.setDisable(true);
	}

	private void enableBackBtn() {
		btnBack.setDisable(false);
	}

	/**
	 * @param usr the usr to set
	 */
	public void setUsr(User usr) {
		this.usr = usr;
		
		if (this.usr == null) {
			disableSendBtn();
		} else {
			enableSendBtn();
			lblUser.setText("Welcome back, " + this.usr.getLogin() + "!");
			lblUser.setVisible(true);
		}
	}

	public Article getArticle() {
		Article result = null;

		if (this.editingArticle != null) {
			result = this.editingArticle.getArticleOriginal();
		}

		return result;
	}

	/**
	 * PRE: User must be set
	 * @param article  the article to set
	 */
	public void setArticle(Article article) {
		this.editingArticle = (article != null) ? new ArticleEditModel(article) : new ArticleEditModel(usr);
		configureCategoriesData();
		configureCategorySelector();
		showArticleDetails();
	}
	
	/**
	 * Save an article to a file in a json format
	 * Article must have a title
	 */
	private void write() {
		this.editingArticle.commit();

		// Removes special characters not allowed for filenames
		String name = this.getArticle().getTitle().replaceAll("\\||/|\\\\|:|\\?","");
		String fileName ="saveNews//" + name + ".news";
		JsonObject data = JsonArticle.articleToJson(this.getArticle());

		try (FileWriter file = new FileWriter(fileName)) {
			file.write(data.toString());
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
