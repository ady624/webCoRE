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

def version() {	return "v0.0.020.20170228" }
/*
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

/******************************************************************************/
/*** COMMON PAGES															***/
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
	//def device = parent.getDevice("owekf34r24r324");
    //subscribe(device, "switch", handler)
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
    piston.v = state.vars
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
    if (state.build == 1) {
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
	state.active = false;
}

def resume() {
	state.active = true;
}

def execute() {

}

private getRunTimeData(rtData = null) {
	rtData = rtData ?: parent.getRunTimeData()
    rtData.localVars = state.vars ?: [:]
    rtData.systemVars = getSystemVars()
    return rtData
}


private sanitizeVariableName(name) {
	name = name ? "$name".trim().replace(" ", "_") : null
}

def getDevice(rtData, idOrName) {
	def device = rtData.devices[idOrName] ?: rtData.devices.find{ it.value.name == idOrName }
    return device    
}

def getVariable(rtData, name) {
	name = sanitizeVariableName(name)
	if (!name) return [t: "error", v: "Invalid empty variable name"]
    /*
    if (name.startsWith("\$"))
		switch (name) {
			case "\$now": return now()
			case "\$hour24": return adjustTime().hours
			case "\$hour":
				def h = adjustTime().hours
				return (h == 0 ? 12 : (h > 12 ? h - 12 : h))
			case "\$meridian":
				def h = adjustTime().hours
				return ( h < 12 ? "AM" : "PM")
			case "\$meridianWithDots":
				def h = adjustTime().hours
				return ( h <12 ? "A.M." : "P.M.")
			case "\$minute": return adjustTime().minutes
			case "\$second": return adjustTime().seconds
			case "\$time":
				def t = adjustTime()
				def h = t.hours
				def m = t.minutes
				return (h == 0 ? 12 : (h > 12 ? h - 12 : h)) + ":" + (m < 10 ? "0$m" : "$m") + " " + (h <12 ? "A.M." : "P.M.")
			case "\$time24":
				def t = adjustTime()
				def h = t.hours
				def m = t.minutes
				return h + ":" + (m < 10 ? "0$m" : "$m")
			case "\$day": return adjustTime().date
			case "\$dayOfWeek": return getDayOfWeekNumber()
			case "\$dayOfWeekName": return getDayOfWeekName()
			case "\$month": return adjustTime().month + 1
			case "\$monthName": return getMonthName()
			case "\$year": return adjustTime().year + 1900
			case "\$now": return now()
			case "\$random":
				def result = getRandomValue(name) ?: (float)Math.random()
				setRandomValue(name, result)
				return result
			case "\$randomColor":
				def result = getRandomValue(name) ?: getColorByName("Random").rgb
				setRandomValue(name, result)
				return result
			case "\$randomColorName":
				def result = getRandomValue(name) ?: getColorByName("Random").name
				setRandomValue(name, result)
				return result
			case "\$randomLevel":
				def result = getRandomValue(name) ?: (int)Math.round(100 * Math.random())
				setRandomValue(name, result)
				return result
			case "\$randomHue":
				def result = getRandomValue(name) ?: (int)Math.round(360 * Math.random())
				setRandomValue(name, result)
				return result
			case "\$randomSaturation":
				def result = getRandomValue(name) ?: (int)Math.round(50 + (50 * Math.random()))
				setRandomValue(name, result)
				return result
			case "\$midnight":
				def rightNow = adjustTime().time
				return convertDateToUnixTime(rightNow - rightNow.mod(86400000))
			case "\$nextMidnight":
				def rightNow = adjustTime().time
				return convertDateToUnixTime(rightNow - rightNow.mod(86400000) + 86400000)
			case "\$noon":
				def rightNow = adjustTime().time
				return convertDateToUnixTime(rightNow - rightNow.mod(86400000) + 43200000)
			case "\$nextNoon":
				def rightNow = adjustTime().time
				if (rightNow - rightNow.mod(86400000) + 43200000 < rightNow) rightNow += 86400000
				return convertDateToUnixTime(rightNow - rightNow.mod(86400000) + 43200000)
			case "\$sunrise":
				def sunrise = getSunrise()
				def rightNow = adjustTime().time
				return convertDateToUnixTime(rightNow - rightNow.mod(86400000) + sunrise.hours * 3600000 + sunrise.minutes * 60000)
			case "\$nextSunrise":
				def sunrise = getSunrise()
				def rightNow = adjustTime().time
				if (sunrise.time < rightNow) rightNow += 86400000
				return convertDateToUnixTime(rightNow - rightNow.mod(86400000) + sunrise.hours * 3600000 + sunrise.minutes * 60000)
			case "\$sunset":
				def sunset = getSunset()
				def rightNow = adjustTime().time
				return convertDateToUnixTime(rightNow - rightNow.mod(86400000) + sunset.hours * 3600000 + sunset.minutes * 60000)
			case "\$nextSunset":
				def sunset = getSunset()
				def rightNow = adjustTime().time
				if (sunset.time < rightNow) rightNow += 86400000
				return convertDateToUnixTime(rightNow - rightNow.mod(86400000) + sunset.hours * 3600000 + sunset.minutes * 60000)
			case "\$currentStateDuration":
				try {
					return state.systemStore["\$currentStateSince"] ? now() - (new Date(state.systemStore["\$currentStateSince"])).time : null
				} catch(all) {
					return null
				}
				return null
			case "\$locationMode":
				return location.mode
			case "\$shmStatus":
				return getAlarmSystemStatus()
		}
    */
    /* end of switch */
	if (name.startsWith("@")) {
    	def result = rtData.globalVars[name]
        if (!(result instanceof Map)) result = [t: "error", v: "Variable '$name' not found"]
		return result
	} else {
		if (name.startsWith("\$")) {
			def result = rtData.systemVars[name]
            if (!(result instanceof Map)) result = [t: "error", v: "Variable '$name' not found"]
            if (result && result.v instanceof Closure) {
            	return [t: result.t, v: result.v.call()]
            }
            return result
		} else {
			def result = rtData.localVars[name]
            if (!(result instanceof Map)) result = [t: "error", v: "Variable '$name' not found"]
            return result
		}
	}
}

