package ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import models.Port;
import models.Ship;
import models.Status;
import protocol.ControllerLogic;
import protocol.ProtocolProperties;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;
import settings.Settings;

public class MapPanel extends JPanel implements ComponentListener, Observer {
	private static final long serialVersionUID = 1L;

	private static final int TRANSITION_WIDTH = 100;
	private static final int TRANSITION_HEIGHT = 100;
	private static final int PARKING_WIDTH = 80;
	private static final int PARKING_HEIGHT = 80;
	private static final int BOAT_WIDTH = 30;
	private static final int BOAT_HEIGHT = 30;

	private Dimension panelDimension;
	private Point panelLocation;
	private Port port;

	private List<Ship> shipList;

	private int seaHeight, transitHeight, parkingHeight, seaWidth, transitWidth, parkingWidth;

	public MapPanel(Port port, ControllerLogic controllerLogic) {
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setLayout(new BorderLayout());
		this.addComponentListener(this);

		this.shipList = new CopyOnWriteArrayList<>();
		this.port = port;

		controllerLogic.addObserver(this);
	}

	public void resetPort(Port port, ControllerLogic controllerLogic) {
		this.port = port;

		controllerLogic.addObserver(this);
		this.shipList.clear();
		this.repaintAllElements();
	}

	private void displayDivision() {
		int displayHeight = panelDimension.height;
		int displayWidth = panelDimension.width;

		seaHeight = displayHeight / 2 - TRANSITION_HEIGHT / 2;
		seaWidth = displayWidth;

		transitHeight = TRANSITION_HEIGHT;
		transitWidth = TRANSITION_WIDTH;

		parkingHeight = displayHeight / 2 - TRANSITION_HEIGHT / 2;
		parkingWidth = displayWidth;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(new Color(100, 155, 244));
		g.fillRect(0, 0, seaWidth, seaHeight);
		paintTransitZone(g);

		g.setColor(new Color(52, 120, 229));
		g.fillRect(0, seaHeight + transitHeight, parkingWidth, parkingHeight);
		paintMoorings(g);

		paintBoats(g);
	}

