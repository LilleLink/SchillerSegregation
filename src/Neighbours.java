import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;

import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.System.*;

/*
 *  Program to simulate segregation.
 *  See : http://nifty.stanford.edu/2014/mccown-schelling-model-segregation/
 *
 * NOTE:
 * - JavaFX first calls method init() and then the method start() far below.
 * - The method updateWorld() is called periodically by a Java timer.
 * - To test uncomment call to test() first in init() method!
 *
 */
// Extends Application because of JavaFX (just accept for now)
public class Neighbours extends Application {

    // Enumeration type for the Actors
    enum Actor {
        BLUE, RED, NONE   // NONE used for empty locations
    }

    // Enumeration type for the state of an Actor
    enum State {
        UNSATISFIED,
        SATISFIED,
        NA     // Not applicable (NA), used for NONEs
    }

    // Below is the *only* accepted instance variable (i.e. variables outside any method)
    // This variable may *only* be used in methods init() and updateWorld()
    Actor[][] world;              // The world is a square matrix of Actors

    // This is the method called by the timer to update the world
    // (i.e move unsatisfied) approx each 1/60 sec.
    void updateWorld() {
        // % of surrounding neighbours that are like me
        final double threshold = 0.75;

        // TODO Update logical state of world

        updateActors(world, threshold);


    }

    // This method initializes the world variable with a random distribution of Actors
    // Method automatically called by JavaFX runtime (before graphics appear)
    // Don't care about "@Override" and "public" (just accept for now)
    @Override
    public void init() {
        //test();    // <---------------- Uncomment to TEST!

        // %-distribution of RED, BLUE and NONE
        double[] dist = {0.25, 0.25, 0.50};
        // Number of locations (places) in world (square)
        int nLocations = 9000;

        // TODO Create and populate world

        world = createWorld(world, dist[0], dist[1], dist[2], nLocations);

        // Should be last
        fixScreenSize(nLocations);
    }

    //---------------- Methods ----------------------------

    /*
    private boolean isAllSatisfied(Actor[][] arr) {

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length; j++) {
                arr
            }
        }

        return true;
    }
*/

