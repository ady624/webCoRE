/**
 *  Aeon Smart Energy Switch gen-1
 *  Aeon Micro Smart Switch (G2)
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
 *  Updates:
 *  -------
 *  02-16-2016 : Removed posting to the Activity Feed (Recently tab) in the phone app and event log.
 *  02-17-2016 : Added the ability to change the icon.
 *  02-20-2016 : Fixed to use the right parameters for changed/timed reporting, and documented the parameters better.
 *  02-21-2016 : Made certain configuration parameters changeable via device preferences instead of having to tweak code all the time.
 *  02-27-2016 : Changed date formats to be MM-dd-yyyy h:mm a
 *  02-29-2016 : Changed reportType variable from 0 to 1.
 *  03-11-2016 : Due to ST's v2.1.0 app totally hosing up SECONDARY_CONTROL, implemented a workaround to display that info in a separate tile.
 *  03-19-2016 : Changed tile layout, added clarity for preferences, and removed rounding (line 171)
 *  07-07-2016 : Check for wildly large watts value coming from the switch and do not process them.
 *  08-22-2016 : Tile format changes, specifically statusText.
 *  08-27-2016 : Modified the device handler for my liking, primarly for looks and feel.
 *  01-08-2017 : Added code for Health Check capabilities/functions, and cleaned up code.
 *  01-18-2017 : Removed code no longer needed, and added another parameter in Preference to enable or disable the display of values in the Recently tab and device's event log (not Live Logs).  Enabling may be required for some SmartApps.
 *  01-19-2017 : Added code similar to the HEM v1 to display energy and cost.
 *  02-11-2017 : Cleaned up code and added an icon to the secondary_control section of the main tile.
 *  03-11-2017 : Changed from valueTile to standardTile for a few tiles since ST's mobile app v2.3.x changed something between the two.
 *  03-24-2017 : Changed color schema to match ST's new format.
 *  03-26-2017 : Added a new device Preference that allows for selecting how many decimal positions should be used to display for WATTS and kWh.  What's stored for the actual meter reading that's seen in the IDE for Power, and what's sent to SmartApps, did not change.
 *  05-28-2017 : Sometimes the HEM will send a super low reading, like 0.04672386; which in that case the decimal position setting would not get applied if you used 3.  I fixed that.
 *  06-12-2017 : Updated code to make sure kWh or kVAh readings from the reader are larger that the previous reading.  There should never be a smaller reading from the previous reading.
 *  09-06-2017 : Removed fingerprint.  Checking to see if this helps in joining the device since it doesn't have to go through all the configuration steps when included.
 *  09-15-2017 : Changed tile layout, made tiles smaller, and removed the reset info messages since you can find them in the Recently tab or the device's event log.
 *  09-23-2017 : Changed layout to look like my Zooz DTH, cleaned up code a lot.
 *  10-04-2017 : Fixed reset issues with energy/kWh not resetting properly.  (more of a workaround for now)
 *  10-07-2017 : Changed several tiles from standard to value to resolve iOS rendering issue.
 *  11-29-2017 : Added resetMeter so that the device can be reset by my SmartApp based on a schedule.
 *  08-21-2018 : Added metadata fields for the new Samsung app.
 *
 */
