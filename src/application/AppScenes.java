/**
 * Patricia Vera
 * 
 */
package application;

/**
 * Contain all app scenes
 * @author ÁngelLucas
 *
 */
public enum AppScenes {
	LOGIN("/application/fxml/Login.fxml"), 
	READER("/application/fxml/NewsReader.fxml"), 
 	NEWS_DETAILS ("/application/fxml/ArticleDetails.fxml"),
	EDITOR("/application/fxml/ArticleEdit.fxml"), 
	ADMIN("/application/fxml/AdminNews.fxml"),
	IMAGE_PICKER("/application/fxml/ImagePicker.fxml");
	// IMAGE_PICKER("ImagePickerMaterailDesign.fxml")
 
	private String fxmlFile;
 
	private AppScenes (String file){
	 	this.fxmlFile = file;
 	}
 
	public String getFxmlFile() {
		return this.fxmlFile;
 	}
 
}
