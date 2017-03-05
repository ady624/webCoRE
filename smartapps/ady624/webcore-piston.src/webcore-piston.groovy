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

def version() {	return "v0.0.022.20170305" }
/*
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
	page(name: "pageMain")
}

/******************************************************************************/
/*** 																		***/
/*** CONFIGURATION PAGES													***/
/*** 																		***/
/******************************************************************************/
def pageMain() {
	//webCoRE Piston main page
	return dynamicPage(name: "pageMain", title: "", install: true, uninstall: false) {
		def currentState = state.currentState
        
        section ("General") {
			label name: "name", title: "Name", required: true, state: (name ? "complete" : null), defaultValue: parent.generatePistonName()
			input "description", "string", title: "Description", required: false, state: (description ? "complete" : null), capitalization: "sentences"
        }
        
		section(title:"Application Info") {
			paragraph version(), title: "Version"
			paragraph mem(), title: "Memory Usage"
			href "pageVariables", title: "Local Variables"
		}

		section("Debugging") {
			input "debugging", "bool", title: "Enable debugging", defaultValue: false, submitOnChange: true, required: false
			def debugging = settings.debugging
			if (debugging) {
				input "log#info", "bool", title: "Log info messages", defaultValue: true, required: false
				input "log#trace", "bool", title: "Log trace messages", defaultValue: true, required: false
				input "log#debug", "bool", title: "Log debug messages", defaultValue: false, required: false
				input "log#warn", "bool", title: "Log warning messages", defaultValue: true, required: false
				input "log#error", "bool", title: "Log error messages", defaultValue: true, required: false
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
    	subscribeAll()
    }
}

def get() {
	def piston = state.piston ?: [:]
    piston.id = hashId(app.id);
    piston.author = state.author;
    piston.name = app.label ?: app.name
    piston.created = state.created
    piston.modified = state.modified
    piston.build = state.build
    piston.bin = state.bin
    piston.active = state.active
    piston.vars = state.vars
    piston.stats = state.stats
    return piston
}

def set(piston) {
	state.modified = now()
    state.build = (int)(state.build ? (int)state.build + 1 : 1)
    state.piston = piston ?[
    	r: piston.r ?: [],
        rn: !!piston.rn,
        ro: piston.ro ?: 'and',
    	s: piston.s ?: [],
    ] : [:]
    state.vars = piston ? (piston.v ?: [:]) : [:]
    if ((state.build == 1) || (!!state.active)) {
    	resume()
    }
    return [active: state.active, build: state.build, modified: state.modified]
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
	def msg = timer "Piston pause complete", -1
    trace "Pausing piston", 0
	state.active = false;
    unsubscribe()
    trace msg
}

def resume() {
	def msg = timer "Piston resume complete", -1
    trace "Resuming piston", 0
	state.active = true;
    //we need to subscribe
    trace "Initializing piston..."
    subscribeAll()
    trace msg
}

def execute() {

}

private getRunTimeData(rtData = null) {
	rtData = rtData ?: parent.getRunTimeData()
    rtData.piston = state.piston
    rtData.localVars = state.vars ?: [:]
    rtData.systemVars = getSystemVars()
    rtData.stats = [:]
    return rtData
}

/******************************************************************************/
/*** 																		***/
/*** EVENT HANDLING															***/
/*** 																		***/
/******************************************************************************/
def handler(event) {
	def startTime = now()
    def eventDelay = startTime - event.date.getTime()
	def msg = timer "Event processed successfuly", -1
    trace "Received event ${event.name} from device ${event.device} with value ${event.value} with a delay of ${eventDelay}ms", 0
    state.temp = [:]
    //todo start execution
	def msg2 = timer "Load stage complete."
    Map rtData = getRunTimeData()
    trace msg2
    rtData.stats.timing = [
    	t: startTime,
    	d: eventDelay,
        l: now() - startTime
    ]
    startTime = now()
	msg2 = timer "Execution stage complete.", -1
    trace "Execution stage started", 1
    handleEvent(rtData, event)
	rtData.stats.timing.e = now() - startTime
    trace msg2
    trace msg
	startTime = now()
	parent.updateRunTimeData(rtData)
    rtData.stats.timing.u = now() - startTime
    def stats = state.stats ?: [:]
    stats.timing = stats.timing ?: []
    stats.timing.push(rtData.stats.timing)
    if (stats.timing.size() > 500) {
    	stats.timing.remove(stats.timing[0]);
    }
    state.stats = stats
}

private handleEvent(rtData, event) {
	rtData = rtData ?: getRunTimeData()
	//todo - check restrictions

	evaluateStatements(rtData, rtData.piston.s)

}

private Boolean evaluateStatements(rtData, statements) {
	for(statement in statements) {
    	if (!evaluateStatement(rtData, statement)) {
        	//stop processing
        	return false
        }
    }
    //continue processing
    return true
}

private Boolean evaluateStatement(rtData, statement) {
	if (!statement) return false
    def value = true
	switch (statement.t) {
    	case 'if':
        case 'while':
    		//check conditions for if and while
        	def perform = evaluateConditions(rtData, statement)
        	if (perform) {
	        	if (!evaluateStatements(rtData, statement.s)) {
	            	//stop processing
	                return false
	            }
	            return true
	        } else {
	        	if (statement.t == 'if') {
	            	//look for else-ifs
	                for (elseIf in statement.ei) {
	                    perform = evaluateConditions(rtData, elseIf)
	                    if (perform) {
	                        if (!evaluateStatements(rtData, elseIf.s)) {
	                            //stop processing
	                            return false
	                        }
	                        return true
	                    }                	
	                }
	                if (!evaluateStatements(rtData, statement.e)) {
	                	//stop processing
	                	return false
	                }
	            }
    	    }
			break
		case 'action':
        	def result = executeAction(rtData, statement)
            if ((statement.a != '1') && !result) {
            	//non-async action requests thread termination
            	return false
            }
        	break
    }
}


private Boolean executeAction(rtData, statement) {
	def devices = []
    for (d in statement.d) {
    	def device = getDevice(rtData, d)
        if (device) {
        	devices.push(device)
        }
    }
    if (devices.size()) {
        for (task in statement.k) {
        	def result = executeTask(rtData, devices, task)
        }
    }
    return true
}

private Boolean executeTask(rtData, devices, task) {
	//def cmd = rtData.commands.physical[task.c]
    //parse parameters
    def params = []
    for (param in task.p) {
		params.push evaluateExpression(rtData, param)
    }
 	def vcmd = rtData.commands.virtual[task.c]
    for (device in devices) {
        if (device.hasCommand(task.c)) {
        	def msg = timer "Executed [$device].${task.c}"
        	try {
            	"cmd_${task.c}"(device, params)
            } catch(all) {
	            device."${task.c}"(params*.v as Object[])
			}
            trace msg
        } else {
            if (vcmd) {
	        	def result = executeVirtualCommand(rtData, vcmd.a ? devices : device, task, params)
                if (!result || vcmd.a) {
                	return result
                }
            }
        }
    }
    
    return true
}

private void cmd_setLevel(device, params) {
	def level = params[0].v
    def state = params.size() > 1 ? params[1].v : ""
    def delay = params.size() > 2 ? params[2].v : 0
    if (state.size() && (device.currentValue('switch') != state)) {
        return
    }
    device.setLevel(level, [delay: delay ?: 0])
}

private Boolean executeVirtualCommand(rtData, devices, task, params) {
   	def msg = timer "Executed virtual command ${devices instanceof List ? "$devices" : "[$devices]"}.${task.c}"
    try {
	    long delay = "vcmd_${task.c}"(devices, params)
        if (delay > 5000) {
        	//schedule a wake up
	        trace msg
        	return false
        } else {
        	pause(delay)
        }
    } catch(all) {
    	error all
    }
    trace msg
    return true
}

private long vcmd_wait(device, params) {
	return params[0].v
}

private long vcmd_waitRandom(device, params) {
	def min = params[0].v
    def max = params[1].v
    if (max < min) {
    	def v = max
        max = min
        min = v
    }
	return min + (int)Math.round((max - min) * Math.random())
}

private Boolean evaluateConditions(rtData, conditions) {
	def grouping = conditions.o
    def not = !!conditions.n
    def value = (grouping == 'or' ? false : true)
	for(condition in conditions.c) {
    	def res = evaluateCondition(rtData, condition)
        value = (grouping == 'or') ? value || res : value && res
        if (value == (grouping == 'or') ? true : false) return (not ? !value : !!value)
    }
    return (not ? !value : !!value)
}

private Boolean evaluateCondition(rtData, condition) {
    def not = !!condition.n
    def comparison = rtData.comparisons.conditions[condition.co] ?: rtData.comparisons.triggers[condition.co]
    if (comparison) {
        def paramCount = comparison.p ?: 0
        def lo = null
        def ro = null
        def ro2 = null
        for(int i = 0; i <= paramCount; i++) {
            def operand = (i == 0 ? condition.lo : (i == 1 ? condition.ro : condition.ro2))
            //parse the operand
            def values
            switch (operand.t) {
            	case "p": //physical device
                	values = []
                    for(deviceId in operand.d) {
                    	values.push(getDeviceAttribute(rtData, deviceId, operand.a))
                    }
                    if (!(operand.g in ['any', 'all'])) {
                    	try {
                        	values = ["func_${operand.g}"(rtData, values)]
                        } catch(all) {
                        }
                    }
                	break;
            	case "x": //constant
                	values = [getVariable(rtData, operand.x)]
            	case "c": //constant
            	case "e": //expression
                	values = [evaluateExpression(rtData, operand.exp)]
            }
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
        def result = evaluateComparison(rtData, condition.co, lo, ro, ro2, options)
        return not ? !result : !!result
    }    
    return false
}

private Boolean evaluateComparison(rtData, comparison, lo, ro = null, ro2 = null, options = null) {
        def fn = "comp_${comparison}"
        def result = (lo.operand.g == 'any' ? false : true)
        if (options?.matches) {
        	options.devices = [matched: [], unmatched: []]
        }
        //if multiple left values, go through each
        for(value in lo.values) {
        	def res
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
                error "Error calling comparison $fn: $all"
                res = false
            }            
            result = (lo.operand.g == 'any' ? result || res : result && res)
            if (options?.matches && value.d) {
            	if (res) {
                	options.devices.matched.push(value.d)
                } else {
                	options.devices.unmatched.push(value.d)
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


//comparison low level functions
private boolean comp_is								(rtData, lv, rv = null, rv2 = null) { return cast(lv.v, 'string') == cast(rv.v, 'string') }
private boolean comp_is_not							(rtData, lv, rv = null, rv2 = null) { return cast(lv.v, 'string') != cast(rv.v, 'string') }
private boolean comp_is_equal_to					(rtData, lv, rv = null, rv2 = null) { return cast(lv.v, 'decimal') == cast(rv.v, 'decimal') }
private boolean comp_is_not_equal_to				(rtData, lv, rv = null, rv2 = null) { return cast(lv.v, 'decimal') != cast(rv.v, 'decimal') }
private boolean comp_is_different_than				(rtData, lv, rv = null, rv2 = null) { return cast(lv.v, 'decimal') != cast(rv.v, 'decimal') }
private boolean comp_is_less_than					(rtData, lv, rv = null, rv2 = null) { return cast(lv.v, 'decimal') < cast(rv.v, 'decimal') }
private boolean comp_is_less_than_or_equal_to		(rtData, lv, rv = null, rv2 = null) { return cast(lv.v, 'decimal') <= cast(rv.v, 'decimal') }
private boolean comp_is_greater_than				(rtData, lv, rv = null, rv2 = null) { return cast(lv.v, 'decimal') > cast(rv.v, 'decimal') }
private boolean comp_is_greater_than_or_equal_to	(rtData, lv, rv = null, rv2 = null) { return cast(lv.v, 'decimal') >= cast(rv.v, 'decimal') }
private boolean comp_is_even						(rtData, lv, rv = null, rv2 = null) { return cast(lv.v, 'integer').mod(2) == 0 }
private boolean comp_is_odd							(rtData, lv, rv = null, rv2 = null) { return cast(lv.v, 'integer').mod(2) != 0 }
private boolean comp_is_true						(rtData, lv, rv = null, rv2 = null) { return !!cast(lv.v, 'boolean') }
private boolean comp_is_false						(rtData, lv, rv = null, rv2 = null) { return !cast(lv.v, 'boolean') }
private boolean comp_is_inside_range				(rtData, lv, rv = null, rv2 = null) { def v = cast(lv.v, 'decimal'); def v1 = cast(rv.v, 'decimal'); def v2 = cast(rv2.v, 'decimal'); return (v1 < v2) ? ((v >= v1) && (v <= v2)) : ((v >= v2) && (v <= v1)); }
private boolean comp_is_outside_range				(rtData, lv, rv = null, rv2 = null) { return !comp_is_inside_range(rtData, lv, rv, rv2) }

private boolean comp_changed(rtData, lv, rv = null, rv2 = null) {
	return true
}


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
    //got a statement, pass it on to the closure
    if (closure instanceof Closure) {
    	closure(node, parentNode)
    }
    //if the statements has substatements, go through them
    if (node.c instanceof List) {
    	traverseConditions(node.c, closure, node)
    }
}

private traverseExpressions(node, closure, parentNode = null) {
    if (!node) return
	//if a statements element, go through each item
	if (node instanceof List) {
    	for(item in node) {
	    	traverseExpressions(item, closure, parentNode)
	    }
        return
	}
    //got a statement, pass it on to the closure
    if (closure instanceof Closure) {
    	closure(node, parentNode)
    }
    //if the statements has substatements, go through them
    if (node.i instanceof List) {
    	traverseExpressions(node.i, closure, node)
    }
}

private void subscribeAll() {
	def msg = timer "Finished subscribing", -1
    trace "Subscribing to devices...", 1
	unsubscribe()
	def rtData = getRunTimeData()
    Map subscriptions = [:]
    def count = 0
    def hasTriggers = false
    //traverse all statements
    traverseStatements(rtData.piston.s, { node, parentNode ->
        if (node.t in ['if', 'switch', 'while', 'repeat']) {
        	traverseConditions(node.c?:[] + node.ei?:[], { condition, parentCondition ->
                def comparison = rtData.comparisons.conditions[condition.co]
                def comparisonType = 'condition'
                if (!comparison) {
                    hasTriggers = true
                	comparisonType = 'trigger'
                	comparison = rtData.comparisons.triggers[condition.co]                	
                }
                if (comparison) {
	                def paramCount = comparison.p ?: 0
                    for(int i = 0; i <= paramCount; i++) {
                    	//get the operand to parse
                    	def operand = (i == 0 ? condition.lo : (i == 1 ? condition.ro : condition.ro2))
                        switch (operand.t) {
                        	case "p": //physical device
                            	for(device in operand.d) {
                                	subscriptions["$device${operand.a}"] = [d: device, a: operand.a, t: comparisonType, c: condition]
                                }
                                break;
							case "c": //constant
                            case "e": //expression
                            	traverseExpressions(operand.exp?.i, { expression, parentExpression -> 
                                	if ((expression.t == 'device') && (expression.id)) {
	                                	subscriptions["${expression.id}${expression.a}"] = [d: expression.id, a: expression.a, t: comparisonType, c: condition]
                                    }
                                })
                                break
                        }
                    }
                }
            })
        }
    })
    //trace subscriptions
    for (subscription in subscriptions) {
    	subscription.value.c.s = false
    	if ((subscription.value.t == "trigger") || (subscription.value.c.sm == "always") || (!hasTriggers && (subscription.value.c.sm != "never"))) {
	    	def device = getDevice(rtData, subscription.value.d)
    	    if (device) {
        		trace "Subscribing to $device.${subscription.value.a}..."
		    	subscription.value.c.s = true
        		subscribe(device, subscription.value.a, handler)
			}
        }
    }
    trace msg
}


private sanitizeVariableName(name) {
	name = name ? "$name".trim().replace(" ", "_") : null
}

private getDevice(rtData, idOrName) {
	def device = rtData.devices[idOrName] ?: rtData.devices.find{ it.value.name == idOrName }
    return device    
}

private Map getDeviceAttribute(rtData, deviceId, attributeName) {
	def device = getDevice(rtData, deviceId)
    if (device) {
        def attribute = rtData.attributes[attributeName]
        if (attribute) {
            return [t: attribute.t, v: device.currentValue(attributeName), d: deviceId, a: attributeName]
        } else {
            return [t: "error", v: "Attribute '${attributeName}' not found"]
        }
    }
    return [t: "error", v: "Device '${deviceId}' not found"]
}

def getVariable(rtData, name) {
	name = sanitizeVariableName(name)
	if (!name) return [t: "error", v: "Invalid empty variable name"]
	if (name.startsWith("@")) {
    	def result = rtData.globalVars[name]
        if (!(result instanceof Map)) result = [t: "error", v: "Variable '$name' not found"]
		return result
	} else {
		if (name.startsWith("\$")) {
			def result = rtData.systemVars[name]
            if (!(result instanceof Map)) result = [t: "error", v: "Variable '$name' not found"]
            if (result && result.v instanceof Closure) {
            	return [t: result.t, v: result.v()]
            }
            return result
		} else {
			def result = rtData.localVars[name]
            if (!(result instanceof Map)) result = [t: "error", v: "Variable '$name' not found"]
            return result
		}
	}
}


/******************************************************************************/
/*** 																		***/
/*** EXPRESSION FUNCTIONS													***/
/*** 																		***/
/******************************************************************************/

def proxyEvaluateExpression(rtData, expression, dataType = null) {
	resetRandomValues()
	return evaluateExpression(getRunTimeData(rtData), expression, dataType)
}
private evaluateExpression(rtData, expression, dataType = null) {
    //if dealing with an expression that has multiple items, let's evaluate each item one by one
    //let's evaluate this expression
    def time = now()
    Map result = [:]
    switch (expression.t) {
        case "string":
        case "integer":
        case "decimal":
        case "boolean":
        case "time":
        	result = [t: expression.t, v: cast(expression.v, expression.t)]
        	break
        case "bool":
        	result = [t: "boolean", v: cast(expression.v, "boolean")]
        	break
        case "number":
        case "float":
        	result = [t: "decimal", v: cast(expression.v, "decimal")]
        	break
        case "enum":
        case "error":
        case "text":
        	result = [t: "string", v: cast(expression.v, "string")]
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
        	result = [t: "long", v: cast(expression.v * multiplier, "long")]
        	break
        case "variable":
        	//get variable as {n: name, t: type, v: value}
        	result = getVariable(rtData, expression.x)
        	break
        case "device":
        	//get variable as {n: name, t: type, v: value}
            def deviceId = expression.id ?: getVariable(rtData, expression.x)?.v
            result = getDeviceAttribute(rtData, deviceId, expression.a)
        	break
        case "operand":
        	result = [t: "string", v: cast(expression.v, "string")]
        	break
        case "function":
            def fn = "func_${expression.n}"
            try {
				result = "$fn"(rtData, expression.i)
			} catch (all) {
				//log error
                result = [t: "error", v: all]
			}        	
        	break
        case "expression":
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
	                items.push(evaluateExpression(rtData, item))
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
                def v2 = items[idx + 1].t
                def t = t1
                //fix-ups
                //integer with decimal gives decimal, also *, /, and ^ require decimals
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
                items[idx + 1].v = cast(v, t)
                def sz = items.size()
                items.remove(idx)
            }
    	    result = [t:items[0].t, v: items[0].v]            
	        break
    }
    //return the value, either directly or via cast, if certain data type is requested
    return result.t == "error" ? [t: "string", v: "[ERROR: ${result.v}]", d: now() - time] : [t: dataType ?: result.t, v: cast(result.v, dataType?: result.t), d: now() - time]
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
    boolean fahrenheit = cast(params.size() > 2 ? evaluateExpression(rtData, params[2]).v : location.temperatureScale, "string").toUpperCase() == "F"
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
    return [t: "integer", v: cast(Math.floor(evaluateExpression(rtData, params[0], 'decimal').v), 'integer')]
}

/******************************************************************************/
/*** ceiling converts a decimal value to it's closest higher integer value	***/
/*** Usage: ceiling(decimal or string)										***/
/******************************************************************************/
private func_ceiling(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting ceiling(decimal or string)"];
    }
    return [t: "integer", v: cast(Math.ceil(evaluateExpression(rtData, params[0], 'decimal').v), 'integer')]
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
    	return [t: "error", v: "Invalid parameters. Expecting avg(values)"];
    }
    float sum = 0
    for (param in params) {
    	sum += evaluateExpression(rtData, param, 'decimal').v
    }
    return [t: "decimal", v: sum / params.size()]
}

/******************************************************************************/
/*** least returns the value that is least found a series of numeric values	***/
/*** Usage: least(values)													***/
/******************************************************************************/
private func_least(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting least(values)"];
    }
    Map data = [:]
    for (param in params) {
    	def value = evaluateExpression(rtData, param)
    	data[value.v] = [t: value.t, v: value.v, c: (data[value.v]?.c ?: 0) + 1]
    }
    def value = data.sort{ it.c }[0]
    return [t: value.t, v: value.v]
}

/******************************************************************************/
/*** most returns the value that is most found a series of numeric values	***/
/*** Usage: most(values)													***/
/******************************************************************************/
private func_most(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting most(values)"];
    }
    Map data = [:]
    for (param in params) {
    	def value = evaluateExpression(rtData, param)
    	data[value.v] = [t: value.t, v: value.v, c: (data[value.v]?.c ?: 0) + 1]
    }
    def value = data.sort{ - it.c }[0]
    return [t: value.t, v: value.v]
}

