package cfe.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Entity
@Table(name="CfeResults")
public class CfeResults implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy=IDENTITY)
    private Long cfeResultsId;

	private Date generatedTime;

	private byte[] results;
	
	private String resultsType;
	
	/*
	private String rScriptLog;
	*/
	private String phene;
	private Integer lowCutoff;
	private Integer highCutoff;
	
	public CfeResults() {
		this.cfeResultsId = null;
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
    public CfeResults(Long resultsId, String resultsType,Date generatedTime, String phene, Integer lowCutoff, Integer highCutoff) {

        this.cfeResultsId = resultsId;
        
        this.resultsType = resultsType;
        
        this.generatedTime      = generatedTime;
        this.phene              = phene;
        this.lowCutoff          = lowCutoff;
        this.highCutoff         = highCutoff;
        
        this.results = null;
    }

    public CfeResults(
            XSSFWorkbook resultsWorkbook,
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
            
    //-----------------------------------------------------------------
    // Getters and Setters
    //-----------------------------------------------------------------

    public Long getDiscoveryResultsId() {
        return cfeResultsId;
    }

    public void setDiscoveryResultsId(Long discoveryResultsId) {
        this.cfeResultsId = discoveryResultsId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="generatedTime", nullable=false, length=0)
    public Date getGeneratedTime() {
        return this.generatedTime;
    }

    public void setGeneratedTime(Date generatedTime) {
        this.generatedTime = generatedTime;
    }

    
    @Lob
    @Column(name="results", nullable=false, columnDefinition="mediumblob")
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

    /*
    public String getRScriptLog() {
        return rScriptLog;
    }

    public void setRScriptLog(String rScriptLog) {
        this.rScriptLog = rScriptLog;
    }
*/
    
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
	
}