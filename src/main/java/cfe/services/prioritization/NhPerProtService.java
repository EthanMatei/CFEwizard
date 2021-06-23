package cfe.services.prioritization;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.services.ServiceException;

import cfe.dao.prioritization.NhPerProtDao;
import cfe.model.prioritization.GeneListInput;
import cfe.model.prioritization.ModelInterface;
import cfe.model.prioritization.NhPerProt;
import cfe.model.prioritization.disease.DiseaseSelection;
import cfe.utils.HibernateUtils;


public class NhPerProtService {

	public static List<NhPerProt> getAll() throws ServiceException {

		List<NhPerProt> nhPerProts = new ArrayList<NhPerProt>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		NhPerProtDao nhPerProtDao = new NhPerProtDao(session, tx);

		try	{
			nhPerProts = nhPerProtDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return nhPerProts;
	}


	public static List<NhPerProt> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<NhPerProt> allNhPerProts = getAll();
		List<NhPerProt> selectedNhPerProts = new ArrayList<NhPerProt>();

		for (NhPerProt nhPerProt: allNhPerProts) {
			if (diseaseSelection.isSelected(nhPerProt.getPsychiatricDomain(), nhPerProt.getSubDomain(), nhPerProt.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( nhPerProt.getGenecardSymbol() ) ) {
						selectedNhPerProts.add( nhPerProt );
					}
				}
				else {
					selectedNhPerProts.add( nhPerProt );
				}
			}
			
		}
		return selectedNhPerProts;
	}
	
	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<NhPerProt> selectedNhPerProts = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (NhPerProt nhPerProt: selectedNhPerProts) {
			ModelInterface modelInterface = nhPerProt;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}
}