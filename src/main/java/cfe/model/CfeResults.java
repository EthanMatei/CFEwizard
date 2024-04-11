package cfe.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.annotations.Type;

import cfe.utils.DataTable;

@Entity
@Table(name="CfeResults")
public class CfeResults implements Serializable {
	
	private static final long serialVersionUID = 1L;
    
	private static Logger log = Logger.getLogger(CfeResults.class.getName());
    
	@Id @GeneratedValue(strategy=IDENTITY)
    private Long cfeResultsId;

	private Date generatedTime;

    @Lob
    @Column(name="results", nullable=true, columnDefinition="mediumblob")
	private byte[] results;
	
	private String resultsType;
	
    @Lob
    @Column(nullable=true, columnDefinition="mediumtext")
	private String discoveryRScriptLog;
    
	private String phene;
	private Double lowCutoff;
	private Double highCutoff;
	
	private boolean uploaded; // Indicates if the results have been uploaded (vs. generated), which means they may
	                          // have been manually modified.
	
    @OneToMany(fetch=FetchType.EAGER, targetEntity=CfeResultsFile.class, mappedBy="cfeResults", cascade=CascadeType.ALL)
	private Set<CfeResultsFile> cfeResultsFile;
	
	public CfeResults() {
		this.cfeResultsId = null;
		
		this.cfeResultsFile = new HashSet<CfeResultsFile>(0);
		this.uploaded = false;
	}

	/**
	 * Constructor needed to retrieve results data without the spreadsheet (to speed up display in table form).
	 * 
	 * @param resultsId
	 * @param resultsType
	 * @param generatedTime
	 * @param phene
	 * @param lowCutoff
	 * @param highCutoff
	 */
    public CfeResults(Long cfeResultsId, String resultsType,Date generatedTime, String phene, Double lowCutoff, Double highCutoff) {

        this.cfeResultsId = cfeResultsId;
        
        this.resultsType = resultsType;
        
        this.generatedTime      = generatedTime;
        this.phene              = phene;
        this.lowCutoff          = lowCutoff;
        this.highCutoff         = highCutoff;
        
        this.results = null;
        
        this.uploaded = false;
        
        this.cfeResultsFile = new HashSet<CfeResultsFile>(0);
    }

    public CfeResults(
            Workbook resultsWorkbook,
            String resultsType, 
            Date generatedTime,
            String phene,
            Double lowCutoff,
            Double highCutoff
        ) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        resultsWorkbook.write(bos);
        bos.close();
        this.results = bos.toByteArray();
        
        this.resultsType = resultsType;
        
        this.generatedTime = generatedTime;
        this.phene         = phene;
        this.lowCutoff     = lowCutoff;
        this.highCutoff    = highCutoff;
        this.uploaded      = false;
        
        this.cfeResultsFile = new HashSet<CfeResultsFile>(0);
    }
