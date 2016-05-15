package mainGame;

import engine.BlockGenerator;
import engine.GameMode;
import engine.RandomizeBlocks;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Class that runs a graphical version of the game that allows the user to play normally
 * 
 * @author Hank O'Brien
 *
 */
public class GameApp extends Application {

  public static final int GAME_HEIGHT = 20;
  public static final int GAME_WIDTH = 10;
  public static final int MIN_TIME_PER_TURN = 100;
  public static final boolean USE_GRAPHICS = true;
  public static final boolean DEBUG = false;
  public static final boolean PLAY_MULTIPLE = false;
  public static final double[] WEIGHTS = new double[] {-70, -97.85, 306.77, 5};
  public static final ScoreMode SCORE_MODE = ScoreMode.NINTENDO;
  public static final BlockGenerator GENERATOR = new RandomizeBlocks();

  public static void main(String args[]) throws Exception {
    launch();
  }

  @Override
  public void start(Stage arg0) throws Exception {
    Game userGame = new Game(GAME_HEIGHT, GAME_WIDTH, MIN_TIME_PER_TURN, GameMode.DISTRO, USE_GRAPHICS,
        DEBUG, GENERATOR, PLAY_MULTIPLE, SCORE_MODE);
    userGame.run(arg0);
//    Stage other = new Stage();
//    Game aiGame = new Game(GAME_HEIGHT, GAME_WIDTH, MIN_TIME_PER_TURN, GameMode.AUTOPLAY,
//        USE_GRAPHICS, DEBUG, GENERATOR, PLAY_MULTIPLE, WEIGHTS, SCORE_MODE);
//    aiGame.run(other);
//    other.show();

  }
}