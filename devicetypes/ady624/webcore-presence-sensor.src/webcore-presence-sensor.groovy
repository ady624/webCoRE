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
public static String version() { return "v0.2.0e9.20170921" }
 
metadata {
	definition (name: "webCoRE Presence Sensor", namespace: "ady624", author: "Adrian Caramaliu") {
		capability "Presence Sensor"
		capability "Sensor"
        capability "Health Check"
        attribute "places", "String"
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
	}

	simulator {
		status "present": "presence: 1"
		status "not present": "presence: 0"
	}

	tiles(scale: 2) {
		standardTile("presence", "device.presence", width: 4, height: 4, canChangeBackground: true) {
			state("present", labelIcon:"st.presence.tile.mobile-present", backgroundColor:"#00A0DC")
			state("not present", labelIcon:"st.presence.tile.mobile-not-present", backgroundColor:"#ffffff")
		}
		valueTile("currentPlace", "device.currentPlaceDisplay", width: 2, height: 2) {
			state("default", label: '${currentValue}')
		}
        valueTile("distance", "device.distanceDisplay", width: 2, height: 2) {
			state("default", label: '${currentValue}')
		}
		valueTile("altitude", "device.altitudeDisplay", width: 3, height: 1) {
			state("default", label: '${currentValue}', icon:"https://dashboard.webcore.co/img/altitude.png")
		}
		valueTile("floor", "device.floorDisplay", width: 3, height: 1) {
			state("default", label: '${currentValue}')
		}
		valueTile("status", "device.status", width: 6, height: 5) {
			state("default", label: '${currentValue}')
		}
		main("presence")
		details(["presence", "currentPlace", "distance", "altitude", "floor", "status"])
	}
    
    preferences {
        input "scale", "enum", title: "Distance scale", description: "Select between imperial (miles) and metric (km)", options: ["Imperial", "Metric"], defaultValue: "Imperial", displayDuringSetup: true
        input "advanced", "enum", title: "Show advanced details", description: "", options: ["Yes", "No"], defaultValue: "Yes", displayDuringSetup: true
    }    
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
    	sendEvent( name: "places", value: list, isStateChange: true, displayed: false )
    }
    state.places = places
    return places
}


def doSendEvent(name, value) {
	if (value != device.currentValue(name)) sendEvent( name: name, value: value, isStateChange: true, displayed: false )    
}

def getOrdinalSuffix(value) {
	if (!("$value".isNumeric())) return ''    
    value = "$value".toNumber()
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
	//log.error "LOCATION=$event.location PLACE=$event.place"
	def places = getPlaces(event?.places)
    if ((event.name == 'updated') && !!event.location && !event.location.error) {
    	//filter out accuracy
    	if (event.location.horizontalAccuracy > 100) return
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
        processLocation(event.location.latitude, event.location.longitude, places)
    } else {
    	if (event?.place && (event?.place.size() == 71)) {
        	List parts = event.place.tokenize('|')
            if (parts.size() == 3) {
                def place = places.find{ it.id == parts[1] }
                if (place) {
                	processPlace(place, event.name, parts[2], places)
                }
			}
        }    
    }
}

private void processLocation(float lat, float lng, List places) {
    String presence = device.currentValue('presence')
	String closestPlace = device.currentValue('closestPlace')
    String currentPlace = device.currentValue('currentPlace')
    String arrivingAtPlace = ""
    String leavingPlace = ""
    float homeDistance = -1
    float closestDistance = -1
    int circles = 0
    for (place in places) {
    	float distance = getDistance(lat, lng, place.p[0], place.p[1])
        if ((closestDistance < 0) || (distance < closestDistance)) {
        	closestDistance = distance
            closestPlace = place.n
        }
        if (distance <= place.i) {
        	//we're at this place
            currentPlace = place.n
            place.meta.p = true
            if (place.h) presence = 'present'
            circles += 1
        } else if (distance <= place.o) {
        	//we're close to this place            
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
            place.meta.p = false
            if (place.h) presence = 'not present'
        }
        place.meta.d = distance
        if (place.h) {
        	homeDistance = distance / 1000.0
        }
    }
    if (!circles) {
    	//we found no current circle, so we clear the current place
    	currentPlace = ""
    }   
	if ((homeDistance >= 0) && homeDistance != device.currentValue('distanceMetric')) {
    	sendEvent( name: "distanceMetric", value: homeDistance, isStateChange: true, displayed: false )
    	sendEvent( name: "distance", value: homeDistance / 1.609344, isStateChange: true, displayed: false )
    	sendEvent( name: "distanceDisplay", value: advanced == "No" ? '' : (scale == 'Metric' ? sprintf('%.1f', homeDistance) + ' km away' : sprintf('%.1f', homeDistance / 1.609344) + ' mi away'), isStateChange: true, displayed: false )
	}        	

	closestDistance = closestDistance / 1000.0
	if ((closestDistance >= 0) && closestDistance != device.currentValue('closestPlaceDistanceMetric')) {
    	sendEvent( name: "closestPlaceDistanceMetric", value: closestDistance, isStateChange: true, displayed: false )
    	sendEvent( name: "closestPlaceDistance", value: closestDistance / 1.609344, isStateChange: true, displayed: false )
	}        	   
    state.places = places
	updateData(places, presence, currentPlace, closestPlace, arrivingAtPlace, leavingPlace, closestDistance)    
}

private void processPlace(Map place, String action, String circle, List places) {
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
    switch (action) {
        case "entered":
       		switch (circle) {
	            case "i":
	            	//arrived
                    presence = place.h ? 'present' : 'not present'
                    currentPlace = place.n
                    arrivingAtPlace = ''
                    leavingPlace = ''
                    place.meta.p = true
	            	break
	            case "o":
	            	//arriving
                    arrivingAtPlace = currentPlace == '' ? place.n : ''
                    leavingPlace = ''
	            	break
			}
			break
        case "exited":
	        switch (circle) {
	            case "i":
	            	//leaving
                    arrivingAtPlace = ''                    
                    leavingPlace = currentPlace == place.n ? place.n : ''
	            	break
	            case "o":
	            	//left
                    presence = 'not present'
                    currentPlace = ''
                    arrivingAtPlace = ''
                    leavingPlace = ''
	            	break
	        }
            break
    }
    state.places = places   
	updateData(places, presence, currentPlace, closestPlace, arrivingAtPlace, leavingPlace)
}

private void updateData(places, presence, currentPlace, closestPlace, arrivingAtPlace, leavingPlace, closestDistance = null) {
	if (presence != device.currentValue('presence')) {
    	sendEvent( name: "presence", value: presence, isStateChange: true, displayed: true, descriptionText: presence == 'present' ? 'Arrived' : 'Left' )
    }
    def prevPlace = device.currentValue('currentPlace')
    if (currentPlace != prevPlace) {    
    	sendEvent( name: "currentPlace", value: currentPlace, isStateChange: true, displayed: advanced != 'No', descriptionText: currentPlace == '' ? "Left $prevPlace" : "Arrived at $currentPlace" )
    }
    def currentPlaceDisplay = advanced == "No" ? '' : currentPlace
    if (currentPlaceDisplay != device.currentValue('currentPlaceDisplay')) {    
    	sendEvent( name: "currentPlaceDisplay", value: currentPlaceDisplay, isStateChange: true, displayed: false )
    }
	if (closestPlace != device.currentValue('closestPlace')) {
    	sendEvent( name: "closestPlace", value: closestPlace, isStateChange: true, displayed: false )
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

def parse(String description) {
	//not used
}