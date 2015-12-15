package stage;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import core.geom.Vector2;
import stage.renderer.LevelRenderer;
import scene.core.Scene;
import scene.level.LevelData;
import stage.gameobj.Block;

import stage.gameobj.FloorPiece;
import stage.gameobj.FloorSwitch;

import stage.gameobj.BlockController;

import stage.gameobj.Gate;
import stage.gameobj.GateController;
import stage.gameobj.GateToGateTeleport;
import stage.gameobj.IDrawable;
import stage.gameobj.ObjectVector;
import stage.gameobj.Player;
import stage.gameobj.Slope;
import stage.gameobj.SwitchController;
import stage.gameobj.TeleportGate;
import stage.gameobj.TeleportGateController;
import stage.gameobj.TeleportToArea;
import util.Constants.ColorSwatch;
import util.Helper;
import util.Resource;

public class GameStage {

	private List<Camera> cameraList = new ArrayList<>();
	private Player player1 = null, player2 = null;
	private FloorLevel floorLevelMap;
	private ArrayList<Block> blocks = new ArrayList<>();
	private ArrayList<FloorSwitch> floorSwitches = new ArrayList<>();
	private ArrayList<Slope> slopes = new ArrayList<>();

	private ArrayList<GateToGateTeleport> teleportGates = new ArrayList<>();
	// private ArrayList<GateToGateTeleport> teleportLinked = new ArrayList<>();
	private int[] telePortLink;
	private ArrayList<TeleportToArea> teleportToAreas = new ArrayList<>();

	private ArrayList<GateController> gateControllers = new ArrayList<>();
	// private int[] gateControllerGateIndex;
	private ArrayList<Integer> gateControllerGateIndex;
	private ArrayList<int[]> linkGate = new ArrayList<>();

	private ArrayList<BlockController> blockControllers = new ArrayList<>();
	private ArrayList<TeleportGateController> teleportGateControllers = new ArrayList<>();
	private ArrayList<Gate> gates = new ArrayList<>();

	// for testing
	private ArrayList<Block> datasetGsonBlock;
	private ArrayList<FloorSwitch> dataSetFloorSwitches;
	private ArrayList<Slope> dataSetSlopes;

	private ArrayList<GateToGateTeleport> dataSetTeleportToGates;
	// private ArrayList<GateToGateTeleport> dataSetTeleportLink;
	private int[] dataSetLinkTeleport;
	private ArrayList<TeleportToArea> dataSetTeleportToArea;

	private ArrayList<GateController> dataSetsGateController;
	private ArrayList<BlockController> dataSetBlockController;
	private ArrayList<TeleportGateController> datasetTeleportGateController;
	private ArrayList<Gate> dataSetsGate;

	private ArrayList<int[]> dataSetLinkGate;

	private GameStageType gameStageType;
	private LevelData levelData;

	public GameStage(LevelData levelData, GameStageType gameStageType) {
		this(levelData, gameStageType, true);
	}

