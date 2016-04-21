package cerulean;

import java.util.ArrayList;
import java.util.stream.DoubleStream;

import blocks.Block;
import blocks.Tile;
import engine.ComputationDoneEvent;
import engine.Engine;
import engine.Renderer;
import javafx.event.Event;
import mainGame.Board;
import mainGame.Move;

/**
 * General AI class, handles game state analysis and move optimization
 * 
 * @author Hank
 *
 */
public class Cerulean {

  // Board Weight constants
  // negative means its a bad thing being weighted (overall board height)
  // positive means its a good thing (full lines);
  // good weights: -100, -50, 70 : ~8000x
//  private static final double HEIGHT_WEIGHT = -100;
//  private static final double VOID_WEIGHT = -50;
//  private static final double LINE_WEIGHT = 70;

   private static final double HEIGHT_WEIGHT = -100;
   private static final double VOID_WEIGHT = -70;
   private static final double LINE_WEIGHT = 500;

  // TODO: add a positive weight for how full each row is?

  private static Move[] solutionPath = new Move[] {Move.RIGHT}; // partially filled to prevent
                                                                // errors later on

  /**
   * called when a solution is needed for a given block
   * 
   * @return an array of moves needed to get piece to the optimal location, should be some form of
   *         left/right, rotate, drop
   */
  public static Move[] getSolution() {
    return solutionPath;
  }

  /**
   * gives AI data for what block is active and the board state it is to be placed in
   * 
   * @param nextBlockType the kind of block to be analyzed
   * @param boardState the current board state
   */
  public static Move[]  submitBlock(Block nextBlock, Tile[][] boardState) {
    // TODO:
    // ideal behavior: blocks drop normally but an array is generated each time a block is added to
    // the game
    // long t1 = System.currentTimeMillis();
    return computeBestPath(nextBlock, boardState);
    // System.out.println("x Weight analysis took " + (System.currentTimeMillis() - t1) + "
    // Milli(s)");
    // using grid from engine as event target, should change to something else
    // TODO: make board extend GridPane? it'd be a node then
//    Event.fireEvent(Engine.getBoard().getGrid(), new ComputationDoneEvent(solutionPath));
  }

  /**
   * Generalized move optimization method
   * 
   * @param nextBlock the block just introduced to the board
   * @param boardState the board state without the block entered, all tiles are not active
   * @return an array of moves that positions the piece in to the optimal location
   */
  private static Move[] computeBestPath(Block nextBlock, Tile[][] boardState) {

    double maxWeight = Double.NEGATIVE_INFINITY;
    // Block nextBlockCopy = new Block(nextBlock.getType(),
    // new int[] {nextBlock.getGridLocation()[0], nextBlock.getGridLocation()[1]});
    // int startingRowIndex = nextBlockCopy.getShape().length - 1 + 3;
    // int startingColumnIndex = (Renderer.HORIZONTAL_TILES - nextBlockCopy.getShape()[0].length) /
    // 2
    // + nextBlockCopy.getShape()[0].length - 1;
    // nextBlockCopy.setGridLocation(startingRowIndex, startingColumnIndex);
    Block nextBlockCopy = new Block(nextBlock.getType(), new int[] {0, 0});

    Move[] bestPath = new Move[] {};
    // TODO: reduce number of loops reps
    for (int moveCount = 0; moveCount < 10; moveCount++) {
      for (int rotCount = 0; rotCount < nextBlockCopy.getNumRotations(); rotCount++) { // 10
                                                                                       // possible
                                                                                       // worst-case
                                                                                       // left/right
        // positions
        Tile[][] testState = positionBlock(nextBlockCopy, boardState, moveCount, rotCount);
        double[] testWeights = evaluateWeight(testState);
        double testWeight = DoubleStream.of(testWeights).sum();
        // System.out.print(moveCount + " " + rotCount);
        if (testWeight > maxWeight) {
          // printBoard(testState);
          // System.out.println(testWeights[0] + " " + testWeights[1] + " " + testWeights[2]);
          maxWeight = testWeight;
          bestPath = getPath(moveCount, rotCount);
        }

        // nextBlockCopy.setGridLocation(new int[] {startingRowIndex, startingColumnIndex});
        nextBlockCopy.setGridLocation(new int[] {0, 0});
        nextBlockCopy.rotateRight();
      }

    }
    return bestPath;
  }

  private static void printBoard(Tile[][] testState) {
    for (int i = 0; i < testState.length; i++) {
      for (int j = 0; j < testState[i].length; j++) {
        System.out.print((testState[i][j].isFilled() ? "x " : "o ")); // Ternary operator,
                                                                      // basically an if statement
      }
      System.out.println();
    }
    System.out.println();

  }

