package cfe.model;

import java.util.Set;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

//import org.hibernate.annotations.Index;

/**
 * Base class for CFE data classes that contains the common fields.
 *
 * @author Jim Mullen
 *
 */
// http://stackoverflow.com/questions/4265454/hibernate-jpa-inheritance-mapping-of-abstract-super-classes
@MappedSuperclass
public interface ModelInterface {
	public String getFieldName(String name);
	public long getId();
	public void setId(long id);
	public String getProbeset();
	public void setProbeset(String probeset);
	public String getGeneTitle();
	public void setGeneTitle(String geneTitle);
	public String getGeneCardsSymbol();
	public void setGeneCardsSymbol(String geneCardsSymbol);
	public String getChangeInExpressionInTrackedPhene();
	public void setChangeInExpressionInTrackedPhene(String changeInExpressionInTrackedPhene);
}
