package cfe.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;

import cfe.parser.DiscoveryDatabaseParser;
import cfe.parser.PheneVisitParser;
import cfe.utils.Authorization;
import cfe.utils.CohortDataTable;
import cfe.utils.ColumnInfo;
import cfe.utils.WebAppProperties;

public class DiscoveryAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(DiscoveryAction.class);

	private Map<String, Object> webSession;
	
	private File discoveryCsv;
	private String discoveryCsvContentType;
	private String discoveryCsvFileName;
	
	private File discoveryDb;
	private String discoverDbContentType;
	private String discoveryDbFileName;
	
	private String discoveryDbTempFileName;
	private String discoveryCsvTempFileName;
	
	private String scriptDir;
	private String scriptFile;
	private String scriptOutput;
	
	private String baseDir;
	
	private List<String> cohorts;
	private Map<String,String> diagnosisCodes;
	
	private String cohort;
	private String diagnosisCode;
	
	private String dbFileName;
	
	private String outputFile;
	private String reportFile;
	
	private String outputFileName;
	private String reportFileName;
	
	private Set<String> pheneTables;
	
	private String pheneTable;
	
	private String pheneSelection;
	
	private int lowCutoff;
	private int highCutoff;
	
	private String cohortCsv;
	private String cohortCsvFile;
	
	Map<String,ArrayList<ColumnInfo>> phenes = new TreeMap<String,ArrayList<ColumnInfo>>();
	
	public String initialize() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
	    return result;
	}
	
	public String uploadDatabase() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		} else {
			// Copy the upload files to temporary files, because the upload files get deleted
			// and they are needed beyond this method
			File discoveryDbTmp = File.createTempFile("discovery-db-", ".accdb");
			FileUtils.copyFile(this.discoveryDb, discoveryDbTmp);
			this.discoveryDbTempFileName = discoveryDbTmp.getAbsolutePath();
			
			this.dbFileName = discoveryDb.getAbsolutePath();

			DiscoveryDatabaseParser dbParser = new DiscoveryDatabaseParser();
			this.pheneTables = dbParser.getPheneTables(this.discoveryDbTempFileName);

			this.phenes = dbParser.getTableColumnMap(this.discoveryDbTempFileName);
			
			/***
			File discoveryCsvTmp = File.createTempFile("discovery-csv-",  ".csv");
            FileUtils.copyFile(this.discoveryCsv, discoveryCsvTmp);
            this.discoveryCsvTempFileName = discoveryCsvTmp.getAbsolutePath();
            
			// NEED TO GET THE COHORT AND DIAGNOSIS CODES
			PheneVisitParser pheneVisitParser = new PheneVisitParser();
		    cohorts = pheneVisitParser.getCohorts(discoveryDb.getAbsolutePath());
		    //cohorts.add(0, "All");
		    
		    diagnosisCodes = pheneVisitParser.getDiagnosisCodes(discoveryDb.getAbsolutePath());
		    ***/
		}
	    return result;
	}
	
	public String cohortSpecification() throws Exception {
		String result = SUCCESS;
		
		// Split phene selection into table and phene
		String[] pheneInfo = pheneSelection.split("\\|", 2);
		this.pheneTable = pheneInfo[0];
		this.pheneSelection = pheneInfo[1];
		
		
		Database db = DatabaseBuilder.open(new File(this.discoveryDbTempFileName));
		
		Table subjectIdentifiers = db.getTable("Subject Identifiers");
		Table demographics       = db.getTable("Demographics");
		Table diagnosis          = db.getTable("Diagnosis");
		Table pheneData          = db.getTable(this.pheneTable);
		Table chips              = db.getTable("794 chips with Microarray data (HLN6-26-2020)");
		
		CohortDataTable subjectIdentifiersData = new CohortDataTable();
		subjectIdentifiersData.initialize(subjectIdentifiers);
		
		CohortDataTable demographicsData = new CohortDataTable();
		demographicsData.initialize(demographics);
		
		CohortDataTable diagnosisData = new CohortDataTable();
		diagnosisData.initialize(diagnosis);
		
		CohortDataTable pheneDataData = new CohortDataTable();
		pheneDataData.initialize(pheneData);
		
		CohortDataTable chipsData = new CohortDataTable();
		chipsData.initialize(chips);
		
		CohortDataTable cohortData = subjectIdentifiersData.merge(demographicsData);
		cohortData = cohortData.merge(diagnosisData);
		cohortData = cohortData.merge(pheneDataData);
		cohortData = cohortData.merge(chipsData);
		
		this.cohortCsv = cohortData.toCsv();
		
		File cohortCsvTempFile = File.createTempFile("cohort-csv-", ".csv");
		FileUtils.writeStringToFile(cohortCsvTempFile, cohortCsv, "UTF-8");
		this.cohortCsvFile = cohortCsvTempFile.getAbsolutePath();
		
		List<String> tableNames = new ArrayList<String>();
		tableNames.add("Subject Identifiers");
		tableNames.add("Demographics");
		tableNames.add("Diagnosis");
		tableNames.add(this.pheneTable);
		tableNames.add("794 chips with Microarray data (HLN6-26-2020)");
		
		List<Table> tables = new ArrayList<Table>();
		ArrayList<String> columns = new ArrayList<String>();
		
		for (String tableName: tableNames) {
		    Table table = db.getTable(tableName);
		    tables.add(table);
			
			for (Column col: table.getColumns()) {
				String columnName = col.getName();
				if (columnName.equalsIgnoreCase("PheneVisit")) {
				    columnName = tableName + "." + columnName;
				}
				columns.add(col.getName());
			}
		}

		return result;
	}
	

	public String execute() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else {
			//baseDir = System.getProperty("user.dir");
			//baseDir = System.getProperty("user.dir");
			baseDir = WebAppProperties.getRootDir();
			
			// Runtime.getRuntime().exec("test.sh");
			
			this.scriptDir  = new File(getClass().getResource("/R").toURI()).getAbsolutePath();
			this.scriptFile = new File(getClass().getResource("/R/DEdiscovery.R").toURI()).getAbsolutePath();
			
			if (this.cohort == null) this.cohort = "";
			this.cohort = this.cohort.trim();
			if (this.cohort.equals("")) {
				this.cohort = "All";
			}
			
			if (this.diagnosisCode == null) this.diagnosisCode = "";
			this.diagnosisCode = this.diagnosisCode.trim();
			if (this.diagnosisCode.equals("")) {
				this.diagnosisCode = "All";
			}

			String[] rScriptCommand = new String[7];
			rScriptCommand[0] = WebAppProperties.getRscriptPath();
			rScriptCommand[1] = scriptFile;
			rScriptCommand[2] = scriptDir;
			rScriptCommand[3] = this.cohort;
			rScriptCommand[4] = this.diagnosisCode;
			rScriptCommand[5] = this.discoveryDbTempFileName;
			rScriptCommand[6] = this.discoveryCsvTempFileName;
			
			// Log a version of the command used for debugging
			String logRScriptCommand = WebAppProperties.getRscriptPath() + " " + scriptFile 
					+ " " + scriptDir 
					+ " " + "\"" + this.cohort + "\"" + " " + "\"" + this.diagnosisCode + "\"" 
					+ " \"" + discoveryDbFileName + "\" \"" + discoveryCsvFileName + "\"";
			log.info("RSCRIPT COMMAND: " + logRScriptCommand);
			
			scriptOutput = this.runCommand(rScriptCommand);
			
			//---------------------------------------------------------------
			// Get the output and report file paths
			//---------------------------------------------------------------
		    String outputFilePatternString = "Output file created: (.*)";
		    String reportFilePatternString = "Report file created: (.*)";
		    
		    Pattern outputFilePattern = Pattern.compile(outputFilePatternString);
		    Pattern reportFilePattern = Pattern.compile(reportFilePatternString);
		    
			String lines[] = scriptOutput.split("\\r?\\n");
			for (String line: lines) {
			    Matcher outputMatcher = outputFilePattern.matcher(line);
			    Matcher reportMatcher = reportFilePattern.matcher(line);
			    
				if (outputMatcher.find()) {
				   	outputFile = outputMatcher.group(1).trim();
				}
				
				if (reportMatcher.find()) {
					reportFile = reportMatcher.group(1).trim();
				}
			}
			
			outputFileName = FilenameUtils.getBaseName(outputFile) + ".xlsx";
			reportFileName = FilenameUtils.getBaseName(reportFile) + ".xlsx";
		      
			// Convert newlines to breaks for displaying in HTML
			scriptOutput = scriptOutput.replaceAll("\n",  "<br/>\n");
		}
		return result;
	}

	/** 
	 * Executes the specified command and returns the output from the command.
	 *
	 * @param command the command to execute
	 * @return the output generated by the command
	 * @throws Exception
	 */
	public String runCommand(String[] command) throws Exception {
		StringBuilder output = new StringBuilder();
		
        //Process process = Runtime.getRuntime().exec(command);
        
		// This allows debugging:
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // redirect standard error to standard output
        Process process = processBuilder.start();


	    BufferedReader reader = new BufferedReader(
	    new InputStreamReader(process.getInputStream()));

	    String line;
	    while ((line = reader.readLine()) != null) {
	        output.append(line + "\n");
	    }

	    int status = process.waitFor();
	    if (status != 0) {
            //throw new Exception("Command \"" + command + "\" exited with code " + status);
	    }

		return output.toString();
	}
	
	public void setSession(Map<String, Object> session) {
		this.webSession = session;
		
	}
	
	public void validate() {
	}

	public Map<String, Object> getSession() {
		return webSession;
	}

	public File getDiscoveryCsv() {
		return discoveryCsv;
	}

	public void setDiscoveryCsv(File discoveryCsv) {
		this.discoveryCsv = discoveryCsv;
	}

	public String getDiscoveryCsvContentType() {
		return discoveryCsvContentType;
	}

	public void setDiscoveryCsvContentType(String discoveryCsvContentType) {
		this.discoveryCsvContentType = discoveryCsvContentType;
	}

	public String getDiscoveryCsvFileName() {
		return discoveryCsvFileName;
	}

	public void setDiscoveryCsvFileName(String discoveryCsvFileName) {
		this.discoveryCsvFileName = discoveryCsvFileName;
	}

	public File getDiscoveryDb() {
		return discoveryDb;
	}

	public void setDiscoveryDb(File discoveryDb) {
		this.discoveryDb = discoveryDb;
	}

	public String getDiscoverDbContentType() {
		return discoverDbContentType;
	}

	public void setDiscoverDbContentType(String discoverDbContentType) {
		this.discoverDbContentType = discoverDbContentType;
	}

	public String getDiscoveryDbFileName() {
		return discoveryDbFileName;
	}

	public void setDiscoveryDbFileName(String dicoveryDbFileName) {
		this.discoveryDbFileName = dicoveryDbFileName;
	}

	public String getBaseDir() {
		return baseDir;
	}	
	
	public String getScriptDir() {
	    return scriptDir;	
	}
	
	public String getScriptFile() {
		return scriptFile;
	}
	
	public String getScriptOutput() {
		return scriptOutput;
	}
	
	public List<String> getCohorts() {
		return cohorts;
	}
	
	public Map<String,String> getDiagnosisCodes() {
		return diagnosisCodes;
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
		return discoveryDbTempFileName;
	}

	public void setDiscoveryDbTempFileName(String discoveryDbTempFileName) {
		this.discoveryDbTempFileName = discoveryDbTempFileName;
	}

	public String getDiscoveryCsvTempFileName() {
		return discoveryCsvTempFileName;
	}

	public void setDiscoveryCsvTempFileName(String discoveryCsvTempFileName) {
		this.discoveryCsvTempFileName = discoveryCsvTempFileName;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public String getReportFile() {
		return reportFile;
	}

	public void setReportFile(String reportFile) {
		this.reportFile = reportFile;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public String getReportFileName() {
		return reportFileName;
	}

	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
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

	public String getPheneSelection() {
		return pheneSelection;
	}

	public void setPheneSelection(String pheneSelection) {
		this.pheneSelection = pheneSelection;
	}

	public int getLowCutoff() {
		return lowCutoff;
	}

	public void setLowCutoff(int lowCutoff) {
		this.lowCutoff = lowCutoff;
	}

	public int getHighCutoff() {
		return highCutoff;
	}

	public void setHighCutoff(int highCutoff) {
		this.highCutoff = highCutoff;
	}

	public String getPheneTable() {
		return pheneTable;
	}

	public void setPheneTable(String pheneTable) {
		this.pheneTable = pheneTable;
	}

	public String getCohortCsv() {
		return cohortCsv;
	}

	public void setCohortCsv(String cohortCsv) {
		this.cohortCsv = cohortCsv;
	}

	public String getCohortCsvFile() {
		return cohortCsvFile;
	}

	public void setCohortCsvFile(String cohortCsvFile) {
		this.cohortCsvFile = cohortCsvFile;
	}

}