/******************************************************************************/
/*** sum calculates the sum of a series of numeric values					***/
/*** Usage: sum(values)														***/
/******************************************************************************/
private func_sum(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting sum(values)"];
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
    	return [t: "error", v: "Invalid parameters. Expecting variance(value1, [..], valueN)"];
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
    	return [t: "error", v: "Invalid parameters. Expecting stdev(value1, [..], valueN)"];
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
    	return [t: "error", v: "Invalid parameters. Expecting min(values)"];
    }
    def min = null
    for (param in params) {
    	float value = evaluateExpression(rtData, param, 'decimal').v
        min = (min == null) ? value : ((min > value) ? value : min)
    }
    return [t: "decimal", v: min]
}

/******************************************************************************/
/*** max calculates the maximum of a series of numeric values				***/
/*** Usage: max(values)														***/
/******************************************************************************/
private func_max(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting max(values)"];
    }
    def max = null
    for (param in params) {
    	float value = evaluateExpression(rtData, param, 'decimal').v
        max = (max == null) ? value : ((max < value) ? value : max)
    }
    return [t: "decimal", v: max]
}


/******************************************************************************/
/*** count calculates the number of true/non-zero/non-empty items in a series of numeric values		***/
/*** Usage: count(values)														***/
/******************************************************************************/
private func_count(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting count(values)"];
    }
    def count = 0
    for (param in params) {
    	count += evaluateExpression(rtData, param, 'boolean').v ? 1 : 0
    }
    return [t: "integer", v: count]
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

