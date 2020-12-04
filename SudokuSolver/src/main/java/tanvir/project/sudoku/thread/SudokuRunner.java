package tanvir.project.sudoku.thread;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tanvir.project.sudoku.Bean;
import tanvir.project.sudoku.engine.SudokuEngine;

/**
 * The Runnable class for parallelizing the solving of a sudoku puzzle.
 * When the Runner instance finds a solution to the puzzle, it outputs it to a queue and terminates.
 * The Runner also terminates if it has exhausted all possible cases. 
 * @author Tanvir Kaykobad
 *
 */
public class SudokuRunner implements Runnable{
	private static final Logger LOGGER = LogManager.getLogger(Runnable.class);
	
	/**
	 * A pool of tasks for all the threads to poll tasks from.
	 */
	private ConcurrentLinkedQueue<Character[][]> taskList;
	
	/**
	 * Whenever a thread finds a solution, it is added here.
	 */
	private List<Character[][]> solutionList;
	
	/**
	 * If a thread finds a solution, the backing bean updates this value to be true so all threads can stop running.
	 */
	private boolean stopRequested = false;
	
	/**
	 * An ID for this instance.
	 */
	private int runnerId;
	
	/**
	 * This value is used by the backing bean to determine if all threads have stopped, in which case solutionList being empty
	 * means the sudoku puzzle has no solution.
	 */
	private boolean isRunning = false;

	/**
	 * @param taskList The thread pulls a task (sudoku board) to solve whenever it finishes its current work
	 * @param solution The runner updates this object with the solution sudoku map
	 * @param runnerId An ID for the runner
	 */
	public SudokuRunner(ConcurrentLinkedQueue<Character[][]> taskList, List<Character[][]> solutionList, int runnerId) {
		super();
		this.taskList = taskList;
		this.solutionList = solutionList;
		this.runnerId=runnerId;
	}

	/**
	 * This is where the thread works.
	 */
	@Override
	public void run() {
		isRunning = true;
		LOGGER.info("Runner "+runnerId+" has started");
		SudokuEngine engine;
		while(!stopRequested) {
			//fetching task
			Character[][] map;
			synchronized(this) { //synchronized because modifying taskList
				if(!taskList.isEmpty()){
					map= taskList.poll();
				} else {
					LOGGER.info("No job found. Runner "+runnerId+" has terminated");
					isRunning = false;
					return;
				}
			}

			//finding solution
			engine = new SudokuEngine(map);			
			Character[][] mySolution = engine.getSolution();
			
			//If solution is found, add it to the list and return
			if(mySolution != null) {
				synchronized(this){	//synchronized because modifying solutionList
					solutionList.add(mySolution);
					LOGGER.info("Solution found. Runner "+runnerId+" has terminated");
					isRunning = false;
					return;
				}
			}
		}
		LOGGER.info("Runner "+runnerId+" has terminated");
		isRunning = false;
	}

	/**
	 * To request this thread to stop.
	 * @param stopRequested
	 */
	public void setStopRequested(boolean stopRequested) {
		this.stopRequested = stopRequested;
	}

	/**
	 * To find out if the thread is still working.
	 * @return
	 */
	public boolean isRunning() {
		return isRunning;
	}
}
