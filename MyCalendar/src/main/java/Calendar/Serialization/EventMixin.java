package Calendar.Serialization;

import Calendar.Events.Event;
import Calendar.vo.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import Calendar.Events.*;

import java.time.LocalDateTime;
import java.util.Iterator;

/**
 * Mixin pour personnaliser la sérialisation de la classe Event
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RendezVous.class, name = "RendezVous"),
        @JsonSubTypes.Type(value = Reunion.class, name = "Reunion"),
        @JsonSubTypes.Type(value = EvenementPeriodique.class, name = "EvenementPeriodique"),
        @JsonSubTypes.Type(value = Anniversaire.class, name = "Anniversaire")
})
public abstract class EventMixin {

    @JsonProperty("id")
    protected EventId id;

    @JsonProperty("titre")
    protected TitreEvenement titre;

    @JsonProperty("dateDebut")
    protected DateEvenement dateDebut;

    @JsonProperty("heureDebut")
    protected HeureDebut heureDebut;

    @JsonProperty("duree")
    protected DureeEvenement duree;

    @JsonProperty("proprietaire")
    protected ProprietaireEvenement proprietaire;

    @JsonIgnore
    public abstract LocalDateTime getStartDateTime();

    @JsonIgnore
    public abstract LocalDateTime getEndDateTime();

    @JsonIgnore
    public abstract String description();

    @JsonIgnore
    public abstract Iterator<Event> occurrences(Periode periode);
}