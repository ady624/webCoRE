/*
 *  webCoRE - Community's own Rule Engine - Web Edition
 *
 *  Copyright 2016 Adrian Caramaliu <ady624("at" sign goes here)gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
public static String version() { return "v0.2.102.20180201" }
/*
 *	02/01/2018 >>> v0.2.102.20180201 - BETA M2 - Fixed SmartThings app crash, thanks to @JohnHoke
 *	10/11/2017 >>> v0.2.0fa.20171010 - BETA M2 - Various bug fixes and improvements - fixed the mid() and random() functions
 *	10/07/2017 >>> v0.2.0f9.20171007 - BETA M2 - Added previous location attribute support and methods to calculate distance between places, people, fixed locations...
 *	10/06/2017 >>> v0.2.0f8.20171006 - BETA M2 - Added support for Android geofence filtering depending on horizontal accuracy
 *	10/04/2017 >>> v0.2.0f7.20171004 - BETA M2 - Added speed and bearing support
 *	10/04/2017 >>> v0.2.0f6.20171004 - BETA M2 - Bug fixes for geofencing
 *	10/04/2017 >>> v0.2.0f5.20171003 - BETA M2 - Bug fixes for geofencing
 *	10/04/2017 >>> v0.2.0f4.20171003 - BETA M2 - Bug fixes for geofencing
 *	10/03/2017 >>> v0.2.0f3.20171003 - BETA M2 - Bug fixes for geofencing
 *	10/03/2017 >>> v0.2.0f2.20171003 - BETA M2 - Updated iOS app to add timestamps
 *	10/01/2017 >>> v0.2.0f1.20171001 - BETA M2 - Added debugging options
 *	09/30/2017 >>> v0.2.0f0.20170930 - BETA M2 - Added last update info for both geofences and location updates
 *	09/30/2017 >>> v0.2.0ef.20170930 - BETA M2 - Minor fixes for Android
 *	09/29/2017 >>> v0.2.0ed.20170929 - BETA M2 - Added support for Android presence
 *	09/27/2017 >>> v0.2.0ec.20170927 - BETA M2 - Fixed a problem where the 'was' comparison would fail when the event had no device
 *	09/25/2017 >>> v0.2.0eb.20170925 - BETA M2 - Added Sleep Sensor capability to the webCoRE Presence Sensor, thanks to @Cozdabuch and @bangali
 *	09/24/2017 >>> v0.2.0ea.20170924 - BETA M2 - Fixed a problem where $nfl.schedule.thisWeek would only return one game, it now returns all games for the week. Same for lastWeek and nextWeek.
 *	09/21/2017 >>> v0.2.0e9.20170921 - BETA M2 - Added support for the webCoRE Presence Sensor
 *	09/18/2017 >>> v0.2.0e8.20170918 - BETA M2 - Alpha testing for presence
*/ 
metadata {
	definition (name: "webCoRE Presence Sensor", namespace: "ady624", author: "Adrian Caramaliu") {
		capability "Presence Sensor"
        capability "Sleep Sensor"
		capability "Sensor"
        capability "Health Check"
        attribute "places", "String"
        attribute "previousPlace", "String"
        attribute "currentPlace", "String"
        attribute "closestPlace", "String"
        attribute "arrivingAtPlace", "String"
        attribute "leavingPlace", "String"
        attribute "closestPlace", "String"
        attribute "distance", "Number"
        attribute "distanceMetric", "Number"
        attribute "closestPlaceDistance", "Number"
        attribute "closestPlaceDistanceMetric", "Number"
        attribute "altitude", "Number"
        attribute "altitudeMetric", "Number"
        attribute "floor", "String"
        attribute "latitude", "Number"
        attribute "longitude", "Number"
        attribute "horizontalAccuracy", "Number"        
        attribute "horizontalAccuracyMetric", "Number"        
        attribute "verticalAccuracy", "Number"
        attribute "verticalAccuracyMetric", "Number"
        attribute "speed", "Number"
        attribute "speedMetric", "Number"
        attribute "bearing", "Number"
        command "asleep"
        command "awake"
        command "toggleSleeping"
	}

	simulator {
		status "present": "presence: 1"
		status "not present": "presence: 0"
	}

	tiles(scale: 2) {    
		multiAttributeTile(name: "display", type: "generic", width: 2, height: 2, canChangeBackground: true) {
			tileAttribute ("device.display", key: "PRIMARY_CONTROL") {
            	attributeState "present, not sleeping", label: 'Home', icon:"st.nest.nest-away", backgroundColor:"#c0ceb9"
				attributeState "present, sleeping", label: 'Home (asleep)', icon:"st.Bedroom.bedroom2", backgroundColor:"#6879a3"
				attributeState "not present", label: 'Away', icon:"st.Office.office5", backgroundColor:"#777777"
            }
       		tileAttribute ("device.status", key: "SECONDARY_CONTROL") {
				attributeState "default", 
					label:'${currentValue}'
			}
        
        }
		standardTile("presence", "device.presence", width: 4, height: 2, canChangeBackground: true) {
			state("present", labelIcon:"st.presence.tile.mobile-present", backgroundColor:"#00A0DC")
			state("not present", labelIcon:"st.presence.tile.mobile-not-present", backgroundColor:"#ffffff")
		}
		standardTile("sleeping", "device.sleeping", width: 2, height: 2, canChangeBackground: true) {
			state("sleeping", label:"Asleep", icon: "st.Bedroom.bedroom2", action: "awake", backgroundColor:"#00A0DC")
			state("not sleeping", label:"Awake", icon: "st.Health & Wellness.health12", action: "asleep", backgroundColor:"#ffffff")
		}
		valueTile("currentPlace", "device.currentPlaceDisplay", width: 2, height: 2) {
			state("default", label: '${currentValue}')
		}
        valueTile("distance", "device.distanceDisplay", width: 2, height: 1) {
			state("default", label: '${currentValue}')
		}
        valueTile("speed", "device.speedDisplay", width: 2, height: 1) {
			state("default", label: '${currentValue}')
		}
		valueTile("altitude", "device.altitudeDisplay", width: 2, height: 1) {
			state("default", label: '${currentValue}', icon:"https://dashboard.webcore.co/img/altitude.png")
		}
		valueTile("floor", "device.floorDisplay", width: 2, height: 1) {
			state("default", label: '${currentValue}')
		}
		valueTile("status", "device.status", width: 6, height: 5) {
			state("default", label: '${currentValue}')
		}
        valueTile("lastGeofenceUpdate", "device.lastGeofenceUpdate", width: 3, height: 1) {
			state("default", label: '${currentValue}')
		}
		valueTile("lastLocationUpdate", "device.lastLocationUpdate", width: 3, height: 1) {
			state("default", label: '${currentValue}')
		}

		main("presence")
		details(["display", "presence", "sleeping", "currentPlace", "distance", "altitude", "speed", "floor", "status", "lastGeofenceUpdate", "lastLocationUpdate"])
	}
    
    preferences {
        input "scale", "enum", title: "Distance scale", description: "Select between imperial (miles) and metric (km)", options: ["Imperial", "Metric"], defaultValue: "Imperial", displayDuringSetup: true
        input "advanced", "enum", title: "Show advanced details", description: "", options: ["Yes", "No"], defaultValue: "Yes", displayDuringSetup: true
        input "presenceMode", "enum", title: "Presence mode", description: "", options: ["Automatic", "Force present", "Force not present"], defaultValue: "Automatic", displayDuringSetup: true
        input "debugging", "bool", title: "Enable debugging", description: "", defaultValue: false, displayDuringSetup: true
    }    
}


