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
import java.util.Map;
import java.util.Set;

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

import cfe.utils.DataTable;

@Entity
@Table(name="CfeResults")
public class CfeResults implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
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
	private Integer lowCutoff;
	private Integer highCutoff;
	
    @OneToMany(fetch=FetchType.EAGER, targetEntity=CfeResultsFile.class, mappedBy="cfeResults", cascade=CascadeType.ALL)
	private Set<CfeResultsFile> cfeResultsFile;
	
	public CfeResults() {
		this.cfeResultsId = null;
		
		this.cfeResultsFile = new HashSet<CfeResultsFile>(0);
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
    public CfeResults(Long cfeResultsId, String resultsType,Date generatedTime, String phene, Integer lowCutoff, Integer highCutoff) {

        this.cfeResultsId = cfeResultsId;
        
        this.resultsType = resultsType;
        
        this.generatedTime      = generatedTime;
        this.phene              = phene;
        this.lowCutoff          = lowCutoff;
        this.highCutoff         = highCutoff;
        
        this.results = null;
        
        this.cfeResultsFile = new HashSet<CfeResultsFile>(0);
    }

    public CfeResults(
            Workbook resultsWorkbook,
            String resultsType, 
            Date generatedTime,
            String phene,
            Integer lowCutoff,
            Integer highCutoff
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
        
        this.cfeResultsFile = new HashSet<CfeResultsFile>(0);
    }
/*
    public CfeResults(
            SXSSFWorkbook resultsWorkbook,
            String resultsType, 
            Date generatedTime,
            String phene,
            Integer lowCutoff,
            Integer highCutoff
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

    public Integer getLowCutoff() {
        return lowCutoff;
    }

    public void setLowCutoff(Integer lowCutoff) {
        this.lowCutoff = lowCutoff;
    }

    public Integer getHighCutoff() {
        return highCutoff;
    }

    public void setHighCutoff(Integer highCutoff) {
        this.highCutoff = highCutoff;
    }

    public Set<CfeResultsFile> getCfeResultsFile() {
        return cfeResultsFile;
    }

    public void setCfeResultsFile(Set<CfeResultsFile> cfeResultsFile) {
        this.cfeResultsFile = cfeResultsFile;
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
    
    public void addCsvFile(String fileType, String content) {
        CfeResultsFile file = new CfeResultsFile();
        file.setToCsvFile(fileType, content);
        file.setCfeResults(this);   // Set the file's parent to this object
        this.cfeResultsFile.add(file);
    }
	
}