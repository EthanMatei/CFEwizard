package cfe.services.prioritization;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.prioritization.NhBrainMetDao;
import cfe.model.prioritization.GeneListInput;
import cfe.model.prioritization.ModelInterface;
import cfe.model.prioritization.NhBrainMet;
import cfe.model.prioritization.disease.DiseaseSelection;
import cfe.utils.HibernateUtils;


public class NhBrainMetService {

	public static List<NhBrainMet> getAll() throws ServiceException {

		List<NhBrainMet> nhBrainMets = new ArrayList<NhBrainMet>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		NhBrainMetDao nhBrainMetDao = new NhBrainMetDao(session, tx);

		try	{
			nhBrainMets = nhBrainMetDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return nhBrainMets;
	}


	public static List<NhBrainMet> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<NhBrainMet> allNhBrainMets = getAll();
		List<NhBrainMet> selectedNhBrainMets = new ArrayList<NhBrainMet>();

		for (NhBrainMet nhBrainMet: allNhBrainMets) {
			if (diseaseSelection.isSelected(nhBrainMet.getPsychiatricDomain(), nhBrainMet.getSubDomain(), nhBrainMet.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( nhBrainMet.getGenecardSymbol() ) ) {
						selectedNhBrainMets.add( nhBrainMet );
					}
				}
				else {
					selectedNhBrainMets.add( nhBrainMet );
				}
			}
			
		}
		return selectedNhBrainMets;
	}
	
	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<NhBrainMet> selectedNhBrainMets = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (NhBrainMet nhBrainMet: selectedNhBrainMets) {
			ModelInterface modelInterface = nhBrainMet;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}
	
}