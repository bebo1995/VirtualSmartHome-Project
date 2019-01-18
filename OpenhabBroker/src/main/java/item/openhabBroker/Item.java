package item.openhabBroker;

public class Item {

	private String itemName;
	private String itemValue;

	/**
	 * Constructor of object Item
	 * 
	 * @param name: contains the name of the item
	 * @param value: contains the actual state of the item
	 */
	public Item(String name, String value) {
		itemName = name;
		itemValue = value;
	}

	/**
	 * Method that returns the name of the object Item which has invoked it.
	 * 
	 * @return itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * Method that returns the value of an object Item which has invoked it.
	 * 
	 * @return itemValue
	 */
	public String getItemValue() {
		return itemValue;
	}

}
