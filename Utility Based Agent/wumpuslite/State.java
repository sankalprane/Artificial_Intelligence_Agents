import java.io.BufferedWriter;
import java.io.FileWriter;

class State {
    public int[][] state = new int[4][4];
    public boolean arrowUsed;
    public int utility, x, y;
    public char direction;
    String outFilename = "test.txt";

    public State(int[][] oldState, char direction, int oldX, int oldY, boolean arrowUsed) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[i][j] = oldState[i][j];
            }
        }
        this.direction = direction;
        x = oldX;
        y= oldY;
        utility = 0;
        this.arrowUsed = arrowUsed;
    }

    public void print() {
        try {
		    // BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outFilename, true));
		    // outputWriter.write("x is: " + x + "\n");
            // outputWriter.write("y is: " + y + "\n");
            // outputWriter.write("direction is: " + direction + "\n");
            // for (int i = 3; i >= 0; i--) {
            //     for (int j = 0; j < 4; j++) {
            //         outputWriter.write(" " + state[i][j] + " ");
            //     }
            //     outputWriter.write("\n");
            // }
            // outputWriter.write("Utility is:" + getUtility(x, y) + "\n");
		    // outputWriter.close();
        } catch (Exception e) {
	    	System.out.println("An exception was thrown: " + e);
	    }
    }

    private int utilityOfSafeSquare() {
        int numberOfSafeSquare = 16;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (state[i][j] == 50)
                    numberOfSafeSquare--;
            }
        }
        return (1000 / numberOfSafeSquare);
    }

    public int getUtility(int x, int y) {
        utility = 0;
        if (state[x][y] == 99)
            utility += 1000;
        if (state[x][y] == 1 || state[x][y] == 2)
            utility -= 1000;
        if (state[x][y] == 0)
            utility += utilityOfSafeSquare();
        return utility;
    }
}