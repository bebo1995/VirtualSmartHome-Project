package ClientBroker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class hashmap {
	private HashMap<String, String> hmap;
	private JSONArray json;
	private static String index = "http://localhost:8080/rest/items";

	public hashmap() {
		this.init();
	}

	public void init() {

		try {
			URL url = new URL(index);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			StringBuilder content = new StringBuilder();
			String line;

			if ((line = br.readLine()) != null) {
				content.append(line);
			}

			Object obj = JSONValue.parse(content.toString());
			this.json = (JSONArray) obj;
			conn.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONArray ajson = this.json;

		this.hmap = new HashMap<String, String>();

		for (int i = 0; i < ajson.size(); i++) {
			JSONObject jobj = (JSONObject) ajson.get(i);
			String name = jobj.get("name").toString();
			String status = jobj.get("state").toString();

			this.hmap.put(name, status);
		}

	}

	public HashMap<String, String> update() {
		HashMap<String, String> changes = new HashMap<String, String>();

		try {
			URL url = new URL(index);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			StringBuilder content = new StringBuilder();
			String line;

			if ((line = br.readLine()) != null) {
				content.append(line);
			}

			Object obj = JSONValue.parse(content.toString());
			this.json = (JSONArray) obj;
			conn.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONArray ajson = this.json;

		for (int i = 0; i < ajson.size(); i++) {
			JSONObject jobj = (JSONObject) ajson.get(i);
			String name = jobj.get("name").toString();
			String status = jobj.get("state").toString();

			if (this.hmap.containsKey(name)) {
				String old_value = this.hmap.get(name).toString();

				// Verifico se ci sono stati cambiamenti
				if (!(old_value.equals(status))) {
					this.hmap.put(name, status);
					changes.put(name, status);
					// System.out.println("TO UPDATE: "+name+" Previous: "+old_value+" Now:
					// "+status);
				}
			} else {
				// System.out.println("NEW INSERT: "+name+" "+status);
				this.hmap.put(name, status);
				changes.put(name, status);
			}
		}
		return changes;
	}

	public HashMap<String, String> getState() {
		return this.hmap;
	}

	public HashMap<String, String> getState(String name) {
		HashMap<String, String> response = new HashMap<String, String>();

		String status = this.hmap.get(name);
		response.put(name, status);

		return response;
	}
}
