package org.openhab.binding.transportation.internal;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "type", "boardId", "stopId", "stopName", "dateTime", "track", "detailsId" })
public class DBTimetableDTO {

	@JsonProperty("name")
	private String name;
	@JsonProperty("type")
	private String type;
	@JsonProperty("boardId")
	private Integer boardId;
	@JsonProperty("stopId")
	private Integer stopId;
	@JsonProperty("stopName")
	private String stopName;
	@JsonProperty("dateTime")
	private String dateTime;
	@JsonProperty("track")
	private String track;
	@JsonProperty("detailsId")
	private String detailsId;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("boardId")
	public Integer getBoardId() {
		return boardId;
	}

	@JsonProperty("boardId")
	public void setBoardId(Integer boardId) {
		this.boardId = boardId;
	}

	@JsonProperty("stopId")
	public Integer getStopId() {
		return stopId;
	}

	@JsonProperty("stopId")
	public void setStopId(Integer stopId) {
		this.stopId = stopId;
	}

	@JsonProperty("stopName")
	public String getStopName() {
		return stopName;
	}

	@JsonProperty("stopName")
	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	@JsonProperty("dateTime")
	public String getDateTime() {
		return dateTime;
	}

	@JsonProperty("dateTime")
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	@JsonProperty("track")
	public String getTrack() {
		return track;
	}

	@JsonProperty("track")
	public void setTrack(String track) {
		this.track = track;
	}

	@JsonProperty("detailsId")
	public String getDetailsId() {
		return detailsId;
	}

	@JsonProperty("detailsId")
	public void setDetailsId(String detailsId) {
		this.detailsId = detailsId;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
}