	public GameStage(LevelData levelData, GameStageType gameStageType, boolean showLevelName) {
		this.gameStageType = gameStageType;
		this.levelData = levelData;

		ObjectMap.drawableObjectHashMap = new HashMap<ObjectVector, IDrawable>();

		floorLevelMap = new FloorLevel(25, 25);
		int floorMapXSize = floorLevelMap.getSizeX();
		int floorMapYSize = floorLevelMap.getSizeY();

		for (int i = 0; i < floorMapXSize; i++) {
			for (int j = 0; j < floorMapYSize; j++) {
				if (i > 11) {
					floorLevelMap.setZValue(i, j, 2);
				} else if (i > 8) {
					floorLevelMap.setZValue(i, j, 1);
				} else {
					floorLevelMap.setZValue(i, j, 0);
				}
			}
		}

		if (gameStageType == GameStageType.PLAY) {
			/*if (gameStageType == GameStageType.ONE_PLAYER || gameStageType == GameStageType.TWO_PLAYER) {
				player1 = new Player(util.Constants.PLAYER1_ID, floorLevelMap, 9, 4, 1);
				this.cameraList.add(new Camera(player1));
			
				if (gameStageType == GameStageType.TWO_PLAYER) {
					player2 = new Player(util.Constants.PLAYER2_ID, floorLevelMap, 0, 0, 0);
					this.cameraList.add(new Camera(player2));
				}
			}*/
		}

		player1 = new Player(util.Constants.PLAYER1_ID, floorLevelMap, 9, 4, 1);
		this.cameraList.add(new Camera(player1));
		player2 = new Player(util.Constants.PLAYER2_ID, floorLevelMap, 0, 0, 0);
		this.cameraList.add(new Camera(player2));

		slopes.add(new Slope(6, 10, 0, Slope.ALIGNMENT_RIGHT));
		slopes.add(new Slope(6, 5, 0, Slope.ALIGNMENT_RIGHT));
		slopes.add(new Slope(9, 10, 1, Slope.ALIGNMENT_RIGHT));
		slopes.add(new Slope(-2, 3, 0, Slope.ALIGNMENT_RIGHT));

		blocks.add(new Block(6, 11, 0, 20, true, floorLevelMap));
		blocks.add(new Block(7, 11, 0, 20, true, floorLevelMap));
		blocks.add(new Block(8, 11, 0, 20, true, floorLevelMap));
		blocks.add(new Block(6, 12, 0, 20, true, floorLevelMap));
		blocks.add(new Block(7, 12, 0, 20, true, floorLevelMap));
		blocks.add(new Block(8, 12, 0, 20, true, floorLevelMap));
		blocks.add(new Block(6, 13, 0, 20, true, floorLevelMap));
		blocks.add(new Block(7, 13, 0, 20, true, floorLevelMap));
		blocks.add(new Block(8, 13, 0, 20, true, floorLevelMap));

		slopes.add(new Slope(13, 4, 2, Slope.ALIGNMENT_RIGHT));
		slopes.add(new Slope(16, 1, 2, Slope.ALIGNMENT_DOWN));
		slopes.add(new Slope(16, 7, 2, Slope.ALIGNMENT_UP));
		slopes.add(new Slope(19, 4, 2, Slope.ALIGNMENT_LEFT));

		blocks.add(new Block(16, 4, 2, 20, true, floorLevelMap));
		blocks.add(new Block(2, 3, 0, 20, true, floorLevelMap));
		blocks.add(new Block(2, 4, 0, 20, true, floorLevelMap));
		blocks.add(new Block(2, 5, 0, 20, true, floorLevelMap));
		blocks.add(new Block(2, 7, 0, 20, true, floorLevelMap));
		blocks.add(new Block(2, 8, 0, 20, true, floorLevelMap));
		blocks.add(new Block(2, 9, 0, 20, true, floorLevelMap));
		blocks.add(new Block(2, 10, 0, 20, true, floorLevelMap));
		blocks.add(new Block(1, 4, 1, 150, true, floorLevelMap));
		blocks.add(new Block(2, 3, 1, 20, true, floorLevelMap));
		blocks.add(new Block(3, 3, 0, 20, true, floorLevelMap));
		blocks.add(new Block(4, 3, 0, 20, true, floorLevelMap));
		blocks.add(new Block(5, 3, 0, 20, true, floorLevelMap));
		blocks.add(new Block(6, 3, 0, 20, true, floorLevelMap));
		blocks.add(new Block(7, 3, 0, 20, true, floorLevelMap));
		blocks.add(new Block(8, 3, 0, 20, true, floorLevelMap));

		GateToGateTeleport gateTele0 = new GateToGateTeleport(0, 1, 0);
		GateToGateTeleport gateTele1 = new GateToGateTeleport(5, 0, 0);
		GateToGateTeleport gateTele2 = new GateToGateTeleport(16, 4, 3);

		teleportGates.add(gateTele0);
		teleportGates.add(gateTele1);
		teleportGates.add(gateTele2);
		teleportToAreas.add(new TeleportToArea(10, 4, 1, 0, 2, 0));

		telePortLink = new int[] { 2, 0, 0 };

		// teleportLinked.add(gateTele2);
		// teleportLinked.add(gateTele1);
		// teleportLinked.add(gateTele0);

		floorSwitches.add(new FloorSwitch(1, 0, 0, false, 20));
		floorSwitches.add(new FloorSwitch(4, 0, 0, false, 10));
		floorSwitches.add(new FloorSwitch(6, 0, 0, false, 10));

		Gate gate1 = new Gate(1, 3, 0);
		Gate gate2 = new Gate(1, 4, 0);
		Gate gate3 = new Gate(1, 5, 0);
		ArrayList<FloorSwitch> fsArray = new ArrayList<>();

		int[] fsArr = new int[floorSwitches.size()];
		fsArr[0] = 1;
		fsArr[1] = 1;
		linkGate.add(fsArr);
		linkGate.add(fsArr);
		GateController gateController = new GateController(null, new int[] { 0, 1, 0, 0 });
		GateController gateController2 = new GateController(null, new int[] { 0, 0, 0, 1 });
		fsArr = new int[floorSwitches.size()];
		fsArr[1] = 1;
		fsArr[2] = 1;
		linkGate.add(fsArr);

		// fsArray.add(floorSwitches.get(1));
		// fsArray.add(floorSwitches.get(2));
		GateController gateController3 = new GateController(fsArray, new int[] { 0, 0, 1, 0 });

		// TeleportGateController teleController = new TeleportGateController(floorSwitches, new int[] {0,1,0,1},
		// gateTele3);
		BlockController bController = new BlockController(floorSwitches, new int[] { 0, 1, 0, 1 }, blocks.get(8),
				BlockController.MOVE_DOWN_TYPE, 1);
		// teleportGateControllers.add(teleController);
		blockControllers.add(bController);

		gates.add(gate1);
		gates.add(gate2);
		gates.add(gate3);
		gateControllers.add(gateController);
		gateControllers.add(gateController2);
		gateControllers.add(gateController3);

		// gateControllerGateIndex.add(new Integer(0)); //gates.get(index 0)
		// gateControllerGateIndex.add(new Integer(1));

		HashMap<String, String> levelObject = new HashMap<String, String>();
		Gson gson = new Gson();

		String blockJson = gson.toJson(blocks);
		String floorSwitchJson = gson.toJson(floorSwitches);
		String slopeJson = gson.toJson(slopes);
		String switchSetSelected = gson.toJson(linkGate);

		String teleportGateJson = gson.toJson(teleportGates);
		// String teleportGateLinkJson = gson.toJson(teleportLinked);
		String teleportGateLinkJson = gson.toJson(telePortLink);
		String teleportGateToAreaJson = gson.toJson(teleportToAreas);

		// String swControllerJson = gson.toJson(switchController);
		String gateJson = gson.toJson(gates);
		String gateControllerJson = gson.toJson(gateControllers);
		// String gateControllerGateIndexJson = gson.toJson(gateControllerGateIndex);

		levelObject.put("block", blockJson);
		levelObject.put("teleport", teleportGateJson);
		String hashmapJson = gson.toJson(levelObject);
		Type hashType = new TypeToken<HashMap<String, String>>() {
		}.getType();

		HashMap<String, String> a = gson.fromJson(hashmapJson, hashType);
//		System.out.println(a.get("block"));

		Type blockType = new TypeToken<ArrayList<Block>>() {
		}.getType();
		Type floorSwitchType = new TypeToken<ArrayList<FloorSwitch>>() {
		}.getType();
		Type slopeType = new TypeToken<ArrayList<Slope>>() {
		}.getType();

		// Type teleportType = new TypeToken<ArrayList<TeleportGate>>(){}.getType();
		Type teleportToGateType = new TypeToken<ArrayList<GateToGateTeleport>>() {
		}.getType();
		Type teleportToAreaType = new TypeToken<ArrayList<TeleportToArea>>() {
		}.getType();

		// Type switchControllerType = new TypeToken<ArrayList<SwitchController>>(){}.getType();
		Type gateType = new TypeToken<ArrayList<Gate>>() {
		}.getType();
		Type gateControllerType = new TypeToken<ArrayList<GateController>>() {
		}.getType();
		Type gateLinkType = new TypeToken<ArrayList<int[]>>() {
		}.getType();

		datasetGsonBlock = gson.fromJson(blockJson, blockType);
		dataSetFloorSwitches = gson.fromJson(floorSwitchJson, floorSwitchType);
		dataSetSlopes = gson.fromJson(slopeJson, slopeType);

		dataSetTeleportToGates = gson.fromJson(teleportGateJson, teleportToGateType);
		dataSetLinkTeleport = gson.fromJson(teleportGateLinkJson, int[].class);
		dataSetTeleportToArea = gson.fromJson(teleportGateToAreaJson, teleportToAreaType);

		// dataSetswitchController = gson.fromJson(swControllerJson,switchControllerType);
		dataSetsGate = gson.fromJson(gateJson, gateType);
		dataSetsGateController = gson.fromJson(gateControllerJson, gateControllerType);
		dataSetLinkGate = gson.fromJson(switchSetSelected, gateLinkType);

		for (int i = 0; i < dataSetTeleportToGates.size(); i++) {
			GateToGateTeleport mainGate = dataSetTeleportToGates.get(i);
			mainGate.setDestinationTelelportGate(dataSetTeleportToGates.get(dataSetLinkTeleport[i]));
		}

		for (int i = 0; i < dataSetsGateController.size(); i++) {
			GateController g = dataSetsGateController.get(i);
			g.setControlGate(dataSetsGate.get(i));
			int[] n = dataSetLinkGate.get(i);
			ArrayList<FloorSwitch> fsArrayList = new ArrayList<>();
			for (int j = 0; j < n.length; j++) {
				if (n[j] == 1) {
					fsArrayList.add(dataSetFloorSwitches.get(j));
				}
			}
			g.setFloorSwitchesControllerSet(fsArrayList);
		}

		for (Slope eachSlope : dataSetSlopes) {

			int slopeStartX = eachSlope.getStartX();
			int slopeStartY = eachSlope.getStartY();
			int slopeStartZ = eachSlope.getStartZ();
			int slopeEndX = eachSlope.getEndX();
			int slopeEndY = eachSlope.getEndY();
			int xBar = (slopeStartX + slopeEndX) / 2;
			int yBar = (slopeStartY + slopeEndY) / 2;

			ObjectMap.drawableObjectHashMap.put(new ObjectVector(slopeStartX, slopeStartY, slopeStartZ), eachSlope);
			ObjectMap.drawableObjectHashMap.put(new ObjectVector(xBar, yBar, slopeStartZ), eachSlope);
			ObjectMap.drawableObjectHashMap.put(new ObjectVector(slopeEndX, slopeEndY, slopeStartZ), eachSlope);

		}

		for (GateToGateTeleport eachTeleport : dataSetTeleportToGates) {
			ObjectMap.drawableObjectHashMap
					.put(new ObjectVector(eachTeleport.getX(), eachTeleport.getY(), eachTeleport.getZ()), eachTeleport);
		}

		for (TeleportToArea eachTeleport : dataSetTeleportToArea) {
			ObjectMap.drawableObjectHashMap
					.put(new ObjectVector(eachTeleport.getX(), eachTeleport.getY(), eachTeleport.getZ()), eachTeleport);
		}

		for (Block eachBlock : datasetGsonBlock) {
			ObjectMap.drawableObjectHashMap.put(new ObjectVector(eachBlock.getX(), eachBlock.getY(), eachBlock.getZ()),
					eachBlock);
		}

		for (FloorSwitch eachSwitch : dataSetFloorSwitches) {
			ObjectMap.drawableObjectHashMap.put(eachSwitch.getObjectVectorWithName(), eachSwitch);
		}

		for (Gate eachGate : dataSetsGate) {
			ObjectMap.drawableObjectHashMap.put(new ObjectVector(eachGate.getX(), eachGate.getY(), eachGate.getZ()),
					eachGate);
		}

		ObjectMap.drawableObjectHashMap.put(
				new ObjectVector(player1.getCellX(), player1.getCellY(), player1.getCellZ(), player1.getName()),
				player1);
		ObjectMap.drawableObjectHashMap.put(
				new ObjectVector(player2.getCellX(), player2.getCellY(), player2.getCellZ(), player2.getName()),
				player2);
		// ObjectMap.drawableObjectHashMap.put(
		// player1.getCellX() + " " + player1.getCellY() + " " + player1.getCellZ() + " " + player1.getName(),
		// player1);

		for (FloorPiece floor : floorLevelMap.getFloorPieces()) {
			ObjectMap.drawableObjectHashMap.put(floor.getObjectVector(), floor);
		}

		if (!showLevelName) {
			levelNameShowTimer = levelNameShowDuration;
		}

	}

