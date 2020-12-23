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
	LOGIN("../fxml/Login.fxml"), 
	READER("fxml/NewsReader.fxml"), 
 	NEWS_DETAILS("../fxml/ArticleDetails.fxml"),
	EDITOR("../fxml/ArticleEdit.fxml"),
	ADMIN("fxml/AdminNews.fxml"),
	IMAGE_PICKER("../fxml/ImagePicker.fxml");
	// IMAGE_PICKER("../fxml/ImagePickerMaterailDesign.fxml");
 
	private String fxmlFile;
 
	private AppScenes (String file){
	 	this.fxmlFile = file;
 	}
 
	public String getFxmlFile() {
		return this.fxmlFile;
 	}
 
}
