package cfe.model.prioritization.reports;

/**
 * Class for throwing exceptions when trying to
 * generate a report.
 * 
 * @author Jim Mullen
 */
public class ReportException extends Exception {
private static final long serialVersionUID = 1L;
	
	public ReportException(String message) {
		super( message );
	}

}
