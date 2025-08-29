package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.DeviceState;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Device {
	private String id;
	private String orgId;
	private String orgName;
	private Type type;
	private String serial;
	private Firmware assignedFirmware;
	private Firmware nextFirmware;
	private Firmware latestFirmware;
	private Channel channel;
	private Place place;
	private Desk desk;
	private Room room;
	private Status status;
	private Attributes attributes;
	private DeviceState state;

	public Device() {
	}

	/**
	 * Retrieves {@link #id}
	 *
	 * @return value of {@link #id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets {@link #id} value
	 *
	 * @param id new value of {@link #id}
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Retrieves {@link #orgId}
	 *
	 * @return value of {@link #orgId}
	 */
	public String getOrgId() {
		return orgId;
	}

	/**
	 * Sets {@link #orgId} value
	 *
	 * @param orgId new value of {@link #orgId}
	 */
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	/**
	 * Retrieves {@link #orgName}
	 *
	 * @return value of {@link #orgName}
	 */
	public String getOrgName() {
		return orgName;
	}

	/**
	 * Sets {@link #orgName} value
	 *
	 * @param orgName new value of {@link #orgName}
	 */
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	/**
	 * Retrieves {@link #type}
	 *
	 * @return value of {@link #type}
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Sets {@link #type} value
	 *
	 * @param type new value of {@link #type}
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * Retrieves {@link #serial}
	 *
	 * @return value of {@link #serial}
	 */
	public String getSerial() {
		return serial;
	}

	/**
	 * Sets {@link #serial} value
	 *
	 * @param serial new value of {@link #serial}
	 */
	public void setSerial(String serial) {
		this.serial = serial;
	}

	/**
	 * Retrieves {@link #assignedFirmware}
	 *
	 * @return value of {@link #assignedFirmware}
	 */
	public Firmware getAssignedFirmware() {
		return assignedFirmware;
	}

	/**
	 * Sets {@link #assignedFirmware} value
	 *
	 * @param assignedFirmware new value of {@link #assignedFirmware}
	 */
	public void setAssignedFirmware(Firmware assignedFirmware) {
		this.assignedFirmware = assignedFirmware;
	}

	/**
	 * Retrieves {@link #nextFirmware}
	 *
	 * @return value of {@link #nextFirmware}
	 */
	public Firmware getNextFirmware() {
		return nextFirmware;
	}

	/**
	 * Sets {@link #nextFirmware} value
	 *
	 * @param nextFirmware new value of {@link #nextFirmware}
	 */
	public void setNextFirmware(Firmware nextFirmware) {
		this.nextFirmware = nextFirmware;
	}

	/**
	 * Retrieves {@link #latestFirmware}
	 *
	 * @return value of {@link #latestFirmware}
	 */
	public Firmware getLatestFirmware() {
		return latestFirmware;
	}

	/**
	 * Sets {@link #latestFirmware} value
	 *
	 * @param latestFirmware new value of {@link #latestFirmware}
	 */
	public void setLatestFirmware(Firmware latestFirmware) {
		this.latestFirmware = latestFirmware;
	}

	/**
	 * Retrieves {@link #channel}
	 *
	 * @return value of {@link #channel}
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * Sets {@link #channel} value
	 *
	 * @param channel new value of {@link #channel}
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	/**
	 * Retrieves {@link #place}
	 *
	 * @return value of {@link #place}
	 */
	public Place getPlace() {
		return place;
	}

	/**
	 * Sets {@link #place} value
	 *
	 * @param place new value of {@link #place}
	 */
	public void setPlace(Place place) {
		this.place = place;
	}

	/**
	 * Retrieves {@link #desk}
	 *
	 * @return value of {@link #desk}
	 */
	public Desk getDesk() {
		return desk;
	}

	/**
	 * Sets {@link #desk} value
	 *
	 * @param desk new value of {@link #desk}
	 */
	public void setDesk(Desk desk) {
		this.desk = desk;
	}

	/**
	 * Retrieves {@link #room}
	 *
	 * @return value of {@link #room}
	 */
	public Room getRoom() {
		return room;
	}

	/**
	 * Sets {@link #room} value
	 *
	 * @param room new value of {@link #room}
	 */
	public void setRoom(Room room) {
		this.room = room;
	}

	/**
	 * Retrieves {@link #status}
	 *
	 * @return value of {@link #status}
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Sets {@link #status} value
	 *
	 * @param status new value of {@link #status}
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * Retrieves {@link #attributes}
	 *
	 * @return value of {@link #attributes}
	 */
	public Attributes getAttributes() {
		return attributes;
	}

	/**
	 * Sets {@link #attributes} value
	 *
	 * @param attributes new value of {@link #attributes}
	 */
	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	/**
	 * Retrieves {@link #state}
	 *
	 * @return value of {@link #state}
	 */
	public DeviceState getState() {
		return state;
	}

	/**
	 * Sets {@link #state} value
	 *
	 * @param state new value of {@link #state}
	 */
	public void setState(DeviceState state) {
		this.state = state;
	}
}
