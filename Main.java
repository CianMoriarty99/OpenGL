/* I declare that the code is my own work  */ 
/* Author <Cian Moriarty> <Cmoriarty1@sheffield.ac.uk> */
/* I have used parts of Dr. Steve Maddocks examples and exercise sheets in this work*/

import gmaths.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class Main extends JFrame implements ActionListener {
  
  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private Main_GLEventListener glEventListener;
  private final FPSAnimator animator; 
  private Camera camera;
  public static boolean rollHead;
  public static boolean rockBody;
  public static boolean slideAround;
  public static boolean sunlight;
  public static boolean reset;
  

  public static void main(String[] args) {
    Main b1 = new Main("Main");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
  }

  public Main(String textForTitleBar) {
    super(textForTitleBar);
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new Main_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));
    getContentPane().add(canvas, BorderLayout.CENTER);
    
    JMenuBar menuBar=new JMenuBar();
    this.setJMenuBar(menuBar);
      JMenu fileMenu = new JMenu("File");
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(this);
        fileMenu.add(quitItem);
    menuBar.add(fileMenu);
    
    JPanel p = new JPanel();
      JButton b = new JButton("Rock");
      b.addActionListener(this);
      p.add(b);
	  b = new JButton("Roll");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Slide");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Slide, Rock, and Roll");
      b.addActionListener(this);
      p.add(b);
      b = new JButton("Reset");
      b.addActionListener(this);
      p.add(b);
	  b = new JButton("Toggle Sunlight");
      b.addActionListener(this);
      p.add(b);
    this.add(p, BorderLayout.SOUTH);
    
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        animator.stop();
        remove(canvas);
        dispose();
        System.exit(0);
      }
    });
    animator = new FPSAnimator(canvas, 60);
    animator.start();
	glEventListener.startAnimation();
  }
  
  public void actionPerformed(ActionEvent e) {
	if (e.getActionCommand().equalsIgnoreCase("Roll")) {
	  reset = false;
	  rollHead = true;
	  rockBody = false;
	  slideAround = false;
    }
	else if (e.getActionCommand().equalsIgnoreCase("Rock")) {
	  reset = false;
	  rockBody = true;
	  rollHead = false;
	  slideAround = false;
    }
    else if (e.getActionCommand().equalsIgnoreCase("Slide")) {
	  reset = false;
	  slideAround = true;
	  rockBody = false;
	  rollHead = false;


	 }
    else if (e.getActionCommand().equalsIgnoreCase("Slide, Rock, and Roll")) {
	  reset = false;
	  rockBody = true;
	  rollHead = true;
	  slideAround = true;
    }
    else if (e.getActionCommand().equalsIgnoreCase("Reset")) {
	  reset = true;
      rockBody = false;
	  rollHead = false;
	  slideAround = false;
	  //Reset position and rotation also
    }
	
	else if (e.getActionCommand().equalsIgnoreCase("Toggle Sunlight")) {
		sunlight = !sunlight;
    }
    else if(e.getActionCommand().equalsIgnoreCase("quit"))
      System.exit(0);
  }
  
}
 
class MyKeyboardInput extends KeyAdapter  {
  private Camera camera;
  
  public MyKeyboardInput(Camera camera) {
    this.camera = camera;
  }
  
  public void keyPressed(KeyEvent e) {
    Camera.Movement m = Camera.Movement.NO_MOVEMENT;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
      case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
      case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
      case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
      case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
      case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
    }
    camera.keyboardInput(m);
  }
}

class MyMouseInput extends MouseMotionAdapter {
  private Point lastpoint;
  private Camera camera;
  
  public MyMouseInput(Camera camera) {
    this.camera = camera;
  }
  
    /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */    
  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    float sensitivity = 0.001f;
    float dx=(float) (ms.x-lastpoint.x)*sensitivity;
    float dy=(float) (ms.y-lastpoint.y)*sensitivity;
    //System.out.println("dy,dy: "+dx+","+dy);
    if (e.getModifiers()==MouseEvent.BUTTON1_MASK)
      camera.updateYawPitch(dx, -dy);
    lastpoint = ms;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */  
  public void mouseMoved(MouseEvent e) {   
    lastpoint = e.getPoint(); 
  }
}