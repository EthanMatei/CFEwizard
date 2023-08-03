package cfe.action.prioritization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.struts2.action.SessionAware;

import cfe.action.BaseAction;
import cfe.model.CfeResults;
import cfe.model.CfeResultsNewestFirstComparator;
import cfe.model.CfeResultsType;
import cfe.model.prioritization.GeneList;
import cfe.model.prioritization.GeneListInput;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;
import cfe.utils.DataTable;

/**
 * Action class for uploading a custom gene list.
 * 
 * @author Michel Tavares
 * @author Jim Mullen
 *
 */
public class GeneListUpload extends BaseAction implements SessionAware {
	
	private static final long serialVersionUID = 6507655467803486283L;
	private static final Logger log = Logger.getLogger(GeneListUpload.class.getName());
	private Map<String, Object> session;
	
	private String geneListButton;
	
	private File file;
    @SuppressWarnings("unused")
	private String contentType;
    @SuppressWarnings("unused")
	private String filename;
    
    private String geneListFileName;
    
    private double comparisonThreshold = 0.0001;
    
    private List<CfeResults> discoveryScoringResultsList;
    private Long discoveryId;
    private Double discoveryScoreCutoff;
    
    Boolean includeNonDiscoveryOptions;
    
    public GeneListUpload() {
        this.setCurrentTab("Special Functions");    
        this.setCurrentSubTab("Scoring");
        this.setCurrentStep(1);
    }
    
    public void setUpload(File file) {
       this.file = file;
    }

    public void setUploadContentType(String contentType) {
       this.contentType = contentType;
    }

    public void setUploadFileName(String filename) {
       this.filename = filename;
    }

    public String initialize() {
        String result = SUCCESS;
        
        this.setCurrentStep(1);
        
        this.discoveryScoringResultsList =
                CfeResultsService.getMetadata(CfeResultsType.DISCOVERY_SCORES);
        Collections.sort(this.discoveryScoringResultsList, new CfeResultsNewestFirstComparator());
        
        return result;
    }
    
    public String execute() throws Exception {
		String result = SUCCESS;

		this.setCurrentStep(2);
		if (!Authorization.isLoggedIn(session)) {
			result = LOGIN;
		}
		else {

		    try {
		        GeneListInput geneListInput = new GeneListInput();

		        if (file == null) {
		            result = INPUT;
		            throw new Exception("No gene list file specified for upload.");
		        }

		        geneListInput = processFile2(file.getPath());

		        geneListFileName = filename;
		        
		        session.put("geneListInput",  geneListInput);
		    }
		    catch (Exception exception) {
		        String message = "Gene list upload error: " + exception.getLocalizedMessage();
		        log.severe(message);
		        this.setErrorMessage(message);
		    }
		}
		
    	log.info("gene list update process result: " + result);
    	return result;
    }
    
    public String processAllGenes() throws Exception {
        String result = SUCCESS;

        if (!Authorization.isLoggedIn(session)) {
            result = LOGIN;
        }
        else {

            try {
                GeneListInput geneListInput = new GeneListInput();
                geneListFileName = "";
                session.put("geneListInput",  geneListInput);
            }
            catch (Exception exception) {
                String message = "Gene list upload error: " + exception.getLocalizedMessage();
                log.severe(message);
                this.setErrorMessage(message);
            }
        }
        
        log.info("process all genes result: " + result);
        return result;
    }
    
    public String processDiscoveryResults() throws Exception {
        String result = SUCCESS;

        if (!Authorization.isLoggedIn(session)) {
            result = LOGIN;
        }
        else {
            log.info("***************** PROCESSING DISCOVERY RESULTS FOR PRIORITIZATION");

            try {
                GeneListInput geneListInput = new GeneListInput();

                // Generating from Discovery results
                if (this.discoveryScoreCutoff == null || this.discoveryScoreCutoff <= 0.0) {
                    result = INPUT;
                    String message = "Discovery score cutoff unset for gene list generation for prioritization.";
                    throw new Exception(message);
                }
                if (this.discoveryId == null || this.discoveryId <= 0) {
                    result = INPUT;
                    throw new Exception("No discovery results specified for gene list generation for prioritization.");
                }

                CfeResults discoveryResults = CfeResultsService.get(discoveryId);
                if (discoveryResults == null) {
                    result = INPUT;
                    throw new Exception("Discovery result with ID \"" + discoveryId + "\" could not be found.");
                }

                geneListInput = this.processDiscoveryResults(discoveryResults);
                
                if (geneListInput == null || geneListInput.size() < 1) {
                    result = INPUT;
                    String message = "Discovery score cutoff of " + this.discoveryScoreCutoff + " generates no genes.";
                    throw new Exception(message);
                }
                
                log.info("Gene list input size: " + geneListInput.size());

                session.put("geneListInput",  geneListInput);
            }
            catch (Exception exception) {
                String message = "Gene list upload error: " + exception.getLocalizedMessage();
                log.severe(message);
                this.setErrorMessage(message);
                result = ERROR;
            }
        }
        
        log.info("gene list update process result: " + result);
        return result;
    }
    
    
    
