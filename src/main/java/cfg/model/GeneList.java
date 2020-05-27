package cfg.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * GeneList only uses one field, which means we are getting some extra stuff from Model.
 * I decided to do this for consistence only.
 * @author mtavares
 *
 */

@Entity
@Table(name=cfg.enums.Tables.TblNames.GENE_LIST)
public class GeneList extends Model implements Serializable {
	
	private static final long serialVersionUID = 8377861130060197993L;
		
}
