package cfe.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.HuPerProtDao;
import cfe.model.GeneListInput;
import cfe.model.HuPerProt;
import cfe.model.ModelInterface;
import cfe.model.disease.DiseaseSelection;
import cfe.utils.HibernateUtils;


public class HuPerProtService {

	public static List<HuPerProt> getAll() throws ServiceException {

		List<HuPerProt> huPerProts = new ArrayList<HuPerProt>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		HuPerProtDao huPerProtDao = new HuPerProtDao(session, tx);

		try	{
			huPerProts = huPerProtDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();	
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return huPerProts;
	}
	
	
	public static List<HuPerProt> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<HuPerProt> allHuPerProts = getAll();
		List<HuPerProt> selectedHuPerProts = new ArrayList<HuPerProt>();

		for (HuPerProt huPerProt: allHuPerProts) {
			if (diseaseSelection.isSelected(huPerProt.getPsychiatricDomain(), huPerProt.getSubDomain(), huPerProt.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( huPerProt.getGenecardSymbol() ) ) {
						selectedHuPerProts.add( huPerProt );
					}
				}
				else {
					selectedHuPerProts.add( huPerProt );
				}
			}
			
		}
		return selectedHuPerProts;
	}

	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<HuPerProt> selectedHuPerProts = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (HuPerProt huPerProt: selectedHuPerProts) {
			ModelInterface modelInterface = huPerProt;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}
}