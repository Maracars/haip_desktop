package protocol;

import static protocol.ProtocolProperties.MASTER_ID;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import models.Frame;
import models.Ship;
import protocol.ProtocolProperties.PacketType;

public class SimulationShipLogic implements Observer{

	ArrayList<Ship> simulationShips;
	ShipLogic shipLogic;

	public SimulationShipLogic(ShipLogic shipLogic) {
		simulationShips = new ArrayList<>();
		this.shipLogic = shipLogic;
	}

	@Override
	public void update(Observable o, Object arg) {
		Frame frame = (Frame) arg;
		PacketType pt = PacketType.getName(frame.getHeader().getPacketType());
		switch(pt) {
		case DISCOVERY:
			sendAckFromAllBoats();
			break;
		case DATA:
			sendDataFromBoat(frame);
			break;
		case TOKEN:
			sendRequestFromBoat(frame);
			break;
		default:
			break;
		}

	}

	private void sendDataFromBoat(Frame receiveFrame) {
		for(Ship ship : simulationShips) {
			if(ship.getId().equals(receiveFrame.getDestinationId())) {
				shipLogic.checkShipMovement(receiveFrame, ship);
			}
		}
	}

	private void sendRequestFromBoat(Frame receiveFrame) {
		for(Ship ship : simulationShips) {
			if(ship.getId().equals(receiveFrame.getDestinationId())) {
				shipLogic.checkToken(receiveFrame, ship);
			}
		}
	}

	private void sendAckFromAllBoats() {
		for(Ship ship: simulationShips) {
			Frame frame =  FrameCreator.createAck(ship.getId(), MASTER_ID);
			System.out.println("Ship number " + Integer.parseInt(ship.getId(), 2) + " trying to connect");
			System.out.println("Ship number " + Integer.parseInt(ship.getId(), 2) + " sends ACK: " + frame.toString());
		}
	}
	
	public void addShipToSimulation(Ship ship) {
		simulationShips.add(ship);
	}

}
