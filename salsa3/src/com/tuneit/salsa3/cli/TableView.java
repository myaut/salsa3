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
		
		public String get(int i) {
			if(i < row.size()) {
				return row.get(i);
			}
			
			return " ";
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
		boolean isHeaderRow = true;
		
		/* Calculate maximum length of data per column */
		for(Row row : rows) {
			for(int i = 0; i < columnCount; ++i) {
				String s = row.get(i);
				int length = s.length() + 1;
				
				maxStringLength[i] = (maxStringLength[i] > length)?
					maxStringLength[i] : length;
			}
		}
		
		for(Row row : rows) {
			/* Print rows adjusted to pre-selected lengths */
			for(int i = 0; i < columnCount; ++i) {
				String s = row.get(i);
				
				sb.append(s);
				
				/* Do not aling last column */
				if(i < (columnCount - 1)) {
					int spacesCount = maxStringLength[i] - s.length();
					
					while(spacesCount > 0) {
						sb.append(' ');
						--spacesCount;
					}
				}
			}
			
			/* Dump line after first row (header) */
			if(isHeaderRow) {
				sb.append("\n");
				for(int i = 0; i < columnCount; ++i) {
					if(headerRow.get(i).contains("\n")) {
						break;
					}
					
					for(int j = 0; j < maxStringLength[i]; ++j) {
						sb.append('-');
					}
				}
				
				isHeaderRow = false;
			}
			
			sb.append("\n");
		}
				
		return sb.toString();
	}
}
