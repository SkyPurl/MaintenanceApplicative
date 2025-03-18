package Calendar.vo;

public record User(String username, String hashedPassword) {
    public User {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est invalide.");
        }
        if (hashedPassword == null || hashedPassword.isBlank()) {
            throw new IllegalArgumentException("Mot de passe haché invalide.");
        }
    }
}

