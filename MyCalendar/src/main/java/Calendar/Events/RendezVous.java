package Calendar.Events;

import Calendar.vo.*;

public class RendezVous extends Event {
    public RendezVous(TitreEvenement titre, DateEvenement dateDebut, HeureDebut heureDebut, DureeEvenement duree) {
        super(titre, dateDebut, heureDebut, duree);
    }

    @Override
    public String description() {
        return "RDV : " + titre.valeur() + " le " + dateDebut.valeur() + " à " + heureDebut.heure() + "h" + heureDebut.minute();
    }

    @Override
    public boolean appartientAPeriode(Periode periode) {
        return !dateDebut.valeur().isBefore(periode.debut()) && !dateDebut.valeur().isAfter(periode.fin());
    }
}
