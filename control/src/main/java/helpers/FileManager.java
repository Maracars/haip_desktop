package helpers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static helpers.SettingProperties.*;

public class FileManager {
	private File file;
	private boolean fileCreated;

	List<Setting> settingList;
	/* 0 -> Dock Size
	1 -> Transit Size */

	public FileManager() throws IOException {
		this.file = new File(FILE_NAME);
		fileCreated = this.file.createNewFile();

		this.settingList = new ArrayList<>();
	}

	public List<Setting> readFile() throws IOException {
		String line;
		String[] values;

		if (!fileCreated) {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME));
			while ((line = bufferedReader.readLine()) != null) {
				values = line.split("[$]");
				settingList.add(new Setting(DOCK_SIZE, Integer.valueOf(values[0])));
				settingList.add(new Setting(TRANSIT_SIZE, Integer.valueOf(values[1])));
			}
		}
		if (fileCreated || settingList.size() != NUM_OF_SETTINGS) {
			settingList.add(new Setting(DOCK_SIZE, DEFAULT_DOCK_SIZE));
			settingList.add(new Setting(TRANSIT_SIZE, DEFAULT_TRANSIT_SIZE));
		}
		return settingList;
	}

	public void writeFile(List<Setting> settingList) {
		File file = new File(FILE_NAME);
		BufferedWriter bufferedWriter;

		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.setLength(0);

			bufferedWriter = new BufferedWriter(new FileWriter(file, true));
			for (Setting setting : settingList) {
				bufferedWriter.write(String.valueOf(setting.getValue()));
				bufferedWriter.write("$");
			}
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}