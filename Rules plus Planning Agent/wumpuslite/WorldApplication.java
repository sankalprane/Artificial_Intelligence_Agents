/*
 * Wumpus-Lite, version 0.18 alpha
 * A lightweight Java-based Wumpus World Simulator
 * 
 * Written by James P. Biagioni (jbiagi1@uic.edu)
 * for CS511 Artificial Intelligence II
 * at The University of Illinois at Chicago
 * 
 * Thanks to everyone who provided feedback and
 * suggestions for improving this application,
 * especially the students from Professor
 * Gmytrasiewicz's Spring 2007 CS511 class.
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
import java.io.FileWriter;
import java.util.Random;

class WorldApplication {
	
	private static String VERSION = "v0.18a";

	public static void main (String args[]) {
		
		int worldSize = 4;
		int numTrials = 1;
		int maxSteps = 50;
		
		boolean nonDeterministicMode = true;
		boolean randomAgentLoc = true;
		boolean userDefinedSeed = false;
		
		String outFilename = "wumpus_out.txt";
		
	    Random rand = new Random();
	    int seed = rand.nextInt();
		
	    
		// iterate through command-line parameters
	    for (int i = 0; i < args.length; i++) {
	    	
	    	String arg = args[i];
	    	
	    	// if the world dimension is specified
	    	if (arg.equals("-d") == true) {
	    		
	    		if (Integer.parseInt(args[i+1]) > 1) {	    		
	    		
	    			worldSize = Integer.parseInt(args[i+1]);
	    		}
	    		
	    		i++;
	    	}	    	
	    	// if the maximum number of steps is specified
	    	else if (arg.equals("-s") == true) {
	    		
	    		maxSteps = Integer.parseInt(args[i+1]);
	    		i++;
	    		
	    	}	    	
	    	// if the number of trials is specified
	    	else if (arg.equals("-t") == true) {
	    		
	    		numTrials = Integer.parseInt(args[i+1]);
	    		i++;
	    		
	    	}
	    	// if the random agent location value is specified
	    	else if (arg.equals("-a") == true) {
	    		
	    		randomAgentLoc = Boolean.parseBoolean(args[i+1]);
	    		i++;
	    		
	    	}
	    	// if the random number seed is specified
	    	else if (arg.equals("-r") == true) {
	    		
	    		seed = Integer.parseInt(args[i+1]);
	    		userDefinedSeed = true;
	    		i++;
	    		
	    	}
	    	// if the output filename is specified
	    	else if (arg.equals("-f") == true) {
	    		
	    		outFilename = String.valueOf(args[i+1]);
	    		i++;
	    		
	    	}
	    	// if the non-determinism is specified
	    	else if (arg.equals("-n") == true) {
	    		
	    		nonDeterministicMode = Boolean.parseBoolean(args[i+1]);
	    		i++;
	    		
	    	}
	    	
	    }
	    	    
	    try {
	    	
		    BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outFilename));
	    
			System.out.println("Wumpus-Lite " + VERSION + "\n");
			outputWriter.write("Wumpus-Lite " + VERSION + "\n\n");
			
			System.out.println("Dimensions: " + worldSize + "x" + worldSize);
		    outputWriter.write("Dimensions: " + worldSize + "x" + worldSize + "\n");
			
		    System.out.println("Maximum number of steps: " + maxSteps);
		    outputWriter.write("Maximum number of steps: " + maxSteps + "\n");
		    
		    System.out.println("Number of trials: " + numTrials);
		    outputWriter.write("Number of trials: " + numTrials + "\n");
		    
		    System.out.println("Random Agent Location: " + randomAgentLoc);
		    outputWriter.write("Random Agent Location: " + randomAgentLoc + "\n");
		    
			System.out.println("Random number seed: " + seed);
			outputWriter.write("Random number seed: " + seed + "\n");		    	
		    
		    System.out.println("Output filename: " + outFilename);
		    outputWriter.write("Output filename: " + outFilename + "\n");
		    
		    System.out.println("Non-Deterministic Behavior: " + nonDeterministicMode + "\n");
		    outputWriter.write("Non-Deterministic Behavior: " + nonDeterministicMode + "\n\n");
		    
		    
		    char[][][] wumpusWorld = generateRandomWumpusWorld(seed, worldSize, randomAgentLoc);
		    Environment wumpusEnvironment = new Environment(worldSize, wumpusWorld, outputWriter);
		    
		    int trialScores[] = new int[numTrials];
		    int totalScore = 0;
	    
		    for (int currTrial = 0; currTrial < numTrials; currTrial++) {
		    		    	
		    	Simulation trial = new Simulation(wumpusEnvironment, maxSteps, outputWriter, nonDeterministicMode);
		    	trialScores[currTrial] = trial.getScore();
		    	
		    	System.out.println("\n\n___________________________________________\n");
		    	outputWriter.write("\n\n___________________________________________\n\n");
		    	
			    if (userDefinedSeed == true) {
			    	wumpusWorld = generateRandomWumpusWorld(++seed, worldSize, randomAgentLoc);	
			    }
			    else {
			    	wumpusWorld = generateRandomWumpusWorld(rand.nextInt(), worldSize, randomAgentLoc);
			    }

			    wumpusEnvironment = new Environment(worldSize, wumpusWorld, outputWriter);

		    	System.runFinalization();
		    }

		    for (int i = 0; i < numTrials; i++) {
		    	
		    	System.out.println("Trial " + (i+1) + " score: " + trialScores[i]);
		    	outputWriter.write("Trial " + (i+1) + " score: " + trialScores[i] + "\n");
		    	totalScore += trialScores[i];
		    	
		    }
		    
		    System.out.println("\nTotal Score: " + totalScore);
		    outputWriter.write("\nTotal Score: " + totalScore + "\n");
		    
		    System.out.println("Average Score: " + ((double)totalScore/(double)numTrials));
		    outputWriter.write("Average Score: " + ((double)totalScore/(double)numTrials) + "\n");
		    
		    outputWriter.close();
		    
	    }
	    catch (Exception e) {
	    	System.out.println("An exception was thrown: " + e);
	    }
	    
	    System.out.println("\nFinished.");	    
	}
	
	public static char[][][] generateRandomWumpusWorld(int seed, int size, boolean randomlyPlaceAgent) {
		
		char[][][] newWorld = new char[size][size][4];
		boolean[][] occupied = new boolean[size][size];
		
		int x, y;
		
		Random randGen = new Random(seed);

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				for (int k = 0; k < 4; k++) {
					newWorld[i][j][k] = ' '; 
				}
			}
		}
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				occupied[i][j] = false;
			}
		}
	     
		int pits = 2;
		
		// default agent location
		// and orientation
		int agentXLoc = 0;
		int agentYLoc = 0;
		char agentIcon = '>';
		
		// randomly generate agent
		// location and orientation
		if (randomlyPlaceAgent == true) {
			
			agentXLoc = randGen.nextInt(size);
			agentYLoc = randGen.nextInt(size);
			
			switch (randGen.nextInt(4)) {
				
				case 0: agentIcon = 'A'; break;
				case 1: agentIcon = '>'; break;
				case 2: agentIcon = 'V'; break;
				case 3: agentIcon = '<'; break;
			}
		}
		
		// place agent in the world
		newWorld[agentXLoc][agentYLoc][3] = agentIcon;
	     
		// Pit generation
		for (int i = 0; i < pits; i++) {
	     
			x = randGen.nextInt(size);
			y = randGen.nextInt(size);
		     
			while ((x == agentXLoc && y == agentYLoc) | occupied[x][y] == true) {
				x = randGen.nextInt(size);
				y = randGen.nextInt(size);    	   
			}
		     
			occupied[x][y] = true;

			newWorld[x][y][0] = 'P';
		     
		}

		// Wumpus Generation
		x = randGen.nextInt(size);
		y = randGen.nextInt(size);
	     
		while (x == agentXLoc && y == agentYLoc) {
			x = randGen.nextInt(size);
			y = randGen.nextInt(size);   
		}
	     
		occupied[x][y] = true;
	     
		newWorld[x][y][1] = 'W';
		
		// Gold Generation
		x = randGen.nextInt(size);
		y = randGen.nextInt(size);
	     
		//while (x == 0 && y == 0) {
		//	x = randGen.nextInt(size);
		//	y = randGen.nextInt(size);    	   
		//}
	     
		occupied[x][y] = true;
	     
		newWorld[x][y][2] = 'G';
		
		return newWorld;
	}
	
}