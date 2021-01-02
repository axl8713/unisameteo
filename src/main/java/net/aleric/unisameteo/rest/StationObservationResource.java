package net.aleric.unisameteo.rest;

import io.smallrye.mutiny.Uni;
import net.aleric.unisameteo.entity.Station;
import net.aleric.unisameteo.entity.StationObservation;
import net.aleric.unisameteo.service.StationObservationService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

@Path("/observation")
public class StationObservationResource {

    @Inject
    StationObservationService stationObservationService;

    @GET
    public Uni<List<String>> observations() {
        return stationObservationService.observations();
    }

    @GET
    @Path("/{station}")
    public StationObservation getObservation(@PathParam("station") Station station) {
        return stationObservationService.retrieveObservation(station);
    }

    @PUT
    @Path("/{station}")
    public void putObservation(@PathParam("station") Station station, StationObservation stationObservation) {
        stationObservationService.saveObservation(station, stationObservation);
    }
}