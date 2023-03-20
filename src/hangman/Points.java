package hangman;

import java.util.List;
import java.util.Map;

public class Points {
    public static int CalculatePoints(boolean foundedChar, char c, int points, Map<Character, Float> ProbabilityPerChar){
        float probability=0;
        if (foundedChar){
            for (Map.Entry<Character, Float> entry : ProbabilityPerChar.entrySet()){
                if (entry.getKey()==c){
                    probability = entry.getValue();
                    break;
                }
            }
            if (probability >= 0.6){
                points = points + 5;
            }
            else if (probability >= 0.4){
                points = points + 10;
            }
            else if (probability >= 0.25){
                points = points + 15;
            }
            else{
                points = points + 30;
            }
        }
        else{
            if (points >= 15){
                points = points - 15;
            }
            else{
                points = 0;
            }
        }
        return points;
    }
}
