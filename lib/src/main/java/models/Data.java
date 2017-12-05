package models;

import java.util.ArrayList;
import java.util.List;

public class Data {

	//Request = 00
	//Status = 01
	//Response = 10
	private String type;
	private Status status;
	private List<String> data; //Hau da data dana gordetzeko oingoz, gero egitura begiratu bikoda

	public Data() {
		data = new ArrayList<String>();
	}

	public Data(String type, Status status) {
		this.type = type;
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		String allData = "";
		if (data != null)
			for (String d : data) {
				allData += d;
			}
		return allData;
	}

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}


}
