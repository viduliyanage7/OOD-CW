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

    public String getName() { return name; }
    public String getGame() { return game; }
    public String getRole() { return role; }
    public int getSkillLevel() { return skillLevel; }
    public int getPersonalityScore() { return personalityScore; }

    public String getPersonalityType() { return personalityType; }
    public void setPersonalityType(String type) { this.personalityType = type; }

    @Override
    public String toString() {
        return name + " | " + game + " | " + role + " | Skill: " + skillLevel +
                " | Personality: " + personalityType;
    }
}
