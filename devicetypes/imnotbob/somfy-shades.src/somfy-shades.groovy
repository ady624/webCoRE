/**
 * 
 * https://community.smartthings.com/t/my-somfy-smartthings-integration/13492
 * Modified ERS 12/29/2016
 *
 * Version 1.0.7
 *
 * Version History
 *
 * 1.0.7    26 Mar 2017		Few updates  (be sure to go into each device, select settings (gear), and hit done
 * 1.0.6    29 Dec 2016		Health Check
 * 1.0.5    01 May 2016		bug fixes
 * 1.0.4    01 May 2016		Sync commands for cases where blinds respond to multiple channels (all vs. single)
 * 1.0.3    17 Apr 2016		Expanded runIn timer for movement and  completed states
 * 1.0.2    04 Apr 2016		Added runIn timer for movement vs. completed states
 * 1.0.1    07 Mar 2016		Add Blinds support by edit device to set to blinds type
 * 1.0.0    24 Feb 2016		Multi-tile, Window Shade Capability, Device Handler attempts to maintain state
 *
 * Notes:
 *
 * Somfy ZRTSII does not report accurate status for the device.
 *
 * This device handler maintains an internal view of device status based on last command
 * reissuing a command to the shade (up, down, preset (when stopped)) does not move the shade/blinds if it is already in that position
 * My/stop command does different actions depending if the shade is idle (go to MY or closed position) or moving (stop)
 *
 * Once the device is installed, it defaults to "shade" operation.  If "blinds" operation is desired, for the device go to settings (gear)
 * and change the device operation to Window Blinds
 *
 *	Shade and Blinds operate differently in ZRTSII buttons
 *	- Shades actions: up button: open (on switch),  down button: close (off switch),       my/stop button: presetPosition (50%)
 *	- Blinds actions: up button: open (on switch),  down button: tilt open (off switch),   my/stop button: close (50%)
 *
 * Window Shade Capability standardizes:  (these should not be changed, except by SmartThings capabilities updates)
 *	- windowShade: unknown, closed, open, partially open, closing, opening 
 *	- Commands:  open(), close(), presetPosition()
 *
 */
  metadata {
    definition (name: "somfy-shades", namespace: "imnotbob", author: "Eric, Ash, Others", vid: "generic-shade") {
        capability "Switch Level"
        capability "Switch"
        capability "Window Shade"
        //capability "Polling"
        capability "Refresh"
        capability "Actuator"
        capability "Health Check"

        attribute "stopStr", "enum", ["preset/stop", "close/stop"]

        command "OpenSync"
        command "CloseSync"
        command "TiltSync"
        command "levelOpenClose", [ "number" ]

	attribute "lastPoll", "STRING"

        fingerprint deviceId: "0x1105", inClusters: "0x2C, 0x72, 0x26, 0x20, 0x25, 0x2B, 0x86"
    }

    simulator {
        status "on":  "command: 2003, payload: FF"
        status "off": "command: 2003, payload: 00"
        status "09%": "command: 2003, payload: 09"
        status "10%": "command: 2003, payload: 0A"
        status "33%": "command: 2003, payload: 21"
        status "66%": "command: 2003, payload: 42"
        status "99%": "command: 2003, payload: 63"
        
        // reply messages
        reply "2001FF,delay 5000,2602": "command: 2603, payload: FF"
        reply "200100,delay 5000,2602": "command: 2603, payload: 00"
        reply "200119,delay 5000,2602": "command: 2603, payload: 19"
        reply "200132,delay 5000,2602": "command: 2603, payload: 32"
        reply "20014B,delay 5000,2602": "command: 2603, payload: 4B"
        reply "200163,delay 5000,2602": "command: 2603, payload: 63"
    }

    preferences {
	input ("shadeType", "enum", options:[
		"shades": "Window Shades",
		"blinds": "Window Blinds"],
		title: "Window Shades or Blinds?", description: "set type (shades or blinds)", defaultValue: "shades",
                required: false, displayDuringSetup: true )
	}

    tiles(scale: 2) {
        multiAttributeTile(name:"shade", type: "lighting", width: 6, height: 4) {
            tileAttribute("device.windowShade", key: "PRIMARY_CONTROL") {
                attributeState("unknown", label:'${name}', action:"refresh.refresh", icon:"st.doors.garage.garage-open", backgroundColor:"#ffa81e")
                attributeState("closed",  label:'${name}', action:"open", icon:"st.doors.garage.garage-closed", backgroundColor:"#bbbbdd", nextState: "opening")
                attributeState("open",    label:'up', action:"close", icon:"st.doors.garage.garage-open", backgroundColor:"#ffcc33", nextState: "closing")
                attributeState("partially open", label:'preset', action:"presetPosition", icon:"st.Transportation.transportation13", backgroundColor:"#ffcc33")
                attributeState("closing", label:'${name}', action:"presetPosition", icon:"st.doors.garage.garage-closing", backgroundColor:"#bbbbdd")
                attributeState("opening", label:'${name}', action:"presetPosition", icon:"st.doors.garage.garage-opening", backgroundColor:"#ffcc33")
            }
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
                attributeState("level", action:"switch level.setLevel")
            }
            tileAttribute ("device.speedLevel", key: "VALUE_CONTROL") {
                attributeState("level", action: "levelOpenClose")
            }
        }

        standardTile("switchmain", "device.windowShade") {
            state("unknown", label:'${name}', action:"refresh.refresh", icon:"st.doors.garage.garage-open", backgroundColor:"#ffa81e")
            state("closed",  label:'${name}', action:"open", icon:"st.doors.garage.garage-closed", backgroundColor:"#bbbbdd", nextState: "opening")
            state("open",    label:'up', action:"close", icon:"st.doors.garage.garage-open", backgroundColor:"#ffcc33", nextState: "closing")
            state("partially open", label:'preset', action:"presetPosition", icon:"st.Transportation.transportation13", backgroundColor:"#ffcc33")
            state("closing", label:'${name}', action:"presetPosition", icon:"st.doors.garage.garage-closing", backgroundColor:"#bbbbdd")
            state("opening", label:'${name}', action:"presetPosition", icon:"st.doors.garage.garage-opening", backgroundColor:"#ffcc33")

//            state("on", label:'up', action:"switch.off", icon:"st.doors.garage.garage-open", backgroundColor:"#ffcc33")
//            state("off", label:'closed', action:"switch.on", icon:"st.doors.garage.garage-closed", backgroundColor:"#bbbbdd")
//            state("default", label:'preset', action:"presetPosition", icon:"st.Transportation.transportation13", backgroundColor:"#ffcc33")
        }

        standardTile("on", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state("on", label:'open', action:"switch.on", icon:"st.doors.garage.garage-opening")
        }
        standardTile("off", "device.stopStr", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state("close/stop", label:'close/stop', action:"switch.off", icon:"st.doors.garage.garage-closing")
            state("default", label:'close', action:"switch.off", icon:"st.doors.garage.garage-closing")
        }
        standardTile("preset", "device.stopStr", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state("close/stop", label:'slats open', action:"switch level.setLevel", icon:"st.Transportation.transportation13")
            state("default", label:'preset/stop', action:"switch level.setLevel", icon:"st.Transportation.transportation13")
        }
        controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 3, inactiveLabel: false) {
            state("level", action:"switch level.setLevel")
        }

        standardTile("refresh", "command.refresh", width:2, height:2, inactiveLabel: false, decoration: "flat") {
                state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }

