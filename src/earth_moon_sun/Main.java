package earth_moon_sun;

import java.awt.event.*;

import javax.swing.JFrame;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.Animator;

public class Main extends JFrame implements GLEventListener, KeyListener, MouseListener, MouseMotionListener
{

	private static final long serialVersionUID = 1L;
	private GLCanvas canvas;
	private Animator animator;
	private TextureHandler sunTexture, moonTexture, cloudsTexture, earthTexture;
	private GLU glu;
	private float angle=0f, speed=0.2f;
	
	//vars for camera
	// Define camera variables
	float cameraAzimuth = 0.0f, cameraSpeed = 0.0f, cameraElevation = 0.0f;

	// Set camera at (0, 0, -20)
	float cameraCoordsPosx = 0.0f, cameraCoordsPosy = 0.0f, cameraCoordsPosz = -20.0f;

	// Set camera orientation
	float cameraUpx = 0.0f, cameraUpy = 1.0f, cameraUpz = 0.0f;

	public static void main(String args[]) {
		new Main();
	}

	// Default constructor;
	public Main()
	{
		
		super("Tema 7");

		// Registering a window event listener to handle the closing event.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 600);
		this.initializeJogl();
		this.setVisible(true);
	}

	private void initializeJogl()
	{
		// Creating a new GL profile.
		GLProfile glprofile = GLProfile.getDefault();
		// Creating an object to manipulate OpenGL parameters.
		GLCapabilities capabilities = new GLCapabilities(glprofile);

		// Setting some OpenGL parameters.
		capabilities.setHardwareAccelerated(true);
		capabilities.setDoubleBuffered(true);

		// Try to enable 2x anti aliasing. It should be supported on most hardware.
		capabilities.setNumSamples(2);
		capabilities.setSampleBuffers(true);
		
		this.glu = new GLU();

		// Creating an OpenGL display widget -- canvas.
		this.canvas = new GLCanvas(capabilities);

		// Adding the canvas in the center of the frame.
		this.getContentPane().add(this.canvas);

		// Adding an OpenGL event listener to the canvas.
		this.canvas.addGLEventListener(this);

		// Creating an animator that will redraw the scene 40 times per second.
		this.animator = new Animator(this.canvas);
		
		this.canvas.addKeyListener(this);
		this.canvas.addMouseListener(this);
		this.canvas.addMouseMotionListener(this);

		// Starting the animator.
		this.animator.start();
	}
	
	public void init(GLAutoDrawable canvas)
	{
		// Obtaining the GL instance associated with the canvas.
		GL2 gl = canvas.getGL().getGL2();

		// Initialize GLU. We'll need it for perspective and camera setup.
		this.glu = GLU.createGLU();

		// Setting the clear color -- the color which will be used to erase the canvas.
		gl.glClearColor(0, 0, 0, 0);

		// Selecting the modelview matrix.
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

		// Choose the shading model.
		gl.glShadeModel(GL2.GL_SMOOTH);

		// Activate the depth test and set the depth function.
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);
		
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glEnable(GL2.GL_LIGHT1);
		gl.glEnable(GL2.GL_LIGHT2);
		
