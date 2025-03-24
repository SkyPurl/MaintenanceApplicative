package Calendar.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;

public record Participants(String[] noms) {

    @JsonCreator
    public Participants(@JsonProperty("noms") String[] noms) {
        if (noms == null || noms.length == 0) {
            throw new IllegalArgumentException("Il doit y avoir au moins un participant.");
        }
        this.noms = Arrays.copyOf(noms, noms.length);
    }

    // Constructeur par défaut pour la sérialisation JSON
    private Participants() {
        this(new String[]{"Participant par défaut"});
    }
}