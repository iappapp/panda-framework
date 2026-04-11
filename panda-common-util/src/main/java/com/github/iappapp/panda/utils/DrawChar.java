package com.github.iappapp.panda.utils;

import com.jhlabs.image.RippleFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class DrawChar extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Graphics2D g;
	private String str;
	private Font f;
	private Color color;
	private int x;
	private int y;
	private int basicStroke = 3;

	public DrawChar(Graphics2D g, String str, Font f, Color color, int x, int y) {
		super();
		this.g = g;
		this.str = str;
		this.f = f;
		this.color = color;
		this.x = x;
		this.y = y;
	}

	public DrawChar(Graphics2D g, String str, Font f, Color color, int x, int y, int basicStroke) {
		super();
		this.g = g;
		this.str = str;
		this.f = f;
		this.color = color;
		this.x = x;
		this.y = y;
		this.basicStroke = basicStroke;
	}

	public Shape draw() throws IOException, FontFormatException {
		return draw(false);
	}

	public Shape draw(boolean mix) throws IOException, FontFormatException {

		BufferedImage buffImg = new BufferedImage(120, 50, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = buffImg.createGraphics();
		buffImg = g2.getDeviceConfiguration().createCompatibleImage(120, 50, Transparency.TRANSLUCENT);
		g2.dispose();
		g2 = buffImg.createGraphics();

		Color[] colors = { color, new Color(255, 245, 238) };

		if (mix) {
			int i = new Random().nextInt(2);
			g2.setColor(colors[i]);
		} else if (mix == false) {

			g2.setColor(new Color(255, 245, 238));
		}

		GlyphVector v = f.createGlyphVector(getFontMetrics(f).getFontRenderContext(), str);
		Shape shape = v.getOutline();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.translate(10, 30);
		g2.fill(shape);
		g2.setColor(color);
		g2.setStroke(new BasicStroke(basicStroke));
		g2.draw(shape);

		BufferedImage filteredBuffimg = new BufferedImage(buffImg.getWidth() * 2, buffImg.getHeight() * 2,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g3 = filteredBuffimg.createGraphics();
		filteredBuffimg = g3.getDeviceConfiguration().createCompatibleImage(buffImg.getWidth() * 2,
				buffImg.getHeight() * 2, Transparency.TRANSLUCENT);
		RippleFilter rippleFilter = new RippleFilter();
		Random random = new Random();

		float xa = 5 + random.nextInt(8) + random.nextFloat();
		rippleFilter.setXAmplitude(xa);

		float ya = 6 + random.nextInt(8) + random.nextFloat();
		rippleFilter.setYAmplitude(ya);

		rippleFilter.filter(buffImg, filteredBuffimg);
		g.drawImage(filteredBuffimg, null, x, y);

		return shape;

	}

	public Shape draw(int randomColor, int tenacity, float torsion) throws IOException, FontFormatException {

		BufferedImage buffImg = new BufferedImage(120, 50, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = buffImg.createGraphics();
		buffImg = g2.getDeviceConfiguration().createCompatibleImage(120, 50, Transparency.TRANSLUCENT);
		g2.dispose();
		g2 = buffImg.createGraphics();


		Color[] colors = { color, new Color(255, 245, 238) };
		// if (mix) {
		// int i = new Random().nextInt(2);
		g2.setColor(colors[randomColor]);
		// } else if (mix == false) {
		//
		// g2.setColor(new Color(255, 245, 238));
		// }

		GlyphVector v = f.createGlyphVector(getFontMetrics(f).getFontRenderContext(), str);
		Shape shape = v.getOutline();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Random random = new Random();
		//
		// int degree = 0;
		//
		// degree = random.nextInt(25) - 25;

		g2.translate(10 + tenacity, 30);
		g2.fill(shape);
		g2.setColor(color);
		g2.setStroke(new BasicStroke(basicStroke));
		// RotateString(str, 60, 25, g2, degree);
		g2.draw(shape);

		BufferedImage filteredBuffimg = new BufferedImage(buffImg.getWidth() * 2, buffImg.getHeight() * 2,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g3 = filteredBuffimg.createGraphics();

		filteredBuffimg = g3.getDeviceConfiguration().createCompatibleImage(buffImg.getWidth() * 2,
				buffImg.getHeight() * 2, Transparency.TRANSLUCENT);
		RippleFilter rippleFilter = new RippleFilter();

		// float xa = 5 + random.nextInt(8) + random.nextFloat() + torsion ;
		// rippleFilter.setXAmplitude(xa);
		float xa = 5 + random.nextInt(1) + random.nextFloat() + torsion;
		rippleFilter.setXAmplitude(xa);
		// float ya = 6 + random.nextInt(8) + random.nextFloat() + torsion;
		// rippleFilter.setYAmplitude(ya);
		float ya = 6 + random.nextInt(1) + random.nextFloat() + torsion - 11;
		rippleFilter.setYAmplitude(ya);
		rippleFilter.filter(buffImg, filteredBuffimg);
		g.drawImage(filteredBuffimg, null, x, y);

		return shape;

	}

}
