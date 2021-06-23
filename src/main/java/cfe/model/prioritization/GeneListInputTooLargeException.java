package cfe.model.prioritization;


public class GeneListInputTooLargeException extends Exception {
	static final long serialVersionUID  = 1L;
	
	public GeneListInputTooLargeException(String message) {
		super( message );
	}
}