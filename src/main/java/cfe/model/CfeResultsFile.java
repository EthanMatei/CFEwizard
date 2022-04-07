package cfe.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;


/**
 * Class for storing files in the database.
 * 
 * @author Jim Mullen
 *
 */
@Entity
@Table(name="CfeResultsFile", uniqueConstraints = {@UniqueConstraint(columnNames = {"cfeResultsId", "name"})})
public class CfeResultsFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy=IDENTITY)
    private Long cfeResultsFileId;
    
    private String name;
    private String fileName;
    private String mimeType;
    private Date creationTime;

    @Lob
    @Column(name="content", nullable=true, columnDefinition="mediumblob")
    private byte[] content;
    
    @ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="cfeResultsId", unique=true)
    private CfeResults cfeResults;  // CfeResults that this file belongs to


    //private Long cfeResultsId;
    
    public CfeResultsFile() {
        super();
    }
    
    /**
     * Constructor for binary files.
     * 
     * @param fileName
     * @param mimeType
     * @param content
     */
    public CfeResultsFile(String fileName, String mimeType, byte[] content) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.content  = content;
        this.creationTime = new Date();
    }
    
    /**
     * Constructor for text (non-binary) files.
     * 
     * @param fileName
     * @param mimeType
     * @param content
     */
    public CfeResultsFile(String fileName, String mimeType, String content) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.content  = content.getBytes(StandardCharsets.UTF_8);
        this.creationTime = new Date();
    }
    
    public Long getCfeResultsFileId() {
        return this.cfeResultsFileId;
    }

    public void setCfeResultsFileId(Long cfeResultsFileId) {
        this.cfeResultsFileId = cfeResultsFileId;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return this.fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getMimeType() {
        return this.mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="creationTime", nullable=false, length=0)
    public Date getCreationTime() {
        return this.creationTime;
    }
    
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
    

    public byte[] getContent() {
        return this.content;
    }
    

    @Transient
    public String getContentAsString() {
        String contentString = "";
        if (this.content != null) {
            contentString = new String(this.content, StandardCharsets.UTF_8);
        }
        return contentString;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
    
    @Transient
    public void setContent(String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        this.content = bytes;
    }

    
    /*
    public Long getCfeResultsId() {
        return cfeResultsId;
    }

    public void setCfeResultsId(Long cfeResultsId) {
        this.cfeResultsId = cfeResultsId;
    }
    */
    
    public CfeResults getCfeResults() {
        return cfeResults;
    }

    public void setCfeResults(CfeResults cfeResults) {
        this.cfeResults = cfeResults;
    }
}