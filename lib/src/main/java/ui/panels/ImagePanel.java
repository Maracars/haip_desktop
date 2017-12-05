package ui.panels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private BufferedImage image;

	public ImagePanel(String imagePath) {
		try {                
			image = ImageIO.read(new File(imagePath));
		} catch (IOException ex) {
			// handle exception...
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image,
				(this.getWidth() / 2) - (this.image.getWidth() / 2),
				this.getHeight() / 2 - (this.image.getHeight() / 2), this);           
	}
}