package cfe.model.discovery;

import static javax.persistence.GenerationType.IDENTITY;

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

@Entity
@Table(name="DiscoveryResults")
public class DiscoveryResults implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy=IDENTITY)
    private Long discoveryResultsId;

	private Date generatedTime;

	private byte[] results;
	/*
	private String rScriptLog;
	*/
	private String phene;
	private Integer lowCutoff;
	private Integer highCutoff;
	
	public DiscoveryResults() {
		this.discoveryResultsId = null;
	}


    public DiscoveryResults(Long discoveryResultsId, Date generatedTime, String phene, Integer lowCutoff, Integer highCutoff) {
        this.discoveryResultsId = discoveryResultsId;
        this.generatedTime      = generatedTime;
        this.phene              = phene;
        this.lowCutoff          = lowCutoff;
        this.highCutoff         = highCutoff;
        
        this.results = null;
    }
            
    //-----------------------------------------------------------------
    // Getters and Setters
    //-----------------------------------------------------------------

    public Long getDiscoveryResultsId() {
        return discoveryResultsId;
    }

    public void setDiscoveryResultsId(Long discoveryResultsId) {
        this.discoveryResultsId = discoveryResultsId;
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