package mainGame;

import engine.Engine;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import tetrominoes.Tile;

public class Main extends Application{
	
	
	//change this to enable debug configurations, allows for fast switching between releases and development versions
	boolean debug = true;
	
	//height should be double width
	private static final int SCREEN_WIDTH = 300;
	private static final int SCREEN_HEIGHT = SCREEN_WIDTH * 2;
	
	public static final int BOARD_HEIGHT = 20;
	public static final int BOARD_WIDTH = 10;

	private Engine engine;
	private AnimationTimer timer;
	private	int score = 0;


	public static void main(String[] args) {	
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		
		//if we only make 1 board and it's in engine, we can always just receive the boardState 
		//from engine when we need it and we wont have to be translating boardStates
		this.engine = new Engine(new Board(BOARD_HEIGHT, BOARD_WIDTH));
		GridPane mainGame = new GridPane();
		mainGame.getColumnConstraints().add(new ColumnConstraints(SCREEN_WIDTH));
		mainGame.getColumnConstraints().add(new ColumnConstraints(150));
		
		for(int i = 0; i < 3; i++){
			mainGame.getRowConstraints().add(new RowConstraints(150));
		}
		Label scoreText = new Label("\t\tScore: " + score);
		StringProperty valueProperty = new SimpleStringProperty();
		valueProperty.setValue("0");
		scoreText.textProperty().bind(valueProperty);
		mainGame.add(scoreText, 1,3);
		
//		mainGame.setGridLinesVisible(true);
		
		
		
		GridPane grid = new GridPane();
		mainGame.add(grid, 0, 0,1,4);
		//we haven't been using this at all
		//but might want to for styling?
//		Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
//		GraphicsContext g = canvas.getGraphicsContext2D();
		
		Scene boardScene = new Scene(mainGame, SCREEN_WIDTH+150, SCREEN_HEIGHT);
		
		stage.setScene(boardScene);
		
		configureGrid(grid);
		
		if(debug){
			indicateFilled(grid);
		}
		
		engine.addBlock();
		
		stage.addEventFilter(KeyEvent.KEY_PRESSED,e -> {
			if(e.getCode() == KeyCode.ESCAPE){
				System.exit(0);
			}
			else if(e.getCode() == KeyCode.P){
				engine.togglePause();
			} else if (e.getCode() == KeyCode.RIGHT){
				engine.getBoard().pressed("right");
			} else if (e.getCode() == KeyCode.LEFT){
				engine.getBoard().pressed("left");
			} else if (e.getCode() == KeyCode.UP){
				engine.getBoard().pressed("up");
			} else if (e.getCode() == KeyCode.DOWN){
				engine.getBoard().pressed("down");
			}
		});  

		timer = new AnimationTimer(){
			private long pastTime;
			@Override
			public void start(){
				pastTime = System.currentTimeMillis();
				super.start();
			}
			
			@Override
			public void handle(long time){

				long now = System.currentTimeMillis();
				if(now-pastTime >= 500){
					score++;
					valueProperty.set("\t\tScore: " + score);
					if (engine.getBoard().isFull()){
						timer.stop();
					}
					engine.update();
					indicateFilled(grid);
					pastTime = now;
				}
			}
		};
		timer.start();
		
		
		stage.show();

	}
		

	private void configureGrid(GridPane grid) {
		for (int i = 0; i < BOARD_WIDTH; i++){
			grid.getColumnConstraints().add(new ColumnConstraints(SCREEN_WIDTH / BOARD_WIDTH));
		}
		for (int i = 0; i < BOARD_HEIGHT; i ++){
			grid.getRowConstraints().add(new RowConstraints(SCREEN_HEIGHT / BOARD_HEIGHT));
		}
		
		//very helpful for debugging purposes
		//maybe we want this even when not debugging?
		if(debug){
			grid.setGridLinesVisible(true);
		}
	}
	
	private void indicateFilled(GridPane grid){
		
		//just for debugging now, although a similar system could be used to display the actual blocks
		for (int i = 0; i < BOARD_HEIGHT; i++){
			for (int j = 0; j < BOARD_WIDTH; j++){
				
				Tile current = engine.getBoardState().get(i).get(j);
				if (current.isFilled()){
					Rectangle r = new Rectangle();
					r.setHeight(29);
					r.setWidth(29);
					r.setFill(current.getColor());
					grid.add(r, j,i);
				} else {
					Rectangle r = new Rectangle();
					r.setHeight(29);
					r.setWidth(29);
					r.setFill(Color.WHITE);
					grid.add(r, j, i);
				}

			}
		}
		
	}


}
