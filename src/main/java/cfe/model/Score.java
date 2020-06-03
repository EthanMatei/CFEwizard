package cfe.model;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cfe.action.CalculateScores;
import cfe.enums.ScoringWeights;
import cfe.model.disease.Disease;
import cfe.model.disease.DiseaseSelection;
import cfe.model.disease.DiseaseSelector;
import cfe.model.results.CategoryResult;
import cfe.model.results.Result;
import cfe.model.results.Results;
import cfe.services.HuBrainGexService;
import cfe.services.HuBrainMetService;
import cfe.services.HuBrainProtService;
import cfe.services.HuGeneAssocService;
import cfe.services.HuGeneCNVService;
import cfe.services.HuGeneLinkageService;
import cfe.services.HuPerGexService;
import cfe.services.HuPerMetService;
import cfe.services.HuPerProtService;
import cfe.services.NhBrainGexService;
import cfe.services.NhBrainMetService;
import cfe.services.NhBrainProtService;
import cfe.services.NhGeneAssocService;
import cfe.services.NhGeneCNVService;
import cfe.services.NhGeneLinkageService;
import cfe.services.NhPerGexService;
import cfe.services.NhPerMetService;
import cfe.services.NhPerProtService;


public class Score {
	
	private static final Log log = LogFactory.getLog(Score.class);
	
