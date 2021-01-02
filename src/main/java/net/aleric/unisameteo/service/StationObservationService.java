package net.aleric.unisameteo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import net.aleric.unisameteo.entity.Station;
import net.aleric.unisameteo.entity.StationObservation;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class StationObservationService {

    private static final Logger LOG = Logger.getLogger(StationObservationService.class);

    @Inject
    StorageService storageService;
    @Inject
    ObjectMapper objectMapper;

    public Uni<List<String>> observations() {
        return storageService.keys();
    }

    public StationObservation retrieveObservation(Station station) {
        try {
            String observationString = storageService.get(station.id())
                    .orElseThrow(NotFoundException::new);
            return objectMapper.readValue(observationString, StationObservation.class);
        } catch (Exception ex) {
            throw new IllegalArgumentException("error retrieving observation", ex);
        }
    }

    public void saveObservation(Station station, StationObservation stationObservation) {
        try {
            storageService.set(station.id(), objectMapper.writeValueAsString(stationObservation));
            LOG.debugf("saved observation from %s station", station);
        } catch (Exception ex) {
            throw new IllegalArgumentException("error saving observation", ex);
        }
    }
}