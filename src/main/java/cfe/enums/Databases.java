package cfe.enums;

/**
 * Enum for database names.
 * 
 * @author Michel Tavares
 * @author Jim Mullen
 *
 */
public enum Databases {
	
	HUBRAIN("Human Brain"),
	HUGENE("Human Genetic"),
	HUPER("Human Peripheral"),
	NHBRAIN("NonHuman Brain"),
	NHGENE("NonHuman Genetic"),
	NHPER("NonHuman Peripheral");
	
	private String label;
	private Databases (String label){ this.label = label; }
	
	public String getLabel() {
		return this.label; 
	}
	
	public static Databases fromLabel(String label) {
		Databases theDb = null;
		if (label != null) {
			for (Databases db : Databases.values()) {
				if (label.equalsIgnoreCase(db.label)) {
					theDb = db;
					break;
				}
			}
		}
		return theDb;
	}
		
}
