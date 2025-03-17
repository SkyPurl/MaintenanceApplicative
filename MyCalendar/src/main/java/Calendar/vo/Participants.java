package Calendar.vo;

public record Participants(String[] noms) {
    public Participants {
        if (noms == null || noms.length == 0) {
            throw new IllegalArgumentException("Il doit y avoir au moins un participant.");
        }
    }
}
