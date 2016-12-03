/**
 *  webCoRE Piston
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
 

def String version() {	return "v0.0.002.20161202" }
/*
 *	12/02/2016 >>> v0.0.002.20161202 - ALPHA - Small progress, Add new piston now points to the piston editor UI
 *	10/28/2016 >>> v0.0.001.20161028 - ALPHA - Initial release
 */
 
/******************************************************************************/
/*** webCoRE DEFINITION														***/
/******************************************************************************/

 definition(
    name: "webCoRE Piston",
    namespace: "ady624",
    author: "Adrian Caramaliu",
    description: "CoRE Piston - Web Edition",
    category: "Convenience",
	parent: "ady624:webCoRE",
    iconUrl: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/app-CoRE.png",
    iconX2Url: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/app-CoRE@2x.png",
    iconX3Url: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/app-CoRE@2x.png"
 )


preferences {
	//common pages
	page(name: "pagePiston")
}

/******************************************************************************/
/*** 																		***/
/*** CONFIGURATION PAGES													***/
/*** 																		***/
/******************************************************************************/

/******************************************************************************/
/*** COMMON PAGES															***/
/******************************************************************************/
def pagePiston() {
	pagePistonMain()
}

def pagePistonMain() {
	//webCoRE Piston main page
	return dynamicPage(name: "pagePistonMain", title: "", install: true, uninstall: false) {
		def currentState = state.currentState
        
		section("Rebuild or remove piston") {
			href "pageRemove", title: "", description: "Remove this CoRE piston"
		}
		section(title:"Application Info") {
			label name: "name", title: "Name", required: true, state: (name ? "complete" : null), defaultValue: parent.generatePistonName()
			input "description", "string", title: "Description", required: false, state: (description ? "complete" : null), capitalization: "sentences"
			paragraph version(), title: "Version"
			paragraph mem(), title: "Memory Usage"
			href "pageVariables", title: "Local Variables"
		}
        
	}
}

def installed() {
	initialize()
	return true
}

def updated() {
	unsubscribe()
	initialize()
	return true
}

def initialize() {
	log.trace "GOT HERE"
	def device = parent.getDevice("owekf34r24r324");
    log.trace device
    subscribe(device, "switch", handler)

}

private getDevice(deviceId) {
	log.trace "LOOKING FOR DEVICE"
	def device = settings["dev:colorControl"][0]
    log.trace "GOT DEVICE $device"
    return device
}

def handler(evt) {
	log.trace "EVENT!"
}



/******************************************************************************/
/*** 																		***/
/*** COMMON PUBLISHED METHODS												***/
/*** 																		***/
/******************************************************************************/

def mem(showBytes = true) {
	def bytes = state.toString().length()
	return Math.round(100.00 * (bytes/ 100000.00)) + "%${showBytes ? " ($bytes bytes)" : ""}"
}
/******************************************************************************/
/***																		***/
/*** UTILITIES																***/
/***																		***/
/******************************************************************************/

/******************************************************************************/
/*** DEBUG FUNCTIONS														***/
/******************************************************************************/

private debug(message, shift = null, cmd = null, err = null) {
	def debugging = settings.debugging
	if (!debugging) {
		return
	}
	cmd = cmd ? cmd : "debug"
	if (!settings["log#$cmd"]) {
		return
	}
	//mode is
	// 0 - initialize level, level set to 1
	// 1 - start of routine, level up
	// -1 - end of routine, level down
	// anything else - nothing happens
	def maxLevel = 4
	def level = state.debugLevel ? state.debugLevel : 0
	def levelDelta = 0
	def prefix = "║"
	def pad = "░"
	switch (shift) {
		case 0:
			level = 0
			prefix = ""
			break
		case 1:
			level += 1
			prefix = "╚"
			pad = "═"
			break
		case -1:
			levelDelta = -(level > 0 ? 1 : 0)
			pad = "═"
			prefix = "╔"
		break
	}

	if (level > 0) {
		prefix = prefix.padLeft(level, "║").padRight(maxLevel, pad)
	}

	level += levelDelta
	state.debugLevel = level

	if (debugging) {
		prefix += " "
	} else {
		prefix = ""
	}

	if (cmd == "info") {
		log.info "$prefix$message", err
	} else if (cmd == "trace") {
		log.trace "$prefix$message", err
	} else if (cmd == "warn") {
		log.warn "$prefix$message", err
	} else if (cmd == "error") {
		log.error "$prefix$message", err
	} else {
		log.debug "$prefix$message", err
	}
}


/******************************************************************************/
/*** DATE & TIME FUNCTIONS													***/
/******************************************************************************/
private getPreviousQuarterHour(unixTime = now()) {
	return unixTime - unixTime.mod(900000)
}

//adjusts the time to local timezone
private adjustTime(time = null) {
	if (time instanceof String) {
		//get UTC time
		time = timeToday(time, location.timeZone).getTime()
	}
	if (time instanceof Date) {
		//get unix time
		time = time.getTime()
	}
	if (!time) {
		time = now()
	}
	if (time) {
		return new Date(time + location.timeZone.getOffset(time))
	}
	return null
}

private formatLocalTime(time, format = "EEE, MMM d yyyy @ h:mm a z") {
	if (time instanceof Long) {
		time = new Date(time)
	}
	if (time instanceof String) {
		//get UTC time
		time = timeToday(time, location.timeZone)
	}
	if (!(time instanceof Date)) {
		return null
	}
	def formatter = new java.text.SimpleDateFormat(format)
	formatter.setTimeZone(location.timeZone)
	return formatter.format(time)
}

private convertDateToUnixTime(date) {
	if (!date) {
		return null
	}
	if (!(date instanceof Date)) {
		date = new Date(date)
	}
	return date.time - location.timeZone.getOffset(date.time)
}

private convertTimeToUnixTime(time) {
	if (!time) {
		return null
	}
	return time - location.timeZone.getOffset(time)
}

private formatTime(time) {
	//we accept both a Date or a settings' Time
	return formatLocalTime(time, "h:mm a z")
}

private formatHour(h) {
	return (h == 0 ? "midnight" : (h < 12 ? "${h} AM" : (h == 12 ? "noon" : "${h-12} PM"))).toString()
}

private formatDayOfMonth(dom, dow) {
	if (dom) {
		if (dom.contains("week")) {
			//relative day of week
			return dom.replace("week", dow)
		} else {
			//dealing with a certain day of the month
			if (dom.contains("last")) {
				//relative day value
				return dom
			} else {
				//absolute day value
				def day = dom.replace("on the ", "").replace("st", "").replace("nd", "").replace("rd", "").replace("th", "").toInteger()
				return "on the ${formatOrdinalNumber(day)}"
			}
		}
	}
	return "[ERROR]"
}

//return the number of occurrences of same day of week up until the date or from the end of the month if backwards, i.e. last Sunday is -1, second-last Sunday is -2
private getWeekOfMonth(date = null, backwards = false) {
	if (!date) {
		date = adjustTime(now())
	}
	def day = date.date
	if (backwards) {
		def month = date.month
		def year = date.year
		def lastDayOfMonth = (new Date(year, month + 1, 0)).date
		return -(1 + Math.floor((lastDayOfMonth - day) / 7))
	} else {
		return 1 + Math.floor((day - 1) / 7) //1 based
	}
}

//returns the number of day in a month, 1 based, or -1 based if backwards (last day of the month)
private getDayOfMonth(date = null, backwards = false) {
	if (!date) {
		date = adjustTime(now())
	}
	def day = date.date
	if (backwards) {
		def month = date.month
		def year = date.year
		def lastDayOfMonth = (new Date(year, month + 1, 0)).date
		return day - lastDayOfMonth - 1
	} else {
		return day
	}
}

//for a given month, returns the Nth instance of a certain day of the week within that month. week ranges from 1 through 5 and -1 through -5
private getDayInWeekOfMonth(date, week, dow) {
	if (!date || (dow == null)) {
		return null
	}
	def lastDayOfMonth = (new Date(date.year, date.month + 1, 0)).date
	if (week > 0) {
		//going forward
		def firstDayOfMonthDOW = (new Date(date.year, date.month, 1)).day
		//find the first matching day
		def firstMatch = 1 + dow - firstDayOfMonthDOW + (dow < firstDayOfMonthDOW ? 7 : 0)
		def result = firstMatch + 7 * (week - 1)
		return result <= lastDayOfMonth ? result : null
	}
	if (week < 0) {
		//going backwards
		def lastDayOfMonthDOW = (new Date(date.year, date.month + 1, 0)).day
		//find the first matching day
		def firstMatch = lastDayOfMonth + dow - lastDayOfMonthDOW - (dow > lastDayOfMonthDOW ? 7 : 0)
		def result = firstMatch + 7 * (week + 1)
		return result >= 1 ? result : null
	}
	return null
}

private getDayOfWeekName(date = null) {
	if (!date) {
		date = adjustTime(now())
	}
	switch (date.day) {
		case 0: return "Sunday"
		case 1: return "Monday"
		case 2: return "Tuesday"
		case 3: return "Wednesday"
		case 4: return "Thursday"
		case 5: return "Friday"
		case 6: return "Saturday"
	}
	return null
}

private getDayOfWeekNumber(date = null) {
	if (!date) {
		date = adjustTime(now())
	}
	if (date instanceof Date) {
		return date.day
	}
	switch (date) {
		case "Sunday": return 0
		case "Monday": return 1
		case "Tuesday": return 2
		case "Wednesday": return 3
		case "Thursday": return 4
		case "Friday": return 5
		case "Saturday": return 6
	}
	return null
}

private getMonthName(date = null) {
	if (!date) {
		date = adjustTime(now())
	}
	def month = date.month + 1
	switch (month) {
		case  1: return "January"
		case  2: return "February"
		case  3: return "March"
		case  4: return "April"
		case  5: return "May"
		case  6: return "June"
		case  7: return "July"
		case  8: return "August"
		case  9: return "September"
		case 10: return "October"
		case 11: return "November"
		case 12: return "December"
	}
	return null
}

private getMonthNumber(date = null) {
	if (!date) {
		date = adjustTime(now())
	}
	if (date instanceof Date) {
		return date.month + 1
	}
	switch (date) {
		case "January": return 1
		case "February": return 2
		case "March": return 3
		case "April": return 4
		case "May": return 5
		case "June": return 6
		case "July": return 7
		case "August": return 8
		case "September": return 9
		case "October": return 10
		case "November": return 11
		case "December": return 12
	}
	return null
}
private getSunrise() {
	if (!(state.sunrise instanceof Date)) {
		def sunTimes = getSunriseAndSunset()
		state.sunrise = adjustTime(sunTimes.sunrise)
		state.sunset = adjustTime(sunTimes.sunset)
	}
	return state.sunrise
}

private getSunset() {
	if (!(state.sunset instanceof Date)) {
		def sunTimes = getSunriseAndSunset()
		state.sunrise = adjustTime(sunTimes.sunrise)
		state.sunset = adjustTime(sunTimes.sunset)
	}
	return state.sunset
}

private addOffsetToMinutes(minutes, offset) {
	if (minutes == null) {
		return null
	}
	if (offset == null) {
		return minutes
	}
	minutes = minutes + offset
	while (minutes >= 1440) {
		minutes -= 1440
	}
	while (minutes < 0) {
		minutes += 1440
	}
	return minutes
}

private timeComparisonOptionValues(trigger, supportVariables = true) {
		return ["custom time", "midnight", "sunrise", "noon", "sunset"] + (supportVariables ? ["time of variable", "date and time of variable"] : []) + (trigger ? ["every minute", "every number of minutes", "every hour", "every number of hours"] : [])
}

private groupOptions() {
	return ["AND", "OR", "XOR", "THEN IF", "ELSE IF", "FOLLOWED BY"]
}


private threeAxisOrientations() {
	return ["rear side up", "down side up", "left side up", "front side up", "up side up", "right side up"]
}

private threeAxisOrientationCoordinates() {
	return ["rear side up", "down side up", "left side up", "front side up", "up side up", "right side up"]
}

private getThreeAxisDistance(coord1, coord2) {
	if (coord1 && coord2){
		def dX = coord1.x - coord2.x
		def dY = coord1.y - coord2.y
		def dZ = coord1.z - coord2.z
		def s = Math.pow(dX,2) + Math.pow(dY,2) + Math.pow(dZ,2)
		def dist = Math.pow(s,0.5)
		return dist.toInteger()
	} else return null
}

