/*
 * Class that defines the simulation environment.
 * 
 * Written by James P. Biagioni (jbiagi1@uic.edu)
 * for CS511 Artificial Intelligence II
 * at The University of Illinois at Chicago
 * 
 * Last modified 3/5/07 
 * 
 * DISCLAIMER:
 * Elements of this application were borrowed from
 * the client-server implementation of the Wumpus
 * World Simulator written by Kruti Mehta at
 * The University of Texas at Arlington.
 * 
 */

import java.io.BufferedWriter;

class Simulation {
	
	private int currScore = 0;
	private static int actionCost = -1;
	private static int deathCost = -1000;
	private static int shootCost = -10;
	private int stepCounter = 0;
	private int lastAction = 0;
	
	private boolean simulationRunning;
	
	private Agent agent;
	private Environment environment;
	private TransferPercept transferPercept;
	private BufferedWriter outputWriter;
	
	public Simulation(Environment wumpusEnvironment, int maxSteps, BufferedWriter outWriter, boolean nonDeterministic) {
		
		// start the simulator
		simulationRunning = true;
		
		outputWriter = outWriter;
		transferPercept = new TransferPercept(wumpusEnvironment);
		environment = wumpusEnvironment;
		
		agent = new Agent(environment, transferPercept, nonDeterministic);
		
		environment.placeAgent(agent);
		environment.printEnvironment();
		
		printCurrentPerceptSequence();
		
		try {
		
			System.out.println("Current score: " + currScore);
			outputWriter.write("Current score: " + currScore + "\n");
			
			while (simulationRunning == true && stepCounter < maxSteps) {
				
				System.out.println("Last action: " + Action.printAction(lastAction));
				outputWriter.write("Last action: " + Action.printAction(lastAction) + "\n");
				
				System.out.println("Time step: " + stepCounter);
				outputWriter.write("Time step: " + stepCounter + "\n");
				
				handleAction(agent.chooseAction());
				wumpusEnvironment.placeAgent(agent);
				
				environment.printEnvironment();								
				printCurrentPerceptSequence();
								
				System.out.println("Current score: " + currScore);
				outputWriter.write("Current score: " + currScore + "\n");
				
				//Scanner in = new Scanner(System.in);
				//in.next();
				
				stepCounter += 1;
				
				if (stepCounter == maxSteps || simulationRunning == false) {
					System.out.println("Last action: " + Action.printAction(lastAction));
					outputWriter.write("Last action: " + Action.printAction(lastAction) + "\n");
					
					System.out.println("Time step: " + stepCounter);
					outputWriter.write("Time step: " + stepCounter + "\n");
					
					lastAction = Action.END_TRIAL;
				}
				
				if (agent.getHasGold() == true) {
					System.out.println("\n" + agent.getName() + " found the GOLD!!");
					outputWriter.write("\n" + agent.getName() + " found the GOLD!!\n");
				}
				if (agent.getIsDead() == true) {
					System.out.println("\n" + agent.getName() + " is DEAD!!");
					outputWriter.write("\n" + agent.getName() + " is DEAD!!\n");	
				}
				
			}
			
		}
		catch (Exception e) {
			System.out.println("An exception was thrown: " + e);
		}		
		
		printEndWorld();
		
	}
	
	public void printEndWorld() {
		
		try {
			
			environment.printEnvironment();
			
			System.out.println("Final score: " + currScore);
			outputWriter.write("Final score: " + currScore + "\n");
			
			System.out.println("Last action: " + Action.printAction(lastAction));
			outputWriter.write("Last action: " + Action.printAction(lastAction) + "\n");

		}
		catch (Exception e) {
			System.out.println("An exception was thrown: " + e);
		}
		
	}
	
	public void printCurrentPerceptSequence() {
		
		try {
		
			System.out.print("Percept: <");	
			outputWriter.write("Percept: <");
			
			if (transferPercept.getBump() == true) {
				System.out.print("bump,");
				outputWriter.write("bump,");
			}
			else if (transferPercept.getBump() == false) {
				System.out.print("none,");
				outputWriter.write("none,");
			}
			if (transferPercept.getGlitter() == true) {
				System.out.print("glitter,");
				outputWriter.write("glitter,");
			}
			else if (transferPercept.getGlitter() == false) {
				System.out.print("none,");
				outputWriter.write("none,");
			}
			if (transferPercept.getBreeze() == true) {
				System.out.print("breeze,");
				outputWriter.write("breeze,");
			}
			else if (transferPercept.getBreeze() == false) {
				System.out.print("none,");
				outputWriter.write("none,");
			}
			if (transferPercept.getStench() == true) {
				System.out.print("stench,");
				outputWriter.write("stench,");
			}
			else if (transferPercept.getStench() == false) {
				System.out.print("none,");
				outputWriter.write("none,");
			}
			if (transferPercept.getScream() == true) {
				System.out.print("scream>\n");
				outputWriter.write("scream>\n");
			}
			else if (transferPercept.getScream() == false) {
				System.out.print("none>\n");
				outputWriter.write("none>\n");
			}
		
		}
		catch (Exception e) {
			System.out.println("An exception was thrown: " + e);
		}
		
	}
	
	public void handleAction(int action) {
		
		try {
		
			if (action == Action.GO_FORWARD) {
				
				if (environment.getBump() == true) environment.setBump(false);
				
				agent.goForward();
				environment.placeAgent(agent);
				
				if (environment.checkDeath() == true) {
					
					currScore += deathCost;
					simulationRunning = false;
					
					agent.setIsDead(true);
				}
				else {
					currScore += actionCost;
				}
				
				if (environment.getScream() == true) environment.setScream(false);
				
				lastAction = Action.GO_FORWARD;
			}
			else if (action == Action.TURN_RIGHT) {
				
				currScore += actionCost;
				agent.turnRight();		
				environment.placeAgent(agent);
				
				if (environment.getBump() == true) environment.setBump(false);
				if (environment.getScream() == true) environment.setScream(false);
				
				lastAction = Action.TURN_RIGHT;
			}
			else if (action == Action.TURN_LEFT) {
				
				currScore += actionCost;
				agent.turnLeft();		
				environment.placeAgent(agent);
				
				if (environment.getBump() == true) environment.setBump(false);
				if (environment.getScream() == true) environment.setScream(false);
				
				lastAction = Action.TURN_LEFT;
			}
			else if (action == Action.GRAB) {
				
				if (environment.grabGold() == true) {
					
					currScore += 1000;
					simulationRunning = false;
					
					agent.setHasGold(true);
				}
				else currScore += actionCost;
				
				environment.placeAgent(agent);
				
				if (environment.getBump() == true) environment.setBump(false);
				if (environment.getScream() == true) environment.setScream(false);
				
				lastAction = Action.GRAB;
			}
			else if (action == Action.SHOOT) {
				
				if (agent.shootArrow() == true) {
					
					if (environment.shootArrow() == true) environment.setScream(true);
				
					currScore += shootCost;					
				}
				else {
					
					if (environment.getScream() == true) environment.setScream(false);
					
					currScore += actionCost;
				}
				
				environment.placeAgent(agent);
				
				if (environment.getBump() == true) environment.setBump(false);
				
				lastAction = Action.SHOOT;
			}
			else if (action == Action.NO_OP) {
				
				environment.placeAgent(agent);
				
				if (environment.getBump() == true) environment.setBump(false);
				if (environment.getScream() == true) environment.setScream(false);
				
				lastAction = Action.NO_OP;
			}
			
		}
		catch (Exception e) {
			System.out.println("An exception was thrown: " + e);
		}
	}
	
	public int getScore() {
		
		return currScore;
		
	}
	
}