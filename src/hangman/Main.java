package hangman;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.*;


public class Main extends Application implements Initializable {

    @FXML
    private Label availability;
    @FXML
    private Label points;
    @FXML
    private Label percentage;
    @FXML
    private GridPane gridWord;
    @FXML
    private GridPane gridLetters;
    @FXML
    private ImageView imageHangman;

    private String DictionaryID = null;
    private String previousDictionaryID = null;
    private String value = null;
    private String word=null;
    private List<String> dictionaryList = null;
    private int tries=6;
    private float numOfChoices=0;
    private Button previousBtn = new Button();
    private boolean disabledButton = false;
    List<List<String>> previousGames = new ArrayList<>();
    private boolean playingGame = false;



    public void setAvailableWords(int numOfWords){ this.availability.setText(String.valueOf(numOfWords)); }
    public void setPoints(int points){ this.points.setText(String.valueOf(points)); }
    public void setPercentage(float rounds, float rights){
        float percentage = (rights/rounds)*100;
        this.percentage.setText(String.format("%.2f",percentage) + "%");
    }


    public static void throwAlert(String exceptionID, String errMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (exceptionID==null){
            alert.setTitle("Error");
        }
        else {
            alert.setTitle(exceptionID + " Error");
        }
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(errMessage);
        alert.showAndWait();
    }

    public void reloadDictionary() {
        try {
            dictionaryList = DictionaryChoice.DictionaryChoice(DictionaryID);
            int availableWords = dictionaryList.size();
            setAvailableWords(availableWords);

        }
        catch (Exception e){
            throwAlert(null, "The game continues with the previous Dictionary.");
            DictionaryID = previousDictionaryID;
        }
    }

    public void loadDictionary() {
        TextInputDialog lDialog = new TextInputDialog("Dictionary_ID");
        lDialog.setHeaderText("Enter the Dictionary ID:");
        lDialog.showAndWait();
        previousDictionaryID = DictionaryID;
        DictionaryID = lDialog.getEditor().getText();
        try {
            dictionaryList = DictionaryChoice.DictionaryChoice(DictionaryID);
            int availableWords = dictionaryList.size();
            setAvailableWords(availableWords);
        } catch (IOException e) {
            throwAlert("Non-existent Dictionary", "This dictionary does not exist. You have to create it!");
        }
    }