private updated() {
	updateData(state.places, device.currentValue('presence'), device.currentValue('sleeping'), device.currentValue('currentPlace'), device.currentValue('closestPlace'), device.currentValue('arrivingAtPlace'), device.currentValue('leavingPlace'))
}

private List getPlaces(List places) {
	places = places ?: []
    String list = ""
    List existingPlaces = state.places ?: []
    Map homePlace = null
    for (place in places) {
    	place.meta = existingPlaces.find{ place.id == it.id }?.meta ?: [:]
        if (place.h) homePlace = place
        list = list + (list.size() ? "," : "") + place.n
    }
    if (!homePlace && places.size()) places[0].h = true
    if (list != device.currentValue("places")) {
    	sendEvent( name: "places", value: list, displayed: false )
    }
    state.places = places
    return places
}


def doSendEvent(name, value) {
	if (value != device.currentValue(name)) sendEvent( name: name, value: value, displayed: false )    
}

def getOrdinalSuffix(value) {
	if (!("$value".isNumber())) return ''    
    value = "$value".toInteger()
	def value100 = value % 100;
	def value10 = value % 10;
    if (((value100 > 3) && (value100 < 21)) || (value10 == 0) || (value10 > 3)) return 'th'
    switch (value10) {
        case 1: return 'st'
        case 2: return 'nd'
        case 3: return 'rd'
    }
    return 'th'
}