		sunTexture = new TextureHandler(gl, glu, "src/earth_moon_sun/Sun.bmp", true);
		moonTexture = new TextureHandler(gl, glu, "src/earth_moon_sun/Moon.bmp", true);
		earthTexture = new TextureHandler(gl, glu, "src/earth_moon_sun/earth.jpg", true);
		cloudsTexture = new TextureHandler(gl, glu, "src/earth_moon_sun/clouds.png", true);

	}
	
	public void display(GLAutoDrawable canvas)
	{
		
		GL2 gl = canvas.getGL().getGL2();
		 
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		

		// Start with a fresh transformation i.e. the 4x4 identity matrix.
		gl.glLoadIdentity();
		
		aimCamera(gl, glu);
		moveCamera();
		
		// Save (push) the current matrix on the stack.
		gl.glPushMatrix();
		
		
		//add lighting
		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, new float [] {0.9f, 0.9f, 0.9f, 1f}, 0);
		//gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_AMBIENT, new float [] {0.8f, 0.8f, 0.8f, 1f}, 0);
		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, new float [] {0.0f, 0.0f, 0.0f, 1f}, 0);
		
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, new float [] {0.8f, 0.8f, 0.8f, 1f}, 0);
		
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, new float [] {0.6f, 0.6f, 0.6f, 1f}, 0);
		// The vector arguments represent the x, y, z, w values of the position.
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float [] {-10, 100, -100, 1f}, 0);
////////////////////
		// Enable stencil test
				gl.glEnable(GL.GL_STENCIL_TEST);

				// Specify what action to take when either the stencil test or depth test succeeds or fails
				gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);

				// Prepare to write a single bit into the stencil buffer in the area outside the viewport for every rendered pixel
				gl.glStencilFunc(GL.GL_ALWAYS, 0x1, 0x1); 
		///////////////////////

		gl.glTranslatef (0f, 0f, -15f);
		gl.glRotatef (90, 0, 1, 0);//aliniere poli
		

		gl.glPushMatrix();
		this.drawSphere(gl, glu, 0.5f, true,sunTexture);
		// Rotația lună+pământ în jurul soarelui
		//gl.glRotatef (90, 0, 1, 0);//aliniere poli
		
///////////////
		// Only render pixels if the corresponding stencil buffer bit is 1 i.e. inside the previously defined triangle
				// If one wishes to draw outside the triangle gl.glStencilFunc(GL.GL_EQUAL, 0x0, 0x1) should be used instead
				gl.glStencilFunc(GL.GL_EQUAL, 0x1, 0x1);
