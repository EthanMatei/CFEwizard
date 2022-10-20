package cfe.calc;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cfe.model.CfeResults;
import cfe.model.prioritization.GeneListInput;
import cfe.model.prioritization.Score;
import cfe.model.prioritization.disease.DiseaseSelection;
import cfe.model.prioritization.disease.DiseaseSelector;
import cfe.model.prioritization.reports.ReportGenerator;
import cfe.model.prioritization.results.Results;
import cfe.utils.DataTable;

public class PrioritizationCalc {
    
    public static CfeResults calculateScores(
            String geneListUploadFileName,
            Long discoveryScoresResultsId,
            List<cfe.enums.prioritization.ScoringWeights> weights,
            List<DiseaseSelector> diseaseSelectors
    ) throws Exception {

        CfeResults cfeResults = null;
        
        GeneListInput geneListInput = null;
        
        DiseaseSelection diseaseSelection = new DiseaseSelection(diseaseSelectors);
        Results results = Score.calculate(geneListInput, diseaseSelection, weights);
        
        /*
        // Generate a workbook with the prioritization scores
        XSSFWorkbook workbook = ReportGenerator.generateScoresWorkbook(
                results, weights, diseaseSelectors, geneListInput,
                this.discoveryScoresResultsId, discoveryScoreCutoff, this.geneListUploadFileName 
                );
        
        // Create the prioritization CFE results
        cfeResults.setResultsSpreadsheet(workbook);

        LinkedHashMap<String,DataTable> dataTables = discoveryCfeResults.getDataTables();
        dataTables.putAll(cfeResults.getDataTables());
        
        workbook = DataTable.createWorkbook(dataTables);
        cfeResults.setResultsSpreadsheet(workbook);
        cfeResults.addTextFiles(discoveryCfeResults);   // Add log file(s) from Discovery to Prioritization
        */
        
        return cfeResults;
    }
}
