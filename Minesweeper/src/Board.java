import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Board {
  private int boardSize;
  private Square[][] board;
  //How many squares have been revealed, to check victory.
  public static int revealedSquares;
  //All the squares minus the padding squares.
  private int totalActiveSquares;
  //How many squares are flagged.
  public static int flaggedSquares;
  //Count first for flags to check for supermine's "super reveal".
  private int flagTries;
  
  public Board (Boolean isDifficult, int numMines, Boolean isSupermine) {
    //Set the size of the square board, according to level of difficulty.
    boardSize = isDifficult ? 16 : 9;
		revealedSquares = 0;
    totalActiveSquares = boardSize * boardSize;
    flaggedSquares = 0;
    flagTries = 0;

    try {
      //Create the mines.txt file.
      File myNewFile = new File("..//mines.txt");
      myNewFile.createNewFile();
      
    } catch (IOException e) {
      System.out.println("An error occurred while creating the board!");
      e.printStackTrace();
    }

    try {
      //Initialize the minefield.
      List<String> minePos = new ArrayList<String>();      
      minePos = plantMinesRand(boardSize, numMines, isSupermine);
      
      //Make a support array, that keeps the mine positions.
      int[][] minePosArray = new int[numMines][3]; 

      FileWriter myWriter = new FileWriter("..//mines.txt");
      for (int i=0; i < minePos.size(); i++) {
        //Write the data in mine.txt.
        myWriter.write(minePos.get(i) + "\n");

        //Slit the string and converted to 3 integers.
        String[] stringArray = minePos.get(i).split(",", 3);
        minePosArray[i][0] = Integer.parseInt(stringArray[0]);
        minePosArray[i][1] = Integer.parseInt(stringArray[1]);
        minePosArray[i][2] = Integer.parseInt(stringArray[2]);
      }
      
      myWriter.close();

      //Set the board with all the input info given.
      createBoard(minePosArray);

     } catch (IOException e) {
         System.out.println("An error occurred while initializing the board!");
         e.printStackTrace();
     }  
  }

  //Method that initializes the random positions of the mines.
  private List<String> plantMinesRand(int boardSize, int numMines, Boolean isSupermine) {
    List<String> minefield = new ArrayList<String>();

    Random rand = new Random();
    
    //Set the Super Mine if there is any.
    int isSuper = -1;
    if (isSupermine) {
      isSuper = rand.nextInt(numMines);
    }

    for (int i = 0; i < numMines; i++) {
      String position = "";
      List<String> takenPosList = new ArrayList<String>();

      //Define posTaken as true until an empty position is found.
      Boolean posTaken = true;

      while(posTaken) {
        //Select 2 random numbers as coordinates. Add 1 to due to the padding layer.
        int pos_x = rand.nextInt(boardSize) + 1;
        int pos_y = rand.nextInt(boardSize) + 1;

        position = String.valueOf(pos_x) + "," + String.valueOf(pos_y);
        
        //Check if the position is empty.
        posTaken = checkPosTaken(position, takenPosList);
      }  
      
      //Declare the new position as taken.
      takenPosList.add(position);

      //Check if the new mine position is a Surermine and add it to the minefield.
      String mineStr = "";
      if (i == isSuper) {
        mineStr = position + ",1";
      } else {
        mineStr = position + ",0";
      }

      minefield.add(mineStr);
    }

    return minefield;
  }

  private Boolean checkPosTaken(String position, List<String> minefield) {
    if (minefield.contains(position)) {
      return true;
    } else {
      return false;
    }
  }

  //Create the 2D board list and its squares. 
  //Adding a "padding layer" around the board, helps us avoid less than 8 neighbors per square.
  private void createBoard(int[][] minePosArray) {
    board = new Square[boardSize + 2][boardSize + 2];

    for (int i=0; i <= boardSize+1; i++) {
      for (int j=0; j <= boardSize+1; j++) {
        board[i][j] = new Square();

        if (i == 0 || i == boardSize+1 || j == 0 || j == boardSize+1) {
          board[i][j].squareState = SquareState.PADDING;
        }
      }
    }

    for (int i=1; i <= boardSize; i++) {
      for (int j=1; j <= boardSize; j++) {
        Square sqr = board[i][j];

        //Array with the 8 neighboring squares.
        Square[] neighbors = new Square[8];
        neighbors[0] = board[i-1][j-1];
        neighbors[1] = board[i-1][j];
        neighbors[2] = board[i-1][j+1];
        neighbors[3] = board[i][j-1];
        neighbors[4] = board[i][j+1];
        neighbors[5] = board[i+1][j-1];
        neighbors[6] = board[i+1][j];
        neighbors[7] = board[i+1][j+1];

        sqr.neighborSquares(neighbors);
      }
    }

    //Check if a square is a mine or a supermine and declare its neighbors.
    for (int[] minePos : minePosArray) {
      int row = minePos[0];
      int col = minePos[1];
      int superMine = minePos[2];

      Square[] neighbors = board[row][col].getNeighbors();

      if (superMine == 0) { //Mine.
        //System.out.print(i + "minePosArray:" + Arrays.toString(minePos) + " " + Arrays.toString(a) + "\n");
        board[row][col].squareHiddenValue = SquareHiddenValue.MINE;

        //Add 1 to the minesAround value of each neighbor if sqr is a mine. 
        for (Square neighbor : neighbors) {
            neighbor.minesAround++;
        }

      } else if (superMine == 1) { //Supermine.
        board[row][col].squareHiddenValue = SquareHiddenValue.SUPERMINE;
            
        //Add 1 to the minesAround value of each neighbor if sqr is a mine. 
        for (Square neighbor : neighbors) {
          neighbor.minesAround++;
        }
      }
    }

    printBoard();
  }

  public GameState revealSquare (int row, int col) {
    if ((row>0) && (row<=boardSize) && (col>0) && (col<=boardSize)) {

      //Square already revealed.
      if (board[row][col].squareState == SquareState.REVEALED) {
        System.out.print("This square is already revealed!\n");

        printBoard();
       
      //Do not reveal flagged squares. 
      } else if (board[row][col].squareState == SquareState.FLAGGED) {
        System.out.print("This square is flagged!");

        printBoard();
      
      } else {
        board[row][col].squareState = SquareState.REVEALED;
        revealedSquares++;

        //If mine the game is lost.
        if (board[row][col].squareHiddenValue == SquareHiddenValue.MINE || 
            board[row][col].squareHiddenValue == SquareHiddenValue.SUPERMINE) {
          
          //The game is lost, show solution with the "detonated" mine marked.
          revealBoard(row, col);

          System.out.print("This square was a mine.\n");

          return GameState.LOSS;

        //If not mine.
        } else {
          
          //If square has no neighboring mines, start recursive revealing the neighbors.
          if (board[row][col].minesAround == 0) {
            System.out.print(board[row][col].minesAround);
            board[row][col].recursiveReveal();
          }
          
          printBoard();

          if (revealedSquares == totalActiveSquares - Game.numMines) {
            return GameState.VICTORY;
          }

          return GameState.PENDING;
        }
      }
          
    } else {
      System.out.print("The coordinates are invalid!\n");
    }

    return GameState.PENDING;
  }

  public void flagSquare (int row, int col) {
    
    if ((row>0) && (row<=boardSize) && (col>0) && (col<=boardSize)) {
      
      //If square is revealed.
      if (board[row][col].squareState == SquareState.REVEALED) {
        System.out.print("This square is revealed, you can not flag it!\n");

        printBoard();
       
      //Unflag square. 
      } else if (board[row][col].squareState == SquareState.FLAGGED) {
        board[row][col].squareState = SquareState.DEFAULT;
        flaggedSquares--;

        printBoard();

      } else {

        //Flags == mines.
        if (flaggedSquares == Game.numMines) {
          System.out.print("\nThere are no flags left!\n");
        
        } else {
          board[row][col].squareState = SquareState.FLAGGED;
          flaggedSquares++;
          flagTries++;
            
          //If supermine in first 4 tries.
          if (board[row][col].squareHiddenValue == SquareHiddenValue.SUPERMINE && flagTries < 5) {
            
            //Reveal whole row and column.
            for (int i=1; i <= boardSize; i++) {
              board[i][col].squareState = SquareState.REVEALED;
              board[row][i].squareState = SquareState.REVEALED;
            }
          }
        }

        printBoard();
      }
          
    } else {
      System.out.print("The coordinates are invalid!\n");
    }
  }

  //revealBoard for "detonated" mine.
  public void revealBoard(int row, int col) {
    for (int i=0; i <= boardSize; i++) {
      for (int j=0; j <= boardSize; j++) {
        
        //Print the axis.
        if (i == 0 && j == 0) {
          System.out.print("\nMS|");
        
        } else if (i == 0) {
          System.out.print(String.format("%02d|", j));
        
        } else if (j == 0) {
          System.out.print(String.format("%02d|", i));
        
        //Print the squares.
        } else if (row == i && col == j) { //The detonated mine.
          System.out.print(" # ");
        
        } else if (board[i][j].squareHiddenValue == SquareHiddenValue.MINE) {
          System.out.print(" x ");
        
        } else if (board[i][j].squareHiddenValue == SquareHiddenValue.SUPERMINE) {
          System.out.print(" x ");
        
        } else if (board[i][j].minesAround == 0) {
          System.out.print(" 0 ");
        
        } else {
          System.out.print(" " + board[i][j].minesAround + " ");
        }
      }
      System.out.print("\n");
    }
  }

  //revealBoard for solution.
  public void revealBoard() {
    for (int i=0; i <= boardSize; i++) {
      for (int j=0; j <= boardSize; j++) {
        
        //Print the axis.
        if (i == 0 && j == 0) {
          System.out.print("\nMS|");
        
        } else if (i == 0) {
          System.out.print(String.format("%02d|", j));
        
        } else if (j == 0) {
          System.out.print(String.format("%02d|", i));
        
        //Print the squares.
        } else if (board[i][j].squareHiddenValue == SquareHiddenValue.MINE) {
          System.out.print(" x ");
        
        } else if (board[i][j].squareHiddenValue == SquareHiddenValue.SUPERMINE) {
          System.out.print(" x ");
        
        } else if (board[i][j].minesAround == 0) {
          System.out.print(" 0 ");
        
        } else {
          System.out.print(" " + board[i][j].minesAround + " ");
        }
      }
      System.out.print("\n");
    }
  }

  //Print the board on terminal.
  public void printBoard() {

    for (int i=0; i <= boardSize; i++) {
      for (int j=0; j <= boardSize; j++) {
        
        //Print the axis.
        if (i == 0 && j == 0) {
          System.out.print("\nMS|");
        
        } else if (i == 0) {
          System.out.print(String.format("%02d|", j));
        
        } else if (j == 0) {
          System.out.print(String.format("%02d|", i));
        
        //Print the squares.
        } else if (board[i][j].squareState == SquareState.DEFAULT) {
          System.out.print(" ? ");
        
        } else if (board[i][j].squareState == SquareState.FLAGGED) {
          System.out.print(" F ");
        
        } else if (board[i][j].squareState == SquareState.REVEALED) {
          
          //If already revealed print the hidden value.
          if (board[i][j].squareHiddenValue == SquareHiddenValue.MINE) {
            System.out.print(" x ");
          
          } else if (board[i][j].squareHiddenValue == SquareHiddenValue.SUPERMINE) {
            System.out.print(" x ");
          
          } else if (board[i][j].minesAround == 0) {
            System.out.print(" 0 ");
          
          } else {
            System.out.print(" " + board[i][j].minesAround + " ");
          }
        }
      }
      System.out.print("\n");
    }
  }
}
