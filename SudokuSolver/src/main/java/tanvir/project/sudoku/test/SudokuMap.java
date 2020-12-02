package tanvir.project.sudoku.test;

import java.security.InvalidParameterException;
import java.text.StringCharacterIterator;
import java.util.List;

import tanvir.project.sudoku.engine.SudokuEngine;

/**
 * This class is used for testing the SudokuEngine class without having to run the entire webapplication.
 * @author Tanvir Kaykobad
 *
 */
public class SudokuMap {
//	public int[][] map = new int[9][9];
	
	/*
	 * This method converts a string sudoku map as it appears in the main method, into a sudoku map format understood by SudokuEngine
	 */
	public int[][] readMap(String input) {
		int[][] map = new int[9][9];
		
		StringCharacterIterator iterator = new StringCharacterIterator(input);
		char next=iterator.first();
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				while((next<'1' || next>'9') && next!='.' && next!=StringCharacterIterator.DONE) {
					next = iterator.next();
				}
				if(next==StringCharacterIterator.DONE) {
					throw new InvalidParameterException("Invalid Input. Input must be a String consisting 100 numerical characters or periods describing sudoku map left to right, top to bottom. Other characters in the string are ignored.");
				}
				if(next>='1' && next<='9') {
					map[i][j]=next-'0';
				} else if(next=='.') {
					map[i][j]=0;
				}
				next = iterator.next();
			}
		}
		return map;
	}
	
	
	/**
	 * This method is used for testing the SudokuEngine class
	 * @param args
	 */
	public static void main(String args[]) {
		SudokuMap s = new SudokuMap();
//		String input =  "113.5.7.9\n" +
//						".2.......\n" +
//						"...4.....\n" +
//						".....6...\n" +
//						"....6..8.\n" +
//						".........\n" +
//						".2....9..\n" +
//						"....5....\n" +
//						"..2....9.\n";
		
		String input = 
				"123.5.7.9\n" +
				".5.......\n" +
				"...4.....\n" +
				".....6...\n" +
				"....2..8.\n" +
				".........\n" +
				".3....9..\n" +
				"....5....\n" +
				"..2....1.\n";

		//easy
//		String input =
//				"... 1.5 .68"+
//				"... ... 7.1"+
//				"9.1 ... .3."+
//				"..7 .26 ..."+
//				"5.. ... ..3"+
//				"... 87. 4.."+
//				".3. ... 8.5"+
//				"1.5 ... ..."+
//				"79. 4.1 ...";
		
//		//easier
//		String input =
//				"... ... 68."+
//				"... .73 ..9"+
//				"3.9 ... .45"+
//				"49. ... ..."+
//				"8.3 .5. 9.2"+
//				"... ... .36"+
//				"96. ... 3.8"+
//				"7.. 68. ..."+
//				".28 ... ...";
		
//		//No Solution
//		String input =
//				"... ... 61."+
//				"... .73 ..."+
//				"... ... .45"+
//				"41. ... ..."+
//				"8.3 .5. 9.2"+
//				"... ... .36"+
//				"96. ... 3.."+
//				"7.. 68. ..."+
//				".2. ... ..."; 

				
		
//		String input = 
//				"435269781"+
//				"682571493"+
//				"197834562"+
//				"826195347"+
//				"374682915"+
//				"951743628"+
//				"519326874"+
//				"248957136"+
//				"763418259";
		
//		String input = 
//				"435269781"+
//				"682571493"+
//				"197834562"+
//				"82619.347"+
//				"374682915"+
//				"951743628"+
//				"519326874"+
//				"248957136"+
//				"763418259";
		
//		String input = 
//				".3.2....1"+
//				"..257.493"+
//				"19.834..."+
//				"...1...47"+
//				"3..68.915"+
//				"..17....."+
//				"51.3....4"+
//				"2489....6"+
//				"763418259";
		
//		String input = 
//				"435269781"+
//				"682571493"+
//				"197834562"+
//				"8261...47"+
//				"3..68.915"+
//				"..17....."+
//				"51.3....4"+
//				"2489....6"+
//				"763418259";
		
//		String input =
//				"1.8 ..6 92."+
//				".2. 49. 1.."+
//				".6. ... .45"+
//				"..3 .7. ..."+
//				".9. ... 2.3"+
//				"... ..5 ..9"+
//				"9.. ... .8."+
//				".5. 1.. .64"+
//				"..1 .5. ...";
		
		int map[][] = s.readMap(input);
		
		SudokuEngine.outputMap(map);
		SudokuEngine engine = new SudokuEngine(map);
		int[][] solution = engine.solveRecursively();
		if(solution!=null) {
			System.out.println("\nObtained Solution:");
			SudokuEngine.outputMap(solution);
		} else {
			System.out.println("No solution found");
		}
//		System.out.println("Is Valid: "+engine.isValid());
	}
}
