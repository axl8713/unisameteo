package net.aleric.unisameteo.service;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;
import net.aleric.unisameteo.entity.Station;
import net.aleric.unisameteo.entity.StationObservation;
import net.aleric.unisameteo.entity.WeatherParameter;
import org.jboss.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ApplicationScoped
public class CurrentConditionsService {

    private static final Logger LOG = Logger.getLogger(CurrentConditionsService.class);

    private static final String UNISA_WEBPAGE = "https://web.unisa.it";
    private static final Pattern OBSERVATION_DATE_TIME_PATTERN = Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{1,4}).+?((?:\\d{1,2}:?)+)");
    private static final DateTimeFormatter OBSERVATION_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");

    public StationObservation fromStation(Station station) {

        try {
            LOG.debugf("retrieving data from %s station", station);

            StationObservation observation = new StationObservation(station);

            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.setScriptPreProcessor(CurrentConditionsService::avoidStationDataAutoLoadPreProcessor);
            new WebConnectionWrapper(webClient) {

                @Override
                public WebResponse getResponse(final WebRequest request) throws IOException {

                    LOG.tracef("request: %s", request.getUrl().toExternalForm());

                    WebResponse response = super.getResponse(request);

                    if (request.getUrl().toExternalForm().contains(station.id())) {
                        Document stationHtmlData = parseWeatherDataResponse(response);
                        observation.setData(scrapeWeatherData(stationHtmlData));
                        observation.setTime(scrapeWeatherTime(stationHtmlData));
                        observation.setWebcamUrl(UNISA_WEBPAGE + "/" + scrapeWebcamImageUrl(stationHtmlData));
                    }

                    return response;
                }
            };

            HtmlPage page = webClient.getPage(UNISA_WEBPAGE + "/servizi-on-line/stazione-meteo");

            page.getElementById(station.name().toLowerCase()).click();
            webClient.waitForBackgroundJavaScriptStartingBefore(1);

            return observation;

        } catch (Exception ex) {
            throw new IllegalStateException("Error in scraping actual conditions", ex);
        }
    }

    private Document parseWeatherDataResponse(WebResponse response) {
        LOG.infof("Scraping `%s`...", response.getWebRequest().getUrl().toExternalForm());
        Document stationHtmlData = Jsoup.parse(response.getContentAsString());
        LOG.debug("done scraping");
        return stationHtmlData;
    }

    private Map<WeatherParameter, String> scrapeWeatherData(Document stationHtmlData) {

        List<Element> weatherRowsElements = new ArrayList<>(stationHtmlData.getElementsByTag("tr"));

        if (WeatherParameter.values().length != weatherRowsElements.size()) {
            throw new IllegalStateException("incorrect number of scraped weather parameters");
        }

        Map<WeatherParameter, String> currentConditions = new EnumMap<>(WeatherParameter.class);
        for (WeatherParameter parameter : WeatherParameter.values()) {
            currentConditions.put(
                    parameter,
                    parseWeatherRow(weatherRowsElements.get(parameter.ordinal())));
        }
        return currentConditions;
    }

    private String parseWeatherRow(Element weatherRow) {
        return weatherRow.getElementsByTag("td").last().text();
    }

    private ZonedDateTime scrapeWeatherTime(Document stationHtmlData) {
        String timeText = stationHtmlData.select(".panel-footer").text();
        return LocalDateTime.from(parseWeatherTime(timeText)).atZone(ZoneId.of("Europe/Rome"));
    }

    private TemporalAccessor parseWeatherTime(String timeText) {
        Matcher matcher = OBSERVATION_DATE_TIME_PATTERN.matcher(timeText.trim());
        matcher.find();
        return OBSERVATION_DATE_TIME_FORMATTER.parse(matcher.group());
    }

    private String scrapeWebcamImageUrl(Document stationHtmlData) {
        return stationHtmlData.getElementsByTag("img")
                .get(0)
                .attr("src");
    }

    private static String avoidStationDataAutoLoadPreProcessor(HtmlPage htmlPage, String sourceCode, String sourceName, int lineNumber, HtmlElement htmlElement) {

        if (sourceName.contains("meteo.js")) {
            List<String> meteoJSCode = sourceCode.lines().collect(Collectors.toList());
            meteoJSCode.remove(6);
            return String.join("\n", meteoJSCode);
        }

        return sourceCode;
    }
}