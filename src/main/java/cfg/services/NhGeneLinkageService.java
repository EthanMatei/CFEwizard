package cfg.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfg.dao.NhGeneLinkageDao;
import cfg.model.GeneListInput;
import cfg.model.ModelInterface;
import cfg.model.NhGeneLinkage;
import cfg.model.disease.DiseaseSelection;
import cfg.utils.HibernateUtils;


public class NhGeneLinkageService {

	public static List<NhGeneLinkage> getAll() throws ServiceException {

		List<NhGeneLinkage> nhGeneLinkages = new ArrayList<NhGeneLinkage>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		NhGeneLinkageDao nhGeneLinkageDao = new NhGeneLinkageDao(session, tx);

		try	{
			nhGeneLinkages = nhGeneLinkageDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return nhGeneLinkages;
	}
	

	public static List<NhGeneLinkage> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<NhGeneLinkage> allNhGeneLinkages = getAll();
		List<NhGeneLinkage> selectedNhGeneLinkages = new ArrayList<NhGeneLinkage>();

		for (NhGeneLinkage nhGeneLinkage: allNhGeneLinkages) {
			if (diseaseSelection.isSelected(nhGeneLinkage.getPsychiatricDomain(), nhGeneLinkage.getSubDomain(), nhGeneLinkage.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( nhGeneLinkage.getGenecardSymbol() ) ) {
						selectedNhGeneLinkages.add( nhGeneLinkage );
					}
				}
				else {
					selectedNhGeneLinkages.add( nhGeneLinkage );
				}
			}
			
		}
		return selectedNhGeneLinkages;
	}
	
	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<NhGeneLinkage> selectedNhGeneLinkages = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (NhGeneLinkage nhGeneLinkage: selectedNhGeneLinkages) {
			ModelInterface modelInterface = nhGeneLinkage;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}

}