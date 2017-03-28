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
public static String version() { return "v0.0.05c.20170328" }
/*
 *	03/28/2016 >>> v0.0.05c.20170328 - ALPHA - Minor fixes regarding location subscriptions
 *	03/28/2016 >>> v0.0.05b.20170328 - ALPHA - Minor fixes for setting location mode
 *	03/27/2016 >>> v0.0.05a.20170327 - ALPHA - Minor fixes - location events do not have a device by default, overriding with location
 *	03/27/2016 >>> v0.0.059.20170327 - ALPHA - Completed SHM status and location mode. Can get/set, can subscribe to changes, any existing condition in pistons needs to be revisited and fixed
 *	03/25/2016 >>> v0.0.058.20170325 - ALPHA - Fixes for major issues introduced due to the new comparison editor (you need to re-edit all comparisons to fix them), added log multiline support, use \r or \n or \r\n in a string
 *	03/24/2016 >>> v0.0.057.20170324 - ALPHA - Improved installation experience, preventing direct installation of child app, location mode and shm status finally working
 *	03/23/2016 >>> v0.0.056.20170323 - ALPHA - Various fixes for restrictions
 *	03/22/2016 >>> v0.0.055.20170322 - ALPHA - Various improvements, including a revamp of the comparison dialog, also moved the dashboard website to https://dashboard.webcore.co
 *	03/21/2016 >>> v0.0.054.20170321 - ALPHA - Moved the dashboard website to https://webcore.homecloudhub.com/dashboard/
 *	03/21/2016 >>> v0.0.053.20170321 - ALPHA - Fixed a bug where variables containing expressions would be cast to the variable type outside of evaluateExpression (the right way)
 *	03/20/2016 >>> v0.0.052.20170320 - ALPHA - Fixed $shmStatus
 *	03/20/2016 >>> v0.0.051.20170320 - ALPHA - Fixed a problem where start values for variables would not be correctly picked up from atomicState (used state by mistake)
 *	03/20/2016 >>> v0.0.050.20170320 - ALPHA - Introducing parallelism, a semaphore mechanism to allow synchronization of multiple simultaneous executions, disabled by default (pistons wait at a semaphore)
 *	03/20/2016 >>> v0.0.04f.20170320 - ALPHA - Minor fixes for device typed variables (lost attribute) and counter variable in for each
 *	03/20/2016 >>> v0.0.04e.20170320 - ALPHA - Major operand/expression/cast refactoring to allow for arrays of devices - may break things. Also introduced for each loops and actions on device typed variables
 *	03/19/2016 >>> v0.0.04d.20170319 - ALPHA - Fixes for functions and device typed variables
 *	03/19/2016 >>> v0.0.04c.20170319 - ALPHA - Device typed variables now enabled - not yet possible to use them in conditions or in actions, but getting there
 *	03/18/2016 >>> v0.0.04b.20170318 - ALPHA - Various fixes
 *	03/18/2016 >>> v0.0.04a.20170318 - ALPHA - Enabled manual piston status and added the set piston status task as well as the exit statement
 *	03/18/2016 >>> v0.0.049.20170318 - ALPHA - Third attempt to fix switch
 *	03/18/2016 >>> v0.0.048.20170318 - ALPHA - Second attempt to fix switch fallbacks with wait breaks, wait in secondary cases were not working
 *	03/18/2016 >>> v0.0.047.20170318 - ALPHA - Attempt to fix switch fallbacks with wait breaks
 *	03/18/2016 >>> v0.0.046.20170318 - ALPHA - Various critical fixes - including issues with setLevel without a required state
 *	03/18/2016 >>> v0.0.045.20170318 - ALPHA - Fixed a newly introduced bug for Toggle (missing parameters)
 *	03/17/2016 >>> v0.0.044.20170317 - ALPHA - Cleanup ghost else-ifs on piston save
 *	03/17/2016 >>> v0.0.043.20170317 - ALPHA - Added "View piston in dashboard" to child app UI
 *	03/17/2016 >>> v0.0.042.20170317 - ALPHA - Various fixes and enabled restrictions - UI for conditions and restrictions needs refactoring to use the new operand editor
 *	03/16/2016 >>> v0.0.041.20170316 - ALPHA - Various fixes
 *	03/16/2016 >>> v0.0.040.20170316 - ALPHA - Fixed a bug where optional parameters were not correctly interpreted, leading to setLevel not working, added functions startsWith, endsWith, contains, eq, le, lt, ge, gt
 *	03/16/2016 >>> v0.0.03f.20170316 - ALPHA - Completely refactored task parameters and enabled variables. Dynamically assigned variables act as functions - it can be defined as an expression and reuse it in lieu of that expression
 *	03/15/2016 >>> v0.0.03e.20170315 - ALPHA - Various improvements
 *	03/14/2016 >>> v0.0.03d.20170314 - ALPHA - Fixed a bug with caching operands for triggers
 *	03/14/2016 >>> v0.0.03c.20170314 - ALPHA - Fixed a bug with switches
 *	03/14/2016 >>> v0.0.03b.20170314 - ALPHA - For statement finally getting some love
 *	03/14/2016 >>> v0.0.03a.20170314 - ALPHA - Added more functions (age, previousAge, newer, older, previousValue) and fixed a bug where operand caching stopped working after earlier code refactorings
 *	03/13/2016 >>> v0.0.039.20170313 - ALPHA - The Switch statement should now be functional - UI validation not fully done
 *	03/12/2016 >>> v0.0.038.20170312 - ALPHA - Traversing else ifs and else statements in search for devices to subscribe to
 *	03/12/2016 >>> v0.0.037.20170312 - ALPHA - Added support for break and exit (partial, piston state is not set on exit) - fixed some comparison data type incompatibilities
 *	03/12/2016 >>> v0.0.036.20170312 - ALPHA - Added TCP = cancel on condition change and TOS = Action - no other values implemented yet, also, WHILE loops are now working, please remember to add a WAIT in it...
 *	03/11/2016 >>> v0.0.035.20170311 - ALPHA - A little error creeped into the conditions, fixed it
 *	03/11/2016 >>> v0.0.034.20170311 - ALPHA - Multiple device selection aggregation now working properly. COUNT(device list's contact) rises above 1 will be true when at least two doors in the list are open :D
 *	03/11/2016 >>> v0.0.033.20170311 - ALPHA - Implemented all conditions except "was..." and all triggers except "stays..."
 *	03/11/2016 >>> v0.0.032.20170311 - ALPHA - Fixed setLevel null params and added version checking
 *	03/11/2016 >>> v0.0.031.20170310 - ALPHA - Various fixes including null optional parameters, conditional groups, first attempt at piston restrictions (statement restrictions not enabled yet), fixed a problem with subscribing device bolt indicators only showing for one instance of each device/attribute pair, fixed sendPushNotification
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
/*** webCoRE DEFINITION														***/
/******************************************************************************/
private static String handle() { return "webCoRE" }
definition(
    name: "webCoRE Piston",
    namespace: "ady624",
    author: "Adrian Caramaliu",
    description: "Do not install this directly, use webCoRE instead",
    category: "Convenience",
	parent: "ady624:webCoRE",
    /* icons courtesy of @chauger - thank you */
	iconUrl: "https://cdn.rawgit.com/ady624/webCoRE/master/resources/icons/app-CoRE.png",
	iconX2Url: "https://cdn.rawgit.com/ady624/webCoRE/master/resources/icons/app-CoRE@2x.png",
	iconX3Url: "https://cdn.rawgit.com/ady624/webCoRE/master/resources/icons/app-CoRE@3x.png"
)


preferences {
	//common pages
	page(name: "pageMain")
}

/******************************************************************************/
/*** 																		***/
/*** CONFIGURATION PAGES													***/
/*** 																		***/
/******************************************************************************/
def pageMain() {
	//webCoRE Piston main page
	return dynamicPage(name: "pageMain", title: "", uninstall: false) {
    	if (!parent.isInstalled()) {        
        	section() {
				paragraph "Sorry, you cannot install a piston directly from the Marketplace, please use the webCoRE SmartApp instead."
            }
        	section("Installing webCoRE") {
            	paragraph "If you are trying to install webCoRE, please go back one step and choose webCoRE, not webCoRE Piston. You can also visit wiki.webcore.co for more information on how to install and use webCoRE"
				href "", title: "More information ${parent.getWikiUrl()}", style: "external", url: parent.getWikiUrl(), image: "https://cdn.rawgit.com/ady624/webCoRE/master/resources/icons/app-CoRE.png", required: false
            }
        } else {
            def currentState = state.currentState

            section ("General") {
                label name: "name", title: "Name", required: true, state: (name ? "complete" : null), defaultValue: parent.generatePistonName()
            }

            section("Dashboard") {        
                def dashboardUrl = parent.getDashboardUrl()
                if (dashboardUrl) {
                    dashboardUrl = "${dashboardUrl}piston/${hashId(app.id)}"
                    href "", title: "View piston in dashboard", style: "external", url: dashboardUrl, image: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/icons/dashboard.png", required: false
                } else {
                    paragraph "Sorry, your dashboard does not seem to be enabled, please go to the parent app and enable the dashboard."
                }
            }

            section(title:"Application Info") {
                paragraph version(), title: "Version"
                paragraph mem(), title: "Memory Usage"
            }
        }
	}
}

/******************************************************************************/
/*** 																		***/
/*** PUBLIC METHODS															***/
/*** 																		***/
/******************************************************************************/

def installed() {
   	state.created = now()
    state.modified = now()
    state.build = 0
    state.piston = [:]
    state.vars = state.vars?: [:]
    state.subscriptions = state.subscriptions ?: [:]
	initialize()
	return true
}

def updated() {
	unsubscribe()
	initialize()
	return true
}

def initialize() {
	if (!!state.active) {
    	resume()
    }
}

def get() {
	return [
    	meta: [
			id: hashId(app.id),
    		author: state.author,
    		name: app.label,
    		created: state.created,
	    	modified: state.modified,
	    	build: state.build,
	    	bin: state.bin,
	    	active: state.active
		],
        piston: state.piston,
        systemVars: getSystemVariables(),
        subscriptions: state.subscriptions,     
	    stats: state.stats,
        state: state.state,
        logs: state.logs,
        trace: state.trace,
        localVars: state.vars,
        memory: mem(),
        lastExecuted: state.lastExecuted,
        nextSchedule: state.nextSchedule,
    ]
	
}

def activity(lastLogTimestamp) {
	def logs = state.logs
    def llt = lastLogTimestamp && lastLogTimestamp instanceof String && lastLogTimestamp.isLong() ? lastLogTimestamp.toLong() : 0
    def index = llt ? logs.findIndexOf{ it.t == llt } : 0
    index = index > 0 ? index : 0
	return [
    	name: app.label,
        state: state.state,
    	logs: index ? logs[0..index-1] : [],		
    	trace: state.trace,
        localVars: state.vars,
        memory: mem(),
        lastExecuted: state.lastExecuted,
        nextSchedule: state.nextSchedule,
    ]    
}


def set(data) {
	if (!data) return false
	state.modified = now()
    state.build = (int)(state.build ? (int)state.build + 1 : 1)
    def piston = [
    	o: data.o ?: {},
    	r: data.r ?: [],
    	rn: !!data.rn,
		rop: data.rop ?: 'and',
		s: data.s ?: [],
        v: data.v ?: [],
        z: data.z ?: ''
    ]
    if (data.n) app.updateLabel(data.n)
    setIds(piston)
    state.piston = piston
    state.trace = [:]
    state.vars = state.vars ?: [:];
    //todo replace this
    if ((state.build == 1) || (!!state.active)) {
    	resume()
    }
    return [active: state.active, build: state.build, modified: state.modified]
}


private int setIds(node, maxId = 0, existingIds = [:], requiringIds = [], level = 0) {
    if (node?.t in ['if', 'while', 'repeat', 'for', 'each', 'switch', 'action', 'condition', 'restriction', 'group']) {
        def id = node['$']
        if (!id || existingIds[id]) {
            requiringIds.push(node)
        } else {
            maxId = maxId < id ? id : maxId
            existingIds[id] = id
        }
        if ((node.t == 'if') && (node.ei)) {
        	node.ei.removeAll{ !it.c && !it.s }
			for (elseIf in node.ei) {
		        id = elseIf['$']
                if (!id || existingIds[id]) {               
                    requiringIds.push(elseIf)
                } else {
                    maxId = (maxId < id) ? id : maxId
                    existingIds[id] = id
                }
            }
        }
        if ((node.t == 'switch') && (node.cs)) {
			for (_case in node.cs) {
		        id = _case['$']
                if (!id || existingIds[id]) {               
                    requiringIds.push(_case)
                } else {
                    maxId = (maxId < id) ? id : maxId
                    existingIds[id] = id
                }
            }
        }
        if ((node.t == 'action') && (node.k)) {
			for (task in node.k) {
		        id = task['$']
                if (!id || existingIds[id]) {               
                    requiringIds.push(task)
                } else {
                    maxId = (maxId < id) ? id : maxId
                    existingIds[id] = id
                }
            }
        }
    }
	for (list in node.findAll{ it.value instanceof List }) { 
        for (item in list.value.findAll{ it instanceof Map }) {
            maxId = setIds(item, maxId, existingIds, requiringIds, level + 1)
        }
    }
    if (level == 0) {
    	for (item in requiringIds) {
        	maxId += 1
        	item['$'] = maxId
        }
    }
    return maxId
}



def config(data) {
	if (!data) {
    	return false;
    }
	if (data.bin) {
		state.bin = data.bin;
    }
	if (data.author) {
		state.author = data.author;
    }
}

def pause() {
	state.active = false
    def rtData = getRunTimeData()
	def msg = timer "Piston successfully stopped", null, -1
    info "Stopping piston...", rtData, 0
    checkVersion(rtData)
    state.schedules = []
    rtData.stats.nextSchedule = 0
    unsubscribe()
    unschedule()
    state.trace = [:]
    state.subscriptions = [:]
    info msg, rtData
    updateLogs(rtData)
    return rtData
}

def resume() {
	state.active = true;
	def tempRtData = getTemporaryRunTimeData()
    def msg = timer "Piston successfully started", null,  -1
	info "Starting piston... (${version()})", tempRtData, 0
    def rtData = getRunTimeData(tempRtData)
    checkVersion(rtData)
    subscribeAll(rtData)    
    info msg, rtData
    updateLogs(rtData)
    rtData.result = [active: true, subscriptions: state.subscriptions]
    return rtData
}

def execute() {

}

private getTemporaryRunTimeData() {
	return [
    	temporary: true,
       	timestamp: now(),
        logs:[]
    ]
}

private getRunTimeData(rtData = null, semaphore = null) {
	def timestamp = rtData?.timestamp ?: now()
    def piston = state.piston
    def logs = rtData && rtData.temporary ? rtData.logs : null
	rtData = rtData && !rtData.temporary ? rtData : parent.getRunTimeData(semaphore && !(piston.o?.pep) ? hashId(app.id) : null)
    rtData.timestamp = timestamp
    rtData.logs = [[t: timestamp]]    
    if (logs && logs.size()) {
    	logs.removeAll{ !it.c || !rtData.logging[it.c] }
        rtData.logs = rtData.logs + logs
    }
    rtData.trace = [t: timestamp, points: [:]]
    rtData.id = hashId(app.id)
	rtData.active = state.active;
    rtData.stats = [nextScheduled: 0]
    //we're reading the cache from atomicState because we might have waited at a semaphore
    rtData.cache = atomicState.cache ?: [:]
    rtData.newCache = [:]
    rtData.schedules = []
    rtData.cancelations = [statements:[], conditions:[]]
    rtData.piston = piston
    rtData.locationId = hashId(location.id)
    //flow control
    //we're reading the old state from atomicState because we might have waited at a semaphore
    def oldState = atomicState.state ?: ''
    rtData.state = [old: oldState, new: piston.o?.mps ? oldState : 'true'];
    rtData.statementLevel = 0;
    rtData.fastForwardTo = null
    rtData.break = false
    rtData.systemVars = getSystemVariables()
    rtData.localVars = getLocalVariables(rtData, piston.v)
    return rtData
}

private checkVersion(rtData) {
	def ver = version()
    if (ver != rtData.coreVersion) {
    	//parent and child apps have different versions
        warn "WARNING: Results may be unreliable because the ${ver > rtData.coreVersion ? "child app's version ($ver) is newer than the parent app's version (${rtData.coreVersion})" : "parent app's version (${rtData.coreVersion}) is newer than the child app's version ($ver)" }. Please consider updating both apps to the same version.", rtData
    }    
}

