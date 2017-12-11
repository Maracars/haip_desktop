package protocol;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import models.Frame;

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

	private static void initializeValidators() {
		validators.add(new HeaderValidator());
		validators.add(new OriginValidator());
		validators.add(new DestinationValidator());
		validators.add(new LengthValidator());
		validators.add(new DataValidator());
		validators.add(new ChecksumValidator());
	}

	private static void initializeParsers() {
		parsers.add(new HeaderParser());
		parsers.add(new OriginParser());
		parsers.add(new DestinationParser());
		parsers.add(new LengthParser());
		parsers.add(new DataParser());
		parsers.add(new ChecksumParser());
	}

	public static int parseRx(String byteString) {

		//First, we check if the receive bytes can form a packet (Min. 5 bytes)
		if(!checkPacketSize(byteString))
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
		if(new BigInteger(byteString, 2).toByteArray().length >= 5) 
			return true;
		return false;	
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
		boolean filtered = true;
		for (Validator validator : validators) {
			if(!validator.validate(frame)) {
				filtered = false;
				break;
			}
		}
		return filtered;
	}

	public static List<String> parseTx(Frame frame) {
		List<String> byteList = new ArrayList<>();
		for (Parser parser : parsers) {
			byteList = parser.parseTx(frame, byteList);
		}
		return byteList;
	}  

}
