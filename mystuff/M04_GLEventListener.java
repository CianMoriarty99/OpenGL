import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class M04_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public M04_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

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
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
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
    sphere.dispose(gl);
    cube.dispose(gl);
    cube2.dispose(gl);
	sphere2.dispose(gl);
  }
  
  
  // ***************************************************
  /* INTERACTION
   *
   *
   */
   
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
   

  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Mat4 perspective;
  private Model floor, wall, sphere, cube, cube2, sphere2;
  private Light light;
  private SGNode robotRoot;
  
  private float xPosition = 0;
  private TransformNode translateX, robotMoveTranslate, topButtonRotate, headRotate, hat1Rotate,
  middleButtonRotate, bottomButtonRotate, eye1Rotate, eye2Rotate, noseRotate, mouthRotate , bodyRotate, bodyRotate2 ;
  
  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/cloud.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/surface_specular.jpg"); //Shiny surface
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/surface_specular.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/wattBook.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/surface_specular.jpg");
    
        
    light = new Light(gl);
    light.setCamera(camera);
    
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16,1f,16);
    floor = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId0, textureId2);
	wall = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3);
    
    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    sphere = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);
	sphere2 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId5, textureId6); 
    
    mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    cube = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3, textureId4);
    

    
    // robot
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
	float armLength = 3.5f;
    float armScale = 0.5f;
    
    robotRoot = new NameNode("root");
    robotMoveTranslate = new TransformNode("robot transform",Mat4Transform.translate(xPosition,0,0));
    
    TransformNode robotTranslate = new TransformNode("robot transform",Mat4Transform.translate(0,0,0));
	
    
