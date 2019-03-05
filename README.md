# Deutsche Bahn Station Timetable Binding

This Binding can provide the timetable for a given station used by Deutsche Bahn. The timetable shows the next departing trains for the selected station.

## Supported Things

A Thing should be created for each station to monitor the departure timetable of.

## Thing Configuration

Please enter the name of the station you want to monitor in the configuration section of the Thing in the Paper UI.

## Channels

There is one RefreshTransportation channel, which refreshes the timetable. The updated timetable in then written onto the OutputTransportation channel.

## Disclaimer

Keep in mind that this is just a proof of concept and not production ready!