/******************************************************************************/
/*** 																		***/
/*** EVENT HANDLING															***/
/*** 																		***/
/******************************************************************************/
def fakeHandler(event) {
	def rtData = getRunTimeData()
	warn "Received unexpected event [${event.device?:location}].${event.name} = ${event.value} (device has no active subscriptions)... ", rtData
    updateLogs(rtData)
}

def deviceHandler(event) {
	handleEvents(event)
}

def timeHandler(event) {
	handleEvents([date: new Date(event.t), device: location, name: 'time', value: event.t, schedule: event])
}


//entry point for all events
def handleEvents(event) {
	//cancel all pending jobs, we'll handle them later
	unschedule(timeHandler)
	def startTime = now()
    state.lastExecuted = startTime
    def eventDelay = startTime - event.date.getTime()
	def msg = timer "Event processed successfully", null, -1
    def tempRtData = getTemporaryRunTimeData()
    info "Received event [${event.device?:location}].${event.name} = ${event.value} with a delay of ${eventDelay}ms", tempRtData, 0
    state.temp = [:]
    //todo start execution
    def ver = version()
	def msg2 = timer "Runtime successfully initialized ($ver)"
    Map rtData = getRunTimeData(tempRtData, true)
    checkVersion(rtData)    
    if (rtData.semaphoreDelay) {
    	warn "Piston waited at a semaphore for ${rtData.semaphoreDelay}ms", rtData
    }
    trace msg2, rtData
    rtData.stats.timing = [
    	t: startTime,
    	d: eventDelay > 0 ? eventDelay : 0,
        l: now() - startTime
    ]
    startTime = now()
	msg2 = timer "Execution stage complete.", null, -1
    trace "Execution stage started", rtData, 1
    def success = true
    if (event.name != 'time') {
    	success = executeEvent(rtData, event)
    }
    //process all time schedules in order
    while (success && (20000 + rtData.timestamp - now() > 10000)) {
        //we only keep doing stuff if we haven't passed the 10s execution time mark
        def schedules = atomicState.schedules
        //anything less than 2 seconds in the future is considered due, we'll do some pause to sync with it
        //we're doing this because many times, the scheduler will run a job early, usually 0-1.5 seconds early...
        if (!schedules || !schedules.size()) break
        event = [date: event.date, device: location, name: 'time', value: now(), schedule: schedules.sort{ it.t }.find{ it.t < now() + 2000 }]        
        if (!event.schedule) break
        schedules.remove(event.schedule)
        atomicState.schedules = schedules
        def delay = event.schedule.t - now()
        success = executeEvent(rtData, event)
    }
	rtData.stats.timing.e = now() - startTime
    trace msg2, rtData
    if (!success) msg.m = "Event processing failed"
    finalizeEvent(rtData, msg, success)
}

private Boolean executeEvent(rtData, event) {
	try {
    	rtData = rtData ?: getRunTimeData()
        //event processing
		rtData.event = event
        rtData.previousEvent = state.lastEvent
        def index = 0
        if (event.jsonData) {
            def attribute = rtData.attributes[event.name]
            if (attribute && attribute.i && event.jsonData[attribute.i]) {
                index = event.jsonData[attribute.i]
            }
            if (!index) index = 1
        }
        rtData.currentEvent = [
            date: event.date.getTime(),
            delay: rtData.stats.timing.d,
            device: hashId((event.device?:location).id),
            name: event.name,
            value: event.value,
            unit: event.unit,
            physical: !!event.physical,
            index: index
        ]
        state.lastEvent = rtData.currentEvent
        //previous variables
        setSystemVariableValue(rtData, '$state', rtData.state.new)
        setSystemVariableValue(rtData, '$previousEventDate', rtData.previousEvent?.date ?: now())
        setSystemVariableValue(rtData, '$previousEventDelay', rtData.previousEvent?.delay ?: 0)
        setSystemVariableValue(rtData, '$previousEventDevice', rtData.previousEvent?.device)
        setSystemVariableValue(rtData, '$previousEventDeviceIndex', rtData.previousEvent?.index ?: 0)
        setSystemVariableValue(rtData, '$previousEventAttribute', rtData.previousEvent?.name ?: '')
        setSystemVariableValue(rtData, '$previousEventValue', rtData.previousEvent?.value ?: '')
        setSystemVariableValue(rtData, '$previousEventUnit', rtData.previousEvent?.unit ?: '')
        setSystemVariableValue(rtData, '$previousEventDevicePhysical', !!rtData.previousEvent?.physical)
        //current variables
        setSystemVariableValue(rtData, '$currentEventDate', rtData.currentEvent.date ?: now())
        setSystemVariableValue(rtData, '$currentEventDelay', rtData.currentEvent.delay ?: 0)
        setSystemVariableValue(rtData, '$currentEventDevice', rtData.currentEvent?.device)
        setSystemVariableValue(rtData, '$currentEventDeviceIndex', rtData.currentEvent.index ?: 0)
        setSystemVariableValue(rtData, '$currentEventAttribute', rtData.currentEvent.name ?: '')
        setSystemVariableValue(rtData, '$currentEventValue', rtData.currentEvent.value ?: '')
        setSystemVariableValue(rtData, '$currentEventUnit', rtData.currentEvent.unit ?: '')
        setSystemVariableValue(rtData, '$currentEventDevicePhysical', !!rtData.currentEvent.physical)
        rtData.fastForwardTo = null
        if (event.name == 'time') {
        	rtData.fastForwardTo = event.schedule.i
        }
		//todo - check restrictions
        rtData.stack = [c: 0, s: 0, cs:[], ss:[]]
        def ended = false
        try {
		    def allowed = !rtData.piston.r || !(rtData.piston.r.length) || evaluateConditions(rtData, rtData.piston, 'r', true)
    		if (allowed || !!rtData.fastForwardTo) {        
				if (executeStatements(rtData, rtData.piston.s)) {
                	ended = true
		        	tracePoint(rtData, 'end', 0, 0)
		        }
            } else {
            	warn "Piston execution aborted due to restrictions in effect", rtData
            }
            if (!ended) tracePoint(rtData, 'break', 0, 0)
        } catch (all) {
        	error "An error occurred while executing the event: ", rtData, null, all
        }
        
		return true
    } catch(all) {
    	error "An error occurred within executeEvent: ", rtData, null, all
    }
    return false
}

private finalizeEvent(rtData, initialMsg, success = true) {
	def startTime = now()
    //reschedule stuff
    //todo, override tasks, if any
    def schedules = (atomicState.schedules ?: [])
    //cancel statements
    schedules.removeAll{ it.s in rtData.cancelations.statements }
    //cancel on conditions
	for(cid in rtData.cancelations.conditions) {
    	schedules.removeAll{ cid in it.cs }
    }
    rtData.cancelations = []
    schedules = (schedules + (rtData.schedules ?: [])).sort{ it.t }
    //add traces for all remaining schedules
    for (schedule in schedules) {
    	def t = now() - schedule.t
        if ((t < 0) && !rtData.trace.points["t:${schedule.i}"]) {
            //we enter a fake trace point to show it on the trace viewfstate
    		tracePoint(rtData, "t:${schedule.i}", 0, t)
        }
    }
    if (schedules.size()) {
    	def next = schedules.sort{ it.t }[0]
        def t = (next.t - now()) / 1000
        t = (t < 1 ? 1 : t)
        rtData.stats.nextSchedule = next.t
        trace "Setting up scheduled job in ${t}s", rtData
        runIn(t, timeHandler, [data: next])
    } else {
    	rtData.stats.nextSchedule = 0
    }
    atomicState.schedules = schedules
    state.nextSchedule = rtData.stats.nextSchedule

    if (initialMsg) {
    	if (success) {
        	info initialMsg, rtData
        } else {
        	error initialMsg
        }
    }
    
	updateLogs(rtData)
	//update graph data
    rtData.stats.timing.u = now() - startTime
    def stats = atomicState.stats ?: [:]
    stats.timing = stats.timing ?: []
    stats.timing.push(rtData.stats.timing)
    if (stats.timing.size() > 500) stats.timing = stats.timing[stats.timing.size() - 500..stats.timing.size() - 1]
    rtData.trace.d = now() - rtData.trace.t


	state.state = rtData.state.new
    state.stats = stats
    state.trace = rtData.trace
    //flush the new cache value
    for(item in rtData.newCache) rtData.cache[item.key] = item.value
    //beat race conditions
    state.cache = rtData.cache    
	parent.updateRunTimeData(rtData)
    //overwrite state, might have changed meanwhile
    state.schedules = atomicState.schedules
}

private updateLogs(rtData) {
	//we only save the logs if we got some
	if (!rtData || !rtData.logs || (rtData.logs.size() < 2)) return
    def logs = (rtData.logs?:[]) + (atomicState.logs?:[])
    def maxLogSize = 500
    //we attempt to store 500 logs, but if that's too much, we go down in 50 increments
    while (maxLogSize >= 0) {
	    if (logs.size() > maxLogSize) {
        	def maxSz = maxLogSize < logs.size() ? maxLogSize : logs.size()
            if (maxSz) {
    			logs = logs[0..maxSz]
            } else {
            	logs = []
            }
    	}
        if ("$state".size() > 75000) {
        	maxLogSize -= 50
        } else {
        	break
        }
    }
    atomicState.logs = logs
    state.logs = logs
}



private Boolean executeStatements(rtData, statements, async = false) {
	rtData.statementLevel = rtData.statementLevel + 1
	for(statement in statements) {
    	if (!executeStatement(rtData, statement, !!async)) {
        	//stop processing
			rtData.statementLevel = rtData.statementLevel - 1
        	return false
        }
    }
    //continue processing
	rtData.statementLevel = rtData.statementLevel - 1
    return true
}

private Boolean executeStatement(rtData, statement, async = false) {
	//if rtData.fastForwardTo is a positive, non-zero number, we need to fast forward through all
    //branches until we find the task with an id equal to that number, then we play nicely after that
	if (!statement) return false
    rtData.stack.ss.push(rtData.stack.s)
    rtData.stack.s = statement.$
    def t = now()
    def value = true
    def c = rtData.stack.c
    def stacked = (true /* cancelable on condition change */)
    if (stacked) {
    	rtData.stack.cs.push(c)
    }
    def parentAsync = async
    def parentIndex = getVariable(rtData, '$index').v
    def parentDevice = getVariable(rtData, '$device').v
    async = !!async || (statement.a == "1")
    def perform = false
    def repeat = true
    def index = null
    def allowed = !statement.r || !(statement.r.length) || evaluateConditions(rtData, statement, 'r', async)
    if (allowed || !!rtData.fastForwardTo) {
    	while (repeat) {
            switch (statement.t) {
                case 'repeat':
                	//we override current condition so that child statements can cancel on it
	                rtData.stack.c = statement.$
                    if (!executeStatements(rtData, statement.s, async)) {
                        //stop processing
                        value = false
                        if (!rtData.fastForwardTo) break
                    }
                    value = true
                    if (!rtData.fastForwardTo) break
                    //repeat falls back on if and while
                case 'if':
                case 'while':
                    //check conditions for if and while
                    perform = evaluateConditions(rtData, statement, 'c', async)
                	//we override current condition so that child statements can cancel on it
	                rtData.stack.c = statement.$
                    if (!rtData.fastForwardTo && (!rtData.piston.o?.mps) && (statement.t == 'if') && (rtData.statementLevel == 1)) {
                        //automatic piston state
                        rtData.state.new = 'true';
                    }
                    
                    if (perform || !!rtData.fastForwardTo) {
                        if (statement.t in ['if', 'while']) {
                            if (!executeStatements(rtData, statement.s, async)) {
                                //stop processing
                                value = false
                                if (!rtData.fastForwardTo) break
                            }
                            value = true
                            if (!rtData.fastForwardTo) break
                        }
                    }
                    if (!perform || !!rtData.fastForwardTo) {
                        if (statement.t == 'if') {
                            //look for else-ifs
                            for (elseIf in statement.ei) {
                                perform = evaluateConditions(rtData, elseIf, 'c', async)
                                if (perform || !!rtData.fastForwardTo) {
                                    if (!executeStatements(rtData, elseIf.s, async)) {
                                        //stop processing
                                        value = false
                                        if (!rtData.fastForwardTo) break
                                    }
                                    value = true
                                    if (!rtData.fastForwardTo) break
                                }                	
                            }
                            if (!rtData.fastForwardTo && (!rtData.piston.o?.mps) && (rtData.statementLevel == 1)) {
                            	//automatic piston state
                                rtData.state.new = 'false';
                            }
                            if ((!perform || !!rtData.fastForwardTo) && !executeStatements(rtData, statement.e, async)) {
                                //stop processing
                                value = false
                                if (!rtData.fastForwardTo) break
                            }
                        }
                    }
                    break
                case 'for':
                case 'each':
                    def devices = []
                    float startValue = 0
                    float endValue = 0
                    float stepValue = 1
                    if (statement.t == 'each') {
                    	devices = evaluateOperand(rtData, null, statement.lo).v ?: []
                        endValue = devices.size() - 1
                    } else {
                    	startValue = evaluateScalarOperand(rtData, statement, statement.lo, null, 'decimal').v
                    	endValue = evaluateScalarOperand(rtData, statement, statement.lo2, null, 'decimal').v
                    	stepValue = evaluateScalarOperand(rtData, statement, statement.lo3, null, 'decimal').v ?: 1.0
                    }
                    String counterVariable = getVariable(rtData, statement.x).t != 'error' ? statement.x : null
                    if (((startValue <= endValue) && (stepValue > 0)) || ((startValue >= endValue) && (stepValue < 0)) || !!rtData.fastForwardTo) {
                    	//initialize the for loop
                        if (rtData.fastForwardTo) index = cast(rtData, rtData.cache["f:${statement.$}"], 'decimal')
                    	if (index == null) {
                        	index = cast(rtData, startValue, 'decimal')
	                        rtData.cache["f:${statement.$}"] = index                            
                        }
                        setSystemVariableValue(rtData, '$index', index)
						if ((statement.t == 'each') && !rtData.fastForward) setSystemVariableValue(rtData, '$device', (index < devices.size() ? [devices[(int) index]] : []))
                        if (counterVariable && !rtData.fastForward) setVariable(rtData, counterVariable, (statement.t == 'each') ? (index < devices.size() ? [devices[(int) index]] : []) : index)
                        //do the loop
                        perform = executeStatements(rtData, statement.s, async)                        
                        if (!perform) {
                            //stop processing
                            value = false
                            if (!!rtData.break) {
                                //we reached a break, so we really want to continue execution outside of the switch
                                value = true
                                rtData.break = null
                                perform = false
                            }
                            break
                        }
                        //don't do the rest if we're fast forwarding
                        if (!!rtData.fastForwardTo) break
                        index = index + stepValue
                        setSystemVariableValue(rtData, '$index', index)
						if ((statement.t == 'each') && !rtData.fastForward) setSystemVariableValue(rtData, '$device', (index < devices.size() ? [devices[(int) index]] : []))
                        if (counterVariable && !rtData.fastForward) setVariable(rtData, counterVariable, (statement.t == 'each') ? (index < devices.size() ? [devices[(int) index]] : []) : index)
                        rtData.cache["f:${statement.$}"] = index
                        if (((stepValue > 0 ) && (index > endValue)) || ((stepValue < 0 ) && (index < endValue))) {
                        	perform = false
                            break
                        }                    
                    }
                	break
				case 'switch':
                    def values = evaluateOperand(rtData, statement, statement.lo)
                	def lo = [operand: statement.lo, values: evaluateOperand(rtData, statement, statement.lo)]
                    //go through all cases
                    def found = false
                    def implicitBreaks = (statement.ctp == 'i')
                    def fallThrough = !implicitBreaks
                    perform = false
                    for (_case in statement.cs) {
                    	def ro = [operand: _case.ro, values: evaluateOperand(rtData, _case, _case.ro)]
                        def ro2 = (_case.t == 'r') ? [operand: _case.ro2, values: evaluateOperand(rtData, _case, _case.ro2)] : null
                        perform = perform || evaluateComparison(rtData, (_case.t == 'r' ? 'is_inside_of_range' : 'is'), lo, ro, ro2)
                        found = found || perform
                        if (perform || (found && fallThrough) || !!rtData.fastForwardTo) {
	                        def fastForwardTo = rtData.fastForwardTo
                        	if (!executeStatements(rtData, _case.s, async)) {
 								//stop processing
                                value = false
                                if (!!rtData.break) {
                                	//we reached a break, so we really want to continue execution outside of the switch
                                	value = true
                                    found = true
                                    fallThrough = false
                                    rtData.break = null
                                }
                                if (!rtData.fastForwardTo) {
                                	break
                                }
							}
                            //if we determine that the fast forwarding ended during this execution, we assume found is true
                            found = found || (fastForwardTo != rtData.fastForwardTo)
                            value = true                            
                            //if implicit breaks
                            if (implicitBreaks && !rtData.fastForwardTo) {
                                fallThrough = false
                            	break
                            }
                        }
                    }
                    if (statement.e && statement.e.length && (value || !!rtData.fastForwardTo) && (!found || fallThrough || !!rtData.fastForwardTo)) {
                    	//no case found, let's do the default
						if (!executeStatements(rtData, statement.e, async)) {
                            //stop processing
                            value = false
                            if (!!rtData.break) {
                                //we reached a break, so we really want to continue execution outside of the switch
                                value = true
                                rtData.break = null
                            }
                            if (!rtData.fastForwardTo) break
						}
                    }
                	break
                case 'action':
                    value = executeAction(rtData, statement, async)
                    break
                case 'break':
                	rtData.break = true
                    value = false
                    break
                case 'exit':
                	vcmd_setState(rtData, null, [cast(rtData, evaluateOperand(rtData, null, statement.lo).v, 'string')])
                    value = false
                    break
            }
            //break the loop
            if (rtData.fastForwardTo || (statement.t == 'if')) perform = false
            
            //is this statement a loop
            def loop = (statement.t in ['while', 'repeat', 'for', 'each'])
            if (loop && !value && !!rtData.break) {
            	//someone requested a break from the loop, we're doing it
            	rtData.break = false
                //but we're allowing the rest to continue
                value = true
                perform = false
            }
            //do we repeat the loop?
            repeat = perform && value && loop && !rtData.fastForwardTo
        }
    }
	if (!rtData.fastForwardTo) tracePoint(rtData, "s:${statement.$}", now() - t, value)
	if (statement.a == '1') {
		//when an async action requests the thread termination, we continue to execute the parent
        //when an async action terminates as a result of a time event, we exit completely
		value = (rtData.event.name != 'time')
	}
    //restore current condition
    rtData.stack.c = c
    if (stacked) {
        rtData.stack.cs.pop()        
    }    
    rtData.stack.s = rtData.stack.ss.pop()
    setSystemVariableValue(rtData, '$index', parentIndex)
    setSystemVariableValue(rtData, '$device', parentDevice)
	return value || !!rtData.fastForwardTo
}


