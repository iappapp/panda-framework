package com.github.iappapp.panda.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class DrawImg {

	private Graphics2D g;
	private Color color;
	private int x, y;
	private String picPath;
	private Random random = new Random();

	public DrawImg(Graphics2D g, Color color, int x, int y, String picPath) {
		super();
		this.g = g;
		this.color = color;
		this.x = x;
		this.y = y;
		this.picPath = picPath;
	}

	public DrawImg(Graphics2D g, Color color, int x, int y) {
		super();
		this.g = g;
		this.color = color;
		this.x = x;
		this.y = y;
	}

	public void draw(String path) throws IOException {
		File file = new File(picPath);
		draw(file);

	}

	public void draw(File file) throws IOException {

		BufferedImage image = ImageIO.read(file);
		BufferedImage buffImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = buffImg.createGraphics();
		buffImg = g2.getDeviceConfiguration().createCompatibleImage(32, 32, Transparency.TRANSLUCENT);
		g2.dispose();
		g2 = buffImg.createGraphics();
		g2.setColor(color);

		int[] rgb = null;

		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				rgb = getRGB(image, i, j);
				if (rgb[0] < 250 && rgb[1] < 250 && rgb[2] < 250)
					// buffImg.setRGB(i, j, 0xffff0000);
					g2.drawLine(i, j, i, j);

			}
		}

		buffImg = rotateImg(buffImg, random.nextInt(360));
		g.drawImage(buffImg, null, x, y);

	}

	public static int[] getRGB(BufferedImage image, int x, int y) {
		int[] rgb = new int[3];
		int pixel = image.getRGB(x, y);
		rgb[0] = (pixel & 0xff0000) >> 16;
		rgb[1] = (pixel & 0xff00) >> 8;
		rgb[2] = (pixel & 0xff);

		return rgb;
	}

	public static BufferedImage rotateImg(BufferedImage bufferedimage, int degree) {
		int w = bufferedimage.getWidth();
		int h = bufferedimage.getHeight();
		int type = bufferedimage.getColorModel().getTransparency();
		BufferedImage img;
		Graphics2D graphics2d;
		(graphics2d = (img = new BufferedImage(w, h, type)).createGraphics())
				.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
		graphics2d.drawImage(bufferedimage, 0, 0, null);
		graphics2d.dispose();
		return img;
	}

}