def processEvent(Map event) {
	//log.error "GOT EVENT $event"
	def places = getPlaces(event?.places)
    def timestamp = (event.location?.timestamp ?: event.timestamp) ?: 0
   	def delay = now() - timestamp
    if ((event.name == 'updated') && !!event.location && !event.location.error) {
        if (delay > 30000) {
            if (debugging) {
                def info = "Received stale location update with a delay of ${delay}ms"
                log.debug info
                sendEvent( name: "debug", value: info, descriptionText: info, isStateChange: true, displayed: true )
            }
            return
        }
        if (timestamp < state.lastTimestamp) {
            if (debugging) {
                def info = "Received location update that is older than the last update"
                log.debug info
                sendEvent( name: "debug", value: info, descriptionText: info, isStateChange: true, displayed: true )
            }
            return
        }
        state.lastTimestamp = timestamp
    	//filter out accuracy
    	doSendEvent("latitude", event.location.latitude)
    	doSendEvent("longitude", event.location.longitude)
    	doSendEvent("altitude", event.location.altitude / 0.3048)
        doSendEvent("altitudeMetric", event.location.altitude)
        doSendEvent("altitudeDisplay", advanced == "No" ? '' : (scale == 'Metric' ? sprintf('%.1f', event.location.altitude) + ' m' : sprintf('%.1f', event.location.altitude / 0.3048) + ' ft'))
    	doSendEvent("floor", event.location.floor)
        doSendEvent("floorDisplay", advanced == "No" ? '' : (event.location.floor ? "${event.location.floor}${getOrdinalSuffix(event.location.floor)} floor" : 'Unknown floor'))
    	doSendEvent("horizontalAccuracy", event.location.horizontalAccuracy / 0.3048)
    	doSendEvent("horizontalAccuracyMetric", event.location.horizontalAccuracy)
    	doSendEvent("verticalAccuracy", event.location.verticalAccuracy / 0.3048)
    	doSendEvent("verticalAccuracyMetric", event.location.verticalAccuracy)
    	doSendEvent("speed", (event.location.speed ?: 0) / 0.3048)
        float speed = event.location.speed ?: 0
        float bearing = event.location.bearing ?: (event.location.course ?: 0)
        doSendEvent("speedMetric", speed)
        doSendEvent("bearing", bearing)
        doSendEvent("speedDisplay", advanced == "No" ? '' : (speed < 0 ? 'Unknown speed' : (speed == 0 ? 'Stationary' : (sprintf('%.1f', (scale == 'Metric' ? speed * 3.6 : speed * 3.6 / 1.609344)) + (scale == 'Metric' ? ' km/h' : ' mph') + (bearing >= 0 ? ' to ' + getBearingName(bearing) : '')))))
        processLocation(event.location.latitude, event.location.longitude, places, event.location.horizontalAccuracy)
    } else {
        state.lastTimestamp = timestamp > state.lastTimestamp ? timestamp : state.lastTimestamp
    	if (event?.place && (event?.place.size() == 71)) {
        	List parts = event.place.tokenize('|')
            if (parts.size() == 3) {
                def place = places.find{ it.id == parts[1] }
                if (place) {
                	processPlace(place, event.name, parts[2], places, event.location)
                }
			}
        }    
    }
}