private Boolean executeAction(rtData, statement, async) {
	def parentDevicesVar = rtData.systemVars['$devices'].v    
	def devices = []
    def deviceIds = []
    //if override
    cancelStatementSchedules(rtData, statement.$)
    def result = true
    for (d in statement.d) {
    	if (d.startsWith(':')) {
    		def device = getDevice(rtData, d)
        	if (device) {
	        	devices.push(device)
                deviceIds.push(d)
	        }
        } else {
        	//we're dealing with a variable, let's get the list of devices from it
        	def var = getVariable(rtData, d)
            if (var.t == 'device') {
            	for (vd in var.v) {
                    def device = getDevice(rtData, vd)
                    if (device) {
                        devices.push(device)
                		deviceIds.push(vd)
                    }
                }
            }
        }
    }
	rtData.systemVars['$devices'].v = deviceIds.unique()
    for (task in statement.k) {
        result = executeTask(rtData, devices.unique(), statement, task, async)
        if (!result && !rtData.fastForwardTo) {
        	break
        }
    }
    rtData.systemVars['$devices'].v = parentDevicesVar
    return result
}

private Boolean executeTask(rtData, devices, statement, task, async) {
    //parse parameters
   	def virtualDevice = devices.size() ? null : location
    def t = now()
    if (rtData.fastForwardTo) {
	    if (task.$ == rtData.fastForwardTo) {
    		//finally found the resuming point, play nicely from hereon
            tracePoint(rtData, "t:${task.$}", now() - t, null)
    		rtData.fastForwardTo = null
        }
       	//we're not doing anything, we're fast forwarding...        
       	return true
    }
    def params = []
    for (param in task.p) {
    	def p = (param.vt == 'variable') ? param.x : cast(rtData, evaluateOperand(rtData, null, param).v, param.vt)
        //ensure value type is successfuly passed through
		params.push p
    }
 	def vcmd = rtData.commands.virtual[task.c]
    long delay = 0
    for (device in (virtualDevice ? [virtualDevice] : devices)) {
        if (!virtualDevice && device.hasCommand(task.c)) {
            def msg = timer "Executed [$device].${task.c}"
        	try {
            	delay = "cmd_${task.c}"(rtData, device, params)
            } catch(all) {
	            executePhysicalCommand(rtData, device, task.c, params)
			}
            trace msg, rtData
        } else {
            if (vcmd) {
	        	delay = executeVirtualCommand(rtData, vcmd.a ? devices : device, task, params)
                if (delay || vcmd.a) {
               		break
                }
            }
        }
    }
    //if we don't have to wait, we're home free
    if (delay) {
    	//get remaining piston time
    	def timeLeft = 20000 + rtData.timestamp - now()
    	//we're aiming at waking up with at least 10s left
    	if ((timeLeft - delay < 10000) || (delay >= 5000) || async) {
	        //schedule a wake up
	        trace "Requesting a wake up in ${delay}ms", rtData
            tracePoint(rtData, "t:${task.$}", now() - t, -delay)
            requestWakeUp(rtData, statement, task, delay)
	        return false
	    } else {
	        trace "Waiting for ${delay}ms", rtData
	        pause(delay)
	    }
	}
	tracePoint(rtData, "t:${task.$}", now() - t, delay)
    return true
}

private executePhysicalCommand(rtData, device, command, params = [], delay = null) {
	try {
    	params = (params instanceof List) ? params : (params ? [params] : [])
    	if (params.size()) {
        	if (delay) {
				device."$command"(params as Object[], [delay: delay])
            } else {
				device."$command"(params as Object[])
            }
        } else {
        	if (delay) {
				device."$command"([delay: delay])
			} else {
				device."$command"()
            }
        }
	} catch(all) {
    	error "Error while executing physical command $device.$command($params):", rtData, null, all
    }
    if (rtData.piston.o?.ced) {
        pause(rtData.piston.o.ced)
    	debug "Injected a ${rtData.piston.o.ced}ms delay after [$device].$command(${params ? "$params" : ''})", rtData
    }
}

private requestWakeUp(rtData, statement, task, timeOrDelay) {
	def time = timeOrDelay > 9999999999 ? timeOrDelay : now() + timeOrDelay
    def cs = [] + rtData.stack.cs
    cs.removeAll{ it == 0 }
    def schedule = [t: time, s: statement.$, i: task.$, cs: cs]
    rtData.schedules.push(schedule)
}


private long cmd_setLevel(rtData, device, params) {
	def level = params[0]
    def state = params.size() > 1 ? params[1] : ""
    def delay = params.size() > 2 ? params[2] : 0
    if (state && (device.currentValue('switch') != "$state")) {
        return 0
    }
    executePhysicalCommand(rtData, device, 'setLevel', level, delay)
    return 0
}

private long executeVirtualCommand(rtData, devices, task, params)
{
	def msg = timer "Executed virtual command ${devices ? (devices instanceof List ? "$devices." : "[$devices].") : ""}${task.c}"
    long delay = 0
    try {
		delay = "vcmd_${task.c}"(rtData, devices, params)
	    trace msg, rtData
    } catch(all) {
    	msg.m = "Error executing virtual command ${devices instanceof List ? "$devices" : "[$devices]"}.${task.c}:"
        msg.e = all
        error msg, rtData
    }
    return delay
}

private long vcmd_log(rtData, device, params) {
	def command = params[0]
	def message = params[1]
	log message, rtData, null, null, "${command}".toLowerCase().trim(), true
    return 0
}

private long vcmd_setState(rtData, device, params) {
	def value = params[0]
    if (rtData.piston.o?.mps) {
    	rtData.state.new = value
        setSystemVariableValue(rtData, '$state', rtData.state.new)        
    } else {
	    error "Cannot set the piston state while in automatic mode. Please edit the piston settings to disable the automatic piston state if you want to manually control the state.", rtData
    }
    return 0
}

private long vcmd_setLocationMode(rtData, device, params) {
	def modeIdOrName = params[0]
    def mode = location.getModes()?.find{ (hashId(it.id) == modeIdOrName) || (it.name == modeIdOrName)}
    if (mode) {
    	location.setMode(mode)
    } else {
	    error "Error setting location mode. Mode '$modeIdOrName' does not exist.", rtData
    }
    return 0
}

private long vcmd_setAlarmSystemStatus(rtData, device, params) {
	def statusIdOrName = params[0]
    def status = rtData.virtualDevices['alarmSystemStatus']?.o?.find{ (it.key == statusIdOrName) || (it.value == statusIdOrName)}.collect{ [id: it.key, name: it.value] }
    if (status && status.size()) {
	    sendLocationEvent(name: 'alarmSystemStatus', value: status[0].id)
    } else {
	    error "Error setting SmartThings Home Monitor status. Status '$statusIdOrName' does not exist.", rtData
    }
    return 0
}

private long vcmd_noop(rtData, device, params) {
	return 0
}

private long vcmd_wait(rtData, device, params) {
	return cast(rtData, params[0], 'long')
}

private long vcmd_waitRandom(rtData, device, params) {
	def min = params[0]
    def max = params[1]
    if (max < min) {
    	def v = max
        max = min
        min = v
    }
	return min + (int)Math.round((max - min) * Math.random())
}

private long vcmd_toggle(rtData, device, params) {
	if (device.currentValue('switch') == 'off') {
	    executePhysicalCommand(rtData, device, 'on')
    } else {
	    executePhysicalCommand(rtData, device, 'off')
    }
    return 0
}

private long vcmd_toggleLevel(rtData, device, params) {
	def level = params[0]
	if (device.currentValue('level') == level) {
	    executePhysicalCommand(rtData, device, 'setLevel', 0)
    } else {
	    executePhysicalCommand(rtData, device, 'setLevel', level)
    }
    return 0
}

private long vcmd_sendNotification(rtData, device, params) {
	def message = params[0]
    sendNotificationEvent(message)
    return 0
}

private long vcmd_sendPushNotification(rtData, device, params) {
	def message = params[0]
    def save = !!params[1]
	if (save) {
		sendPush(message)
	} else {
		sendPushMessage(message)
	}
    return 0
}

private long vcmd_sendSMSNotification(rtData, device, params) {
	def message = params[0]
	def phones = "${params[1]}".replace(" ", "").replace("-", "").replace("(", "").replace(")", "").tokenize(",;*|").unique()
	def save = !!params[2]
	for(def phone in phones) {
		if (save) {
			sendSms(phone, message)
		} else {
			sendSmsMessage(phone, message)
		}
		//we only need one notification
		save = false
	}
    return 0
}

private long vcmd_setVariable(rtData, device, params) {
	def name = params[0]
    def value = params[1]
	setVariable(rtData, name, value)
    return 0
}


private Boolean evaluateConditions(rtData, conditions, collection, async) {
	def t = now()
    def msg = timer ''
    //override condition id
    def c = rtData.stack.c    
    rtData.stack.c = conditions.$
    def not = (collection == 'c') ? !!conditions.n : !!conditions.rn
    def grouping = (collection == 'c') ? conditions.o : conditions.rop
    def value = (grouping == 'or' ? false : true)
	for(condition in conditions[collection]) {
    	def res = evaluateCondition(rtData, condition, collection, async)
        value = (grouping == 'or') ? value || res : value && res
        //conditions optimizations go here
        if (!rtData.fastForwardTo && (!rtData.piston.o?.cto) && (value == (grouping == 'or') ? true : false)) break
    }
    def result = not ? !value : !!value
    if (!rtData.fastForwardTo) tracePoint(rtData, "c:${conditions.$}", now() - t, result)
    def oldResult = !!rtData.cache["c:${conditions.$}"];
    if (oldResult != result) {  
    	//condition change
        cancelConditionSchedules(rtData, conditions.$)
    }
    rtData.cache["c:${conditions.$}"] = result
    //true/false actions
    if (collection == 'c') {
	    if ((result || rtData.fastForwardTo) && conditions.ts && conditions.ts.length) executeStatements(rtData, conditions.ts, async) 
    	if ((!result || rtData.fastForwardTo) && conditions.fs && conditions.fs.length) executeStatements(rtData, conditions.fs, async)
    }
    //restore condition id
    rtData.stack.c = c
    msg.m = "Condition group #${conditions.$} evaluated $result"
    debug msg, rtData
	return result
}

private evaluateOperand(rtData, node, operand, index = null, trigger = false) {
	def values = []
    switch (operand.t) {
        case "p": //physical device
        	def j = 0;
        	for(deviceId in operand.d) {
            	def value = [i: "${deviceId}:${operand.a}", v:getDeviceAttribute(rtData, deviceId, operand.a, operand.i, trigger)]
            	rtData.newCache[value.i] = value.v + [s: since]
	            values.push(value)
	            j++
			}
	        if ((values.size() > 1) && !(operand.g in ['any', 'all'])) {
	            //if we have multiple values and a grouping other than any or all we need to apply that function
	            try {
	                values = [[i: "${node?.$}:$index:0", v:"func_${operand.g}"(rtData, values*.v)]]
	            } catch(all) {
	                error "Error applying grouping method ${operand.g}", rtData
	            }
	        }
	        break;
		case 'd': //devices
        	def deviceIds = []
            for (d in operand.d) {
                if (d.startsWith(':')) {
                    if (getDevice(rtData, d)) deviceIds.push(d)
                } else {
                    //we're dealing with a variable, let's get the list of devices from it
                    def var = getVariable(rtData, d)
                    if (var.t == 'device') {
                        for (vd in var.v) {
							if (getDevice(rtData, vd)) deviceIds.push(vd)
                    	}
                	}
            	}
            }            
			values = [[i: "${node?.$}:d", v:[t: 'device', v: deviceIds.unique()]]]	
            break
		case 'v': //virtual devices
        	switch (operand.v) {
            	case 'mode':
            	case 'alarmSystemStatus':
                	values = [[i: "${node?.$}:v", v:getDeviceAttribute(rtData, rtData.locationId, operand.v)]];
                    break;
            }
            break
        case "x": //variable
	        values = [[i: "${node?.$}:$index:0", v:getVariable(rtData, operand.x)]]
            break
        case "c": //constant
        case "e": //expression
	        values = [[i: "${node?.$}:$index:0", v: [:] + evaluateExpression(rtData, operand.exp)]]
            break
    }
    if (!node) {
    	if (values.length) return values[0].v
        return [t: 'dynamic', v: null]
    }
    return values
}

private evaluateScalarOperand(rtData, node, operand, index = null, dataType = 'string') {
	def value = evaluateOperand(rtData, null, operand, index)
    return [t: dataType, v: cast(rtData, (value ? value.v: ''), dataType)]
}

