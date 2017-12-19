package protocol;

import models.Frame;
import protocol.parsers.*;
import protocol.validators.*;

import java.util.*;
import java.util.stream.Collectors;

import static protocol.ProtocolProperties.*;

public class FrameParser extends Observable {
	private static List<String> potentialPackets;
	static boolean validFrame;
	private static Frame frame;
	private static List<Validator> validators;
	private static List<Parser> parsers;

	static {
		potentialPackets = new ArrayList<>();
		validFrame = false;
		validators = new ArrayList<>();
		parsers = new ArrayList<>();
		initializeValidators();
		initializeParsers();
	}

	public static Frame getFrame() {
		return frame;
	}

	private static void initializeValidators() {
		validators = Arrays.asList(new HeaderValidator(), new OriginValidator(), new DestinationValidator(),
				new DataValidator(), new ChecksumValidator());

	}

	private static void initializeParsers() {
		parsers = Arrays.asList(new HeaderParser(), new OriginParser(), new DestinationParser(),
				new DataParser(), new ChecksumParser());
	}

	public static boolean parseRx(String newBits) {
		String packet = newBits;
		potentialPackets = potentialPackets.stream().map(s -> s + newBits).collect(Collectors.toCollection(ArrayList::new));

		Iterator<String> iterator = potentialPackets.iterator();
		while (iterator.hasNext()) {
			String potentialPacket = iterator.next();

			if (potentialPacket.length() >= LENGTH) {
				if (getExpectedPacketLength(potentialPacket) <= potentialPacket.length()) {

					Frame frame = parseData(potentialPacket);
					iterator.remove();
					if (validateData(frame)) {
						FrameParser.frame = frame;
						validFrame = true;
						break;
					}
				}
			}
		}
		while (packet.length() > 0) {
			if (packet.length() >= LENGTH) {
				if (getExpectedPacketLength(packet) <= packet.length()) {

					Frame frame = parseData(packet);
					if (validateData(frame)) {
						FrameParser.frame = frame;
						validFrame = true;
						break;
					}
				} else {
					potentialPackets.add(packet);
				}
			} else {
				potentialPackets.add(packet);
			}
			packet = packet.substring(1, packet.length());
		}
		return validFrame;
	}

	private static int getExpectedPacketLength(String packet) {
		int dataLength = (8 * Integer.parseInt(packet.substring(0, LENGTH), 2));
		return HEADER + ORIGIN_ID + DESTINATION_ID + dataLength + CHECKSUM;
	}

	private static Frame parseData(String byteString) {
		Frame frame = new Frame();
		for (Parser parser : parsers) {
			frame = parser.parseRx(frame, byteString);
		}
		return frame;
	}

	private static boolean validateData(Frame frame) {
		for (Validator validator : validators) {
			if (!validator.validate(frame)) {
				return false;
			}
		}
		return true;
	}

	public static void resetCommunication() {
		frame = new Frame();
		validFrame = false;
	}

	public static List<String> parseTx(Frame frame) {
		List<String> byteList = new ArrayList<>();
		for (Parser parser : parsers) {
			byteList = parser.parseTx(frame, byteList);
		}
		return byteList;
	}

}
