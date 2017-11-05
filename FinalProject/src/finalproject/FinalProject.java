package finalproject;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class FinalProject {

    private CameraController camera = new CameraController(0, 0, 0);
    private DisplayMode displayMode;

    /**
     * Method used to start program.
     */
    public void start() {
        try {
            createWindow();
            initGL();
            loop(); //render() is inside gameLoop()
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
    }

    private void render() {
        try {
            Block b = new Block();
            b.drawBlock();
        } catch (Exception e) {
        }
    }

    public void loop() {
        float dx, dy;
        float mouseSens = 0.09f;
        float movementSpeed = .35f;
        Mouse.setGrabbed(true); // hides mouse

        while (!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && !Display.isCloseRequested()) {
            dx = Mouse.getDX();
            dy = Mouse.getDY();
            // control camera movement from mouse
            camera.yaw(dx * mouseSens);
            camera.pitch(dy * mouseSens);
            if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                camera.walkForward(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                camera.walkBackwards(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                camera.strafeLeft(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                camera.strafeRight(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                camera.moveUp(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                camera.moveDown(movementSpeed);
            }
            glLoadIdentity();
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            render();

            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }

    public static void main(String[] args) {
        FinalProject test = new FinalProject();
        test.start();
    }

}
