public class Participant extends Person {

    private String preferredGame;
    private int skillLevel;
    private String preferredRole;
    private int personalityScore;
    private String personalityType;

    public Participant(String name, String preferredGame,
                       int skillLevel, String preferredRole, int personalityScore) {

        super(name);
        this.preferredGame = preferredGame;
        this.skillLevel = skillLevel;
        this.preferredRole = preferredRole;
        this.personalityScore = personalityScore;
        this.personalityType = classifyPersonality(personalityScore);
    }

    private String classifyPersonality(int score) {
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



    public String getPreferredGame() { return preferredGame; }
    public int getSkillLevel() { return skillLevel; }
    public String getPreferredRole() { return preferredRole; }
    public int getPersonalityScore() { return personalityScore; }
    public String getPersonalityType() { return personalityType; }
    public void setPersonalityType(String personalityType) {
        this.personalityType = personalityType;
    }

    @Override
    public void displayInfo() {
        System.out.println("Participant: " + name );
    }

    @Override
    public String toString() {
        return name + " | " + preferredGame + " | Role: " + preferredRole +
                " | Skill: " + skillLevel + " | Personality: " + personalityType;
    }
}
