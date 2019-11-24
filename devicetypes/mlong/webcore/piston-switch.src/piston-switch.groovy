/**
 *  Copyright 2015 SmartThings
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
	definition (name: "Piston Switch", namespace: "mlong/webcore", author: "Martin Long") {
		capability "Actuator"
		capability "Switch"
		capability "Sensor"
		capability "Health Check"
        
        command "off"
        command "on"
	}

	simulator {

	}
	tiles {
		//standardTile("button", "device.button", width: 1, height: 1) {
		//	state "default", label: "", icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
		//}
 		//standardTile("push1", "device.button", width: 1, height: 1, decoration: "flat") {
		//	state "default", label: "Push 1", backgroundColor: "#ffffff", action: "push1"
		//} 
 		//standardTile("hold1", "device.button", width: 1, height: 1, decoration: "flat") {
		//	state "default", label: "Hold 1", backgroundColor: "#ffffff", action: "hold1"
		//}          
		//main "button"
		//details(["button","push1","hold1"])
	}
}

def parse(String description) {
	
}

def off() {
	sendEvent(name: "switch", value: "off", descriptionText: "$device.displayName Manually turned off", isStateChange: true)
} 

def on() {
	sendEvent(name: "switch", value: "on", descriptionText: "$device.displayName Switch on", isStateChange: true)
    parent.executeSelf()
	sendEvent(name: "switch", value: "off", descriptionText: "$device.displayName Completed Switch off", isStateChange: true)
}

def installed() {
	log.trace "Executing 'installed'"
	initialize()
}

def updated() {
	log.trace "Executing 'updated'"
	initialize()
}

private initialize() {
	log.trace "Executing 'initialize'"

	sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
	sendEvent(name: "healthStatus", value: "online")
	sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
	sendEvent(name: "switch", value: "off", descriptionText: "Switch off", isStateChange: true)
}