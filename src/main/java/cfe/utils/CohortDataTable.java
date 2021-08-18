package cfe.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.opencsv.CSVReader;

import cfe.action.DiscoveryAction;
import cfe.model.CfeResultsSheets;

/**
 * Class for storing the the merged data tables used to construct the Discovery cohort.
 * 
 * @author Jim Mullen
 *
 */
public class CohortDataTable extends DataTable {
	
	private static final Log log = LogFactory.getLog(CohortDataTable.class);
	
	public static final Long RANDOM_SEED = 10972359723095792L;
	   
	private String pheneTable; 
	
	public CohortDataTable() {
		super("PheneVisit");
	}
    
    public CohortDataTable(String pheneTable) {
        super("PheneVisit");
        this.pheneTable = pheneTable;
    }
    
	public void initializeToAccessTable(Table table) throws IOException {

		// Reset key, because sometimes PheneVisit is misspelled.
		for (Column col: table.getColumns()) {
			String columnName = col.getName();
			if (columnName.trim().equalsIgnoreCase("PheneVisit")) {
				this.key = columnName;
				break;
			}
		}
		
		super.initializeToAccessTable(table);
		
		// Now, after table has been initialized, make sure key and column name are "PheneVisit"
		this.key = "PheneVisit";
		for (int i = 0; i < this.columns.size(); i++) {
			if (columns.get(i).trim().equalsIgnoreCase("PheneVisit")) {
				columns.set(i, "PheneVisit");
				break;
			}
		}
    }
	
	public CohortDataTable merge(CohortDataTable mergeTable) throws Exception {
		if (this.key == null || mergeTable.key == null) {
			throw new Exception("Merge of tables without a key defined.");
		}
		
		// If this cohort data table doesn't have a phene table, but the merged
		// table does, set this table's phene table to that of the merged table
		if ((this.pheneTable == null || this.pheneTable.isEmpty())
		        && mergeTable.pheneTable != null && !mergeTable.pheneTable.isEmpty()) {
		    this.pheneTable = mergeTable.pheneTable;
		}
		
		CohortDataTable merge = new CohortDataTable();
		
		merge.keyIndex = this.keyIndex;
		
		ArrayList<String> columns1 = new ArrayList<String>();
		for (String columnName: this.columns) {
			if (columnName.equals(key)) {
				columnName = this.name + "." + columnName;
			}
			columns1.add(columnName);
		}
		
		ArrayList<String> columns2 = new ArrayList<String>();
		for (String columnName: mergeTable.columns) {
			if (columnName.equals(key)) {
				columnName = mergeTable.name + "." + columnName;
			}
			columns2.add(columnName);
		}	
		
		merge.columns.addAll(columns1);
		merge.columns.addAll(columns2);
		
		//String columnsString = String.join(", ", merge.columns);
		
		Set<String> keys1 = this.index.keySet();
		Set<String> keys2 = mergeTable.index.keySet();
		keys1.retainAll(keys2);
		
		for (String key: keys1) {
			ArrayList<String> mergedRow = new ArrayList<String>();
			mergedRow.addAll(this.index.get(key));
			mergedRow.addAll(mergeTable.index.get(key));
			merge.addRow(mergedRow);
		}
		
		//merge.deleteLastColumn(key);
		
		return merge;
	}

	public TreeSet<String> getDiscoveryCohortSubjects(String phene, int lowCutoff, int highCutoff) {
        
	    int subjectIndex = this.getColumnIndexTrimAndIgnoreCase("Subject");
        int pheneIndex   = this.getColumnIndexTrimAndIgnoreCase(phene.trim());

	    // Find subjects with low score and subjects with high score
	    TreeSet<String> lowScoreSubjects   = new TreeSet<String>();
	    TreeSet<String> highScoreSubjects  = new TreeSet<String>();

	    for (ArrayList<String> row : this.data) {
	        String pheneValueString = row.get(pheneIndex);
	        String subject = row.get(subjectIndex);
	        if (pheneValueString != null) {
	            pheneValueString = pheneValueString.trim();
                if (pheneValueString.matches("\\d+") || pheneValueString.matches("\\d+\\.\\d*")) {
	                double pheneValue = Double.parseDouble(pheneValueString);

	                if (pheneValue <= lowCutoff) {
	                    lowScoreSubjects.add(subject);
	                }

	                if (pheneValue >= highCutoff) {
	                    highScoreSubjects.add(subject);
	                }
	            }
	        }
	    }

	    // Set cohort subjects as the intersection of subject sets with low and high phene scores, i.e., 
	    // subjects with both low and high phene values, minus subjects that have intermediate scores
	    TreeSet<String> cohortSubjects = lowScoreSubjects;
	    cohortSubjects.retainAll(highScoreSubjects);    // intersection of sets of users with low and high scores
	    return cohortSubjects;
	}

