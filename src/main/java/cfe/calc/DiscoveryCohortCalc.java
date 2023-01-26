package cfe.calc;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Workbook;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;
import com.opencsv.CSVReader;

import cfe.model.CfeResults;
import cfe.model.CfeResultsFileType;
import cfe.model.CfeResultsSheets;
import cfe.model.CfeResultsType;
import cfe.model.VersionNumber;
import cfe.parser.DiscoveryDatabaseParser;
import cfe.services.CfeResultsService;
import cfe.utils.CohortDataTable;
import cfe.utils.CohortTable;
import cfe.utils.ColumnInfo;
import cfe.utils.DataTable;

public class DiscoveryCohortCalc {

	//private static final Log log = LogFactory.getLog(DiscoveryAction.class);
    private static Logger log = Logger.getLogger(DiscoveryCohortCalc.class.getName());
   
    private String testingDatabaseIdentifier;  // String used to identify testing database in output, possibly the
                                               // original upload file name, since the file path used may be for
                                               // a temporary file.
    
	private String testingDatabaseFileName;   // file path to testing database

	
	private String cohort;
	private String diagnosisCode;
	
	private String dbFileName;
	
	private Set<String> pheneTables;
	
	private String pheneTable;
	
	private String phene;
	
	private double lowCutoff;
	private double highCutoff;
    private double comparisonThreshold = 0.0001;  
	
	private Set<String> genomicsTables;
	private String genomicsTable;
	
	private int numberOfSubjects;
	private int lowVisits;
	private int highVisits;
	
	private String tempDir;
	
	private Date cohortGeneratedTime;
	
	private List<CfeResults> discoveryCohortResultsList;
	private Long discoveryId;
	
	private Long cfeResultsId;
	
	Map<String,ArrayList<ColumnInfo>> phenes = new TreeMap<String,ArrayList<ColumnInfo>>();
	private String scriptOutputTextFileName;
	