def handler(evt) {
	Map rtData = parent.getRunTimeData()
    //todo start execution
}


/******************************************************************************/
/*** 																		***/
/*** EXPRESSION FUNCTIONS													***/
/*** 																		***/
/******************************************************************************/

def proxyEvaluateExpression(rtData, expression, dataType = null) {
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
        case "bool":
        	result = [t: expression.t, v: cast(expression.v, expression.t)]
        	break
        case "enum":
        case "error":
        	result = [t: "string", v: cast(expression.v, "string")]
        	break
        case "variable":
        	//get variable as {n: name, t: type, v: value}
        	result = getVariable(rtData, expression.x)
        	break
        case "device":
        	//get variable as {n: name, t: type, v: value}
            def deviceId = expression.id ?: getVariable(rtData, expression.x)?.v
        	def device = getDevice(rtData, deviceId)
            if (device) {
            	def attribute = rtData.attributes[expression.a]
                if (attribute) {
            		result = [t: attribute.t, v: device.currentValue(expression.a)]
                } else {
					result = [t: "error", v: "Attribute ${expression.a} not found"]
                }
            } else {
				result = [t: "error", v: "Device ${expression.id ?: expression.x} not found"]
            }
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
                    if ((t1 != 'number') && (t1 != 'integer') && (t1 != 'decimal') && (t1 != 'float')) t1 = 'decimal'
                    if ((t2 != 'number') && (t2 != 'integer') && (t2 != 'decimal') && (t2 != 'float')) t2 = 'decimal'
                    t = 'decimal'
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
                if ((t1 == 'integer') || (t2 == 'integer')) {
                    t1 = 'integer'
                    t2 = 'integer'
                    t = 'integer'
                }
                if ((t1 == 'number') || (t2 == 'number') || (t1 == 'decimal') || (t2 == 'decimal') || (t1 == 'float') || (t2 == 'float')) {
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
                //log.trace "Executing  ($t1) $v1 $o ($t2) $v2 = ($t) $v"
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
/*** count calculates the number of items in a series of numeric values		***/
/*** Usage: max(values)														***/
/******************************************************************************/
private func_count(rtData, params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting count(values)"];
    }
    return [t: "integer", v: params.size()]
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
			return value instanceof String ? adjustTime(value).time : cast(value, "long")
		case "vector3":
			return value instanceof String ? adjustTime(value).time : cast(value, "long")
		case "orientation":
			return getThreeAxisOrientation(value)
	}
	//anything else...
	return value
}
/******************************************************************************/
/*** DEBUG FUNCTIONS														***/
/******************************************************************************/
private debug(message, cmd = null, shift = null, err = null) {
	def debugging = settings.debugging
	if (!debugging && (cmd != "error")) {
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





















private Map getSystemVars() {
	return [
		"\$currentEventAttribute": [t: 'string', v: null],
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
		"\$now": [t: 'time', v: {(long) now()}],
		"\$utc": [t: 'time', v: {(long) now()}],
		"\$localNow": [t: 'time', v: {(long) now()}],
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