package uk.ac.dur.duchess.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import uk.ac.dur.duchess.GlobalApplicationData;
import uk.ac.dur.duchess.io.json.EventJSONParser;
import uk.ac.dur.duchess.io.provider.DataProvider;
import uk.ac.dur.duchess.io.xml.EventXMLParser;
import uk.ac.dur.duchess.model.Event;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class EventAPI
{
	private static final String EVENT_BASE_URI = "events.php";

	private EventAPI()
	{
		// private constructor to prevent instantiation of an otherwise static class
	}

	public static List<Event> downloadAllEvents() throws IOException
	{
//		return downloadEventXML(EVENT_BASE_URI);
		
		return EventJSONParser.parseJSONEvents("https://api.dur.ac.uk/events?category=50");
	}

	public static List<Event> downloadNewEvents(String timestamp) throws IOException,
			ParseException
	{
		List<Event> eventList = new ArrayList<Event>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		long unixTimpstamp = (sdf.parse(timestamp)).getTime() / 1000;

		return downloadEventXML(EVENT_BASE_URI + "?time=" + unixTimpstamp);
	}

	public static List<Event> downloadFeaturedEvents() throws IOException
	{
		return downloadEventXML(EVENT_BASE_URI + "?featured=true");
	}

	public static List<Event> downloadEventsByCategory(String category) throws IOException
	{
		return downloadEventXML(EVENT_BASE_URI + "?category=" + category);
	}

	public static List<Event> downloadEventsByCollege(String college) throws IOException
	{
		return downloadEventXML(EVENT_BASE_URI + "?college=" + URLEncoder.encode(college, "UTF-8"));
	}

	public static List<Event> downloadEventsBySociety(String society) throws IOException
	{
		return downloadEventXML(EVENT_BASE_URI + "?societyName=" + URLEncoder.encode(society, "UTF-8"));
	}

	private static List<Event> downloadEventXML(String urlExtension) throws MalformedURLException,
			IOException, ProtocolException, FactoryConfigurationError
	{
		List<Event> eventList = new ArrayList<Event>();

		URL url = new URL(API.DUCHESS_API_BASE_URL + urlExtension);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		
		conn.connect();
		int httpResponseCode = conn.getResponseCode();

		if (httpResponseCode != HttpURLConnection.HTTP_OK) throw new IOException();

		GlobalApplicationData delegate = GlobalApplicationData.getInstance();
		DataProvider data = delegate.getDataProvider();
		data.setCacheExpiresAt(delegate.getApplicationContext(), conn.getExpiration());
		
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
			conn.disconnect();
		}

		return eventList;
	}

	public static String downloadText(InputStream inputStream) throws IOException
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

	public static Bitmap downloadImage(InputStream inputStream)
	{
		return BitmapFactory.decodeStream(inputStream);
	}
}
