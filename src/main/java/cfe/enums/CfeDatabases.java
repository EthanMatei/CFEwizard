package cfe.enums;

/**
 * Enum for database names.
 * 
 * @author Michel Tavares
 * @author Jim Mullen
 *
 */
public enum CfeDatabases {
	DISCOVERY("Discovery"),
	PRIORITIZATION("Prioritization"),
	VALIDATION("Validation"),
	TESTING("Testing")
	;
	
	private String label;
	private CfeDatabases (String label){ this.label = label; }
	
	public String getLabel() {
		return this.label; 
	}
	
	public static CfeDatabases fromLabel(String label) {
		CfeDatabases theDb = null;
		if (label != null) {
			for (CfeDatabases db : CfeDatabases.values()) {
				if (label.equalsIgnoreCase(db.label)) {
					theDb = db;
					break;
				}
			}
		}
		return theDb;
	}
		
}
