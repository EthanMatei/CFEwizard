package cfe.services.prioritization;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.prioritization.NhBrainProtDao;
import cfe.model.prioritization.GeneListInput;
import cfe.model.prioritization.ModelInterface;
import cfe.model.prioritization.NhBrainProt;
import cfe.model.prioritization.disease.DiseaseSelection;
import cfe.utils.HibernateUtils;


public class NhBrainProtService {

	public static List<NhBrainProt> getAll() throws ServiceException {

		List<NhBrainProt> nhBrainProts = new ArrayList<NhBrainProt>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		NhBrainProtDao nhBrainProtDao = new NhBrainProtDao(session, tx);

		try	{
			nhBrainProts = nhBrainProtDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return nhBrainProts;
	}


	public static List<NhBrainProt> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<NhBrainProt> allNhBrainProts = getAll();
		List<NhBrainProt> selectedNhBrainProts = new ArrayList<NhBrainProt>();

		for (NhBrainProt nhBrainProt: allNhBrainProts) {
			if (diseaseSelection.isSelected(nhBrainProt.getPsychiatricDomain(), nhBrainProt.getSubDomain(), nhBrainProt.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( nhBrainProt.getGenecardSymbol() ) ) {
						selectedNhBrainProts.add( nhBrainProt );
					}
				}
				else {
					selectedNhBrainProts.add( nhBrainProt );
				}
			}
			
		}
		return selectedNhBrainProts;
	}
	
	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<NhBrainProt> selectedNhBrainProts = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (NhBrainProt nhBrainProt: selectedNhBrainProts) {
			ModelInterface modelInterface = nhBrainProt;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}
}