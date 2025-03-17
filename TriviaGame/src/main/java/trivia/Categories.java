package trivia;

public enum Categories {
    POP("Pop", "pop.properties"),
    SCIENCE("Science", "science.properties"),
    SPORTS("Sports", "sports.properties"),
    ROCK("Rock", "rock.properties"),
    GEOGRAPHY("Géographie", "geography.properties");

    private final String displayName;
    private final String filename;

    Categories(String displayName, String filename) {
        this.displayName = displayName;
        this.filename = filename;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public String getFilename() {
        return filename;
    }
}
