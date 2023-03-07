package apbiot.core.io.csv;

public class CSVCell implements Comparable<CSVCell> {

	private String content;
	
	public CSVCell() {
		this.content = "NaN";
	}
	
	public CSVCell(final String content) {
		this.content = content.trim();
	}
	
	public CSVCell(final int content) {
		this.content = (""+content).trim();
	}
	
	public CSVCell(final long content) {
		this.content = (""+content).trim();
	}
	
	public CSVCell(final boolean content) {
		this.content = (""+content).trim();
	}
	
	public int formatInteger() throws NumberFormatException {
		return Integer.parseInt(this.content);
	}
	
	public long formatLong() throws NumberFormatException {
		return Long.parseLong(this.content);
	}
	
	public double formatDouble() throws NumberFormatException {
		return Double.parseDouble(this.content);
	}
	
	public float formatFloat() throws NumberFormatException {
		return Float.parseFloat(this.content);
	}
	
	public String getContent() {
		return content;
	}
	
	public int getSize() {
		return this.content.length();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof CSVCell && ((CSVCell)obj).content.equals(this.content);
	}

	@Override
	public int compareTo(CSVCell o) {
		return this.getContent().compareTo(o.getContent());
	}
	
}
