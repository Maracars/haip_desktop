package settings;

import java.util.ArrayList;
import java.util.List;

public final class Settings {
	public static final String FILE_NAME = "control/src/main/java/settings/settings.properties";

	public static final String[] PROPERTY_FIELD_TITLES = {"Docking bay size", "Transit zone size"};
	public static final String[] PROPERTY_NAMES = {"dockShipLimit", "transitShipLimit"};
	public static int[] SHIP_LIMITS = {254, 1};

	public static final int NUM_OF_SETTINGS = 2;

	public static void setProperties(List<String> settingList) {
		for (int i = 0; i < SHIP_LIMITS.length; i++) {
			SHIP_LIMITS[i] = Integer.parseInt(settingList.get(i));
		}
	}

	public static List<Integer> getProperties() {
		List<Integer> settingList = new ArrayList<>();
		for (int i = 0; i < SHIP_LIMITS.length; i++) {
			settingList.add(SHIP_LIMITS[i]);
		}
		return settingList;
	}
}