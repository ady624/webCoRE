/*
 *  CoRE (SE) - Community's own Rule Engine - Web Edition
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

static String handle() { return "CoRE (SE)" }
def version() {	return "v0.0.034.20170311" }
/*
 *	03/11/2016 >>> v0.0.034.20170311 - ALPHA - Multiple device selection aggregation now working properly. COUNT(device list's contact) rises above 1 will be true when at least two doors in the list are open :D
 *	03/11/2016 >>> v0.0.033.20170311 - ALPHA - Implemented all conditions except "was..." and all triggers except "stays..."
 *	03/11/2016 >>> v0.0.032.20170311 - ALPHA - Fixed setLevel null params and added version checking
 *	03/11/2016 >>> v0.0.031.20170311 - ALPHA - Various fixes including null optional parameters, conditional groups, first attempt at piston restrictions (statement restrictions not enabled yet), fixed a problem with subscribing device bolt indicators only showing for one instance of each device/attribute pair, fixed sendPushNotification
 *	03/10/2016 >>> v0.0.030.20170310 - ALPHA - Fixed a bug in scheduler introduced in 02e/02f
 *	03/10/2016 >>> v0.0.02f.20170310 - ALPHA - Various improvements, added toggle and toggleLevel
 *	03/10/2016 >>> v0.0.02e.20170310 - ALPHA - Fixed a problem where long expiration settings prevented logins (integer overflow)
 *	03/10/2016 >>> v0.0.02d.20170310 - ALPHA - Reporting version to JS
 *	03/10/2016 >>> v0.0.02c.20170310 - ALPHA - Various improvements and a new virtual command: Log to console. Powerful.
 *	03/10/2016 >>> v0.0.02b.20170310 - ALPHA - Implemented device versioning to correctly handle multiple browsers accessing the same dashboard after a device selection was performed, enabled security token expiry
 *	03/09/2016 >>> v0.0.02a.20170309 - ALPHA - Fixed parameter issues, added support for expressions in all parameters, added notification virtual tasks
 *	03/09/2016 >>> v0.0.029.20170309 - ALPHA - More execution flow fixes, sticky trace lines fixed
 *	03/08/2016 >>> v0.0.028.20170308 - ALPHA - Scheduler fixes
 *	03/08/2016 >>> v0.0.027.20170308 - ALPHA - Very early implementation of wait/delay scheduling, needs extensive testing
 *	03/08/2016 >>> v0.0.026.20170308 - ALPHA - More bug fixes, trace enhancements
 *	03/07/2016 >>> v0.0.025.20170307 - ALPHA - Improved logs and traces, added basic time event handler
 *	03/07/2016 >>> v0.0.024.20170307 - ALPHA - Improved logs (reverse order and live updates) and added trace support
 *	03/06/2016 >>> v0.0.023.20170306 - ALPHA - Added logs to the dashboard
 *	03/05/2016 >>> v0.0.022.20170305 - ALPHA - Some tasks are now executed. UI has an issue with initializing params on editing a task, will get fixed soon.
 *	03/01/2016 >>> v0.0.021.20170301 - ALPHA - Most conditions (and no triggers yet) are now parsed and evaluated during events - action tasks not yet executed, but getting close, very close
 *	02/28/2016 >>> v0.0.020.20170228 - ALPHA - Added runtime data - pistons are now aware of devices and global variables - expressions can query devices and variables (though not all system variables are ready yet)
 *	02/27/2016 >>> v0.0.01f.20170227 - ALPHA - Added support for a bunch more functions
 *	02/27/2016 >>> v0.0.01e.20170227 - ALPHA - Fixed a bug in expression parser where integer + integer would result in a string
 *	02/27/2016 >>> v0.0.01d.20170227 - ALPHA - Made progress evaluating expressions
 *	02/24/2016 >>> v0.0.01c.20170224 - ALPHA - Added functions support to main app
 *	02/06/2016 >>> v0.0.01b.20170206 - ALPHA - Fixed a problem with selecting thermostats
 *	02/01/2016 >>> v0.0.01a.20170201 - ALPHA - Updated comparisons
 *	01/30/2016 >>> v0.0.019.20170130 - ALPHA - Improved comparisons - ouch
 *	01/29/2016 >>> v0.0.018.20170129 - ALPHA - Fixed a conditions where devices would not be sent over to the UI
 *	01/28/2016 >>> v0.0.017.20170128 - ALPHA - Incremental update
 *	01/27/2016 >>> v0.0.016.20170127 - ALPHA - Minor compatibility fixes
 *	01/27/2016 >>> v0.0.015.20170127 - ALPHA - Updated capabilities, attributes, commands and refactored them into maps
 *	01/26/2016 >>> v0.0.014.20170126 - ALPHA - Progress getting comparisons to work
 *	01/25/2016 >>> v0.0.013.20170125 - ALPHA - Implemented the author field and more improvements to the piston editor
 *	01/23/2016 >>> v0.0.012.20170123 - ALPHA - Implemented the "delete" piston
 *	01/23/2016 >>> v0.0.011.20170123 - ALPHA - Fixed a bug where account id was not hashed
 *	01/23/2016 >>> v0.0.010.20170123 - ALPHA - Duplicate piston and restore from automatic backup :)
 *	01/23/2016 >>> v0.0.00f.20170123 - ALPHA - Automatic backup to myjson.com is now enabled. Restore is not implemented yet.
 *	01/22/2016 >>> v0.0.00e.20170122 - ALPHA - Enabled device cache on main app to speed up dashboard when using large number of devices
 *	01/22/2016 >>> v0.0.00d.20170122 - ALPHA - Optimized data usage for piston JSON class (might have broken some things), save now works
 *	01/21/2016 >>> v0.0.00c.20170121 - ALPHA - Made more progress towards creating new pistons
 *	01/21/2016 >>> v0.0.00b.20170121 - ALPHA - Made progress towards creating new pistons
 *	01/20/2016 >>> v0.0.00a.20170120 - ALPHA - Fixed a problem with dashboard URL and shards other than na01
 *	01/20/2016 >>> v0.0.009.20170120 - ALPHA - Reenabled the new piston UI at new URL
 *	01/20/2016 >>> v0.0.008.20170120 - ALPHA - Enabled html5 routing and rewrite to remove the /#/ contraption
 *	01/20/2016 >>> v0.0.007.20170120 - ALPHA - Cleaned up CoRE ST UI and removed "default" theme from URL.
 *	01/19/2016 >>> v0.0.006.20170119 - ALPHA - UI is now fully moved and security enabled - security password is now required
 *	01/18/2016 >>> v0.0.005.20170118 - ALPHA - Moved UI to homecloudhub.com and added support for pretty url (core.homecloudhub.com) and web+core:// handle
 *	01/17/2016 >>> v0.0.004.20170117 - ALPHA - Updated to allow multiple instances
 *	01/17/2016 >>> v0.0.003.20170117 - ALPHA - Improved security, object ids are hashed, added multiple-location-multiple-instance support (CoRE will be able to work across multiple location and installed instances)
 *	12/02/2016 >>> v0.0.002.20161202 - ALPHA - Small progress, Add new piston now points to the piston editor UI
 *	10/28/2016 >>> v0.0.001.20161028 - ALPHA - Initial release
 */
 
/******************************************************************************/
/*** CoRE (SE) DEFINITION													***/
/******************************************************************************/

 definition(
	name: "webCoRE",
	namespace: "ady624",
	author: "Adrian Caramaliu",
	description: handle(),
	category: "Convenience",
	singleInstance: false,
    /* icons courtesy of @chauger - thank you */
	iconUrl: "https://cdn.rawgit.com/ady624/webCoRE/master/smartapps/ady624/res/img/app-CoRE.png",
	iconX2Url: "https://cdn.rawgit.com/ady624/webCoRE/master/smartapps/ady624/res/img/app-CoRE@2x.png",
	iconX3Url: "https://cdn.rawgit.com/ady624/webCoRE/master/smartapps/ady624/res/img/app-CoRE@3x.png"
 )


preferences {
	//UI pages
	page(name: "pageMain")
	page(name: "pageInitializeDashboard")
	page(name: "pageFinishInstall")
	page(name: "pageSelectDevices")
	page(name: "pageSettings")
    page(name: "pageChangePassword")
    page(name: "pageSavePassword")
	page(name: "pageRemove")
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
        return dynamicPage(name: "pageMain", title: "", install: false, uninstall: false, nextPage: "pageInitializeDashboard") {
            section() {
                paragraph "Welcome to ${handle()}!"
                paragraph "You will be guided through a few steps that will get ${handle()} ready for the road. First, we'll configure the dashboard. You will need to setup OAuth in the SmartThings IDE for the ${handle()}web app. If you haven't done so already, please go to the SmartThings IDE, go to the SmartApps tab, select ${handle()} and choose App Settings, then enable OAuth under the OAuth section."
                paragraph "Once you're ready, tap Next"
            }
        }
	}
	//CoRE main page
    def dashboardDomain = "core.homecloudhub.com"
    def dashboardUrl = ""
	dynamicPage(name: "pageMain", title: "", install: true, uninstall: false) {
    	section("Engine block") {
			href "pageEngineBlock", title: "Cast iron", description: app.version(), image: "https://cdn.rawgit.com/ady624/webCoRE/master/smartapps/ady624/res/img/app-CoRE.png", required: false
        }
		section("Dashboard") {
			if (!state.endpoint) {
				href "pageInitializeDashboard", title: "${handle()} Dashboard", description: "Tap here to initialize the ${handle()} dashboard", image: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/icons/dashboard.png", required: false
			} else {
            	dashboardUrl = "https://${dashboardDomain}/dashboard/init/" + (apiServerUrl("").replace("https://", '').replace(".api.smartthings.com", "").replace(":443", "").replace("/", "") + (state.accessToken + app.id).replace("-", "")).bytes.encodeBase64()
				trace "*** DO NOT SHARE THIS LINK WITH ANYONE *** Dashboard URL: ${dashboardUrl}"
				href "", title: "${handle()} Dashboard", style: "external", url: dashboardUrl, image: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/icons/dashboard.png", required: false
			}
		}	
		section(title:"Settings") {
			href "pageSettings", title: "Settings", image: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/icons/settings.png", required: false
		}
	}	
}