metadata {
	definition (name: "My Aeon Metering Switch", namespace: "jscgs350", author: "SmartThings", ocfDeviceType: "oic.d.switch", mnmn: "SmartThings", vid:"generic-switch-power-energy") {
		capability "Energy Meter"
		capability "Actuator"
		capability "Switch"
        capability "Outlet"
		capability "Power Meter"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"
        capability "Configuration"
        capability "Health Check"

		attribute "kwhCosts", "string"
		attribute "history", "string"
        attribute "powerLow", "string"
        attribute "powerHigh", "string"

		command "resetWatts"
		command "resetEnergy"
        command "resetMeter"
	}

    preferences {
        input "disableOnOff", "boolean", title: "Disable On/Off switch?", defaultValue: false, displayDuringSetup: true
        input "debugOutput", "boolean", title: "Enable debug logging?", defaultValue: false, displayDuringSetup: true
        input "displayEvents", "boolean", title: "Display all events in the Recently tab and the device's event log?", defaultValue: false, required: false, displayDuringSetup: true
        input "kWhCost", "string", title: "Enter your cost per kWh (or just use the default, or use 0 to not calculate):", defaultValue: 0.16, required: false, displayDuringSetup: true            
        input "reportType", "number", title: "ReportType: Send watts/kWh data on a time interval (0), or on a change in wattage (1)? Enter a 0 or 1:", defaultValue: 1, range: "0..1", required: false, displayDuringSetup: true
        input "wattsChanged", "number", title: "For ReportType = 1, Don't send unless watts have changed by this many watts: (range 0 - 32,000W)", defaultValue: 50, range: "0..32000", required: false, displayDuringSetup: true
        input "wattsPercent", "number", title: "For ReportType = 1, Don't send unless watts have changed by this percent: (range 0 - 99%)", defaultValue: 10, range: "0..99", required: false, displayDuringSetup: true
        input "secondsWatts", "number", title: "For ReportType = 0, Send Watts data every how many seconds? (range 0 - 65,000 seconds)", defaultValue: 10, range: "0..65000", required: false, displayDuringSetup: true
        input "secondsKwh", "number", title: "For ReportType = 0, Send kWh data every how many seconds? (range 0 - 65,000 seconds)", defaultValue: 60, range: "0..65000", required: false, displayDuringSetup: true 
        input "decimalPositions", "number", title: "How many decimal positions do you want watts AND kWh to display? (range 0 - 3)", defaultValue: 3, range: "0..3", required: false, displayDuringSetup: true
    }

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4, decoration: "flat"){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC"
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			}
            tileAttribute ("power", key: "SECONDARY_CONTROL") {
           		attributeState "device.power", label:'Currently using ${currentValue} watts', icon: "https://raw.githubusercontent.com/constjs/jcdevhandlers/master/img/device-activity-tile@2x.png"
            }
		}
		valueTile("energy", "device.energy", width: 3, height: 1, decoration: "flat") {
			state "energy", label:'${currentValue} kWh'
		}
		valueTile("kwhCosts", "device.kwhCosts", width: 3, height: 1, inactiveLabel: false, decoration: "flat") {
			state("default", label: 'Cost ${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("history", "device.history", decoration:"flat",width: 6, height: 2) {
			state "history", label:'${currentValue}'
		}
		valueTile("resetWatts", "device.resetWatts", width: 3, height: 2, decoration: "flat") {
			state "default", label:'\nReset Watts Min/Max', action: "resetWatts", icon:"st.secondary.refresh-icon"
		}
		valueTile("resetEnergy", "device.resetEnergy", width: 3, height: 2, decoration: "flat") {
			state "default", label:'\nReset kWh/Costs', action: "resetEnergy", icon:"st.secondary.refresh-icon"
		}
		valueTile("refresh", "device.power", width: 6, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:'\nRefresh', action:"refresh.refresh", icon:"st.secondary.refresh-icon"
		}
		standardTile("power2", "device.power", width: 3, height: 1, decoration: "flat") {
			state "power", icon: "st.switches.switch.on", label:'${currentValue} W'
		}

		main "power2"
		details(["switch", "energy", "kwhCosts", "history", "resetWatts", "resetEnergy", "refresh"])
	}
}

def updated() {
	// Device-Watch simply pings if no device events received for 32min(checkInterval)
	sendEvent(name: "checkInterval", value: 2 * 15 * 60 + 2 * 60, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID])
    state.onOffDisabled = ("true" == disableOnOff)
    state.debug = ("true" == debugOutput)
    state.displayDisabled = ("true" == displayEvents)
    log.debug "updated(disableOnOff: ${disableOnOff}(${state.onOffDisabled}), debugOutput: ${debugOutput}(${state.debug}), reportType: ${reportType}, wattsChanged: ${wattsChanged}, wattsPercent: ${wattsPercent}, secondsWatts: ${secondsWatts}, secondsKwh: ${secondsKwh}, decimalPositions: ${decimalPositions})"
    response(configure())
}

def parse(String description) {
	if (state.debug) log.debug "Incoming to parse: ${description}"
	def result = null
	def cmd = zwave.parse(description, [0x20: 1, 0x32: 1])
	if (cmd) {
		result = createEvent(zwaveEvent(cmd))
	}
	return result
}

