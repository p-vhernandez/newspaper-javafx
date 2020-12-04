/**
 * 
 */
package application.controllers;

import java.io.IOException;

import application.AppScenes;
import application.news.Article;
import application.news.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

/**
 * @author ÁngelLucas
 *
 */
public class ArticleDetailsController {
	
	private User usr;
	private Article article;

	private Pane root;

	private NewsReaderController newsReaderController;
	
	public ArticleDetailsController(NewsReaderController newsReaderController) {
		this.newsReaderController = newsReaderController;
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.NEWS_DETAILS.getFxmlFile()));
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
		// TODO: show game details in screen
	}

	/**
	 * @param usr the usr to set
	 */
	public void setUsr(User usr) {
		this.usr = usr;

		if (usr == null) {
			// Not logged user
			return; 
		}

		// TODO Update UI information
	}

	/**
	 * @param article the article to set
	 */
	public void setArticle(Article article) {
		this.article = article;
	}
}
