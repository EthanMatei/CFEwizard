package cfe.model.prioritization.disease;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.opencsv.CSVReader;

import cfe.action.prioritization.DiseaseSelectionAction;
import cfe.services.prioritization.DisorderService;

public class DiseaseSelector {

    private static final Logger log = Logger.getLogger(DiseaseSelector.class.getName());

	private String psychiatricDomain;
	private boolean psychiatricDomainSelected;
	
	private String psychiatricSubDomain;
	private boolean psychiatricSubDomainSelected;
	
	private String relevantDisorder;
	private boolean relevantDisorderSelected;
	private Double coefficient;
	
	public DiseaseSelector () {
	    this.psychiatricDomain = "";
	    this.psychiatricDomainSelected = false;
	    
	    this.psychiatricSubDomain = "";
	    this.psychiatricSubDomainSelected = false;
	    
	    this.relevantDisorder = "";
	    this.relevantDisorderSelected = false;
	    this.coefficient = 0.0;
	}

	/**
	 * Sets the values for the disease selector to the values in the specified CSV file.
	 * 
	 * @param diseasesCsvFileName the name of the CSV file.
	 * @param diseasesCsvFile the File object for the CSV file.
	 */
	public static List<DiseaseSelector> importCsvFile(String diseasesCsvFileName, File diseasesCsvFile) throws Exception {

        if (diseasesCsvFileName == null || diseasesCsvFileName.isEmpty()) {
            throw new Exception("Diseases CSV file not specified.");    
        }
        
        if (!diseasesCsvFileName.endsWith(".csv")) {
            throw new Exception("Diseases CSV file \"" + diseasesCsvFileName + "\" is not a \".csv\" file.");
        }        
        
        
        if (diseasesCsvFile == null) {
            throw new Exception("Diseases import file \"" + diseasesCsvFileName +"\" could not be accessed.");
        }

        log.info("Importing diseases from file " + diseasesCsvFileName);

        List<DiseaseSelector> diseaseSelectors = DisorderService.getDiseaseSelectors();
        
        List<String[]> data = new ArrayList<String[]>();
        String[] header = new String[0];

        // Get the data and header of the CSV file

        try (CSVReader reader = new CSVReader(new FileReader(diseasesCsvFile))) {
            data = reader.readAll();
            header = data.remove(0);
            // data.forEach(x -> System.out.println(Arrays.toString(x)));
        }
        catch (Exception exception) {
            String message = "Unable to read diseases CSV file: " + exception.getLocalizedMessage();
            throw new Exception(message);
        }

        log.info("Diseases import file has " + header.length + " header columns.");
        log.info("Diseases import file has " + data.size() + " data rows.");

        if (header.length != 4) {
            String message = "Imported diseases CSV file has " + header.length + "header columns, instead of 4.";
            throw new Exception(message);
        }

        if (!header[0].contentEquals("Domain")) {
            throw new Exception("First header column in imported diseases CSV file is \"" + header[0] + "\" and not \"Domain\"");
        }

        if (!header[1].contentEquals("SubDomain")) {
            String message = "Second header column in imported diseases CSV file is \"" + header[1] + "\" and not \"SubDomain\"";
            throw new Exception(message);
        }

        if (!header[2].contentEquals("Relevant Disorder")) {
            String message = "Third header column in imported diseases CSV file is \""
                + header[1] + "\" and not \"Relevant Disorder\"";
            throw new Exception(message);
        }

        if (!header[3].contentEquals("Coefficient")) {
            String message = "Fourth header column in imported diseases CSV file is \""
                + header[1] + "\" and not \"Coefficient\"";
            throw new Exception(message);
        }
        
        for (int i = 0; i < diseaseSelectors.size(); i++) {
            DiseaseSelector diseaseSelector = diseaseSelectors.get(i);
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

                    diseaseSelectors.get(i).setRelevantDisorderSelected(true);

                    double coefficientValue = 0.0;

                    log.info("*** SETTING domain \"" + domain + "\" with coefficent " + coefficientValue);

                    try {
                        coefficientValue = Double.parseDouble(coefficient);
                    }
                    catch (NumberFormatException exception) {
                        ;
                    }
                    diseaseSelectors.get(i).setCoefficient(coefficientValue);
                }
            }
        }
        
        return diseaseSelectors;
	}
	
	public String getPsychiatricDomain() {
		return psychiatricDomain;
	}

	public void setPsychiatricDomain(String psychiatricDomain) {
		this.psychiatricDomain = psychiatricDomain;
	}

	public boolean isPsychiatricDomainSelected() {
		return psychiatricDomainSelected;
	}

	public void setPsychiatricDomainSelected(boolean psychiatricDomainSelected) {
		this.psychiatricDomainSelected = psychiatricDomainSelected;
	}

	public String getPsychiatricSubDomain() {
		return psychiatricSubDomain;
	}

	public void setPsychiatricSubDomain(String psychiatricSubDomain) {
		this.psychiatricSubDomain = psychiatricSubDomain;
	}

	public boolean isPsychiatricSubDomainSelected() {
		return psychiatricSubDomainSelected;
	}

	public void setPsychiatricSubDomainSelected(boolean psychiatricSubDomainSelected) {
		this.psychiatricSubDomainSelected = psychiatricSubDomainSelected;
	}

	public String getRelevantDisorder() {
		return relevantDisorder;
	}

	public void setRelevantDisorder(String relevantDisorder) {
		this.relevantDisorder = relevantDisorder;
	}

	public boolean isRelevantDisorderSelected() {
		return relevantDisorderSelected;
	}

	public void setRelevantDisorderSelected(boolean relevantDisorderSelected) {
		this.relevantDisorderSelected = relevantDisorderSelected;
	}

	public Double getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(Double coefficient) {
		this.coefficient = coefficient;
	}
	
}