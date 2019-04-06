/**
 *  Enhanced SmartPower Outlet
 *
 *  Copyright 2016 Adrian Caramaliu
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	// Automatically generated. Make future change here.
	definition (name: "Enhanced SmartPower Outlet", namespace: "ady624", author: "ady624") {
		capability "Actuator"
		capability "Switch"
		capability "Power Meter"
		capability "Energy Meter"
		capability "Configuration"
		capability "Refresh"
		capability "Sensor"

		// indicates that device keeps track of heartbeat (in state.heartbeat)
		attribute "heartbeat", "string"
        attribute "display", "string"
        
        command "reset"

		fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0B04,0B05", outClusters: "0019", manufacturer: "CentraLite",  model: "3200", deviceJoinName: "Outlet"
		fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0B04,0B05", outClusters: "0019", manufacturer: "CentraLite",  model: "3200-Sgb", deviceJoinName: "Outlet"
		fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0B04,0B05", outClusters: "0019", manufacturer: "CentraLite",  model: "4257050-RZHAC", deviceJoinName: "Outlet"
		fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0B04,0B05", outClusters: "0019"
	}

	// simulator metadata
	simulator {
		// status messages
		status "on": "on/off: 1"
		status "off": "on/off: 0"

		// reply messages
		reply "zcl on-off on": "on/off: 1"
		reply "zcl on-off off": "on/off: 0"
	}

	preferences {
		section {
			image(name: 'educationalcontent', multiple: true, images: [
				"http://cdn.device-gse.smartthings.com/Outlet/US/OutletUS1.jpg",
				"http://cdn.device-gse.smartthings.com/Outlet/US/OutletUS2.jpg"
				])
		}
	}

	// UI tile definitions
	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821", nextState: "turningOff"
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
				attributeState "turningOn", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821", nextState: "turningOff"
				attributeState "turningOff", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
			}
			tileAttribute ("power", key: "SECONDARY_CONTROL") {
				attributeState "power", label:'${currentValue} W', icon: "st.unknown.unknown"
			}
		}

		standardTile("refresh", "device.power", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		valueTile("energy", "device.display", inactiveLabel: false, decoration: "flat", width: 4, height: 1) {
			state "default", label:'${currentValue}', icon:"st.secondary.energy", unit: "kWh"
		}


		standardTile("reset", "device.energy", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
			state "default", label:'Reset kWh', action:"reset", icon:"st.secondary.refresh-icon"
		}

		valueTile("power", "device.power", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'${currentValue} W', icon:"st.secondary.energy", unit: "W"
		}

		main "power"
		details(["switch","energy", "reset","refresh"])
	}
}

// Parse incoming device messages to generate events
def parse(String description) {
	log.debug "description is $description"

	// save heartbeat (i.e. last time we got a message from device)
	state.heartbeat = Calendar.getInstance().getTimeInMillis()

	def finalResult = zigbee.getKnownDescription(description)

	//TODO: Remove this after getKnownDescription can parse it automatically
	if (!finalResult && description!="updated")
		finalResult = getPowerDescription(zigbee.parseDescriptionAsMap(description))

	if (finalResult) {
		log.info finalResult
		if (finalResult.type == "update") {
			log.info "$device updates: ${finalResult.value}"
		}
		else if (finalResult.type == "power") {
			def powerValue = (finalResult.value as Integer)/10
            def currentPower = device.currentValue("power")
            if (state.powerLastReported > 0) {
            	//calculate elapsed time in milli hours, we'll multiply this with W to get mWh for better accuracy
            	def elapsed = (now() - state.powerLastReported) / 3600 
                def energy = device.currentValue("power") * elapsed //energy calculated in mWh
                if (state.energySince > 0) {
                	state.energy = state.energy + energy
                } else {
                	state.energy = energy
                    state.energySince = now()
                }
                sendEvent(name: "energy", value: state.energy / 1000000) //energy is measured in kWh, but we store it in mWh for better accuracy
                sendEvent(name: "display", value: "${getEnergyValue()} in ${getEnergySince()}")
            }
            state.powerLastReported = now()
			sendEvent(name: "power", value: powerValue)
			/*
				Dividing by 10 as the Divisor is 10000 and unit is kW for the device. AttrId: 0302 and 0300. Simplifying to 10

				power level is an integer. The exact power level with correct units needs to be handled in the device type
				to account for the different Divisor value (AttrId: 0302) and POWER Unit (AttrId: 0300). CLUSTER for simple metering is 0702
			*/
		}
		else {
			sendEvent(name: finalResult.type, value: finalResult.value)
		}
	}
	else {
		log.warn "DID NOT PARSE MESSAGE for description : $description"
		log.debug zigbee.parseDescriptionAsMap(description)
	}
}

def off() {
	zigbee.off()
}

def on() {
	zigbee.on()
}

def refresh() {
	sendEvent(name: "heartbeat", value: "alive", displayed:false)
	zigbee.onOffRefresh() + zigbee.refreshData("0x0B04", "0x050B")
}

def configure() {
    reset()
	zigbee.onOffConfig() + powerConfig() + refresh()
}

def reset() {
    state.energySince = state.powerLastReported
    state.energy = 0
    sendEvent(name: "energy", value: 0)
	sendEvent(name: "display", value: "${getEnergyValue()} in ${getEnergySince()}")
}

//power config for devices with min reporting interval as 1 seconds and reporting interval if no activity as 10min (600s)
//min change in value is 01
def powerConfig() {
	[
		"zdo bind 0x${device.deviceNetworkId} 1 ${endpointId} 0x0B04 {${device.zigbeeId}} {}", "delay 200",
		"zcl global send-me-a-report 0x0B04 0x050B 0x29 30 600 {05 00}",				//The send-me-a-report is custom to the attribute type for CentraLite
		"send 0x${device.deviceNetworkId} 1 ${endpointId}", "delay 500"
	]
}

private getEndpointId() {
	new BigInteger(device.endpointId, 16).toString()
}

//TODO: Remove this after getKnownDescription can parse it automatically
def getPowerDescription(descMap) {
	def powerValue = "undefined"
	if (descMap.cluster == "0B04") {
		if (descMap.attrId == "050b") {
			if(descMap.value!="ffff")
				powerValue = zigbee.convertHexToInt(descMap.value)
		}
	}
	else if (descMap.clusterId == "0B04") {
		if(descMap.command=="07"){
			return	[type: "update", value : "power (0B04) capability configured successfully"]
		}
	}

	if (powerValue != "undefined"){
		return	[type: "power", value : powerValue]
	}
	else {
		return [:]
	}
}


def getEnergyValue() {
	if (!state || !state.energy) {
    	return "0Wh!!!"
    }
    if (state.energy > 1000000) {
    	return String.format("%.3f", state.energy / 1000000.00) + "kWh"
    } else {
    	return String.format("%.1f", state.energy / 1000.00) + "Wh"
    }
}


def getEnergySince() {
	if (!state || !state.energySince || state.energySince <= 0) {
    	return "0s!!!"
    }
    def dur = (now() - state.energySince) / 1000
    def d = (dur / 86400).toBigInteger()
    def h = ((dur - d * 86400) / 3600).toBigInteger()
    def m = ((dur - d * 86400 - h * 3600) / 60).toBigInteger()
    def s = (dur - d * 86400 - h * 3600 - m * 60).toBigInteger()
    return (d > 0 ? "${d} day" + (d > 1 ? "s " : " ") : " ") + "$h".padLeft(2, "0") + ":" + "$m".padLeft(2, "0") + ":" + "$s".padLeft(2, "0")
}