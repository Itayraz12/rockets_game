package com.example.first_assignment.buisnessLogic;


import static com.example.first_assignment.MainActivity.GOLD_COLLISION;
import static com.example.first_assignment.MainActivity.NO_COLLISION;
import static com.example.first_assignment.MainActivity.OBSTACLE_COLLISION;

public class GameManager {

    // Constants for initial game settings

    public static final int SLOW_SPEED = 1000;
    public static final int FAST_SPEED = 500;
    private static final int INITIAL_LIVES = 3;
    private static final int INITIAL_HITS_COUNT = 0;
    private static final int INITIAL_SCORE = 0;
    private static final String INITIAL_OBSTACLE = "obstacle";
    private final String OBSTACLE = "obstacle";
    private final String NOTHING = "none";
    private final String GOLD = "gold";
    public static final String NO_COLLISION = "none";
    public static final String OBSTACLE_COLLISION = "obstacle";
    public static final String GOLD_COLLISION = "gold";
    private int speed;
    private int live;
    private int score = 0;
    private int hits_count = 0;

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getCoin() {
        return GOLD;
    }

    public String getObstacle() {
        return OBSTACLE;
    }

    public String getNothing() {
        return NOTHING;
    }

    public int getLive() {
        return live;
    }

    public void setLive(int live) {
        this.live = live;
    }

    public int getHits_count() {
        return hits_count;
    }

    public void setHits_count(int hits_count) {
        this.hits_count = hits_count;
    }

    public String[][] getGame_mat() {
        return game_mat;
    }

    public void setGame_mat(String[][] game_mat) {
        this.game_mat = game_mat;
    }

    public int getPlayer_position() {
        return player_position;
    }

    public void setPlayer_position(int player_position) {
        this.player_position = player_position;
    }

    public int getCols() {
        return Cols;
    }

    public int getRows() {
        return Rows;
    }

    public int getScore() {
        return score;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getOBSTACLE() {
        return OBSTACLE;
    }

    public String getNOTHING() {
        return NOTHING;
    }

    public String getGOLD() {
        return GOLD;
    }

    private String[][] game_mat;

    private int player_position = 1; // start always from the middle position

    private final int Cols;
    private final int Rows;

    private  String UserName;

    //GameManager constructor
    public GameManager(String userName, int rows, int cols) {
        // Initialize game state
        this.UserName = userName;
        this.Rows = rows;
        this.Cols = cols;
        this.live = INITIAL_LIVES;
        //hits_count = INITIAL_HITS_COUNT;
        //game_mat = new String[rows][cols];
        initializeGameMatrix(); // Method to initialize game matrix with initial values
        setPlayer_position(cols / 2); // Starting position at the middle column
    }


    private void initializeGameMatrix() {
        this.game_mat = new String[getRows()][getCols()];
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getCols(); j++) {
                this.game_mat[i][j] = getNothing(); // set none in every position
            }
        }
    }


    public boolean isOutOfLife() {
        int currentLifeAmount = getLive();
        if (currentLifeAmount == 0) {
            return true;
        } else return false;

    }

    public void movePlayerIcon(String directionClicked) {
        if (directionClicked.equals("left")) {
            if (getPlayer_position() == 0) {
                return;
            } else {
                setPlayer_position(getPlayer_position() - 1); // current position -1 will move left
            }
        } else {
            if (getPlayer_position() == getCols() - 1) {
                return;
            } else {
                setPlayer_position(getPlayer_position() + 1);
            }
        }
    }


    public void matrixChangePeriod() {
        for (int i = getRows() - 1; i > -1; i--) {
            for (int j = getCols() - 1; j > -1; j--) {
                if (i == 0) {
                    getGame_mat()[i][j] = getNothing();
                } else {
                    getGame_mat()[i][j] = getGame_mat()[i - 1][j];
                }
            }
        }

        // New obstacle
        int randomColumn = (int) (Math.random() * getCols());
        getGame_mat()[0][randomColumn] = getObstacle();

        // New coin
        int randomized_value = (int) (Math.random() * 5);
        int res = (int) (Math.random() * 5);
        if (randomized_value == res) {
            int randomNumberCol;
            do {
                randomNumberCol = (int) (Math.random() * getCols());
            } while (randomNumberCol == randomColumn); // Ensure the coin is not placed where the obstacle is
            getGame_mat()[0][randomNumberCol] = getCoin();
        }
    }
    public boolean detectCollisionAndAdjustStats() {
        if (getGame_mat()[getRows()-2][getPlayer_position()].equals(getObstacle())) {
            setLive(getLive() -1); //
            setHits_count(getHits_count() +1); // increase hits count
            return true;
        }
      return false ;
    }


//    public String detectCollisionAndAdjustStats() {
//        int playerColumn = getPlayer_position();
//        int secondLastRow = getRows() - 2;
//        String[][] gameMatrix = getGame_mat();
//        String obstacle = getObstacle();
//        String goldCoin = getGOLD();
//
//        if (gameMatrix[secondLastRow][playerColumn].equals(obstacle)) {
//            setLive(getLive() - 1);
//            setHits_count(getHits_count() + 1);
//            return OBSTACLE_COLLISION;
//        } else if (gameMatrix[secondLastRow][playerColumn].equals(goldCoin)) {
//            return GOLD_COLLISION;
//        }
//        return NO_COLLISION;
//    }


    public void resetGame() {
            live = INITIAL_LIVES;
            hits_count = INITIAL_HITS_COUNT;
            initializeGameMatrix();
            score = INITIAL_SCORE;
            player_position = game_mat[0].length / 2; // Reset player position to middle column
        }

    public void scoreTimeIncrease() {
        score++;
    }

    public void scoreGoldIncrease() { // every gold coin earn increase score in 3 points
        if (getGame_mat()[getRows() - 2][getPlayer_position()].equals(getGOLD())) {
            score += 3;
        }
    }

    }


