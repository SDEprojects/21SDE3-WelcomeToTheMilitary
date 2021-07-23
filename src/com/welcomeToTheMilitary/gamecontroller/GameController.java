package com.welcomeToTheMilitary.gamecontroller;

import com.welcomeToTheMilitary.bases.BaseMap;

import com.welcomeToTheMilitary.character.Rank;
import com.welcomeToTheMilitary.character.ServiceMember;
import com.welcomeToTheMilitary.json_pack.JsonReader;
import com.welcomeToTheMilitary.minigame.MinigameFactory;
import com.welcomeToTheMilitary.minigame.PTGame;
import com.welcomeToTheMilitary.minigame.iMinigame;
import com.welcomeToTheMilitary.textparser.ParseResponse;
import com.welcomeToTheMilitary.textparser.TextParser;
import com.welcomeToTheMilitary.tutorial.Welcome;
import com.welcomeToTheMilitary.gui.mainDisplay;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class GameController implements java.io.Serializable {


    private static ParseResponse response = null;
    private static TextParser parser = null;
    private static Scanner input = new Scanner(System.in);
    private static ArrayList<String> spellList = new ArrayList<>();
    private static MinigameFactory gameFactory = new MinigameFactory();
    private static iMinigame minigame = null;
    Welcome sepWelcome = new Welcome();

    //instance of SaveAndLoad class
    SaveAndLoad savedGame = new SaveAndLoad();
    SaveAndLoad loadedGame = new SaveAndLoad();


    public GameController() throws IOException, ParseException {
    }

    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        //Generate UI


        BaseMap fortSill = new BaseMap("Fort Sill", "Some post");
        BaseMap fortBliss = new BaseMap("Fort Bliss", "So close to Mexico");

        BaseMap currentMap = fortSill;

        ServiceMember usrSM = Welcome.intro(spellList);
        parser = new TextParser();

        //instance of SaveAndLoad class
        SaveAndLoad savedGame = new SaveAndLoad();
        SaveAndLoad loadedGame = new SaveAndLoad();

        //Saved method
        savedGame.saveGame();
        //Loaded method
        loadedGame.loadGame();

        // below this line while loop
        String userAction = "";
        int counter = 0;
        while (!userAction.equals("exit") && !userAction.equals("quit") && !userAction.equals("save")) {
            // condition that checks if the player's rank is E-6 then it invoke the challenge against the boss
            if (usrSM.getRank().equals(Rank.E6) && (usrSM.getPostName().equals("Fort Sill"))) {
                System.out.println("Reached E-6...");
                System.out.println("Final Challenge!");
                minigame = gameFactory.playGame("boss game");
                boolean isWon = minigame.play(usrSM);
                // if player won
                if (isWon) {
                    Welcome.separatorTitle();
                    System.out.println("Your journey in Fort Sill is over soldier..");
                    usrSM.setPostName("Fort Bliss");
                    currentMap = fortBliss;
                    usrSM.setLocation(currentMap.getName());
                    Welcome.separatorTitle();
                    counter = 9000000;
//                    break;
                } else {
                    // lost in fort sill
                    Welcome.separatorTitle();
                    System.out.println("You challenge your sergeant you lose\nKick out");
                    System.out.println("Game over");
                    Welcome.separatorTitle();
                    System.exit(0);
                }
            } else if (usrSM.getRank().equals(Rank.E9) && (usrSM.getPostName().equals("Fort Bliss"))) {
                System.out.println("Reached E-9...");
                System.out.println("Final Challenge!");
                minigame = gameFactory.playGame("boss game");
                boolean isWon = minigame.play(usrSM);
                // if player won
                if (isWon) {
                    Welcome.separatorTitle();
                    System.out.println("Your journey in Fort Bliss is over soldier..");
                    System.out.println("You completed the game\nYou won");
                    Welcome.separatorTitle();
                    System.exit(0);
                } else {
                    // lost in fort sill
                    Welcome.separatorTitle();
                    System.out.println("You challenge your sergeant major you lose\nKick out");
                    System.out.println("Game over");
                    Welcome.separatorTitle();
                    System.exit(0);
                }
            }
            if (counter == 0) {
                Welcome.separatorTitle();
                System.out.println("Welcome to Fort Sill. Your Drill Instructor dropped you off at the gate.");
                Welcome.separatorTitle();
            } else if (counter == 9000000) {
                Welcome.separatorTitle();
                System.out.println("You beat the boss. You are now PCS'ed to Fort Bliss.");
                Welcome.separatorTitle();
            }
            //display persistent information for players to track
            System.out.println("~~~~ " + usrSM.getName() + " Rank " + usrSM.getRank().getAbbreviation());
            System.out.println("~~~~ Service Member Health = " + usrSM.getHealth() + " ~~~~");
            System.out.println("~~~~ Current Inventory " + usrSM.getItems());
            System.out.println("~~~~ Current Location = " + usrSM.getLocation() + " ~~~~");
            System.out.println("~~~~ Special abilities " + usrSM.getSpecial() + " ~~~~");
            System.out.println("Enter your action [format= verb + noun] for help type (help me)\n" + "-".repeat(50));
            userAction = input.nextLine();
            response = parser.receiveAction(userAction, usrSM.getPostName());
            if (!(response.getVerb().equals("")) || !(response.getNoun().equals(""))) {
                try {
                    switch (response.getVerb().trim()) {
                        case "go":
                            Display.enteringBuildingController(response.getNoun(), usrSM, currentMap);
                            break;
                        case "show":
                            Display.showController(response.getNoun(), usrSM, currentMap);
                            break;
                        case "talk":
                            Interactions.interactWithNPC(response.getNoun(), usrSM, currentMap);
                            break;
                        case "help":
                            HelpmeHelper.interactHelpRequest(response.getNoun(), usrSM);
                            break;
                        case "request":
                            applyToPcs();
                            break;
                        case "use":
                            usrSM.useItem(response.getNoun());
                            break;
                        case "do":
                            if (response.getNoun().equals("pt")){
                                PTGame game = new PTGame();
                                game.doPt(usrSM);
                            }
                            break;
                        case "jun":
                            System.out.println("Good job");
                            System.out.println("You WON");
                            System.exit(0);

                        case "save":
                            System.out.println("Saving game...");
                            savedGame.saveGame();
                            break;
                        case "load":
                            System.out.println("Loading game...");
                            loadedGame.loadGame();
                            break;
                        default:
                            System.out.println("Verb " + response.getVerb());
                            System.out.println("Noun: " + response.getNoun());
                            break;
                    }
                } catch (Exception e) {
                    System.out.println("Invalid action: type 'help me' to get info");
                    e.printStackTrace();
                } // end of try-catch
            }// end of try-catch
            counter++;
        } // end of if statement
    } // end of while loop

    // private method to get possible buildings for pcs
    private static void applyToPcs() throws IOException, ParseException, InterruptedException {
        JsonReader jsonReader = new JsonReader();
        System.out.println("Please type the post you would like to move to: ");
        System.out.println(jsonReader.getLocations());
        Scanner pcsInput = new Scanner(System.in);
        pcsInput.next();
        Thread.sleep(800);
        System.out.println("Sorry to inform you but your application has been denied");
    }
}