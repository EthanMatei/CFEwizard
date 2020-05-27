package cfg.action;

/**
 * Class for throwing exceptions in actions when
 * an error occurs to make the control flow
 * of the action easier to manage.
 * 
 * @author Jim Mullen
 *
 */
public class ActionErrorException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public ActionErrorException(String message) {
		super( message );
	}
}