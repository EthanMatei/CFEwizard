package cfe.services.prioritization;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.services.ServiceException;

import cfe.dao.prioritization.NhPerMetDao;
import cfe.model.prioritization.GeneListInput;
import cfe.model.prioritization.ModelInterface;
import cfe.model.prioritization.NhPerMet;
import cfe.model.prioritization.disease.DiseaseSelection;
import cfe.utils.HibernateUtils;


public class NhPerMetService {

	public static List<NhPerMet> getAll() throws ServiceException {

		List<NhPerMet> nhPerMets = new ArrayList<NhPerMet>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		NhPerMetDao nhPerMetDao = new NhPerMetDao(session, tx);

		try	{
			nhPerMets = nhPerMetDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return nhPerMets;
	}


	public static List<NhPerMet> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<NhPerMet> allNhPerMets = getAll();
		List<NhPerMet> selectedNhPerMets = new ArrayList<NhPerMet>();

		for (NhPerMet nhPerMet: allNhPerMets) {
			if (diseaseSelection.isSelected(nhPerMet.getPsychiatricDomain(), nhPerMet.getSubDomain(), nhPerMet.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( nhPerMet.getGenecardSymbol() ) ) {
						selectedNhPerMets.add( nhPerMet );
					}
				}
				else {
					selectedNhPerMets.add( nhPerMet );
				}
			}
			
		}
		return selectedNhPerMets;
	}
	
	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<NhPerMet> selectedNhPerMets = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (NhPerMet nhPerMet: selectedNhPerMets) {
			ModelInterface modelInterface = nhPerMet;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}
}