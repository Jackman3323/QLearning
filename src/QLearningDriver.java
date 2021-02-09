public class QLearningDriver {
    public static void main(String[] args) {
        Maze maze = new Maze(10,5);
        maze.getMaze();

        QLearningDriver ql = new QLearningDriver();

        ql.init();
        ql.calculateQ();
        ql.printQ();
        ql.printPolicy();

    }
}
