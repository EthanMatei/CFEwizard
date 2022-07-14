package cfe.action.prioritization;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;

import cfe.action.BaseAction;
import cfe.action.CfeResultsAction;
import cfe.enums.prioritization.Scores;
import cfe.model.prioritization.Disorder;
import cfe.model.prioritization.disease.DiseaseSelector;
import cfe.services.prioritization.DisorderService;
import cfe.services.ServiceException;
import cfe.utils.Authorization;

import com.opencsv.CSVReader;
import com.opensymphony.xwork2.ModelDriven;

// http://stackoverflow.com/questions/3044447/iterating-over-hashmap-in-jsp-in-struts-application
public class DiseaseSelectionAction extends BaseAction implements ModelDriven<List<DiseaseSelector>>, SessionAware {
	

	private static final long serialVersionUID = 1L;
	
    private static final Logger log = Logger.getLogger(DiseaseSelectionAction.class.getName());

    private Long discoveryId;
    private Double discoveryScoreCutoff;
    private String geneListFileName;
    
	private Map<String, Object> session;
	
	private List<DiseaseSelector> diseaseSelectors = new ArrayList<DiseaseSelector>();
	private String score;
	private boolean otherCompleted;
	
    private File diseasesImport;
	private String diseasesImportContentType;
	private String diseasesImportFileName;
	    
	private List<Disorder> disorders;
	
	public List<DiseaseSelector> getModel() {
		return diseaseSelectors;
	}

	public String initialize() {
		String result = SUCCESS;

		if (!Authorization.isLoggedIn(session)) {
			result = LOGIN;
		}
		else {
			try {
				this.diseaseSelectors = DisorderService.getDiseaseSelectors();
			}
			catch (ServiceException exception) {
				result = ERROR;
			}

			score = Scores.OTHER.getLabel();
			otherCompleted = true;
		}
		
		return result;
	}
	
