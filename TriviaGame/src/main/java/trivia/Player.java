package trivia;

public class Player {
    private final String name;
    private int position;
    private int coins;
    private boolean inPenaltyBox;
    private int streak;
    private Categories lastWrongCategory;

    public Player(String name) {
        this.name = name;
        this.position = 1;
        this.coins = 0;
        this.inPenaltyBox = false;
        this.streak = 0;
        this.lastWrongCategory = null;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCoins() {
        return coins;
    }

    /**
     * Ajoute 1 ou 2 pièces selon que le joueur a une série active (3 réponses ou plus).
     */
    public void addCoin() {
        if (streak >= 3) {
            this.coins += 2;
        } else {
            this.coins++;
        }
    }

    public boolean isInPenaltyBox() {
        return inPenaltyBox;
    }

    public void setInPenaltyBox(boolean inPenaltyBox) {
        this.inPenaltyBox = inPenaltyBox;
    }

    public int getStreak() {
        return streak;
    }

    public void incrementStreak() {
        this.streak++;
    }

    public void resetStreak() {
        this.streak = 0;
    }

    public Categories getLastWrongCategory() {
        return lastWrongCategory;
    }

    public void setLastWrongCategory(Categories category) {
        this.lastWrongCategory = category;
    }

    public void clearLastWrongCategory() {
        this.lastWrongCategory = null;
    }
}
