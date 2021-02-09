import sun.util.resources.cldr.rof.CurrencyNames_rof;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Maze {
    //INSTANCE-DATA
    //maze: global variable to store the maze matrix
    private ArrayList<ArrayList<Character>> maze;
    private ArrayList<ArrayList<MazeCell>> mazeCells;
    //static final chars: nice, easily adjustable implementation of maze key
    private static final String END = "F";
    private static final String START = "S";
    private static final String WALL = "|";
    private static final String ROOF = "-";
    private static final String PATH = ".";
    private static final String HOLE = " ";
    //width and height: dimensions of maze
    private int width;
    private int height;
    private Random r;

    //CONSTRUCTOR
    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        this.r = new Random();
    }

    //MAIN-METHODS
    private void generateMaze() {
        this.mazeCells = new ArrayList<ArrayList<MazeCell>>();
        for (int i = 0; i < this.height; i++) {
            this.mazeCells.add(new ArrayList<MazeCell>());
            for (int c = 0; c < this.width; c++) {
                this.mazeCells.get(i).add(new MazeCell(new Point(c, i), false));
            }
        }
        Stack<Point> stack = new Stack<Point>();
        int randomStartLocation = r.nextInt(this.width);
        stack.push(new Point(randomStartLocation, 0));
        mazeCells.get(0).add(new MazeCell(new Point(randomStartLocation, 0), true));
        mazeCells.get(0).get(randomStartLocation).setStart(true);
        ArrayList<Point> alreadySearched = new ArrayList<Point>();
        ArrayList<Point> toSearch = new ArrayList<Point>();
        Point exit = new Point();
        while (stack.size() > 0) {
            //PUSH NEW POTENTIALS ONTO THE STACK
            Point curLoc = stack.peek();
            int currX = (int) curLoc.getX();
            int currY = (int) curLoc.getY();
            Point above = new Point(currX, currY - 1);
            Point below = new Point(currX, currY + 1);
            Point left = new Point(currX - 1, currY);
            Point right = new Point(currX + 1, currY);
            char dir = ' ';
            ArrayList<Point> potentials = new ArrayList<Point>();
            if (!alreadySearched.contains(new Point(above))) {
                //Haven't searched the one above this one
                if (!(currY == 0)) {
                    potentials.add(new Point(above));
                }
            }
            if (!alreadySearched.contains(new Point(below))) {
                //Haven't searched the one below this one
                if (!(currY == this.height - 1)) {
                    potentials.add(new Point(below));
                }
            }
            if (!alreadySearched.contains(new Point(left))) {
                //Haven't searched the one to the left of this one
                if (!(currX == 0)) {
                    potentials.add(new Point(left));
                }
            }
            if (!alreadySearched.contains(new Point(right))) {
                //Haven't searched the one to the right of this one
                if (!(currX == this.width - 1)) {
                    potentials.add(new Point(right));
                }
            }
            if (potentials.size() != 0) {
                //No dead ends yet
                Point randomSelected = potentials.get(r.nextInt(potentials.size()));
                stack.push(randomSelected);
                if (randomSelected.equals(above)) {
                    dir = 'T';
                    try {
                        this.mazeCells.get(currY - 1).get(currX).toggleWall(dir, true);
                        exit = curLoc;
                    } catch (IndexOutOfBoundsException impossible) {
                        //Do nothing, this is an impossible cell (meaning we are near a boundary), remove it from potential searches
                        stack.pop();
                    }
                } else if (randomSelected.equals(below)) {
                    dir = 'B';
                    try {
                        this.mazeCells.get(currY + 1).get(currX).toggleWall(dir, true);
                        exit = curLoc;
                    } catch (IndexOutOfBoundsException impossible) {
                        //Do nothing, this is an impossible cell (meaning we are near a boundary), remove it from potential searches
                        stack.pop();
                    }
                } else if (randomSelected.equals(right)) {
                    dir = 'R';
                    try {
                        this.mazeCells.get(currY).get(currX + 1).toggleWall(dir, true);
                        exit = curLoc;
                    } catch (IndexOutOfBoundsException impossible) {
                        //Do nothing, this is an impossible cell (meaning we are near a boundary), remove it from potential searches
                        stack.pop();
                    }
                } else if (randomSelected.equals(left)) {
                    dir = 'L';
                    try {
                        this.mazeCells.get(currY).get(currX - 1).toggleWall(dir, true);
                        exit = curLoc;
                    } catch (IndexOutOfBoundsException impossible) {
                        //Do nothing, this is an impossible cell (meaning we are near a boundary), remove it from potential searches
                        stack.pop();
                    }
                }
                this.mazeCells.get(currY).get(currX).toggleWall(dir, false);
            } else {
                stack.pop();//no potentials
            }
            alreadySearched.add(curLoc);
        }
        this.mazeCells.get((int) exit.getY()).get((int) exit.getX()).setFinish(true);
    }

    public String[][] getMaze() {
        this.generateMaze();
        int rowCounter = 0;
        int colCounter = 0;
        //This method interprets the arraylist of maze cell monstrosity into a nice string matrix and returns it.
        String[][] toReturn = new String[(this.height * 2) + 1][(this.width * 2) + 1];
        for (int r = 0; r < (this.height * 2); r += 2) {
            colCounter = 0;
            for (int c = 0; c < (this.width * 2); c += 2) {
                if(rowCounter >= this.height){
                    rowCounter = this.height-1;
                }
                if(colCounter >= this.width){
                    colCounter = this.width -1;
                }
                MazeCell curCell = this.mazeCells.get(rowCounter).get(colCounter);
                if (curCell.hasTopWall()) {
                    toReturn[r][c] = ROOF;
                    toReturn[r][c + 1] = ROOF;
                } else {
                    toReturn[r][c] = ROOF;
                    toReturn[r][c + 1] = HOLE;
                }
                if (curCell.hasLeftWall()) {
                    toReturn[r + 1][c] = WALL;
                } else {
                    toReturn[r + 1][c] = HOLE;
                }
                if(r == (this.height * 2) -2) {
                    if (curCell.hasBottomWall()){
                        toReturn[r + 2][c] = ROOF;
                        toReturn[r + 2][c + 1] = ROOF;
                        toReturn[r + 2][c + 2] = WALL;
                    } else {
                        toReturn[r + 2][c] = ROOF;
                        toReturn[r + 2][c + 1] = HOLE;
                        toReturn[r + 2][c + 2] = WALL;
                    }
                }
                if(c == this.width * 2 - 2){
                    toReturn[r][c + 2] = WALL;
                    toReturn[r + 1][c + 2] = WALL;
                }
                if(c == 0){
                    toReturn[r][c] = WALL;
                    toReturn[r+2][c] = WALL;
                }
                toReturn[r + 1][c + 1] = PATH;
                if(curCell.isFinish()){
                    toReturn[r+1][c+1] = END;
                }
                if(curCell.isStart()){
                    toReturn[r+1][c+1] = START;
                }
                colCounter++;
            }
            rowCounter++;
        }
        for(int r = 0; r < toReturn.length; r++){
            for(int c = 0; c < toReturn[0].length; c++){
                System.out.print(toReturn[r][c]);
            }
            System.out.print("\n");
        }
        return toReturn;
    }
}
