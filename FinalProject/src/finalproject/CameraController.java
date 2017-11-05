package finalproject;

import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;

/**
 * Used to control camera.
 */
public class CameraController {

    private Vector3f position = null; // 3d vector to store camera pos

    //rotation around y axis of camera
    private float yaw = 0.0f;
    //rotations around x axis of camera
    private float pitch = 0.0f;

    public CameraController(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
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

}
