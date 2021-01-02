package net.aleric.unisameteo.rest;

import net.aleric.unisameteo.entity.Station;
import net.aleric.unisameteo.entity.StationObservation;
import net.aleric.unisameteo.service.CurrentConditionsService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/currentConditions")
public class CurrentConditionsResource {

    @Inject
    CurrentConditionsService actualConditionsService;

    @Path("/{station}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StationObservation actualWeather(@PathParam("station") Station station) {

        return actualConditionsService.fromStation(station);
    }
}
