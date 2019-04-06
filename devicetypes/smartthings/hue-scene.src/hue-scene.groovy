/**
 *  Hue Scene
 *
 *  Author: CyrilPeponnet
 */
// for the UI
metadata {
    // Automatically generated. Make future change here.
    definition (name: "Hue Scene", namespace: "smartthings", author: "CyrilPeponnet") {
        capability "Actuator"
        capability "Switch"
        capability "Momentary"
        capability "Sensor"
        
        command "alertBlink"
        command "alertPulse"
        command "alertNone"
        
        attribute "alertMode", "string"
        attribute "lights", "string"
        attribute "group", "string"
        attribute "offStateId", "string"

    }

    // simulator metadata
    simulator {
    }

    tiles (scale: 2){
        multiAttributeTile(name:"push", type: "momentary", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on",  label:'Push', action:"momentary.push", icon:"st.lights.philips.hue-multi", backgroundColor:"#07A4D2"
            }
            tileAttribute ("lights", key: "SECONDARY_CONTROL") {
                attributeState "default", label:'${currentValue}'
            }
        }
        standardTile("switch", "device.switch", inactiveLabel: false, height: 2, width: 2, decoration: "flat") {
            state "off", label:"", action:"switch.off", icon:"st.secondary.off"
        }
        
        standardTile("alertSelector", "device.alertMode", decoration: "flat", width: 2, height: 2) {
        	state "blink", label:'${name}', action:"alertBlink", icon:"st.Lighting.light11", backgroundColor:"#ffffff", nextState:"pulse"
            state "pulse", label:'${name}', action:"alertPulse", icon:"st.Lighting.light11", backgroundColor:"#e3eb00", nextState:"off"
            state "off", label:'${name}', action:"alertNone", icon:"st.Lighting.light13", backgroundColor:"#79b821", nextState:"blink"
       }

        main "push"
        details "push", "switch", "alertSelector"
    }
}

def parse(String description) {
}

def push() {
    parent.pushScene(this, device.currentValue("group")?: 0)
    sendEvent(name: "momentary", value: "pushed", isStateChange: true)
}

def on() {
    parent.pushScene(this, device.currentValue("group")?: 0)
    sendEvent(name: "switch", value: "on", isStateChange: true)
}

def off() {
    parent.pushScene(this, device.currentValue("group")?: 0, device.currentValue("offStateId")?: null)
    sendEvent(name: "switch", value: "off", isStateChange: true)
}

def setAlert(v) {
    log.debug "setAlert: ${v}, $this"
    parent.setGroupAlert(this, v)
    sendEvent(name: "alert", value: v, isStateChange: true)
}

def alertNone() {
	log.debug "Alert option: 'none'"
    setAlert("none")
}

def alertBlink() {
	log.debug "Alert option: 'select'"
    setAlert("select")
}

def alertPulse() {
	log.debug "Alert option: 'lselect'"
    setAlert("lselect")
}