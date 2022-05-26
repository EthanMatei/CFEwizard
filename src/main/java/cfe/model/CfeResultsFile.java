package cfe.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

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
@Table(name="CfeResultsFile", uniqueConstraints = {@UniqueConstraint(columnNames = {"cfeResultsId", "fileType"})})
public class CfeResultsFile implements Serializable, Comparable<CfeResultsFile> {

    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy=IDENTITY)
    private Long cfeResultsFileId;
    
    // CfeResultsFileType
    private String fileType;

    private String mimeType;

    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="creationTime", nullable=false)
    private Date creationTime;

    @Lob
    @Column(name="content", nullable=true, columnDefinition="mediumblob")
    private byte[] content;
    
    @ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="cfeResultsId", unique=true)
    private CfeResults cfeResults;  // CfeResults that this file belongs to

    // Import file version (want to be able to store generated and imported file so they could be compared for differences)
    // NOTE: Don't want to allow import of info files, since these are metadata files, which are not used in calculations, and might
    // have info on what files were imported and generated ???
    // Note: Also don't want to allow import of script files ??? - might if script is run externally
    /*
    private String importFileName;
    private Date importTime;
    
    @Lob
    @Column(name="content", nullable=true, columnDefinition="mediumblob")
    private byte[] importContent;
    
    @Lob
    @Column(nullable=true, columnDefinition="text")
    String importComment;
    */
    
    @Override
    public int compareTo(CfeResultsFile file) {
      return this.fileType.compareTo(file.fileType);
    }
    
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
    public CfeResultsFile(String fileType, String mimeType, byte[] content) {
        this.fileType = fileType;
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
    public CfeResultsFile(String fileType, String mimeType, String content) {
        this.fileType = fileType;
        this.mimeType = mimeType;
        this.content  = content.getBytes(StandardCharsets.UTF_8);
        this.creationTime = new Date();
    }
    
    public void setToTextFile(String fileType, String content) {
        this.fileType = fileType;
        this.mimeType = "text/plain";
        this.content  = content.getBytes(StandardCharsets.UTF_8);
        this.creationTime = new Date();
    }
    
    public void setToCsvFile(String fileType, String content) {
        this.fileType = fileType;
        this.mimeType = "text/csv";
        this.content  = content.getBytes(StandardCharsets.UTF_8);
        this.creationTime = new Date();
    }
    
    public Long getCfeResultsFileId() {
        return this.cfeResultsFileId;
    }

    public void setCfeResultsFileId(Long cfeResultsFileId) {
        this.cfeResultsFileId = cfeResultsFileId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    public String getMimeType() {
        return this.mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

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
    
    @Transient
    public String getFileSuffix() {
        String suffix = "";
        if (this.mimeType.equals("text/plain")) {
            suffix = ".txt";
        }
        else if (this.mimeType.equals("text/csv")) {
            suffix = ".csv";
        }
        else {
            suffix = ".txt";
        }
        return suffix;
    }
    
    @Transient
    public String getFileName() {
        String fileName = "";
        fileName = this.fileType + this.getFileSuffix();
        return fileName;
    }
    
}