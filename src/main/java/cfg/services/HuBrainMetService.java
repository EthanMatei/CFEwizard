package cfg.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfg.dao.HuBrainMetDao;
import cfg.model.GeneListInput;
import cfg.model.HuBrainMet;
import cfg.model.ModelInterface;
import cfg.model.disease.DiseaseSelection;
import cfg.utils.HibernateUtils;


public class HuBrainMetService {

	/**
	 * Gets all the HuBrainMet records.
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public static List<HuBrainMet> getAll() throws ServiceException {

		List<HuBrainMet> huBrainMets = new ArrayList<HuBrainMet>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		HuBrainMetDao huBrainMetDao = new HuBrainMetDao(session, tx);

		try	{
			huBrainMets = huBrainMetDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();			
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return huBrainMets;
	}

	
	public static List<HuBrainMet> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<HuBrainMet> allHuBrainMets = getAll();
		List<HuBrainMet> selectedHuBrainMets = new ArrayList<HuBrainMet>();

		for (HuBrainMet huBrainMet: allHuBrainMets) {
			if (diseaseSelection.isSelected(huBrainMet.getPsychiatricDomain(), huBrainMet.getSubDomain(), huBrainMet.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( huBrainMet.getGenecardSymbol() ) ) {
						selectedHuBrainMets.add( huBrainMet );
					}
				}
				else {
					selectedHuBrainMets.add( huBrainMet );
				}
			}
			
		}
		return selectedHuBrainMets;
	}
	
	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<HuBrainMet> selectedHuBrainMets = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (HuBrainMet huBrainMet: selectedHuBrainMets) {
			ModelInterface modelInterface = huBrainMet;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}
}