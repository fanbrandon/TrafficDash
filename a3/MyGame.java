package a3;

import tage.*;
import tage.Light.LightType;
import tage.shapes.*;
import tage.input.*;
import tage.input.action.*;
import net.java.games.input.*;
import net.java.games.input.Component.Identifier.*;
import tage.input.InputManager;
import tage.nodeControllers.*;
import tage.networking.IGameConnection.ProtocolType;
import tage.physics.PhysicsEngine;
import tage.physics.PhysicsObject;
import tage.physics.JBullet.*;
import tage.audio.*;
import tage.ai.behaviortrees.*;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.collision.dispatch.CollisionObject;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import org.joml.*;
import com.jogamp.opengl.awt.GLCanvas;

import a3.client.*;
import a3.npc.*;
import a3.AvatarColorSelector;

public class MyGame extends VariableFrameRateGame
{
	private static Engine engine;
	private GhostManager gm;
	private IAudioManager audioMgr;
	private Sound walkSound, vehicleSound, truckSound, car2Sound, truck2Sound;

	private int counter=0;
	private int score = 0;
	private int clearSky;
	private double lastFrameTime, currFrameTime, elapsTime, deltaTime;
	private double prevTime, startTime, elapsedTime, amt;
	private boolean running = true;
	private double gameTimer = 90;
	private boolean gameLoss = false;
	private boolean gameWin = false;
	private boolean pause = false;
	private boolean isMoving = false;
	private boolean isWaving = false;
	private boolean hasItem = false;
	private PhysicsEngine physicsEngine;
	private PhysicsObject av1, av2, car1, truck1, groundP, car2P, truck2P;
	private float vals[] = new float[16];
	private boolean hasCollided = false;
	private ArrayList<PhysicsObject> vehicles = new ArrayList<>();
	private BehaviorTree carBT, truckBT, car2BT, truck2BT;
	private long lastThinkTime;
	private Vector3f carStartPosition, car2StartPosition;
	private Vector3f truckStartPosition, truck2StartPosition;
	private MoveForward moveForwardCar, moveForwardTruck, moveForwardCar2, moveForwardTruck2;
	private double walkSoundTimer = 0;
	private double walkSoundDuration = 1.0; // adjust based on your Door.wav length (seconds)
	private String playerTexture;
	private String ghostTexture;
	
	// Mouse Controls
	private Robot robot;
	private float curMouseX, curMouseY, centerX, centerY;
	private float prevMouseX, prevMouseY;
	private boolean isRecentering;
	private Canvas canvas;
	
	private boolean isJumping = false;      // To track if the object is jumping
	private float jumpSpeed = 10.0f;        // The initial jump speed
	private float gravity = -11.0f;          // Gravity strength
	private float verticalSpeed = 0.0f;     // Vertical speed (positive for up, negative for down)
	private float groundLevel;       // The ground level (or starting position for y-coordinate)

	private InputManager im;
	private GameObject avatar, dol, x, y, z, ground, terr, truck, truck2, car, car2, terr2, terr3, item, itemPart, start, end;
	private ObjShape dolS, linxS, linyS, linzS, groundS, ghostS1, terrS,  truckS, carS, itemS, startS, endS;
	private AnimatedShape avatarS, ghostS;
	private TextureImage doltx, groundtx, ghostT, hills, grass, trucktx, cartx, itemTex, starttx, endTx;
	private Light light1, light2, light3, light4;
	private CameraOrbit3D orbitController;
	private Viewport mainVp;
	private DriveController dc, dc2;
	
	GhostAvatar ghostAvatar;
	
	private Camera cam;

	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;
	private boolean isClientConnected = false;

	public MyGame(String serverAddress, int serverPort, String protocol, String playerTexture, String ghostTexture)
	{   
		super();
		gm = new GhostManager(this);
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.playerTexture = playerTexture;
		this.ghostTexture = ghostTexture;
		if (protocol.toUpperCase().compareTo("TCP") == 0)
			this.serverProtocol = ProtocolType.TCP;
		else
			this.serverProtocol = ProtocolType.UDP;
	}

