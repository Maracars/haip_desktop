package protocol;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import helpers.Helpers;
import models.Frame;
import serial.Serial;
import serial.SocketClient;
import ui.log.LogListModel;

public class AckDelayScheduler {
	private final ScheduledExecutorService scheduler;

	AckDelayScheduler() {
		this.scheduler = Executors.newScheduledThreadPool(1);
	}

	public void sendAckAfterDelay(Frame frame, Serial serial, long delayMs, SocketClient socketClient) {
		scheduler.schedule(() -> sendAck(frame, serial, socketClient), delayMs, MILLISECONDS);
	}
	
	public void sendAck(Frame frame, Serial serial, SocketClient socketClient) {
		Helpers.sendParsedFrame(frame, serial, null, socketClient);
		LogListModel.add("ACK sent by ship: "+Integer.parseInt(frame.getOriginId(), 2));
	}
}