    public void showWordLengthPercentage(){
        if (dictionaryList==null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Error");
            alert.setGraphic(null);
            alert.setContentText("You have to load or create a dictionary!");
            alert.showAndWait();
        }
        else {
            float[] lengthPercentage = HowManyLetters.countNumOfLetters(dictionaryList);
//            System.out.printf("%.2f%n",lengthPercentage[0]);
//            System.out.println(lengthPercentage[1]);
//            System.out.println(lengthPercentage[2]);
            Dialog dicDetails = new Dialog();
            dicDetails.setHeaderText("Dictionary Details");
            dicDetails.setContentText("Total words: "+dictionaryList.size()+"\n\n"+
                    "Words of 6 letters: "+String.format("%.2f",lengthPercentage[0])+ "%\n"+
                    "Words of 7 to 9 letters: "+String.format("%.2f",lengthPercentage[1])+"%\n"+
                    "Words of 10 or more letters: " + String.format("%.2f",lengthPercentage[2])+"%");
            dicDetails.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE));
            dicDetails.showAndWait();
        }
    }


    public void createDictionary() throws IOException {
        TextInputDialog lDialog = new TextInputDialog("Dictionary_ID");
        lDialog.setHeaderText("Enter the Dictionary ID:");
        lDialog.showAndWait();
        previousDictionaryID = DictionaryID;
        DictionaryID = lDialog.getEditor().getText();
        boolean flag = false;
        try {
            value = HttpRequest.HttpRequestMethod(DictionaryID);
        } catch (IOException e) {
            throwAlert(null, "Please give a valid dictionary ID");
            flag = true;
        }
        if (!flag){
            try {
                FileCreator.FileCreatorMethod(value, DictionaryID);
            }
            catch(UndersizeException e){
                throwAlert("Undersized Exception","Dictionary has less than 20 words!");
            }
            catch(UnbalancedException e){
                throwAlert("Unbalanced Exception","Less than 20% of words have 9 or more letters");
            }
            try {
                dictionaryList = DictionaryChoice.DictionaryChoice(DictionaryID);
                int availableWords = dictionaryList.size();
                setAvailableWords(availableWords);
            }
            catch (Exception e){
                throwAlert(null, "Please give a different dictionary ID");
            }
        }
    }

    public void start() throws IOException {
            if (playingGame){
                reloadDictionary();
            }
        try {
            playTheGame();
        }
        catch(Exception e){
            throwAlert(null, "You have to load or create a dictionary!");
        }
    }

    public void exitGame(){ Platform.exit(); }



    public void playTheGame(){
        numOfChoices = 0;
        tries=6;
        word = WordChoice.WordChoice(dictionaryList);
        int availableWords = dictionaryList.size();
        setAvailableWords(availableWords);
        Image newImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("png/1.png")));
        imageHangman.setImage(newImage);
        playingGame = true;
        gridWord.getChildren().clear();
        gridLetters.getChildren().clear();
        char[] rightWord = new char[word.length()];
        setPoints(0);
        setPercentage(1,0);
        int lengthOfWord = word.length();
        for (int i=0; i<lengthOfWord; i++){
            Button b = new Button("_");
            b.setPrefSize(50,50);
            b.setId(String.valueOf(i));
            b.setOnAction(actionEvent -> {
                gridLetters.getChildren().clear();
                createLettersButtons(b, word, dictionaryList, rightWord);
            });
            gridWord.add(b,i,0,1, 1);
        }
    }

    public void createLettersButtons(Button b, String word, List<String> dictionaryList, char[] rightWord){
        if ((previousBtn!=b) && (!disabledButton)){
            previousBtn.setStyle("-fx-border-color: rgba(0,140,255,0); -fx-border-radius: 2");
        }
        previousBtn = b;
        disabledButton = false;
        b.setStyle("-fx-border-color: rgba(0,140,255,0.94); -fx-border-radius: 2");
        gridLetters.getChildren().clear();
        Label lblLetter = new Label();
        lblLetter.setText("Possible Letter");
        lblLetter.setStyle("-fx-font-weight: 600; -fx-font-size: 14");
        Label lblProbability = new Label();
        lblProbability.setText("Probability");
        lblProbability.setStyle("-fx-font-weight: 600; -fx-font-size: 14");
        gridLetters.add(lblLetter,0,0,1,1);
        gridLetters.add(lblProbability,1,0,1,1);
        List<Map<Character, Float>> ListOfProbabilityPerSpace = Round.FilteringDictionaryList(word, dictionaryList);
        int availableWords = dictionaryList.size();
        setAvailableWords(availableWords);
        int position = Integer.parseInt(b.getId());
        int i=0;
        for (Map.Entry<Character, Float> entry : ListOfProbabilityPerSpace.get(position).entrySet()) {
            Button btn = new Button(entry.getKey().toString());
            Label lbl = new Label(String.format("%.0f", (entry.getValue())*100)+"%");
            btn.setPrefSize(30, 30);
            lbl.setPrefSize(60, 30);
            btn.setId(String.valueOf(i));
            btn.setOnAction(actionEvent -> choosingLetter(b, word, btn, dictionaryList, ListOfProbabilityPerSpace, rightWord));
            gridLetters.add(btn, 0, i+1, 1, 1);
            gridLetters.add(lbl, 1, i+1, 1, 1);
            i++;
        }
    }

    private void choosingLetter(Button b, String word, Button btn, List<String> dictionaryList, List<Map<Character, Float>> ListOfProbabilityPerSpace, char[] rightWord) {
        numOfChoices++;
        int points = Integer.parseInt(this.points.getText());
        int position = Integer.parseInt(b.getId());
        char c = btn.getText().charAt(0);
        boolean foundedChar = CheckTheGivenChar.CheckTheGivenCharMethod(word, dictionaryList, c, position);
        points = Points.CalculatePoints(foundedChar, c, points, ListOfProbabilityPerSpace.get(position));
        setPoints(points);
        if (!foundedChar){
            tries--;
            changeImage();
            if (tries==0){
                youLost(word, points);
                Label loser = new Label("RIP. \nHOPE THAT THE NEXT \nTIME YOU WILL BE LUCKIER");
                loser.setStyle("-fx-font-weight: 600; -fx-font-size: 15");
                gridLetters.getChildren().clear();
                gridLetters.add(loser,0,0,1,1);
                Image newImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("png/loser.png")));
                imageHangman.setImage(newImage);
                setPreviousGames(word, points, "Enemy");
                gridWord.getChildren().clear();
                for(int i=0; i<word.length(); i++){
                    Button buttons = new Button(String.valueOf(word.charAt(i)));
                    buttons.setPrefSize(50,50);
                    buttons.setStyle("-fx-base: rgba(90,94,103,0.67); -fx-text-fill: rgba(255,255,255,0.86); -fx-font-weight: 700; -fx-font-size: 20");
                    buttons.setDisable(true);
                    gridWord.add(buttons,i,0,1, 1);
                }
                playAgain();
            }
            else {
                Dialog WrongAnswer = new Dialog();
                WrongAnswer.setHeaderText("Wrong Answer");
                if (tries>1) {
                    WrongAnswer.setContentText("Bad Choice. It's not the letter: " + btn.getText() +
                            "\nYou have " + tries + " more tries!");
                }
                else{
                    WrongAnswer.setContentText("Bad Choice. It's not the letter: " + btn.getText() +
                            "\nYou have one last try!");
                }
                WrongAnswer.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE));
                WrongAnswer.showAndWait();
                setPercentage(numOfChoices, numOfChoices-(6-tries));
                createLettersButtons(b, word, dictionaryList, rightWord);
            }
        }
        else{
            setPercentage(numOfChoices, numOfChoices-(6-tries));
            rightWord[position] = c;
            b.setText(String.valueOf(c));
            b.setStyle("-fx-base: rgba(0,153,255,0.91); -fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 20");
            gridLetters.getChildren().clear();
            b.setDisable(true);
            Label lbl = new Label("Good jod!\nYou found the right letter: " + word.charAt(Integer.parseInt(b.getId())));
            lbl.setStyle("-fx-font-weight: 600; -fx-font-size: 15");
            gridLetters.add(lbl,0,0,1,1);
            disabledButton = true;
            if (String.valueOf(rightWord).equals(word)){
                youWon(word, points);
                Label loser = new Label("CONGRATULATIONS! \nYOU ARE THE BIG WINNER! \nCAN YOU DO IT AGAIN?");
                loser.setStyle("-fx-font-weight: 600; -fx-font-size: 15");
                gridLetters.getChildren().clear();
                gridLetters.add(loser,0,0,1,1);
                Image newImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("png/winner.png")));
                imageHangman.setImage(newImage);
                setPreviousGames(word, points, "Player");
                playAgain();
            }
        }
    }

    public void playAgain(){
        Button playAgain = new Button("Play Again");
        playAgain.setPrefSize(200, 50);
        playAgain.setStyle("-fx-base: rgba(90,94,103,0.67); -fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 20");
        playAgain.setOnAction(actionEvent -> {
            try {
                start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        gridLetters.add(playAgain,0,1,1,1);
    }

    public void changeImage(){
        int imageID = (6-tries)+1;
        String imageName;
        imageName = "png/"+ imageID +".png";
        Image newImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imageName)));
        imageHangman.setImage(newImage);
    }


    public void setPreviousGames(String word, int points, String winner){
        List<String> r = new ArrayList<>();
        int numOfPreviousGames = previousGames.size();
        if (numOfPreviousGames==5){
            previousGames.remove(0);
            previousGames.add(r);
            previousGames.get(4).add(word);
            previousGames.get(4).add(winner);
            previousGames.get(4).add(String.valueOf(points));
            previousGames.get(4).add(String.valueOf(numOfChoices));
        }
        else {
            previousGames.add(r);
            previousGames.get(numOfPreviousGames).add(word);
            previousGames.get(numOfPreviousGames).add(winner);
            previousGames.get(numOfPreviousGames).add(String.valueOf(points));
            previousGames.get(numOfPreviousGames).add(String.valueOf(numOfChoices));
        }
    }

    public void youWon(String word, int points){
        Dialog Win = new Dialog();
        Win.setHeaderText("CONGRATULATIONS, YOU WON!");
        Win.setContentText("You found the right word! You are the winner!\n"+"The word was: "+word+"\n"+"Your score is: "+points);
        Win.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE));
        Win.showAndWait();
    }

    public void youLost(String word, int points){
        Dialog Lose = new Dialog();
        Lose.setHeaderText("END OF GAME, YOU LOST!");
        Lose.setContentText("Unfortunately you didn't find the right word!\n"+"The word was: "+word+"\n"+"Your score is: "+points);
        Lose.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE));
        Lose.showAndWait();
    }

    public void Rounds(){
        Dialog rounds = new Dialog();
        rounds.setHeaderText("Previous rounds");
        if (previousGames.size()==0){
            rounds.setContentText("There are not previous games");
        }
        else {
            StringBuilder output = new StringBuilder("Information for previous games (starting from the most recent)\n\n");
            for (int i = previousGames.size()-1; i >=0; i--) {
                int num = previousGames.size()-i;
                output.append(num).append(".\nChosen Word: ").append(previousGames.get(i).get(0)).append("\nWinner: ").append(previousGames.get(i).get(1)).append("\nScore: ").append(previousGames.get(i).get(2)).append("\nTries: ").append(previousGames.get(i).get(3)).append("\n\n");
            }
            rounds.setContentText(output.toString());
        }
        rounds.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE));
        rounds.showAndWait();
    }

    public void showTheSolution(){
        try {
            gridWord.getChildren().clear();
            for (int i = 0; i < word.length(); i++) {
                Button buttons = new Button(String.valueOf(word.charAt(i)));
                buttons.setPrefSize(50, 50);
                buttons.setStyle("-fx-base: rgba(101,111,122,0.67); -fx-text-fill: rgba(255,255,255,0.86); -fx-font-weight: 700; -fx-font-size: 20");
                buttons.setDisable(true);
                gridWord.add(buttons, i, 0, 1, 1);
            }
            setPreviousGames(word, 0, "Enemy");
            Image newImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("png/loser.png")));
            imageHangman.setImage(newImage);
            Label loser = new Label("RIP. \nHOPE THAT THE NEXT \nTIME YOU WILL BE LUCKIER");
            loser.setStyle("-fx-font-weight: 600; -fx-font-size: 15");
            gridLetters.getChildren().clear();
            gridLetters.add(loser, 0, 0, 1, 1);
            playAgain();
            youLost(word, 0);
        }
        catch (Exception e){
            throwAlert(null, "The game didn't start yet! There is no word.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hangman.fxml")));
        Scene scene = new Scene(root, 1000, 700);
        stage.setResizable(false);
        stage.setTitle("MediaLab Hangman");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

}

