import java.awt.*;

/**
 * MazeCell.java
 *
 * This is a class intended to be used as a cell within a maze. It stores its location, whether or not it's the start or
 * the finish, and it's wall data.
 *
 * Authors: Jack Hughes
 * Date: 2-8-21
 * -JBH
 */
public class MazeCell {
    //INSTANCE-DATA
    private Point location;
    private boolean hasTopWall;
    private boolean hasBottomWall;
    private boolean hasLeftWall;
    private boolean hasRightWall;
    private boolean isStart;
    private boolean isFinish;

    //CONSTRUCTOR
    public MazeCell(Point location, boolean start){
        this.location = location;
        hasBottomWall = true;
        hasLeftWall = true;
        hasRightWall = true;
        isFinish = false;
        isStart = start;
        hasTopWall = true;
    }

    //METHODS, all self explanatory setters and getters
    public void setWalls(boolean top, boolean left, boolean right, boolean bottom){
        this.hasRightWall = right;
        this.hasTopWall = top;
        this.hasLeftWall = left;
        this.hasBottomWall = bottom;
        if(hasLeftWall && hasBottomWall && hasRightWall && hasTopWall){
            System.out.println("ERROR: Impossible cell with four walls");
            System.exit(-9);
        }
    }
    public void toggleWall(char id, boolean opp){
        if(opp) {
            switch (id) {
                case 'L':
                    this.hasRightWall = !this.hasRightWall;
                    break;
                case 'R':
                    this.hasLeftWall = !this.hasLeftWall;
                    break;
                case 'T':
                    this.hasBottomWall = !this.hasBottomWall;
                    break;
                case 'B':
                    this.hasTopWall = !this.hasTopWall;
                    break;
            }
        }
        else {
            switch (id) {
                case 'L':
                    this.hasLeftWall = !this.hasLeftWall;
                    break;
                case 'R':
                    this.hasRightWall = !this.hasRightWall;
                    break;
                case 'T':
                    this.hasTopWall = !this.hasTopWall;
                    break;
                case 'B':
                    this.hasBottomWall = !this.hasBottomWall;
                    break;
            }
        }
    }
    public void setFinish(boolean finish){
        this.isFinish = finish;
    }

    public boolean isFinish(){
        return this.isFinish;
    }

    public boolean isStart(){
        return this.isStart;
    }

    public void setStart(boolean start){
        this.isStart = start;
        if(start){
            this.hasTopWall = false;
        }
    }

    public boolean atFinish(){
        return isFinish;
    }

    public boolean hasBottomWall() {
        return hasBottomWall;
    }
    public boolean hasTopWall() {
        return hasTopWall;
    }
    public boolean hasLeftWall() {
        return hasLeftWall;
    }
    public boolean hasRightWall() {
        return hasRightWall;
    }

    //toString for debugging
    @Override
    public String toString() {
        return "MazeCell{" +
                "location=" + location +
                ", hasTopWall=" + hasTopWall +
                ", hasBottomWall=" + hasBottomWall +
                ", hasLeftWall=" + hasLeftWall +
                ", hasRightWall=" + hasRightWall +
                ", isStart=" + isStart +
                ", isFinish=" + isFinish +
                '}';
    }
}
