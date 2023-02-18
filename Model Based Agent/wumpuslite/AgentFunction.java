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

	public AgentFunction()
	{
		// initialise state to 0
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				state[i][j] = -1;
				visited[i][j] = -1;
			}
		}
		state[0][0] = 0;
		visited[0][0] = 1;
	}

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

	private boolean isValid(int x, int y) {
		if (x >= 0 && x < 4 && y >= 0 && y < 4)
			return true;
		return false;
	}

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

	private void markPit() {
		for (int i = 0; i < 4; i++) {
			int new_x = x_coordinate + dir_row[i];
			int new_y = y_coordinate + dir_col[i];
			if (isValid(new_x, new_y) && state[new_x][new_y] != 0) {
				state[new_x][new_y] = 1;
			}
		}
	}

	private void markWumpus() {
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

	private void updatePitInfo() {
		for (int i = 0; i < 4; i++) {
			int new_x = x_coordinate + dir_row[i];
			int new_y = y_coordinate + dir_col[i];
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

	public int chooseTurnDirection(int x, int y) {
		System.out.println("chooseTurnDirection() func");
		System.out.println("x:" + x + " y:" + y);
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
		//left side of edge
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
		// Right side of Board
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
		// Top of the board
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
		// Bottom of the board
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
		if (numberOfSafePlacesToVisit() == 0) {
			return Action.NO_OP;
		}
		if (isValid(new_x, new_y) && visited[new_x][new_y] == 0) {
			x_coordinate = new_x;
			y_coordinate = new_y;
			visited[new_x][new_y] = 1;
			return Action.GO_FORWARD;
		}
		if (turn) {
			turn = false;
			return chooseTurnDirection(x_coordinate, y_coordinate);
		}
		if (isValid(new_x, new_y) && state[new_x][new_y] == 0) {
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
		System.out.print("new x:" + new_x+ "new y:" + new_y);
		if (isValid(new_x, new_y)) {
			if (visited[new_x][new_y] == 0) {
				x_coordinate = new_x;
				y_coordinate = new_y;
				visited[new_x][new_y] = 1;
				return Action.GO_FORWARD;
			}
			//special case if wumpus is in 1, 1 and arrow is used
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