	public static void main(String[] args)
	{   
		// Show color selection dialog first
		AvatarColorSelector colorSelector = new AvatarColorSelector();
		colorSelector.waitForSelection();
		
		// Create game with selected colors
		MyGame game = new MyGame(args[0], Integer.parseInt(args[1]), args[2], 
					colorSelector.getPlayerColorChoice(), 
					colorSelector.getGhostColorChoice());
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	// load all shapes for objects
	@Override
	public void loadShapes()
	{	dolS = new ImportedModel("gameAvatarTest.obj");
		ghostS = new AnimatedShape("gameAvatar.rkm", "gameAvatar.rks");
		avatarS = new AnimatedShape("gameAvatar.rkm", "gameAvatar.rks");
		avatarS.loadAnimation("Wave", "wave.rka");
		avatarS.loadAnimation("Idle","idle.rka");
		avatarS.loadAnimation("Walk","walk.rka");
		ghostS.loadAnimation("Wave", "wave.rka");
		ghostS.loadAnimation("Idle","idle.rka");
		ghostS.loadAnimation("Walk","walk.rka");
		linxS = new Line(new Vector3f(0f,0f,0f), new Vector3f(3f,0f,0f));
		linyS = new Line(new Vector3f(0f,0f,0f), new Vector3f(0f,3f,0f));
		linzS = new Line(new Vector3f(0f,0f,0f), new Vector3f(0f,0f,-3f));
		groundS = new Plane();
		terrS = new TerrainPlane(100);
		truckS = new ImportedModel("truck.obj");
		carS = new ImportedModel("car.obj");
		itemS = new Cube();
		startS = new Plane();
		endS = new Plane();
		
		
	}

	// Load all textures for objects
	@Override
	public void loadTextures()
	{	doltx = new TextureImage(playerTexture);
		groundtx = new TextureImage("road.jpg");
		ghostT = new TextureImage(ghostTexture);
		hills = new TextureImage("hills.jpg");
		grass = new TextureImage("grass.jpg");
		trucktx = new TextureImage("truckUV.png");
		cartx = new TextureImage("carUV.png");
		starttx = new TextureImage("checkerboard.jpg");
		endTx = new TextureImage("checkerboard.jpg");
	}
	 
	@Override
	public void loadSkyBoxes(){
		clearSky = (engine.getSceneGraph()).loadCubeMap("clearSky");
		(engine.getSceneGraph()).setActiveSkyBoxTexture(clearSky);
		(engine.getSceneGraph()).setSkyBoxEnabled(true);
	}

	@Override
	public void loadSounds(){
		 AudioResource resource1, resource2, resource3, resource4, resource5;
		audioMgr = engine.getAudioManager();
		
		// Load sound resources
		resource1 = audioMgr.createAudioResource("walk.wav", AudioResourceType.AUDIO_SAMPLE);
		resource2 = audioMgr.createAudioResource("vehicle_mono.wav", AudioResourceType.AUDIO_SAMPLE);
		resource3 = audioMgr.createAudioResource("vehicle_mono.wav", AudioResourceType.AUDIO_SAMPLE);
		resource4 = audioMgr.createAudioResource("vehicle_mono.wav", AudioResourceType.AUDIO_SAMPLE);
		resource5 = audioMgr.createAudioResource("vehicle_mono.wav", AudioResourceType.AUDIO_SAMPLE);
		
		// Create sound instances
		walkSound = new Sound(resource1, SoundType.SOUND_EFFECT, 100, false);
		vehicleSound = new Sound(resource2, SoundType.SOUND_EFFECT, 100, false); // Original car sound
		truckSound = new Sound(resource3, SoundType.SOUND_EFFECT, 100, false);
		car2Sound = new Sound(resource4, SoundType.SOUND_EFFECT, 100, false);
		truck2Sound = new Sound(resource5, SoundType.SOUND_EFFECT, 100, false);
		
		// Initialize all sounds
		walkSound.initialize(audioMgr);
		vehicleSound.initialize(audioMgr);
		truckSound.initialize(audioMgr);
		car2Sound.initialize(audioMgr);
		truck2Sound.initialize(audioMgr);
		
		// Configure walk sound
		walkSound.setMaxDistance(10.0f);
		walkSound.setMinDistance(0.5f);
		walkSound.setRollOff(2.0f);
		
		// Configure vehicle sounds with different parameters for variation
		vehicleSound.setMaxDistance(40.0f);
		vehicleSound.setMinDistance(0.5f);
		vehicleSound.setRollOff(2.0f);
		
		truckSound.setMaxDistance(50.0f);  // Trucks might be louder
		truckSound.setMinDistance(1.0f);
		truckSound.setRollOff(1.8f);
		truckSound.setPitch(0.9f);  // Slightly lower pitch for trucks
		
		car2Sound.setMaxDistance(35.0f);
		car2Sound.setMinDistance(0.5f);
		car2Sound.setRollOff(2.2f);
		
		truck2Sound.setMaxDistance(45.0f);
		truck2Sound.setMinDistance(1.0f);
		truck2Sound.setRollOff(1.8f);
		truck2Sound.setPitch(0.95f);
		}
	
	// Build all game objects
	@Override
	public void buildObjects()
	{	Matrix4f initialTranslation, initialScale, initialRotation;
		

		// Build dolphin at a fixed position near the origin
		dol = new GameObject(GameObject.root(), avatarS, doltx);
		initialTranslation = new Matrix4f().translation(0f, 1.5f, -110f);  
		initialScale = new Matrix4f().scaling(0.5f);  
		dol.setLocalTranslation(initialTranslation);  
		dol.setLocalScale(initialScale);  

		item = new GameObject(GameObject.root(),itemS);
		initialTranslation = new Matrix4f().translation(5.0f, 1.0f, -50.0f);
		initialScale = new Matrix4f().scaling(0.5f);
		item.setLocalTranslation(initialTranslation);
		item.setLocalScale(initialScale);

		// add truck
		truck = new GameObject(GameObject.root(), truckS,trucktx);
		initialTranslation = new Matrix4f().translation(10f,0.7f,100f);
		initialScale = new Matrix4f().scaling(1.0f);
		initialRotation = (new Matrix4f()).rotationY((float)java.lang.Math.toRadians(180.0f));
		truck.setLocalTranslation(initialTranslation);
		truck.setLocalScale(initialScale);
		truck.setLocalRotation(initialRotation);
		moveForwardTruck = new MoveForward(truck);

		truck2 = new GameObject(GameObject.root(), truckS,trucktx);
		initialTranslation = new Matrix4f().translation(-5f,0.7f,70f);
		initialScale = new Matrix4f().scaling(1.0f);
		initialRotation = (new Matrix4f()).rotationY((float)java.lang.Math.toRadians(180.0f));
		truck2.setLocalTranslation(initialTranslation);
		truck2.setLocalScale(initialScale);
		truck2.setLocalRotation(initialRotation);
		moveForwardTruck2 = new MoveForward(truck2);

		//add car
		car = new GameObject(GameObject.root(), carS, cartx);
		initialTranslation = new Matrix4f().translation(5f,0.3f,80f);
		initialScale = new Matrix4f().scaling(0.8f);
		initialRotation = (new Matrix4f()).rotationY((float)java.lang.Math.toRadians(180.0f));
		car.setLocalTranslation(initialTranslation);
		car.setLocalScale(initialScale);
		car.setLocalRotation(initialRotation);
		moveForwardCar = new MoveForward(car);

		car2 = new GameObject(GameObject.root(), carS, cartx);
		initialTranslation = new Matrix4f().translation(-10f,0.3f,90f);
		initialScale = new Matrix4f().scaling(0.8f);
		initialRotation = (new Matrix4f()).rotationY((float)java.lang.Math.toRadians(180.0f));
		car2.setLocalTranslation(initialTranslation);
		car2.setLocalScale(initialScale);
		car2.setLocalRotation(initialRotation);
		moveForwardCar2 = new MoveForward(car2);

		// add X,Y,-Z axes
		x = new GameObject(GameObject.root(), linxS);
		y = new GameObject(GameObject.root(), linyS);
		z = new GameObject(GameObject.root(), linzS);
		(x.getRenderStates()).setColor(new Vector3f(3f,0f,0f));
		(y.getRenderStates()).setColor(new Vector3f(0f,3f,0f));
		(z.getRenderStates()).setColor(new Vector3f(0f,0f,3f));

		
		//Build plane object for ground
		ground = new GameObject(GameObject.root(),groundS,groundtx);
		initialScale = (new Matrix4f()).scaling(17.0f,0.0f,100.0f);
		ground.setLocalScale(initialScale);
		ground.getRenderStates().setTiling(1);

		// start line
		start = new GameObject(GameObject.root(), startS, starttx);
		initialScale = (new Matrix4f()).scaling(17.0f,0.0f,5.0f);
		initialTranslation = new Matrix4f().translation(0.0f,0.0f,-105.0f);
		start.setLocalScale(initialScale);
		start.setLocalTranslation(initialTranslation);

		// finish line
		end = new GameObject(GameObject.root(), endS, endTx);
		initialScale = (new Matrix4f()).scaling(17.0f,0.0f,5.0f);
		initialTranslation = new Matrix4f().translation(0.0f,0.0f,105.0f);
		end.setLocalScale(initialScale);
		end.setLocalTranslation(initialTranslation);
		 
		// build terrain object
		terr = new GameObject(GameObject.root(), terrS, grass);
		initialTranslation = (new Matrix4f()).translation(0f,-0.01f,0f);
		terr.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(30.0f, 8.0f, 30.0f);
		terr.setLocalScale(initialScale);
		terr.setHeightMap(hills);
		// set tiling for terrain texture
		terr.getRenderStates().setTiling(2);
		terr.getRenderStates().setTileFactor(10);

		// build terrain object
		terr2 = new GameObject(GameObject.root(), terrS, grass);
		initialTranslation = (new Matrix4f()).translation(0f,-0.01f,60.0f);
		terr2.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(30.0f, 8.0f, 40.0f);
		terr2.setLocalScale(initialScale);
		terr2.setHeightMap(hills);
		// set tiling for terrain texture
		terr2.getRenderStates().setTiling(2);
		terr2.getRenderStates().setTileFactor(10);

		// build terrain object
		terr3 = new GameObject(GameObject.root(), terrS, grass);
		initialTranslation = (new Matrix4f()).translation(0f,-0.01f,-60.0f);
		terr3.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(30.0f, 8.0f, 40.0f);
		terr3.setLocalScale(initialScale);
		terr3.setHeightMap(hills);
		// set tiling for terrain texture
		terr3.getRenderStates().setTiling(2);
		terr3.getRenderStates().setTileFactor(10);
		
	}


	@Override
	public void initializeLights() {    
		Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);

		light1 = new Light();  // Default light (optional: set as directional/point)
		light2 = new Light();  // Spotlight 1 (yellow)
		light3 = new Light();  // Spotlight 2 (e.g., blue)
		light4 = new Light();  // Spotlight 3 (e.g., red)

		// --- Light 1 (Default) ---
		light1.setLocation(new Vector3f(5.0f, 10.0f, -5.0f));
		
		// --- Light 2 (Yellow Spotlight) ---
		light2.setType(Light.LightType.SPOTLIGHT);
		light2.setLocation(new Vector3f(-4.0f, 4.0f, -50.0f));
		light2.setDirection(new Vector3f(0.0f, -1.0f, 0.0f)); 
		light2.setCutoffAngle(30.0f);
		light2.setAmbient(0.23f, 0.23f, 0.0f);
		light2.setDiffuse(1.0f, 0.9f, 0.5f);  // Yellow
		light2.setSpecular(1.0f, 0.9f, 0.5f);
		light2.setOffAxisExponent(0.5f);
		light2.setConstantAttenuation(0.5f);

		// --- Light 3 ( Spotlight) ---
		light3.setType(Light.LightType.SPOTLIGHT);
		light3.setLocation(new Vector3f(10.0f, 8.0f, 0.0f));  // Different position
		light3.setDirection(new Vector3f(-0.5f, -1.0f, 0.0f)); // Angled direction
		light3.setCutoffAngle(25.0f);  // Tighter cone
		light3.setAmbient(0.1f, 0.1f, 0.3f);
		light3.setDiffuse(1.0f, 0.9f, 0.5f);
		light3.setSpecular(0.5f, 0.5f, 1.0f);
		light3.setOffAxisExponent(0.5f);  // Sharper falloff
		light3.setConstantAttenuation(0.7f);

		// --- Light 4 ( Spotlight) ---
		light4.setType(Light.LightType.SPOTLIGHT);
		light4.setLocation(new Vector3f(-10.0f, 6.0f, 50.0f));  // Another position
		light4.setDirection(new Vector3f(0.5f, -1.0f, -0.5f));  // Diagonal direction
		light4.setCutoffAngle(45.0f);  // Wider cone
		light4.setAmbient(0.3f, 0.1f, 0.1f);
		light4.setDiffuse(1.0f, 0.9f, 0.5f);
		light4.setSpecular(1.0f, 0.3f, 0.3f);
		light4.setOffAxisExponent(0.3f);  // Softer falloff
		light4.setConstantAttenuation(0.4f);

		// Add all lights to the scene
		engine.getSceneGraph().addLight(light1);
		engine.getSceneGraph().addLight(light2);
		engine.getSceneGraph().addLight(light3);
		engine.getSceneGraph().addLight(light4);
	}
	/*
	 * Create Viewports
	 */
	@Override
	public void createViewports(){
		(engine.getRenderSystem()).addViewport("MAIN",0,0,1f,1f);
		Viewport mainVp = (engine.getRenderSystem()).getViewport("MAIN");
		Camera mainCamera = mainVp.getCamera();
		mainCamera.setLocation(new Vector3f(-2,0,2));
		mainCamera.setU(new Vector3f(1,0,0));
		mainCamera.setV(new Vector3f(0,1,0));
		mainCamera.setN(new Vector3f(0,0,-1));
	}

	

