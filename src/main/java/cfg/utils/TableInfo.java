package cfg.utils;

public class TableInfo {
	private String className;
	private long count;
	private String tableName;

	public TableInfo(String className, long count, String tableName) {
		this.className = className;
		this.count     = count;
		this.tableName = tableName;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