private cast(value, dataType) {
	def trueStrings = ["1", "on", "open", "locked", "active", "wet", "detected", "present", "occupied", "muted", "sleeping"]
	def falseStrings = ["0", "false", "off", "closed", "unlocked", "inactive", "dry", "clear", "not detected", "not present", "not occupied", "unmuted", "not sleeping"]
    if (value instanceof GString) {
    	value = value.toString()
    }
	switch (dataType) {
		case "string":
		case "text":
			if (value instanceof Boolean) {
				return value ? "true" : "false"
			}										
			if ((value instanceof Long) && (value > 9999999999)) {
				return formatLocalTime(value)
			}
			return "$value"
		case "integer":
		case "int32":
		case "number":
			if (value == null) return (int) 0
			if (value instanceof String) {
				if (value.isInteger())
					return value.toInteger()
				if (value.isFloat())
					return (int) Math.floor(value.toFloat())
				if (value in trueStrings)
					return (int) 1
			}
			if (value instanceof Boolean) {
            	return (int) (value ? 1 : 0)
            }
			def result = (int) 0
			try {
				result = (int) value
			} catch(all) {
				result = (int) 0
			}
			return result ? result : (int) 0
		case "int64":
		case "long":
			if (value == null) return (long) 0
			if (value instanceof String) {
				if (value.isInteger())
					return (long) value.toInteger()
				if (value.isFloat())
					return (long) Math.round(value.toFloat())
				if (value in trueStrings)
					return (long) 1
			}
			if (value instanceof Boolean) {
            	return (long) (value ? 1 : 0)
            }
			def result = (long) 0
			try {
				result = (long) value
			} catch(all) {
			}
			return result ? result : (long) 0
		case "float":
		case "decimal":
			if (value == null) return (float) 0
			if (value instanceof String) {
				if (value.isFloat())
					return (float) value.toFloat()
				if (value.isInteger())
					return (float) value.toInteger()
				if (value in trueStrings)
					return (float) 1
			}
			if (value instanceof Boolean) {
            	return (float) (value ? 1 : 0)
            }
			def result = (float) 0
			try {
				result = (float) value
			} catch(all) {
			}
			return result ? result : (float) 0
		case "boolean":
        	if (value instanceof String) {
				if (!value || (value.toLowerCase().trim() in falseStrings))
					return false
				return true
			}
			return !!value
		case "time":
			return value instanceof String ? utcToLocalDate(value).time : cast(value, "long")
		case "vector3":
			return value instanceof String ? utcToLocalDate(value).time : cast(value, "long")
		case "orientation":
			return getThreeAxisOrientation(value)
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
private debug(message, shift = null, err = null, cmd = null) {
    if (cmd == "timer") {
    	return [m: message, t: now(), s: shift, e: err]
    }
    if (message instanceof Map) {
    	shift = message.s
        err = message.e
        message = message.m + " (done in ${now() - message.t}ms)"
    }
	def debugging = settings.debugging
	if (!debugging && (cmd != "error")) {
		//return
	}
	cmd = cmd ? cmd : "debug"
	if (!settings["log#$cmd"]) {
		//return
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
	def pad = "" //"░"
	switch (shift) {
		case 0:
			level = 0
		case 1:
			level += 1
			prefix = "╚"
			pad = "═"
			break
		case -1:
        	level -= 1
			//levelDelta = -(level > 0 ? 1 : 0)
			pad = "═"
			prefix = "╔"
		break
	}

	if (level > 0) {
		prefix = prefix.padLeft(level + (shift == -1 ? 1 : 0), "║") //+ pad //padRight(maxLevel, pad)
    }

	//level += levelDelta
	state.debugLevel = level

	log."$cmd" "$prefix $message", err
}
private info(message, shift = null, err = null) { debug message, shift, err, 'info' }
private trace(message, shift = null, err = null) { debug message, shift, err, 'trace' }
private warn(message, shift = null, err = null) { debug message, shift, err, 'warn' }
private error(message, shift = null, err = null) { debug message, shift, err, 'error' }
private timer(message, shift = null, err = null) { debug message, shift, err, 'timer' }



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












private Map getSystemVars() {
	return [
		"\$currentEventAttribute": [t: "string", v: null],
		"\$currentEventDate": [t: "time", v: null],
		"\$currentEventDelay": [t: "integer", v: null],
		"\$currentEventDevice": [t: "device", v: null],
		"\$currentEventDeviceIndex": [t: "integer", v: null],
		"\$currentEventDevicePhysical": [t: "boolean", v: null],
		"\$currentEventReceived": [t: "time", v: null],
		"\$currentEventValue": [t: "string", v: null],
		"\$currentState": [t: "string", v: null],
		"\$currentStateDuration": [t: "string", v: null],
		"\$currentStateSince": [t: "time", v: null],
		"\$nextScheduledTime": [t: "time", v: null],
		"\$now": [t: "time", v: {(long) now()}],
		"\$utc": [t: "time", v: {(long) now()}],
		"\$localNow": [t: "time", v: {(long) localTime()}],
		"\$hour": [t: "integer", v: { def h = localDate().hours; return (h == 0 ? 12 : (h > 12 ? h - 12 : h)) }],
		"\$hour24": [t: "integer", v: { localDate.hours}],
		"\$minute": [t: "integer", v: { localDate().minutes }],
		"\$second": [t: "integer", v: { localDate().seconds }],
		"\$meridian": [t: "string", v: { def h = localDate().hours; return ( h < 12 ? "AM" : "PM") }],
		"\$meridianWithDots":  [t: "string", v: { def h = localDate().hours; return ( h < 12 ? "A.M." : "P.M.") }],
		"\$day": [t: "integer", v: { localDate().date }],
		"\$dayOfWeek": [t: "integer", v: { localDate().day }],
		"\$dayOfWeekName": [t: "string", v: { weekDays()[localDate().day] }],
		"\$month": [t: "integer", v: { localDate().month + 1 }],
		"\$monthName": [t: "string", v: { yearMonths()[localDate().month + 1] }],
		"\$index": [t: "integer", v: null],
		"\$year": [t: "integer", v: { localDate().year + 1900 }],
		"\$previousEventAttribute": [t: "string", v: null],
		"\$previousEventDate": [t: "time", v: null],
		"\$previousEventDelay": [t: "integer", v: null],
		"\$previousEventDevice": [t: "device", v: null],
		"\$previousEventDeviceIndex": [t: "integer", v: null],
		"\$previousEventDevicePhysical": [t: "boolean", v: null],
		"\$previousEventExecutionTime": 0,
		"\$previousEventReceived": [t: "time", v: null],
		"\$previousEventValue": [t: "string", v: null],
		"\$previousState": [t: "string", v: null],
		"\$previousStateDuration": [t: "string", v: null],
		"\$previousStateSince": [t: "time", v: null],
		"\$random": [t: "decimal", v: { def result = getRandomValue("\$random") ?: (float)Math.random(); setRandomValue("\$random", result); return result }],
		"\$randomColor": [t: "string", v: { def result = getRandomValue("\$randomColor") ?: colorUtil.RANDOM.rgb; setRandomValue("\$randomColor", result); return result }],
		"\$randomColorName": [t: "string", v: { def result = getRandomValue("\$randomColorName") ?: colorUtil.RANDOM.name; setRandomValue("\$randomColorName", result); return result }],
		"\$randomLevel": [t: "integer", v: { def result = getRandomValue("\$randomLevel") ?: (int)Math.round(100 * Math.random()); setRandomValue("\$randomLevel", result); return result }],
		"\$randomSaturation": [t: "integer", v: { def result = getRandomValue("\$randomSaturation") ?: (int)Math.round(50 + 50 * Math.random()); setRandomValue("\$randomSaturation", result); return result }],
		"\$randomHue": [t: "integer", v: { def result = getRandomValue("\$randomHue") ?: (int)Math.round(360 * Math.random()); setRandomValue("\$randomHue", result); return result }],
		"\$midnight": [t: "time", v: { def rightNow = localTime(); return localToUtcTime(rightNow - rightNow.mod(86400000)) }],
		"\$noon": [t: "time", v: { def rightNow = localTime(); return localToUtcTime(rightNow - rightNow.mod(86400000) + 43200000) }],
		"\$sunrise": [t: "time", v: { def sunrise = getSunrise(); def rightNow = localTime(); return localToUtcTime(rightNow - rightNow.mod(86400000) + sunrise.hours * 3600000 + sunrise.minutes * 60000) }],
		"\$sunset": [t: "time", v: { def sunset = getSunset(); def rightNow = localTime(); return localToUtcTime(rightNow - rightNow.mod(86400000) + sunset.hours * 3600000 + sunset.minutes * 60000) }],
		"\$nextMidnight": [t: "time", v: { def rightNow = localTime(); return localToUtcTime(rightNow - rightNow.mod(86400000) + 86400000) }],
		"\$nextNoon": [t: "time", v: { def rightNow = localTime(); if (rightNow - rightNow.mod(86400000) + 43200000 < rightNow) rightNow += 86400000; return localToUtcTime(rightNow - rightNow.mod(86400000) + 43200000) }],
		"\$nextSunrise": [t: "time", v: { def sunrise = getSunrise(); def rightNow = localTime(); if (sunrise.time < rightNow) rightNow += 86400000; return localToUtcTime(rightNow - rightNow.mod(86400000) + sunrise.hours * 3600000 + sunrise.minutes * 60000)}],
		"\$nextSunset": [t: "time", v: { def sunset = getSunset(); def rightNow = localTime(); if (sunset.time < rightNow) rightNow += 86400000; return localToUtcTime(rightNow - rightNow.mod(86400000) + sunset.hours * 3600000 + sunset.minutes * 60000)}],
		"\$time": [t: "string", v: { def t = localDate(); def h = t.hours; def m = t.minutes; return (h == 0 ? 12 : (h > 12 ? h - 12 : h)) + ":" + (m < 10 ? "0$m" : "$m") + " " + (h <12 ? "A.M." : "P.M.") }],
		"\$time24": [t: "string", v: { def t = localDate(); def h = t.hours; def m = t.minutes; return h + ":" + (m < 10 ? "0$m" : "$m") }],
		"\$httpStatusCode": [t: "integer", v: null],
		"\$httpStatusOk": [t: "boolean", v: null],
		"\$iftttStatusCode": [t: "integer", v: null], 
		"\$iftttStatusOk": [t: "boolean", v: null],
		"\$locationMode": [t: "string", v: { return location.mode }],
		"\$shmStatus": [t: "string", v: { return location.mode }]
	]
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