	@Override
	public void initializeGame()
	{	lastFrameTime = System.currentTimeMillis();
		currFrameTime = System.currentTimeMillis();
		elapsTime = 0.0;
		initMouseMode();
		(engine.getRenderSystem()).setWindowDimensions(1920,1080);
		setAvatar(dol); // sets the avatar objects as the dolphin object

		// ------------- positioning the camera -------------
		//(engine.getRenderSystem().getViewport("MAIN").getCamera()).setLocation(new Vector3f(0,0,-5));

		avatarS.stopAnimation();
		avatarS.playAnimation("Idle", 0.5f, AnimatedShape.EndType.LOOP, 0);


		walkSound.setLocation(avatar.getWorldLocation());
		vehicleSound.setLocation(car.getWorldLocation());
		truckSound.setLocation(truck.getWorldLocation());
		car2Sound.setLocation(car2.getWorldLocation());
		truck2Sound.setLocation(truck2.getWorldLocation());
		vehicleSound.play(100,true);
		truckSound.play(100,true);
		car2Sound.play(100,true);
		truck2Sound.play(100, true);
		setEarParameters();


		setUpBTs();
		
		

		// ------------------ create physics world ----------
		float[] gravity = {0f, -9.8f, 0f};
        physicsEngine = (engine.getSceneGraph()).getPhysicsEngine();
        physicsEngine.setGravity(gravity);

		float mass = 1.0f;
        float up[] = {0, 1, 0};
        float radius = 0.75f;
        float height = 1.5f;
		float size[] = { 4f, 4f, 7f };
		float tSize[] = {5f, 9f, 11f};
        double[] tempTransform;

		Matrix4f translation = new Matrix4f(dol.getLocalTranslation());
        tempTransform = toDoubleArray(translation.get(vals));
        av1 = (engine.getSceneGraph()).addPhysicsCylinder(mass, tempTransform, radius, height);
        av1.setBounciness(0.5f);
		av1.setFriction(0.4f);
		
		
		
        dol.setPhysicsObject(av1);

		translation = new Matrix4f(car.getLocalTranslation());
        tempTransform = toDoubleArray(translation.get(vals));
        car1 = (engine.getSceneGraph()).addPhysicsBox(mass, tempTransform, size);
        car1.setBounciness(0.0f);
        car.setPhysicsObject(car1);
		vehicles.add(car1);

		translation = new Matrix4f(truck.getLocalTranslation());
        tempTransform = toDoubleArray(translation.get(vals));
        truck1 = (engine.getSceneGraph()).addPhysicsBox(mass, tempTransform, tSize);
        truck1.setBounciness(0.0f);
        truck.setPhysicsObject(truck1);
		vehicles.add(truck1);

		// For car2
		translation = new Matrix4f(car2.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		car2P = (engine.getSceneGraph()).addPhysicsBox(mass, tempTransform, size);
		car2P.setBounciness(0.0f);
		car2.setPhysicsObject(car2P);
		vehicles.add(car2P);

		// For truck2
		translation = new Matrix4f(truck2.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		truck2P = (engine.getSceneGraph()).addPhysicsBox(mass, tempTransform, tSize);
		truck2P.setBounciness(0.0f);
		truck2.setPhysicsObject(truck2P);
		vehicles.add(truck2P);
		 
		translation = new Matrix4f(ground.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		groundP = (engine.getSceneGraph().addPhysicsStaticPlane(tempTransform, up, 0.0f));
		groundP.setBounciness(0.0f);
		ground.setPhysicsObject(groundP);

		// Add this where you initialize other physics objects
		float endSize[] = {17.0f, 0.1f, 5.0f}; // Match the dimensions of your end plane
		translation = new Matrix4f(end.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		PhysicsObject endP = (engine.getSceneGraph()).addPhysicsStaticPlane(tempTransform, new float[]{0,1,0}, 0.0f);
		end.setPhysicsObject(endP);

		float itemMass = 0.5f; // Make it light but not static
		float itemSize[] = {0.5f, 0.5f, 0.5f}; // Match the cube size

		translation = new Matrix4f(item.getLocalTranslation());
		tempTransform = toDoubleArray(translation.get(vals));
		PhysicsObject itemP = (engine.getSceneGraph()).addPhysicsBox(itemMass, tempTransform, itemSize);
		itemP.setBounciness(0.3f); // Slightly bouncy
		itemP.setFriction(0.5f);
		item.setPhysicsObject(itemP);
				
		
		
		im = engine.getInputManager();

		cam = (engine.getRenderSystem().getViewport("MAIN").getCamera());
		mainVp = (engine.getRenderSystem().getViewport("MAIN"));
		orbitController = new CameraOrbit3D(cam, avatar, engine);

		FwdAction fwdAction = new FwdAction(this);
		LateralAction latAction = new LateralAction(this);
		YawAction yawAction = new YawAction(this);
		BackAction backAction = new BackAction(this);
		PitchAction pitchAction = new PitchAction(this);
		RollAction rollAction = new RollAction(this);
		GlobalYawAction gYawAction = new GlobalYawAction(this);
		ZoomAction zoomAction = new ZoomAction(this);
		PanAction panAction = new PanAction(this);
		JumpAction jumpAction = new JumpAction(this);
		

		
		// Gamepad Controls
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.X, latAction,InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.Y, fwdAction,InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.Z, gYawAction,InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._1, jumpAction,InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		// Keyboard Controls
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.W, fwdAction,InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.S, backAction,InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.A, latAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.D, latAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.LEFT, gYawAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.RIGHT, gYawAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.SPACE, jumpAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllMice(net.java.games.input.Component.Identifier.Axis.X,gYawAction,InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
				
		setupNetworking();
	}

	@Override
	public void update()
	{	
		if (!pause) {
			lastFrameTime = currFrameTime;
			currFrameTime = System.currentTimeMillis();
			deltaTime = (currFrameTime - lastFrameTime) / 1000.0;
			elapsTime += deltaTime;
			gameTimer = getGameTimer();
			if (!pause) gameTimer -= deltaTime;
			
			avatarS.updateAnimation();
			
			for (GhostAvatar ghost : gm.getGhostAvatars()) {
				ghost.updateAnimation();
			}

			im.update((float)elapsTime);
			
			orbitController.updateCameraPosition();
			physicsEngine.update((float)(deltaTime * 1000.0));
			gameVictory();
			timeRanOut();
			updatePlayerPosition((float)deltaTime);
			
			
			
			
			updatePhysicsObjectLocation(av1, avatar.getWorldTranslation());
			updatePhysicsObjectLocation(car1, car.getWorldTranslation());
			updatePhysicsObjectLocation(truck1,truck.getWorldTranslation());
			updatePhysicsObjectLocation(car2P, car2.getWorldTranslation());
			updatePhysicsObjectLocation(truck2P,truck2.getWorldTranslation());
			checkForCollisions();
			

			if (isMoving) {
			walkSoundTimer -= deltaTime;
			if (walkSoundTimer <= 0) {
					isMoving = false;
				}
			}
			
			vehicleSound.setLocation(car.getWorldLocation());
			truckSound.setLocation(truck.getWorldLocation());
			car2Sound.setLocation(car2.getWorldLocation());
			truck2Sound.setLocation(truck2.getWorldLocation());
			setEarParameters();

			moveForwardCar.update((float)(deltaTime * 1000)); // smooth frame-based motion
			moveForwardTruck.update((float)(deltaTime * 1000)); // smooth frame-based motion
			moveForwardCar2.update((float)(deltaTime * 1000)); // smooth frame-based motion
			moveForwardTruck2.update((float)(deltaTime * 1000)); // smooth frame-based motion
			long now = System.nanoTime();
			float elapsedThinkMS = (now - lastThinkTime) / 1_000_000.0f;
			if (elapsedThinkMS >= 250.0f) {
				carBT.update(elapsedThinkMS);
				truckBT.update(elapsedThinkMS);
				car2BT.update(elapsedThinkMS);
				truck2BT.update(elapsedThinkMS);
				lastThinkTime = now;
			}

			float vpWidth = getMain().getActualWidth();
			float vpHeight = getMain().getActualHeight();
			
			// build and set HUD
			int elapsTimeSec = Math.round((float)elapsTime);
			int gameTimerSec = Math.round((float)gameTimer);
			String elapsTimeStr = Integer.toString(elapsTimeSec);
			String counterStr = Integer.toString(counter);
			String scoreStr = Integer.toString(score);
			String timerStr = Integer.toString(gameTimerSec);
			String dispStr2 = "Timer = " + gameTimerSec;
			Vector3f hud1Color = new Vector3f(1,0,0);
			Vector3f hud2Color = new Vector3f(1,0,0);
			Vector3f hud3Color = new Vector3f(0,1,0);
			Vector3f hud4Color = new Vector3f(1,1,0);
			//(engine.getHUDmanager()).setHUD1(dispStr1, hud1Color, (int)(vpWidth * 0.01), (int)(vpHeight * 0.02));  // Bottom left
			(engine.getHUDmanager()).setHUD2(dispStr2, hud2Color, (int)(vpWidth * 0.01), (int)(vpHeight * 0.02));  // Bottom left
			processNetworking((float)deltaTime);
		}
	}


	

	
	/* Display game win message if win condition is met */
	public void gameVictory() {
		// Check distance to end object as a backup (in case physics collision fails)
		Vector3f dolPos = dol.getWorldLocation();
		Vector3f endPos = end.getWorldLocation();
		float distance = dolPos.distance(endPos);
		
		// If either collision detected or very close to end, and has item
		if ((gameWin || distance < 5.0f) && hasItem) {
			gameWin = true;
			pause = true;
			String vicString = "You reached the finish with the item! You Win! Press ESC to exit.";
			Vector3f vicColor = new Vector3f(0,1,0);
			float vpWidth = getMain().getActualWidth();
			float vpHeight = getMain().getActualHeight();
			(engine.getHUDmanager()).setHUD5(vicString, vicColor,
				(int)(vpWidth * 0.5) - 200, (int)(vpHeight * 0.5) - 20);
		}
	}

	

	/*
	 * Display game message if the game timer runs out. Player will lose game.
	 */
	public void timeRanOut(){
		if (gameTimer < 0){
			pause = true;
			String str = "Time Ran Out. Press Esc to exit the game.";
			Vector3f color = new Vector3f(1,0,0);
			float vpWidth = getMain().getActualWidth();
			float vpHeight = getMain().getActualHeight();
			(engine.getHUDmanager()).setHUD15(str,color,(int)(vpWidth * 0.5) - 200, (int)(vpHeight * 0.5) -20);
		}
	}


	
	

	/* Getter for avatar */
	public GameObject getAvatar() { return avatar;}

	public PhysicsObject getPhysicsAvatar(){
		return av1;
	}

	/* Setter for avatar */
	public void setAvatar(GameObject avatar){
		this.avatar = avatar;
	}
	
	public float getJumpSpeed(){
		return jumpSpeed;
	}

	public boolean getIsJumping(){
		return isJumping;
	}
	
	public float getVerticalSpeed(){
		return verticalSpeed;
	}

	public boolean setIsJumping(boolean isJumping){
		this.isJumping = isJumping;
		return this.isJumping;
	}

	public float setVerticalSpeed(float verticalSpeed){
		this.verticalSpeed = verticalSpeed;
		return this.verticalSpeed;
	}

	/* Getter for camera object */
	public Camera getCamera(){
		return cam;
	}


	/* Getter for if lose condition is met
	 * Used in if condition for movement. If condition
	 * is not met, player can move, if it is met the player
	 * will not be able to move
	 */
	public boolean gameLose(){
		return gameLoss;
	}

	/* Getter for win condition boolean
	 * Follows same logic as loss condition
	 */
	public boolean gameWin(){
		return gameWin;
	}

	/* Getter for dolphin game object */
	public GameObject getDolphin(){
		return dol;
	}

	/* Getter for delta time */
	public double getDeltaTime(){
		return deltaTime;
	}

	/* Getter for game timer */
	public double getGameTimer(){
		return gameTimer;
	}

	public Sound getWalkSound(){
		return walkSound;
	}

	public AnimatedShape getAvatarShape(){
		return avatarS;
	}

	public AnimatedShape setAvatarShape(AnimatedShape avatarS){
		this.avatarS = avatarS;
		return this.avatarS;
	}

	/* Getter for main viewport */
	public Viewport getMain(){
		return mainVp;
	}

	public Boolean getIsMoving(){
		return isMoving;
	}

	public Boolean setIsMoving(boolean isMoving){
		this.isMoving = isMoving;
		return this.isMoving;
	}

	public void resetWalkSoundTimer() {
    	walkSoundTimer = walkSoundDuration;
	}

	// Initializes mouse settings, including recentering and setting a custom cursor 
    private void initMouseMode() {
        RenderSystem rs = engine.getRenderSystem();
        Viewport vw = rs.getViewport("MAIN");

        float left = vw.getActualLeft();
        float bottom = vw.getActualBottom();
        float width = vw.getActualWidth();
        float height = vw.getActualHeight();

        centerX = (int) (left + width / 2);
        centerY = (int) (bottom - height / 2);
        isRecentering = false;

        try {
            robot = new Robot();
        } catch (AWTException ex) {
            throw new RuntimeException("Couldn't create Robot!");
        }

        recenterMouse();
        prevMouseX = centerX;
        prevMouseY = centerY;

        // Set custom cursor
        Image faceImage = new ImageIcon("./assets/textures/face.gif").getImage();
        Cursor faceCursor = Toolkit.getDefaultToolkit()
                .createCustomCursor(faceImage, new Point(0, 0), "FaceCursor");

        canvas = rs.getGLCanvas();
        canvas.setCursor(faceCursor);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // If the robot is recentering and the MouseEvent location is at the center,
        // this event was generated by the robot, so we ignore it.
        if (isRecentering &&
            centerX == e.getXOnScreen() && centerY == e.getYOnScreen()) {
            isRecentering = false;
        } else {
            // Process user mouse movement
            curMouseX = e.getXOnScreen();
            curMouseY = e.getYOnScreen();

            float mouseDeltaX = prevMouseX - curMouseX;
            float mouseDeltaY = prevMouseY - curMouseY;

            

            prevMouseX = curMouseX;
            prevMouseY = curMouseY;

            // Recenter mouse to prevent hitting screen edges
            recenterMouse();
            prevMouseX = centerX;
            prevMouseY = centerY;
        }
    }

    // Repositions the mouse to the center of the screen 
    private void recenterMouse() {
        RenderSystem rs = engine.getRenderSystem();
        Viewport vw = rs.getViewport("MAIN");

        float left = vw.getActualLeft();
        float bottom = vw.getActualBottom();
        float width = vw.getActualWidth();
        float height = vw.getActualHeight();

        int centerX = (int) (left + width / 2.0f);
        int centerY = (int) (bottom - height / 2.0f);

        isRecentering = true;
        robot.mouseMove(centerX, centerY);
    }
	

	

	public void updatePlayerPosition(float deltaTime) {
		Vector3f loc = dol.getWorldLocation();
		float terrainHeight = terr.getHeight(loc.x(), loc.z()); // Get terrain height at current position

		// Handle Jumping
		if (isJumping) {
			// Apply gravity
			verticalSpeed += gravity * deltaTime;

			// Calculate new Y position
			float newY = loc.y() + verticalSpeed * deltaTime;

			// Stop jump when reaching terrain height
			if (newY <= terrainHeight) {
				newY = terrainHeight;  // Align with terrain
				verticalSpeed = 0.0f;   // Reset velocity
				isJumping = false;      // End the jump
			}

			// Update position after jumping logic
			dol.setLocalLocation(new Vector3f(loc.x(), newY, loc.z()));
		} else {
			// Ensure the player follows terrain height while walking
			dol.setLocalLocation(new Vector3f(loc.x(), terrainHeight, loc.z()));
		}

		// Send position update to server
		protClient.sendMoveMessage(dol.getWorldLocation());
	}

	// ------------------ UTILITY FUNCTIONS used by physics
    private float[] toFloatArray(double[] arr) {
        if (arr == null) return null;
        int n = arr.length;
        float[] ret = new float[n];
        for (int i = 0; i < n; i++) {
            ret[i] = (float) arr[i];
        }
        return ret;
    }

    private double[] toDoubleArray(float[] arr) {
        if (arr == null) return null;
        int n = arr.length;
        double[] ret = new double[n];
        for (int i = 0; i < n; i++) {
            ret[i] = (double) arr[i];
        }
        return ret;
    }

	

	private void checkForCollisions() {
		com.bulletphysics.dynamics.DynamicsWorld dynamicsWorld;
		com.bulletphysics.collision.broadphase.Dispatcher dispatcher;
		com.bulletphysics.collision.narrowphase.PersistentManifold manifold;
		com.bulletphysics.dynamics.RigidBody object1, object2;
		com.bulletphysics.collision.narrowphase.ManifoldPoint contactPoint;

		dynamicsWorld = ((JBulletPhysicsEngine) physicsEngine).getDynamicsWorld();
		dispatcher = dynamicsWorld.getDispatcher();
		int manifoldCount = dispatcher.getNumManifolds();
		

		for (int i = 0; i < manifoldCount; i++) {
			manifold = dispatcher.getManifoldByIndexInternal(i);
			object1 = (RigidBody) manifold.getBody0();
			object2 = (RigidBody) manifold.getBody1();

			JBulletPhysicsObject obj1 = JBulletPhysicsObject.getJBulletPhysicsObject(object1);
			JBulletPhysicsObject obj2 = JBulletPhysicsObject.getJBulletPhysicsObject(object2);

			for (int j = 0; j < manifold.getNumContacts(); j++) {
				contactPoint = manifold.getContactPoint(j);
				if (contactPoint.getDistance() < 0.0f) {
					// Check if this is a player-car collision
					handleVehicleCollision(obj1, obj2);
					handleItemCollision(obj1, obj2);
					handleEndCollision(obj1, obj2);
					
				}
			}
		}

		// Reset the collision flag when the avatar is no longer in contact with the car
		if (hasCollided) {
			hasCollided = false;

			}
	}

	private void handleVehicleCollision(JBulletPhysicsObject obj1, JBulletPhysicsObject obj2){
		if ((obj1 == av1 && vehicles.contains(obj2)) || (obj2 == av1 && vehicles.contains(obj1))) {
			if (!hasCollided) {  // Only handle once
				System.out.println("Avatar collided with car!");
				// Perform the collision response, e.g., reset position or apply force
				Matrix4f initialTranslation = new Matrix4f().translation(0f, 0f, -110f);  
				dol.setLocalTranslation(initialTranslation);

				// Send move message
				protClient.sendMoveMessage(dol.getWorldLocation());
				hasCollided = true;
			}
		}
	}

	private void handleItemCollision(JBulletPhysicsObject obj1, JBulletPhysicsObject obj2) {
		// Check if avatar collided with item
		Matrix4f initialTranslation, initialScale, initialRotation;
		if ((obj1 == av1 && obj2 == item.getPhysicsObject()) || 
			(obj2 == av1 && obj1 == item.getPhysicsObject())) {
			
			itemPart = new GameObject(GameObject.root(),itemS);
			initialTranslation = (new Matrix4f()).translation(0,1,-1);
			itemPart.setLocalTranslation(initialTranslation);
			initialScale = (new Matrix4f()).scaling(0.5f);
			itemPart.setLocalScale(initialScale);
			itemPart.setParent(dol);
			itemPart.propagateTranslation(true);
			itemPart.propagateRotation(false);
			
			// Hide the item visually
			item.getRenderStates().disableRendering();
			hasItem = true;
			
			
		}
	}

	private void handleEndCollision(JBulletPhysicsObject obj1, JBulletPhysicsObject obj2) {
		// Check if avatar collided with end object
		if ((obj1 == av1 && obj2 == end.getPhysicsObject()) || 
			(obj2 == av1 && obj1 == end.getPhysicsObject())) {
			
			// Only trigger victory if player has the item
			if (hasItem) {
				gameWin = true;
			}
		}
	}

	private PhysicsObject updatePhysicsObjectLocation(PhysicsObject po, Matrix4f localTranslation){
		Matrix4f translation = new Matrix4f();
		double[] tempTransform;
		translation = new Matrix4f(localTranslation);
		translation.translate(0f, 1.5f, 0f); 
		tempTransform = toDoubleArray(translation.get(vals));
		po.setTransform(tempTransform);
		return po;
	}

	


	public void setEarParameters(){
			Camera camera = (engine.getRenderSystem()).getViewport("MAIN").getCamera();
			audioMgr.getEar().setLocation(avatar.getWorldLocation());
			audioMgr.getEar().setOrientation(camera.getN(), new Vector3f(0.0f, 1.0f, 0.0f));
		}


	public void setUpBTs(){
		carStartPosition = new Vector3f(car.getWorldLocation());
		car2StartPosition = new Vector3f(car2.getWorldLocation());
		truckStartPosition = new Vector3f(truck.getWorldLocation());
		truck2StartPosition = new Vector3f(truck2.getWorldLocation());
		lastThinkTime = System.nanoTime();
		carBT = new BehaviorTree(BTCompositeType.SELECTOR);
		truckBT = new BehaviorTree(BTCompositeType.SELECTOR);
		car2BT = new BehaviorTree(BTCompositeType.SELECTOR);
		truck2BT = new BehaviorTree(BTCompositeType.SELECTOR);

		// Sequence 1: If avatar is near, move right
		carBT.insertAtRoot(new BTSequence(1));
		carBT.insert(1, new CarOutOfBound(car));
		carBT.insert(1, new CarReset(car, carStartPosition));

		// Sequence 2: If 5 sec passed, move left
		carBT.insertAtRoot(new BTSequence(2));
		carBT.insert(2, new FiveSecPassed(this));
		carBT.insert(2, new MoveLeft(car));

		// Sequence 3: If car is too far, reset position
		carBT.insertAtRoot(new BTSequence(3));
		carBT.insert(3, new CarTooFar(car));
		carBT.insert(3, new ResetCar(car, carStartPosition));

		// car 2
		car2BT.insertAtRoot(new BTSequence(1));
		car2BT.insert(1, new CarOutOfBound(car2));
		car2BT.insert(1, new CarReset(car2, carStartPosition));

		// Sequence 2: If 5 sec passed, move left
		car2BT.insertAtRoot(new BTSequence(2));
		car2BT.insert(2, new FiveSecPassed(this));
		car2BT.insert(2, new MoveRight(car2));

		// Sequence 3: If car is too far, reset position
		car2BT.insertAtRoot(new BTSequence(3));
		car2BT.insert(3, new CarTooFar(car2));
		car2BT.insert(3, new ResetCar(car2, car2StartPosition));

		
		// Sequence 1: 
		truckBT.insertAtRoot(new BTSequence(1));
		truckBT.insert(1, new CarOutOfBound(truck));
		truckBT.insert(1, new CarReset(truck, truckStartPosition));

		// Sequence 2: If 5 sec passed, move left
		truckBT.insertAtRoot(new BTSequence(2));
		truckBT.insert(2, new FiveSecPassed(this));
		truckBT.insert(2, new MoveRight(truck));

		// Sequence 3: If car is too far, reset position
		truckBT.insertAtRoot(new BTSequence(3));
		truckBT.insert(3, new CarTooFar(truck));
		truckBT.insert(3, new ResetCar(truck, truckStartPosition));

		// truck 2
		truck2BT.insertAtRoot(new BTSequence(1));
		truck2BT.insert(1, new CarOutOfBound(truck2));
		truck2BT.insert(1, new CarReset(truck2, truck2StartPosition));

		// Sequence 2: If 5 sec passed, move left
		truck2BT.insertAtRoot(new BTSequence(2));
		truck2BT.insert(2, new FiveSecPassed(this));
		truck2BT.insert(2, new MoveLeft(truck2));

		// Sequence 3: If car is too far, reset position
		truck2BT.insertAtRoot(new BTSequence(3));
		truck2BT.insert(3, new CarTooFar(truck2));
		truck2BT.insert(3, new ResetCar(truck2, truck2StartPosition));


	}

	/* Some Java KeyListener inputs are used
	 * 1 - toggles the render state of the lines
	 * 2-5 - toggles the lights respectively
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{	
		
		switch (e.getKeyCode())
		{	
			case KeyEvent.VK_1:
				x.getRenderStates().toggle();
				y.getRenderStates().toggle();
				z.getRenderStates().toggle();
				break;
			case KeyEvent.VK_2:
				light1.toggleOnOff();
				if (light1.isEnabled() == true){
					Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
				} else {
					Light.setGlobalAmbient(0.01f,0.01f,0.01f);
				}

			case KeyEvent.VK_4:
				engine.togglePhysicsWorldRender();
			case KeyEvent.VK_5:
				{
				if (isWaving) {
					avatarS.stopAnimation();
					isWaving = false;
					avatarS.playAnimation("Idle", 0.5f, AnimatedShape.EndType.LOOP, 0);
				} else {
					avatarS.playAnimation("Wave", 0.5f, AnimatedShape.EndType.LOOP, 0);
					isWaving = true;
				}
				break;
				}
				
		}
		super.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
			{
				avatarS.stopAnimation();
				avatarS.playAnimation("Idle", 0.5f, AnimatedShape.EndType.LOOP, 0);
				walkSound.stop();
				setIsMoving(false);
				break;
			}
			case KeyEvent.VK_S:
			{
				avatarS.stopAnimation();
				avatarS.playAnimation("Idle", 0.5f, AnimatedShape.EndType.LOOP, 0);
				walkSound.stop();
				setIsMoving(false);
				break;
			}
			case KeyEvent.VK_A:
			{
				avatarS.stopAnimation();
				avatarS.playAnimation("Idle", 0.5f, AnimatedShape.EndType.LOOP, 0);
				walkSound.stop();
				setIsMoving(false);
				break;
			}
			case KeyEvent.VK_D:
			{
				avatarS.stopAnimation();
				avatarS.playAnimation("Idle", 0.5f, AnimatedShape.EndType.LOOP, 0);
				walkSound.stop();
				setIsMoving(false);
				break;
			}
		}
		super.keyReleased(e);
	}


	// ---------- NETWORKING SECTION ----------------

	public AnimatedShape getGhostShape() { return ghostS; }
	public TextureImage getGhostTexture() { return ghostT; }
	public GhostManager getGhostManager() { return gm; }
	public Engine getEngine() { return engine; }
	public ProtocolClient getProtClient() {return protClient;}
	
	private void setupNetworking()
	{	isClientConnected = false;	
		try 
		{	protClient = new ProtocolClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this);
		} 	catch (UnknownHostException e) 
		{	e.printStackTrace();
		}	catch (IOException e) 
		{	e.printStackTrace();
		}
		if (protClient == null)
		{	System.out.println("missing protocol host");
		}
		else
		{	// Send the initial join message with a unique identifier for this client
			System.out.println("sending join message to protocol host");
			protClient.sendJoinMessage();
		}
	}
	
	protected void processNetworking(float elapsTime)
	{	// Process packets received by the client from the server
		if (protClient != null)
			protClient.processPackets();
	}

	public Vector3f getPlayerPosition() { return avatar.getWorldLocation(); }

	public void setIsConnected(boolean value) { this.isClientConnected = value; }
	
	private class SendCloseConnectionPacketAction extends AbstractInputAction
	{	@Override
		public void performAction(float time, net.java.games.input.Event evt) 
		{	if(protClient != null && isClientConnected == true)
			{	protClient.sendByeMessage();
			}
		}
	}

}