//  Poll provides data, but the ZRTSII does not provide accurate status
//
//      standardTile("poll", "command.poll", width:2, height:2, inactiveLabel: false, decoration: "flat") {
//              state "default", label:'poll', action:"poll", icon:"st.secondary.poll"
//      }

        main(["switchmain"])
        details(["shade", "on", "off", "preset"])
    }
}

def configure() {
    log.trace "configure() called"
    updated()
}

def ping() {
	log.trace "ping called"
	def now=new Date()
	def tz = location.timeZone
	def nowString = now.format("MMM/dd HH:mm",tz)
	sendEvent("name":"lastPoll", "value":nowString, displayed: false)
	refresh()
}

def updated() {
    log.trace "updated() called"

    sendEvent(name: "checkInterval", value: 60 * 60 * 1, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID], displayed: false)

    def currstat = device.latestValue("level")
    def currstat1 = device.latestValue("windowShade")

    log.debug "Shade type: ${settings?.shadeType}"
    if (settings?.shadeType) {
        if (settings.shadeType == "shades") {
            sendEvent(name: "stopStr", value: "preset/stop")
        } else {
            sendEvent(name: "stopStr", value: "close/stop")
        }
    } else {
        sendEvent(name: "stopStr", value: "preset/stop")
    }

    log.debug "switch state: ${currstat}  windowShade state: ${currstat1}"
    if ( (currstat == null) || (currstat1 == null)) {
        if (currstat > null) {
            if (currstat >= 75) {
                //sendEvent(name: "windowShade", value: "open")
                finishOpenShade()
            } else if (currstat <= 25) {
                //sendEvent(name: "windowShade", value: "closed")
                finishCloseShade()
            } else {
                //sendEvent(name: "windowShade", value: "partially open")
                finishPartialOpenShade()
            }
        }
    }
}