    /**
     * An attacker might try to embed commands in the file 
     * http://owasp-esapi-java.googlecode.com/svn/trunk_doc/latest/index.html
     * @throws IOException 
     */
	private ArrayList<GeneList> processFile (String filename) throws IOException   {
    	ArrayList<GeneList> genes = new ArrayList<GeneList>(50);
    	
    	BufferedReader br = new BufferedReader(new FileReader(filename));
    	String line, _gene;

    	while ((line = br.readLine()) != null) {
    		 _gene = line.trim();
    		 
    		 if (cfe.secure.Security.passesGeneWhiteList(_gene))	{
    			 GeneList gene = new GeneList();
    			 gene.setGenecardSymbol(_gene);
    			 genes.add(gene);
    		 } else {
    			 log.warning("Invalid gene " + _gene + " in file " + filename);
    		 }
    	}
    	
    	br.close();
    	return genes;    	
    }
	
	private GeneListInput processFile2(String filename) throws Exception {
		GeneListInput geneList = new GeneListInput();
    	
    	BufferedReader br = new BufferedReader(new FileReader(filename));
    	String line, geneCardSymbol;

    	while ((line = br.readLine()) != null) {
    		 geneCardSymbol = line.trim();
    		 
    		 if (geneCardSymbol.equals("")) {
    			 ; // skip blank lines
    		 }
    		 else if (cfe.secure.Security.passesGeneWhiteList(geneCardSymbol))	{
    			 geneList.add(geneCardSymbol);
    		 } 
    		 else {
    			 String message = "Invalid gene name \"" + geneCardSymbol + "\" in gene list.";
    			 log.warning( message  + " File: " + filename);
    			 br.close();
    			 throw new Exception( message );
    		 }
    	}
    	
    	br.close();
		return geneList;
	}
	
	public GeneListInput processDiscoveryResults(CfeResults discoveryResults) throws Exception {
	    GeneListInput geneList = new GeneListInput();
	    
	    // String key = "Probe Set ID";
	    String key = null;
	    DataTable discoveryScores = discoveryResults.getSheetAsDataTable(CfeResultsType.DISCOVERY_SCORES, key);
	    
	    Map<String,String> row = null;
	    
	    for (int i = 0; i < discoveryScores.getNumberOfRows(); i++) {
	        row = discoveryScores.getRowMap(i);
	        
	        String deScore = "DE Score";
	        
	        if (!row.containsKey(deScore)) {
	            String message = "Row for discovery scores does not contain the \"" + deScore + "\" column.";
	            log.severe(message);
	            throw new Exception(message);
	        }
	        
	        String scoreString = row.get(deScore);
	        
	        if (scoreString == null) {
	            
	        }
	        
	        double score = 0.0;
	        
	        try {
	            score = Double.parseDouble(scoreString);
	        }
	        catch (NumberFormatException exception) {
	            score = 0.0;    
	        }
	        
	        log.info("DISCOVERY SCORE CUTOFF FOR PRIORITIZATION FROM DISCOVERY SCORES: " + this.discoveryScoreCutoff);
	        if (score >= this.discoveryScoreCutoff - this.comparisonThreshold) {
	            String genecardsSymbol = row.get("Genecards Symbol");
	            geneList.add(genecardsSymbol);
	        }
	    }
	    
	    return geneList;
	}
	
	
	public void withSession(Map<String, Object> session) {
		this.session = session;
	}
	

    public List<CfeResults> getDiscoveryScoringResultsList() {
        return discoveryScoringResultsList;
    }

    public void setDiscoveryScoringResultsList(List<CfeResults> discoveryScoringResultsList) {
        this.discoveryScoringResultsList = discoveryScoringResultsList;
    }

    public Long getDiscoveryId() {
        return discoveryId;
    }

    public void setDiscoveryId(Long discoveryId) {
        this.discoveryId = discoveryId;
    }

    public Double getDiscoveryScoreCutoff() {
        return discoveryScoreCutoff;
    }

    public void setDiscoveryScoreCutoff(Double discoveryScoreCutoff) {
        this.discoveryScoreCutoff = discoveryScoreCutoff;
    }

    public String getGeneListButton() {
        return geneListButton;
    }

    public void setGeneListButton(String geneListButton) {
        this.geneListButton = geneListButton;
    }

    public double getComparisonThreshold() {
        return comparisonThreshold;
    }

    public void setComparisonThreshold(double comparisonThreshold) {
        this.comparisonThreshold = comparisonThreshold;
    }

    public String getGeneListFileName() {
        return geneListFileName;
    }

    public void setGeneListFileName(String geneListFileName) {
        this.geneListFileName = geneListFileName;
    }

    public Boolean getIncludeNonDiscoveryOptions() {
        return includeNonDiscoveryOptions;
    }

    public void setIncludeNonDiscoveryOptions(Boolean includeNonDiscoveryOptions) {
        this.includeNonDiscoveryOptions = includeNonDiscoveryOptions;
    }
    
}