private Boolean evaluateCondition(rtData, condition, collection, async) {
	def t = now()
    def msg = timer ''
    //override condition id
    def c = rtData.stack.c    
    rtData.stack.c = condition.$
    def not = false
    def result = false
    if (condition.t == 'group') {
    	result = evaluateConditions(rtData, condition, collection, async)
    } else {
        not = !!condition.n
        def comparison = rtData.comparisons.triggers[condition.co]
        def trigger = !!comparison
        if (!comparison) comparison = rtData.comparisons.conditions[condition.co]
        if (rtData.fastForwardTo || comparison) {
            if (!rtData.fastForwardTo) {
                def paramCount = comparison.p ?: 0
                def lo = null
                def ro = null
                def ro2 = null
                for(int i = 0; i <= paramCount; i++) {
                    def operand = (i == 0 ? condition.lo : (i == 1 ? condition.ro : condition.ro2))
                    //parse the operand
                    def values = evaluateOperand(rtData, condition, operand, i, trigger)                    
                    switch (i) {
                        case 0:
                            lo = [operand: operand, values: values]
                            break
                        case 1:
                            ro = [operand: operand, values: values]
                            break
                        case 2:
                            ro2 = [operand: operand, values: values]
                            break
                    }
                }

                //we now have all the operands, their values, and the comparison, let's get to work
                def options = [smatches: true]
                result = evaluateComparison(rtData, condition.co, lo, ro, ro2, options)
                result = not ? !result : !!result
                //save new values to cache
                def since = now()
                if (lo) for (value in lo.values) rtData.newCache[value.i] = value.v + [s: since]
                if (ro) for (value in ro.values) rtData.newCache[value.i] = value.v + [s: since]
                if (ro2) for (value in ro2.values) rtData.newCache[value.i] = value.v + [s: since]

				if (!rtData.fastForwardTo) tracePoint(rtData, "c:${condition.$}", now() - t, result)
            } else {
                result = true
            }
        }
    }
    if (rtData.cache["c:${condition.$}"] != result) {
    	//condition change
        cancelConditionSchedules(rtData, condition.$)
    }
    rtData.cache["c:${condition.$}"] = result
    //true/false actions
    if ((result || rtData.fastForwardTo) && condition.ts && condition.ts.length) executeStatements(rtData, condition.ts, async)
    if ((!result || rtData.fastForwardTo) && condition.fs && condition.fs.length) executeStatements(rtData, condition.fs, async)
    //restore condition id
    rtData.stack.c = c
    msg.m = "Condition #${condition.$} evaluated $result"
    debug msg, rtData
    return result
}

private Boolean evaluateComparison(rtData, comparison, lo, ro = null, ro2 = null, options = null) {
		def fn = "comp_${comparison}"
        def result = (lo.operand.g == 'any' ? false : true)
        if (options?.matches) {
        	options.devices = [matched: [], unmatched: []]
        }
        //if multiple left values, go through each
        for(value in lo.values) {
        	def res = false
            if (!value.v.x) {
                try {
                    if (!ro) {
                        res = "$fn"(rtData, value)
                    } else {
                        def rres
                        res = (ro.operand.g == 'any' ? false : true)
                        //if multiple right values, go through each
                        for (rvalue in ro.values) {
                            if (!ro2) {
                                rres = "$fn"(rtData, value, rvalue)
                            } else {
                                rres = (ro2.operand.g == 'any' ? false : true)
                                //if multiple right2 values, go through each
                                for (r2value in ro2.values) {
                                    def r2res = "$fn"(rtData, value, rvalue, r2value)

                                    rres = (ro2.operand.g == 'any' ? rres || r2res : rres && r2res)
                                    if (((ro2.operand.g == 'any') && rres) || ((ro2.operand.g != 'any') && !rres)) break
                                }
                            }
                            res = (ro.operand.g == 'any' ? res || rres : res && rres)
                            if (((ro.operand.g == 'any') && res) || ((ro.operand.g != 'any') && !res)) break
                        }
                    }
                } catch(all) {
                    error "Error calling comparison $fn:", rtData, null, all
                    res = false
                }            
            }
            result = (lo.operand.g == 'any' ? result || res : result && res)
            if (options?.matches && value.v.d) {
            	if (res) {
                	options.devices.matched.push(value.v.d)
                } else {
                	options.devices.unmatched.push(value.v.d)
                }
            }
            if ((lo.operand.g == 'any') && res && !(options?.matches)) {
            	//logical OR if we're using the ANY keyword
            	break;
            }
            if ((lo.operand.g == 'all') && !result && !(options?.matches)) {
            	//logical AND if we're using the ALL keyword
            	break;
            }
        }
        return result
}

private cancelStatementSchedules(rtData, statementId) {
	//cancel all schedules that are pending for statement statementId
    if (!(statementId in rtData.cancelations.statements)) {
    	rtData.cancelations.statements.push(statementId)
    }
}

private cancelConditionSchedules(rtData, conditionId) {
	//cancel all schedules that are pending for condition conditionId
    if (!(conditionId in rtData.cancelations.conditions)) {
    	rtData.cancelations.conditions.push(conditionId)
    }
}

private Boolean matchDeviceSubIndex(list, deviceSubIndex) {
	if (!list || !(list instanceof List)) return true
    return list.collect{ "$it".toString() }.indexOf("$deviceSubIndex".toString()) >= 0
}

private Map valueChanged(rtData, comparisonValue) {
	def oldValue = rtData.cache[comparisonValue.i]
    def newValue = comparisonValue.v
    if (!(oldValue instanceof Map)) oldValue = false
    return (!!oldValue && ((oldValue.t != newValue.t) || ("${oldValue.v}" != "${newValue.v}"))) ? [i: comparisonValue.i, v: oldValue] : null
}

//comparison low level functions
private boolean comp_is								(rtData, lv, rv = null, rv2 = null) { return (cast(rtData, lv.v.v, 'string') == cast(rtData, rv.v.v, 'string')) || (lv.v.n && (cast(rtData, lv.v.n, 'string') == cast(rtData, rv.v.v, 'string'))) }
private boolean comp_is_not							(rtData, lv, rv = null, rv2 = null) { return !comp_is(rtData, lv, rv, rv2) }
private boolean comp_is_equal_to					(rtData, lv, rv = null, rv2 = null) { return cast(rtData, lv.v.v, 'decimal') == cast(rtData, rv.v.v, 'decimal') }
private boolean comp_is_not_equal_to				(rtData, lv, rv = null, rv2 = null) { return cast(rtData, lv.v.v, 'decimal') != cast(rtData, rv.v.v, 'decimal') }
private boolean comp_is_different_than				(rtData, lv, rv = null, rv2 = null) { return cast(rtData, lv.v.v, 'decimal') != cast(rtData, rv.v.v, 'decimal') }
private boolean comp_is_less_than					(rtData, lv, rv = null, rv2 = null) { return cast(rtData, lv.v.v, 'decimal') < cast(rtData, rv.v.v, 'decimal') }
private boolean comp_is_less_than_or_equal_to		(rtData, lv, rv = null, rv2 = null) { return cast(rtData, lv.v.v, 'decimal') <= cast(rtData, rv.v.v, 'decimal') }
private boolean comp_is_greater_than				(rtData, lv, rv = null, rv2 = null) { return cast(rtData, lv.v.v, 'decimal') > cast(rtData, rv.v.v, 'decimal') }
private boolean comp_is_greater_than_or_equal_to	(rtData, lv, rv = null, rv2 = null) { return cast(rtData, lv.v.v, 'decimal') >= cast(rtData, rv.v.v, 'decimal') }
private boolean comp_is_even						(rtData, lv, rv = null, rv2 = null) { return cast(rtData, lv.v.v, 'integer').mod(2) == 0 }
private boolean comp_is_odd							(rtData, lv, rv = null, rv2 = null) { return cast(rtData, lv.v.v, 'integer').mod(2) != 0 }
private boolean comp_is_true						(rtData, lv, rv = null, rv2 = null) { return !!cast(rtData, lv.v.v, 'boolean') }
private boolean comp_is_false						(rtData, lv, rv = null, rv2 = null) { return !cast(rtData, lv.v.v, 'boolean') }
private boolean comp_is_inside_of_range				(rtData, lv, rv = null, rv2 = null) { def v = cast(rtData, lv.v.v, 'decimal'); def v1 = cast(rtData, rv.v.v, 'decimal'); def v2 = cast(rtData, rv2.v.v, 'decimal'); return (v1 < v2) ? ((v >= v1) && (v <= v2)) : ((v >= v2) && (v <= v1)); }
private boolean comp_is_outside_of_range			(rtData, lv, rv = null, rv2 = null) { return !comp_is_inside_of_range(rtData, lv, rv, rv2) }
private boolean comp_changed						(rtData, lv, rv = null, rv2 = null) { return valueChanged(rtData, lv); }
private boolean comp_did_not_change					(rtData, lv, rv = null, rv2 = null) { return !valueChanged(rtData, lv); }

/*triggers*/
private boolean comp_gets							(rtData, lv, rv = null, rv2 = null) { return (cast(rtData, lv.v.v, 'string') == cast(rtData, rv.v.v, 'string')) && matchDeviceSubIndex(lv.v.i, rtData.currentEvent.index)}
private boolean comp_changes						(rtData, lv, rv = null, rv2 = null) { return valueChanged(rtData, lv); }
private boolean comp_changes_to						(rtData, lv, rv = null, rv2 = null) { return valueChanged(rtData, lv) && ("${lv.v.v}" == "${rv.v.v}"); }
private boolean comp_changes_away_from				(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); return oldValue && ("${oldValue.v.v}" == "${rv.v.v}"); }
private boolean comp_drops							(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); return oldValue && (cast(rtData, oldValue.v.v, 'decimal') > cast(rtData, lv.v.v, 'decimal')); }
private boolean comp_does_not_drop					(rtData, lv, rv = null, rv2 = null) { return !comp_drops(rtData, lv, rv, rv2); }
private boolean comp_drops_below					(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); return oldValue && (cast(rtData, oldValue.v.v, 'decimal') >= cast(rtData, rv.v.v, 'decimal')) && (cast(rtData, lv.v.v, 'decimal') < cast(rtData, rv.v.v, 'decimal')); }
private boolean comp_drops_to_or_below				(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); return oldValue && (cast(rtData, oldValue.v.v, 'decimal') > cast(rtData, rv.v.v, 'decimal')) && (cast(rtData, lv.v.v, 'decimal') <= cast(rtData, rv.v.v, 'decimal')); }
private boolean comp_rises							(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); return oldValue && (cast(rtData, oldValue.v.v, 'decimal') < cast(rtData, lv.v.v, 'decimal')); }
private boolean comp_does_not_rise					(rtData, lv, rv = null, rv2 = null) { return !comp_rises(rtData, lv, rv, rv2); }
private boolean comp_rises_above					(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); return oldValue && (cast(rtData, oldValue.v.v, 'decimal') <= cast(rtData, rv.v.v, 'decimal')) && (cast(rtData, lv.v.v, 'decimal') > cast(rtData, rv.v.v, 'decimal')); }
private boolean comp_rises_to_or_above				(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); return oldValue && (cast(rtData, oldValue.v.v, 'decimal') < cast(rtData, rv.v.v, 'decimal')) && (cast(rtData, lv.v.v, 'decimal') >= cast(rtData, rv.v.v, 'decimal')); }
private boolean comp_remains_below					(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); return oldValue && (cast(rtData, oldValue.v.v, 'decimal') < cast(rtData, rv.v.v, 'decimal')) && (cast(rtData, lv.v.v, 'decimal') < cast(rtData, rv.v.v, 'decimal')); }
private boolean comp_remains_below_or_equal_to		(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); return oldValue && (cast(rtData, oldValue.v.v, 'decimal') <= cast(rtData, rv.v.v, 'decimal')) && (cast(rtData, lv.v.v, 'decimal') <= cast(rtData, rv.v.v, 'decimal')); }
private boolean comp_remains_above					(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); return oldValue && (cast(rtData, oldValue.v.v, 'decimal') > cast(rtData, rv.v.v, 'decimal')) && (cast(rtData, lv.v.v, 'decimal') > cast(rtData, rv.v.v, 'decimal')); }
private boolean comp_remains_above_or_equal_to		(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); return oldValue && (cast(rtData, oldValue.v.v, 'decimal') >= cast(rtData, rv.v.v, 'decimal')) && (cast(rtData, lv.v.v, 'decimal') >= cast(rtData, rv.v.v, 'decimal')); }
private boolean comp_enters_range					(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); if (!oldValue) return false; def ov = cast(rtData, oldValue.v.v, 'decimal'); def v = cast(rtData, lv.v.v, 'decimal'); def v1 = cast(rtData, rv.v.v, 'decimal'); def v2 = cast(rtData, rv2.v.v, 'decimal'); if (v1 > v2) { def vv = v1; v1 = v2; v2 = vv; }; return ((ov < v1) || (ov > v2)) && ((v >= v1) && (v <= v2)); }
private boolean comp_exits_range					(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); if (!oldValue) return false; def ov = cast(rtData, oldValue.v.v, 'decimal'); def v = cast(rtData, lv.v.v, 'decimal'); def v1 = cast(rtData, rv.v.v, 'decimal'); def v2 = cast(rtData, rv2.v.v, 'decimal'); if (v1 > v2) { def vv = v1; v1 = v2; v2 = vv; }; return ((ov >= v1) && (ov <= v2)) && ((v < v1) || (v > v2)); }
private boolean comp_remains_inside_of_range		(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); if (!oldValue) return false; def ov = cast(rtData, oldValue.v.v, 'decimal'); def v = cast(rtData, lv.v.v, 'decimal'); def v1 = cast(rtData, rv.v.v, 'decimal'); def v2 = cast(rtData, rv2.v.v, 'decimal'); if (v1 > v2) { def vv = v1; v1 = v2; v2 = vv; }; return (ov >= v1) && (ov <= v2) && (v >= v1) && (v <= v2); }
private boolean comp_remains_outside_of_range		(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); if (!oldValue) return false; def ov = cast(rtData, oldValue.v.v, 'decimal'); def v = cast(rtData, lv.v.v, 'decimal'); def v1 = cast(rtData, rv.v.v, 'decimal'); def v2 = cast(rtData, rv2.v.v, 'decimal'); if (v1 > v2) { def vv = v1; v1 = v2; v2 = vv; }; return ((ov < v1) || (ov > v2)) && ((v < v1) || (v > v2)); }
private boolean comp_becomes_even					(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); return oldValue && (cast(rtData, oldValue.v.v, 'integer').mod(2) != 0) && (cast(rtData, lv.v.v, 'integer').mod(2) == 0); }
private boolean comp_becomes_odd					(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); return oldValue && (cast(rtData, oldValue.v.v, 'integer').mod(2) == 0) && (cast(rtData, lv.v.v, 'integer').mod(2) != 0); }
private boolean comp_remains_even					(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); return oldValue && (cast(rtData, oldValue.v.v, 'integer').mod(2) == 0) && (cast(rtData, lv.v.v, 'integer').mod(2) == 0); }
private boolean comp_remains_odd					(rtData, lv, rv = null, rv2 = null) { def oldValue = valueChanged(rtData, lv); return oldValue && (cast(rtData, oldValue.v.v, 'integer').mod(2) != 0) && (cast(rtData, lv.v.v, 'integer').mod(2) != 0); }


private traverseStatements(node, closure, parentNode = null) {
    if (!node) return
	//if a statements element, go through each item
	if (node instanceof List) {
    	for(item in node) {
	    	traverseStatements(item, closure, parentNode)
	    }
        return
	}
    //got a statement, pass it on to the closure
    if (closure instanceof Closure) {
    	closure(node, parentNode)
    }
    //if the statements has substatements, go through them
    if (node.s instanceof List) {
    	traverseStatements(node.s, closure, node)
    }
    if (node.e instanceof List) {
    	traverseStatements(node.e, closure, node)
    }
}

private traverseConditions(node, closure, parentNode = null) {
    if (!node) return
	//if a statements element, go through each item
	if (node instanceof List) {
    	for(item in node) {
	    	traverseConditions(item, closure, parentNode)
	    }
        return
	}
    //got a condition, pass it on to the closure
    if ((node.t == 'condition') && (closure instanceof Closure)) {
    	closure(node, parentNode)
    }
    //if the statements has substatements, go through them
    if (node.c instanceof List) {
    	if (closure instanceof Closure) closure(node, parentNode)
    	traverseConditions(node.c, closure, node)
    }
}

private traverseRestrictions(node, closure, parentNode = null) {
    if (!node) return
	//if a statements element, go through each item
	if (node instanceof List) {
    	for(item in node) {
	    	traverseRestrictions(item, closure, parentNode)
	    }
        return
	}
    //got a restriction, pass it on to the closure
    if ((node.t == 'restriction') && (closure instanceof Closure)) {
    	closure(node, parentNode)
    }
    //if the statements has substatements, go through them
    if (node.r instanceof List) {
    	if (closure instanceof Closure) closure(node, parentNode)
    	traverseRestrictions(node.r, closure, node)
    }
}
private traverseExpressions(node, closure, param, parentNode = null) {
    if (!node) return
	//if a statements element, go through each item
	if (node instanceof List) {
    	for(item in node) {
	    	traverseExpressions(item, closure, param, parentNode)
	    }
        return
	}
    //got a statement, pass it on to the closure
    if (closure instanceof Closure) {
    	closure(node, parentNode, param)
    }
    //if the statements has substatements, go through them
    if (node.i instanceof List) {
    	traverseExpressions(node.i, closure, param, node)
    }
}

