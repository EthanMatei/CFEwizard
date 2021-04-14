package cfe.utils;

public class ColumnInfo {
	private String tableName;
	private String columnName;
	private String ColumnType;
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public String getColumnType() {
		return ColumnType;
	}
	
	public void setColumnType(String columnType) {
		ColumnType = columnType;
	}
	
	public String getTableAndColumnName() {
		return this.tableName + '|' + this.columnName;
	}
}