    /**
     * Returns list of subjects for the validation and testing cohorts who have a high score visit, do not have
     * a low score visit (i.e., subjects with a high visit who are not in the discovery cohort), and meet all
     * additional phene conditions specified (if any).
     * 
     * @param phene
     * @param lowCutoff
     * @param highCutoff
     * @param pheneConditions
     * @return
     */
    public TreeSet<String> setValidationAndTestingCohorts(String phene, double lowCutoff, double highCutoff,
            List<PheneCondition> pheneConditions, double percentInValidation) throws Exception {
        
        log.info("Cohort Data size: " + this.data.size());
        log.info("Phene: " + phene + ", low cutoff: " + lowCutoff + ", high cutoff: " + highCutoff);
        
        int subjectIndex = this.getColumnIndexTrimAndIgnoreCase("Subject");
        int pheneIndex   = this.getColumnIndexTrimAndIgnoreCase(phene.trim());
        
        log.info("subject index: " + subjectIndex + ", pheneIndex: " + pheneIndex);

        // Find subjects with low score and subjects with high score
        TreeSet<String> lowScoreSubjects   = new TreeSet<String>();
        TreeSet<String> highScoreSubjects  = new TreeSet<String>();
        TreeSet<String> pheneConditionsSubjects = new TreeSet<String>(); // Subjects who meet the additional phene conditions

        for (ArrayList<String> row : this.data) {
            Map<String,String> rowMap = new HashMap<String,String>();
            for (int i = 0; i < this.columns.size(); i++) {
                rowMap.put(columns.get(i), row.get(i));
            }
            
            String pheneValueString = row.get(pheneIndex);
            String subject = row.get(subjectIndex);
            
            // If the subject meets the additional phene conditions
            if (PheneCondition.isTrue(pheneConditions, rowMap)) {
                pheneConditionsSubjects.add(subject);
            }
            
            if (pheneValueString != null) {
                pheneValueString = pheneValueString.trim();
                if (StringUtil.isFloat(pheneValueString)) {
                    double pheneValue = Double.parseDouble(pheneValueString);

                    if (pheneValue <= lowCutoff) {
                        lowScoreSubjects.add(subject);
                    }

                    if (pheneValue >= highCutoff) {
                        highScoreSubjects.add(subject);
                    }
                }
            }
        }
        
        TreeSet<String> discoverySubjects = lowScoreSubjects;
        discoverySubjects.retainAll(highScoreSubjects);  

        // Set cohort subjects to high score subject, who do NOT have a low score, and who
        // meet all the additional phene conditions (if any)
        TreeSet<String> cohortSubjects = highScoreSubjects;
        cohortSubjects.removeAll(lowScoreSubjects);
        cohortSubjects.retainAll(pheneConditionsSubjects); // Intersection of subjects with high but not low visits
                                                           // with subjects that meet all phene conditions
        
        int cohortIndex = this.getColumnIndex("Cohort");
        if (cohortIndex == -1) {
            throw new Exception("Could not find \"Cohort\" column in cohort data table.");
        }
        
        // Add validation cohort columns
        // NOTE: this probably needs to be moved to a new method. At this point, we don't
        // know which subjects are in the Validation cohort and which are in the Testing
        // cohort, so these columns cannot be set
        // OR this method could be changed to calculate that also.
        // OR, we could start with all in, and then randomly select 
        this.insertColumn("Validation", cohortIndex + 1, "");
        this.insertColumn("ValCategory", cohortIndex + 2, "");
        this.insertColumn("ValidationCohort", cohortIndex + 3, "");
        this.insertColumn("TestingCohort", cohortIndex + 4, "");
        
        subjectIndex = this.getColumnIndexTrimAndIgnoreCase("Subject");
        pheneIndex   = this.getColumnIndexTrimAndIgnoreCase(phene.trim());
        int validationIndex       = this.getColumnIndex("Validation");
        int valCategoryIndex      = this.getColumnIndex("ValCategory");
        int validationCohortIndex = this.getColumnIndex("ValidationCohort");
        int testingCohortIndex    = this.getColumnIndex("TestingCohort");
        
        for (ArrayList<String> row : this.data) {
            String subject = row.get(subjectIndex);
            String pheneValueString = row.get(pheneIndex);
            Double pheneValue = null;
            if (StringUtil.isFloat(pheneValueString)) {
                pheneValue = Double.parseDouble(pheneValueString);
            }
            
            if (discoverySubjects.contains(subject)) {
                if (pheneValue != null && pheneValue <= lowCutoff) {
                    row.set(validationIndex, "Low Validation");  
                    row.set(valCategoryIndex, "Low");
                    row.set(validationCohortIndex, "1");
                    row.set(testingCohortIndex, "0");
                }
                else if (pheneValue != null && pheneValue >= highCutoff && !pheneConditionsSubjects.contains(subject)) {
                    row.set(validationIndex, "High Validation");
                    row.set(valCategoryIndex, "High");
                    row.set(validationCohortIndex, "1");
                    row.set(testingCohortIndex, "0");
                }
                else {
                    row.set(validationIndex, "nothing");
                    row.set(valCategoryIndex, "nothing");
                    row.set(validationCohortIndex,  "0");
                    row.set(testingCohortIndex, "0");
                }
            }
            else if (cohortSubjects.contains(subject)) {
                row.set(validationIndex, "Clinically Severe");
                row.set(valCategoryIndex, "Clinical");
                row.set(validationCohortIndex,  "1");
                row.set(testingCohortIndex, "0");
            }
            else {
                row.set(validationIndex, "nothing");
                row.set(valCategoryIndex, "nothing");
                row.set(validationCohortIndex, "0");
                row.set(testingCohortIndex, "0");
            }
        }
        
        List<String> subjects = new ArrayList<String>();
        subjects.addAll(cohortSubjects);

        TreeSet<String> validationSubjects = new TreeSet();
        TreeSet<String> testingSubjects    = new TreeSet();
        
        Random rand = new Random(RANDOM_SEED);
        Collections.shuffle(subjects, rand);
        int count = subjects.size();

        for (int i = 0; i < count; i++) {
            double percent = (i + 1.0) / count;


            if (percent <= percentInValidation) {
                validationSubjects.add(subjects.get(i));
            }
            else {
                testingSubjects.add(subjects.get(i));
            }
        }
        
        for (ArrayList<String> row : this.data) {
            String subject = row.get(subjectIndex);

            if (testingSubjects.contains(subject)) {
                row.set(validationCohortIndex, "0");
                row.set(testingCohortIndex, "1");
            }
        }
        return testingSubjects;
    }
    