    private void updateActors(Actor[][] world, double threshold) {
        Actor[][] currentWorld = world;
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world.length; j++) {
                switch (currentWorld[i][j]) {
                    case RED:
                        if (((double)redNeighbours(currentWorld, i, j)/totalNeighbours(currentWorld,i,j)) < threshold) {
                            moveActor(world, i, j);
                        }
                        break;

                    case BLUE:
                        if (((double)blueNeighbours(currentWorld, i, j)/totalNeighbours(currentWorld,i,j)) < threshold) {
                            moveActor(world, i, j);
                        }
                        break;

                    default:
                        continue;
                }

            }
        }

    }

    private void moveActor(Actor[][] world, int i, int j) {
        Random rand = new Random();
        int row = 0;
        int col = 0;

        while(true) {
            row = rand.nextInt(world.length);
            col = rand.nextInt(world.length);

            if (world[row][col] == Actor.NONE)
                break;
        }

        Actor temp = world[i][j];
        world[i][j] = world[row][col];
        world[row][col] = temp;

    }

    private int redNeighbours(Actor[][] world, int row, int col) {
        int redNeighbours = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (isValidLocation(world.length, row+i, col+j)) {
                    if (world[row+i][col+j] == Actor.RED)
                        redNeighbours++;
                }
            }
        }

        return redNeighbours;
    }

    private int blueNeighbours(Actor[][] world, int row, int col) {
        int blueNeighbours = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (isValidLocation(world.length, row+i, col+j)) {
                    if (world[row+i][col+j] == Actor.BLUE)
                        blueNeighbours++;
                }
            }
        }

        return blueNeighbours;
    }

    private int totalNeighbours(Actor[][] world, int row, int col) {
        int totalNeighbours = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (isValidLocation(world.length, row+i, col+j)) {
                    if (world[row+i][col+j] == Actor.BLUE || world[row+i][col+j] == Actor.RED)
                        totalNeighbours++;
                }
            }
        }

        return totalNeighbours;
    }

    // Check if inside world
    boolean isValidLocation(int size, int row, int col) {
        return 0 <= row && row < size &&
                0 <= col && col < size;
    }

    private Actor[][] createWorld(Actor[][] world, double redDist, double blueDist, double empty, int nLocations) {

        Actor[][] res = new Actor[(int)Math.sqrt(nLocations)][(int)Math.sqrt(nLocations)];
        out.println(res.length+" | "+res[0].length);
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[i].length; j++) {
                if ((i-1)*res.length+j < redDist*nLocations)
                    res[i][j] = Actor.RED;
                else if ((i-1)*res.length+j >= redDist*nLocations && (i-1)*res.length+j < (blueDist+redDist)*nLocations)
                    res[i][j] = Actor.BLUE;
                else
                    res[i][j] = Actor.NONE;
            }
        }

        return shuffle(res);
    }

    <T> T[][] shuffle(T[][] arr) {
        Random rand = new Random();
        int randomCol = 0;
        int randomRow = 0;

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                randomCol = rand.nextInt(arr.length);
                randomRow = rand.nextInt(arr.length);

                // Replace
                T temp =  arr[i][j];
                arr[i][j] = arr[randomCol][randomRow];
                arr[randomCol][randomRow] = temp;
            }
        }
        return arr;
    }

    // ------- Testing -------------------------------------

    // Here you run your tests i.e. call your logic methods
    // to see that they really work
    void test() {
        // A small hard coded world for testing
       Actor[][] testWorld = new Actor[][]{
                {Actor.RED, Actor.NONE, Actor.BLUE},
                {Actor.NONE, Actor.NONE, Actor.BLUE},
                {Actor.RED, Actor.NONE, Actor.BLUE}
        };
        double th = 0.5;   // Simple threshold used for testing
        /*
        int size = testWorld.length;
        out.println(isValidLocation(size, 0, 0));
        out.println(!isValidLocation(size, -1, 0));
        out.println(!isValidLocation(size, 0, 3));
        out.println(isValidLocation(size, 2, 2));
        /*

         */
        // TODO More tests
        //Actor[][] testWorld = new Actor[30][30];
        //testWorld = createWorld(testWorld, 0.25, 0.25, 0.5, 900);
        out.println(((double)redNeighbours(testWorld, 0, 0)/totalNeighbours(testWorld,0,0)));


        exit(0);
    }

    // Helper method for testing (NOTE: reference equality)
    <T> int count(T[] arr, T toFind) {
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == toFind) {
                count++;
            }
        }
        return count;
    }

    // ###########  NOTHING to do below this row, it's JavaFX stuff  ###########

    double width = 400;   // Size for window
    double height = 400;
    long previousTime = nanoTime();
    final long interval = 450000000;
    double dotSize;
    final double margin = 50;

    void fixScreenSize(int nLocations) {
        // Adjust screen window depending on nLocations
        dotSize = (width - 2 * margin) / sqrt(nLocations);
        if (dotSize < 1) {
            dotSize = 2;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Build a scene graph
        Group root = new Group();
        Canvas canvas = new Canvas(width, height);
        root.getChildren().addAll(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create a timer
        AnimationTimer timer = new AnimationTimer() {
            // This method called by FX, parameter is the current time
            public void handle(long currentNanoTime) {
                long elapsedNanos = currentNanoTime - previousTime;
                if (elapsedNanos > interval) {
                    updateWorld();
                    renderWorld(gc, world);
                    previousTime = currentNanoTime;
                }
            }
        };

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Segregation Simulation");
        primaryStage.show();

        timer.start();  // Start simulation
    }

    // Render the state of the world to the screen
    public void renderWorld(GraphicsContext g, Actor[][] world) {
        g.clearRect(0, 0, width, height);
        int size = world.length;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                double x = dotSize * col + margin;
                double y = dotSize * row + margin;

                if (world[row][col] == Actor.RED) {
                    g.setFill(Color.RED);
                } else if (world[row][col] == Actor.BLUE) {
                    g.setFill(Color.BLUE);
                } else {
                    g.setFill(Color.WHITE);
                }
                g.fillOval(x, y, dotSize, dotSize);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
