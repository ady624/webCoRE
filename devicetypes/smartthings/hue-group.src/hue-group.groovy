/**
 *  Hue Group
 *
 *  Author: Stuart Buchanan Based heavily on original code by Anthony Pastor with thanks
 */
// for the UI
preferences {
	input("transitionTimePref", "integer", title: "Time it takes for the lights to transition (default: 2)")   
}

metadata {
	definition (name: "Hue Group", namespace: "smartthings", author: "SmartThings") {
		capability "Switch Level"
		capability "Actuator"
		capability "Color Control"
		capability "Switch"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"

		command "setAdjustedColor"
        command "effectColorloop"        
        command "effectNone" 
        command "alertBlink"
        command "alertPulse"
        command "alertNone"
        command "refresh"
        command "reset"
        
        attribute "alertMode", "string"
        attribute "effectMode", "string"
		attribute "groupID", "string"
	}

	simulator {
		// TODO: define status and reply messages here
	}

    tiles(scale: 2) {
        multiAttributeTile(name: "switch", type: "lighting", width: 6, height: 6, canChangeIcon: true) {
            tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label: '${name}', action: "switch.off", icon: "st.lights.philips.hue-single", backgroundColor: "#79b821", nextState: "turningOff"
                attributeState "off", label: '${name}', action: "switch.on", icon: "st.lights.philips.hue-single", backgroundColor: "#ffffff", nextState: "turningOn"
                attributeState "turningOn", label: '${name}', action: "switch.off", icon: "st.lights.philips.hue-single", backgroundColor: "#79b821", nextState: "turningOff"
                attributeState "turningOff", label: '${name}', action: "switch.on", icon: "st.lights.philips.hue-single", backgroundColor: "#ffffff", nextState: "turningOn"
            }
            tileAttribute("device.level", key: "SLIDER_CONTROL") {
                attributeState "level", action: "switch level.setLevel"
            }
            tileAttribute("device.color", key: "COLOR_CONTROL") {
                attributeState "color", action: "setAdjustedColor"
            }
        }

        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label: "", action: "refresh.refresh", icon: "st.secondary.refresh"
        }

        standardTile("effectSelector", "device.effectMode", decoration: "flat", width: 2, height: 2) {
            state "colorloop on", label: '${name}', icon: "st.Weather.weather3", action: "effectColorloop", nextState: "colorloop off"
            state "colorloop off", label: '${name}', icon: "st.Weather.weather3", action: "effectNone", nextState: "colorloop on"
        }

        standardTile("alertSelector", "device.alertMode", decoration: "flat", width: 2, height: 2) {
            state "blink", label: '${name}', action: "alertBlink", icon: "st.Lighting.light11", backgroundColor: "#ffffff", nextState: "pulse"
            state "pulse", label: '${name}', action: "alertPulse", icon: "st.Lighting.light11", backgroundColor: "#e3eb00", nextState: "off"
            state "off", label: '${name}', action: "alertNone", icon: "st.Lighting.light13", backgroundColor: "#79b821", nextState: "blink"
        }

        standardTile("reset", "device.reset", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label: "Reset Color", action: "reset", icon: "st.lights.philips.hue-single"
        }
        valueTile("groupID", "device.groupID", inactiveLabel: false, decoration: "flat", width: 4, height: 2) {
            state "groupID", label: 'The Group ID is ${currentValue}   '
        }
        valueTile("transitiontime", "device.transitiontime", inactiveLabel: false, decoration: "flat", width: 6, height: 2) {
            state "transitiontime", label: 'Transitiontime is set to ${currentValue}   '
        }
    }

    main(["switch"])
    details(["switch", "refresh", "effectSelector", "alertSelector", "reset", "groupID", "transitiontime"])
    }

// parse events into attributes
def parse(description) {
	log.debug "parse() - $description"
	def results = []

	def map = description
	if (description instanceof String)  {
		log.debug "Hue Group stringToMap - ${map}"
		map = stringToMap(description)
	}

	if (map?.name && map?.value) {
		results << createEvent(name: "${map?.name}", value: "${map?.value}")
	}

	results

}

// handle commands
def on() 
{
	def transitiontime = transitionTimePref ?: 2
	def level = device.currentValue("level")
    if(level == null)
    {
    	level = 100
    }
	parent.groupOn(this, transitiontime, level)
	sendEvent(name: "switch", value: "on")
	sendEvent(name: "transitiontime", value: transitiontime)
    parent.poll()
}

def on(transitiontime)
{
	def level = device.currentValue("level")
    if(level == null)
    {
    	level = 100
    }
	parent.groupOn(this, transitiontime, level)
	sendEvent(name: "switch", value: "off")
	sendEvent(name: "transitiontime", value: transitiontime)
    parent.poll()
}

def off() 
{
	def transitiontime = transitionTimePref ?: 2
	parent.groupOff(this, transitiontime)
	sendEvent(name: "switch", value: "off")
	sendEvent(name: "transitiontime", value: transitiontime)
    parent.poll()
}

