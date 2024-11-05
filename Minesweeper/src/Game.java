import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Game {
	public GameState gameState;
	private Board board;
	//private timeLeft;

	//First Level -> isDifficult==0 and Second Level -> isDifficult==1.
	private Boolean isDifficult;
	public static int numMines;
	private int gameTime;
	private Boolean isSupermine;

	 //The Game Constructor.
	 public Game(File myFileName) 
	 throws InvalidDescriptionException, InvalidValueException{
		//The game starts as pending.
		this.gameState = GameState.PENDING;
		//Take the game info from medialab folder.
		readGameInfo(myFileName);
		//Create the board
		board = new Board(isDifficult, numMines, isSupermine);	
	 }

	//Method that reads the SENARIO files.
	public void readGameInfo(File myFileName) 
	throws InvalidDescriptionException, InvalidValueException {
	  	try {
			Scanner myReader = new Scanner(myFileName);
		
			//Read first line (difficulty).
			int firstLineInt;
			try {
				if (myReader.hasNextLine()) {
					String firstLine = myReader.nextLine();
					firstLineInt = Integer.parseInt(firstLine);
				} else {
					myReader.close();
					throw new InvalidDescriptionException("No info/lines in the file!");
				}
			} catch (NumberFormatException ex) {
				myReader.close();
				throw new InvalidValueException("Data on line 1 is invalid, or an error occured while trying reading it!");
			}
			
			//If difficulty is level 1.
			if (firstLineInt == 1) {
				isDifficult = false;

				//Read second line (numMines).
				int secondLineInt;
				try {
					if (myReader.hasNextLine()) {
						String secondLine = myReader.nextLine();
						secondLineInt = Integer.parseInt(secondLine);
					} else {
						myReader.close();
						throw new InvalidDescriptionException("Less info/lines than expected in file!");
					}
				} catch (NumberFormatException ex) {
					myReader.close();
					throw new InvalidValueException("Data on line 2 is invalid, or an error occured while trying reading it!");
				}

				//Read third line (gameTime).
				int thirdLineInt;
				try {
					if (myReader.hasNextLine()) {
						String thirdLine = myReader.nextLine();
						thirdLineInt = Integer.parseInt(thirdLine);
					} else {
						myReader.close();
						throw new InvalidDescriptionException("Less info/lines than expected in file!");
					}
				} catch (NumberFormatException ex) {
					myReader.close();
					throw new InvalidValueException("Data on line 3 is invalid, or an error occured while trying reading it!");
				}

				//Read fourth line (superMine).
				int fourthLineInt;
				try {
					if (myReader.hasNextLine()) {
						String fourthLine = myReader.nextLine();
						fourthLineInt = Integer.parseInt(fourthLine);
					} else {
						myReader.close();
						throw new InvalidDescriptionException("Less info/lines than expected in file!");
					}
				} catch (NumberFormatException ex) {
					myReader.close();
					throw new InvalidValueException("Data on line 4 is invalid, or an error occured while trying reading it!");
				}

				//Check validity of values: (9<=nunMines<=11 && 120<=gameTime<=180 && superMine == 0)
				if ((secondLineInt >= 9) && (secondLineInt <= 11) && (thirdLineInt >= 120) && (thirdLineInt <= 180) 
					&& (fourthLineInt == 0)) {
					
					numMines = secondLineInt;
					gameTime = thirdLineInt;
					isSupermine = false;

					if (myReader.hasNextLine()) {
						myReader.close();
						throw new InvalidDescriptionException("More lines than expected in file!");
					}

				} else {
					myReader.close();
					throw new InvalidValueException("Data on line 2 or 3 or 4 is invalid (out of range)!");
				}
		
			//If difficulty is level 2.
			} else if (firstLineInt == 2) {
				isDifficult = true;

				//Read second line (numMines).
				int secondLineInt;
				try {
					if (myReader.hasNextLine()) {
						String secondLine = myReader.nextLine();
						secondLineInt = Integer.parseInt(secondLine);
					} else {
						myReader.close();
						throw new InvalidDescriptionException("Less info/lines than expected in file!");
					}
				} catch (NumberFormatException ex) {
					myReader.close();
					throw new InvalidValueException("Data on line 2 is invalid, or an error occured while trying reading it!");
				}

				//Read third line (gameTime).
				int thirdLineInt;
				try {
					if (myReader.hasNextLine()) {
						String thirdLine = myReader.nextLine();
						thirdLineInt = Integer.parseInt(thirdLine);
					} else {
						myReader.close();
						throw new InvalidDescriptionException("Less info/lines than expected in file!");
					}
				} catch (NumberFormatException ex) {
					myReader.close();
					throw new InvalidValueException("Data on line 3 is invalid, or an error occured while trying reading it!");
				}

				//Read fourth line (superMine).
				int fourthLineInt;
				try {
					if (myReader.hasNextLine()) {
						String fourthLine = myReader.nextLine();
						fourthLineInt = Integer.parseInt(fourthLine);
					} else {
						myReader.close();
						throw new InvalidDescriptionException("Less info/lines than expected in file!");
					}
				} catch (NumberFormatException ex) {
					myReader.close();
					throw new InvalidValueException("Data on line 4 is invalid, or an error occured while trying reading it!");
				}

				//Check validity of values: (35<=nunMines<=45 && 240<=gameTime<=360 && (superMine == 0||superMine == 1))
				if ((secondLineInt >= 35) && (secondLineInt <= 45) && (thirdLineInt >= 240) && (thirdLineInt <= 360) 
					&& ((fourthLineInt == 0) || (fourthLineInt == 1))) {
					
					numMines = secondLineInt;
					gameTime = thirdLineInt;
					
					if (fourthLineInt == 0) {
						isSupermine = false;
					} else if (fourthLineInt == 1) {
						isSupermine = true;
					}

					if (myReader.hasNextLine()) {
						myReader.close();
						throw new InvalidDescriptionException("More lines than expected in file!");
					}

				} else {
					myReader.close();
					throw new InvalidValueException("Data on line 2 or 3 or 4 is invalid (out of range)!");
				}
			} else {
				myReader.close();
				throw new InvalidValueException("Data on line 1 is invalid!");
			}
			
			//If all expected information is taken, close the file.
		  	myReader.close();

		} catch (FileNotFoundException e) {
		  	System.out.println("\nThis file does not exist!");
		}
	}

	//Check if input is valid.
	public void isActValid(String squareAndAct) {

		//If solution.
		if (squareAndAct == "solution"){
			board.revealBoard();
			this.gameState = GameState.LOSS;

		} else {
			//Split the string and converted to 3 integers.
			String[] squareAndActArray = squareAndAct.split(",", 3);
			
			try {
				int row = Integer.parseInt(squareAndActArray[0]);
				int col = Integer.parseInt(squareAndActArray[1]);
				int action = Integer.parseInt(squareAndActArray[2]);
				
				switch (action) {
					case 1:
						this.gameState = board.revealSquare(row, col);
						break;
				
					case 2:    
						board.flagSquare(row, col);
						break;
				}


			} catch (Exception e) {
				System.out.println("\nThe input is wrong!");
				e.getStackTrace();
			}
		}	
	}
}
