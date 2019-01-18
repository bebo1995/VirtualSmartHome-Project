package test.openhabBroker;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

import item.openhabBroker.Item;
import openhabBroker.OpenhabManager;

public class OpenhabManagerTest {

	// Create OpenhabManager object test
	@Test
	public void testCreateOpenhabManager() {
		OpenhabManager om1 = OpenhabManager.createOpenhabManager();
		OpenhabManager om2 = OpenhabManager.createOpenhabManager();

		// Creation control for objects om1 and om2
		assertNotNull(om1);
		assertNotNull(om2);

		// Singleton control: om1 and om2 are equals?
		assertTrue(om1.key == om2.key && om1.key == 1);
	}

	// Create OpenhabManager object test with loop time
	@Test
	public void testCreateOpenhabManagerInt() {
		OpenhabManager om1 = OpenhabManager.createOpenhabManager(2);
		OpenhabManager om2 = OpenhabManager.createOpenhabManager(2);

		// Creation control for objects om1 and om2
		assertNotNull(om1);
		assertNotNull(om2);

		// Singleton control: om1 and om2 are equals?
		assertTrue(om1.key == om2.key && om1.key == 1);
	}

	// Create test class for getJson method
	@Test
	public void testGetJson() {
		OpenhabManager om = OpenhabManager.createOpenhabManager();

		// Start polling service
		om.startPolling();

		// Check the method return something
		assertNotNull(om.getJson());

		try {
			URL url = new URL("http://localhost:8080/rest/items");
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
			JSONArray ajson = (JSONArray) obj;

			// Check the method return the expected value
			assertTrue(om.getJson().compareTo(ajson.toString()) == 0);

			conn.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Stop polling service
		om.stopPolling();
	}

	// Create test class for getList method
	@Test
	public void testGetList() {
		OpenhabManager om = OpenhabManager.createOpenhabManager();
		om.startPolling();

		List<Item> l = om.getList();
		assertNotNull(l);

		String itemName = "GF_Bathroom_Temperature";
		Iterator<Item> iterator = l.iterator();
		String status = "";

		while (iterator.hasNext()) {
			Item i = iterator.next();
			if (i.getItemName().equals(itemName)) {
				status = i.getItemValue();
				break;
			}
		}

		assertEquals(om.getStatus(itemName), status);
	}

	// Create test class for getStatus method
	@Test
	public void testGetStatus() {
		OpenhabManager om = OpenhabManager.createOpenhabManager();
		assertNotNull(om);

		String itemName = "GF_Bathroom_Temperature";

		String content = om.getJson();
		Object obj = JSONValue.parse(content);
		JSONArray ajson = (JSONArray) obj;

		String status = "";
		String name = "";

		int i = 0;
		do {
			JSONObject jobj = (JSONObject) ajson.get(i);
			name = jobj.get("name").toString();
			status = jobj.get("state").toString();

			i++;
		} while (i < ajson.size() && name == itemName);

		assertTrue(om.getStatus(name).compareTo(status) == 0);
	}

	// Create test class for getStatusFromJson method
	@Test
	public void testGetStatusFromJson() {
		OpenhabManager om = OpenhabManager.createOpenhabManager();

		// Starting polling service
		om.startPolling();

		String itemName = "GF_Bathroom_Temperature";

		assertEquals(om.getStatus(itemName), om.getStatusFromJson(itemName));
	}

	// Create test class for getHashmap method
	@Test
	public void testGetHashmap() {
		OpenhabManager om = OpenhabManager.createOpenhabManager();
		om.startPolling();

		HashMap<String, String> hm = om.getHashmap();
		assertNotNull(hm);

		assertEquals(om.getStatus("GF_Bathroom_Temperature"), hm.get("GF_Bathroom_Temperature"));
	}

	// Create test class for SetStatus method
	@Test
	public void testSetStatus() {
		OpenhabManager om = OpenhabManager.createOpenhabManager();
		om.setStatus("GF_Frontdoor", "OPEN");
		assertEquals("OPEN", om.getStatus("GF_Frontdoor"));
	}

	// Test incorrect item exception
	@Test(expected = Exception.class)
	public void verifySetStatusException() {
		OpenhabManager om = OpenhabManager.createOpenhabManager();
		om.setStatus("F_Frontdoor", "OPEN");
	}

	// Create test class for SendCommand method
	@Test
	public void testSendCommand() {
		OpenhabManager om = OpenhabManager.createOpenhabManager();
		assertEquals(200, om.sendCommand("GF_Bathroom_Temperature", "19.1"));
		assertEquals(0, om.sendCommand("GF_Frontdoor", "OPEN")); // You cannot send commands to Contact items
	}

	// Test incorrect item exception
	@Test(expected = Exception.class)
	public void verifySendCommandException() {
		OpenhabManager om = OpenhabManager.createOpenhabManager();
		om.sendCommand("F_Frontdoor", "OPEN");
	}

	// Create test class for StopPolling method
	@Test
	public void testStopPollinggetStatusFromJson() {
		OpenhabManager om = OpenhabManager.createOpenhabManager();
		om.startPolling();

		String value = om.getStatusFromJson("GF_Bathroom_Temperature");
		om.stopPolling();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Check of the unchanging value of the variable
		assertEquals(value, om.getStatusFromJson("GF_Bathroom_Temperature"));
	}

	// Create test class for StopPolling method
	@Test
	public void testStopPollinggetHashmap() {
		OpenhabManager om = OpenhabManager.createOpenhabManager();
		om.startPolling();

		HashMap<String, String> hm = om.getHashmap();
		om.stopPolling();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Check of the unchanging value of the variable
		assertEquals(om.getHashmap(), hm);
	}

	// Create test class for StopPolling method
	@Test
	public void testStopPollinggetJson() {
		OpenhabManager om = OpenhabManager.createOpenhabManager();
		om.startPolling();

		String s = om.getJson();
		om.stopPolling();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Check of the unchanging value of the variable
		assertEquals(om.getJson(), s);
	}

}