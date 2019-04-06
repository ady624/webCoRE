/*
 *
 *  Modified from SmartThings Aeon Minimote Device Handler Template.
 *
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
  *  Contributors: Jared Fisher, jscgs350
 *
 *  Updates:
 *  -------
 *  04-17-2016 : jscgs350: Original commit.
 *  08-27-2016 : jscgs350: Modified the device handler for my liking, primarly for looks and feel.
 *  03-11-2017 : Changed from valueTile to standardTile for a few tiles since ST's mobile app v2.3.x changed something between the two.
 *
 */

metadata {
	definition (name: "My Aeon Minimote", namespace: "jscgs350", author: "Jared Fisher") {
		capability "Actuator"
		capability "Button"
		capability "Configuration"
		capability "Sensor"
        
		attribute "numButtons", "STRING"
        
        // Virtual Button Attributes for defining button labels.
        attribute "lblPush1", "STRING"
        attribute "lblHold1", "STRING"
        attribute "lblPush2", "STRING"
        attribute "lblHold2", "STRING"
        attribute "lblPush3", "STRING"
        attribute "lblHold3", "STRING"
        attribute "lblPush4", "STRING"
        attribute "lblHold4", "STRING"
        
		command "pushed"
        command "held"
        command "pushed", [int]
        command "held", [int]
        command "push1"
        command "hold1"
        command "push2"
        command "hold2"
        command "push3"
        command "hold3"
        command "push4"
        command "hold4"

		fingerprint deviceId: "0x0101", inClusters: "0x86,0x72,0x70,0x9B", outClusters: "0x26,0x2B"
		fingerprint deviceId: "0x0101", inClusters: "0x86,0x72,0x70,0x9B,0x85,0x84", outClusters: "0x26" // old style with numbered buttons
	}
    
    preferences {
    	section ("Labels") {
            input("lblPush1", "text", title: "Label for Button 1 Push?", required: false, displayDuringSetup: false, defaultValue: "Push 1", description: "Label for the push button.")  
            input("lblHold1", "text", title: "Label for Button 1 Hold?", required: false, displayDuringSetup: false, defaultValue: "Hold 1", description: "Label for the hold button.")  
            input("lblPush2", "text", title: "Label for Button 2 Push?", required: false, displayDuringSetup: false, defaultValue: "Push 2", description: "Label for the push button.")  
            input("lblHold2", "text", title: "Label for Button 2 Hold?", required: false, displayDuringSetup: false, defaultValue: "Hold 2", description: "Label for the hold button.")  
            input("lblPush3", "text", title: "Label for Button 3 Push?", required: false, displayDuringSetup: false, defaultValue: "Push 3", description: "Label for the push button.")  
            input("lblHold3", "text", title: "Label for Button 3 Hold?", required: false, displayDuringSetup: false, defaultValue: "Hold 3", description: "Label for the hold button.")  
            input("lblPush4", "text", title: "Label for Button 4 Push?", required: false, displayDuringSetup: false, defaultValue: "Push 4", description: "Label for the push button.")  
            input("lblHold4", "text", title: "Label for Button 4 Hold?", required: false, displayDuringSetup: false, defaultValue: "Hold 4", description: "Label for the hold button.")  
        }
    }
 
	tiles (scale: 2) {
    	standardTile("buttonRemote", "device.button", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
      		state "default", label: 'Remote', action:"", icon:"st.unknown.zwave.remote-controller"
        }
    	standardTile("buttonHead", "device.button", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
      		state "default", label: '', action:"", icon:"st.unknown.zwave.remote-controller"
    	}
    	standardTile("pushHead", "device.button", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
      		state "default", label: 'Push', action:"", icon:"st.illuminance.illuminance.dark"
    	}
    	standardTile("holdHead", "device.button", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
      		state "default", label: 'Hold', action:"", icon:"st.illuminance.illuminance.dark"
    	}
        
		standardTile("buttonNum1", "device.button", width: 2, height: 2) {
        	state("default", label: "Button 1", defaultState: true, backgroundColor: "#ffffff", icon: "st.unknown.thing.thing-circle", decoration: "flat")
        }        
        standardTile("pushNum1", "device.lblPush1", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state("default", label: '${currentValue}', action: "push1", defaultState: true)
        }
        standardTile("holdNum1", "device.lblHold1", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
           	state("default", label: '${currentValue}', action: "hold1", defaultState: true) 
        }
		standardTile("buttonNum2", "device.button", width: 2, height: 2) {
        	state("default", label: "Button 2", defaultState: true, backgroundColor: "#ffffff", icon: "st.unknown.thing.thing-circle", decoration: "flat")
        }        
        standardTile("pushNum2", "device.lblPush2", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state("default", label: '${currentValue}', action: "push2", defaultState: true)
        }
        standardTile("holdNum2", "device.lblHold2", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
           	state("default", label: '${currentValue}', action: "hold2", defaultState: true) 
        }
		standardTile("buttonNum3", "device.button", width: 2, height: 2) {
        	state("default", label: "Button 3", defaultState: true, backgroundColor: "#ffffff", icon: "st.unknown.thing.thing-circle", decoration: "flat")
        }        
        standardTile("pushNum3", "device.lblPush3", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state("default", label: '${currentValue}', action: "push3", defaultState: true)
        }
        standardTile("holdNum3", "device.lblHold3", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
           	state("default", label: '${currentValue}', action: "hold3", defaultState: true) 
        }
		standardTile("buttonNum4", "device.button", width: 2, height: 2) {
        	state("default", label: "Button 4", defaultState: true, backgroundColor: "#ffffff", icon: "st.unknown.thing.thing-circle", decoration: "flat")
        }        
        standardTile("pushNum4", "device.lblPush4", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state("default", label: '${currentValue}', action: "push4", defaultState: true)
        }
        standardTile("holdNum4", "device.lblHold4", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
           	state("default", label: '${currentValue}', action: "hold4", defaultState: true) 
        }

    	standardTile("configure", "device.configure", width: 6, height: 2, inactiveLabel: false, decoration: "flat") {
      		state "configure", label: '', action:"configuration.configure", icon:"st.secondary.configure"
    	}
        
        main("buttonRemote")
		details("buttonHead", "pushHead", "holdHead",
        		"buttonNum1", "pushNum1", "holdNum1",
                "buttonNum2", "pushNum2", "holdNum2",
                "buttonNum3", "pushNum3", "holdNum3",
                "buttonNum4", "pushNum4", "holdNum4",
                "configure")
	}
}

