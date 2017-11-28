package finalproject;

/***************************************************************
* file: FinalProject.java
* authors: Kristin Adachi
*          Je'Don Roc Carter
*          Calvin Teng
*          Felix Zhang
*          Oscar Zhang
* class: CS 445 – Computer Graphics
*
* assignment: Final Program
* date last modified: 11/28/2017
*
* purpose: This program uses the LWJGL library to create a window of 
*          size 640x480. Within this window, we were required to create 
*          a small 3D world similar to that of the popular game Minecraft. 
*          We needed to implement a camera as well as render the 3D world 
*          itself by using OpenGL.
*
****************************************************************/ 

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class FinalProject {
    private CameraController camera;
    private DisplayMode displayMode;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;

    //method: start
    //purpose: Creates and initializes the window and the camera. The render 
    //         method is called within the camera's loop method.
    public void start() {
        try {
            createWindow();
            initGL();
            camera = new CameraController(-60, 0, 0);
            camera.loop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //method: createWindow
    //purpose: Creates the window with initial size 640x480 along with the 
    //         specified title.
    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        displayMode = new DisplayMode(640, 480);
        Display.setDisplayMode(displayMode);
        Display.setTitle("Final Project");
        Display.create();
    }

    //method: initGL
    //purpose: Initializes OpenGL with specific values. It specifies the 
    //         canvas background color, uses projection to view the scene, 
    //         loads the identity matrix, and sets up an orthographic matrix 
    //         with the origin at the center of the window.
    private void initGL() {
        glClearColor(0.5f, 0.8f, .97f, 0f);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        GLU.gluPerspective(100.0f, (float) displayMode.getWidth() / (float) displayMode.getHeight(), 0.1f, 300.0f);

        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnable(GL_DEPTH_TEST);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition); //sets our light’s position
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);//sets our specular light
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);//sets our diffuse light
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);//sets our ambient light
        glEnable(GL_LIGHTING);//enables our lighting
        glEnable(GL_LIGHT0);//enables light0    

        
    }
    
    //method: initLightArrays
    //purpose: initializes the position of the light source and the 
    //         white light which depicts the original color of the terrain
    private void initLightArrays(){
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
    }

    //method: main
    //purpose: Creates a FinalProject object and starts the program.
    public static void main(String[] args) {
        FinalProject test = new FinalProject();
        test.start();
    }

}
