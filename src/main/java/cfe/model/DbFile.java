package cfe.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
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


/**
 * Class for storing files in the database.
 * 
 * @author Jim Mullen
 *
 */
@Entity
@Table(name="DbFile")
public class DbFile extends Model implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long dbFileId;
    private String fileName;
    private String mimeType;
    private Date creationTime;
    private byte[] content;

    public DbFile() {
        super();
    }
    
    /**
     * Constructor for binary files.
     * 
     * @param fileName
     * @param mimeType
     * @param content
     */
    public DbFile(String fileName, String mimeType, byte[] content) {
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
    public DbFile(String fileName, String mimeType, String content) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.content  = content.getBytes(StandardCharsets.UTF_8);
        this.creationTime = new Date();
    }
    
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="dbFileId", unique=true, nullable=false)
    public Long getDbFileId() {
        return this.dbFileId;
    }

    public void setDbFileId(Long dbFileId) {
        this.dbFileId = dbFileId;
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
    
    @Lob
    @Column(name="content", nullable=true, columnDefinition="mediumblob")
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
}