def installed() {
	initLabels()
}

def updated() {
	initLabels()
}

def initLabels() {
    (1..4).each { button ->
    	["Push","Hold"].each { action ->
           	def descriptionText = "Updating button ${button} ${action}"
            def settingName = "lbl${action}${button}"
            log.debug descriptionText + ": ${settings[settingName]}"
			sendEvent(name: "lbl${action}${button}", value: "${settings[settingName]}", descriptionText: descriptionText, isStateChange: true, displayed: false)
		}
    }
}

def parse(String description) {
	def results = []
	if (description.startsWith("Err")) {
	    results = createEvent(descriptionText:description, displayed:true)
	} else {
		def cmd = zwave.parse(description, [0x2B: 1, 0x80: 1, 0x84: 1])
    	if(cmd) results += zwaveEvent(cmd)
		if(!results) results = [ descriptionText: cmd, displayed: false ]
	}
	//log.debug("Parsed '$description' to $results")
	return results
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd) {
	def results = [createEvent(descriptionText: "$device.displayName woke up", isStateChange: false)]
    
    results += configurationCmds().collect{ response(it) }
	results << response(zwave.wakeUpV1.wakeUpNoMoreInformation().format())

	return results
}

def buttonEvent(button, held) {
	// Leaving value as pushed or held to stay compatible with Buton Controller Smart App for now.
	button = button as Integer
	if (held) {
		createEvent(name: "button", value: "held", data: [buttonNumber: button, action: (held ? "held" : "pushed")], source: "DEVICE", descriptionText: "$device.displayName button $button was held", isStateChange: true)
	} else {
		createEvent(name: "button", value: "pushed", data: [buttonNumber: button, action: (held ? "held" : "pushed")], source: "DEVICE", descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
	}
}

def zwaveEvent(physicalgraph.zwave.commands.sceneactivationv1.SceneActivationSet cmd) {
	Integer button = ((cmd.sceneId + 1) / 2) as Integer
	Boolean held = !(cmd.sceneId % 2)
	buttonEvent(button, held)
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
	Integer button = (cmd.value / 40 + 1) as Integer
	Boolean held = (button * 40 - cmd.value) <= 20
	buttonEvent(button, held)
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	[ descriptionText: "$device.displayName: $cmd", linkText:device.displayName, displayed: false ]
}

def configurationCmds() {
	def cmds = []
	def hubId = zwaveHubNodeId
	(1..4).each { button ->
		cmds << zwave.configurationV1.configurationSet(parameterNumber: 240+button, scaledConfigurationValue: 1).format()
	}
	(1..4).each { button ->
		cmds << zwave.configurationV1.configurationSet(parameterNumber: (button-1)*40, configurationValue: [hubId, (button-1)*40 + 1, 0, 0]).format()
		cmds << zwave.configurationV1.configurationSet(parameterNumber: (button-1)*40 + 20, configurationValue: [hubId, (button-1)*40 + 21, 0, 0]).format()
	}
	cmds
}

def configure() {
	// Set the number of buttons to 4
	sendEvent(name: "numButtons", value: "4", displayed: false)

	def cmds = configurationCmds()
	//log.debug("Sending configuration: $cmds")
	return cmds
}

def push1() {
	pushed(1)
}

def push2() {
	pushed(2)
}

def push3() {
	pushed(3)
}

def push4() {
	pushed(4)
}

def pushed(button) {
	sendEvent(name: "button", value: "pushed", data: [buttonNumber: button, action: "pushed"], source: "COMMAND", descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
}

def hold1() {
	held(1)
}

def hold2() {
	held(2)
}

def hold3() {
	held(3)
}

def hold4() {
	held(4)
}

def held(button) {
    sendEvent(name: "button", value: "held", data: [buttonNumber: button, action: "held"], source: "COMMAND", descriptionText: "$device.displayName button $button was held", isStateChange: true)
}