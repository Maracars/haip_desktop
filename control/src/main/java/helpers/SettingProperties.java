package helpers;

import java.util.ArrayList;
import java.util.List;

public final class SettingProperties {
	public final static String FILE_NAME = "control/src/main/java/helpers/settings.txt";

	public static final int NUM_OF_SETTINGS = 2;

	public static final String DOCK_SIZE_PROPERTY_NAME = "Docking bay size";
	public static final String TRANSIT_SIZE_PROPERTY_NAME = "Transit zone size";

	public static int DOCKING_BAY_SHIP_LIMIT = 254;
	public static int TRANSIT_ZONE_SHIP_LIMIT = 1;

	public static void setDockingBayShipLimit(int dockingBayShipLimit) {
		DOCKING_BAY_SHIP_LIMIT = dockingBayShipLimit;
	}

	public static int getDockingBayShipLimit() {
		return DOCKING_BAY_SHIP_LIMIT;
	}

	public static int getTransitZoneShipLimit() {
		return TRANSIT_ZONE_SHIP_LIMIT;
	}

	public static void setTransitZoneShipLimit(int transitZoneShipLimit) {
		TRANSIT_ZONE_SHIP_LIMIT = transitZoneShipLimit;
	}

	public static List<String> getPropertyNames() {
		List<String> settingNameList = new ArrayList<>();
		settingNameList.add(DOCK_SIZE_PROPERTY_NAME);
		settingNameList.add(TRANSIT_SIZE_PROPERTY_NAME);
		return settingNameList;
	}

	public static void setProperties(List<Integer> settingList) {
		DOCKING_BAY_SHIP_LIMIT = settingList.get(0);
		TRANSIT_ZONE_SHIP_LIMIT = settingList.get(1);
	}

	public static List<Integer> getProperties() {
		List<Integer> settingList = new ArrayList<>();
		settingList.add(DOCKING_BAY_SHIP_LIMIT);
		settingList.add(TRANSIT_ZONE_SHIP_LIMIT);
		return settingList;
	}
}