	public static Results calculate(GeneListInput geneListInput, DiseaseSelection diseaseSelection, List<cfe.enums.ScoringWeights> weights) 
	        throws Exception {
		
		Results results = new Results();
		PrintWriter writer = null;
		boolean debug = false;

		//--------------------------------------------------------------------------
		// If debug set, try to set up a debug file to write information to
		//--------------------------------------------------------------------------
		if (debug) {
			try {
				File file = new File("score-debug.txt");
				writer = new PrintWriter(file, "UTF-8");
				writer.println("Score Test");
				writer.flush();
				log.info("+++++++++++++++++++++++++++++++++++++++++++++++++ file write did not fail.");
			}
			catch (IOException exception) {
				log.error("********************** COULD NOT WRITE TO SCORING TEST FILE");
			}
		}
		
		
		
		//----------------------------------------------------------------
		// Create Research Data and add genes from gene list (if any)
		//----------------------------------------------------------------
		ResearchData researchData = new ResearchData();
		if (geneListInput != null && geneListInput.size() > 0) {
			for (String gene: geneListInput.getGeneList()) {
			    researchData.add(gene);
			}
		}
		
		//---------------------------------------------------------------
		
		List<ModelInterface> huBrainGexs     = HuBrainGexService.getSelected(diseaseSelection, geneListInput);
		researchData.add("HUBRAIN",  "HuBrainGex", huBrainGexs);
		
		List<ModelInterface> huBrainMets       = HuBrainMetService.getSelected(diseaseSelection, geneListInput);
		researchData.add("HUBRAIN",  "HuBrainMet", huBrainMets);
		
		List<ModelInterface> huBrainProts     = HuBrainProtService.getSelected(diseaseSelection, geneListInput);
		researchData.add("HUBRAIN",  "HuBrainProt", huBrainProts);
		
		List<ModelInterface> huGeneAssocs     = HuGeneAssocService.getSelected(diseaseSelection, geneListInput);
		researchData.add("HUGENE",  "HuGeneAssoc", huGeneAssocs);
		
		List<ModelInterface> huGeneCNVs         = HuGeneCNVService.getSelected(diseaseSelection, geneListInput);
		researchData.add("HUGENE",  "HuGeneCNV", huGeneCNVs);
		
		//List<ModelInterface> huGeneLinkages = HuGeneLinkageService.getSelected(diseaseSelection, geneListInput);
		//researchData.add("HUGENE",  "HuGeneLinkage", huGeneLinkages);
		
		List<ModelInterface> huPerGexs         = HuPerGexService.getSelected(diseaseSelection, geneListInput);
		researchData.add("HUPER",  "HuPerGex", huPerGexs);
		
		List<ModelInterface> huPerMets           = HuPerMetService.getSelected(diseaseSelection, geneListInput);
		researchData.add("HUPER",  "HuPerMet", huPerMets);
		
		List<ModelInterface> huPerProts         = HuPerProtService.getSelected(diseaseSelection, geneListInput);
		researchData.add("HUPER",  "HuPerProt", huPerProts);
		
		List<ModelInterface> nhBrainGexs     = NhBrainGexService.getSelected(diseaseSelection, geneListInput);
		researchData.add("NHBRAIN",  "NhBrainGex", nhBrainGexs);
		    
		List<ModelInterface> nhBrainMets       = NhBrainMetService.getSelected(diseaseSelection, geneListInput);
		researchData.add("NHBRAIN",  "NhBrainMet", nhBrainMets);
		
		List<ModelInterface> nhBrainProts     = NhBrainProtService.getSelected(diseaseSelection, geneListInput);
		researchData.add("NHBRAIN",  "NhBrainProt", nhBrainProts);
		
		List<ModelInterface> nhGeneAssocs     = NhGeneAssocService.getSelected(diseaseSelection, geneListInput);
		researchData.add("NHGENE",  "NhGeneAssoc", nhGeneAssocs);
		
		List<ModelInterface> nhGeneCNVs         = NhGeneCNVService.getSelected(diseaseSelection, geneListInput);
		researchData.add("NHGENE",  "NhGeneCNV", nhGeneCNVs);
		
		//List<ModelInterface> nhGeneLinkages = NhGeneLinkageService.getSelected(diseaseSelection, geneListInput);
		//researchData.add("NHGENE",  "NhGeneLinkage", nhGeneLinkages);
		
		List<ModelInterface> nhPerGexs         = NhPerGexService.getSelected(diseaseSelection, geneListInput);
		researchData.add("NHPER",  "NhPerGex", nhPerGexs);
		
		List<ModelInterface> nhPerMets           = NhPerMetService.getSelected(diseaseSelection, geneListInput);
		researchData.add("NHPER",  "NhPerMet", nhPerMets);
		
		List<ModelInterface> nhPerProts         = NhPerProtService.getSelected(diseaseSelection, geneListInput);
		researchData.add("NHPER",  "NhPerProt", nhPerProts);

		//-------------------------------------------------
		// DEBUG
		//-------------------------------------------------
		if (debug) {
			if (geneListInput != null) {
				writer.println("GENE LIST:");
				List<String> geneList = geneListInput.getGeneList();
				writer.print("(");
				int count = 0;
				for (String gene: geneList) {
					if (count != 0) writer.print(", ");
					if (count % 10 == 0) writer.println();
					writer.print(gene);
					count++;
				}
				writer.println(")");
				writer.println();
			}


			if (diseaseSelection != null) {
				writer.println();
				writer.println("DISEASE SELECTION");
			    for (DiseaseSelector disease: diseaseSelection.getDiseaseSelectors()) {
			    	if (disease.isRelevantDisorderSelected()) {
			    	    writer.println("    " + disease.getPsychiatricDomain() + " "
			    	                         + disease.getPsychiatricSubDomain() + " "
			    	                         + disease.getRelevantDisorder() + " "
			    	                         + disease.getCoefficient()
			    	                         + " (" + disease.isRelevantDisorderSelected() + ")");
			    	}
			    }
			}
			
			if (researchData != null) {
				writer.println();
				writer.println("RESEARCH DATA:");
				Set<String> genes = researchData.getGenes();
				for (String gene: genes) {
					List<Research> researchList = researchData.get(gene);
					writer.println("    GENE " + gene + ": " );
					for (Research research: researchList) {
						writer.println("        " + research.getCategory() + " " + research.getSubcategory() 
								+ " | " + research.getPsychiatricDomain() + " " + research.getSubdomain()
								+ " " + research.getRelevantDisorder() + " " + research.getPubMedId()
								+ " | " + research.getTissue() + " " + research.getDirectionChange());
					}
				}
			}

			if (writer != null) {
				writer.flush();
				writer.close();
				log.info("++++++++++++++++++++++++++++++++++++++++++++++++++ writer closed");
			}
		}

		
    	//---------------------------------------------------------
		// Get the global scoring weights
		//---------------------------------------------------------
		double huBrainScore     = 0.0;
		double huPerScore       = 0.0;
		double huGeneAssocScore = 0.0;
		double huGeneCNVScore   = 0.0;
			
		double nhBrainScore     = 0.0;
		double nhPerScore       = 0.0;
		double nhGeneAssocScore = 0.0;
		double nhGeneCNVScore   = 0.0;
			
		for (cfe.enums.ScoringWeights weight: weights) {
			switch (weight) {
			case HUBRAIN:
				huBrainScore = weight.getScore();
				break;
			case HUPER:
				huPerScore = weight.getScore();
				break;
			case HUGENEASSOC:
				huGeneAssocScore = weight.getScore();
				break;
			case HUGCNV:
				huGeneCNVScore = weight.getScore();
				break;
			case NHBRAIN:
				nhBrainScore = weight.getScore();
				break;
			case NHPER:
				nhPerScore = weight.getScore();
				break;
			case NHGENEASSOC:
				nhGeneAssocScore = weight.getScore();
				break;
			case NHGCNV:
				nhGeneCNVScore = weight.getScore();
				break;
			}
		}

		//---------------------------------------------------------------
		// For each gene in the (applicable) research data,
		// calculate the scoring result
		//---------------------------------------------------------------
		for (String gene: researchData.getGenes()) {

			CategoryResult cResult = null;
			Result result = new Result();
			
			//-----------------------------------------------
			// HUBRAIN
			//-----------------------------------------------
			Set<Disease> d1 = researchData.getUniqueDiseases(gene, "HUBRAIN");
			cResult = new CategoryResult();
			cResult.setResearchList( researchData.getResearchByScoringCategory(gene, ScoringWeights.HUBRAIN) );
			double huBrainCategoryScore = 0.0;
			for (Disease d: d1) {
				double coefficient = diseaseSelection.getCoefficent(d.getDomain(), d.getSubdomain(), d.getRelevantDisorder());
				huBrainCategoryScore += coefficient * huBrainScore;
			}
			huBrainCategoryScore = Math.min(huBrainScore, huBrainCategoryScore);
			cResult.setScore( huBrainCategoryScore );
			result.add(ScoringWeights.HUBRAIN, cResult);

			//----------------------------------------------------
			// HUPER
			//----------------------------------------------------
			Set<Disease> d2 = researchData.getUniqueDiseases(gene, "HUPER");
			cResult = new CategoryResult();
			cResult.setResearchList( researchData.getResearchByScoringCategory(gene, ScoringWeights.HUPER) );
			double huPerCategoryScore = 0.0;
			for (Disease d: d2) {
				double coefficient = diseaseSelection.getCoefficent(d.getDomain(), d.getSubdomain(), d.getRelevantDisorder());
				huPerCategoryScore += coefficient * huPerScore;
			}
			huPerCategoryScore = Math.min(huPerScore, huPerCategoryScore);
			cResult.setScore( huPerCategoryScore );
			result.add(ScoringWeights.HUPER, cResult);
			
			//----------------------------------------------------
			// HUGENEASSOC
			//----------------------------------------------------
			Set<Disease> d3 = researchData.getUniqueDiseases(gene, "HUGENE", "HuGeneAssoc");
			cResult = new CategoryResult();
			cResult.setResearchList( researchData.getResearchByScoringCategory(gene, ScoringWeights.HUGENEASSOC) );
			double huGeneAssocCategoryScore = 0.0;
			for (Disease d: d3) {
				double coefficient = diseaseSelection.getCoefficent(d.getDomain(), d.getSubdomain(), d.getRelevantDisorder());
				huGeneAssocCategoryScore += coefficient * huGeneAssocScore;
			}
			huGeneAssocCategoryScore = Math.min(huGeneAssocScore, huGeneAssocCategoryScore);
			cResult.setScore( huGeneAssocCategoryScore );
			result.add(ScoringWeights.HUGENEASSOC, cResult);
			
			//------------------------------------------------------
			// HUGNV
			//------------------------------------------------------
			Set<Disease> d4 = researchData.getUniqueDiseases(gene, "HUGENE", "HuGeneCNV");
			cResult = new CategoryResult();
			cResult.setResearchList( researchData.getResearchByScoringCategory(gene, ScoringWeights.HUGCNV) );
			double huGeneCNVCategoryScore = 0.0;
			for (Disease d: d4) {
				double coefficient = diseaseSelection.getCoefficent(d.getDomain(), d.getSubdomain(), d.getRelevantDisorder());
				huGeneCNVCategoryScore += coefficient * huGeneCNVScore;
			}
			huGeneCNVCategoryScore = Math.min(huGeneCNVScore, huGeneCNVCategoryScore);
			cResult.setScore( huGeneCNVCategoryScore );
			result.add(ScoringWeights.HUGCNV, cResult);
			
			
			
			//------------------------------------------------------			
			// NHBRAIN
			//------------------------------------------------------
			Set<Disease> d5 = researchData.getUniqueDiseases(gene, "NHBRAIN");
			cResult = new CategoryResult();
			cResult.setResearchList( researchData.getResearchByScoringCategory(gene, ScoringWeights.NHBRAIN) );
			double nhBrainCategoryScore = 0.0;
			for (Disease d: d5) {
				double coefficient = diseaseSelection.getCoefficent(d.getDomain(), d.getSubdomain(), d.getRelevantDisorder());
				nhBrainCategoryScore += coefficient * nhBrainScore;
			}
			nhBrainCategoryScore = Math.min(nhBrainScore, nhBrainCategoryScore);
			cResult.setScore( nhBrainCategoryScore );
			result.add(ScoringWeights.NHBRAIN, cResult);
			
			//------------------------------------------------------
			// NHPER
			//------------------------------------------------------
			Set<Disease> d6 = researchData.getUniqueDiseases(gene, "NHPER");
			cResult = new CategoryResult();
			cResult.setResearchList( researchData.getResearchByScoringCategory(gene, ScoringWeights.NHPER) );
			double nhPerCategoryScore = 0.0;
			for (Disease d: d6) {
				double coefficient = diseaseSelection.getCoefficent(d.getDomain(), d.getSubdomain(), d.getRelevantDisorder());
				nhPerCategoryScore += coefficient * nhPerScore;
			}
			nhPerCategoryScore = Math.min(nhPerScore, nhPerCategoryScore);
			cResult.setScore( nhPerCategoryScore );
			result.add(ScoringWeights.NHPER, cResult);
			
			//------------------------------------------------------
			// NHGENEASSOC
			//------------------------------------------------------
			Set<Disease> d7 = researchData.getUniqueDiseases(gene, "NHGENE", "NhGeneAssoc");
			cResult = new CategoryResult();
			cResult.setResearchList( researchData.getResearchByScoringCategory(gene, ScoringWeights.NHGENEASSOC) );
			double nhGeneAssocCategoryScore = 0.0;
			for (Disease d: d7) {
				double coefficient = diseaseSelection.getCoefficent(d.getDomain(), d.getSubdomain(), d.getRelevantDisorder());
				nhGeneAssocCategoryScore += coefficient * nhGeneAssocScore;
			}
			nhGeneAssocCategoryScore = Math.min(nhGeneAssocScore, nhGeneAssocCategoryScore);
			cResult.setScore( nhGeneAssocCategoryScore );
			result.add(ScoringWeights.NHGENEASSOC, cResult);
			
			//------------------------------------------------------
			// NHGNV
			//------------------------------------------------------
			Set<Disease> d8 = researchData.getUniqueDiseases(gene, "NHGENE", "NhGeneCNV");
			cResult = new CategoryResult();
			cResult.setResearchList( researchData.getResearchByScoringCategory(gene, ScoringWeights.NHGCNV) );
			double nhGeneCNVCategoryScore = 0.0;
			for (Disease d: d8) {
				double coefficient = diseaseSelection.getCoefficent(d.getDomain(), d.getSubdomain(), d.getRelevantDisorder());
				nhGeneCNVCategoryScore += coefficient * nhGeneCNVScore;
			}
			nhGeneCNVCategoryScore = Math.min(nhGeneCNVScore, nhGeneCNVCategoryScore);
			cResult.setScore( nhGeneCNVCategoryScore );
			result.add(ScoringWeights.NHGCNV, cResult);			
			

			
			result.setScore( huBrainCategoryScore + huPerCategoryScore + Math.max(huGeneAssocCategoryScore, huGeneCNVCategoryScore)
					+ nhBrainCategoryScore + nhPerCategoryScore + Math.max(nhGeneAssocCategoryScore, nhGeneCNVCategoryScore) );
			
			results.add(gene, result);
		}

		return results;
		
	}
	
}