package stage.gameobj;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import core.DrawManager;
import core.geom.Vector2;
import stage.Camera;
import stage.FloorLevel;
import stage.ObjectMap;
import util.Constants;
import util.Resource;
import util.Constants.ColorSwatch;

public class Block implements PushableObject, WalkThroughable {

	protected static final float BLOCK_HEIGHT = 1.0f;
	private int x, y, z, nextX, nextY, nextZ, weight;
	private boolean isWalkThroughable;
	private FloorLevel floorLevelMap;
	// private boolean isObjectAbove;

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	protected int getWeight() {
		return weight;
	}

	public Block(int x, int y, int z, FloorLevel floorLevelMap) {
		this(x, y, z, 100, false, floorLevelMap);
	}

	public Block(int x, int y, int z, int weight, boolean isWalkThroughable, FloorLevel floorLevelMap) {
		this.floorLevelMap = floorLevelMap;
		this.x = x;
		this.y = y;
		this.z = z;
		this.nextX = x;
		this.nextY = y;
		this.nextZ = z;
		this.weight = weight;
		this.isWalkThroughable = isWalkThroughable;
	}

	public boolean isPushable() {

		if (weight >= 100 || ObjectMap.drawableObjectHashMap.get(x + " " + y + " " + (z + 1)) != null)
			return false;
		if (ObjectMap.drawableObjectHashMap
				.get(x + " " + y + " " + (z + 1) + " Player" + util.Constants.PLAYER1_ID) != null
				|| ObjectMap.drawableObjectHashMap
						.get(x + " " + y + " " + (z + 1) + " Player" + util.Constants.PLAYER2_ID) != null)
			return false;
		return true;
	}

