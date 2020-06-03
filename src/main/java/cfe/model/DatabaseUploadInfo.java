package cfe.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import cfe.enums.Tables.TblNames;

@Entity
@Table(name=TblNames.DATABASE_UPLOAD_INFO)
public class DatabaseUploadInfo {

    @Id @GeneratedValue(strategy=IDENTITY)
	private Integer databaseUpdloadInfoId;
	private String databaseName;
	private String uploadFileName;
	private Date   uploadTime;
	
	
	//----------------------------------------------
	// Getters and Setters
	//----------------------------------------------
	public Integer getDatabaseUpdloadInfoId() {
		return databaseUpdloadInfoId;
	}

	public void setDatabaseUpdloadInfoId(Integer databaseUpdloadInfoId) {
		this.databaseUpdloadInfoId = databaseUpdloadInfoId;
	}

	public DatabaseUploadInfo() {
		super();
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	
	
}