	/**
	 * Processes cohort specification and generates the cohort from it.
	 *
	 * @param phene generally has form "phene-table.phene-name"
	 * @return
	 * @throws Exception
	 */
	public CfeResults calculate(
	        String testingDatabaseFilePath,
	        String testingDbLabel,
	        String phene,
	        String pheneTable,
	        double lowCutoffParam,
	        double highCutoffParam,
	        String genomicsTableParam,
	        double comparisonThresholdParam
	) throws Exception {

	    if (testingDatabaseFilePath == null || testingDatabaseFilePath.trim().isEmpty()) {
	        throw new Exception("No testing database specified for Discovery Cohort calculation.");
	    }

        if (phene == null || phene.trim().isEmpty()) {
            throw new Exception("No phene specified for Discovery Cohort calculation.");
        }
        
        if (pheneTable == null || pheneTable.trim().isEmpty()) {
            throw new Exception("No phene table specified for Discovery Cohort calculation.");
        }
        
	    this.testingDatabaseFileName  = testingDatabaseFilePath.trim();
	    this.testingDatabaseIdentifier = testingDbLabel;
	    this.phene                     = phene;
	    this.pheneTable                = pheneTable;
	    this.lowCutoff                 = lowCutoffParam;
	    this.highCutoff                = highCutoffParam;
	    this.genomicsTable             = genomicsTableParam;
        this.comparisonThreshold       = comparisonThresholdParam;
        
	    DiscoveryDatabaseParser dbParser = new DiscoveryDatabaseParser(this.testingDatabaseFileName);
		         
		log.info("Phene \"" + phene + "\" selected from table \"" + pheneTable + "\"");

		dbParser.checkPheneTable(pheneTable);
				
		//-------------------------------------------------
		// Create the cohort data table
		//-------------------------------------------------
		Database db = dbParser.getDatabase();

		Table subjectIdentifiers = db.getTable("Subject Identifiers");
		Table demographics       = db.getTable("Demographics");
		Table diagnosis          = db.getTable("Diagnosis");
		//Table pheneData          = db.getTable(this.pheneTable);
		Table chips              = db.getTable(this.genomicsTable);

		CohortDataTable subjectIdentifiersData = new CohortDataTable();
		subjectIdentifiersData.initializeToAccessTable(subjectIdentifiers);

		CohortDataTable demographicsData = new CohortDataTable();
		demographicsData.initializeToAccessTable(demographics);

		CohortDataTable diagnosisData = new CohortDataTable();
		diagnosisData.initializeToAccessTable(diagnosis);

		//CohortDataTable pheneDataData = new CohortDataTable(this.pheneTable);
		//pheneDataData.initializeToAccessTable(pheneData);

		CohortDataTable chipsData = new CohortDataTable();
		chipsData.initializeToAccessTable(chips);

		CohortDataTable cohortData = subjectIdentifiersData.merge(demographicsData);
				
		log.info("Cohort data table has " + cohortData.getNumberOfRows() 
		    + " rows after merge of demographics table.");
				
		cohortData = cohortData.merge(diagnosisData);
	       
        log.info("Cohort data table has " + cohortData.getNumberOfRows() 
            + " rows after merge of diagnosis table.");
                
		log.info("Before phene tables merge");
		
		//-----------------------------------------------------------------
		// Add all of the phene data tables to the cohort data
		//-----------------------------------------------------------------
		Set<String> pheneTableNames = dbParser.getPheneTables();
		for (String pheneTableName: pheneTableNames) {
		    log.info("Starting to add phene table \"" + pheneTableName + "\" to the cohort data.");
		    CohortDataTable pheneDataTable = new CohortDataTable();
		    Table dbTable = dbParser.getTable(pheneTableName);
		    try {
		        pheneDataTable.initializeToAccessTable(dbTable);
		    } catch (Exception exception) {
		        String message = "Error while processing MS Access database table \"" + pheneTableName
		                + "\": " + exception.getLocalizedMessage();
		        log.severe(message);
		        throw new Exception(message);

		    }

		    if (pheneDataTable.hasColumn("ID")) {
		        pheneDataTable.deleteColumn("ID");  // Delete an ID column if it exists
		    }

		    String firstColumn = pheneDataTable.getColumnName(0);
		    if (!firstColumn.endsWith("PheneVisit")) {
		        throw new Exception("Phene table \"" + pheneTableName 
		                + "\" has first column \"" + firstColumn + "\", which is not "
		                + "a PheneVisit column.");    
		    }
		    //log.info("Data table for phene table \"" + pheneTableName + "\" created.");
		    cohortData = cohortData.mergePheneTable(pheneDataTable);

		    log.info("Cohort data table has " + cohortData.getNumberOfRows() 
		    + " rows after merge of phene table \"" + pheneTableName + "\".");

		}
						
		cohortData = cohortData.merge(chipsData);
        log.info("chip data merged.");

		log.info("Cohort data table has " + cohortData.getNumberOfRows() 
            + " rows after merge of chips data table.");
                
		cohortData.deleteColumns("Field\\d+");
				
		// DEBUG
		// String csv1 = cohortData.toCsv();
		// File cfile1 = FileUtil.createTempFile("cohort-data-1-", ".csv");
		// FileUtils.writeStringToFile(cfile1, csv1, "UTF-8");

		cohortData.enhance(phene, lowCutoff, highCutoff, this.comparisonThreshold);
        log.info("Cohort data enhanced.");
        
        // DEBUG
        // String csv2 = cohortData.toCsv();
        // File cfile2 = FileUtil.createTempFile("cohort-data-2-", ".csv");
        // FileUtils.writeStringToFile(cfile2, csv2, "UTF-8");

		db.close();
		log.info("Database closed.");

        //-------------------------------------------
		// Create cohort and cohort CSV file
		//-------------------------------------------
				log.info("Discovery cohort phene selection: \"" + phene + "\".");
				CohortTable cohort = cohortData.getDiscoveryCohort(phene, lowCutoff, highCutoff, this.comparisonThreshold);
				cohort.sort("Subject", "PheneVisit"); 
                this.cohortGeneratedTime = new Date();
				
                // DEBUG
                // String csv3 = cohortData.toCsv();
                // File cfile3 = FileUtil.createTempFile("cohort-data-3-", ".csv");
                // FileUtils.writeStringToFile(cfile3, csv3, "UTF-8");
                
				this.numberOfSubjects = cohort.getNumberOfSubjects();
				this.lowVisits = cohort.getLowVisits();
				this.highVisits = cohort.getHighVisits();
				
				ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
                
				DataTable infoTable = this.createCohortInfoTable();
				
                // DEBUG
                // String csv4 = cohortData.toCsv();
                // File cfile4 = FileUtil.createTempFile("cohort-data-4-", ".csv");
                // FileUtils.writeStringToFile(cfile4, csv4, "UTF-8");
                
				LinkedHashMap<String, DataTable> cohortTables = new LinkedHashMap<String, DataTable>();
				cohortTables.put(CfeResultsSheets.DISCOVERY_COHORT,  cohort);
				cohortTables.put(CfeResultsSheets.COHORT_DATA, cohortData);
				cohortTables.put(CfeResultsSheets.DISCOVERY_COHORT_INFO, infoTable);
				
				int rowAccessWindowSize = 100;
				Workbook cohortWorkbook = DataTable.createStreamingWorkbook(cohortTables, rowAccessWindowSize);
				//cohortData.enhanceCohortDataSheet(cohortWorkbook, "cohort data", pheneSelection, lowCutoff, highCutoff);
                  
				// NO LONGER NEEDED ???
				//File cohortXlsxTempFile = File.createTempFile("cohort-", ".xlsx");
				//out = new FileOutputStream(cohortXlsxTempFile);
				//cohortWorkbook.write(out);
				//out.close();
				//this.cohortXlsxFile = cohortXlsxTempFile.getAbsolutePath();

	                
		        // Save the discovery cohort results in the CFE database
                CfeResults cfeResults = new CfeResults(cohortWorkbook, CfeResultsType.DISCOVERY_COHORT,
                        this.cohortGeneratedTime, this.phene,
                        lowCutoff, highCutoff);
                
                // Save all the discovery cohort data files
                cfeResults.addCsvFile(CfeResultsFileType.DISCOVERY_COHORT, cohort.toCsv());
                cfeResults.addCsvFile(CfeResultsFileType.DISCOVERY_COHORT_DATA, cohortData.toCsv());
                cfeResults.addCsvFile(CfeResultsFileType.DISCOVERY_COHORT_INFO, infoTable.toCsv());
                
                CfeResultsService.save(cfeResults);
                this.cfeResultsId = cfeResults.getCfeResultsId();
		
		return cfeResults;
	}