private getThreeAxisOrientation(value, getIndex = false) {
	if (value instanceof Map) {    
		if ((value.x != null) && (value.y != null) && (value.z != null)) {
			def orientations = threeAxisOrientations()
			def x = Math.abs(value.x)
			def y = Math.abs(value.y)
			def z = Math.abs(value.z)
			def side = (x > y ? (x > z ? 0 : 2) : (y > z ? 1 : 2))
			side = side + (((side == 0) && (value.x < 0)) || ((side == 1) && (value.y < 0)) || ((side == 2) && (value.z < 0)) ? 3 : 0)
			def result = getIndex ? side : orientations[side]
			return result
		}
	}
	return value
}
private timeOptions(trigger = false) {
	def result = ["1 minute"]
	for (def i =2; i <= (trigger ? 360 : 60); i++) {
		result.push("$i minutes")
	}
	return result
}

private timeRepeatOptions() {
	return ["every day", "every number of days", "every week", "every number of weeks", "every month", "every number of months", "every year", "every number of years"]
}

private timeMinuteOfHourOptions() {
	def result = []
	for (def i =0; i <= 59; i++) {
		result.push("$i".padLeft(2, "0"))
	}
	return result
}

private timeHourOfDayOptions() {
	def result = []
	for (def i =0; i <= 23; i++) {
		result.push(formatHour(i))
	}
	return result
}

private timeDayOfMonthOptions() {
	def result = []
	for (def i =1; i <= 31; i++) {
		result.push("on the ${formatOrdinalNumber(i)}")
	}
	return result + ["on the last day", "on the second-last day", "on the third-last day", "on the first week", "on the second week", "on the third week", "on the fourth week", "on the fifth week", "on the last week", "on the second-last week", "on the third-last week"]
}

private timeDayOfMonthOptions2() {
	def result = []
	for (def i =1; i <= 31; i++) {
		result.push("the ${formatOrdinalNumber(i)}")
	}
	return result + ["the last day of the month", "the second-last day of the month", "the third-last day of the month"]
}

private timeDayOfWeekOptions() {
	return ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
}

private timeWeekOfMonthOptions() {
	return ["the first week", "the second week", "the third week", "the fourth week", "the fifth week", "the last week", "the second-last week"]
}

private timeMonthOfYearOptions() {
	return ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]
}

private timeYearOptions() {
	def result = ["even years", "odd years", "leap years"]
	def year = 1900 + (new Date()).getYear()
	for (def i = year; i <= 2099; i++) {
		result.push("$i")
	}
	for (def i = 2016; i < year; i++) {
		result.push("$i")
	}
	return result
}

private timeToMinutes(time) {
	if (!(time instanceof String)) return 0
	def value = time.replace(" minutes", "").replace(" minute", "")
	if (value.isInteger()) {
		return value.toInteger()
	}
	debug "ERROR: Time '$time' could not be parsed", null, "error"
	return 0
}


























/******************************************************************************/
/*** DATABASE																***/
/******************************************************************************/

private capabilities() {
	return [
		[ name: "accelerationSensor",				display: "Acceleration Sensor",				attribute: "acceleration",				multiple: true,			devices: "acceleration sensors",	],
		[ name: "alarm",							display: "Alarm",							attribute: "alarm",						commands: ["off", "strobe", "siren", "both"],										multiple: true,			devices: "sirens",			],
		[ name: "askAlexaMacro",					display: "Ask Alexa Macro",					attribute: "askAlexaMacro",				commands: [],																		multiple: true,			virtualDevice: location,	virtualDeviceName: "Ask Alexa Macro"	],
		[ name: "audioNotification",				display: "Audio Notification",				commands: ["playText", "playSoundAndTrack", "playText", "playTextAndResume", "playTextAndRestore", "playTrack", "playTrackAndResume", "playTrackAndRestore", "playTrackAtVolume"],	multiple: true,			devices: "audio notification devices", ],
		[ name: "doorControl",						display: "Automatic Door",					attribute: "door",						commands: ["open", "close"],														multiple: true,			devices: "doors",			],
		[ name: "garageDoorControl",				display: "Automatic Garage Door",			attribute: "door",						commands: ["open", "close"],														multiple: true,			devices: "garage doors",		],
		[ name: "battery",							display: "Battery",							attribute: "battery",					multiple: true,			devices: "battery powered devices",	],
		[ name: "beacon",							display: "Beacon",							attribute: "presence",					multiple: true,			devices: "beacons",	],
		[ name: "switch",							display: "Bulb",							attribute: "switch",					commands: ["on", "off"],															multiple: true,			devices: "lights", 			],
		[ name: "button",							display: "Button",							attribute: "button",					multiple: true,			devices: "buttons",			count: "numberOfButtons,numButtons", data: "buttonNumber", momentary: true],
		[ name: "imageCapture",						display: "Camera",							attribute: "image",						commands: ["take"],																	multiple: true,			devices: "cameras",			],
		[ name: "carbonDioxideMeasurement",			display: "Carbon Dioxide Measurement",		attribute: "carbonDioxide",				multiple: true,			devices: "carbon dioxide sensors",	],
		[ name: "carbonMonoxideDetector",			display: "Carbon Monoxide Detector",		attribute: "carbonMonoxide",			multiple: true,			devices: "carbon monoxide detectors",	],
		[ name: "colorControl",						display: "Color Control",					attribute: "color",						commands: ["setColor", "setHue", "setSaturation"],									multiple: true,			devices: "RGB/W lights"		],
		[ name: "colorTemperature",					display: "Color Temperature",				attribute: "colorTemperature",			commands: ["setColorTemperature"],													multiple: true,			devices: "RGB/W lights",	],
		[ name: "configure",						display: "Configure",						commands: ["configure"],															multiple: true,			devices: "configurable devices",	],
		[ name: "consumable",						display: "Consumable",						attribute: "consumable",				commands: ["setConsumableStatus"],													multiple: true,			devices: "consumables",	],
		[ name: "contactSensor",					display: "Contact Sensor",					attribute: "contact",					multiple: true,			devices: "contact sensors",	],
		[ name: "piston",							display: "CoRE Piston",						attribute: "piston",					commands: ["executePiston"],														multiple: true,			virtualDevice: location,	virtualDeviceName: "Piston"	],
		[ name: "dateAndTime",						display: "Date & Time",						attribute: "time",						commands: null, /* wish we could control time */									multiple: true,			, virtualDevice: [id: "time", name: "time"],		virtualDeviceName: "Date & Time"	],
		[ name: "switchLevel",						display: "Dimmable Light",					attribute: "level",						commands: ["setLevel"],																multiple: true,			devices: "dimmable lights",	],
		[ name: "switchLevel",						display: "Dimmer",							attribute: "level",						commands: ["setLevel"],																multiple: true,			devices: "dimmers",			],
		[ name: "energyMeter",						display: "Energy Meter",					attribute: "energy",					multiple: true,			devices: "energy meters"],
		[ name: "ifttt",							display: "IFTTT",							attribute: "ifttt",						commands: [],																		multiple: false,		virtualDevice: location,	virtualDeviceName: "IFTTT"	],
		[ name: "illuminanceMeasurement",			display: "Illuminance Measurement",			attribute: "illuminance",				multiple: true,			devices: "illuminance sensors",	],
		[ name: "imageCapture",						display: "Image Capture",					attribute: "image",						commands: ["take"],																	multiple: true,			devices: "cameras"],
		[ name: "indicator",						display: "Indicator",						attribute: "indicatorStatus",			multiple: true,			devices: "indicator devices"],
		[ name: "waterSensor",						display: "Leak Sensor",						attribute: "water",						multiple: true,			devices: "leak sensors",	],
		[ name: "switch",							display: "Light bulb",						attribute: "switch",					commands: ["on", "off"],															multiple: true,			devices: "lights", 			],
		[ name: "locationMode",						display: "Location Mode",					attribute: "mode",						commands: ["setMode"],																multiple: false,		devices: "location", virtualDevice: location	],
		[ name: "lock",								display: "Lock",							attribute: "lock",						commands: ["lock", "unlock"],						count: "numberOfCodes,numCodes", data: "usedCode", subDisplay: "By user code", multiple: true,			devices: "electronic locks", ],
		[ name: "mediaController",					display: "Media Controller",				attribute: "currentActivity",			commands: ["startActivity", "getAllActivities", "getCurrentActivity"],				multiple: true,			devices: "media controllers"],
		[ name: "locationMode",						display: "Mode",							attribute: "mode",						commands: ["setMode"],																multiple: false,		devices: "location", virtualDevice: location	],
		[ name: "momentary",						display: "Momentary",						commands: ["push"],																	multiple: true,			devices: "momentary switches"],
		[ name: "motionSensor",						display: "Motion Sensor",					attribute: "motion",					multiple: true,			devices: "motion sensors",	],
		[ name: "musicPlayer",						display: "Music Player",					attribute: "status",					commands: ["play", "pause", "stop", "nextTrack", "playTrack", "setLevel", "playText", "mute", "previousTrack", "unmute", "setTrack", "resumeTrack", "restoreTrack"],	multiple: true,			devices: "music players", ],
		[ name: "notification",						display: "Notification",					commands: ["deviceNotification"],													multiple: true,			devices: "notification devices",	],
		[ name: "pHMeasurement",					display: "pH Measurement",					attribute: "pH",						multiple: true,			devices: "pH sensors",	],
		[ name: "occupancy",						display: "Occupancy",						attribute: "occupancy",					multiple: true,			devices: "occupancy detectors",	],
		[ name: "switch",							display: "Outlet",							attribute: "switch",					commands: ["on", "off"],															multiple: true,			devices: "outlets",			],
		[ name: "piston",							display: "Piston",							attribute: "piston",					commands: ["executePiston"],														multiple: true,			virtualDevice: location,	virtualDeviceName: "Piston"	],
		[ name: "polling",							display: "Polling",							commands: ["poll"],																	multiple: true,			devices: "pollable devices",	],
		[ name: "powerMeter",						display: "Power Meter",						attribute: "power",						multiple: true,			devices: "power meters",	],
		[ name: "power",							display: "Power",							attribute: "powerSource",				multiple: true,			devices: "powered devices",	],
		[ name: "presenceSensor",					display: "Presence Sensor",					attribute: "presence",					multiple: true,			devices: "presence sensors",	],
		[ name: "refresh",							display: "Refresh",							commands: ["refresh"],																multiple: true,			devices: "refreshable devices",	],
		[ name: "relativeHumidityMeasurement",		display: "Relative Humidity Measurement",	attribute: "humidity",					multiple: true,			devices: "humidity sensors",	],
		[ name: "relaySwitch",						display: "Relay Switch",					attribute: "switch",					commands: ["on", "off"],															multiple: true,			devices: "relays",			],
		[ name: "routine",							display: "Routine",							attribute: "routineExecuted",			commands: ["executeRoutine"],														multiple: true,			virtualDevice: location,	virtualDeviceName: "Routine"	],
		[ name: "sensor",							display: "Sensor",							attribute: "sensor",					multiple: true,			devices: "sensors",	],
		[ name: "shockSensor",						display: "Shock Sensor",					attribute: "shock",						multiple: true,			devices: "shock sensors",	],
		[ name: "signalStrength",					display: "Signal Strength",					attribute: "lqi",						multiple: true,			devices: "wireless devices",	],
		[ name: "alarm",							display: "Siren",							attribute: "alarm",						commands: ["off", "strobe", "siren", "both"],										multiple: true,			devices: "sirens",			],
		[ name: "sleepSensor",						display: "Sleep Sensor",					attribute: "sleeping",					multiple: true,			devices: "sleep sensors",	],
		[ name: "smartHomeMonitor",					display: "Smart Home Monitor",				attribute: "alarmSystemStatus",			commands: ["setAlarmSystemStatus"],																		multiple: true,			, virtualDevice: location,	virtualDeviceName: "Smart Home Monitor"	],
		[ name: "smokeDetector",					display: "Smoke Detector",					attribute: "smoke",						multiple: true,			devices: "smoke detectors",	],
		[ name: "soundSensor",						display: "Sound Sensor",					attribute: "sound",						multiple: true,			devices: "sound sensors",	],
		[ name: "speechSynthesis",					display: "Speech Synthesis",				commands: ["speak"],																multiple: true,			devices: "speech synthesizers", ],
		[ name: "stepSensor",						display: "Step Sensor",						attribute: "steps",						multiple: true,			devices: "step sensors",	],
		[ name: "switch",							display: "Switch",							attribute: "switch",					commands: ["on", "off"],															multiple: true,			devices: "switches",			],
		[ name: "switchLevel",						display: "Switch Level",					attribute: "level",						commands: ["setLevel"],																multiple: true,			devices: "dimmers" ],
		[ name: "soundPressureLevel",				display: "Sound Pressure Level",			attribute: "soundPressureLevel",		multiple: true,			devices: "sound pressure sensors",	],
		[ name: "consumable",						display: "Stock Management",				attribute: "consumable",				multiple: true,			devices: "consumables",	],
		[ name: "tamperAlert",						display: "Tamper Alert",					attribute: "tamper",					multiple: true,			devices: "tamper sensors",	],
		[ name: "temperatureMeasurement",			display: "Temperature Measurement",			attribute: "temperature",				multiple: true,			devices: "temperature sensors",	],
		[ name: "thermostat",						display: "Thermostat",						attribute: "temperature",				commands: ["setHeatingSetpoint", "setCoolingSetpoint", "off", "heat", "emergencyHeat", "cool", "setThermostatMode", "fanOn", "fanAuto", "fanCirculate", "setThermostatFanMode", "auto"],	multiple: true,		devices: "thermostats",	showAttribute: true],
		[ name: "thermostatCoolingSetpoint",		display: "Thermostat Cooling Setpoint",		attribute: "coolingSetpoint",			commands: ["setCoolingSetpoint"],													multiple: true,			],
		[ name: "thermostatFanMode",				display: "Thermostat Fan Mode",				attribute: "thermostatFanMode",			commands: ["fanOn", "fanAuto", "fanCirculate", "setThermostatFanMode"],				multiple: true,			devices: "fans",	],
		[ name: "thermostatHeatingSetpoint",		display: "Thermostat Heating Setpoint",		attribute: "heatingSetpoint",			commands: ["setHeatingSetpoint"],													multiple: true,			],
		[ name: "thermostatMode",					display: "Thermostat Mode",					attribute: "thermostatMode",			commands: ["off", "heat", "emergencyHeat", "cool", "auto", "setThermostatMode"],	multiple: true,			],
		[ name: "thermostatOperatingState",			display: "Thermostat Operating State",		attribute: "thermostatOperatingState",	multiple: true,			],
		[ name: "thermostatSetpoint",				display: "Thermostat Setpoint",				attribute: "thermostatSetpoint",		multiple: true,			],
		[ name: "threeAxis",						display: "Three Axis Sensor",				attribute: "orientation",				multiple: true,			devices: "three axis sensors",	],
		[ name: "dateAndTime",						display: "Time",							attribute: "time",						commands: null, /* wish we could control time */									multiple: true,			, virtualDevice: [id: "time", name: "time"],		virtualDeviceName: "Date & Time"	],
		[ name: "timedSession",						display: "Timed Session",					attribute: "sessionStatus",				commands: ["setTimeRemaining", "start", "stop", "pause", "cancel"],					multiple: true,			devices: "timed sessions"],
		[ name: "tone",								display: "Tone Generator",					commands: ["beep"],																	multiple: true,			devices: "tone generators",	],
		[ name: "touchSensor",						display: "Touch Sensor",					attribute: "touch",						multiple: true,			],
		[ name: "valve",							display: "Valve",							attribute: "contact",					commands: ["open", "close"],														multiple: true,			devices: "valves",			],
		[ name: "variable",							display: "Variable",						attribute: "variable",					commands: ["setVariable"],															multiple: true,			virtualDevice: location,	virtualDeviceName: "Variable"	],
		[ name: "voltageMeasurement",				display: "Voltage Measurement",				attribute: "voltage",					multiple: true,			devices: "volt meters",	],
		[ name: "waterSensor",						display: "Water Sensor",					attribute: "water",						multiple: true,			devices: "leak sensors",	],
		[ name: "windowShade",						display: "Window Shade",					attribute: "windowShade",				commands: ["open", "close", "presetPosition"],										multiple: true,			devices: "window shades",	],
	]
}