private void processLocation(float lat, float lng, List places, horizontalAccuracy) {
	doSendEvent("lastLocationUpdate", "Last location update on\r\n${formatLocalTime("MM/dd/yyyy @ h:mm:ss a")}")
    String presence = device.currentValue('presence')
	String closestPlace = device.currentValue('closestPlace')
    String currentPlace = device.currentValue('currentPlace')
    String arrivingAtPlace = ""
    String leavingPlace = ""
    float homeDistance = -1
    String bearing = '?'
    float closestDistance = -1
    int circles = 0
    String info = ''
    if (horizontalAccuracy > 100) {
    	info = " Low accuracy of ${horizontalAccuracy}m prevented updates to presence."
    } else {
        for (place in places) {
            float distance = getDistance(lat, lng, place.p[0], place.p[1])
            if ((closestDistance < 0) || (distance < closestDistance)) {
                closestDistance = distance
                closestPlace = place.n
            }
            if (distance <= place.i) {
                info += " Location is inside inner ${place.n}.\r\n"
                //we're at this place
                currentPlace = place.n
                place.meta.p = true
                if (place.h) presence = 'present'
                circles += 1
            } else if (distance <= place.o) {
                //we're close to this place            
                info += " Location is in the buffer zone of ${place.n}.\r\n"
                if (place.n == currentPlace) {
                    //departing
                    arrivingAtPlace = ''
                    leavingPlace = place.n
                } else {
                    //arriving
                    arrivingAtPlace = place.n
                    leavingPlace = ''
                }
                circles += 1
            } else {
                //we're not at this place
                info += " Location is outside of ${place.n}.\r\n"
                place.meta.p = false
                if (place.h) presence = 'not present'
            }
            place.meta.d = distance
            if (place.h) {
                homeDistance = distance / 1000.0
                bearing = getBearing(place.p[0], place.p[1], lat, lng)
            }
        }
        if (!circles) {
            //we found no current circle, so we clear the current place
            info += " Location is outside of all circles."
            currentPlace = ""
        }   
        if ((homeDistance >= 0) && homeDistance != device.currentValue('distanceMetric')) {
            sendEvent( name: "distanceMetric", value: homeDistance, isStateChange: true, displayed: false )
            sendEvent( name: "distance", value: homeDistance / 1.609344, isStateChange: true, displayed: false )
            sendEvent( name: "distanceDisplay", value: advanced == "No" ? '' : (scale == 'Metric' ? sprintf('%.1f', homeDistance) + ' km @ ' + bearing : sprintf('%.1f', homeDistance / 1.609344) + ' mi @ ' + bearing), isStateChange: true, displayed: false )
        }        	

        closestDistance = closestDistance / 1000.0
        if ((closestDistance >= 0) && closestDistance != device.currentValue('closestPlaceDistanceMetric')) {
            sendEvent( name: "closestPlaceDistanceMetric", value: closestDistance, isStateChange: true, displayed: false )
            sendEvent( name: "closestPlaceDistance", value: closestDistance / 1.609344, isStateChange: true, displayed: false )
        }        	   
        state.places = places
        updateData(places, presence, device.currentValue('sleeping'), currentPlace, closestPlace, arrivingAtPlace, leavingPlace)    
    }
    if (debugging) {
    	info = "Received location update with horizontal accuracy of ${horizontalAccuracy} meters.$info"
        log.debug info
		sendEvent( name: "debug", value: info, descriptionText: info, isStateChange: true, displayed: true )
    }
}

