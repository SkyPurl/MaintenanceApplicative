package Calendar.Events;

import Calendar.vo.*;

public class EvenementPeriodique extends Event {
    private final int frequenceJours;

    public EvenementPeriodique(TitreEvenement titre, DateEvenement dateDebut, HeureDebut heureDebut, DureeEvenement duree, int frequenceJours) {
        super(titre, dateDebut, heureDebut, duree);
        this.frequenceJours = frequenceJours;
    }

    public int getFrequenceJours() {
        return frequenceJours;
    }

    @Override
    public String description() {
        return "Événement périodique : " + titre.valeur() + " tous les " + frequenceJours + " jours";
    }
}