private void subscribeAll(rtData) {
	rtData = rtData ?: getRunTimeData()
    def ss = [
    	events: 0,
        controls: 0,
        devices: 0,
    ]
	def x = {
    }
	def msg = timer "Finished subscribing", null, -1
	unsubscribe()
    trace "Subscribing to devices...", rtData, 1
    Map devices = [:]
    Map subscriptions = [:]
    def count = 0
    def hasTriggers = false
    //traverse all statements
    //def statementTraverser
    //def expressionTraverser
    //def operandTraverser
    def expressionTraverser = { expression, parentExpression, comparisonType -> 
        if ((expression.t == 'device') && (expression.id)) {
            devices[expression.id] = [c: (comparisonType ? 1 : 0) + (devices[expression.id]?.c ?: 0)]
            subscriptions["${expression.id}${expression.a}"] = [d: expression.id, a: expression.a, t: comparisonType, c: (subscriptions["${expression.id}${expression.a}"] ? subscriptions["${expression.id}${expression.a}"].c : []) + [condition]]
        }
    }    
    def operandTraverser = { node, operand, comparisonType ->
        switch (operand.t) {
            case "p": //physical device
            	for(deviceId in operand.d) {
	                devices[deviceId] = [c: (comparisonType ? 1 : 0) + (devices[deviceId]?.c ?: 0)]
                    //if we have any trigger, it takes precedence over anything else
                    def ct = subscriptions["$deviceId${operand.a}"]?.t ?: null
                    if ((ct == 'trigger') || (comparisonType == 'trigger')) {
                    	ct = 'trigger'                       
                    } else {
                    	ct = ct ?: comparisonType
                    }
	                subscriptions["$deviceId${operand.a}"] = [d: deviceId, a: operand.a, t: ct , c: (subscriptions["$deviceId${operand.a}"] ? subscriptions["$deviceId${operand.a}"].c : []) + (comparisonType?[node]:[])]
	            }
	            break;
            case "v": //physical device
            	def deviceId = rtData.locationId
                //if we have any trigger, it takes precedence over anything else
                devices[deviceId] = [c: (comparisonType ? 1 : 0) + (devices[deviceId]?.c ?: 0)]
                switch (operand.v) {
                	case 'mode':
                    case 'alarmSystemStatus':
                		def ct = subscriptions["$deviceId${operand.v}"]?.t ?: null
                        if ((ct == 'trigger') || (comparisonType == 'trigger')) {
                            ct = 'trigger'                       
                        } else {
                            ct = ct ?: comparisonType
                        }
                        subscriptions["$deviceId${operand.v}"] = [d: deviceId, a: operand.v, t: ct , c: (subscriptions["$deviceId${operand.v}"] ? subscriptions["$deviceId${operand.v}"].c : []) + (comparisonType?[node]:[])]
                        break;
                }
	            break;
            case "c": //constant
            case "e": //expression
    	        traverseExpressions(operand.exp?.i, expressionTraverser, comparisonType)
        	    break
        }
    }
    def conditionTraverser = { condition, parentCondition ->
    	if (condition.co) {
            def comparison = rtData.comparisons.conditions[condition.co]
            def comparisonType = 'condition'
            if (!comparison) {
                hasTriggers = true
                comparisonType = 'trigger'
                comparison = rtData.comparisons.triggers[condition.co]                	
            }
            if (comparison) {
                condition.ct = comparisonType.take(1)
                def paramCount = comparison.p ?: 0
                for(int i = 0; i <= paramCount; i++) {
                    //get the operand to parse
                    def operand = (i == 0 ? condition.lo : (i == 1 ? condition.ro : condition.ro2))
                    operandTraverser(condition, operand, comparisonType)
                }
            }
        }
        if (condition.ts instanceof List) traverseStatements(condition.ts, statementTraverser)
        if (condition.fs instanceof List) traverseStatements(condition.fs, statementTraverser)                
    }
    def restrictionTraverser = { restriction, parentRestriction ->
    	if (restriction.co) {
            def comparison = rtData.comparisons.conditions[restriction.co]
            def comparisonType = 'condition'
            if (!comparison) {
                hasTriggers = true
                comparisonType = 'trigger'
                comparison = rtData.comparisons.triggers[restriction.co]                	
            }
            if (comparison) {
                def paramCount = comparison.p ?: 0
                for(int i = 0; i <= paramCount; i++) {
                    //get the operand to parse
                    def operand = (i == 0 ? restriction.lo : (i == 1 ? restriction.ro : restriction.ro2))
                    operandTraverser(restriction, operand, null)
                }
            }
        }
    }    
    def statementTraverser = { node, parentNode ->
    	if (node.r) traverseRestrictions(node.r, restrictionTraverser)
    	for(deviceId in node.d) {
        	devices[deviceId] = devices[deviceId] ?: [c: 0]
        }
        if (node.t in ['if', 'while', 'repeat']) {
            traverseConditions((node.c?:[]) + (node.ei?node.ei*.c:[]), conditionTraverser)
        }
        if (node.t == 'switch') {
        	operandTraverser(node, node.lo, 'condition')
        	for (c in node.cs) {
            	operandTraverser(c, c.ro, null)
                //if case is a range, traverse the second operand too
                if (c.t == 'r') operandTraverser(c, c.ro2, null)
                if (c.s instanceof List) {
                	traverseStatements(c.s, statementTraverser)
                }
            }
        }
        if (node.t == 'if') {
        	if (node.ei) traverseStatements(node.ei*.s, statementTraverser)
        }
        
    }
    if (rtData.piston.r) traverseRestrictions(rtData.piston.r, restrictionTraverser)
    if (rtData.piston.s) traverseStatements(rtData.piston.s, statementTraverser)
    //device variables
	for(variable in rtData.piston.v.findAll{ it.t == 'device' }) {
    	for (deviceId in variable.v) {
			devices[deviceId] = [c: 0 + (devices[deviceId]?.c ?: 0)]
        }
    }
    def dds = [:]
    for (subscription in subscriptions) {
    	for (condition in subscription.value.c) if (condition) { condition.s = false }
    	if (subscription.value.t && ((subscription.value.t == "trigger") || (subscription.value.c.sm == "always") || (!hasTriggers && (subscription.value.c.sm != "never")))) {
	    	def device = getDevice(rtData, subscription.value.d)
    	    if (device) {
        		info "Subscribing to $device.${subscription.value.a}...", rtData
		    	for (condition in subscription.value.c) if (condition) { condition.s = (condition.ct == 't') || (condition.cm == 'always') || (!hasTriggers) }
        		subscribe(device, subscription.value.a, deviceHandler)
                ss.events = ss.events + 1
                if (!dds[device.id]) {
                	ss.devices = ss.devices + 1
                	dds[device.id] = 1
                }
            } else {
            	error "Failed subscribing to $device.${subscription.value.a}, device not found", rtData
            }
        } else {
        	devices[subscription.value.d].c = devices[subscription.value.d].c - 1
        }
    }
    //fake subscriptions for controlled devices to force the piston being displayed in those devices' Smart Apps tabs
    for (d in devices.findAll{ it.value.c <= 0 }) {
    	def device = getDevice(rtData, d.key)
        if (device && (device != location)) {
       		trace "Subscribing to $device...", rtData
			subscribe(device, "", fakeHandler)
            ss.controls = ss.controls + 1
            if (!dds[device.id]) {
                ss.devices = ss.devices + 1
                dds[device.id] = 1
            }
        }
    }
    state.subscriptions = ss
    trace msg, rtData
    
    subscribe(app, appHandler)
}

def appHandler(evt) {
    log.debug "app event ${evt.name}:${evt.value} received"
}


private sanitizeVariableName(name) {
	name = name ? "$name".trim().replace(" ", "_") : null
}

private getDevice(rtData, idOrName) {
	if (rtData.locationId == idOrName) return location
	def device = rtData.devices[idOrName] ?: rtData.devices.find{ it.value.name == idOrName }
    return device    
}

private Map getDeviceAttribute(rtData, deviceId, attributeName, subDeviceIndex = null, trigger = false) {
	if (deviceId == rtData.locationId) {
    	//we have the location here
        switch (attributeName) {
        	case 'mode': 
            	def mode = location.getCurrentMode();
            	return [t: 'string', v: hashId(mode.getId()), n: mode.getName()]
        	case 'alarmSystemStatus': 
				def v = location.currentState("alarmSystemStatus")?.value
                def n = rtData.virtualDevices['alarmSystemStatus']?.o[v]
				return [t: 'string', v: v, n: n]
        }
        return [t: 'string', v: location.getName().toString()]
    }
	def device = getDevice(rtData, deviceId)
    if (device) {
        def attribute = rtData.attributes[attributeName ?: '']
        if (!attribute) {
            attribute = [t: 'string', m: false]
        }
        //x = eXclude - if a momentary attribute is looked for and the device does not match the current device, then we must ignore this during comparisons
		return [t: attribute.t, v: (attributeName ? cast(rtData, device.currentValue(attributeName), attribute.t) : "$device"), d: deviceId, a: attributeName, i: subDeviceIndex, x: (!!attribute.m || !!trigger) && ((device?.id != (rtData.event.device?:location).id) || (attributeName != rtData.event.name))]
    }
    return [t: "error", v: "Device '${deviceId}' not found"]
}

private getVariable(rtData, name) {
	name = sanitizeVariableName(name)
	if (!name) return [t: "error", v: "Invalid empty variable name"]
    def result
	if (name.startsWith("@")) {
    	result = rtData.globalVars[name]
        if (!(result instanceof Map)) result = [t: "error", v: "Variable '$name' not found"]
	} else {
		if (name.startsWith("\$")) {
			result = rtData.systemVars[name]
            if (!(result instanceof Map)) result = [t: "error", v: "Variable '$name' not found"]
            if (result && result.d) {
            	result = [t: result.t, v: getSystemVariableValue(rtData, name)]
            }
		} else {
			result = rtData.localVars[name]
            if (!(result instanceof Map)) result = [t: "error", v: "Variable '$name' not found"]
		}
	}
    if (result && (result.t == 'device')) {
	   	def deviceIds = []
        def devices = []
        for(deviceId in ((result.v instanceof List) ? result.v : [result.v])) {
            deviceIds.push(deviceId)
        }
	    result = [t: result.t, v: deviceIds]
    } else if (result.v instanceof Map) {
    	//we're dealing with an operand, let's parse it
        result = evaluateExpression(rtData, evaluateOperand(rtData, null, result.v), result.t)
    }
    return result
}

def setVariable(rtData, name, value) {
	name = sanitizeVariableName(name)
	if (!name) return [t: "error", v: "Invalid empty variable name"]
	if (name.startsWith("@")) {
    	def variable = rtData.globalVars[name]
    	if (variable instanceof Map) {
        	//set global var
            variable.v = cast(rtData, value, variable.t)
            return variable
        }
	} else {
		def variable = rtData.localVars[name]
        if (variable instanceof Map) {
            //set value
            variable.v = cast(rtData, value, variable.t)
            if (!variable.f) {
            	def vars = state.vars
                vars[name] = variable.v
                state.vars = vars
            }
            return variable
            
		}
	}
   	result = [t: 'error', v: 'Invalid variable']
}


/******************************************************************************/
/*** 																		***/
/*** EXPRESSION FUNCTIONS													***/
/*** 																		***/
/******************************************************************************/

def Map proxyEvaluateExpression(rtData, expression, dataType = null) {
	resetRandomValues()
	return evaluateExpression(getRunTimeData(rtData), expression, dataType)
}
private Map simplifyExpression(expression) {
	while ((expression.t == 'expression') && expression.i && (expression.i.size() == 1)) expression = expression.i[0]
    return expression
}

