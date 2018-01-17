package protocol;

import helpers.Helpers;
import models.Frame;
import serial.Serial;
import ui.log.LogListModel;

public class WaitForDiscovery implements Runnable{
	private long sleep;
	private Serial serial;
	private Frame frame;
	
	public WaitForDiscovery(long sleep, Serial serial, Frame frame) {
		this.sleep = sleep;
		this.serial = serial;
		this.frame = frame;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Helpers.sendParsedFrame(frame, serial);
		LogListModel.add("Ship number " + Integer.parseInt(frame.getOriginId(),2) + " sends ACK to connect");
	}
}
