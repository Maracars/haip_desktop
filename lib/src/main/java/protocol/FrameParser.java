package protocol;

import models.Frame;
import protocol.parsers.*;
import protocol.validators.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static protocol.ProtocolProperties.*;

public class FrameParser {

	public static final int FIN_PACKET = 1;
	public static final int BAD_PACKET = -1;
	public static final int UNFIN_PACKET = 0;
	public static Frame frame;
	private static List<Validator> validators;
	private static List<Parser> parsers;

	static {
		frame = new Frame();
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
				new LengthValidator(), new DataValidator(), new ChecksumValidator());

	}

	private static void initializeParsers() {
		parsers = Arrays.asList(new HeaderParser(), new OriginParser(), new DestinationParser(), new LengthParser(),
				new DataParser(), new ChecksumParser());
	}

	public static int parseRx(String byteString) {

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
	}

	private static boolean checkPacketSize(String byteString) {
		try {

			int length = Integer.parseInt(byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID,
					HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH), 2);

			return (byteString.length() == (HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH
					+ length + CHECKSUM));

		} catch (StringIndexOutOfBoundsException e) {
			return false;
		}

	}

	public static void resetCommunication() {
		frame = new Frame();
	}

	private static int checkPacketFinal() {
		if (frame.getChecksum() != null) {
			return FIN_PACKET;
		}
		return UNFIN_PACKET;
	}

	private static void parseData(String byteString) {
		for (Parser parser : parsers) {
			frame = parser.parseRx(frame, byteString);
		}
	}

	private static boolean validateData() {
		for (Validator validator : validators) {
			if (!validator.validate(frame)) {
				return false;
			}
		}
		return true;
	}

	public static List<String> parseTx(Frame frame) {
		List<String> byteList = new ArrayList<>();
		for (Parser parser : parsers) {
			byteList = parser.parseTx(frame, byteList);
		}
		return byteList;
	}

}
