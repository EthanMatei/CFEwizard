package cfe.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.print.DocFlavor.STRING;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Table;

/**
 * Class for storing the the merged data tables used to construct the Discovery cohort.
 * 
 * @author Jim Mullen
 *
 */
public class CohortDataTable extends DataTable {
	
	private static final Logger log = Logger.getLogger(CohortDataTable.class.getName());
	
	public static final Long RANDOM_SEED = 10972359723095792L;
	   
	private String pheneTable; 
	
	public CohortDataTable() {
		super("PheneVisit");
	}
    
    public CohortDataTable(String pheneTable) {
        super("PheneVisit");
        this.pheneTable = pheneTable;
    }
    
    // OBSOLETE
    public int getPheneColumnIndex(String phene) throws Exception {
        int index = -1;
        
        int matchCount = 0;
        for (int i = 0; i < this.columns.size(); i++) {
            String column = this.columns.get(i);
            // Remove table name from column (if any)
            String modifiedColumn = column.trim().replaceFirst("[^\\.]+\\.",  "");
            //log.info("Modified column: \"" + modifiedColumn + "\"." + " - Phene: \"" + phene + "\".");
            if (modifiedColumn.equalsIgnoreCase(phene)) {
                //log.info("MATCH FOUND!!!!!!!!!!!!!!!!!!!!!!!!!.");
                matchCount++;
                index = i;
            }
        }
        
        if (matchCount > 1) {
            throw new Exception("Phene column found " + matchCount + " times in the cohort data table.");    
        }
        
        log.info("Returning phene column index: " + index);
        return index;
    }
    
    
	public void initializeToAccessTable(Table table) throws IOException {

		// Reset key, because sometimes PheneVisit is misspelled.
		for (Column col: table.getColumns()) {
			String columnName = col.getName();
			if (columnName.trim().equalsIgnoreCase("PheneVisit")
			        || columnName.trim().equalsIgnoreCase("Phene Visit")) {
				this.key = columnName;
				break;
			}
		}
		
		super.initializeToAccessTable(table);
		
		// Now, after table has been initialized, make sure key and column name are "PheneVisit"
		this.key = "PheneVisit";
		for (int i = 0; i < this.columns.size(); i++) {
			if (columns.get(i).trim().equalsIgnoreCase("PheneVisit")
			        || columns.get(i).trim().equalsIgnoreCase("Phene Visit")) {
				columns.set(i, "PheneVisit");
				break;
			}
		}
    }
	
	public CohortDataTable mergePheneTable(CohortDataTable mergeTable) throws Exception {

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
            // ADD THIS LATER??? Add the table name to all phene table column names
            // if (columnName.equals(key)) {
                columnName = mergeTable.name + "." + columnName;
            // }
            
            columns2.add(columnName);
        }   
        
        merge.columns.addAll(columns1);
        merge.columns.addAll(columns2);
        
        // log.info("Columns for phene table merge have been merged.");
        
        //String columnsString = String.join(", ", merge.columns);
        
        Set<String> keys1 = this.index.keySet();
        
        for (String key: keys1) {
            ArrayList<String> mergedRow = new ArrayList<String>();
            mergedRow.addAll(this.index.get(key));
            
            // log.info("Going to merge row for key \""+ key + "\".");
            ArrayList<String> mergeRow = mergeTable.getRow(key);
            if (mergeRow == null) {
                // log.info("merge row is null.");
                // If the row doesn't exist in the phene table, create a blank row
                mergeRow = new ArrayList<String>();
                mergeRow.add(key);   // Add the phene value
                for (int i = 1; i < columns2.size(); i++) {
                    mergeRow.add("");
                }
            }
            // log.info("merge row size: " + mergeRow.size());
            
            mergedRow.addAll(mergeRow);
            
            merge.addRow(mergedRow);
        }
        
        //merge.deleteLastColumn(key);
        
