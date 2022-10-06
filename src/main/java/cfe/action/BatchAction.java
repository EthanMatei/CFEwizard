package cfe.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.SessionAware;

import com.healthmarketscience.jackcess.Table;

import cfe.calc.DiscoveryCalc;
import cfe.model.CfeResults;
import cfe.model.CfeResultsFileType;
import cfe.model.PercentileScores;
import cfe.model.prioritization.GeneListInput;
import cfe.model.prioritization.Score;
import cfe.model.prioritization.disease.DiseaseSelection;
import cfe.model.prioritization.disease.DiseaseSelector;
import cfe.model.prioritization.reports.ReportGenerator;
import cfe.model.prioritization.results.Results;
import cfe.parser.DiscoveryDatabaseParser;
import cfe.parser.ProbesetMappingParser;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;
import cfe.utils.DataTable;
import cfe.utils.FileUtil;

public class BatchAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	//private static final Log log = LogFactory.getLog(DiscoveryAction.class);
    private static Logger log = Logger.getLogger(BatchAction.class.getName());

	private Map<String, Object> webSession;
	
	private File testingDb;
	private String testingDbContentType;
	private String testingDbFileName;
	private String testingDbTempFileName;
	
    private File probesetToGeneMappingDb;
    private String probesetToGeneMappingDbContentType;
    private String probesetToGeneMappingDbFileName;
    private String probesetToGeneMappingDbTempFileName;
    private DataTable probesetToGeneMapTable;
    private Map<String,String> probesetToGeneMap;
    
    private File discoveryGeneExpressionCsv;
    private String discoveryGeneExpressionCsvContentType;
    private String discoveryGeneExpressionCsvFileName;
    
    private File geneExpressionCsv;
    private String geneExpressionCsvContentType;
    private String geneExpressionCsvFileName;
    
    private Set<String> genomicsTables;
    private String genomicsTable;
    
    Long discoveryCohortResultsId;
    Long discoveryScoresResultsId;

    private String diagnosisCode;
    private Map<String,String> diagnosisCodes;
    private List<String> diagnosisCodesList;
    
    private PercentileScores discoveryPercentileScores;
    
    private boolean debugDiscoveryScoring = false;
    
    private List<String> phenes;
    
    /* Discovery ------------------------------------------------------------- */
    private String discoveryPhene;
    private String discoveryPheneInfo;
    private String discoveryPheneTable;
    private double discoveryPheneLowCutoff;
    private double discoveryPheneHighCutoff;
    List<String> discoveryPheneList;
    
    /* Prioritization -------------------------------------------------------- */
    private String geneListSpecification;
    private GeneListInput geneListInput;
    private File geneListUpload;
    private String geneListUploadContentType;
    private String geneListUploadFileName;
    private Double discoveryScoreCutoff;
    private Double prioritizationComparisonThreshold;
    
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
    
    /* Validation ------------------------------------------------------------ */
    private String[] operators = {">=", ">", "<=", "<"};

    /* Testing --------------------------------------------------------------- */
    private File followUpDb;
    private String followUpDbContentType;
    private String followUpDbFileName;
    
    private List<String> admissionReasons;
    
    public BatchAction() {
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
    }
    
    public void setSession(Map<String, Object> session) {
	    this.webSession = session;    
	}
	   
	public String initialize() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
	    return result;
	}
	
	public String uploadData() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else if (this.testingDb == null || this.testingDbFileName == null) {
		    this.setErrorMessage("No testing database was specified.");
		    result = ERROR;
		}
		else if (!this.testingDbFileName.endsWith(".accdb")) {
		    this.setErrorMessage("Testing database file \"" + testingDbFileName
		            + "\" does not have MS Access database file extension \".accdb\".");
		    result = ERROR;
		}
		else {
		    try {
		        log.info("Testing database \"" + this.testingDbFileName + "\" uploaded.");
		        
	            this.discoveryPercentileScores = new PercentileScores();
		        
			    // Copy the upload files to temporary files, because the upload files get deleted
			    // and they are needed beyond this method
		        
		        // Testing database
			    File testingDbTmp = FileUtil.createTempFile("testing-db-", ".accdb");
			    FileUtils.copyFile(this.testingDb, testingDbTmp);
			    this.testingDbTempFileName = testingDbTmp.getAbsolutePath();
			    this.testingDbFileName = testingDb.getAbsolutePath();


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
			    
		    } catch (Exception exception) {
		        this.setErrorMessage("The database could not be processed. " + exception.getLocalizedMessage());
		        result = ERROR;
		    }
		}
	    return result;
	}
	
    public String calculate() throws Exception {
        String result = SUCCESS;
        
        if (!Authorization.isAdmin(webSession)) {
            result = LOGIN;
        }
        else if (this.probesetToGeneMappingDb == null || this.probesetToGeneMappingDbFileName == null) {
            this.setErrorMessage("No probeset to gene mapping database was specified.");
            result = ERROR;
        }
        else if (!this.probesetToGeneMappingDbFileName.endsWith(".accdb")) {
            this.setErrorMessage("Probeset to gene mapping database file \"" + probesetToGeneMappingDbFileName
                    + "\" does not have MS Access database file extension \".accdb\".");
            result = ERROR;
        }
        else {
            try { 
                // Probeset to gene database mapping
                File probesetToGeneMappingDbTmp = FileUtil.createTempFile("probest-to-gene-mapping-db-", ".accdb");
                FileUtils.copyFile(this.probesetToGeneMappingDb, probesetToGeneMappingDbTmp);
                this.probesetToGeneMappingDbTempFileName = probesetToGeneMappingDbTmp.getAbsolutePath();
                this.probesetToGeneMappingDbFileName = probesetToGeneMappingDb.getAbsolutePath();

                //------------------------------------------------------------
                // Get the probeset to mapping information
                //------------------------------------------------------------
                String key = ProbesetMappingParser.PROBE_SET_ID_COLUMN;
                DataTable probesetMapping = new DataTable(key);

                ProbesetMappingParser probesetDbParser = new ProbesetMappingParser(this.probesetToGeneMappingDb.getAbsolutePath());
                Table table = probesetDbParser.getMappingTable();
                probesetMapping.initializeToAccessTable(table);
                this.probesetToGeneMap = probesetMapping.getMap(key, ProbesetMappingParser.GENECARDS_SYMBOL_COLUMN);
                
                String[] pheneInfo = this.discoveryPheneInfo.split("]", 2);
                this.discoveryPheneTable = pheneInfo[0].replace('[', ' ').trim();
                this.discoveryPhene = pheneInfo[1].trim();
                
                //-------------------------------------------
                // Create Discovery Cohort
                //-------------------------------------------
                CfeResults discoveryCohort = DiscoveryCalc.createDiscoveryCohort(
                        this.testingDbTempFileName,
                        this.discoveryPheneTable,
                        this.discoveryPhene,
                        this.discoveryPheneLowCutoff,
                        this.discoveryPheneHighCutoff,
                        this.genomicsTable
                );
                this.discoveryCohortResultsId = discoveryCohort.getCfeResultsId();
                

                String scriptDir  = new File(getClass().getResource("/R").toURI()).getAbsolutePath();
                String scriptFile = new File(getClass().getResource("/R/DEdiscovery.R").toURI()).getAbsolutePath();
                
                //--------------------------------------------
                // Calculate Discovery Scores
                //--------------------------------------------
                CfeResults discoveryCfeResults = DiscoveryCalc.calculateScores(
                    discoveryCohortResultsId,
                    discoveryGeneExpressionCsv,
                    probesetToGeneMappingDbFileName,
                    discoveryPheneTable,
                    discoveryPhene,
                    discoveryPheneLowCutoff,
                    discoveryPheneHighCutoff,
                    diagnosisCode,
                    discoveryPercentileScores,
                    scriptDir,
                    scriptFile,
                    debugDiscoveryScoring
                );

                if (discoveryCfeResults == null) {
                    throw new Exception("Discovery scores could not be calculated.");
                }
                this.discoveryScoresResultsId = discoveryCfeResults.getCfeResultsId();
                
                //---------------------------------------------------------------
                // Calculate Prioritization Scores
                //---------------------------------------------------------------
                
                if (this.geneListSpecification.contentEquals("All")) {
                    this.geneListUploadFileName = "";
                    this.geneListInput = new GeneListInput();
                }
                else if (this.geneListSpecification.contentEquals("Upload File:")) {
                    this.geneListInput = new GeneListInput(this.geneListUploadFileName);
                }
                else if (this.geneListSpecification.contentEquals("Generate from Discovery:")) {
                    this.geneListUploadFileName = "";
                    this.geneListInput = new GeneListInput(discoveryCfeResults, this.discoveryScoreCutoff, this.prioritizationComparisonThreshold);
                }
                else {
                    result = INPUT;
                    throw new Exception("Gene list specification\"" + this.geneListSpecification + "\" is invalid.");
                }
                
                this.diseaseSelectors = DiseaseSelector.importCsvFile(diseasesCsvFileName, diseasesCsv);
                List<cfe.enums.prioritization.ScoringWeights> weights = this.getPrioritizationWeights();
                
                DiseaseSelection diseaseSelection = new DiseaseSelection(diseaseSelectors);
                Results results = Score.calculate(geneListInput, diseaseSelection, weights);
                
                // Generate a workbook with the prioritization scores
                XSSFWorkbook workbook = ReportGenerator.generateScoresWorkbook(
                        results, /* scores, */ weights, diseaseSelectors, geneListInput,
                        this.discoveryScoresResultsId, discoveryScoreCutoff, this.geneListUploadFileName 
                        );
                
                CfeResults prioritizationCfeResults = new CfeResults();
                prioritizationCfeResults.setResultsSpreadsheet(workbook);
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

    public String getProbesetToGeneMappingDbTempFileName() {
        return probesetToGeneMappingDbTempFileName;
    }

    public void setProbesetToGeneMappingDbTempFileName(String probesetToGeneMappingDbTempFileName) {
        this.probesetToGeneMappingDbTempFileName = probesetToGeneMappingDbTempFileName;
    }

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

    public Map<String, String> getProbesetToGeneMap() {
        return probesetToGeneMap;
    }

    public void setProbesetToGeneMap(Map<String, String> probesetToGeneMap) {
        this.probesetToGeneMap = probesetToGeneMap;
    }

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

    public Double getDiscoveryScoreCutoff() {
        return discoveryScoreCutoff;
    }

    public void setDiscoveryScoreCutoff(Double discoveryScoreCutoff) {
        this.discoveryScoreCutoff = discoveryScoreCutoff;
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
   
}
