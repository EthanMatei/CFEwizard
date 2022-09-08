package cfe.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.interceptor.SessionAware;

import cfe.parser.DiscoveryDatabaseParser;
import cfe.utils.Authorization;
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
    
    private String discoveryPhene;
    private double discoveryPheneLowCutoff;
    private double discoveryPheneHighCutoff;
    List<String> discoveryPheneList;
    
    private Set<String> genomicsTables;
    private String geneomicsTable;

    private Map<String,String> diagnosisCodes;
    private List<String> diagnosisCodesList;
    
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
		        log.info("Testing database \"" + this.testingDbFileName + "\" uploaded.");
		        
			    // Copy the upload files to temporary files, because the upload files get deleted
			    // and they are needed beyond this method
		        
		        // Testing database
			    File testingDbTmp = FileUtil.createTempFile("testing-db-", ".accdb");
			    FileUtils.copyFile(this.testingDb, testingDbTmp);
			    this.testingDbTempFileName = testingDbTmp.getAbsolutePath();
			    this.testingDbFileName = testingDb.getAbsolutePath();

	            // Probeset to gene database mapping
                File probesetToGeneMappingDbTmp = FileUtil.createTempFile("probest-to-gene-mapping-db-", ".accdb");
                FileUtils.copyFile(this.probesetToGeneMappingDb, probesetToGeneMappingDbTmp);
                this.probesetToGeneMappingDbTempFileName = probesetToGeneMappingDbTmp.getAbsolutePath();
                this.probesetToGeneMappingDbFileName = probesetToGeneMappingDb.getAbsolutePath();
                
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
			    
		    } catch (Exception exception) {
		        this.setErrorMessage("The Discovery database could not be processed. " + exception.getLocalizedMessage());
		        result = ERROR;
		    }
		}
	    return result;
	}
	
    public String calculate() throws Exception {
        String result = SUCCESS;
        return result;
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

    public String getDiscoveryPhene() {
        return discoveryPhene;
    }

    public void setDiscoveryPhene(String discoveryPhene) {
        this.discoveryPhene = discoveryPhene;
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

    public String getGeneomicsTable() {
        return geneomicsTable;
    }

    public void setGeneomicsTable(String geneomicsTable) {
        this.geneomicsTable = geneomicsTable;
    }

    public Set<String> getGenomicsTables() {
        return genomicsTables;
    }

    public void setGenomicsTables(Set<String> genomicsTables) {
        this.genomicsTables = genomicsTables;
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

}
