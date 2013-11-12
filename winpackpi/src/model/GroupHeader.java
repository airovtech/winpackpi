package model;

public class GroupHeader {

	private String startColumnName;
	private int startColumnIndex;
	private int numberOfColumns;
	private String titleText;
	
	public GroupHeader(String startColumnName, int startColumnIndex, int numberOfColumns, String titleText) {
		this.startColumnName = startColumnName;
		this.startColumnIndex = startColumnIndex;
		this.numberOfColumns = numberOfColumns;
		this.titleText = titleText;
	}
	public GroupHeader() {
		super();
	}
	
	public String getStartColumnName() {
		return startColumnName;
	}
	public void setStartColumnName(String startColumnName) {
		this.startColumnName = startColumnName;
	}
	public int getNumberOfColumns() {
		return numberOfColumns;
	}
	public void setNumberOfColumns(int numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}
	public String getTitleText() {
		return titleText;
	}
	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}
	public int getStartColumnIndex() {
		return startColumnIndex;
	}
	public void setStartColumnIndex(int startColumnIndex) {
		this.startColumnIndex = startColumnIndex;
	}
	
}
