package com.github.iappapp.panda.engine;



import com.github.iappapp.panda.utils.DrawChar;
import com.github.iappapp.panda.utils.DrawImg;
import com.github.iappapp.panda.utils.ReadFolder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Random;

public class AccountListImageCaptchaEngine {
	private static final int IMG_WIDTH = 4 * 28; //图片的宽
	private static final int IMG_HEIGHT = 45; //图片的高
	private static final int FONT_SIZE = 35;
	private int picCount = 2;// 验证码干扰图案数
	private static final char[] codeSequence = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',  'M', 'N', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z','a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k',  'm', 'n', 'p', 'q', 'r', 
			's','t', 'u', 'v', 'w', 'x', 'y', 'z' , '2', '3', '4', '5', '6', '7', '8', '9' };
	
	private static final char[] billCodeSequence = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',  'M', 'N', 'P', 'Q', 'R', 'S',
		'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9' };
	
	private Random random = new Random();
	private static int[][] colors = {{17, 123, 2}, {0, 114, 255}, {118, 14, 80}, {28, 46, 121}, {63, 60, 64}};
	
	private Font[] fg;   // 字体类型
	private List<File> bg;   //背景图

	private static AccountListImageCaptchaEngine instance ; 
	
	private AccountListImageCaptchaEngine(String imgPath){
		try {
			initImgList(imgPath);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static AccountListImageCaptchaEngine getInstance(String imgPath){
		if( instance == null )
			instance = new AccountListImageCaptchaEngine(imgPath);
		return instance;
	}

	/**
	 * 获取验证码
	 * @param codeSize 验证码大小
	 * @param isNeedSmallCode 是否需要小写字母
	 * @return
	 */
	public String buildCode(int codeSize, boolean isNeedSmallCode){
		StringBuilder sb = new StringBuilder();
		if(!isNeedSmallCode){
			for(int i = 0;i < codeSize;i++){
				sb.append(billCodeSequence[random.nextInt(billCodeSequence.length)]);
			}
		}else{
			char c ;
			int j = 2;
			for(int i = 0;i < codeSize;i++){
				if(j <= 0){
					c = billCodeSequence[random.nextInt(billCodeSequence.length)];
				}else{
					c = codeSequence[random.nextInt(codeSequence.length)];
				}
				
				if(Character.isLowerCase(c)){
					j--;
				}
				sb.append(c);
			}
			
		}
		
		return sb.toString();
	}
	
	private void initImgList(String imgPath) throws UnsupportedEncodingException {
		imgPath = URLDecoder.decode(imgPath, "utf-8");
		bg = ReadFolder.readFlies(imgPath);
	}
	
	public static Color getRandomColor(boolean isRandom) {
		if(isRandom) {
			Random random = new Random();
			int red = 0, green = 0, blue = 0;
			red = random.nextInt(128);
			green = random.nextInt(128);
			blue = random.nextInt(128);
			return new Color(red, green, blue);
		} else {
			int index = new Random().nextInt(colors.length);
			return new Color(colors[index][0], colors[index][1], colors[index][2]);
		}

	}
	
	/**获取验证码图片
	 * 
	 * @param code  验证码
	 * @param type  类型 0：以下2种情况随机
	 * 
	 * 类型 1：全部空心，字体边缘有锯齿、加粗（3种字体随机，，扭曲度随机，粘连度随机）
	 * 类型 2：混合字体，实心和空心；（实习和空心数量随机，，扭曲度随机，粘连度随机）
	 * @return
	 */
	public BufferedImage getImageStrategy(int ImageWidth,int ImageHeight,String code,int type){
		 switch (type) {
	         case 1:
	        	 return getImageStrategy(ImageWidth,ImageHeight,code,Font.BOLD, 2, false);  
	         case 2:
	        	 return getImageStrategy(ImageWidth,ImageHeight,code,Font.BOLD, 2, true); 
	         default:
	        	 return getImageStrategy(ImageWidth,ImageHeight,code, random.nextInt(2)+1); 
 		 }
		
	}
	
	/**
	 * 获取验证码图片
	 * 
	 * @param code
	 *            验证码
	 * @param type
	 *            类型 0：以下2种情况随机
	 * 
	 *            类型 1：全部实心，字体边缘有锯齿、加粗（3种字体随机） 类型 2：混合字体，实心和空心；（实习和空心数量随机）
	 * @param disturbance
	 *            干扰图案
	 * @param tenacity
	 *            黏连度
	 * @param torsion
	 *            扭曲度
	 * @param createRandomLine
	 *            是否有干扰线
	 * @return
	 */
	public BufferedImage getAdjustmentImageStrategy(int ImageWidth, int ImageHeight, String code, int type,
			int disturbance, int tenacity, float torsion, boolean createRandomLine) {
		switch (type) {
		case 1:
			return getAdjustmentImageStrategy(ImageWidth, ImageHeight, code, Font.BOLD, 1, false, disturbance, tenacity,
					torsion, createRandomLine);
		case 2:
			return getAdjustmentImageStrategy(ImageWidth, ImageHeight, code, Font.BOLD, 2, true, disturbance, tenacity,
					torsion, createRandomLine);
		default:
			return getAdjustmentImageStrategy(ImageWidth, ImageHeight, code, random.nextInt(2) + 1, disturbance,
					tenacity, torsion, createRandomLine);
		}

	}

	/**
	 * 获取对应风险等级的图形验证码
	 * 
	 * @param ImageWidth
	 * @param ImageHeight
	 * @param code
	 * @param type
	 * @param degree
	 *            风险等级1-5
	 * @return
	 */
	public BufferedImage getDegreeImageStrategy(int ImageWidth, int ImageHeight, String code, int type, int degree) {
		switch (degree) {
		case 0:
			return getAdjustmentImageStrategy(ImageWidth, ImageHeight, code, 1, 6, 4, 0F, false);
		case 1:
			return getImageStrategy(ImageWidth, ImageHeight, code, type);
		case 2:
			return getAdjustmentImageStrategy(ImageWidth, ImageHeight, code, type, -2, -2, 7F, true);
		case 3:
			return getAdjustmentImageStrategy(ImageWidth, ImageHeight, code, type, -3, -3, 9F, true);
		case 4:
			return getAdjustmentImageStrategy(ImageWidth, ImageHeight, code, type, -3, -3, 11F, true);
		case 5:
			return getAdjustmentImageStrategy(ImageWidth, ImageHeight, code, type, -4, -4, 13F, true);
		default:
			return getImageStrategy(ImageWidth, ImageHeight, code, type);
		}
	}

	/**
	 * 
	 * @param code
	 * @param basicStroke 1正常   2有锯齿
	 * @param font 字体（Font.BOLD Font.PLAIN）
	 * @param mix
	 * @return
	 * @throws IOException
	 * @throws FontFormatException
	 */
	private BufferedImage getImageStrategy(int ImageWidth,int ImageHeight,String code,int font,int basicStroke,boolean mix)  {
		BufferedImage buffImg = null;
		try{
			buffImg = new BufferedImage(ImageWidth, ImageHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = buffImg.createGraphics();// 生成验证码背景
	
			g.setColor(new Color(255, 255, 255));
			g.fillRect(0, 0, IMG_WIDTH, IMG_HEIGHT);// 验证码背景颜色
	
			Color color = getRandomColor(true);
			g.setColor(color);
			if(bg != null && bg.size()>0){
				for (int i = 0; i < picCount; i++) {
					new DrawImg(g, color, (80 * i) + random.nextInt(30), random.nextInt(20))
							.draw(bg.get(random.nextInt(bg.size())));// 绘制干扰图案
				}
			}
			int x = 0, y = random.nextInt(5);  //上下位置
			fg = new Font[] { new Font("宋体", font, FONT_SIZE), 
					new Font("宋体", font, FONT_SIZE), 
					new Font("sans-serif", font, FONT_SIZE),
					};
			//AffineTransform affine = new AffineTransform();
			for (int i = 0; i < code.length(); i++) {
				//affine.setToRotation(Math.PI / 4 * random.nextDouble() * (random.nextBoolean() ? 1 : -1), ((IMG_WIDTH-10) / code.length()) * i + FONT_SIZE/2, (IMG_HEIGHT-7)/2);
				//g.setTransform(affine);
				Shape shape = new DrawChar(g, code.substring(i, i+1), fg[random.nextInt(fg.length)], color, x, y,basicStroke).draw(mix);
				x += shape.getBounds2D().getCenterX() + 5 + random.nextInt(8);  //粘连度
				y = random.nextInt(8);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return buffImg;
	}

	/**
	 * 
	 * @param code
	 * @param basicStroke
	 *            1正常 2有锯齿
	 * @param font
	 *            字体（Font.BOLD Font.PLAIN）
	 * @param mix 是否混合字体，false 为全部实心，false 为实心不能连续出现
	 * @return
	 * @throws IOException
	 * @throws FontFormatException
	 */
	private BufferedImage getAdjustmentImageStrategy(int ImageWidth, int ImageHeight, String code, int font,
			int basicStroke, boolean mix, int disturbance, int tenacity, float torsion, boolean createRandomLine) {
		BufferedImage buffImg = null;
		try {
			buffImg = new BufferedImage(ImageWidth, ImageHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = buffImg.createGraphics();// 生成验证码背景

			g.setColor(new Color(255, 255, 255));
			g.fillRect(0, 0, IMG_WIDTH, IMG_HEIGHT);// 验证码背景颜色

			// CreateRandomPoint(ImageWidth, ImageHeight, 200, g, 200);
			Color color = getRandomColor(mix);
			g.setColor(color);

			if (bg != null && bg.size() > 0) {
				for (int i = 0; i < picCount; i++) {
					new DrawImg(g, color, (80 * i) + random.nextInt(30) + disturbance, random.nextInt(20) + disturbance)
							.draw(bg.get(random.nextInt(bg.size())));// 绘制干扰图案
				}
			}
			// 干扰线
			if (createRandomLine) {
				CreateRandomLine(ImageWidth, ImageHeight, 2, g, color);
			}
			int x = 0, y = random.nextInt(5); // 上下位置
			fg = new Font[] { new Font("Arial", font, FONT_SIZE), new Font("ArialBlack", font, FONT_SIZE),
					new Font("sans-serif", font, FONT_SIZE), };
			// AffineTransform affine = new AffineTransform();
			int colorCount = 0;
			int randomColor = 0;

			for (int i = 0; i < code.length(); i++) {
				// affine.setToRotation(Math.PI / 4 * random.nextDouble() *
				// (random.nextBoolean() ? 1 : -1), ((IMG_WIDTH-10) /
				// code.length()) * i + FONT_SIZE/2, (IMG_HEIGHT-7)/2);
				// g.setTransform(affine);

				// 判断是否需要实体与空心同时出现
				if(mix) {
                    // 控制实体字母不能相连出现， randomColor=0 时是实体
					randomColor = new Random().nextInt(2);
					if (randomColor == 0 && colorCount != 1) {
						colorCount = 1;
					} else {
						colorCount = 0;
						randomColor = 1;
					}
				}
				Shape shape = new DrawChar(g, code.substring(i, i + 1), fg[random.nextInt(fg.length)], color, x, y,
						basicStroke).draw(randomColor, -tenacity + 10, torsion);
				// 粘连度
				x += shape.getBounds2D().getCenterX() + 7 + tenacity;
				y = random.nextInt(8);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffImg;
	}

	/**
	 * 随机产生干扰线条
	 * 
	 * @param width
	 * @param height
	 * @param minMany
	 *            最少产生的数量
	 * @param g
	 * @param
	 *            透明度0~255 0表示全透
	 */
	private void CreateRandomLine(int width, int height, int minMany, Graphics g, Color color) { // 随机产生干扰线条
		for (int i = 0; i < getIntRandom(minMany, minMany + 2); i++) {
			int x1 = getIntRandom(0, (int) (width * 0.3));
			int y1 = getIntRandom((int) (height * 0.2), (int) (height * 0.8));
			int x2 = getIntRandom((int) (width * 0.8), width);
			int y2 = getIntRandom((int) (height * 0.1), (int) (height * 0.8));

			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(3f));
			g.drawLine(x1, y1, x2, y2);

		}
	}

	/***
	 * @return 随机返一个指定区间的数字
	 */
	private int getIntRandom(int start, int end) {
		if (end < start) {
			int t = end;
			end = start;
			start = t;
		}
		int i = start + (int) (Math.random() * (end - start));
		return i;
	}
	
	public static void main(String[] args) {
		AccountListImageCaptchaEngine engine = AccountListImageCaptchaEngine.getInstance("captchaImages");
		String code = engine.buildCode(4, true); // 随机验证码
		System.out.println(code);
		// String code = "cnas";
		BufferedImage bi1_1 = engine.getImageStrategy(114, 46, code, 2);
		BufferedImage bi2_1 = engine.getAdjustmentImageStrategy(114, 46, code, 2, 1, 8, 1F, false);
		// BufferedImage bi3_1 = engine.getAdjustmentImageStrategy(114, 46,
		// code, 1, 1, 4, 1F, false);
		BufferedImage bi3_1 = engine.getDegreeImageStrategy(114, 46, code, 2, 0);
		BufferedImage bi4_1 = engine.getAdjustmentImageStrategy(114, 46, code, 0, -1, 8, 1F, false);
		BufferedImage bi5_1 = engine.getAdjustmentImageStrategy(114, 46, code, 0, -1, 8, 5F, false);
		BufferedImage bi1_2 = engine.getImageStrategy(114, 46, code, 2);
		BufferedImage bi2_2 = engine.getAdjustmentImageStrategy(114, 46, code, 2, -2, -2, 7F, true);
		BufferedImage bi3_2 = engine.getAdjustmentImageStrategy(114, 46, code, 2, -3, -3, 9F, true);
		BufferedImage bi4_2 = engine.getAdjustmentImageStrategy(114, 46, code, 2, -3, -3, 11F, true);
		BufferedImage bi5_2 = engine.getAdjustmentImageStrategy(114, 46, code, 2, -4, -4, 13F, true);
		BufferedImage bi5_6 = engine.getDegreeImageStrategy(114, 46, code, 2, 4);
		try {
			ImageIO.write(bi1_1, "jpg", new File("captchaImage1-1.jpg"));
			ImageIO.write(bi2_1, "jpg", new File("captchaImage2-1.jpg"));
			ImageIO.write(bi3_1, "jpg", new File("captchaImage3-1.jpg"));
			ImageIO.write(bi4_1, "jpg", new File("captchaImage4-1.jpg"));
			ImageIO.write(bi5_1, "jpg", new File("captchaImage5-1.jpg"));
			ImageIO.write(bi1_2, "jpg", new File("captchaImage1-2.jpg"));
			ImageIO.write(bi2_2, "jpg", new File("captchaImage2-2.jpg"));
			ImageIO.write(bi3_2, "jpg", new File("captchaImage3-2.jpg"));
			ImageIO.write(bi4_2, "jpg", new File("captchaImage4-2.jpg"));
			ImageIO.write(bi5_2, "jpg", new File("captchaImage5-2.jpg"));
			ImageIO.write(bi5_6, "jpg", new File("captchaImage5-6.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}