package hw7;

public class SimpleKey {
	String myRow;
	String myColumn;
	String myTime;
	
	public SimpleKey(String row, String column) {
		myRow = row;
		myColumn = column;
		myTime = String.valueOf(System.currentTimeMillis());
	}
	
	public SimpleKey(String row, String column, String time) {
		myRow = row;
		myColumn = column;
		myTime = time;
	}

	public String getMyRow() {
		return myRow;
	}
	public void setMyRow(String row) {
		this.myRow = row;
	}
	public String getMyColumn() {
		return myColumn;
	}
	public void setMyColumn(String column) {
		this.myColumn = column;
	}
	public String getMyTime() {
		return myTime;
	}
	public void setMyTime(String time) {
		this.myTime = time;
	}
	
	@Override
	public String toString() {
		return "(" + myRow + ":" + myColumn + "@" + myTime + ")" ;
	}
}
