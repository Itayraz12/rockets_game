package com.example.first_assignment.buisnessLogic;



public class GameManager {

    // Constants for initial game settings
    private static final int INITIAL_LIVES = 3;
    private static final int INITIAL_HITS_COUNT = 0;
    private static final String INITIAL_OBSTACLE = "obstacle";
    private final String obstacle = "obstacle";
    private final String nothing = "none";
    private int live;
    private int hits_count = 0;

    public String getObstacle() {
        return obstacle;
    }

    public String getNothing() {
        return nothing;
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

    private String[][] game_mat;

    private int player_position = 1; // start always from the middle position

    private final int Cols;
    private final int Rows;

    //GameManager constructor
    public GameManager(int rows, int cols) {
        // Initialize game state
        this.Rows = rows;
        this.Cols = cols;
        live = INITIAL_LIVES;
        hits_count = INITIAL_HITS_COUNT;
        game_mat = new String[rows][cols];
        initializeGameMatrix(); // Method to initialize game matrix with initial values
        setPlayer_position(cols / 2); // Starting position at the middle column
    }



    private void initializeGameMatrix(){
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
            return true ;
        }else return false;
            
    }

    public void movePlayerIcon(String direction) {
        if (direction.equals("left")) {
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
                    getGame_mat()[i][j] = "none";
                }
                else {
                    getGame_mat()[i][j] = getGame_mat()[i - 1][j];
                }
            }
        }

        //new obstacle
        int randomColumn = (int) (Math.random() * getCols());
        getGame_mat()[0][randomColumn] = getObstacle();


    }


    public boolean detectCollisionAndAdjustStats() {
        int playerColumn = getPlayer_position();
        int secondLastRow = getRows() - 2;
        String[][] gameMatrix = getGame_mat();
        String obstacle = getObstacle();

        if (gameMatrix[secondLastRow][playerColumn].equals(obstacle)) {
            setLive(getLive() - 1);
            setHits_count(getHits_count() + 1);
            return true;
        }
        return false;
    }

    public void resetGame() {
        live = INITIAL_LIVES;
        hits_count = INITIAL_HITS_COUNT;
        initializeGameMatrix();
        player_position = game_mat[0].length / 2; // Reset player position to middle column
    }
}