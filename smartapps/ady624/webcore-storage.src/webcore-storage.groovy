/*
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
*/
public static String version() { return "v0.3.106.20180731" }
/******************************************************************************/
/*** webCoRE DEFINITION														***/
/******************************************************************************/
private static String handle() { return "webCoRE" }
definition(
	name: "${handle()} Storage",
	namespace: "ady624",
	author: "Adrian Caramaliu",
	description: "Do not install this directly, use webCoRE instead",
	parent: "ady624:${handle()}",
	category: "Convenience",
    /* icons courtesy of @chauger - thank you */
	iconUrl: "https://cdn.rawgit.com/ady624/${handle()}/master/resources/icons/app-CoRE.png",
	iconX2Url: "https://cdn.rawgit.com/ady624/${handle()}/master/resources/icons/app-CoRE@2x.png",
	iconX3Url: "https://cdn.rawgit.com/ady624/${handle()}/master/resources/icons/app-CoRE@3x.png"
)

preferences {
	//UI pages
	page(name: "pageSettings")
	page(name: "pageSelectDevices")
}


/******************************************************************************/
/*** 																		***/
/*** CONFIGURATION PAGES													***/
/*** 																		***/
/******************************************************************************/
def pageSettings() {
    //clear devices cache
	dynamicPage(name: "pageSettings", title: "", install: false, uninstall: false) {
        if (!parent || !parent.isInstalled()) {
            section() {
                paragraph "Sorry, you cannot install a piston directly from the Marketplace, please use the webCoRE SmartApp instead."
            }
            section("Installing webCoRE") {
                paragraph "If you are trying to install webCoRE, please go back one step and choose webCoRE, not webCoRE Piston. You can also visit wiki.webcore.co for more information on how to install and use webCoRE"
                if (parent) href "", title: "More information", description: parent.getWikiUrl(), style: "external", url: parent.getWikiUrl(), image: "https://cdn.rawgit.com/ady624/webCoRE/master/resources/icons/app-CoRE.png", required: false
            }
        } else {
            section("Available devices") {
                href "pageSelectDevices", title: "Available devices", description: "Tap here to select which devices are available to pistons"
            }
        }
	}
}
private pageSelectDevices() {
	parent.refreshDevices()
	dynamicPage(name: "pageSelectDevices", title: "") {
		section() {
			paragraph "Select the devices you want ${handle()} to have access to."
            paragraph "It is a good idea to only select the devices you plan on using with ${handle()} pistons. Pistons will only have access to the devices you selected."
        }

		section ('Select devices by type') {
        	paragraph "Most devices should fall into one of these two categories"
			input "dev:actuator", "capability.actuator", multiple: true, title: "Which actuators", required: false, submitOnChange: true
			input "dev:sensor", "capability.sensor", multiple: true, title: "Which sensors", required: false, submitOnChange: true
		}

		section ('Select devices by capability') {
        	paragraph "If you cannot find a device by type, you may try looking for it by category below"
			def d
			for (capability in parent.capabilities().findAll{ (!(it.value.d in [null, 'actuators', 'sensors'])) }.sort{ it.value.d }) {
				if (capability.value.d != d) input "dev:${capability.key}", "capability.${capability.key}", multiple: true, title: "Which ${capability.value.d}", required: false, submitOnChange: true
				d = capability.value.d
			}
		}
	}
}


/******************************************************************************/
/*** 																		***/
/*** INITIALIZATION ROUTINES												***/
/*** 																		***/
/******************************************************************************/


private installed() {
	initialize()
	return true
}

private updated() {
	unsubscribe()
	initialize()
	return true
}

private initialize() {
	//update parent
    parent.refreshDevices()
}


/******************************************************************************/
/*** 																		***/
/*** PUBLIC METHODS															***/
/*** 																		***/
/******************************************************************************/

def initData(devices, contacts) {
    if (devices) {
		for(item in devices) {
	    	if (item) {
	    		def deviceType = item.key.replace('dev:', 'capability.')
	    		def deviceIdList = item.value.collect{ it.id }
	    		app.updateSetting(item.key, [type: deviceType, value: deviceIdList])
	        }
	    }
	}
}

def Map listAvailableDevices(raw = false) {
	if (raw) {
    	return settings.findAll{ it.key.startsWith("dev:") }.collect{ it.value }.flatten().collectEntries{ dev -> [(hashId(dev.id)): dev]}
    } else {
    	return settings.findAll{ it.key.startsWith("dev:") }.collect{ it.value }.flatten().collectEntries{ dev -> [(hashId(dev.id)): dev]}.collectEntries{ id, dev -> [ (id): [ n: dev.getDisplayName(), cn: dev.getCapabilities()*.name, a: dev.getSupportedAttributes().unique{ it.name }.collect{def x = [n: it.name, t: it.getDataType(), o: it.getValues()]; try {x.v = dev.currentValue(x.n);} catch(all) {}; x}, c: dev.getSupportedCommands().unique{ it.getName() }.collect{[n: it.getName(), p: it.getArguments()]} ]]}
	}
}

def Map getDashboardData() {
    boolean ok
    def value
    def item
    def start = now()
	return settings.findAll{ it.key.startsWith("dev:") }.collect{ it.value }.flatten().collectEntries{ dev -> [(hashId(dev.id)): dev]}.collectEntries{ id, dev ->
        [ (id): dev.getSupportedAttributes().collect{ it.name }.unique().collectEntries{
	    	try { value = dev.currentValue(it); } catch (all) { value = null};
			return [ (it) : value]
	    }]
    }
}

public String mem(showBytes = true) {
	def bytes = state.toString().length()
	return Math.round(100.00 * (bytes/ 100000.00)) + "%${showBytes ? " ($bytes bytes)" : ""}"
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

/******************************************************************************/
/***																		***/
/*** END OF CODE															***/
/***																		***/
/******************************************************************************/
