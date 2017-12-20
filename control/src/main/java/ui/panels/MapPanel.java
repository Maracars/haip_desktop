package ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class MapPanel extends JPanel implements ComponentListener{
	private static final long serialVersionUID = 1L;
	
	private static int MAX_PARKING = 6;
	private static int MAX_TRANSIT = 1;
	private static final int TRANSITION_HEIGHT = 100;
	private static final int PARKING_HEIGHT = 80;
	private static final int PARKING_WIDTH = 80;
	
	Dimension panelDimension;
	Point panelLocation;
	
	int seaHeight, transitHeight, parkingHeight, seaWidth, transitWidth, parkingWidth;
	
	public MapPanel() {
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setLayout(new BorderLayout());
		this.addComponentListener(this);
	}
	
	public void displayDivision() {
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
		
	}

	private void paintParkings(Graphics g) {
		parkingWidth = panelDimension.width / MAX_PARKING;
		g.setColor(Color.black);
		for(int i = -Math.round(MAX_PARKING/2); i < Math.round(MAX_PARKING/2); i++) {
			g.drawRect((panelLocation.x+(panelDimension.width/2)-PARKING_WIDTH/2)+(i+1)*PARKING_WIDTH, panelDimension.height-PARKING_HEIGHT, PARKING_WIDTH, PARKING_HEIGHT);
			g.drawString(String.valueOf(i), (panelLocation.x+(panelDimension.width/2))+(i+1)*PARKING_WIDTH, panelDimension.height-PARKING_HEIGHT-2);
		}
		if(MAX_PARKING % 2 != 0)
			g.drawRect((panelLocation.x+(panelDimension.width/2)-PARKING_WIDTH/2)+(Math.round(MAX_PARKING/2)+1)*PARKING_WIDTH, panelDimension.height-PARKING_HEIGHT, PARKING_WIDTH, PARKING_HEIGHT);

		
	}

	private void paintTransitZone(Graphics g) {
		g.setColor(Color.gray);
		transitWidth = panelDimension.width / 2;
		int leftWall = 0, rightWall = 0;
		for(int i = 0; i < MAX_TRANSIT; i++) {
			leftWall = transitWidth - ((i+1)*50);
			rightWall = transitWidth - ((i+1)*50);
		}
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
}
