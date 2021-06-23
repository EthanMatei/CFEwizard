package cfe.services.prioritization;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.services.ServiceException;

import cfe.dao.prioritization.NhGeneAssocDao;
import cfe.model.prioritization.GeneListInput;
import cfe.model.prioritization.ModelInterface;
import cfe.model.prioritization.NhGeneAssoc;
import cfe.model.prioritization.disease.DiseaseSelection;
import cfe.utils.HibernateUtils;


public class NhGeneAssocService {

	public static List<NhGeneAssoc> getAll() throws ServiceException {

		List<NhGeneAssoc> nhGeneAssocs = new ArrayList<NhGeneAssoc>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		NhGeneAssocDao nhGeneAssocDao = new NhGeneAssocDao(session, tx);

		try	{
			nhGeneAssocs = nhGeneAssocDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return nhGeneAssocs;
	}


	public static List<NhGeneAssoc> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<NhGeneAssoc> allNhGeneAssocs = getAll();
		List<NhGeneAssoc> selectedNhGeneAssocs = new ArrayList<NhGeneAssoc>();

		for (NhGeneAssoc nhGeneAssoc: allNhGeneAssocs) {
			if (diseaseSelection.isSelected(nhGeneAssoc.getPsychiatricDomain(), nhGeneAssoc.getSubDomain(), nhGeneAssoc.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( nhGeneAssoc.getGenecardSymbol() ) ) {
						selectedNhGeneAssocs.add( nhGeneAssoc );
					}
				}
				else {
					selectedNhGeneAssocs.add( nhGeneAssoc );
				}
			}
			
		}
		return selectedNhGeneAssocs;
	}
	
	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<NhGeneAssoc> selectedNhGeneAssocs = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (NhGeneAssoc nhGeneAssoc: selectedNhGeneAssocs) {
			ModelInterface modelInterface = nhGeneAssoc;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}
}