package cfg.services;

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

import cfg.dao.DisorderDao;
import cfg.dao.HuBrainGexDao;
import cfg.dao.HuBrainMetDao;
import cfg.dao.HuBrainProtDao;
import cfg.dao.HuGeneAssocDao;
import cfg.dao.HuGeneCNVDao;
import cfg.dao.HuGeneLinkageDao;
import cfg.dao.HuPerGexDao;
import cfg.dao.HuPerMetDao;
import cfg.dao.HuPerProtDao;
import cfg.dao.NhBrainGexDao;
import cfg.dao.NhBrainMetDao;
import cfg.dao.NhBrainProtDao;
import cfg.dao.NhGeneAssocDao;
import cfg.dao.NhGeneCNVDao;
import cfg.dao.NhGeneLinkageDao;
import cfg.dao.NhPerGexDao;
import cfg.dao.NhPerMetDao;
import cfg.dao.NhPerProtDao;
import cfg.model.Disorder;
import cfg.model.DisorderComparator;
import cfg.model.disease.DiseaseSelector;
import cfg.utils.HibernateUtils;


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