package protocol;

import models.Frame;
import protocol.parsers.*;
import protocol.validators.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

import static protocol.ProtocolProperties.*;

public class FrameParser extends Observable {

	public static final int FIN_PACKET = 1;
	public static final int BAD_PACKET = -1;
	public static final int UNFIN_PACKET = 0;
	private static String packet;
	private static List<String> potentialPackets;
	private static Frame frame;
	private static boolean validPacket;
	private static List<Validator> validators;
	private static List<Parser> parsers;

	static {
		packet = "";
		potentialPackets = new ArrayList<>();
		validPacket = false;
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

	/*public static int parseRx(String byteString) {

		//First, we check if the receive bytes can form a packet (Min. 5 bytes)
		if (!checkPacketSize(byteString))
			return UNFIN_PACKET;

		//If the packet can be formed, we parse all the bytes
		parseData(byteString);

		//Then, we validate the Data
		if (!validateData()) {
			resetCommunication();
			return BAD_PACKET;
		}

		//If the validation is OK, we check that finally, the packet has been form entirely.
		return checkPacketFinal();
	}*/

	public static boolean parseRx(String newBits) {
		packet += newBits;
		potentialPackets = potentialPackets.stream().map(s -> s + newBits).collect(Collectors.toCollection(ArrayList::new));

		while (packet.length() >= 1) {
			System.out.println(packet);
			if (packet.length() >= 3) {
				int dataLength = Integer.parseInt(packet.substring(0, LENGTH), 3);
				int expectedLength = (HEADER + ORIGIN_ID + DESTINATION_ID + (dataLength * 8) + CHECKSUM);

				if (expectedLength <= packet.length()) {
					Frame frame = parseData(packet);

					if (validateData(frame)) {
						if (checkPacketFinal(frame)) {
							FrameParser.frame = frame;
							return true;
						}
					}
				}
				else {
					potentialPackets.add(packet);
				}
			}
			else potentialPackets.add(packet);
			packet = packet.substring(1, packet.length());

			/*for (String s : potentialPackets) {
				int dataLengthPotential = Integer.parseInt(s.substring(0, LENGTH), 3);
				int expectedLengthPotential = (HEADER + ORIGIN_ID + DESTINATION_ID + (dataLengthPotential * 8) + CHECKSUM);

				if (expectedLengthPotential <= s.length()) {
					Frame frame = parseData(s);

					if (validateData(frame)) {
						return checkPacketFinal(frame);
					}
					else {
						potentialPackets.remove(s);
					}
				}
			}*/
		}
		return false;
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

	private static boolean checkPacketFinal(Frame frame) {
		if (frame.getChecksum() != null) {
			return true;
		}
		else {
			return false;
		}
	}

	/*private static boolean checkPacketSize(String byteString) {
		try {
			int length = Integer.parseInt(byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID,
					HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH), 2);

			return (byteString.length() == (HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH
					+ length + CHECKSUM));

		} catch (StringIndexOutOfBoundsException e) {
			return false;
		}

	}*/

	public static void resetCommunication() {
		frame = new Frame();
	}

	public static List<String> parseTx(Frame frame) {
		List<String> byteList = new ArrayList<>();
		for (Parser parser : parsers) {
			byteList = parser.parseTx(frame, byteList);
		}
		return byteList;
	}

}
