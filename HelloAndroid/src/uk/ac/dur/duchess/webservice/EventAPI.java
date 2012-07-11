package uk.ac.dur.duchess.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import uk.ac.dur.duchess.entity.Event;
import uk.ac.dur.duchess.entity.EventXMLParser;

public class EventAPI
{
	private EventAPI()
	{
		// private constructor to prevent instantiation of an otherwise static class
	}

	public static List<Event> getAllEvents() throws IOException
	{
		List<Event> eventList = new ArrayList<Event>();

		URL url = new URL(API.DUCHESS_API_BASE_URL + "events.php");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);

		conn.connect();
		int httpResponseCode = conn.getResponseCode();

		if (httpResponseCode != HttpURLConnection.HTTP_OK) throw new IOException();
		
		InputStream inputStream = conn.getInputStream();
		
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();

			EventXMLParser myXMLHandler = new EventXMLParser(eventList);

			reader.setContentHandler(myXMLHandler);
			reader.parse(new InputSource(inputStream));
		}
		catch (SAXException e)
		{
			e.printStackTrace();
			throw new IOException();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			throw new IOException();
		}
		finally
		{
			inputStream.close();
		}

		return eventList;
	}

	private static String downloadText(InputStream inputStream) throws IOException
	{
		int BUFFER_SIZE = 2000;
		InputStreamReader streamReader = new InputStreamReader(inputStream);
		int charRead;
		StringBuilder sb = new StringBuilder();
		char[] inputBuffer = new char[BUFFER_SIZE];

		while ((charRead = streamReader.read(inputBuffer)) > 0)
		{
			sb.append(inputBuffer, 0, charRead);
			inputBuffer = new char[BUFFER_SIZE];
		}

		return sb.toString();
	}

	private static Bitmap downloadImage(InputStream inputStream)
	{
		return BitmapFactory.decodeStream(inputStream);
	}
}