	public void update(int step) {

		for (Camera camera : cameraList) {
			camera.update(step);
		}
		if (player1 != null) {
			player1.update(step);
		}
		if (player2 != null) {
			player2.update(step);
		}

		for (FloorSwitch fs : dataSetFloorSwitches) {
			fs.update(step);
		}

		for (GateToGateTeleport teleGate : dataSetTeleportToGates) {
			teleGate.update(step);
		}
		for (TeleportToArea teleGate : dataSetTeleportToArea) {
			teleGate.update(step);
		}
		for (Block each : datasetGsonBlock) {
			each.update(step);
		}
		for (GateController eachController : dataSetsGateController) {
			eachController.update();
		}
		for (Gate each : dataSetsGate) {
			each.update(step);
		}

		levelNameShowTimer += step;
		if (levelNameShowTimer > levelNameShowDuration) {
			levelNameShowTimer = levelNameShowDuration;
		}

	}

	private int levelNameShowTimer = 0;
	private int levelNameShowDuration = 100 * 120;
	private static final int OVERLAY_LEVEL_NAME_TEXT_SIZE = 50;
	private static final int OVERLAY_CHAPTER_NAME_TEXT_SIZE = 30;
	private static final int OVERLAY_BOX_VERTICAL_MARGIN = 20;

