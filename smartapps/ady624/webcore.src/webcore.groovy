/**
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
 *
 *  Version history
*/

def String version() {	return "v0.0.001.20161130" }
/*
 *	11/30/2016 >>> v0.0.001.20161130 - ALPHA - Initial release
 */
 
/******************************************************************************/
/*** webCoRE DEFINITION														***/
/******************************************************************************/

 definition(
    name: "webCoRE",
    namespace: "ady624",
    author: "Adrian Caramaliu",
    description: "CoRE - Web Edition",
    category: "Convenience",
    singleInstance: true,
    iconUrl: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/app-CoRE.png",
    iconX2Url: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/app-CoRE@2x.png",
    iconX3Url: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/app-CoRE@2x.png"
 )


preferences {
	//common pages
	page(name: "pageMain")
	page(name: "pageViewVariable")
	page(name: "pageDeleteVariable")
	page(name: "pageRemove")

	//webCoRE pages
    page(name: "pageFinishInstall")
	page(name: "pageInitializeDashboard")
	page(name: "pageSelectDevices")
	page(name: "pageViewVariable")
	page(name: "pageDeleteVariable")
	page(name: "pageCreatePiston")
	page(name: "pageRemove")
    page(name: "pagePistonMain")
}


/******************************************************************************/
/*** CoRE CONSTANTS															***/
/******************************************************************************/


/******************************************************************************/
/*** 																		***/
/*** CONFIGURATION PAGES													***/
/*** 																		***/
/******************************************************************************/

