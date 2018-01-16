package protocol;

import helpers.Helpers;
import models.Frame;
import serial.Serial;

public class WaitForDiscovery implements Runnable{
	long sleep;
	Serial serial;
	Frame frame;
	
	public WaitForDiscovery(long sleep, Serial serial, Frame frame) {
		this.sleep = sleep;
		this.serial = serial;
		this.frame = frame;
	}

	@Override
	public void run() {
		long startingTime = System.currentTimeMillis();
		long elapsedTime = 0;
		do {
			elapsedTime = System.currentTimeMillis() - startingTime;
		} while (elapsedTime < sleep);
		Helpers.sendParsedFrame(frame, serial);
	}
}