//    NameNode body = new NameNode("body");
//      Mat4 m = Mat4Transform.scale(bodyScale,bodyScale,bodyScale);
//      m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
//	  bodyRotate = new TransformNode("body rotate",Mat4Transform.rotateAroundX(0));
//      TransformNode bodyTransform = new TransformNode("body transform", m);
//        ModelNode bodyShape = new ModelNode("Sphere(body)", sphere);
		
	NameNode body = new NameNode("body");
      TransformNode bodyTranslate = new TransformNode("body translate", 
                                           Mat4Transform.translate(0,0,0));
      bodyRotate = new TransformNode("body rotate",Mat4Transform.rotateAroundX(0)); // 0 is correct orientation
	  bodyRotate2 = new TransformNode("body rotate2",Mat4Transform.rotateAroundX(0));
      Mat4 m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(bodyScale,bodyScale,bodyScale));
      m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
      TransformNode body1Scale = new TransformNode("body scale", m);
        ModelNode bodyShape = new ModelNode("Sphere(body)", sphere);

		
	NameNode head = new NameNode("head");
      TransformNode headTranslate = new TransformNode("head translate", 
                                           Mat4Transform.translate(0,bodyScale,0));
      headRotate = new TransformNode("head rotate",Mat4Transform.rotateAroundX(0)); // 0 is correct orientation
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(headScale,headScale,headScale));
      m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
      TransformNode head1Scale = new TransformNode("head scale", m);
        ModelNode headShape = new ModelNode("Sphere(head)", sphere);
		
	NameNode hat1 = new NameNode("hat1");
      TransformNode hat1Translate = new TransformNode("hat1 translate", 
                                           Mat4Transform.translate(0.3f,((headScale*0.5f)+0.2f),((headScale*0.5f)-0.2f)));
      hat1Rotate = new TransformNode("hat1 rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(eyeScale,eyeScale,eyeScale));
      TransformNode hat1Scale = new TransformNode("hat1", m);
        ModelNode hat1Shape = new ModelNode("Sphere(hat1)", sphere2);
		
		


		
	NameNode eye1 = new NameNode("eye1");
      TransformNode eye1Translate = new TransformNode("eye1 translate", 
                                           Mat4Transform.translate(0.3f,((headScale*0.5f)+0.2f),((headScale*0.5f)-0.2f)));
      eye1Rotate = new TransformNode("eye1 rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(eyeScale,eyeScale,eyeScale));
      TransformNode eye1Scale = new TransformNode("eye1", m);
        ModelNode eye1Shape = new ModelNode("Sphere(eye1)", sphere2);
		
	NameNode eye2 = new NameNode("eye2");
      TransformNode eye2Translate = new TransformNode("eye2 translate", 
                                           Mat4Transform.translate(-0.3f,((headScale*0.5f)+0.2f),((headScale*0.5f)-0.2f)));
      eye2Rotate = new TransformNode("eye2 rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(eyeScale,eyeScale,eyeScale));
      TransformNode eye2Scale = new TransformNode("eye2", m);
        ModelNode eye2Shape = new ModelNode("Sphere(eye2)", sphere2);
		
	NameNode nose = new NameNode("nose");
      TransformNode noseTranslate = new TransformNode("nose translate", 
                                           Mat4Transform.translate(0,((headScale*0.5f)),((headScale*0.5f)-0.2f)));
      noseRotate = new TransformNode("nose rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(noseWidth,noseHeight,noseDepth));
      TransformNode noseScale = new TransformNode("nose", m);
        ModelNode noseShape = new ModelNode("Sphere(nose)", sphere2);
	
	NameNode mouth = new NameNode("mouth");
      TransformNode mouthTranslate = new TransformNode("mouth translate", 
                                           Mat4Transform.translate(0,((headScale*0.5f)-0.3f),((headScale*0.5f))));
      mouthRotate = new TransformNode("mouth rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(mouthWidth,mouthHeight,mouthDepth));
      TransformNode mouthScale = new TransformNode("mouth", m);
        ModelNode mouthShape = new ModelNode("Sphere(mouth)", sphere2);
		
		
		
    
   NameNode topbutton = new NameNode("top button");
      TransformNode topButtonTranslate = new TransformNode("topbutton translate", 
                                           Mat4Transform.translate(0,((bodyScale*0.5f)+0.5f),((bodyScale*0.5f)-0.2f)));
      topButtonRotate = new TransformNode("topButton rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(buttonScale,buttonScale,buttonScale));
      TransformNode topButtonScale = new TransformNode("topButton scale", m);
        ModelNode topButtonShape = new ModelNode("Sphere(top button)", sphere2);
		
		
    
    NameNode middleButton = new NameNode("middle button");
      TransformNode middleButtonTranslate = new TransformNode("middleButton translate", 
                                            Mat4Transform.translate(0,(bodyScale*0.5f),(bodyScale*0.5f)-0.1f));
      middleButtonRotate = new TransformNode("middleButton rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(buttonScale,buttonScale,buttonScale));
      TransformNode middleButtonScale = new TransformNode("middleButton scale", m);
        ModelNode middleButtonShape = new ModelNode("Sphere(middle button)", sphere2);
		
		
		
	NameNode bottomButton = new NameNode("bottom button");
      TransformNode bottomButtonTranslate = new TransformNode("bottomButton translate", 
                                           Mat4Transform.translate(0,((bodyScale*0.5f)-0.5f),((bodyScale*0.5f)-0.2f)));
      bottomButtonRotate = new TransformNode("bottomButton rotate",Mat4Transform.rotateAroundX(180));
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(buttonScale,buttonScale,buttonScale));
      TransformNode bottomButtonScale = new TransformNode("bottomButton scale", m);
        ModelNode bottomButtonShape = new ModelNode("Sphere(bottom button)", sphere2);
    

        
        
    robotRoot.addChild(robotMoveTranslate);
      robotMoveTranslate.addChild(robotTranslate);
        robotTranslate.addChild(body);
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
			


    
    robotRoot.update();  // IMPORTANT - don't forget this
    //robotRoot.print(0, false);
    //System.exit(0);
  }
  
  private Mat4 getMforWall() {
    float size = 16f;
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,size*0.5f,-size*0.5f), modelMatrix);
    return modelMatrix;
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    floor.render(gl); 
	wall.render(gl);
	wall.setModelMatrix(getMforWall());
    if (animation) 
		if (M04.rollHead) {
			updateHead();
		}
		if (M04.rockBody) {
			updateBody();
		}
		if (M04.slideAround){
			slideAround();
		}
    robotRoot.draw(gl);
  }
  

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
  
  private void updateBody() {
    double elapsedTime = getSeconds()-startTime;
    float rotateAngle = 180f+90f*(float)Math.sin(elapsedTime);
    bodyRotate.setTransform(Mat4Transform.rotateAroundZ((rotateAngle -180)/4));
    bodyRotate.update();
  }
  
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
  
  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
    //return new Vec3(5f,3.4f,5f);
  }

  
  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  /* An array of random numbers
   */ 
  
  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }
  
}