private Map evaluateExpression(rtData, expression, dataType = null) {
    //if dealing with an expression that has multiple items, let's evaluate each item one by one
    //let's evaluate this expression
    if (!expression) return [t: 'error', v: 'Null expression']
    //not sure what it was needed for - need to comment more
    //if (expression && expression.v instanceof Map) return evaluateExpression(rtData, expression.v, expression.t)
    expression = simplifyExpression(expression)
    def time = now()
    def result = expression
    switch (expression.t) {
        case "string":
        case "integer":
        case "int32":
        case "int64":
        case "long":
        case "decimal":
        case "boolean":
        case "time":
        case "date":
        case "datetime":
        	result = [t: expression.t, v: cast(rtData, expression.v, expression.t)]
        	break
        case "enum":
        case "error":
        case "phone":
        case "text":
        	result = [t: 'string', v: cast(rtData, expression.v, 'string')]
        	break        
		case "bool":
        	result = [t: "boolean", v: cast(rtData, expression.v, "boolean")]
        	break
        case "number":
        case "float":
        	result = [t: "decimal", v: cast(rtData, expression.v, "decimal")]
			result = expression
        	break
        case "duration":
        	def multiplier = 1
            switch (expression.vt) {
            	case 'ms': multiplier = 1; break;
            	case 's': multiplier = 1000; break;
            	case 'm': multiplier = 60000; break;
            	case 'h': multiplier = 3600000; break;
            	case 'd': multiplier = 86400000; break;
            	case 'w': multiplier = 604800000; break;
            	case 'n': multiplier = 2592000000; break;
            	case 'y': multiplier = 31536000000; break;
            }
        	result = [t: "long", v: cast(rtData, cast(rtData, expression.v, 'decimal') * multiplier, "long")]
        	break
        case "variable":
        	//get variable as {n: name, t: type, v: value}
           	result = [t: 'error', v: 'Invalid variable']
        	result = getVariable(rtData, expression.x)
        	break
        case "device":
        	//get variable as {n: name, t: type, v: value}
            if (expression.v instanceof List) {
            	//already parsed
                result = expression
            } else {
                def deviceIds = (expression.id instanceof List) ? expression.id : (expression.id ? [expression.id] : [])
                if (!deviceIds.size()) {
                    def var = getVariable(rtData, expression.x)
                    if (var && (var.t == 'device')) {
                        deviceIds = var.v
                    }
                }
				result = [t: 'device', v: deviceIds, a: expression.a]
            }
        	break
        case "operand":
        	result = [t: "string", v: cast(rtData, expression.v, "string")]
        	break
        case "function":
            def fn = "func_${expression.n}"
            //in a function, we look for device parameters, they may be lists - we need to reformat all parameters to send them to the function properly
            try {
				def params = []
                if (expression.i && expression.i.size()) {
                    for (i in expression.i) {
                    	def param = simplifyExpression(i)
                        if (param.t == 'device') {
                        	//if multiple devices involved, we need to spread the param into multiple params
                            param = evaluateExpression(rtData, param)
                            switch (param.v.size()) {
                            	case 0: break;
                            	case 1: params.push(param); break;
                                default:
		                            for (v in param.v) {
                                    	params.push([t: param.t, a: param.a, v: [v]])
                                    }
                            }
                        } else {
                        	params.push(param);
                        }
                    }
                }
				result = "$fn"(rtData, params)
			} catch (all) {
				//log error
                result = [t: "error", v: all]
			}        	
        	break
        case "expression":
        	//if we have a single item, we simply traverse the expression
        	List items = []
            def operand = -1
        	for(item in expression.i) {
	            if (item.t == "operator") {
                	if (operand < 0) {
                    	switch (item.o) {
                        	case '+':
                            case '-':
                            case '^':
                            	items.push([t: decimal, v: 0, o: item.o])
                                break;
                        	case '*':
                            case '/':
                            	items.push([t: decimal, v: 1, o: item.o])
                                break;
                        	case '&':
                            	items.push([t: boolean, v: true, o: item.o])
                                break;
                        	case '|':
                            	items.push([t: boolean, v: false, o: item.o])
                                break;
                        }
                    } else {
                    	items[operand].o = item.o;
                        operand = -1;
                    }
	            } else {
	                items.push(evaluateExpression(rtData, item) + [:])
                    operand = items.size() - 1;
	            }
	        }
            //clean up operators, ensure there's one for each
            def idx = 0
            for(item in items) {
            	if (!item.o) {
                	switch (item.t) {
                    	case "integer":
                    	case "float":
                    	case "decimal":
                    	case "number":
                        	def nextType = 'string'
                        	if (idx < items.size() - 1) nextType = items[idx+1].t
                        	item.o = (nextType == 'string' || nextType == 'text') ? '+' : '*';
                            break;
                        default:
                        	item.o = '+';
                            break;
                    }
                }
                idx++
            }
            //do the job
            idx = 0
            while (items.size() > 1) {
	           	//order of operations :D
                //we first look for power ^
                idx = 0
                for (item in items) {
                	if ((item.o) == '^') break;
                    idx++
                }
                if (idx >= items.size()) {
                    //we then look for * or /
                    idx = 0
                    for (item in items) {
                        if (((item.o) == '*') || ((item.o) == '/')) break;
                        idx++
                    }
                }
                if (idx >= items.size()) {
                    //we then look for + or -
                    idx = 0
                    for (item in items) {
                        if (((item.o) == '+') || ((item.o) == '-')) break;
                        idx++
                    }
                }
                if (idx >= items.size()) {
                	//just get the first one
                	idx = 0;
                }                
                if (idx >= items.size() - 1) idx = 0
                //we're onto something
                def v = null
                def o = items[idx].o
                def t1 = items[idx].t
                def v1 = items[idx].v
                def t2 = items[idx + 1].t
                def v2 = items[idx + 1].v
                def t = t1
                //fix-ups
                //integer with decimal gives decimal, also *, /, and ^ require decimals
                if ((t1 == 'device') && (t2 == 'device') && ((o == '+') || (o == '-'))) {
					v1 = (v1 instanceof List) ? v1 : [v1]
					v2 = (v2 instanceof List) ? v2 : [v2]
                    v = (o == '+') ? v1 + v2 : v1 - v2                    
        	        //set the results
    	            items[idx + 1].t = 'device'
                    items[idx + 1].v = v
                } else {
                    if ((o == '*') || (o == '*') || (o == '/') || (o == '-') || (o == '^')) {
                        if ((t1 != 'number') && (t1 != 'integer') && (t1 != 'decimal') && (t1 != 'float') && (t1 != 'time')) t1 = 'decimal'
                        if ((t2 != 'number') && (t2 != 'integer') && (t2 != 'decimal') && (t2 != 'float') && (t2 != 'time')) t2 = 'decimal'
                        t = (t1 == 'time') || (t2 == 'time') ? 'time' : 'decimal'
                    }
                    if ((o == '&') || (o == '|')) {
                        t1 = 'boolean'
                        t2 = 'boolean'
                        t = 'boolean'
                    }
                    if ((o == '+') && ((t1 == 'string') || (t1 == 'text') || (t2 == 'string') || (t2 == 'text'))) {
                        t1 = 'string';
                        t2 = 'string';
                        t = 'string'
                    }
                    if ((((t1 == 'number') || (t1 == 'integer')) && ((t2 == 'decimal') || (t2 == 'float'))) || (((t2 == 'number') || (t2 == 'integer')) && ((t1 == 'decimal') || (t1 == 'float')))) {
                        t1 = 'decimal'
                        t2 = 'decimal'
                        t = 'decimal'
                    }
                    if ((t != 'time') && ((t1 == 'integer') || (t2 == 'integer'))) {
                        t1 = 'integer'
                        t2 = 'integer'
                        t = 'integer'
                    }
                    if ((t != 'time') && ((t1 == 'number') || (t2 == 'number') || (t1 == 'decimal') || (t2 == 'decimal') || (t1 == 'float') || (t2 == 'float'))) {
                        t1 = 'decimal'
                        t2 = 'decimal'
                        t = 'decimal'
                    }
                    v1 = evaluateExpression(rtData, items[idx], t1).v
	                v2 = evaluateExpression(rtData, items[idx + 1], t2).v
    	            switch (o) {
        	            case '-':
            	        	v = v1 - v2
                	    	break
	                    case '*':
    	                	v = v1 * v2
        	            	break
            	        case '/':
                	    	v = (v2 != 0 ? v1 / v2 : 0)
	                    	break
    	                case '^':
        	            	v = v1 ** v2
            	        	break
                	    case '&':
	                    	v = !!v1 && !!v2
    	                	break
        	            case '|':
            	        	v = !!v1 || !!v2
                	    	break
	                    case '+':
    	                default:                    	
        	                v = t == 'string' ? "$v1$v2" : v1 + v2
            	        	break
                	}

                    //set the results
                    items[idx + 1].t = t
                    items[idx + 1].v = cast(rtData, v, t)
                }
                def sz = items.size()
                items.remove(idx)
            }
    	    result = items[0] ? ((items[0].t == 'device') ? items[0] : evaluateExpression(rtData, items[0])) : [t: 'dynamic', v: null]
	        break
    }
    //return the value, either directly or via cast, if certain data type is requested
  	//when dealing with devices, they need to be "converted" unless the request is to return devices
    if (dataType && (dataType != 'device') && (result.t == 'device')) {
        switch (result.v.size()) {
            case 0: result = [t: 'error', v: 'Empty device list']; break;
            case 1: result = getDeviceAttribute(rtData, result.v[0], result.a, result.i); break;
            default: result = [t: 'string', v: buildDeviceAttributeList(rtData, result.v, result.a)]; break;
        }
    }
    if (dataType) {
    	result = [t: dataType, v: cast(rtData, result.v, dataType)] + (result.a ? [a: result.a] : [:]) + (result.i ? [a: result.i] : [:])
    }
    result.d = now() - time;
	return result
}

private buildList(list, suffix = 'and') {
    if (!list) return ''
    if (!(list instanceof List)) list = [list]
	def cnt = 1
	def result = ""
	for (item in list) {
		result += "$item" + (cnt < list.size() ? (cnt == list.size() - 1 ? " $suffix " : ", ") : "")
		cnt++
	}
	return result;
}

private buildDeviceList(rtData, devices, suffix = 'and') {
    if (!devices) return ''
    if (!(devices instanceof List)) devices = [devices]
    def list = []
	for (device in devices) {
    	def dev = getDevice(rtData, device)
        if (dev) list.push(dev)
	}
	return buildList(list, suffix);
}

private buildDeviceAttributeList(rtData, devices, attribute, suffix = 'and') {
    if (!devices) return ''
    if (!(devices instanceof List)) devices = [devices]
    def list = []
	for (device in devices) {
    	def value = getDeviceAttribute(rtData, device, attribute).v
        list.push(value)
	}
	return buildList(list, suffix);
}


/******************************************************************************/
/*** dewPoint returns the calculated dew point temperature					***/
/*** Usage: dewPoint(temperature, relativeHumidity[, scale])				***/
/******************************************************************************/
private func_dewpoint(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting dewPoint(temperature, relativeHumidity[, scale])"];
    }
    double t = evaluateExpression(rtData, params[0], 'decimal').v
    double rh = evaluateExpression(rtData, params[1], 'decimal').v
    //if no temperature scale is provided, we assume the location's temperature scale
    boolean fahrenheit = cast(rtData, params.size() > 2 ? evaluateExpression(rtData, params[2]).v : location.temperatureScale, "string").toUpperCase() == "F"
    if (fahrenheit) {
    	//convert temperature to Celsius
        t = (t - 32.0) * 5.0 / 9.0
    }
    //convert rh to percentage
    if ((rh > 0) && (rh < 1)) {
    	rh = rh * 100.0
    }
    double b = (Math.log(rh / 100) + ((17.27 * t) / (237.3 + t))) / 17.27
	double result = (237.3 * b) / (1 - b)
    if (fahrenheit) {
    	//convert temperature back to Fahrenheit
        result = result * 9.0 / 5.0 + 32.0
    }
    return [t: "decimal", v: result]
}

/******************************************************************************/
/*** celsius converts temperature from Fahrenheit to Celsius				***/
/*** Usage: celsius(temperature)											***/
/******************************************************************************/
private func_celsius(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting celsius(temperature)"];
    }
    double t = evaluateExpression(rtData, params[0], 'decimal').v
    //convert temperature to Celsius
    return [t: "decimal", v: (double) (t - 32.0) * 5.0 / 9.0]
}


/******************************************************************************/
/*** fahrenheit converts temperature from Celsius to Fahrenheit				***/
/*** Usage: fahrenheit(temperature)											***/
/******************************************************************************/
private func_fahrenheit(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting fahrenheit(temperature)"];
    }
    double t = evaluateExpression(rtData, params[0], 'decimal').v
    //convert temperature to Fahrenheit
    return [t: "decimal", v: (double) t * 9.0 / 5.0 + 32.0]
}

/******************************************************************************/
/*** integer converts a decimal value to it's integer value					***/
/*** Usage: integer(decimal or string)										***/
/******************************************************************************/
private func_integer(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting integer(decimal or string)"];
    }
    return [t: "integer", v: evaluateExpression(rtData, params[0], 'integer').v]
}
private func_int(rtData, params) { return func_integer(rtData, params) }

/******************************************************************************/
/*** decimal/float converts an integer value to it's decimal value			***/
/*** Usage: decimal(integer or string)										***/
/******************************************************************************/
private func_decimal(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting decimal(integer or string)"];
    }
    return [t: "decimal", v: evaluateExpression(rtData, params[0], 'decimal').v]
}
private func_float(rtData, params) { return func_decimal(rtData, params) }
private func_number(rtData, params) { return func_decimal(rtData, params) }

/******************************************************************************/
/*** string converts an value to it's string value							***/
/*** Usage: string(anything)												***/
/******************************************************************************/
private func_string(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting string(anything)"];
    }
	def result = ''
    for(param in params) {
    	result += evaluateExpression(rtData, param, 'string').v
    }
    return [t: "string", v: result]
}
private func_concat(rtData, params) { return func_string(rtData, params) }
private func_text(rtData, params) { return func_string(rtData, params) }

/******************************************************************************/
/*** boolean converts a value to it's boolean value							***/
/*** Usage: boolean(anything)												***/
/******************************************************************************/
private func_boolean(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting boolean(anything)"];
    }
    return [t: "boolean", v: evaluateExpression(rtData, params[0], 'boolean').v]
}
private func_bool(rtData, params) { return func_boolean(rtData, params) }

/******************************************************************************/
/*** sqr converts a decimal value to it's square decimal value				***/
/*** Usage: sqr(integer or decimal or string)								***/
/******************************************************************************/
private func_sqr(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting sqr(integer or decimal or string)"];
    }
    return [t: "decimal", v: evaluateExpression(rtData, params[0], 'decimal').v ** 2]
}

/******************************************************************************/
/*** sqrt converts a decimal value to it's square root decimal value		***/
/*** Usage: sqrt(integer or decimal or string)								***/
/******************************************************************************/
private func_sqrt(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting sqrt(integer or decimal or string)"];
    }
    return [t: "decimal", v: Math.sqrt(evaluateExpression(rtData, params[0], 'decimal').v)]
}

/******************************************************************************/
/*** power converts a decimal value to it's power decimal value				***/
/*** Usage: power(integer or decimal or string, power)						***/
/******************************************************************************/
private func_power(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting sqrt(integer or decimal or string, power)"];
    }
    return [t: "decimal", v: evaluateExpression(rtData, params[0], 'decimal').v ** evaluateExpression(rtData, params[1], 'decimal').v]
}

/******************************************************************************/
/*** round converts a decimal value to it's rounded value					***/
/*** Usage: round(decimal or string[, precision])							***/
/******************************************************************************/
private func_round(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting round(decimal or string[, precision])"];
    }
    int precision = (params.size() > 1) ? evaluateExpression(rtData, params[1], 'integer').v : 0
    return [t: "decimal", v: Math.round(evaluateExpression(rtData, params[0], 'decimal').v * (10 ** precision)) / (10 ** precision)]
}

/******************************************************************************/
/*** floor converts a decimal value to it's closest lower integer value		***/
/*** Usage: floor(decimal or string)										***/
/******************************************************************************/
private func_floor(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting floor(decimal or string)"];
    }
    return [t: "integer", v: cast(rtData, Math.floor(evaluateExpression(rtData, params[0], 'decimal').v), 'integer')]
}

/******************************************************************************/
/*** ceiling converts a decimal value to it's closest higher integer value	***/
/*** Usage: ceiling(decimal or string)										***/
/******************************************************************************/
private func_ceiling(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting ceiling(decimal or string)"];
    }
    return [t: "integer", v: cast(rtData, Math.ceil(evaluateExpression(rtData, params[0], 'decimal').v), 'integer')]
}
private func_ceil(rtData, params) { return func_ceiling(rtData, params) }


/******************************************************************************/
/*** sprintf converts formats a series of values into a string				***/
/*** Usage: sprintf(format, arguments)										***/
/******************************************************************************/
private func_sprintf(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting sprintf(format, arguments)"];
    }
    def format = evaluateExpression(rtData, params[0], 'string').v
    List args = []
    for (int x = 1; x < params.size(); x++) {
    	args.push(evaluateExpression(rtData, params[x]).v)
    }
    try {
        return [t: "string", v: sprintf(format, args)]
    } catch(all) {
    	return [t: "error", v: "$all"]
    }
}
private func_format(rtData, params) { return func_sprintf(rtData, params) }

/******************************************************************************/
/*** left returns a substring of a value									***/
/*** Usage: left(string, count)												***/
/******************************************************************************/
private func_left(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting left(string, count)"];
    }
    def value = evaluateExpression(rtData, params[0], 'string').v
    def count = evaluateExpression(rtData, params[1], 'integer').v
    if (count > value.size()) count = value.size()    
    return [t: "string", v: value.substring(0, count)]
}

/******************************************************************************/
/*** right returns a substring of a value									***/
/*** Usage: right(string, count)												***/
/******************************************************************************/
private func_right(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting right(string, count)"];
    }
    def value = evaluateExpression(rtData, params[0], 'string').v
    def count = evaluateExpression(rtData, params[1], 'integer').v
    if (count > value.size()) count = value.size()
    return [t: "string", v: value.substring(value.size() - count, value.size())]
}

/******************************************************************************/
/*** substring returns a substring of a value								***/
/*** Usage: substring(string, start, count)									***/
/******************************************************************************/
private func_substring(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting substring(string, start, count)"];
    }
    def value = evaluateExpression(rtData, params[0], 'string').v
    def start = evaluateExpression(rtData, params[1], 'integer').v
   	def count = params.size() > 2 ? evaluateExpression(rtData, params[2], 'integer').v : null
    def end = null
    def result = ''
    if ((start < value.size()) && (start > -value.size())) {
        if (count != null) {
        	if (count < 0) {
           		//reverse
                start = start < 0 ? -start : value.size() - start
                count = - count
                value = value.reverse()
            }
        	if (start >= 0) {
            	if (count > value.size() - start) count = value.size() - start
            } else {
            	if (count > -start) count = -start
            }
        }
        start = start >= 0 ? start : value.size() + start
        if (count > value.size() - start) count = value.size() - start
        result = value.substring(start, count == null ? null : start + count)
    }
    return [t: "string", v: result]
}
private func_substr(rtData, params) { return func_substring(rtData, params) }
private func_mid(rtData, params) { return func_substring(rtData, params) }

/******************************************************************************/
/*** replace replaces a search text inside of a value						***/
/*** Usage: replace(string, search, replace[, [..], search, replace])		***/
/******************************************************************************/
private func_replace(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 3)) {
    	return [t: "error", v: "Invalid parameters. Expecting replace(string, search, replace)"];
    }
    def value = evaluateExpression(rtData, params[0], 'string').v
    int cnt = Math.floor((params.size() - 1) / 2)
    for (int i = 0; i < cnt; i++) {
    	def search = evaluateExpression(rtData, params[i * 2 + 1], 'string').v
        if ((search.size() > 2) && search.startsWith('/') && search.endsWith('/')) {
        	search = ~search.substring(1, search.size() - 1)
        }
        value = value.replaceAll(search, evaluateExpression(rtData, params[i * 2 + 2], 'string').v)
    }
    return [t: "string", v: value]
}


/******************************************************************************/
/*** lower returns a lower case value of a string							***/
/*** Usage: lower(string)													***/
/******************************************************************************/
private func_lower(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting lower(string)"];
    }
    def result = ''
    for(param in params) {
    	result += evaluateExpression(rtData, param, 'string').v
    }
    return [t: "string", v: result.toLowerCase()]
}

/******************************************************************************/
/*** upper returns a upper case value of a string							***/
/*** Usage: upper(string)													***/
/******************************************************************************/
private func_upper(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting upper(string)"];
    }
    def result = ''
    for(param in params) {
    	result += evaluateExpression(rtData, param, 'string').v
    }
    return [t: "string", v: result.toUpperCase()]
}

