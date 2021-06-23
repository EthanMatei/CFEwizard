package cfe.services.prioritization;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.prioritization.HuGeneLinkageDao;
import cfe.model.prioritization.GeneListInput;
import cfe.model.prioritization.HuGeneLinkage;
import cfe.model.prioritization.ModelInterface;
import cfe.model.prioritization.disease.DiseaseSelection;
import cfe.utils.HibernateUtils;


public class HuGeneLinkageService {

	public static List<HuGeneLinkage> getAll() throws ServiceException {

		List<HuGeneLinkage> huGeneLinkages = new ArrayList<HuGeneLinkage>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		HuGeneLinkageDao huGeneLinkageDao = new HuGeneLinkageDao(session, tx);

		try	{
			huGeneLinkages = huGeneLinkageDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return huGeneLinkages;
	}
	
	
	public static List<HuGeneLinkage> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<HuGeneLinkage> allHuGeneLinkages = getAll();
		List<HuGeneLinkage> selectedHuGeneLinkages = new ArrayList<HuGeneLinkage>();

		for (HuGeneLinkage huGeneLinkage: allHuGeneLinkages) {
			if (diseaseSelection.isSelected(huGeneLinkage.getPsychiatricDomain(), huGeneLinkage.getSubDomain(), huGeneLinkage.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( huGeneLinkage.getGenecardSymbol() ) ) {
						selectedHuGeneLinkages.add( huGeneLinkage );
					}
				}
				else {
					selectedHuGeneLinkages.add( huGeneLinkage );
				}
			}
			
		}
		return selectedHuGeneLinkages;
	}
	
	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<HuGeneLinkage> selectedHuGeneLinkages = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (HuGeneLinkage huGeneLinkage: selectedHuGeneLinkages) {
			ModelInterface modelInterface = huGeneLinkage;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}

}