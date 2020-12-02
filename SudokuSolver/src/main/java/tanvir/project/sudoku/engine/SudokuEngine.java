package tanvir.project.sudoku.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SudokuEngine {
	private int[][] map;
	private long attempt = 0;
//	private int[][] available;
	
	private List<Integer> rulesRow, rulesCol, rules3x3;
	
	public SudokuEngine(int[][] map) {
		super();
		this.map = map.clone();
		
		try {
			isValid();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
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
	
//	public SudokuEngine(Integer[][] map) throws IllegalArgumentException {
//		if(map.length!=9 || (map.length==9 && map[0].length!=9)) {
//			throw new IllegalArgumentException("Sudoku Table must be 9x9");
//		}
//		
//		this.map = new int[9][9];
//		for(int i=0;i<9;i++) {
//			for(int j=0;j<9;j++) {
//				if(map[i][j]>=1 && map[i][j]<=9) {
//					this.map[i][j]=map[i][j].intValue();
//				} else {
//					this.map[i][j]=0;
//				}
//			}
//		}
//		
//		try {
//			isValid();
//		} catch (IllegalArgumentException e) {
//			throw e;
//		}
//	}
	
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
	
//	private Integer[][] getPrimitiveToWrapped(int[][] map) {
//		if(map==null)
//			return null;
//		
//		Integer[][] solution = new Integer[9][9];
//		for(int i=0;i<9;i++) {
//			for(int j=0;j<9;j++) {
//				if(map[i][j]>=1 && map[i][j]<=9) {
//					solution[i][j]=map[i][j];
//				} else {
//					solution[i][j]=null;
//				}
//			}
//		}
//		return solution;
//	}
	
	/**
	 * This method returns the solution of the sudoku problem
	 * @return
	 */
	public Character[][] getSolution(){
		return getPrimitiveToWrapped(solveRecursively());
	}
	
	public long getNumberOfAttempts() {
		return this.attempt;
	}
	
	public int[][] solveRecursively(){
		try {
			isValid();
		} catch (Exception e) {
			System.err.print(e.getMessage());
			return null;
		}
		
		int[][] solution = solveRecursively(map, rulesRow, rulesCol, rules3x3); 
//		System.out.println("Number of attempts: "+attempt);
		return solution;
	}
	
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
							//debugging
							findFirstEmpty(map);
							if(firstEmptyi<i || (firstEmptyi==i && firstEmptyj<j)) {
								System.out.println("Left cell unsolved");
							}
							
							
							int[][] mapNew = Arrays.stream(map).map(int[]::clone).toArray(int[][]::new);
							mapNew[i][j]=bitShift+1;
							
							List<Integer> rulesRowNew = new ArrayList<Integer>(rulesRow);
							rulesRowNew.set(i, rulesRowNew.get(i) | 1<<bitShift);
							List<Integer> rulesColNew = new ArrayList<Integer>(rulesCol);
							rulesColNew.set(j, rulesColNew.get(j) | 1<<bitShift);
							List<Integer> rules3x3New = new ArrayList<Integer>(rules3x3);
							rules3x3New.set((i/3)*3 + j/3, rules3x3New.get((i/3)*3 + j/3) | 1<<bitShift);

							attempt += 1;
							
							if(attempt%1000000==0) {
								outputMap(map);
								System.out.println("Attempt: "+attempt+" Adding "+(bitShift+1) + " to ["+i+"]["+j+"]");
							}
							solutionMap = solveRecursively(mapNew, rulesRowNew, rulesColNew, rules3x3New);

//							System.out.println("Returned:");
//							outputMap(map);
							
							if(solutionMap!=null) {
								return solutionMap;
							}
						} else if(available==0){
							System.out.println("No Solution Here");
						}
						available = available>>1;								
					}
					return null;
				}
			}
		}
		return null;
	}
	
	static int firstEmptyi=0, firstEmptyj=0;
	private void findFirstEmpty(int[][] map) {
		firstEmptyi=9;
		firstEmptyj=9;
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				if(map[i][j]==0){
					firstEmptyi=i;
					firstEmptyj=j;
					return;
				}
	}
	
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
	
	public static void outputMap(int[][] map) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<map.length;i++) {
			for(int j=0;j<map.length;j++) {
				if(map[i][j]!=0){
					sb.append(map[i][j]);
//					System.out.print(map[i][j]);
				} else{
					sb.append('.');
//					System.out.print(.);
				}
			}
			sb.append('\n');
//			System.out.println();
		}
		System.out.println(sb.toString());
	}

	
/*	
	public int[][] computeValidChoices(){
		available = new int[9][9];
		
		//Inserting each unsolved cells of the map in the priority queue with key = col*9+row
		//and value = number of possible remaining option for the cell without breaking the row/column/3x3 rules.
		PriorityQueue<Map.Entry<Integer, Integer>> pq = new PriorityQueue<>(
				(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) -> 
				(Integer.compare(o1.getValue(),o2.getValue()))
				);
		
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				//if this cell's value has not yet been chosen
				if(map[i][j]==0) {
					//combined marks all the values that are unavailable to this cell
					int combined = rulesRow.get(i) | rulesCol.get(j) | rules3x3.get((i/3)*3 + j/3);
					//we want the complement of these values marking the values available to this cell
					//mask is used to avoid overflow
					int mask = (1 << 9) -1;
					available[i][j] = ~combined & mask;
					
					int currentOption = countSetBits(available[i][j]);
					Map.Entry<Integer, Integer> entry = Map.entry(i*9+j, currentOption);
					pq.add(entry);
				}
			}
		}
		
		return map;
	}
*/
	
    /* Brian Kernighan�s Algorithm for counting the one bits of an integer 
     * Function to get no of set bits in binary representation of passed binary no. */
    static int countSetBits(int n) 
    { 
        int count = 0; 
        while (n > 0) { 
            n &= (n - 1); 
            count++; 
        } 
        return count; 
    }
	
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
//						System.out.println("Row "+i+" has multiple "+ map[i][j]);
						if(!valid) {
							error+=", ";
						}
						error+="Row "+(i+1)+" has multiple "+ map[i][j]+"'s";
						valid = false;
					}

					//check along vertical line
					if(((rulesCol.get(j)>>shiftBy) & 1) > 0) {
//						System.out.println("Col "+j+" has multiple "+ map[i][j]);
						if(!valid) {
							error+=", ";
						}
						error+="Col "+(j+1)+" has multiple "+ map[i][j]+"'s";
						valid = false;
					}

					//check along 3x3 squires line
					if(((rules3x3.get((i/3)*3 + j/3)>>shiftBy) & 1) > 0) {
//						System.out.println("3x3 "+((i/3)*3 + j/3)+" has multiple "+ map[i][j]);
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
