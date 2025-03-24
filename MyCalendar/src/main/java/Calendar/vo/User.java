package Calendar.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record User(String username, String hashedPassword) {

    @JsonCreator
    public User(
            @JsonProperty("username") String username,
            @JsonProperty("hashedPassword") String hashedPassword) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est invalide.");
        }
        if (hashedPassword == null || hashedPassword.isBlank()) {
            throw new IllegalArgumentException("Mot de passe haché invalide.");
        }
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    // Constructeur par défaut pour la sérialisation JSON
    private User() {
        this("user", "default-hashed-password");
    }
}