def zwaveEvent(physicalgraph.zwave.commands.meterv1.MeterReport cmd) {
    def dispValue
	def timeString = new Date().format("MM-dd-yy h:mm a", location.timeZone)
    if (cmd.scale == 0) {
        if (cmd.scaledMeterValue != state.energyValue) {
            state.energyValue = cmd.scaledMeterValue
            if (decimalPositions == 2) {
                dispValue = String.format("%3.2f",cmd.scaledMeterValue)
            } else if (decimalPositions == 1) {
                dispValue = String.format("%3.1f",cmd.scaledMeterValue)
            } else if (decimalPositions == 0) {
                dispValue = Math.round(cmd.scaledMeterValue)
            } else {
                dispValue = String.format("%3.3f",cmd.scaledMeterValue)
            }
            BigDecimal costDecimal = cmd.scaledMeterValue * (kWhCost as BigDecimal)
            def costDisplay = "\$"
            costDisplay += String.format("%3.2f",costDecimal)
            sendEvent(name: "kwhCosts", value: costDisplay as String, unit: "", displayed: false)
            if (state.displayDisabled) {
                sendEvent(name: "energy", value: dispValue, unit: "kWh", displayed: true)
            } else {
            	sendEvent(name: "energy", value: dispValue, unit: "kWh", displayed: false)
            }
        }
    } else if (cmd.scale==2) {
        if (cmd.scaledMeterValue < 2000) {
            if (cmd.scaledMeterValue != state.powerValue) {
                state.powerValue = cmd.scaledMeterValue
                if (decimalPositions == 2) {
                    dispValue = String.format("%3.2f",cmd.scaledMeterValue)
                } else if (decimalPositions == 1) {
                    dispValue = String.format("%3.1f",cmd.scaledMeterValue)
                } else if (decimalPositions == 0) {
                    dispValue = Math.round(cmd.scaledMeterValue)
                } else {
                    dispValue = String.format("%3.3f",cmd.scaledMeterValue)
                }
                if (cmd.scaledMeterValue < state.powerLowVal) {
                    def dispLowValue = dispValue+" watts on "+timeString
                    sendEvent(name: "powerLow", value: dispLowValue as String, unit: "", displayed: false)
                    state.powerLowVal = cmd.scaledMeterValue
                    def historyDisp = ""
					historyDisp = "Minimum/Maximum Readings as of ${timeString}\n-------------------------------------------------------------------------\nPower Low : ${device.currentState('powerLow')?.value}\nPower High : ${device.currentState('powerHigh')?.value}"
					sendEvent(name: "history", value: historyDisp, displayed: false)
                }
                if (cmd.scaledMeterValue > state.powerHighVal) {
                    def dispHighValue = dispValue+" watts on "+timeString
                    sendEvent(name: "powerHigh", value: dispHighValue as String, unit: "", displayed: false)
                    state.powerHighVal = cmd.scaledMeterValue
                    def historyDisp = ""
					historyDisp = "Minimum/Maximum Readings as of ${timeString}\n-------------------------------------------------------------------------\nPower Low : ${device.currentState('powerLow')?.value}\nPower High : ${device.currentState('powerHigh')?.value}"
					sendEvent(name: "history", value: historyDisp, displayed: false)
                }
                if (state.displayDisabled) {
                	sendEvent(name: "power", value: dispValue, unit: "watts", displayed: true)
                } else {
                    sendEvent(name: "power", value: dispValue, unit: "watts", displayed: false)
                }
            }
        }
    }
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd){
	if (state.debug) log.debug "${device.label}: $cmd"
	[name: "switch", value: cmd.value ? "on" : "off", type: "physical"]
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd){
	if (state.debug) log.debug "${device.label}: $cmd"
	[name: "switch", value: cmd.value ? "on" : "off", type: "digital"]
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	// Handles all Z-Wave commands we aren't interested in
	[:]
}

def on() {
    if (state.onOffDisabled) {
        if (state.debug) log.debug "On/Off disabled"
        delayBetween([
            zwave.basicV1.basicGet().format(),
            zwave.switchBinaryV1.switchBinaryGet().format()
        ], 5)
    }
    else {
        delayBetween([
            zwave.basicV1.basicSet(value: 0xFF).format(),
            zwave.switchBinaryV1.switchBinaryGet().format()
        ])
    }
}


def off() {
    if (state.onOffDisabled) {
        if (state.debug) log.debug "On/Off disabled"
        delayBetween([
            zwave.basicV1.basicGet().format(),
            zwave.switchBinaryV1.switchBinaryGet().format()
        ], 5)
    }
    else {
        delayBetween([
            zwave.basicV1.basicSet(value: 0x00).format(),
            zwave.switchBinaryV1.switchBinaryGet().format()
        ])
    }
}

def poll() {
    refresh()
}

// PING is used by Device-Watch in attempt to reach the Device
def ping() {
	refresh()
}

def refresh() {
    if (state.debug) log.debug "${device.name} refresh"
	delayBetween([
		zwave.switchBinaryV1.switchBinaryGet().format(),
		zwave.meterV2.meterGet(scale: 0).format(),
		zwave.meterV2.meterGet(scale: 2).format()
	])
}

def resetWatts() {
    if (state.debug) log.debug "${device.label} watts min/max reset"
    def historyDisp = ""
    state.powerHighVal = 0
    state.powerLowVal = 999999
	def timeString = new Date().format("MM-dd-yy h:mm a", location.timeZone)
    sendEvent(name: "powerLow", value: "Value reset on "+timeString, unit: "")    
    sendEvent(name: "powerHigh", value: "Value reset on "+timeString, unit: "")
    historyDisp = "Minimum/Maximum Readings as of ${timeString}\n-------------------------------------------------------------------------\nPower Low : ${device.currentState('powerLow')?.value}\nPower High : ${device.currentState('powerHigh')?.value}"
    sendEvent(name: "history", value: historyDisp, displayed: false)
    def cmd = delayBetween( [
        zwave.meterV2.meterGet(scale: 2).format()
    ])
    cmd
}

