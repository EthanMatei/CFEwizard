package cfe.services.prioritization;

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

import cfe.services.ServiceException;

import cfe.dao.prioritization.DisorderDao;
import cfe.dao.prioritization.HuBrainGexDao;
import cfe.dao.prioritization.HuBrainMetDao;
import cfe.dao.prioritization.HuBrainProtDao;
import cfe.dao.prioritization.HuGeneAssocDao;
import cfe.dao.prioritization.HuGeneCNVDao;
import cfe.dao.prioritization.HuGeneLinkageDao;
import cfe.dao.prioritization.HuPerGexDao;
import cfe.dao.prioritization.HuPerMetDao;
import cfe.dao.prioritization.HuPerProtDao;
import cfe.dao.prioritization.NhBrainGexDao;
import cfe.dao.prioritization.NhBrainMetDao;
import cfe.dao.prioritization.NhBrainProtDao;
import cfe.dao.prioritization.NhGeneAssocDao;
import cfe.dao.prioritization.NhGeneCNVDao;
import cfe.dao.prioritization.NhGeneLinkageDao;
import cfe.dao.prioritization.NhPerGexDao;
import cfe.dao.prioritization.NhPerMetDao;
import cfe.dao.prioritization.NhPerProtDao;
import cfe.model.prioritization.Disorder;
import cfe.model.prioritization.DisorderComparator;
import cfe.model.prioritization.disease.DiseaseSelector;
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
		HuBrainMetDao  huBrainMetDao  = new HuBrainMetDao(session, tx);
		HuBrainProtDao huBrainProtDao  = new HuBrainProtDao(session, tx);
		
		HuGeneAssocDao   huGeneAssocDao   = new HuGeneAssocDao(session, tx);
		HuGeneCNVDao     huGeneCNVDao     = new HuGeneCNVDao(session, tx);
		HuGeneLinkageDao huGeneLinkageDao = new HuGeneLinkageDao(session, tx);
		
		HuPerGexDao huPerGexDao = new HuPerGexDao(session, tx);
		HuPerMetDao  huPerMetDao  = new HuPerMetDao(session, tx);
		HuPerProtDao huPerProtDao  = new HuPerProtDao(session, tx);

		NhBrainGexDao nhBrainGexDao = new NhBrainGexDao(session, tx);
		NhBrainMetDao  nhBrainMetDao  = new NhBrainMetDao(session, tx);
		NhBrainProtDao nhBrainProtDao  = new NhBrainProtDao(session, tx);
		
		NhGeneAssocDao   nhGeneAssocDao   = new NhGeneAssocDao(session, tx);
		NhGeneCNVDao     nhGeneCNVDao     = new NhGeneCNVDao(session, tx);
		NhGeneLinkageDao nhGeneLinkageDao = new NhGeneLinkageDao(session, tx);
		
		NhPerGexDao nhPerGexDao = new NhPerGexDao(session, tx);
		NhPerMetDao  nhPerMetDao  = new NhPerMetDao(session, tx);
		NhPerProtDao nhPerProtDao  = new NhPerProtDao(session, tx);
		
		TreeSet<Disorder> disordersSet = new TreeSet<Disorder>(new DisorderComparator());
		
		try	{
			disordersSet.addAll( huBrainGexDao.getDisorders() );
			disordersSet.addAll( huBrainMetDao.getDisorders() );
			disordersSet.addAll( huBrainProtDao.getDisorders() );
			
			disordersSet.addAll( huGeneAssocDao.getDisorders() );
			disordersSet.addAll( huGeneCNVDao.getDisorders() );
			disordersSet.addAll( huGeneLinkageDao.getDisorders() );
			
			disordersSet.addAll( huPerGexDao.getDisorders() );
			disordersSet.addAll( huPerMetDao.getDisorders() );
			disordersSet.addAll( huPerProtDao.getDisorders() );

			
			disordersSet.addAll( nhBrainGexDao.getDisorders() );
			disordersSet.addAll( nhBrainMetDao.getDisorders() );
			disordersSet.addAll( nhBrainProtDao.getDisorders() );
			
			disordersSet.addAll( nhGeneAssocDao.getDisorders() );
			disordersSet.addAll( nhGeneCNVDao.getDisorders() );
			disordersSet.addAll( nhGeneLinkageDao.getDisorders() );
			
			disordersSet.addAll( nhPerGexDao.getDisorders() );
			disordersSet.addAll( nhPerMetDao.getDisorders() );
			disordersSet.addAll( nhPerProtDao.getDisorders() );
			
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