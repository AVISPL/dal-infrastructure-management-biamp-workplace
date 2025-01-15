/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common;

/**
 * DisruptiveTechnologiesConstant class used in monitoring
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 30/12/2024
 * @since 1.0.0
 */
public class BiampWorkplaceConstant {
	public static final String HASH = "#";
	public static final String MODEL_MAPPING_AGGREGATED_DEVICE = "biamp_workplace/model-mapping.yml";
	public static final String NONE = "None";
	public static final String SPACE = " ";
	public static final String EMPTY = "";
	public static final String ID = "id";
	public static final String DEFAULT_FORMAT_DATETIME_WITHOUT_MILLIS  = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String TARGET_FORMAT_DATETIME = "MMM d, yyyy, h:mm a";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String GRANT_TYPE = "grant_type";
	public static final String CLIENT_ID = "client_id";
	public static final String DATA = "data";
	public static final String PROFILE = "profile";
	public static final String DEVICE = "device";
	public static final String REBOOT = "Reboot";
	public static final String MEMBERSHIPS = "memberships";
	public static final String DEVICES = "devices";
	public static final String STATUS = "status";
	public static final String ROLE = "role";
	public static final String TOTAL_COUNT = "totalCount";
	public static final String MOCKUP_PASSWORD = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjMwMTc4MTg5MDY4NjI4NzkxNCIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiMjY5Mzk5NTA1Njg0NzU4NTQyQHBsYXRmb3JtIiwiMjY5Mzk5NzY2MTUxMDM2OTQyQHBsYXRmb3JtIiwiMjgzNTkwNTkxMDEzNTUyMTg1QHBsYXRmb3JtIiwiMjg2MzY0NTk3Mzk1NDg4ODIxQHBsYXRmb3JtIiwiMjY5Mzk5NzY2NDY5ODA0MDQ2QHBsYXRmb3JtIiwiMjY5Mzk5NzY1OTQ5NzEwMzUwQHBsYXRmb3JtIiwiMjk0MzEwNzAyMDc4ODUzMTc1IiwiMjk0NDA3NjI5NzQzMTYxMzk5IiwiMjY5Mzk5MTYxODE4ODczODcwIl0sImV4cCI6MTczNjQzNTAwOSwiaWF0IjoxNzM2MzkxODA5LCJpc3MiOiJodHRwczovL2lhbS53b3JrcGxhY2UuYmlhbXAuYXBwIiwianRpIjoiVjJfMzAxODE0MjI5OTQ0NTMzMDY4LWF0XzMwMTgxNTA4NTM0NzY2ODA0NCIsIm5iZiI6MTczNjM5MTgwOSwic3ViIjoiMjk4NjE4OTcyMjQ3NDU3ODg1IiwidXJuOnppdGFkZWw6aWFtOnVzZXI6bWV0YWRhdGEiOnsiZXdwX3VzZXJfaWQiOiJNV0k1TWpVek1qUXROamRsT1MwME5UUmhMVGhrWXpZdE9UbG1PVGRsTlRRMk9HSm0iLCJld3BfdXNlcl90eXBlIjoiZFhObGNnIn19.mtyfwJhKztjcTZrA64jv-IO3P0nk34u2X6CutjSQ5Tw_uFFYrHriNqHLxAe43TP2AV1Fk51ZTs19mRWIKmsgXfwpyC71wH0gnj7Yo99qvG6bWZVrHbv-0ferf5elQDv5gSl0TdiT1KSIgMq97dPyDcRdyhqPlOVZ0YwrSn5dGoGP-KjSSlbOhi7L-CF31ZBA8vR314taEPnjvj2vBBVa3tE_BE00yK-GmSuL-lDJUagEhy9cZlNWpCfFcZyMQkO5GUl4dgDgjitG7yNZQ0PDZupSzWlh1BSHDLsaEDjEgccSV934EMvyIQDpHZswYkQvaA1PpTgJ8x650lUM7fptAg OFIo6nzYs-kmtJJPSfH_D4MSY2ONKw8NQiqA9UaR-EhRYdb1Yfn83SjpylUrUUGCbvnE_zSZGgK6dg0zld-19WWx06JabSDMV0DglhwI";

	public static final String MONITORING_CYCLE_DURATION = "LastMonitoringCycleDuration(s)";
	public static final String ADAPTER_VERSION = "AdapterVersion";
	public static final String ADAPTER_BUILD_DATE = "AdapterBuildDate";
	public static final String ADAPTER_UPTIME_MIN = "AdapterUptime(min)";
	public static final String ADAPTER_UPTIME = "AdapterUptime";
	/**
	 * Token timeout is 29 minutes, as this case reserve 1 minutes to make sure we never failed because of the timeout
	 */
	public static final long TIMEOUT = 28;
}