private commands() {
	def tempUnit = "°" + location.temperatureScale
	return [
		[ name: "locationMode.setMode",						category: "Location",					group: "Control location mode, Smart Home Monitor, routines, pistons, variables, and more...",		display: "Set location mode",			],
		[ name: "smartHomeMonitor.setAlarmSystemStatus",	category: "Location",					group: "Control location mode, Smart Home Monitor, routines, pistons, variables, and more...",		display: "Set Smart Home Monitor status",],
		[ name: "on",										category: "Convenience",				group: "Control [devices]",			display: "Turn on", 						attribute: "switch",	value: "on",	],
		[ name: "on1",										display: "Turn on #1", 						attribute: "switch1",	value: "on",	],
		[ name: "on2",										display: "Turn on #2", 						attribute: "switch2",	value: "on",	],
		[ name: "on3",										display: "Turn on #3", 						attribute: "switch3",	value: "on",	],
		[ name: "on4",										display: "Turn on #4", 						attribute: "switch4",	value: "on",	],
		[ name: "on5",										display: "Turn on #5", 						attribute: "switch5",	value: "on",	],
		[ name: "on6",										display: "Turn on #6", 						attribute: "switch6",	value: "on",	],
		[ name: "on7",										display: "Turn on #7", 						attribute: "switch7",	value: "on",	],
		[ name: "on8",										display: "Turn on #8", 						attribute: "switch8",	value: "on",	],
		[ name: "off",										category: "Convenience",				group: "Control [devices]",			display: "Turn off",						attribute: "switch",	value: "off",	],
		[ name: "off1",										display: "Turn off #1",						attribute: "switch1",	value: "off",	],
		[ name: "off2",										display: "Turn off #2",						attribute: "switch2",	value: "off",	],
		[ name: "off3",										display: "Turn off #3",						attribute: "switch3",	value: "off",	],
		[ name: "off4",										display: "Turn off #4",						attribute: "switch4",	value: "off",	],
		[ name: "off5",										display: "Turn off #5",						attribute: "switch5",	value: "off",	],
		[ name: "off6",										display: "Turn off #6",						attribute: "switch6",	value: "off",	],
		[ name: "off7",										display: "Turn off #7",						attribute: "switch7",	value: "off",	],
		[ name: "off8",										display: "Turn off #8",						attribute: "switch8",	value: "off",	],
		[ name: "toggle",									display: "Toggle",		],
		[ name: "toggle1",									display: "Toggle #1",	],
		[ name: "toggle2",									display: "Toggle #1",	],
		[ name: "toggle3",									display: "Toggle #1",	],
		[ name: "toggle4",									display: "Toggle #1",	],
		[ name: "toggle5",									display: "Toggle #1",	],
		[ name: "toggle6",									display: "Toggle #1",	],
		[ name: "toggle7",									display: "Toggle #1",	],
		[ name: "toggle8",									display: "Toggle #1",	],
		[ name: "setColor",									category: "Convenience",				group: "Control [devices]",			display: "Set color",					parameters: ["?*Color:color","?*RGB:text","Hue:hue","Saturation:saturation","Lightness:level"], 	attribute: "color",		value: "*|color",	],
		[ name: "setLevel",									category: "Convenience",				group: "Control [devices]",			display: "Set level",					parameters: ["Level:level"], description: "Set level to {0}%",		attribute: "level",		value: "*|number",	],
		[ name: "setHue",									category: "Convenience",				group: "Control [devices]",			display: "Set hue",						parameters: ["Hue:hue"], description: "Set hue to {0}°",	attribute: "hue",		value: "*|number",	],
		[ name: "setSaturation",							category: "Convenience",				group: "Control [devices]",			display: "Set saturation",				parameters: ["Saturation:saturation"], description: "Set saturation to {0}%",	attribute: "saturation",		value: "*|number",	],
		[ name: "setColorTemperature",						category: "Convenience",				group: "Control [devices]",			display: "Set color temperature",		parameters: ["Color Temperature:colorTemperature"], description: "Set color temperature to {0}°K",	attribute: "colorTemperature",		value: "*|number",	],
		[ name: "open",										category: "Convenience",				group: "Control [devices]",			display: "Open",						attribute: "door",		value: "open",	],
		[ name: "close",									category: "Convenience",				group: "Control [devices]",			display: "Close",						attribute: "door",		value: "close",	],
		[ name: "windowShade.open",							category: "Convenience",				group: "Control [devices]",			display: "Open fully",					],
		[ name: "windowShade.close",						category: "Convenience",				group: "Control [devices]",			display: "Close fully",					],
		[ name: "windowShade.presetPosition",				category: "Convenience",				group: "Control [devices]",			display: "Move to preset position",		],
		[ name: "lock",										category: "Safety and Security",		group: "Control [devices]",			display: "Lock",						attribute: "lock",		value: "locked",	],
		[ name: "unlock",									category: "Safety and Security",		group: "Control [devices]",			display: "Unlock",						attribute: "lock",		value: "unlocked",	],
		[ name: "take",										category: "Safety and Security",		group: "Control [devices]",			display: "Take a picture",				],
		[ name: "alarm.off",								category: "Safety and Security",		group: "Control [devices]",			display: "Stop",						attribute: "alarm",		value: "off",	],
		[ name: "alarm.strobe",								category: "Safety and Security",		group: "Control [devices]",			display: "Strobe",						attribute: "alarm",		value: "strobe",	],
		[ name: "alarm.siren",								category: "Safety and Security",		group: "Control [devices]",			display: "Siren",						attribute: "alarm",		value: "siren",	],
		[ name: "alarm.both",								category: "Safety and Security",		group: "Control [devices]",			display: "Strobe and Siren",			attribute: "alarm",		value: "both",	],
		[ name: "thermostat.off",							category: "Comfort",					group: "Control [devices]",			display: "Set to Off",					attribute: "thermostatMode",	value: "off",	],
		[ name: "thermostat.heat",							category: "Comfort",					group: "Control [devices]",			display: "Set to Heat",					attribute: "thermostatMode",	value: "heat",	],
		[ name: "thermostat.cool",							category: "Comfort",					group: "Control [devices]",			display: "Set to Cool",					attribute: "thermostatMode",	value: "cool",	],
		[ name: "thermostat.auto",							category: "Comfort",					group: "Control [devices]",			display: "Set to Auto",					attribute: "thermostatMode",	value: "auto",	],
		[ name: "thermostat.emergencyHeat",					category: "Comfort",					group: "Control [devices]",			display: "Set to Emergency Heat",		attribute: "thermostatMode",	value: "emergencyHeat",	],
		[ name: "thermostat.quickSetHeat",					category: "Comfort",					group: "Control [devices]",			display: "Quick set heating point",		parameters: ["Desired temperature:thermostatSetpoint"], description: "Set quick heating point at {0}$tempUnit",	],
		[ name: "thermostat.quickSetCool",					category: "Comfort",					group: "Control [devices]",			display: "Quick set cooling point",		parameters: ["Desired temperature:thermostatSetpoint"], description: "Set quick cooling point at {0}$tempUnit",	],
		[ name: "thermostat.setHeatingSetpoint",			category: "Comfort",					group: "Control [devices]",			display: "Set heating point",			parameters: ["Desired temperature:thermostatSetpoint"], description: "Set heating point at {0}$tempUnit",	attribute: "thermostatHeatingSetpoint",	value: "*|decimal",	],
		[ name: "thermostat.setCoolingSetpoint",			category: "Comfort",					group: "Control [devices]",			display: "Set cooling point",			parameters: ["Desired temperature:thermostatSetpoint"], description: "Set cooling point at {0}$tempUnit",	attribute: "thermostatCoolingSetpoint",	value: "*|decimal",	],
		[ name: "thermostat.setThermostatMode",				category: "Comfort",					group: "Control [devices]",			display: "Set thermostat mode",			parameters: ["Mode:thermostatMode"], description: "Set thermostat mode to {0}",	attribute: "thermostatMode",	value: "*|string",	],
		[ name: "fanOn",									category: "Comfort",					group: "Control [devices]",			display: "Set fan to On",				],
		[ name: "fanCirculate",								category: "Comfort",					group: "Control [devices]",			display: "Set fan to Circulate",		],
		[ name: "fanAuto",									category: "Comfort",					group: "Control [devices]",			display: "Set fan to Auto",				],
		[ name: "setThermostatFanMode",						category: "Comfort",					group: "Control [devices]",			display: "Set fan mode",				parameters: ["Fan mode:thermostatFanMode"], description: "Set fan mode to {0}",	],
		[ name: "play",										category: "Entertainment",				group: "Control [devices]",			display: "Play",	],
		[ name: "pause",									category: "Entertainment",				group: "Control [devices]",			display: "Pause",	],
		[ name: "stop",										category: "Entertainment",				group: "Control [devices]",			display: "Stop",	],
		[ name: "nextTrack",								category: "Entertainment",				group: "Control [devices]",			display: "Next track",					],
		[ name: "previousTrack",							category: "Entertainment",				group: "Control [devices]",			display: "Previous track",				],
		[ name: "mute",										category: "Entertainment",				group: "Control [devices]",			display: "Mute",	],
		[ name: "unmute",									category: "Entertainment",				group: "Control [devices]",			display: "Unmute",	],
		[ name: "musicPlayer.setLevel",						category: "Entertainment",				group: "Control [devices]",			display: "Set volume",					parameters: ["Level:level"], description: "Set volume to {0}%",	],
		[ name: "playText",									category: "Entertainment",				group: "Control [devices]",			display: "Speak text",					parameters: ["Text:string", "?Volume:level"], description: "Speak text \"{0}\" at volume {1}", ],
		[ name: "playTextAndRestore",	display: "Speak text and restore",		parameters: ["Text:string","?Volume:level"], 	description: "Speak text \"{0}\" at volume {1} and restore", ],
		[ name: "playTextAndResume",	display: "Speak text and resume",		parameters: ["Text:string","?Volume:level"], 	description: "Speak text \"{0}\" at volume {1} and resume", ],
		[ name: "playTrack",								category: "Entertainment",				group: "Control [devices]",			display: "Play track",					parameters: ["Track URI:string","?Volume:level"],				description: "Play track \"{0}\" at volume {1}",	],
		[ name: "playTrackAtVolume",	display: "Play track at volume",		parameters: ["Track URI:string","Volume:level"],description: "Play track \"{0}\" at volume {1}",	],
		[ name: "playTrackAndRestore",	display: "Play track and restore",		parameters: ["Track URI:string","?Volume:level"], 	description: "Play track \"{0}\" at volume {1} and restore", ],
		[ name: "playTrackAndResume",	display: "Play track and resume",		parameters: ["Track URI:string","?Volume:level"], 	description: "Play track \"{0}\" at volume {1} and resume", ],
		[ name: "setTrack",									category: "Entertainment",				group: "Control [devices]",			parameters: ["Track URI:string"],	display: "Set track to '{0}'",					],
		[ name: "setLocalLevel",display: "Set local level",				parameters: ["Level:level"],	description: "Set local level to {0}", ],
		[ name: "resumeTrack",								category: "Entertainment",				group: "Control [devices]",			display: "Resume track",				],
		[ name: "restoreTrack",								category: "Entertainment",				group: "Control [devices]",			display: "Restore track",				],
		[ name: "speak",									category: "Entertainment",				group: "Control [devices]",			display: "Speak",						parameters: ["Message:string"], description: "Speak \"{0}\"", ],
		[ name: "startActivity",							category: "Entertainment",				group: "Control [devices]",			display: "Start activity",				parameters: ["Activity:string"], description: "Start activity\"{0}\"",	],
		[ name: "getCurrentActivity",						category: "Entertainment",				group: "Control [devices]",			display: "Get current activity",		],
		[ name: "getAllActivities",							category: "Entertainment",				group: "Control [devices]",			display: "Get all activities",			],
		[ name: "push",										category: "Other",						group: "Control [devices]",			display: "Push",	],
		[ name: "beep",										category: "Other",						group: "Control [devices]",			display: "Beep",	],
		[ name: "timedSession.setTimeRemaining",			category: "Other",						group: "Control [devices]",			display: "Set remaining time",			parameters: ["Remaining time [s]:number"], description: "Set remaining time to {0}s",	],
		[ name: "timedSession.start",						category: "Other",						group: "Control [devices]",			display: "Start timed session",			],
		[ name: "timedSession.stop",						category: "Other",						group: "Control [devices]",			display: "Stop timed session",			],
		[ name: "timedSession.pause",						category: "Other",						group: "Control [devices]",			display: "Pause timed session",			],
		[ name: "timedSession.cancel",						category: "Other",						group: "Control [devices]",			display: "Cancel timed session",		],
		[ name: "setConsumableStatus",						category: "Other",						group: "Control [devices]",			display: "Set consumable status",		parameters: ["Status:consumable"], description: "Set consumable status to {0}",	],
		[ name: "configure",	display: "Configure",					],
		[ name: "poll",			display: "Poll",	],
		[ name: "refresh",		display: "Refresh",	],
		/* predfined commands below */
		//general
		[ name: "reset",		display: "Reset",	],
		//hue
		[ name: "startLoop",	display: "Start color loop",			],
		[ name: "stopLoop",		display: "Stop color loop",				],
		[ name: "setLoopTime",	display: "Set loop duration",			parameters: ["Duration [s]:number[1..*]"], description: "Set loop duration to {0}s"],
		[ name: "setDirection",	display: "Switch loop direction",		description: "Set loop duration to {0}s"],
		[ name: "alert",		display: "Alert with lights",			parameters: ["Method:enum[Blink,Breathe,Okay,Stop]"], description: "Alert with lights: {0}"],
		[ name: "setAdjustedColor",display: "Transition to color",			parameters: ["Color:color","Duration [s]:number[1..60]"], description: "Transition to color {0} in {1}s"],
		//harmony
		[ name: "allOn",		display: "Turn all on",					],
		[ name: "allOff",		display: "Turn all off",				],
		[ name: "hubOn",		display: "Turn hub on",					],
		[ name: "hubOff",		display: "Turn hub off",				],
		//blink camera
		[ name: "enableCamera",	display: "Enable camera",				],
		[ name: "disableCamera",display: "Disable camera",				],
		[ name: "monitorOn",	display: "Turn monitor on",				],
		[ name: "monitorOff",	display: "Turn monitor off",			],
		[ name: "ledOn",		display: "Turn LED on",					],
		[ name: "ledOff",		display: "Turn LED off",				],
		[ name: "ledAuto",		display: "Set LED to Auto",				],
		[ name: "setVideoLength",display: "Set video length",			parameters: ["Seconds:number[1..10]"],	description: "Set video length to {0}s", ],
		//dlink camera
		[ name: "pirOn",		display: "Enable PIR motion detection",	],
		[ name: "pirOff",		display: "Disable PIR motion detection",],
		[ name: "nvOn",			display: "Set Night Vision to On",		],
		[ name: "nvOff",		display: "Set Night Vision to Off",		],
		[ name: "nvAuto",		display: "Set Night Vision to Auto",	],
		[ name: "vrOn",			display: "Enable local video recording",],
		[ name: "vrOff",		display: "Disable local video recording",],
		[ name: "left",			display: "Pan camera left",				],
		[ name: "right",		display: "Pan camera right",			],
		[ name: "up",			display: "Pan camera up",				],
		[ name: "down",			display: "Pan camera down",				],
		[ name: "home",			display: "Pan camera to the Home",		],
		[ name: "presetOne",	display: "Pan camera to preset #1",		],
		[ name: "presetTwo",	display: "Pan camera to preset #2",		],
		[ name: "presetThree",	display: "Pan camera to preset #3",		],
		[ name: "presetFour",	display: "Pan camera to preset #4",		],
		[ name: "presetFive",	display: "Pan camera to preset #5",		],
		[ name: "presetSix",	display: "Pan camera to preset #6",		],
		[ name: "presetSeven",	display: "Pan camera to preset #7",		],
		[ name: "presetEight",	display: "Pan camera to preset #8",		],
		[ name: "presetCommand",display: "Pan camera to custom preset",	parameters: ["Preset #:number[1..99]"], description: "Pan camera to preset #{0}",	],
        //zwave fan speed control by @pmjoen
		[ name: "low",	display: "Set to Low"],
		[ name: "med",	display: "Set to Medium"],
		[ name: "high",	display: "Set to High"],
	]
}