private void processPlace(Map place, String action, String circle, List places, Map location) {
	doSendEvent("lastGeofenceUpdate", "Last geofence update on\r\n${formatLocalTime("MM/dd/yyyy @ h:mm:ss a")}")
	def horizontalAccuracy = location?.horizontalAccuracy ?: 0
    if (horizontalAccuracy > 100) {
    	info = " Low accuracy of ${horizontalAccuracy}m prevented updates to presence."
    } else {
        for (p in places) {
            p.meta = p.meta ?: [:]
            if (p != place) {
                p.meta.p = false
            }
        }
        String presence = device.currentValue('presence')
        String closestPlace = place.n
        String currentPlace = device.currentValue('currentPlace')
        String arrivingAtPlace = ""
        String leavingPlace = ""
        String info = ''
        switch (action) {
            case "entered":
                switch (circle) {
                    case "i":
                        //arrived
                        info += " Inner geofence of ${place.n} was just entered."
                        presence = place.h ? 'present' : 'not present'
                        currentPlace = place.n
                        arrivingAtPlace = ''
                        leavingPlace = ''
                        place.meta.p = true
                        break
                    case "o":
                        //arriving
                        info += " Outter geofence of ${place.n} was just entered."
                        arrivingAtPlace = currentPlace == '' ? place.n : ''
                        leavingPlace = ''
                        break
                }
                break
            case "exited":
                switch (circle) {
                    case "i":
                        //leaving
                        info += " Inner geofence of ${place.n} was just exited."                    
                        arrivingAtPlace = ''                    
                        leavingPlace = currentPlace == place.n ? place.n : ''
                        break
                    case "o":
                        //left
                        info += " Outer geofence of ${place.n} was just exited."                    
                        presence = 'not present'
                        currentPlace = ''
                        arrivingAtPlace = ''
                        leavingPlace = ''
                        break
                }
                break
        }
        state.places = places    
        updateData(places, presence, device.currentValue('sleeping'), currentPlace, closestPlace, arrivingAtPlace, leavingPlace)
        if (debugging) {
            info = "Received geofence update for ${circle == 'i' ? 'inner' : 'outer'} circle of ${place.n}.$info"
            log.debug info
            sendEvent( name: "debug", value: info, descriptionText: info, isStateChange: true, displayed: true )
        }
    }
}