def off(transitiontime)
{
	parent.groupOff(this, transitiontime)
	sendEvent(name: "switch", value: "off")
	sendEvent(name: "transitiontime", value: transitiontime)
    parent.poll()
}

def poll() {
	parent.poll()
}

def nextLevel() {
	def level = device.latestValue("level") as Integer ?: 0
	if (level < 100) {
		level = Math.min(25 * (Math.round(level / 25) + 1), 100) as Integer
	}
	else {
		level = 25
	}
	setLevel(level)
}

def setLevel(percent) 
{
	def transitiontime = transitionTimePref ?: 2
	log.debug "Executing 'setLevel'"
	parent.setGroupLevel(this, percent, transitiontime)
	sendEvent(name: "level", value: percent)
	sendEvent(name: "transitiontime", value: transitiontime)

}
def setLevel(percent, transitiontime) 
{
	log.debug "Executing 'setLevel'"
	parent.setGroupLevel(this, percent, transitiontime)
	sendEvent(name: "level", value: percent)
	sendEvent(name: "transitiontime", value: transitiontime)
}

def setSaturation(percent) 
{
	def transitiontime = transitionTimePref ?: 2
	log.debug "Executing 'setSaturation'"
	parent.setGroupSaturation(this, percent, transitiontime )
	sendEvent(name: "saturation", value: percent)
	sendEvent(name: "transitiontime", value: transitiontime)
}
def setSaturation(percent, transitiontime) 
{
	log.debug "Executing 'setSaturation'"
	parent.setGroupSaturation(this, percent, transitiontime)
	sendEvent(name: "saturation", value: percent)
	sendEvent(name: "transitiontime", value: transitiontime)
}

def setHue(percent) 
{
	def transitiontime = transitionTimePref ?: 2
	log.debug "Executing 'setHue'"
	parent.setGroupHue(this, percent, transitionTimePref ?: 2)
	sendEvent(name: "hue", value: percent)
	sendEvent(name: "transitiontime", value: transitionTimePref ?: 2)
}

def setHue(percent, transitiontime) 
{
	log.debug "Executing 'setHue'"
	parent.setGroupHue(this, percent, transitiontime)
	sendEvent(name: "hue", value: percent)
	sendEvent(name: "transitiontime", value: transitiontime)
}

def setColor(value) {
	log.debug "setColor: ${value}"

	
	if(value.transitiontime)
	{
		sendEvent(name: "transitiontime", value: value.transitiontime)
	}
	else
	{
    	def transitiontime = transitionTimePref ?: 2
		sendEvent(name: "transitiontime", value: transitiontime)
		value << [transitiontime: transitiontime]
	}
	if (value.hex) 
	{
		sendEvent(name: "color", value: value.hex)
        
	} 
	else if (value.hue && value.saturation) 
	{
		def hex = colorUtil.hslToHex(value.hue, value.saturation)
		sendEvent(name: "color", value: hex)
	}
    if (value.hue && value.saturation) 
	{
		sendEvent(name: "saturation", value:  value.saturation)
        sendEvent(name: "hue", value:  value.hue)
	}
	if (value.level) 
	{
		sendEvent(name: "level", value: value.level)
	}
	if (value.switch) 
	{
		sendEvent(name: "switch", value: value.switch)
	}
	parent.setGroupColor(this, value)
}

def reset() {
    log.debug "Executing 'reset'"
    def value = [level:100, hex:"#90C638", saturation:56, hue:23]
    setAdjustedColor(value)
    parent.poll()
}

def setAdjustedColor(value) {
	log.debug "setAdjustedColor: ${value}"
	def adjusted = value + [:]
	adjusted.hue = adjustOutgoingHue(value.hue)
	adjusted.level = null // needed because color picker always sends 100
	setColor(adjusted)
}

def save() {
	log.debug "Executing 'save'"
}

def refresh() {
    def GroupIDfromParent = parent.getGroupID(this)
    log.debug "GroupID: ${GroupIDfromParent}"
    sendEvent(name: "groupID", value: GroupIDfromParent, isStateChange: true)
    log.debug "Executing 'refresh'"
    parent.manualRefresh()
	//parent.poll()
}

def adjustOutgoingHue(percent) {
	def adjusted = percent
	if (percent > 31) {
		if (percent < 63.0) {
			adjusted = percent + (7 * (percent -30 ) / 32)
		}
		else if (percent < 73.0) {
			adjusted = 69 + (5 * (percent - 62) / 10)
		}
		else {
			adjusted = percent + (2 * (100 - percent) / 28)
		}
	}
	log.info "percent: $percent, adjusted: $adjusted"
	adjusted
}

def setAlert(v) {
    log.debug "setGroupAlert: ${v}, $this"
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

def setEffect(v) {
    log.debug "setEffect: ${v}, $this"
    parent.setGroupEffect(this, v)
    sendEvent(name: "effect", value: v, isStateChange: true)
}

def effectNone() { 
    log.debug "Effect option: 'none'"
    setEffect("none")
}

def effectColorloop() { 
    log.debug "Effect option: 'colorloop'"
    setEffect("colorloop")
}