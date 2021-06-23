package cfe.services.prioritization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.prioritization.NhBrainGexDao;
import cfe.model.prioritization.GeneListInput;
import cfe.model.prioritization.ModelInterface;
import cfe.model.prioritization.NhBrainGex;
import cfe.model.prioritization.disease.DiseaseSelection;
import cfe.utils.HibernateUtils;

/**
 * Non-human brain gene expression service class.
 */
public class NhBrainGexService {
	
	private static final Log log = LogFactory.getLog(NhBrainGexService.class);
	
	public static List<NhBrainGex> getAll() throws ServiceException {

		List<NhBrainGex> nhBrainGenes = new ArrayList<NhBrainGex>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		NhBrainGexDao nhBrainGeneDao = new NhBrainGexDao(session, tx);

		try	{
			nhBrainGenes = nhBrainGeneDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
			throw new ServiceException(exception.getMessage());
		} 
		finally	{
			session.close();
		}

		return nhBrainGenes;
	}

	public static List<NhBrainGex> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<NhBrainGex> allNhBrainGenes = getAll();
		List<NhBrainGex> selectedNhBrainGenes = new ArrayList<NhBrainGex>();

		for (NhBrainGex nhBrainGene: allNhBrainGenes) {
			if (diseaseSelection.isSelected(nhBrainGene.getPsychiatricDomain(), nhBrainGene.getSubDomain(), nhBrainGene.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( nhBrainGene.getGenecardSymbol() ) ) {
						selectedNhBrainGenes.add( nhBrainGene );
					}
				}
				else {
					selectedNhBrainGenes.add( nhBrainGene );
				}
			}
			
		}
		return selectedNhBrainGenes;
	}
	
	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<NhBrainGex> selectedNhBrainGenes = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (NhBrainGex nhBrainGene: selectedNhBrainGenes) {
			ModelInterface modelInterface = nhBrainGene;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}
}