package cfe.dao;

import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import cfe.model.CfeResults;
import cfe.model.CfeResultsFile;
import cfe.services.CfeResultsFileService;


public class CfeResultsFileDao extends AbstractDao<CfeResultsFile> {
	
	private static final Logger log = Logger.getLogger(CfeResultsFileDao.class.getName());
	
	
	public CfeResultsFileDao( Session sess, Transaction tx) {
		super(CfeResultsFile.class, sess, tx);
	}
	
	/*
	public List<CfeResultsFile> getAllMetadata() {
	    List<CfeResultsFile> files = null;
	    
	    String queryString = "Select new CfeResultsFile(cfeResultsFileId, name, fileName, mimeType, cfeResultsId)"
	        + " from CfeResults";
	    
        Query<CfeResultsFile> query = sess.createQuery(queryString, CfeResultsFile.class);
        files = query.list();
	    return files;
	}
    */
	
	public List<CfeResultsFile> getFilesForCfeResults(Long cfeResultsId) {
	   List<CfeResultsFile> files = null;
	       
	   String queryString = "Select from CfeResultsFile where cfeResultsId = :id";
	   Query<CfeResultsFile> query = sess.createQuery(queryString, CfeResultsFile.class);
	   query.setParameter("id", cfeResultsId);
	   
	   files = query.list();
	     
	   return files;    
	}
   
}
