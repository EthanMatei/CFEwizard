package cfe.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.ValidationDao;
import cfe.model.Validation;
import cfe.utils.HibernateUtils;


public class ValidationService {
	
	private static final Log log = LogFactory.getLog(ValidationService.class);
	
	public static List<Validation> getAll() throws ServiceException {

		List<Validation> validations = new ArrayList<Validation>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		ValidationDao validationDao = new ValidationDao(session, tx);

		try	{
			validations = validationDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();			
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return validations;
	}

}