        log.info("Returning merge table; number of rows: " + merge.getNumberOfRows());
        return merge;
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

	public TreeSet<String> getDiscoveryCohortSubjects(String phene, double lowCutoff, double highCutoff) throws Exception {
	    
	    int subjectIndex = this.getColumnIndexTrimAndIgnoreCase("Subject");
        int pheneIndex   = this.getColumnIndex(phene.trim());
        
        log.info("phene index returned: " + pheneIndex);
        
        if (subjectIndex < 0) {
            throw new Exception("Column \"Subject\" could not be found in the cohort data table.");
        }
        
        if (pheneIndex < 0) {
            throw new Exception("Column \"" + phene + "\" could not be found in the cohort data table.");
        }

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
     * @return a list with the first element containing a set of validation subjects and the second element containing
     *     a set of testing subjects
     */
    public List<TreeSet<String>> setValidationAndTestingCohorts(
            String phene, double lowCutoff, double highCutoff,
            // String clinicalPhene, double clinicalHighCutoff,
            List<PheneCondition> pheneConditions, double percentInValidation
            ) throws Exception {
        
        log.info("Cohort Data size: " + this.data.size());
        log.info("Phene: " + phene + ", low cutoff: " + lowCutoff + ", high cutoff: " + highCutoff);
        
        int subjectIndex = this.getColumnIndexTrimAndIgnoreCase("Subject");
        int pheneIndex   = this.getColumnIndexTrimAndIgnoreCase(phene.trim());
        
        //int clinicalPheneIndex = this.getColumnIndexTrimAndIgnoreCase(clinicalPhene.trim());
        
        if (subjectIndex < 0) {
            throw new Exception("Can't find \"Subject\" column in cohort data.");
        }
        else if (pheneIndex < 0) {
            throw new Exception("Can't find discovery phene \"" + phene + "\" column in cohort data.");
        }
        //else if (clinicalPheneIndex < 0) {
        //    throw new Exception ("Can't find clinical phene \"" + clinicalPhene + "\" column in cohort data.");
        //}
        
        log.info("subject index: " + subjectIndex + ", pheneIndex: " + pheneIndex);

        // Find subjects with low score and subjects with high score
        TreeSet<String> allSubjects        = new TreeSet<String>();
        TreeSet<String> lowScoreSubjects   = new TreeSet<String>();
        TreeSet<String> highScoreSubjects  = new TreeSet<String>();
        TreeSet<String> pheneConditionsSubjects = new TreeSet<String>(); // Subjects who meet the additional phene conditions
        
        TreeSet<String> clinicalConditionSubjects = new TreeSet<String>();
        
        for (ArrayList<String> row : this.data) {
            Map<String,String> rowMap = new HashMap<String,String>();
            for (int i = 0; i < this.columns.size(); i++) {
                rowMap.put(columns.get(i), row.get(i));
            }
            
            String pheneValueString = row.get(pheneIndex);
            String subject = row.get(subjectIndex);
            
            allSubjects.add(subject);
            
            //String clinicalPheneValueString = row.get(clinicalPheneIndex);
            
            // If the subject meets the clinical phene condition
            //if (clinicalPheneValueString != null) {
            //    clinicalPheneValueString = clinicalPheneValueString.trim();
            //    if (StringUtil.isFloat(clinicalPheneValueString)) {
            //        double clinicalPheneValue = Double.parseDouble(clinicalPheneValueString);
            //        if (clinicalPheneValue >= clinicalHighCutoff) {
            //            clinicalConditionSubjects.add(subject);
            //        }
            //    }
            //}
            
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
        TreeSet<String> cohortSubjects = allSubjects;
        cohortSubjects.removeAll(discoverySubjects);
        cohortSubjects.retainAll(pheneConditionsSubjects); // Intersection of non-discovery subjects with
                                                           // with subjects that meet all phene conditions
        
        log.info("discoverySubjects: {" + StringUtils.join(discoverySubjects, ",") + "}");
        log.info("pheneConditionSubjects: {" + StringUtils.join(pheneConditionsSubjects, ",") + "}");
        log.info("cohortSubjects: {" + StringUtils.join(cohortSubjects, ",") + "}");
        
        //TreeSet<String> cohortSubjects = clinicalConditionSubjects;
        //cohortSubjects.removeAll(discoverySubjects)array;
        
        int cohortIndex = this.getColumnIndex("Cohort");
        if (cohortIndex == -1) {
            throw new Exception("Could not find \"Cohort\" column in cohort data table.");
        }
        
        // Add validation (Clinical) cohort columns
        // NOTE: this probably needs to be moved to a new method. At this point, we don't
        // know which subjects are in the Validation (Clinical) cohort and which are in the Testing
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
            Map<String,String> rowMap = new HashMap<String,String>();
            for (int i = 0; i < this.columns.size(); i++) {
                rowMap.put(columns.get(i), row.get(i));
            }
            
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
                    row.set(validationCohortIndex, "0");
                    row.set(testingCohortIndex, "1");
                }
                else if (pheneValue != null && pheneValue >= highCutoff && !clinicalConditionSubjects.contains(subject)) {
                    row.set(validationIndex, "High Validation");
                    row.set(valCategoryIndex, "High");
                    row.set(validationCohortIndex, "0");
                    row.set(testingCohortIndex, "1");
                }
                else {
                    row.set(validationIndex, "nothing");
                    row.set(valCategoryIndex, "nothing");
                    row.set(validationCohortIndex,  "0");
                    row.set(testingCohortIndex, "0");
                }
            }
            else if (cohortSubjects.contains(subject)) {
                if (PheneCondition.isTrue(pheneConditions, rowMap)) {
                    // If this phene visit meets the addition clinical cohort phene conditions
                    row.set(validationIndex, "Clinically Severe");
                    row.set(valCategoryIndex, "Clinical");
                    row.set(validationCohortIndex,  "1");
                    row.set(testingCohortIndex, "0");
                }
                else {
                    row.set(validationIndex, "nothing");
                    row.set(valCategoryIndex, "nothing");
                    row.set(validationCohortIndex,  "0");
                    row.set(testingCohortIndex, "0");
                }
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

        TreeSet<String> validationSubjects = new TreeSet<String>();
        TreeSet<String> testingSubjects    = new TreeSet<String>();
        
        Random rand = new Random(RANDOM_SEED);
        Collections.shuffle(subjects, rand);
        int count = subjects.size();

        for (int i = 0; i < count; i++) {
            double percent = (i + 0.5) / count;


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
        
        List<TreeSet<String>> results = new ArrayList<TreeSet<String>>();
        results.add(validationSubjects);
        results.add(testingSubjects);
        return results;
    }
 
    public TreeSet<String> getCohort(String cohort) throws Exception {
        // OR, we could start with all in, and then randomly select 
        List<String> validationCohortSubjectsList = this.getValues("Subject", cohort, "Cohort");
        TreeSet<String> validationCohortSubjects = new TreeSet<String>();
        validationCohortSubjects.addAll(validationCohortSubjectsList);

        return validationCohortSubjects;
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
	public CohortTable getDiscoveryCohort(String phene, double lowCutoff, double highCutoff) throws Exception {
		// Get the subject column index
	   	int subjectIndex = this.getColumnIndexTrimAndIgnoreCase("Subject");
	    if (subjectIndex == -1) {
	        throw new Exception("Subject column could not be found in cohort data table.");
	    }
	     
		// Get the selected phene column index
		int pheneIndex = this.getColumnIndex(phene.trim());
		if (pheneIndex == -1) {
		    throw new Exception("Phene column \"" + phene + "\" could not be found in the cohort data table while trying to get discovery cohort.");
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
	
    public void enhance(String phene, double lowCutoff, double highCutoff) throws Exception {
        log.info("Enhancement of cohort data table started for phene \"" + phene 
                + "\" with low cutoff = " + lowCutoff + " and high cutoff = " + highCutoff + ".");
        
        log.info("Columns: " + StringUtils.join(this.columns, ","));
        
        int subjectIndex = this.getColumnIndexTrimAndIgnoreCase("Subject");
        
        if (subjectIndex < 0) {
            throw new Exception("Column \"Subject\" not found in cohort data table.");
        }
        
        this.insertColumn("Cohort", 1, "");
        
        int cohortIndex = this.getColumnIndex("Cohort");
        
        log.info("Cohort index: " + cohortIndex);
        
        TreeSet<String> cohortSubjects = this.getDiscoveryCohortSubjects(phene, lowCutoff, highCutoff);

        log.info("Cohort subjects retrieved.");
        
        for (ArrayList<String> dataRow: this.data) {
            if (cohortSubjects.contains(dataRow.get(subjectIndex))) {
                dataRow.set(cohortIndex, "discovery");
            }
        }
        
        log.info("Setting of cohort column is done.");
        
        // Add and set "VisitNumber" column
        int visitNumberIndex = 1;
        this.insertColumn("VisitNumber", visitNumberIndex, "");
        String[] visitNumberSortColumns = {"Subject", "Visit Date"};
        this.sort(visitNumberSortColumns);
        
        log.info("Sort for visit number complete.");
        
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
        
        log.info("Before cohort data table enhancement sort.");
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
			int lastRow = sheet.getLastRowNum();
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
	 * Recreate generation of bigData table in the
	 * access integration R script, so that the database does not need to be
	 * re-uploaded for the discovery scoring phase.
	 *
	 * @return
	 * @throws Exception
	 */
	public DataTable getBigData(String phene) throws Exception {
	    DataTable bigData = new DataTable(null);
	    
	    String bigDataKey = "Subject Identifiers.PheneVisit";
   
	    //ArrayList<String> phenes       = this.getPhenes();
	    ArrayList<String> demographics = this.getTableNonKeyColumns("Demographics");
	    ArrayList<String> diagnosis    = this.getTableNonKeyColumns("Diagnosis");
	    ArrayList<String> subjectIdentifiers = this.getTableNonKeyColumns("Subject Identifiers");
	       
        ArrayList<String> columns = new ArrayList<String>();
        
	    columns.add(bigDataKey);
	    //columns.addAll(phenes);
	    columns.add(phene);
	    columns.addAll(demographics);
	    columns.addAll(diagnosis);
	    columns.addAll(subjectIdentifiers);
	       
	    log.info("demographics columns: " + String.join(", ", demographics));
	       
	    log.info("bigData columns: " + String.join(", ", columns));

	    // Get only the columns needed from this cohort data table
	    bigData = this.filter(bigDataKey, columns);
	    
	    // Rename the key column and key
	    bigData.setColumnName(0, "PheneVisit");
	    bigData.setKey("PheneVisit");
	    
	       
        log.info("bigData columns: " + String.join(", ", bigData.getColumnNames()));
	    
        //String[] sortColumns = {"Subject", "Visit Date", "Phene Visit"};
        //bigData.sort(sortColumns);
	    
	    return bigData;
	}
	
	/**
	 * Gets the next phene visit column index from the start index. This method
	 * searches to the next to last position, because the last position is
	 * for microarray data.
	 * 
	 * @param startIndex
	 * @return the index of the next phene visit column, or -1 if not found
	 */
	public int getNextPheneVisitColumnIndex(int startIndex)
	{
	    int index = -1;
	    for (int i = startIndex; i < this.getNumberOfColumns() - 1; i++) {
	        if (this.getColumnName(i).matches(".*\\.PheneVisit")) {
	            index = i;
	            break;
	        }
	    }
	    return index;
	}
	
	/**
	 * NOTE: This method assumes that:
	 * - the Diagnosis table values are the values right before the first phene table values.
	 * - all the phene table values are stored contiguously together
	 * - the last phene table column is the one before the last column (which is the microarray table
	 *   data column.
	 * 
	 * @return
	 * @throws Exception
	 */
	public Map<String,ArrayList<ColumnInfo>> getPheneMap() throws Exception
	{
	    Map<String,ArrayList<ColumnInfo>> pheneMap = new TreeMap<String,ArrayList<ColumnInfo>>();
	    
	    // The diagnosis table should come before the first phene table, so find the start of it,
	    // and then look for the next PheneVisit column, which should be the start of the first
	    // phene table.
	    int diagnosisPheneVisitIndex = this.getColumnIndex("Demographics.PheneVisit");
	    if (diagnosisPheneVisitIndex < 0) {
	        throw new Exception("The Diagnosis table PheneVisit column could not be found in cohort data table.");
	    }
	    
	    int pheneStartIndex = this.getNextPheneVisitColumnIndex(diagnosisPheneVisitIndex + 1);
	    
	    while (pheneStartIndex != -1) {
	        String pheneTable = this.getColumnName(pheneStartIndex);
	        pheneTable = pheneTable.substring(0, pheneTable.indexOf('.'));
	        
	        int pheneEndIndex = this.getNextPheneVisitColumnIndex(pheneStartIndex + 1);
	        if (pheneEndIndex == -1) {
	            pheneEndIndex = this.getNumberOfColumns() - 1;
	        }
	        
	        for (int columnIndex = pheneStartIndex + 1; columnIndex < pheneEndIndex; columnIndex++) {
                ArrayList<ColumnInfo> columnInfos = new ArrayList<ColumnInfo>();
	            if (pheneMap.containsKey(pheneTable)) {
	                columnInfos = pheneMap.get(pheneTable);
	            }
	            ColumnInfo columnInfo = new ColumnInfo();
	            columnInfo.setColumnName(this.getColumnName(columnIndex));
	            columnInfo.setTableName(pheneTable);
	            columnInfos.add(columnInfo);
                pheneMap.put(pheneTable, columnInfos);
	        }
	        
	        pheneStartIndex = this.getNextPheneVisitColumnIndex(pheneStartIndex + 1);
	    }
	    
	    return pheneMap;
	}
	
	/**
	 * Note: assumes that phene table names have been prepended to the phene names.
	 * @return
	 * @throws Exception
	 */
    public ArrayList<String> getPheneList() throws Exception {
        ArrayList<String> pheneList = new ArrayList<String>();
        
        // The diagnosis table should come before the first phene table, so find the start of it,
        // and then look for the next PheneVisit column, which should be the start of the first
        // phene table.
        int diagnosisPheneVisitIndex = this.getColumnIndex("Diagnosis.PheneVisit");
        if (diagnosisPheneVisitIndex < 0) {
            throw new Exception("The Diagnosis table PheneVisit column could not be found in cohort data table.");
        }
        
        int pheneStartIndex = this.getNextPheneVisitColumnIndex(diagnosisPheneVisitIndex + 1);
        
        while (pheneStartIndex != -1) {
            String pheneTable = this.getColumnName(pheneStartIndex);
            pheneTable = pheneTable.substring(0, pheneTable.indexOf('.'));
            
            int pheneEndIndex = this.getNextPheneVisitColumnIndex(pheneStartIndex + 1);
            if (pheneEndIndex == -1) {
                pheneEndIndex = this.getNumberOfColumns() - 1;
            }
            
            for (int columnIndex = pheneStartIndex + 1; columnIndex < pheneEndIndex; columnIndex++) {
                String pheneName = this.getColumnName(columnIndex);
                pheneList.add(pheneName);
            }
            
            pheneStartIndex = this.getNextPheneVisitColumnIndex(pheneStartIndex + 1);
        }
        
        return pheneList;
    }	
}