private pageInitializeDashboard() {
	//CoRE Dashboard initialization
	def success = initializeCoREEndpoint()
	dynamicPage(name: "pageInitializeDashboard", title: "", nextPage: state.installed && success ? null : "pageSelectDevices") {
		if (!state.installed) section() {
			if (success) {
				paragraph "Success! Your ${handle()} dashboard is now enabled. ${state.installed ? "Tap Done to continue." : "Now, choose a security password for your dashboard. You will need to enter this password when accessing your dashboard for the first time and possibly from time to time."}", required: false			   
			} else {
				paragraph "Please go to your SmartThings IDE, select the My SmartApps section, click the 'Edit Properties' button of the CoRE app, open the OAuth section and click the 'Enable OAuth in Smart App' button. Click the Update button to finish.\n\nOnce finished, tap Done and try again.", title: "Please enable OAuth for CoRE", required: true, state: null
				return
			}
		}
        pageSectionPIN()
	}
}

private pageSelectDevices() {
	state.deviceVersion = now().toString()
	dynamicPage(name: "pageSelectDevices", title: "", nextPage: state.installed ? null : "pageFinishInstall") {
		section() {
			paragraph "${state.installed ? "Select the devices you want ${handle()} to have access to." : "It's now time to allow ${handle()} access to some of your devices."} Only allow ${handle()} access to devices you plan on using with ${handle()} pistons, as they only have access to these selected devices.${state.installed ? "" : " When ready, tap Done to finish installing ${handle()}."}"
			if (!state.installed) paragraph "NOTE: You can always come back to ${handle()} and add or remove devices from the list."
			def d
			for (capability in capabilities().findAll{ it.value.d != null }.sort{ it.value.d }) {
				if (capability.value.d != d) input "dev:${capability.key}", "capability.${capability.key}", multiple: true, title: "Which ${capability.value.d}", required: false
				d = capability.value.d
			}
		}
	}
}

private pageFinishInstall() {
	initTokens()
	dynamicPage(name: "pageFinishInstall", title: "", install: true) {
		section() {
			paragraph "Excellent! You have now completed all the ${handle()} installation steps and are ready to go. Remember, you can now access ${handle()} from the SmartApps section of the Automation tab of the SmartThings app. Now go ahead and tap Done and start enjoying ${handle()}!"
		}
	}
}

def pageSettings() {
    //clear devices cache
	dynamicPage(name: "pageSettings", title: "", install: false, uninstall: false) {
		section("General") {
			label name: "name", title: "Name", state: (name ? "complete" : null), defaultValue: app.name, required: false
			paragraph "Memory usage is at ${mem()}", required: false			   
		}
		section("Available devices") {
			href "pageSelectDevices", title: "Available devices", description: "Tap here to select which devices are available to pistons" 
		}
		section("Security") {
			href "pageChangePassword", title: "Security", description: "Tap here to change your dashboard security settings" 
		}
		
		section(title: "Logging") {
			input "logging", "bool", title: "Enable logging", description: "Logs will be available in your dashboard if this feature is enabled", defaultValue: false, required: false
		}
        
		section("Uninstall") {
			href "pageRemove", title: "Uninstall ${handle()}", description: "Tap here to uninstall ${handle()}" 
		}
		
	}
}

private pageChangePassword() {
	dynamicPage(name: "pageChangePassword", title: "", nextPage: "pageSavePassword") {
		section() {
			paragraph "Choose a security password for your dashboard. You will need to enter this password when accessing your dashboard for the first time and possibly from time to time.", required: false			   
		}
		pageSectionPIN()
	}
}

private pageSectionPIN() {
    section() {
        input "PIN", "password", title: "Choose a security password for your dashboard", required: true
        input "expiry", "enum", options: ["Every hour", "Every day", "Every week", "Every month (recommended)", "Every three months", "Never (not recommended)"], defaultValue: "Every month (recommended)", title: "Choose how often the dashboard login expires", required: true
    }

}

