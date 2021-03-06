package clients;

import blocks.blockGeneration.BlockGenerator;
import blocks.blockGeneration.RandomizeBlocks;
import blocks.blockGeneration.StandardizeBlocks;
import clients.interfaces.Viewable;
import javafx.application.Application;
import javafx.stage.Stage;
import mainGame.Game;
import mainGame.GameMode;
import mainGame.ScoreMode;

/**
 * Class that runs a graphical version of the game that allows the user to play normally
 * 
 * @author Hank O'Brien
 *
 */
public class UserGameClient extends Application implements Viewable{

  public static final int GAME_HEIGHT = 20;
  public static final int GAME_WIDTH = 10;
  //I think 90000000 nanos is a good minTime personally - Liam
  public static final int MIN_TIME_PER_TURN = 100000000;
  public static final boolean DEBUG = false;
  public static final ScoreMode SCORE_MODE = ScoreMode.SIMPLE;
  public static final BlockGenerator GENERATOR = new RandomizeBlocks();
//  public static final BlockGenerator GENERATOR = new StandardizeBlocks(2);
  public static final int MAX_GAMES_TO_PLAY = 5;

  public static void main(String args[]) throws Exception {
    launch();
  }

  @Override
  public void start(Stage arg0) throws Exception {
    Game userGame = new Game(GAME_HEIGHT, GAME_WIDTH, MIN_TIME_PER_TURN, MAX_GAMES_TO_PLAY, GameMode.DISTRO, USE_GRAPHICS,
        DEBUG, GENERATOR, SCORE_MODE);
    userGame.run(arg0);

  }
}
