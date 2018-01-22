package protocol;

import helpers.Helpers;
import models.Frame;
import serial.Serial;
import ui.log.LogListModel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class AckDelayScheduler {
	private final ScheduledExecutorService scheduler;

	AckDelayScheduler() {
		this.scheduler = Executors.newScheduledThreadPool(1);
	}

	public void sendAckAfterDelay(Frame frame, Serial serial, long delayMs) {
		scheduler.schedule(() -> sendAck(frame, serial), delayMs, MILLISECONDS);
	}
	
	public void sendAck(Frame frame, Serial serial) {
		Helpers.sendParsedFrame(frame, serial);
		LogListModel.add("ACK sent by ship: "+Integer.parseInt(frame.getOriginId(), 2));
	}
}