package com.mycompany.butter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class App extends Application {

    private String currentWord; // the randomly selected word
    private String correctWord=""; // correct word
    private String wrongWord=""; // wrong word
    private TextField guessField; // the user enters their guess here
    private Text currentWordText; // show the current word (with - for unguessed letters)
    private Text outcomeText; // show the outcome of each guess and the game
    private Text hintText; // show hint
    private Text wrongGuessesText; // show a list of incorrect guesses
    private int wrong_cnt;
    private Text wrongGuessNumberText; // show how many incorrect guesses (or how many guesses remain)
    private final static int MAX_WRONG_GUESSES = 7;
    private static final Color TITLE_AND_OUTCOME_COLOR = Color.rgb(221, 160, 221);
    private static final Color INFO_COLOR = Color.rgb(224, 255, 255);
    private static final Color WORD_COLOR = Color.rgb(224, 255, 255);
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        VBox mainVBox = new VBox();
        mainVBox.setStyle("-fx-background-color: royalblue");
        mainVBox.setAlignment(Pos.CENTER);
        mainVBox.setSpacing(10);

        Text welcomeText = new Text("Welcome to Hangman!");
        welcomeText.setFont(Font.font("Helvetica", FontWeight.BOLD, 36));
        welcomeText.setFill(TITLE_AND_OUTCOME_COLOR);
        Text introText1 = new Text("Guess a letter.");
        Text introText2 = new Text("You can make " + MAX_WRONG_GUESSES + " wrong guesses!");
        introText1.setFont(Font.font("Helvetica", 24));
        introText1.setFill(INFO_COLOR);
        introText2.setFont(Font.font("Helvetica", 24));
        introText2.setFill(INFO_COLOR);

        VBox introBox = new VBox(welcomeText, introText1, introText2);
        introBox.setAlignment(Pos.CENTER);
        introBox.setSpacing(10);
        mainVBox.getChildren().add(introBox);

        // create before game is started
        outcomeText = new Text("");
        guessField = new TextField();
        
        wrongGuessNumberText = new Text("");
        wrongGuessesText = new Text("Wrong Guesses: []");

        currentWord = chooseWord();
        currentWordText = new Text("");
        currentWordText.setFont(Font.font("Helvetica", FontWeight.BOLD, 48));
        currentWordText.setFill(WORD_COLOR);
        HBox currentBox = new HBox(currentWordText);
        currentBox.setAlignment(Pos.CENTER);
        currentBox.setSpacing(10);
        mainVBox.getChildren().add(currentBox);

        hintText = new Text(currentWord);
        hintText.setFont(Font.font("Helvetica", FontWeight.BOLD, 48));
        hintText.setFill(WORD_COLOR);
        HBox hintBox = new HBox(hintText);
        hintBox.setAlignment(Pos.CENTER);
        hintBox.setSpacing(10);
        mainVBox.getChildren().add(hintBox);

        Text guessIntroText = new Text("Enter your guess: ");
        guessIntroText.setFont(Font.font("Helvetica", 26));
        guessIntroText.setFill(INFO_COLOR);
        guessField.setOnAction(this::handleGuessField);
        HBox guessBox = new HBox(guessIntroText, guessField);
        guessBox.setAlignment(Pos.CENTER);
        guessBox.setSpacing(10);
        mainVBox.getChildren().add(guessBox);

        outcomeText.setFont(Font.font("Helvetica", 28));
        outcomeText.setFill(TITLE_AND_OUTCOME_COLOR);
        HBox outcomeBox = new HBox(outcomeText);
        outcomeBox.setAlignment(Pos.CENTER);
        outcomeBox.setSpacing(10);
        mainVBox.getChildren().add(outcomeBox);

        wrongGuessesText.setFont(Font.font("Helvetica", 24));
        wrongGuessesText.setFill(INFO_COLOR);
        HBox wrongGuessesBox = new HBox(wrongGuessesText);
        wrongGuessesBox.setAlignment(Pos.CENTER);
        wrongGuessesBox.setSpacing(10);
        mainVBox.getChildren().add(wrongGuessesBox);
        
        wrongGuessNumberText = new Text("Wrong number:");
        wrongGuessNumberText.setFont(Font.font("Helvetica", 24));
        wrongGuessNumberText.setFill(INFO_COLOR);
        HBox wrongGuessNumberBox = new HBox(wrongGuessNumberText);
        wrongGuessNumberBox.setAlignment(Pos.CENTER);
        mainVBox.getChildren().add(wrongGuessNumberBox);

        Scene scene = new Scene(mainVBox, 550, 500);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    public void init() throws IOException//init the value to replay the game
    {
        currentWord = chooseWord();
        wrong_cnt = 0;
        wrongWord = "";
        correctWord = "";
        
        if(hintText != null)
        {
            hintText.setText(currentWord);
        }
    }
    
    private void replayGame()
    {
       //display confirm message
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game over");
        alert.setHeaderText("");
        alert.setContentText("Do you want to continue?");
        
        Optional<ButtonType> res = alert.showAndWait();//wait for user choose option
        
        if (res.isPresent() && res.get() == ButtonType.OK) {
           try{init();}
           catch(IOException e)
           {
                guessField.setEditable(false);
           }
        } else {
            System.exit(0);
        }
    }
    
    private void updateDisplays()
    {
        String temp = String.format("%1$-" + currentWord.length() + "s", correctWord).replace(' ', '-');
        currentWordText.setText(temp);
        
        if(currentWord.equals(correctWord))
        {
            outcomeText.setText("game won");
            replayGame();
        }
        
        if(wrong_cnt == 7)
        {
            outcomeText.setText("game lost");
            replayGame();
        }
        
        wrongGuessesText.setText("Wrong Guesses:["+wrongWord+"]");
        wrongGuessNumberText.setText("Wrong number:"+wrong_cnt);
        guessField.setText(""); 
    }
    
    public boolean isGuessValid(String guess_text)
    {
        return guess_text.equals(currentWord.substring(correctWord.length(), correctWord.length()+1));
    }
    
    private void handleGuessField(ActionEvent event){
        String guess_text = guessField.getText();
        
        if(isGuessValid(guess_text))
        {
            correctWord = correctWord.concat(guess_text);
            wrongWord = "";//init wrongWord value
            outcomeText.setText("right guess");
        }else{
            
            if(!wrongWord.contains(guess_text))
            {
                wrongWord = wrongWord.concat(guess_text)+" ";
                wrong_cnt++;//increase the wrong number
            }
            
            outcomeText.setText("wrong guess");
        }
        
        updateDisplays();
    }
    
    public String chooseWord() throws IOException {
        String filename = "words.txt";//filename
        ArrayList<String> words = new ArrayList<>();//to store the word
        
        try{
           BufferedReader reader = new BufferedReader(new FileReader(filename));
           String line = reader.readLine();//line

           while (line != null) {
               words.add(line);//we suggest that each line has only one word.
               line = reader.readLine();
           }
           
            Random rand = new Random();
            int randomIndex = rand.nextInt(words.size());//generate random number max size=words.size
            String randomWord = words.get(randomIndex);
            
            return randomWord;
            
        } catch (IOException e) {
           outcomeText = new Text("Error: No dicitionary.");//No dictionary
           guessField.setEditable(false);
       }
        
        return "";
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}