package com.tuneit.salsa3.cli;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProfileView {
	public class Section {
		private String header;
		private String data;
		
		public Section(String header, String data) {
			this.header = header;
			this.data = data;
		}
		
		public String getHeader() {
			return header;
		}
		
		public String toString() {
			return data;
		}
	}
	
	public class TableSection extends Section {
		private TableView tv;
		
		public TableSection(String header) {
			super(header, null);
			this.tv = new TableView();
		}
		
		public TableView.Row newRow() {
			return tv.newRow();
		}
		
		public String toString() {
			return tv.toString();
		}
	}
	
	public class ProfileSection extends Section {		
		private Map<String, String> map;
		
		public ProfileSection(String header) {
			super(header, null);
			this.map = new LinkedHashMap<String, String>();
		}
		
		public void setParameter(String name, String value) {
			this.map.put(name, value);
		}
		
		public void setParameter(String name, Object value) {
			this.map.put(name, value.toString());
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			int maxParamNameLength = 0;
			
			for(String name : map.keySet()) {
				if(name.length() > maxParamNameLength) {
					maxParamNameLength = name.length();
				}
			}
			
			for(Map.Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey();
				sb.append(key);
				
				int spacesCount = maxParamNameLength - key.length();
				
				while(spacesCount > 0) {
					sb.append(' ');
					--spacesCount;
				}
				
				sb.append(": ");
				sb.append(entry.getValue());
				sb.append('\n');
			}
			
			return sb.toString();
		}
	}
	
	private List<Section> sections;	
	
	public ProfileView() {
		sections = new ArrayList<Section>();
	}
	
	public Section newSection(String header, String data) {
		Section section = new Section(header, data);
		sections.add(section);
		return section;
	}
	
	public TableSection newTableSection(String header) {
		TableSection section = new TableSection(header);
		sections.add(section);
		return section;
	}
	
	public ProfileSection newProfileSection(String header) {
		ProfileSection section = new ProfileSection(header);
		sections.add(section);
		return section;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		boolean isFirstSection = true;
		
		for(Section section : sections) {
			if(!isFirstSection) {
				sb.append("\n\n");
			}
			
			String header = section.getHeader();
			
			sb.append(header); sb.append('\n');
			
			for(int i = 0; i < header.length(); ++i) {
				sb.append('=');
			}
			sb.append("\n\n");
			
			sb.append(section.toString());
			
			isFirstSection = false;
		}
		
		return sb.toString();
	}
}
