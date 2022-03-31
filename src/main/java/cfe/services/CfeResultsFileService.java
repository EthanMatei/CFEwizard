package cfe.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.CfeResultsFileDao;
import cfe.model.CfeResults;
import cfe.model.CfeResultsFile;
import cfe.utils.HibernateUtils;


public class CfeResultsFileService {
	
	private static final Logger log = Logger.getLogger(CfeResultsFileService.class.getName());
	
	
	public static List<CfeResultsFile> getAll() {

		List<CfeResultsFile> cfeResultsFiles = new ArrayList<CfeResultsFile>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		CfeResultsFileDao cfeResultsFileDao = new CfeResultsFileDao(session, tx);

		try	{
			cfeResultsFiles = cfeResultsFileDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
		} 
		finally	{
			session.close();
		}

		return cfeResultsFiles;
	}
	
	public static void save(CfeResultsFile cfeResultsFile) throws Exception {

        Session session = HibernateUtils.getSession();      
        Transaction tx = session.beginTransaction();
        CfeResultsFileDao cfeResultsFileDao = new CfeResultsFileDao(session, tx);
        
        try {
            cfeResultsFileDao.save(cfeResultsFile);
            tx.commit();
        }
        catch (Exception exception) {
            tx.rollback();
            throw new Exception("Unable to save CfeResultsFile object: " + exception.getMessage());
        }
        finally {
            session.close();
        }
	}
	
    public static void saveOrUpdate(CfeResultsFile cfeResultsFile) throws Exception {

        Session session = HibernateUtils.getSession();      
        Transaction tx = session.beginTransaction();
        CfeResultsFileDao cfeResultsFileDao = new CfeResultsFileDao(session, tx);
        
        try {
            cfeResultsFileDao.saveOrUpdate(cfeResultsFile);
            tx.commit();
        }
        catch (Exception exception) {
            tx.rollback();
            throw new Exception("Unable to save CfeResultsFile object: " + exception.getMessage());
        }
        finally {
            session.close();
        }
    }
    	
	public static CfeResultsFile get(Long cfeResultsFileId) {
        CfeResultsFile cfeResultsFile = null;
        
        Session session = HibernateUtils.getSession();      
        Transaction tx = session.beginTransaction();
        CfeResultsFileDao cfeResultsFileDao = new CfeResultsFileDao(session, tx);
        
        try {
            cfeResultsFile = cfeResultsFileDao.getById(cfeResultsFileId);
            tx.commit();
        }
        catch (Exception exception) {
            tx.rollback();
        }
        finally {
            session.close();
        }
        
        return cfeResultsFile;
	}

    
    public static List<CfeResultsFile> getFilesForCfeResults(Long cfeResultsId) {
        List<CfeResultsFile> cfeResultsFiles = new ArrayList<CfeResultsFile>();
        
        Session session = HibernateUtils.getSession();      
        Transaction tx = session.beginTransaction();
        CfeResultsFileDao cfeResultsFileDao = new CfeResultsFileDao(session, tx);
        
        try {
            cfeResultsFiles = cfeResultsFileDao.getFilesForCfeResults(cfeResultsId);
            tx.commit();
        }
        catch (Exception exception) {
            tx.rollback();
        }
        finally {
            session.close();
        }
        
        return cfeResultsFiles;
    }    
}