	public DataTable createPheneDataTable(DiscoveryDatabaseParser dbParser) throws Exception {
	    String keyColumn = "PheneVisit";
	    DataTable pheneDataTable = null;
	    
	    Set<String> pheneTableNames = new TreeSet<String>();
	    pheneTableNames = dbParser.getPheneTables();
	    
	    for (String pheneTableName: pheneTableNames) {
	        DataTable dataTable = new DataTable(keyColumn);
	        dataTable.initializeToAccessTable( dbParser.getTable(pheneTableName) );
	        if (pheneDataTable == null) {
	            pheneDataTable = dataTable;
	        }
	        else {
	            String joinColumn1 = keyColumn;
	            String joinColumn2 = keyColumn;
	            DataTable.join(keyColumn, joinColumn1, joinColumn2, pheneDataTable, dataTable, DataTable.JoinType.OUTER);
	        }
	    }
	    
	    return pheneDataTable;
	}

    public DataTable createCohortInfoTable() throws Exception {
		DataTable infoTable = new DataTable("attribute");
		infoTable.insertColumn("attribute", 0, "");
		infoTable.insertColumn("value",  1,  "");
		
		ArrayList<String> row = new ArrayList<String>();
		row.add("CFE Version");
		row.add(VersionNumber.VERSION_NUMBER);
		infoTable.addRow(row);
		
		row = new ArrayList<String>();
		row.add("Time Cohort Generated");
		row.add(this.cohortGeneratedTime.toString());
		infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Test Database");
        row.add(this.testingDatabaseIdentifier);
        infoTable.addRow(row);
        
		row = new ArrayList<String>();
		row.add("Phene Table");
		row.add(this.pheneTable);
		infoTable.addRow(row);
		
		row = new ArrayList<String>();
		row.add("Phene");
		row.add(this.phene);
		infoTable.addRow(row);
		
		row = new ArrayList<String>();
		row.add("Low Cutoff");
		row.add(this.lowCutoff + "");
		infoTable.addRow(row);
		
		row = new ArrayList<String>();
		row.add("High Cutoff");
		row.add(this.highCutoff + "");
		infoTable.addRow(row);
		
		row = new ArrayList<String>();
		row.add("Microarray Table");
		row.add(this.genomicsTable);
		infoTable.addRow(row);

        row = new ArrayList<String>();
        row.add("Number of Cohort Subjects");
        row.add(this.numberOfSubjects + "");
        infoTable.addRow(row);
        
	    row = new ArrayList<String>();
	    row.add("Number of Cohort Low Visits");
	    row.add(this.lowVisits + "");
	    infoTable.addRow(row);

        row = new ArrayList<String>();
        row.add("Number of Cohort High Visits");
        row.add(this.highVisits + "");
        infoTable.addRow(row);	        
		return infoTable;
    }
    
