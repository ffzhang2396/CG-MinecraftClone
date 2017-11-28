package finalproject;

/***************************************************************
 * file: Block.java
 * authors: Kristin Adachi
 *          Je'Don Roc Carter
 *          Calvin Teng
 *           Felix Zhang
 *          Oscar Zhang
 * class: CS 445 – Computer Graphics
 *
 * assignment: Final Program
 * date last modified: 11/17/2017
 *
 * purpose: The Chunk class essentially creates a chunk of textured blocks and
 * represents the world in our game. The world is 30x30 cubes large and each
 * cube is textured and randomly placed using simplex noise classes.
 *
 **************************************************************/

import java.io.IOException;
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
    static int stickAmount = 0;
    private Block[][][] blocks;
    private int startX, startY, startZ;

    private int vboVertexHandle;
    private int vboColorHandle;
    private int vboTextureHandle;
    private Texture texture;

    private Random r;

    //method: Chunk
    //purpose: Chunk constructor. Loads the textures and assigns block 
    //         types to each block. It also calls the rebuildMesh method 
    //         to draw the world.
    public Chunk(int startX, int startY, int startZ) throws IOException {
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        } catch (Exception e) {
            System.out.print("e-REEEEEEEEEEEEEEEEEEEEEE-r!");
        }
        r = new Random();

        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
//        for (int x = 0; x < CHUNK_SIZE; x++) {
//            for (int y = 0; y < CHUNK_SIZE; y++) {
//                for (int z = 0; z < CHUNK_SIZE; z++) {
//                    blocks[x][y][z] = new Block(Block.BlockType.getRandom());
//                }
//            }
//        }
        this.vboColorHandle = glGenBuffers();
        this.vboVertexHandle = glGenBuffers();
        this.vboTextureHandle = glGenBuffers();
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }

    //method: render
    //purpose: Renders the chunk.
    public void render() {
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

    //method: rebuildMesh
    //purpose: Method responsible for generating the terrain using simplex 
    //         noise classes. It also draws the chunk, creating our world.
    public void rebuildMesh(float startX, float startY, float startZ) throws IOException {
        //if persistence was less than 0.06, the chunk was almost flat
        //if it was greater than 0.1, valleys and mountains were too steep
        float persistence = 0f;
        while (persistence < 0.06f) {
            persistence = (0.1f) * r.nextFloat();
        }
        int seed = r.nextInt(50) + 1;
        System.out.println(persistence);
        System.out.println(seed);

        SimplexNoise noise = new SimplexNoise(CHUNK_SIZE, persistence, seed);
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);

        for (float x = 0; x < CHUNK_SIZE; x++) {
            for (float z = 0; z < CHUNK_SIZE; z++) {
                for (float y = 0; y < CHUNK_SIZE; y++) {
                    //height created using simplex noise
                    int height = (int) (startY + (int) (CHUNK_SIZE * noise.getNoise((int) x, (int) y, (int) z)) * CUBE_LENGTH);
                    if (y >= height) {
                        break;
                    }
                    determineBlockType((int) x, (int) y, (int) z);
                    VertexPositionData.put(createCube(
                            (float) (startX + x * CUBE_LENGTH + 10),
                            (float) (y * CUBE_LENGTH + (int) (CHUNK_SIZE * 0.8) - 60),
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
    //purpose: Differentiates the block types of each layer with bedrock
    //         at the bottom-most layer and with grass, water, or sand at
    //         the surface. In the center are dirt and stone blocks.
    //         The height is determined by the 'y' value of the block
    private void determineBlockType(int x, int y, int z) throws IOException {
        if (y == 0) {
            //bedrock is the bottom-most layer
            blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
        } else if ((y >= 6) && (stickAmount > 3)) {
            // creates grass on top layer, usually hills are grass blocks
            blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
        } else if ((y >= 6) && (stickAmount <= 3)) { //CHANGE!!!!
            // creates grass on top layer, usually hills are stick blocks
            blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stick);
            stickAmount = stickAmount + 1;
        } else if (y < 6 && y >= 4) {
            // creates water on outer layer, usually in a crater
            blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
            // makes edges of terrain be stone/dirt
            if ((x == 0 || x == CHUNK_SIZE - 1) || (z == 0 || z == CHUNK_SIZE - 1))
                blocks[x][y][z] = new Block(Block.BlockType.getRandomMidLayer());
        } else {
            //fills the middle layer with stone/dirt
            blocks[x][y][z] = new Block(Block.BlockType.getRandomMidLayer());
        }
        // chooses random spot on of top terrain to be sand
        if (((y == 7) && (x > (5 + Math.random() * 10)) && x < (11 + Math.random() * 15)) && (z > (5 + Math.random() * 10) && z < (11 + Math.random() * 15))) 
            blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
        /* DIAZ BLOCK
        if (((y >= 6) && (x > (5 + Math.random() * 10)) && x < (11 + Math.random() * 15)) && (z > (5 + Math.random() * 10) && z < (11 + Math.random() * 15))) { //CHANGE!!!!
            // creates grass on top layer, usually hills are stick blocks
            texture = null;
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("Diaz.png"));
            blocks[x][y][z] = new Block(Block.BlockType.BlockType_Diaz);
            stickAmount = stickAmount + 1;
        }
        */
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
    //purpose: Assigns textures from "terrain.png" to each side of the block. 
    //         The texture sheet is read from 0.0 - 1.0, indicating the need 
    //         for an offset. The origin on the texture sheet is located at 
    //         the top left corner (0px, 0px).
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f / 16) / 1024f;
        switch (block.getID()) {
            //grass block type
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
            //sand block type
            case 1:
                return getTexCubeCoord(x, y, offset, 3, 2, 2, 1);
            //water block type
            case 2:
                return getTexCubeCoord(x, y, offset, 16, 15, 12, 13);
            //dirt block type
            case 3:
                return getTexCubeCoord(x, y, offset, 3, 2, 0, 1);
            //stone block type
            case 4:
                return getTexCubeCoord(x, y, offset, 2, 1, 0, 1);
            //bedrock block type
            case 5:
                return getTexCubeCoord(x, y, offset, 2, 1, 1, 2);
            //stick block type
            case 6:
                return getTexCubeCoord(x, y, offset, 3, 2, 4, 3);
            //Diaz block type
            case 7:
                return getTexCubeCoord(x, y, offset, 1, 0, 1, 0);
        }
        return null;
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
