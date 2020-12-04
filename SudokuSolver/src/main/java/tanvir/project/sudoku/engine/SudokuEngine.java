package tanvir.project.sudoku.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tanvir.project.sudoku.Bean;

/**
 * This class solves sudoku puzzles using backtracking. 
 * 
 * @author Tanvir Kaykobad
 *
 */
public class SudokuEngine {
	private static final Logger LOGGER = LogManager.getLogger(SudokuEngine.class);

	
	private int[][] map;
	private long attempt = 0;
	
	/**
	 * Each integer of rulesRow, rulesCol and rules3x3 keep track of the numbers occured so far in its respective row, column or sub-matrix.
	 */
	private List<Integer> rulesRow, rulesCol, rules3x3;
	
	/**
	 * Contructor used by the class SudokuMap for testing
	 * @param map a 2d Character map where each cell is a digit between '1' and '9'.If the cell contains ' ', it means the cell has not yet been filled.
	 */
	public SudokuEngine(int[][] map) {
		super();
		this.map = map.clone();
		
		try {
			isValid();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * Constructor
	 * @param map a 2d Character map where each cell is a digit between '1' and '9'.If the cell contains ' ', it means the cell has not yet been filled.

	 * @throws IllegalArgumentException If the map must be of size 9X9, or if it already breaks one of the row, column or sub-matrix rules.
	 */
	public SudokuEngine(final Character[][] map) throws IllegalArgumentException {
		if(map.length!=9 || (map.length==9 && map[0].length!=9)) {
			throw new IllegalArgumentException("Sudoku Table must be 9x9");
		}
		
		this.map = new int[9][9];
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				if(map[i][j]!=null && (map[i][j]>='1' && map[i][j]<='9')) {
					this.map[i][j]=map[i][j].charValue()-'0';
				} else {
					this.map[i][j]=0;
				}
			}
		}
		
		try {
			isValid();
		} catch (IllegalArgumentException e) {
			throw e;
		}
	}
	