	private void drawLevelName(Graphics2D g, int x, int y, int width, int height) {
		if (gameStageType == GameStageType.PLAY && levelNameShowTimer < levelNameShowDuration) {
			Rectangle oldClip = g.getClipBounds();

			Font chapterNameFont = Resource.getInstance().getDefaultFont(OVERLAY_CHAPTER_NAME_TEXT_SIZE);
			Font levelNameFont = Resource.getInstance().getDefaultFont(OVERLAY_LEVEL_NAME_TEXT_SIZE,
					Resource.FontWeight.BOLD);
			String chapterName = levelData.getChapter().getChapterName();
			String levelName = levelData.getMapName();

			final int boxWidth = 100 + g.getFontMetrics(levelNameFont).stringWidth(levelName);
			final int boxHeight = OVERLAY_BOX_VERTICAL_MARGIN * 2 + OVERLAY_LEVEL_NAME_TEXT_SIZE
					+ OVERLAY_CHAPTER_NAME_TEXT_SIZE;
			int boxLeft = x + (width - boxWidth) / 2;
			int boxTop = y + 100;

			int clipperHeight = boxHeight;
			if (levelNameShowTimer < 100 * 20) {
				clipperHeight = (int) Helper.sineInterpolate(0, clipperHeight, levelNameShowTimer / (100 * 20f), false,
						true);
			} else if (levelNameShowTimer > 100 * 100) {
				clipperHeight = (int) Helper.sineInterpolate(clipperHeight, 0,
						(levelNameShowTimer - 100 * 100) / (100 * 20f), true, false);
			}
			int clipperTop = boxTop + (boxHeight - clipperHeight) / 2;
			g.setClip(boxLeft, clipperTop, boxWidth, clipperHeight);

			g.setColor(ColorSwatch.SHADOW);
			g.fillRect(boxLeft, boxTop, boxWidth, boxHeight);

			boxTop = clipperTop;

			// Chapter Name

			Vector2 chapterNameCenterPos = Helper.getCenteredTextPosition(chapterName, chapterNameFont, g, boxLeft, 0,
					boxWidth, 0);

			g.setFont(chapterNameFont);
			g.setColor(ColorSwatch.FOREGROUND);
			g.drawString(chapterName, chapterNameCenterPos.getX(),
					boxTop + OVERLAY_BOX_VERTICAL_MARGIN + g.getFontMetrics().getAscent());

			// Level Name

			Vector2 levelNameCenterPos = Helper.getCenteredTextPosition(levelName, levelNameFont, g, boxLeft, 0,
					boxWidth, 0);

			g.setFont(levelNameFont);
			g.setColor(ColorSwatch.FOREGROUND);
			g.drawString(levelName, levelNameCenterPos.getX(), boxTop + OVERLAY_BOX_VERTICAL_MARGIN
					+ OVERLAY_CHAPTER_NAME_TEXT_SIZE + g.getFontMetrics().getAscent());

			g.setClip(oldClip);
		}
	}

