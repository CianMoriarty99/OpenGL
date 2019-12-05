/* I declare that the code is my own work  */ 
/* Author <Cian Moriarty> <Cmoriarty1@sheffield.ac.uk> */
/* I have used parts of Dr. Steve Maddocks examples and exercise sheets in this work*/

import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;


  
public class Main_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  private Shader shader;
  private Shader shaderBackground;


  public Main_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
  }
  


  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Set viewport */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    floor.dispose(gl);
	wall.dispose(gl);
    hat.dispose(gl);
	hatButton.dispose(gl);
	shinyCube.dispose(gl); 
	bodySphere.dispose(gl); 
	buttons.dispose(gl); 
	shinySphere.dispose(gl);
  }

  /* Animation control */
  private boolean animation = false;
  private double savedTime = 0;
   
  public void startAnimation() {
    animation = true;
    startTime = getSeconds()-savedTime;
  }
   
  public void stopAnimation() {
    animation = false;
    double elapsedTime = getSeconds()-startTime;
    savedTime = elapsedTime;
  }

/* Initialise Variable and textures */
  private Camera camera;
  private Mat4 perspective;

  private Model floor, wall, hat, shinyCube, bodySphere, buttons, shinySphere,hatButton;
  private Light light ;
  private SGNode snowmanRoot;
  
  private float xPosition = 0;
  
  private TransformNode  snowmanMoveTranslate, topButtonRotate, headRotate, hat1Rotate, hat2Rotate, hat3Rotate,
  middleButtonRotate, bottomButtonRotate, eye1Rotate, eye2Rotate, noseRotate, mouthRotate , 
  bodyRotate, bodyRotate2, spotlightBaseRotate, spotlightPivotRotate, shinyObjectRotate ;
  
  private void initialise(GL3 gl) {
    createRandomNumbers();
	
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/snow2.jpg"); //floor
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/SnowBackground1.jpg"); //background
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/DirtySnow.jpg"); //Snow Body
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/snowFall.jpg"); //Body Specular
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/Button.jpg"); //Button
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/surface_specular.jpg"); //Button Specular
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/wool.jpg"); //Hat
	int[] textureId7 = TextureLibrary.loadTexture(gl, "textures/wool_spec.jpg"); //Hat Specular
    int[] textureId8 = TextureLibrary.loadTexture(gl, "textures/Metal.jpg"); //Shiny Metal Object
    int[] textureId9 = TextureLibrary.loadTexture(gl, "textures/surface_specular.jpg"); //Shiny Metal Specular
    int[] textureId10 = TextureLibrary.loadTexture(gl, "textures/woolRed.jpg"); //

    


    
		
    //Shaders
	shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
	shaderBackground = new Shader(gl, "vs_background_06.txt", "fs_background_06.txt");
	
	
	//Light
	light = new Light(gl);
    light.setCamera(camera);
	
	
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone()); 
    Material material = new Material(new Vec3(0.0f, 0.0f, 0.0f) , new Vec3(0.0f, 0.0f, 0.0f) , new Vec3(0.0f, 0.0f, 0.0f), 100f);
    Mat4 modelMatrix = Mat4Transform.scale(25,1f,25);
    floor = new Model(gl, camera, light, shader,  material, modelMatrix, mesh, textureId0, textureId3);
	wall = new Model(gl, camera, light, shaderBackground,  material, modelMatrix, mesh, textureId1, textureId3);
    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
	
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    bodySphere = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId2, textureId9);
	
	material = new Material(new Vec3(1f, 1f, 1f), new Vec3(1f, 1f, 1f), new Vec3(1f, 1f, 1f), 0.1f);
	buttons = new Model(gl, camera, light, shader,  material, modelMatrix, mesh, textureId4, textureId9); 
	hatButton = new Model(gl, camera, light, shader,  material, modelMatrix, mesh, textureId10, textureId7); 
	shinySphere = new Model(gl, camera, light, shader,  material, modelMatrix, mesh, textureId8, textureId9);
    mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
	
	material = new Material(new Vec3(1f, 1f, 1f) , new Vec3(1f, 1f, 1f) , new Vec3(0f, 0f, 0f), 100f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    hat = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId6, textureId7);
	
	material = new Material(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.0f, 0.0f, 0.0f), new Vec3(1f, 1f, 1f), 0.1f);
	shinyCube = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId8, textureId9);

    
    // Snowman shape variables
	float noseDepth = 1f;
    float noseWidth = 0.2f;
	float noseHeight = 0.2f;
	
	float mouthDepth = 0.2f;
    float mouthWidth = 0.6f;
	float mouthHeight = 0.2f;
	
    float bodyScale = 3f;
    float headScale = 2f;
    float buttonScale = 0.5f;
	float eyeScale = 0.5f;
	
	//Hat variables
	float hatBaseScale = 2.2f;
	float hatBaseHeight = 0.2f;
	float hatTopScale = 1.1f;
	float hatTopHeight = 1f;
	float hatSphereScale = 0.7f;
	
	//Spotlight variables
	float spotlightBaseScale = 0.2f;
	float spotlightBaseHeight = 10f;
	float spotlightPivotScale = 0.6f;
	float spotlightPivotLength = 3f;
	
	//Shiny object variables
	float shinyObjectScale = 3f;
	float shinyObjectHeight = 6f;

    
    snowmanRoot = new NameNode("root");
    snowmanMoveTranslate = new TransformNode("snowman transform",Mat4Transform.translate(xPosition,0,0));
    TransformNode snowmanTranslate = new TransformNode("snowman transform",Mat4Transform.translate(0,0,0));
	
    

	//Body
	NameNode body = new NameNode("body");
      TransformNode bodyTranslate = new TransformNode("body translate", 
                                           Mat4Transform.translate(0,0,0));
      bodyRotate = new TransformNode("body rotate",Mat4Transform.rotateAroundX(0)); // 0 is correct orientation
	  bodyRotate2 = new TransformNode("body rotate2",Mat4Transform.rotateAroundX(0));
      Mat4 m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(bodyScale,bodyScale,bodyScale));
      m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
      TransformNode body1Scale = new TransformNode("body scale", m);
        ModelNode bodyShape = new ModelNode("Sphere(body)", bodySphere);

	//Head	
	NameNode head = new NameNode("head");
      TransformNode headTranslate = new TransformNode("head translate", 
                                           Mat4Transform.translate(0,bodyScale,0));
      headRotate = new TransformNode("head rotate",Mat4Transform.rotateAroundX(0)); // 0 is correct orientation
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(headScale,headScale,headScale));
      m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
      TransformNode head1Scale = new TransformNode("head scale", m);
        ModelNode headShape = new ModelNode("Sphere(head)", bodySphere);
	
	//Hat	
	NameNode hat1 = new NameNode("hat1");
      TransformNode hat1Translate = new TransformNode("hat1 translate", 
                                           Mat4Transform.translate(0,((headScale)-0.1f),0));
      hat1Rotate = new TransformNode("hat1 rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(hatBaseScale,hatBaseHeight,hatBaseScale));
      TransformNode hat1Scale = new TransformNode("hat1", m);
        ModelNode hat1Shape = new ModelNode("Sphere(hat1)", hat);
		
	NameNode hat2 = new NameNode("hat2");
      TransformNode hat2Translate = new TransformNode("hat2 translate", 
                                           Mat4Transform.translate(0,((headScale)+(hatTopHeight*0.5f)-0.1f),0));
      hat2Rotate = new TransformNode("hat2 rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(hatTopScale,hatTopHeight,hatTopScale));
      TransformNode hat2Scale = new TransformNode("hat2", m);
        ModelNode hat2Shape = new ModelNode("Sphere(hat2)", hat);
		
	NameNode hat3 = new NameNode("hat3");
      TransformNode hat3Translate = new TransformNode("hat3 translate", 
                                           Mat4Transform.translate(hatTopScale*0.5f,((headScale)+(hatTopHeight)),0));
      hat3Rotate = new TransformNode("hat3 rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(hatSphereScale,hatSphereScale,hatSphereScale));
      TransformNode hat3Scale = new TransformNode("hat3", m);
        ModelNode hat3Shape = new ModelNode("Sphere(hat3)", hatButton);
		
		
	//Face	
	NameNode eye1 = new NameNode("eye1");
      TransformNode eye1Translate = new TransformNode("eye1 translate", 
                                           Mat4Transform.translate(0.3f,((headScale*0.5f)+0.2f),((headScale*0.5f)-0.2f)));
      eye1Rotate = new TransformNode("eye1 rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(eyeScale,eyeScale,eyeScale));
      TransformNode eye1Scale = new TransformNode("eye1", m);
        ModelNode eye1Shape = new ModelNode("Sphere(eye1)", buttons);
		
	NameNode eye2 = new NameNode("eye2");
      TransformNode eye2Translate = new TransformNode("eye2 translate", 
                                           Mat4Transform.translate(-0.3f,((headScale*0.5f)+0.2f),((headScale*0.5f)-0.2f)));
      eye2Rotate = new TransformNode("eye2 rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(eyeScale,eyeScale,eyeScale));
      TransformNode eye2Scale = new TransformNode("eye2", m);
        ModelNode eye2Shape = new ModelNode("Sphere(eye2)", buttons);
		
	NameNode nose = new NameNode("nose");
      TransformNode noseTranslate = new TransformNode("nose translate", 
                                           Mat4Transform.translate(0,((headScale*0.5f)),((headScale*0.5f)-0.2f)));
      noseRotate = new TransformNode("nose rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(noseWidth,noseHeight,noseDepth));
      TransformNode noseScale = new TransformNode("nose", m);
        ModelNode noseShape = new ModelNode("Sphere(nose)", buttons);
	
	NameNode mouth = new NameNode("mouth");
      TransformNode mouthTranslate = new TransformNode("mouth translate", 
                                           Mat4Transform.translate(0,((headScale*0.5f)-0.3f),((headScale*0.5f))));
      mouthRotate = new TransformNode("mouth rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(mouthWidth,mouthHeight,mouthDepth));
      TransformNode mouthScale = new TransformNode("mouth", m);
        ModelNode mouthShape = new ModelNode("Sphere(mouth)", buttons);
		
	//Buttons	
    NameNode topbutton = new NameNode("top button");
      TransformNode topButtonTranslate = new TransformNode("topbutton translate", 
                                           Mat4Transform.translate(0,((bodyScale*0.5f)+0.5f),((bodyScale*0.5f)-0.2f)));
      topButtonRotate = new TransformNode("topButton rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(buttonScale,buttonScale,buttonScale));
      TransformNode topButtonScale = new TransformNode("topButton scale", m);
        ModelNode topButtonShape = new ModelNode("Sphere(top button)", buttons);
		
    
    NameNode middleButton = new NameNode("middle button");
      TransformNode middleButtonTranslate = new TransformNode("middleButton translate", 
                                            Mat4Transform.translate(0,(bodyScale*0.5f),(bodyScale*0.5f)-0.1f));
      middleButtonRotate = new TransformNode("middleButton rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(buttonScale,buttonScale,buttonScale));
      TransformNode middleButtonScale = new TransformNode("middleButton scale", m);
        ModelNode middleButtonShape = new ModelNode("Sphere(middle button)", buttons);
		
		
		
	NameNode bottomButton = new NameNode("bottom button");
      TransformNode bottomButtonTranslate = new TransformNode("bottomButton translate", 
                                           Mat4Transform.translate(0,((bodyScale*0.5f)-0.5f),((bodyScale*0.5f)-0.2f)));
      bottomButtonRotate = new TransformNode("bottomButton rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(buttonScale,buttonScale,buttonScale));
      TransformNode bottomButtonScale = new TransformNode("bottomButton scale", m);
        ModelNode bottomButtonShape = new ModelNode("Sphere(bottom button)", buttons);
    
	
	//Spotlight
	NameNode spotlightBase = new NameNode("spotlightBase");
      TransformNode spotlightBaseTranslate = new TransformNode("spotlightBase translate", 
                                           Mat4Transform.translate(-10f,(spotlightBaseHeight*0.5f),0));
      spotlightBaseRotate = new TransformNode("spotlightBase rotate",Mat4Transform.rotateAroundX(0));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(spotlightBaseScale,spotlightBaseHeight,spotlightBaseScale));
      TransformNode spotlightBaseScale1 = new TransformNode("spotlightBase scale", m);
        ModelNode spotlightBaseShape = new ModelNode("Sphere(spotlightBase)", shinySphere);
		
	NameNode spotlightPivot = new NameNode("spotlightPivot");
      TransformNode spotlightPivotTranslate = new TransformNode("spotlightPivot translate", 
                                           Mat4Transform.translate(0,(spotlightBaseHeight*0.5f),0));
      spotlightPivotRotate = new TransformNode("spotlightPivot rotate",Mat4Transform.rotateAroundZ(50));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(spotlightPivotScale,spotlightPivotLength,spotlightPivotScale));
      TransformNode spotlightPivotScale1 = new TransformNode("spotlightPivot scale", m);
        ModelNode spotlightPivotShape = new ModelNode("Sphere(spotlightPivot)", shinySphere);


	//Shiny Object
	NameNode shinyObject = new NameNode("shinyObject");
      TransformNode shinyObjectTranslate = new TransformNode("shinyObject translate", 
                                           Mat4Transform.translate(8f,(shinyObjectHeight*0.5f),0));
      shinyObjectRotate = new TransformNode("shinyObject rotate",Mat4Transform.rotateAroundX(0));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(shinyObjectScale,shinyObjectHeight,shinyObjectScale));
      TransformNode shinyObjectScale1 = new TransformNode("shinyObject scale", m);
        ModelNode shinyObjectShape = new ModelNode("Cube(shinyObject)", shinyCube);

        
    //SceneGraph
    snowmanRoot.addChild(snowmanMoveTranslate);
      snowmanMoveTranslate.addChild(snowmanTranslate);
        snowmanTranslate.addChild(body);
		snowmanTranslate.addChild(spotlightBase);
		snowmanTranslate.addChild(shinyObject);
		
		  shinyObject.addChild(shinyObjectTranslate);
            shinyObjectTranslate.addChild(shinyObjectRotate);
			shinyObjectRotate.addChild(shinyObjectScale1);
            shinyObjectScale1.addChild(shinyObjectShape);
		
		  spotlightBase.addChild(spotlightBaseTranslate);
            spotlightBaseTranslate.addChild(spotlightBaseRotate);
			spotlightBaseRotate.addChild(spotlightBaseScale1);
            spotlightBaseScale1.addChild(spotlightBaseShape);
			
			spotlightBaseRotate.addChild(spotlightPivotTranslate);
				spotlightPivotTranslate.addChild(spotlightPivotRotate);
				spotlightPivotRotate.addChild(spotlightPivotScale1);
				spotlightPivotScale1.addChild(spotlightPivotShape);
			
          body.addChild(bodyTranslate);
            bodyTranslate.addChild(bodyRotate);
			bodyRotate.addChild(bodyRotate2);
            bodyRotate2.addChild(body1Scale);
            body1Scale.addChild(bodyShape);
			
			
			bodyRotate2.addChild(head);
				head.addChild(headTranslate);
				headTranslate.addChild(headRotate);
				headRotate.addChild(head1Scale);
				head1Scale.addChild(headShape);
				
				
			headRotate.addChild(hat1);
				hat1.addChild(hat1Translate);
				hat1Translate.addChild(hat1Rotate);
				hat1Rotate.addChild(hat1Scale);
				hat1Scale.addChild(hat1Shape);	
			
			headRotate.addChild(hat2);
				hat2.addChild(hat2Translate);
				hat2Translate.addChild(hat2Rotate);
				hat2Rotate.addChild(hat2Scale);
				hat2Scale.addChild(hat2Shape);	
				
			headRotate.addChild(hat3);
				hat3.addChild(hat3Translate);
				hat3Translate.addChild(hat3Rotate);
				hat3Rotate.addChild(hat3Scale);
				hat3Scale.addChild(hat3Shape);	
			
			
			headRotate.addChild(eye1);
				eye1.addChild(eye1Translate);
				eye1Translate.addChild(eye1Rotate);
				eye1Rotate.addChild(eye1Scale);
				eye1Scale.addChild(eye1Shape);

			headRotate.addChild(eye2);
				eye2.addChild(eye2Translate);
				eye2Translate.addChild(eye2Rotate);
				eye2Rotate.addChild(eye2Scale);
				eye2Scale.addChild(eye2Shape);
			
			headRotate.addChild(nose);
				nose.addChild(noseTranslate);
				noseTranslate.addChild(noseRotate);
				noseRotate.addChild(noseScale);
				noseScale.addChild(noseShape);
		
			headRotate.addChild(mouth);
				mouth.addChild(mouthTranslate);
				mouthTranslate.addChild(mouthRotate);
				mouthRotate.addChild(mouthScale);
				mouthScale.addChild(mouthShape);
			
			bodyRotate2.addChild(topbutton);
				topbutton.addChild(topButtonTranslate);
				topButtonTranslate.addChild(topButtonRotate);
				topButtonRotate.addChild(topButtonScale);
				topButtonScale.addChild(topButtonShape);
				
			bodyRotate2.addChild(middleButton);
				middleButton.addChild(middleButtonTranslate);
				middleButtonTranslate.addChild(middleButtonRotate);
				middleButtonRotate.addChild(middleButtonScale);
				middleButtonScale.addChild(middleButtonShape);
				
			bodyRotate2.addChild(bottomButton);
				bottomButton.addChild(bottomButtonTranslate);
				bottomButtonTranslate.addChild(bottomButtonRotate);
				bottomButtonRotate.addChild(bottomButtonScale);
				bottomButtonScale.addChild(bottomButtonShape);
			
    snowmanRoot.update();

  }
  
  //Manipulate background position/rotation
  private Mat4 getMforWall() {
    float size = 25f;
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,size*0.5f,-size*0.5f), modelMatrix);
    return modelMatrix;
  }
 
 //Render everything in the scene
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

	shaderBackground.use(gl);
	double elapsedTime = getSeconds()-startTime;
	double t = elapsedTime *0.1;
	float offsetX = 0.0f;
	float offsetY = (float)(t- Math.floor(t));
	shaderBackground.setFloat(gl, "offset", offsetX, offsetY);
	
	wall.render(gl);
	light.render(gl);
    floor.render(gl); 
	
	wall.setModelMatrix(getMforWall());
	
    if (animation) 
		if (Main.rollHead) {
			updateHead();
		}
		if (Main.rockBody) {
			updateBody();
		}
		if (Main.slideAround){
			slideAround();
		}
		if ((Main.sunlight)){
			//gl.glEnable(GL_LIGHT0);
		}
		if (!(Main.sunlight)){
			//gl.glDisable(GL_LIGHT0);
		}
		if (Main.reset){
			resetSnowman();
		}
		
	updateSpotlight();
    snowmanRoot.draw(gl);
  }
	

  private void resetSnowman() {
	  Mat4 m1 = new Mat4(1);
	  headRotate.setTransform(m1);
	  bodyRotate.setTransform(m1);
	  bodyRotate2.setTransform(m1);
	  headRotate.update();
	  bodyRotate.update();
	  bodyRotate2.update();

  }
  
	//Animate head
  private void updateHead() {
    double elapsedTime = getSeconds()-startTime;
    float rotateAngle1 = 180f+90f*(float)Math.sin(elapsedTime);
	float rotateAngle2 = 180f+90f*(float)Math.sin(elapsedTime+2);
	Mat4 m1 = Mat4Transform.rotateAroundX((rotateAngle1 -180)/2);
	Mat4 m2 = Mat4Transform.rotateAroundZ((rotateAngle2 -180)/2);
	Mat4 m3 = Mat4.multiply(m1, m2);
    headRotate.setTransform(m3);
    headRotate.update();
  }
  
  //Animate Body
  private void updateBody() {
    double elapsedTime = getSeconds()-startTime;
    float rotateAngle = 180f+90f*(float)Math.sin(elapsedTime);
    bodyRotate.setTransform(Mat4Transform.rotateAroundZ((rotateAngle -180)/4));
    bodyRotate.update();
  }
  
  //Animate Slide motion
   private void slideAround() {
    double elapsedTime = getSeconds()-startTime;
    float rotateAngle = 180f+90f*(float)Math.sin(elapsedTime*5);
	float slideSwing1 = (float)Math.sin(elapsedTime);
	float slideSwing2 = (float)Math.sin(elapsedTime+2);
	Mat4 m1 = Mat4Transform.rotateAroundY((rotateAngle -180)/7);
	Mat4 m2 = Mat4Transform.translate(slideSwing1/2,0,slideSwing2/2);
	Mat4 m3 = Mat4.multiply(m1, m2);
    bodyRotate2.setTransform(m3);
    bodyRotate2.update();
  }
  
  //Rotate spotlight object
    private void updateSpotlight() {
    double elapsedTime = getSeconds()-startTime;
    spotlightBaseRotate.setTransform(Mat4Transform.rotateAroundY((float)(elapsedTime*180f)));
    spotlightBaseRotate.update();
	float x = -10f + 1.3f*(float)(Math.sin(Math.toRadians(elapsedTime*180f+90f)));
    float y = 9f;
    float z = 1.3f*(float)(Math.cos(Math.toRadians(elapsedTime*180f+90f)));
    light.setPosition(x,y,z);   
  }
  
  private double startTime;
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }
  
}