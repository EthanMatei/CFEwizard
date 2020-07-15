package cfe.enums;

/**
 * label: MS ACCESS table name
 * tblname: MySQL table name
 * @author mtavares
 *
 */
public enum Tables {
	
	// MS Access table name, CFE MySQL table name, class name
	DISCOVERY("Discovery", TblNames.DISCOVERY, "Discovery"),
	PRIORITIZATION("Prioritization", TblNames.PRIORITIZATION, "Prioritization")
	;
	
	// Used for sanity check in HibernateUtils
	// All the tables should be here
	public static final int size = Tables.values().length;
	
	private final String label;
	private final String tblname;
	private final String classname;
	
	private Tables (String label, String tblname, String classname){ 
		this.label = label; 
		this.tblname = tblname;
		this.classname = classname;
	}
	
	public String getLabel(){ return this.label; }
	
	public final String getTblName() {return this.tblname;}
	
	public final String getClassname() { return this.classname;}
	
	public String toString() {return getLabel();}
	
	/**
	 * The only reason we have this is to have a single place for the table names
	 */
	public final static class TblNames	{
		
		public static final String DATABASE_UPLOAD_INFO	= "databaseuploadinfo";

	    public static final String DISCOVERY = "discovery";
	    public static final String PRIORITIZATION = "prioritization";
	}
}
