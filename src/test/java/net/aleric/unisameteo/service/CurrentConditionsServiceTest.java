package net.aleric.unisameteo.service;

import net.aleric.unisameteo.entity.Station;
import net.aleric.unisameteo.entity.StationObservation;
import net.aleric.unisameteo.entity.WeatherParameter;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

//@QuarkusTest
public class CurrentConditionsServiceTest {

    @Inject
    private CurrentConditionsService service;


//    @Test
    public void testHelloEndpoint() {

        final StationObservation scrape = service.stationData(Station.RETTORATO);

        System.out.println(scrape);
    }

}