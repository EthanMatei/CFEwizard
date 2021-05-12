package cfe.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
import cfe.utils.CohortTable;
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
	private String discoveryDbContentType;
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
	
	private String cohortDataCsv;
	private String cohortDataCsvFile;
	private String cohortDataXlsxFile;
	
	private String cohortCsvFile;
	private String cohortXlsxFile;
	
	private Set<String> microarrayTables;
	private String microarrayTable;
	
	private int numberOfSubjects;
	private int lowVisits;
	private int highVisits;
	
	private String scriptOutputTextFile;
	
	private String errorMessage;
	
	Map<String,ArrayList<ColumnInfo>> phenes = new TreeMap<String,ArrayList<ColumnInfo>>();
	private String scriptOutputTextFileName;
	
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
			
			this.microarrayTables = dbParser.getMicroarrayTables(this.discoveryDbTempFileName);
			
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
	
	/**
	 * Processes cohort specification and generates the cohort from it.
	 *
	 * @return
	 * @throws Exception
	 */
	public String cohortSpecification() {
		String result = SUCCESS;

		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		} else {
			try {
				// Split phene selection into table and phene
				String[] pheneInfo = pheneSelection.split("\\|", 2);
				this.pheneTable = pheneInfo[0];
				this.pheneSelection = pheneInfo[1];
				log.info("Phene \"" + pheneSelection + "\" selected from table \"" + pheneTable + "\"");

				//-------------------------------------------------
				// Create the chort data table
				//-------------------------------------------------
				Database db = DatabaseBuilder.open(new File(this.discoveryDbTempFileName));

				Table subjectIdentifiers = db.getTable("Subject Identifiers");
				Table demographics       = db.getTable("Demographics");
				Table diagnosis          = db.getTable("Diagnosis");
				Table pheneData          = db.getTable(this.pheneTable);
				Table chips              = db.getTable(this.microarrayTable);

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
				
				this.cohortDataCsv = cohortData.toCsv();

				// Create an Xlsx (spreadsheet) version of the cohort data
				File cohortDataXlsxTempFile = File.createTempFile("cohort-data-", ".xslx");
				FileOutputStream out = new FileOutputStream(cohortDataXlsxTempFile);
				cohortData.toXlsx().write(out);
				out.close();
				this.cohortDataXlsxFile = cohortDataXlsxTempFile.getAbsolutePath();

				File cohortDataCsvTempFile = File.createTempFile("cohort-data-", ".csv");
				FileUtils.writeStringToFile(cohortDataCsvTempFile, cohortDataCsv, "UTF-8");
				this.cohortDataCsvFile = cohortDataCsvTempFile.getAbsolutePath();

				db.close();

				//-------------------------------------------
				// Create cohort and cohort CSV file
				//-------------------------------------------
				CohortTable cohort = cohortData.getCohort(pheneSelection, lowCutoff, highCutoff);
				cohort.sort("Subject", "PheneVisit");
				
				this.numberOfSubjects = cohort.getNumberOfSubjects();
				this.lowVisits = cohort.getLowVisits();
				this.highVisits = cohort.getHighVisits();
				
				String cohortCsv = cohort.toCsv();

				File cohortCsvTempFile = File.createTempFile("cohort-", ".csv");
				FileUtils.writeStringToFile(cohortCsvTempFile, cohortCsv, "UTF-8");
				this.cohortCsvFile = cohortCsvTempFile.getAbsolutePath();

				// Create an Xlsx (spreadsheet) version of the cohort data
				File cohortXlsxTempFile = File.createTempFile("cohort-", ".xslx");
				out = new FileOutputStream(cohortXlsxTempFile);
				cohort.toXlsx().write(out);
				out.close();
				this.cohortXlsxFile = cohortXlsxTempFile.getAbsolutePath();

				//----------------------------------------------------
				// Get diagnosis codes needed for calculation
				//----------------------------------------------------
				PheneVisitParser pheneVisitParser = new PheneVisitParser();
				diagnosisCodes = pheneVisitParser.getDiagnosisCodes(this.discoveryDbTempFileName);
			} catch (Exception exception) {
				result = ERROR;
				log.error("*** ERROR: " + exception.getMessage());
				errorMessage = exception.getLocalizedMessage();
			}
		}
		
		return result;
	}
	
	public String calculate() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		} else {
			log.info("Diagnosis code: " + this.diagnosisCode);
			this.baseDir = WebAppProperties.getRootDir();

			this.scriptDir  = new File(getClass().getResource("/R").toURI()).getAbsolutePath();
			this.scriptFile = new File(getClass().getResource("/R/DEdiscovery.R").toURI()).getAbsolutePath();
			
	        //------------------------------------------------------
	        // Get the gene expression CSV file
	        //------------------------------------------------------
		    File discoveryCsvTmp = File.createTempFile("discovery-csv-",  ".csv");
		    if (this.discoveryCsv != null) {
                FileUtils.copyFile(this.discoveryCsv, discoveryCsvTmp);
		    }
            this.discoveryCsvTempFileName = discoveryCsvTmp.getAbsolutePath();
            
            //---------------------------------------------
            // Process diagnosis code
            //---------------------------------------------
			if (this.diagnosisCode == null) this.diagnosisCode = "";
			this.diagnosisCode = this.diagnosisCode.trim();
			if (this.diagnosisCode.equals("") || this.diagnosisCode.equalsIgnoreCase("ALL")) {
				this.diagnosisCode = "All";
			}
			
            //---------------------------------------
			// Create R script command
			//---------------------------------------
			String[] rScriptCommand = new String[11];
			rScriptCommand[0] = WebAppProperties.getRscriptPath();    // Full path of the Rscript command
			rScriptCommand[1] = scriptFile;     // The R script to run
			rScriptCommand[2] = scriptDir;
			rScriptCommand[3] = this.cohortCsvFile;   // Change - name of cohort CSV File
			rScriptCommand[4] = this.diagnosisCode;
			rScriptCommand[5] = this.discoveryDbTempFileName;
			rScriptCommand[6] = this.discoveryCsvTempFileName;
			rScriptCommand[7] = this.pheneSelection;
			rScriptCommand[8] = this.pheneTable;
			rScriptCommand[9] = this.lowCutoff + "";
			rScriptCommand[10] = this.highCutoff + "";
			
			// Log a version of the command used for debugging
			String logRScriptCommand = WebAppProperties.getRscriptPath() + " " + scriptFile 
					+ " " + scriptDir 
					+ " " + "\"" + this.cohortCsvFile + "\"" + " " + "\"" + this.diagnosisCode + "\"" 
					+ " \"" + discoveryDbFileName + "\" \"" + discoveryCsvFileName + "\"" + this.pheneSelection;
			log.info("RSCRIPT COMMAND: " + logRScriptCommand);
			
			scriptOutput = this.runCommand(rScriptCommand);
			
			//-------------------------------------------------------
			// Set up script output file log
			//-------------------------------------------------------
			File scriptOutputTextTempFile = File.createTempFile("discovery-r-script-output-", ".txt");
			FileOutputStream out = new FileOutputStream(scriptOutputTextTempFile);
			PrintWriter writer = new PrintWriter(out);
			writer.write(scriptOutput);
			writer.close();
			this.scriptOutputTextFile = scriptOutputTextTempFile.getAbsolutePath();
			this.scriptOutputTextFileName = "script-output.xlsx";
			log.info("script output text file: " + scriptOutputTextFile);
			
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
		
		log.info("run command: " + String.join(" ", command));
        
		// This allows debugging:
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // redirect standard error to standard output
        
        log.info("*** Before process start");
        Process process = processBuilder.start();


	    BufferedReader reader = new BufferedReader(
	    new InputStreamReader(process.getInputStream()));

	    String line;
	    while ((line = reader.readLine()) != null) {
	        output.append(line + "\n");
	    }

	    log.info("*** Going to wait for process...");
	    int status = process.waitFor();
	    if (status != 0) {
            //throw new Exception("Command \"" + command + "\" exited with code " + status);
	    }
	    
	    reader.close();
        log.info("*** reader closed");
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

	public String getDiscoveryDbContentType() {
		return discoveryDbContentType;
	}

	public void setDiscoveryDbContentType(String discoverDbContentType) {
		this.discoveryDbContentType = discoverDbContentType;
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
		log.info("*** Getter diagnosis code: " + this.diagnosisCode);
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

	public String getCohortDataCsv() {
		return cohortDataCsv;
	}

	public void setCohortDataCsv(String cohortDataCsv) {
		this.cohortDataCsv = cohortDataCsv;
	}

	public String getCohortDataCsvFile() {
		return cohortDataCsvFile;
	}

	public void setCohortDataCsvFile(String cohortDataCsvFile) {
		this.cohortDataCsvFile = cohortDataCsvFile;
	}

	public String getCohortCsvFile() {
		return cohortCsvFile;
	}

	public void setCohortCsvFile(String cohortCsvFile) {
		this.cohortCsvFile = cohortCsvFile;
	}

	public Set<String> getMicroarrayTables() {
		return microarrayTables;
	}

	public void setMicroarrayTables(Set<String> microarrayTables) {
		this.microarrayTables = microarrayTables;
	}

	public String getMicroarrayTable() {
		return microarrayTable;
	}

	public void setMicroarrayTable(String microarrayTable) {
		this.microarrayTable = microarrayTable;
	}

	public String getCohortDataXlsxFile() {
		return cohortDataXlsxFile;
	}

	public void setCohortDataXlsxFile(String cohortDataXlsxFile) {
		this.cohortDataXlsxFile = cohortDataXlsxFile;
	}

	public String getCohortXlsxFile() {
		return cohortXlsxFile;
	}

	public void setCohortXlsxFile(String cohortXlsxFile) {
		this.cohortXlsxFile = cohortXlsxFile;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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

	public String getScriptOutputTextFile() {
		return scriptOutputTextFile;
	}

	public void setScriptOutputTextFile(String scriptOutputTextFile) {
		this.scriptOutputTextFile = scriptOutputTextFile;
	}

	public String getScriptOutputTextFileName() {
		return scriptOutputTextFileName;
	}

	public void setScriptOutputTextFileName(String scriptOutputTextFileName) {
		this.scriptOutputTextFileName = scriptOutputTextFileName;
	}

}
