package cfe.model.disease;

import java.util.ArrayList;
import java.util.List;


public class DiseaseSelection {
	private List<DiseaseSelector> diseaseSelectors;

	public DiseaseSelection() {
		this.diseaseSelectors = new ArrayList<DiseaseSelector>();
	}
	
	public DiseaseSelection(List<DiseaseSelector> diseaseSelectors) {
		this.diseaseSelectors = diseaseSelectors;
	}
	
	public DiseaseSelector get(int index) {
	    return this.diseaseSelectors.get(index);
	}
	
	public List<DiseaseSelector> getDiseaseSelectors() {
		return diseaseSelectors;
	}

	public void setDiseaseSelectors(List<DiseaseSelector> diseaseSelectors) {
		this.diseaseSelectors = diseaseSelectors;
	}
	
	public double getCoefficent(String domain, String subdomain, String disorder) {
		double coefficient = 0.0;
		for (DiseaseSelector disease: this.diseaseSelectors) {
			if (disease.getPsychiatricDomain().equals(domain)
					&& disease.getPsychiatricSubDomain().equals(subdomain)
					&& disease.getRelevantDisorder().equals(disorder)) {
				coefficient = disease.getCoefficient();
			}
		}
		return coefficient;
	}
	
	/**
	 * Indicates if the disorder specified by the inputs was selected for calculations; the
	 * comparison done is case-insensitive, and ignores leading and trailing blanks, however
	 * intra-word spaces are not ignored, so "a b" will NOT match "a    b".
	 * 
	 * @param domain
	 * @param subdomain
	 * @param disorder
	 * @return
	 */
	public boolean isSelected(String domain, String subdomain, String disorder) {
		boolean isSelected = false;
		
		if (domain != null && subdomain != null && disorder != null) {
			domain    = domain.trim();
			subdomain = subdomain.trim();
			disorder  = disorder.trim();
			
		    for (DiseaseSelector diseaseSelector: this.diseaseSelectors) {
			    if (diseaseSelector.getPsychiatricDomain().trim().equalsIgnoreCase(domain)
			    	&& diseaseSelector.getPsychiatricSubDomain().trim().equalsIgnoreCase(subdomain)
			    	&& diseaseSelector.getRelevantDisorder().trim().equalsIgnoreCase(disorder)
			    	&& diseaseSelector.isRelevantDisorderSelected()
			    	&& diseaseSelector.getCoefficient() > 0.0) {
			    	isSelected = true;
			    	break;
			    }
		    }
		}
		return isSelected;
	}
	
}