	public boolean push(int previousWeight, int diffX, int diffY, int diffZ) {

		if (floorLevelMap.isOutOfMap(x + diffX, y + diffY))
			return false;
		if (this.weight + previousWeight > 100 || !isPushable())
			return false;
		if (ObjectMap.drawableObjectHashMap.get(
				(x + diffX) + " " + (y + diffY) + " " + (z + diffZ) + " Player" + util.Constants.PLAYER1_ID) != null
				|| ObjectMap.drawableObjectHashMap.get((x + diffX) + " " + (y + diffY) + " " + (z + diffZ) + " Player"
						+ util.Constants.PLAYER2_ID) != null)
			return false;

		IDrawable nextObjectObstacles = ObjectMap.drawableObjectHashMap
				.get((x + diffX) + " " + (y + diffY) + " " + (z + diffZ));
		if (z != floorLevelMap.getZValueFromXY(x + diffX, y + diffY)) {
			IDrawable nextObjectBelow = ObjectMap.drawableObjectHashMap
					.get((x + diffX) + " " + (y + diffY) + " " + (z + diffZ - 1));

			if (!(nextObjectBelow instanceof Block))
				return false;
		}

		if (nextObjectObstacles == null) {
			ObjectMap.drawableObjectHashMap.put((x + diffX) + " " + (y + diffY) + " " + (z + diffZ), this);
			ObjectMap.drawableObjectHashMap.remove(x + " " + y + " " + z);
			this.x += diffX;
			this.y += diffY;
			this.z += diffZ;
			return true;

		} else {
			if (nextObjectObstacles instanceof PushableObject) {
				boolean isPushed = ((PushableObject) nextObjectObstacles).push(previousWeight + this.weight, diffX,
						diffY, diffZ);
				if (isPushed) {
					ObjectMap.drawableObjectHashMap.put((x + diffX) + " " + (y + diffY) + " " + (z + diffZ), this);
					ObjectMap.drawableObjectHashMap.remove(x + " " + y + " " + z);
					this.x += diffX;
					this.y += diffY;
					this.z += diffZ;
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean isWalkThroughable() {
		return isWalkThroughable;
	}

	private static BufferedImage cachedBoxImg;
	private static int cachedBoxImgSize = 150;
	private static final float[][] cornerShifter = new float[][] { { -0.5f, -0.5f }, { +0.5f, -0.5f }, { +0.5f, +0.5f },
			{ -0.5f, +0.5f } };

	private static void drawBlock(Graphics2D g, Camera camera, float x, float y, float z, boolean isRawDrawPosition) {

		Vector2[] basis = new Vector2[4];
		for (int i = 0; i < 4; i++) {
			basis[i] = camera.getDrawPosition(x + cornerShifter[i][0], y + cornerShifter[i][1], z, isRawDrawPosition);
		}

		int furthestBaseId = 0;
		Vector2 furthestBase = basis[0];

		for (int i = 1; i < 4; i++) {
			if (basis[i].getY() < furthestBase.getY()) {
				furthestBaseId = i;
				furthestBase = basis[i];
			}
		}

		Vector2[] outerBorder = new Vector2[6];
		Vector2 innerPoint;

		outerBorder[0] = basis[(furthestBaseId + 2) % 4];
		outerBorder[1] = basis[(furthestBaseId + 3) % 4];
		outerBorder[2] = camera.getDrawPosition(x + cornerShifter[(furthestBaseId + 3) % 4][0],
				y + cornerShifter[(furthestBaseId + 3) % 4][1], z + BLOCK_HEIGHT, isRawDrawPosition);
		outerBorder[3] = camera.getDrawPosition(x + cornerShifter[furthestBaseId][0],
				y + cornerShifter[furthestBaseId][1], z + BLOCK_HEIGHT, isRawDrawPosition);
		outerBorder[4] = camera.getDrawPosition(x + cornerShifter[(furthestBaseId + 1) % 4][0],
				y + cornerShifter[(furthestBaseId + 1) % 4][1], z + BLOCK_HEIGHT, isRawDrawPosition);
		outerBorder[5] = basis[(furthestBaseId + 1) % 4];

		innerPoint = camera.getDrawPosition(x + cornerShifter[(furthestBaseId + 2) % 4][0],
				y + cornerShifter[(furthestBaseId + 2) % 4][1], z + BLOCK_HEIGHT, isRawDrawPosition);

		int[] outerBorderCoordX = new int[6];
		int[] outerBorderCoordY = new int[6];
		for (int i = 0; i < 6; i++) {
			outerBorderCoordX[i] = (int) outerBorder[i].getX();
			outerBorderCoordY[i] = (int) outerBorder[i].getY();
		}

		g.setColor(ColorSwatch.BACKGROUND);
		g.fillPolygon(new Polygon(outerBorderCoordX, outerBorderCoordY, 6));

		g.setStroke(Resource.getGameObjectThickStroke());
		g.setColor(ColorSwatch.FOREGROUND);
		g.drawPolygon(new Polygon(outerBorderCoordX, outerBorderCoordY, 6));

		g.setStroke(Resource.getGameObjectThinStroke());
		g.drawLine(innerPoint.getIntX(), innerPoint.getIntY(), outerBorder[0].getIntX(), outerBorder[0].getIntY());
		g.drawLine(innerPoint.getIntX(), innerPoint.getIntY(), outerBorder[2].getIntX(), outerBorder[2].getIntY());
		g.drawLine(innerPoint.getIntX(), innerPoint.getIntY(), outerBorder[4].getIntX(), outerBorder[4].getIntY());
	}

	public static void refreshDrawCache(Camera camera) {
		if (Constants.CACHE_DRAWABLE) {
			cachedBoxImg = DrawManager.getInstance().createBlankBufferedImage(cachedBoxImgSize, cachedBoxImgSize,
					Transparency.BITMASK);
			Graphics2D g = cachedBoxImg.createGraphics();

			g.setComposite(AlphaComposite.Src);
			g.setColor(new Color(0, 0, 0, 0));
			g.fillRect(0, 0, cachedBoxImgSize, cachedBoxImgSize); // Clears the image.
			g.setComposite(AlphaComposite.SrcOver);

			g.setTransform(AffineTransform.getTranslateInstance(cachedBoxImgSize / 2, cachedBoxImgSize / 2));
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			drawBlock(g, camera, 0, 0, 0, true);

			g.dispose();
		}
	}

	public void draw(Graphics2D g, Camera camera) {

		if (Constants.CACHE_DRAWABLE) {
			Vector2 drawPosition = camera.getDrawPosition(x, y, z);

			g.drawImage(cachedBoxImg, drawPosition.getIntX() - cachedBoxImgSize / 2,
					drawPosition.getIntY() - cachedBoxImgSize / 2, null);
		} else {
			drawBlock(g, camera, x, y, z, false);
		}

	}

	@Override
	public float getDrawX() {
		return x;
	}

	@Override
	public float getDrawY() {
		return y;
	}

	@Override
	public float getDrawZ() {
		return z;
	}

}