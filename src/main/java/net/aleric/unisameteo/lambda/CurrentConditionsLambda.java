package net.aleric.unisameteo.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import net.aleric.unisameteo.entity.Station;
import net.aleric.unisameteo.entity.StationObservation;
import net.aleric.unisameteo.service.CurrentConditionsService;
import net.aleric.unisameteo.service.StationObservationService;

import javax.inject.Inject;
import javax.inject.Named;

@Named("currentConditions")
public class CurrentConditionsLambda implements RequestHandler<String, StationObservation> {

    @Inject
    CurrentConditionsService currentConditionsService;
    @Inject
    StationObservationService stationObservationService;

    @Override
    public StationObservation handleRequest(String stationId, Context context) {

        Station station = Station.fromId(stationId);

        StationObservation observation = currentConditionsService.fromStation(station);
        stationObservationService.saveObservation(station, observation);

        return observation;
    }
}
