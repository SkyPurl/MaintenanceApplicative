package Calendar;

import Calendar.Events.Event;
import Calendar.Serialization.JsonCalendarSerializer;
import Calendar.Util.EventConflitDetector;
import Calendar.vo.EventId;
import Calendar.vo.Periode;
import Calendar.Events.Evenements;

import java.io.IOException;
import java.util.*;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CalendarManager {

    private final Evenements evenements;
    private final EventConflitDetector conflitDetector;

    public CalendarManager() {
        this.evenements = new Evenements();
        this.conflitDetector = new EventConflitDetector();
    }

    public void ajouterEvenement(Event evenement) {
        List<Event> conflits = detecterConflits(evenement);

        if (!conflits.isEmpty()) {
            throw new ConflitEvenementException(evenement, conflits);
        }

        evenements.ajouter(evenement);
    }

    private List<Event> detecterConflits(Event nouvelEvenement) {
        List<Event> conflits = new ArrayList<>();

        Iterator<Event> it = evenements.iterator();
        while (it.hasNext()) {
            Event existingEvent = it.next();
            if (conflitDetector.detectConflict(nouvelEvenement, existingEvent)) {
                conflits.add(existingEvent);
            }
        }

        return conflits;
    }

    public List<Event> eventsDansPeriode(Periode periode) {
        Iterator<Event> it = evenements.occurrences(periode);
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED),
                false
        ).collect(Collectors.toList());
    }

    public boolean supprimerEvenement(EventId id) {
        return evenements.supprimer(id);
    }

    public Optional<Event> trouverParId(EventId id) {
        return evenements.trouverParId(id);
    }

    public void afficherEvenements() {
        Iterator<Event> it = evenements.iterator();
        if (!it.hasNext()) {
            System.out.println("Aucun événement dans le calendrier.");
            return;
        }

        System.out.println("\n=== Liste des événements ===");
        while (it.hasNext()) {
            Event e = it.next();
            System.out.println("[ID: " + e.getId().valeur() + "] " + e.description());
        }
    }

    public static class ConflitEvenementException extends RuntimeException {
        private final List<Event> evenementsEnConflit;

        public ConflitEvenementException(Event nouvelEvenement, List<Event> conflits) {
            super("Conflit détecté : L'événement \"" + nouvelEvenement.getTitre().valeur() +
                    "\" chevauche " + conflits.size() + " événement(s) existant(s)");
            this.evenementsEnConflit = new ArrayList<>(conflits);
        }

        public List<Event> getEvenementsEnConflit() {
            return Collections.unmodifiableList(evenementsEnConflit);
        }
    }

    public List<Event> getAllEvents() {
        List<Event> result = new ArrayList<>();
        evenements.iterator().forEachRemaining(result::add);
        return result;
    }


    public void clearEvents() {
        evenements.clear();
    }

    /**
     * Sauvegarde le calendrier en JSON dans un fichier
     */
    public void saveToJson(String filePath) throws IOException {
        JsonCalendarSerializer serializer = new JsonCalendarSerializer();
        serializer.saveCalendarToFile(this, filePath);
    }

    /**
     * Charge le calendrier depuis un fichier JSON
     */
    public void loadFromJson(String filePath) throws IOException {
        JsonCalendarSerializer serializer = new JsonCalendarSerializer();
        serializer.loadCalendarFromFile(this, filePath);
    }
}