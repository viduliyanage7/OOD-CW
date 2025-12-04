public abstract class Person {
    protected String name;
    protected String email;

    public Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public abstract void displayInfo();

    public String getName() { return name; }
    public String getEmail() { return email; }
}