private void updateData(places, presence, sleeping, currentPlace, closestPlace, arrivingAtPlace, leavingPlace) {
    def prevPlace = device.currentValue('currentPlace')
    if (currentPlace != prevPlace) {    
    	sendEvent( name: "previousPlace", value: prevPlace, isStateChange: true, displayed: false)   
    	sendEvent( name: "currentPlace", value: currentPlace, isStateChange: true, displayed: advanced != 'No', descriptionText: currentPlace == '' ? "Left $prevPlace" : "Arrived at $currentPlace" )
    }
   	doSendEvent("currentPlaceDisplay", advanced == "No" ? '' : (currentPlace ?: 'Away'))
	if (closestPlace != device.currentValue('closestPlace')) {
    	sendEvent( name: "closestPlace", value: closestPlace, displayed: false )
    }
	if (arrivingAtPlace != device.currentValue('arrivingAtPlace')) {
    	sendEvent( name: "arrivingAtPlace", value: arrivingAtPlace, isStateChange: true, displayed: false )
    }
	if (leavingPlace != device.currentValue('leavingPlace')) {
    	sendEvent( name: "leavingPlace", value: leavingPlace, isStateChange: true, displayed: false )
    }
    
    def status = ''
    if (advanced != "No") {
        def count = 0
        for (place in places.sort{ it.meta?.d }) {
            def line = ( place.n == arrivingAtPlace ? "Arriving at $arrivingAtPlace" : ( leavingPlace == place.n ? "Leaving $leavingPlace" : ( currentPlace == place.n ? "Currently at $currentPlace" : (place.meta?.d == null ? '' : "~${scale == "Metric" ? sprintf("%.2f", place.meta.d / 1000) + " km" : sprintf("%.2f", place.meta.d / 1609.344) + " miles"} from ${place.n}"))))    
            if (line) {
                status += (status ? '\r\n' : '') + line
                count += 1
            }
        }
        while (count < 10) {
            status += '\r\n'
            count += 1
        }
    }
    if (status != device.currentValue('status')) {
    	sendEvent( name: "status", value: status, isStateChange: true, displayed: false )
    }
    switch (presenceMode) {
    	case 'Force present':
        	presence = 'present'
            break
    	case 'Force not present':
        	presence = 'not present'
            break
    }
	if (presence != device.currentValue('presence')) {
    	sendEvent( name: "presence", value: presence, isStateChange: true, displayed: true, descriptionText: presence == 'present' ? 'Arrived' : 'Left' )
    }
    sleeping = sleeping ? (presence == 'not present' ? 'not sleeping' : sleeping) : 'not sleeping'
	if (sleeping != device.currentValue('sleeping')) {
    	sendEvent( name: "sleeping", value: sleeping, isStateChange: true, displayed: true, descriptionText: sleeping == 'sleeping' ? 'Sleeping' : 'Awake' )
    }
    def display = presence + (presence == 'present' ? ', ' + sleeping : '')
	if (display != device.currentValue('display')) {
    	sendEvent( name: "display", value: display, isStateChange: true, displayed: false )
    }
}

private static float getDistance(float lat1, float lng1, float lat2, float lng2) {
    double earthRadius = 6371000; //meters
    double dLat = Math.toRadians(lat2-lat1);
    double dLng = Math.toRadians(lng2-lng1);
    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
               Math.sin(dLng/2) * Math.sin(dLng/2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    float dist = (float) (earthRadius * c);
    return dist; //meters
}

private String getBearingName(float degrees) {
  def bearings = ['N', 'N-NE', 'NE', 'E-NE', 'E', 'E-SE', 'SE', 'S-SE', 'S', 'S-SW', 'SW', 'W-SW', 'W', 'W-NW', 'NW', 'N-NW']
  int bearing = Math.floor(((degrees + 360 + 11.25)%360) / 22.5).toInteger()
  return bearings[bearing]
}

private String getBearing(float lat1, float lon1, float lat2, float lon2){
	double longitude1 = lon1;
	double longitude2 = lon2;
	double latitude1 = Math.toRadians(lat1);
	double latitude2 = Math.toRadians(lat2);
	double longDiff= Math.toRadians(longitude2-longitude1);
	double y= Math.sin(longDiff)*Math.cos(latitude2);
	double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);

	return getBearingName((float) Math.toDegrees(Math.atan2(y, x)))
}

def parse(String description) {
	//not used
}

private toggleSleeping(sleeping = null) {
	sleeping = sleeping ?: (device.currentValue('sleeping') == 'not sleeping' ? 'sleeping' : 'not sleeping')
   	updateData(state.places, device.currentValue('presence'), sleeping, device.currentValue('currentPlace'), device.currentValue('closestPlace'), device.currentValue('arrivingAtPlace'), device.currentValue('leavingPlace'))
}

def asleep() {
	toggleSleeping('sleeping')
}

def awake() {
	toggleSleeping('not sleeping')
}

private formatLocalTime(format = "EEE, MMM d yyyy @ h:mm:ss a z", time = now()) {
	def formatter = new java.text.SimpleDateFormat(format)
	formatter.setTimeZone(location.timeZone)
	return formatter.format(time)
}