def resetEnergy() {
    if (state.debug) log.debug "${device.label} reset kWh/Cost values"
	def timeString = new Date().format("MM-dd-yy h:mm a", location.timeZone)
    sendEvent(name: "kwhCosts", value: "(reset)", unit: "", displayed: false)
    sendEvent(name: "energy", value: 0, unit: "kWh", displayed: false)
    state.energyValue = 0
    def cmd = delayBetween( [
        zwave.meterV2.meterReset().format(),
        zwave.meterV2.meterGet(scale: 0).format()
    ])
    cmd
}

def resetMeter() {
	log.debug "Resetting all energy meter values..."
    def resetDisp = ""
    def timeString = new Date().format("MM-dd-yy h:mm a", location.timeZone)
    resetDisp = "kWh value at time of last reset was ${device.currentState('energy')?.value}"
    sendEvent(name: "kWhLastReset", value: resetDisp, displayed: true)
    resetDisp = "Costs at time of last reset was ${device.currentState('kwhCosts')?.value}"
    sendEvent(name: "CostLastReset", value: resetDisp, displayed: true)
    def historyDisp = ""
	state.powerHigh = 0
	state.powerLow = 99999
    state.powerHighVal = 0
    state.powerLowVal = 999999
    state.energyValue = 0
    sendEvent(name: "powerLow", value: "Value reset on "+timeString, unit: "")    
    sendEvent(name: "powerHigh", value: "Value reset on "+timeString, unit: "")
    sendEvent(name: "kwhCosts", value: "(reset)", unit: "", displayed: false)
    sendEvent(name: "energy", value: 0, unit: "kWh", displayed: false)
	sendEvent(name: "resetMessage", value: "Device was reset on "+timeString, unit: "", displayed: true)
    historyDisp = "Minimum/Maximum Readings as of ${timeString}\n-------------------------------------------------------------------------\nPower Low : ${device.currentState('powerLow')?.value}\nPower High : ${device.currentState('powerHigh')?.value}"
    sendEvent(name: "history", value: historyDisp, displayed: false)
	def cmd = delayBetween( [
		zwave.meterV2.meterReset().format(),
		zwave.meterV2.meterGet(scale: 0).format(),
		zwave.meterV2.meterGet(scale: 2).format()
	])
	cmd
}

def configure() {
    log.debug "${device.name} configuring..."
	delayBetween([
        // Send data based on a time interval (0), or based on a change in wattage (1).  0 is default. 1 enables parameters 91 and 92.
        zwave.configurationV1.configurationSet(parameterNumber: 90, size: 1, scaledConfigurationValue: reportType).format(),
        // If parameter 90 is 1, don't send unless watts have changed by 50 <default>
        zwave.configurationV1.configurationSet(parameterNumber: 91, size: 2, scaledConfigurationValue: wattsChanged).format(),
        // If parameter 90 is 1, don't send unless watts have changed by 10% <default>
        zwave.configurationV1.configurationSet(parameterNumber: 92, size: 1, scaledConfigurationValue: wattsPercent).format(),
        // Defines the type of report sent for Reporting Group 1.  2->MultiSensor Report, 4->Meter Report for Watt, 8->Meter Report for kWh
        zwave.configurationV1.configurationSet(parameterNumber: 101, size: 4, scaledConfigurationValue: 4).format(),
        // If parameter 90 is 0, report every XX Seconds (for Watts) for Reporting Group 1.
        zwave.configurationV1.configurationSet(parameterNumber: 111, size: 4, scaledConfigurationValue: secondsWatts).format(),
        // Defines the type of report sent for Reporting Group 2.  2->MultiSensor Report, 4->Meter Report for Watt, 8->Meter Report for kWh
        zwave.configurationV1.configurationSet(parameterNumber: 102, size: 4, scaledConfigurationValue: 8).format(),
        // If parameter 90 is 0, report every XX seconds (for kWh) for Reporting Group 2.
        zwave.configurationV1.configurationSet(parameterNumber: 112, size: 4, scaledConfigurationValue: secondsKwh).format(),
        // Disable Reporting Group 3 parameters
        zwave.configurationV1.configurationSet(parameterNumber: 103, size: 4, scaledConfigurationValue: 0).format(),
        zwave.configurationV1.configurationSet(parameterNumber: 113, size: 4, scaledConfigurationValue: 0).format()
	])
}