/******************************************************************************/
/*** COMMON PAGES															***/
/******************************************************************************/
def pageMain() {
	if (!state.installed) {
    	return pageInstallWebCoRE()
    }
	//CoRE main page   
	dynamicPage(name: "pageMain", title: "", install: true, uninstall: false) {
        section() {
            if (!state.endpoint) {
                href "pageInitializeDashboard", title: "webCoRE Dashboard", description: "Tap here to initialize the webCoRE dashboard", image: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/icons/dashboard.png", required: false
            } else {
                def url = "${state.endpoint}dashboard"
                log.trace "Dashboard URL: $url *** DO NOT SHARE THIS LINK WITH ANYONE ***"
                href "", title: "webCoRE Dashboard", style: "external", url: url, image: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/icons/dashboard.png", required: false
            }
        }
        
        section() {
        	href "pageSelectDevices", title: "Available devices", description: "Tap here to select which devices are available to pistons" 
        }
        
        section() {
       // 	href "pageCreatePiston", title: "Add a new piston", description: "Tap here to add a new piston" 
        }
        
        def apps = getChildApps()
		section() {
        	for (app in apps) {
                def url = "${state.endpoint}piston/" + app.id
	        	href "pagePistonMain", title: app.label, description: "Piston " + app.id, required: false, params: [appId: app.id]
            }
			app( name: "pistons", title: "Add a CoRE piston...", appName: "webCoRE Piston", namespace: "ady624", multiple: true, install: false, uninstall: false, image: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/icons/piston.png")
		}

        section(title:"Application Info") {
            paragraph app.version(), title: "webCoRE Version", required: false
            label name: "name", title: "Name", state: (name ? "complete" : null), defaultValue: app.name, required: false
            href "pageGlobalVariables", title: "Global Variables", image: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/icons/variables.png", required: false
            href "pageStatistics", title: "Runtime Statistics", image: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/icons/statistics.png", required: false
        }

        section(title:"") {
            href "pageGeneralSettings", title: "Settings", image: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/icons/settings.png", required: false
        }
	}
    
}

private pageInstallWebCoRE() {
	//CoRE main page   
	dynamicPage(name: "pageMain", title: "", install: false, uninstall: false, nextPage: "pageInitializeDashboard") {
    	section() {
        	paragraph "Welcome to webCoRE!"
            paragraph "Let's go through the steps to setup webCoRE. First, let's configure the dashboard. You will need to setup OAuth in the SmartThings IDE for the webCoRE app. If you haven't done so already, please go to the SmartThings IDE, go to the SmartApps tab, select webCoRE and choose App Settings, then enable OAuth under the OAuth section."
            paragraph "Once you're ready, tap Next"
        }
    }
}

private pageInitializeDashboard() {
	//CoRE Dashboard initialization
	def success = initializeWebCoREEndpoint()
	dynamicPage(name: "pageInitializeDashboard", title: "", nextPage: state.installed && success ? null : "pageSelectDevices") {
		section() {
			if (success) {
				paragraph "Success! Your CoRE dashboard is now enabled. Tap ${state.installed ? "Done" : "Next"} to continue.", required: false
			} else {
				paragraph "Please go to your SmartThings IDE, select the My SmartApps section, click the 'Edit Properties' button of the CoRE app, open the OAuth section and click the 'Enable OAuth in Smart App' button. Click the Update button to finish.\n\nOnce finished, tap Done and try again.", title: "Please enable OAuth for CoRE", required: true, state: null
			}
		}
	}
}

private pageSelectDevices() {
	dynamicPage(name: "pageSelectDevices", title: "", nextPage: state.installed ? null : "pageFinishInstall") {
		section() {
        	paragraph "Please select all the devices that webCoRE should have access to. You will need to allow webCoRE access to a device in order to use it in any of its pistons."
            paragraph "NOTE: You can always come back here and select more devices, but know that removing devices that are currently in use by any piston(s) may yield unknown results."
            if (!state.installed) paragraph "When ready, tap Done to finish the install process. You can always access webCoRE from the Automations tab of your SmartThings app."
            def devices
        	for (capability in capabilities().findAll{ it.s != null }.sort{ it.s }) {
            	if (capability.s != devices) input "dev:${capability.n}", "capability.${capability.n}", multiple: true, title: "Which ${capability.s}", required: false
                devices = capability.s
            }
		}
	}
}

private pageFinishInstall() {
	dynamicPage(name: "pageFinishInstall", title: "", install: true) {
		section() {
        	paragraph "Excellent! You have now completed all the webCoRE installation steps and are ready to finish the installation."
            paragraph "You can now access webCoRE from the Automation tab of the SmartThings app, the SmartApps section."
            paragraph "Tap Done to finish and enjoy webCoRE!"
		}
	}
}

def pageViewVariable(params) {
	def var = params?.var
		dynamicPage(name: "pageViewVariable", title: "", uninstall: false, install: false) {
		if (var) {
			section() {
				paragraph var, title: "Variable name", required: false
				def value = getVariable(var)
				if (value == null) {
					paragraph "Undefined value (null)", title: "Oh-oh", required: false
				} else {
					def type = "string"
					if (value instanceof Boolean) {
						type = "boolean"
					} else if ((value instanceof Long) && (value >= 999999999999)) {
						type = "time"
					} else if ((value instanceof Float) || ((value instanceof String) && value.isFloat())) {
						type = "decimal"
					} else if ((value instanceof Integer) || ((value instanceof String) && value.isInteger())) {
						type = "number"
					}
					paragraph "$type", title: "Data type", required: false
					paragraph "$value", title: "Raw value", required: false
					value = getVariable(var, true)
					paragraph "$value", title: "Display value", required: false
				}
				if (!var.startsWith("\$")) {
					href "pageDeleteVariable", title: "Delete variable", description: "CAUTION: Tapping here will delete this variable and its value", params: [var: var], required: false
				}
			}
		} else {
			section() {
				paragraph "Sorry, variable not found.", required: false
			}
		}
	}
}

def pageDeleteVariable(params) {
	def var = params?.var
		dynamicPage(name: "pageInitializedVariable", title: "", uninstall: false, install: false) {
		if (var != null) {
			section() {
				deleteVariable(var)
				paragraph "Variable {$var} was successfully deleted.\n\nPlease tap < or Done to continue.", title: "Success", required: false
			}
		} else {
			section() {
				paragraph "Sorry, variable not found.", required: false
			}
		}
	}
}

def pageRemove() {
	dynamicPage(name: "pageRemove", title: "", install: false, uninstall: true) {
		section() {
			paragraph "CAUTION: You are about to completely remove CoRE and all of its pistons. This action is irreversible. If you are sure you want to do this, please tap on the Remove button below.", required: true, state: null
		}
	}
}

def pageCreatePiston() {
	dynamicPage(name: "pageCreatePiston", title: "", install: false, uninstall: true) {
       	def app = addChildApp("ady624", "webCoRE", "test")
        log.trace "Created app $app"
        log.trace "Created app ID ${app.id}"
		def url = "${state.endpoint}edit/${app.id}"
		section() {
			paragraph "created"
            href "", title: "Create a new webCoRE Piston", style: "external", url: url, image: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/icons/dashboard.png", required: false
		}
	}
}

def pagePistonMain(params) {
//log.trace params
	def appId = params?.appId ?: atomicState.currentAppId
    atomicState.currentAppId = appId
    def app = getChildApps().find{ it.id == appId }
    if (app) {
    	return app.pagePistonMain()
    } else {
    	dynamicPage(name: "pagePistonMain", title: "", install: false, uninstall: false) {
        	section() {
	        	paragraph "Sorry, an error has occurred. "
            }
        }
    }
}


private getDeviceListVersion() {
    def deviceListVersion = atomicState.deviceListVersion
    if (!(deviceListVersion instanceof Integer) && !(deviceListVersion instanceof Long) && !(deviceListVersion instanceof Float)) deviceListVersion = 0
    return version() + ".$deviceListVersion"
}

private incrementDeviceListVersion() {
    def deviceListVersion = atomicState.deviceListVersion
    if (!(deviceListVersion instanceof Integer) && !(deviceListVersion instanceof Long) && !(deviceListVersion instanceof Float)) deviceListVersion = 0
    atomicState.deviceListVersion = deviceListVersion + 1
}

private getDatabaseVersion() {
    def databaseVersion = atomicState.databaseVersion
    if (!(databaseVersion instanceof Integer) && !(databaseVersion instanceof Long) && !(databaseVersion instanceof Float)) databaseVersion = 0
    return version() + ".$databaseVersion"
}

private incrementDatabaseVersion() {
    def databaseVersion = atomicState.databaseVersion
    if (!(databaseVersion instanceof Integer) && !(databaseVersion instanceof Long) && !(databaseVersion instanceof Float)) databaseVersion = 0
    atomicState.databaseVersion = databaseVersion + 1
}



/******************************************************************************/
/*** 																		***/
/*** DASHBOARD MAPPINGS														***/
/*** 																		***/
/******************************************************************************/

mappings {
	path("/dashboard") {action: [GET: "api_dashboard"]}
	path("/init") {action: [GET: "api_getDashboardData"]}
	path("/piston") {action: [GET: "api_piston", POST: "api_piston"]}
	path("/ifttt/:eventName") {action: [GET: "api_ifttt", POST: "api_ifttt"]}
	path("/execute") {action: [POST: "api_execute"]}
	path("/execute/:pistonName") {action: [GET: "api_execute", POST: "api_execute"]}
	path("/tap") {action: [POST: "api_tap"]}
	path("/tap/:tapId") {action: [GET: "api_tap"]}
	path("/pause") {action: [POST: "api_pause"]}
	path("/resume") {action: [POST: "api_resume"]}
}

private api_dashboard(params) {
	def cdn = "https://core.caramaliu.com/webCoRE/dashboard"
    def theme = (settings["dashboardTheme"] ?: "default").toLowerCase()
	render contentType: "text/html", data: "<!DOCTYPE html><html lang=\"en\" ng-app=\"CoRE\"><base href=\"${state.endpoint}\"><head><meta charset=\"utf-8\"/><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"><link rel=\"stylesheet prefetch\" href=\"$cdn/$theme/css/components/components.min.css\"/><link rel=\"stylesheet prefetch\" href=\"$cdn/$theme/css/app.css\"/><script type=\"text/javascript\" src=\"$cdn/$theme/js/components/components.min.js\"></script><script type=\"text/javascript\" src=\"$cdn/$theme/js/app.js\"></script><script type=\"text/javascript\" src=\"$cdn/$theme/js/modules/dashboard.module.js\"></script><script type=\"text/javascript\" src=\"$cdn/$theme/js/modules/edit.module.js\"></script></head><body><ng-view></ng-view></body></html>"
}

private api_init() {
	def result = [now:now()]
    return result
}

private api_piston() {
	def data = request?.JSON
    def pistonId = data?.pistonId
    def databaseVersion = getDatabaseVersion()
    def clientDatabaseVersion = data?.databaseVersion
    def deviceListVersion = getDeviceListVersion()
    def clientDeviceListVersion = data?.deviceListVersion
    pistonId = 1
    if (pistonId) {
    	def result = [
        	now: now(),
	    	piston: [
	        	id: pistonId
	 		]
	    ]
        if (databaseVersion != clientDatabaseVersion) {
        	result.databaseVersion = databaseVersion
        	result.capabilities = capabilities()
            result.commands = commands()
            result.virtualCommands = virtualCommands()
            result.attributes = attributes()
            result.colors = colors()
        }
        if (deviceListVersion != clientDeviceListVersion) {
        	result.deviceListVersion = deviceListVersion
            result.devices = []
            def devices = [:]
            for (devs in settings.findAll{ it.key.startsWith("dev:") }) {
                for(dev in devs.value) {
                    devices[dev.id] = [n: dev.getDisplayName(), c: dev.getCapabilities()*.name, a: dev.getSupportedAttributes()*.name, o: dev.getSupportedCommands()*.name]
                }
            }
            for(device in devices.sort{ it.value.n }) {
                result.devices.push([ id: device.key ] + device.value)
            }
        }
		return result
    }
}

private initializeWebCoREEndpoint() {
	if (!state.endpoint) {
		try {
			def accessToken = createAccessToken()
			if (accessToken) {
				state.endpoint = apiServerUrl("/api/token/${accessToken}/smartapps/installations/${app.id}#/")
			}
		} catch(e) {
			state.endpoint = null
		}
	}
	return state.endpoint
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
	state.installed = true
    incrementDeviceListVersion()
    incrementDatabaseVersion()
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

private generatePistonName() {
	def apps = getChildApps()
	def i = 1
	while (true) {
		def name = i == 5 ? "Mambo No. 5" : "webCoRE Piston #$i"
		def found = false
		for (app in apps) {
			if (app.label == name) {
				found = true
				break
			}
		}
		if (found) {
				i++
			continue
		}
		return name
	}
}




/******************************************************************************/
/*** DATABASE																***/
/******************************************************************************/

private capabilities() {
	return [
		[ n: "accelerationSensor",		d: "Acceleration Sensor",		a: "acceleration",								m: true,			s: "acceleration sensors",	],
		[ n: "alarm",				d: "Alarm",				a: "alarm",			c: ["off", "strobe", "siren", "both"],										m: true,			s: "sirens",			],
		[ n: "askAlexaMacro",			d: "Ask Alexa Macro",			a: "askAlexaMacro",																				m: true,			vd: "location",	vdn: "Ask Alexa Macro"	],
		[ n: "audioNotification",		d: "Audio Notification",						c: ["playText", "playSoundAndTrack", "playText", "playTextAndResume", "playTextAndRestore", "playTrack", "playTrackAndResume", "playTrackAndRestore", "playTrackAtVolume"],	m: true,			s: "audio notification devices", ],
		[ n: "doorControl",			d: "Automatic Door",			a: "door",			c: ["open", "close"],														m: true,			s: "doors",			],
		[ n: "garageDoorControl",		d: "Automatic Garage Door",		a: "door",			c: ["open", "close"],														m: true,			s: "garage doors",		],
		[ n: "battery",				d: "Battery",				a: "battery",									m: true,			s: "battery powered devices",	],
		[ n: "beacon",				d: "Beacon",				a: "presence",									m: true,			s: "beacons",	],
		[ n: "switch",				d: "Bulb",				a: "switch",			c: ["on", "off"],															m: true,			s: "lights", 			],
		[ n: "button",				d: "Button",				a: "button",									m: true,			s: "buttons",			count: "numberOfButtons,numButtons", data: "buttonNumber", momentary: true],
		[ n: "imageCapture",			d: "Camera",				a: "image",			c: ["take"],																	m: true,			s: "cameras",			],
		[ n: "carbonDioxideMeasurement",	d: "Carbon Dioxide Measurement",	a: "carbonDioxide",								m: true,			s: "carbon dioxide sensors",	],
		[ n: "carbonMonoxideDetector",		d: "Carbon Monoxide Detector",		a: "carbonMonoxide",								m: true,			s: "carbon monoxide detectors",	],
		[ n: "colorControl",			d: "Color Control",			a: "color",			c: ["setColor", "setHue", "setSaturation"],									m: true,			s: "RGB/W lights"		],
		[ n: "colorTemperature",		d: "Color Temperature",			a: "colorTemperature",		c: ["setColorTemperature"],													m: true,			s: "RGB/W lights",	],
		[ n: "configure",			d: "Configure",								c: ["configure"],															m: true,			s: "configurable devices",	],
		[ n: "consumable",			d: "Consumable",			a: "consumable",		c: ["setConsumableStatus"],													m: true,			s: "consumables",	],
		[ n: "contactSensor",			d: "Contact Sensor",			a: "contact",									m: true,			s: "contact sensors",	],
		[ n: "piston",				d: "CoRE Piston",			a: "piston",			c: ["executePiston"],														m: true,			vd: "location",	vdn: "Piston"	],
		[ n: "dateAndTime",			d: "Date & Time",			a: "time",			c: null, /* wish we could control time */									m: true,			, vd: "time",		vdn: "Date & Time"	],
		[ n: "switchLevel",			d: "Dimmable Light",			a: "level",			c: ["setLevel"],																m: true,			s: "dimmable lights",	],
		[ n: "switchLevel",			d: "Dimmer",				a: "level",			c: ["setLevel"],																m: true,			s: "dimmers",			],
		[ n: "energyMeter",			d: "Energy Meter",			a: "energy",									m: true,			s: "energy meters"],
		[ n: "ifttt",				d: "IFTTT",				a: "ifttt",																								m: false,		vd: "location",	vdn: "IFTTT"	],
		[ n: "illuminanceMeasurement",		d: "Illuminance Measurement",		a: "illuminance",								m: true,			s: "illuminance sensors",	],
		[ n: "imageCapture",			d: "Image Capture",			a: "image",			c: ["take"],																	m: true,			s: "cameras"],
		[ n: "indicator",			d: "Indicator",				a: "indicatorStatus",								m: true,			s: "indicator devices"],
		[ n: "waterSensor",			d: "Leak Sensor",			a: "water",									m: true,			s: "leak sensors",	],
		[ n: "switch",				d: "Light bulb",			a: "switch",			c: ["on", "off"],															m: true,			s: "lights", 			],
		[ n: "locationMode",			d: "Location Mode",			a: "mode",			c: ["setMode"],																m: false,		s: "location", vd: "location"	],
		[ n: "lock",				d: "Lock",				a: "lock",			c: ["lock", "unlock"],				m: true,			s: "electronic locks",	ct: "numberOfCodes,numCodes", dt: "usedCode", sd: "By user code", ],
		[ n: "mediaController",			d: "Media Controller",			a: "currentActivity",		c: ["startActivity", "getAllActivities", "getCurrentActivity"],	m: true,	s: "media controllers"		],
		[ n: "locationMode",			d: "Mode",				a: "mode",			c: ["setMode"],					m: false,		s: "location", vd: "location"		],
		[ n: "momentary",			d: "Momentary",								c: ["push"],					m: true,			s: "momentary switches"		],
		[ n: "motionSensor",			d: "Motion Sensor",			a: "motion",									m: true,			s: "motion sensors",		],
		[ n: "musicPlayer",			d: "Music Player",			a: "status",			c: ["play", "pause", "stop", "nextTrack", "playTrack", "setLevel", "playText", "mute", "previousTrack", "unmute", "setTrack", "resumeTrack", "restoreTrack"],	m: true,	s: "music players", ],
		[ n: "notification",			d: "Notification",							c: ["deviceNotification"],			m: true,			s: "notification devices",	],
		[ n: "pHMeasurement",			d: "pH Measurement",			a: "pH",									m: true,			s: "pH sensors",		],
		[ n: "occupancy",			d: "Occupancy",				a: "occupancy",									m: true,			s: "occupancy detectors",	],
		[ n: "switch",				d: "Outlet",				a: "switch",			c: ["on", "off"],				m: true,			s: "outlets",			],
		[ n: "piston",				d: "Piston",				a: "piston",			c: ["executePiston"],				m: true,			vd: "location",	vdn: "Piston"	],
		[ n: "polling",				d: "Polling",								c: ["poll"],					m: true,			s: "pollable devices",		],
		[ n: "powerMeter",			d: "Power Meter",			a: "power",									m: true,			s: "power meters",		],
		[ n: "power",				d: "Power",				a: "powerSource",								m: true,			s: "powered devices",		],
		[ n: "presenceSensor",			d: "Presence Sensor",			a: "presence",									m: true,			s: "presence sensors",		],
		[ n: "refresh",				d: "Refresh",								c: ["refresh"],					m: true,			s: "refreshable devices",	],
		[ n: "relativeHumidityMeasurement",	d: "Relative Humidity Measurement",	a: "humidity",									m: true,			s: "humidity sensors",		],
		[ n: "relaySwitch",			d: "Relay Switch",			a: "switch",			c: ["on", "off"],				m: true,			s: "relays",			],
		[ n: "routine",				d: "Routine",				a: "routineExecuted",		c: ["executeRoutine"],				m: true,			vd: "location",	vdn:"Routine"	],
		[ n: "sensor",				d: "Sensor",				a: "sensor",									m: true,			s: "sensors",			],
		[ n: "shockSensor",			d: "Shock Sensor",			a: "shock",									m: true,			s: "shock sensors",		],
		[ n: "signalStrength",			d: "Signal Strength",			a: "lqi",									m: true,			s: "wireless devices",		],
		[ n: "alarm",				d: "Siren",				a: "alarm",			c: ["off", "strobe", "siren", "both"],		m: true,			s: "sirens",			],
		[ n: "sleepSensor",			d: "Sleep Sensor",			a: "sleeping",									m: true,			s: "sleep sensors",		],
		[ n: "smartHomeMonitor",		d: "Smart Home Monitor",		a: "alarmSystemStatus",		c: ["setAlarmSystemStatus"],			m: true,			, vd: "location",	vdn:"Smart Home Monitor"	],
		[ n: "smokeDetector",			d: "Smoke Detector",			a: "smoke",									m: true,			s: "smoke detectors",		],
		[ n: "soundSensor",			d: "Sound Sensor",			a: "sound",									m: true,			s: "sound sensors",		],
		[ n: "speechSynthesis",			d: "Speech Synthesis",							c: ["speak"],					m: true,			s: "speech synthesizers", 	],
		[ n: "stepSensor",			d: "Step Sensor",			a: "steps",									m: true,			s: "step sensors",		],
		[ n: "switch",				d: "Switch",				a: "switch",			c: ["on", "off"],				m: true,			s: "switches",			],
		[ n: "switchLevel",			d: "Switch Level",			a: "level",			c: ["setLevel"],				m: true,			s: "dimmers"			],
		[ n: "soundPressureLevel",		d: "Sound Pressure Level",		a: "soundPressureLevel",							m: true,			s: "sound pressure sensors",	],
		[ n: "consumable",			d: "Stock Management",			a: "consumable",								m: true,			s: "consumables",		],
		[ n: "tamperAlert",			d: "Tamper Alert",			a: "tamper",									m: true,			s: "tamper sensors",		],
		[ n: "temperatureMeasurement",		d: "Temperature Measurement",		a: "temperature",								m: true,			s: "temperature sensors",	],
		[ n: "thermostat",			d: "Thermostat",			a: "temperature",		c: ["setHeatingSetpoint", "setCoolingSetpoint", "off", "heat", "emergencyHeat", "cool", "setThermostatMode", "fanOn", "fanAuto", "fanCirculate", "setThermostatFanMode", "auto"],	m: true,	s: "thermostats",	sa: true],
		[ n: "thermostatCoolingSetpoint",	d: "Thermostat Cooling Setpoint",	a: "coolingSetpoint",		c: ["setCoolingSetpoint"],			m: true,							],
		[ n: "thermostatFanMode",		d: "Thermostat Fan Mode",		a: "thermostatFanMode",		c: ["fanOn", "fanAuto", "fanCirculate", "setThermostatFanMode"],m: true,	s: "fans",			],
		[ n: "thermostatHeatingSetpoint",	d: "Thermostat Heating Setpoint",	a: "heatingSetpoint",		c: ["setHeatingSetpoint"],			m: true,							],
		[ n: "thermostatMode",			d: "Thermostat Mode",			a: "thermostatMode",		c: ["off", "heat", "emergencyHeat", "cool", "auto", "setThermostatMode"],	m: true,			],
		[ n: "thermostatOperatingState",	d: "Thermostat Operating State",	a: "thermostatOperatingState",							m: true,							],
		[ n: "thermostatSetpoint",		d: "Thermostat Setpoint",		a: "thermostatSetpoint",							m: true,			],
		[ n: "threeAxis",			d: "Three Axis Sensor",			a: "orientation",								m: true,			s: "three axis sensors",	],
		[ n: "dateAndTime",			d: "Time",				a: "time",									m: true,			, vd: "time",		vdn:"Date & Time"	],
		[ n: "timedSession",			d: "Timed Session",			a: "sessionStatus",		c: ["setTimeRemaining", "start", "stop", "pause", "cancel"],	m: true,	s: "timed sessions",		],
		[ n: "tone",				d: "Tone Generator",							c: ["beep"],					m: true,			s: "tone generators",		],
		[ n: "touchSensor",			d: "Touch Sensor",			a: "touch",									m: true,							],
		[ n: "valve",				d: "Valve",				a: "contact",			c: ["open", "close"],				m: true,			s: "valves",			],
		[ n: "variable",			d: "Variable",				a: "variable",			c: ["setVariable"],				m: true,			vd: "location",	vdn:"Variable"	],
		[ n: "voltageMeasurement",		d: "Voltage Measurement",		a: "voltage",									m: true,			s: "volt meters",		],
		[ n: "waterSensor",			d: "Water Sensor",			a: "water",									m: true,			s: "leak sensors",		],
		[ n: "windowShade",			d: "Window Shade",			a: "windowShade",		c: ["open", "close", "presetPosition"],		m: true,			s: "window shades",		],
	]
}

private commands() {
	def tempUnit = "°" + location.temperatureScale
    def defGroup = "Control [devices]"
	return [
		[ n: "locationMode.setMode",			d: "Set location mode",																							],
		[ n: "smartHomeMonitor.setAlarmSystemStatus",	d: "Set Smart Home Monitor status",																					],
		[ n: "on",					d: "Turn on", 				a: "switch",		v: "on",																],
		[ n: "on1",					d: "Turn on #1", 			a: "switch1",		v: "on",																],
		[ n: "on2",					d: "Turn on #2", 			a: "switch2",		v: "on",																],
		[ n: "on3",					d: "Turn on #3", 			a: "switch3",		v: "on",																],
		[ n: "on4",					d: "Turn on #4", 			a: "switch4",		v: "on",																],
		[ n: "on5",					d: "Turn on #5", 			a: "switch5",		v: "on",																],
		[ n: "on6",					d: "Turn on #6", 			a: "switch6",		v: "on",																],
		[ n: "on7",					d: "Turn on #7", 			a: "switch7",		v: "on",																],
		[ n: "on8",					d: "Turn on #8", 			a: "switch8",		v: "on",																],
		[ n: "off",					d: "Turn off",				a: "switch",		v: "off",																],
		[ n: "off1",					d: "Turn off #1",			a: "switch1",		v: "off",																],
		[ n: "off2",					d: "Turn off #2",			a: "switch2",		v: "off",																],
		[ n: "off3",					d: "Turn off #3",			a: "switch3",		v: "off",																],
		[ n: "off4",					d: "Turn off #4",			a: "switch4",		v: "off",																],
		[ n: "off5",					d: "Turn off #5",			a: "switch5",		v: "off",																],
		[ n: "off6",					d: "Turn off #6",			a: "switch6",		v: "off",																],
		[ n: "off7",					d: "Turn off #7",			a: "switch7",		v: "off",																],
		[ n: "off8",					d: "Turn off #8",			a: "switch8",		v: "off",																],
		[ n: "toggle",					d: "Toggle",																								],
		[ n: "toggle1",					d: "Toggle #1",																								],
		[ n: "toggle2",					d: "Toggle #1",																								],
		[ n: "toggle3",					d: "Toggle #1",																								],
		[ n: "toggle4",					d: "Toggle #1",																								],
		[ n: "toggle5",					d: "Toggle #1",																								],
		[ n: "toggle6",					d: "Toggle #1",																								],
		[ n: "toggle7",					d: "Toggle #1",																								],
		[ n: "toggle8",					d: "Toggle #1",																								],
		[ n: "setColor",				d: "Set color",				a: "color",		v: "*|color",		p: [[n:"Color",t:"color"]], 											],
		[ n: "setLevel",				d: "Set level",				a: "level",		v: "*|number",		p: [[n:"Level",t:"level"]],				dd: "Set level to {0}%",				],
		[ n: "setHue",					d: "Set hue",				a: "hue",		v: "*|number",		p: [[n:"Hue",t:"hue"]],					dd: "Set hue to {0}°",					],
		[ n: "setSaturation",				d: "Set saturation",			a: "saturation",	v: "*|number",		p: [[n:"Saturation",t:"saturation"]],			dd: "Set saturation to {0}%",				],
		[ n: "setColorTemperature",			d: "Set color temperature",		a: "colorTemperature",	v: "*|number",		p: [[n:"Color Temperature",t:"colorTemperature"]],	dd: "Set color temperature to {0}°K",			],
		[ n: "open",					d: "Open",				a: "door",		v: "open",																],
		[ n: "close",					d: "Close",				a: "door",		v: "close",																],
		[ n: "windowShade.open",			d: "Open fully",																							],
		[ n: "windowShade.close",			d: "Close fully",																							],
		[ n: "windowShade.presetPosition",		d: "Move to preset position",																						],
		[ n: "lock",					d: "Lock",				a: "lock",		v: "locked",																],
		[ n: "unlock",					d: "Unlock",				a: "lock",		v: "unlocked",																],
		[ n: "take",					d: "Take a picture",																							],
		[ n: "alarm.off",				d: "Stop",				a: "alarm",		v: "off",																],
		[ n: "alarm.strobe",				d: "Strobe",				a: "alarm",		v: "strobe",																],
		[ n: "alarm.siren",				d: "Siren",				a: "alarm",		v: "siren",																],
		[ n: "alarm.both",				d: "Strobe and Siren",			a: "alarm",		v: "both",																],
		[ n: "thermostat.off",				d: "Set to Off",			a: "thermostatMode",	v: "off",																],
		[ n: "thermostat.heat",				d: "Set to Heat",			a: "thermostatMode",	v: "heat",																],
		[ n: "thermostat.cool",				d: "Set to Cool",			a: "thermostatMode",	v: "cool",																],
		[ n: "thermostat.auto",				d: "Set to Auto",			a: "thermostatMode",	v: "auto",																],
		[ n: "thermostat.emergencyHeat",		d: "Set to Emergency Heat",		a: "thermostatMode",	v: "emergencyHeat",															],
		[ n: "thermostat.quickSetHeat",			d: "Quick set heating point",								p: ["Desired temperature:thermostatSetpoint"],		dd: "Set quick heating point at {0}$tempUnit",		],
		[ n: "thermostat.quickSetCool",			d: "Quick set cooling point",								p: ["Desired temperature:thermostatSetpoint"],		dd: "Set quick cooling point at {0}$tempUnit",		],
		[ n: "thermostat.setHeatingSetpoint",		d: "Set heating point",			a: "thermostatHeatingSetpoint",	v: "*|decimal",	p: ["Desired temperature:thermostatSetpoint"],		dd: "Set heating point at {0}$tempUnit",		],
		[ n: "thermostat.setCoolingSetpoint",		d: "Set cooling point",			a: "thermostatCoolingSetpoint",	v: "*|decimal",	p: ["Desired temperature:thermostatSetpoint"],		dd: "Set cooling point at {0}$tempUnit",		],
		[ n: "thermostat.setThermostatMode",		d: "Set thermostat mode",		a: "thermostatMode",	v: "*|string",		p: ["Mode:thermostatMode"],				dd: "Set thermostat mode to {0}",			],
		[ n: "fanOn",					d: "Set fan to On",																							],
		[ n: "fanCirculate",				d: "Set fan to Circulate",																						],
		[ n: "fanAuto",					d: "Set fan to Auto",																							],
		[ n: "setThermostatFanMode",			d: "Set fan mode",									p: ["Fan mode:thermostatFanMode"],			dd: "Set fan mode to {0}",				],
		[ n: "play",					d: "Play",																								],
		[ n: "pause",					d: "Pause",																								],
		[ n: "stop",					d: "Stop",																								],
		[ n: "nextTrack",				d: "Next track",																							],
		[ n: "previousTrack",				d: "Previous track",																							],
		[ n: "mute",					d: "Mute",																								],
		[ n: "unmute",					d: "Unmute",																								],
		[ n: "musicPlayer.setLevel",			d: "Set volume",									p: ["Level:level"], dd: "Set volume to {0}%",									],
		[ n: "playText",				d: "Speak text",									p: ["Text:string", "?Volume:level"], 			dd: "Speak text \"{0}\" at volume {1}", 		],
		[ n: "playTextAndRestore",			d: "Speak text and restore",								p: ["Text:string","?Volume:level"],			dd: "Speak text \"{0}\" at volume {1} and restore",	],
		[ n: "playTextAndResume",			d: "Speak text and resume",								p: ["Text:string","?Volume:level"],			dd: "Speak text \"{0}\" at volume {1} and resume",	],
		[ n: "playTrack",				d: "Play track",									p: ["Track URI:string","?Volume:level"],		dd: "Play track \"{0}\" at volume {1}",			],
		[ n: "playTrackAtVolume",			d: "Play track at volume",								p: ["Track URI:string","Volume:level"],			dd: "Play track \"{0}\" at volume {1}",			],
		[ n: "playTrackAndRestore",			d: "Play track and restore",								p: ["Track URI:string","?Volume:level"], 		dd: "Play track \"{0}\" at volume {1} and restore", 	],
		[ n: "playTrackAndResume",			d: "Play track and resume",								p: ["Track URI:string","?Volume:level"], 		dd: "Play track \"{0}\" at volume {1} and resume", 	],
		[ n: "setTrack",				d: "Set track",										p: ["Track URI:string"],				dd: "Set track to \"{0}\"",				],
		[ n: "setLocalLevel",				d: "Set local level",									p: ["Level:level"],					dd: "Set local level to {0}", 				],
		[ n: "resumeTrack",				d: "Resume track",																							],
		[ n: "restoreTrack",				d: "Restore track",																							],
		[ n: "speak",					d: "Speak",										p: ["Message:string"],					dd: "Speak \"{0}\"", 					],
		[ n: "startActivity",				d: "Start activity",									p: ["Activity:string"],					dd: "Start activity\"{0}\"",				],
		[ n: "getCurrentActivity",			d: "Get current activity",																						],
		[ n: "getAllActivities",			d: "Get all activities",																						],
		[ n: "push",					d: "Push",																								],
		[ n: "beep",					d: "Beep",																								],
		[ n: "timedSession.setTimeRemaining",		d: "Set remaining time",								p: ["Remaining time [s]:number"],			dd: "Set remaining time to {0}s",			],
		[ n: "timedSession.start",			d: "Start timed session",																						],
		[ n: "timedSession.stop",			d: "Stop timed session",																						],
		[ n: "timedSession.pause",			d: "Pause timed session",																						],
		[ n: "timedSession.cancel",			d: "Cancel timed session",																						],
		[ n: "setConsumableStatus",			d: "Set consumable status",								p: ["Status:consumable"],				dd: "Set consumable status to {0}",			],
		[ n: "configure",				d: "Configure",																								],
		[ n: "poll",					d: "Poll",																								],
		[ n: "refresh",					d: "Refresh",																								],
		/* predfined commands below */
		//general
		[ n: "reset",					d: "Reset",																								],
		//hue
		[ n: "startLoop",				d: "Start color loop",																							],
		[ n: "stopLoop",				d: "Stop color loop",																							],
		[ n: "setLoopTime",				d: "Set loop duration",									p: ["Duration [s]:number[1..*]"],			dd: "Set loop duration to {0}s"				],
		[ n: "setDirection",				d: "Switch loop direction",															dd: "Set loop duration to {0}s"				],
		[ n: "alert",					d: "Alert with lights",									p: ["Method:enum[Blink,Breathe,Okay,Stop]"],		dd: "Alert with lights: {0}"				],
		[ n: "setAdjustedColor",			d: "Transition to color",								p: ["Color:color","Duration [s]:number[1..60]"],	dd: "Transition to color {0} in {1}s"			],
		//harmony
		[ n: "allOn",					d: "Turn all on",																							],
		[ n: "allOff",					d: "Turn all off",																							],
		[ n: "hubOn",					d: "Turn hub on",																							],
		[ n: "hubOff",					d: "Turn hub off",																							],
		//blink camera
		[ n: "enableCamera",				d: "Enable camera",																							],
		[ n: "disableCamera",				d: "Disable camera",																							],
		[ n: "monitorOn",				d: "Turn monitor on",																							],
		[ n: "monitorOff",				d: "Turn monitor off",																							],
		[ n: "ledOn",					d: "Turn LED on",																							],
		[ n: "ledOff",					d: "Turn LED off",																							],
		[ n: "ledAuto",					d: "Set LED to Auto",																							],
		[ n: "setVideoLength",				d: "Set video length",									p: ["Seconds:number[1..10]"],				dd: "Set video length to {0}s", 			],
		//dlink camera
		[ n: "pirOn",					d: "Enable PIR motion detection",																					],
		[ n: "pirOff",					d: "Disable PIR motion detection",																					],
		[ n: "nvOn",					d: "Set Night Vision to On",																						],
		[ n: "nvOff",					d: "Set Night Vision to Off",																						],
		[ n: "nvAuto",					d: "Set Night Vision to Auto",																						],
		[ n: "vrOn",					d: "Enable local video recording",																					],
		[ n: "vrOff",					d: "Disable local video recording",																					],
		[ n: "left",					d: "Pan camera left",																							],
		[ n: "right",					d: "Pan camera right",																							],
		[ n: "up",					d: "Pan camera up",																							],
		[ n: "down",					d: "Pan camera down",																							],
		[ n: "home",					d: "Pan camera to the Home",																						],
		[ n: "presetOne",				d: "Pan camera to preset #1",																						],
		[ n: "presetTwo",				d: "Pan camera to preset #2",																						],
		[ n: "presetThree",				d: "Pan camera to preset #3",																						],
		[ n: "presetFour",				d: "Pan camera to preset #4",																						],
		[ n: "presetFive",				d: "Pan camera to preset #5",																						],
		[ n: "presetSix",				d: "Pan camera to preset #6",																						],
		[ n: "presetSeven",				d: "Pan camera to preset #7",																						],
		[ n: "presetEight",				d: "Pan camera to preset #8",																						],
		[ n: "presetCommand",				d: "Pan camera to custom preset",							p: ["Preset #:number[1..99]"],				dd: "Pan camera to preset #{0}",			],
        //zwave fan speed control by @pmjoen
		[ n: "low",					d: "Set to Low"																								],
		[ n: "med",					d: "Set to Medium"																							],
		[ n: "high",					d: "Set to High"],
	]
}

private virtualCommands() {
	def cmds = [
		[ n: "wait",									d: "Wait",							p: [[n:"Time",t:"number",r:[1,1440]],[n:"Unit",t:"enum",o:["seconds","minutes","hours"]]],													immediate: true,	l: true,	dd: "Wait {0} {1}",	],
		[ n: "waitRandom",								d: "Wait (random)",					p: ["At least:number[1..1440]","At most:number[1..1440]","Unit:enum[seconds,minutes,hours]"],	immediate: true,	l: true,	dd: "Wait {0}-{1} {2}",	],
		[ n: "waitState",								d: "Wait for piston state change",	p: ["Change to:enum[any,false,true]"],															immediate: true,	l: true,						dd: "Wait for {0} state"],
		[ n: "waitTime",								d: "Wait for time",			p: ["Time:enum[midnight,sunrise,noon,sunset]","?Offset [minutes]:number[-1440..1440]","Days of week:enums[Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday]"],							immediate: true,	l: true,						dd: "Wait for next {0} (offset {1} min), on {2}"],
		[ n: "toggle",				r: ["on", "off"], 			d: "Toggle",		],
		[ n: "toggle#1",			r: ["on1", "off1"], 			d: "Toggle #1",		],
		[ n: "toggle#2",			r: ["on2", "off2"], 			d: "Toggle #2",		],
		[ n: "toggle#3",			r: ["on3", "off3"], 			d: "Toggle #3",		],
		[ n: "toggle#4",			r: ["on4", "off4"], 			d: "Toggle #4",		],
		[ n: "toggle#5",			r: ["on5", "off5"], 			d: "Toggle #5",		],
		[ n: "toggle#6",			r: ["on6", "off6"], 			d: "Toggle #6",		],
		[ n: "toggle#7",			r: ["on7", "off7"], 			d: "Toggle #7",		],
		[ n: "toggle#8",			r: ["on8", "off8"], 			d: "Toggle #8",		],
		[ n: "toggleLevel",			r: ["on", "off", "setLevel"],		d: "Toggle level",					p: ["Level:level"],																																	dd: "Toggle level between 0% and {0}%",	],
		[ n: "delayedOn",			r: ["on"], 				d: "Turn on (delayed)",				p: ["Delay (ms):number[1..60000]"],																													dd: "Turn on after {0}ms",	],
		[ n: "delayedOn#1",			r: ["on1"], 				d: "Turn on #1 (delayed)",			p: ["Delay (ms):number[1..60000]"],																													dd: "Turn on #1 after {0}ms",	],
		[ n: "delayedOn#2",			r: ["on2"], 				d: "Turn on #2 (delayed)",			p: ["Delay (ms):number[1..60000]"],																													dd: "Turn on #2 after {0}ms",	],
		[ n: "delayedOn#3",			r: ["on3"], 				d: "Turn on #3 (delayed)",			p: ["Delay (ms):number[1..60000]"],																													dd: "Turn on #3 after {0}ms",	],
		[ n: "delayedOn#4",			r: ["on4"], 				d: "Turn on #4 (delayed)",			p: ["Delay (ms):number[1..60000]"],																													dd: "Turn on #4 after {0}ms",	],
		[ n: "delayedOn#5",			r: ["on5"], 				d: "Turn on #5 (delayed)",			p: ["Delay (ms):number[1..60000]"],																													dd: "Turn on #5 after {0}ms",	],
		[ n: "delayedOn#6",			r: ["on6"], 				d: "Turn on #6 (delayed)",			p: ["Delay (ms):number[1..60000]"],																													dd: "Turn on #6 after {0}ms",	],
		[ n: "delayedOn#7",			r: ["on7"], 				d: "Turn on #7 (delayed)",			p: ["Delay (ms):number[1..60000]"],																													dd: "Turn on #7 after {0}ms",	],
		[ n: "delayedOn#8",			r: ["on8"], 				d: "Turn on #8 (delayed)",			p: ["Delay (ms):number[1..60000]"],																													dd: "Turn on #8 after {0}ms",	],
		[ n: "delayedOff",			r: ["off"], 				d: "Turn off (delayed)",				p: ["Delay (ms):number[1..60000]"],																													dd: "Turn off after {0}ms",	],
		[ n: "delayedOff#1",			r: ["off1"],				d: "Turn off #1 (delayed)",			p: ["Delay (ms):number[1..60000]"],																													dd: "Turn off #1 after {0}ms",	],
		[ n: "delayedOff#2",			r: ["off2"],				d: "Turn off #2 (delayed)",			p: ["Delay (ms):number[1..60000]"],																													dd: "Turn off #2 after {0}ms",	],
		[ n: "delayedOff#3",			r: ["off3"],				d: "Turn off #3 (delayed)",			p: ["Delay (ms):number[1..60000]"],																													dd: "Turn off #3 after {0}ms",	],
		[ n: "delayedOff#4",			r: ["off4"],				d: "Turn off #4 (delayed)",			p: ["Delay (ms):number[1..60000]"],																													dd: "Turn off #4 after {0}ms",	],
		[ n: "delayedOff#5",			r: ["off5"],				d: "Turn off #5 (delayed)",			p: ["Delay (ms):number[1..60000]"],																													dd: "Turn off #5 after {0}ms",	],
		[ n: "delayedOff#6",			r: ["off7"],				d: "Turn off #6 (delayed)",			p: ["Delay (ms):number[1..60000]"],																													dd: "Turn off #6 after {0}ms",	],
		[ n: "delayedOff#7",			r: ["off7"],				d: "Turn off #7 (delayed)",			p: ["Delay (ms):number[1..60000]"],																													dd: "Turn off #7 after {0}ms",	],
		[ n: "delayedOff#8",			r: ["off8"],				d: "Turn off #8 (delayed)",			p: ["Delay (ms):number[1..60000]"],																													dd: "Turn off #8 after {0}ms",	],
		[ n: "delayedToggle",			r: ["on", "off"], 			d: "Toggle (delayed)",				p: ["Delay (ms):number[1..60000]"],																													dd: "Toggle after {0}ms",	],
		[ n: "delayedToggle#1",			r: ["on1", "off1"], 			d: "Toggle #1 (delayed)",				p: ["Delay (ms):number[1..60000]"],																													dd: "Toggle #1 after {0}ms",	],
		[ n: "delayedToggle#2",			r: ["on2", "off2"], 			d: "Toggle #2 (delayed)",				p: ["Delay (ms):number[1..60000]"],																													dd: "Toggle #2 after {0}ms",	],
		[ n: "delayedToggle#3",			r: ["on3", "off3"], 			d: "Toggle #3 (delayed)",				p: ["Delay (ms):number[1..60000]"],																													dd: "Toggle #3 after {0}ms",	],
		[ n: "delayedToggle#4",			r: ["on4", "off4"], 			d: "Toggle #4 (delayed)",				p: ["Delay (ms):number[1..60000]"],																													dd: "Toggle #4 after {0}ms",	],
		[ n: "delayedToggle#5",			r: ["on5", "off5"], 			d: "Toggle #5 (delayed)",				p: ["Delay (ms):number[1..60000]"],																													dd: "Toggle #5 after {0}ms",	],
		[ n: "delayedToggle#6",			r: ["on6", "off6"], 			d: "Toggle #6 (delayed)",				p: ["Delay (ms):number[1..60000]"],																													dd: "Toggle #6 after {0}ms",	],
		[ n: "delayedToggle#7",			r: ["on7", "off7"], 			d: "Toggle #7 (delayed)",				p: ["Delay (ms):number[1..60000]"],																													dd: "Toggle #7 after {0}ms",	],
		[ n: "delayedToggle#8",			r: ["on8", "off8"], 			d: "Toggle #8 (delayed)",				p: ["Delay (ms):number[1..60000]"],																													dd: "Toggle #8 after {0}ms",	],
		[ n: "setLevelVariable",		r: ["setLevel"],			d: "Set level (variable)",						p: ["Level:variable"], dd: "Set level to {0}%"],
		[ n: "setSaturationVariable",		r: ["setSaturation"],			d: "Set saturation (variable)",						p: ["Saturation:variable"], dd: "Set saturation to {0}%"],
		[ n: "setHueVariable",			r: ["setHue"],				d: "Set hue (variable)",						p: ["Hue:variable"], dd: "Set hue to {0}°"],
		[ n: "fadeLevelHW",			r: ["setLevel"], 			d: "Fade to level (hardware)",		p: ["Target level:level","Duration (ms):number[1..60000]"],																							dd: "Fade to {0}% in {1}ms",				],
		[ n: "fadeLevel",			r: ["setLevel"], 			d: "Fade to level",					p: ["?Start level (optional):level","Target level:level","Duration (seconds):number[1..600]"],															dd: "Fade level from {0}% to {1}% in {2}s",				],
		[ n: "fadeLevelVariable",		r: ["setLevel"], 			d: "Fade to level (variable)",		p: ["?Start level (optional):variable","Target level:variable","Duration (seconds):number[1..600]"],															dd: "Fade level from {0}% to {1}% in {2}s",				],
		[ n: "setLevelIf",								d: "Set level (advanced)",					p: ["Level:level","Only if switch state is:enum[on,off]"], dd: "Set level to {0}% if switch is {1}",		attribute: "level",		value: "*|number",	],
		[ n: "adjustLevel",			r: ["setLevel"], 			d: "Adjust level",					p: ["Adjustment (+/-):number[-100..100]"],																												dd: "Adjust level by {0}%",	],
		[ n: "adjustLevelVariable",		r: ["setLevel"], 			d: "Adjust level (variable)",					p: ["Adjustment (+/-):variable"],																												dd: "Adjust level by {0}%",	],
		[ n: "fadeSaturation",			r: ["setSaturation"],			d: "Fade to saturation",				p: ["?Start saturation (optional):saturation","Target saturation:saturation","Duration (seconds):number[1..600]"],											dd: "Fade saturation from {0}% to {1}% in {2}s",				],
		[ n: "fadeSaturationVariable",		r: ["setSaturation"],			d: "Fade to saturation (variable)",				p: ["?Start saturation (optional):variable","Target saturation:variable","Duration (seconds):number[1..600]"],											dd: "Fade saturation from {0}% to {1}% in {2}s",				],
		[ n: "adjustSaturation",		r: ["setSaturation"],			d: "Adjust saturation",				p: ["Adjustment (+/-):number[-100..100]"],																												dd: "Adjust saturation by {0}%",	],
		[ n: "adjustSaturationVariable",	r: ["setSaturation"],			d: "Adjust saturation (variable)",				p: ["Adjustment (+/-):variable"],																												dd: "Adjust saturation by {0}%",	],
		[ n: "fadeHue",				r: ["setHue"], 				d: "Fade to hue",						p: ["?Start hue (optional):hue","Target hue:hue","Duration (seconds):number[1..600]"],																dd: "Fade hue from {0}° to {1}° in {2}s",				],
		[ n: "fadeHueVariable",			r: ["setHue"], 				d: "Fade to hue (variable)",			p: ["?Start hue (optional):variable","Target hue:variable","Duration (seconds):number[1..600]"],																dd: "Fade hue from {0}° to {1}° in {2}s",				],
		[ n: "adjustHue",			r: ["setHue"], 				d: "Adjust hue",						p: ["Adjustment (+/-):number[-360..360]"],																												dd: "Adjust hue by {0}°",	],
		[ n: "adjustHueVariable",		r: ["setHue"], 				d: "Adjust hue (variable)",			p: ["Adjustment (+/-):variable"],																												dd: "Adjust hue by {0}°",	],
		[ n: "flash",				r: ["on", "off"], 			d: "Flash",							p: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					dd: "Flash {0}ms/{1}ms for {2} time(s)",		],
		[ n: "flash#1",				r: ["on1", "off1"], 			d: "Flash #1",						p: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					dd: "Flash #1 {0}ms/{1}ms for {2} time(s)",	],
		[ n: "flash#2",				r: ["on2", "off2"], 			d: "Flash #2",						p: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					dd: "Flash #2 {0}ms/{1}ms for {2} time(s)",	],
		[ n: "flash#3",				r: ["on3", "off3"], 			d: "Flash #3",						p: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					dd: "Flash #3 {0}ms/{1}ms for {2} time(s)",	],
		[ n: "flash#4",				r: ["on4", "off4"], 			d: "Flash #4",						p: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					dd: "Flash #4 {0}ms/{1}ms for {2} time(s)",	],
		[ n: "flash#5",				r: ["on5", "off5"], 			d: "Flash #5",						p: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					dd: "Flash #5 {0}ms/{1}ms for {2} time(s)",	],
		[ n: "flash#6",				r: ["on6", "off6"], 			d: "Flash #6",						p: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					dd: "Flash #6 {0}ms/{1}ms for {2} time(s)",	],
		[ n: "flash#7",				r: ["on7", "off7"], 			d: "Flash #7",						p: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					dd: "Flash #7 {0}ms/{1}ms for {2} time(s)",	],
		[ n: "flash#8",				r: ["on8", "off8"], 			d: "Flash #8",						p: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					dd: "Flash #8 {0}ms/{1}ms for {2} time(s)",	],
		[ n: "setVariable",		d: "Set variable", 					p: ["Variable:var"],																				varEntry: 0, 						l: true,																	a: true,	],
		[ n: "saveAttribute",	d: "Save attribute to variable", 		p: ["Attribute:attribute","Aggregation:aggregation","?Convert to data type:dataType","Save to variable:string"],					varEntry: 3,		dd: "Save attribute '{0}' to variable |[{3}]|'",			a: true,	],
		[ n: "saveState",		d: "Save state to variable",			p: ["Attributes:attributes","Aggregation:aggregation","?Convert to data type:dataType","Save to state variable:string"],			stateVarEntry: 3,	dd: "Save state of attributes {0} to variable |[{3}]|'",	a: true,	],
		[ n: "saveStateLocally",	d: "Capture state to local store",	p: ["Attributes:attributes","?Only if state is empty:bool"],																															dd: "Capture state of attributes {0} to local store",		],
		[ n: "saveStateGlobally",d: "Capture state to global store",	p: ["Attributes:attributes","?Only if state is empty:bool"],																															dd: "Capture state of attributes {0} to global store",	],
		[ n: "loadAttribute",	d: "Load attribute from variable",	p: ["Attribute:attribute","Load from variable:variable","Allow translations:bool","Negate translation:bool"],											dd: "Load attribute '{0}' from variable |[{1}]|",	],
		[ n: "loadState",		d: "Load state from variable",		p: ["Attributes:attributes","Load from state variable:stateVariable","Allow translations:bool","Negate translation:bool"],								dd: "Load state of attributes {0} from variable |[{1}]|"				],
		[ n: "loadStateLocally",	d: "Restore state from local store",	p: ["Attributes:attributes","?Empty the state:bool"],																															dd: "Restore state of attributes {0} from local store",			],
		[ n: "loadStateGlobally",d: "Restore state from global store",	p: ["Attributes:attributes","?Empty the state:bool"],																															dd: "Restore state of attributes {0} from global store",			],
		[ n: "setLocationMode",	d: "Set location mode",				p: [[n:"Mode",t:"mode"]],																														l: true,	dd: "Set location mode to '{0}'",		a: true,	],
		[ n: "setAlarmSystemStatus",d: "Set Smart Home Monitor status",	p: [[n:"Status",t:"alarmSystemStatus"]],																										l: true,	dd: "Set SHM alarm to '{0}'",			a: true,	],
		[ n: "sendNotification",	d: "Send notification",				p: [[n:"Message",t:"text"]],																													l: true,	dd: "Send notification '{0}' in notifications page",			a: true,	],
		[ n: "sendPushNotification",d: "Send Push notification",			p: [[n:"Message",t:"text"],[n:"Show in notifications page",t:"bool"]],																							l: true,	dd: "Send Push notification '{0}'",		a: true,	],
		[ n: "sendSMSNotification",d: "Send SMS notification",			p: [[n:"Message",t:"text"],[n:"Phone number",t:"phone"],[n:"Show in notifications page",t:"bool"]],																		l: true, dd: "Send SMS notification '{0}' to {1}",a: true,	],
		[ n: "queueAskAlexaMessage",d: "Queue AskAlexa message",			p: [[n:"Message",t:"text"],[n:"Unit",t:"text",r:false],[n:"Application",t:"text",r:false]],																		l: true, dd: "Queue AskAlexa message '{0}' in unit {1}",a: true,	],
		[ n: "deleteAskAlexaMessages",d: "Delete AskAlexa messages",			p: [[n:"Unit",t:"text"],[n:"Application",t:"text",r:false]],																	l: true, dd: "Delete AskAlexa messages in unit {1}",a: true,	],
		[ n: "executeRoutine",	d: "Execute routine",					p: [[n:"Routine",t:"routine"]],																		l: true, 										dd: "Execute routine '{0}'",				a: true,	],
		[ n: "cancelPendingTasks",d: "Cancel pending tasks",			p: [[n:"Scope",t:"enum",o:["Local","Global"]]],																														dd: "Cancel all pending {0} tasks",		],
		[ n: "followUp",				d: "Follow up with piston",			p: ["Delay:number[1..1440]","Unit:enum[seconds,minutes,hours]","Piston:piston","?Save state into variable:string"],	immediate: true,	varEntry: 3,	l: true,	dd: "Follow up with piston '{2}' after {0} {1}",	a: true],
		[ n: "executePiston",		d: "Execute piston",					p: [[n:"Piston",t:"piston"],[n:"Save state into variable",t:"string",z:false]],																varEntry: 1,	l: true,	dd: "Execute piston '{0}'",	a: true],
		[ n: "pausePiston",			d: "Pause piston",					p: [[n:"Piston",t:"piston"]],																l: true,	dd: "Pause piston '{0}'",	a: true],
		[ n: "resumePiston",			d: "Resume piston",					p: ["Piston:piston"],																l: true,	dd: "Resume piston '{0}'",	a: true],
	        [ n: "httpRequest",			d: "Make a web request", p: ["URL:string","Method:enum[GET,POST,PUT,DELETE,HEAD]","Content Type:enum[JSON,FORM]","?Variables to send:variables","Import response data into variables:bool","?Variable import name prefix (optional):string"], l: true, dd: "Make a {1} web request to {0}", a: true],
	        [ n: "wolRequest",			d: "Wake a LAN device", p: ["MAC address:string","?Secure code:string"], l: true, dd: "Wake LAN device at address {0} with secure code {1}", a: true],        
    ]
   	if (location.contactBookEnabled) {
    	cmds.push([ n: "sendNotificationToContacts", d: "Send notification to contacts", p: ["Message:text","Contacts:contacts","Save notification:bool"], l: true, dd: "Send notification '{0}' to {1}", a: true])
    }
    if (getIftttKey()) {
    	cmds.push([ n: "iftttMaker", d: "Send IFTTT Maker event", p: ["Event:text", "?Value1:string", "?Value2:string", "?Value3:string"], l: true, dd: "Send IFTTT Maker event '{0}' with parameters '{1}', '{2}', and '{3}'", a: true])
    }
	if (getLifxToken()) {
		cmds.push([ n: "lifxScene", d: "Activate LIFX scene", p: ["Scene:lifxScenes"], l: true, dd: "Activate LIFX Scene '{0}'", a: true])
    }    
    return cmds
}

private attributes() {
	def tempUnit = "°" + location.temperatureScale ?: "F"
	return [
		[ n: "acceleration",			t: "enum",							o: ["active", "inactive"],											],
		[ n: "alarm",				t: "enum",							o: ["off", "strobe", "siren", "both"],										],
		[ n: "battery",				t: "number",		r: [0, 100],		u: "%",																],
		[ n: "beacon",				t: "enum",							o: ["present", "not present"],											],
		[ n: "button",				t: "enum",							o: ["held", "pushed"],								c: "button",	m: true		], //default capability so that we can figure out multi sub devices
		[ n: "carbonDioxide",			t: "decimal",		r: [0, null],																		],
		[ n: "carbonMonoxide",			t: "enum",							o: ["clear", "detected", "tested"],										],
		[ n: "color",				t: "color",																					],
		[ n: "hue",				t: "number",		r: [0, 360],		u: "°",																],
		[ n: "hex",				t: "hexcolor",																					],
		[ n: "saturation",			t: "number",		r: [0, 100],		u: "%",																],
		[ n: "level",				t: "number",		r: [0, 100],		u: "%",																],
		[ n: "switch",				t: "enum",							o: ["on", "off"],										i: true,	],
		[ n: "colorTemperature",		t: "number",		r: [1000, 30000],	u: "°K",															],
		[ n: "consumable",			t: "enum",							o: ["missing", "good", "replace", "maintenance_required", "order"],						],
		[ n: "contact",				t: "enum",							o: ["open", "closed"],												],
		[ n: "door",				t: "enum",							o: ["unknown", "closed", "open", "closing", "opening"],	i: true,						],
		[ n: "energy",				t: "decimal",		r: [0, null],		u: "kWh",															],
		[ n: "energy*",				t: "decimal",		r: [0, null],		u: "kWh",															],
		[ n: "indicatorStatus",			t: "enum",							o: ["when off", "when on", "never"],										],
		[ n: "illuminance",			t: "number",		r: [0, null],		u: "lux",															],
		[ n: "image",				t: "image",																					],
		[ n: "lock",				t: "enum",							o: ["locked", "unlocked"],							c: "lock",	i: true,	],
		[ n: "activities",			t: "string",																					],
		[ n: "currentActivity",			t: "string",																					],
		[ n: "motion",				t: "enum",							o: ["active", "inactive"],											],
		[ n: "status",				t: "string",																					],
		[ n: "mute",				t: "enum",							o: ["muted", "unmuted"],											],
		[ n: "pH",				t: "decimal",		r: [0, 14],																		],
		[ n: "power",				t: "decimal",		r: [0, null],		u: "W",																],
		[ n: "power*",				t: "decimal",		r: [0, null],		u: "W",																],
		[ n: "occupancy",			t: "enum",							o: ["occupied", "not occupied"]											],
		[ n: "presence",			t: "enum",							o: ["present", "not present"]											],
		[ n: "humidity",			t: "number",		r: [0, 100],		u: "%",																],
		[ n: "shock",				t: "enum",							o: ["detected", "clear"]											],
		[ n: "lqi",				t: "number",		r: [0, 255],																		],
		[ n: "rssi",				t: "number",		r: [0, 100],		u: "%",																],
		[ n: "sleeping",			t: "enum",							o: ["sleeping", "not sleeping"]											],
		[ n: "smoke",				t: "enum",							o: ["clear", "detected", "tested"]										],
		[ n: "sound",				t: "enum",							o: ["detected", "not detected"]											],
		[ n: "steps",				t: "number",		r: [0, null],																		],
		[ n: "goal",				t: "number",		r: [0, null],																		],
		[ n: "soundPressureLevel",		t: "number",		r: [0, null],																		],
		[ n: "tamper",				t: "enum",							o: ["clear", "detected"],											],
		[ n: "temperature",			t: "decimal",		r: [-127, 127],		u: tempUnit,															],
		[ n: "thermostatMode",			t: "enum",							o: ["off", "auto", "cool", "heat", "emergency heat"],								],
		[ n: "thermostatFanMode",		t: "enum",							o: ["auto", "on", "circulate"],											],
		[ n: "thermostatOperatingState",	t: "enum",							o: ["idle", "pending cool", "cooling", "pending heat", "heating", "fan only", "vent economizer"],		],
		[ n: "coolingSetpoint",			t: "decimal",		r: [-127, 127],		u: tempUnit,															],
		[ n: "heatingSetpoint",			t: "decimal",		r: [-127, 127],		u: tempUnit,															],
		[ n: "thermostatSetpoint",		t: "decimal",		r: [-127, 127],		u: tempUnit,															],
		[ n: "sessionStatus",			t: "enum",							o: ["paused", "stopped", "running", "canceled"],								],
		[ n: "threeAxis",			t: "vector3",																					],
		[ n: "orientation",			t: "orientation",						o: threeAxisOrientations(),						vt: "enum",	s: "threeAxis",		],
		[ n: "axisX",				t: "number",		r: [-1024, 1024],														s: "threeAxis",		],
		[ n: "axisY",				t: "number",		r: [-1024, 1024],														s: "threeAxis",		],
		[ n: "axisZ",				t: "number",		r: [-1024, 1024],														s: "threeAxis",		],
		[ n: "touch",				t: "enum",							o: ["touched"],													],
		[ n: "valve",				t: "enum",							o: ["open", "closed"],												],
		[ n: "voltage",				t: "decimal",		r: [null, null],	u: "V",																],
		[ n: "water",				t: "enum",							o: ["dry", "wet"],												],
		[ n: "windowShade",			t: "enum",							o: ["unknown", "open", "closed", "opening", "closing", "partially open"],					],
		[ n: "mode",				t: "mode",							o: getLocationModeOptions(),											],
		[ n: "alarmSystemStatus",		t: "enum",							o: getAlarmSystemStatusOptions(),										],
		[ n: "routineExecuted",			t: "routine",							o: location.helloHome?.getPhrases()*.label,	vt: "enum",							],
		//[ n: "piston",			t: "piston",							o: state.run == "config" ? parent.listPistons(state.config.expertMode ? null : app.label) : [],	vt: "enum",	],
		//[ n: "variable",			t: "enum",							o: state.run == "config" ? listVariables(true) : [],						vt: "enum",	],
		[ n: "time",				t: "time",																					],
		//[ n: "askAlexaMacro",			t: "askAlexaMacro",						o: state.run == "config" ? listAskAlexaMacros() : []						vt: "enum",	],
		//[ n: "echoSistantProfile",		t: "echoSistantProfile",					o: state.run == "config" ? listEchoSistantProfiles() : [] 					vt: "enum",	],
		[ n: "ifttt",				t: "ifttt",																			vt: "string"	],
	]
}

private colors() {
	return [
		[ n:"Random",					rgb: "#000000",		h: 0,		s: 0,		l: 0,	],
		[ n:"Soft White",				rgb: "#B6DA7C",		h: 83,		s: 44,		l: 67,	],
		[ n:"Warm White",				rgb: "#DAF17E",		h: 72,		s: 20,		l: 72,	],
		[ n:"Daylight White",			rgb: "#CEF4FD",		h: 191,		s: 9,		l: 90,	],
		[ n:"Cool White",				rgb: "#F3F6F7",		h: 187,		s: 19,		l: 96,	],
		[ n:"White",					rgb: "#FFFFFF",		h: 0,		s: 0,		l: 100,	],
		[ n:"Alice Blue",				rgb: "#F0F8FF",		h: 208,		s: 100,		l: 97,	],
		[ n:"Antique White",			rgb: "#FAEBD7",		h: 34,		s: 78,		l: 91,	],
		[ n:"Aqua",						rgb: "#00FFFF",		h: 180,		s: 100,		l: 50,	],
		[ n:"Aquamarine",				rgb: "#7FFFD4",		h: 160,		s: 100,		l: 75,	],
		[ n:"Azure",					rgb: "#F0FFFF",		h: 180,		s: 100,		l: 97,	],
		[ n:"Beige",					rgb: "#F5F5DC",		h: 60,		s: 56,		l: 91,	],
		[ n:"Bisque",					rgb: "#FFE4C4",		h: 33,		s: 100,		l: 88,	],
		[ n:"Blanched Almond",			rgb: "#FFEBCD",		h: 36,		s: 100,		l: 90,	],
		[ n:"Blue",						rgb: "#0000FF",		h: 240,		s: 100,		l: 50,	],
		[ n:"Blue Violet",				rgb: "#8A2BE2",		h: 271,		s: 76,		l: 53,	],
		[ n:"Brown",					rgb: "#A52A2A",		h: 0,		s: 59,		l: 41,	],
		[ n:"Burly Wood",				rgb: "#DEB887",		h: 34,		s: 57,		l: 70,	],
		[ n:"Cadet Blue",				rgb: "#5F9EA0",		h: 182,		s: 25,		l: 50,	],
		[ n:"Chartreuse",				rgb: "#7FFF00",		h: 90,		s: 100,		l: 50,	],
		[ n:"Chocolate",				rgb: "#D2691E",		h: 25,		s: 75,		l: 47,	],
		[ n:"Coral",					rgb: "#FF7F50",		h: 16,		s: 100,		l: 66,	],
		[ n:"Corn Flower Blue",			rgb: "#6495ED",		h: 219,		s: 79,		l: 66,	],
		[ n:"Corn Silk",				rgb: "#FFF8DC",		h: 48,		s: 100,		l: 93,	],
		[ n:"Crimson",					rgb: "#DC143C",		h: 348,		s: 83,		l: 58,	],
		[ n:"Cyan",						rgb: "#00FFFF",		h: 180,		s: 100,		l: 50,	],
		[ n:"Dark Blue",				rgb: "#00008B",		h: 240,		s: 100,		l: 27,	],
		[ n:"Dark Cyan",				rgb: "#008B8B",		h: 180,		s: 100,		l: 27,	],
		[ n:"Dark Golden Rod",			rgb: "#B8860B",		h: 43,		s: 89,		l: 38,	],
		[ n:"Dark Gray",				rgb: "#A9A9A9",		h: 0,		s: 0,		l: 66,	],
		[ n:"Dark Green",				rgb: "#006400",		h: 120,		s: 100,		l: 20,	],
		[ n:"Dark Khaki",				rgb: "#BDB76B",		h: 56,		s: 38,		l: 58,	],
		[ n:"Dark Magenta",				rgb: "#8B008B",		h: 300,		s: 100,		l: 27,	],
		[ n:"Dark Olive Green",			rgb: "#556B2F",		h: 82,		s: 39,		l: 30,	],
		[ n:"Dark Orange",				rgb: "#FF8C00",		h: 33,		s: 100,		l: 50,	],
		[ n:"Dark Orchid",				rgb: "#9932CC",		h: 280,		s: 61,		l: 50,	],
		[ n:"Dark Red",					rgb: "#8B0000",		h: 0,		s: 100,		l: 27,	],
		[ n:"Dark Salmon",				rgb: "#E9967A",		h: 15,		s: 72,		l: 70,	],
		[ n:"Dark Sea Green",			rgb: "#8FBC8F",		h: 120,		s: 25,		l: 65,	],
		[ n:"Dark Slate Blue",			rgb: "#483D8B",		h: 248,		s: 39,		l: 39,	],
		[ n:"Dark Slate Gray",			rgb: "#2F4F4F",		h: 180,		s: 25,		l: 25,	],
		[ n:"Dark Turquoise",			rgb: "#00CED1",		h: 181,		s: 100,		l: 41,	],
		[ n:"Dark Violet",				rgb: "#9400D3",		h: 282,		s: 100,		l: 41,	],
		[ n:"Deep Pink",				rgb: "#FF1493",		h: 328,		s: 100,		l: 54,	],
		[ n:"Deep Sky Blue",			rgb: "#00BFFF",		h: 195,		s: 100,		l: 50,	],
		[ n:"Dim Gray",					rgb: "#696969",		h: 0,		s: 0,		l: 41,	],
		[ n:"Dodger Blue",				rgb: "#1E90FF",		h: 210,		s: 100,		l: 56,	],
		[ n:"Fire Brick",				rgb: "#B22222",		h: 0,		s: 68,		l: 42,	],
		[ n:"Floral White",				rgb: "#FFFAF0",		h: 40,		s: 100,		l: 97,	],
		[ n:"Forest Green",				rgb: "#228B22",		h: 120,		s: 61,		l: 34,	],
		[ n:"Fuchsia",					rgb: "#FF00FF",		h: 300,		s: 100,		l: 50,	],
		[ n:"Gainsboro",				rgb: "#DCDCDC",		h: 0,		s: 0,		l: 86,	],
		[ n:"Ghost White",				rgb: "#F8F8FF",		h: 240,		s: 100,		l: 99,	],
		[ n:"Gold",						rgb: "#FFD700",		h: 51,		s: 100,		l: 50,	],
		[ n:"Golden Rod",				rgb: "#DAA520",		h: 43,		s: 74,		l: 49,	],
		[ n:"Gray",						rgb: "#808080",		h: 0,		s: 0,		l: 50,	],
		[ n:"Green",					rgb: "#008000",		h: 120,		s: 100,		l: 25,	],
		[ n:"Green Yellow",				rgb: "#ADFF2F",		h: 84,		s: 100,		l: 59,	],
		[ n:"Honeydew",					rgb: "#F0FFF0",		h: 120,		s: 100,		l: 97,	],
		[ n:"Hot Pink",					rgb: "#FF69B4",		h: 330,		s: 100,		l: 71,	],
		[ n:"Indian Red",				rgb: "#CD5C5C",		h: 0,		s: 53,		l: 58,	],
		[ n:"Indigo",					rgb: "#4B0082",		h: 275,		s: 100,		l: 25,	],
		[ n:"Ivory",					rgb: "#FFFFF0",		h: 60,		s: 100,		l: 97,	],
		[ n:"Khaki",					rgb: "#F0E68C",		h: 54,		s: 77,		l: 75,	],
		[ n:"Lavender",					rgb: "#E6E6FA",		h: 240,		s: 67,		l: 94,	],
		[ n:"Lavender Blush",			rgb: "#FFF0F5",		h: 340,		s: 100,		l: 97,	],
		[ n:"Lawn Green",				rgb: "#7CFC00",		h: 90,		s: 100,		l: 49,	],
		[ n:"Lemon Chiffon",			rgb: "#FFFACD",		h: 54,		s: 100,		l: 90,	],
		[ n:"Light Blue",				rgb: "#ADD8E6",		h: 195,		s: 53,		l: 79,	],
		[ n:"Light Coral",				rgb: "#F08080",		h: 0,		s: 79,		l: 72,	],
		[ n:"Light Cyan",				rgb: "#E0FFFF",		h: 180,		s: 100,		l: 94,	],
		[ n:"Light Golden Rod Yellow",	rgb: "#FAFAD2",		h: 60,		s: 80,		l: 90,	],
		[ n:"Light Gray",				rgb: "#D3D3D3",		h: 0,		s: 0,		l: 83,	],
		[ n:"Light Green",				rgb: "#90EE90",		h: 120,		s: 73,		l: 75,	],
		[ n:"Light Pink",				rgb: "#FFB6C1",		h: 351,		s: 100,		l: 86,	],
		[ n:"Light Salmon",				rgb: "#FFA07A",		h: 17,		s: 100,		l: 74,	],
		[ n:"Light Sea Green",			rgb: "#20B2AA",		h: 177,		s: 70,		l: 41,	],
		[ n:"Light Sky Blue",			rgb: "#87CEFA",		h: 203,		s: 92,		l: 75,	],
		[ n:"Light Slate Gray",			rgb: "#778899",		h: 210,		s: 14,		l: 53,	],
		[ n:"Light Steel Blue",			rgb: "#B0C4DE",		h: 214,		s: 41,		l: 78,	],
		[ n:"Light Yellow",				rgb: "#FFFFE0",		h: 60,		s: 100,		l: 94,	],
		[ n:"Lime",						rgb: "#00FF00",		h: 120,		s: 100,		l: 50,	],
		[ n:"Lime Green",				rgb: "#32CD32",		h: 120,		s: 61,		l: 50,	],
		[ n:"Linen",					rgb: "#FAF0E6",		h: 30,		s: 67,		l: 94,	],
		[ n:"Maroon",					rgb: "#800000",		h: 0,		s: 100,		l: 25,	],
		[ n:"Medium Aquamarine",		rgb: "#66CDAA",		h: 160,		s: 51,		l: 60,	],
		[ n:"Medium Blue",				rgb: "#0000CD",		h: 240,		s: 100,		l: 40,	],
		[ n:"Medium Orchid",			rgb: "#BA55D3",		h: 288,		s: 59,		l: 58,	],
		[ n:"Medium Purple",			rgb: "#9370DB",		h: 260,		s: 60,		l: 65,	],
		[ n:"Medium Sea Green",			rgb: "#3CB371",		h: 147,		s: 50,		l: 47,	],
		[ n:"Medium Slate Blue",		rgb: "#7B68EE",		h: 249,		s: 80,		l: 67,	],
		[ n:"Medium Spring Green",		rgb: "#00FA9A",		h: 157,		s: 100,		l: 49,	],
		[ n:"Medium Turquoise",			rgb: "#48D1CC",		h: 178,		s: 60,		l: 55,	],
		[ n:"Medium Violet Red",		rgb: "#C71585",		h: 322,		s: 81,		l: 43,	],
		[ n:"Midnight Blue",			rgb: "#191970",		h: 240,		s: 64,		l: 27,	],
		[ n:"Mint Cream",				rgb: "#F5FFFA",		h: 150,		s: 100,		l: 98,	],
		[ n:"Misty Rose",				rgb: "#FFE4E1",		h: 6,		s: 100,		l: 94,	],
		[ n:"Moccasin",					rgb: "#FFE4B5",		h: 38,		s: 100,		l: 85,	],
		[ n:"Navajo White",				rgb: "#FFDEAD",		h: 36,		s: 100,		l: 84,	],
		[ n:"Navy",						rgb: "#000080",		h: 240,		s: 100,		l: 25,	],
		[ n:"Old Lace",					rgb: "#FDF5E6",		h: 39,		s: 85,		l: 95,	],
		[ n:"Olive",					rgb: "#808000",		h: 60,		s: 100,		l: 25,	],
		[ n:"Olive Drab",				rgb: "#6B8E23",		h: 80,		s: 60,		l: 35,	],
		[ n:"Orange",					rgb: "#FFA500",		h: 39,		s: 100,		l: 50,	],
		[ n:"Orange Red",				rgb: "#FF4500",		h: 16,		s: 100,		l: 50,	],
		[ n:"Orchid",					rgb: "#DA70D6",		h: 302,		s: 59,		l: 65,	],
		[ n:"Pale Golden Rod",			rgb: "#EEE8AA",		h: 55,		s: 67,		l: 80,	],
		[ n:"Pale Green",				rgb: "#98FB98",		h: 120,		s: 93,		l: 79,	],
		[ n:"Pale Turquoise",			rgb: "#AFEEEE",		h: 180,		s: 65,		l: 81,	],
		[ n:"Pale Violet Red",			rgb: "#DB7093",		h: 340,		s: 60,		l: 65,	],
		[ n:"Papaya Whip",				rgb: "#FFEFD5",		h: 37,		s: 100,		l: 92,	],
		[ n:"Peach Puff",				rgb: "#FFDAB9",		h: 28,		s: 100,		l: 86,	],
		[ n:"Peru",						rgb: "#CD853F",		h: 30,		s: 59,		l: 53,	],
		[ n:"Pink",						rgb: "#FFC0CB",		h: 350,		s: 100,		l: 88,	],
		[ n:"Plum",						rgb: "#DDA0DD",		h: 300,		s: 47,		l: 75,	],
		[ n:"Powder Blue",				rgb: "#B0E0E6",		h: 187,		s: 52,		l: 80,	],
		[ n:"Purple",					rgb: "#800080",		h: 300,		s: 100,		l: 25,	],
		[ n:"Red",						rgb: "#FF0000",		h: 0,		s: 100,		l: 50,	],
		[ n:"Rosy Brown",				rgb: "#BC8F8F",		h: 0,		s: 25,		l: 65,	],
		[ n:"Royal Blue",				rgb: "#4169E1",		h: 225,		s: 73,		l: 57,	],
		[ n:"Saddle Brown",				rgb: "#8B4513",		h: 25,		s: 76,		l: 31,	],
		[ n:"Salmon",					rgb: "#FA8072",		h: 6,		s: 93,		l: 71,	],
		[ n:"Sandy Brown",				rgb: "#F4A460",		h: 28,		s: 87,		l: 67,	],
		[ n:"Sea Green",				rgb: "#2E8B57",		h: 146,		s: 50,		l: 36,	],
		[ n:"Sea Shell",				rgb: "#FFF5EE",		h: 25,		s: 100,		l: 97,	],
		[ n:"Sienna",					rgb: "#A0522D",		h: 19,		s: 56,		l: 40,	],
		[ n:"Silver",					rgb: "#C0C0C0",		h: 0,		s: 0,		l: 75,	],
		[ n:"Sky Blue",					rgb: "#87CEEB",		h: 197,		s: 71,		l: 73,	],
		[ n:"Slate Blue",				rgb: "#6A5ACD",		h: 248,		s: 53,		l: 58,	],
		[ n:"Slate Gray",				rgb: "#708090",		h: 210,		s: 13,		l: 50,	],
		[ n:"Snow",						rgb: "#FFFAFA",		h: 0,		s: 100,		l: 99,	],
		[ n:"Spring Green",				rgb: "#00FF7F",		h: 150,		s: 100,		l: 50,	],
		[ n:"Steel Blue",				rgb: "#4682B4",		h: 207,		s: 44,		l: 49,	],
		[ n:"Tan",						rgb: "#D2B48C",		h: 34,		s: 44,		l: 69,	],
		[ n:"Teal",						rgb: "#008080",		h: 180,		s: 100,		l: 25,	],
		[ n:"Thistle",					rgb: "#D8BFD8",		h: 300,		s: 24,		l: 80,	],
		[ n:"Tomato",					rgb: "#FF6347",		h: 9,		s: 100,		l: 64,	],
		[ n:"Turquoise",				rgb: "#40E0D0",		h: 174,		s: 72,		l: 56,	],
		[ n:"Violet",					rgb: "#EE82EE",		h: 300,		s: 76,		l: 72,	],
		[ n:"Wheat",					rgb: "#F5DEB3",		h: 39,		s: 77,		l: 83,	],
		[ n:"White Smoke",				rgb: "#F5F5F5",		h: 0,		s: 0,		l: 96,	],
		[ n:"Yellow",					rgb: "#FFFF00",		h: 60,		s: 100,		l: 50,	],
		[ n:"Yellow Green",				rgb: "#9ACD32",		h: 80,		s: 61,		l: 50,	],
	]
}

private threeAxisOrientations() {
	return ["rear side up", "down side up", "left side up", "front side up", "up side up", "right side up"]
}

def getIftttKey() {
	def module = atomicState.modules?.IFTTT
	return (module && module.connected ? module.key : null)
}

def getLifxToken() {
	def module = atomicState.modules?.LIFX
	return (module && module.connected ? module.token : null)
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