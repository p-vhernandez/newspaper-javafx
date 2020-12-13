package application.controllers;

import java.io.IOException;

import application.AppScenes;
import application.news.Categories;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class ArticleNewController {

    private NewsReaderController newsReaderController;
	private Pane root;

	private ObservableList<Categories> categories;

    public ArticleNewController() {
        this.newsReaderController = newsReaderController;
		categories = FXCollections.observableArrayList();

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(AppScenes.EDITOR.getFxmlFile()));
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
	void initialize() { }

}
