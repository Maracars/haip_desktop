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
		scheduler.schedule(() -> Helpers.sendParsedFrame(frame, serial), delayMs, MILLISECONDS);
	}
}