	/**
	 * Imports a CSV file of diseases and coefficients for disease selection.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String importDiseases() throws Exception {
	    String result = SUCCESS;

	    try {

	        if (this.diseasesImportFileName == null || this.diseasesImportFileName.isEmpty()) {
	            result = INPUT;
	            throw new Exception("Diseases import file not specified.");    
	        }
	        
	        if (this.diseasesImport == null) {
	            result = INPUT;
	            throw new Exception("Diseases import file \"" + this.diseasesImportFileName +"\" could not be accessed.");
	        }
	        
	        if (!this.diseasesImportFileName.endsWith(".csv")) {
	            result = INPUT;
	            throw new Exception("Diseases import file \"" + this.diseasesImportFileName + "\" is not a \".csv\" file.");
	        }

            log.info("Importing diseases from file " + this.diseasesImportFileName);
   
            this.diseaseSelectors = DisorderService.getDiseaseSelectors();
            
	        List<String[]> data = new ArrayList<String[]>();
	        String[] header = new String[0];

	        // Get the data and header of the CSV file

	        try (CSVReader reader = new CSVReader(new FileReader(this.diseasesImport))) {
	            data = reader.readAll();
	            header = data.remove(0);
	            // data.forEach(x -> System.out.println(Arrays.toString(x)));
	        }

	        log.info("Diseases import file has " + header.length + " header columns.");
	        log.info("Diseases import file has " + data.size() + " data rows.");

	        if (header.length != 4) {
	            result = INPUT;
	            String message = "Imported diseases file has " + header.length + "header columns, instead of 4.";
	            throw new Exception(message);
	        }

	        if (!header[0].contentEquals("Domain")) {
	            result = INPUT;
	            throw new Exception("First header column in imported diseases CSV file is \"" + header[0] + "\" and not \"Domain\"");
	        }

	        if (!header[1].contentEquals("SubDomain")) {
	            result = INPUT;
	            String message = "Second header column in imported diseases CSV file is \"" + header[1] + "\" and not \"SubDomain\"";
	            throw new Exception(message);
	        }

            if (!header[2].contentEquals("Relevant Disorder")) {
                result = INPUT;
                String message = "Third header column in imported diseases CSV file is \""
                    + header[1] + "\" and not \"Relevant Disorder\"";
                throw new Exception(message);
            }

            if (!header[3].contentEquals("Coefficient")) {
                result = INPUT;
                String message = "Fourth header column in imported diseases CSV file is \""
                    + header[1] + "\" and not \"Coefficient\"";
                throw new Exception(message);
            }
            
	        for (int i = 0; i < this.diseaseSelectors.size(); i++) {
	            DiseaseSelector diseaseSelector = this.diseaseSelectors.get(i);
	            log.info("= " + diseaseSelector.getPsychiatricDomain()
	            + "." + diseaseSelector.getPsychiatricSubDomain() + "." + diseaseSelector.getRelevantDisorder());

	            for (String[] line: data) {
	                String domain           = line[0];
	                String subDomain        = line[1];
	                String relevantDisorder = line[2];
	                String coefficient      = line[3];

	                log.info("    " + domain + "." + subDomain + "." + relevantDisorder + ": " + coefficient);

	                if (diseaseSelector.getPsychiatricDomain().contentEquals(domain)
	                        && diseaseSelector.getPsychiatricSubDomain().contentEquals(subDomain)
	                        && diseaseSelector.getRelevantDisorder().contentEquals(relevantDisorder)) {

	                    this.diseaseSelectors.get(i).setRelevantDisorderSelected(true);

	                    double coefficientValue = 0.0;

	                    log.info("*** SETTING domain \"" + domain + "\" with coefficent " + coefficientValue);

	                    try {
	                        coefficientValue = Double.parseDouble(coefficient);
	                    }
	                    catch (NumberFormatException exception) {
	                        ;
	                    }
	                    this.diseaseSelectors.get(i).setCoefficient(coefficientValue);
	                }
	            }
	        }

	        session.put("diseaseSelectors",  diseaseSelectors);
	    }
	    catch (Exception exception) {
	        if (result == SUCCESS) result = ERROR;
	        String message = "Diseases import error: " + exception.getLocalizedMessage();
	        log.severe(message);
	        this.setErrorMessage(message);
	    }
	   
	    log.info("Disease import completed with status: \"" + result + "\".");
	    return result;
	}
	
	
	/**
	 * Processes disease selection form submit.
	 */
	public String execute() throws Exception {

		String result = SUCCESS;
		
		if (!Authorization.isLoggedIn(session)) {
			result = LOGIN;
		}
		else {
		    //JGM FIX!!!!!!!!!
		    // Need a check for the case of no disorders selected (return INPUT)
		    // And for negative coefficients
	        session.put("diseaseSelectors",  diseaseSelectors);
		}
		
		return result;
	}

	
	//-----------------------------------------------------------------
	// Getters and Setters
	//-----------------------------------------------------------------
	public List<DiseaseSelector> getDiseaseSelectors() {
		return diseaseSelectors;
	}

	public void setDiseaseSelectors(List<DiseaseSelector> diseaseSelectors) {
		this.diseaseSelectors = diseaseSelectors;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public boolean isOtherCompleted() {
		return otherCompleted;
	}

	public void setOtherCompleted(boolean otherCompleted) {
		this.otherCompleted = otherCompleted;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
		
	}

	public List<Disorder> getDisorders() {
		return disorders;
	}

	public void setDisorders(List<Disorder> disorders) {
		this.disorders = disorders;
	}

    public File getDiseasesImport() {
        return diseasesImport;
    }

    public void setDiseasesImport(File diseasesImport) {
        this.diseasesImport = diseasesImport;
    }

    public String getDiseasesImportContentType() {
        return diseasesImportContentType;
    }

    public void setDiseasesImportContentType(String diseasesImportContentType) {
        this.diseasesImportContentType = diseasesImportContentType;
    }

    public String getDiseasesImportFileName() {
        return diseasesImportFileName;
    }

    public void setDiseasesImportFileName(String diseasesImportFileName) {
        this.diseasesImportFileName = diseasesImportFileName;
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

    public String getGeneListFileName() {
        return geneListFileName;
    }

    public void setGeneListFileName(String geneListFileName) {
        this.geneListFileName = geneListFileName;
    }

}
