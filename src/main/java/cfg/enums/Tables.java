package cfg.enums;

/**
 * label: MS ACCESS table name
 * tblname: MySQL table name
 * @author mtavares
 *
 */
public enum Tables {
	
	// MS Access table name, CFG MySQL table name, class name
	HU_BRAIN_GEX("HUBRAIN-GEX", TblNames.HU_BRAIN_GEX, "HuBrainGex"), // *
	HU_BRAIN_PROT("HUBRAIN-PROT", TblNames.HU_BRAIN_PROT, "HuBrainProt"),
	HU_BRAIN_MET("HUBRAIN-MET", TblNames.HU_BRAIN_MET, "HuBrainMet"),
	
	HU_GENE_CNV("HUGEN-CNV", TblNames.HU_GENE_CNV, "HuGeneCNV"), // *
	HU_GENE_ASSOC("HUGEN-ASSOCIATION", TblNames.HU_GENE_ASSOC, "HuGeneAssoc"), 
	HU_GENE_LINKAGE("HUGEN-LINKAGE", TblNames.HU_GENE_LINKAGE, "HuGeneLinkage"), // *
	
	HU_PER_GEX("HUPER-GEX", TblNames.HU_PER_GEX, "HuPerGex"),
	HU_PER_MET("HUPER-MET", TblNames.HU_PER_MET, "HuPerMet"),
	HU_PER_PROT("HUPER-PROT", TblNames.HU_PER_PROT, "HuPerProt"),
	
	NH_BRAIN_GEX("NHBRAIN-GEX", TblNames.NH_BRAIN_GEX, "NhBrainGex"),
	NH_BRAIN_PROT("NHBRAIN-PROT", TblNames.NH_BRAIN_PROT, "NhBrainProt"),
	NH_BRAIN_MET("NHBRAIN-MET", TblNames.NH_BRAIN_MET, "NhBrainMet"),
	
	NH_PER_GEX("NHPER-GEX", TblNames.NH_PER_GEX, "NhPerGex"),
	NH_PER_PROT("NHPER-PROT", TblNames.NH_PER_PROT, "NhPerProt"),
	NH_PER_MET("NHPER-MET", TblNames.NH_PER_MET, "NhPerMet"),
	
	NH_GENE_CNV("NHGEN-CNV", TblNames.NH_GENE_CNV, "NhGeneCNV"), // *
	NH_GENE_ASSOC("NHGEN-ASSOCIATION", TblNames.NH_GENE_ASSOC, "NhGeneAssoc"), 
	NH_GENE_LINKAGE("NHGEN-LINKAGE", TblNames.NH_GENE_LINKAGE, "NhGeneLinkage"), // *
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
		public static final String DISORDER         	= "disorder";
		
		public static final String HU_BRAIN_GEX 	= "hubraingex";
		public static final String HU_BRAIN_PROT 	= "hubrainprot";
		public static final String HU_BRAIN_MET 	= "hubrainmet";
		
		public static final String HU_GENE_CNV 		= "hugenecnv";
		public static final String HU_GENE_ASSOC 	= "hugeneassoc";
		public static final String HU_GENE_LINKAGE	= "hugenelinkage";
		
		public static final String HU_PER_GEX 		= "hupergex";
		public static final String HU_PER_MET		= "hupermet";
		public static final String HU_PER_PROT 		= "huperprot";
		

		public static final String NH_BRAIN_GEX 	= "nhbraingex";
		public static final String NH_BRAIN_PROT 	= "nhbrainprot";
		public static final String NH_BRAIN_MET 	= "nhbrainmet";
		
		public static final String NH_PER_GEX 		= "nhpergex";
		public static final String NH_PER_PROT 		= "nhperprot";
		public static final String NH_PER_MET 		= "nhpermet";
		
		public static final String NH_GENE_CNV 		= "nhgenecnv";
		public static final String NH_GENE_ASSOC 	= "nhgeneassoc";
		public static final String NH_GENE_LINKAGE	= "nhgenelinkage";
		
		public static final String GENE_LIST 		= "genelist";
		public static final String SCORING_DATA 	= "scoringdata";
	}
}