def parse(String description) {
    description
    def result = []
    def cmd = zwave.parse(description, [0x20: 1, 0x26: 1, 0x70: 1])
    //log.debug "Parsed ${description} to ${cmd}"
    if (cmd) {
        result = zwaveEvent(cmd)
        log.debug "zwaveEvent( ${cmd} ) returned ${result.inspect()}"
    } else {
        log.debug "Non-parsed event: ${description}"
    }
    def now=new Date()
    def tz = location.timeZone
    def nowString = now.format("MMM/dd HH:mm",tz)
    result << createEvent("name":"lastPoll", "value":nowString, displayed: false)
    return result
}

def levelOpenClose(value) {
    log.trace "levelOpenClose called with value $value"
    if (value) {
        on()
    } else {
        off()
    }
}

// Somfy ZRTSII does not report accurate status for the device.
// This device handler maintains an internal view of device status based on last command
// reissuing a command to the shade (up, down, preset (my) (when stopped)) does not move the shade if it is already in that position
// My/stop command does different actions depending if the shade is idle (go to MY position) or moving (stop)

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd)
{
    def result = []
    def tempstr = ""
    def statstr = "SAME"

    //log.trace "Basic report cmd.value:  ${cmd.value}"

    if (cmd.value == 0) {
        //result << createEvent(name: "switch", value: "off")

        tempstr = "closed"
        if (settings?.shadeType) {
            if (settings.shadeType == "blinds") {
                tempstr = "tilted open"
            }
        }
    } else if (cmd.value == 0xFF) {
        //result << createEvent(name: "switch", value: "on")
        tempstr = "open"

    } else {  // This has never happend
        //result << createEvent(name: "switch", value: "default")
        tempstr="neither open or closed"
    }
    def swstatstr = "${device.latestValue('switch')}"
    if (cmd.value == 0 && swstatstr == "on") { statstr = "DIFFERENT" }
    if (cmd.value == 0xFF && swstatstr == "off") { statstr = "DIFFERENT" }
        
    //log.debug "${statstr} Zwave state is ${tempstr}; device stored state is ${device.latestValue('switch')} dimmer level: ${device.latestValue('level')} "
    return result
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
    def result = []
    def tempstr = ""

    log.debug "SwitchBinaryReport cmd.value:  ${cmd.value}"
    
    if (cmd.value == 0) {
        tempstr = "closed"
        if (settings?.shadeType) {
            if (settings.shadeType == "blinds") {
                tempstr = "tilted open"
            }
        }

    } else if (cmd.value == 0xFF) {
        tempstr = "open"

    } else {  // this has never happened
        tempstr="neither open or closed"
    }
    log.debug "Reported state is ${tempstr}; device is ${device.latestValue('switch')}  ${device.latestValue('level')} "
    
    //result << createEvent(name:"switch", value: cmd.value ? "on" : "off")
    //result << createEvent(name: "level",value: cmd.value, unit:"%",
        //descriptionText:"${device.displayName} dimmed ${cmd.value==255 ? 100 : cmd.value}%")
    return result
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv1.SwitchMultilevelReport cmd)
{
    def result = []
    def tempstr = ""

    log.trace "SwitchMultilevelReport cmd.value:  ${cmd.value}"
    
    if (cmd.value == 0) {
        //result << createEvent(name: "switch", value: "off")
        tempstr = "closed"
        if (settings?.shadeType) {
            if (settings.shadeType == "blinds") {
                tempstr = "tilted open"
            }
        }

    } else if (cmd.value == 0xFF) {
        //result << createEvent(name: "switch", value: "on")
        tempstr = "open"
    } else {
        //result << createEvent(name: "switch", value: "default")
        tempstr="neither open or closed"
    }
    //result << createEvent(name: "level",value: cmd.value, unit:"%",
      //descriptionText:"${device.displayName} dimmed ${cmd.value==255 ? 100 : cmd.value}%")
    log.debug "Reported state is ${tempstr}; device is ${device.latestValue('switch')}  ${device.latestValue('level')} "
    return result
}

def on() {
    int level = 100
    log.trace "on() treated as open()"
    setLevel(level) 
}

def off() {
    int level = 0
    log.trace "off() treated as close()"
    setLevel(level) 
}

def setLevel() {
    log.trace "setLevel() treated as preset position"
    setLevel(50) 
}

def open() {
    log.trace "open()"
    on()
}

def close() {
    log.trace "close()"
    off()
}

def presetPosition() {
    log.trace "presetPosition()"
    setLevel(50)
}

def OpenSync() {
    log.trace "OpenSync()"
    finishOpenShade()
}

def CloseSync() {
    log.trace "CloseSync()"
    finishCloseShade()
}

