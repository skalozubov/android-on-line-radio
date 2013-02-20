package org.zub.dnb101ru;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

public class Downloader {

public Downloader() 
        {
    // TODO Auto-generated method stub
    }

public boolean DownloadFile(String url, File outputFile) 
    {
	int count;
    try {
      URL u = new URL(url);
      Log.i("JSONTest", ">>>>>>>>>>>>>>>>>>>>>" + u);
      URLConnection conn = u.openConnection();
      int contentLength = conn.getContentLength();
      Log.i("JSONTest", ">>>>>>>>>>>>>>>>>>>>>contentLength is: " + contentLength);
      if (contentLength == -1) {
    	  return false;
      }
      DataInputStream stream = new DataInputStream(u.openStream());


      OutputStream output = new FileOutputStream(outputFile);
      byte data[] = new byte[1024];

      System.out.println("downloading.............");
      while ((count = stream.read(data)) != -1) {
          output.write(data, 0, count);                   
      }
      Log.i("JSONTest", ">>>>>>>>>>>>>>>>>>>>>Download complete");
      output.flush();
      output.close();
      stream.close();
      } 
    catch(FileNotFoundException e) 
      {
      return false; 
      } 
    catch (IOException e) 
      {
      return false; 
      }

    return true;
    }
}
