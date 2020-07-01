package cfe.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.DisorderDao;
import cfe.dao.HuBrainGexDao;

import cfe.model.Disorder;
import cfe.model.DisorderComparator;
import cfe.model.disease.DiseaseSelector;
import cfe.utils.HibernateUtils;


public class DisorderService {
	
	private static final Log log = LogFactory.getLog(DisorderService.class);
	
	
	public static List<Disorder> getAll() {

		List<Disorder> disorders = new ArrayList<Disorder>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		DisorderDao disorderDao = new DisorderDao(session, tx);

		try	{
			disorders = disorderDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();			
		} 
		finally	{
			session.close();
		}

		return disorders;
	}
	
	/**
	 * Updates the Disorder table with values form the research data tables.
	 */
	public static void update() {

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		
		DisorderDao disorderDao = new DisorderDao(session, tx);
		
		HuBrainGexDao huBrainGexDao = new HuBrainGexDao(session, tx);
		
		TreeSet<Disorder> disordersSet = new TreeSet<Disorder>(new DisorderComparator());
		
		try	{
			disordersSet.addAll( huBrainGexDao.getDisorders() );
			
			// Clear the Disorder table
			disorderDao.deleteAll();

			// Update the table with the new records.
			List<Disorder> disorders = new ArrayList<Disorder>( disordersSet );
			log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DISORDER LIST SIZE: " + disorders.size());
			disorderDao.saveAllRows( disorders );

			tx.commit();
		}
		catch (Exception exception)	{
			log.error( exception.getMessage() );
			tx.rollback();			
		} 
		finally	{
			session.close();
		}

	}
	
	public static List<DiseaseSelector> getDiseaseSelectors() throws HibernateException, ServiceException	{
	
		List<DiseaseSelector> diseases = new ArrayList<DiseaseSelector>();
        
		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		
		DisorderDao disorderDao = new DisorderDao(session, tx);
		
		try {
			List<Disorder> disorders = disorderDao.getAll();
			for (Disorder disorder: disorders) {
				DiseaseSelector diseaseSelector = new DiseaseSelector();
				
				diseaseSelector.setPsychiatricDomain( disorder.getDomain() );
				diseaseSelector.setPsychiatricDomainSelected( false );
				diseaseSelector.setPsychiatricSubDomain( disorder.getSubdomain() );
				diseaseSelector.setPsychiatricSubDomainSelected( false );
				diseaseSelector.setRelevantDisorder( disorder.getRelevantDisorder() );
				diseaseSelector.setRelevantDisorderSelected( false );
				
				if (disorder.getDomain().equals("Addiction") || disorder.getDomain().equals("Addictions")) {
				    diseaseSelector.setCoefficient( 0.5 );
				} else if (disorder.getDomain().equals("Treatment") || disorder.getDomain().equals("Treatments")) {
				    diseaseSelector.setCoefficient( 0.5 );					
				} else {
				    diseaseSelector.setCoefficient( 1.0 );
				}
				
				diseases.add(diseaseSelector);
			}
		}
		catch (Exception exception)	{
			log.error( exception.getMessage() );
			tx.rollback();
			throw new ServiceException( "Unable to retrieve diseases from the database: " + exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return diseases;
	}

}