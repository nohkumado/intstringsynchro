package com.nohkumado.intstringsynchro;

import java.util.*;
/**
 * @author Noh Kuma Do <nohkumado at gmail dot com>
 * @licence GLP v3
 * @version  "%I%, %G%",
 * 

 Representation of a string ressource of type array 

 example 
 <string-array name="planets_array">
 <item>Mercury</item>
 <item>Venus</item>
 <item>Earth</item>
 <item>Mars</item>
 </string-array>

 */
public class ArrayEntry extends StringEntry
{
	/** the data structure holding the data */
	private ArrayList<CdataString> array;
	/** 
	 * CTOR
	 * @argument name the token name
	 */
	public ArrayEntry(String n)
	{
		this(n, null);
	}
	/** 
	 * CTOR
	 * @argument name the token name
	 * @argument list with an arraylist to use instead of the internal one
	 */
	public ArrayEntry(String n, ArrayList<CdataString> a)
	{
		super(n, new CdataString(""));
		if (a != null) array = a;
		else array = new ArrayList<>();
	}//public ArrayEntry(String n, ArrayList<String> a)
	public ArrayEntry(ArrayEntry a)
	{
		super(a);
		array = new ArrayList<>();
		for(CdataString tok : a.array) array.add(new CdataString(tok));
	}//public ArrayEntry(ArrayEntry a)
	
	/**
	 * remove an entry at 
	 * @argument index
	 */
	public void remove(int line)
	{
		array.remove(line);
	}// public void remove(int line)
	/**
	 * make a string representation of this object
	 */
	@Override
	public String toString()
	{
		StringBuilder  sb = new StringBuilder();
		sb.append("Array[").append(token).append("]");
		if (array != null) sb.append(Arrays.toString(array.toArray(new String[array.size()])));
		return sb.toString();
	}//public String toString()
	/**
	 * the real purpose of this thing, make an xml representation, ready to be printed into a string.xml file
	 * @argument indent the indentation to use even if it is machine generated a bit of pretty printing doesn't hurt
	 */
	public String toXml(String indent)
	{
		StringBuilder  sb = new StringBuilder();
		sb.append(indent).append("<string-array name=\"").append(token).append("\">\n");
		for (CdataString iem : array) sb.append(indent).append(indent).append("<item>").append(iem.toXml().trim()).append("</item>\n");

		sb.append(indent).append("</string-array>\n");
		return sb.toString();
	}//public String toXml(String indent)
	/**
	 * set a string at index n, autoexpands the list if the index is bigger than the container
	 * @arg num the line to set it to
	 * @arg msg the string to store at that position
	 */
	public void set(int num, CdataString msg)
	{
		while (num >= array.size()) array.add(new CdataString());

		array.set(num, msg);
	}//public void set(int num, String msg)
	/**
	 * add a line to the end of this array
	 * @arg msg the strriiing to add
	 */
	public void add(CdataString msg)
	{
		array.add(msg);
	}//public void add(String msg)
	/**
	 * delegated method for the array size
	 */
	public int size()
	{
		if (array != null)  return array.size();
		return -1;
	}//public int size()
	/**
	 * delegated method for getting at index n
	 */
	public CdataString get(int line)
	{
		if (array != null && line >= 0 && line < array.size()) return array.get(line);
		return null;
	}//public String get(int line)


	public void swap(int line, int p1)
	{
		if (line >= 0 && line < array.size() && p1 >= 0 && p1 < array.size())
		{
			if (array == null) return;
			Collections.swap(array, line, p1);
		}

	}//public void swap(int line, int p1)
}//class
