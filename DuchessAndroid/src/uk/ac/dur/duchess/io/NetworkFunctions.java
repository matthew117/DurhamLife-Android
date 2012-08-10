package uk.ac.dur.duchess.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkFunctions
{
	public static InputStream getHTTPResponseStream(String url, String httpMethod,
			byte[] postData) throws IOException
	{
		InputStream inputStream = null;
		int responseCode = -1;

		URLConnection urlConnection = (new URL(url)).openConnection();

		if (!(urlConnection instanceof HttpURLConnection))
			throw new IOException("Not an HTTP connection");

		HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
		httpConnection.setAllowUserInteraction(false);
		httpConnection.setInstanceFollowRedirects(true);
		httpConnection.setRequestMethod(httpMethod);

		if (httpMethod.equalsIgnoreCase("PUT") && postData != null)
		{
			httpConnection.setRequestProperty("Content-type", "application/xml");
			httpConnection.setDoOutput(true);
			OutputStream requestOutput = httpConnection.getOutputStream();
			requestOutput.write(postData);
			requestOutput.close();
		}
		
		httpConnection.connect();
		responseCode = httpConnection.getResponseCode();
		
		inputStream = httpConnection.getInputStream();

		return inputStream;
	}
	
	public static String downloadText(String url)
	{
		int BUFFER_SIZE = 2000;
		InputStream inputStream = null;
		
		try
		{
			inputStream = getHTTPResponseStream(url, "GET", null);
		}
		catch (Exception ex)
		{
			// TODO: handle exception
			return "";
		}
		
		InputStreamReader streamReader = new InputStreamReader(inputStream);
		int charRead;
		String s = "";
		char[] inputBuffer = new char[BUFFER_SIZE];
		
		try
		{
			while((charRead = streamReader.read(inputBuffer)) > 0)
			{
				s += String.valueOf(inputBuffer, 0, charRead);
				inputBuffer = new char[BUFFER_SIZE];
			}
			inputStream.close();
		}
		catch (Exception ex)
		{
			// TODO: handle exception
			return "";
		}
		return s;
	}
	
	public static Bitmap downloadImage(String url)
	{
		Bitmap bitmap = null;
		InputStream inputStream = null;
		try
		{
			inputStream = getHTTPResponseStream(url, "GET", null);
			bitmap = BitmapFactory.decodeStream(inputStream);
			inputStream.close();
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		return bitmap;
	}
	
	public static boolean networkIsConnected(Activity activity)
	{
		ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}
}