def TiltSync() {
    log.trace "TiltSync()"
    finishPartialOpenShade()
}

def refresh() {
    log.trace "refresh()"
    delayBetween([
        //zwave.switchBinaryV1.switchBinaryGet().format(),
        //zwave.switchMultilevelV1.switchMultilevelGet().format(),
        //zwave.meterV2.meterGet(scale: 0).format(),      // get kWh
        //zwave.meterV2.meterGet(scale: 2).format(),      // get Watts
        //zwave.sensorMultilevelV1.sensorMultilevelGet().format(),
        //zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType:1, scale:1).format(),  // get temp in Fahrenheit
        //zwave.batteryV1.batteryGet().format(),
        zwave.basicV1.basicGet().format()
    ], 3000)
}

// If you add the Polling capability to your device type, this command
// will be called approximately every 5 minutes to check the device's state
// zrtsII does not provide accurate status of shade position

//def poll() {
//        log.trace "Poll"
//        zwave.basicV1.basicGet().format()
//}

def setLevel(level) {
    log.trace "setLevel(level)  {$level}"
    log.debug "level.inspect " + level.inspect()

    int newlevel = level

    if (level > null) {

        if (level >= 75) {
            sendEvent(name: "windowShade", value: "opening")
            sendEvent(name: "level", value: level)
            sendEvent(name: "switch", value: "on")
            runIn(25, "finishOpenShade", [overwrite: true])
            delayBetween([
                zwave.switchMultilevelV1.switchMultilevelSet(value: 0xFF).format(),
                zwave.basicV1.basicGet().format()
//                sendEvent(name: "windowShade", value: "open"),
//                sendEvent(name: "switch", value: "on")
            ], 4000)
        } else if (level <= 25) {
            sendEvent(name: "windowShade", value: "closing")
            sendEvent(name: "switch", value: "off")
            runIn(25, "finishCloseShade", [overwrite: true])
            if (settings.shadeType == "shades") {
                delayBetween([
                    zwave.switchMultilevelV1.switchMultilevelSet(value: 0x00).format(),
                    zwave.basicV1.basicGet().format()
//                    sendEvent(name: "windowShade", value: "closed"),
//                    sendEvent(name: "switch", value: "off")
                ], 4000)
            } else {
                delayBetween([
                    zwave.switchMultilevelV1.switchMultilevelStopLevelChange().format(),
                    zwave.basicV1.basicGet().format()
//                    sendEvent(name: "windowShade", value: "closed"),
//                    sendEvent(name: "switch", value: "off")
                ], 4000)
            }
        } else {
            def currstat = device.latestValue("windowShade")
            if (currstat == "open") { sendEvent(name: "windowShade", value: "closing") }
            else { sendEvent(name: "windowShade", value: "opening") }
            sendEvent(name: "level", value: level)
            sendEvent(name: "switch", value: "on")
            runIn(15, "finishPartialOpenShade", [overwrite: true])
            if (settings.shadeType == "shades") {
                delayBetween([
                    zwave.switchMultilevelV1.switchMultilevelStopLevelChange().format(),
                    zwave.basicV1.basicGet().format()
//                    sendEvent(name: "windowShade", value: "partially open"),
//                    sendEvent(name: "switch", value: "default")
                ], 4000)
            } else {
                delayBetween([
                    zwave.switchMultilevelV1.switchMultilevelSet(value: 0x00).format(),
                    zwave.basicV1.basicGet().format()
//                    sendEvent(name: "windowShade", value: "partially open"),
//                    sendEvent(name: "switch", value: "default")
                ], 4000)
            }
        }

        // this code below causes commands not be sent/received by the Somfy ZRTSII - I assume delayBetween is asynchronous...

        //log.trace("finished level adjust")
        //if (newlevel != level) { 
            //log.trace("finished level adjust1")
            //delayBetween([
                //sendEvent(name: "level", value: newlevel)
            //], 1000)
        //}
    }
}

def finishOpenShade() {
    sendEvent(name: "windowShade", value: "open")
    def newlevel = 99
    sendEvent(name: "level", value: newlevel)
    sendEvent(name: "switch", value: "on")
}

def finishCloseShade() {
    sendEvent(name: "windowShade", value: "closed")
    def newlevel = 0
    sendEvent(name: "level", value: newlevel)
    sendEvent(name: "switch", value: "off")
}

def finishPartialOpenShade() {
    sendEvent(name: "windowShade", value: "partially open")
    def newlevel = 50
    sendEvent(name: "level", value: newlevel)
    sendEvent(name: "switch", value: "on")
}

// this appears to never be called

//def setLevel(level, duration) {
//    log.trace "setLevel(level, duration)  {$level} ${duration}"
//    setLevel(level)
//    return
//}
