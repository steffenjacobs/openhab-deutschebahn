package org.openhab.binding.transportation.internal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

/** @author Steffen Jacobs */
public class DeutscheBahnController {

	private final Logger LOG = LoggerFactory.getLogger(DeutscheBahnController.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

	public boolean isApiReacheableBlocking() throws UnsupportedEncodingException {
		final String stationName = "Berlin Hbf";
		String url = "https://api.deutschebahn.com/freeplan/v1/location/" + URLEncoder.encode(stationName, "UTF-8").replaceAll("\\+", "%20");
		try {
			HttpResponse r = Request.Get(url).execute().returnResponse();
			LOG.info("API Recheable - {}", r.getStatusLine().getStatusCode());
			if(r.getStatusLine().getStatusCode()!=200) {
				LOG.error("API not reacheable - {}", EntityUtils.toString(r.getEntity()));
				return false;
			}
			return true;
		} catch (IOException e) {
			LOG.error("API not reacheable - {}", e.getMessage());
			return false;
		}
	}
	
	public CompletableFuture<Long> getStationIdByName(final String stationName) throws ClientProtocolException, IOException {
 		CompletableFuture<Long> future = new CompletableFuture<>();
		String url = "https://api.deutschebahn.com/freeplan/v1/location/" + URLEncoder.encode(stationName, "UTF-8").replaceAll("\\+", "%20");
		Request.Get(url).execute().handleResponse(new ResponseHandler<Response>() {

			@Override
			public Response handleResponse(HttpResponse r) throws ClientProtocolException, IOException {
				LOG.info("Requested station id for station {} - {}", stationName, r.getStatusLine().getStatusCode());
				String msg = EntityUtils.toString(r.getEntity());
				future.complete(getStationIdFromJSON(msg));
				return null;
			}
		});
		return future;
	}

	public CompletableFuture<Collection<String>> getNextDepartures(final long stationId) throws ClientProtocolException, IOException {
		CompletableFuture<Collection<String>> future = new CompletableFuture<>();
		Request.Get("https://api.deutschebahn.com/freeplan/v1/departureBoard/" + stationId + "?date=" + sdf.format(new Date())).execute().handleResponse(new ResponseHandler<Response>() {

			@Override
			public Response handleResponse(HttpResponse r) throws ClientProtocolException, IOException {
				LOG.info("Requested departure board for station {} - {}", stationId, r.getStatusLine().getStatusCode());
				String msg = EntityUtils.toString(r.getEntity());
				future.complete(getTimetableFromJSON(msg));
				return null;
			}
		});
		return future;
	}

	private Long getStationIdFromJSON(String json) throws JsonParseException, JsonMappingException, IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, DBStationInfoDTO.class);
		List<DBStationInfoDTO> stations = mapper.readValue(json, javaType);
		for(DBStationInfoDTO station:stations) {
			LOG.info("Successfully parsed station id {}", station.getId());
			return station.getId();
		}
		return 0l;
	}

	private Collection<String> getTimetableFromJSON(String json) throws JsonParseException, JsonMappingException, IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, DBTimetableDTO.class);
		Collection<DBTimetableDTO> timetables = mapper.readValue(json, javaType);
		Collection<String> list = new ArrayList<>();
		for (DBTimetableDTO timetable : timetables) {
			list.add(timetable.getDateTime() + " - " + timetable.getName());
		}
		LOG.info("Successfully parsed {} timetable entries.", timetables.size());
		return list;
	}
}
