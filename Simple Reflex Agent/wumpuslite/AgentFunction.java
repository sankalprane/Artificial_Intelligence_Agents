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
		// for illustration purposes; you may delete all code
		// inside this constructor when implementing your 
		// own intelligent agent

		// this integer array will store the agent actions
		actionTable = new int[6];
		actionTable[0] = Action.GO_FORWARD;
		actionTable[1] = Action.TURN_RIGHT;
		actionTable[2] = Action.TURN_LEFT;
		actionTable[3] = Action.GRAB;
		actionTable[4] = Action.SHOOT;
		actionTable[5] = Action.NO_OP;
		
		// new random number generator, for
		// randomly picking actions to execute
		rand = new Random();
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
		
		if (glitter) {
			return Action.GRAB;
		}
		if (stench && breeze) {
			return Action.NO_OP;
		}
		if (stench) {
			return Action.SHOOT;
		}
		if (breeze) {
			return Action.NO_OP;
		}
		if (scream) {
			return Action.GO_FORWARD;
		}
		if (bump) {
			return Action.TURN_LEFT;
		}

		// if (bump == true || glitter == true || breeze == true || stench == true || scream == true) {
		// 	// do something...?
		// }
		
		// return action to be performed
	    return Action.GO_FORWARD;    
	}
	
	// public method to return the agent's name
	// do not remove this method
	public String getAgentName() {
		return agentName;
	}
}