	public void draw(Graphics2D g, int sceneWidth, int sceneHeight) {

		// Draw background
		g.setColor(ColorSwatch.BACKGROUND);
		g.fillRect(0, 0, sceneWidth, sceneHeight);

		int cameraCount = cameraList.size();

		for (int i = 0; i < cameraCount; i++) {
			Camera camera = cameraList.get(i);

			camera.setSceneSize(sceneWidth / cameraCount, sceneHeight);

			Block.refreshDrawCache(camera);
			FloorPiece.refreshDrawCache(camera);

			AffineTransform oldTransform = g.getTransform();
			Rectangle oldClip = g.getClipBounds();

			g.setClip(sceneWidth * i / cameraCount, 0, sceneWidth / cameraCount, sceneHeight);
			g.translate(sceneWidth * i / cameraCount, 0);

			LevelRenderer.draw(ObjectMap.drawableObjectHashMap.values(), g, camera);

			g.setTransform(oldTransform);
			g.setClip(oldClip);

			drawLevelName(g, sceneWidth * i / cameraCount, 0, sceneWidth / cameraCount, sceneHeight);

			if (i > 0) {
				g.setColor(Helper.getAlphaColorPercentage(ColorSwatch.BACKGROUND, 1));
				g.setStroke(new BasicStroke(9));
				g.drawLine(sceneWidth * i / cameraCount, 0, sceneWidth * i / cameraCount, sceneHeight);

				g.setColor(ColorSwatch.SHADOW);
				g.setStroke(new BasicStroke(5));
				g.drawLine(sceneWidth * i / cameraCount, 0, sceneWidth * i / cameraCount, sceneHeight);
			}
			
			camera.resetDeformationChanged();

		}

	}

}
