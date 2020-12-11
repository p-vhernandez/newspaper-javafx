/**
 * 
 */
package application.models;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;


/**
 *This class is used to represent an article when it is editing
 * This class is needed to develop NewsEditController
 * @author ÁngelLucas
 *
 */
public class ArticleEditModel {

	// Reference to original article. Useful for undo changes
	private Article original;
	// Reference to modified article
	private Article edited;

	/**
	 * It is a flag used to indicate that exists updates for the article
	 * - False: unmodified
	 * - True: modified
	 */
	private boolean bModified = false;

	public ArticleEditModel(Article org) {
		original = org;
		edited = new Article (original);
		addedChangeListener();
	}
	
	public ArticleEditModel(User usr) {
		original = new Article();
		if (usr != null) {
			original.setIdUser(usr.getIdUser());
		}
			
		edited = new Article (original);
		addedChangeListener();
	}

	//Getters and setters
	
	/**
	 * This method provide access to edited article abstract property. So, this abstract can be modified through this property
	 * @return a StringPorperty of edited article abstract.
	 * The returned value is suitable for binding
	 */
	public StringProperty abstractTextProperty() {
			return edited.abstractTextProperty();
	}

	public void setAbstractText(String abstractString) {
		edited.setAbstractText(abstractString);
	}

	/**
	 *  This method adds a change listener to all article
	 *  properties in order to know whenever
	 *  there is a change on edited article
	 */
	private void addedChangeListener(){
 	 	this.edited.abstractTextProperty().addListener(
			  (observable, oldvalue, newvalue) -> this.bModified =  true);

		this.edited.bodyTextProperty().addListener(
				(observable, oldvalue, newvalue) -> this.bModified =  true);

		this.edited.isDeletedProperty().addListener(
				(observable, oldvalue, newvalue) -> this.bModified =  true);

		this.edited.titleProperty().addListener(
				(observable, oldvalue, newvalue) -> this.bModified =  true);
				
		this.edited.subtitleProperty().addListener(
				(observable, oldvalue, newvalue) -> this.bModified =  true);
	}
	
	/**
	 * This method provide access to edited article body property. So, this body can be modified through this property
	 * @return a StringPorperty of edited article body.
	 * The returned value is suitable for binding
	 */
	public StringProperty bodyTextProperty() {
		return edited.bodyTextProperty();
	}

	public void setBodyText(String body) {
		edited.setBodyText(body);
	}
	
	/**
	 * Consolidates changes permanently. Changes can't be undone 
	 * Copy article data from the edited one to the original
	 */
	public void commit() {
		if (!this.bModified) {
			// Nothing to do
			return; 
		}		

		copyArticleData(this.edited, this.original);
		
		this.bModified = false;
		this.original.setNeedBeSaved(true);
	}
	
	/**
	 * Copy article data from source to dest
	 * @param source article to copy
	 * @param dest copy from the original (source)
	 */
	private void copyArticleData (Article source, Article dest) {
		dest.setAbstractText(source.getAbstractText());
		dest.setBodyText(source.getBodyText());
		dest.setTitle(source.getTitle());
		dest.setSubtitle(source.getSubtitle());
		dest.setCategory(source.getCategory());
		dest.setDeleted(source.getDeleted());
		dest.setImageData(source.getImageData());
	}
	
	/**
	 * Discard all changes. Changes will be lost
	 * Copy article data from the original one to the edited
	 * 
	 */
	public void discardChanges() {
		if (!this.bModified) {
			// Nothing to do
			return; 
		}

		copyArticleData(this.original, this.edited);
		this.bModified = false;
	}
	
	/**
	 *  This method provide a copy of edited article abstract 
	 * @return a copy of edited article abstract
	 */
	public String getAbstractText() {
		return edited.getAbstractText();
	}
	
	/**
	 * This method give access to the original article
	 * @return the original article if only if has a valid title
	 */
	public Article getArticleOriginal() {
		return (this.original != null &&
				this.original.getTitle() != null &&
				! this.original.getTitle().equals(""))? this.original : null;
	}

	/**
	 * This method provide a copy of article body text
	 * @return a copy of edited article body
	 */
	public String getBodyText() {
		return edited.getBodyText();
	}
	
	/**
	 * This method give access to the edited article category
	 * @return category from the edited article
	 */
	public Categories getCategory() {
		return Categories.valueOf(edited.getCategory().toUpperCase());
	}
	
	/**
	 * This method provide a copy of edited article subtitle 
	 * @return a copy the edited article subtitle
	 */
	public String getSubtitle() {
		return edited.getSubtitle();
	}
	
	/**
	 * This method provide a copy of edited article title 
	 * @return a copy the edited article title
	 */
	public String getTitle() {
		return edited.getTitle();
	}
	
	/**
	 * 
	 * @return isDeleted from the edited article
	 */
	public boolean isDeleted() {
		return edited.isDeleted();
	}

	/**
	 * @return a Property of edited article isDeleted.
	 * The returned value is suitable for binding
	 */
	public Property<Boolean> isDeletedProperty() {
		return edited.isDeletedProperty();
	}

	/**
	 * Set category to the edited article
	 * @param category new category for edited article
	 */
	public void setCategory(Categories category){
		this.bModified = true;
		edited.setCategory(category.toString());
	}

	public Image getImage() {
		return edited.getImageData();
	}
	
	/**
	 * Change the associated image in the edited article
	 * @param image new image for the edited article
	 */
	public void setImage(Image image) {
		edited.setImageData(image);
		this.bModified = true;
	}
	
	/**
	 * Change the associated image in the edited article
	 * @param urlImage uri to an image. The image will be loaded
	 */
	public void setUrlImage(String urlImage) {
		edited.setUrlImage(urlImage);
		this.bModified = true;
	}

	/**
	 * This method provide access to  edited article subtitle property. So, this subtitle can be modified through this property
	 * @return a StringPorperty of edited article title.
	 * The returned value is suitable for binding
	 */
	public StringProperty subtitleProperty(){
		return edited.subtitleProperty();
	}

	/**
	 * Change the edited article's subtitle
	 * @param subtitle subtitle String to be saved
	 */
	public void setSubtitle(String subtitle) {
		edited.setSubtitle(subtitle);
		this.bModified = true;
	}
	
	/**
	 * This method provide access to  edited article title property. So, this title can be modified through this property
	 * @return a StringPorperty of edited article title.
	 * The returned value is suitable for binding
	 */
	public StringProperty titleProperty(){
		return edited.titleProperty();
	}
	
}
