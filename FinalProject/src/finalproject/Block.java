package finalproject;

/***************************************************************
* file: Block.java
* authors: Kristin Adachi
*          Je'Don Roc Carter
*          Calvin Teng
*          Felix Zhang
*          Oscar Zhang
* class: CS 445 – Computer Graphics
*
* assignment: Final Program
* date last modified: 11/15/2017
*
* purpose: The Block class is able to create 6 different types of 
*          blocks and determines whether each block is active. This 
*          class also displays the block in the world.
*
****************************************************************/ 

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3f;

public class Block {
    private boolean isActive;
    private BlockType type;
    private float x, y, z;

    //method: BlockType
    //purpose: Defines the block types.
    public enum BlockType {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5);

        private int blockID;

        BlockType(int i) {
            this.blockID = i;
        }

        public int getID() {
            return this.blockID;
        }

        public void setID(int i) {
            this.blockID = i;
        }

        public static BlockType getRandom() {
            return values()[(int) (Math.random() * values().length)];
        }
    }

    //method: Block
    //purpose: Block constructor.
    public Block(BlockType type) {
        this.type = type;
    }

    //method: setCoords
    //purpose: Sets the block's coordinates to the specified coordinate 
    //         values.
    public void setCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    //method: isActive
    //purpose: Determines if the block is active. Returns true if it is 
    //         and false otherwise.
    public boolean isActive() {
        return this.isActive;
    }

    //method: setActive
    //purpose: Sets the block's status to active.
    public void setActive(boolean active) {
        this.isActive = active;
    }

    //method: getID
    //purpose: Returns the block's ID.
    public int getID() {
        return this.type.getID();
    }

    
    //method: drawBlock
    //purpose: Renders and displays the block within the window in a 
    //         counter-clockwise fashion.
    protected void drawBlock() {
        glBegin(GL_QUADS);

        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);      //Top Right Of The Quad (Top)
        glVertex3f(-1.0f, 1.0f, -1.0f);     //Top Left Of The Quad (Top)
        glVertex3f(-1.0f, 1.0f, 1.0f);      //Bottom Left Of The Quad (Top)
        glVertex3f(1.0f, 1.0f, 1.0f);       //Bottom Right Of The Quad (Top)

        glColor3f(0.0f, 1.0f, 0.0f);
        glVertex3f(1.0f, -1.0f, 1.0f);      //Top Right Of The Quad (Bottom)
        glVertex3f(-1.0f, -1.0f, 1.0f);     //Top Left Of The Quad (Bottom)
        glVertex3f(-1.0f, -1.0f, -1.0f);    //Bottom Left Of The Quad (Bottom)
        glVertex3f(1.0f, -1.0f, -1.0f);     //Bottom Right Of The Quad (Bottom)

        glColor3f(0.0f, 0.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);       //Top Right Of The Quad (Front)
        glVertex3f(-1.0f, 1.0f, 1.0f);      //Top Left Of The Quad (Front)
        glVertex3f(-1.0f, -1.0f, 1.0f);     //Bottom Left Of The Quad (Front)
        glVertex3f(1.0f, -1.0f, 1.0f);      //Bottom Right Of The Quad (Front)

        glColor3f(0.5f, 0.5f, 0.5f);
        glVertex3f(1.0f, -1.0f, -1.0f);     //Bottom Left Of The Quad (Back)
        glVertex3f(-1.0f, -1.0f, -1.0f);    //Bottom Right Of The Quad (Back)
        glVertex3f(-1.0f, 1.0f, -1.0f);     //Top Right Of The Quad (Back)
        glVertex3f(1.0f, 1.0f, -1.0f);      //Top Left Of The Quad (Back)

        glColor3f(1.0f, 0.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);      //Top Right Of The Quad (Left)
        glVertex3f(-1.0f, 1.0f, -1.0f);     //Top Left Of The Quad (Left)
        glVertex3f(-1.0f, -1.0f, -1.0f);    //Bottom Left Of The Quad (Left)
        glVertex3f(-1.0f, -1.0f, 1.0f);     //Bottom Right Of The Quad (Left)

        glColor3f(0.0f, 1.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);      //Top Right Of The Quad (Right)
        glVertex3f(1.0f, 1.0f, 1.0f);       //Top Left Of The Quad (Right)
        glVertex3f(1.0f, -1.0f, 1.0f);      //Bottom Left Of The Quad (Right)
        glVertex3f(1.0f, -1.0f, -1.0f);     //Bottom Right Of The Quad (Right)
        glEnd();
    }

}
