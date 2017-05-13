package com.nohkumado.intstringsynchro;

import android.content.*;
import android.os.*;
import android.util.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class DirectoryScanner
{
  private static final String TAG="DirSc"; /** needed for Log.d */

  private final Pattern valuesPat = Pattern.compile("^(.*)values$|^(.*)values-([a-z\\-]{2,})$");
  private final Pattern ignPat = Pattern.compile(
    "^drawable$|^drawable-.*$|^build$|^bin$|^java$|^layout$|^layout$|^layout-.*$");
  private ArrayList<StringFile> toLoad = new ArrayList<StringFile>();

  private boolean foundDefault;

  private boolean ambiguous;

  private StringBuilder error;

  private Context context;

  /**
   * return from the filechooserdialog
   */
  public ArrayList<StringFile> findStringFiles(String pathToLoad, StringBuilder error, Context context)
  {
    this.error = error;
    this.context = context;
    toLoad = new ArrayList<StringFile>(); 
    ambiguous = false;
    foundDefault = false;

    //Log.d(TAG, "findStringFiles in " + pathToLoad);
    File resValuesDir = new File(pathToLoad.trim());
    //ok check if the xml file was selected and use its parent dir
    if (pathToLoad.endsWith(".xml"))
    {
      resValuesDir =  new File(pathToLoad);
      resValuesDir = new File(resValuesDir.getAbsolutePath());
      resValuesDir = moveUp(resValuesDir);//into valiues
      resValuesDir = moveUp(resValuesDir);//into res
      return(findStringFiles(resValuesDir.getAbsolutePath(), error, context));    
    }//if (pathToLoad.endsWith(".xml"))

    if (!resValuesDir.isDirectory()) 
    {
      //Log.e(TAG, "ehm?? not a dir " + resValuesDir);
      if (error != null) error.append(context.getResources().getString(R.string.not_a_dir)).append("\n");

      if (toLoad.size() <= 0) toLoad.add(new StringFile(Environment.getExternalStorageDirectory().getAbsolutePath(), "default"));
      return toLoad; 
    }

    if (!resValuesDir.canRead())
    {

      if (error != null) error.append(context.getResources().getString(R.string.no_right))
          .append(resValuesDir.getAbsolutePath()).append("\n");
      //Log.e(TAG, "ehm?? noright " + resValuesDir);
      if (toLoad.size() <= 0) toLoad.add(new StringFile(Environment.getExternalStorageDirectory().getAbsolutePath(), "default"));
      return toLoad; 
    } 

    ArrayList<File> tmp = new ArrayList<>();
    //if(resValuesDir.getName())
    Matcher tstValues = valuesPat.matcher(resValuesDir.getName());
    if (tstValues.find())
    {
      //we are in values....
      //Log.d(TAG, "values dr... moving up");
      resValuesDir = moveUp(resValuesDir);
    }
    tmp.add(resValuesDir);
    //Log.d(TAG,"enteding scandir with "+tmp);
    scanDirs(tmp);
    //Log.d(TAG,"exiting scandir");

    return(toLoad);
  }

  private File moveUp(File resValuesDir)
  {
    resValuesDir = resValuesDir.getParentFile();
    if (resValuesDir == null) resValuesDir = Environment.getExternalStorageDirectory();
    return resValuesDir;
  }//public ArrayList<StringFile> findStringFiles(String pathToLoad, StringBuilder error)

  private void scanDirs(ArrayList<File> list)
  {
    ArrayList<File> subDirs = new ArrayList<>();

    for (File aDir: list)
    {
      //Log.d(TAG, "checking " + aDir.getAbsolutePath());
      if (!aDir.canRead()) 
      {
        //Log.d(TAG, "not enugh rights : " + aDir.getAbsolutePath());


        if (error != null) 
        {

          if (context != null) error.append(context.getResources().getString(R.string.no_right)).append("\n");
          else error.append("insufficient rights for ");
          error.append(aDir.getAbsolutePath()).append("\n");  
        }//if (error != null) 


        continue;
      }
      //Log.d(TAG, "continuing with " + aDir);
      if (aDir.exists() && aDir.isFile())
      {
        //Log.d(TAG, "ignoring file : " + aDir.getAbsolutePath());
      }//if (aDir.exists() && aDir.isFile())
      else
      if (aDir.exists() && aDir.isDirectory() && !aDir.isHidden()) 
      {
        //Log.d(TAG, "isdir");
        Matcher m = valuesPat.matcher(aDir.getAbsolutePath());
        Matcher toIgn = ignPat.matcher(aDir.getAbsolutePath());
        if (m.find()) addFileToList(m, aDir);
        else
        {
          //Log.d(TAG, "no match ");
          try
          {
            //Log.d(TAG, "file list : " + aDir.listFiles());
            //if (!aDir.canRead()) Log.d(TAG, "can't read : " + aDir.getAbsolutePath());
            //if (!aDir.canExecute()) Log.d(TAG, "can't exe : " + aDir.getAbsolutePath());
            //if (!aDir.canWrite()) Log.d(TAG, "can't write : " + aDir.getAbsolutePath());
            if (aDir.canRead()) for (File aFile: aDir.listFiles())
              {
                //Log.d(TAG, "file:" + aFile);
                if (aFile.exists() && aFile.isDirectory() && !aFile.isHidden())
                {
                  m = valuesPat.matcher(aFile.getAbsolutePath());
                  if (m.find()) addFileToList(m, aFile);
                  else 
                  {
                    String fName = aFile.getName().trim();
                    toIgn = ignPat.matcher(fName);
                    if(toIgn.matches())
                    {
                      Log.i(TAG, "rejected("+fName+") " + aFile.getAbsolutePath());
                    }
                   else
                    {
                      //Log.d(TAG, "dirscan sd" + subDirs + " f:" + aFile);
                      //if(subDirs != null && aFile != null) //fix for a zendphone crash
                      subDirs.add(aFile);
                      //Log.d(TAG, "added " + aFile + " to " + subDirs);
                      Log.d(TAG, "added("+fName+") " + aFile.getAbsolutePath());
                    }//else
                  }//else 
                }
                //ignore files
              }//for (File aFile: aDir.listFiles())
          }//try
          catch (Exception e)
          {
            Log.d(TAG, "someting is null that shouldn't sd" + subDirs  + " e:" + e);
          }

        }//else
      }//if (aFile.exists())
    }//if (aDir.exists() && aDir.isDirectory()) 
    //Log.e(TAG,"would descent into "+subDirs);
    if (subDirs.size() > 0) scanDirs(subDirs);
  }//private void scanDirs(ArrayList<File> list)
  private void addFileToList(Matcher m, File aDir)
  {
    //Log.d(TAG, "isdir");
    String lang ;
    if (m.groupCount() <= 0) lang = "default";
    else 
    {
      LangNameNormalizer normalizer = new LangNameNormalizer();
      //StringBuilder sb = new StringBuilder();
      //sb.append(aDir.getName()).append(" matches count ").append(m.groupCount());
      //for (int i = 0; i <= m.groupCount(); i++) sb.append(" ").append(i).append(" ").append(m.group(i)).append(", ");
      //Log.d(TAG, sb.toString());

      lang = "default";
      if (m.group(1) == null)
      {
        lang = m.group(3);
        lang = normalizer.normalizeLangName(lang);
      }
      StringFile resLangDir = new StringFile(aDir, "", lang);

      StringFile resLangFile = new StringFile(aDir, "/strings.xml", lang);
      //Log.d(TAG,"adding for lang '"+lang+"' file :"+m.group(0));
      if (resLangFile.exists())
      {
        toLoad.add(resLangFile); 
        if ("default".equals(lang))
        {
          if (foundDefault) ambiguous = true;
          foundDefault = true;
        }
      }

      //else Log.e(TAG, "no strings in " + resLangDir);
    }//else
  }//private void addFileToList(Matcher m, File aDir)
  public boolean isAmbiguous()
  {
    return ambiguous;
  } 

}//class
