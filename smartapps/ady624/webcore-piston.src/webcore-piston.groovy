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

def version() {	return "v0.0.01d.20170227" }
/*
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

private getDevice(deviceId) {
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
    	v: piston.v ?: [] 
    ] : [:]
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

def handler(evt) {
	log.trace "EVENT!"
}


/******************************************************************************/
/*** 																		***/
/*** EXPRESSION FUNCTIONS													***/
/*** 																		***/
/******************************************************************************/

def evaluateExpression(expression, dataType = null) {
    //if dealing with an expression that has multiple items, let's evaluate each item one by one
    //let's evaluate this expression
    Map result = [:]
    switch (expression.t) {
        case "string":
        case "integer":
        case "decimal":
        	result = [t: expression.t, v: cast(expression.v, expression.t)]
        	break
        case "variable":
        	//get variable as {n: name, t: type, v: value}
        	def variable = [n: 'name', t: 'dynamic', v: '0']
        	result = [t: variable.t, v: variable.v]
        	break
        case "device":
        	//get variable as {n: name, t: type, v: value}
        	def device = [:]; //get physical device
			def attribute = [:]; //getAttribute
        	def value = 0; //device.getValue(attribute)
			result = [t: attribute.t, v: value]
        	break
        case "operand":
        	result = [t: "string", v: cast(expression.v, "string")]
        	break
        case "function":
            def fn = "func_${expression.n}"
            try {
				result = "$fn"(expression.i)
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
                    	switch (operator.o) {
                        	case '+':
                            case '-':
                            case '^':
                            	items.push([t: decimal, v: 0, o: operator.o])
                                break;
                        	case '*':
                            case '/':
                            	items.push([t: decimal, v: 1, o: operator.o])
                                break;
                        	case '&':
                            	items.push([t: boolean, v: true, o: operator.o])
                                break;
                        	case '|':
                            	items.push([t: boolean, v: false, o: operator.o])
                                break;
                        }
                    } else {
                    	items[operand].o = item.o;
                        operand = -1;
                    }
	            } else {
	                items.push(evaluateExpression(item))
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
                def t = 'string'
                def o = items[idx].o
                def t1 = items[idx].t
                def v1 = items[idx].v
                def t2 = items[idx + 1].t
                def v2 = items[idx + 1].t
                //fix-ups
                //integer with decimal gives decimal, also *, /, and ^ require decimals
                if ((o == '*') || (o == '*') || (o == '/') || (o == '-')) {
                    if ((t1 != 'number') && (t1 != 'integer') && (t1 != 'decimal') && (t1 != 'float')) t1 = 'decimal'
                    if ((t2 != 'number') && (t2 != 'integer') && (t2 != 'decimal') && (t2 != 'float')) t2 = 'decimal'
                    t = 'decimal'
                }
                if ((o == '&') || (o == '|')) {
                    t1 = 'boolean'
                    t2 = 'boolean'
                    t = 'boolean'
                }
                if ((o == '+') && ((t1 == 'string') || (t1 == 'string') || (t1 == 'string') || (t1 == 'string'))) {
                    t1 = 'string';
                    t2 = 'string';
                    t = 'string'
                }
                if ((((t1 == 'number') || (t1 == 'integer')) && ((t2 == 'decimal') || (t2 == 'float'))) || (((t2 == 'number') || (t2 == 'integer')) && ((t1 == 'decimal') || (t1 == 'float')))) {
                    t1 = 'decimal'
                    t2 = 'decimal'
                    t = 'decimal'
                }
                v1 = evaluateExpression(items[idx], t1).v
                v2 = evaluateExpression(items[idx + 1], t2).v
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
                        v = v1 + v2
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
    return result.t == "error" ? result : [t: dataType ?: result.t, v: cast(result.v, dataType?: result.t)]
}


/******************************************************************************/
/*** dewPoint returns the calculated dew point temperature					***/
/*** Usage: dewPoint(temperature, relativeHumidity[, scale])				***/
/******************************************************************************/
def func_dewpoint(params) {
	if (!params || !(params instanceof List) || (params.size() < 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting dewPoint(temperature, relativeHumidity[, scale])"];
    }
    double t = evaluateExpression(params[0], 'decimal').v
    double rh = evaluateExpression(params[1], 'decimal').v
    //if no temperature scale is provided, we assume the location's temperature scale
    boolean fahrenheit = cast(params.size() > 2 ? evaluateExpression(params[2]).v : location.temperatureScale, "string").toUpperCase() == "F"
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
private func_celsius(params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting celsius(temperature)"];
    }
    double t = evaluateExpression(params[0], 'decimal').v
    //convert temperature to Celsius
    return [t: "decimal", v: (double) t * 9.0 / 5.0 + 32.0]
}


/******************************************************************************/
/*** fahrenheit converts temperature from Celsius to Fahrenheit				***/
/*** Usage: fahrenheit(temperature)											***/
/******************************************************************************/
private func_fahrenheit(params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting fahrenheit(temperature)"];
    }
    double t = evaluateExpression(params[0], 'decimal').v
    //convert temperature to Fahrenheit
    return [t: "decimal", v: (double) (t - 32.0) * 5.0 / 9.0]
}

/******************************************************************************/
/*** integer/number converts a decimal value to it's integer value			***/
/*** Usage: integer(decimal)												***/
/******************************************************************************/
private func_integer(params) {
	if (!params || !(params instanceof List) || (params.size() < 1)) {
    	return [t: "error", v: "Invalid parameters. Expecting integer(decimal)"];
    }
    return [t: "integer", v: evaluateExpression(params[0], 'integer').v]
}
private func_number(params) { return func_number(params) }

/******************************************************************************/
/*** sprintf converts formats a series of values into a string				***/
/*** Usage: sprintf(format, arguments)										***/
/******************************************************************************/
private func_sprintf(params) {
	if (!params || !(params instanceof List) || (params.size() < 2)) {
    	return [t: "error", v: "Invalid parameters. Expecting sprintf(format, arguments)"];
    }
    def format = evaluateExpression(params[0], 'string').v
    List args = []
    for (int x = 1; x < params.size(); x++) {
    	args.push(evaluateExpression(params[x]).v)
    }
    return [t: "string", v: sprintf(format, args)]
}
private func_format(params) { return func_sprintf(params) }



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
	switch (dataType) {
		case "string":
		case "text":
			if (value instanceof Boolean) {
				return value ? "true" : "false"
			}
			return value ? "$value" : ""
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
			def result = (float) 0
			try {
				result = (float) value
			} catch(all) {
			}
			return result ? result : (float) 0
		case "boolean":
			if (value instanceof String) {
				if (!value || (value in falseStrings))
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