    /** 
     * WORK IN PROGRESS
     * For checking that gene expression file has phene visit, instead of affy visit, columns. 
     */
    public void checkGeneExpressionFile(String geneExpressionFile) throws Exception {
        FileReader filereader = new FileReader(geneExpressionFile);
        CSVReader csvReader = new CSVReader(filereader);
        String[] visits;
     
        visits = csvReader.readNext();
        for (String visit: visits) {
            // Need to compare these to pheneVisits in the cohort data
            // ...
        }

    }

	public String getDbFileName() {
		return this.dbFileName;
	}

	public void setDbFileName(String dbFileName) {
	    this.dbFileName = dbFileName;
	}

	public String getCohort() {
		return cohort;
	}

	public void setCohort(String cohort) {
		this.cohort = cohort;
	}

	public String getDiagnosisCode() {
		return diagnosisCode;
	}

	public void setDiagnosisCode(String diagnosisCode) {
		this.diagnosisCode = diagnosisCode;
	}

	public String getDiscoveryDbTempFileName() {
		return testingDatabaseFileName;
	}

	public void setDiscoveryDbTempFileName(String discoveryDbTempFileName) {
		this.testingDatabaseFileName = discoveryDbTempFileName;
	}

	public Set<String> getPheneTables() {
		return pheneTables;
	}

	public void setPheneTables(Set<String> pheneTables) {
		this.pheneTables = pheneTables;
	}

	public Map<String, ArrayList<ColumnInfo>> getPhenes() {
		return phenes;
	}

	public void setPhenes(Map<String, ArrayList<ColumnInfo>> phenes) {
		this.phenes = phenes;
	}

	public String getPhene() {
		return this.phene;
	}

	public void setPhene(String phene) {
		this.phene = phene;
	}

	public double getLowCutoff() {
		return lowCutoff;
	}

	public void setLowCutoff(double lowCutoff) {
		this.lowCutoff = lowCutoff;
	}

	public double getHighCutoff() {
		return highCutoff;
	}

	public void setHighCutoff(double highCutoff) {
		this.highCutoff = highCutoff;
	}

	public String getPheneTable() {
		return pheneTable;
	}

	public void setPheneTable(String pheneTable) {
		this.pheneTable = pheneTable;
	}


	public Set<String> getGenomicsTables() {
        return genomicsTables;
    }

    public void setGenomicsTables(Set<String> genomicsTables) {
        this.genomicsTables = genomicsTables;
    }

    public String getGenomicsTable() {
        return genomicsTable;
    }

    public void setGenomicsTable(String genomicsTable) {
        this.genomicsTable = genomicsTable;
    }

	public int getNumberOfSubjects() {
		return numberOfSubjects;
	}

	public void setNumberOfSubjects(int numberOfSubjects) {
		this.numberOfSubjects = numberOfSubjects;
	}

	public int getLowVisits() {
		return lowVisits;
	}

	public void setLowVisits(int lowVisits) {
		this.lowVisits = lowVisits;
	}

	public int getHighVisits() {
		return highVisits;
	}

	public void setHighVisits(int highVisits) {
		this.highVisits = highVisits;
	}

	public String getScriptOutputTextFileName() {
		return scriptOutputTextFileName;
	}

	public void setScriptOutputTextFileName(String scriptOutputTextFileName) {
		this.scriptOutputTextFileName = scriptOutputTextFileName;
	}

    public String getTempDir() {
		return tempDir;
	}

	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

    public Date getCohortGeneratedTime() {
        return cohortGeneratedTime;
    }

    public void setCohortGeneratedTime(Date cohortGeneratedTime) {
        this.cohortGeneratedTime = cohortGeneratedTime;
    }

    public List<CfeResults> getDiscoveryCohortResultsList() {
        return discoveryCohortResultsList;
    }

    public void setDiscoveryCohortResultsList(List<CfeResults> discoveryCohortResultsList) {
        this.discoveryCohortResultsList = discoveryCohortResultsList;
    }

    public Long getDiscoveryId() {
        return discoveryId;
    }

    public void setDiscoveryId(Long discoveryId) {
        this.discoveryId = discoveryId;
    }

    public Long getCfeResultsId() {
        return cfeResultsId;
    }

    public void setCfeResultsId(Long cfeResultsId) {
        this.cfeResultsId = cfeResultsId;
    }

    public double getComparisonThreshold() {
        return this.comparisonThreshold;
    }

    public void setComparisonThreshold(double comparisonThreshold) {
        this.comparisonThreshold = comparisonThreshold;
    }

}
