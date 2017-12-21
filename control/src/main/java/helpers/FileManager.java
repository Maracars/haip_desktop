package helpers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static helpers.SettingProperties.*;

public class FileManager {
	private File file;
	private boolean fileCreated;

	public FileManager() throws IOException {
		this.file = new File(FILE_NAME);
		fileCreated = this.file.createNewFile();
	}

	public void readFile() throws IOException {
		String line;
		String[] values;

		if (!fileCreated) {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME));
			while ((line = bufferedReader.readLine()) != null) {
				values = line.split("[$]");

				List<Integer> settingList = new ArrayList<>();
				for (String str : values) {
					settingList.add(Integer.parseInt(str));
				}
				SettingProperties.setProperties(settingList);
			}
		}
	}

	public void writeFile() {
		File file = new File(FILE_NAME);
		BufferedWriter bufferedWriter;

		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.setLength(0);

			bufferedWriter = new BufferedWriter(new FileWriter(file, true));

			for (Integer integer : SettingProperties.getProperties()) {
				bufferedWriter.write(String.valueOf(integer));
				bufferedWriter.write("$");
			}
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}