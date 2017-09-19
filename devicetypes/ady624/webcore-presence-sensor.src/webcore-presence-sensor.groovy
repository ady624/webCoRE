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
public static String version() { return "v0.2.0e8.20170918" }
 
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
        attribute "verticalAccuracy", "Number"        
        attribute "status", "String"
        attribute "display", "String"
	}

	simulator {
		status "present": "presence: 1"
		status "not present": "presence: 0"
	}

	tiles(scale: 2) {
		standardTile("display", "device.display") {
			state("PRESENT", labelIcon:"st.presence.tile.mobile-present", label: '${currentValue}', color:"#00a0dc")
			state("AWAY", labelIcon:"st.presence.tile.mobile-not-present", label: '${currentValue}', color:"#e86d13")
			state("default", labelIcon:"st.presence.tile.mobile-not-present", label: '${currentValue}', color:"#cccccc")
		}
		standardTile("presence", "device.presence", width: 4, height: 4, canChangeBackground: true) {
			state("present", labelIcon:"st.presence.tile.mobile-present", backgroundColor:"#00A0DC")
			state("not present", labelIcon:"st.presence.tile.mobile-not-present", backgroundColor:"#ffffff")
		}
		valueTile("currentPlace", "device.currentPlace", width: 3, height: 2) {
			state("", label: 'Currently at ${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("closestPlace", "device.closestPlace", width: 3, height: 2) {
			state("default", label: 'Closest to ${currentValue}', backgroundColor:"#ffffff")
		}
        valueTile("distance", "device.distance", width: 2, height: 2) {
			state("default", label: '${currentValue} miles away', backgroundColor:"#ffffff")
		}
		valueTile("distanceMetric", "device.distanceMetric", width: 1, height: 1) {
			state("default", label: '${currentValue} km away', backgroundColor:"#ffffff")
		}
		valueTile("altitude", "device.altitude", width: 2, height: 2) {
			state("default", label: 'Altitude: ${currentValue}ft', backgroundColor:"#ffffff")
		}
		valueTile("status", "device.status", width: 6, height: 2) {
			state("default", label: '${currentValue}', backgroundColor:"#ffffff")
		}
		main("presence")
		details(["presence", "distance", "altitude", "status"])
	}
    
    preferences {
        input "scale", "enum", title: "Distance scale", description: "Select between imperial (miles) and metric (km)", options: ["Imperial", "Metric"], defaultValue: "Imperial", displayDuringSetup: true
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

def processEvent(Map event) {
	def places = getPlaces(event?.places)
    if ((event.name == 'updated') && !!event.location && !event.location.error) {
    	sendEvent( name: "latitude", value: event.location.latitude, isStateChange: true, displayed: false )
    	sendEvent( name: "longitude", value: event.location.longitude, isStateChange: true, displayed: false )
    	sendEvent( name: "altitude", value: event.location.altitude / 0.3048, isStateChange: true, displayed: false )
        sendEvent( name: "altitudeMetric", value: event.location.altitude, isStateChange: true, displayed: false )
    	sendEvent( name: "floor", value: event.location.floor, isStateChange: true, displayed: false )
    	sendEvent( name: "horizontalAccuracy", value: event.location.horizontalAccuracy, isStateChange: true, displayed: false )
    	sendEvent( name: "verticalAccuracy", value: event.location.verticalAccuracy, isStateChange: true, displayed: false )
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
    if (!places.findAll{ it.meta.p }.size()) {
    	//we found no current place, so we clear it
    	currentPlace = ""
    }   
	if ((homeDistance >= 0) && homeDistance != device.currentValue('distanceMetric')) {
    	sendEvent( name: "distanceMetric", value: homeDistance, isStateChange: true, displayed: false )
    	sendEvent( name: "distance", value: homeDistance / 1.609344, isStateChange: true, displayed: false )
	}        	

	closestDistance = closestDistance / 1000.0
	if ((closestDistance >= 0) && closestDistance != device.currentValue('closestPlaceDistanceMetric')) {
    	sendEvent( name: "closestPlaceDistanceMetric", value: closestDistance, isStateChange: true, displayed: false )
    	sendEvent( name: "closestPlaceDistance", value: closestDistance / 1.609344, isStateChange: true, displayed: false )
	}        	   
	updateData(presence, currentPlace, closestPlace, arrivingAtPlace, leavingPlace, closestDistance)    
    state.places = places
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
	updateData(presence, currentPlace, closestPlace, arrivingAtPlace, leavingPlace)
    state.places = places   
}

private void updateData(presence, currentPlace, closestPlace, arrivingAtPlace, leavingPlace, closestDistance = null) {
	if (presence != device.currentValue('presence')) {
    	sendEvent( name: "presence", value: presence, isStateChange: true, displayed: true )
    }
    if (currentPlace != device.currentValue('currentPlace')) {
    	sendEvent( name: "currentPlace", value: currentPlace, isStateChange: true, displayed: true )
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
    def status = ( !!arrivingAtPlace ? "Arriving at $arrivingAtPlace" : ( !!leavingPlace ? "Leaving $leavingPlace" : ( !!currentPlace ? "Currently at $currentPlace" : "Closest to $closestPlace" + (closestDistance == null ? '' : " (${scale == "Metric" ? sprintf("%.2f", closestDistance) + " km" : sprintf("%.2f", closestDistance / 1.609344) + " miles"} away)"))))
	if (status != device.currentValue('status')) {
    	sendEvent( name: "status", value: status, isStateChange: true, displayed: true )
    }
    def display = ( presence == 'present' ? 'PRESENT' : (currentPlace != '' ? currentPlace : 'AWAY'))
	if (display != device.currentValue('display')) {
    	sendEvent( name: "display", value: display, isStateChange: true, displayed: true )
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
	def name = parseName(description)
	def value = parseValue(description)
	def linkText = getLinkText(device)
	def descriptionText = parseDescriptionText(linkText, value, description)
	def handlerName = getState(value)
	def isStateChange = isStateChange(device, name, value)

	def results = [
    	translatable: true,
		name: name,
		value: value,
		unit: null,
		linkText: linkText,
		descriptionText: descriptionText,
		handlerName: handlerName,
		isStateChange: isStateChange,
		displayed: displayed(description, isStateChange)
	]
	log.debug "Parse returned $results.descriptionText"
	return results

}

private String parseName(String description) {
	if (description?.startsWith("presence: ")) {
		return "presence"
	}
	null
}

private String parseValue(String description) {
	switch(description) {
		case "presence: 1": return "present"
		case "presence: 0": return "not present"
		default: return description
	}
}

private parseDescriptionText(String linkText, String value, String description) {
	switch(value) {
		case "present": return "{{ linkText }} has arrived"
		case "not present": return "{{ linkText }} has left"
		default: return value
	}
}

private getState(String value) {
	switch(value) {
		case "present": return "arrived"
		case "not present": return "left"
		default: return value
	}
}