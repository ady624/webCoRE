/**
 *  Real Momentary Button
 *
 *  Copyright 2016 Eric Vitale
 *
 *  Version 1.0.0 - Initial Release (06/27/2017)
 *
 *  This SmartThings device handler can be found @ https://github.com/ericvitale/ST-Real-Momentary-Button
 *  You can find my other SmartApps or Device Handlers @ https://github.com/ericvitale
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
	definition (name: "Real Momentary Button", namespace: "ericvitale", author: "ericvitale@gmail.com") {
		capability "Actuator"
		capability "Button"
		capability "Momentary"
		capability "Sensor"
        capability "Switch"
	}
        
    tiles {
		standardTile("button", "device.button", width: 2, height: 2, canChangeIcon: true) {
        	state "default", label: "Push", action: "push", icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
			state "pushed", label: 'Push', action: "push", backgroundColor: "#ffffff"
		}
		main "button"
		details "button"
	}
}

def parse(String description) {
}

def push() {
	sendEvent(name: "button", value: "pushed", isStateChange: true)
    log.debug "Pushed..."
}

def on() {
	push()
}

def off() {
}