	/**
	 * Gets the Discovery cohort.
	 * 
	 * Create 4 columns: Subject, PheneVisit, DiscoveryCohort, Date
	 * Sort by Subject, and then PheneVisit
	 * 
	 * DiscoveryCohort is 1 if subject is in cohort, date is the date of the visit.
	 * 
	 * @param low
	 * @param high
	 * @param phene
	 */
	public CohortTable getDiscoveryCohort(String phene, int lowCutoff, int highCutoff) throws Exception {
		// Get the subject column index
	   	int subjectIndex = this.getColumnIndexTrimAndIgnoreCase("Subject");
	    if (subjectIndex == -1) {
	        throw new Exception("Subject column could not be found in cohort data table.");
	    }
	     
		// Get the selected phene column index
		int pheneIndex = this.getColumnIndexTrimAndIgnoreCase(phene.trim());
		if (pheneIndex == -1) {
		    throw new Exception("Phene column \"" + phene + "\" could not be found in the cohort data table.");
		}
		
		int pheneVisitIndex = this.getColumnIndexTrimAndIgnoreCase("Subject Identifiers.PheneVisit");
		if (pheneVisitIndex == -1) {
		    throw new Exception("PheneVisit column could not be found in cohort data table.");    
		}
		
		// Get the "visit date" column index
		int visitDateIndex = this.getColumnIndexTrimAndIgnoreCase("Visit Date");
		if (visitDateIndex == -1) {
		    throw new Exception("VisitDate column could not be found in cohort data table.");
		}


		
		TreeSet<String> cohortSubjects = this.getDiscoveryCohortSubjects(phene, lowCutoff, highCutoff);
	    
	    //---------------------------------------------------------------------------
	    // Calculate the number of low and high visits for subjects in the cohort
	    //---------------------------------------------------------------------------
		int lowVisits          = 0;
		int highVisits         = 0;
		
	    for (ArrayList<String> row : this.data) {
			String pheneValueString = row.get(pheneIndex);
			String subject = row.get(subjectIndex);
			if (cohortSubjects.contains(subject) && pheneValueString != null) {
				pheneValueString = pheneValueString.trim();
				// If the phene value is an integer
                if (pheneValueString.matches("\\d+") || pheneValueString.matches("\\d+\\.\\d*")) {
					double pheneValue = Double.parseDouble(pheneValueString);
					
					if (pheneValue <= lowCutoff) {
						lowVisits++;
					}
					
					if (pheneValue >= highCutoff) {
						highVisits++;
					}
				}
			}
	    }
	    
	    //-----------------------------------------
	    // Create the cohort table
	    //-----------------------------------------
	    CohortTable cohort = new CohortTable();
	    
	    cohort.setSubjects(cohortSubjects);
	    cohort.setLowVisits(lowVisits);
	    cohort.setHighVisits(highVisits);
	    
	    cohort.key = "PheneVisit";
	    cohort.columns.add("Subject");
	    cohort.columns.add("PheneVisit");
	    cohort.columns.add("DiscoveryCohort");
	    cohort.columns.add("date");
	    
	    log.info("Going to add data rows to cohort...");
	    
	    for (ArrayList<String> cohortDataRow: this.data) {
	        ArrayList<String> cohortRow = new ArrayList<String>();
	        
	        String subject = cohortDataRow.get(subjectIndex);
	        String keyValue = cohortDataRow.get(this.keyIndex);
	        String pheneVisit = cohortDataRow.get(pheneVisitIndex);
	        
	        cohortRow.add(subject);
	        cohortRow.add(pheneVisit);
	        
	        if (cohortSubjects.contains(subject)) {
				String pheneValueString = cohortDataRow.get(pheneIndex);
				pheneValueString = pheneValueString.trim();
				
                if (pheneValueString.matches("\\d+") || pheneValueString.matches("\\d+\\.\\d*")) {
		            double pheneValue = Double.parseDouble(pheneValueString);
		        
	        	    if (pheneValue <= lowCutoff) {
	        	        cohortRow.add("1");
	        	    }
	        	    else if (pheneValue >= highCutoff) {
	        	        cohortRow.add("1");
	        	    }
	        	    else {
	        		    // Intermediate phene value
	        	        cohortRow.add("0.5");
	        	    }
			  	}
	        	else {
	        	    // Blank or non-numeric phene value
	        	    cohortRow.add("0");
	        	}
	        }
	        else {
	        	cohortRow.add("0");
	        }
	        cohortRow.add(cohortDataRow.get(visitDateIndex));
	        cohort.addRow(cohortRow);
	    }
	    
	    return cohort;
	}
	/*
	public XSSFWorkbook toXlsx(String phene, int lowCutoff, int highCutoff) {
        XSSFWorkbook workbook = super.toXlsx();
        
        this.getColumnIndex(phene);
        
        
        return workbook;
	}
	
	public XSSFSheet addToWorkbook(XSSFWorkbook workbook, String sheetName) {
		XSSFSheet sheet = super.addToWorkbook(workbook, sheetName);
		return sheet;
	}
	*/
	
