package test.openhabBroker;

import openhabBroker.OpenhabManager;

public class Main {

	public static void main(String[] args) {

		OpenhabManager manager = OpenhabManager.createOpenhabManager(2);

		// The polling is not starting it returns NULL
		manager.getStatusFromJson("GF_Bathroom_Temperature");
		// Verify warning for out of date data
		manager.getJson();
		manager.getList();
		manager.getHashmap();
		// Starts the polling

		manager.startPolling();
		// Set a status and verify by getFromJSON and by Get
		manager.setStatus("GF_Kitchen_Smoke", "ON");
		manager.getStatusFromJson("GF_Kitchen_Fire_Alarm");
		manager.getStatus("GF_Kitchen_Fire_Alarm");
		// It creates a list and an hashmap with all openhab items
		manager.getHashmap();
		manager.getList();
		// Stop the polling service
		manager.stopPolling();

		// Verify warning for out of date data
		manager.getJson();
		manager.getList();
		manager.getHashmap();

		// Verify the error message when we use sendCommand instead of setStatus
		manager.sendCommand("GF_Frontdoor", "CLOSED");
		// Verify setStatus()
		manager.setStatus("GF_Frontdoor", "CLOSED");
		// Restart the polling
		manager.startPolling();

	}
}
