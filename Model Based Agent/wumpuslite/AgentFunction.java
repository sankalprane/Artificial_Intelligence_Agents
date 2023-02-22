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

import java.util.Random;


class AgentFunction {


	// string to store the agent's name
	// do not remove this variable
	private String agentName = "Agent Smith";

	private boolean turn = false;
	private int[][] state = new int[4][4];
	private int[][] visited = new int[4][4];
	private int x_coordinate = 0;
	private int y_coordinate = 0;
	private char direction = 'E';
	private int[] dir_row = {1, -1, 0, 0};
	private int[] dir_col = {0, 0, 1, -1};
	private boolean arrow = true;
	private int reverse = 0;
	private boolean firstTimeStench = true;
	
	// all of these variables are created and used
	// for illustration purposes; you may delete them
	// when implementing your own intelligent agent
	private int[] actionTable;
	private boolean bump;
	private boolean glitter;
	private boolean breeze;
	private boolean stench;
	private boolean scream;
	private Random rand;

	// If state is -1 it means nothing is known about the state of the puzzle
	public AgentFunction()
	{
		// initialise state to -1
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				state[i][j] = -1;
				visited[i][j] = -1;
			}
		}
		/*
		 * If the state is 0 then the square is safe.
		 * Mark the 0, 0 square as safe and visited
		 */
		state[0][0] = 0;
		visited[0][0] = 1;
	}

	/*
	 * This function returns if there is a safe place that is still
	 * not visited. If there is no such place which is safe then the
	 * agent can do NO-OP.
	 */
	public int numberOfSafePlacesToVisit() {
		int cnt = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (visited[i][j] == 0)
					cnt++;
			}
		}
		return cnt;
	}

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
	 * When there is no percept marks all the valid squares nearby as
	 * safe
	 */
	private void markSafe() {
		for (int i = 0; i < 4; i++) {
			int new_x = x_coordinate + dir_row[i];
			int new_y = y_coordinate + dir_col[i];
			if (isValid(new_x, new_y)) {
				state[new_x][new_y] = 0;
				if (visited[new_x][new_y] == -1)
					visited[new_x][new_y] = 0;
			}
		}
	}

	/*
	 * If breeze is felt then set the state to 1 which indicates a pit
	 */
	private void markPit() {
		for (int i = 0; i < 4; i++) {
			int new_x = x_coordinate + dir_row[i];
			int new_y = y_coordinate + dir_col[i];
			if (isValid(new_x, new_y) && state[new_x][new_y] != 0) {
				state[new_x][new_y] = 1;
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
				if (isValid(new_x, new_y) && state[new_x][new_y] != 0) {
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
					if (visited[new_x][new_y] == -1)
						visited[new_x][new_y] = 0;
				}
			}
		}

	}

	private void markPitAndWumpus() {
		for (int i = 0; i < 4; i++) {
			int new_x = x_coordinate + dir_row[i];
			int new_y = y_coordinate + dir_col[i];
			if (isValid(new_x, new_y) && state[new_x][new_y] != 0) {
				state[new_x][new_y] = 3;
			}
		}
	}

	// This function is executed on Scream
	private void removeWumpus() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (state[i][j] == 2) {
					state[i][j] = 0;
					if (visited[i][j] == -1)
						visited[i][j] = 0;
				}
			}
		}
	}

	/*
	 * This function updates the position of the pit of when the breeze 
	 * is not felt from a different square
	 */
	private void updatePitInfo() {
		for (int i = 0; i < 4; i++) {
			int new_x = x_coordinate + dir_row[i];
			int new_y = y_coordinate + dir_col[i];
			/*
			 * Example: If the breeze is felt at 2,1. The pit can be
			 * at 2,2 or 3,1. But if the breeze is not felt at 1,2.
			 * Then we can mark the square 2,2 as safe.
			 */
			if (isValid(new_x, new_y) && state[new_x][new_y] == 1) {
				state[new_x][new_y] = 0;
				if (visited[new_x][new_y] == -1)
					visited[new_x][new_y] = 0;
			}
			if (isValid(new_x, new_y) && state[new_x][new_y] == 3) {
				state[new_x][new_y] = 2;
			}
		}
	}


	/*
	 * This updates the position of the wumpus
	 */
	private void updateWumpusInfo() {
		for (int i = 0; i < 4; i++) {
			int new_x = x_coordinate + dir_row[i];
			int new_y = y_coordinate + dir_col[i];
			if (isValid(new_x, new_y) && state[new_x][new_y] == 2) {
				state[new_x][new_y] = 0;
				if (visited[new_x][new_y] == -1)
					visited[new_x][new_y] = 0;
			}
			if (isValid(new_x, new_y) && state[new_x][new_y] == 3) {
				state[new_x][new_y] = 1;
			}
		}
	}

	 
	// This function is used to update the direction on turn left
	public void updateDirectionOnLeftMove() {
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
	public void updateDirectionOnRightMove() {
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

	/*
	 * This function is used to choose the direction when we want to 
	 * take a turn.
	 */
	public int chooseTurnDirection(int x, int y) {
		/*
		 * At bottom right corner if agent is facing east then turn left,
		 * if agent is facing south the turn right 
		 */
		if (x == 0 && y == 3) {
			if (direction == 'E') {
				updateDirectionOnLeftMove();
				return Action.TURN_LEFT;
			}
			if (direction == 'S') {
				updateDirectionOnRightMove();
				return Action.TURN_RIGHT;
			}
		}
		/*
		 * At bottom left corner if agent is facing west then turn right,
		 * if agent is facing south the turn left. 
		 */
		if (x == 0 && y == 0) {
			if (direction == 'W') {
				updateDirectionOnRightMove();
				return Action.TURN_RIGHT;
			}
			if (direction == 'S') {
				updateDirectionOnLeftMove();
				return Action.TURN_LEFT;
			}
		}
		/*
		 * At top right corner if agent is facing north then turn left,
		 * if agent is facing east the turn right. 
		 */
		if (x == 3 && y == 3) {
			if (direction == 'N') {
				updateDirectionOnLeftMove();
				return Action.TURN_LEFT;
			}
			if (direction == 'E') {
				updateDirectionOnRightMove();
				return Action.TURN_RIGHT;
			}
		}
		/*
		 * At top left corner if agent is facing north then turn right,
		 * if agent is facing west the turn left. 
		 */
		if (x == 3 && y == 0) {
			if (direction == 'N') {
				updateDirectionOnRightMove();
				return Action.TURN_RIGHT;
			}
			if (direction == 'W') {
				updateDirectionOnLeftMove();
				return Action.TURN_LEFT;
			}
		}
		// When agent facing west, check if north and south square are safe and not visited
		if (direction == 'W') {
			// North check 
			if (isValid(x + 1, y)) {
				if (visited[x + 1][y] == 0) {
					updateDirectionOnRightMove();
					return Action.TURN_RIGHT;
				}
			}
			// South check
			if (isValid(x - 1, y)) {
				if (visited[x - 1][y] == 0) {
					updateDirectionOnLeftMove();
					return Action.TURN_LEFT;
				}
			}
		}
		// When agent facing East check if north and south are safe and not visited
		if (direction == 'E') {
			// North check 
			if (isValid(x + 1, y)) {
				if (visited[x + 1][y] == 0) {
					updateDirectionOnLeftMove();
					return Action.TURN_LEFT;
				}
			}
			// South check
			if (isValid(x - 1, y)) {
				if (visited[x - 1][y] == 0) {
					updateDirectionOnRightMove();
					return Action.TURN_RIGHT;
				}
			}
		}
		// When agent facing North check if west and east are safe and not visited
		if (direction == 'N') {
			// West check 
			if (isValid(x, y - 1)) {
				if (visited[x][y - 1] == 0) {
					updateDirectionOnLeftMove();
					return Action.TURN_LEFT;
				}
			}
			// East check
			if (isValid(x, y + 1)) {
				if (visited[x][y + 1] == 0) {
					updateDirectionOnRightMove();
					return Action.TURN_RIGHT;
				}
			}
		}
		// When agent facing South check if west and east are safe and not visited
		if (direction == 'S') {
			// West check 
			if (isValid(x, y - 1)) {
				if (visited[x][y - 1] == 0) {
					updateDirectionOnRightMove();
					return Action.TURN_RIGHT;
				}
			}
			// East check
			if (isValid(x, y + 1)) {
				if (visited[x][y + 1] == 0) {
					updateDirectionOnLeftMove();
					return Action.TURN_LEFT;
				}
			}
		}
		//left side of grid
		if (y == 0) {
			if (direction == 'N') {
				updateDirectionOnRightMove();
				return Action.TURN_RIGHT;
			}
			if (direction == 'S') {
				updateDirectionOnLeftMove();
				return Action.TURN_LEFT;
			}
		}
		// Right side of grid
		if (y == 3) {
			if (direction == 'N') {
				updateDirectionOnLeftMove();
				return Action.TURN_LEFT;
			}
			if (direction == 'S') {
				updateDirectionOnRightMove();
				return Action.TURN_RIGHT;
			}
		}
		// Top of the grid
		if (x == 3) {
			if (direction == 'E') {
				updateDirectionOnRightMove();
				return Action.TURN_RIGHT;
			}
			if (direction == 'W') {
				updateDirectionOnLeftMove();
				return Action.TURN_LEFT;
			}
		}
		// Bottom of the grid
		if (x == 0) {
			if (direction == 'W') {
				updateDirectionOnRightMove();
				return Action.TURN_RIGHT;
			}
			if (direction == 'E') {
				updateDirectionOnLeftMove();
				return Action.TURN_LEFT;
			}
		}
		updateDirectionOnRightMove();
		return Action.TURN_RIGHT;
	}

	/*
	 * This function checks if agent can move forward safely
	 */
	public int checkForwardMove() {
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
		 * If there are no safe places to visit perform NO-OP
		 */
		if (numberOfSafePlacesToVisit() == 0) {
			return Action.NO_OP;
		}
		/*
		 * If the forward move is safe and the square in the front is 
		 * not visited then move forward
		 */
		if (isValid(new_x, new_y) && visited[new_x][new_y] == 0) {
			x_coordinate = new_x;
			y_coordinate = new_y;
			visited[new_x][new_y] = 1;
			return Action.GO_FORWARD;
		}
		/*
		 * When the forward square is already visited, Turn will be true.
		 * This will help the agent to explore all the places not visited
		 * yet. The chooseTurnDirection function helps the agent to choose
		 * the direction of the turn. 
		 */
		if (turn) {
			turn = false;
			return chooseTurnDirection(x_coordinate, y_coordinate);
		}
		// Checks if the forward move is safe
		if (isValid(new_x, new_y) && state[new_x][new_y] == 0) {
			/*
			 * If the forward square is not visited. Then go forward
			 * else set the Turn to True to explore other squares and 
			 * then perform move forward
			 */
			if (visited[new_x][new_y] == 0) {
				x_coordinate = new_x;
				y_coordinate = new_y;
				visited[new_x][new_y] = 1;
				return Action.GO_FORWARD;
			} else if (visited[new_x][new_y] == 1 && !turn) {
				x_coordinate = new_x;
				y_coordinate = new_y;
				visited[new_x][new_y] = 1;
				turn = true;
				return Action.GO_FORWARD;
			} else {
				if (numberOfSafePlacesToVisit() == 0)
				 	return Action.NO_OP;
				updateDirectionOnRightMove();
				return Action.TURN_RIGHT;
			}
		}
		if (numberOfSafePlacesToVisit() == 0)
			return Action.NO_OP;
		/*
		 * If forward move is not safe, then take a Turn by using
		 * chooseTurnDirection Function
		 */
		if (!isValid(new_x, new_y)) {
			return chooseTurnDirection(x_coordinate, y_coordinate);
		}
		updateDirectionOnRightMove();
		return Action.TURN_RIGHT;
	}

	public int onStench() {
		System.out.println("onStench() func");
		// new_x and new_y are coordinates of next square in forward direction
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

		if (isValid(new_x, new_y)) {
			/*
			 * After updating the wumpus position with the updateWumpusInfo 
			 * function if we are sure the wumpus is not in the forward
			 * square and the square is still not visited then go forward
			 */
			if (visited[new_x][new_y] == 0) {
				x_coordinate = new_x;
				y_coordinate = new_y;
				visited[new_x][new_y] = 1;
				return Action.GO_FORWARD;
			}
			// special case if wumpus is in 1, 1 and arrow is used
			if (new_x == 0 && new_y == 0 && direction == 'W') {
				x_coordinate = new_x;
				y_coordinate = new_y;
				return Action.GO_FORWARD;
			}
		}

		// If no array wumpus is same as pit
		if (!arrow) {
			return onBreeze();
		} 

		// This is to SHOOT IN CORRECT DIRECTION IN CORNERS
		int x = x_coordinate;
		int y = y_coordinate;
		if (x == 0 && y == 3) {
			if (direction == 'E') {
				updateDirectionOnLeftMove();
				return Action.TURN_LEFT;
			}
			if (direction == 'S') {
				updateDirectionOnRightMove();
				return Action.TURN_RIGHT;
			}
		}
		if (x == 0 && y == 0) {
			if (direction == 'W') {
				updateDirectionOnRightMove();
				return Action.TURN_RIGHT;
			}
			if (direction == 'S') {
				updateDirectionOnLeftMove();
				return Action.TURN_LEFT;
			}
		}
		if (x == 3 && y == 3) {
			if (direction == 'N') {
				updateDirectionOnLeftMove();
				return Action.TURN_LEFT;
			}
			if (direction == 'E') {
				updateDirectionOnRightMove();
				return Action.TURN_RIGHT;
			}
		}
		if (x == 3 && y == 0) {
			if (direction == 'N') {
				updateDirectionOnRightMove();
				return Action.TURN_RIGHT;
			}
			if (direction == 'W') {
				updateDirectionOnLeftMove();
				return Action.TURN_LEFT;
			}
		}
		//left side of edge
		if (y == 0) {
			if (direction == 'W') {
				updateDirectionOnRightMove();
				return Action.TURN_RIGHT;
			}
		}
		// Right side of Board
		if (y == 3) {
			if (direction == 'E') {
				updateDirectionOnLeftMove();
				return Action.TURN_LEFT;
			}
		}
		// Top of the board
		if (x == 3) {
			if (direction == 'N') {
				updateDirectionOnRightMove();
				return Action.TURN_RIGHT;
			}
		}
		// Bottom of the board
		if (x == 0) {
			if (direction == 'S') {
				updateDirectionOnLeftMove();
				return Action.TURN_LEFT;
			}
		}
		// Shoot the arrow and clear the state in the square in front
		if (isValid(new_x, new_y)) {
			if (state[new_x][new_y] == 2) {
				state[new_x][new_y] = 0;
				visited[new_x][new_y] = 0;
			}
		}

		arrow = false;
		return Action.SHOOT;
	}

	public int onBreeze() {
		System.out.println("onBreeze() func");

		// coordinates of the square in the forward direction
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

		if (numberOfSafePlacesToVisit() == 0)
			return Action.NO_OP;
		// Go forward if the breeze is not in forward direction
		if (isValid(new_x, new_y) && visited[new_x][new_y] == 0) {
			visited[new_x][new_y] = 1;
			x_coordinate = new_x;
			y_coordinate = new_y;
			return Action.GO_FORWARD;
		}
		// visit the unvisited place
		int x = x_coordinate;
		int y = y_coordinate;
		// When agent facing west, check if north and south square are safe and not visited
		if (direction == 'W') {
			// North check 
			if (isValid(x + 1, y)) {
				if (visited[x + 1][y] == 0) {
					updateDirectionOnRightMove();
					return Action.TURN_RIGHT;
				}
			}
			// South check
			if (isValid(x - 1, y)) {
				if (visited[x - 1][y] == 0) {
					updateDirectionOnLeftMove();
					return Action.TURN_LEFT;
				}
			}
		}
		// When agent facing East check if north and south are safe and not visited
		if (direction == 'E') {
			// North check 
			if (isValid(x + 1, y)) {
				if (visited[x + 1][y] == 0) {
					updateDirectionOnLeftMove();
					return Action.TURN_LEFT;
				}
			}
			// South check
			if (isValid(x - 1, y)) {
				if (visited[x - 1][y] == 0) {
					updateDirectionOnRightMove();
					return Action.TURN_RIGHT;
				}
			}
		}
		// When agent facing North check if west and east are safe and not visited
		if (direction == 'N') {
			// West check 
			if (isValid(x, y - 1)) {
				if (visited[x][y - 1] == 0) {
					updateDirectionOnLeftMove();
					return Action.TURN_LEFT;
				}
			}
			// East check
			if (isValid(x, y + 1)) {
				if (visited[x][y + 1] == 0) {
					updateDirectionOnRightMove();
					return Action.TURN_RIGHT;
				}
			}
		}
		// When agent facing South check if west and east are safe and not visited
		if (direction == 'S') {
			// West check 
			if (isValid(x, y - 1)) {
				if (visited[x][y - 1] == 0) {
					updateDirectionOnRightMove();
					return Action.TURN_RIGHT;
				}
			}
			// East check
			if (isValid(x, y + 1)) {
				if (visited[x][y + 1] == 0) {
					updateDirectionOnLeftMove();
					return Action.TURN_LEFT;
				}
			}
		}

		/*
		 * This is to help the agent to take a reverse move in case it
		 * feels breeze but is not sure about the position of the pit
		 */
		if (reverse % 3 == 0 || reverse % 3 == 1) {
			reverse++;
			updateDirectionOnRightMove();
			return Action.TURN_RIGHT;
		}
		reverse++;

		if (visited[new_x][new_y] == 1) {
			turn = true;
		}
		x_coordinate = new_x;
		y_coordinate = new_y;
		return Action.GO_FORWARD;
	}

	public int process(TransferPercept tp)
	{
		bump = tp.getBump();
		glitter = tp.getGlitter();
		breeze = tp.getBreeze();
		stench = tp.getStench();
		scream = tp.getScream();
		
		if (glitter) {
			return Action.GRAB;
		}
		if (!bump && !stench && !breeze) {
			System.out.println("no percept");

			markSafe();
			return checkForwardMove();
		}
		if (!breeze) {
			updatePitInfo();
			System.out.println("no breeze");
		}
		if (!stench) {
			updateWumpusInfo();
			System.out.println("no stench");
		}
		if (bump) {
			System.out.println("bump");
			return Action.TURN_LEFT;
		}

		if (breeze && !stench) {
			updateWumpusInfo();
			markPit();
			System.out.println("breeze");
			return onBreeze();
		}
		if (stench && !breeze) {
			updatePitInfo();
			markWumpus();
			System.out.println("stench");
			return onStench();
		}
		if (scream) {
			removeWumpus();
		}
		if (stench && breeze) {
			markPitAndWumpus();
			return checkForwardMove();
		}

		return Action.NO_OP;
	}
	
	// public method to return the agent's name
	// do not remove this method
	public String getAgentName() {
		return agentName;
	}
}