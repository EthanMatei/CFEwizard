package cfe.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.HuPerMetDao;
import cfe.model.GeneListInput;
import cfe.model.HuPerMet;
import cfe.model.ModelInterface;
import cfe.model.disease.DiseaseSelection;
import cfe.utils.HibernateUtils;


public class HuPerMetService {

	public static List<HuPerMet> getAll() throws ServiceException {

		List<HuPerMet> huPerMets = new ArrayList<HuPerMet>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		HuPerMetDao huPerMetDao = new HuPerMetDao(session, tx);

		try	{
			huPerMets = huPerMetDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return huPerMets;
	}
	
	public static List<HuPerMet> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<HuPerMet> allHuPerMets = getAll();
		List<HuPerMet> selectedHuPerMets = new ArrayList<HuPerMet>();

		for (HuPerMet huPerMet: allHuPerMets) {
			if (diseaseSelection.isSelected(huPerMet.getPsychiatricDomain(), huPerMet.getSubDomain(), huPerMet.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( huPerMet.getGenecardSymbol() ) ) {
						selectedHuPerMets.add( huPerMet );
					}
				}
				else {
					selectedHuPerMets.add( huPerMet );
				}
			}
			
		}
		return selectedHuPerMets;
	}
	
	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<HuPerMet> selectedHuPerMets = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (HuPerMet huPerMet: selectedHuPerMets) {
			ModelInterface modelInterface = huPerMet;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}

}