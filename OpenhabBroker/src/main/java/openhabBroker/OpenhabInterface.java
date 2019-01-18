package openhabBroker;

import java.util.HashMap;
import java.util.List;
import item.openhabBroker.Item;

public interface OpenhabInterface {

	public void startPolling();
	public void stopPolling();
	public String getJson();
	public List<Item> getList();
	public HashMap<String, String> getHashmap();
	public String getStatusFromJson(String item);
	public String getStatus(String item);
	public int setStatus(String item, String newState); 
	public int sendCommand(String item, String newState);

		
}
