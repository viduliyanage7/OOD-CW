public class PersonalityClassifier {

    public String classify(int score) {

        if (score > 89 && score <= 100) {
            return "Leader";
        } else if (score > 69 && score <= 89) {
            return "Balanced";
        } else if(score > 49 && score <= 69) {
            return "Thinker";
        }else{
            return "Unknown";
        }
    }
}
