package cfe.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.PrioritizationDao;
import cfe.model.Prioritization;
import cfe.utils.HibernateUtils;


public class PrioritizationService {
	
	private static final Log log = LogFactory.getLog(PrioritizationService.class);
	
	public static List<Prioritization> getAll() throws ServiceException {

		List<Prioritization> prioritizations = new ArrayList<Prioritization>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
	    PrioritizationDao prioritizationDao = new PrioritizationDao(session, tx);

		try	{
			prioritizations = prioritizationDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();			
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return prioritizations;
	}

}