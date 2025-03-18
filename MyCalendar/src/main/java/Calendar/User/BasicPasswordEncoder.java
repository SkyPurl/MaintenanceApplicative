package Calendar.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class BasicPasswordEncoder implements PasswordEncoder {

    private static final SecureRandom random = new SecureRandom();

    @Override
    public String encode(CharSequence rawPassword) {
        // Génère un sel
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // Concatène salt + mdp, applique SHA-256
        byte[] hash = sha256(salt, rawPassword.toString());
        // stocke salt + hash en Base64
        byte[] combined = new byte[salt.length + hash.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(hash, 0, combined, salt.length, hash.length);
        return Base64.getEncoder().encodeToString(combined);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        byte[] combined = Base64.getDecoder().decode(encodedPassword);
        // sel = 16 premiers octets
        byte[] salt = new byte[16];
        System.arraycopy(combined, 0, salt, 0, 16);
        // hash stocké = reste
        byte[] storedHash = new byte[combined.length - 16];
        System.arraycopy(combined, 16, storedHash, 0, storedHash.length);

        // recalculer hash
        byte[] newHash = sha256(salt, rawPassword.toString());
        // comparer
        if (newHash.length != storedHash.length) return false;
        for (int i = 0; i < newHash.length; i++) {
            if (newHash[i] != storedHash[i]) return false;
        }
        return true;
    }

    private static byte[] sha256(byte[] salt, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            return md.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

