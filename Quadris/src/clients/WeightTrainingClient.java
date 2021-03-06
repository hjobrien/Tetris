package clients;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import cerulean.Cerulean;
import clients.interfaces.Autoplayable;
import engine.ParallelizedCore;
import mainGame.ScoreMode;

public class WeightTrainingClient implements Autoplayable {

  public static final String LOGFILE_PATH = "src/gameLogs/AI Output";

  public static final int GAME_HEIGHT = 20;
  public static final int GAME_WIDTH = 10;
  public static final int MIN_TIME_PER_TURN = 10;
  public static final boolean USE_GRAPHICS = false;
  public static final boolean DO_DEBUG = false;
  public static final int NUM_BLOCKS_TO_CONSIDER = 2;
  // should be 1, if a game finishes the thread should be allowed to take a new thread from the pool
  public static final int MAX_CONSEC_GAMES_PER_THREAD = 1;
  public static final int MAX_GAMES_PER_GEN = 16;
  public static final int MAX_GENERATIONS = 30;
  public static final ScoreMode SCORE_MODE = ScoreMode.SIMPLE;
  public static final double MUTATION_FACTOR = 0.5;


  private static double[][] species =
      new double[][] {
          {-40, -100, 1, 400, 400 * 3, 400 * 7, 400 * 20},      //TODO: Errors
          {-200, -50, 1.68, 100, 100 * 3, 100 * 7, 100 * 20},
//          {-70, -70, 5, 500, 500 * 3, 500 * 7, 500 * 20},
          {-57.29, -83.97, 1.76, 1573.34, 2451.18, 2089.19, 6228.07},
          {-100, -50, 2, 100, 100 * 3, 100 * 7, 100 * 20},
          {-200, -70, 7, 300, 300 * 3, 300 * 7, 300 * 20},
          {-400, -300, 1, 100, 100 * 3, 100 * 7, 100 * 20},
          {-200, -100, 3, 100, 100 * 3, 100 * 7, 100 * 20},
          {-150, -70, 0, 400, 400 * 3, 400 * 7, 400 * 20},
          {-70, -150, -5, 500, 500 * 3, 500 * 7, 500 * 20},
          {-200, -35.4, 8, 100, 100 * 3, 100 * 7, 100 * 20},
          {-294.75, -34.44, 5, 101.72, 101.72 * 3, 101.72 * 7, 101.72 * 20},
          {-72.15, -37.37, 5.0, 818.84, 1683.85, 2963.27, 11916.65}};

  public static void main(String[] args) throws IOException {
    File file = new File(LOGFILE_PATH);
    if (!file.exists()) {
      file.createNewFile();
    }
    PrintStream aiPrinter = new PrintStream(file);

    ParallelizedCore runner = new ParallelizedCore(GAME_HEIGHT, GAME_WIDTH, MIN_TIME_PER_TURN,
        MAX_CONSEC_GAMES_PER_THREAD, MAX_GAMES_PER_GEN, USE_GRAPHICS, DO_DEBUG, SCORE_MODE,
        NUM_BLOCKS_TO_CONSIDER);

    double[] speciesAvgScore = new double[species.length];
    for (int genNumber = 0; genNumber < MAX_GENERATIONS; genNumber++) {
      for (int i = 0; i < species.length; i++) {
        double[] currentSpecies = species[i];
        if (i == 0 && Cerulean.getBestScore() != 0) {
          double avgScore = Cerulean.getBestScore();
          System.out.println("Average Score of " + Arrays.toString(currentSpecies) + "\t over "
              + MAX_GAMES_PER_GEN + " Games using " + SCORE_MODE + " scoring is " + avgScore);
          speciesAvgScore[0] = avgScore;
        } else if (i == 1 && Cerulean.getSecondScore() != 0) {
          double avgScore = Cerulean.getSecondScore();
          System.out.println("Average Score of " + Arrays.toString(currentSpecies) + "\t over "
              + MAX_GAMES_PER_GEN + " Games using " + SCORE_MODE + " scoring is " + avgScore);
          speciesAvgScore[1] = avgScore;
        } else {
          try {
            double avgScore = runner.run(currentSpecies);
            System.out.println("Average Score of " + Arrays.toString(currentSpecies) + "\t over "
                + MAX_GAMES_PER_GEN + " Games using " + SCORE_MODE + " scoring is " + avgScore);
            aiPrinter.println(avgScore);
            speciesAvgScore[i] = avgScore;
          } catch (Exception e) {
            speciesAvgScore[i] = -1;
            e.printStackTrace();
          }
        }
      }
      aiPrinter.println();
      System.out.println("***BREEDING***");
      species = Cerulean.breed(species, speciesAvgScore, MUTATION_FACTOR);
    }
    aiPrinter.close();
    System.exit(0);
  }

}
