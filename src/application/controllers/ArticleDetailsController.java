/**
 * 
 */
package application.controllers;

import application.news.Article;
import application.news.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @author ÁngelLucas
 *
 */
public class ArticleDetailsController {

	@FXML
	private Label lblTitle;

	@FXML
	private Label lblSubtitle;

	@FXML
	private Label lblCategory;

	@FXML
	private ImageView imgArticle;

	@FXML
	private WebView articleContent;

	@FXML
	private Button btnBack;

	@FXML
	private Button btnChangeContent;

	@FXML 
	private Label lblContent;

	@FXML
	private Label lblUser;
	
	private User usr;
	private Article article;

	private boolean abstractShown = false;

	private Pane root;

	private NewsReaderController newsReaderController;

	public void setContent(Pane root) {
		this.root = root;
	}

	public Pane getContent() {
		return root;
	}

	public void setNewsReaderController(NewsReaderController newsReaderController) {
		this.newsReaderController = newsReaderController;
	}

	/**
	 * This method is called after the 
	 * screen (FXML file) has been loaded.
	 */
	@FXML
	void initialize() {
		hideWelcomeMessage();
		lblUser.managedProperty().bind(lblUser.visibleProperty());
	}

	/**
	 * Show the selected article's data
	 * on the screen. 
	 */
	private void showArticleDetails() {
		if (article != null) {
			lblTitle.setText(article.getTitle());
			lblSubtitle.setText(article.getSubtitle());
			lblCategory.setText(article.getCategory());
			
			WebEngine engine = articleContent.getEngine();
			engine.loadContent(article.getBodyText());

			if (article.getImageData() != null) {
				imgArticle.setImage(article.getImageData());
			} else {
				imgArticle.setImage(new Image(getClass().getResourceAsStream("../../images/ic_news.png")));
			}
		}
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
	 * Switch between the abstract and the body.
	 * 
	 * @param event - button event that triggered the action
	 */
	@FXML
	private void btnChangeContentClicked(ActionEvent event) {
		if (abstractShown) {
			WebEngine engine = articleContent.getEngine();
			engine.loadContent(article.getBodyText());
			changeButtonText("Show abstract");
			lblContent.setText("Body:");
			abstractShown = false;
		} else {
			WebEngine engine = articleContent.getEngine();
			engine.loadContent(article.getAbstractText());
			changeButtonText("Show body");
			lblContent.setText("Abstract:");
			abstractShown = true;
		}
	}

	/**
	 * Change the button text.
	 * 
	 * @param buttonText - string to show
	 */
	private void changeButtonText(String buttonText) {
		btnChangeContent.setText(buttonText);
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
	}

	/**
	 * Hide the message for the logged user.
	 */
	private void hideWelcomeMessage() {
		lblUser.setText("");
		lblUser.setVisible(false);
	}

	/**
	 * @param article the article to set
	 */
	public void setArticle(Article article) {
		this.article = article;
		showArticleDetails();
	}
}
