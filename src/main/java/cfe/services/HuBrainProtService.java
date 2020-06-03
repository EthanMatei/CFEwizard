package cfe.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.HuBrainProtDao;
import cfe.model.GeneListInput;
import cfe.model.HuBrainProt;
import cfe.model.ModelInterface;
import cfe.model.disease.DiseaseSelection;
import cfe.utils.HibernateUtils;


public class HuBrainProtService {

	public static List<HuBrainProt> getAll() throws ServiceException {

		List<HuBrainProt> huBrainProts = new ArrayList<HuBrainProt>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		HuBrainProtDao huBrainProtDao = new HuBrainProtDao(session, tx);

		try	{
			huBrainProts = huBrainProtDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return huBrainProts;
	}
	
	public static List<HuBrainProt> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput)  throws ServiceException {
		
		List<HuBrainProt> allHuBrainProts = getAll();
		List<HuBrainProt> selectedHuBrainProts = new ArrayList<HuBrainProt>();

		for (HuBrainProt huBrainProt: allHuBrainProts) {
			if (diseaseSelection.isSelected(huBrainProt.getPsychiatricDomain(), huBrainProt.getSubDomain(), huBrainProt.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( huBrainProt.getGenecardSymbol() ) ) {
						selectedHuBrainProts.add( huBrainProt );
					}
				}
				else {
					selectedHuBrainProts.add( huBrainProt );
				}
			}
			
		}
		return selectedHuBrainProts;
	}

	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput)  throws ServiceException {
		List<HuBrainProt> selectedHuBrainProts = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (HuBrainProt huBrainProt: selectedHuBrainProts) {
			ModelInterface modelInterface = huBrainProt;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}
}