    public void enhance(String phene, int lowCutoff, int highCutoff) {
        int subjectIndex = this.getColumnIndexTrimAndIgnoreCase("Subject");
        
        this.insertColumn("Cohort", 1, "");
        
        int cohortIndex = this.getColumnIndex("Cohort");
        
        TreeSet<String> cohortSubjects = this.getDiscoveryCohortSubjects(phene, lowCutoff, highCutoff);

        for (ArrayList<String> dataRow: this.data) {
            if (cohortSubjects.contains(dataRow.get(subjectIndex))) {
                dataRow.set(cohortIndex, "discovery");
            }
        }
        
        // Add and set "Visit Number" column
        int visitNumberIndex = 1;
        this.insertColumn("Visit Number", visitNumberIndex, "");
        String[] visitNumberSortColumns = {"Subject", "Visit Date"};
        this.sort(visitNumberSortColumns);
        
        String lastSubject = "";
        int visit = 0;
        for (ArrayList<String> row: this.data) {
            String subject = row.get(subjectIndex);
            if (subject.equals(lastSubject)) {
                visit++;
            } else {
                visit = 1;
                lastSubject = subject;
            }
            row.set(visitNumberIndex, visit + "");
        }
        
        String[] sortColumns = {"Cohort", "Subject", "Subject Identifiers.PheneVisit"};
        this.sortWithBlanksLast(sortColumns);
    }
    
