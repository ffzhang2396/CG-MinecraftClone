package finalproject;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {

    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private Block[][][] blocks;
    private int vboVertexHandle;
    private int vboColorHandle;
    private int startX, startY, startZ;
    private Random r;
    private int vboTextureHandle;
    private Texture texture;

    public Chunk(int startX, int startY, int startZ) {
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        } catch (Exception e) {
            System.out.print("e-REEEEEEEEEEEEEEEEEEEEEE-r!");
        }
        r = new Random();

        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    blocks[x][y][z] = new Block(Block.BlockType.getRandom());
                }
            }
        }
        this.vboColorHandle = glGenBuffers();
        this.vboVertexHandle = glGenBuffers();
        this.vboTextureHandle = glGenBuffers();
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }

    public void render() {
        glPushMatrix();
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, vboColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, vboTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2, GL_FLOAT, 0, 0L);
        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }

    public void rebuildMesh(float startX, float startY, float startZ) {
        SimplexNoise noise = new SimplexNoise(100, r.nextDouble() * .3, 27);
        vboColorHandle = glGenBuffers();
        vboVertexHandle = glGenBuffers();
        vboTextureHandle = glGenBuffers();
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);

        for (float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                for (float y = 0; y < CHUNK_SIZE; y += 1) {
                    VertexPositionData.put(createCube(
                            (float) (startX + x * CUBE_LENGTH),
                            //(float)(startY+y*CUBE_LENGTH+(int)(CHUNK_SIZE*.8)),
                            (float) (startY + (int) (100 * noise.getNoise((int) x, (int) y, (int) z)) * CUBE_LENGTH),
                            (float) (startZ + z * CUBE_LENGTH))
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

    /**
     * Stores cube color data in a vbo to be drawn when render is called.
     *
     * @param cubeColorArray array of RGB values (1, 1, 1)
     * @return cube colors
     */
    private float[] createCubeVertexCol(float[] cubeColorArray) {
        float[] cubeColors = new float[cubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = cubeColorArray[i % cubeColorArray.length];
        }
        return cubeColors;
    }

    /**
     * Creates coordinates of each cube to be stored within the vbo.
     *
     * @param x start x value
     * @param y start y value
     * @param z start z value
     * @return coordinates for creation of the cube
     */
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[]{
            // TOP QUAD
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            // BOTTOM QUAD
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            // FRONT QUAD
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            // BACK QUAD
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            // LEFT QUAD
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            // RIGHT QUAD
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z
        };
    }

    /**
     * Textures on texture sheet are read from 0.0 - 1.0 which is why the offset
     * is needed. The origin on the texture sheet (0px, 0px) is the top-left
     * corner of the texture sheet.
     *
     * @param x the xStart
     * @param y the yStart
     * @param block located inside the chunk
     * @return float containing coordinates for texture sheet
     */
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f / 16) / 1024f;
        switch (block.getID()) {
            case 0:
                return new float[]{
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 3, y + offset * 10,
                    x + offset * 2, y + offset * 10,
                    x + offset * 2, y + offset * 9,
                    x + offset * 3, y + offset * 9,
                    // TOP!
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // FRONT QUAD
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // BACK QUAD
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    // LEFT QUAD
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1};
            case 1:
                return getTexCubeCoord(x, y, offset, 3, 2, 2, 1);
            case 2:
                return getTexCubeCoord(x, y, offset, 16, 15, 12, 13);
            case 3:
                return getTexCubeCoord(x, y, offset, 3, 2, 0, 1);
            case 4:
                return getTexCubeCoord(x, y, offset, 2, 1, 0, 1);
            case 5:
                return getTexCubeCoord(x, y, offset, 2, 1, 1, 2);
        }
        return null;
    }

    /**
     * Method to retrieve simpler textures from terrain.png
     */
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

    /**
     * Gives color to each cube texture
     *
     * @return RGB value to show all 3 colors
     */
    private float[] getCubeColor() {
        return new float[]{1, 1, 1};
    }
}
