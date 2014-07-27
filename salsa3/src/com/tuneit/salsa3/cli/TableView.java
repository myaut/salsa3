package com.tuneit.salsa3.cli;

import java.util.List;
import java.util.ArrayList;

public class TableView {
	public class Row {
		private List<String> row;
		
		public Row() {
			this.row = new ArrayList<String>();
		}
		
		public Row append(Object data) {
			row.add(data.toString());
			
			return this;
		}
		
		public List<String> getRow() {
			return row;
		}
	}
	
	private List<Row> rows;
	
	public TableView() {
		this.rows = new ArrayList<Row>();
	}
	
	public Row newRow() {
		Row row = new Row();
		rows.add(row);
		return row;
	}
	
	public String toString() {
		if(rows.isEmpty()) {
			return "No data acquired";
		}
		
		StringBuilder sb = new StringBuilder();
		
		Row headerRow = rows.get(0);
		int columnCount = headerRow.getRow().size();
		int[] maxStringLength = new int[columnCount];
		
		for(Row row : rows) {
			for(int i = 0; i < columnCount; ++i) {
				String s = row.getRow().get(i);
				int length = s.length() + 1;
				
				maxStringLength[i] = (maxStringLength[i] > length)?
					maxStringLength[i] : length;
			}
		}
		
		for(Row row : rows) {
			for(int i = 0; i < columnCount; ++i) {
				String s = row.getRow().get(i);
				int spacesCount = maxStringLength[i] - s.length();
				
				sb.append(s);
				
				while(spacesCount > 0) {
					sb.append(' ');
					--spacesCount;
				}
			}
			
			sb.append("\n");
		}
				
		return sb.toString();
	}
}