    public void enhanceCohortDataSheet(XSSFWorkbook workbook, String sheetName, String phene, int lowCutoff, int highCutoff) {
    
        /*
        XSSFSheet sheet = workbook.getSheet(sheetName);
        
        CellStyle lowCellStyle = workbook.createCellStyle();
        lowCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        lowCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        CellStyle neutralCellStyle = workbook.createCellStyle();
        neutralCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        neutralCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        CellStyle highCellStyle = workbook.createCellStyle();
        highCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        highCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle intraCellStyle = workbook.createCellStyle();
        intraCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        intraCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        int pheneIndex = this.getColumnIndex(phene);
        int subjectIndex = this.getColumnIndex("Subject");
        
        int intraIndex = pheneIndex -1;
        int lowIndex   = pheneIndex -2;
        int highIndex  = pheneIndex -3;
        
        XSSFRow header = sheet.getRow(0);
        header.getCell(lowIndex).setCellStyle(lowCellStyle);
        header.getCell(highIndex).setCellStyle(highCellStyle);
        header.getCell(intraIndex).setCellStyle(intraCellStyle);
        
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            XSSFCell cell = row.getCell(pheneIndex);

            try {
                double dvalue = cell.getNumericCellValue();
                int value = (int) (dvalue + 0.5); // round to the nearest int
                
                if (value <= lowCutoff) {
                    cell.setCellStyle(lowCellStyle);
                }
                else if (value < highCutoff) {
                    cell.setCellStyle(neutralCellStyle);
                } 
                else {
                    // high value
                    cell.setCellStyle(highCellStyle);
                }       
            } catch (Exception exception) {
                ; // Ignore; this is a non-numeric value (perhaps blank or "NA")
            }
            
            cell = row.getCell(highIndex);
            int rowNumber = i + 1;
            int lastRow = sheet.getLastRowNum() + 1;
            String subjectCol = DataTable.columnLetter(subjectIndex);
            String pheneCol   = DataTable.columnLetter(pheneIndex);
            String highFormula = "COUNTIFS("
                    + "$" + subjectCol + "$2" + ":" + "$" + subjectCol + "$" + lastRow 
                    + "," + subjectCol + rowNumber 
                    + "," 
                    + "$" + pheneCol + "$2" + ":" + "$" + pheneCol + "$" + lastRow
                    + "," + "\">="+ highCutoff + "\""
                    + ")";
            cell.setCellFormula(highFormula);
            
            cell = row.getCell(lowIndex);
            String lowFormula = "IF(" + pheneCol + rowNumber + "=\"\"," + "0" + ","
                    + "COUNTIFS("
                    + "$" + subjectCol + "$2" + ":" + "$" + subjectCol + "$" + lastRow 
                    + "," + subjectCol + rowNumber 
                    + "," 
                    + "$" + pheneCol + "$2" + ":" + "$" + pheneCol + "$" + lastRow
                    + "," + "\"<="+ lowCutoff + "\""
                    + ")"
                    + ")";
            cell.setCellFormula(lowFormula);
            
            String lowCol = DataTable.columnLetter(lowIndex);
            String highCol = DataTable.columnLetter(highIndex);
            String intraFormula = "IF("
                    + "AND(" + lowCol + rowNumber + ">0" + "," + highCol + rowNumber + ">0)"
                    + "," + "\"INTRA\""
                    + "," + "\"no\""
                    + ")"
                    ;
             cell = row.getCell(intraIndex);
             cell.setCellFormula(intraFormula); 
        }
        */
    }
    
    
    public void addCohort(String cohortName, Set<String> subjects) throws Exception {
        
        int cohortIndex  = this.getColumnIndex("Cohort");
        int subjectIndex = this.getColumnIndex("Subject");
        
        if (cohortIndex == -1) {
            throw new Exception("Cohort column not found in cohort data table");
        }
        
        if (subjectIndex == -1) {
            throw new Exception("Subject column not found in cohort data table");
        }
        
        for (ArrayList<String> row: this.data) {
            String subject = row.get(subjectIndex);
            if (subjects.contains(subject)) {
                row.set(cohortIndex,  cohortName);
            }
        }
    }

	
	public void enhanceCohortDataSheetOriginal(XSSFWorkbook workbook, String sheetName, String phene, int lowCutoff, int highCutoff) {
        XSSFSheet sheet = workbook.getSheet(sheetName);
        
		CellStyle lowCellStyle = workbook.createCellStyle();
		lowCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		lowCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		CellStyle neutralCellStyle = workbook.createCellStyle();
		neutralCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		neutralCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		CellStyle highCellStyle = workbook.createCellStyle();
		highCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
		highCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		CellStyle intraCellStyle = workbook.createCellStyle();
		intraCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
		intraCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		int pheneIndex = this.getColumnIndex(phene);
		int subjectIndex = this.getColumnIndex("Subject");
		
		int intraIndex = pheneIndex -1;
		int lowIndex   = pheneIndex -2;
		int highIndex  = pheneIndex -3;
		
        XSSFRow header = sheet.getRow(0);
        header.getCell(lowIndex).setCellStyle(lowCellStyle);
        header.getCell(highIndex).setCellStyle(highCellStyle);
        header.getCell(intraIndex).setCellStyle(intraCellStyle);
        
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			XSSFRow row = sheet.getRow(i);
			XSSFCell cell = row.getCell(pheneIndex);

			try {
			    double dvalue = cell.getNumericCellValue();
                int value = (int) (dvalue + 0.5); // round to the nearest int
                
				if (value <= lowCutoff) {
                    cell.setCellStyle(lowCellStyle);
				}
				else if (value < highCutoff) {
                    cell.setCellStyle(neutralCellStyle);
				} 
				else {
					// high value
	                cell.setCellStyle(highCellStyle);
				}		
			} catch (Exception exception) {
				; // Ignore; this is a non-numeric value (perhaps blank or "NA")
			}
			
			cell = row.getCell(highIndex);
			int rowNumber = i + 1;
			int lastRow = sheet.getLastRowNum() + 1;
			String subjectCol = DataTable.columnLetter(subjectIndex);
			String pheneCol   = DataTable.columnLetter(pheneIndex);
			String highFormula = "COUNTIFS("
			        + "$" + subjectCol + "$2" + ":" + "$" + subjectCol + "$" + lastRow 
			        + "," + subjectCol + rowNumber 
			        + "," 
			        + "$" + pheneCol + "$2" + ":" + "$" + pheneCol + "$" + lastRow
			        + "," + "\">="+ highCutoff + "\""
			        + ")";
			cell.setCellFormula(highFormula);
			
			cell = row.getCell(lowIndex);
			String lowFormula = "IF(" + pheneCol + rowNumber + "=\"\"," + "0" + ","
			        + "COUNTIFS("
                    + "$" + subjectCol + "$2" + ":" + "$" + subjectCol + "$" + lastRow 
                    + "," + subjectCol + rowNumber 
                    + "," 
                    + "$" + pheneCol + "$2" + ":" + "$" + pheneCol + "$" + lastRow
                    + "," + "\"<="+ lowCutoff + "\""
                    + ")"
                    + ")";
			cell.setCellFormula(lowFormula);
			
			String lowCol = DataTable.columnLetter(lowIndex);
			String highCol = DataTable.columnLetter(highIndex);
			String intraFormula = "IF("
			        + "AND(" + lowCol + rowNumber + ">0" + "," + highCol + rowNumber + ">0)"
			        + "," + "\"INTRA\""
			        + "," + "\"no\""
			        + ")"
			        ;
	         cell = row.getCell(intraIndex);
	         cell.setCellFormula(intraFormula);	
		}
	}
	
	public ArrayList<String> getPhenes() throws Exception {
	    ArrayList<String> phenes = new ArrayList<String>();
	    String pheneTablePheneVisitColumn = this.pheneTable + ".PheneVisit";
	    
	    int startIndex = this.getColumnIndex(pheneTablePheneVisitColumn);
	    if (startIndex == -1) {
	        throw new Exception("Could not find phene table PheneVisit column\""
	            + pheneTablePheneVisitColumn + "\" in the cohort data table."
	        );
	    }
	    
	    startIndex += 1;
	    
	    for (int index = startIndex; index < columns.size(); index++) {
	        String column = this.getColumnName(index);
	        if (column.contains("PheneVisit")) {
	            break;
	        }
	        else {
	            phenes.add(column);
	        }
	    }
	    return phenes;
	}
	

	/**
	 * Gets the column names for the specified (originally loaded) table,
	 * excluding the PheneVisit column. This method assumes that each
	 * tables columns are stored sequentially and that the tables first
	 * column is names <table-name>.PheneVisit.
	 * 
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> getTableNonKeyColumns(String tableName) throws Exception {
	    ArrayList<String> tableColumns = new ArrayList<String>();
	    String tablePheneVisitColumn = tableName + ".PheneVisit";

	    int startIndex = this.getColumnIndex(tablePheneVisitColumn);
	    
	    if (startIndex == -1) {
	        throw new Exception("Could not find table \"" + tableName + "\" PheneVisit column\""
	                + tablePheneVisitColumn + "\" in the cohort data table."
	                );
	    }

	    startIndex += 1;

	    for (int index = startIndex; index < this.columns.size(); index++) {
	        String column = this.getColumnName(index);
	        if (column.contains("PheneVisit")) {
	            // If the next table has been encountered
	            break;
	        }
	        else {
	            tableColumns.add(column);
	        }
	    }
	    return tableColumns;
	}
	
	/**
	 * TO BE COMPLETED - try to recreate generation of bigData table in the
	 * access integration R script, so that the database does not need to be
	 * re-uploaded for the discovery scoring phase.
	 *
	 * @return
	 * @throws Exception
	 */
	public DataTable getBigData() throws Exception {
	    DataTable bigData = new DataTable(null);
	    
	    String bigDataKey = "Subject Identifiers.PheneVisit";
   
	    ArrayList<String> phenes       = this.getPhenes();
	    ArrayList<String> demographics = this.getTableNonKeyColumns("Demographics");
	    ArrayList<String> diagnosis    = this.getTableNonKeyColumns("Diagnosis");
	    ArrayList<String> subjectIdentifiers = this.getTableNonKeyColumns("Subject Identifiers");
	       
        ArrayList<String> columns = new ArrayList<String>();
        
	    columns.add(bigDataKey);
	    columns.addAll(phenes);
	    columns.addAll(demographics);
	    columns.addAll(diagnosis);
	    columns.addAll(subjectIdentifiers);
	       
	    log.info("**************** demographics columns: " + String.join(", ", demographics));
	       
	    log.info("**************** bigData columns: " + String.join(", ", columns));

	    // Get only the columns needed from this cohort data table
	    bigData = this.filter(bigDataKey, columns);
	    
	    // Rename the key column and key
	    bigData.setColumnName(0, "PheneVisit");
	    bigData.setKey("PheneVisit");
	    
	       
        log.info("**************** bigData columns: " + String.join(", ", bigData.getColumnNames()));
	    
        //String[] sortColumns = {"Subject", "Visit Date", "Phene Visit"};
        //bigData.sort(sortColumns);
	    
	    return bigData;
	}
}
