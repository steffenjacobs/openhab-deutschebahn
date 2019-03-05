/**
 * Copyright (c) 2014,2019 by the respective copyright holders.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.transportation.internal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link TransportationHandler} is responsible for handling commands, which
 * are sent to one of the channels.
 *
 * @author Steffen Jacobs - Initial contribution
 */
@NonNullByDefault
public class TransportationHandler extends BaseThingHandler {

	private static final Logger LOG = LoggerFactory.getLogger(TransportationHandler.class);

	@Nullable
	private DeutscheBahnController deutscheBahnController;

	@Nullable
	private TransportationConfiguration config;

	public TransportationHandler(Thing thing) {
		super(thing);
	}

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
		if (TransportationBindingConstants.TRANSPORTATION_REFRESH_CHANNEL.equals(channelUID.getId())) {
			if (command instanceof RefreshType) {
				if (config == null) {
					updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Invalid configuration");
					LOG.error("Invalid config");
				} else if (config.location == null || config.location.isEmpty()) {
					updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Invalid location");
					LOG.error("No location specified!");
				} else {
					switch (config.location) {
					case "Mannheim Hbf":
						refreshDeutscheBahn(config.location);
						break;
					default:
						updateState(TransportationBindingConstants.TRANSPORTATION_OUTPUT_CHANNEL, new StringType("Unknown location: " + config.location));
						break;
					}
				}
			} else if (command instanceof StringType) {
				String commandString = ((StringType) command).toFullString();
				LOG.info("String command {}", commandString);
			}
		}
	}

	private void refreshDeutscheBahn(final String location) {
		try {
			CompletableFuture<Long> stationIdByName = deutscheBahnController.getStationIdByName(location);
			Collection<String> collection = deutscheBahnController.getNextDepartures(stationIdByName.get()).get();
			StringBuilder sb = new StringBuilder();
			for (String s : collection) {
				sb.append(s);
				sb.append("\n");
			}
			updateState(TransportationBindingConstants.TRANSPORTATION_OUTPUT_CHANNEL, new StringType(sb.toString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO: handle data refresh
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initialize() {
		LOG.debug("Start initializing!");
		config = getConfigAs(TransportationConfiguration.class);

		updateStatus(ThingStatus.UNKNOWN);

		scheduler.execute(() -> {
			deutscheBahnController = new DeutscheBahnController();
			boolean apiAvailable;
			try {
				apiAvailable = deutscheBahnController.isApiReacheableBlocking();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				apiAvailable = false;
			}
			if (apiAvailable) {
				updateStatus(ThingStatus.ONLINE);
			} else {
				updateStatus(ThingStatus.OFFLINE);
			}
			LOG.debug("Finished initializing!");
		});
	}
}
