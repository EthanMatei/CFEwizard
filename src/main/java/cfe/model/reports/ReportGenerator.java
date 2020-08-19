package cfe.model.reports;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cfe.enums.ScoringWeights;
import cfe.model.CfeScore;
import cfe.model.CfeScores;
import cfe.model.VersionNumber;


/**
 * Class that generates reports in Excel format.
 * 
 * @author Jim Mullen
 *
 */
public class ReportGenerator {

	private static Log log = LogFactory.getLog(ReportGenerator.class);
	
	/**
	 * Generates reports in Excel format.
	 * 
	 * @param reportName the name of the report to generate
	 * @param reportFormat indicates the Excel format to generate (either "xls" or "xlsx",
	 *                     which corresponds to the file extension used).
	 * @param results 
	 * @param scores
	 * @param weights
	 * @param diseaseSelectors the diseases/disorders selected for a scoring calculation.
	 * 
	 * @return an InputStream for the generated spreadsheet.
	 * 
	 * @throws ReportException
	 */
	public static InputStream generate(String reportName, String reportFormat, CfeScores cfeScores, List<cfe.enums.ScoringWeights> weights) throws ReportException {
		InputStream fileStream = null;

		log.info("############### GENERATE: " + reportName);
		
		if (reportName.equals("scores")) {
			
			fileStream = generateScoresReport( reportFormat, cfeScores, weights);
		}

		return fileStream;
	}
	
	
	/**
	 * Generates an Excel spreadsheet with scoring information based on user selected inputs.
	 * 
	 * @param reportFormat indicates the Excel format to generate (either "xls" or "xlsx",
	 *                     which corresponds to the file extension used).
	 * @param results
	 * @param scores
	 * @param weights
	 * @param diseaseSelectors
	 * @return an InputStream for the generated spreadsheet.
	 */
	private static InputStream generateScoresReport(String reportFormat, CfeScores cfeScores, List<cfe.enums.ScoringWeights> weights) {

		log.info("############### IN GENERATE SCORES REPORT: " + reportFormat);
		Report report = new Report();
		
		InputStream fileStream = null;
		List<ReportSheet> sheets = new ArrayList<ReportSheet>();
		ReportSheet sheet = new ReportSheet();
		
	    //----------------------------------------------------------------------------------------
	    // Scores Sheet
	    //----------------------------------------------------------------------------------------
	    //sheet = getScoresSheet(reportFormat, scores);
	    sheet = getScoresSheet(reportFormat, cfeScores);
	    if (sheet != null) sheets.add(sheet);

	    //----------------------------------------------------------------------------------------
	    // Score Detail Sheet
	    //----------------------------------------------------------------------------------------
	    //sheet = getScoreDetailsSheet(reportFormat, cfeScores);
	    //if (sheet != null) sheets.add(sheet);
	    
	    //----------------------------------------------------------------------------------------
	    // Scoring Weights Sheet
	    //----------------------------------------------------------------------------------------
	    sheet = getScoringWeightsSheet(reportFormat, weights);
	    if (sheet != null) sheets.add(sheet);

	    //----------------------------------------------------------------------------------------
	    // Version Sheet
	    //----------------------------------------------------------------------------------------
	    sheet = getVersionSheet(reportFormat, cfeScores);
	    if (sheet != null) sheets.add(sheet);
	    
		report.setReportSheets( sheets );
		boolean isLandscape=false;
		boolean xlsxFormat = false;
		if (reportFormat != null && reportFormat.equals("xlsx")) xlsxFormat = true;
		fileStream = report.getExcel(isLandscape,xlsxFormat);		

		return fileStream;
	}
	
	
	/**
	 * Creates the main score sheet.
	 * 
	 * @param reportFormat
	 * @param results
	 * @return
	 */
	private static ReportSheet getScoresSheet(String reportFormat, CfeScores cfeScores) {
		ReportSheet sheet = new ReportSheet();
		
		sheet.setTitle( "CFE Wizard Scores" );
		
		String[] columnNames = {
			    "Probeset", "GeneCards Symbol", "Gene Title", "Change in expression in tracked phene",
			    "Discovery Score", "Prioritization Score", "Validation Score", "Testing Score",
			    "TOTAL Score"
		};
		sheet.setColumnNames( columnNames );

		int[] columnWidths = {
				0, 0, 0, 0,
				0, 0, 0, 0,
				0
		};
		sheet.setColumnWidths(columnWidths);
		
		String[] columnTypes = {
				"string", "string", "string", "string",
				"float", "float", "float", "float",
				"float"
		};
		sheet.setColumnTypes(columnTypes);


		int i = 0;
		for (CfeScore cfeScore: cfeScores.getScores().values()) {
			List<String> row = new ArrayList<String>();
			
			row.add(cfeScore.getProbeset());
			row.add(cfeScore.getGeneCardsSymbol());
			row.add(cfeScore.getGeneTitle());
		    row.add(cfeScore.getChangeInExpressionInTrackedPhene());
		    row.add("" + cfeScore.getDiscoveryScore());
		    row.add("" + cfeScore.getPrioritizationScore());
		    row.add("" + cfeScore.getValidationScore());
		    row.add("" + cfeScore.getTestingScore());
		    row.add("" + cfeScore.getTotalScore());
		    
			sheet.addData( row );
			
			i++;
		}
		
		/*
        for (String gene: results.getResults().keySet()) {
        	    TreeMap<String, Result> resultMap = results.getResults();
				List<String> row = new ArrayList<String>();
				row.add("" + i);
				row.add( results.getGeneNames(gene) );
				row.add(resultMap.get(gene).getScore() + "");
				
				List<Research> researchList = resultMap.get(gene).getAllResearch();
				String directionChange = "";
				String tissue = "";
				String disorder = "";
				String pubMedIds = "";
				
				boolean isFirst = true;
				for (Research research: researchList) {
					if (isFirst) isFirst = false;
					else {
						directionChange += "\n";
						tissue          += "\n";
						disorder        += "\n";
						pubMedIds       += "\n";
					}
					
					String tissueValue = research.getTissue();
					if (tissueValue == null || tissueValue.equals("null")) tissueValue = "";
					
					String directionChangeValue = research.getDirectionChange();
					if (directionChangeValue == null || directionChangeValue.equals("null")) directionChangeValue = "";
					
					tissue += tissueValue;
					directionChange += directionChangeValue;
					disorder += research.getPsychiatricDomain() + ", " + research.getSubdomain() + ", " + research.getRelevantDisorder();
					pubMedIds += research.getPubMedId();
				}

				row.add(directionChange);
				row.add(tissue);
				row.add(disorder);
				row.add(pubMedIds);
				
				sheet.addData( row );
				
				i++;
    	}
    	*/		
		return sheet;
	}
	
	
	/**
	 * Generates the score detail spreadsheet sheet.
	 * 
	 * @param reportFormat
	 * @param results
	 * @return
	 */
	private static ReportSheet getScoreDetailsSheet(String reportFormat, CfeScores cfeScores) {
		ReportSheet sheet = new ReportSheet();
		
		/*
		List<String> categoryHeaders = results.getCategoryHeaders();
		int numberOfColumns = 2 + (categoryHeaders.size() * 2);
		
		sheet.setTitle( "Score Details" );
		
		//---------------------------------------------------------------
		// Set the column names
		//---------------------------------------------------------------
		String[] columnNames = new String[numberOfColumns];
		columnNames[0] = "Gene";
		columnNames[1] = "Score";
		for (int i = 2; i < numberOfColumns; i++) {
			if (i % 2 == 0) {
				columnNames[i] = categoryHeaders.get((i/2) - 1);
			}
			else {
				columnNames[i] = "";
			}
		}
		sheet.setColumnNames( columnNames );

		//-----------------------------------------------------
		// Set the column widths
		//-----------------------------------------------------
		int[] columnWidths = new int[numberOfColumns];
		for (int i = 0; i < numberOfColumns; i++) columnWidths[i] = 0;
		sheet.setColumnWidths(columnWidths);
		
		//----------------------------------------------------------------
		// Set the column types
		//----------------------------------------------------------------
		String[] columnTypes = new String[numberOfColumns];
		columnTypes[0] = "string";
		columnTypes[1] = "float";
		for (int i = 2; i < numberOfColumns; i++) {
			if (i % 2 == 0) {
				columnTypes[i] = "float";
			}
			else {
				columnTypes[i] = "string";
			}
		}		
		sheet.setColumnTypes(columnTypes);

		//----------------------------------------------------------------------
		// Set merged regions
		//----------------------------------------------------------------------
		List<Region> mergedRegions = new ArrayList<Region>();
		for (int i = 2; i < numberOfColumns; i++) {
			if (i % 2 == 0) {
				Region region = new Region();
				region.setFromRow(0);
				region.setToRow(0);
				region.setFromColumn(i);
				region.setToColumns(i+1);
				mergedRegions.add(region);
			}
		}	
		sheet.setMergedRegions(mergedRegions);


		for (String gene: results.getResults().keySet()) {

			TreeMap<String, Result> resultMap = results.getResults();

			Result result = resultMap.get(gene);

			
			List<String> row = new ArrayList<String>();
			row.add( results.getGeneNames(gene) );
			row.add( result.getScore() + "");

            TreeMap<ScoringWeights,CategoryResult> categoryResults = result.getCategoryResults();
			for (ScoringWeights weight: categoryResults.keySet()) {
				CategoryResult categoryResult = categoryResults.get(weight);
				row.add( categoryResult.getScore() + "");
				List<Research> researchList = categoryResult.getResearchList();
				String researchData = "";
				
				boolean isFirst = true;
				for (Research research: researchList) {
					
					String directionChange = research.getDirectionChange();
					if (directionChange == null) directionChange = "";
					
					String tissue = research.getTissue();
					if (tissue == null) tissue = "";

					String pubMedId = research.getPubMedId();
					if (pubMedId == null) pubMedId = "";
					
					String disorder = "(" + research.getPsychiatricDomain() + ", " + research.getSubdomain() + ", " + research.getRelevantDisorder() + ")";
					
					if (isFirst) {
						isFirst = false;
					}
					else {
						researchData += "\n";
					}
					resea)rchData += directionChange + " " + tissue + " " + disorder + " " + pubMedId;
				}
				row.add(researchData);
			}

			sheet.addData( row );

		}
		*/		
		return sheet;
	}
	

	
	private static ReportSheet getVersionSheet(String reportFormat, CfeScores cfeScores) {

		ReportSheet sheet = new ReportSheet();

		sheet.setTitle( "Version" );

		String[] columnNames = {"CFE Wizard Version", "Time Output Genereated"};
		sheet.setColumnNames( columnNames );

		int[] columnWidths = {0, 0};
		sheet.setColumnWidths(columnWidths);

		String[] columnTypes = {"string", "string"};
		sheet.setColumnTypes(columnTypes);

		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();


		List<String> row = new ArrayList<String>();

		row.add( VersionNumber.VERSION_NUMBER );
		row.add( dateFormat.format(date) );

		sheet.addData( row );

		return sheet;
	}
	
	
	private static ReportSheet getScoringWeightsSheet(String reportFormat, List<cfe.enums.ScoringWeights> weights) {

		ReportSheet sheet = new ReportSheet();

		sheet.setTitle( "Scoring Weights" );

		String[] columnNames = {"Name", "Weight"};
		sheet.setColumnNames( columnNames );

		int[] columnWidths = {0, 0};
		sheet.setColumnWidths(columnWidths);

		String[] columnTypes = {"string", "float"};
		sheet.setColumnTypes(columnTypes);


		for (cfe.enums.ScoringWeights weight: weights) {
		    List<String> row = new ArrayList<String>();

		    row.add( weight.getLabel() );
		    row.add( "" + weight.getWeight() );

		    sheet.addData( row );
		}

		return sheet;
	}

}