package openhabBroker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

public class PollingThread implements Runnable {

	private OpenhabManager manager;
	private String openhabUrl;

	public PollingThread(String url) {
		manager = OpenhabManager.createOpenhabManager();
		openhabUrl = url;
	}

	/**
	 * This thread executes the GET REST call on the Openhab items json file. It
	 * implements the action of the polling system for Openhab management.
	 */
	public void run() {
		JSONArray newData = null;
		try {
			URL url = new URL(openhabUrl + "/rest/items/");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				OpenhabManager.log.fatal("Openhab service is not running.");
				throw new RuntimeException("Openhab service is not running!" + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			String jsonTxt = "";
			while ((output = br.readLine()) != null) {
				jsonTxt += output;
			}

			conn.disconnect();
			Object obj = new Object();
			obj = JSONValue.parse(jsonTxt);
			newData = (JSONArray) obj;
		} catch (MalformedURLException e) {
			OpenhabManager.log.fatal("Malformed Url");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		manager.set(newData);

	}
}
