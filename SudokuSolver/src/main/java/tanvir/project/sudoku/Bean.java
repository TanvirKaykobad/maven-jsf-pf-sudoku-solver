package tanvir.project.sudoku;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tanvir.project.sudoku.engine.SudokuEngine;

@Named
//@RequestScoped
@ViewScoped
public class Bean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LogManager.getLogger(Bean.class);
	
	private Character[][] map;
	private Map<String,Object> possibleValues;

    private String input;
    private String output;
    
    private SudokuEngine engine = null;
    
	@PostConstruct
	public void initialize(){
		if(!FacesContext.getCurrentInstance().isPostback()) {
		defaultBoard();
		
    	possibleValues = new LinkedHashMap<String,Object>();
		for(Character i='1';i<='9';i++) {
			possibleValues.put(""+i,i);
		}
		possibleValues.put(" ",' ');
		}
	}
	
	public void logMap() {
		for(int i=0;i<map.length;i++) {
			StringBuilder sb = new StringBuilder();
			for(int j=0; j<map[0].length;j++) {
				sb.append(map[i][j]+" ");
			}
			LOGGER.info(sb.toString());
		}
	}
	
	public void validateSudoku() {
    	try {
    		sanatizeBoard(map);
    		engine = new SudokuEngine(map);
    		addMessage("Board is valid.");
    	} catch(IllegalArgumentException e) {
    		addErrorMessage(e.getLocalizedMessage());
    	}
	}
	
    public void solveSudoku() {
    	if(map!=null) {
    		sanatizeBoard(map);
    		logMap();
    	} else {
    		LOGGER.error("ButtonAction: map is null");
    	}
    	
    	try {
    		long startTime = System.currentTimeMillis();

    		engine = new SudokuEngine(map);

    		Character[][] solution = engine.getSolution();
    		Long attempt = engine.getNumberOfAttempts();
    		if(solution==null) {
    			addErrorMessage("No solution exists");    			
    		} else {
    			map = solution;
    		}
    		addMessage("Searching possible soltuion");

    		long endTime = System.currentTimeMillis();
    		addMessage("Attempts: "+attempt+" Time: " + (endTime-startTime) + "ms"); 
    		
    	} catch(IllegalArgumentException e) {
    		addErrorMessage(e.getLocalizedMessage());
    	}
    }
 
    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    public void addErrorMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    

    
    public void clearBoard() {
    	map = new Character[9][9];
    	for(int i=0;i<9;i++) {
    		for(int j=0;j<9;j++) {
    			map[i][j]=' ';
    		}
    	}
    }
    
    public void defaultBoard(){
//		map = new Integer[][]{
//			{1,		null,	8,		null,	null,	6,		9,		2,		null},
//			{null,	2,		null,	4,		9,		null,	1,		null,	null},
//			{null,	6,		null,	null,	null,	null,	null,	4,		5},
//			{null,	null,	3,		null,	7,		null,	null,	null,	null},
//			{null,	9,		null,	null,	null,	null,	2,		null,	3},
//			{null,	null,	null,	null,	null,	5,		null,	null,	9},
//			{9,		null,	null,	null,	null,	null,	null,	8,		null},
//			{null,	5,		null,	1,		null,	null,	null,	6,		4},
//			{null,	null,	1,		null,	5,		null,	null,	null,	null}
//		};
		map = new Character[][]{
			{'1',	' ',	'8',	' ',	' ',	'6',	'9',	'2',	' '},
			{' ',	'2',	' ',	'4',	'9',	' ',	'1',	' ',	' '},
			{' ',	'6',	' ',	' ',	' ',	' ',	' ',	'4',	'5'},
			{' ',	' ',	'3',	' ',	'7',	' ',	' ',	' ',	' '},
			{' ',	'9',	' ',	' ',	' ',	' ',	'2',	' ',	'3'},
			{' ',	' ',	' ',	' ',	' ',	'5',	' ',	' ',	'9'},
			{'9',	' ',	' ',	' ',	' ',	' ',	' ',	'8',	' '},
			{' ',	'5',	' ',	'1',	' ',	' ',	' ',	'6',	'4'},
			{' ',	' ',	'1',	' ',	'5',	' ',	' ',	' ',	' '}
		};
//		map = new String[][]{
//			{"1",	"0",	"8",	"0",	"0",	"6",	"9",	"2",	"0"},
//			{"0",	"2",	"0",	"4",	"9",	"0",	"1",	"0",	"0"},
//			{"0",	"6",	"0",	"0",	"0",	"0",	"0",	"4",	"5"},
//			{"0",	"0",	"3",	"0",	"7",	"0",	"0",	"0",	"0"},
//			{"0",	"9",	"0",	"0",	"0",	"0",	"2",	"0",	"3"},
//			{"0",	"0",	"0",	"0",	"0",	"5",	"0",	"0",	"9"},
//			{"9",	"0",	"0",	"0",	"0",	"0",	"0",	"8",	"0"},
//			{"0",	"5",	"0",	"1",	"0",	"0",	"0",	"6",	"4"},
//			{"0",	"0",	"1",	"0",	"5",	"0",	"0",	"0",	"0"}
//		};
    }
	
	public Map<String, Object> getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(Map<String, Object> possibleValues) {
		this.possibleValues = possibleValues;
	}

	public Character[][] getMap() {
		return map;
	}

	public void setMap(Character[][] mapInput) {
		this.map = mapInput;
		sanatizeBoard(map);
	}
	
	public void sanatizeBoard(Character[][] map) {
		if(map!=null) {
			for(int i=0; i<9; i++) {
				for(int j=0; j<9; j++) {
					if(map[i][j]==null) {
						map[i][j]=' ';
					}
				}
			}
		}
	}

	public void submit() {
        output = "Hello World! You have typed: " + input;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }
}