/******************************************************************************/
/*** title returns a title case value of a string							***/
/*** Usage: title(string)													***/
/******************************************************************************/
private func_title(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting title(string)"];
    }
    def result = ''
    for(param in params) {
    	result += evaluateExpression(rtData, param, 'string').v
    }
    return [t: "string", v: result.tokenize(" ")*.toLowerCase()*.capitalize().join(" ")]
}

/******************************************************************************/
/*** avg calculates the average of a series of numeric values				***/
/*** Usage: avg(values)														***/
/******************************************************************************/
private func_avg(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting avg(value1, value2, ..., valueN)"];
    }
    float sum = 0
    for (param in params) {
    	sum += evaluateExpression(rtData, param, 'decimal').v
    }
    return [t: "decimal", v: sum / params.size()]
}

/******************************************************************************/
/*** median returns the value in the middle of a sorted array				***/
/*** Usage: median(values)													***/
/******************************************************************************/
private func_median(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting median(value1, value2, ..., valueN)"];
    }
    List data = params.collect{ evaluateExpression(rtData, it, 'dynamic') }.sort{ it.v }
    if (data.size()) {
    	return data[(int) Math.floor(data.size() / 2)]
    }
    return [t: 'dynamic', v: '']
}


/******************************************************************************/
/*** least returns the value that is least found a series of numeric values	***/
/*** Usage: least(values)													***/
/******************************************************************************/
private func_least(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting least(value1, value2, ..., valueN)"];
    }
    Map data = [:]
    for (param in params) {
    	def value = evaluateExpression(rtData, param, 'dynamic')
    	data[value.v] = [t: value.t, v: value.v, c: (data[value.v]?.c ?: 0) + 1]
    }
    def value = data.sort{ it.value.c }.collect{ it.value }[0]
    return [t: value.t, v: value.v]
}

/******************************************************************************/
/*** most returns the value that is most found a series of numeric values	***/
/*** Usage: most(values)													***/
/******************************************************************************/
private func_most(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting most(value1, value2, ..., valueN)"];
    }
    Map data = [:]
    for (param in params) {
    	def value = evaluateExpression(rtData, param, 'dynamic')
    	data[value.v] = [t: value.t, v: value.v, c: (data[value.v]?.c ?: 0) + 1]
    }
    def value = data.sort{ - it.value.c }.collect{ it.value }[0]
    return [t: value.t, v: value.v]
}

/******************************************************************************/
/*** sum calculates the sum of a series of numeric values					***/
/*** Usage: sum(values)														***/
/******************************************************************************/
private func_sum(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting sum(value1, value2, ..., valueN)"];
    }
    float sum = 0
    for (param in params) {
    	sum += evaluateExpression(rtData, param, 'decimal').v
    }
    return [t: "decimal", v: sum]
}

/******************************************************************************/
/*** variance calculates the standard deviation of a series of numeric values */
/*** Usage: stdev(values)													***/
/******************************************************************************/
private func_variance(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting variance(value1, value2, ..., valueN)"];
    }
    float sum = 0
    List values = []
    for (param in params) {
    	float value = evaluateExpression(rtData, param, 'decimal').v
        values.push(value)
        sum += value
    }
    float avg = sum / values.size()
    sum = 0
    for(int i  = 0; i < values.size(); i++) {
    	sum += (values[i] - avg) ** 2
    }
    return [t: "decimal", v: sum / values.size()]
}

/******************************************************************************/
/*** stdev calculates the standard deviation of a series of numeric values	***/
/*** Usage: stdev(values)													***/
/******************************************************************************/
private func_stdev(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting stdev(value1, value2, ..., valueN)"];
    }
    def result = func_variance(rtData, params)
    return [t: "decimal", v: Math.sqrt(result.v)]
}

/******************************************************************************/
/*** min calculates the minimum of a series of numeric values				***/
/*** Usage: min(values)														***/
/******************************************************************************/
private func_min(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting min(value1, value2, ..., valueN)"];
    }
    List data = params.collect{ evaluateExpression(rtData, it, 'dynamic') }.sort{ it.v }
    if (data.size()) {
    	return data[0]
    }
    return [t: 'dynamic', v: '']
}

/******************************************************************************/
/*** max calculates the maximum of a series of numeric values				***/
/*** Usage: max(values)														***/
/******************************************************************************/
private func_max(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting max(value1, value2, ..., valueN)"];
    }
    List data = params.collect{ evaluateExpression(rtData, it, 'dynamic') }.sort{ it.v }
    if (data.size()) {
    	return data[data.size() - 1]
    }
    return [t: 'dynamic', v: '']
}


/******************************************************************************/
/*** count calculates the number of true/non-zero/non-empty items in a series of numeric values		***/
/*** Usage: count(values)														***/
/******************************************************************************/
private func_count(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting count(value1, value2, ..., valueN)"];
    }
    def count = 0
    for (param in params) {
    	count += evaluateExpression(rtData, param, 'boolean').v ? 1 : 0
    }
    return [t: "integer", v: count]
}

/******************************************************************************/
/*** age returns the number of milliseconds an attribute had the current value*/
/*** Usage: age([device:attribute])											***/
/******************************************************************************/
private func_age(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() != 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting age([device:attribute])"];
    }
    def param = evaluateExpression(rtData, params[0], 'device')
    if ((param.t == 'device') && (param.a) && param.v.size()) {
		def device = getDevice(rtData, param.v[0])
        if (device) {
        	def state = device.currentState(param.a)
            if (state) {
            	long result = now() - state.getDate().getTime()
                return [t: "long", v: result]
            }
        }
    }
    return [t: "error", v: "Invalid device"]
}

/******************************************************************************/
/*** previousAge returns the number of milliseconds an attribute had the 	***/
/*** previous value															***/
/*** Usage: previousAge([device:attribute])									***/
/******************************************************************************/
private func_previousage(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() != 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting previousAge([device:attribute])"];
    }
    def param = evaluateExpression(rtData, params[0], 'device')
    if ((param.t == 'device') && (param.a) && param.v.size()) {
		def device = getDevice(rtData, param.v[0])
        if (device) {
        	def states = device.statesSince(param.a, new Date(now() - 604500000), [max: 5])
            if (states.size() > 1) {
            	def newValue = states[0].getValue()
                //some events get duplicated, so we really want to look for the last "different valued" state
                for(int i = 1; i < states.size(); i++) {
                	if (states[i].getValue() != newValue) {
            			def result = now() - states[i].getDate().getTime()
                		return [t: "long", v: result]
                    }
                }
            }
            //we're saying 7 days, though it may be wrong - but we have no data
             return [t: "long", v: 604800000]
        }
    }
    return [t: "error", v: "Invalid device"]
}

/******************************************************************************/
/*** previousValue returns the previous value of the attribute				***/
/*** Usage: previousValue([device:attribute])								***/
/******************************************************************************/
private func_previousvalue(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() != 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting previousValue([device:attribute])"];
    }
    def param = evaluateExpression(rtData, params[0], 'device')
    if ((param.t == 'device') && (param.a) && param.v.size()) {
    	def attribute = rtData.attributes[param.a]
        if (attribute) {
			def device = getDevice(rtData, param.v[0])
	        if (device) {
                def states = device.statesSince(param.a, new Date(now() - 604500000), [max: 5])
                if (states.size() > 1) {
                    def newValue = states[0].getValue()
                    //some events get duplicated, so we really want to look for the last "different valued" state
                    for(int i = 1; i < states.size(); i++) {
                        def result = states[i].getValue()
                        if (result != newValue) {
                            return [t: attribute.t, v: cast(rtData, result, attribute.t)]
                        }
                    }                    
                }
                //we're saying 7 days, though it may be wrong - but we have no data
                return [t: 'string', v: '']
            }
        }
    }
    return [t: "error", v: "Invalid device"]
}

/******************************************************************************/
/*** newer returns the number of devices whose attribute had the current    ***/
/*** value for less than the specified number of milliseconds			    ***/
/*** Usage: newer([device:attribute] [,.., [device:attribute]], threshold)	***/
/******************************************************************************/
private func_newer(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting newer([device:attribute] [,.., [device:attribute]], threshold)"];
    }
    def threshold = evaluateExpression(rtData, params[params.size() - 1], 'integer').v
    int result = 0
    for (def i = 0; i < params.size() - 1; i++) {
    	def age = func_age(rtData, [params[i]])
        if ((age.t != 'error') && (age.v < threshold)) result++
    }
    return [t: "integer", v: result]
}

/******************************************************************************/
/*** older returns the number of devices whose attribute had the current    ***/
/*** value for more than the specified number of milliseconds			    ***/
/*** Usage: older([device:attribute] [,.., [device:attribute]], threshold)	***/
/******************************************************************************/
private func_older(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting older([device:attribute] [,.., [device:attribute]], threshold)"];
    }
    def threshold = evaluateExpression(rtData, params[params.size() - 1], 'integer').v
    int result = 0
    for (def i = 0; i < params.size() - 1; i++) {
    	def age = func_age(rtData, [params[i]])
        if ((age.t != 'error') && (age.v >= threshold)) result++
    }
    return [t: "integer", v: result]
}

/******************************************************************************/
/*** startsWith returns true if a string starts with a substring			***/
/*** Usage: startsWith(string, substring)									***/
/******************************************************************************/
private func_startswith(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() != 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting startsWith(string, substring)"];
    }
    def string = evaluateExpression(rtData, params[0], 'string').v
    def substring = evaluateExpression(rtData, params[1], 'string').v
    return [t: "boolean", v: string.startsWith(substring)]
}

/******************************************************************************/
/*** endsWith returns true if a string ends with a substring				***/
/*** Usage: endsWith(string, substring)										***/
/******************************************************************************/
private func_endswith(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() != 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting endsWith(string, substring)"];
    }
    def string = evaluateExpression(rtData, params[0], 'string').v
    def substring = evaluateExpression(rtData, params[1], 'string').v
    return [t: "boolean", v: string.endsWith(substring)]
}

/******************************************************************************/
/*** contains returns true if a string contains a substring					***/
/*** Usage: contains(string, substring)										***/
/******************************************************************************/
private func_contains(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() != 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting contains(string, substring)"];
    }
    def string = evaluateExpression(rtData, params[0], 'string').v
    def substring = evaluateExpression(rtData, params[1], 'string').v
    return [t: "boolean", v: string.contains(substring)]
}

/******************************************************************************/
/*** eq returns true if two values are equal								***/
/*** Usage: eq(value1, value2)												***/
/******************************************************************************/
private func_eq(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() != 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting eq(value1, value2)"];
    }
    def value1 = evaluateExpression(rtData, params[0])
    def value2 = evaluateExpression(rtData, params[1], value1.t)
    return [t: "boolean", v: value1.v == value2.v]
}

/******************************************************************************/
/*** lt returns true if value1 < value2										***/
/*** Usage: lt(value1, value2)												***/
/******************************************************************************/
private func_lt(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() != 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting lt(value1, value2)"];
    }
    def value1 = evaluateExpression(rtData, params[0])
    def value2 = evaluateExpression(rtData, params[1], value1.t)
    return [t: "boolean", v: value1.v < value2.v]
}

/******************************************************************************/
/*** le returns true if value1 <= value2									***/
/*** Usage: le(value1, value2)												***/
/******************************************************************************/
private func_le(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() != 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting le(value1, value2)"];
    }
    def value1 = evaluateExpression(rtData, params[0])
    def value2 = evaluateExpression(rtData, params[1], value1.t)
    return [t: "boolean", v: value1.v <= value2.v]
}

/******************************************************************************/
/*** gt returns true if value1 > value2									***/
/*** Usage: gt(value1, value2)												***/
/******************************************************************************/
private func_gt(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() != 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting gt(value1, value2)"];
    }
    def value1 = evaluateExpression(rtData, params[0])
    def value2 = evaluateExpression(rtData, params[1], value1.t)
    return [t: "boolean", v: value1.v > value2.v]
}

/******************************************************************************/
/*** ge returns true if value1 >= value2									***/
/*** Usage: ge(value1, value2)												***/
/******************************************************************************/
private func_ge(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() != 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting ge(value1, value2)"];
    }
    def value1 = evaluateExpression(rtData, params[0])
    def value2 = evaluateExpression(rtData, params[1], value1.t)
    return [t: "boolean", v: value1.v >= value2.v]
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
	return ":${md5("core." + id)}:"
}

private cast(rtData, value, dataType, srcDataType = null) {
	if (dataType == 'dynamic') return value
	def trueStrings = ["1", "true", "on", "open", "locked", "active", "wet", "detected", "present", "occupied", "muted", "sleeping"]
	def falseStrings = ["0", "false", "off", "closed", "unlocked", "inactive", "dry", "clear", "not detected", "not present", "not occupied", "unmuted", "not sleeping"]
	//get rid of GStrings
    if (value == null) {
    	value = '';
        srcDataType = 'string';
    }
	value = (value instanceof GString) ? value.toString() : value
    if (!srcDataType) {
        switch (value) {
            case {it instanceof List}: srcDataType = 'device'; break;
            case {it instanceof Boolean}: srcDataType = 'boolean'; break;
            case {it instanceof String}: srcDataType = 'string'; break;
            case {it instanceof String}: srcDataType = 'string'; break;
            case {it instanceof Integer}: srcDataType = 'integer'; break;
            case {it instanceof BigInteger}: srcDataType = 'long'; break;
            case {it instanceof Long}: srcDataType = 'long'; break;
            case {it instanceof Float}: srcDataType = 'decimal'; break;
            case {it instanceof BigDecimal}: srcDataType = 'decimal'; break;
            default: value = "$value".toString(); srcDataType = 'string'; break;
        }
	}
    //overrides
    switch (srcDataType) {
    	case 'bool': srcDataType = 'boolean'; break;
    	case 'number': srcDataType = 'decimal'; break;
    	case 'enum': srcDataType = 'string'; break;
    }
    switch (dataType) {
    	case 'bool': dataType = 'boolean'; break;
    	case 'number': dataType = 'decimal'; break;
    	case 'enum': dataType = 'string'; break;
    }
    //perform the conversion
	switch (dataType) {
		case "string":
		case "text":
        	switch (srcDataType) {
            	case 'boolean': return value ? "true" : "false";
            	case 'integer':
            	case 'long': if (value > 9999999999) { return formatLocalTime(value) }; break;
                case 'time': return formatLocalTime(value);
                case 'date':
                case 'datetime': return formatLocalTime(value);
                case 'device': return buildDeviceList(rtData, value);
            }
			return "$value".toString()
		case "integer":
			switch (srcDataType) {
            	case 'string':
                    value = value.replaceAll(/[^\d.-]/, '')
                    if (value.isInteger())
                        return (int) value.toInteger()
                    if (value.isFloat())
                        return (int) Math.floor(value.toFloat())
                    if (value in trueStrings)
                        return (int) 1
                    break
				case 'boolean': return (int) (value ? 1 : 0);
            }
			def result = (int) 0
			try {
				result = (int) value
			} catch(all) {
				result = (int) 0
			}
			return result ? result : (int) 0
		case "long":
			switch (srcDataType) {
            	case 'string':
                    value = value.replaceAll(/[^\d.-]/, '')
                    if (value.isLong())
                        return (long) value.toLong()
                    if (value.isInteger())
                        return (long) value.toInteger()
                    if (value.isFloat())
                        return (long) Math.floor(value.toFloat())
                    if (value in trueStrings)
                        return (long) 1
                    break
				case 'boolean': return (long) (value ? 1 : 0);
            }
			def result = (long) 0
			try {
				result = (long) value
			} catch(all) {
				result = (long) 0
			}
			return result ? result : (long) 0
		case "decimal":
			switch (srcDataType) {
            	case 'string':
                    value = value.replaceAll(/[^\d.-]/, '')
                    if (value.isFloat())
                        return (float) value.toFloat()
                    if (value.isLong())
                        return (float) value.toLong()
                    if (value.isInteger())
                        return (float) value.toInteger()
                    if (value in trueStrings)
                        return (float) 1
					break
				case 'boolean': return (float) (value ? 1 : 0);
            }
			def result = (float) 0
			try {
				result = (float) value
			} catch(all) {
			}
			return result ? result : (float) 0
		case "boolean":
			switch (srcDataType) {
            	case 'integer':
            	case 'decimal':
            	case 'boolean':
					return !!value;
			}
            if (value) {
            	if ("$value".toLowerCase().trim() in trueStrings) return true
	            if ("$value".toLowerCase().trim() in falseStrings) return false
            }
			return !!value
		case "time":
			return ((srcDataType == 'string') ? utcToLocalDate(value).time : cast(rtData, value, "long")) % 86400000
		case "date":
			def d = ((srcDataType == 'string') ? utcToLocalDate(value).time : cast(rtData, value, "long"))
            return d - (d % 864000000)
		case "datetime":
			return ((srcDataType == 'string') ? utcToLocalDate(value).time : cast(rtData, value, "long"))
		case "vector3":
			return value instanceof String ? utcToLocalDate(value).time : cast(rtData, value, "long")
		case "orientation":
			return getThreeAxisOrientation(value)
        case 'ms': return cast(rtData, value, 'decimal')
        case 's': return cast(rtData, value, 'decimal') * 1000
        case 'm': return cast(rtData, value, 'decimal') * 60000
        case 'h': return cast(rtData, value, 'decimal') * 3600000
        case 'd': return cast(rtData, value, 'decimal') * 86400000
        case 'w': return cast(rtData, value, 'decimal') * 604800000
        case 'n': return cast(rtData, value, 'decimal') * 2592000000
        case 'y': return cast(rtData, value, 'decimal') * 31536000000
        case 'device':
        	//device type is an array of device Ids
        	if (value instanceof List) return value;
            return [cast(rtData, value, 'string')]
	}
	//anything else...
	return value
}

