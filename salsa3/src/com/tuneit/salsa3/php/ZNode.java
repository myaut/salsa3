package com.tuneit.salsa3.php;

public final class ZNode {
	public int id;
	public int type;
	public String value;
	
	public String toString() {
		return "<ZNode " + id + " type: " + type + " " + value + ">";			
	}
}
