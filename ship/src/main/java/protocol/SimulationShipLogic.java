package protocol;

import models.Frame;
import models.Ship;
import protocol.ProtocolProperties.PacketType;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import static protocol.ProtocolProperties.MASTER_ID;

public class SimulationShipLogic implements Observer {

	private ArrayList<Ship> simulationShips;
	private ShipLogic shipLogic;

	public SimulationShipLogic(ShipLogic shipLogic) {
		simulationShips = new ArrayList<>();
		this.shipLogic = shipLogic;
	}

	@Override
	public void update(Observable o, Object arg) {
		Frame frame = (Frame) arg;
		PacketType pt = PacketType.getName(frame.getHeader().getPacketType());
		switch (pt) {
			case DISCOVERY:
				sendAckFromAllBoats(frame);
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
		for (Ship ship : simulationShips) {
			if (ship.getId().equals(receiveFrame.getDestinationId())) {
				shipLogic.checkShipMovement(receiveFrame, ship);
			}
		}
	}

	private void sendRequestFromBoat(Frame receiveFrame) {
		for (Ship ship : simulationShips) {
			if (ship.getId().equals(receiveFrame.getDestinationId())) {
				System.out.println("Received token, permission to talk for ship " + Integer.parseInt(ship.getId(), 2));
				Frame sendFrame = shipLogic.checkToken(receiveFrame, ship);
				shipLogic.replyController(sendFrame);
			}
		}
	}

	private void sendAckFromAllBoats(Frame receiveFrame) {
		for (Ship ship : simulationShips) {
			if(ship.checkDiscovery()) {
				shipLogic.checkDiscovery(receiveFrame, ship);
			}
		}
	}

	public void addShipToSimulation(Ship ship) {
		simulationShips.add(ship);
	}

	public void resetSimulationShipList() {
		simulationShips.clear();
	}

	public ShipLogic getShipLogic() {
		return shipLogic;
	}

}
