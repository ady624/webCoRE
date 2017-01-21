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
 

def String version() {	return "v0.0.00c.20170121" }
/*
 *	01/21/2017 >>> v0.0.00c.20170121 - ALPHA - Added created/modified attributes
 *	12/02/2016 >>> v0.0.002.20161028 - ALPHA - Small progress, Add new piston now points to the piston editor UI
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
	//log.trace "GOT HERE"    
	//def device = parent.getDevice("owekf34r24r324");
    //log.trace device
    //subscribe(device, "switch", handler)
}

private getDevice(deviceId) {
}

def getPiston() {
	def piston = state.piston ?: [:]
    piston.id = hashId(app.id);
    piston.name = app.label ?: app.name
    piston.created = state.created
    piston.modified = state.modified
    return piston
}

def setPiston(piston) {
	log.trace "GOT HERE"
	state.modified = now()
    state.piston = piston ?: [:]
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