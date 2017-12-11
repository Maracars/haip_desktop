package ui.panels;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private BufferedImage image, scaledImage;

	public ImagePanel(String imagePath) throws IOException {
		this.image = scaledImage = null;
		image = ImageIO.read(new File(imagePath));
	}

	public void scaleImage(int newWidth, int newHeight) {
		this.setPreferredSize(new Dimension(newWidth, newHeight));
		this.scaledImage = Scalr.resize(this.image, newWidth, newHeight);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage((scaledImage == null) ? image : scaledImage,
				(this.getWidth() / 2) - ((scaledImage == null) ? this.image.getWidth() / 2 : this.scaledImage.getWidth() / 2),
				this.getHeight() / 2 - ((scaledImage == null) ? this.image.getHeight() / 2 : this.scaledImage.getHeight() / 2),
				this);
	}
}