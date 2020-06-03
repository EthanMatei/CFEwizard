package cfe.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.dao.HuPerGexDao;
import cfe.model.GeneListInput;
import cfe.model.HuPerGex;
import cfe.model.ModelInterface;
import cfe.model.disease.DiseaseSelection;
import cfe.utils.HibernateUtils;


public class HuPerGexService {

	public static List<HuPerGex> getAll() throws ServiceException {

		List<HuPerGex> huPerGenes = new ArrayList<HuPerGex>();

		Session session = HibernateUtils.getSession();		
		Transaction tx = session.beginTransaction();
		HuPerGexDao huPerGeneDao = new HuPerGexDao(session, tx);

		try	{
			huPerGenes = huPerGeneDao.getAll();
			tx.commit();
		}
		catch (Exception exception)	{
			tx.rollback();
			throw new ServiceException( exception.getMessage() );
		} 
		finally	{
			session.close();
		}

		return huPerGenes;
	}
	
	
	public static List<HuPerGex> getAllSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		
		List<HuPerGex> allHuPerGenes = getAll();
		List<HuPerGex> selectedHuPerGenes = new ArrayList<HuPerGex>();

		for (HuPerGex huPerGene: allHuPerGenes) {
			if (diseaseSelection.isSelected(huPerGene.getPsychiatricDomain(), huPerGene.getSubDomain(), huPerGene.getRelevantDisorder())) {
				if (geneListInput!= null && geneListInput.size() > 0) {
					if (geneListInput.contains( huPerGene.getGenecardSymbol() ) ) {
						selectedHuPerGenes.add( huPerGene );
					}
				}
				else {
					selectedHuPerGenes.add( huPerGene );
				}
			}
			
		}
		return selectedHuPerGenes;
	}
	
	
	
	public static List<ModelInterface> getSelected(DiseaseSelection diseaseSelection, GeneListInput geneListInput) throws ServiceException {
		List<HuPerGex> selectedHuPerGenes = getAllSelected(diseaseSelection, geneListInput);
		List<ModelInterface> modelInterfaces = new ArrayList<ModelInterface>();
		for (HuPerGex huPerGene: selectedHuPerGenes) {
			ModelInterface modelInterface = huPerGene;
			modelInterfaces.add( modelInterface );
		}
		return modelInterfaces;
	}

}