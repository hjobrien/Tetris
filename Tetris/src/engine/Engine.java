package engine;

import java.util.ArrayList;
import java.util.Random;

import mainGame.Board;
import tetrominoes.Block;
import tetrominoes.LeftL;
import tetrominoes.LeftS;
import tetrominoes.RightL;
import tetrominoes.RightS;
import tetrominoes.Square;
import tetrominoes.StraightLine;
import tetrominoes.TBlock;
import tetrominoes.Tile;

public class Engine {
	private static Board board;
	private static Board nextPieceBoard;
	private static Block nextBlock = generateRandomBlock();
	private static boolean isPaused = false;
	
	private static boolean logMode;
//	private static boolean debugMode;

	public static void update() {
		if(!isPaused){ //little hacky, could be improved
			if (board.getFallingBlock().isFalling()){
				if (board.checkDown()){
					board.blockDown();
				} else {
					if(logMode){
						Logger.log(board.getBoardState(), board.getFallingBlock(), nextPieceBoard.getFallingBlock());
					}
					ArrayList<Integer> linesToClear = board.getFullRows();
					if (!linesToClear.isEmpty()){
						board.clearLines(linesToClear);
					} else {
						board.setNotFalling();
						addBlock();
					}
				}
			} else {
				addBlock();
			}
		}
	}

	public Tile[][] getBoardState(){
		return board.getBoardState();
	}
	
	//Adds a random block to the board
	public static void addBlock() {
		board.setFallingBlock(nextBlock);
		board.updateBoardWithNewBlock(nextBlock);
		nextBlock = generateRandomBlock();	
		nextPieceBoard.clearBoard();
		Renderer.draw(nextPieceBoard);
		nextPieceBoard.setFallingBlock(nextBlock);
		nextPieceBoard.updateBoardWithNewBlock(nextBlock);
		nextPieceBoard.setNotFalling();
		Renderer.draw(nextPieceBoard);
	}
	
	private static Block generateRandomBlock() {
		Random r = new Random();
		int i = r.nextInt(7);
		switch (i){
		case 0:
			return new LeftL();
		case 1:
			return new RightL();
		case 2:
			return new LeftS();
		case 3: 
			return new RightS();
		case 4:
			return new StraightLine();
		case 5:
			return new TBlock();
		case 6: 
			return new Square();
		}
		throw new RuntimeException("bad random num");
	}

	public static Board getBoard(){
		return board;
	}

	public static boolean togglePause() {
		isPaused = !isPaused;
		return isPaused;
	}
	
	
	public static void setValues(Board b1, Board b2) {
		board = b1;
		nextPieceBoard = b2;
		
	}

	public static void setMode(boolean debugMode, boolean logMode) {
//		Engine.debugMode = debugMode;
		Engine.logMode = logMode;
	}

}
