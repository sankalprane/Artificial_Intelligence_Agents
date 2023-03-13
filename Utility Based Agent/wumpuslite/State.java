import java.io.BufferedWriter;
import java.io.FileWriter;

class State {
    public int[][] state = new int[4][4];
    public int[][] knowledgeOfPit = new int[4][4];
    public boolean arrowUsed;
    public int utility, x, y;
    public char direction;
    public boolean grabPerformed = false;
    String outFilename = "test.txt";

    public State(int[][] probabilityOfPit, int[][] oldState, char direction, int oldX, int oldY, boolean arrowUsed, boolean grabPerformed) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[i][j] = oldState[i][j];
                knowledgeOfPit[i][j] = probabilityOfPit[i][j];
            }
        }
        this.direction = direction;
        x = oldX;
        y= oldY;
        utility = 0;
        this.arrowUsed = arrowUsed;
        this.grabPerformed = grabPerformed;
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

    private boolean isValid(int x, int y) {
		if (x >= 0 && x < 4 && y >= 0 && y < 4)
			return true;
		return false;
	}

    /*
     * This is calculated using the values of gold * probability of gold being the square
     */
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


    /*
     * Using probability calculate the changes of pit being in a square
     * The utility of the pit is -1000 * probability of pit being the square
     */
    private int utilityOfPit(int x, int y) {
        int cnt = 0;
        if (isValid(x + 1, y + 1)) {
            cnt += knowledgeOfPit[x + 1][y + 1];
        }
        if (isValid(x + 1, y - 1)) {
            cnt += knowledgeOfPit[x + 1][y - 1];
        }
        if (isValid(x - 1, y + 1)) {
            cnt += knowledgeOfPit[x - 1][y + 1];
        }
        if (isValid(x - 1, y - 1)) {
            cnt += knowledgeOfPit[x - 1][y - 1];
        }
        double value = (1000.0 /(double)cnt) * knowledgeOfPit[x][y];
        return (int)value;
    }


    /*
     * Gives the utility of the current state. This in addition with the utility 
     * equation used in dfs function gives the total utility.
     */
    public int getUtility(int x, int y) {
        if (grabPerformed && state[x][y] == 99)
            return utility = 9999999;
        utility = 0;
        if (state[x][y] == 99)
            utility += 1000;
        if (state[x][y] == 2)
            utility -= 1000;
        if (state[x][y] == 1)
            utility -= utilityOfPit(x, y);
        if (state[x][y] == 0)
            utility += utilityOfSafeSquare();
        return utility;
    }
}