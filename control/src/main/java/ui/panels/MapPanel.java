package ui.panels;

import models.Port;
import models.Ship;
import protocol.ProtocolProperties;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;
import settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MapPanel extends JPanel implements ComponentListener, Observer {
	private static final long serialVersionUID = 1L;

	private static final int TRANSITION_HEIGHT = 100;
	private static final int PARKING_HEIGHT = 80;
	private static final int PARKING_WIDTH = 80;
	private static final int BOAT_HEIGHT = 30;
	private static final int BOAT_WIDTH = 30;

	private Dimension panelDimension;
	private Point panelLocation;
	private List<Ship> shipList;
	private Port port;

	private int seaHeight, transitHeight, parkingHeight, seaWidth, transitWidth, parkingWidth;

	public MapPanel(Port port) {
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setLayout(new BorderLayout());
		this.addComponentListener(this);
		shipList = new ArrayList<>();
		this.port = port;
	}

	private void displayDivision() {
		int displayHeight = panelDimension.height;
		int displayWidth = panelDimension.width;

		seaHeight = displayHeight/2 - TRANSITION_HEIGHT/2;
		seaWidth = displayWidth;

		transitHeight = TRANSITION_HEIGHT;
		transitWidth = displayWidth;

		parkingHeight = displayHeight/2 - TRANSITION_HEIGHT/2;
		parkingWidth = displayWidth;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(new Color(100, 155, 244));
		g.fillRect(0, 0, seaWidth, seaHeight);
		paintTransitZone(g);

		g.setColor(new Color(52, 120, 229));
		g.fillRect(0, seaHeight+transitHeight, parkingWidth, parkingHeight);
		paintParkings(g);

		paintBoats(g);

	}

	private void paintBoats(Graphics g) {
		for(Ship ship : shipList) {
			if(ship.getStatus() != null) {
				checkBoatPositionAndDraw(g, ship);
			}
		}
	}

	private void checkBoatPositionAndDraw(Graphics g, Ship ship) {
		int x, y;
		StatusType st = StatusType.getName(ship.getStatus().getStatus());
		g.setColor(new Color(77, 244, 65));
		checkBoatActionTypeAndPermissions(g, ship);
		switch(st) {
			case PARKING:
				int parkingIndex = checkBoatParking(ship);
				x = (panelLocation.x+(panelDimension.width/2))+(parkingIndex)*PARKING_WIDTH;
				y = panelDimension.height-(PARKING_HEIGHT/2);
				g.fillOval(x,y, BOAT_WIDTH, BOAT_HEIGHT);
				g.setColor(Color.BLACK);
				g.drawString(Integer.toHexString(Integer.parseInt(ship.getId(), 2)), x + BOAT_WIDTH/2 - 4, y + BOAT_HEIGHT / 2 + 3);
				break;
			case TRANSIT:
				x = (int) (panelLocation.getX() + transitWidth + BOAT_WIDTH / 2);
				y = seaHeight + transitHeight/2;
				g.fillOval(x, y, BOAT_WIDTH, BOAT_HEIGHT);
				g.setColor(Color.BLACK);
				g.drawString(Integer.toHexString(Integer.parseInt(ship.getId(), 2)), x + BOAT_WIDTH/2 - 4, y + BOAT_HEIGHT / 2 + 3);
				break;
			case SEA:
				Point point = checkBoatLocation(ship);
				g.fillOval((int)point.getX(), (int)point.getY(), BOAT_WIDTH, BOAT_HEIGHT);
				g.setColor(Color.BLACK);
				g.drawString(Integer.toHexString(Integer.parseInt(ship.getId(), 2)), (int) (point.getX() + BOAT_WIDTH/2 - 4), (int) (point.getY() + BOAT_HEIGHT / 2 + 3));
				break;
		}

	}

	//Funtzio hau puta mierda bat da
	private int checkBoatParking(Ship ship) {
		for (int i = 0; i < port.getDock().getMoorings().size(); i++) {
			if (port.getDock().getMoorings().get(i).getShip() != null
					&& port.getDock().getMoorings().get(i).getShip().getId().equals(ship.getId())) {
				return (i < Math.round(port.getDock().getMoorings().size() / 2)) ? i - Math.round(port.getDock().getMoorings().size() / 2) : i;
			}
		}
		return -1;

	}

	private Point checkBoatLocation(Ship ship) {
		int x, y;
		int maxBoats = (int) Math.pow(ProtocolProperties.ORIGIN_ID, 2);
		int shipId = Integer.parseInt(ship.getId(), 2);
		if(shipId < maxBoats/2) {
			x = (int) (panelLocation.getX() + shipId*(seaWidth/(maxBoats/2)));
			y = seaHeight/4;
			return new Point(x, y);

		}else{
			x = (int) (panelLocation.getX() + shipId*(seaWidth/(maxBoats/2)));
			y = seaHeight/2;
			return new Point(x, y);
		}

	}

	private void checkBoatActionTypeAndPermissions(Graphics g, Ship ship) {
		ActionType at = ActionType.getName(ship.getStatus().getAction());
		PermissionType pt = PermissionType.getName(ship.getStatus().getPermission());
		System.out.println(pt.toString());
		if (at.equals(ActionType.IDLE)) {
			g.setColor(new Color(244, 160, 65));
		}
		if(pt.equals(PermissionType.DENY)) {
			g.setColor(new Color(244, 77, 65));
		}
	}

	private void paintParkings(Graphics g) {
		parkingWidth = panelDimension.width / port.getDock().getMoorings().size();
		g.setColor(Color.black);
		for(int i = -Math.round(port.getDock().getMoorings().size()/2); i < Math.round(port.getDock().getMoorings().size()/2); i++) {
			if(port.getDock().getMoorings().get(i+Math.round(port.getDock().getMoorings().size()/2)).getShip() != null){
				g.setColor(new Color(244, 77, 65));
			}else {
				g.setColor(new Color(77, 244, 65));
			}
 			g.drawRect((panelLocation.x+(panelDimension.width/2)-PARKING_WIDTH/2)+(i*PARKING_WIDTH), panelDimension.height-PARKING_HEIGHT, PARKING_WIDTH, PARKING_HEIGHT);
			g.drawString(String.valueOf(Integer.parseInt(port.getDock().getMoorings().get(i+Math.round(port.getDock().getMoorings().size()/2)).getId(), 2)), (panelLocation.x+(panelDimension.width/2))+(i*PARKING_WIDTH), panelDimension.height-PARKING_HEIGHT-2);
		}
		if(port.getDock().getMoorings().size() % 2 != 0) {
			if(port.getDock().getMoorings().get(port.getDock().getMoorings().size()-1).getShip() != null){
				g.setColor(new Color(244, 77, 65));
			}else {
				g.setColor(new Color(77, 244, 65));
			}
			g.drawRect((panelLocation.x+(panelDimension.width/2)-PARKING_WIDTH/2)+(Math.round(port.getDock().getMoorings().size()/2))*PARKING_WIDTH, panelDimension.height-PARKING_HEIGHT, PARKING_WIDTH, PARKING_HEIGHT);
			g.drawString(String.valueOf(Integer.parseInt(port.getDock().getMoorings().get(port.getDock().getMoorings().size()-1).getId(), 2)), (panelLocation.x+(panelDimension.width/2))+((Math.round(port.getDock().getMoorings().size()/2)))*PARKING_WIDTH, panelDimension.height-PARKING_HEIGHT-2);
		}

	}

	private void paintTransitZone(Graphics g) {
		int MAX_TRANSIT = Settings.getProperties().get(1);

		g.setColor(Color.gray);
		transitWidth = panelDimension.width / 2;
		int leftWall = 0, rightWall = 0;
		for(int i = 0; i < MAX_TRANSIT; i++) {
			leftWall = transitWidth - ((i+1)*50);
			rightWall = transitWidth - ((i+1)*50);
		}
		transitWidth = leftWall;
		g.fillRect(0, seaHeight, leftWall, transitHeight);
		g.fillRect(leftWall+MAX_TRANSIT*100, seaHeight, rightWall, transitHeight);

	}

	@Override
	public void componentResized(ComponentEvent e) {
		repaintAllElements();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		repaintAllElements();
	}

	@Override
	public void componentShown(ComponentEvent e) {
		repaintAllElements();
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		repaintAllElements();
	}

	public void repaintAllElements() {
		panelDimension = this.getSize();
		panelLocation = this.getLocation();
		displayDivision();
		this.repaint();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof Ship) {
			Ship ship = (Ship) arg;
			addShipToTheList(ship);
			repaintAllElements();
		}
	}

	public void addShipToTheList(Ship ship) {
		for(Ship s: shipList) {
			if(s.equals(ship)) {
				s.setStatus(ship.getStatus());
				return;
			}
		}
		shipList.add(ship);
	}
}