private virtualCommands() {
	def cmds = [
		[ name: "wait",				display: "Wait",							parameters: ["Time:number[1..1440]","Unit:enum[seconds,minutes,hours]"],													immediate: true,	location: true,	description: "Wait {0} {1}",	],
		[ name: "waitVariable",		display: "Wait (variable)",					parameters: ["Time (variable):variable","Unit:enum[seconds,minutes,hours]"],													immediate: true,	location: true,	description: "Wait |[{0}]| {1}",	],
		[ name: "waitRandom",		display: "Wait (random)",					parameters: ["At least:number[1..1440]","At most:number[1..1440]","Unit:enum[seconds,minutes,hours]"],	immediate: true,	location: true,	description: "Wait {0}-{1} {2}",	],
		[ name: "waitState",		display: "Wait for piston state change",	parameters: ["Change to:enum[any,false,true]"],															immediate: true,	location: true,						description: "Wait for {0} state"],
		[ name: "waitTime",			display: "Wait for common time",			parameters: ["Time:enum[midnight,sunrise,noon,sunset]","?Offset [minutes]:number[-1440..1440]","Days of week:enums[Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday]"],							immediate: true,	location: true,						description: "Wait for next {0} (offset {1} min), on {2}"],
		[ name: "waitCustomTime",	display: "Wait for custom time",			parameters: ["Time:time","Days of week:enums[Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday]"],							immediate: true,	location: true,						description: "Wait for {0}, on {1}"],
		[ name: "toggle",				requires: ["on", "off"], 			display: "Toggle",		],
		[ name: "toggle#1",				requires: ["on1", "off1"], 			display: "Toggle #1",		],
		[ name: "toggle#2",				requires: ["on2", "off2"], 			display: "Toggle #2",		],
		[ name: "toggle#3",				requires: ["on3", "off3"], 			display: "Toggle #3",		],
		[ name: "toggle#4",				requires: ["on4", "off4"], 			display: "Toggle #4",		],
		[ name: "toggle#5",				requires: ["on5", "off5"], 			display: "Toggle #5",		],
		[ name: "toggle#6",				requires: ["on6", "off6"], 			display: "Toggle #6",		],
		[ name: "toggle#7",				requires: ["on7", "off7"], 			display: "Toggle #7",		],
		[ name: "toggle#8",				requires: ["on8", "off8"], 			display: "Toggle #8",		],
		[ name: "toggleLevel",			requires: ["on", "off", "setLevel"],display: "Toggle level",					parameters: ["Level:level"],																																	description: "Toggle level between 0% and {0}%",	],
		[ name: "delayedOn",			requires: ["on"], 					display: "Turn on (delayed)",				parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn on after {0}ms",	],
		[ name: "delayedOn#1",			requires: ["on1"], 					display: "Turn on #1 (delayed)",			parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn on #1 after {0}ms",	],
		[ name: "delayedOn#2",			requires: ["on2"], 					display: "Turn on #2 (delayed)",			parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn on #2 after {0}ms",	],
		[ name: "delayedOn#3",			requires: ["on3"], 					display: "Turn on #3 (delayed)",			parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn on #3 after {0}ms",	],
		[ name: "delayedOn#4",			requires: ["on4"], 					display: "Turn on #4 (delayed)",			parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn on #4 after {0}ms",	],
		[ name: "delayedOn#5",			requires: ["on5"], 					display: "Turn on #5 (delayed)",			parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn on #5 after {0}ms",	],
		[ name: "delayedOn#6",			requires: ["on6"], 					display: "Turn on #6 (delayed)",			parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn on #6 after {0}ms",	],
		[ name: "delayedOn#7",			requires: ["on7"], 					display: "Turn on #7 (delayed)",			parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn on #7 after {0}ms",	],
		[ name: "delayedOn#8",			requires: ["on8"], 					display: "Turn on #8 (delayed)",			parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn on #8 after {0}ms",	],
		[ name: "delayedOff",			requires: ["off"], 					display: "Turn off (delayed)",				parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn off after {0}ms",	],
		[ name: "delayedOff#1",			requires: ["off1"],					display: "Turn off #1 (delayed)",			parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn off #1 after {0}ms",	],
		[ name: "delayedOff#2",			requires: ["off2"],					display: "Turn off #2 (delayed)",			parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn off #2 after {0}ms",	],
		[ name: "delayedOff#3",			requires: ["off3"],					display: "Turn off #3 (delayed)",			parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn off #3 after {0}ms",	],
		[ name: "delayedOff#4",			requires: ["off4"],					display: "Turn off #4 (delayed)",			parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn off #4 after {0}ms",	],
		[ name: "delayedOff#5",			requires: ["off5"],					display: "Turn off #5 (delayed)",			parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn off #5 after {0}ms",	],
		[ name: "delayedOff#6",			requires: ["off7"],					display: "Turn off #6 (delayed)",			parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn off #6 after {0}ms",	],
		[ name: "delayedOff#7",			requires: ["off7"],					display: "Turn off #7 (delayed)",			parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn off #7 after {0}ms",	],
		[ name: "delayedOff#8",			requires: ["off8"],					display: "Turn off #8 (delayed)",			parameters: ["Delay (ms):number[1..60000]"],																													description: "Turn off #8 after {0}ms",	],
		[ name: "delayedToggle",		requires: ["on", "off"], 			display: "Toggle (delayed)",				parameters: ["Delay (ms):number[1..60000]"],																													description: "Toggle after {0}ms",	],
		[ name: "delayedToggle#1",		requires: ["on1", "off1"], 			display: "Toggle #1 (delayed)",				parameters: ["Delay (ms):number[1..60000]"],																													description: "Toggle #1 after {0}ms",	],
		[ name: "delayedToggle#2",		requires: ["on2", "off2"], 			display: "Toggle #2 (delayed)",				parameters: ["Delay (ms):number[1..60000]"],																													description: "Toggle #2 after {0}ms",	],
		[ name: "delayedToggle#3",		requires: ["on3", "off3"], 			display: "Toggle #3 (delayed)",				parameters: ["Delay (ms):number[1..60000]"],																													description: "Toggle #3 after {0}ms",	],
		[ name: "delayedToggle#4",		requires: ["on4", "off4"], 			display: "Toggle #4 (delayed)",				parameters: ["Delay (ms):number[1..60000]"],																													description: "Toggle #4 after {0}ms",	],
		[ name: "delayedToggle#5",		requires: ["on5", "off5"], 			display: "Toggle #5 (delayed)",				parameters: ["Delay (ms):number[1..60000]"],																													description: "Toggle #5 after {0}ms",	],
		[ name: "delayedToggle#6",		requires: ["on6", "off6"], 			display: "Toggle #6 (delayed)",				parameters: ["Delay (ms):number[1..60000]"],																													description: "Toggle #6 after {0}ms",	],
		[ name: "delayedToggle#7",		requires: ["on7", "off7"], 			display: "Toggle #7 (delayed)",				parameters: ["Delay (ms):number[1..60000]"],																													description: "Toggle #7 after {0}ms",	],
		[ name: "delayedToggle#8",		requires: ["on8", "off8"], 			display: "Toggle #8 (delayed)",				parameters: ["Delay (ms):number[1..60000]"],																													description: "Toggle #8 after {0}ms",	],
		[ name: "setLevelVariable",		requires: ["setLevel"],				display: "Set level (variable)",						parameters: ["Level:variable"], description: "Set level to {0}%"],
		[ name: "setSaturationVariable",		requires: ["setSaturation"],				display: "Set saturation (variable)",						parameters: ["Saturation:variable"], description: "Set saturation to {0}%"],
		[ name: "setHueVariable",		requires: ["setHue"],				display: "Set hue (variable)",						parameters: ["Hue:variable"], description: "Set hue to {0}°"],
		[ name: "fadeLevelHW",			requires: ["setLevel"], 			display: "Fade to level (hardware)",		parameters: ["Target level:level","Duration (ms):number[1..60000]"],																							description: "Fade to {0}% in {1}ms",				],
		[ name: "fadeLevel",			requires: ["setLevel"], 			display: "Fade to level",					parameters: ["?Start level (optional):level","Target level:level","Duration (seconds):number[1..600]"],															description: "Fade level from {0}% to {1}% in {2}s",				],
		[ name: "fadeLevelVariable",	requires: ["setLevel"], 			display: "Fade to level (variable)",		parameters: ["?Start level (optional):variable","Target level:variable","Duration (seconds):number[1..600]"],															description: "Fade level from {0}% to {1}% in {2}s",				],
		[ name: "setLevelIf",			category: "Convenience",			group: "Control [devices]",					display: "Set level (advanced)",					parameters: ["Level:level","Only if switch state is:enum[on,off]"], description: "Set level to {0}% if switch is {1}",		attribute: "level",		value: "*|number",	],
		[ name: "adjustLevel",			requires: ["setLevel"], 			display: "Adjust level",					parameters: ["Adjustment (+/-):number[-100..100]"],																												description: "Adjust level by {0}%",	],
		[ name: "adjustLevelVariable",			requires: ["setLevel"], 			display: "Adjust level (variable)",					parameters: ["Adjustment (+/-):variable"],																												description: "Adjust level by {0}%",	],
        [ name: "fadeSaturation",		requires: ["setSaturation"],		display: "Fade to saturation",				parameters: ["?Start saturation (optional):saturation","Target saturation:saturation","Duration (seconds):number[1..600]"],											description: "Fade saturation from {0}% to {1}% in {2}s",				],
        [ name: "fadeSaturationVariable",requires: ["setSaturation"],		display: "Fade to saturation (variable)",				parameters: ["?Start saturation (optional):variable","Target saturation:variable","Duration (seconds):number[1..600]"],											description: "Fade saturation from {0}% to {1}% in {2}s",				],
		[ name: "adjustSaturation",		requires: ["setSaturation"],		display: "Adjust saturation",				parameters: ["Adjustment (+/-):number[-100..100]"],																												description: "Adjust saturation by {0}%",	],
		[ name: "adjustSaturationVariable",		requires: ["setSaturation"],		display: "Adjust saturation (variable)",				parameters: ["Adjustment (+/-):variable"],																												description: "Adjust saturation by {0}%",	],
		[ name: "fadeHue",				requires: ["setHue"], 				display: "Fade to hue",						parameters: ["?Start hue (optional):hue","Target hue:hue","Duration (seconds):number[1..600]"],																description: "Fade hue from {0}° to {1}° in {2}s",				],
		[ name: "fadeHueVariable",		requires: ["setHue"], 				display: "Fade to hue (variable)",			parameters: ["?Start hue (optional):variable","Target hue:variable","Duration (seconds):number[1..600]"],																description: "Fade hue from {0}° to {1}° in {2}s",				],
		[ name: "adjustHue",			requires: ["setHue"], 				display: "Adjust hue",						parameters: ["Adjustment (+/-):number[-360..360]"],																												description: "Adjust hue by {0}°",	],
		[ name: "adjustHueVariable",	requires: ["setHue"], 				display: "Adjust hue (variable)",			parameters: ["Adjustment (+/-):variable"],																												description: "Adjust hue by {0}°",	],
		[ name: "flash",				requires: ["on", "off"], 			display: "Flash",							parameters: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					description: "Flash {0}ms/{1}ms for {2} time(s)",		],
		[ name: "flash#1",				requires: ["on1", "off1"], 			display: "Flash #1",						parameters: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					description: "Flash #1 {0}ms/{1}ms for {2} time(s)",	],
		[ name: "flash#2",				requires: ["on2", "off2"], 			display: "Flash #2",						parameters: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					description: "Flash #2 {0}ms/{1}ms for {2} time(s)",	],
		[ name: "flash#3",				requires: ["on3", "off3"], 			display: "Flash #3",						parameters: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					description: "Flash #3 {0}ms/{1}ms for {2} time(s)",	],
		[ name: "flash#4",				requires: ["on4", "off4"], 			display: "Flash #4",						parameters: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					description: "Flash #4 {0}ms/{1}ms for {2} time(s)",	],
		[ name: "flash#5",				requires: ["on5", "off5"], 			display: "Flash #5",						parameters: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					description: "Flash #5 {0}ms/{1}ms for {2} time(s)",	],
		[ name: "flash#6",				requires: ["on6", "off6"], 			display: "Flash #6",						parameters: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					description: "Flash #6 {0}ms/{1}ms for {2} time(s)",	],
		[ name: "flash#7",				requires: ["on7", "off7"], 			display: "Flash #7",						parameters: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					description: "Flash #7 {0}ms/{1}ms for {2} time(s)",	],
		[ name: "flash#8",				requires: ["on8", "off8"], 			display: "Flash #8",						parameters: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					description: "Flash #8 {0}ms/{1}ms for {2} time(s)",	],
		[ name: "setVariable",		display: "Set variable", 					parameters: ["Variable:var"],																				varEntry: 0, 						location: true,																	aggregated: true,	],
		[ name: "saveAttribute",	display: "Save attribute to variable", 		parameters: ["Attribute:attribute","Aggregation:aggregation","?Convert to data type:dataType","Save to variable:string"],					varEntry: 3,		description: "Save attribute '{0}' to variable |[{3}]|'",			aggregated: true,	],
		[ name: "saveState",		display: "Save state to variable",			parameters: ["Attributes:attributes","Aggregation:aggregation","?Convert to data type:dataType","Save to state variable:string"],			stateVarEntry: 3,	description: "Save state of attributes {0} to variable |[{3}]|'",	aggregated: true,	],
		[ name: "saveStateLocally",	display: "Capture state to local store",	parameters: ["Attributes:attributes","?Only if state is empty:bool"],																															description: "Capture state of attributes {0} to local store",		],
		[ name: "saveStateGlobally",display: "Capture state to global store",	parameters: ["Attributes:attributes","?Only if state is empty:bool"],																															description: "Capture state of attributes {0} to global store",	],
		[ name: "loadAttribute",	display: "Load attribute from variable",	parameters: ["Attribute:attribute","Load from variable:variable","Allow translations:bool","Negate translation:bool"],											description: "Load attribute '{0}' from variable |[{1}]|",	],
		[ name: "loadState",		display: "Load state from variable",		parameters: ["Attributes:attributes","Load from state variable:stateVariable","Allow translations:bool","Negate translation:bool"],								description: "Load state of attributes {0} from variable |[{1}]|"				],
		[ name: "loadStateLocally",	display: "Restore state from local store",	parameters: ["Attributes:attributes","?Empty the state:bool"],																															description: "Restore state of attributes {0} from local store",			],
		[ name: "loadStateGlobally",display: "Restore state from global store",	parameters: ["Attributes:attributes","?Empty the state:bool"],																															description: "Restore state of attributes {0} from global store",			],
		[ name: "setLocationMode",	display: "Set location mode",				parameters: ["Mode:mode"],																														location: true,	description: "Set location mode to '{0}'",		aggregated: true,	],
		[ name: "setAlarmSystemStatus",display: "Set Smart Home Monitor status",	parameters: ["Status:alarmSystemStatus"],																										location: true,	description: "Set SHM alarm to '{0}'",			aggregated: true,	],
		[ name: "sendNotification",	display: "Send notification",				parameters: ["Message:text"],																													location: true,	description: "Send notification '{0}' in notifications page",			aggregated: true,	],
		[ name: "sendPushNotification",display: "Send Push notification",			parameters: ["Message:text","Show in notifications page:bool"],																							location: true,	description: "Send Push notification '{0}'",		aggregated: true,	],
		[ name: "sendSMSNotification",display: "Send SMS notification",			parameters: ["Message:text","Phone number:phone","Show in notifications page:bool"],																		location: true, description: "Send SMS notification '{0}' to {1}",aggregated: true,	],
		[ name: "queueAskAlexaMessage",display: "Queue AskAlexa message",			parameters: ["Message:text", "?Unit:text", "?Application:text"],																		location: true, description: "Queue AskAlexa message '{0}' in unit {1}",aggregated: true,	],
		[ name: "deleteAskAlexaMessages",display: "Delete AskAlexa messages",			parameters: ["Unit:text", "?Application:text"],																	location: true, description: "Delete AskAlexa messages in unit {1}",aggregated: true,	],
		[ name: "executeRoutine",	display: "Execute routine",					parameters: ["Routine:routine"],																		location: true, 										description: "Execute routine '{0}'",				aggregated: true,	],
		[ name: "cancelPendingTasks",display: "Cancel pending tasks",			parameters: ["Scope:enum[Local,Global]"],																														description: "Cancel all pending {0} tasks",		],
		[ name: "followUp",				display: "Follow up with piston",			parameters: ["Delay:number[1..1440]","Unit:enum[seconds,minutes,hours]","Piston:piston","?Save state into variable:string"],	immediate: true,	varEntry: 3,	location: true,	description: "Follow up with piston '{2}' after {0} {1}",	aggregated: true],
		[ name: "executePiston",		display: "Execute piston",					parameters: ["Piston:piston","?Save state into variable:string"],																varEntry: 1,	location: true,	description: "Execute piston '{0}'",	aggregated: true],
		[ name: "pausePiston",			display: "Pause piston",					parameters: ["Piston:piston"],																location: true,	description: "Pause piston '{0}'",	aggregated: true],
		[ name: "resumePiston",			display: "Resume piston",					parameters: ["Piston:piston"],																location: true,	description: "Resume piston '{0}'",	aggregated: true],
        [ name: "httpRequest",			display: "Make a web request", parameters: ["URL:string","Method:enum[GET,POST,PUT,DELETE,HEAD]","Content Type:enum[JSON,FORM]","?Variables to send:variables","Import response data into variables:bool","?Variable import name prefix (optional):string"], location: true, description: "Make a {1} web request to {0}", aggregated: true],
        [ name: "wolRequest",			display: "Wake a LAN device", parameters: ["MAC address:string","?Secure code:string"], location: true, description: "Wake LAN device at address {0} with secure code {1}", aggregated: true],
        
		//flow control commands
		[ name: "beginSimpleForLoop",	display: "Begin FOR loop (simple)",			parameters: ["Number of cycles:string"],																																										location: true,		description: "FOR {0} CYCLES DO",			flow: true,					indent: 1,	],
		[ name: "beginForLoop",			display: "Begin FOR loop",					parameters: ["Variable to use:string","From value:string","To value:string"],																													varEntry: 0,	location: true,		description: "FOR {0} = {1} TO {2} DO",		flow: true,					indent: 1,	],
		[ name: "beginWhileLoop",		display: "Begin WHILE loop",				parameters: ["Variable to test:variable","Comparison:enum[is equal to,is not equal to,is less than,is less than or equal to,is greater than,is greater than or equal to]","Value:string"],						location: true,		description: "WHILE (|[{0}]| {1} {2}) DO",		flow: true,					indent: 1,	],
		[ name: "breakLoop",			display: "Break loop",						location: true,		description: "BREAK",						flow: true,			],
		[ name: "breakLoopIf",			display: "Break loop (conditional)",		parameters: ["Variable to test:variable","Comparison:enum[is equal to,is not equal to,is less than,is less than or equal to,is greater than,is greater than or equal to]","Value:string"],						location: true,		description: "BREAK IF ({0} {1} {2})",		flow: true,			],
		[ name: "exitAction",			display: "Exit Action",						location: true,		description: "EXIT",						flow: true,			],
		[ name: "endLoop",				display: "End loop",						parameters: ["Delay (seconds):number[0..*]"],																																									location: true,		description: "LOOP AFTER {0}s",				flow: true,	selfIndent: -1, indent: -1,	],
		[ name: "beginIfBlock",			display: "Begin IF block",					parameters: ["Variable to test:variable","Comparison:enum[is equal to,is not equal to,is less than,is less than or equal to,is greater than,is greater than or equal to]","Value:string"],						location: true,		description: "IF (|[{0}]| {1} {2}) THEN",		flow: true,					indent: 1,	],
		[ name: "beginElseIfBlock",		display: "Begin ELSE IF block",				parameters: ["Variable to test:variable","Comparison:enum[is equal to,is not equal to,is less than,is less than or equal to,is greater than,is greater than or equal to]","Value:string"],						location: true,		description: "ELSE IF (|[{0}]| {1} {2}) THEN",	flow: true,	selfIndent: -1,				],
		[ name: "beginElseBlock",		display: "Begin ELSE block",				location: true,		description: "ELSE",						flow: true,	selfIndent: -1,		 		],
		[ name: "endIfBlock",			display: "End IF block",					location: true,		description: "END IF",						flow: true,	selfIndent: -1, indent: -1,	],
		[ name: "beginSwitchBlock",		display: "Begin SWITCH block",				parameters: ["Variable to test:variable"],																																										location: true,		description: "SWITCH (|[{0}]|) DO",				flow: true,					indent: 2,	],
		[ name: "beginSwitchCase",		display: "Begin CASE block",				parameters: ["Value:string"],																																													location: true,		description: "CASE '{0}':",					flow: true,	selfIndent: -1,		],
		[ name: "endSwitchBlock",		display: "End SWITCH block",				location: true,		description: "END SWITCH",					flow: true,	selfIndent: -2,	indent: -2,	],
    ]
   	if (location.contactBookEnabled) {
    	cmds.push([ name: "sendNotificationToContacts", display: "Send notification to contacts", parameters: ["Message:text","Contacts:contacts","Save notification:bool"], location: true, description: "Send notification '{0}' to {1}", aggregated: true])
    }
    if (getIftttKey()) {
    	cmds.push([ name: "iftttMaker", display: "Send IFTTT Maker event", parameters: ["Event:text", "?Value1:string", "?Value2:string", "?Value3:string"], location: true, description: "Send IFTTT Maker event '{0}' with parameters '{1}', '{2}', and '{3}'", aggregated: true])
    }
	if (getLifxToken()) {
		cmds.push([ name: "lifxScene", display: "Activate LIFX scene", parameters: ["Scene:lifxScenes"], location: true, description: "Activate LIFX Scene '{0}'", aggregated: true])
    }    
    return cmds
}

private attributes() {
	if (state.temp && state.temp.attributes) return state.temp.attributes
	def tempUnit = "°" + location.temperatureScale
	state.temp = state.temp ?: [:]
	state.temp.attributes = [
		[ name: "acceleration",				type: "enum",			options: ["active", "inactive"],	],
		[ name: "alarm",					type: "enum",			options: ["off", "strobe", "siren", "both"],	],
		[ name: "battery",					type: "number",			range: "0..100",		unit: "%",	],
		[ name: "beacon",					type: "enum",			options: ["present", "not present"],	],
		[ name: "button",					type: "enum",			options: ["held", "pushed"],	capability: "button",	momentary: true], //default capability so that we can figure out multi sub devices
		[ name: "carbonDioxide",			type: "decimal",		range: "0..*",	],
		[ name: "carbonMonoxide",			type: "enum",			options: ["clear", "detected", "tested"],	],
		[ name: "color",					type: "color",			unit: "#RRGGBB",	],
		[ name: "hue",						type: "number",			range: "0..360",		unit: "°",	],
		[ name: "saturation",				type: "number",			range: "0..100",		unit: "%",	],
		[ name: "hex",						type: "hexcolor",		],
		[ name: "saturation",				type: "number",			range: "0..100",		unit: "%",	],
		[ name: "level",					type: "number",			range: "0..100",		unit: "%",	],
		[ name: "switch",					type: "enum",			options: ["on", "off"],	interactive: true,	],
		[ name: "switch*",					type: "enum",			options: ["on", "off"],	interactive: true,	],
		[ name: "colorTemperature",			type: "number",			range: "1700..27000",	unit: "°K",	],
		[ name: "consumable",				type: "enum",			options: ["missing", "good", "replace", "maintenance_required", "order"],	],
		[ name: "contact",					type: "enum",			options: ["open", "closed"],	],
		[ name: "door",						type: "enum",			options: ["unknown", "closed", "open", "closing", "opening"],	interactive: true,	],
		[ name: "energy",					type: "decimal",		range: "0..*",			unit: "kWh",	],
		[ name: "energy*",					type: "decimal",		range: "0..*",			unit: "kWh",	],
		[ name: "indicatorStatus",			type: "enum",			options: ["when off", "when on", "never"],	],
		[ name: "illuminance",				type: "number",			range: "0..*",			unit: "lux",	],
		[ name: "image",					type: "image",			],
		[ name: "lock",						type: "enum",			options: ["locked", "unlocked"],	capability: "lock", interactive: true,	],
		[ name: "activities",				type: "string",			],
		[ name: "currentActivity",			type: "string",			],
		[ name: "motion",					type: "enum",			options: ["active", "inactive"],	],
		[ name: "status",					type: "string",			],
		[ name: "mute",						type: "enum",			options: ["muted", "unmuted"],	],
		[ name: "pH",						type: "decimal",		range: "0..14",	],
		[ name: "power",					type: "decimal",		range: "0..*",			unit: "W",	],
		[ name: "power*",					type: "decimal",		range: "0..*",			unit: "W",	],
		[ name: "occupancy",				type: "enum",			options: ["occupied", "not occupied"],	],
		[ name: "presence",					type: "enum",			options: ["present", "not present"],	],
		[ name: "humidity",					type: "number",			range: "0..100",		unit: "%",	],
		[ name: "shock",					type: "enum",			options: ["detected", "clear"],	],
		[ name: "lqi",						type: "number",			range: "0..255",	],
		[ name: "rssi",						type: "number",			range: "0..100",		unit: "%",	],
		[ name: "sleeping",					type: "enum",			options: ["sleeping", "not sleeping"],	],
		[ name: "smoke",					type: "enum",			options: ["clear", "detected", "tested"],	],
		[ name: "sound",					type: "enum",			options: ["detected", "not detected"], ],
		[ name: "steps",					type: "number",			range: "0..*",	],
		[ name: "goal",						type: "number",			range: "0..*",	],
		[ name: "soundPressureLevel",		type: "number",			range: "0..*",	],
		[ name: "tamper",					type: "enum",			options: ["clear", "detected"],	],
		[ name: "temperature",				type: "decimal",		range: "*..*",			unit: tempUnit,		],
		[ name: "thermostatMode",			type: "enum",			options: ["off", "auto", "cool", "heat", "emergency heat"],	],
		[ name: "thermostatFanMode",		type: "enum",			options: ["auto", "on", "circulate"],	],
		[ name: "thermostatOperatingState",	type: "enum",			options: ["idle", "pending cool", "cooling", "pending heat", "heating", "fan only", "vent economizer"],		],
		[ name: "coolingSetpoint",			type: "decimal",		range: "-127..127",		unit: tempUnit,	],
		[ name: "heatingSetpoint",			type: "decimal",		range: "-127..127",		unit: tempUnit,	],
		[ name: "thermostatSetpoint",		type: "decimal",		range: "-127..127",		unit: tempUnit,	],
		[ name: "sessionStatus",			type: "enum",			options: ["paused", "stopped", "running", "canceled"],	],
		[ name: "threeAxis",				type: "vector3",		],
		[ name: "orientation",				type: "orientation",	options: threeAxisOrientations(),	valueType: "enum",	subscribe: "threeAxis",	],
		[ name: "axisX",					type: "number",			range: "-1024..1024",	unit: null,		options: null,					subscribe: "threeAxis",		],
		[ name: "axisY",					type: "number",			range: "-1024..1024",	unit: null,		options: null,					subscribe: "threeAxis",		],
		[ name: "axisZ",					type: "number",			range: "-1024..1024",	unit: null,		options: null,					subscribe: "threeAxis",		],
		[ name: "touch",					type: "enum",			options: ["touched"],		],
		[ name: "valve",					type: "enum",			options: ["open", "closed"],					],
		[ name: "voltage",					type: "decimal",		range: "*..*",			unit: "V",	],
		[ name: "water",					type: "enum",			options: ["dry", "wet"],	],
		[ name: "windowShade",				type: "enum",			options: ["unknown", "open", "closed", "opening", "closing", "partially open"],	],
		[ name: "mode",						type: "mode",			options: state.run == "config" ? getLocationModeOptions() : [],	],
		[ name: "alarmSystemStatus",		type: "enum",			options: state.run == "config" ? getAlarmSystemStatusOptions() : [],	],
		[ name: "routineExecuted",			type: "routine",		options: state.run == "config" ? location.helloHome?.getPhrases()*.label : [],	valueType: "enum",	],
		[ name: "piston",					type: "piston",			options: state.run == "config" ? parent.listPistons(state.config.expertMode ? null : app.label) : [],	valueType: "enum",	],
		[ name: "variable",					type: "enum",			options: state.run == "config" ? listVariables(true) : [],	valueType: "enum",	],
		[ name: "time",						type: "time",	],
		[ name: "askAlexaMacro",			type: "askAlexaMacro",	options: state.run == "config" ? listAskAlexaMacros() : [], valueType: "enum"],
		[ name: "ifttt",					type: "ifttt",			valueType: "string"],
	]
	return state.temp.attributes
}

private comparisons() {
	def optionsEnum = [
		[ condition: "is", trigger: "changes to", parameters: 1, timed: false],
		[ condition: "is not", trigger: "changes away from", parameters: 1, timed: false],
		[ condition: "is one of", trigger: "changes to one of", parameters: 1, timed: false, multiple: true, minOptions: 2],
		[ condition: "is not one of", trigger: "changes away from one of", parameters: 1, timed: false, multiple: true, minOptions: 2],
		[ condition: "was", trigger: "stays", parameters: 1, timed: true],
		[ condition: "was not", trigger: "stays away from", parameters: 1, timed: true],
		[ trigger: "changes", parameters: 0, timed: false],
		[ condition: "changed", parameters: 0, timed: true],
		[ condition: "did not change", parameters: 0, timed: true],
	]

	def optionsMomentary = [
		[ condition: "is", trigger: "changes to", parameters: 1, timed: false],
	]

	def optionsBool = [
		[ condition: "is equal to", parameters: 1, timed: false],
		[ condition: "is not equal to", parameters: 1, timed: false],
		[ condition: "is true", parameters: 0, timed: false],
		[ condition: "is false", parameters: 0, timed: false],
	]
	def optionsEvents = [
		[ trigger: "executed", parameters: 1, timed: false],
	]
	def optionsNumber = [
		[ condition: "is equal to", trigger: "changes to", parameters: 1, timed: false],
		[ condition: "is not equal to", trigger: "changes away from", parameters: 1, timed: false],
		[ condition: "is less than", trigger: "drops below", parameters: 1, timed: false],
		[ condition: "is less than or equal to", trigger: "drops to or below", parameters: 1, timed: false],
		[ condition: "is greater than", trigger: "raises above", parameters: 1, timed: false],
		[ condition: "is greater than or equal to", trigger: "raises to or above", parameters: 1, timed: false],
		[ condition: "is inside range", trigger: "enters range", parameters: 2, timed: false],
		[ condition: "is outside of range", trigger: "exits range", parameters: 2, timed: false],
		[ condition: "is even", trigger: "changes to an even value", parameters: 0, timed: false],
		[ condition: "is odd", trigger: "changes to an odd value", parameters: 0, timed: false],
		[ condition: "was equal to", trigger: "stays equal to", parameters: 1, timed: true],
		[ condition: "was not equal to", trigger: "stays not equal to", parameters: 1, timed: true],
		[ condition: "was less than", trigger: "stays less than", parameters: 1, timed: true],
		[ condition: "was less than or equal to", trigger: "stays less than or equal to", parameters: 1, timed: true],
		[ condition: "was greater than", trigger: "stays greater than", parameters: 1, timed: true],
		[ condition: "was greater than or equal to", trigger: "stays greater than or equal to", parameters: 1, timed: true],
		[ condition: "was inside range",trigger: "stays inside range",  parameters: 2, timed: true],
		[ condition: "was outside of range", trigger: "stays outside of range", parameters: 2, timed: true],
		[ condition: "was even", trigger: "stays even", parameters: 0, timed: true],
		[ condition: "was odd", trigger: "stays odd", parameters: 0, timed: true],
		[ trigger: "changes", parameters: 0, timed: false],
		[ trigger: "raises", parameters: 0, timed: false],
		[ trigger: "drops", parameters: 0, timed: false],
		[ condition: "changed", parameters: 0, timed: true],
		[ condition: "did not change", parameters: 0, timed: true],
	]
	def optionsTime = [
		[ trigger: "happens at", parameters: 1],
		[ condition: "is any time of day", parameters: 0],
		[ condition: "is around", parameters: 1],
		[ condition: "is before", parameters: 1],
		[ condition: "is after", parameters: 1],
		[ condition: "is between", parameters: 2],
		[ condition: "is not between", parameters: 2],
	]
	return [
		[ type: "bool",					options: optionsBool,		],
		[ type: "boolean",				options: optionsBool,		],
		[ type: "vector3",				options: optionsEnum,		],
		[ type: "orientation",			options: optionsEnum,		],
		[ type: "string",				options: optionsEnum,		],
		[ type: "text",					options: optionsEnum,		],
		[ type: "enum",					options: optionsEnum,		],
		[ type: "mode",					options: optionsEnum,		],
		[ type: "alarmSystemStatus",	options: optionsEnum,		],
		[ type: "routine",				options: optionsEvents		],
		[ type: "piston",				options: optionsEvents		],
		[ type: "askAlexaMacro",		options: optionsEvents		],
		[ type: "ifttt",				options: optionsEvents		],
		[ type: "number",				options: optionsNumber,		],
		[ type: "variable",				options: optionsNumber,		],
		[ type: "decimal",				options: optionsNumber		],
		[ type: "time",					options: optionsTime,		],
		[ type: "momentary",			options: optionsMomentary,	],
	]
}

private getLocationModeOptions() {
	def result = []
	for (mode in location.modes) {
		if (mode) result.push("$mode")
	}
	return result
}
private getAlarmSystemStatusOptions() {
	return ["Disarmed", "Armed/Stay", "Armed/Away"]
}

private initialSystemStore() {
	return [
		"\$currentEventAttribute": null,
		"\$currentEventDate": null,
		"\$currentEventDelay": 0,
		"\$currentEventDevice": null,
		"\$currentEventDeviceIndex": 0,
		"\$currentEventDevicePhysical": false,
		"\$currentEventReceived": null,
		"\$currentEventValue": null,
		"\$currentState": null,
		"\$currentStateDuration": 0,
		"\$currentStateSince": null,
		"\$currentStateSince": null,
		"\$nextScheduledTime": null,
		"\$now": 999999999999,
		"\$hour": 0,
		"\$hour24": 0,
		"\$minute": 0,
		"\$second": 0,
		"\$meridian": "",
		"\$meridianWithDots": "",
		"\$day": 0,
		"\$dayOfWeek": 0,
		"\$dayOfWeekName": "",
		"\$month": 0,
		"\$monthName": "",
		"\$index": 0,
		"\$year": 0,
		"\$meridianWithDots": "",
		"\$previousEventAttribute": null,
		"\$previousEventDate": null,
		"\$previousEventDelay": 0,
		"\$previousEventDevice": null,
		"\$previousEventDeviceIndex": 0,
		"\$previousEventDevicePhysical": 0,
		"\$previousEventExecutionTime": 0,
		"\$previousEventReceived": null,
		"\$previousEventValue": null,
		"\$previousState": null,
		"\$previousStateDuration": 0,
		"\$previousStateSince": null,
		"\$random": 0,
		"\$randomColor": "#FFFFFF",
		"\$randomColorName": "White",
		"\$randomLevel": 0,
		"\$randomSaturation": 0,
		"\$randomHue": 0,
        "\$midnight": 999999999999,
        "\$noon": 999999999999,
        "\$sunrise": 999999999999,
        "\$sunset": 999999999999,
        "\$nextMidnight": 999999999999,
        "\$nextNoon": 999999999999,
        "\$nextSunrise": 999999999999,
        "\$nextSunset": 999999999999,
		"\$time": "",
		"\$time24": "",
        "\$httpStatusCode": 0,
        "\$httpStatusOk": true,
        "\$iftttStatusCode": 0,
        "\$iftttStatusOk": true,
        "\$locationMode": "",
        "\$shmStatus": ""
	]
}


private colors() {
	return [
		[ name: "Random",					rgb: "#000000",		h: 0,		s: 0,		l: 0,	],
		[ name: "Soft White",				rgb: "#B6DA7C",		h: 83,		s: 44,		l: 67,	],
		[ name: "Warm White",				rgb: "#DAF17E",		h: 72,		s: 20,		l: 72,	],
		[ name: "Daylight White",			rgb: "#CEF4FD",		h: 191,		s: 9,		l: 90,	],
		[ name: "Cool White",				rgb: "#F3F6F7",		h: 187,		s: 19,		l: 96,	],
		[ name: "White",					rgb: "#FFFFFF",		h: 0,		s: 0,		l: 100,	],
		[ name: "Alice Blue",				rgb: "#F0F8FF",		h: 208,		s: 100,		l: 97,	],
		[ name: "Antique White",			rgb: "#FAEBD7",		h: 34,		s: 78,		l: 91,	],
		[ name: "Aqua",						rgb: "#00FFFF",		h: 180,		s: 100,		l: 50,	],
		[ name: "Aquamarine",				rgb: "#7FFFD4",		h: 160,		s: 100,		l: 75,	],
		[ name: "Azure",					rgb: "#F0FFFF",		h: 180,		s: 100,		l: 97,	],
		[ name: "Beige",					rgb: "#F5F5DC",		h: 60,		s: 56,		l: 91,	],
		[ name: "Bisque",					rgb: "#FFE4C4",		h: 33,		s: 100,		l: 88,	],
		[ name: "Blanched Almond",			rgb: "#FFEBCD",		h: 36,		s: 100,		l: 90,	],
		[ name: "Blue",						rgb: "#0000FF",		h: 240,		s: 100,		l: 50,	],
		[ name: "Blue Violet",				rgb: "#8A2BE2",		h: 271,		s: 76,		l: 53,	],
		[ name: "Brown",					rgb: "#A52A2A",		h: 0,		s: 59,		l: 41,	],
		[ name: "Burly Wood",				rgb: "#DEB887",		h: 34,		s: 57,		l: 70,	],
		[ name: "Cadet Blue",				rgb: "#5F9EA0",		h: 182,		s: 25,		l: 50,	],
		[ name: "Chartreuse",				rgb: "#7FFF00",		h: 90,		s: 100,		l: 50,	],
		[ name: "Chocolate",				rgb: "#D2691E",		h: 25,		s: 75,		l: 47,	],
		[ name: "Coral",					rgb: "#FF7F50",		h: 16,		s: 100,		l: 66,	],
		[ name: "Corn Flower Blue",			rgb: "#6495ED",		h: 219,		s: 79,		l: 66,	],
		[ name: "Corn Silk",				rgb: "#FFF8DC",		h: 48,		s: 100,		l: 93,	],
		[ name: "Crimson",					rgb: "#DC143C",		h: 348,		s: 83,		l: 58,	],
		[ name: "Cyan",						rgb: "#00FFFF",		h: 180,		s: 100,		l: 50,	],
		[ name: "Dark Blue",				rgb: "#00008B",		h: 240,		s: 100,		l: 27,	],
		[ name: "Dark Cyan",				rgb: "#008B8B",		h: 180,		s: 100,		l: 27,	],
		[ name: "Dark Golden Rod",			rgb: "#B8860B",		h: 43,		s: 89,		l: 38,	],
		[ name: "Dark Gray",				rgb: "#A9A9A9",		h: 0,		s: 0,		l: 66,	],
		[ name: "Dark Green",				rgb: "#006400",		h: 120,		s: 100,		l: 20,	],
		[ name: "Dark Khaki",				rgb: "#BDB76B",		h: 56,		s: 38,		l: 58,	],
		[ name: "Dark Magenta",				rgb: "#8B008B",		h: 300,		s: 100,		l: 27,	],
		[ name: "Dark Olive Green",			rgb: "#556B2F",		h: 82,		s: 39,		l: 30,	],
		[ name: "Dark Orange",				rgb: "#FF8C00",		h: 33,		s: 100,		l: 50,	],
		[ name: "Dark Orchid",				rgb: "#9932CC",		h: 280,		s: 61,		l: 50,	],
		[ name: "Dark Red",					rgb: "#8B0000",		h: 0,		s: 100,		l: 27,	],
		[ name: "Dark Salmon",				rgb: "#E9967A",		h: 15,		s: 72,		l: 70,	],
		[ name: "Dark Sea Green",			rgb: "#8FBC8F",		h: 120,		s: 25,		l: 65,	],
		[ name: "Dark Slate Blue",			rgb: "#483D8B",		h: 248,		s: 39,		l: 39,	],
		[ name: "Dark Slate Gray",			rgb: "#2F4F4F",		h: 180,		s: 25,		l: 25,	],
		[ name: "Dark Turquoise",			rgb: "#00CED1",		h: 181,		s: 100,		l: 41,	],
		[ name: "Dark Violet",				rgb: "#9400D3",		h: 282,		s: 100,		l: 41,	],
		[ name: "Deep Pink",				rgb: "#FF1493",		h: 328,		s: 100,		l: 54,	],
		[ name: "Deep Sky Blue",			rgb: "#00BFFF",		h: 195,		s: 100,		l: 50,	],
		[ name: "Dim Gray",					rgb: "#696969",		h: 0,		s: 0,		l: 41,	],
		[ name: "Dodger Blue",				rgb: "#1E90FF",		h: 210,		s: 100,		l: 56,	],
		[ name: "Fire Brick",				rgb: "#B22222",		h: 0,		s: 68,		l: 42,	],
		[ name: "Floral White",				rgb: "#FFFAF0",		h: 40,		s: 100,		l: 97,	],
		[ name: "Forest Green",				rgb: "#228B22",		h: 120,		s: 61,		l: 34,	],
		[ name: "Fuchsia",					rgb: "#FF00FF",		h: 300,		s: 100,		l: 50,	],
		[ name: "Gainsboro",				rgb: "#DCDCDC",		h: 0,		s: 0,		l: 86,	],
		[ name: "Ghost White",				rgb: "#F8F8FF",		h: 240,		s: 100,		l: 99,	],
		[ name: "Gold",						rgb: "#FFD700",		h: 51,		s: 100,		l: 50,	],
		[ name: "Golden Rod",				rgb: "#DAA520",		h: 43,		s: 74,		l: 49,	],
		[ name: "Gray",						rgb: "#808080",		h: 0,		s: 0,		l: 50,	],
		[ name: "Green",					rgb: "#008000",		h: 120,		s: 100,		l: 25,	],
		[ name: "Green Yellow",				rgb: "#ADFF2F",		h: 84,		s: 100,		l: 59,	],
		[ name: "Honeydew",					rgb: "#F0FFF0",		h: 120,		s: 100,		l: 97,	],
		[ name: "Hot Pink",					rgb: "#FF69B4",		h: 330,		s: 100,		l: 71,	],
		[ name: "Indian Red",				rgb: "#CD5C5C",		h: 0,		s: 53,		l: 58,	],
		[ name: "Indigo",					rgb: "#4B0082",		h: 275,		s: 100,		l: 25,	],
		[ name: "Ivory",					rgb: "#FFFFF0",		h: 60,		s: 100,		l: 97,	],
		[ name: "Khaki",					rgb: "#F0E68C",		h: 54,		s: 77,		l: 75,	],
		[ name: "Lavender",					rgb: "#E6E6FA",		h: 240,		s: 67,		l: 94,	],
		[ name: "Lavender Blush",			rgb: "#FFF0F5",		h: 340,		s: 100,		l: 97,	],
		[ name: "Lawn Green",				rgb: "#7CFC00",		h: 90,		s: 100,		l: 49,	],
		[ name: "Lemon Chiffon",			rgb: "#FFFACD",		h: 54,		s: 100,		l: 90,	],
		[ name: "Light Blue",				rgb: "#ADD8E6",		h: 195,		s: 53,		l: 79,	],
		[ name: "Light Coral",				rgb: "#F08080",		h: 0,		s: 79,		l: 72,	],
		[ name: "Light Cyan",				rgb: "#E0FFFF",		h: 180,		s: 100,		l: 94,	],
		[ name: "Light Golden Rod Yellow",	rgb: "#FAFAD2",		h: 60,		s: 80,		l: 90,	],
		[ name: "Light Gray",				rgb: "#D3D3D3",		h: 0,		s: 0,		l: 83,	],
		[ name: "Light Green",				rgb: "#90EE90",		h: 120,		s: 73,		l: 75,	],
		[ name: "Light Pink",				rgb: "#FFB6C1",		h: 351,		s: 100,		l: 86,	],
		[ name: "Light Salmon",				rgb: "#FFA07A",		h: 17,		s: 100,		l: 74,	],
		[ name: "Light Sea Green",			rgb: "#20B2AA",		h: 177,		s: 70,		l: 41,	],
		[ name: "Light Sky Blue",			rgb: "#87CEFA",		h: 203,		s: 92,		l: 75,	],
		[ name: "Light Slate Gray",			rgb: "#778899",		h: 210,		s: 14,		l: 53,	],
		[ name: "Light Steel Blue",			rgb: "#B0C4DE",		h: 214,		s: 41,		l: 78,	],
		[ name: "Light Yellow",				rgb: "#FFFFE0",		h: 60,		s: 100,		l: 94,	],
		[ name: "Lime",						rgb: "#00FF00",		h: 120,		s: 100,		l: 50,	],
		[ name: "Lime Green",				rgb: "#32CD32",		h: 120,		s: 61,		l: 50,	],
		[ name: "Linen",					rgb: "#FAF0E6",		h: 30,		s: 67,		l: 94,	],
		[ name: "Maroon",					rgb: "#800000",		h: 0,		s: 100,		l: 25,	],
		[ name: "Medium Aquamarine",		rgb: "#66CDAA",		h: 160,		s: 51,		l: 60,	],
		[ name: "Medium Blue",				rgb: "#0000CD",		h: 240,		s: 100,		l: 40,	],
		[ name: "Medium Orchid",			rgb: "#BA55D3",		h: 288,		s: 59,		l: 58,	],
		[ name: "Medium Purple",			rgb: "#9370DB",		h: 260,		s: 60,		l: 65,	],
		[ name: "Medium Sea Green",			rgb: "#3CB371",		h: 147,		s: 50,		l: 47,	],
		[ name: "Medium Slate Blue",		rgb: "#7B68EE",		h: 249,		s: 80,		l: 67,	],
		[ name: "Medium Spring Green",		rgb: "#00FA9A",		h: 157,		s: 100,		l: 49,	],
		[ name: "Medium Turquoise",			rgb: "#48D1CC",		h: 178,		s: 60,		l: 55,	],
		[ name: "Medium Violet Red",		rgb: "#C71585",		h: 322,		s: 81,		l: 43,	],
		[ name: "Midnight Blue",			rgb: "#191970",		h: 240,		s: 64,		l: 27,	],
		[ name: "Mint Cream",				rgb: "#F5FFFA",		h: 150,		s: 100,		l: 98,	],
		[ name: "Misty Rose",				rgb: "#FFE4E1",		h: 6,		s: 100,		l: 94,	],
		[ name: "Moccasin",					rgb: "#FFE4B5",		h: 38,		s: 100,		l: 85,	],
		[ name: "Navajo White",				rgb: "#FFDEAD",		h: 36,		s: 100,		l: 84,	],
		[ name: "Navy",						rgb: "#000080",		h: 240,		s: 100,		l: 25,	],
		[ name: "Old Lace",					rgb: "#FDF5E6",		h: 39,		s: 85,		l: 95,	],
		[ name: "Olive",					rgb: "#808000",		h: 60,		s: 100,		l: 25,	],
		[ name: "Olive Drab",				rgb: "#6B8E23",		h: 80,		s: 60,		l: 35,	],
		[ name: "Orange",					rgb: "#FFA500",		h: 39,		s: 100,		l: 50,	],
		[ name: "Orange Red",				rgb: "#FF4500",		h: 16,		s: 100,		l: 50,	],
		[ name: "Orchid",					rgb: "#DA70D6",		h: 302,		s: 59,		l: 65,	],
		[ name: "Pale Golden Rod",			rgb: "#EEE8AA",		h: 55,		s: 67,		l: 80,	],
		[ name: "Pale Green",				rgb: "#98FB98",		h: 120,		s: 93,		l: 79,	],
		[ name: "Pale Turquoise",			rgb: "#AFEEEE",		h: 180,		s: 65,		l: 81,	],
		[ name: "Pale Violet Red",			rgb: "#DB7093",		h: 340,		s: 60,		l: 65,	],
		[ name: "Papaya Whip",				rgb: "#FFEFD5",		h: 37,		s: 100,		l: 92,	],
		[ name: "Peach Puff",				rgb: "#FFDAB9",		h: 28,		s: 100,		l: 86,	],
		[ name: "Peru",						rgb: "#CD853F",		h: 30,		s: 59,		l: 53,	],
		[ name: "Pink",						rgb: "#FFC0CB",		h: 350,		s: 100,		l: 88,	],
		[ name: "Plum",						rgb: "#DDA0DD",		h: 300,		s: 47,		l: 75,	],
		[ name: "Powder Blue",				rgb: "#B0E0E6",		h: 187,		s: 52,		l: 80,	],
		[ name: "Purple",					rgb: "#800080",		h: 300,		s: 100,		l: 25,	],
		[ name: "Red",						rgb: "#FF0000",		h: 0,		s: 100,		l: 50,	],
		[ name: "Rosy Brown",				rgb: "#BC8F8F",		h: 0,		s: 25,		l: 65,	],
		[ name: "Royal Blue",				rgb: "#4169E1",		h: 225,		s: 73,		l: 57,	],
		[ name: "Saddle Brown",				rgb: "#8B4513",		h: 25,		s: 76,		l: 31,	],
		[ name: "Salmon",					rgb: "#FA8072",		h: 6,		s: 93,		l: 71,	],
		[ name: "Sandy Brown",				rgb: "#F4A460",		h: 28,		s: 87,		l: 67,	],
		[ name: "Sea Green",				rgb: "#2E8B57",		h: 146,		s: 50,		l: 36,	],
		[ name: "Sea Shell",				rgb: "#FFF5EE",		h: 25,		s: 100,		l: 97,	],
		[ name: "Sienna",					rgb: "#A0522D",		h: 19,		s: 56,		l: 40,	],
		[ name: "Silver",					rgb: "#C0C0C0",		h: 0,		s: 0,		l: 75,	],
		[ name: "Sky Blue",					rgb: "#87CEEB",		h: 197,		s: 71,		l: 73,	],
		[ name: "Slate Blue",				rgb: "#6A5ACD",		h: 248,		s: 53,		l: 58,	],
		[ name: "Slate Gray",				rgb: "#708090",		h: 210,		s: 13,		l: 50,	],
		[ name: "Snow",						rgb: "#FFFAFA",		h: 0,		s: 100,		l: 99,	],
		[ name: "Spring Green",				rgb: "#00FF7F",		h: 150,		s: 100,		l: 50,	],
		[ name: "Steel Blue",				rgb: "#4682B4",		h: 207,		s: 44,		l: 49,	],
		[ name: "Tan",						rgb: "#D2B48C",		h: 34,		s: 44,		l: 69,	],
		[ name: "Teal",						rgb: "#008080",		h: 180,		s: 100,		l: 25,	],
		[ name: "Thistle",					rgb: "#D8BFD8",		h: 300,		s: 24,		l: 80,	],
		[ name: "Tomato",					rgb: "#FF6347",		h: 9,		s: 100,		l: 64,	],
		[ name: "Turquoise",				rgb: "#40E0D0",		h: 174,		s: 72,		l: 56,	],
		[ name: "Violet",					rgb: "#EE82EE",		h: 300,		s: 76,		l: 72,	],
		[ name: "Wheat",					rgb: "#F5DEB3",		h: 39,		s: 77,		l: 83,	],
		[ name: "White Smoke",				rgb: "#F5F5F5",		h: 0,		s: 0,		l: 96,	],
		[ name: "Yellow",					rgb: "#FFFF00",		h: 60,		s: 100,		l: 50,	],
		[ name: "Yellow Green",				rgb: "#9ACD32",		h: 80,		s: 61,		l: 50,	],
	]
}