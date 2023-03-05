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

class AgentFunction {
	
	// string to store the agent's name
	// do not remove this variable
	private String agentName = "Agent Smith";
    String outFilename = "test.txt";
	int timestamp = 0;
	
	// all of these variables are created and used
	// for illustration purposes; you may delete them
	// when implementing your own intelligent agent
	private boolean bump;
	private boolean glitter;
	private boolean breeze;
	private boolean stench;
	private boolean scream;
	private int[][] state = new int[4][4];
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

	/*
	 * If breeze is felt then set the state to 1 which indicates a pit
	 */
	private void markPit() {
		for (int i = 0; i < 4; i++) {
			int new_x = x_coordinate + dir_row[i];
			int new_y = y_coordinate + dir_col[i];
			if (isValid(new_x, new_y) && state[new_x][new_y] == -1) {
				state[new_x][new_y] = 1;
			}
		}
	}

	private void removeWumpus() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (state[i][j] == 2) {
					state[i][j] = 0;
				}
			}
		}
	}

	private void markGold() {
		state[x_coordinate][y_coordinate] = 99;
	}

	private void markSafe() {
		for (int i = 0; i < 4; i++) {
			int new_x = x_coordinate + dir_row[i];
			int new_y = y_coordinate + dir_col[i];
			if (isValid(new_x, new_y)) {
				if (state[new_x][new_y] != 50)
				state[new_x][new_y] = 0;
			}
		}
	}

		/*
	 * This functions sets the state to 2 which indicates that the square
	 * has wumpus.
	 */
	private void markWumpus() {
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
				if (isValid(new_x, new_y) && state[new_x][new_y] == -1) {
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
		// initialise state to -1
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				state[i][j] = -1;
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

		if (breeze) {
			markPit();
		}
		if (glitter) {
			markGold();
		}
		if (stench) {
			markWumpus();
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
		int nextMove = new Search(state, x_coordinate, y_coordinate, direction).nextMove();
		updateAgent(nextMove);
		// return action to be performed
	    return nextMove;
	}
	
	// public method to return the agent's name
	// do not remove this method
	public String getAgentName() {
		return agentName;
	}
}