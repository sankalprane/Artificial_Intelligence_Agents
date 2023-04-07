import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

class Search {
    private int[] possibleEnv = {1, 2, 0};
	private int[] moves = { Action.TURN_LEFT, Action.TURN_RIGHT, Action.GO_FORWARD, Action.SHOOT};
    private int bestMove = Action.NO_OP;
    private int maxUtility, numberOfMoves;
    String outFilename = "test.txt";

    public Search(int[][] probabilityOfPit, int[][] state, int x, int y, char direction, boolean arrowUsed) {
        maxUtility = 0;
        numberOfMoves = 99999;
        dfs(new State(probabilityOfPit, state, direction, x, y, arrowUsed), 0, 0, new ArrayList<>());
    }

    public int nextMove() {
        System.out.println("Max utility" + maxUtility + "Number of Moves" + numberOfMoves);
        return bestMove;
    }

    private boolean isValid(int x, int y) {
		if (x >= 0 && x < 4 && y >= 0 && y < 4)
			return true;
		return false;
	}

    // This function is used to update the direction on turn left
	private char updateDirectionOnLeftMove(char direction) {
		if (direction == 'E') {
			direction = 'N';
		} else if (direction == 'N') {
			direction = 'W';
		} else if (direction == 'W') {
			direction = 'S';
		} else {
			direction = 'E';
		}
        return direction;
	}

	// This function is used to update the direction on turn right
	private char updateDirectionOnRightMove(char direction) {
		if (direction == 'E') {
			direction = 'S';
		} else if (direction == 'N') {
			direction = 'E';
		} else if (direction == 'W') {
			direction = 'N';
		} else {
			direction = 'W';
		}
        return direction;
	}

    private boolean checkForwardMove(State currState) {
        int x = currState.x;
        int y = currState.y;
        int direction = currState.direction;
		int new_x = x;
		int new_y = y;
		if (direction == 'E') {
			new_y += 1;
		} else if (direction == 'N') {
			new_x += 1;
		} else if (direction == 'W') {
			new_y -= 1;
		} else {
			new_x -= 1;
		}
		/*
		 * If the forward move is safe and the square in the front is 
		 * not visited then move forward
		 */
		if (isValid(new_x, new_y)) {
			return true;
		}
        return false;
    }

    private State onShoot(State currState) {
        State newState = new State(currState.knowledgeOfPit, currState.state, currState.direction, currState.x, currState.y, true);
        int x = currState.x;
        int y = currState.y;
        int direction = currState.direction;
		int new_x = x;
		int new_y = y;
		if (direction == 'E') {
			new_y += 1;
		} else if (direction == 'N') {
			new_x += 1;
		} else if (direction == 'W') {
			new_y -= 1;
		} else {
			new_x -= 1;
		}
        if (isValid(new_x, new_y)) {
            if (newState.state[new_x][new_y] == 2)
                newState.state[new_x][new_y] = 0;
        }
            
        return newState;
    }

    private int[] goForward(State currState) {
        int x = currState.x;
        int y = currState.y;
        char direction = currState.direction;
		int new_x = x;
		int new_y = y;
		if (direction == 'E') {
			new_y += 1;
		} else if (direction == 'N') {
			new_x += 1;
		} else if (direction == 'W') {
			new_y -= 1;
		} else {
			new_x -= 1;
		}
        return new int[]{new_x, new_y};
    }

    // Generate the pruned search tree
    private List<State> generatePossibleStates(State currState, int x, int y) {
        List<State> listOfStates = new ArrayList<State>();
        if (currState.state[x][y] == 0) {
            for (int i = 0; i < possibleEnv.length; i++) {
                State newState = new State(currState.knowledgeOfPit, currState.state, currState.direction, x, y, currState.arrowUsed);
                newState.state[x][y] = possibleEnv[i];
                listOfStates.add(newState);
                // newState.print();
            }
        }
        if (currState.state[x][y] == 50 || currState.state[x][y] == 0) {
            listOfStates.add(new State(currState.knowledgeOfPit, currState.state, currState.direction, x, y, currState.arrowUsed));
        }
        return listOfStates;
    }


    /*
     * Depth limited search with a depth of 10
     */
    private void dfs(State currState, int depth, int utility, List<Integer> listOfMoves) {
        if (utility >= maxUtility) {
            if (listOfMoves.size() != 0 && listOfMoves.size() < numberOfMoves)
                bestMove = listOfMoves.get(0);
            numberOfMoves = listOfMoves.size();
            maxUtility = utility;
        }
        currState.print();
        try {
		    // BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outFilename, true));
		    // outputWriter.write("List of Moves: \n");
            // for (int i = 0; i < listOfMoves.size(); i++) {
            //     outputWriter.write(listOfMoves.get(i)+ " ");
            // }
            // outputWriter.write("\n\n");
		    // outputWriter.close();
        } catch (Exception e) {
	    	System.out.println("An exception was thrown: " + e);
	    }
        if (depth == 10) {
            return;
        }
        for (int i = 0; i < moves.length; i++) {
            if (moves[i] == Action.GO_FORWARD) {
                if (checkForwardMove(currState)) {
                    int new_x = goForward(currState)[0];
                    int new_y =  goForward(currState)[1];
                    listOfMoves.add(Action.GO_FORWARD);
                    List<State> listOfStates = generatePossibleStates(currState, new_x, new_y);
                    for (int j = 0; j < listOfStates.size(); j++) {
                        dfs(listOfStates.get(j), depth + 1, listOfStates.get(j).getUtility(new_x, new_y) - (depth + 1), listOfMoves);
                    }
                    listOfMoves.remove(listOfMoves.size() - 1);
                }
            }
            if (moves[i] == Action.TURN_LEFT) {
                listOfMoves.add(Action.TURN_LEFT);
                dfs(new State(currState.knowledgeOfPit, currState.state, updateDirectionOnLeftMove(currState.direction), currState.x, currState.y, currState.arrowUsed), depth + 1, currState.getUtility(currState.x, currState.y) - (depth + 1), listOfMoves);
                listOfMoves.remove(listOfMoves.size() - 1);
            }
            if (moves[i] == Action.TURN_RIGHT) {
                listOfMoves.add(Action.TURN_RIGHT);
                dfs(new State(currState.knowledgeOfPit, currState.state, updateDirectionOnRightMove(currState.direction), currState.x, currState.y, currState.arrowUsed), depth + 1, currState.getUtility(currState.x, currState.y) - (depth + 1), listOfMoves);
                listOfMoves.remove(listOfMoves.size() - 1);
            }
            // Perform Shoot only if arrow is present
            if (moves[i] == Action.SHOOT && currState.arrowUsed == false) {
                listOfMoves.add(Action.SHOOT);
                State newState = onShoot(currState);
                // utility of -10 for shooting arrow.
                dfs(newState, depth + 1, newState.getUtility(currState.x, currState.y) - 10, listOfMoves);
                listOfMoves.remove(listOfMoves.size() - 1);
            }
        }
    }

}