  /**
   * converts integer representations of moves into array of individual moves
   * 
   * @param moveCount starting from the far left, how many times the block must be moved to the
   *        right
   * @param rotCount starting from the default configuration, the number of rotations to the right
   *        are required
   * @return an array of moves that first shifts the block to the left, then some amount to the
   *         right, rotates, and issues a 'drop' command to terminate the sequence x
   */
  private static Move[] getPath(int moveCount, int rotCount) {
    ArrayList<Move> path = new ArrayList<Move>();
    for (int i = 0; i < rotCount; i++) {
      path.add(Move.ROT_RIGHT);
    }
    for (int i = 0; i < 6; i++) { // puts the block into a constant, known position
      path.add(Move.LEFT);
    }
    for (int i = 0; i < moveCount; i++) {
      path.add(Move.RIGHT);
    }


    path.add(Move.DROP);
    return path.toArray(new Move[] {});
  }

  /**
   * positions a block in a copy of the board based on translation and rotation parameters
   * 
   * @param nextBlock the active block
   * @param boardState the state of non active tiles
   * @param moveCount the number of left/right moves in the test arrangement
   * @param rotCount the number of rotations in the test arrangement
   * @return the board with the block moved to a certain position
   */
  private static Tile[][] positionBlock(Block nextBlock, Tile[][] boardState, int moveCount,
      int rotCount) {
    // avoids reference issues
    Tile[][] tileCopy = new Tile[boardState.length][boardState[0].length];
    for (int i = 0; i < boardState.length; i++) {
      for (int j = 0; j < boardState[0].length; j++) {
        tileCopy[i][j] = new Tile(boardState[i][j].isActive(), boardState[i][j].isFilled(),
            boardState[i][j].getColor());
      }
    }

    Board boardCopy = new Board(tileCopy, nextBlock);

    for (int i = 0; i < 10; i++) {
      boardCopy.pressed(Move.LEFT);
    }

    for (int i = 0; i < moveCount; i++) {
      boardCopy.pressed(Move.RIGHT);
    }

    for (int i = 0; i < rotCount; i++) {
      boardCopy.pressed(Move.ROT_RIGHT);
    }

    boardCopy.pressed(Move.DROP);

    return boardCopy.getBoardState();

  }

  /**
   * evaluates the relative value of the board
   * 
   * @param boardCopy the board to be analyzed
   * @return the value of the board
   */
  public static double[] evaluateWeight(Tile[][] boardCopy) {
    double[] weight = new double[3];
    double voids = 0;
    double height = 0;
    int maxHeight = 0;
    for (int i = 0; i < boardCopy[0].length; i++) {
      Tile[] colCopy = new Tile[boardCopy.length];
      for (int j = 0; j < boardCopy.length; j++) {
        colCopy[j] = boardCopy[j][i];
      }
      if (getHeight(colCopy) > maxHeight) {
        maxHeight = getHeight(colCopy);
      }
      // height += (HEIGHT_WEIGHT * getHeight(colCopy));
      // double voidCount = Math.pow(getNumVoids(colCopy), 2);
      // voids += (VOID_WEIGHT * (voidCount == 0 ? 0 : ( 1.0 / voidCount)));
      voids += (VOID_WEIGHT * getNumVoids(colCopy));
    }
    height = HEIGHT_WEIGHT * maxHeight;
    double lines = 0;
    for (int i = 0; i < boardCopy[0].length; i++) {
      lines += (LINE_WEIGHT * getNumLines(boardCopy));
    }
    // System.out.println("voids: " + voids + " heights: " + height + " lines: " + lines);
    weight[0] = voids;
    weight[1] = height;
    weight[2] = lines;
    return weight;
  }

  /**
   * returns the number of completed lines in the board
   * 
   * @param boardCopy the board to be analyzed. It should not have been processed to remove lines
   * @return the number of completed lines on the board (rows)
   */
  private static int getNumLines(Tile[][] boardCopy) {
    int numLines = 0;
    for (int i = 0; i < boardCopy.length; i++) {
      boolean isFull = true;
      for (int j = 0; j < boardCopy[0].length; j++) {
        if (!boardCopy[i][j].isFilled()) {
          isFull = false;
        }
      }
      if (isFull) {
        numLines++;
      }
    }
    // if (nu mLinexrintln(numLines);
    return numLines;
  }

  /**
   * analyzes a column of tiles for the number of voids it contains. A void is defined as any blank
   * space with a filled, non active tile at some point above it
   * 
   * @param tiles the column of tiles to be analyzed
   * @return the number of voids in the column
   */
  private static double getNumVoids(Tile[] tiles) {
    boolean hasFoundBlock = false;
    double voidCount = 0;
    for (int i = 0; i < tiles.length; i++) {
      if (hasFoundBlock && !tiles[i].isFilled()) {
        voidCount++;
      }
      if (tiles[i].isFilled()) {
        hasFoundBlock = true;
      }
    }
    return voidCount;
  }

  /**
   * gets the height of a column of tiles. The height is defined as the index of the highest filled,
   * non active tile in the column
   * 
   * @param tiles the column to be analyzed
   * @return the height of the column
   */
  private static int getHeight(Tile[] tiles) {
    int height = 0;
    for (int i = tiles.length - 1; i >= 0; i--) {
      if (tiles[i].isFilled()) {
        height = tiles.length - i;
      }
    }
    return height;
  }
}


