package finalproject;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class FinalProject {

    private CameraController camera;
    private DisplayMode displayMode;

    /**
     * Method used to start program.
     */
    public void start() {
        try {
            createWindow();
            initGL();
            camera = new CameraController(0, 0, 0);
            camera.loop(); //render() is inside gameLoop()
        } catch (Exception e) {
        }
    }

    /**
     * Method creates a window of size 640x480
     *
     * @throws Exception, LWJGLException
     */
    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("Final Project");
        Display.create();
    }

    /**
     * Method specifies the canvas background color, uses projection to view the
     * scene, loads the identity matrix, and sets up an orthographic matrix with
     * the origin at the center of the window.
     */
    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        GLU.gluPerspective(100.0f, (float) displayMode.getWidth() / (float) displayMode.getHeight(), 0.1f, 300.0f);

        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnable(GL_DEPTH_TEST); // so planes do not overlap when looking at them
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
    }

    public static void main(String[] args) {
        FinalProject test = new FinalProject();
        test.start();
    }

}
