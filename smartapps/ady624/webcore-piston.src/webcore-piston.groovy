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

def version() {	return "v0.0.011.20170123" }
/*
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
	//log.trace "GOT HERE"    
	//def device = parent.getDevice("owekf34r24r324");
    //log.trace device
    //subscribe(device, "switch", handler)
}

private getDevice(deviceId) {
}

def get() {
	def piston = state.piston ?: [:]
    piston.id = hashId(app.id);
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
    	s: piston.s ?: [],
    	v: piston.v ?: []        
    ] : [:]
    if (state.build == 1) {
    	resume()
    }
    return [active: state.active, build: state.build, modified: state.modified]
}

def bin(id) {
	state.bin = id;
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