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
* purpose: Creates an additional feature to our project that displays a wall
*  of the FaceOfGod.
*
*************************************************************
*/
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class FaceOfGod {

    static final int CUBE_LENGTH = 2;
    private Block[][][] blocks;
    private int startX, startY, startZ;

    private int vboVertexHandle;
    private int vboColorHandle;
    private int vboTextureHandle;
    private Texture texture;

    //method: FaceOfGod
    //purpose: FaceOfGod constructor. Loads the textures and assigns block 
    //         types to each block. It also calls the rebuildMesh method 
    //         to draw the world.
    public FaceOfGod(int startX, int startY, int startZ) {
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        } catch (Exception e) {
            System.out.print("e-REEEEEEEEEEEEEEEEEEEEEE-r!");
        }
        blocks = new Block[30][15][1];
        this.vboColorHandle = glGenBuffers();
        this.vboVertexHandle = glGenBuffers();
        this.vboTextureHandle = glGenBuffers();
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }

    //method: render
    //purpose: Renders the FaceOfGod.
    public void render() {
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2, GL_FLOAT, 0, 0L);
        glDrawArrays(GL_QUADS, 0, 30 * 1 * 15 * 24);
        glPopMatrix();
    }

    //method: rebuildMesh
    //purpose: Method responsible for generating the FaceOfGod using.
    public void rebuildMesh(float startX, float startY, float startZ) {
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((30 * 1 * 15) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((30 * 1 * 15) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((30 * 1 * 15) * 6 * 12);

        for (float x = 0; x < 30; x++) {
            for (float z = 0; z < 1; z++) {
                for (float y = 0; y < 15; y++) {
                    //height created using simplex noise
                    determineBlockType((int) x, (int) y, (int) z);
                    VertexPositionData.put(createCube(
                            (float) (startX + x * CUBE_LENGTH + 10),
                            (float) (startY + y * CUBE_LENGTH + 10),
                            (float) (startZ + z * CUBE_LENGTH) + 10)
                    );
                    VertexColorData.put(createCubeVertexCol(getCubeColor()));
                    VertexTextureData.put(createTexCube(0f, 0f, blocks[(int) (x)][(int) (y)][(int) (z)]));
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    //method: determineBlockType
    //purpose: returns a default block type to avoid null block
    private void determineBlockType(int x, int y, int z) {
        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Default);
    }

    //method: createCubeVertexCol
    //purpose: Creates an array of type float and stores the block color 
    //         data in that array. Returns the float array.
    private float[] createCubeVertexCol(float[] cubeColorArray) {
        float[] cubeColors = new float[cubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = cubeColorArray[i % cubeColorArray.length];
        }
        return cubeColors;
    }

    //method: createCube
    //purpose: Creates an array of type float. Defines the vertices of 
    //         the block and stores these values in the array. Returns 
    //         the float array.
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[]{
            //Top quad
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            //Bottom quad
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            //Front quad
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            //Back quad
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            //Left Quad
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            //Right Quad
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z
        };
    }

    //method: createTexCube
    //purpose: creates tex-cube for FaceOfGod.
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f / 16) / 1024f;
        return getTexCubeCoord(x, y, offset, 1, 0, 1, 0);
    }

    //method: getTexCubeCoord
    //purpose: Helper method used to retrieve simpler textures from the 
    //         texture file ("terrain.png").
    private static float[] getTexCubeCoord(float x, float y, float offset, int m1, int m2, int m3, int m4) {
        return new float[]{
            // BOTTOM QUAD(DOWN=+Y)
            x + offset * m1, y + offset * m3,
            x + offset * m2, y + offset * m3,
            x + offset * m2, y + offset * m4,
            x + offset * m1, y + offset * m4,
            // TOP!
            x + offset * m1, y + offset * m3,
            x + offset * m2, y + offset * m3,
            x + offset * m2, y + offset * m4,
            x + offset * m1, y + offset * m4,
            // FRONT QUAD
            x + offset * m1, y + offset * m3,
            x + offset * m2, y + offset * m3,
            x + offset * m2, y + offset * m4,
            x + offset * m1, y + offset * m4,
            // BACK QUAD
            x + offset * m1, y + offset * m3,
            x + offset * m2, y + offset * m3,
            x + offset * m2, y + offset * m4,
            x + offset * m1, y + offset * m4,
            // LEFT QUAD
            x + offset * m1, y + offset * m3,
            x + offset * m2, y + offset * m3,
            x + offset * m2, y + offset * m4,
            x + offset * m1, y + offset * m4,
            // RIGHT QUAD
            x + offset * m1, y + offset * m3,
            x + offset * m2, y + offset * m3,
            x + offset * m2, y + offset * m4,
            x + offset * m1, y + offset * m4,};
    }

    //method: getCubeColor
    //purpose: Determines color for each cube texture. Returns an array 
    //         of type float with the 3 colors.
    private float[] getCubeColor() {
        return new float[]{1, 1, 1};
    }
}
