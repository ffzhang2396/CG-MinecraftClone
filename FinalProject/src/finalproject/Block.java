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
* purpose: The Block class is used to create 6 different types of 
*          blocks and determines whether each block is active.
*          Coordinates for each block are stored inside a separate
*          vertex buffer object.
*
****************************************************************/ 


public class Block {
    private boolean isActive;
    private BlockType type;
    private float x, y, z;

    public enum BlockType {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5);

        private int blockID;
        
        //method: BlockType
        //purpose: BlockType constructor.
        BlockType(int i) {
            this.blockID = i;
        }

        //method: getID
        //purpose: To retrieve ID of block's block-type.
        public int getID() {
            return this.blockID;
        }

        //method: setID
        //purpose: To set the ID of the block's block-type.
        public void setID(int i) {
            this.blockID = i;
        }

        //method: getRandom
        //purpose: to return a random block-type
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

}
