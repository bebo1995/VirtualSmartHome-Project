package openhabBroker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import item.openhabBroker.Item;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OpenhabManager implements OpenhabInterface {

	public int key = 0;
	private static OpenhabManager openhabmanager;
	private JSONArray data = null;
	private ScheduledExecutorService executor;
	private Runnable polling;
	private static int secLoop;
	private String openhabUrl;
	private boolean isStarted = false;

	protected final static Logger log = LogManager.getLogger(OpenhabManager.class);

	/**
	 * Constructor
	 * 
	 * private constructor (singleton pattern)
	 */
	private OpenhabManager() {
		data = new JSONArray();

		// Variable for singleton test
		key = (key == 0 ? 1 : key + 1);

		System.out.println("Insert openhab url:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			openhabUrl = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		log.debug("OpenhabManager created.");

	}

	/**
	 * Auxiliary constructor method. It must be called for instantiate the object
	 * OpenhabManager. It must be used when you want to specify a specific loop
	 * period. If an OpenhabManager has been already created it returns that object.
	 * 
	 * @param int secondsLoop polling loop seconds
	 * @return object OpenhabManager
	 */
	public static OpenhabManager createOpenhabManager(int secondsLoop) {
		if (openhabmanager == null) {
			secLoop = secondsLoop;
			openhabmanager = new OpenhabManager();
		}
		return openhabmanager;
	}

	/**
	 * Auxiliary constructor method. It must be called for instantiate the object
	 * OpenhabManager if you don't want a specific loop period, it sets the loop by
	 * default to 1 second. If an OpenhabManager has been already created it returns
	 * that object.
	 * 
	 * @return object OpenhabManager
	 */
	public static OpenhabManager createOpenhabManager() {
		if (openhabmanager == null) {
			secLoop = 1;
			openhabmanager = new OpenhabManager();
		}
		return openhabmanager;
	}

	/**
	 * This method starts the execution of the polling service. It creates a
	 * PollingTheadObject only if there isn't another polling thread in execution.
	 */
	public void startPolling() {
		if (!isStarted) {
			executor = Executors.newScheduledThreadPool(1);
			polling = new PollingThread(openhabUrl);
			executor.scheduleAtFixedRate(polling, 0, secLoop, TimeUnit.SECONDS);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			isStarted = true;
			log.debug("Polling started.");
		}
	}

	/**
	 * This method stops the execution of the polling service.
	 */
	public void stopPolling() {
		if (executor != null) {
			executor.shutdown();
			try {
				if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
					executor.shutdownNow();
					if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
						log.error("Pool did not terminate");
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		polling = null;
		executor = null;
		isStarted = false;
		log.debug("Stop polling.");
	}

	/**
	 * This method returns the entire json items file.
	 * 
	 * @return string that contains the entire json file
	 */
	public String getJson() {
		log.debug("GetJson");
		if (isStarted) {
			// log.info("The json is: " + data.toString());
		} else {
			log.warn("Polling has been stopped. Data out of date. Json: " + data.toString());
		}
		return data.toString();
	}

	/**
	 * This method allows to create a list of object Item from json file; each Item
	 * contains the name of the item and his value.
	 * 
	 * @return List of objects Item
	 * 
	 */

	public List<Item> getList() {
		List<Item> list = null;
		log.debug("GetList");
		list = new ArrayList<Item>();
		for (int i = 0; i < data.size(); i++) {
			JSONObject jo = (JSONObject) data.get(i);
			String name = jo.get("name").toString();
			String value = jo.get("state").toString();
			Item newItem = new Item(name, value);
			list.add(newItem);
		}
		if (isStarted) {
			log.info("List created: " + list.toString());
		} else {
			log.warn("Polling has been stopped. Data out of date. Return: " + list.toString());
		}
		return list;
	}

	/**
	 * This method allows to create an hashmap from json file with all items of
	 * openhab.
	 * 
	 * @return HashMap
	 */
	public HashMap<String, String> getHashmap() {
		HashMap<String, String> hashmap = null;
		log.debug("GetHashmap");
		hashmap = new HashMap<String, String>();
		for (int i = 0; i < data.size(); i++) {
			JSONObject jo = (JSONObject) data.get(i);
			String name = jo.get("name").toString();
			String value = jo.get("state").toString();
			hashmap.put(name, value);
		}
		if (isStarted) {
			log.info("Hashmap created: " + hashmap.toString());
		} else {
			log.warn("Polling has been stopped. Data out of date. Return: " + hashmap.toString());
		}
		return hashmap;
	}

	/**
	 * This method returns the state of a specific Openhab item saved in json file
	 * by the polling service.
	 * 
	 * @param itemName: name of the Openhab item
	 * @return string that contains the state of the itemName item
	 */
	public String getStatusFromJson(String itemName) {
		String state = null;
		log.debug("GetStatusFromJson");
		for (int i = 0; i < data.size(); i++) {
			JSONObject jo = (JSONObject) data.get(i);
			if (jo.get("name").toString().equals(itemName)) {
				state = jo.get("state").toString();
			}
		}
		if (isStarted) {
			log.info("The status of the item required " + itemName + ": " + state);
		} else {
			log.warn("Polling has been stopped. Data out of date. Return: " + state);
		}

		return state;
	}

	/**
	 * This method is used to allow to GET immediately the status of an item or to
	 * update the json file after the invocation of the setStatus method.
	 * 
	 * @param fromUrl url where we want to do the GET rest call
	 * @return an Object that contains the rest response json
	 */
	private Object getImmediately(String fromUrl) {
		Object obj = null;
		try {
			URL url = new URL(fromUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				log.fatal("Openhab service is not running.");
				throw new RuntimeException("Openhab service is not running!" + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			String jsonTxt = "";
			while ((output = br.readLine()) != null) {
				jsonTxt += output;
			}

			conn.disconnect();
			obj = new Object();
			obj = JSONValue.parse(jsonTxt);
		} catch (MalformedURLException e) {
			log.fatal("Malformed Url");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * This method return the status of an OpenHab item, executing immediately a GET
	 * rest call on Openhab.
	 * 
	 * @param itemName: name of the Openhab item
	 * @return String that contains the state of the itemName item
	 */
	public String getStatus(String itemName) {
		Object obj = getImmediately(openhabUrl + "/rest/items/" + itemName);
		JSONObject item = (JSONObject) obj;
		String status = item.get("state").toString();
		log.debug("Get status from OpenHab");
		log.info("The status of required item " + itemName + ": " + status);
		return status;
	};

	/**
	 * This method update the JsonFile after the execution of the method setStatus
	 * to maintain update information.
	 */
	private void updateAfterSet() {
		Object obj = getImmediately(openhabUrl + "/rest/items/");
		JSONArray newData = (JSONArray) obj;
		data = newData;
		log.debug("Update Json file after setStatus or sendCommand method invocation.");
	}

	/**
	 * This method executes a PUT rest call on Openhab. It changes the state of a
	 * specific item. The method ends updating the current json file.
	 * 
	 * @param item: name of the item
	 * @param newState: new state of the item
	 * @return an Integer that contains the rest response
	 */
	public int setStatus(String item, String newState) {
		int response = 0;
		log.debug("Set Status");
		try {
			URL url = new URL(openhabUrl + "/rest/items/" + item + "/state");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Type", "text/plain");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestMethod("PUT");

			OutputStream wr = conn.getOutputStream();
			wr.write(newState.getBytes("UTF-8"));
			wr.close();

			response = conn.getResponseCode();

			if (conn.getResponseCode() != 202) {
				log.fatal("Openhab service is not running.");
				throw new RuntimeException("Item doesn't exist!" + conn.getResponseCode());
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			conn.disconnect();

		} catch (MalformedURLException e) {
			log.fatal("Malformed Url");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		updateAfterSet();
		log.info("The rest call response is: " + response);
		return response;
	}

	/**
	 * This method executes a POST rest call on Openhab. It sends a command and
	 * changes the state of a specific item. The method ends updating the current
	 * json file. This method must NOT be used to modify the state of CONTACT Items.
	 * 
	 * @param item: name of the item
	 * @param newState: new state of the item
	 * @return an Integer that contains the rest response
	 */
	public int sendCommand(String item, String newState) {
		int response = 0;
		if (!item.equals("GF_Frontdoor") && !item.equals("GF_Living_Window") && !item.equals("GF_Bathroom_Window")
				&& !item.equals("GF_Kitchen_Window") && !item.equals("FF_Bedroom_Window")
				&& !item.equals("FF_ChildrenRoom_Window")) {

			log.debug("Set Status");
			try {
				URL url = new URL(openhabUrl + "/rest/items/" + item);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setRequestProperty("Content-Type", "text/plain");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestMethod("POST");

				OutputStream wr = conn.getOutputStream();
				wr.write(newState.getBytes("UTF-8"));
				wr.close();

				response = conn.getResponseCode();

				if (conn.getResponseCode() != 200) {
					log.fatal("Openhab service is not running.");
					throw new RuntimeException("Item doesn't exist!" + conn.getResponseCode());
				}
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				conn.disconnect();

			} catch (MalformedURLException e) {
				log.fatal("Malformed Url");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			updateAfterSet();
			log.info("The rest call response is: " + response);
		} else {
			log.error("Method sendCommand() is not valid for Contact Item (e.g. " + item + "), try with setStatus().");
		}

		return response;
	}

	/**
	 * This method changes the content of the JSONArray.
	 * 
	 * @param newData: the new JSONArray object
	 */
	protected void set(JSONArray newData) {
		data = newData;
		log.debug("Polling. Time JVM: " + System.nanoTime());
		// log.info(newData.toString());
	}

}