	private void paintBoats(Graphics g) {
		for (Ship ship : shipList) {
			if (ship.getStatus() != null) {
				try {
					checkBoatPositionAndDraw(g, ship);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void checkBoatPositionAndDraw(Graphics g, Ship ship) throws IOException {
		int x, y;
		StatusType statusType = StatusType.getName(ship.getStatus().getPosition());
		g.setColor(new Color(77, 244, 65));
		File file = checkBoatActionTypeAndPermissions(g, ship);
		BufferedImage shipIcon = ImageIO.read(file);
		switch (statusType) {
			case PARKING:
				int parkingIndex = checkBoatParking(ship);
				x = (panelLocation.x + (panelDimension.width / 2)) + (parkingIndex) * PARKING_WIDTH - 12;
				y = panelDimension.height - (PARKING_HEIGHT / 2);
				g.drawImage(shipIcon, x, y, BOAT_WIDTH, BOAT_HEIGHT, null);
				g.drawString(Integer.toHexString(Integer.parseInt(ship.getId(), 2)),
						x + BOAT_WIDTH / 2 - 4, y - BOAT_HEIGHT / 2 + 3);
				g.setColor(Color.BLACK);
				break;
			case TRANSIT:
				int index = port.getTransitZone().indexOf(ship);
				x = (int) (panelLocation.getX() + transitWidth
						+ (TRANSITION_WIDTH * index) + TRANSITION_WIDTH / 2 - BOAT_WIDTH / 2);
				y = seaHeight + transitHeight / 2 - BOAT_HEIGHT / 2;
				g.drawImage(shipIcon, x, y, BOAT_WIDTH, BOAT_HEIGHT, null);
				g.drawString(Integer.toHexString(Integer.parseInt(ship.getId(), 2)),
						x + BOAT_WIDTH / 2 - 4, y - BOAT_HEIGHT / 2 + 3);
				g.setColor(Color.BLACK);
				break;
			case SEA:
				Point pointSea = checkBoatLocation(ship);
				g.drawImage(shipIcon, (int) pointSea.getX(), (int) pointSea.getY(), BOAT_WIDTH, BOAT_HEIGHT, null);
				g.drawString(Integer.toHexString(Integer.parseInt(ship.getId(), 2)),
						(int) (pointSea.getX() + BOAT_WIDTH / 2 - 4),
						(int) (pointSea.getY() - BOAT_HEIGHT / 2 + 3));
				g.setColor(Color.BLACK);
				break;
		}
	}

	private int checkBoatParking(Ship ship) {
		for (int i = 0; i < port.getDock().getMoorings().size(); i++) {
			if (port.getDock().getMoorings().get(i).getShip() != null
					&& port.getDock().getMoorings().get(i).getShip().getId().equals(ship.getId())) {
				return i - Math.round(port.getDock().getMoorings().size() / 2);
			}
		}
		return -1;
	}

	private Point checkBoatLocation(Ship ship) {
		int x, y;
		int maxBoats = (int) Math.pow(ProtocolProperties.ORIGIN_ID, 2);
		int shipId = Integer.parseInt(ship.getId(), 2);
		if (shipId < maxBoats / 2) {
			x = (int) (panelLocation.getX() + shipId * (seaWidth / (maxBoats / 2)));
			y = seaHeight / 4;
			return new Point(x, y);

		} else {
			x = (int) (panelLocation.getX() + shipId * (seaWidth / (maxBoats / 2)));
			y = seaHeight / 2;
			return new Point(x, y);
		}
	}

	private File checkBoatActionTypeAndPermissions(Graphics g, Ship ship) {
		ActionType actionType = ActionType.getName(ship.getStatus().getAction());
		PermissionType pt = PermissionType.getName(ship.getStatus().getPermission());
		if (actionType.equals(ActionType.IDLE)) {
			return new File("control/src/main/resources/Ship_icon_orange.png");
		}
		if (pt.equals(PermissionType.DENY)) {
			return new File("control/src/main/resources/Ship_icon_red.png");
		}
		return new File("control/src/main/resources/Ship_icon_green.png");
	}

	private void paintMoorings(Graphics g) {
		int parkingSize = Settings.getProperties().get(0);
		parkingWidth = panelDimension.width / parkingSize;
		g.setColor(Color.black);
		for (int i = -Math.round(parkingSize / 2); i < Math.round(parkingSize / 2); i++) {
			if (port.getDock().getMoorings().get(i + Math.round(parkingSize / 2)).getShip() != null) {
				g.setColor(new Color(244, 77, 65));
			} else {
				g.setColor(new Color(77, 244, 65));
			}
			g.drawRect((panelLocation.x + (panelDimension.width / 2) - PARKING_WIDTH / 2) + (i * PARKING_WIDTH), panelDimension.height - PARKING_HEIGHT, PARKING_WIDTH, PARKING_HEIGHT);
			g.drawString(String.valueOf(Integer.parseInt(port.getDock().getMoorings().get(i + Math.round(parkingSize / 2)).getId(), 2)), (panelLocation.x + (panelDimension.width / 2)) + (i * PARKING_WIDTH), panelDimension.height - PARKING_HEIGHT - 2);
		}
		if (port.getDock().getMoorings().size() % 2 != 0) {
			if (port.getDock().getMoorings().get(parkingSize - 1).getShip() != null) {
				g.setColor(new Color(244, 77, 65));
			} else {
				g.setColor(new Color(77, 244, 65));
			}
			g.drawRect((panelLocation.x + (panelDimension.width / 2) - PARKING_WIDTH / 2) + (Math.round(port.getDock().getMoorings().size() / 2)) * PARKING_WIDTH, panelDimension.height - PARKING_HEIGHT, PARKING_WIDTH, PARKING_HEIGHT);
			g.drawString(String.valueOf(Integer.parseInt(port.getDock().getMoorings().get(parkingSize - 1).getId(), 2)), (panelLocation.x + (panelDimension.width / 2)) + ((Math.round(parkingSize / 2))) * PARKING_WIDTH, panelDimension.height - PARKING_HEIGHT - 2);
		}

	}

	private void paintTransitZone(Graphics g) {
		int MAX_TRANSIT = Settings.getProperties().get(1);

		g.setColor(Color.gray);
		transitWidth = panelDimension.width / 2;
		int leftWall = 0, rightWall = 0;
		for (int i = 0; i < MAX_TRANSIT; i++) {
			leftWall = transitWidth - ((i + 1) * 50);
			rightWall = transitWidth - ((i + 1) * 50);
		}
		transitWidth = leftWall;
		g.fillRect(0, seaHeight, leftWall, transitHeight);
		g.fillRect(leftWall + MAX_TRANSIT * 100, seaHeight, rightWall, transitHeight);

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
		for (Ship s : shipList) {
			if (s.equals(ship)) {
				s.setStatus(ship.getStatus());
				return;
			}
		}
		shipList.add(ship);
	}
}
