# SudokuSolver

SudokuSolver is a Java EE webapplication for solving sudoku puzzles. 

## Sudoku Puzzles
Sudokus are 9x9 grid puzzles forming a matrix. This matrix has 9 rows, 9 columns and 9 non-overlapping 3x3 sub-matrices. The goal of the puzzle is to fill up every cell of the puzzle without violating the following rules.

#### Sudoku Rules:
* Every cell must have a number between 1 and 9 inclusively.
* No two distinct cells in the same row, same column, or same 3X3 sub-matrix can have the same number.

## About The Project
This project is written Jakarta Java EE 8 using Eclipse IDE Version: 2020-09 (4.17.0). The dependencies are defined using Maven. It is deployed in Wildfly 21.0 Server. JSF 2.2 and Primefaces 7.0 are used. 

## Installation
The JSF framework of the project was prepared by following BalusC's guide that can be found [here](https://balusc.omnifaces.org/2020/04/jsf-23-tutorial-with-eclipse-maven.html). In particular, the following sections of the guide should be followed by the user:

* If JDK is not yet installed: Installing Java SE JDK
* If WildFly is not yet installed: Installing WildFly
* If Eclipse is not yet installed: Installing Eclipse, Configuring Eclipse
* To integrate Eclipse with WildFly: Integrating New Server in Eclipse

Next check out the project using EGit to import it in Eclipse, update the project using Maven to ensure all necessary jar files are downloaded and the dependencies are resolved. Finally add the project to WildFly server and start the server. Once the server has loaded SudokuSolver, it should be accessible via browser in "<HOST_PATH>/sudokusolver/".

To run the webapp on a remote server, produce the war file (SudokuSolver-0.0.1-SNAPSHOT.war) using maven install. 

## Architecture
The front end file viewed by users is index.xhtml. Here the sudoku board is initially drawn by a default board. The page has four functionalities:

* Clear board: To clear the board
* Default board: To load the default board
* Validate board: This Checks if the current board breaks any of the rules mentioned in Section Sudoku Rules.
* Solve board: This feature solves the board or notifies the user if no solution to the puzzle exists.

The algorithm for solving sudoku is implemented in tanvir.project.sudoku.engine.SudokuEngine class. On the other hand, the xhtml file is backed by tanvir.project.sudoku.Bean backing bean. Thus the backing bean works as the controller between the view (index.xhtml) file and the model (SudokuEngine). The board.css file is used to draw the board in index.xhtml. It is based on [this](https://codepen.io/gc-nomade/pen/eBcCI) example found in CodePen. Bootstrap css is also used to provide a responsive view so long as the device's screen dimension is at least 375X560 px.

## SudokuEngine | The Algorithm
A brute force solution of the problem would require exponential number of computation to solve the problem. But the process can be sped up by using brute force algorithm. To do so, the initial matrix is inserted in a stack. On each iteration we pull the top matrix from the stack, fill in the next unfilled cell using all possible numbers such that the matrix does not break any sudoku rules. All of these new matrices are copied into a stack (Depth-First-Search). If at any point we obtain a complete matrix then we have found a solution. Otherwise, if the queue becomes empty, we conclude that the given sudoku puzzle has no solution. In SudokuEngine, instead of using a queue, I opted for using recursion (see method solveRecursively).

Since recursion can eat up memory very quickly, it was important to represent the state of the problem using as less memory as possible.
To do so I have chosen to use bit-operations to validate the rules. Normally we would use 9 integers for each row rule, column rule and sub-matrix rule, requiring 27 integers a total for each iteration. But notice that for each rule (row, column or submatrix), all we need to keep track of is whether a digit (1 to 9) has appeared yet or not. This can be done in an integer where the i-th bit being 1 denotes that the digit i has already appeared for the rule. Let an integer variable 'var\_i' denote the digits that have appeared thus far in row i. Let us next insert the digit j in the i-th row next. Then to keep track of it we denote var\_i = var\_i BITWISE\_OR (1 LEFT\_SHIFT (j-1)). Now for the cell in the i-th row and j-th column, it has three integers row, col, sub denoting the three rules (row rule, column rule, sub-matrix rule) it has. Then to compute the available digits we can use in this cell, we simply have to take the inverse of (row BITWISE\_OR col BITWISE\_OR sub) from the 0-th bit to 8-th bit. Then by repeatedly dividing the result by 2 (or by right shifting) we can find all these available digits. As a result we are not only saving space per iteration, we are also saving computational time by opting for bit operations instead of iterating through a list of numbers that have appeared for each rule.

## Future Work
* Implement threading for parallelized solution searching computation.
* Significantly reduce the memory requirement of SudokuEngine by not creating a new sudoku matrix for each iteration.
* Use one int (32 bit) to save 3 rules, requiring 9 integers instead of 27 per iteration.
* Experiment and explore more lightweight front-end solutions.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html)