/*
    public CfeResults(
            SXSSFWorkbook resultsWorkbook,
            String resultsType, 
            Date generatedTime,
            String phene,
            Double lowCutoff,
            Double highCutoff
        ) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        resultsWorkbook.write(bos);
        bos.close();
        this.results = bos.toByteArray();
        
        this.resultsType = resultsType;
        
        this.generatedTime      = generatedTime;
        this.phene              = phene;
        this.lowCutoff          = lowCutoff;
        this.highCutoff         = highCutoff;
    }
  */
    
    /**
     * Copies the attribute fields (used to identify the results) from the specified results.
     * 
     * @param results the results from which attribute fields are to be copied.
     */
    public void copyAttributes(CfeResults results) {
        this.phene      = results.phene;
        this.lowCutoff  = results.lowCutoff;
        this.highCutoff = results.highCutoff;
    }
    
    public String asString() {
        String value = "";
        value += "ID: " + this.cfeResultsId + "\n";
        value += "results size: " + results.length + "\n";
        value += "results type: " + this.resultsType + "\n";
        value += "generated time: " + this.generatedTime + "\n";
        value += "phene: " + this.phene + " [" + this.lowCutoff + ", " + this.highCutoff + "]\n";
        return value;
    }
    
    //-----------------------------------------------------------------
    // Getters and Setters
    //-----------------------------------------------------------------

    public Long getCfeResultsId() {
        return cfeResultsId;
    }

    public void setCfeResultsId(Long cfeResultsId) {
        this.cfeResultsId = cfeResultsId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="generatedTime", nullable=false, length=0)
    public Date getGeneratedTime() {
        return this.generatedTime;
    }

    public void setGeneratedTime(Date generatedTime) {
        this.generatedTime = generatedTime;
    }

    
    public byte[] getResults() {
        return results;
    }

    public void setResults(byte[] results) {
        this.results = results;
    }

    @Transient
    /**
     * Get the spreadsheet consisting of the individual CSV Files.
     * 
     * @return
     * @throws Exception
     */
    public XSSFWorkbook getSpreadsheet() throws Exception {
        Set<CfeResultsFile> files = this.getCfeResultsFile();
        
        Map<String,DataTable> sheetMap = new TreeMap<String,DataTable>();   // Might need custom sorting here?
        for (CfeResultsFile file: files) {
            if (file.getMimeType().contentEquals("text/csv")) {
                String csvString = file.getContentAsString();
                DataTable dataTable = new DataTable(null);
                dataTable.initializeToCsvString(csvString);
                String sheetName = file.getFileType();
                sheetMap.put(sheetName, dataTable);
            }
        }
        
        XSSFWorkbook workbook = DataTable.createWorkbook(sheetMap);
        return workbook;
    }
    
    /**
     * Gets the specified file as a data table (only works for CSV files).
     * 
     * @param fileType
     * @return the specified file as a data table, or null if the file does not exist.
     * @throws Exception
     */
    public DataTable getFileAsDataTable(String fileType) throws Exception {
        DataTable dataTable = null;
        CfeResultsFile file = this.getFile(fileType);

        if (file != null) {
            if (!file.getMimeType().contentEquals("text/csv")) {
                throw new Exception("File with mime type \"" + file.getMimeType() + "\" cannot be converted to a data table.");
            }

            String csvString = file.getContentAsString();
            dataTable = new DataTable(null);
            dataTable.initializeToCsvString(csvString);
        }
        
        return dataTable;
    }
    
    @Transient
    public DataTable getSheetAsDataTable(String sheetName, String key) throws Exception {
        DataTable dataTable = new DataTable(key);
        
        XSSFWorkbook workbook = this.getResultsSpreadsheet();
        
        if (workbook == null) {
            throw new Exception("No results spreadsheet found for data table.");
        }
        
        XSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new Exception("No sheet \"" + sheetName + "\" found in results workbook.");
        }
        
        dataTable.initializeToWorkbookSheet(sheet);
        
        return dataTable;
    }
    
    @Transient
    public XSSFWorkbook getResultsSpreadsheet() throws Exception {
        InputStream fileStream;
        fileStream = new ByteArrayInputStream( this.results );
        XSSFWorkbook workbook = new XSSFWorkbook(fileStream);
        return workbook;
    }
    
    @Transient
    public SXSSFWorkbook getResultsStreamingSpreadsheet() throws Exception {
        XSSFWorkbook workbook = this.getResultsSpreadsheet();
        
        int rowAccessWindowSize = 100;
        SXSSFWorkbook streamingWorkbook = new SXSSFWorkbook(workbook, rowAccessWindowSize);
        return streamingWorkbook;
    }
    
    @Transient
    public void setResultsSpreadsheet(XSSFWorkbook workbook) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }
        byte[] bytes = bos.toByteArray();
        this.results = bytes;
    }
    
    @Transient
    /**
     * Gets a map of data tables that corresponds to the sheets in the results workbook.
     * The map maps from spreadsheet name to the corresponding data table.
     * 
     * @return
     * @throws Exception
     */
    public LinkedHashMap<String, DataTable> getDataTables() throws Exception {
        LinkedHashMap<String, DataTable> dataTableMap = new LinkedHashMap<String, DataTable>();
        
        XSSFWorkbook workbook = this.getResultsSpreadsheet();
        
        int numSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numSheets; i++) {
            String sheetName = workbook.getSheetName(i);
            XSSFSheet sheet = workbook.getSheetAt(i);
            
            DataTable dataTable = new DataTable(null);
            dataTable.initializeToWorkbookSheet(sheet);
            
            dataTableMap.put(sheetName, dataTable);
        }
        return dataTableMap;
    }
    
    public static void filterByPhene(List<CfeResults> cfeResults, String phene) {

        if (!phene.equals("ALL")) {
            for (int i = cfeResults.size() - 1; i >= 0; i--) {
                CfeResults results = cfeResults.get(i);
                String resultsPhene = results.getPhene();
                
                if (resultsPhene == null) {
                    resultsPhene = "";
                }
                resultsPhene = resultsPhene.trim();
                


                if (!resultsPhene.equals(phene)) {
                    cfeResults.remove(i);
                }
            }
        }
    }
    
    public String getDiscoveryRScriptLog() {
        return this.discoveryRScriptLog;
    }

    public void setDiscoveryRScriptLog(String discoveryRScriptLog) {
        this.discoveryRScriptLog = discoveryRScriptLog;
    }
    
    public String getPhene() {
        return phene;
    }

    public String getResultsType() {
        return resultsType;
    }

    public void setResultsType(String resultsType) {
        this.resultsType = resultsType;
    }

    public void setPhene(String phene) {
        this.phene = phene;
    }

    public Double getLowCutoff() {
        return lowCutoff;
    }

    public void setLowCutoff(Double lowCutoff) {
        this.lowCutoff = lowCutoff;
    }

    public Double getHighCutoff() {
        return highCutoff;
    }

    public void setHighCutoff(Double highCutoff) {
        this.highCutoff = highCutoff;
    }

    public Set<CfeResultsFile> getCfeResultsFile() {
        return cfeResultsFile;
    }

    public void setCfeResultsFile(Set<CfeResultsFile> cfeResultsFile) {
        this.cfeResultsFile = cfeResultsFile;
    }


    //@Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    //@Column(nullable = false, columnDefinition = "BOOLEAN")
    //@Column(nullable=false)
    //@Column(nullable = false, columnDefinition = "TINYINT(1)")
    //@Type(type = "org.hibernate.type.NumbericBooleanType")
    //@Column(name = "uploaded", columnDefinition = "BOOLEAN")
    //@Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    //@Column(nullable=false)
    //@Type(type = "org.hibernate.type.NumbericBooleanType")
    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    @Transient Map<String, CfeResultsFile> getCfeResultsFileMap() {
        Map<String, CfeResultsFile> map = new HashMap<String, CfeResultsFile>();
        
        for (CfeResultsFile file: this.getCfeResultsFile()) {
            map.put(file.getFileType(), file);
        }
        
        return map;
    }
    
    
    @Transient
    /**
     * Gets the file with the specified type and returns it, or null, if the
     * file is not founds.
     * 
     * @param fileType the type of the file to get.
     * @return
     */
    public CfeResultsFile getFile(String fileType) {
        CfeResultsFile file = null;
        for (CfeResultsFile searchFile: this.getCfeResultsFile()) {
            if (searchFile.getFileType().contentEquals(fileType)) {
                file = searchFile;
                break;
            }
        }
        return file;
    }
    
    public void addTextFile(String fileType, String content) {
        CfeResultsFile file = new CfeResultsFile();
        file.setToTextFile(fileType, content);
        file.setCfeResults(this);   // Set the file's parent to this object
        this.cfeResultsFile.add(file);
    }

    public void addTextFile(String fileType, byte[] content) {
        CfeResultsFile file = new CfeResultsFile();
        file.setToTextFile(fileType, content);
        file.setCfeResults(this);   // Set the file's parent to this object
        this.cfeResultsFile.add(file);
    }
    
    public void addCsvFile(String fileType, String content) {
        CfeResultsFile file = new CfeResultsFile();
        log.info("New File object created.");
        file.setToCsvFile(fileType, content);
        log.info("File object st to CSV File.");
        file.setCfeResults(this);   // Set the file's parent to this object
        log.info("CSV File parent set.");
        this.cfeResultsFile.add(file);
        log.info("CSV file added to cfeResults.");
    }

    
    public void addCsvFile(String fileType, byte[] content) {
        CfeResultsFile file = new CfeResultsFile();
        file.setToCsvFile(fileType, content);
        file.setCfeResults(this);   // Set the file's parent to this object
        this.cfeResultsFile.add(file);
    }
    @Transient
    /**
     * Gets the set of all file types (whether generated, imported, or both)
     * @return
     */
    public Set<String> getFileTypes() {
        Set<String> set = new TreeSet<String>();  // Use TreeSet, so file types are ordered alphabetically
        for (CfeResultsFile file: this.cfeResultsFile) {
            set.add(file.getFileType());
        }
        return set;
    }
    
    public void addTextFiles(CfeResults addResults) {
        for (CfeResultsFile cfeResultsFile: addResults.cfeResultsFile) {
            
            if (cfeResultsFile.getMimeType().contentEquals("text/plain")) {
                String fileType = cfeResultsFile.getFileType();
                byte[] content = cfeResultsFile.getContent();
                this.addTextFile(fileType, content);
            }
        }
    }
    
    public void addCsvAndTextFiles(CfeResults addResults) {
        
        log.info("Number of CSV and test files to add: " + addResults.cfeResultsFile.size());
        
        for (CfeResultsFile cfeResultsFile: addResults.cfeResultsFile) {
            
            if (cfeResultsFile.getMimeType().contentEquals("text/plain")) {
                String fileType = cfeResultsFile.getFileType();
                byte[] content = cfeResultsFile.getContent();
                this.addTextFile(fileType, content);
            }
            else if (cfeResultsFile.getMimeType().contentEquals("text/csv")) {
                String fileType = cfeResultsFile.getFileType();
                byte[] content = cfeResultsFile.getContent();
                this.addCsvFile(fileType, content);
            }
        }
    }	
}