	/**
	 * This method returns the primitive integer 2d array sudoku map saved in the solution engine.
	 * @return an Integer[9][9]
	 */
	private Character[][] getPrimitiveToWrapped(int[][] map) {
		if(map==null)
			return null;
		
		Character[][] solution = new Character[9][9];
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				if(map[i][j]>=1 && map[i][j]<=9) {
					solution[i][j]=(char) (map[i][j]+'0');
				} else {
					solution[i][j]=' ';
				}
			}
		}
		return solution;
	}
	
	/**
	 * This method returns the solution of the sudoku problem
	 * @return The solution of the sudoku puzzle, or null if none exists
	 */
	public Character[][] getSolution(){
		return getPrimitiveToWrapped(solveRecursively());
	}
	
	/**
	 * @return the number of attempts SudokuEngine took for the puzzle, which is the number of times a cell in the matrix was filled.
	 */
	public long getNumberOfAttempts() {
		return this.attempt;
	}
	
	/**
	 * The engine attemps to solves the puzzle. 
	 * @return The solution map. If no solution is found then null is returned.
	 */
	public int[][] solveRecursively(){
		try {
			isValid();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return null;
		}
		
		int[][] solution = solveRecursively(map, rulesRow, rulesCol, rules3x3); 
		LOGGER.info("Number of attempts: "+attempt);
		return solution;
	}
	
	/**
	 * Recursively solves the sudoku board
	 * @param map the sudoku board
	 * @param rulesRow The list of 9 integers tracking the numbers occurred in its respective row 
	 * @param rulesCol The list of 9 integers tracking the numbers occurred in its respective column
	 * @param rules3x3 The list of 9 integers tracking the numbers occurred in its respective 3x3 sub-matrix
	 * @return The solved board, or null
	 */
	private int[][] solveRecursively(final int[][] map, List<Integer> rulesRow, List<Integer> rulesCol, List<Integer> rules3x3){
		if(isMapComplete(map))
			return map;
		
		int[][] solutionMap;
		
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				if(map[i][j]==0) {
					//combined marks all the values that are unavailable to this cell
					int combined = rulesRow.get(i) | rulesCol.get(j) | rules3x3.get((i/3)*3 + j/3);
					//we want the complement of these values marking the values available to this cell
					//mask is used to avoid overflow
					int mask = (1 << 9) -1;
					int available = ~combined & mask;
					if(available==0) {
						return null;
					}
					
					
					for (int bitShift=0; available>0; bitShift++) {
						if((available & 1) > 0) {
							map[i][j]=bitShift+1;
							
							List<Integer> rulesRowNew = new ArrayList<Integer>(rulesRow);
							rulesRowNew.set(i, rulesRowNew.get(i) | 1<<bitShift);
							List<Integer> rulesColNew = new ArrayList<Integer>(rulesCol);
							rulesColNew.set(j, rulesColNew.get(j) | 1<<bitShift);
							List<Integer> rules3x3New = new ArrayList<Integer>(rules3x3);
							rules3x3New.set((i/3)*3 + j/3, rules3x3New.get((i/3)*3 + j/3) | 1<<bitShift);

							attempt += 1;
							
							//output the current state to show the engine is active
							if(attempt%1000==0) {
								outputMap(map);
								LOGGER.debug("Attempt: "+attempt+" Adding "+(bitShift+1) + " to ["+i+"]["+j+"]");
							}
							solutionMap = solveRecursively(map, rulesRowNew, rulesColNew, rules3x3New);
							
							if(solutionMap!=null) {
								return solutionMap;
							}
						} else if(available==0){
							LOGGER.info("No Solution found");
						}
						available = available>>1;								
					}
					map[i][j]=0;
					return null;
				}
			}
		}
		return null;
	}
	
	/**
	 * Checks if the map has been solved
	 * @param map The sudoku map
	 * @return true of the map is complete, false otherwise.
	 */
	private boolean isMapComplete(int[][] map) {
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				if(map[i][j]<1 || map[i][j]>9) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Outputs the map into LOGGER
	 * @param map
	 */
	public static void outputMap(int[][] map) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for(int i=0;i<map.length;i++) {
			for(int j=0;j<map.length;j++) {
				if(map[i][j]!=0){
					sb.append(map[i][j]);
				} else{
					sb.append('.');
				}
			}
			sb.append('\n');
		}
		LOGGER.info(sb.toString());
	}

	
	/**
	 * Brian Kernighan's algorithm for counting the one bits of an integer
	 * @param n the number whose one bits are to be counted
	 * @return The number of set bits in n
	 */
    static int countSetBits(int n) 
    { 
        int count = 0; 
        while (n > 0) { 
            n &= (n - 1); 
            count++; 
        } 
        return count; 
    }
	
    /**
     * This method checks if any of the row, column or sub-matrix rules have been broken by the map
     * @return true if the current map is valid, false otherwise
     * @throws IllegalArgumentException If the map breaks any rules. All broken instances of the rules are specified in the exception.
     */
	public boolean isValid() throws IllegalArgumentException{
		//initializing rules
		rulesRow = new ArrayList<Integer>(9);
		rulesCol = new ArrayList<Integer>(9);
		rules3x3 = new ArrayList<Integer>(9);
		for(int i=0;i<9;i++) {
			rulesRow.add(0);
			rulesCol.add(0);
			rules3x3.add(0);
		}
				
		boolean valid = true;
		
		String error = new String();
		

		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				if(map[i][j]!=0) {
					int shiftBy = map[i][j]-1;
					//check along horizontal
					if(((rulesRow.get(i)>>shiftBy) & 1) > 0) {
//						LOGGER.debug("Row "+i+" has multiple "+ map[i][j]);
						if(!valid) {
							error+=", ";
						}
						error+="Row "+(i+1)+" has multiple "+ map[i][j]+"'s";
						valid = false;
					}

					//check along vertical line
					if(((rulesCol.get(j)>>shiftBy) & 1) > 0) {
//						LOGGER.debug("Col "+j+" has multiple "+ map[i][j]);
						if(!valid) {
							error+=", ";
						}
						error+="Col "+(j+1)+" has multiple "+ map[i][j]+"'s";
						valid = false;
					}

					//check along 3x3 squires line
					if(((rules3x3.get((i/3)*3 + j/3)>>shiftBy) & 1) > 0) {
//						LOGGER.debug("3x3 "+((i/3)*3 + j/3)+" has multiple "+ map[i][j]);
						if(!valid) {
							error+=", ";
						}
						error+="Sub-matrix "+((i/3)*3 + j/3 + 1)+" has multiple "+ map[i][j]+"'s";
						valid = false;
					}

					//update counter in rules
					if(map[i][j]>=1 && map[i][j]<=9) {
						rulesRow.set(i, rulesRow.get(i) | 1<<shiftBy);
						rulesCol.set(j, rulesCol.get(j) | 1<<shiftBy);
						rules3x3.set((i/3)*3 + j/3, rules3x3.get((i/3)*3 + j/3) | 1<<shiftBy);
					}
				}
			}
		}
		if(!valid) {
			throw new IllegalArgumentException(error);
		}
		return valid;
	}
}
