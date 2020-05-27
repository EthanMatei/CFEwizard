package cfg.model.reports;


/**
 * Class for specifying a region in a spreadsheet; assumes rows and columns are numbered starting at zero.
 * 
 * @author Jim Mullen
 *
 */
public class Region {
    private int fromRow;
    private int toRow;
    private int fromColumn;
    private int toColumns;
	
    public int getFromRow() {
		return fromRow;
	}
	
    public void setFromRow(int fromRow) {
		this.fromRow = fromRow;
	}
	
    public int getToRow() {
		return toRow;
	}
	
    public void setToRow(int toRow) {
		this.toRow = toRow;
	}
	
    public int getFromColumn() {
		return fromColumn;
	}
	
    public void setFromColumn(int fromColumn) {
		this.fromColumn = fromColumn;
	}
	
    public int getToColumns() {
		return toColumns;
	}
	
    public void setToColumns(int toColumns) {
		this.toColumns = toColumns;
	}
    
}