public class Participant {
    private String name;
    private String game;
    private String role;
    private int skillLevel;
    private int personalityScore;
    private String personalityType;

    public Participant(String name, String game, String role, int skillLevel, int personalityScore) {
        this.name = name;
        this.game = game;
        this.role = role;
        this.skillLevel = skillLevel;
        this.personalityScore = personalityScore;
    }
}
