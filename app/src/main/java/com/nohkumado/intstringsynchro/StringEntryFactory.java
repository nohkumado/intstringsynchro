package com.nohkumado.intstringsynchro;

public class StringEntryFactory
{


	public static StringEntry instantiate(StringEntry aE)
	{
		if(aE instanceof ArrayEntry)return new ArrayEntry((ArrayEntry)aE);
		if(aE instanceof PluralEntry)return new PluralEntry((PluralEntry)aE);
		return new StringEntry(aE);
	}//public static StringEntry instantiate(StringEntry aE)
}//public class StringEntryFactory
