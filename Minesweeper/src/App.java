import java.util.*; 
import java.io.File;

public class App {

    public static void main(String[] args) 
    throws Exception {

        String filepath = "";
        int action = -1;
        //Terminal Intreface
        System.out.print("\n Welcome! \n");

        try {
            
            Scanner sc = new Scanner(System.in);

            while (filepath == "" && action != 0) {
                //Print MENU
                System.out.print("\n(There is no game file loaded)");
                System.out.print("\nSelect action from the MENU:" +
                                "\n~0~ END GAME - (input: 0)" +
                                "\n~1~ LOAD GAME - (input: 1)" +
                                "\nAction: ");
                
                action = sc.nextInt();

                if (action == 0) {
                //Terminate game
                System.out.print("\n Thank you for playing! \n");

                } else if (action == 1) {
                    System.out.print("\nLoad game file (without .txt): ");
                    String fileName = sc.next();

                    //Define in which file the game info is.
                    filepath = "..\\..\\medialab\\" + fileName + ".txt"; 
                    System.out.print("\npath: " + filepath);

                } else {
                    System.out.print("\n Wrong input, try again! \n");
                }
            }

            while (action != 0) {
                System.out.print("\nSelect action from the MENU:" +
                                    "\n~0~ END GAME - (input: 0)" +
                                    "\n~1~ LOAD GAME - (input: 1)" +
                                    "\n~2~ START GAME - (input: 2)" +
                                    "\nAction: ");
                
                action = sc.nextInt();

                if (action == 0) {
                    //Terminate game
                    System.out.print("\n Thank you for playing! \n");
            
                } else if (action == 1) {
                    System.out.print("\nLoad game file (without .txt): ");
                    String fileName = sc.next();

                    //Define in which file the game info is.
                    filepath = "..\\..\\medialab\\" + fileName + ".txt"; 
                    
                } else if (action == 2) {
                    //The loaded game starts
                    try {
                        File myFileName = new File(filepath);
                        Game game = new Game(myFileName);
                        
                        System.out.print("\n The game is on! \n");

                        String squareAndAction = "null";
                        while (game.gameState == GameState.PENDING && !squareAndAction.equals("0")) {
                            
                            //The player selects action and square to apply the action.
                            try {
                                int act = -1;

                                //Print options.
                                System.out.print("\nSelect action:" +
                                                    "\n~0~ BACK TO MENU - (input: 0)" +
                                                    "\n~1~ REVEAL SQUARE - (input: 1)" +
                                                    "\n~2~ FLAG/UNFLAG SQUARE - (input: 2)" +
                                                    "\n~3~ SOLUTION - (input: 3)" +
                                                    "\nAction: ");
                                
                                act = sc.nextInt();

                                if (act == 0) {
                                    //Back to menu.
                                    squareAndAction = "0";
                                    
                                } else if (act == 1) {
                                    System.out.print("\nReveal square on:\n");
                                    System.out.print("Row = ");
                                    String row = sc.next();
                                    System.out.print("Column = ");
                                    String col = sc.next();

                                    squareAndAction = row + "," + col + "," + act;
                                    game.isActValid(squareAndAction);

                                } else if (act == 2) {
                                    System.out.print("\nFlag/Unflag square on:\n");
                                    System.out.print("Row = ");
                                    String row = sc.next();
                                    System.out.print("Column = ");
                                    String col = sc.next();

                                    squareAndAction = row + "," + col + "," + act;
                                    game.isActValid(squareAndAction);
                                    
                                } else if (act == 3) {
                                    squareAndAction = "solution";
                                    game.isActValid(squareAndAction);

                                } else {
                                    System.out.print("\n Wrong input, try again! \n");

                                }

                            } catch (Exception e) {
                                System.out.println("\nAn error occurred while trying reading the input, or the input is wrong!");
                                e.printStackTrace();
                            }
                        }

                        if (game.gameState == GameState.LOSS) {
                            System.out.println("\n  YOU LOST   \n");
                        } else if (game.gameState == GameState.VICTORY) {
                            System.out.println("\n  VICTORY   \n");
                        }

                    } catch (Exception e) {
                        System.out.println("\nThe game could not be created, check if you are trying to load a game file with invalid game info!");
                        e.printStackTrace();
                    }

                } else {
                    sc.close();

                    System.out.print("\n Wrong input, try again! \n");
                }
            }

        } catch (Exception e) {
            System.out.println("\nAn error occurred while trying reading the input!");
            e.printStackTrace();
        }
    }
}
