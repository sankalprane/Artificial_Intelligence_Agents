/*
 * Class that handles transferring the percepts
 * from the environment to the agent function.
 * 
 * Written by James P. Biagioni (jbiagi1@uic.edu)
 * for CS511 Artificial Intelligence II
 * at The University of Illinois at Chicago
 * 
 * Last modified 1/31/07 
 * 
 * DISCLAIMER:
 * Elements of this application were borrowed from
 * the client-server implementation of the Wumpus
 * World Simulator written by Kruti Mehta at
 * The University of Texas at Arlington.
 * 
 */

class TransferPercept {
	
	private Environment environment;
		
	public TransferPercept(Environment wumpusEnvironment) {
		
		environment = wumpusEnvironment;
		
	}
	
	public boolean getBump() {
		return environment.getBump();
	}
	
	public boolean getGlitter() {
		return environment.getGlitter();
	}

	public boolean getBreeze() {
		return environment.getBreeze();
	}

	public boolean getStench() {
		return environment.getStench();
	}
	
	public boolean getScream() {
		return environment.getScream();
	}
	
}