private utcToLocalDate(dateOrTimeOrString = null) {
	if (dateOrTimeOrString instanceof String) {
		//get UTC time
		dateOrTimeOrString = timeToday(dateOrTimeOrString, location.timeZone).getTime()
	}
	if (dateOrTimeOrString instanceof Date) {
		//get unix time
		dateOrTimeOrString = dateOrTimeOrString.getTime()
	}
	if (!dateOrTimeOrString) {
		dateOrTimeOrString = now()
	}
	if (dateOrTimeOrString instanceof Long) {
		return new Date(dateOrTimeOrString + location.timeZone.getOffset(dateOrTimeOrString))
	}
	return null
}
private localDate() { return utcToLocalDate() }

private utcToLocalTime(dateOrTimeOrString = null) {
	if (dateOrTimeOrString instanceof String) {
		//get UTC time
		dateOrTimeOrString = timeToday(dateOrTimeOrString, location.timeZone).getTime()
	}
	if (dateOrTimeOrString instanceof Date) {
		//get unix time
		dateOrTimeOrString = dateOrTimeOrString.getTime()
	}
	if (!dateOrTimeOrString) {
		dateOrTimeOrString = now()
	}
	if (dateOrTimeOrString instanceof Long) {
		return dateOrTimeOrString + location.timeZone.getOffset(dateOrTimeOrString)
	}
	return null
}
private localTime() { return utcToLocalTime() }

private localToUtcDate(dateOrTime) {
	if (dateOrTime instanceof Date) {
		//get unix time
		dateOrTime = dateOrTime.getTime()
	}
	if (dateOrTime instanceof Long) {
		return new Date(dateOrTime - location.timeZone.getOffset(dateOrTime))
	}
	return null
}

private localToUtcTime(dateOrTime) {
	if (dateOrTime instanceof Date) {
		//get unix time
		dateOrTime = dateOrTime.getTime()
	}
	if (dateOrTime instanceof Long) {
		return dateOrTime - location.timeZone.getOffset(dateOrTime)
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

/******************************************************************************/
/*** DEBUG FUNCTIONS														***/
/******************************************************************************/
private log(message, rtData = null, shift = null, err = null, cmd = null, force = false) {
    if (cmd == "timer") {
    	return [m: message, t: now(), s: shift, e: err]
    }
    if (message instanceof Map) {
    	shift = message.s
        err = message.e
        message = message.m + " (${now() - message.t}ms)"
    }
	if (!force && rtData && rtData.logging && !rtData.logging[cmd] && (cmd != "error")) {
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
    def prefix2 = "║"
	def pad = "" //"░"
	switch (shift) {
		case 0:
			level = 0
		case 1:
			level += 1
			prefix = "╚"
			prefix2 = "╔"
			pad = "═"
			break
		case -1:
        	level -= 1
			//levelDelta = -(level > 0 ? 1 : 0)
			pad = "═"
			prefix = "╔"
			prefix2 = "╚"           
		break
	}

	if (level > 0) {
		prefix = prefix.padLeft(level + (shift == -1 ? 1 : 0), "║")
		prefix2 = prefix2.padLeft(level + (shift == -1 ? 1 : 0), "║")
    }

	//level += levelDelta
	state.debugLevel = level

	if (rtData && (rtData instanceof Map) && (rtData.logs instanceof List)) {
    	message = "$message".toString().replaceAll(/(\r\n|\r|\n|\\r\\n|\\r|\\n)+/, "\r");
    	List msgs = !err ? message.tokenize("\r") : [message]
        for(msg in msgs) {
    		rtData.logs.push([o: now() - rtData.timestamp, p: prefix2, m: msg + (!!err ? " $err" : ""), c: cmd])
        }
    }
	log."$cmd" "$prefix $message", err
}
private info(message, rtData = null, shift = null, err = null) { log message, rtData, shift, err, 'info' }
private trace(message, rtData = null, shift = null, err = null) { log message, rtData, shift, err, 'trace' }
private debug(message, rtData = null, shift = null, err = null) { log message, rtData, shift, err, 'debug' }
private warn(message, rtData = null, shift = null, err = null) { log message, rtData, shift, err, 'warn' }
private error(message, rtData = null, shift = null, err = null) { log message, rtData, shift, err, 'error' }
private timer(message, rtData = null, shift = null, err = null) { log message, rtData, shift, err, 'timer' }

private tracePoint(rtData, objectId, duration, value) {
	if (objectId && rtData && rtData.trace) {
    	rtData.trace.points[objectId] = [o: now() - rtData.trace.t - duration, d: duration, v: value]
    } else {
    	error "Invalid object ID $objectID for trace point...", rtData
    }
}


private static Map weekDays() {
	return [
    	0: "Sunday",
        1: "Monday",
        2: "Tuesday",
        3: "Wednesday",
        4: "Thursday",
        5: "Friday",
        6: "Saturday"
    ]
}

private static Map yearMonths() {
	return [
    	1: "January",
        2: "February",
        3: "March",
        4: "April",
        5: "May",
        6: "June",
        7: "July",
        8: "August",
        9: "September",
        10: "October",
        11: "November",
        12: "December"
    ]
}

private getSunrise() {
	if (!(state.sunrise instanceof Date)) {
		def sunTimes = getSunriseAndSunset()
		state.sunrise = utcToLocalDate(sunTimes.sunrise)
		state.sunset = utcToLocalDate(sunTimes.sunset)
	}
	return state.sunrise
}

private getSunset() {
	if (!(state.sunset instanceof Date)) {
		def sunTimes = getSunriseAndSunset()
		state.sunrise = utcToLocalDate(sunTimes.sunrise)
		state.sunset = utcToLocalDate(sunTimes.sunset)
	}
	return state.sunset
}









private Map getLocalVariables(rtData, vars) {
	Map result = [:]
    def values = atomicState.vars
	for (var in vars) {
    	def variable = [t: var.t, v: var.v ?: cast(rtData, values[var.n], var.t), f: !!var.v] //f means fixed value - we won't save this to the state
        if (rtData && var.v && (var.a == 's')) {
        	variable.v = cast(rtData, evaluateOperand(rtData, null, var.v).v, var.t)
        }
        result[var.n] = variable
    }
    return result    
}


private static Map getSystemVariables() {
	return [
		"\$currentEventAttribute": [t: "string", v: null],
		"\$currentEventDate": [t: "datetime", v: null],
		"\$currentEventDelay": [t: "integer", v: null],
		"\$currentEventDevice": [t: "device", v: null],
		"\$currentEventDeviceIndex": [t: "integer", v: null],
		"\$currentEventDevicePhysical": [t: "boolean", v: null],
		"\$currentEventReceived": [t: "datetime", v: null],
		"\$currentEventValue": [t: "dynamic", v: null],
		"\$currentEventUnit": [t: "string", v: null],
		"\$currentState": [t: "string", v: null],
		"\$currentStateDuration": [t: "string", v: null],
		"\$currentStateSince": [t: "datetime", v: null],
		"\$nextScheduledTime": [t: "datetime", v: null],
		"\$name": [t: "string", d: true],
		"\$state": [t: "string", v: ''],
		"\$now": [t: "datetime", d: true],
        '$device': [t: 'device', v: null],
        '$devices': [t: 'device', v: null],
        '$location': [t: 'device', v: null],
		"\$utc": [t: "datetime", d: true],
		"\$localNow": [t: "datetime", d: true],
		"\$hour": [t: "integer", d: true],
		"\$hour24": [t: "integer", d: true],
		"\$minute": [t: "integer", d: true],
		"\$second": [t: "integer", d: true],
		"\$meridian": [t: "string", d: true],
		"\$meridianWithDots":  [t: "string", d: true],
		"\$day": [t: "integer", d: true],
		"\$dayOfWeek": [t: "integer", d: true],
		"\$dayOfWeekName": [t: "string", d: true],
		"\$month": [t: "integer", d: true],
		"\$monthName": [t: "string", d: true],
		"\$year": [t: "integer", d: true],
		"\$midnight": [t: "datetime", d: true],
		"\$noon": [t: "datetime", d: true],
		"\$sunrise": [t: "datetime", d: true],
		"\$sunset": [t: "datetime", d: true],
		"\$nextMidnight": [t: "datetime", d: true],
		"\$nextNoon": [t: "datetime", d: true],
		"\$nextSunrise": [t: "datetime", d: true],
		"\$nextSunset": [t: "datetime", d: true],
		"\$time": [t: "string", d: true],
		"\$time24": [t: "string", d: true],
		"\$index": [t: "decimal", v: null],
		"\$previousEventAttribute": [t: "string", v: null],
		"\$previousEventDate": [t: "datetime", v: null],
		"\$previousEventDelay": [t: "integer", v: null],
		"\$previousEventDevice": [t: "device", v: null],
		"\$previousEventDeviceIndex": [t: "integer", v: null],
		"\$previousEventDevicePhysical": [t: "boolean", v: null],
		"\$previousEventExecutionTime": [t: "integer", v: null],
		"\$previousEventReceived": [t: "datetime", v: null],
		"\$previousEventValue": [t: "dynamic", v: null],
		"\$previousEventUnit": [t: "string", v: null],
		"\$previousState": [t: "string", v: null],
		"\$previousStateDuration": [t: "string", v: null],
		"\$previousStateSince": [t: "datetime", v: null],
		"\$random": [t: "decimal", d: true],
		"\$randomColor": [t: "string", d: true],
		"\$randomColorName": [t: "string", d: true],
		"\$randomLevel": [t: "integer", d: true],
		"\$randomSaturation": [t: "integer", d: true],
		"\$randomHue": [t: "integer", d: true],
		"\$httpStatusCode": [t: "integer", v: null],
		"\$httpStatusOk": [t: "boolean", v: null],
		"\$iftttStatusCode": [t: "integer", v: null], 
		"\$iftttStatusOk": [t: "boolean", v: null],
		"\$locationMode": [t: "string", d: true],
		"\$shmStatus": [t: "string", d: true]
	]
}

private getSystemVariableValue(rtData, name) {
	switch (name) {
		case "\$name": return app.label
		case "\$now": return (long) now()
		case "\$utc": return (long) now()
		case "\$localNow": return (long) localTime()
		case "\$hour": def h = localDate().hours; return (h == 0 ? 12 : (h > 12 ? h - 12 : h)) 
		case "\$hour24": return localDate().hours
		case "\$minute": return localDate().minutes 
		case "\$second": return localDate().seconds 
		case "\$meridian": def h = localDate().hours; return ( h < 12 ? "AM" : "PM") 
		case "\$meridianWithDots": def h = localDate().hours; return ( h < 12 ? "A.M." : "P.M.") 
		case "\$day": return localDate().date 
		case "\$dayOfWeek": return localDate().day 
		case "\$dayOfWeekName": return weekDays()[localDate().day] 
		case "\$month": return localDate().month + 1 
		case "\$monthName": return yearMonths()[localDate().month + 1] 
		case "\$year": return localDate().year + 1900 
		case "\$midnight": def rightNow = localTime(); return localToUtcTime(rightNow - rightNow.mod(86400000)) 
		case "\$noon": def rightNow = localTime(); return localToUtcTime(rightNow - rightNow.mod(86400000) + 43200000) 
		case "\$sunrise": def sunrise = getSunrise(); def rightNow = localTime(); return localToUtcTime(rightNow - rightNow.mod(86400000) + sunrise.hours * 3600000 + sunrise.minutes * 60000) 
		case "\$sunset": def sunset = getSunset(); def rightNow = localTime(); return localToUtcTime(rightNow - rightNow.mod(86400000) + sunset.hours * 3600000 + sunset.minutes * 60000) 
		case "\$nextMidnight": def rightNow = localTime(); return localToUtcTime(rightNow - rightNow.mod(86400000) + 86400000) 
		case "\$nextNoon": def rightNow = localTime(); if (rightNow - rightNow.mod(86400000) + 43200000 < rightNow) rightNow += 86400000; return localToUtcTime(rightNow - rightNow.mod(86400000) + 43200000) 
		case "\$nextSunrise": def sunrise = getSunrise(); def rightNow = localTime(); if (sunrise.time < rightNow) rightNow += 86400000; return localToUtcTime(rightNow - rightNow.mod(86400000) + sunrise.hours * 3600000 + sunrise.minutes * 60000)
		case "\$nextSunset": def sunset = getSunset(); def rightNow = localTime(); if (sunset.time < rightNow) rightNow += 86400000; return localToUtcTime(rightNow - rightNow.mod(86400000) + sunset.hours * 3600000 + sunset.minutes * 60000)
		case "\$time": def t = localDate(); def h = t.hours; def m = t.minutes; return (h == 0 ? 12 : (h > 12 ? h - 12 : h)) + ":" + (m < 10 ? "0$m" : "$m") + " " + (h <12 ? "A.M." : "P.M.") 
		case "\$time24": def t = localDate(); def h = t.hours; def m = t.minutes; return h + ":" + (m < 10 ? "0$m" : "$m") 
		case "\$random": def result = getRandomValue("\$random") ?: (float)Math.random(); setRandomValue("\$random", result); return result 
		case "\$randomColor": def result = getRandomValue("\$randomColor") ?: colorUtil.RANDOM.rgb; setRandomValue("\$randomColor", result); return result 
		case "\$randomColorName": def result = getRandomValue("\$randomColorName") ?: colorUtil.RANDOM.name; setRandomValue("\$randomColorName", result); return result 
		case "\$randomLevel": def result = getRandomValue("\$randomLevel") ?: (int)Math.round(100 * Math.random()); setRandomValue("\$randomLevel", result); return result 
		case "\$randomSaturation": def result = getRandomValue("\$randomSaturation") ?: (int)Math.round(50 + 50 * Math.random()); setRandomValue("\$randomSaturation", result); return result 
		case "\$randomHue": def result = getRandomValue("\$randomHue") ?: (int)Math.round(360 * Math.random()); setRandomValue("\$randomHue", result); return result 
  		case "\$locationMode": return location.getMode()
		case "\$shmStatus": return rtData.virtualDevices['alarmSystemStatus']?.o[location.currentState("alarmSystemStatus")?.value]
    }
}

private setSystemVariableValue(rtData, name, value) {
	if (!name || !(name.startsWith('$'))) return
    def var = rtData.systemVars[name]
    if (!var || var.d) return
   	rtData.systemVars[name].v = value
}

private getRandomValue(name) {
	state.temp = state.temp ?: [:]
	state.temp.randoms = state.temp.randoms ?: [:]
	return state.temp?.randoms[name]
}

private void setRandomValue(name, value) {
	state.temp = state.temp ?: [:]
	state.temp.randoms = state.temp.randoms ?: [:]
	state.temp.randoms[name] = value
}

private void resetRandomValues() {
	state.temp = state.temp ?: [:]
	state.temp.randoms = [:]
}