/////////////////
				
				// Push on the name stack the name (id) of the sphere.
				gl.glPushName(1);
				// Then draw it.
				this.drawSphere(gl, glu, 10f, false, sunTexture);
				// We are done so pop the name.
				gl.glPopName();
				
		
		gl.glRotatef (angle, 0, 1, 0);
		//Translate the second sphere to coordinates (4,0,0).
		gl.glTranslatef (3.0f, 0.0f, 0.0f);
		// Scale it to be half the size of the first one.
		gl.glScalef (0.7f, 0.7f, 0.7f);
		// Draw the second sphere.
		
		
		gl.glRotatef (angle*2/*365.24f*/, 0, 1, 0);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_DST_ALPHA);
		
		this.drawSphere(gl, glu, 0.5f, true, earthTexture);
		
		
		
		gl.glPushMatrix();
		
		
		gl.glRotatef (angle*0.8f, 0, 1, 0);
		gl.glScalef (1.005f, 1.005f, 1.005f);
		this.drawSphere(gl, glu, 0.5f, true, cloudsTexture);
		gl.glDisable(GL.GL_BLEND);
		
		gl.glScalef (0.5f, 0.5f, 0.5f);
		//rotația lunii în jurul pământului
		gl.glRotatef (angle*(365.24f/27.3f), 0, 1, 0);
		gl.glTranslatef (-3.0f, 0.0f, 0.0f);
		this.drawSphere(gl, glu, 0.5f, true,  moonTexture);
	
		
		// Translate the second sphere to
		// Restore (pop) from the stack the matrix holding the transformations produced by translating the first sphere.
		gl.glPopMatrix();
	
		// Restore (pop) from the stack the matrix holding the transformations prior to our translation of the first sphere. 
		//gl.glPopMatrix();

		gl.glFlush();


		// Increase the angle of rotation by 5 degrees.
		angle += speed;
	}
	
	public void reshape(GLAutoDrawable canvas, int left, int top, int width, int height)
	{
		GL2 gl = canvas.getGL().getGL2();

		// Selecting the viewport -- the display area -- to be the entire widget.
		gl.glViewport(0, 0, width, height);

		// Determining the width to height ratio of the widget.
		double ratio = (double) width / (double) height;

		// Selecting the projection matrix.
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);

		gl.glLoadIdentity();

		//glu.gluPerspective(45, ratio, .29f, 100f);
		glu.gluPerspective(38, ratio, 0.28, 100);
		gl.glViewport(0, 0, width, height);
		
		// Selecting the modelview matrix.
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	}
	
	private void drawSphere(GL gl, GLU glu, float radius, boolean texturing, TextureHandler texture)
	{

		if (texturing)
		{
			texture.bind();
			texture.enable();
		}

		GLUquadric sphere = glu.gluNewQuadric();
		if (texturing)
		{
	        	// Enabling texturing on the quadric.
			glu.gluQuadricTexture(sphere, true);
		}
		glu.gluSphere(sphere, radius, 64, 64);
		glu.gluDeleteQuadric(sphere);
	}

	public void displayChanged(GLAutoDrawable canvas, boolean modeChanged, boolean deviceChanged) {
		return;
	}
	
	public void keyPressed(KeyEvent event)
	{ 
		System.out.println(event.getKeyCode()+"");
		if (event.getKeyCode()== KeyEvent.VK_UP) {
			cameraElevation -= 2;
		}
			
		if (event.getKeyCode()== KeyEvent.VK_DOWN) {
			cameraElevation += 2;
		}
					
		if (event.getKeyCode()== KeyEvent.VK_RIGHT) {
			cameraAzimuth -= 2;
		}

		if (event.getKeyCode()== KeyEvent.VK_LEFT) {
			cameraAzimuth += 2;
		}
			
		if (event.getKeyCode()== KeyEvent.VK_I) {
			cameraSpeed += 0.05;
		}

		if (event.getKeyCode()== KeyEvent.VK_O) {
			cameraSpeed -= 0.05;
		}

		if (event.getKeyCode()== KeyEvent.VK_S) {
			cameraSpeed = 0;
		}

		if (cameraAzimuth > 359)
			cameraAzimuth = 1;
			
		if (cameraAzimuth < 1)
			cameraAzimuth = 359;	 	
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
// TODO Auto-generated method stub

	}
	
	public void moveCamera()
	{
		float[] tmp = polarToCartesian(cameraAzimuth, cameraSpeed, cameraElevation);
		
		// Replace old x, y, z coords for camera
		cameraCoordsPosx += tmp[0];
		cameraCoordsPosy += tmp[1];
		cameraCoordsPosz += tmp[2];
	}

	public void aimCamera(GL2 gl, GLU glu)
	{
		gl.glLoadIdentity();
		
		// Calculate new eye vector
		float[] tmp = polarToCartesian(cameraAzimuth, 100.0f, cameraElevation);
		
		// Calculate new up vector
		float[] camUp = polarToCartesian(cameraAzimuth, 100.0f, cameraElevation + 90);
		
		cameraUpx = camUp[0];
		cameraUpy = camUp[1];
		cameraUpz = camUp[2];
		
		glu.gluLookAt(cameraCoordsPosx, cameraCoordsPosy, cameraCoordsPosz,
				cameraCoordsPosx + tmp[0], cameraCoordsPosy + tmp[1],
				cameraCoordsPosz + tmp[2], cameraUpx, cameraUpy, cameraUpz);
	}

	private float[] polarToCartesian (float azimuth, float length, float altitude)
	{
		float[] result = new float[3];
		float x, y, z;
		
		// Do x-z calculation
		float theta = (float)Math.toRadians(90 - azimuth);
		float tantheta = (float) Math.tan(theta);
		float radian_alt = (float)Math.toRadians(altitude);
		float cospsi = (float) Math.cos(radian_alt);
		
		x = (float) Math.sqrt((length * length) / (tantheta * tantheta + 1));
		z = tantheta * x;
		
		x = -x;
		
		if ((azimuth >= 180.0 && azimuth <= 360.0) || azimuth == 0.0f) {
			x = -x;
			z = -z;
		}
		
		// Calculate y, and adjust x and z
		y = (float) (Math.sqrt(z * z + x * x) * Math.sin(radian_alt));
		
		if (length < 0) {
			x = -x;
			z = -z;
			y = -y;
		}
		
		x = x * cospsi;
		z = z * cospsi;
		
		// In contrast we could use the simplest form for computing Cartesian from Spherical as follows:
		// x = (float)(length * Math.sin(Math.toRadians(altitude))*Math.cos(Math.toRadians(azimuth)));
		// y = (float)(length * Math.sin(Math.toRadians(altitude))*Math.sin(Math.toRadians(azimuth)));
		// z = (float)(length * Math.cos(Math.toRadians(altitude)));


		result[0] = x;
		result[1] = y;
		result[2] = z;
		
		return result;
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}	
}