private pageSavePassword() {
	initTokens()
    dynamicPage(name: "pageSavePassword", title: "") {
		section() {
			paragraph "Your password has been changed. Please note you may need to reauthenticate when opening the dashboard.", required: false
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






/******************************************************************************/
/*** 																		***/
/*** INITIALIZATION ROUTINES												***/
/*** 																		***/
/******************************************************************************/


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
    state.vars = state.vars ?: [:]
}

private initializeCoREEndpoint() {
	if (!state.endpoint) {
		try {
			def accessToken = createAccessToken()
			if (accessToken) {
				state.endpoint = apiServerUrl("/api/token/${accessToken}/smartapps/installations/${app.id}/")
			}
		} catch(e) {
			state.endpoint = null
		}
	}
	return state.endpoint
}


/******************************************************************************/
/*** 																		***/
/*** DASHBOARD MAPPINGS														***/
/*** 																		***/
/******************************************************************************/

mappings {
	//path("/dashboard") {action: [GET: "api_dashboard"]}
	path("/intf/dashboard/load") {action: [GET: "api_intf_dashboard_load"]}
	path("/intf/dashboard/piston/new") {action: [GET: "api_intf_dashboard_piston_new"]}
	path("/intf/dashboard/piston/create") {action: [GET: "api_intf_dashboard_piston_create"]}
	path("/intf/dashboard/piston/get") {action: [GET: "api_intf_dashboard_piston_get"]}
	path("/intf/dashboard/piston/set") {action: [GET: "api_intf_dashboard_piston_set"]}
	path("/intf/dashboard/piston/set.start") {action: [GET: "api_intf_dashboard_piston_set_start"]}
	path("/intf/dashboard/piston/set.chunk") {action: [GET: "api_intf_dashboard_piston_set_chunk"]}
	path("/intf/dashboard/piston/set.end") {action: [GET: "api_intf_dashboard_piston_set_end"]}
	path("/intf/dashboard/piston/pause") {action: [GET: "api_intf_dashboard_piston_pause"]}
	path("/intf/dashboard/piston/resume") {action: [GET: "api_intf_dashboard_piston_resume"]}
	path("/intf/dashboard/piston/delete") {action: [GET: "api_intf_dashboard_piston_delete"]}
	path("/intf/dashboard/piston/evaluate") {action: [GET: "api_intf_dashboard_piston_evaluate"]}
	path("/intf/dashboard/piston/activity") {action: [GET: "api_intf_dashboard_piston_activity"]}
	path("/ifttt/:eventName") {action: [GET: "api_ifttt", POST: "api_ifttt"]}
	path("/execute") {action: [POST: "api_execute"]}
	path("/execute/:pistonName") {action: [GET: "api_execute", POST: "api_execute"]}
	path("/tap") {action: [POST: "api_tap"]}
	path("/tap/:tapId") {action: [GET: "api_tap"]}
}

private api_get_error_result(error) {
	return [
        name: location.name + ' \\ ' + (app.label ?: app.name),
        error: error,
        now: now()
    ]
}

private api_get_base_result(deviceVersion = 0) {
	def tz = location.getTimeZone()
    def currentDeviceVersion = atomicState.deviceVersion
	def Boolean sendDevices = (deviceVersion != currentDeviceVersion)
	return [
        name: location.name + ' \\ ' + (app.label ?: app.name),
        instance: [
	    	account: [id: hashId(app.getAccountId())],
        	pistons: getChildApps().sort{ it.label }.collect{ [ id: hashId(it.id), 'name': it.label ] },
            id: hashId(app.id),
            locationId: hashId(location.id),
            name: app.label ?: app.name,
            uri: atomicState.endpoint,
            deviceVersion: currentDeviceVersion,
            coreVersion: version(),
            logging: !!settings.logging,
        ] + (sendDevices ? [devices: listAvailableDevices()] : [:]),
        location: [
            contactBookEnabled: location.getContactBookEnabled(),
            hubs: location.getHubs().collect{ [id: hashId(it.id), name: it.name, firmware: it.getFirmwareVersionString(), physical: it.getType().toString().contains('PHYSICAL') ]},
            id: hashId(location.id),
            mode: hashId(location.getCurrentMode().id),
            modes: location.getModes().collect{ [id: hashId(it.id), name: it.name ]},
            name: location.name,
            temperatureScale: location.getTemperatureScale(),
            timeZone: tz ? [
                id: tz.ID,
                name: tz.displayName,
                offset: tz.rawOffset
            ] : null,
            zipCode: location.getZipCode(),
        ],
        now: now(),        
    ]
}

private api_intf_dashboard_load() {
	def result
    debug "Dashboard: Request received to initialize instance"
	if (verifySecurityToken(params.token)) {
    	result = api_get_base_result(params.dev)
    } else {
    	if (params.pin) {
        	if (settings.PIN && (md5("pin:${settings.PIN}") == params.pin)) {
            	result = api_get_base_result()
                result.instance.token = createSecurityToken()
            } else {
		        error "Dashboard: Authentication failed due to an invalid PIN"
            }
        }
        if (!result) result = api_get_error_result("ERR_INVALID_TOKEN")
    }
    //for accuracy, use the time as close as possible to the render
    result.now = now()            
	render contentType: "application/javascript;charset=utf-8", data: "${params.callback}(${result.encodeAsJSON()})"
}


private api_intf_dashboard_piston_new() {
	def result
    debug "Dashboard: Request received to generate a new piston name"
	if (verifySecurityToken(params.token)) {
    	result = [status: "ST_SUCCESS", name: generatePistonName()]
	} else {
    	result = api_get_error_result("ERR_INVALID_TOKEN")
    }
    render contentType: "application/javascript;charset=utf-8", data: "${params.callback}(${result.encodeAsJSON()})"
}

private api_intf_dashboard_piston_create() {
	def result
    debug "Dashboard: Request received to generate a new piston name"
	if (verifySecurityToken(params.token)) {
    	def piston = addChildApp("ady624", "webCoRE Piston", params.name?:generatePistonName())
        if (params.author || params.bin) {
        	piston.config([bin: params.bin, author: params.author])
        }
        result = [status: "ST_SUCCESS", id: hashId(piston.id)]
	} else {
    	result = api_get_error_result("ERR_INVALID_TOKEN")
    }
    render contentType: "application/javascript;charset=utf-8", data: "${params.callback}(${result.encodeAsJSON()})"
}

private api_intf_dashboard_piston_get() {
	def result
    debug "Dashboard: Request received to get piston ${params?.id}"
	if (verifySecurityToken(params.token)) {
        def pistonId = params.id
        def serverDbVersion = version()
        def clientDbVersion = params.db
        def requireDb = serverDbVersion != clientDbVersion
        if (pistonId) {
            result = api_get_base_result(requireDb ? 0 : params.dev)            
            def piston = getChildApps().find{ hashId(it.id) == pistonId };
            if (piston) {
            	result.data = piston.get() ?: [:] + [globalVars: listAvailableVariables()]
            }
            if (requireDb) {
                result.dbVersion = serverDbVersion
                result.db = [
                    capabilities: capabilities().sort{ it.value.d },
                    commands: [
                        physical: commands().sort{ it.value.d ?: it.value.n },
                        virtual: virtualCommands().sort{ it.value.d ?: it.value.n }
                    ],
                    attributes: attributes().sort{ it.key },
                    comparisons: comparisons(),
                    functions: functions(),
                    colors: [                
                        standard: colorUtil.ALL
                    ],
                ]
            }
        } else {
	    	result = api_get_error_result("ERR_INVALID_ID")
        }
	} else {
    	result = api_get_error_result("ERR_INVALID_TOKEN")
    }
    //for accuracy, use the time as close as possible to the render
    result.now = now()            
    render contentType: "application/javascript;charset=utf-8", data: "${params.callback}(${result.encodeAsJSON()})"
}


private api_intf_dashboard_piston_set_save(id, data) {
    def piston = getChildApps().find{ hashId(it.id) == id };
    if (piston) {
		def p = new groovy.json.JsonSlurper().parseText(new String(data.decodeBase64(), "UTF-8"))
		return piston.set(p);
    }
    return false;
}

//set is used for small pistons, for large data, using set.start, set.chunk, and set.end
private api_intf_dashboard_piston_set() {
	def result
    debug "Dashboard: Request received to set a piston"
	if (verifySecurityToken(params.token)) {
    	def data = params?.data
        //save the piston here
        def saved = api_intf_dashboard_piston_set_save(params?.id, data)
        if (saved) {
            result = [status: "ST_SUCCESS"] + saved
        } else {
            result = [status: "ST_ERROR", error: "ERR_UNKNOWN"]
        }
	} else {
    	result = api_get_error_result("ERR_INVALID_TOKEN")
    }
    render contentType: "application/javascript;charset=utf-8", data: "${params.callback}(${result.encodeAsJSON()})"
}

private api_intf_dashboard_piston_set_start() {
	def result
    debug "Dashboard: Request received to set a piston (chunked start)"
	if (verifySecurityToken(params.token)) {
    	def chunks = "${params?.chunks}";
        chunks = chunks.isInteger() ? chunks.toInteger() : 0;
        if ((chunks > 0) && (chunks < 100)) {
	        atomicState.chunks = [id: params?.id, count: chunks];
    		result = [status: "ST_READY"]
        } else {
    		result = [status: "ST_ERROR", error: "ERR_INVALID_CHUNK_COUNT"]
        }
	} else {
    	result = api_get_error_result("ERR_INVALID_TOKEN")
    }
    render contentType: "application/javascript;charset=utf-8", data: "${params.callback}(${result.encodeAsJSON()})"
}

private api_intf_dashboard_piston_set_chunk() {
	def result
    def chunk = "${params?.chunk}"
    chunk = chunk.isInteger() ? chunk.toInteger() : -1
    debug "Dashboard: Request received to set a piston chunk (#${1 + chunk}/${atomicState.chunks?.count})"
	if (verifySecurityToken(params.token)) {
    	def data = params?.data
        def chunks = atomicState.chunks
        if (chunks && chunks.count && (chunk >= 0) && (chunk < chunks.count)) {
        	chunks["chunk:$chunk"] = data;
            atomicState.chunks = chunks;
    		result = [status: "ST_READY"]
        } else {
    		result = [status: "ST_ERROR", error: "ERR_INVALID_CHUNK"]
        }
	} else {
    	result = api_get_error_result("ERR_INVALID_TOKEN")
    }
    render contentType: "application/javascript;charset=utf-8", data: "${params.callback}(${result.encodeAsJSON()})"
}

private api_intf_dashboard_piston_set_end() {
	def result
    debug "Dashboard: Request received to set a piston (chunked end)"
	if (verifySecurityToken(params.token)) {
    	def chunks = atomicState.chunks
        if (chunks && chunks.count) {
            def ok = true
            def data = ""
            def i = 0;
            def count = chunks.count;
            while(i<count) {
            	def s = chunks["chunk:$i"]
            	if (s) {
                	data += s
                } else {
                	data = ""
                	ok = false;
                    break;
                }
                i++
            }
            if (ok) {
                //save the piston here
                def saved = api_intf_dashboard_piston_set_save(chunks.id, data)
                if (saved) {
	        		result = [status: "ST_SUCCESS"] + saved
                } else {
	        		result = [status: "ST_ERROR", error: "ERR_UNKNOWN"]
                }
	        } else {
    			result = [status: "ST_ERROR", error: "ERR_INVALID_CHUNK"]
            }
        } else {
    		result = [status: "ST_ERROR", error: "ERR_INVALID_CHUNK"]
        }
	} else {
    	result = api_get_error_result("ERR_INVALID_TOKEN")
    }
    render contentType: "application/javascript;charset=utf-8", data: "${params.callback}(${result.encodeAsJSON()})"
}


private api_intf_dashboard_piston_pause() {
	def result
    debug "Dashboard: Request received to pause a piston"
	if (verifySecurityToken(params.token)) {
	    def piston = getChildApps().find{ hashId(it.id) == params.id };
	    if (piston) {
        	piston.pause()
			result = [status: "ST_SUCCESS", active: false]
        } else {
	    	result = api_get_error_result("ERR_INVALID_ID")
        }
	} else {
    	result = api_get_error_result("ERR_INVALID_TOKEN")
    }
    render contentType: "application/javascript;charset=utf-8", data: "${params.callback}(${result.encodeAsJSON()})"
}

private api_intf_dashboard_piston_resume() {
	def result
    debug "Dashboard: Request received to resume a piston"
	if (verifySecurityToken(params.token)) {
	    def piston = getChildApps().find{ hashId(it.id) == params.id };
	    if (piston) {
        	piston.resume()
			result = [status: "ST_SUCCESS", active: true]
        } else {
	    	result = api_get_error_result("ERR_INVALID_ID")
        }
	} else {
    	result = api_get_error_result("ERR_INVALID_TOKEN")
    }
    render contentType: "application/javascript;charset=utf-8", data: "${params.callback}(${result.encodeAsJSON()})"
}

private api_intf_dashboard_piston_delete() {
	def result
    debug "Dashboard: Request received to delete a piston"
	if (verifySecurityToken(params.token)) {
	    def piston = getChildApps().find{ hashId(it.id) == params.id };
	    if (piston) {
        	app.deleteChildApp(piston);
			result = [status: "ST_SUCCESS"]
        } else {
	    	result = api_get_error_result("ERR_INVALID_ID")
        }
	} else {
    	result = api_get_error_result("ERR_INVALID_TOKEN")
    }
    render contentType: "application/javascript;charset=utf-8", data: "${params.callback}(${result.encodeAsJSON()})"
}

private api_intf_dashboard_piston_evaluate() {
	def result
    debug "Dashboard: Request received to evaluate an expression"
	if (verifySecurityToken(params.token)) {
	    def piston = getChildApps().find{ hashId(it.id) == params.id };
	    if (piston) {
			def expression = new groovy.json.JsonSlurper().parseText(new String(params.expression.decodeBase64(), "UTF-8"))
            def msg = timer "Evaluating expression"
			result = [status: "ST_SUCCESS", value: piston.proxyEvaluateExpression(getRunTimeData(), expression, params.dataType)]
            trace msg
        } else {
	    	result = api_get_error_result("ERR_INVALID_ID")
        }
	} else {
    	result = api_get_error_result("ERR_INVALID_TOKEN")
    }
    render contentType: "application/javascript;charset=utf-8", data: "${params.callback}(${result.encodeAsJSON()})"
}

private api_intf_dashboard_piston_activity() {
	def result
	if (verifySecurityToken(params.token)) {
	    def piston = getChildApps().find{ hashId(it.id) == params.id };
	    if (piston) {
			result = [status: "ST_SUCCESS", activity: piston.activity(params.log)]
        } else {
	    	result = api_get_error_result("ERR_INVALID_ID")
        }
	} else {
    	result = api_get_error_result("ERR_INVALID_TOKEN")
    }
    render contentType: "application/javascript;charset=utf-8", data: "${params.callback}(${result.encodeAsJSON()})"
}


















/******************************************************************************/
/*** 																		***/
/*** PRIVATE METHODS														***/
/*** 																		***/
/******************************************************************************/

private Map listAvailableDevices(raw = false) {
    def devices = [:]
    for (devs in settings.findAll{ it.key.startsWith("dev:") }) {
        for(dev in devs.value) {
        	def devId = hashId(dev.id);
        	if (!devices[devId]) {
            	if (raw) {
                    devices[devId] = dev
                } else {
                	devices[devId] = [n: dev.getDisplayName(), cn: dev.getCapabilities()*.name, a: dev.getSupportedAttributes().unique{ it.getName() }.collect{[n: it.getName(), t: it.getDataType(), o: it.getValues()]}, c: dev.getSupportedCommands().collect{[n: it.getName(), p: it.getArguments()]}]
                }
            }
        }
    }
    return devices
}

private Map listAvailableVariables() {
	return atomicState.vars ?: [:]
}

private void initTokens() {
    debug "Dashboard: Initializing security tokens"
	atomicState.securityTokens = [:]
}

private Boolean verifySecurityToken(tokenId) {
	def tokens = atomicState.securityTokens
    if (!tokens) return false
    def threshold = now()
    def modified = false
    //remove all expired tokens
    for (token in tokens.findAll{ it.value < threshold }) {
    	tokens.remove(token.key)
        modified = true
    }
    if (modified) {
    	atomicState.securityTokens = tokens
    }
	def token = tokens[tokenId]
    if (!token || token < now()) {
        error "Dashboard: Authentication failed due to an invalid token"
    	return false
    }
    return true
}

private String createSecurityToken() {
    trace "Dashboard: Generating new security token after a successful PIN authentication"
	def token = UUID.randomUUID().toString()
    def tokens = atomicState.securityTokens ?: [:]
    long expiry = 0
    def eo = "$settings.expiry".toLowerCase().replace("every ", "").replace("(recommended)", "").replace("(not recommended)", "").trim()
    switch (eo) {
		case "hour": expiry = 3600; break;
        case "day": expiry = 86400; break;
        case "week": expiry = 604800; break;
        case "month": expiry = 2592000; break;
        case "three months": expiry = 7776000; break;
        case "never": expiry = 3110400000; break; //never means 100 years, okay?
	}
    tokens[token] = now() + (expiry * 1000)
    atomicState.securityTokens = tokens
    state.securityTokens = tokens
    return token
}

private String generatePistonName() {
	def apps = getChildApps()
	def i = 1
	while (true) {
		def name = i == 5 ? "Mambo No. 5" : "${handle()} Piston #$i"
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
/*** 																		***/
/*** PUBLIC METHODS															***/
/*** 																		***/
/******************************************************************************/

public String mem(showBytes = true) {
	def bytes = atomicState.toString().length()
	return Math.round(100.00 * (bytes/ 100000.00)) + "%${showBytes ? " ($bytes bytes)" : ""}"
}

public Map getRunTimeData() {
	//def msg = timer "Generated run time data"
	Map result = [
    	logging: settings.logging,
    	attributes: attributes(),
        commands: commands(),
		commands: [
        	physical: commands(),
			virtual: virtualCommands()
		],
        comparisons: comparisons(),
        coreVersion: version(),
    	devices: listAvailableDevices(true),
        globalVars: listAvailableVariables()
    ]
    //trace msg
    return result
}

public void updateRunTimeData(data) {
	List events = []
	Map vars = atomicState.vars ?: [:]
    def modified = false
    if (data && data.vars) {
    	for(var in data.vars) {
        	if (var.n && (vars[var.n]) && (var.v != vars[var.n].v)) {
            	events.push([v: var.n, ov: vars[var.n].v, nv: var.v])
            	vars[var.n].v = var.v
                modified = true
            }
        }
	}
    if (modified) {
    	atomicState.vars = vars
    }
    //broadcast variable change events
    //todo
}

/******************************************************************************/
/***																		***/
/*** SECURITY METHODS														***/
/***																		***/
/******************************************************************************/
def String md5(String md5) {
   try {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5")
        byte[] array = md.digest(md5.getBytes())
        def result = ""
        for (int i = 0; i < array.length; ++i) {
          result += Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3)
       }
        return result
    } catch (java.security.NoSuchAlgorithmException e) {
    }
    return null;
}

def String hashId(id) {
	//enabled hash caching for faster processing
	def result = state.hash ? state.hash[id] : null
    if (!result) {
		result = ":${md5("core." + id)}:"
        def hash = state.hash ?: [:]
        hash[id] = result
        state.hash = hash
    }
    return result
}

def String temperatureUnit() {
	return "°" + location.temperatureScale;
}

/******************************************************************************/
/*** DEBUG FUNCTIONS														***/
/******************************************************************************/
private debug(message, shift = null, err = null, cmd = null) {
    if (cmd == "timer") {
    	return [m: message, t: now(), s: shift, e: err]
    }
    if (message instanceof Map) {
    	shift = message.s
        err = message.e
        message = message.m + " (${now() - message.t}ms)"
    }
	if (!settings.logging && (cmd != "error")) {
		return
	}
	cmd = cmd ? cmd : "debug"
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
private info(message, shift = null, err = null) { debug message, shift, err, 'info' }
private trace(message, shift = null, err = null) { debug message, shift, err, 'trace' }
private warn(message, shift = null, err = null) { debug message, shift, err, 'warn' }
private error(message, shift = null, err = null) { debug message, shift, err, 'error' }
private timer(message, shift = null, err = null) { debug message, shift, err, 'timer' }









/******************************************************************************/
/*** DATABASE																***/
/******************************************************************************/

private static Map capabilities() {
    //n = name
    //d = friendly devices name
    //a = default attribute
    //c = accepted commands
    //m = momentary
    //s = number of subdevices
    //i = subdevice index in event data
	return [
		accelerationSensor			: [ n: "Acceleration Sensor",			d: "acceleration sensors",			a: "acceleration",																																																							],
		actuator					: [ n: "Actuator", 						d: "actuators",																																																																	],
		alarm						: [ n: "Alarm",							d: "alarms and sirens",				a: "alarm",								c: ["off", "strobe", "siren", "both"],																																								],
		audioNotification			: [ n: "Audio Notification",			d: "audio notification devices",											c: ["playText", "playTextAndResume", "playTextAndRestore", "playTrack", "playTrackAndResume", "playTrackAndRestore"],				 																],
		battery						: [ n: "Battery",						d: "battery powered devices",		a: "battery",																																																								],
		beacon						: [ n: "Beacon",						d: "beacons",						a: "presence",																																																								],
		bulb						: [ n: "Bulb",							d: "bulbs",							a: "switch",							c: ["off", "on"],																																													],
		button						: [ n: "Button",						d: "buttons",						a: "button",				m: true,	s: "numberOfButtons,numButtons", i: "buttonNumber",																																					],
		carbonDioxideMeasurement	: [ n: "Carbon Dioxide Measurement",	d: "carbon dioxide sensors",		a: "carbonDioxide",																																																							],
		carbonMonoxideDetector		: [ n: "Carbon Monoxide Detector",		d: "carbon monoxide detectors",		a: "carbonMonoxide",																																																						],
		colorControl				: [ n: "Color Control",					d: "adjustable color lights",		a: "color",								c: ["setColor", "setHue", "setSaturation"],																																							],
		colorTemperature			: [ n: "Color Temperature",				d: "adjustable white lights",		a: "colorTemperature",					c: ["setColorTemperature"],																																											],
		configuration				: [ n: "Configuration",					d: "configurable devices",													c: ["configure"],																																													],
		consumable					: [ n: "Consumable",					d: "consumables",					a: "consumableStatus",					c: ["setConsumableStatus"],																																											],
		contactSensor				: [ n: "Contact Sensor",				d: "contact sensors",				a: "contact",																																																								],
		doorControl					: [ n: "Door Control",					d: "automatic doors",				a: "door",								c: ["close", "open"],																																												],
		energyMeter					: [ n: "Energy Meter",					d: "energy meters",					a: "energy",																																																								],
		estimatedTimeOfArrival		: [ n: "Estimated Time of Arrival", 	d: "moving devices (ETA)",			a: "eta",																																																									],
		garageDoorControl			: [ n: "Garage Door Control",			d: "automatic garage doors",		a: "door",								c: ["close", "open"],																																												],
		holdableButton				: [ n: "Holdable Button",				d: "holdable buttons",				a: "button",				m: true,	s: "numberOfButtons,numButtons", i: "buttonNumber",																																					],
		illuminanceMeasurement		: [ n: "Illuminance Measurement",		d: "illuminance sensors",			a: "illuminance",																																																							],
		imageCapture				: [ n: "Image Capture",					d: "cameras, imaging devices",		a: "image",								c: ["take"],																																														],
		indicator					: [ n: "Indicator",						d: "indicator devices",				a: "indicatorStatus",					c: ["indicatorNever", "indicatorWhenOn", "indicatorWhenOff"],																																		],
		infraredLevel				: [ n: "Infrared Level",				d: "adjustable infrared lights",	a: "infraredLevel",						c: ["setInfraredLevel"],																																											],
		light						: [ n: "Light",							d: "lights",						a: "switch",							c: ["off", "on"],																		 																											],
		lock						: [ n: "Lock",							d: "electronic locks",				a: "lock",								c: ["lock", "unlock"],	s:"numberOfCodes,numCodes", i: "usedCode", 																									 								],
		lockOnly					: [ n: "Lock Only",						d: "electronic locks (lock only)",	a: "lock",								c: ["lock"],																																														],
		mediaController				: [ n: "Media Controller",				d: "media controllers",				a: "currentActivity",					c: ["startActivity", "getAllActivities", "getCurrentActivity"],																																		],
		momentary					: [ n: "Momentary",						d: "momentary switches",													c: ["push"],																																														],
		motionSensor				: [ n: "Motion Sensor",					d: "motion sensors",				a: "motion",																																																								],
        musicPlayer					: [ n: "Music Player",					d: "music players",					a: "status",							c: ["mute", "nextTrack", "pause", "play", "playTrack", "previousTrack", "restoreTrack", "resumeTrack", "setLevel", "setTrack", "stop", "unmute"],													],
		notification				: [ n: "Notification",					d: "notification devices",													c: ["deviceNotification"],																																											],
		outlet						: [ n: "Outlet",						d: "lights",						a: "switch",							c: ["off", "on"],																																										 			],
		pHMeasurement				: [ n: "pH Measurement",				d: "pH sensors",					a: "pH",																																																									],
        polling						: [ n: "Polling",						d: "pollable devices",														c: ["poll"],																																														],
		powerMeter					: [ n: "Power Meter",					d: "power meters",					a: "power",																																																									],
		powerSource					: [ n: "Power Source",					d: "multisource powered devices",	a: "powerSource",																																																							],
		presenceSensor				: [ n: "Presence Sensor",				d: "presence sensors",				a: "presence",																																																								],
		refresh						: [ n: "Refresh",						d: "refreshable devices",													c: ["refresh"],																																														],
		relativeHumidityMeasurement	: [ n: "Relative Humidity Measurement",	d: "humidity sensors",				a: "humidity",																																																								],
		relaySwitch					: [ n: "Relay Switch",					d: "relay switches",				a: "switch",							c: ["off", "on"],																																													],
		sensor						: [ n: "Sensor",						d: "sensors",						a: "sensor",																																																								],
		shockSensor					: [ n: "Shock Sensor",					d: "shock sensors",					a: "shock",																																																									],
		signalStrength				: [ n: "Signal Strength",				d: "wireless devices",				a: "rssi",																																																									],
		sleepSensor					: [ n: "Sleep Sensor",					d: "sleep sensors",					a: "sleeping",																																																								],
		smokeDetector				: [ n: "Smoke Detector",				d: "smoke detectors",				a: "smoke",																																																									],
		soundPressureLevel			: [ n: "Sound Pressure Level",			d: "sound pressure sensors",		a: "soundPressureLevel",																																																					],
		soundSensor					: [ n: "Sound Sensor",					d: "sound sensors",					a: "sound",																																																									],
		speechRecognition			: [ n: "Speech Recognition",			d: "speech recognition devices",	a: "phraseSpoken",			m: true,																																																		],
		speechSynthesis				: [ n: "Speech Synthesis",				d: "speech synthesizers",													c: ["speak"],																																														],
		stepSensor					: [ n: "Step Sensor",					d: "step counters",					a: "steps",																																																									],
		switch						: [ n: "Switch",						d: "switches",						a: "switch",							c: ["off", "on"],																																										 			],
		switchLevel					: [ n: "Switch Level",					d: "dimmers and dimmable lights",	a: "level",								c: ["setLevel"],																																													],
		tamperAlert					: [ n: "Tamper Alert",					d: "tamper sensors",				a: "tamper",																																																								],
		temperatureMeasurement		: [ n: "Temperature Measurement",		d: "temperature sensors",			a: "temperature",																																																							],
		thermostat					: [ n: "Thermostat",					d: "thermostats",									a: "thermostateMode",					c: ["auto", "cool", "emergencyHeat", "fanAuto", "fanCirculate", "fanOn", "heat", "off", "setCoolingSetpoint", "setHeatingSetpoint", "setSchedule", "setThermostatFanMode", "setThermostatMode"],	],
		thermostatCoolingSetpoint	: [ n: "Thermostat Cooling Setpoint",	d: "thermostats (cooling)",			a: "coolingSetpoint",					c: ["setCoolingSetpoint"],																																											],
		thermostatFanMode			: [ n: "Thermostat Fan Mode",			d: "fans",							a: "thermostatFanMode",					c: ["fanAuto", "fanCirculate", "fanOn", "setThermostatFanMode"],																																	],
		thermostatHeatingSetpoint	: [ n: "Thermostat Heating Setpoint",	d: "thermostats (heating)",			a: "heatingSetpoint",					c: ["setHeatingSetpoint"],																																											],
		thermostatMode				: [ n: "Thermostat Mode",													a: "thermostatMode",					c: ["auto", "cool", "emergencyHeat", "heat", "off", "setThermostatMode"],																															],
		thermostatOperatingState	: [ n: "Thermostat Operating State",										a: "thermostatOperatingState",																																																				],
		thermostatSetpoint			: [ n: "Thermostat Setpoint",												a: "thermostatSetpoint",																																																					],
		threeAxis					: [ n: "Three Axis Sensor",				d: "three axis sensors",			a: "orientation",																																																							],
		timedSession				: [ n: "Timed Session",					d: "timers",						a: "sessionStatus",						c: ["cancel", "pause", "setTimeRemaining", "start", "stop", ],																																		],
		tone						: [ n: "Tone",							d: "tone generators",														c: ["beep"],																																														],
		touchSensor					: [ n: "Touch Sensor",					d: "touch sensors",					a: "touch",																																																									],
		ultravioletIndex			: [ n: "Ultraviolet Index",				d: "ultraviolet sensors",			a: "ultravioletIndex",																																																						],
		valve						: [ n: "Valve",							d: "valves",						a: "valve",								c: ["close", "open"],																																												],
		voltageMeasurement			: [ n: "Voltage Measurement",			d: "voltmeters",					a: "voltage",																																																								],
		waterSensor					: [ n: "Water Sensor",					d: "water and leak sensors",		a: "water",																																																									],
		windowShade					: [ n: "Window Shade",					d: "automatic window shades",		a: "windowShade",						c: ["close", "open", "presetPosition"],																																								],
	]
}

private Map attributes() {
	return [
		acceleration				: [ n: "acceleration",			t: "enum",		o: ["active", "inactive"],																			],
		activities					: [ n: "activities", 			t: "object",																										],
		alarm						: [ n: "alarm", 				t: "enum",		o: ["both", "off", "siren", "strobe"],																],
		axisX						: [ n: "X axis",				t: "number",	r: [-1024, 1024],	s: "threeAxis",																	],
		axisY						: [ n: "Y axis",				t: "number",	r: [-1024, 1024],	s: "threeAxis",																	],
		axisZ						: [ n: "Z axis",				t: "number",	r: [-1024, 1024],	s: "threeAxis",																	],
		battery						: [ n: "battery", 				t: "number",	r: [0, 100],		u: "%",																			],
		button						: [ n: "button", 				t: "enum",		o: ["pushed", "held"],									c: "button",					m: true, s: "numberOfButtons,numButtons", i: "buttonNumber"		],
		carbonDioxide				: [ n: "carbon dioxide",		t: "decimal",	r: [0, null],																						],
		carbonMonoxide				: [ n: "carbon monoxide",		t: "enum",		o: ["clear", "detected", "tested"],																	],
		color						: [ n: "color",					t: "color",																											],
		colorTemperature			: [ n: "color temperature",		t: "integer",	r: [1000, 30000],	u: "°K",																		],
		consumableStatus			: [ n: "consumable status",		t: "enum",		o: ["good", "maintenance_required", "missing", "order", "replace"],									],
		contact						: [ n: "contact",				t: "enum",		o: ["closed", "open"],																				],
		coolingSetpoint				: [ n: "cooling setpoint",		t: "decimal",	r: [-127, 127],		u: temperatureUnit(),															],
		currentActivity				: [ n: "current activity",		t: "string",																										],
		door						: [ n: "door",					t: "enum",		o: ["closed", "closing", "open", "opening", "unknown"],					i: true,					],
		energy						: [ n: "energy",				t: "decimal",	r: [0, null],		u: "kWh",																		],
		eta							: [ n: "ETA",					t: "datetime",																										],
		goal						: [ n: "goal",					t: "integer",	r: [0, null],																						],
		heatingSetpoint				: [ n: "heating setpoint",		t: "decimal",	r: [-127, 127],		u: temperatureUnit(),															],
		hex							: [ n: "hexadecimal code",		t: "hexcolor",																										],
		holdableButton				: [ n: "holdable button",		t: "enum",		o: ["held", "pushed"],								c: "holdableButton",			m: true,		],
		hue							: [ n: "hue",					t: "integer",	r: [0, 360],		u: "°",																			],
		humidity					: [ n: "relative humidity",		t: "integer",	r: [0, 100],		u: "%",																			],
		illuminance					: [ n: "illuminance",			t: "integer",	r: [0, null],		u: "lux",																		],
		image						: [ n: "image",					t: "image",																											],
		indicatorStatus				: [ n: "indicator status",		t: "enum",		o: ["never", "when off", "when on"],																],
		infraredLevel				: [ n: "infrared level",		t: "integer",	r: [0, 100],		u: "%",																			],
		level						: [ n: "level",					t: "integer",	r: [0, 100],		u: "%",																			],
		lock						: [ n: "lock",					t: "enum",		o: ["locked", "unknown", "unlocked", "unlocked with timeout"],	c: "lock",			 i: true,		],
		lqi							: [ n: "link quality",			t: "integer",	r: [0, 255],																						],
		motion						: [ n: "motion",				t: "enum",		o: ["active", "inactive"],																			],
		mute						: [ n: "mute",					t: "enum",		o: ["muted", "unmuted"],																			],
		orientation					: [ n: "orientation",			t: "enum",		o: threeAxisOrientations(),	s: "threeAxis",															],
		pH							: [ n: "pH level",				t: "decimal",	r: [0, 14],																							],
		phraseSpoken				: [ n: "phrase",				t: "string",																										],
		power						: [ n: "power",					t: "decimal",	r: [0, null],		u: "W",																			],
		powerSource					: [ n: "power source",			t: "enum",		o: ["battery", "dc", "mains", "unknown"],															],
		presence					: [ n: "presence",				t: "enum",		o: ["not present", "present"],																		],
		rssi						: [ n: "signal strength",		t: "integer",	r: [0, 100],		u: "%",																			],
		saturation					: [ n: "saturation",			t: "integer",	r: [0, 100],		u: "%",																			],
		schedule					: [ n: "schedule",				t: "object",																										],
		sessionStatus				: [ n: "session status",		t: "enum",		o: ["canceled", "paused", "running", "stopped"],													],
		shock						: [ n: "shock",					t: "enum",		o: ["clear", "detected"],																			],
		sleeping					: [ n: "sleeping",				t: "enum",		o: ["not sleeping", "sleeping"],																	],
		smoke						: [ n: "smoke",					t: "enum",		o: ["clear", "detected", "tested"],																	],
		sound						: [ n: "sound",					t: "enum",		o: ["detected", "not detected"],																	],
		soundPressureLevel			: [ n: "sound pressure level",	t: "integer",	r: [0, null],		u: "dB",																		],
		status						: [ n: "status",				t: "string",																										],
		steps						: [ n: "steps",					t: "number",	r: [0, null],																						],
		switch						: [ n: "switch",				t: "enum",		o: ["off", "on"],														i: true,					],
		tamper						: [ n: "tamper",				t: "enum",		o: ["clear", "detected"],																			],
		temperature					: [ n: "temperature",			t: "decimal",	r: [-460, 10000],	u: temperatureUnit(),															],
		thermostatFanMode			: [ n: "fan mode",				t: "enum",		o: ["auto", "circulate", "on"],																		],
		thermostatMode				: [ n: "thermostat mode",		t: "enum",		o: ["auto", "cool", "emergency heat", "heat", "off"],												],
		thermostatOperatingState	: [ n: "operating state",		t: "enum",		o: ["cooling", "fan only", "heating", "idle", "pending cool", "pending heat", "vent economizer"],	],
		thermostatSetpoint			: [ n: "setpoint",				t: "decimal",	r: [-127, 127],		u: temperatureUnit(),															],
		threeAxis					: [ n: "vector",				t: "vector3",																										],
		timeRemaining				: [ n: "time remaining",		t: "integer",	r: [0, null],		u: "s",																			],
		touch						: [ n: "touch",					t: "enum",		o: ["touched"],																						],
		trackData					: [ n: "track data",			t: "object",																										],
		trackDescription			: [ n: "track description",		t: "string",																										],
		ultravioletIndex			: [ n: "UV index",				t: "integer",	r: [0, null],																						],
		valve						: [ n: "valve",					t: "enum",		o: ["closed", "open"],																				],
		voltage						: [ n: "voltage",				t: "decimal",	r: [null, null],	u: "V",																			],
		water						: [ n: "water",					t: "enum",		o: ["dry", "wet"],																					],
		windowShade					: [ n: "window shade",			t: "enum",		o: ["closed", "closing", "open", "opening", "partially open", "unknown"],							],
	]
}

private static Map commands() {
	return [
		auto						: [ n: "Set to Auto",																	a: "thermostatMode",				v: "auto",																																			],
		beep						: [ n: "Beep",																																																																	],
		both						: [ n: "Strobe and Siren",																a: "alarm",							v: "both",																																			],
		cancel						: [ n: "Cancel",																																																																],
		close						: [ n: "Close",																			a: "door|valve|windowShade",		v: "close",																																			],
		configure					: [ n: "Configure",																																																																],
		cool						: [ n: "Set to Cool",																	a: "thermostatMode",				v: "cool",																																			],
		deviceNotification			: [ n: "Send device notification...",	d: "Send device notification \"{0}\"",																		p: [[n:"Message",t:"string"]],  																							],
		emergencyHeat				: [ n: "Set to Emergency Heat",															a: "thermostatMode",				v: "emergencyHeat",																																	],
		fanAuto						: [ n: "Set fan to Auto",																a: "thermostatFanMode",				v: "auto",																																			],
		fanCirculate				: [ n: "Set fan to Circulate",															a: "thermostatFanMode",				v: "circulate",																																		],
		fanOn						: [ n: "Set fan to On",																	a: "thermostatFanMode",				v: "on",																																			],
		getAllActivities			: [ n: "Get all activities",																																																													],
		getCurrentActivity			: [ n: "Get current activity",																																																													],
		heat						: [ n: "Set to Heat",																	a: "thermostatMode",				v: "heat",																																			],
		indicatorNever				: [ n: "Disable indicator",																																																														],
		indicatorWhenOff			: [ n: "Enable indicator when off",																																																												],
		indicatorWhenOn				: [ n: "Enable indicator when on",																																																												],
		lock						: [ n: "Lock",																			a: "lock",							v: "locked",																																		],
		mute						: [ n: "Mute",																			a: "mute",							v: "muted",																																			],
		nextTrack					: [ n: "Next track",																																																															],
		off							: [ n: "Turn off",																		a: "switch|alarm|thermostatMode",	v: "off",																																			],
		on							: [ n: "Turn on",																		a: "switch",						v: "on",																																			],
		open						: [ n: "Open",																			a: "door|valve|windowShade",		v: "open",																																			],
		pause						: [ n: "Pause",																																																																	],
		play						: [ n: "Play",																																																																	],
		playText					: [ n: "Speak text...",					d: "Speak text \"{0}\"{1}",																					p: [[n:"Text",t:"string"], [n:"Volume", t:"level", d:" at volume {v}"]],  													],
		playTextAndRestore			: [ n: "Speak text...",					d: "Speak text \"{0}\"{1} and restore",																		p: [[n:"Text",t:"string"], [n:"Volume", t:"level", d:" at volume {v}"]],  													],
		playTextAndResume			: [ n: "Speak text...",					d: "Speak text \"{0}\"{1} and resume",																		p: [[n:"Text",t:"string"], [n:"Volume", t:"level", d:" at volume {v}"]],  													],
		playTrack					: [ n: "Play track...",					d: "Play track <uri>{0}</uri>{1}",																			p: [[n:"Track URL",t:"url"], [n:"Volume", t:"level", d:" at volume {v}"]],  												],
		playTrackAndRestore			: [ n: "Play track...",					d: "Play track <uri>{0}</uri>{1} and restore",																p: [[n:"Track URL",t:"url"], [n:"Volume", t:"level", d:" at volume {v}"]],  												],
		playTrackAndResume			: [ n: "Play track...",					d: "Play track <uri>{0}</uri>{1} and resume",																p: [[n:"Track URL",t:"url"], [n:"Volume", t:"level", d:" at volume {v}"]],  												],
		poll						: [ n: "Poll",																																																																	],
		presetPosition				: [ n: "Move to preset position",														a: "windowShade",					v: "partially open",																																],
		previousTrack				: [ n: "Previous track",																																																														],
		push						: [ n: "Push",																																																																	],
		refresh						: [ n: "Refresh",																																																																],
		restoreTrack				: [ n: "Restore track...",				d: "Restore track <uri>{0}</uri>",																			p: [[n:"Track URL",t:"url"]],  																								],
		resumeTrack					: [ n: "Resume track...",				d: "Resume track <uri>{0}</uri>",																			p: [[n:"Track URL",t:"url"]],  																								],
		setColor					: [ n: "Set color...",					d: "Set color to {0}{1}",						a: "color",													p: [[n:"Color",t:"color"], [n:"Only if already", t:"enum",o:["on","off"], d:" if already {v}"]],  							],
		setColorTemperature			: [ n: "Set color temperature...",		d: "Set color temperature to {0}°K{1}",			a: "colorTemperature",										p: [[n:"Color Temperature", t:"colorTemperature"], [n:"Only if already", t:"enum",o:["on","off"], d:" if already {v}"]],	],
		setConsumableStatus			: [ n: "Set consumable status...",		d: "Set consumable status to {0}",																			p: [[n:"Status", t:"consumable"]],																							],
		setCoolingSetpoint			: [ n: "Set cooling point...",			d: "Set cooling point at {0}{T}",				a: "thermostatCoolingSetpoint",								p: [[n:"Desired temperature", t:"thermostatSetpoint"]], 																	],
		setHeatingSetpoint			: [ n: "Set heating point...",			d: "Set heating point at {0}{T}",				a: "thermostatHeatingSetpoint",								p: [[n:"Desired temperature", t:"thermostatSetpoint"]], 																	],
		setHue						: [ n: "Set hue...",					d: "Set hue to {0}°{1}",						a: "hue",													p: [[n:"Hue", t:"hue"], [n:"Only if already", t:"enum",o:["on","off"], d:" if already {v}"]], 								],
		setInfraredLevel			: [ n: "Set infrared level...",			d: "Set infrared level to {0}%{1}",				a: "infraredLevel",											p: [[n:"Level",t:"infraredLevel"], [n:"Only if already", t:"enum",o:["on","off"], d:" if already {v}"]], 					],
		setLevel					: [ n: "Set level...",					d: "Set level to {0}%{1}",						a: "level",													p: [[n:"Level",t:"level"], [n:"Only if already", t:"enum",o:["on","off"], d:" if already {v}"]], 							],
		setSaturation				: [ n: "Set saturation...",				d: "Set saturation to {0}{1}",					a: "saturation",											p: [[n:"Saturation", t:"saturation"], [n:"Only if already", t:"enum",o:["on","off"], d:" if already {v}"]],					],
		setSchedule					: [ n: "Set thermostat schedule...",	d: "Set schedule to {0}",						a: "schedule",												p: [[n:"Schedule", t:"object"]],																							],
		setThermostatFanMode		: [ n: "Set fan mode...",				d: "Set fan mode to {0}",						a: "thermostatFanMode",										p: [[n:"Fan mode", t:"thermostatFanMode"]],																					],
		setThermostatMode			: [ n: "Set thermostat mode...",		d: "Set thermostate mode to {0}",				a: "thermostatMode",										p: [[n:"Thermostat mode",t:"thermostatMode"]],																				],
		setTimeRemaining			: [ n: "Set remaining time...",			d: "Set remaining time to {0}s",				a: "timeRemaining",											p: [[n:"Remaining time [seconds]", t:"number"]],																						],
		setTrack					: [ n: "Set track...",					d: "Set track to <uri>{0}</uri>",																			p: [[n:"Track URL",t:"url"]], 																								],
		siren						: [ n: "Siren",																			a: "alarm",							v: "siren",																																			],
		speak						: [ n: "Speak...",						d: "Speak \"{0}\"",																							p: [[n:"Message", t:"string"]],																								],
		start						: [ n: "Start",																																																																	],
		startActivity				: [ n: "Start activity...",				d: "Start activity \"{0}\"",																				p: [[n:"Activity", t:"string"]],																							],
		stop						: [ n: "Stop",																																																																	],
		strobe						: [ n: "Strobe",																		a: "alarm",							v: "strobe",																																		],
		take						: [ n: "Take a picture",																																																														],
		unlock						: [ n: "Unlock",																		a: "lock",							v: "unlocked",																																		],
		unmute						: [ n: "Unmute",																		a: "mute",							v: "unmuted",																																		],
		/* predfined commands below */
		//general
		quickSetCool				: [ n: "Quick set cooling point...",	d: "Set quick cooling point at {0}{T}",																		p: [[n:"Desired temperature",t:"thermostatSetpoint"]],																		],
		quickSetHeat				: [ n: "Quick set heating point...",	d: "Set quick heating point at {0}{T}",																		p: [[n:"Desired temperature",t:"thermostatSetpoint"]],																		],
		toggle						: [ n: "Toggle",																																																																],
		reset						: [ n: "Reset",																																																																	],
		//hue
		startLoop					: [ n: "Start color loop",																																																														],
		stopLoop					: [ n: "Stop color loop",																																																														],
		setLoopTime					: [ n: "Set loop duration...",			d: "Set loop duration to {0}",																				p: [[n:"Duration", t:"duration"]]																							],
		setDirection				: [ n: "Switch loop direction",																																																													],
		alert						: [ n: "Alert with lights...",			d: "Alert \"{0}\" with lights",																				p: [[n:"Alert type", t:"enum", o:["Blink","Breathe","Okay","Stop"]]], 														],
		setAdjustedColor			: [ n: "Transition to color...",		d: "Transition to color {0} in {1}",																		p: [[n:"Color", t:"color"], [n:"Duration",t:"duration"]],																	],
		//harmony
		allOn						: [ n: "Turn all on",																																																															],
		allOff						: [ n: "Turn all off",																																																															],
		hubOn						: [ n: "Turn hub on",																																																															],
		hubOff						: [ n: "Turn hub off",																																																															],
		//blink camera
		enableCamera				: [ n: "Enable camera",																																																															],
		disableCamera				: [ n: "Disable camera",																																																														],
		monitorOn					: [ n: "Turn monitor on",																																																														],
		monitorOff					: [ n: "Turn monitor off",																																																														],
		ledOn						: [ n: "Turn LED on",																																																															],
		ledOff						: [ n: "Turn LED off",																																																															],
		ledAuto						: [ n: "Set LED to Auto",																																																														],
		setVideoLength				: [ n: "Set video length...",			d: "Set video length to {0}", 																				p: [[n:"Duration", t:"duration"]], 																							],
		//dlink camera
		pirOn						: [ n: "Enable PIR motion detection",																																																											],
		pirOff						: [ n: "Disable PIR motion detection",																																																											],
		nvOn						: [ n: "Set Night Vision to On",																																																												],
		nvOff						: [ n: "Set Night Vision to Off",																																																												],
		nvAuto						: [ n: "Set Night Vision to Auto",																																																												],
		vrOn						: [ n: "Enable local video recording",																																																											],
		vrOff						: [ n: "Disable local video recording",																																																											],
		left						: [ n: "Pan camera left",																																																														],
		right						: [ n: "Pan camera right",																																																														],
		up							: [ n: "Pan camera up",																																																															],
		down						: [ n: "Pan camera down",																																																														],
		home						: [ n: "Pan camera to the Home",																																																												],
		presetOne					: [ n: "Pan camera to preset #1",																																																												],
		presetTwo					: [ n: "Pan camera to preset #2",																																																												],
		presetThree					: [ n: "Pan camera to preset #3",																																																												],
		presetFour					: [ n: "Pan camera to preset #4",																																																												],
		presetFive					: [ n: "Pan camera to preset #5",																																																												],
		presetSix					: [ n: "Pan camera to preset #6",																																																												],
		presetSeven					: [ n: "Pan camera to preset #7",																																																												],
		presetEight					: [ n: "Pan camera to preset #8",																																																												],
		presetCommand				: [ n: "Pan camera to preset...",		d: "Pan camera to preset #{0}",																				p: [[n:"Preset #", t:"number",r:[1,99]]], 																					],
		//zwave fan speed control by @pmjoen
		low							: [ n: "Set to Low",																																																															],
		med							: [ n: "Set to Medium",																																																															],
		high						: [ n: "Set to High",																																																															],
	]
}

private virtualCommands() {
	//a = aggregate
    //d = display
	//n = name
    //t = type
	return [
		noop				: [	n: "No operation",				a: true,	d: "No operation",																										],
		wait				: [	n: "Wait...", 					a: true,	d: "Wait {0}",															p: [[n:"Duration", t:"duration"]],				],
		waitRandom			: [ n: "Wait randomly...",			a: true,	d: "Wait randomly between {0} and {1}",									p: [[n:"At least", t:"duration"],[n:"At most", t:"duration"]],	],
		toggle				: [ n: "Toggle", r: ["on", "off"], 					],
		toggleLevel			: [ n: "Toggle level...", 						d: "Toggle level between 0% and {0}%",	r: ["on", "off", "setLevel"],	p: [[n:"Level", t:"level"]],																																	],
		sendNotification	: [ n: "Send notification...",		a: true,	d: "Send notification \"{0}\"",											p: [[n:"Message", t:"string"]],												],
		sendPushNotification: [ n: "Send PUSH notification...",	a: true,	d: "Send PUSH notification \"{0}\"{1}",									p: [[n:"Message", t:"string"],[n:"Store in Messages", t:"boolean", d:" and store in Messages"]],	],
		sendSMSNotification	: [ n: "Send SMS notification...",	a: true,	d: "Send SMS notification \"{0}\" to {1}{2}",							p: [[n:"Message", t:"string"],[n:"Phone number",t:"phone"],[n:"Store in Messages", t:"boolean", d:" and store in Messages"]],	],
		log					: [ n: "Log to console...",			a: true,	d: "Log {0} \"{1}\"",													p: [[n:"Log type", t:"enum", o:["info","trace","debug","warn","error"]],[n:"Message",t:"string"]],	],
		httpRequest			: [ n: "Make a web request",		a: true, 	d: "Make a {1} request to {0}",									        p: [[n:"URL", t:"string"],[n:"Method", t:"enum", o:["GET","POST","PUT","DELETE","HEAD"]],[n:"Content", t:"enum", o:["JSON","FORM"]],[n:"Send variables", t:"variables", d:" and data {v}"],[n:"Import response data into variables", t:"boolean"],[n:"Variable import name prefix", t:"string"]],	],


/*		[ n: "waitState",											d: "Wait for piston state change",	p: ["Change to:enum[any,false,true]"],															i: true,	l: true,						dd: "Wait for {0} state"],
		[ n: "waitTime",											d: "Wait for time",			p: ["Time:enum[midnight,sunrise,noon,sunset]","?Offset [minutes]:number[-1440..1440]","Days of week:enums[Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday]"],							i: true,	l: true,						dd: "Wait for next {0} (offset {1} min), on {2}"],
		[ n: "setHueVariable",		r: ["setHue"],					d: "Set hue (variable)",						p: ["Hue:variable"], dd: "Set hue to {0}°"],
		[ n: "fadeLevelHW",			r: ["setLevel"], 				d: "Fade to level (hardware)",		p: ["Target level:level","Duration (ms):number[1..60000]"],																							dd: "Fade to {0}% in {1}ms",				],
		[ n: "fadeLevel",			r: ["setLevel"], 				d: "Fade to level",					p: ["?Start level (optional):level","Target level:level","Duration (seconds):number[1..600]"],															dd: "Fade level from {0}% to {1}% in {2}s",				],
		[ n: "fadeLevelVariable",	r: ["setLevel"], 				d: "Fade to level (variable)",		p: ["?Start level (optional):variable","Target level:variable","Duration (seconds):number[1..600]"],															dd: "Fade level from {0}% to {1}% in {2}s",				],
		[ n: "adjustLevel",			r: ["setLevel"], 				d: "Adjust level",					p: ["Adjustment (+/-):number[-100..100]"],																												dd: "Adjust level by {0}%",	],
		[ n: "fadeSaturation",		r: ["setSaturation"],			d: "Fade to saturation",				p: ["?Start saturation (optional):saturation","Target saturation:saturation","Duration (seconds):number[1..600]"],											dd: "Fade saturation from {0}% to {1}% in {2}s",				],
		[ n: "adjustSaturation",		r: ["setSaturation"],		d: "Adjust saturation",				p: ["Adjustment (+/-):number[-100..100]"],																												dd: "Adjust saturation by {0}%",	],
		[ n: "fadeHue",				r: ["setHue"], 					d: "Fade to hue",						p: ["?Start hue (optional):hue","Target hue:hue","Duration (seconds):number[1..600]"],																dd: "Fade hue from {0}° to {1}° in {2}s",				],
		[ n: "adjustHue",			r: ["setHue"], 					d: "Adjust hue",						p: ["Adjustment (+/-):number[-360..360]"],																												dd: "Adjust hue by {0}°",	],
		[ n: "flash",				r: ["on", "off"], 				d: "Flash",							p: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					dd: "Flash {0}ms/{1}ms for {2} time(s)",		],
		[ n: "setVariable",		d: "Set variable", 					p: ["Variable:var"],																				varEntry: 0, 						l: true,																	aggregated: true,	],
		[ n: "saveAttribute",	d: "Save attribute to variable", 		p: ["Attribute:attribute","Aggregation:aggregation","?Convert to data t:dataType","Save to variable:string"],					varEntry: 3,		dd: "Save attribute '{0}' to variable |[{3}]|'",			aggregated: true,	],
		[ n: "saveState",		d: "Save state to variable",			p: ["Attributes:attributes","Aggregation:aggregation","?Convert to data t:dataType","Save to state variable:string"],			stateVarEntry: 3,	dd: "Save state of attributes {0} to variable |[{3}]|'",	aggregated: true,	],
		[ n: "saveStateLocally",	d: "Capture state to local store",	p: ["Attributes:attributes","?Only if state is empty:bool"],																															dd: "Capture state of attributes {0} to local store",		],
		[ n: "saveStateGlobally",d: "Capture state to global store",	p: ["Attributes:attributes","?Only if state is empty:bool"],																															dd: "Capture state of attributes {0} to global store",	],
		[ n: "loadAttribute",	d: "Load attribute from variable",	p: ["Attribute:attribute","Load from variable:variable","Allow translations:bool","Negate translation:bool"],											dd: "Load attribute '{0}' from variable |[{1}]|",	],
		[ n: "loadState",		d: "Load state from variable",		p: ["Attributes:attributes","Load from state variable:stateVariable","Allow translations:bool","Negate translation:bool"],								dd: "Load state of attributes {0} from variable |[{1}]|"				],
		[ n: "loadStateLocally",	d: "Restore state from local store",	p: ["Attributes:attributes","?Empty the state:bool"],																															dd: "Restore state of attributes {0} from local store",			],
		[ n: "loadStateGlobally",d: "Restore state from global store",	p: ["Attributes:attributes","?Empty the state:bool"],																															dd: "Restore state of attributes {0} from global store",			],
		[ n: "setLocationMode",	d: "Set location mode",				p: [[n:"Mode",t:"mode"]],																														l: true,	dd: "Set location mode to '{0}'",		aggregated: true,	],
		[ n: "setAlarmSystemStatus",d: "Set Smart Home Monitor status",	p: ["Status:alarmSystemStatus"],																										l: true,	dd: "Set SHM alarm to '{0}'",			aggregated: true,	],
		[ n: "sendNotification",	d: "Send notification",				p: ["Message:text"],																													l: true,	dd: "Send notification '{0}' in notifications page",			aggregated: true,	],
		[ n: "sendPushNotification",d: "Send Push notification",			p: ["Message:text","Show in notifications page:bool"],																							l: true,	dd: "Send Push notification '{0}'",		aggregated: true,	],
		[ n: "sendSMSNotification",d: "Send SMS notification",			p: ["Message:text","Phone number:phone","Show in notifications page:bool"],																		l: true, dd: "Send SMS notification '{0}' to {1}",aggregated: true,	],
		[ n: "queueAskAlexaMessage",d: "Queue AskAlexa message",			p: ["Message:text", "?Unit:text", "?Application:text"],																		l: true, dd: "Queue AskAlexa message '{0}' in unit {1}",aggregated: true,	],
		[ n: "deleteAskAlexaMessages",d: "Delete AskAlexa messages",			p: ["Unit:text", "?Application:text"],																	l: true, dd: "Delete AskAlexa messages in unit {1}",aggregated: true,	],
		[ n: "executeRoutine",	d: "Execute routine",					p: ["Routine:routine"],																		l: true, 										dd: "Execute routine '{0}'",				aggregated: true,	],
		[ n: "cancelPendingTasks",d: "Cancel pending tasks",			p: ["Scope:enum[Local,Global]"],																														dd: "Cancel all pending {0} tasks",		],
		[ n: "followUp",				d: "Follow up with piston",			p: ["Delay:number[1..1440]","Unit:enum[seconds,minutes,hours]","Piston:piston","?Save state into variable:string"],	i: true,	varEntry: 3,	l: true,	dd: "Follow up with piston '{2}' after {0} {1}",	aggregated: true],
		[ n: "executePiston",		d: "Execute piston",					p: ["Piston:piston","?Save state into variable:string"],																varEntry: 1,	l: true,	dd: "Execute piston '{0}'",	aggregated: true],
		[ n: "pausePiston",			d: "Pause piston",					p: ["Piston:piston"],																l: true,	dd: "Pause piston '{0}'",	aggregated: true],
		[ n: "resumePiston",			d: "Resume piston",					p: ["Piston:piston"],																l: true,	dd: "Resume piston '{0}'",	aggregated: true],
		[ n: "httpRequest",			d: "Make a web request", p: ["URL:string","Method:enum[GET,POST,PUT,DELETE,HEAD]","Content t:enum[JSON,FORM]","?Variables to send:variables","Import response data into variables:bool","?Variable import name prefix (optional):string"], l: true, dd: "Make a {1} web request to {0}", aggregated: true],
		[ n: "wolRequest",			d: "Wake a LAN device", p: ["MAC address:string","?Secure code:string"], l: true, dd: "Wake LAN device at address {0} with secure code {1}", aggregated: true],
*/		
	]
    + (location.contactBookEnabled ? [
		sendNotificationToContacts : [n: "Send notification to contacts", p: ["Message:text","Contacts:contacts","Save notification:bool"], l: true, dd: "Send notification '{0}' to {1}", aggregated: true],
	] : [:])
	+ (getIftttKey() ? [
		iftttMaker : [n: "Send IFTTT Maker event", p: ["Event:text", "?Value1:string", "?Value2:string", "?Value3:string"], l: true, dd: "Send IFTTT Maker event '{0}' with parameters '{1}', '{2}', and '{3}'", aggregated: true],
	] : [:])
	+ (getLifxToken() ? [
		lifxScene: [n: "Activate LIFX scene", p: ["Scene:lifxScenes"], l: true, dd: "Activate LIFX Scene '{0}'", aggregated: true],
	] : [:])
}




private static Map comparisons() {
	return [
    	conditions: [
        	changed							: [ d: "changed",																	g:"bmns",						t: 1,	],
        	did_not_change					: [ d: "did not change",															g:"bmns",						t: 1,	],
    		is 								: [ d: "is",								dd: "are",								g:"bmns",	p: 1						],
    		is_not	 						: [ d: "is not",							dd: "are not",							g:"bmns",	p: 1						],
    		is_any_of 						: [ d: "is any of",							dd: "are any of",						g:"ns",		p: 1,	m: true,			],
    		is_not_any_of 					: [ d: "is not any of",						dd: "are not any of",					g:"ns",		p: 1,	m: true,			],
    		is_equal_to						: [ d: "is equal to",						dd: "are equal to",						g:"bn",		p: 1						],
    		is_different_than				: [ d: "is different than",					dd: "are different than",				g:"bn",		p: 1						],
    		is_less_than					: [ d: "is less than",						dd: "are less than",					g:"n",		p: 1						],
    		is_less_to_or_equal_than		: [ d: "is less to or equal than",			dd: "are less to or equal than",		g:"n",		p: 1						],
    		is_greater_than					: [ d: "is greater than",					dd: "are greater than",					g:"n",		p: 1						],
    		is_greater_to_or_equal_than		: [ d: "is greater to or equal than",		dd: "are greater to or equal than",		g:"n",		p: 1						],
    		is_inside_range					: [ d: "is inside range",					dd: "are inside range",					g:"n",		p: 2						],
    		is_outside_range				: [ d: "is outside range",					dd: "are outside range",				g:"n",		p: 2						],
			is_even							: [ d: "is even",							dd: "are even",							g:"n",									],
			is_odd							: [ d: "is odd",							dd: "are odd",							g:"n",									],
    		was 							: [ d: "was",								dd: "were",								g:"bns",	p: 1,				t: 2,	],
    		was_not 						: [ d: "was not",							dd: "were not",							g:"bns",	p: 1,				t: 2,	],
    		was_any_of 						: [ d: "was any of",						dd: "were any of",						g:"bns",	p: 1,	m: true,	t: 2,	],
    		was_not_any_of 					: [ d: "was not any of",					dd: "were not any of",					g:"bns",	p: 1,	m: true,	t: 2,	],
			was_equal_to 					: [ d: "was equal to",						dd: "were equal to",					g:"n",		p: 1,				t: 2,	],
			was_different_than 				: [ d: "was different than",				dd: "were different than",				g:"n",		p: 1,				t: 2,	],
			was_less_than 					: [ d: "was less than",						dd: "were less than",					g:"n",		p: 1,				t: 2,	],
			was_less_than_or_equal_to 		: [ d: "was less than or equal to",			dd: "were less than or equal to",		g:"n",		p: 1,				t: 2,	],
			was_greater_than 				: [ d: "was greater than",					dd: "were greater than",				g:"n",		p: 1,				t: 2,	],
			was_greater_than_or_equal_to 	: [ d: "was greater than or equal to",		dd: "were greater than or equal to",	g:"n",		p: 1,				t: 2,	],
			was_inside_range 				: [ d: "was inside range",					dd: "were inside range",				g:"n",		p: 2,				t: 2,	],
			was_outside_range 				: [ d: "was outside range",					dd: "were outside range",				g:"n",		p: 2,				t: 2,	],
    		was_even						: [ d: "was even",							dd: "were even",						g:"n",							t: 2,	],
    		was_odd							: [ d: "was odd",							dd: "were odd",							g:"n",							t: 2,	],
    	],
        triggers: [
    		changes 						: [ d: "changes",							dd: "change",							g:"bmns",								],
    		changes_to 						: [ d: "changes to",						dd: "change to",						g:"bns",	p: 1,						],
    		changes_away_from 				: [ d: "changes away from",					dd: "change away from",					g:"bns",	p: 1,						],
    		changes_to_any_of 				: [ d: "changes to any of",					dd: "change to any of",					g:"ns",		p: 1,	m: true,			],
    		changes_away_from_any_of 		: [ d: "changes away from any of",			dd: "change away from any of",			g:"ns",		p: 1,	m: true,			],
            drops							: [ d: "drops",								dd: "drop",								g:"n",									],
            does_not_drop					: [ d: "does not drop",						dd: "do not drop",						g:"n",									],
            drops_below						: [ d: "drops below",						dd: "drop below",						g:"n",		p: 1,						],
            drops_to_or_below				: [ d: "drops to or below",					dd: "drop to or below",					g:"n",		p: 1,						],
            remains_below					: [ d: "remains below",						dd: "remains below",					g:"n",		p: 1,						],
            remains_below_or_equal_to		: [ d: "remains below or equal to",			dd: "remains below or equal to",		g:"n",		p: 1,						],
            rises							: [ d: "rises",								dd: "rise",								g:"n",									],
            does_not_rise					: [ d: "does not rise",						dd: "do not rise",						g:"n",									],
            rises_above						: [ d: "rises above",						dd: "rise above",						g:"n",		p: 1,						],
            rises_to_or_above				: [ d: "rises to or above",					dd: "rise to or above",					g:"n",		p: 1,						],
            remains_above					: [ d: "remains above",						dd: "remains above",					g:"n",		p: 1,						],
            remains_above_or_equal_to		: [ d: "remains above or equal to",			dd: "remains above or equal to",		g:"n",		p: 1,						],
            enters_range					: [ d: "enters range",						dd: "enter range",						g:"n",		p: 2,						],
            remains_outside_of_range		: [ d: "remains outside of range",			dd: "remain outside of range",			g:"n",		p: 2,						],
            exits_range						: [ d: "exits range",						dd: "exit range",						g:"n",		p: 2,						],
            remains_inside_of_range			: [ d: "remains inside of range",			dd: "remain inside of range",			g:"n",		p: 2,						],
			becomes_even					: [ d: "becomes even",						dd: "become even",						g:"n",									],
			remains_even					: [ d: "remains even",						dd: "remain even",						g:"n",									],
			becomes_odd						: [ d: "becomes odd",						dd: "become odd",						g:"n",									],
			remains_odd						: [ d: "remains odd",						dd: "remain odd",						g:"n",									],
    		stays_unchanged					: [ d: "stays unchanged",					dd: "stay unchanged",					g:"bsn",						t: 2,	],
    		stays	 						: [ d: "stays",								dd: "stay",								g:"bns",	p: 1,				t: 2,	],
    		stays_away_from					: [ d: "stays away from",					dd: "stay away from",					g:"bns",	p: 1,				t: 2,	],
    		stays_any_of					: [ d: "stays any of",						dd: "stay any of",						g:"ns",		p: 1,	m: true,	t: 2,	],
    		stays_away_from_any_of			: [ d: "stays away from any of",			dd: "stay away from any of",			g:"bns",	p: 1,	m: true,	t: 2,	],
			stays_equal_to 					: [ d: "stays equal to",					dd: "stay equal to",					g:"n",		p: 1,				t: 2,	],
			stays_different_than			: [ d: "stays different than",				dd: "stay different than",				g:"n",		p: 1,				t: 2,	],
			stays_less_than 				: [ d: "stays less than",					dd: "stay less than",					g:"n",		p: 1,				t: 2,	],
			stays_less_than_or_equal_to 	: [ d: "stays less than or equal to",		dd: "stay less than or equal to",		g:"n",		p: 1,				t: 2,	],
			stays_greater_than 				: [ d: "stays greater than",				dd: "stay greater than",				g:"n",		p: 1,				t: 2,	],
			stays_greater_than_or_equal_to 	: [ d: "stays greater than or equal to",	dd: "stay greater than or equal to",	g:"n",		p: 1,				t: 2,	],
			stays_inside_range 				: [ d: "stays inside range",				dd: "stay inside range",				g:"n",		p: 2,				t: 2,	],
			stays_outside_range 			: [ d: "stays outside range",				dd: "stay outside range",				g:"n",		p: 2,				t: 2,	],
			stays_even						: [ d: "stays even",						dd: "stay even",						g:"n",							t: 2,	],
			stays_odd						: [ d: "stays odd",							dd: "stay odd",							g:"n",							t: 2,	],
        ]
	]
}

private static Map functions() {
	return [
      	avg				: [ t: "decimal",					],
      	variance		: [ t: "decimal",					],
      	stdev			: [ t: "decimal",					],
      	round			: [ t: "decimal",					],
      	ceil			: [ t: "decimal",					],
      	ceiling			: [ t: "decimal",					],
      	floor			: [ t: "decimal",					],
      	min				: [ t: "decimal",					],
      	max				: [ t: "decimal",					],
      	sum				: [ t: "decimal",					],
      	count			: [ t: "integer",					],
      	left			: [ t: "string",					],
      	right			: [ t: "string",					],
      	mid				: [ t: "string",					],
      	substring		: [ t: "string",					],
      	sprintf			: [ t: "string",					],
      	format			: [ t: "string",					],
      	string			: [ t: "string",					],
      	replace			: [ t: "string",					],
      	concat			: [ t: "string",					],
      	text			: [ t: "string",					],
      	lower			: [ t: "string",					],
      	upper			: [ t: "string",					],
      	title			: [ t: "string",					],
        int				: [ t: "integer",					],
        integer			: [ t: "integer",					],
        float			: [ t: "decimal",					],
        decimal			: [ t: "decimal",					],
        number			: [ t: "decimal",					],
        bool			: [ t: "boolean",					],
        boolean			: [ t: "boolean",					],
        power			: [ t: "decimal",					],
        sqr				: [ t: "decimal",					],
        sqrt			: [ t: "decimal",					],
        dewpoint		: [ t: "decimal",	d: "dewPoint",	],
        fahrenheit		: [ t: "decimal",					],
        celsius			: [ t: "decimal",					],
        dateAdd			: [ t: "time",		d: "dateAdd",	],
	]
}

private static List threeAxisOrientations() {
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
		if (mode) result.push([id: mode.id, n: mode.name])
	}
	return result
}
private getAlarmSystemStatusOptions() {
	return [[id: "off", n:"Disarmed"], [id: "stay", n: "Armed/Stay"], [id: "away", n: "Armed/Away"]]
}


//debug
