/*
 * Class that defines the agent function.
 * 
 * Written by James P. Biagioni (jbiagi1@uic.edu)
 * for CS511 Artificial Intelligence II
 * at The University of Illinois at Chicago
 * 
 * Last modified 2/19/07 
 * 
 * DISCLAIMER:
 * Elements of this application were borrowed from
 * the client-server implementation of the Wumpus
 * World Simulator written by Kruti Mehta at
 * The University of Texas at Arlington.
 * 
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class AgentFunction {
	
	// string to store the agent's name
	// do not remove this variable
	private String agentName = "Agent Smith";
    String outFilename = "test.txt";
	int timestamp = 0;
	
	// all of these variables are created and used
	// for illustration purposes; you may delete them
	// when implementing your own intelligent agent
	private Set<IntPair> pitLocations = new HashSet<>();
	private boolean pitsDiscovered = false;
	private boolean arrowUsed = false;
	private boolean bump;
	private boolean glitter;
	private boolean breeze;
	private boolean stench;
	private boolean scream;
	private int[][] state = new int[4][4];
	private int[][] probabilityOfPit = new int[4][4];
	private int lastMove = Action.NO_OP;
	private int x_coordinate = 0;
	private int y_coordinate = 0;
	private char direction = 'E';
	private int[] dir_row = {1, -1, 0, 0};
	private int[] dir_col = {0, 0, 1, -1};
	private boolean firstTimeStench = true;

	/*
	 * To check if the forward move is a valid square or 
	 * out of index
	 */
	private boolean isValid(int x, int y) {
		if (x >= 0 && x < 4 && y >= 0 && y < 4)
			return true;
		return false;
	}

	private boolean checkForwardMove() {
		int new_x = x_coordinate;
		int new_y = y_coordinate;
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
		if (isValid(new_x, new_y) && state[new_x][new_y] == 0) {
			return true;
		}
        return false;
    }

	/*
	 * If breeze is felt then set the state to 1 which indicates a pit
	 */
	private void markPit() {
		// If the two clusters of pits are discovered. Stop adding new pits
		if (pitsDiscovered == true) {
			for (int i = 0; i < 4; i++) {
				int new_x = x_coordinate + dir_row[i];
				int new_y = y_coordinate + dir_col[i];
				if (isValid(new_x, new_y) && state[new_x][new_y] == -1) {
					state[new_x][new_y] = 0;
					probabilityOfPit[new_x][new_y] = 0;
				}
			}
			return;
		}
		for (int i = 0; i < 4; i++) {
			int new_x = x_coordinate + dir_row[i];
			int new_y = y_coordinate + dir_col[i];
			if (isValid(new_x, new_y) && (state[new_x][new_y] == -1 || state[new_x][new_y] == 2)) {
				state[new_x][new_y] = 1;
				pitLocations.add(new IntPair(new_x, new_y));
				if (lastMove == Action.GO_FORWARD) {
					/*
					 * Increase the probablity of pit being the square when a breeze is felt.
					 */
					probabilityOfPit[new_x][new_y] = probabilityOfPit[new_x][new_y] * 2 + 1;
				}
			}
		}
	}

	// Remove the wumpus if the scream is heard
	private void removeWumpus() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (state[i][j] == 2) {
					state[i][j] = 0;
				}
			}
		}
	}

	// Mark gold is glitter is perceivedxs
	private void markGold() {
		state[x_coordinate][y_coordinate] = 99;
	}

	/*
	 * Mark all the adjacent squares as safe when no percept is felt.
	 */
	private void markSafe() {
		for (int i = 0; i < 4; i++) {
			int new_x = x_coordinate + dir_row[i];
			int new_y = y_coordinate + dir_col[i];
			if (isValid(new_x, new_y)) {
				if (pitLocations.contains(new IntPair(new_x, new_y))) {
					pitLocations.remove(new IntPair(new_x, new_y));
				}
				if (state[new_x][new_y] != 50) {
					state[new_x][new_y] = 0;
					probabilityOfPit[new_x][new_y] = 0;
				}
			}
		}
	}

		/*
	 * This functions sets the state to 2 which indicates that the square
	 * has wumpus.
	 */
	private void markWumpus(boolean alsoBreeze) {
		/* 
		 * Since there is just one wumpus. We just have to mark the squares
		 * as wumpus for the first time we get the stench.
		 * Later when we dont get the stench we can update the one marked 
		 * as wumpus earlier to safe. Thereby getting the exact position 
		 * of the wumpus
		 */
		if (firstTimeStench) {
			for (int i = 0; i < 4; i++) {
				int new_x = x_coordinate + dir_row[i];
				int new_y = y_coordinate + dir_col[i];
				if (isValid(new_x, new_y) && state[new_x][new_y] == -1) {
					state[new_x][new_y] = 2;
				}
			}
			firstTimeStench = false;
		} else {
			/*
			 * Since this is the second time we are getting stench. We can
			 * narrow down the position of the wumpus. And mark the places
			 * that are not visited to be safe.
			 */
			for (int i = 0; i < 4; i++) {
				int new_x = x_coordinate + dir_row[i];
				int new_y = y_coordinate + dir_col[i];
				if (isValid(new_x, new_y) && state[new_x][new_y] == -1 && alsoBreeze == false) {
					state[new_x][new_y] = 0;
				}
			}
		}

	}

	public AgentFunction()
	{
		//clear test buffer
		try {
		    BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outFilename));
		    outputWriter.write("\n");
		    outputWriter.close();
        } catch (Exception e) {
	    	System.out.println("An exception was thrown: " + e);
	    }
		// initialise state to -1 and probability of pit to be 0
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				state[i][j] = -1;
				probabilityOfPit[i][j] = 0;
			}
		}
		/*
		 * If the state is 0 then the square is safe.
		 * Mark the 0, 0 square as safe and visited
		 */
		state[0][0] = 50;
	}

	// This function is used to update the direction on turn left
	private void updateDirectionOnLeftMove() {
		if (direction == 'E') {
			direction = 'N';
		} else if (direction == 'N') {
			direction = 'W';
		} else if (direction == 'W') {
			direction = 'S';
		} else {
			direction = 'E';
		}
	}

	// This function is used to update the direction on turn right
	private void updateDirectionOnRightMove() {
		if (direction == 'E') {
			direction = 'S';
		} else if (direction == 'N') {
			direction = 'E';
		} else if (direction == 'W') {
			direction = 'N';
		} else {
			direction = 'W';
		}
	}

	private void goForward() {
		if (direction == 'E') {
			y_coordinate += 1;
		} else if (direction == 'N') {
			x_coordinate += 1;
		} else if (direction == 'W') {
			y_coordinate -= 1;
		} else {
			x_coordinate -= 1;
		}
		// Mark the square as visited on visiting it.
		state[x_coordinate][y_coordinate] = 50;
    }


	private void updateAgent(int nextMove) {
		if (nextMove == Action.TURN_LEFT) {
			updateDirectionOnLeftMove();
		}
		if (nextMove == Action.TURN_RIGHT) {
			updateDirectionOnRightMove();
		}
		if (nextMove == Action.GO_FORWARD) {
			goForward();
		}
		/*
		 * On shooting mark the next square as safe.
		 */
		if (nextMove == Action.SHOOT) {
			arrowUsed = true;
			int new_x = x_coordinate;
			int new_y = y_coordinate	;
			if (direction == 'E') {
				new_y += 1;
			} else if (direction == 'N') {
				new_x += 1;
			} else if (direction == 'W') {
				new_y -= 1;
			} else {
				new_x -= 1;
			}
			if (isValid(new_x, new_y))
				state[new_x][new_y] = 0;
		}
	}

	/*
	 * This function uses manhattan distance to find 2 different cluster of pits. So
	 * that we can stop marking new position as pits.
	 */
	private void countPits() {
		if (pitLocations.size() >= 2) {
			for (IntPair x : pitLocations) {
				boolean flag = false;
				for (IntPair y: pitLocations) {
					if (!x.equals(y)) {
						int distance = Math.abs(x.first - y.first) + Math.abs(x.second - y.second);
						if (distance <= 2)
							flag = true;
					}
				}
				if (flag == false) {
					pitsDiscovered = true;
					return;
				}
			}
		}
	}


	public int process(TransferPercept tp)
	{
		// To build your own intelligent agent, replace
		// all code below this comment block. You have
		// access to all percepts through the object
		// 'tp' as illustrated here:
		// read in the current percepts
		bump = tp.getBump();
		glitter = tp.getGlitter();
		breeze = tp.getBreeze();
		stench = tp.getStench();
		scream = tp.getScream();
		timestamp++;

		countPits();

		if (stench) {
			boolean alsoBreeze = false;
			if (breeze)
				alsoBreeze = true;
			markWumpus(alsoBreeze);
		}
		if (breeze) {
			markPit();
		}
		if (glitter) {
			markGold();
			return Action.GRAB;
		}
		if (!breeze && !stench) {
			markSafe();
		}
		if (scream) {
			removeWumpus();
		}
		try {
		    BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outFilename, true));
		    outputWriter.write("Search at timestamp"+timestamp+"\n");
		    outputWriter.close();
        } catch (Exception e) {
	    	System.out.println("An exception was thrown: " + e);
	    }
		int nextMove;
		if (checkForwardMove()) {
			nextMove = Action.GO_FORWARD;
		} else {
			nextMove = new Search(probabilityOfPit, state, x_coordinate, y_coordinate, direction, arrowUsed).nextMove();
		}
		updateAgent(nextMove);

		// Saves the last move
		lastMove = nextMove;
		// return action to be performed
	    return nextMove;
	}
	
	// public method to return the agent's name
	// do not remove this method
	public String getAgentName() {
		return agentName;
	}
}