package cfe.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.HuGeneCNVDao;
import cfe.model.GeneListInput;
import cfe.model.HuGeneCNV;
import cfe.model.ModelInterface;
import cfe.model.disease.DiseaseSelection;
import cfe.utils.HibernateUtils;


public class HuGeneCNVService {

	/**
	 * Gets all the HuGeneCNV entities.
	 * 
	 * @return all the HuGeneCNV entities
	 * @throws ServiceException
	 */
	public static List<HuGeneCNV> getAll() throws ServiceException {

		List<HuGeneCNV> huGeneCNVs = new ArrayList<HuGeneCNV>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		HuGeneCNVDao huGeneCNVDao = new HuGeneCNVDao(session, tx);

		try	{
			huGeneCNVs = huGeneCNVDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
			throw new ServiceException( "The following error occurred while trying to retrieve the HuGeneCNV records: " + exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return huGeneCNVs;
	}
	
	public static List<HuGeneCNV> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<HuGeneCNV> allHuGeneCNVs = getAll();
		List<HuGeneCNV> selectedHuGeneCNVs = new ArrayList<HuGeneCNV>();

		for (HuGeneCNV huGeneCNV: allHuGeneCNVs) {
			if (diseaseSelection.isSelected(huGeneCNV.getPsychiatricDomain(), huGeneCNV.getSubDomain(), huGeneCNV.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( huGeneCNV.getGenecardSymbol() ) ) {
						selectedHuGeneCNVs.add( huGeneCNV );
					}
				}
				else {
					selectedHuGeneCNVs.add( huGeneCNV );
				}
			}
			
		}
		return selectedHuGeneCNVs;
	}
	
	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<HuGeneCNV> selectedHuGeneCNVs = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (HuGeneCNV huGeneCNV: selectedHuGeneCNVs) {
			ModelInterface modelInterface = huGeneCNV;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}

}