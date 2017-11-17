package com.nohkumado.intstringsynchro;

import android.util.*;
import java.util.regex.*;

public class CdataString
{
	private static final String TAG="CDATA";

	private String content;
	private boolean cdata = false;

	public CdataString()
	{
		content("");
	}

	public CdataString(String init)
	{
		content(init);
	}

	public boolean isCdata()
	{
		return cdata;
	}
	public void addContent(String summary)
	{
		content += " " + summary;
	}
	public CdataString cdata(String summary)
	{
		cdata = true;
		content(summary);
		return this;
	}

	public CdataString content(String summary)
	{
		Pattern pattern = Pattern.compile("[\"'<>&]+");
		Matcher m = pattern.matcher(summary);

		//if(summary.matches("[\"'<>&]+")) 
		if (m.find()) 
		{
			cdata = true;
			summary = summary.replace("]]>", "");
			Log.d(TAG, "created cdata for " + summary);
		}
		else Log.d(TAG, "no problem with " + summary);
		content = summary;

		return this;
	}//public CdataString content(String summary)

	public CdataString asCdata()
	{
		cdata = true;
		return this;
	}
	public CdataString asText()
	{
		cdata = false;
		return this;
	}

	@Override
	public String toString()
	{
		return content;
	}//public void cdata(String summary)

	public String toXml()
	{
		if (cdata) return "<![CDATA[" + content + "]]>";
		return content;
	}//public void cdata(String summary)
}//public class CdataString

