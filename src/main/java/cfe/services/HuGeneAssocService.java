package cfe.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.HuGeneAssocDao;
import cfe.model.GeneListInput;
import cfe.model.HuGeneAssoc;
import cfe.model.ModelInterface;
import cfe.model.disease.DiseaseSelection;
import cfe.utils.HibernateUtils;


public class HuGeneAssocService {

	/**
	 * Gets all the HuGeneAssoc entities.
	 * 
	 * @return all the HuGeneAssoc entities.
	 * 
	 * @throws ServiceException
	 */
	static public List<HuGeneAssoc> getAll() throws ServiceException {

		List<HuGeneAssoc> huGeneAssocs = new ArrayList<HuGeneAssoc>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		HuGeneAssocDao huGeneAssocDao = new HuGeneAssocDao(session, tx);

		try	{
			huGeneAssocs = huGeneAssocDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return huGeneAssocs;
	}
	
	public static List<HuGeneAssoc> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<HuGeneAssoc> allHuGeneAssocs = getAll();
		List<HuGeneAssoc> selectedHuGeneAssocs = new ArrayList<HuGeneAssoc>();

		for (HuGeneAssoc huGeneAssoc: allHuGeneAssocs) {
			if (diseaseSelection.isSelected(huGeneAssoc.getPsychiatricDomain(), huGeneAssoc.getSubDomain(), huGeneAssoc.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( huGeneAssoc.getGenecardSymbol() ) ) {
						selectedHuGeneAssocs.add( huGeneAssoc );
					}
				}
				else {
					selectedHuGeneAssocs.add( huGeneAssoc );
				}
			}
			
		}
		return selectedHuGeneAssocs;
	}

	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<HuGeneAssoc> selectedHuGeneAssocs = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (HuGeneAssoc huGeneAssoc: selectedHuGeneAssocs) {
			ModelInterface modelInterface = huGeneAssoc;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}
}