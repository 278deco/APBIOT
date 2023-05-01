package apbiot.core.io.csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import apbiot.core.objects.Tuple;

public class CSVDocument {

	private List<List<CSVCell>> fileContent;
	
	public CSVDocument() {
		this.fileContent = new ArrayList<>();
	}
	
	public CSVDocument(List<List<CSVCell>> fileContent) {
		this.fileContent = fileContent;
	}

	public CSVCell getCell(int row, int column) {
		return this.fileContent.get(row).get(column);
	}
	
	public boolean setCell(CSVCell cell, int row, int column) {
		if(row >= this.fileContent.size()) return false; 
		
		return !this.fileContent.get(row).set(column, cell).equals(cell);		
	}
	
//	public boolean addCell(CSVCell cell, int column) {
//		this.fileContent.add(new ArrayList<>());
//		//TODO Rework method because will throw IndexOutOfBoundExpection else
//		return !this.fileContent.get(this.fileContent.size()-1).set(column, cell).equals(cell);		
//	}
	
	public boolean addCell(CSVCell cell) {
		this.fileContent.add(new ArrayList<>());
		
		return this.fileContent.get(this.fileContent.size()-1).add(cell);		
	}
	
	public List<CSVCell> getRow(int row) {
		return Collections.unmodifiableList(this.fileContent.get(row));
	}
	
	public boolean setRow(List<CSVCell> rowList, int row) {
		if(row >= this.fileContent.size()) return false;
		
		return !this.fileContent.set(row, rowList).equals(rowList);
	}
	
	public boolean addRow(List<CSVCell> rowList) {
		return this.fileContent.add(rowList);
	}
	
	public int getRowCount() {
		return this.fileContent.size();
	}
	
	public List<CSVCell> getColumn(int column) {
		final List<CSVCell> result = new ArrayList<>();
		this.fileContent.forEach(row -> result.add(row.get(column)));
		
		return result;
	}
	
	public List<Tuple<Integer, CSVCell>> getColumnWithIndex(int column) {
		final List<Tuple<Integer, CSVCell>> result = new ArrayList<>();
		
		for(int i = 0; i < this.fileContent.size(); i++) result.add(Tuple.of(i, this.fileContent.get(i).get(column)));
		
		return result;
	}
	
	/**
	 * Sort a CSV Document by choosing a sorting column and rearrange the rows depending on the type of comparison defined
	 * @param paramColumn the column used for the comparison
	 * @param the comparator used
	 * @see apbiot.core.io.csv.CSVDocument.SortComparaison
	 */
	public <U extends Comparable<? super U>> void sortByColumnSelection(int paramColumn, Function<Tuple<Integer, CSVCell>, U> comparatorKey) {
		final List<List<CSVCell>> result = new ArrayList<>();
		final List<Tuple<Integer, CSVCell>> sortingCol = getColumnWithIndex(paramColumn);
		
		Collections.sort(sortingCol, Comparator.comparing(comparatorKey));
		
		for(Tuple<Integer, CSVCell> tp : sortingCol) result.add(fileContent.get(tp.getValueA()));
		this.fileContent = result;
	}
	
	public static class SortComparaison {
		
		public static double sortDouble(Tuple<Integer, CSVCell> tuple) {
			String value = tuple.getValueB().getContent();
			return (value = value.replaceAll("[^\\d.+]", "")).isEmpty() ? 0 : Double.parseDouble(value);
		}
		
		public static float sortFloat(Tuple<Integer, CSVCell> tuple) {
			String value = tuple.getValueB().getContent();
			return (value = value.replaceAll("[^\\d.+]", "")).isEmpty() ? 0 : Float.parseFloat(value);
		}
		
		public static int sortInteger(Tuple<Integer, CSVCell> tuple) {
			String value = tuple.getValueB().getContent();
			return (value = value.replaceAll("[^\\d+]", "")).isEmpty() ? 0 : Integer.parseInt(value);
		}
		
		public static long sortLong(Tuple<Integer, CSVCell> tuple) {
			String value = tuple.getValueB().getContent();
			return (value = value.replaceAll("[^\\d+]", "")).isEmpty() ? 0 : Long.parseLong(value);
		}
		
		public static String sortString(Tuple<Integer, CSVCell> tuple) {
			return tuple.getValueB().getContent();
		}
		
		public static boolean sortBoolean(Tuple<Integer, CSVCell> tuple) {
			return Boolean.parseBoolean(tuple.getValueB().getContent());
		}
	}
	
}
