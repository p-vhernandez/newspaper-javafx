/**
 * 
 */
package application.utils.exceptions;

/**
 * @author agonzalez
 *
 */
@SuppressWarnings("serial")
public class ErrorMalFormedArticle extends Exception {

	/**
	 * 
	 */
	public ErrorMalFormedArticle() { }

	/**
	 * @param message error message
	 */
	public ErrorMalFormedArticle(String message) {
		super(message);
	}

}
