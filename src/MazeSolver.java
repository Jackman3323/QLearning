import com.sun.java.swing.action.HelpAction;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

class QLearning {

    private final double alpha = 0.1; // Learning rate
    private final double gamma = 0.9; // Eagerness - 0 looks in the near future, 1 looks in the distant future

    private final int mazeWidth = 3;
    private final int mazeHeight = 3;
    private final int statesCount = mazeHeight * mazeWidth;

    private final int reward = 100;
    private final int penalty = -10;

    private Maze mazeGenerator = new Maze(3, 3);

    private String[][] maze;  // Maze read from file
    private int[][] R;       // Reward lookup
    private double[][] Q;    // Q learning

    public QLearning(int width, int height) {

    }

    public QLearning() {
        super();
    }


    public static void main(String args[]) {
        QLearning ql = new QLearning();
        ql.init();
        ql.calculateQ();
        ql.printQ();
        ql.printPolicy();
        ql.getPath();
    }

    public void init() {
        this.maze = mazeGenerator.getMaze();
        File file = new File("maze.txt");
        R = new int[statesCount][statesCount];
        Q = new double[statesCount][statesCount];

        Point startPoint = mazeGenerator.getStart();

        int i = (int) startPoint.getX();
        int j = (int) startPoint.getY();
        int k = i * mazeWidth;
        // We will navigate through the reward matrix R using k index
        for (int dumb = 0; dumb < statesCount; dumb++) {
            // We will navigate with i and j through the maze, so we need
            // to translate k into i and j
            i = k / mazeWidth;
            j = k - i * mazeWidth;
            for (int r = 0; r < this.mazeHeight; r++) {
                for (int c = 0; c < this.mazeWidth; c++) {
                    R[r][c] = -1;
                }
            }

            // If not in final state or a wall try moving in all directions in the maze
            try {
                if (!maze[i][j].equals("F")) {
                    // Try to move left in the maze
                    int goLeft = j - 1;
                    if (goLeft >= 0) {
                        int target = i * mazeWidth + goLeft;
                        if (maze[i][goLeft].equals(" ")) {
                            R[k][target] = 0;
                        } else if (maze[i][goLeft].equals("F")) {
                            R[k][target] = reward;
                        } else {
                            R[k][target] = penalty;
                        }
                    }

                    // Try to move right in the maze
                    int goRight = j + 1;
                    if (goRight < mazeWidth) {
                        int target = i * mazeWidth + goRight;
                        if (maze[i][goRight].equals(" ")) {
                            R[k][target] = 0;
                        } else if (maze[i][goRight].equals("F")) {
                            R[k][target] = reward;
                        } else {
                            R[k][target] = penalty;
                        }
                    }

                    // Try to move up in the maze
                    int goUp = i - 1;
                    if (goUp >= 0) {
                        int target = goUp * mazeWidth + j;
                        if (maze[goUp][j].equals(" ")) {
                            R[k][target] = 0;
                        } else if (maze[goUp][j].equals("F")) {
                            R[k][target] = reward;
                        } else {
                            R[k][target] = penalty;
                        }
                    }

                    // Try to move down in the maze
                    int goDown = i + 1;
                    if (goDown < mazeHeight) {
                        int target = goDown * mazeWidth + j;
                        if (maze[goDown][j].equals(" ")) {
                            R[k][target] = 0;
                        } else if (maze[goDown][j].equals("F")) {
                            R[k][target] = reward;
                        } else {
                            R[k][target] = penalty;
                        }
                    }
                }
                if (k == statesCount - 1) {
                    k = 0;
                } else {
                    k++;
                }
            }catch (IndexOutOfBoundsException e){

            }
        }
        initializeQ();
        printR(R);
        System.out.println("initialized");
    }

    //Set Q values to R values
    void initializeQ() {
        for (int i = 0; i < statesCount; i++) {
            for (int j = 0; j < statesCount; j++) {
                Q[i][j] = (double) R[i][j];
            }
        }
    }

    // Used for debug
    void printR(int[][] matrix) {
        System.out.printf("%25s", "States: ");
        for (int i = 0; i <= 8; i++) {
            System.out.printf("%4s", i);
        }
        System.out.println();

        for (int i = 0; i < statesCount; i++) {
            System.out.print("Possible states from " + i + " :[");
            for (int j = 0; j < statesCount; j++) {
                System.out.printf("%4s", matrix[i][j]);
            }
            System.out.println("]");
        }
    }

    void calculateQ() {
        Random rand = new Random();

       for (int i = 0; i < 1000; i++) { // Train cycles
            // Select random initial state
            int crtState = rand.nextInt(statesCount);

            while (!isFinalState(crtState)) {
                //System.out.println("stuck in calculatedq");
                int[] actionsFromCurrentState = possibleActionsFromState(crtState);

                // Pick a random action from the ones possible
                int index = rand.nextInt(actionsFromCurrentState.length);
                int nextState = actionsFromCurrentState[index];

                // Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) - Q(state,action))
                double q = Q[crtState][nextState];
                double maxQ = maxQ(nextState);
                int r = R[crtState][nextState];

                double value = q + alpha * (r + gamma * maxQ - q);
                Q[crtState][nextState] = value;

                crtState = nextState;
            }
       }
        System.out.println("calculatedQ");
    }
    public void getPath(){
        System.out.println("Path:");
        int state = (int) (mazeGenerator.getStart().getX()+mazeGenerator.getStart().getY());
        int next;
        do{
            next = getPolicyFromState(state);
            System.out.println("From "+state+" go to "+next);
            state = next;
        }while(!isFinalState(state));
    }
    boolean isFinalState(int state) {
        int i = state / mazeWidth;
        int j = state - i * mazeWidth;

        return maze[i][j].equals("F");
    }

    int[] possibleActionsFromState(int state) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < statesCount; i++) {
            if (R[state][i] != -1) {
                result.add(i);
            }
        }

        return result.stream().mapToInt(i -> i).toArray();
    }

    double maxQ(int nextState) {
        int[] actionsFromState = possibleActionsFromState(nextState);
        //the learning rate and eagerness will keep the W value above the lowest reward
        double maxValue = -10;
        for (int nextAction : actionsFromState) {
            double value = Q[nextState][nextAction];

            if (value > maxValue)
                maxValue = value;
        }
        return maxValue;
    }

    void printPolicy() {
        System.out.println("\nPrint policy");
        for (int i = 0; i < statesCount; i++) {
            System.out.println("From state " + i + " goto state " + getPolicyFromState(i));
        }
        System.out.println("printed policy");
    }

    int getPolicyFromState(int state) {
        int[] actionsFromState = possibleActionsFromState(state);

        double maxValue = Double.MIN_VALUE;
        int policyGotoState = state;

        // Pick to move to the state that has the maximum Q value
        for (int nextState : actionsFromState) {
            double value = Q[state][nextState];

            if (value > maxValue) {
                maxValue = value;
                policyGotoState = nextState;
            }
        }
        return policyGotoState;
    }

    void printQ() {
        System.out.println("Q matrix");
        for (int i = 0; i < Q.length; i++) {
            System.out.print("From state " + i + ":  ");
            for (int j = 0; j < Q[i].length; j++) {
                System.out.printf("%6.2f ", (Q[i][j]));
            }
            System.out.println("");
        }
        System.out.println("printedQ");
    }
}

