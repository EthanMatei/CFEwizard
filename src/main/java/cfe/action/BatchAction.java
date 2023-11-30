package cfe.action;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.action.SessionAware;

import cfe.calc.DiscoveryCohortCalc;
import cfe.calc.DiscoveryScoresCalc;
import cfe.calc.PrioritizationScoresCalc;
import cfe.calc.TestingCohortsCalc;
import cfe.calc.TestingScoresCalc;
import cfe.calc.ValidationCohortCalc;
import cfe.calc.ValidationScoresCalc;
import cfe.model.CfeResults;
import cfe.model.CfeResultsNewestFirstComparator;
import cfe.model.CfeResultsType;
import cfe.model.CfeResultsWorkbook;
import cfe.model.DiagnosisType;
import cfe.model.PercentileScores;
import cfe.model.prioritization.GeneListInput;
import cfe.model.prioritization.disease.DiseaseSelector;
import cfe.parser.DiscoveryDatabaseParser;
import cfe.services.CfeResultsService;
import cfe.services.prioritization.DisorderService;
import cfe.utils.Authorization;
import cfe.utils.FileUtil;
import cfe.utils.PheneCondition;
import cfe.utils.TableCheckInfo;


public class BatchAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	//private static final Log log = LogFactory.getLog(DiscoveryAction.class);
    private static Logger log = Logger.getLogger(BatchAction.class.getName());

	private Map<String, Object> webSession;
	
	public final static long TESTING_PHASE_START = -1L;
	
    public final static long MANUAL_RESULTS_START = 0L;
	
	private double DEFAULT_COMPARISON_THRESHOLD = 0.0001;
	
	private File testingDb;
	private String testingDbContentType;
	private String testingDbFileName;
	private String testingDbTempFileName;

	// Testing Input Spreadsheet for running the Testng Phase by itself
	private File testingInputSpreadsheet;
	private String testingInputSpreadsheetContentType;
	private String testingInputSpreadsheetFileName;
	private String testingInputSpreadsheetTempFileName;
	    
    private File probesetToGeneMappingDb;
    private String probesetToGeneMappingDbContentType;
    private String probesetToGeneMappingDbFileName;
    //private String probesetToGeneMappingDbTempFileName;
    
    //private DataTable probesetToGeneMapTable;
    //private Map<String,String> probesetToGeneMap;
    
    private File discoveryGeneExpressionCsv;
    private String discoveryGeneExpressionCsvContentType;
    private String discoveryGeneExpressionCsvFileName;
    
    private File geneExpressionCsv;
    private String geneExpressionCsvContentType;
    private String geneExpressionCsvFileName;
    
    private Set<String> genomicsTables;
    private String genomicsTable;

    private String diagnosisCode;
    private Map<String,String> diagnosisCodes;
    private List<String> diagnosisCodesList;
    

    
    private boolean debugDiscoveryScoring = false;
    
    private List<String> phenes;
    
    private String manualPheneInfoSource;
    
    /* UI flags (to avoid static method calls in JSP pages -------------------------*/
    private boolean showDiscoveryCohort;
    private boolean showDiscoveryScores;
    private boolean showPrioritizationScores;
    private boolean showValidationCohort;
    private boolean showValidationScores;
    private boolean showTestingInputs;    // For case when starting from testing phase
    private boolean showTestingCohorts;
    private boolean showTestingScores;
    
    /* Starting Results (optional) -------------------------------------------------- */
    private Map<Long, String> pastCfeResultsMap;
    private Long startingCfeResultsId;
    private List<CfeResults> startingResultsList;
    private String startingResultsType;
    
    private List<String> manualResultsTypeList;
    private String manualResultsType;
    private File manualResultsSpreadsheet;
    private String manualResultsSpreadsheetFileName;
    private String manualResultsSpreadsheetContentType;
    
    private List<String> endingResultsTypeList;
    private String endingResultsType;
    
    /* Results filter */
    private String cfeResultsType;
    private List<String> cfeResultsTypes;
    private String resultsPhene;
    private List<String> resultsPhenes;
    
    /* General */
    private List<String> diagnosisTypes;
    
    /* Discovery Cohort ------------------------------------------------------------- */
    private String discoveryPhene;
    private String discoveryPheneInfo;
    private String discoveryPheneTable;
    
    private double discoveryPheneLowCutoff;
    private double discoveryPheneHighCutoff;
    private Double discoveryCohortComparisonThreshold = DEFAULT_COMPARISON_THRESHOLD; 
   
    List<String> discoveryPheneList;
    
    Long discoveryCohortResultsId;
    
    private List<TableCheckInfo> tableCheckInfos;
    
    private int phenomicDatabaseErrorCount;
    private int phenomicDatabaseWarningCount;
    
    /* Discovery Scores ------------------------------------------------------------- */
    
    private PercentileScores discoveryPercentileScores = new PercentileScores();
    private String discoveryRScriptCommandFile;
    private String discoveryRScriptLogFile;
    
    Long discoveryScoresResultsId;
    
    /* Prioritization -------------------------------------------------------- */
    private String geneListSpecification;
    private GeneListInput geneListInput;
    private File geneListUpload;
    private String geneListUploadContentType;
    private String geneListUploadFileName;
    
    private Double prioritizationScoreCutoff;
    private Double prioritizationComparisonThreshold = DEFAULT_COMPARISON_THRESHOLD;
    
    private double huBrainScore;
    private double huPerScore;
    private double huGeneCnvScore;
    private double huGeneAssocScore;
    private double huGeneLinkageScore;
    
    private double nhBrainScore;
    private double nhPerScore;
    private double nhGeneCnvScore;
    private double nhGeneAssocScore;
    private double nhGeneLinkageScore;
    
    private File diseasesCsv;
    private String diseasesCsvContentType;
    private String diseasesCsvFileName;
    
    private List<DiseaseSelector> diseaseSelectors = new ArrayList<DiseaseSelector>();
    
    Long prioritizationScoresResultsId;
    
    /* Validation ------------------------------------------------------------ */
    private String[] operators = {">=", ">", "<=", "<"};
    	
	private String phene1;
	private String phene2;
	private String phene3;
	
	private String operator1;
	private String operator2;
	private String operator3;
	
	private String value1;
	private String value2;
	private String value3;

	private String percentInValidationCohort;
	
    private Double validationScoreCutoff = 6.0;
    //private Double validationComparisonThreshold = 0.0001; 
    private Double validationCohortComparisonThreshold = DEFAULT_COMPARISON_THRESHOLD;
    private Double validationScoresComparisonThreshold = DEFAULT_COMPARISON_THRESHOLD; 
    
    private double bonferroniScore  = 6;
    private double nominalScore     = 4;
    private double stepwiseScore    = 2;
    private double nonStepwiseScore = 0;

    private File updatedValidationMasterSheet;
    private String updatedValidationMasterSheetContentType;
    private String updatedValidationMasterSheetFileName;
    private String updatedValidationMasterSheetTempFileName;  // Need to create temp file for R-script to use
    
    private File updatedValidationPredictorList;
    private String updatedValidationPredictorListContentType;
    private String updatedValidationPredictorListFileName;
    private String updatedValidationPredictorListTempFileName;
    
    private Long validationCohortResultsId;
    private Long validationScoresResultsId;
    
    private String validationDiagnosisType;
    
    private String validationRScriptCommandFile;
    private String validationRScriptOutputFile;
    
    private boolean skipValidationSteps;


    /* Testing Cohorts --------------------------------------------------------------- */
    
    private File followUpDb;
    private String followUpDbContentType;
    private String followUpDbFileName;

    private String admissionPhene;
    
    private Long testingCohortsResultsId;
    
    private String testingCohortsPythonScriptCommandFile;
    private String testingCohortsPythonScriptOutputFile;
    
    /* Testing -Scores -------------------------------------------------------------- */
    private String testingDiagnosisType;
    
    private double testingScoreCutoff = 8.0;
    private Double testingComparisonThreshold = DEFAULT_COMPARISON_THRESHOLD;
    
    private List<String> admissionReasons;
    
    private File updatedTestingMasterSheet;
    private String updatedTestingMasterSheetContentType;
    private String updatedTestingMasterSheetFileName;
    private String updatedTestingMasterSheetTempFileName;  // Need to create temp file for R-script to use
    
    private File updatedTestingPredictorList;
    private String updatedTestingPredictorListContentType;
    private String updatedTestingPredictorListFileName;
    private String updatedTestingPredictorListTempFileName;
    
    private boolean stateCrossSectional;
    private boolean stateLongitudinal;
    private boolean firstYearCrossSectional;
    private boolean firstYearLongitudinal;
    private boolean futureCrossSectional;
    private boolean futuretLongitudinal;
    
    private String predictionPhene;
    private Double predictionPheneHighCutoff;
    private Double predictionPheneComparisonThreshold = DEFAULT_COMPARISON_THRESHOLD;
    
    private Double testingAllScore;
    private Double testingGenderScore;
    private Double testingGenderDiagnosisScore;

    
    // Script command and output files (for case where process fails)
    private String stateCrossSectionalRScriptCommandFile;
    private String stateCrossSectionalRScriptOutputFile;
    private String stateLongitudinalRScriptCommandFile;
    private String stateLongitudinalRScriptOutputFile;
    
    private String firstYearCrossSectionalRScriptCommandFile;
    private String firstYearCrossSectionalRScriptOutputFile;
    private String firstYearLongitudinalRScriptCommandFile;
    private String firstYearLongitudinalRScriptOutputFile;
    
    private String futureCrossSectionalRScriptCommandFile;
    private String futureCrossSectionalRScriptOutputFile;
    private String futureLongitudinalRScriptCommandFile;
    private String futureLongitudinalRScriptOutputFile;       
    
    private Long testingScoresResultsId;

    
    
    
    
    public BatchAction() {
        this.setCurrentTab("CFE Pipeline");
        this.setCurrentStep(1);
        
        this.testingAllScore             = 4.0;
        this.testingGenderScore          = 2.0;
        this.testingGenderDiagnosisScore = 1.0;
        
        admissionReasons = new ArrayList<String>();
        admissionReasons.add("Suicide");
        admissionReasons.add("Violence");
        admissionReasons.add("Depression");
        admissionReasons.add("Mania");
        admissionReasons.add("Hallucinations");
        admissionReasons.add("Delusion");
        admissionReasons.add("Other Psychosis"); 
        admissionReasons.add("Anxiety"); 
        admissionReasons.add("Stress");
        admissionReasons.add("Alcohol");
        admissionReasons.add("Drugs");
        admissionReasons.add("Pain");
        Collections.sort(admissionReasons);
        
        this.cfeResultsTypes = new ArrayList<String>();
        this.cfeResultsTypes.add("ALL");
        this.cfeResultsType = "ALL";
        this.cfeResultsTypes.addAll( CfeResultsType.getStartTypes() );
        
        this.resultsPhene = "ALL";
        this.resultsPhenes = new ArrayList<String>();
        this.resultsPhenes.add("ALL");
        this.resultsPhenes.addAll( CfeResultsService.getPhenes() );
        
        this.tableCheckInfos = new ArrayList<TableCheckInfo>();
    }
    
    public void withSession(Map<String, Object> session) {
	    this.webSession = session;    
	}
	
    /**
     * Initializes the batch processing interface.
     * @return
     * @throws Exception
     */
	public String initialize() throws Exception {
		String result = SUCCESS;
		
        this.setCurrentStep(1);
        
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else {

	        this.startingResultsList = CfeResultsService.getMetadata(
	                CfeResultsType.DISCOVERY_COHORT,
	                CfeResultsType.DISCOVERY_SCORES,
	                CfeResultsType.PRIORITIZATION_SCORES,
	                CfeResultsType.TESTING_COHORTS,
	                CfeResultsType.VALIDATION_COHORT,
	                CfeResultsType.VALIDATION_SCORES
	        );
	        Collections.sort(this.startingResultsList, new CfeResultsNewestFirstComparator());
	        
	        this.manualResultsTypeList = CfeResultsType.getCompleteTypes();
	        
	        this.endingResultsTypeList = CfeResultsType.getEndTypes();
		}
	    return result;
	}
	
	
	/**
	 * Uploads the testing database that contains the phene and phene visits information.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String uploadData() throws Exception {
		String result = SUCCESS;

		this.setCurrentStep(2);
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else if (this.testingDb == null || this.testingDbFileName == null) {
		    this.setErrorMessage("No testing database was specified.");
		    result = INPUT;
		}
		else if (!this.testingDbFileName.endsWith(".accdb")) {
		    this.setErrorMessage("Testing database file \"" + testingDbFileName
		            + "\" does not have MS Access database file extension \".accdb\".");
		    result = INPUT;
		}
		else {
		    try {
		        log.info("Testing database \"" + this.testingDbFileName + "\" uploaded.");
		        
		        // Set default disease selectors (for manually entered case)
                this.diseaseSelectors = DisorderService.getDiseaseSelectors();
	              
                // Copy the upload files to temporary files, because the upload files get deleted
                // and they are needed beyond this method
                
                // Testing database
                File testingDbTmp = FileUtil.createTempFile("testing-db-", ".accdb");
                FileUtils.copyFile(this.testingDb, testingDbTmp);
                this.testingDbTempFileName = testingDbTmp.getAbsolutePath();
                
                // Check the phenomic (testing) database
                this.tableCheckInfos = TableCheckInfo.checkTestingDatabase(testingDbTempFileName);
                this.phenomicDatabaseErrorCount   = TableCheckInfo.getErrorCount(tableCheckInfos);
                this.phenomicDatabaseWarningCount = TableCheckInfo.getWarningCount(tableCheckInfos);

                // Process testing database
                DiscoveryDatabaseParser dbParser = new DiscoveryDatabaseParser(this.testingDbTempFileName);
                dbParser.checkCoreTables();
                //this.pheneTables = dbParser.getPheneTables();
                this.discoveryPheneList = dbParser.getPheneList();
                this.genomicsTables = dbParser.getGenomicsTables();
                this.diagnosisCodes = dbParser.getDiagnosisCodes();
                this.diagnosisCodesList = new ArrayList<String>();
                this.diagnosisCodesList.add("All");
                this.diagnosisCodesList.addAll(diagnosisCodes.keySet());
                
                this.phenes = new ArrayList<String>();
                this.phenes.add("");
                this.phenes.addAll(this.discoveryPheneList);
		        
		        // Get diagnosis types
		        this.diagnosisTypes = DiagnosisType.getTypes();
                
		        this.showTestingInputs = false;
                
		        //----------------------------------------------------------------------------
		        // If starting results were specified, check that they exist and that the
		        // step of the starting results is before the step specified for the
		        // ending results.
		        //----------------------------------------------------------------------------
		        if (this.startingCfeResultsId != null) {
		            
		            log.info("STARTING RESULTS IS NOT NULL: " + this.startingCfeResultsId);
                    
		            //-----------------------------------------------------------
		            // If starting with manually created results
		            //-----------------------------------------------------------
                    if (this.startingCfeResultsId == BatchAction.MANUAL_RESULTS_START) {
                        
                        log.info("Starting with manually created results.");
                        
                        // Get the results spreadsheet
                        byte[] resultsSpreadsheet = FileUtils.readFileToByteArray(this.manualResultsSpreadsheet);
                        
                        //if (this.manualPheneInfoSource.equals("spreadsheet")) {
                            // If the user specified to get the discovery phene information
                            // from the uploaded results spreasheet
                            InputStream inputStream = new ByteArrayInputStream(resultsSpreadsheet);

                            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                            Triple<String, Double, Double> pheneNameLowHigh = CfeResultsWorkbook.getPheneInfo(workbook, this.manualResultsSpreadsheetFileName);
                            this.discoveryPheneInfo       = pheneNameLowHigh.getLeft();
                            this.discoveryPheneLowCutoff  = pheneNameLowHigh.getMiddle();
                            this.discoveryPheneHighCutoff = pheneNameLowHigh.getRight();
                            
                            this.manualResultsType = CfeResultsWorkbook.check(workbook, this.manualResultsSpreadsheetFileName);  // Do some validity checks
                            
                            this.startingResultsType = this.manualResultsType;
                            log.info("MANUAL STARTING RESULTS TYPE: " + this.startingResultsType);
                        //}

                        if (this.discoveryPheneInfo == null || this.discoveryPheneInfo.isEmpty()) {
                            throw new Exception("No discovery phene specified for manually created results upload.");
                        }
                        
                        if (!this.discoveryPheneInfo.contains(".")) {
                            throw new Exception("The specified discovery phene \"" + this.discoveryPheneInfo + "\" does not"
                                    + " contain a table name.");
                        }
                        
                        String[] pheneInfo = this.discoveryPheneInfo.split("\\.", 2);
                        this.discoveryPheneTable = pheneInfo[0].trim();
                        this.discoveryPhene = this.discoveryPheneInfo;
                        // Need to change to bracket format for search of phene
                        String searchPhene = "[" + pheneInfo[0] + "] " + pheneInfo[1];
                        
                        if (!this.discoveryPheneList.contains(searchPhene)) {
                            String message = "Phene \"" + this.discoveryPhene + "\" was not found in the testing database.";
                            //String validPhenes = String.join(", ", this.discoveryPheneList);
                            //validPhenes = validPhenes.substring(0, 100) + "...";
                            //message += " Valid phenes are: " + validPhenes + ".";
                            
                            throw new Exception(message);
                        }
                        
                        CfeResults manualResults = new CfeResults();

                        Date generatedTime = new Date();
                        manualResults.setGeneratedTime(generatedTime);
                        manualResults.setResultsType(this.manualResultsType);
                        manualResults.setResults(resultsSpreadsheet);

                        manualResults.setPhene(this.discoveryPhene);
                        manualResults.setLowCutoff(this.discoveryPheneLowCutoff);
                        manualResults.setHighCutoff(this.discoveryPheneHighCutoff);

                        manualResults.setUploaded(true);
                        
                        log.info("DISCOVERY PHENE LOW CUTOFF: " + this.discoveryPheneLowCutoff);
                        log.info("DISCOVERY PHENE HIGH CUTOFF: " + this.discoveryPheneHighCutoff);
                       
                        if (this.discoveryPheneLowCutoff > this.discoveryPheneHighCutoff) {
                            throw new Exception("The discovery phene low cutoff " + this.discoveryPheneLowCutoff + " is"
                                    + " greater than the high cutoff " + this.discoveryPheneHighCutoff);    
                        }
                        
                        CfeResultsService.save(manualResults);
                        
                        this.startingCfeResultsId = manualResults.getCfeResultsId();
                        log.info("Manually created results asigned ID of " + this.startingCfeResultsId + ".");
                    }		            
		            
		            if (this.startingCfeResultsId > 0) {
		                CfeResults startingResults = CfeResultsService.get(this.startingCfeResultsId);
		                if (startingResults == null) {
		                    String message = "Starting results with ID " + this.startingCfeResultsId + " could not be found.";
		                    log.severe(message);
		                    throw new Exception(message);
		                }
		                this.startingResultsType = startingResults.getResultsType();
		                CfeResultsType startingResultsTypeObj = new CfeResultsType(startingResultsType);

		                if (startingResultsTypeObj != null && !startingResultsTypeObj.isBefore(this.endingResultsType)) {
		                    String message = "The starting step \"" + this.startingResultsType + "\" is not"
		                            + " before the ending step \"" + this.endingResultsType + "\".";
		                    log.severe(message);
		                    throw new Exception(message);
		                }
		            }

		            //else if (this.startingCfeResultsId == BatchAction.TESTING_PHASE_START) {
		            //    this.startingResultsType = CfeResultsType.VALIDATION_SCORES;
	                //    this.showTestingInputs        = true;
	                //}
		        }

	               

		        if (this.startingResultsType != null) {
                    if (this.startingResultsType.equals(CfeResultsType.VALIDATION_COHORT) || this.startingResultsType.equals(CfeResultsType.VALIDATION_SCORES)) {
                        if (this.skipValidationSteps) {
                            throw new Exception("Illegal starting step of \"" + startingResultsType + "\" specified when skipping validation steps selected.");
                        }
                    }
		        }
                
	            this.showDiscoveryCohort      = CfeResultsType.typeIsInRange(CfeResultsType.DISCOVERY_COHORT, this.startingResultsType, this.endingResultsType);
	            this.showDiscoveryScores      = CfeResultsType.typeIsInRange(CfeResultsType.DISCOVERY_SCORES, this.startingResultsType, this.endingResultsType);
	            this.showPrioritizationScores = CfeResultsType.typeIsInRange(CfeResultsType.PRIORITIZATION_SCORES, this.startingResultsType, this.endingResultsType);
	            this.showValidationCohort     = CfeResultsType.typeIsInRange(CfeResultsType.VALIDATION_COHORT, this.startingResultsType, this.endingResultsType);
	            this.showValidationScores     = CfeResultsType.typeIsInRange(CfeResultsType.VALIDATION_SCORES, this.startingResultsType, this.endingResultsType);
	            this.showTestingCohorts       = CfeResultsType.typeIsInRange(CfeResultsType.TESTING_COHORTS, this.startingResultsType, this.endingResultsType); 
	            this.showTestingScores        = CfeResultsType.typeIsInRange(CfeResultsType.TESTING_SCORES, this.startingResultsType, this.endingResultsType); 

	               
	            /*
		        pastCfeResultsMap = new LinkedHashMap<Long, String>();
		        pastCfeResultsMap.put(0L, "");
		        
		        List<CfeResults> pastCfeResults = CfeResultsService.getMetadata(
		                CfeResultsType.DISCOVERY_COHORT,
		                CfeResultsType.DISCOVERY_SCORES,
		                CfeResultsType.PRIORITIZATION_SCORES,
		                CfeResultsType.VALIDATION_COHORT,
		                CfeResultsType.VALIDATION_SCORES,
		                CfeResultsType.TESTING_COHORTS
		        );
		        Collections.sort(pastCfeResults, new CfeResultsNewestFirstComparator());
		          
		        for (CfeResults cfeResults: pastCfeResults) {
		            Long key = cfeResults.getCfeResultsId();
		            String value = "["  + cfeResults.getCfeResultsId() + "]";
		            value += " " + cfeResults.getResultsType();
		            value += " (" + cfeResults.getGeneratedTime() + ")";
		            value += " " + cfeResults.getLowCutoff() + " <=";
		            value += " " + cfeResults.getPhene() + " <= " + cfeResults.getHighCutoff();
		            pastCfeResultsMap.put(key, value);
		        }
		        */
		        	    
		    } catch (Exception exception) {
		        this.setErrorMessage("The uploads could not be processed. " + exception.getLocalizedMessage());
                String stackTrace = ExceptionUtils.getStackTrace(exception);
                this.setExceptionStack(stackTrace);
                log.severe(exception.getMessage());
                log.severe(stackTrace);
		        result = ERROR;
		    }
		}
	    return result;
	}
	
	/**
	 * Processes all inputs and calculates all score results.
	 * @return
	 * @throws Exception
	 */
    public String calculate() throws Exception {
        String result = SUCCESS;
        
        log.info("START of BatchAction calculate method.");
        
        this.setCurrentStep(3);
        
        if (!Authorization.isAdmin(webSession)) {
            result = LOGIN;
        }
        else {
            try { 
                String[] pheneInfo = null;

                //----------------------------------------
                // Get starting results (if any)
                //----------------------------------------
                CfeResultsType startingResultsTypeObj = null;
                CfeResults startingResults = null;
                
                if (this.startingCfeResultsId != null && this.startingCfeResultsId > 0) {
                    startingResults = CfeResultsService.get(startingCfeResultsId);
                    if (startingResults == null) {
                        throw new Exception("Starting results with ID " + startingCfeResultsId + " could not be found.");
                    }
                    
                    log.info("STARTING RESULTS PHENE: " + startingResults.getPhene());
                    pheneInfo = startingResults.getPhene().split("\\.", 2);
                    this.discoveryPheneLowCutoff  = startingResults.getLowCutoff();
                    this.discoveryPheneHighCutoff = startingResults.getHighCutoff();
                    log.info("PHENE INFO from starting results with ID: " + startingCfeResultsId);
                   
                    startingResultsTypeObj = new CfeResultsType(startingResults.getResultsType());
                }
                else {
                    startingResultsType = CfeResultsType.NONE;
                    startingResultsTypeObj = new CfeResultsType(startingResultsType);
                    // No starting results, so get phene info from input fields
                    pheneInfo = this.discoveryPheneInfo.split("]", 2);
                    log.info("PHENE INFO from input field");
                }
                
                if (pheneInfo.length > 1) {
                    log.info("PHENE INFO (2): " + pheneInfo[0] + " | " + pheneInfo[1]);
                }
                else if (pheneInfo.length == 1) {
                    log.info("PHENE INFO (1): " + pheneInfo[0]);
                }
                
                this.discoveryPheneTable = pheneInfo[0].replace('[', ' ').trim();
                this.discoveryPhene = discoveryPheneTable + "." + pheneInfo[1].trim();
                log.info("PHENE TABLE: " + this.discoveryPheneTable);
                log.info("PHENE: " + this.discoveryPhene);
                
                //CfeResultsType endingResultsTypeObj = new CfeResultsType(this.endingResultsType);

                
                        
                //=========================================================================
                // Create Discovery Cohort
                //=========================================================================
                CfeResults discoveryCohort = null;
                this.discoveryCohortResultsId = null;
                
                // Skip this step, if it is out of the range of the steps specified
                if (startingResultsTypeObj.isEqualTo(CfeResultsType.NONE)) {
                    log.info("Step " + CfeResultsType.DISCOVERY_COHORT + " started.");
                    DiscoveryCohortCalc discoveryCohortCalc = new DiscoveryCohortCalc();
                    discoveryCohort = discoveryCohortCalc.calculate(
                            testingDbTempFileName,
                            testingDbFileName,
                            discoveryPhene,
                            discoveryPheneTable,
                            this.discoveryPheneLowCutoff,
                            this.discoveryPheneHighCutoff,
                            this.genomicsTable,
                            this.discoveryCohortComparisonThreshold
                    );
                    
                    this.discoveryCohortResultsId = discoveryCohort.getCfeResultsId();
                    log.info("Step " + CfeResultsType.DISCOVERY_COHORT + " completed;"
                            + " discoveryCohortResultsId = " + this.discoveryCohortResultsId + ".");
                }
                else {
                    if (startingResultsTypeObj != null && startingResultsTypeObj.isEqualTo(CfeResultsType.DISCOVERY_COHORT)) {
                        discoveryCohort = startingResults;       
                        this.discoveryCohortResultsId = discoveryCohort.getCfeResultsId();
                    }
                    log.info("Step " + CfeResultsType.DISCOVERY_COHORT + " skipped.");
                }
                
                // Delete the testing database temporary file
                if (testingDbTempFileName != null) {
				    File testingDbFile = new File(testingDbTempFileName);
				    boolean fileDeleted = testingDbFile.delete();
				    if (fileDeleted) {
					    log.info(
					        "Temporary file \"" + testingDbTempFileName
					        + "\" for testing database \"" + testingDbFileName + "\" deleted."
					    );
					}
					else {
						log.warning(
						    "Unable to delete temporary file \"" + testingDbTempFileName
						    + "\" for testing datbase \"" + testingDbFileName + "\"."
						);
					}
				} 
                

                //=========================================================================
                // Calculate Discovery Scores
                //=========================================================================
                CfeResults discoveryScores = null;
                
                // Skip this step, if it is out of the range of the steps specified
                if (CfeResultsType.typeIsInRange(CfeResultsType.DISCOVERY_SCORES, this.startingResultsType,  this.endingResultsType)) {
                    log.info("Step " + CfeResultsType.DISCOVERY_SCORES + " started.");
                    
                    DiscoveryScoresCalc discoveryScoresCalc = new DiscoveryScoresCalc();

                    try {
                        discoveryScores = discoveryScoresCalc.calculate(
                            discoveryCohort,
                            this.discoveryGeneExpressionCsv,
                            this.probesetToGeneMappingDb,
                            this.probesetToGeneMappingDbFileName,
                            diagnosisCode,
                            this.discoveryPercentileScores,
                            debugDiscoveryScoring
                                );
                    }
                    catch (Exception exception) {
                        // Something went wrong. Get R script command and output if available.
                        if (discoveryScoresCalc != null) {
                            this.discoveryRScriptCommandFile = FileUtil.createTempFile(
                                    "discovery-r-script-command-", ".txt", discoveryScoresCalc.getDiscoveryScoringCommand()
                            );
                            this.discoveryRScriptLogFile = FileUtil.createTempFile(
                                "discovery-r-script-output-", ".txt", discoveryScoresCalc.getScriptOutput()
                            );
                        }
                        throw new Exception("Discovery scoring error: " + exception.getLocalizedMessage(), exception);
                    }


                    if (discoveryScores == null) {
                        throw new Exception("Discovery scores could not be calculated.");
                    }
                    this.discoveryScoresResultsId = discoveryScores.getCfeResultsId();
                    log.info("Step " + CfeResultsType.DISCOVERY_SCORES + " completed.");
                }
                else {
                    if (startingResultsTypeObj != null && startingResultsTypeObj.isEqualTo(CfeResultsType.DISCOVERY_SCORES)) {
                        discoveryScores = startingResults;       
                        this.discoveryScoresResultsId = discoveryScores.getCfeResultsId();
                    }
                    log.info("Step " + CfeResultsType.DISCOVERY_SCORES + " skipped.");
                }
                
                //=========================================================================
                // Calculate Prioritization Scores
                //=========================================================================

                CfeResults prioritizationScores = null;
                
                // Skip this step, if it is out of the range of the steps specified
                if (CfeResultsType.typeIsInRange(CfeResultsType.PRIORITIZATION_SCORES, this.startingResultsType,  this.endingResultsType)) {
                    log.info("Step " + CfeResultsType.PRIORITIZATION_SCORES + " started.");
                    
                    // Process the gene list
                    if (this.geneListSpecification.contentEquals("All")) {
                        this.geneListUploadFileName = "";
                        this.geneListInput = new GeneListInput();
                    }
                    else if (this.geneListSpecification.contentEquals("Upload File:")) {
                        // FIX!!!!!!!!!! - NOT CORRECT FILE NAME HERE - need the path????????????????
                        this.geneListInput = new GeneListInput(this.geneListUpload.getAbsolutePath());
                    }
                    else if (this.geneListSpecification.contentEquals("Generate from Discovery:")) {
                        this.geneListUploadFileName = "";
                        this.geneListInput = new GeneListInput(discoveryScores, this.prioritizationScoreCutoff, this.prioritizationComparisonThreshold);
                    }
                    else {
                        result = INPUT;
                        throw new Exception("Gene list specification\"" + this.geneListSpecification + "\" is invalid.");
                    }

                    // If a diseases CSV File was specified, then use it
                    // Otherwise, use the manusally entered values
                    if (diseasesCsv != null) {
                        log.info("diseasesCsv set, diseasesCsvFileName = \"" + diseasesCsvFileName + "\"");
                        this.diseaseSelectors = DiseaseSelector.importCsvFile(diseasesCsvFileName, diseasesCsv);
                    }
                    
                    List<cfe.enums.prioritization.ScoringWeights> weights = this.getPrioritizationWeights();

                    PrioritizationScoresCalc prioritizationScoresCalc = new PrioritizationScoresCalc();

                    log.info("**** DISEASE SELECTORS COUNT before calculate: " + this.diseaseSelectors.size());
                    
                    prioritizationScores = prioritizationScoresCalc.calculate(
                            discoveryScores,
                            diseaseSelectors,
                            weights,
                            geneListInput,
                            this.geneListUploadFileName,
                            this.prioritizationScoreCutoff
                            );

                    this.prioritizationScoresResultsId = prioritizationScores.getCfeResultsId();
                    log.info("Step " + CfeResultsType.PRIORITIZATION_SCORES + " completed.");
                }
                else {
                    if (startingResultsTypeObj != null && startingResultsTypeObj.isEqualTo(CfeResultsType.PRIORITIZATION_SCORES)) {
                        prioritizationScores = startingResults;       
                        this.prioritizationScoresResultsId = prioritizationScores.getCfeResultsId();
                    }
                    log.info("Step " + CfeResultsType.PRIORITIZATION_SCORES + " skipped.");
                }
                
                
                //================================================================================
                // Create Validation Cohort
                //================================================================================
                
                
                CfeResults validationCohort = null;

                if (!this.skipValidationSteps) {
                    // Skip this step, if it is out of the range of the steps specified
                    if (CfeResultsType.typeIsInRange(CfeResultsType.VALIDATION_COHORT, this.startingResultsType,  this.endingResultsType)) {

                        log.info("Step " + CfeResultsType.VALIDATION_COHORT + " started.");

                        phene1 = phene1.replace("[", "").replace("] ", ".");
                        phene2 = phene2.replace("[", "").replace("] ", ".");
                        phene3 = phene3.replace("[", "").replace("] ", ".");
                        List<PheneCondition> pheneConditions = PheneCondition.createList(
                                phene1, operator1, value1,
                                phene2, operator2, value2,
                                phene3, operator3, value3
                                );

                        double percentInValidation = Double.parseDouble(this.percentInValidationCohort) / 100.0;

                        ValidationCohortCalc validationCohortCalc = new ValidationCohortCalc();

                        validationCohort = validationCohortCalc.calculate(
                                prioritizationScores,
                                pheneConditions,
                                percentInValidation,
                                this.validationCohortComparisonThreshold
                                );

                        this.validationCohortResultsId = validationCohort.getCfeResultsId();
                        log.info("Step " + CfeResultsType.VALIDATION_COHORT + " completed.");
                    }
                    else {
                        if (startingResultsTypeObj != null && startingResultsTypeObj.isEqualTo(CfeResultsType.VALIDATION_COHORT)) {
                            validationCohort = startingResults;       
                            this.validationCohortResultsId = validationCohort.getCfeResultsId();
                        }
                        log.info("Step " + CfeResultsType.VALIDATION_COHORT + " skipped.");
                    }
                }
                
             
                //===============================================================
                // Calculate Validation Scores
                //===============================================================

                CfeResults validationScores = null;
                
                if (!this.skipValidationSteps) {
                    // Skip this step, if it is out of the range of the steps specified
                    if (CfeResultsType.typeIsInRange(CfeResultsType.VALIDATION_SCORES, this.startingResultsType,  this.endingResultsType)) {
                        log.info("Step " + CfeResultsType.VALIDATION_SCORES + " started.");
                        ValidationScoresCalc validationScoresCalc = new ValidationScoresCalc();

                        log.info("Validation score cutoff: " + this.validationScoreCutoff);
                        log.info("Validation scores comparison threshold: " + this.validationScoresComparisonThreshold);

                        try {
                            validationScores = validationScoresCalc.calculate(
                                    validationCohort,
                                    this.validationScoreCutoff,
                                    this.validationScoresComparisonThreshold,
                                    this.bonferroniScore,
                                    this.nominalScore,
                                    this.stepwiseScore,
                                    this.nonStepwiseScore,
                                    this.updatedValidationMasterSheet,
                                    this.updatedValidationMasterSheetFileName,
                                    this.updatedValidationPredictorList,
                                    this.updatedValidationPredictorListFileName,
                                    this.validationDiagnosisType,
                                    this.geneExpressionCsv
                            );
                        }
                        catch (Exception exception) {
                            // Something went wrong. Get R script command and output if available.
                            if (validationScoresCalc != null) {
                                this.validationRScriptCommandFile = FileUtil.createTempFile(
                                        "validation-r-script-command-", ".txt", validationScoresCalc.getValidationScoringCommand()
                                        );
                                this.validationRScriptOutputFile = FileUtil.createTempFile(
                                        "validation-r-script-output-", ".txt", validationScoresCalc.getScriptOutput()
                                        );
                            }
                            throw new Exception("Validation scoring error: " + exception.getLocalizedMessage(), exception);
                        }

                        this.validationScoresResultsId = validationScores.getCfeResultsId();
                        log.info("Step " + CfeResultsType.VALIDATION_SCORES + " completed.");
                    }
                    else {
                        if (startingResultsTypeObj != null && startingResultsTypeObj.isEqualTo(CfeResultsType.VALIDATION_SCORES)) {
                            validationScores = startingResults;       
                            this.validationScoresResultsId = validationScores.getCfeResultsId();
                        }
                        log.info("Step " + CfeResultsType.VALIDATION_SCORES + " skipped.");
                    }
                }
                
                
                //================================================================================
                // Create Testing Cohorts
                //================================================================================
                
                CfeResults testingCohorts = null;
                
                // Skip this step, if it is out of the range of the steps specified
                if (CfeResultsType.typeIsInRange(CfeResultsType.TESTING_COHORTS, this.startingResultsType,  this.endingResultsType)) {
                    log.info("Step " + CfeResultsType.TESTING_COHORTS + " started.");
                    TestingCohortsCalc testingCohortsCalc = new TestingCohortsCalc();
                    
                    try {
                        if (this.skipValidationSteps) {
                            // If the validation steps are being skipped, use the discovery scores
                            testingCohorts = testingCohortsCalc.calculate(
                                    prioritizationScores,
                                    this.followUpDb,
                                    this.followUpDbFileName,
                                    this.admissionPhene
                                );                            
                        }
                        else {
                            testingCohorts = testingCohortsCalc.calculate(
                                validationScores,
                                this.followUpDb,
                                this.followUpDbFileName,
                                this.admissionPhene
                            );
                        }
                    }
                    catch (Exception exception) {
                        // Something went wrong. Get R script command and output if available.
                        if (testingCohortsCalc != null) {
                            this.testingCohortsPythonScriptCommandFile = FileUtil.createTempFile(
                                    "testing-cohorts-python-script-command-", ".txt", testingCohortsCalc.getPredictionCohortCreationCommand()
                            );
                            this.testingCohortsPythonScriptOutputFile = FileUtil.createTempFile(
                                "testing-cohorts-python-script-output-", ".txt", testingCohortsCalc.getScriptOutput()
                            );
                        }
                        throw new Exception("Testing cohorts creation error: " + exception.getLocalizedMessage(), exception);
                    }
                    
                    if (testingCohorts == null) {
                        throw new Exception("Testing cohorts could not be calculated.");
                    }
                    
                    this.testingCohortsResultsId = testingCohorts.getCfeResultsId();
                    log.info("Step " + CfeResultsType.TESTING_COHORTS + " completed.");
                }
                else {
                    if (startingResultsTypeObj != null && startingResultsTypeObj.isEqualTo(CfeResultsType.TESTING_COHORTS)) {
                        testingCohorts = startingResults;       
                        this.testingCohortsResultsId = testingCohorts.getCfeResultsId();
                    }
                    log.info("Step " + CfeResultsType.TESTING_COHORTS + " skipped.");
                }

                
                //===============================================================
                // Calculate Testing Scores
                //===============================================================
                CfeResults testingScores = null;
                
                // Skip this step, if it is out of the range of the steps specified
                if (CfeResultsType.typeIsInRange(CfeResultsType.TESTING_SCORES, this.startingResultsType,  this.endingResultsType)) {
                    log.info("Step " + CfeResultsType.TESTING_SCORES + " started.");
                    TestingScoresCalc testingScoresCalc = new TestingScoresCalc();
                    
                    if (this.predictionPhene != null && !this.predictionPhene.trim().isEmpty()) {
                        this.predictionPhene = this.predictionPhene.trim();
                        String[] predictionPheneInfo = this.predictionPhene.split("]", 2);
                        String predictionPheneTable = predictionPheneInfo[0].replace('[', ' ').trim();
                        this.predictionPhene = predictionPheneTable + "." + predictionPheneInfo[1].trim();
                    }
                
                    try {
                        testingScores = testingScoresCalc.calculate(
                            testingCohorts,
                            this.testingScoreCutoff,
                            this.testingComparisonThreshold,
                            this.geneExpressionCsv,
                            this.geneExpressionCsvFileName,
                            this.updatedTestingPredictorList,
                            this.updatedTestingPredictorListFileName,
                            this.updatedTestingMasterSheet,
                            this.updatedTestingMasterSheetFileName,
                            this.stateCrossSectional,
                            this.stateLongitudinal,
                            this.firstYearCrossSectional,
                            this.firstYearLongitudinal,
                            this.futureCrossSectional,
                            this.futuretLongitudinal,
                            this.predictionPhene,
                            this.predictionPheneHighCutoff,
                            this.predictionPheneComparisonThreshold,
                            this.testingDiagnosisType,
                            this.testingAllScore,
                            this.testingGenderScore,
                            this.testingGenderDiagnosisScore
                        );
                    }
                    catch (Exception exception) {
                        if (testingScoresCalc != null) {
                            
                            // STATE CROSS-SECTIONAL
                            this.stateCrossSectionalRScriptCommandFile = FileUtil.createTempFile(
                                "state-cross-sectional-r-script-command-", ".txt", testingScoresCalc.getrCommandStateCrossSectional()
                            );
                            this.stateCrossSectionalRScriptOutputFile = FileUtil.createTempFile(
                                "state-cross-sectional-r-script-output-", ".txt", testingScoresCalc.getrScriptOutputFileStateCrossSectional()
                            );
                            
                            // STATE LONGITUDINAL
                            this.stateLongitudinalRScriptCommandFile = FileUtil.createTempFile(
                                "state-longitudinal-r-script-command-", ".txt", testingScoresCalc.getrCommandStateLongitudinal()
                            );
                            this.stateLongitudinalRScriptOutputFile = FileUtil.createTempFile(
                                "state-longitudinal-r-script-output-", ".txt", testingScoresCalc.getrScriptOutputFileStateLongitudinal()
                            );
                            
                            //--------------------------------------------------------------------------------------------------
                            
                            // FIRST YEAR CROSS-SECTIONAL
                            this.firstYearCrossSectionalRScriptCommandFile = FileUtil.createTempFile(
                                "first-year-cross-sectional-r-script-command-", ".txt", testingScoresCalc.getrCommandFirstYearCrossSectional()
                            );
                            this.firstYearCrossSectionalRScriptOutputFile = FileUtil.createTempFile(
                                "first-year-cross-sectional-r-script-output-", ".txt", testingScoresCalc.getrScriptOutputFileFirstYearCrossSectional()
                            );
                            
                            // FIRST YEAR LONGITUDINAL
                            this.firstYearLongitudinalRScriptCommandFile = FileUtil.createTempFile(
                                "first-year-longitudinal-r-script-command-", ".txt", testingScoresCalc.getrCommandFirstYearLongitudinal()
                            );
                            this.firstYearLongitudinalRScriptOutputFile = FileUtil.createTempFile(
                                "first-year-longitudinal-r-script-output-", ".txt", testingScoresCalc.getrScriptOutputFileFirstYearLongitudinal()
                            );
                            
                            //--------------------------------------------------------------------------------------------------
                            
                            // FUTURE CROSS-SECTIONAL
                            this.futureCrossSectionalRScriptCommandFile = FileUtil.createTempFile(
                                "future-cross-sectional-r-script-command-", ".txt", testingScoresCalc.getrCommandFutureCrossSectional()
                            );
                            this.futureCrossSectionalRScriptOutputFile = FileUtil.createTempFile(
                                "future-cross-sectional-r-script-output-", ".txt", testingScoresCalc.getrScriptOutputFileFutureCrossSectional()
                            );
                            
                            // FUTURE LONGITUDINAL
                            this.futureLongitudinalRScriptCommandFile = FileUtil.createTempFile(
                                "future-longitudinal-r-script-command-", ".txt", testingScoresCalc.getrCommandFutureLongitudinal()
                            );
                            this.futureLongitudinalRScriptOutputFile = FileUtil.createTempFile(
                                "future-longitudinal-r-script-output-", ".txt", testingScoresCalc.getrScriptOutputFileFutureLongitudinal()
                            );
                                                                                        
                        }
                        throw new Exception("Testing scoring error: " + exception.getLocalizedMessage(), exception);   
                    }
                    
                    if (testingScores == null) {

                        throw new Exception("Testing scores could not be calculated.");
                    }
                    
                    this.testingScoresResultsId = testingScores.getCfeResultsId();
                    log.info("Step " + CfeResultsType.TESTING_SCORES + " completed.");
                }
                else {
                    if (startingResultsTypeObj != null && startingResultsTypeObj.isEqualTo(CfeResultsType.TESTING_SCORES)) {
                        testingScores = startingResults;       
                        this.testingScoresResultsId = testingScores.getCfeResultsId();
                    }
                    log.info("Step " + CfeResultsType.TESTING_SCORES + " skipped.");
                }
            }
            catch (Exception exception) {
                this.setErrorMessage("The input data could not be processed. " + exception.getLocalizedMessage());
                String stackTrace = ExceptionUtils.getStackTrace(exception);
                this.setExceptionStack(stackTrace);
                if (result == SUCCESS) {
                    result = ERROR;
                }
            }
        }
        return result;
    }

    public List<cfe.enums.prioritization.ScoringWeights> getPrioritizationWeights() {
        List<cfe.enums.prioritization.ScoringWeights> weights = new ArrayList<cfe.enums.prioritization.ScoringWeights>();
        cfe.enums.prioritization.ScoringWeights weight;

        weight = cfe.enums.prioritization.ScoringWeights.HUBRAIN;
        weight.setScore( huBrainScore );
        weights.add(weight);

        weight = cfe.enums.prioritization.ScoringWeights.HUPER;
        weight.setScore( huPerScore );
        weights.add(weight);

        weight = cfe.enums.prioritization.ScoringWeights.HUGENEASSOC;
        weight.setScore( huGeneAssocScore );
        weights.add(weight);

        weight = cfe.enums.prioritization.ScoringWeights.HUGCNV;
        weight.setScore( huGeneCnvScore );
        weights.add(weight);


        weight = cfe.enums.prioritization.ScoringWeights.NHBRAIN;
        weight.setScore( nhBrainScore );
        weights.add(weight);

        weight = cfe.enums.prioritization.ScoringWeights.NHPER;
        weight.setScore( nhPerScore );
        weights.add(weight);

        weight = cfe.enums.prioritization.ScoringWeights.NHGENEASSOC;
        weight.setScore( nhGeneAssocScore );
        weights.add(weight);

        weight = cfe.enums.prioritization.ScoringWeights.NHGCNV;
        weight.setScore( nhGeneCnvScore );
        weights.add(weight);
        
        return weights;
    }

    public String getManualPheneInfoSource() {
        return manualPheneInfoSource;
    }

    public void setManualPheneInfoSource(String manualPheneInfoSource) {
        this.manualPheneInfoSource = manualPheneInfoSource;
    }

    public boolean isShowDiscoveryCohort() {
        return showDiscoveryCohort;
    }

    public void setShowDiscoveryCohort(boolean showDiscoveryCohort) {
        this.showDiscoveryCohort = showDiscoveryCohort;
    }

    public boolean isShowDiscoveryScores() {
        return showDiscoveryScores;
    }

    public void setShowDiscoveryScores(boolean showDiscoveryScores) {
        this.showDiscoveryScores = showDiscoveryScores;
    }

    public boolean isShowPrioritizationScores() {
        return showPrioritizationScores;
    }

    public void setShowPrioritizationScores(boolean showPrioritizationScores) {
        this.showPrioritizationScores = showPrioritizationScores;
    }

    public boolean isShowValidationCohort() {
        return showValidationCohort;
    }

    public void setShowValidationCohort(boolean showValidationCohort) {
        this.showValidationCohort = showValidationCohort;
    }

    public boolean isShowValidationScores() {
        return showValidationScores;
    }

    public void setShowValidationScores(boolean showValidationScores) {
        this.showValidationScores = showValidationScores;
    }
    
    public boolean isShowTestingInputs() {
        return showTestingInputs;
    }

    public void setShowTestingInputs(boolean showTestingInputs) {
        this.showTestingInputs = showTestingInputs;
    }

    public boolean isShowTestingCohorts() {
        return showTestingCohorts;
    }

    public void setShowTestingCohorts(boolean showTestingCohorts) {
        this.showTestingCohorts = showTestingCohorts;
    }

    public boolean isShowTestingScores() {
        return showTestingScores;
    }

    public void setShowTestingScores(boolean showTestingScores) {
        this.showTestingScores = showTestingScores;
    }


    // Diagnosis Types 

    public List<String> getDiagnosisTypes() {
        return diagnosisTypes;
    }

    public void setDiagnosisTypes(List<String> diagnosisTypes) {
        this.diagnosisTypes = diagnosisTypes;
    }
    
    
    public Map<Long, String> getPastCfeResultsMap() {
        return pastCfeResultsMap;
    }

    public void setPastCfeResultsMap(Map<Long, String> pastCfeResultsMap) {
        this.pastCfeResultsMap = pastCfeResultsMap;
    }
    
    public Long getStartingCfeResultsId() {
        return startingCfeResultsId;
    }

    public void setStartingCfeResultsId(Long startingCfeResultsId) {
        this.startingCfeResultsId = startingCfeResultsId;
    }

    
    public List<CfeResults> getStartingResultsList() {
        return startingResultsList;
    }

    public void setStartingResultsList(List<CfeResults> startingResultsList) {
        this.startingResultsList = startingResultsList;
    }

    public String getStartingResultsType() {
        return startingResultsType;
    }

    public void setStartingResultsType(String startingResultsType) {
        this.startingResultsType = startingResultsType;
    }

    
    
    public List<String> getManualResultsTypeList() {
        return manualResultsTypeList;
    }

    public void setManualResultsTypeList(List<String> manualResultsTypeList) {
        this.manualResultsTypeList = manualResultsTypeList;
    }

    public String getManualResultsType() {
        return manualResultsType;
    }

    public void setManualResultsType(String manualResultsType) {
        this.manualResultsType = manualResultsType;
    }
    
    
    public File getManualResultsSpreadsheet() {
        return manualResultsSpreadsheet;
    }

    public void setManualResultsSpreadsheet(File manualResultsSpreadsheet) {
        this.manualResultsSpreadsheet = manualResultsSpreadsheet;
    }

    public String getManualResultsSpreadsheetFileName() {
        return manualResultsSpreadsheetFileName;
    }

    public void setManualResultsSpreadsheetFileName(String manualResultsSpreadsheetFileName) {
        this.manualResultsSpreadsheetFileName = manualResultsSpreadsheetFileName;
    }

    public String getManualResultsSpreadsheetContentType() {
        return manualResultsSpreadsheetContentType;
    }

    public void setManualResultsSpreadsheetContentType(String manualResultsSpreadsheetContentType) {
        this.manualResultsSpreadsheetContentType = manualResultsSpreadsheetContentType;
    }

    
    public List<String> getEndingResultsTypeList() {
        return endingResultsTypeList;
    }

    public void setEndingResultsTypeList(List<String> endingResultsTypeList) {
        this.endingResultsTypeList = endingResultsTypeList;
    }
    
    public String getEndingResultsType() {
        return endingResultsType;
    }

    public void setEndingResultsType(String endingResultsType) {
        this.endingResultsType = endingResultsType;
    }

    
    public File getTestingDb() {
        return testingDb;
    }

    public void setTestingDb(File testingDb) {
        this.testingDb = testingDb;
    }

    public String getTestingDbContentType() {
        return testingDbContentType;
    }

    public void setTestingDbContentType(String testingDbContentType) {
        this.testingDbContentType = testingDbContentType;
    }

    public String getTestingDbFileName() {
        return testingDbFileName;
    }

    public void setTestingDbFileName(String testingDbFileName) {
        this.testingDbFileName = testingDbFileName;
    }

    public String getTestingDbTempFileName() {
        return testingDbTempFileName;
    }

    public void setTestingDbTempFileName(String testingDbTempFileName) {
        this.testingDbTempFileName = testingDbTempFileName;
    }

    public List<TableCheckInfo> getTableCheckInfos() {
        return tableCheckInfos;
    }

    public void setTableCheckInfos(List<TableCheckInfo> tableCheckInfos) {
        this.tableCheckInfos = tableCheckInfos;
    }
    
    public int getPhenomicDatabaseErrorCount() {
        return phenomicDatabaseErrorCount;
    }

    public void setPhenomicDatabaseErrorCount(int phenomicDatabaseErrorCount) {
        this.phenomicDatabaseErrorCount = phenomicDatabaseErrorCount;
    }

    public int getPhenomicDatabaseWarningCount() {
        return phenomicDatabaseWarningCount;
    }

    public void setPhenomicDatabaseWarningCount(int phenomicDatabaseWarningCount) {
        this.phenomicDatabaseWarningCount = phenomicDatabaseWarningCount;
    }

    //------------------------------------
    // Testing Input Spreadsheet
    //------------------------------------
    public File getTestingInputSpreadsheet() {
        return testingInputSpreadsheet;
    }

    public void setTestingInputSpreadsheet(File testingInputSpreadsheet) {
        this.testingInputSpreadsheet = testingInputSpreadsheet;
    }

    public String getTestingInputSpreadsheetContentType() {
        return testingInputSpreadsheetContentType;
    }

    public void setTestingInputSpreadsheetContentType(String testingInputSpreadsheetContentType) {
        this.testingInputSpreadsheetContentType = testingInputSpreadsheetContentType;
    }

    public String getTestingInputSpreadsheetFileName() {
        return testingInputSpreadsheetFileName;
    }

    public void setTestingInputSpreadsheetFileName(String testingInputSpreadsheetFileName) {
        this.testingInputSpreadsheetFileName = testingInputSpreadsheetFileName;
    }

    public String getTestingInputSpreadsheetTempFileName() {
        return testingInputSpreadsheetTempFileName;
    }

    public void setTestingInputSpreadsheetTempFileName(String testingInputSpreadsheetTempFileName) {
        this.testingInputSpreadsheetTempFileName = testingInputSpreadsheetTempFileName;
    }
    
    
    
    
    public File getProbesetToGeneMappingDb() {
        return probesetToGeneMappingDb;
    }

    public void setProbesetToGeneMappingDb(File probesetToGeneMappingDb) {
        this.probesetToGeneMappingDb = probesetToGeneMappingDb;
    }

    public String getProbesetToGeneMappingDbContentType() {
        return probesetToGeneMappingDbContentType;
    }

    public void setProbesetToGeneMappingDbContentType(String probesetToGeneMappingDbContentType) {
        this.probesetToGeneMappingDbContentType = probesetToGeneMappingDbContentType;
    }

    public String getProbesetToGeneMappingDbFileName() {
        return probesetToGeneMappingDbFileName;
    }

    public void setProbesetToGeneMappingDbFileName(String probesetToGeneMappingDbFileName) {
        this.probesetToGeneMappingDbFileName = probesetToGeneMappingDbFileName;
    }

    /*
    public String getProbesetToGeneMappingDbTempFileName() {
        return probesetToGeneMappingDbTempFileName;
    }

    public void setProbesetToGeneMappingDbTempFileName(String probesetToGeneMappingDbTempFileName) {
        this.probesetToGeneMappingDbTempFileName = probesetToGeneMappingDbTempFileName;
    }
    */
    
    public File getDiscoveryGeneExpressionCsv() {
        return discoveryGeneExpressionCsv;
    }

    public void setDiscoveryGeneExpressionCsv(File discoveryGeneExpressionCsv) {
        this.discoveryGeneExpressionCsv = discoveryGeneExpressionCsv;
    }

    public String getDiscoveryGeneExpressionCsvContentType() {
        return discoveryGeneExpressionCsvContentType;
    }

    public void setDiscoveryGeneExpressionCsvContentType(String discoveryGeneExpressionCsvContentType) {
        this.discoveryGeneExpressionCsvContentType = discoveryGeneExpressionCsvContentType;
    }

    public String getDiscoveryGeneExpressionCsvFileName() {
        return discoveryGeneExpressionCsvFileName;
    }

    public void setDiscoveryGeneExpressionCsvFileName(String discoveryGeneExpressionCsvFileName) {
        this.discoveryGeneExpressionCsvFileName = discoveryGeneExpressionCsvFileName;
    }

    public String getDiscoveryPhene() {
        return discoveryPhene;
    }

    public void setDiscoveryPhene(String discoveryPhene) {
        this.discoveryPhene = discoveryPhene;
    }

    public String getDiscoveryPheneInfo() {
        return discoveryPheneInfo;
    }

    public void setDiscoveryPheneInfo(String discoveryPheneInfo) {
        this.discoveryPheneInfo = discoveryPheneInfo;
    }

    public String getDiscoveryPheneTable() {
        return discoveryPheneTable;
    }

    public void setDiscoveryPheneTable(String discoveryPheneTable) {
        this.discoveryPheneTable = discoveryPheneTable;
    }

    public double getDiscoveryPheneLowCutoff() {
        return discoveryPheneLowCutoff;
    }

    public void setDiscoveryPheneLowCutoff(double discoveryPheneLowCutoff) {
        this.discoveryPheneLowCutoff = discoveryPheneLowCutoff;
    }

    public double getDiscoveryPheneHighCutoff() {
        return discoveryPheneHighCutoff;
    }

    public void setDiscoveryPheneHighCutoff(double discoveryPheneHighCutoff) {
        this.discoveryPheneHighCutoff = discoveryPheneHighCutoff;
    }

    public List<String> getDiscoveryPheneList() {
        return discoveryPheneList;
    }

    public void setDiscoveryPheneList(List<String> discoveryPheneList) {
        this.discoveryPheneList = discoveryPheneList;
    }

    public String getGenomicsTable() {
        return genomicsTable;
    }

    public void setGenomicsTable(String genomicsTable) {
        this.genomicsTable = genomicsTable;
    }

    public Set<String> getGenomicsTables() {
        return genomicsTables;
    }

    public void setGenomicsTables(Set<String> genomicsTables) {
        this.genomicsTables = genomicsTables;
    }

    public Long getDiscoveryCohortResultsId() {
        return discoveryCohortResultsId;
    }

    public void setDiscoveryCohortResultsId(Long discoveryCohortResultsId) {
        this.discoveryCohortResultsId = discoveryCohortResultsId;
    }

    public Long getDiscoveryScoresResultsId() {
        return discoveryScoresResultsId;
    }

    public void setDiscoveryScoresResultsId(Long discoveryScoresResultsId) {
        this.discoveryScoresResultsId = discoveryScoresResultsId;
    }

    public String getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(String diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public Map<String, String> getDiagnosisCodes() {
        return diagnosisCodes;
    }

    public void setDiagnosisCodes(Map<String, String> diagnosisCodes) {
        this.diagnosisCodes = diagnosisCodes;
    }

    public List<String> getDiagnosisCodesList() {
        return diagnosisCodesList;
    }

    public void setDiagnosisCodesList(List<String> diagnosisCodesList) {
        this.diagnosisCodesList = diagnosisCodesList;
    }

    public PercentileScores getDiscoveryPercentileScores() {
        return discoveryPercentileScores;
    }

    public void setDiscoveryPercentileScores(PercentileScores discoveryPercentileScores) {
        this.discoveryPercentileScores = discoveryPercentileScores;
    }

    
    public String getDiscoveryRScriptCommandFile() {
        return discoveryRScriptCommandFile;
    }

    public void setDiscoveryRScriptCommandFile(String discoveryRScriptCommandFile) {
        this.discoveryRScriptCommandFile = discoveryRScriptCommandFile;
    }

    public String getDiscoveryRScriptLogFile() {
        return discoveryRScriptLogFile;
    }

    public void setDiscoveryRScriptLogFile(String discoveryRScriptLogFile) {
        this.discoveryRScriptLogFile = discoveryRScriptLogFile;
    }

    /*
    public Map<String, String> getProbesetToGeneMap() {
        return probesetToGeneMap;
    }

    public void setProbesetToGeneMap(Map<String, String> probesetToGeneMap) {
        this.probesetToGeneMap = probesetToGeneMap;
    }
*/
    public boolean isDebugDiscoveryScoring() {
        return debugDiscoveryScoring;
    }

    public void setDebugDiscoveryScoring(boolean debugDiscoveryScoring) {
        this.debugDiscoveryScoring = debugDiscoveryScoring;
    }

    public String[] getOperators() {
        return operators;
    }

    public void setOperators(String[] operators) {
        this.operators = operators;
    }

    public List<String> getPhenes() {
        return phenes;
    }

    public void setPhenes(List<String> phenes) {
        this.phenes = phenes;
    }

    public File getGeneExpressionCsv() {
        return geneExpressionCsv;
    }

    public void setGeneExpressionCsv(File geneExpressionCsv) {
        this.geneExpressionCsv = geneExpressionCsv;
    }

    public String getGeneExpressionCsvContentType() {
        return geneExpressionCsvContentType;
    }

    public void setGeneExpressionCsvContentType(String geneExpressionCsvContentType) {
        this.geneExpressionCsvContentType = geneExpressionCsvContentType;
    }

    public String getGeneExpressionCsvFileName() {
        return geneExpressionCsvFileName;
    }

    public void setGeneExpressionCsvFileName(String geneExpressionCsvFileName) {
        this.geneExpressionCsvFileName = geneExpressionCsvFileName;
    }

    public File getFollowUpDb() {
        return followUpDb;
    }

    public void setFollowUpDb(File followUpDb) {
        this.followUpDb = followUpDb;
    }

    public String getFollowUpDbContentType() {
        return followUpDbContentType;
    }

    public void setFollowUpDbContentType(String followUpDbContentType) {
        this.followUpDbContentType = followUpDbContentType;
    }

    public String getFollowUpDbFileName() {
        return followUpDbFileName;
    }

    public void setFollowUpDbFileName(String followUpDbFileName) {
        this.followUpDbFileName = followUpDbFileName;
    }

    public List<String> getAdmissionReasons() {
        return admissionReasons;
    }

    public void setAdmissionReasons(List<String> admissionReasons) {
        this.admissionReasons = admissionReasons;
    }


    // PRIORITIZATION -------------------------------------------------------------------
    
    public String getGeneListSpecification() {
        return geneListSpecification;
    }

    public void setGeneListSpecification(String geneListSpecification) {
        this.geneListSpecification = geneListSpecification;
    }

    public GeneListInput getGeneListInput() {
        return geneListInput;
    }

    public void setGeneListInput(GeneListInput geneListInput) {
        this.geneListInput = geneListInput;
    }

    public File getGeneListUpload() {
        return geneListUpload;
    }

    public void setGeneListUpload(File geneListUpload) {
        this.geneListUpload = geneListUpload;
    }

    public String getGeneListUploadContentType() {
        return geneListUploadContentType;
    }

    public void setGeneListUploadContentType(String geneListUploadContentType) {
        this.geneListUploadContentType = geneListUploadContentType;
    }

    public String getGeneListUploadFileName() {
        return geneListUploadFileName;
    }

    public void setGeneListUploadFileName(String geneListUploadFileName) {
        this.geneListUploadFileName = geneListUploadFileName;
    }

    public Double getPrioritizationScoreCutoff() {
        return prioritizationScoreCutoff;
    }

    public void setPrioritizationScoreCutoff(Double prioritizationScoreCutoff) {
        this.prioritizationScoreCutoff = prioritizationScoreCutoff;
    }
    
    public Double getPrioritizationComparisonThreshold() {
        return prioritizationComparisonThreshold;
    }

    public void setPrioritizationComparisonThreshold(Double prioritizationComparisonThreshold) {
        this.prioritizationComparisonThreshold = prioritizationComparisonThreshold;
    }

    
    public double getHuBrainScore() {
        return huBrainScore;
    }

    public void setHuBrainScore(double huBrainScore) {
        this.huBrainScore = huBrainScore;
    }

    public double getHuPerScore() {
        return huPerScore;
    }

    public void setHuPerScore(double huPerScore) {
        this.huPerScore = huPerScore;
    }

    public double getHuGeneCnvScore() {
        return huGeneCnvScore;
    }

    public void setHuGeneCnvScore(double huGeneCnvScore) {
        this.huGeneCnvScore = huGeneCnvScore;
    }

    public double getHuGeneAssocScore() {
        return huGeneAssocScore;
    }

    public void setHuGeneAssocScore(double huGeneAssocScore) {
        this.huGeneAssocScore = huGeneAssocScore;
    }

    public double getHuGeneLinkageScore() {
        return huGeneLinkageScore;
    }

    public void setHuGeneLinkageScore(double huGeneLinkageScore) {
        this.huGeneLinkageScore = huGeneLinkageScore;
    }

    public double getNhBrainScore() {
        return nhBrainScore;
    }

    public void setNhBrainScore(double nhBrainScore) {
        this.nhBrainScore = nhBrainScore;
    }

    public double getNhPerScore() {
        return nhPerScore;
    }

    public void setNhPerScore(double nhPerScore) {
        this.nhPerScore = nhPerScore;
    }

    public double getNhGeneCnvScore() {
        return nhGeneCnvScore;
    }

    public void setNhGeneCnvScore(double nhGeneCnvScore) {
        this.nhGeneCnvScore = nhGeneCnvScore;
    }

    public double getNhGeneAssocScore() {
        return nhGeneAssocScore;
    }

    public void setNhGeneAssocScore(double nhGeneAssocScore) {
        this.nhGeneAssocScore = nhGeneAssocScore;
    }

    public double getNhGeneLinkageScore() {
        return nhGeneLinkageScore;
    }

    public void setNhGeneLinkageScore(double nhGeneLinkageScore) {
        this.nhGeneLinkageScore = nhGeneLinkageScore;
    }

    public File getDiseasesCsv() {
        return diseasesCsv;
    }

    public void setDiseasesCsv(File diseasesCsv) {
        this.diseasesCsv = diseasesCsv;
    }

    public String getDiseasesCsvContentType() {
        return diseasesCsvContentType;
    }

    public void setDiseasesCsvContentType(String diseasesCsvContentType) {
        this.diseasesCsvContentType = diseasesCsvContentType;
    }

    public String getDiseasesCsvFileName() {
        return diseasesCsvFileName;
    }

    public void setDiseasesCsvFileName(String diseasesCsvFileName) {
        this.diseasesCsvFileName = diseasesCsvFileName;
    }

    public List<DiseaseSelector> getDiseaseSelectors() {
        return diseaseSelectors;
    }

    public void setDiseaseSelectors(List<DiseaseSelector> diseaseSelectors) {
        this.diseaseSelectors = diseaseSelectors;
    }

    public Long getPrioritizationScoresResultsId() {
        return prioritizationScoresResultsId;
    }

    public void setPrioritizationScoresResultsId(Long prioritizationScoresResultsId) {
        this.prioritizationScoresResultsId = prioritizationScoresResultsId;
    }
    
    /* VALIDATION ---------------------------------------------------------------------------- */
    
    public String getPhene1() {
		return this.phene1;
	}
	
	public void setPhene1(String phene1) {
		this.phene1 = phene1;
	}
	
    public String getPhene2() {
		return this.phene2;
	}
	
	public void setPhene2(String phene2) {
		this.phene2 = phene2;
	}
	
	public String getPhene3() {
		return this.phene3;
	}
	
	public void setPhene3(String phene3) {
		this.phene3 = phene3;
	}

  
    public String getOperator1() {
		return this.operator1;
	}
	
	public void setOperator1(String operator1) {
		this.operator1 = operator1;
	}
	
    public String getOperator2() {
		return this.operator2;
	}
	
	public void setOperator2(String operator2) {
		this.operator2 = operator2;
	}
	
	public String getOperator3() {
		return this.operator3;
	}
	
	public void setOperator3(String operator3) {
		this.operator3 = operator3;
	}

    
    public String getValue1() {
		return this.value1;
	}
	
	public void setValue1(String value1) {
		this.value1 = value1;
	}
	
    public String getValue2() {
		return this.value2;
	}
	
	public void setValue2(String value2) {
		this.value2 = value2;
	}
	
	public String getValue3() {
		return this.value3;
	}
	
	public void setValue3(String value3) {
		this.value3 = value3;
	}

	/*
    public DataTable getProbesetToGeneMapTable() {
        return probesetToGeneMapTable;
    }

    public void setProbesetToGeneMapTable(DataTable probesetToGeneMapTable) {
        this.probesetToGeneMapTable = probesetToGeneMapTable;
    }
    */
	
    public Double getDiscoveryCohortComparisonThreshold() {
        return discoveryCohortComparisonThreshold;
    }

    public void setDiscoveryCohortComparisonThreshold(Double discoveryCohortComparisonThreshold) {
        this.discoveryCohortComparisonThreshold = discoveryCohortComparisonThreshold;
    }

    
    
    
    
    public String getValidationDiagnosisType() {
        return validationDiagnosisType;
    }

    public void setValidationDiagnosisType(String validationDiagnosisType) {
        this.validationDiagnosisType = validationDiagnosisType;
    }



    public String getValidationRScriptCommandFile() {
        return validationRScriptCommandFile;
    }

    public void setValidationRScriptCommandFile(String validationRScriptCommandFile) {
        this.validationRScriptCommandFile = validationRScriptCommandFile;
    }

    public String getValidationRScriptOutputFile() {
        return validationRScriptOutputFile;
    }

    public void setValidationRScriptOutputFile(String validationRScriptOutputFile) {
        this.validationRScriptOutputFile = validationRScriptOutputFile;
    }
    
    public boolean getSkipValidationSteps() {
        return skipValidationSteps;
    }

    public void setSkipValidationSteps(boolean skipValidationSteps) {
        this.skipValidationSteps = skipValidationSteps;
    }
    
    public String getPercentInValidationCohort() {
        return percentInValidationCohort;
    }

    public void setPercentInValidationCohort(String percentInValidationCohort) {
        this.percentInValidationCohort = percentInValidationCohort;
    }

    public Double getValidationScoreCutoff() {
        return validationScoreCutoff;
    }

    public void setValidationScoreCutoff(Double validationScoreCutoff) {
        this.validationScoreCutoff = validationScoreCutoff;
    }


    

    public Double getValidationCohortComparisonThreshold() {
        return validationCohortComparisonThreshold;
    }

    public void setValidationCohortComparisonThreshold(Double validationCohortComparisonThreshold) {
        this.validationCohortComparisonThreshold = validationCohortComparisonThreshold;
    }

    public Double getValidationScoresComparisonThreshold() {
        return validationScoresComparisonThreshold;
    }

    public void setValidationScoresComparisonThreshold(Double validationScoresComparisonThreshold) {
        this.validationScoresComparisonThreshold = validationScoresComparisonThreshold;
    }

    public double getBonferroniScore() {
        return bonferroniScore;
    }

    public void setBonferroniScore(double bonferroniScore) {
        this.bonferroniScore = bonferroniScore;
    }

    public double getNominalScore() {
        return nominalScore;
    }

    public void setNominalScore(double nominalScore) {
        this.nominalScore = nominalScore;
    }

    public double getStepwiseScore() {
        return stepwiseScore;
    }

    public void setStepwiseScore(double stepwiseScore) {
        this.stepwiseScore = stepwiseScore;
    }

    public double getNonStepwiseScore() {
        return nonStepwiseScore;
    }

    public void setNonStepwiseScore(double nonStepwiseScore) {
        this.nonStepwiseScore = nonStepwiseScore;
    }
    
    public File getUpdatedValidationMasterSheet() {
        return updatedValidationMasterSheet;
    }

    public void setUpdatedValidationMasterSheet(File updatedValidationMasterSheet) {
        this.updatedValidationMasterSheet = updatedValidationMasterSheet;
    }

    public String getUpdatedValidationMasterSheetContentType() {
        return updatedValidationMasterSheetContentType;
    }

    public void setUpdatedValidationMasterSheetContentType(String updatedValidationMasterSheetContentType) {
        this.updatedValidationMasterSheetContentType = updatedValidationMasterSheetContentType;
    }

    public String getUpdatedValidationMasterSheetFileName() {
        return updatedValidationMasterSheetFileName;
    }

    public void setUpdatedValidationMasterSheetFileName(String updatedValidationMasterSheetFileName) {
        this.updatedValidationMasterSheetFileName = updatedValidationMasterSheetFileName;
    }

    public String getUpdatedValidationMasterSheetTempFileName() {
        return updatedValidationMasterSheetTempFileName;
    }

    public void setUpdatedValidationMasterSheetTempFileName(String updatedValidationMasterSheetTempFileName) {
        this.updatedValidationMasterSheetTempFileName = updatedValidationMasterSheetTempFileName;
    }

    public File getUpdatedValidationPredictorList() {
        return updatedValidationPredictorList;
    }

    public void setUpdatedValidationPredictorList(File updatedValidationPredictorList) {
        this.updatedValidationPredictorList = updatedValidationPredictorList;
    }

    public String getUpdatedValidationPredictorListContentType() {
        return updatedValidationPredictorListContentType;
    }

    public void setUpdatedValidationPredictorListContentType(String updatedValidationPredictorListContentType) {
        this.updatedValidationPredictorListContentType = updatedValidationPredictorListContentType;
    }

    public String getUpdatedValidationPredictorListFileName() {
        return updatedValidationPredictorListFileName;
    }

    public void setUpdatedValidationPredictorListFileName(String updatedValidationPredictorListFileName) {
        this.updatedValidationPredictorListFileName = updatedValidationPredictorListFileName;
    }

    public String getUpdatedValidationPredictorListTempFileName() {
        return updatedValidationPredictorListTempFileName;
    }

    public void setUpdatedValidationPredictorListTempFileName(String updatedValidationPredictorListTempFileName) {
        this.updatedValidationPredictorListTempFileName = updatedValidationPredictorListTempFileName;
    }

    public Long getValidationCohortResultsId() {
        return validationCohortResultsId;
    }

    public void setValidationCohortResultsId(Long validationCohortResultsId) {
        this.validationCohortResultsId = validationCohortResultsId;
    }
    
    public Long getValidationScoresResultsId() {
        return validationScoresResultsId;
    }

    public void setValidationScoresResultsId(Long validationScoresResultsId) {
        this.validationScoresResultsId = validationScoresResultsId;
    }
    

    /* TESTING ----------------------------------------------------------------------------------------- */

    public String getTestingDiagnosisType() {
        return testingDiagnosisType;
    }

    public void setTestingDiagnosisType(String testingDiagnosisType) {
        this.testingDiagnosisType = testingDiagnosisType;
    }
    
    public String getAdmissionPhene() {
        return admissionPhene;
    }

    public void setAdmissionPhene(String admissionPhene) {
        this.admissionPhene = admissionPhene;
    }
    
    public double getTestingScoreCutoff() {
        return testingScoreCutoff;
    }

    public void setTestingScoreCutoff(double testingScoreCutoff) {
        this.testingScoreCutoff = testingScoreCutoff;
    }

    public Double getTestingComparisonThreshold() {
        return testingComparisonThreshold;
    }

    public void setTestingComparisonThreshold(Double testingComparisonThreshold) {
        this.testingComparisonThreshold = testingComparisonThreshold;
    }

    public File getUpdatedTestingMasterSheet() {
        return updatedTestingMasterSheet;
    }

    public void setUpdatedTestingMasterSheet(File updatedTestingMasterSheet) {
        this.updatedTestingMasterSheet = updatedTestingMasterSheet;
    }

    public String getUpdatedTestingMasterSheetContentType() {
        return updatedTestingMasterSheetContentType;
    }

    public void setUpdatedTestingMasterSheetContentType(String updatedTestingMasterSheetContentType) {
        this.updatedTestingMasterSheetContentType = updatedTestingMasterSheetContentType;
    }

    public String getUpdatedTestingMasterSheetFileName() {
        return updatedTestingMasterSheetFileName;
    }

    public void setUpdatedTestingMasterSheetFileName(String updatedTestingMasterSheetFileName) {
        this.updatedTestingMasterSheetFileName = updatedTestingMasterSheetFileName;
    }

    public String getUpdatedTestingMasterSheetTempFileName() {
        return updatedTestingMasterSheetTempFileName;
    }

    public void setUpdatedTestingMasterSheetTempFileName(String updatedTestingMasterSheetTempFileName) {
        this.updatedTestingMasterSheetTempFileName = updatedTestingMasterSheetTempFileName;
    }

    public File getUpdatedTestingPredictorList() {
        return updatedTestingPredictorList;
    }

    public void setUpdatedTestingPredictorList(File updatedTestingPredictorList) {
        this.updatedTestingPredictorList = updatedTestingPredictorList;
    }

    public String getUpdatedTestingPredictorListContentType() {
        return updatedTestingPredictorListContentType;
    }

    public void setUpdatedTestingPredictorListContentType(String updatedTestingPredictorListContentType) {
        this.updatedTestingPredictorListContentType = updatedTestingPredictorListContentType;
    }

    public String getUpdatedTestingPredictorListFileName() {
        return updatedTestingPredictorListFileName;
    }

    public void setUpdatedTestingPredictorListFileName(String updatedTestingPredictorListFileName) {
        this.updatedTestingPredictorListFileName = updatedTestingPredictorListFileName;
    }

    public String getUpdatedTestingPredictorListTempFileName() {
        return updatedTestingPredictorListTempFileName;
    }

    public void setUpdatedTestingPredictorListTempFileName(String updatedTestingPredictorListTempFileName) {
        this.updatedTestingPredictorListTempFileName = updatedTestingPredictorListTempFileName;
    }

    public Long getTestingCohortsResultsId() {
        return testingCohortsResultsId;
    }

    public void setTestingCohortsResultsId(Long testingCohortsResultsId) {
        this.testingCohortsResultsId = testingCohortsResultsId;
    }
    
   
    public String getTestingCohortsPythonScriptCommandFile() {
        return testingCohortsPythonScriptCommandFile;
    }

    public void setTestingCohortsPythonScriptCommandFile(String testingCohortsPythonScriptCommandFile) {
        this.testingCohortsPythonScriptCommandFile = testingCohortsPythonScriptCommandFile;
    }

    
    
    /* --------------------------------------------- */
 
    public String getTestingCohortsPythonScriptOutputFile() {
        return testingCohortsPythonScriptOutputFile;
    }

    public void setTestingCohortsPythonScriptOutputFile(String testingCohortsPythonScriptOutputFile) {
        this.testingCohortsPythonScriptOutputFile = testingCohortsPythonScriptOutputFile;
    }

    public boolean isStateCrossSectional() {
        return stateCrossSectional;
    }

    public void setStateCrossSectional(boolean stateCrossSectional) {
        this.stateCrossSectional = stateCrossSectional;
    }

    public boolean isStateLongitudinal() {
        return stateLongitudinal;
    }

    public void setStateLongitudinal(boolean stateLongitudinal) {
        this.stateLongitudinal = stateLongitudinal;
    }

    public boolean isFirstYearCrossSectional() {
        return firstYearCrossSectional;
    }

    public void setFirstYearCrossSectional(boolean firstYearCrossSectional) {
        this.firstYearCrossSectional = firstYearCrossSectional;
    }

    public boolean isFirstYearLongitudinal() {
        return firstYearLongitudinal;
    }

    public void setFirstYearLongitudinal(boolean firstYearLongitudinal) {
        this.firstYearLongitudinal = firstYearLongitudinal;
    }

    public boolean isFutureCrossSectional() {
        return futureCrossSectional;
    }

    public void setFutureCrossSectional(boolean futureCrossSectional) {
        this.futureCrossSectional = futureCrossSectional;
    }

    public boolean isFuturetLongitudinal() {
        return futuretLongitudinal;
    }

    public void setFuturetLongitudinal(boolean futuretLongitudinal) {
        this.futuretLongitudinal = futuretLongitudinal;
    }

    public String getPredictionPhene() {
        return predictionPhene;
    }

    public void setPredictionPhene(String predictionPhene) {
        this.predictionPhene = predictionPhene;
    }

    public Double getPredictionPheneHighCutoff() {
        return predictionPheneHighCutoff;
    }

    public void setPredictionPheneHighCutoff(Double predictionPheneHighCutoff) {
        this.predictionPheneHighCutoff = predictionPheneHighCutoff;
    }

    public Double getPredictionPheneComparisonThreshold() {
        return predictionPheneComparisonThreshold;
    }

    public void setPredictionPheneComparisonThreshold(Double predictionPheneComparisonThreshold) {
        this.predictionPheneComparisonThreshold = predictionPheneComparisonThreshold;
    }

  

    public Double getTestingAllScore() {
        return testingAllScore;
    }

    public void setTestingAllScore(Double testingAllScore) {
        this.testingAllScore = testingAllScore;
    }
    
    public Double getTestingGenderScore() {
        return testingGenderScore;
    }

    public void setTestingGenderScore(Double testingGenderScore) {
        this.testingGenderScore = testingGenderScore;
    }

    public Double getTestingGenderDiagnosisScore() {
        return testingGenderDiagnosisScore;
    }

    public void setTestingGenderDiagnosisScore(Double testingGenderDiagnosisScore) {
        this.testingGenderDiagnosisScore = testingGenderDiagnosisScore;
    }

    
    public String getStateCrossSectionalRScriptCommandFile() {
        return stateCrossSectionalRScriptCommandFile;
    }

    public void setStateCrossSectionalRScriptCommandFile(String stateCrossSectionalRScriptCommandFile) {
        this.stateCrossSectionalRScriptCommandFile = stateCrossSectionalRScriptCommandFile;
    }

    public String getStateCrossSectionalRScriptOutputFile() {
        return stateCrossSectionalRScriptOutputFile;
    }

    public void setStateCrossSectionalRScriptOutputFile(String stateCrossSectionalRScriptOutputFile) {
        this.stateCrossSectionalRScriptOutputFile = stateCrossSectionalRScriptOutputFile;
    }

    public String getStateLongitudinalRScriptCommandFile() {
        return stateLongitudinalRScriptCommandFile;
    }

    public void setStateLongitudinalRScriptCommandFile(String stateLongitudinalRScriptCommandFile) {
        this.stateLongitudinalRScriptCommandFile = stateLongitudinalRScriptCommandFile;
    }

    public String getStateLongitudinalRScriptOutputFile() {
        return stateLongitudinalRScriptOutputFile;
    }

    public void setStateLongitudinalRScriptOutputFile(String stateLongitudinalRScriptOutputFile) {
        this.stateLongitudinalRScriptOutputFile = stateLongitudinalRScriptOutputFile;
    }

    public String getFirstYearCrossSectionalRScriptCommandFile() {
        return firstYearCrossSectionalRScriptCommandFile;
    }

    public void setFirstYearCrossSectionalRScriptCommandFile(String firstYearCrossSectionalRScriptCommandFile) {
        this.firstYearCrossSectionalRScriptCommandFile = firstYearCrossSectionalRScriptCommandFile;
    }

    public String getFirstYearCrossSectionalRScriptOutputFile() {
        return firstYearCrossSectionalRScriptOutputFile;
    }

    public void setFirstYearCrossSectionalRScriptOutputFile(String firstYearCrossSectionalRScriptOutputFile) {
        this.firstYearCrossSectionalRScriptOutputFile = firstYearCrossSectionalRScriptOutputFile;
    }

    public String getFirstYearLongitudinalRScriptCommandFile() {
        return firstYearLongitudinalRScriptCommandFile;
    }

    public void setFirstYearLongitudinalRScriptCommandFile(String firstYearLongitudinalRScriptCommandFile) {
        this.firstYearLongitudinalRScriptCommandFile = firstYearLongitudinalRScriptCommandFile;
    }

    public String getFirstYearLongitudinalRScriptOutputFile() {
        return firstYearLongitudinalRScriptOutputFile;
    }

    public void setFirstYearLongitudinalRScriptOutputFile(String firstYearLongitudinalRScriptOutputFile) {
        this.firstYearLongitudinalRScriptOutputFile = firstYearLongitudinalRScriptOutputFile;
    }

    public String getFutureCrossSectionalRScriptCommandFile() {
        return futureCrossSectionalRScriptCommandFile;
    }

    public void setFutureCrossSectionalRScriptCommandFile(String futureCrossSectionalRScriptCommandFile) {
        this.futureCrossSectionalRScriptCommandFile = futureCrossSectionalRScriptCommandFile;
    }

    public String getFutureCrossSectionalRScriptOutputFile() {
        return futureCrossSectionalRScriptOutputFile;
    }

    public void setFutureCrossSectionalRScriptOutputFile(String futureCrossSectionalRScriptOutputFile) {
        this.futureCrossSectionalRScriptOutputFile = futureCrossSectionalRScriptOutputFile;
    }

    public String getFutureLongitudinalRScriptCommandFile() {
        return futureLongitudinalRScriptCommandFile;
    }

    public void setFutureLongitudinalRScriptCommandFile(String futureLongitudinalRScriptCommandFile) {
        this.futureLongitudinalRScriptCommandFile = futureLongitudinalRScriptCommandFile;
    }

    public String getFutureLongitudinalRScriptOutputFile() {
        return futureLongitudinalRScriptOutputFile;
    }

    public void setFutureLongitudinalRScriptOutputFile(String futureLongitudinalRScriptOutputFile) {
        this.futureLongitudinalRScriptOutputFile = futureLongitudinalRScriptOutputFile;
    }

    public Long getTestingScoresResultsId() {
        return testingScoresResultsId;
    }

    public void setTestingScoresResultsId(Long testingScoresResultsId) {
        this.testingScoresResultsId = testingScoresResultsId;
    }

    

    /* Filter Variables */
    
    public String getCfeResultsType() {
        return cfeResultsType;
    }

    public void setCfeResultsType(String cfeResultsType) {
        this.cfeResultsType = cfeResultsType;
    }

    public List<String> getCfeResultsTypes() {
        return cfeResultsTypes;
    }

    public void setCfeResultsTypes(List<String> cfeResultsTypes) {
        this.cfeResultsTypes = cfeResultsTypes;
    }

    public String getResultsPhene() {
        return resultsPhene;
    }

    public void setResultsPhene(String resultsPhene) {
        this.resultsPhene = resultsPhene;
    }

    public List<String> getResultsPhenes() {
        return resultsPhenes;
    }

    public void setResultsPhenes(List<String> resultsPhenes) {
        this.resultsPhenes = resultsPhenes;
    }

    
}
