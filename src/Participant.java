public class Participant extends Person {

    private String preferredGame;
    private int skillLevel;
    private String preferredRole;
    private int personalityScore;
    private String personalityType;

    public Participant(String name, String email, String preferredGame,
                       int skillLevel, String preferredRole, int personalityScore) {

        super(name, email); // Inheritance: calling parent constructor

        this.preferredGame = preferredGame;
        this.skillLevel = skillLevel;
        this.preferredRole = preferredRole;
        this.personalityScore = personalityScore;

        // Auto classify personality
        this.personalityType = classifyPersonality(personalityScore);
    }

    private String classifyPersonality(int score) {
        if (score >= 80) return "Leader";
        if (score >= 60) return "Thinker";
        return "Balanced";
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
        System.out.println("Participant: " + name + " (" + email + ")");
    }

    @Override
    public String toString() {
        return name + " | " + preferredGame + " | Role: " + preferredRole +
                " | Skill: " + skillLevel + " | Personality: " + personalityType;
    }
}
