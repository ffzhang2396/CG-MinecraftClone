package finalproject;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;

/**
 * Used to control camera.
 */
public class CameraController {

    private Vector3f position = null; // 3d vector to store camera pos
    private float yaw = 0.0f;     //rotation around y axis of camera
    private float pitch = 0.0f;    //rotations around x axis of camera
    private Chunk chunk;

    public CameraController(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
        chunk = new Chunk((int) x, (int) y, (int) z);
    }

    // increament camera rotation around y axis
    public void yaw(float amount) {
        this.yaw += amount;
    }

    // decrement camera rotation around x axis
    public void pitch(float amount) {
        this.pitch -= amount;
    }

    //move camera forward relative to current yaw rotation
    public void walkForward(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        this.position.x -= xOffset;
        this.position.z += zOffset;
    }

    public void walkBackwards(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
    }

    public void strafeLeft(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw - 90));
        this.position.x -= xOffset;
        this.position.z += zOffset;
    }

    public void strafeRight(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw + 90));
        this.position.x -= xOffset;
        this.position.z += zOffset;
    }

    //moves the camera up relative to its current rotation (yaw)
    public void moveUp(float distance) {
        this.position.y -= distance;
    }

    //moves the camera down
    public void moveDown(float distance) {
        this.position.y += distance;
    }

    /**
     * Translates and rotates the matrix so that it looks through the camera.
     */
    public void lookThrough() {
        //roatate the pitch around the X axis
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        //roatate the yaw around the Y axis
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        //translate to the position vector's location
        glTranslatef(this.position.x, this.position.y, this.position.z);
    }

    public void loop() {
        CameraController camera = new CameraController(0, 0, 0);
        float dx, dy;
        float dt = 0.0f, lastTime = 0.0f;
        long time = 0;
        float mouseSens = 0.09f;
        float movementSpeed = .35f;
        Mouse.setGrabbed(true); // hides mouse

        while (!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && !Display.isCloseRequested()) {
            time = Sys.getTime();
            lastTime = time;

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

            try {
                chunk.render();
            } catch (Exception e) {
            }

            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }

}
