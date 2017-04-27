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
 *
 *  Version history
*/
public static String version() { return "v0.0.092.20170427" }
/*
 *	04/27/2017 >>> v0.0.092.20170427 - ALPHA - Added time trigger happens daily at...
 *	04/27/2017 >>> v0.0.091.20170427 - ALPHA - Various improvements and fixes
 *	04/26/2017 >>> v0.0.090.20170426 - ALPHA - Minor fixes for variables and the eq() function
 *	04/26/2017 >>> v0.0.08f.20170426 - ALPHA - Implemented $args and the special $args.<dynamic> variables to read arguments from events. Bonus: ability to parse JSON data to read subitem by using $args.item.subitem (no array support yet)
 *	04/26/2017 >>> v0.0.08e.20170426 - ALPHA - Implemented Send notification to contacts
 *	04/26/2017 >>> v0.0.08d.20170426 - ALPHA - Timed triggers should now play nice with multiple devices (any/all)
 *	04/25/2017 >>> v0.0.08c.20170425 - ALPHA - Various fixes and improvements and implemented custom commands with parameters
 *	04/24/2017 >>> v0.0.08b.20170424 - ALPHA - Fixed a bug preventing subscription to IFTTT events
 *	04/24/2017 >>> v0.0.08a.20170424 - ALPHA - Implemented Routine/AskAlexa/EchoSistant/IFTTT integrations - arguments (where available) are not processed yet - not tested
 *	04/24/2017 >>> v0.0.089.20170424 - ALPHA - Added variables in conditions and matching/non-matching device variable output
 *	04/23/2017 >>> v0.0.088.20170423 - ALPHA - Time condition offsets
 *	04/23/2017 >>> v0.0.087.20170423 - ALPHA - Timed triggers (stay/stays) implemented - need additional work to get them to play nicely with "Any of devices stays..." - this never worked in CoRE, but proved to might-have-been-helpful
 *	04/23/2017 >>> v0.0.086.20170423 - ALPHA - Subscriptions to @global variables
 *	04/22/2017 >>> v0.0.085.20170422 - ALPHA - Fixed a bug with virtual device options
 *	04/22/2017 >>> v0.0.084.20170422 - ALPHA - NFL integration complete LOL (not really, implemented global variables though)
 *	04/21/2017 >>> v0.0.083.20170421 - ALPHA - Fixed a bug introduced during device-typed variable refactoring, $currentEventDevice was not properly stored as a List of device Ids
 *	04/21/2017 >>> v0.0.082.20170421 - ALPHA - Fixed a pseudo-bug where older pistons (created before some parameters were added) are missing some operands and that causes errors during evaluations
 *	04/21/2017 >>> v0.0.081.20170421 - ALPHA - Fixed a bug preventing a for-each to work with device-typed variables
 *	04/21/2017 >>> v0.0.080.20170421 - ALPHA - Fixed a newly introduced bug where function parameters were parsed as strings, also fixed functions time, date, and datetime's timezone
 *	04/21/2017 >>> v0.0.07f.20170421 - ALPHA - Fixed an inconsistency in setting device variable (array) - this was in the UI and may require resetting the variables
 *	04/21/2017 >>> v0.0.07e.20170421 - ALPHA - Fixed a bug with local variables introduced in 07d
 *	04/21/2017 >>> v0.0.07d.20170421 - ALPHA - Lots of improvements for device variables
 *	04/20/2017 >>> v0.0.07c.20170420 - ALPHA - Timed conditions are finally working (was* and changed/not changed), basic tests performed
 *	04/19/2017 >>> v0.0.07b.20170419 - ALPHA - First attempt to get 'was' conditions up and running
 *	04/19/2017 >>> v0.0.07a.20170419 - ALPHA - Minor bug fixes, triggers inside timers no longer subscribe to events (the timer is a trigger itself) - triggers should not normally be used inside timers
 *	04/19/2017 >>> v0.0.079.20170419 - ALPHA - Time condition restrictions are now working, added date and date&time conditions, offsets still missing
 *	04/18/2017 >>> v0.0.078.20170418 - ALPHA - Time conditions now subscribe for time events - added restrictions to UI dialog, but not yet implemented
 *	04/18/2017 >>> v0.0.077.20170418 - ALPHA - Implemented time conditions - no date or datetime yet, also, no subscriptions for time events yet
 *	04/18/2017 >>> v0.0.076.20170418 - ALPHA - Implemented task mode restrictions and added setColor using HSL
 *	04/17/2017 >>> v0.0.075.20170417 - ALPHA - Fixed a problem with $sunrise and $sunset pointing to the wrong date
 *	04/17/2017 >>> v0.0.074.20170417 - ALPHA - Implemented HTTP requests, importing response data not working yet, need to figure out a way to specify what data goes into which variables
 *	04/17/2017 >>> v0.0.073.20170417 - ALPHA - isBetween fix - use three params, not two, thanks to @c1arkbar
 *	04/16/2017 >>> v0.0.072.20170416 - ALPHA - Quick fix for isBetween
 *	04/16/2017 >>> v0.0.071.20170416 - ALPHA - Added the ability to execute routines
 *	04/16/2017 >>> v0.0.070.20170416 - ALPHA - Added support for multiple-choice comparisons (any of), added more improvements like the ability to disable event subscriptions (follow up pistons)
 *	04/15/2017 >>> v0.0.06f.20170415 - ALPHA - Fix for wait for date&time
 *	04/15/2017 >>> v0.0.06e.20170415 - ALPHA - Attempt to fix a race condition where device value would change before we even executed - using event's value instead
 *	04/15/2017 >>> v0.0.06d.20170415 - ALPHA - Various fixes and improvements, added the ability to execute pistons in the same location (arguments not working yet)
 *	04/15/2017 >>> v0.0.06c.20170415 - ALPHA - Fixed a bug with daily timers and day of week restrictions
 *	04/14/2017 >>> v0.0.06b.20170414 - ALPHA - Added more functions: date(value), time(value), if(condition, valueIfTrue, valueIfFalse), not(value), isEmpty(value), addSeconds(dateTime, seconds), addMinutes(dateTime, minutes), addHours(dateTime, hours), addDays(dateTime, days), addWeeks(dateTime, weeks)
 *	04/14/2017 >>> v0.0.06a.20170414 - ALPHA - Fixed a bug where multiple timers would cancel each other's actions out, implemented (not extensively tested yet) the TCP and TEP
 *	04/13/2017 >>> v0.0.069.20170413 - ALPHA - Various bug fixes and improvements
 *	04/12/2017 >>> v0.0.068.20170412 - ALPHA - Fixed a bug with colors from presets
 *	04/12/2017 >>> v0.0.067.20170412 - ALPHA - Fixed a bug introduced in 066 and implemented setColor
 *	04/12/2017 >>> v0.0.066.20170412 - ALPHA - Fixed hourly timers and implemented setInfraredLevel, setHue, setSaturation, setColorTemperature
 *	04/11/2017 >>> v0.0.065.20170411 - ALPHA - Fix for long waits being converted to scientific notation, causing the scheduler to misunderstand them and wait 1ms instead
 *	04/11/2017 >>> v0.0.064.20170411 - ALPHA - Fix for timer restrictions error
 *	04/11/2017 >>> v0.0.063.20170411 - ALPHA - Some fixes for timers, implemented all timers, implemented all timer restrictions.
 *	04/10/2017 >>> v0.0.062.20170410 - ALPHA - Some fixes for timers, implemented all timers, their restrictions still not active.
 *	04/07/2017 >>> v0.0.061.20170407 - ALPHA - Some fixes for timers (waits inside timers) and implemented weekly timers. Months/years not working yet. Should be more stable.
 *	04/06/2017 >>> v0.0.060.20170406 - ALPHA - Timers for second/minute/hour/day are in. week/month/year not working yet. May be VERY quirky, still. *	03/30/2017 >>> v0.0.05f.20170329 - ALPHA - Attempt to fix setLocation, added Twilio integration (dialog support coming soon)
 *	03/30/2017 >>> v0.0.05f.20170329 - ALPHA - Attempt to fix setLocation, added Twilio integration (dialog support coming soon)
 *	03/29/2017 >>> v0.0.05e.20170329 - ALPHA - Added sendEmail
 *	03/29/2017 >>> v0.0.05d.20170329 - ALPHA - Minor typo fixes, thanks to @rayzurbock
 *	03/28/2017 >>> v0.0.05c.20170328 - ALPHA - Minor fixes regarding location subscriptions
 *	03/28/2017 >>> v0.0.05b.20170328 - ALPHA - Minor fixes for setting location mode
 *	03/27/2017 >>> v0.0.05a.20170327 - ALPHA - Minor fixes - location events do not have a device by default, overriding with location
 *	03/27/2017 >>> v0.0.059.20170327 - ALPHA - Completed SHM status and location mode. Can get/set, can subscribe to changes, any existing condition in pistons needs to be revisited and fixed
 *	03/25/2017 >>> v0.0.058.20170325 - ALPHA - Fixes for major issues introduced due to the new comparison editor (you need to re-edit all comparisons to fix them), added log multiline support, use \r or \n or \r\n in a string
 *	03/24/2017 >>> v0.0.057.20170324 - ALPHA - Improved installation experience, preventing direct installation of child app, location mode and shm status finally working
 *	03/23/2017 >>> v0.0.056.20170323 - ALPHA - Various fixes for restrictions
 *	03/22/2017 >>> v0.0.055.20170322 - ALPHA - Various improvements, including a revamp of the comparison dialog, also moved the dashboard website to https://dashboard.webcore.co
 *	03/21/2017 >>> v0.0.054.20170321 - ALPHA - Moved the dashboard website to https://webcore.homecloudhub.com/dashboard/
 *	03/21/2017 >>> v0.0.053.20170321 - ALPHA - Fixed a bug where variables containing expressions would be cast to the variable type outside of evaluateExpression (the right way)
 *	03/20/2017 >>> v0.0.052.20170320 - ALPHA - Fixed $shmStatus
 *	03/20/2017 >>> v0.0.051.20170320 - ALPHA - Fixed a problem where start values for variables would not be correctly picked up from atomicState (used state by mistake)
 *	03/20/2017 >>> v0.0.050.20170320 - ALPHA - Introducing parallelism, a semaphore mechanism to allow synchronization of multiple simultaneous executions, disabled by default (pistons wait at a semaphore)
 *	03/20/2017 >>> v0.0.04f.20170320 - ALPHA - Minor fixes for device typed variables (lost attribute) and counter variable in for each
 *	03/20/2017 >>> v0.0.04e.20170320 - ALPHA - Major operand/expression/cast refactoring to allow for arrays of devices - may break things. Also introduced for each loops and actions on device typed variables
 *	03/19/2017 >>> v0.0.04d.20170319 - ALPHA - Fixes for functions and device typed variables
 *	03/19/2017 >>> v0.0.04c.20170319 - ALPHA - Device typed variables now enabled - not yet possible to use them in conditions or in actions, but getting there
 *	03/18/2017 >>> v0.0.04b.20170318 - ALPHA - Various fixes
 *	03/18/2017 >>> v0.0.04a.20170318 - ALPHA - Enabled manual piston status and added the set piston status task as well as the exit statement
 *	03/18/2017 >>> v0.0.049.20170318 - ALPHA - Third attempt to fix switch
 *	03/18/2017 >>> v0.0.048.20170318 - ALPHA - Second attempt to fix switch fallbacks with wait breaks, wait in secondary cases were not working
 *	03/18/2017 >>> v0.0.047.20170318 - ALPHA - Attempt to fix switch fallbacks with wait breaks
 *	03/18/2017 >>> v0.0.046.20170318 - ALPHA - Various critical fixes - including issues with setLevel without a required state
 *	03/18/2017 >>> v0.0.045.20170318 - ALPHA - Fixed a newly introduced bug for Toggle (missing parameters)
 *	03/17/2017 >>> v0.0.044.20170317 - ALPHA - Cleanup ghost else-ifs on piston save
 *	03/17/2017 >>> v0.0.043.20170317 - ALPHA - Added "View piston in dashboard" to child app UI
 *	03/17/2017 >>> v0.0.042.20170317 - ALPHA - Various fixes and enabled restrictions - UI for conditions and restrictions needs refactoring to use the new operand editor
 *	03/16/2017 >>> v0.0.041.20170316 - ALPHA - Various fixes
 *	03/16/2017 >>> v0.0.040.20170316 - ALPHA - Fixed a bug where optional parameters were not correctly interpreted, leading to setLevel not working, added functions startsWith, endsWith, contains, eq, le, lt, ge, gt
 *	03/16/2017 >>> v0.0.03f.20170316 - ALPHA - Completely refactored task parameters and enabled variables. Dynamically assigned variables act as functions - it can be defined as an expression and reuse it in lieu of that expression
 *	03/15/2017 >>> v0.0.03e.20170315 - ALPHA - Various improvements
 *	03/14/2017 >>> v0.0.03d.20170314 - ALPHA - Fixed a bug with caching operands for triggers
 *	03/14/2017 >>> v0.0.03c.20170314 - ALPHA - Fixed a bug with switches
 *	03/14/2017 >>> v0.0.03b.20170314 - ALPHA - For statement finally getting some love
 *	03/14/2017 >>> v0.0.03a.20170314 - ALPHA - Added more functions (age, previousAge, newer, older, previousValue) and fixed a bug where operand caching stopped working after earlier code refactorings
 *	03/13/2017 >>> v0.0.039.20170313 - ALPHA - The Switch statement should now be functional - UI validation not fully done
 *	03/12/2017 >>> v0.0.038.20170312 - ALPHA - Traversing else ifs and else statements in search for devices to subscribe to
 *	03/12/2017 >>> v0.0.037.20170312 - ALPHA - Added support for break and exit (partial, piston state is not set on exit) - fixed some comparison data type incompatibilities
 *	03/12/2017 >>> v0.0.036.20170312 - ALPHA - Added TCP = cancel on condition change and TOS = Action - no other values implemented yet, also, WHILE loops are now working, please remember to add a WAIT in it...
 *	03/11/2017 >>> v0.0.035.20170311 - ALPHA - A little error creeped into the conditions, fixed it
 *	03/11/2017 >>> v0.0.034.20170311 - ALPHA - Multiple device selection aggregation now working properly. COUNT(device list's contact) rises above 1 will be true when at least two doors in the list are open :D
 *	03/11/2017 >>> v0.0.033.20170311 - ALPHA - Implemented all conditions except "was..." and all triggers except "stays..."
 *	03/11/2017 >>> v0.0.032.20170311 - ALPHA - Fixed setLevel null params and added version checking
 *	03/11/2017 >>> v0.0.031.20170310 - ALPHA - Various fixes including null optional parameters, conditional groups, first attempt at piston restrictions (statement restrictions not enabled yet), fixed a problem with subscribing device bolt indicators only showing for one instance of each device/attribute pair, fixed sendPushNotification
 *	03/10/2017 >>> v0.0.030.20170310 - ALPHA - Fixed a bug in scheduler introduced in 02e/02f
 *	03/10/2017 >>> v0.0.02f.20170310 - ALPHA - Various improvements, added toggle and toggleLevel
 *	03/10/2017 >>> v0.0.02e.20170310 - ALPHA - Fixed a problem where long expiration settings prevented logins (integer overflow)
 *	03/10/2017 >>> v0.0.02d.20170310 - ALPHA - Reporting version to JS
 *	03/10/2017 >>> v0.0.02c.20170310 - ALPHA - Various improvements and a new virtual command: Log to console. Powerful.
 *	03/10/2017 >>> v0.0.02b.20170310 - ALPHA - Implemented device versioning to correctly handle multiple browsers accessing the same dashboard after a device selection was performed, enabled security token expiry
 *	03/09/2017 >>> v0.0.02a.20170309 - ALPHA - Fixed parameter issues, added support for expressions in all parameters, added notification virtual tasks
 *	03/09/2017 >>> v0.0.029.20170309 - ALPHA - More execution flow fixes, sticky trace lines fixed
 *	03/08/2017 >>> v0.0.028.20170308 - ALPHA - Scheduler fixes
 *	03/08/2017 >>> v0.0.027.20170308 - ALPHA - Very early implementation of wait/delay scheduling, needs extensive testing
 *	03/08/2017 >>> v0.0.026.20170308 - ALPHA - More bug fixes, trace enhancements
 *	03/07/2017 >>> v0.0.025.20170307 - ALPHA - Improved logs and traces, added basic time event handler
 *	03/07/2017 >>> v0.0.024.20170307 - ALPHA - Improved logs (reverse order and live updates) and added trace support
 *	03/06/2017 >>> v0.0.023.20170306 - ALPHA - Added logs to the dashboard
 *	03/05/2017 >>> v0.0.022.20170305 - ALPHA - Some tasks are now executed. UI has an issue with initializing params on editing a task, will get fixed soon.
 *	03/01/2017 >>> v0.0.021.20170301 - ALPHA - Most conditions (and no triggers yet) are now parsed and evaluated during events - action tasks not yet executed, but getting close, very close
 *	02/28/2017 >>> v0.0.020.20170228 - ALPHA - Added runtime data - pistons are now aware of devices and global variables - expressions can query devices and variables (though not all system variables are ready yet)
 *	02/27/2017 >>> v0.0.01f.20170227 - ALPHA - Added support for a bunch more functions
 *	02/27/2017 >>> v0.0.01e.20170227 - ALPHA - Fixed a bug in expression parser where integer + integer would result in a string
 *	02/27/2017 >>> v0.0.01d.20170227 - ALPHA - Made progress evaluating expressions
 *	02/24/2017 >>> v0.0.01c.20170224 - ALPHA - Added functions support to main app
 *	02/06/2017 >>> v0.0.01b.20170206 - ALPHA - Fixed a problem with selecting thermostats
 *	02/01/2017 >>> v0.0.01a.20170201 - ALPHA - Updated comparisons
 *	01/30/2017 >>> v0.0.019.20170130 - ALPHA - Improved comparisons - ouch
 *	01/29/2017 >>> v0.0.018.20170129 - ALPHA - Fixed a conditions where devices would not be sent over to the UI
 *	01/28/2017 >>> v0.0.017.20170128 - ALPHA - Incremental update
 *	01/27/2017 >>> v0.0.016.20170127 - ALPHA - Minor compatibility fixes
 *	01/27/2017 >>> v0.0.015.20170127 - ALPHA - Updated capabilities, attributes, commands and refactored them into maps
 *	01/26/2017 >>> v0.0.014.20170126 - ALPHA - Progress getting comparisons to work
 *	01/25/2017 >>> v0.0.013.20170125 - ALPHA - Implemented the author field and more improvements to the piston editor
 *	01/23/2017 >>> v0.0.012.20170123 - ALPHA - Implemented the "delete" piston
 *	01/23/2017 >>> v0.0.011.20170123 - ALPHA - Fixed a bug where account id was not hashed
 *	01/23/2017 >>> v0.0.010.20170123 - ALPHA - Duplicate piston and restore from automatic backup :)
 *	01/23/2017 >>> v0.0.00f.20170123 - ALPHA - Automatic backup to myjson.com is now enabled. Restore is not implemented yet.
 *	01/22/2017 >>> v0.0.00e.20170122 - ALPHA - Enabled device cache on main app to speed up dashboard when using large number of devices
 *	01/22/2017 >>> v0.0.00d.20170122 - ALPHA - Optimized data usage for piston JSON class (might have broken some things), save now works
 *	01/21/2017 >>> v0.0.00c.20170121 - ALPHA - Made more progress towards creating new pistons
 *	01/21/2017 >>> v0.0.00b.20170121 - ALPHA - Made progress towards creating new pistons
 *	01/20/2017 >>> v0.0.00a.20170120 - ALPHA - Fixed a problem with dashboard URL and shards other than na01
 *	01/20/2017 >>> v0.0.009.20170120 - ALPHA - Reenabled the new piston UI at new URL
 *	01/20/2017 >>> v0.0.008.20170120 - ALPHA - Enabled html5 routing and rewrite to remove the /#/ contraption
 *	01/20/2017 >>> v0.0.007.20170120 - ALPHA - Cleaned up CoRE ST UI and removed "default" theme from URL.
 *	01/19/2017 >>> v0.0.006.20170119 - ALPHA - UI is now fully moved and security enabled - security password is now required
 *	01/18/2017 >>> v0.0.005.20170118 - ALPHA - Moved UI to homecloudhub.com and added support for pretty url (core.homecloudhub.com) and web+core:// handle
 *	01/17/2017 >>> v0.0.004.20170117 - ALPHA - Updated to allow multiple instances
 *	01/17/2017 >>> v0.0.003.20170117 - ALPHA - Improved security, object ids are hashed, added multiple-location-multiple-instance support (CoRE will be able to work across multiple location and installed instances)
 *	12/02/2016 >>> v0.0.002.20161202 - ALPHA - Small progress, Add new piston now points to the piston editor UI
 *	10/28/2016 >>> v0.0.001.20161028 - ALPHA - Initial release
 */

/******************************************************************************/
/*** webCoRE DEFINITION														***/
/******************************************************************************/
private static String handle() { return "webCoRE" }
private static String domain() { return "webcore.co" }
definition(
	name: "${handle()}",
	namespace: "ady624",
	author: "Adrian Caramaliu",
	description: "Tap here to install ${handle()} ${version()}",
	category: "Convenience",
	singleInstance: false,
    /* icons courtesy of @chauger - thank you */
	iconUrl: "https://cdn.rawgit.com/ady624/${handle()}/master/resources/icons/app-CoRE.png",
	iconX2Url: "https://cdn.rawgit.com/ady624/${handle()}/master/resources/icons/app-CoRE@2x.png",
	iconX3Url: "https://cdn.rawgit.com/ady624/${handle()}/master/resources/icons/app-CoRE@3x.png"
)


preferences {
	//UI pages
	page(name: "pageMain")
	page(name: "pageInitializeDashboard")
	page(name: "pageFinishInstall")
	page(name: "pageSelectDevices")
	page(name: "pageSelectContacts")
	page(name: "pageSettings")
    page(name: "pageChangePassword")
    page(name: "pageSavePassword")
	page(name: "pageIntegrations")
	page(name: "pageIntegrationAskAlexa")
	page(name: "pageIntegrationIFTTT")
	page(name: "pageIntegrationIFTTTConfirm")
	page(name: "pageIntegrationTwilio")
	page(name: "pageIntegrationTwilioTest")
	page(name: "pageRemove")
}


/******************************************************************************/
/*** webCoRE CONSTANTS														***/
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
	//webCoRE Dashboard initialization
	def success = initializeWebCoREEndpoint()
	if (!state.installed) {
        return dynamicPage(name: "pageMain", title: "", install: false, uninstall: false, nextPage: "pageInitializeDashboard") {
            section() {
                paragraph "Welcome to ${handle()}"
                paragraph "You will be guided through a few installation steps that should only take a minute."
            }
            if (success) {
            	if (!state.oAuthRequired) {
            		section('Note') {
		            	paragraph "If you have previously installed ${handle()} and are trying to open it, please go back to the Automations tab and access ${handle()} from the SmartApps section.\r\n\r\nIf you are trying to install another instance of ${handle()} then please continue with the steps.", required: true
		            }
                }
                section() {
                	paragraph "It looks like you are ready to go, please tap Next"
                }
            } else {
                section() {
                    paragraph "We'll start by configuring the dashboard. You need to setup OAuth in the SmartThings IDE for the ${handle()} SmartApp."
                }
                pageSectionInstructions()
                section () {
                    paragraph "Once you have finished the steps above, tap Next", required: true
                }
            }
        }
	}
	//webCoRE main page
	dynamicPage(name: "pageMain", title: "", install: true, uninstall: false) {
    	section("Engine block") {
			href "pageEngineBlock", title: "Cast iron", description: app.version(), image: "https://cdn.rawgit.com/ady624/${handle()}/master/resources/icons/app-CoRE.png", required: false
        }
		section("") {
			if (!state.endpoint) {
				href "pageInitializeDashboard", title: "Dashboard", description: "Tap to initialize", image: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/icons/dashboard.png", required: false
			} else {
				//trace "*** DO NOT SHARE THIS LINK WITH ANYONE *** Dashboard URL: ${getDashboardInitUrl()}"
				href "", title: "Dashboard", style: "external", url: getDashboardInitUrl(), description: "Tap to open", image: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/icons/dashboard.png", required: false
				href "", title: "Register a browser", style: "embedded", url: getDashboardInitUrl(true), description: "Tap to open", image: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/icons/dashboard.png", required: false
			}
		}	
		section(title:"Settings") {
			href "pageSettings", title: "Settings", image: "https://cdn.rawgit.com/ady624/CoRE/master/resources/images/icons/settings.png", required: false
		}
	}	
}

private pageSectionInstructions() {
	state.oAuthRequired = true
    section () {
        paragraph "Please follow these steps:", required: true
        paragraph "1. Go to your SmartThings IDE and log in", required: true
        paragraph "2. Click on 'My SmartApps' and locate the 'ady624 : ${handle()}' SmartApp in the list", required: true
        paragraph "3. Click the 'Edit Properties' button to the left of the SmartApp name (a notepad and pencil icon)", required: true
        paragraph "4. Click on 'OAuth'", required: true
        paragraph "5. Click the 'Enable OAuth in Smart App' button", required: true
        paragraph "6. Click the 'Update' button", required: true
    }
}

private pageInitializeDashboard() {
	//webCoRE Dashboard initialization
	def success = initializeWebCoREEndpoint()
	dynamicPage(name: "pageInitializeDashboard", title: "", nextPage: success ? "pageSelectDevices" : null) {
		if (!state.installed) {
			if (success) {
            	section() {
					paragraph "Great, the dashboard is ready to go."
                }
                section() {
	            	paragraph "Now, please choose a name for this ${handle()} instance"
					//label name: "name", title: "Name", defaultValue: "webCoRE", required: false
					label name: "name", title: "Name", state: (name ? "complete" : null), defaultValue: app.name, required: false

                }
                section() {
                	paragraph "${state.installed ? "Tap Done to continue." : "Next, choose a security password for your dashboard. You will need to enter this password when accessing your dashboard for the first time, and possibly from time to time, depending on your settings."}", required: false
				}
			} else {
            	section() {
					paragraph "Sorry, it looks like OAuth is not properly enabled."
                }
				pageSectionInstructions()
				section () {
                    paragraph "Once you have finished the steps above, go back and try again", required: true
	            }
				return
			}
		}
        pageSectionPIN()
	}
}

private pageSelectDevices() {
	state.deviceVersion = now().toString()
	dynamicPage(name: "pageSelectDevices", title: "", nextPage: state.installed ? null : (location.getContactBookEnabled() ? "pageSelectContacts" : "pageFinishInstall")) {
		section() {
			paragraph "${state.installed ? "Select the devices you want ${handle()} to have access to." : "Great, now let's select some devices."}"
            paragraph "It is a good idea to only select the devices you plan on using with ${handle()} pistons. Pistons will only have access to the devices you selected."
        }
        if (!state.installed) {
        	section (Note) {
            	paragraph "Remember, you can always come back to ${handle()} and add or remove devices as needed.", required: true
            }
        	section() {
            	paragraph "So go ahead, select a few devices, then tap Next"
            }
        }

		section ('Select devices by type') {
        	paragraph "Most devices should fall into one of these two categories"
			input "dev:actuator", "capability.actuator", multiple: true, title: "Which actuators", required: false
			input "dev:sensor", "capability.sensor", multiple: true, title: "Which sensors", required: false
		}
        
		section ('Select devices by capability') {
        	paragraph "If you cannot find a device by type, you may try looking for it by category below"
			def d
			for (capability in capabilities().findAll{ (!(it.value.d in [null, 'actuators', 'sensors'])) }.sort{ it.value.d }) {
				if (capability.value.d != d) input "dev:${capability.key}", "capability.${capability.key}", multiple: true, title: "Which ${capability.value.d}", required: false
				d = capability.value.d
			}
		}
	}
}

private pageSelectContacts() {
	state.deviceVersion = now().toString()
	dynamicPage(name: "pageSelectContacts", title: "", nextPage: state.installed ? null : "pageFinishInstall") {
		section() {
			paragraph "${state.installed ? "Select the contacts you want ${handle()} to have access to." : "Great, now let's select some contacts."}"
        }
        if (!state.installed) {
        	section (Note) {
            	paragraph "Remember, you can always come back to ${handle()} and add or remove contacts as needed.", required: true
            }
        	section() {
            	paragraph "So go ahead, select a few contacts, then tap Next"
            }
        }
        section () {
			input "contacts", "contact", multiple: true, title: "Which contacts", required: false
		}
	}
}

private pageFinishInstall() {
	initTokens()
	dynamicPage(name: "pageFinishInstall", title: "", install: true) {
		section() {
			paragraph "Excellent! You are now ready to use ${handle()}"
        }
        section("Note") {
            paragraph "After you tap Done, go to the Automation tab, select the SmartApps section, and open the '${app.label}' SmartApp to access the dashboard.", required: true
            paragraph "You can also access the dashboard on any another device by entering ${domain()} in the address bar of your browser.", required: true
        }
        section() {
            paragraph "Now tap Done and enjoy ${handle()}!"
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

		section("Available contacts") {
			if (location.getContactBookEnabled()) {
				href "pageSelectContacts", title: "Available contacts", description: "Tap here to select which contacts are available to pistons" 
			} else {
            	paragraph "Your contact book is not enabled."
            }
		}

		section("Integrations") {
			href "pageIntegrations", title: "Integrations with other services", description: "Tap here to configure your integrations" 
		}

		section("Security") {
			href "pageChangePassword", title: "Security", description: "Tap here to change your dashboard security settings" 
		}

		section(title: "Logging") {
			input "logging", "enum", title: "Logging level", options: ["None", "Minimal", "Medium", "Full"], description: "Logs will be available in your dashboard if this feature is enabled", defaultValue: "None", required: false
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


def pageIntegrations() {
    //clear devices cache
	dynamicPage(name: "pageIntegrations", title: "", install: false, uninstall: false) {
        def twilio = settings.twilio_sid && settings.twilio_token && settings.twilio_number
		section() {
			paragraph "Integrate other services into webCoRE to extend its capabilities."
		}
		section("Available integrations") {
			href "pageIntegrationAskAlexa", title: "Ask Alexa", description: "Allow interactions with AskAlexa"
			href "pageIntegrationIFTTT", title: "IFTTT", description: "Allow IFTTT interactions with external services"
			href "pageIntegrationTwilio", title: "Twilio", description: "Allows two-way SMS interactions", state: twilio ? 'complete' : null, required: twilio
		}
	}
}


def pageIntegrationIFTTT() {
	return dynamicPage(name: "pageIntegrationIFTTT", title: "IFTTT Integration", nextPage: settings.iftttEnabled ? "pageIntegrationIFTTTConfirm" : null) {
		section() {
			paragraph "CoRE can optionally integrate with IFTTT (IF This Then That) via the Maker channel, triggering immediate events to IFTTT. To enable IFTTT, please login to your IFTTT account and connect the Maker channel. You will be provided with a key that needs to be entered below", required: false
			input "iftttEnabled", "bool", title: "Enable IFTTT", submitOnChange: true, required: false
			if (settings.iftttEnabled) href name: "", title: "IFTTT Maker channel", required: false, style: "external", url: "https://www.ifttt.com/maker", description: "tap to go to IFTTT and connect the Maker channel"
		}
		if (settings.iftttEnabled) {
			section("IFTTT Maker key"){
				input("iftttKey", "string", title: "Key", description: "Your IFTTT Maker key", required: false)
			}
		}
	}
}

def pageIntegrationIFTTTConfirm() {
	if (testIFTTT()) {
		return dynamicPage(name: "pageIntegrationIFTTTConfirm", title: "IFTTT Integration") {
			section(){
				paragraph "Congratulations! You have successfully connected CoRE to IFTTT."
			}
		}
	} else {
		return dynamicPage(name: "pageIntegrateIFTTTConfirm",  title: "IFTTT Integration") {
			section(){
				paragraph "Sorry, the credentials you provided for IFTTT are invalid. Please go back and try again."
			}
		}
	}
}
def pageIntegrationTwilio() {
    //clear devices cache
	dynamicPage(name: "pageIntegrationTwilio", title: "Twilio", install: false, uninstall: false) {
		section() {
			paragraph "Twilio allows two-way messaging between you and webCoRE, bringing interactivity to your automations."
			paragraph "NOTE: Usage charges apply to your Twilio account and possibly your mobile phone bill.", required: true
		}
        section() {
        	paragraph "You will need to setup a Twilio account, purchase a number, and configure a Messaging Service to get this intergration working."
			href "", title: "How to configure your Twilio account", style: "external", url: "${getWikiUrl()}Twilio", description: "Tap to open", required: false
        }
		section("Twilio settings") {
        	paragraph "Login to your Twilio and go to your console. Find the Account SID and the Auth Token and copy and paste them below:"
	        input "twilio_sid", "password", title: "Twilio account SID", required: true
	        input "twilio_token", "password", title: "Twilio authorization token", required: true
	        input "twilio_number", "text", title: "Twilio phone number (+E.164 format)", required: true, defaultValue: "+"
		}
        
        section("Test your settings") {
        	paragraph "Once you have provided all details, test your integration here"
	        input "twilio_test_number", "text", title: "Your mobile phone number (+E.164 format)", defaultValue: "+"
	        input "twilio_test_message", "text", title: "A test message", defaultValue: "This is a test message from webCoRE"
			href "pageIntegrationTwilioTest", title: "Test your Twilio account"
        }
	}
}

def pageIntegrationTwilioTest() {
	def data = [
    	s: settings.twilio_sid,
        t: settings.twilio_token,
        n: settings.twilio_number,
        p: settings.twilio_test_number,
        m: settings.twilio_test_message
    ]
	def requestParams = [
		uri:  "https://api.webcore.co/sms/send/",
		query: null,
		requestContentType: "application/json",
		body: data
	]
    def success = false
	httpPost(requestParams) { response ->
    	if (response.status == 200) {
			def jsonData = response.data instanceof Map ? response.data : new groovy.json.JsonSlurper().parseText(response.data)
            if (jsonData && (jsonData.result == 'OK')) {
            	success = true
            }
        }
	}
	dynamicPage(name: "pageIntegrationTwilioTest", title: "Twilio Test", install: false, uninstall: false) {
        section("Test result") {
            if (success) {
                paragraph "Congratulations! Your Twilio account is correctly setup."
            } else {
                paragraph "Oh-oh, something unexpected happened. Please check your settings and try again.", required: true
            }
        }
    }
}

def pageRemove() {
	dynamicPage(name: "pageRemove", title: "", install: false, uninstall: true) {
		section('CAUTION') {
			paragraph "You are about to completely remove ${handle()} and all of its pistons.", required: true
            paragraph "This action is irreversible.", required: true
            paragraph "If you are sure you want to do this, please tap on the Remove button below.", required: true
		}
	}
}






/******************************************************************************/
/*** 																		***/
/*** INITIALIZATION ROUTINES												***/
/*** 																		***/
/******************************************************************************/


private installed() {
	state.installed = true
	initialize()
	return true
}

private updated() {
	unsubscribe()
	initialize()
	return true
}

private initialize() {
	subscribeAll()
    state.vars = state.vars ?: [:]
    ping()
}

private initializeWebCoREEndpoint() {
	try {
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
	} catch (all) {
    	log.error "An error has occurred during endpoint initialization: ", all
    }
    return false
}

private subscribeAll() {
	subscribe(location, handle(), webCoREHandler)
	subscribe(location, "askAlexa", askAlexaHandler)
	subscribe(location, "echoSistant", echoSistantHandler)    
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
	path("/intf/dashboard/variable/set") {action: [GET: "api_intf_variable_set"]}
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
    def virtualDevices = virtualDevices()
    atomicState.virtualDevices = virtualDevices
	return [
        name: location.name + ' \\ ' + (app.label ?: app.name),
        instance: [
	    	account: [id: hashId(app.getAccountId())],
        	pistons: getChildApps().sort{ it.label }.collect{ [ id: hashId(it.id), 'name': it.label, 'meta': state[hashId(it.id)] ] },
            id: hashId(app.id),
            locationId: hashId(location.id),
            name: app.label ?: app.name,
            uri: atomicState.endpoint,
            deviceVersion: currentDeviceVersion,
            coreVersion: version(),
            logging: settings.logging,
            virtualDevices: virtualDevices,
            globalVars: listAvailableVariables(),
            contacts: listAvailableContacts(),
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
    //debug "Dashboard: Request received to initialize instance"
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
    	def piston = addChildApp("ady624", "${handle()} Piston", params.name?:generatePistonName())
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
            	result.data = piston.get() ?: [:]
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
        	if (saved.rtData) {
            	updateRunTimeData(saved.rtData)
                saved.rtData = null
            }
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
                    if (saved.rtData) {
                        updateRunTimeData(saved.rtData)
                        saved.rtData = null
                    }
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
        	def rtData = piston.pause()
            updateRunTimeData(rtData)
            //update the state because it will overwrite the atomicState
            state[piston.id] = atomicState[piston.id]
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
        	def rtData = piston.resume()
            result = rtData.result
            updateRunTimeData(rtData)
            //update the state because it will overwrite the atomicState
            state[piston.id] = atomicState[piston.id]
			result.status = "ST_SUCCESS"
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

private api_intf_variable_set() {
	def result
    debug "Dashboard: Request received to set a variable"
	if (verifySecurityToken(params.token)) {
    	def name = params.name;
        def value = params.value ? new groovy.json.JsonSlurper().parseText(new String(params.value.decodeBase64(), "UTF-8")) : null        
        Map globalVars = atomicState.vars ?: [:]
        if (name && !value) {
        	//deleting a variable
            globalVars.remove(name);
        } else if (value && value.n) {
        	if (!name || (name != value.n)) {
	        	//add a new variable
                if (name) globalVars.remove(name);
    	        globalVars[value.n] = [t: value.t, v: value.v]
            } else {
                //update a variable
                globalVars[name] = [t: value.t, v: value.v]
            }
			sendVariableEvent([name: value.n, value: value.v, type: value.t])
		}
        atomicState.vars = globalVars
		result = [status: "ST_SUCCESS"] + [globalVars: globalVars]
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
			result = [status: "ST_SUCCESS", activity: (piston.activity(params.log) ?: [:]) + [globalVars: listAvailableVariables()]]
        } else {
	    	result = api_get_error_result("ERR_INVALID_ID")
        }
	} else {
    	result = api_get_error_result("ERR_INVALID_TOKEN")
    }
    render contentType: "application/javascript;charset=utf-8", data: "${params.callback}(${result.encodeAsJSON()})"
}

def api_ifttt() {
	def data = request?.JSON
	def eventName = params?.eventName
	if (eventName) {
		sendLocationEvent([name: "ifttt", value: eventName, isStateChange: true, linkText: "IFTTT event", descriptionText: "${handle()} has received an IFTTT event: $eventName", data: data])
	}
	render contentType: "text/html", data: "<!DOCTYPE html><html lang=\"en\">Received event $eventName.<body></body></html>"
}
















/******************************************************************************/
/*** 																		***/
/*** PRIVATE METHODS														***/
/*** 																		***/
/******************************************************************************/

private String getDashboardInitUrl(register = false) {
	def url = register ? getDashboardRegistrationUrl() : getDashboardUrl()
    if (!url) return null
    return url + (register ? "register/" : "init/") + (apiServerUrl("").replace("https://", '').replace(".api.smartthings.com", "").replace(":443", "").replace("/", "") + (state.accessToken + app.id).replace("-", "")).bytes.encodeBase64()
}

private String getDashboardRegistrationUrl() {
	if (!state.endpoint) return null
	return "https://api.${domain()}/dashboard/"
}

private Map listAvailableDevices(raw = false) {
	//long t = now()
	if (raw) {
    	return settings.findAll{ it.key.startsWith("dev:") }.collect{ it.value }.flatten().collectEntries{ dev -> [(hashId(dev.id)): dev]}
    } else {
    	return settings.findAll{ it.key.startsWith("dev:") }.collect{ it.value }.flatten().collectEntries{ dev -> [(hashId(dev.id)): dev]}.collectEntries{ id, dev -> [ (id): [ n: dev.getDisplayName(), cn: dev.getCapabilities()*.name, a: dev.getSupportedAttributes().unique{ it.name }.collect{def x = [n: it.name, t: it.getDataType(), o: it.getValues()]; try {x.v = dev.currentValue(x.n);} catch(all) {}; x}, c: dev.getSupportedCommands().unique{ it.getName() }.collect{[n: it.getName(), p: it.getArguments()]} ]]}
	}
/*    def devices = [:]
    for (devs in settings.findAll{ it.key.startsWith("dev:") }) {
        for(dev in devs.value) {
        	def devId = hashId(dev.id);
        	if (!devices[devId]) {
            	if (raw) {
                    devices[devId] = dev
                } else {
                	devices[devId] = [n: dev.getDisplayName(), cn: dev.getCapabilities()*.name, a: dev.getSupportedAttributes().unique{ it.getName() }.collect{def x = [n: it.getName(), t: it.getDataType(), o: it.getValues()]; try {x.v = dev.currentValue(x.n);} catch(all) {}; x}, c: dev.getSupportedCommands().unique{ it.getName() }.collect{[n: it.getName(), p: it.getArguments()]}]
                }
            }
        }
    }
    log.error "Time for devices: ${now() - t}ms"
    return devices*/
}


private Map listAvailableContacts(raw = false) {
    def contacts = [:]
    for(contact in settings.contacts) {
        def contactId = hashId(contact.id);
        if (raw) {
            contacts[contactId] = contact
        } else {
            contacts[contactId] = [t: contact.name, f: contact.contact.firstName, l: contact.contact.lastName, p: "$contact".endsWith('PUSH')]
        }
    }
    return contacts
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

private ping() {
	sendLocationEvent( [name: handle(), value: 'ping', isStateChange: true, displayed: false, linkText: "${handle()} ping reply", descriptionText: "${handle()} has received a ping reply and is replying with a pong", data: [id: hashId(app.id), name: app.label]] )
}

private getLogging() {
	def logging = settings.logging
	return [
        error: true,
        warn: true,
        info: (logging != 'None'),
        trace: (logging == 'Medium') || (logging == 'Full'),
        debug: (logging == 'Full')
    ]
}


private testIFTTT() {
	//setup our security descriptor
    state.modules = state.modules ?: [:]
	state.modules["IFTTT"] = [
		key: settings.iftttKey,
		connected: false
	]
	if (settings.iftttKey) {
		//verify the key
		return httpGet("https://maker.ifttt.com/trigger/test/with/key/" + settings.iftttKey) { response ->
			if (response.status == 200) {
				if (response.data == "Congratulations! You've fired the test event")
					state.modules["IFTTT"].connected = true
				return true;
			}
			return false;
		}
	}
	return false
}

private testLIFX() {
	if ((!settings.lifxToken) || (!settings.lifxEnabled)) return false
	//setup our security descriptor
	state.modules["LIFX"] = [
		token: settings.lifxToken,
		connected: false
	]
	if (settings.lifxToken) {
		//verify the key
		def requestParams = [
			uri:  "https://api.lifx.com",
			path: "/v1/scenes",
			headers: [
				"Authorization": "Bearer ${settings.lifxToken}"
			],
			requestContentType: "application/json"
		]
		try {
			return httpGet(requestParams) { response ->
				if (response.status == 200) {
					if (response.data instanceof List) {
						state.modules["LIFX"].connected = true
						def ss = []
						for(scene in response.data) {
							def s = [
								id: scene.uuid,
								name: scene.name
							]
							ss.push(s)
						}
						state.modules["LIFX"].scenes = ss
					}
					return true;
				}
				return false;
			}
		}
		catch(all) {
			return false
		}
	}
	return false
}


/******************************************************************************/
/*** 																		***/
/*** PUBLIC METHODS															***/
/*** 																		***/
/******************************************************************************/
public Boolean isInstalled() {
	return !!state.installed
}

public String getDashboardUrl() {
	if (!state.endpoint) return null
	return "https://dashboard.${domain()}/"
}

public String getWikiUrl() {
	return "https://wiki.${domain()}/"
}
public String mem(showBytes = true) {
	def bytes = atomicState.toString().length()
	return Math.round(100.00 * (bytes/ 100000.00)) + "%${showBytes ? " ($bytes bytes)" : ""}"
}

public Map getRunTimeData(semaphore) {
    def startTime = now()
    semaphore = semaphore ?: 0
    def semaphoreDelay = 0
    def semaphoreName = "sph$semaphore"
    def waited = false
    //if we need to wait for a semaphore, we do it here
    while (semaphore) {
        def lastSemaphore = atomicState[semaphoreName] ?: 0
        if (!lastSemaphore || (now() - lastSemaphore > 10000)) {
        	semaphoreDelay = waited ? now() - startTime : 0
            semaphore = now()
            atomicState[semaphoreName] = semaphore
            break
        }
        waited = true
    	pause(250)
    }
    return [
        logging: getLogging(),
    	attributes: attributes(),
        semaphore: semaphore,
        semaphoreName: semaphoreName,
        semaphoreDelay: semaphoreDelay,
        commands: commands(),
		commands: [
        	physical: commands(),
			virtual: virtualCommands()
		],
        comparisons: comparisons(),
        coreVersion: version(),
    	contacts: listAvailableContacts(true),
    	devices: listAvailableDevices(true),
        virtualDevices: atomicState.virtualDevices ?: virtualDevices(),
        globalVars: listAvailableVariables()
    ]
}

public void updateRunTimeData(data) {
	if (!data || !data.id) return
	List variableEvents = []
    if (data && data.gvCache) {
	Map vars = atomicState.vars ?: [:]
    def modified = false
    	for(var in data.gvCache) {
        	if (var.key && var.key.startsWith('@') && (vars[var.key]) && (var.value.v != vars[var.key].v)) {
            	variableEvents.push([name: var.key, oldValue: vars[var.key].v, value: var.value.v, type: var.value.t])
            	vars[var.key].v = var.value.v
                modified = true
            }
        }
        if (modified) {
            atomicState.vars = vars
        }
	}
    def id = data.id
    Map piston = [
    	a: data.active,
        t: now(), //last run
        n: data.stats.nextSchedule,
        z: data.piston.z, //description
        s: data.state, //state
    ]
    atomicState[id] = piston

    //broadcast variable change events
    for (variable in variableEvents) {
        sendVariableEvent(variable)
    }
    //release semaphores
	if (data.semaphoreName && (atomicState[data.semaphoreName] <= data.semaphore)) {
    	//release the semaphore
        atomicState[data.semaphoreName] = 0
    }
}

public pausePiston(pistonId) {
    def piston = getChildApps().find{ hashId(it.id) == pistonId };
	if (piston) {
		def rtData = piston.pause()
		updateRunTimeData(rtData)
    }
}

public resumePiston(pistonId) {
    def piston = getChildApps().find{ hashId(it.id) == pistonId };
	if (piston) {
		def rtData = piston.resume()
		updateRunTimeData(rtData)
    }
}

private sendVariableEvent(variable) {
	sendLocationEvent( [name: handle(), value: variable.name, isStateChange: true, displayed: false, linkText: "${handle()} global variable ${variable.name} changed", descriptionText: "${handle()} global variable ${variable.name} changed", data: [id: hashId(app.id), name: app.label, event: 'variable', variable: variable]])
}

def webCoREHandler(event) {
    if (!event || (event.name != handle())) return;
    def data = event.jsonData ?: null
    if (data && data.variable && (data.event == 'variable') && event.value && event.value.startsWith('@')) {
    	Map vars = atomicState.vars ?: [:]
        Map variable = data.variable
        def oldVar = vars[variable.name] ?: [t:'', v:'']
        if ((oldVar.t != variable.type) || (oldVar.v != variable.value)) {
	        vars[variable.name] = [t: variable.type ? variable.type : 'dynamic', v: variable.value]
            atomicState.vars = vars
        }
        return;
    }    
    switch (event.value) {
    	case 'ping':
        	if (data && data.id && data.name && (data.id != hashId(app.id))) {
        		sendLocationEvent( [name: handle(), value: 'pong', isStateChange: true, displayed: false, linkText: "${handle()} ping reply", descriptionText: "${handle()} has received a ping reply and is replying with a pong", data: [id: hashId(app.id), name: app.label]] )
            } else {
        		break;
            }
			//fall through to pong
    	case 'pong':
        	if (data && data.id && data.name && (data.id != hashId(app.id))) {
        		def pong = atomicState.pong ?: [:]
            	pong[data.id] = data.name
                atomicState.pong = pong
			}
        	break;
    }
}

def askAlexaHandler(evt) {
	if (!evt) return
	switch (evt.value) {
		case "refresh":
        	Map macros = [:]
			for(macro in (evt.jsonData && evt.jsonData?.macros ? evt.jsonData.macros : [])) {
            	if (macro instanceof Map) {
                	macros[hashId(macro.id)] = macro.name
                } else {
                	macros[hashId(macro)] = macro;
                }
            }
            atomicState.askAlexaMacros = macros
			break
	}
}

def echoSistantHandler(evt) {
	if (!evt) return
	switch (evt.value) {
		case "refresh":
        	Map profiles = [:]
			for(profile in (evt.jsonData && evt.jsonData?.profiles ? evt.jsonData.profiles : [])) {
            	if (profile instanceof Map) {
                	profiles[hashId(profile.id)] = profile.name
                } else {
                	profiles[hashId(profile)] = profile;
                }
            }
			atomicState.echoSistantProfiles = profiles
			break
	}
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
		thermostat					: [ n: "Thermostat",					d: "thermostats",					a: "thermostatMode",					c: ["auto", "cool", "emergencyHeat", "fanAuto", "fanCirculate", "fanOn", "heat", "off", "setCoolingSetpoint", "setHeatingSetpoint", "setSchedule", "setThermostatFanMode", "setThermostatMode"],	],
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
		axisX						: [ n: "X axis",				t: "integer",	r: [-1024, 1024],	s: "threeAxis",																	],
		axisY						: [ n: "Y axis",				t: "integer",	r: [-1024, 1024],	s: "threeAxis",																	],
		axisZ						: [ n: "Z axis",				t: "integer",	r: [-1024, 1024],	s: "threeAxis",																	],
		battery						: [ n: "battery", 				t: "integer",	r: [0, 100],		u: "%",																			],
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
		steps						: [ n: "steps",					t: "integer",	r: [0, null],																						],
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
		configure					: [ n: "Configure",						i: 'gear',																																																										],
		cool						: [ n: "Set to Cool",					i: 'asterisk',									a: "thermostatMode",				v: "cool",																																			],
		deviceNotification			: [ n: "Send device notification...",	d: "Send device notification \"{0}\"",																		p: [[n:"Message",t:"string"]],  																							],
		emergencyHeat				: [ n: "Set to Emergency Heat",															a: "thermostatMode",				v: "emergencyHeat",																																	],
		fanAuto						: [ n: "Set fan to Auto",																a: "thermostatFanMode",				v: "auto",																																			],
		fanCirculate				: [ n: "Set fan to Circulate",															a: "thermostatFanMode",				v: "circulate",																																		],
		fanOn						: [ n: "Set fan to On",																	a: "thermostatFanMode",				v: "on",																																			],
		getAllActivities			: [ n: "Get all activities",																																																													],
		getCurrentActivity			: [ n: "Get current activity",																																																													],
		heat						: [ n: "Set to Heat",					i: 'fire',										a: "thermostatMode",				v: "heat",																																			],
		indicatorNever				: [ n: "Disable indicator",																																																														],
		indicatorWhenOff			: [ n: "Enable indicator when off",																																																												],
		indicatorWhenOn				: [ n: "Enable indicator when on",																																																												],
		lock						: [ n: "Lock",							i: "lock",										a: "lock",							v: "locked",																																		],
		mute						: [ n: "Mute",							i: 'volume-off',								a: "mute",							v: "muted",																																			],
		nextTrack					: [ n: "Next track",																																																															],
		off							: [ n: "Turn off",						i: "circle-o-notch",							a: "switch|alarm|thermostatMode",	v: "off",																																			],
		on							: [ n: "Turn on",						i: "power-off",									a: "switch",						v: "on",																																			],
		open						: [ n: "Open",																			a: "door|valve|windowShade",		v: "open",																																			],
		pause						: [ n: "Pause",																																																																	],
		play						: [ n: "Play",																																																																	],
		playText					: [ n: "Speak text...",					d: "Speak text \"{0}\"",																					p: [[n:"Text",t:"string"]],  													],
		playTextAndRestore			: [ n: "Speak text and restore...",		d: "Speak text \"{0}\" and restore",																		p: [[n:"Text",t:"string"]],  													],
		playTextAndResume			: [ n: "Speak text and resume...",		d: "Speak text \"{0}\" and resume",																		p: [[n:"Text",t:"string"]],  													],
		playTrack					: [ n: "Play track...",					d: "Play track <uri>{0}</uri>{1}",																			p: [[n:"Track URL",t:"url"], [n:"Volume", t:"level", d:" at volume {v}"]],  												],
		playTrackAndRestore			: [ n: "Play track...",					d: "Play track <uri>{0}</uri>{1} and restore",																p: [[n:"Track URL",t:"url"], [n:"Volume", t:"level", d:" at volume {v}"]],  												],
		playTrackAndResume			: [ n: "Play track...",					d: "Play track <uri>{0}</uri>{1} and resume",																p: [[n:"Track URL",t:"url"], [n:"Volume", t:"level", d:" at volume {v}"]],  												],
		poll						: [ n: "Poll",						i: 'question',																																																											],
		presetPosition				: [ n: "Move to preset position",														a: "windowShade",					v: "partially open",																																],
		previousTrack				: [ n: "Previous track",																																																														],
		push						: [ n: "Push",																																																																	],
		refresh						: [ n: "Refresh",					i: 'refresh',																																																											],
		restoreTrack				: [ n: "Restore track...",				d: "Restore track <uri>{0}</uri>",																			p: [[n:"Track URL",t:"url"]],  																								],
		resumeTrack					: [ n: "Resume track...",				d: "Resume track <uri>{0}</uri>",																			p: [[n:"Track URL",t:"url"]],  																								],
		setColor					: [ n: "Set color...",				i: 'barcode',	d: "Set color to {0}{1}",						a: "color",													p: [[n:"Color",t:"color"], [n:"Only if switch is...", t:"enum",o:["on","off"], d:" if already {v}"]],  							],
		setColorTemperature			: [ n: "Set color temperature...",		d: "Set color temperature to {0}°K{1}",			a: "colorTemperature",										p: [[n:"Color Temperature", t:"colorTemperature"], [n:"Only if switch is...", t:"enum",o:["on","off"], d:" if already {v}"]],	],
		setConsumableStatus			: [ n: "Set consumable status...",		d: "Set consumable status to {0}",																			p: [[n:"Status", t:"consumable"]],																							],
		setCoolingSetpoint			: [ n: "Set cooling point...",			d: "Set cooling point at {0}{T}",				a: "thermostatCoolingSetpoint",								p: [[n:"Desired temperature", t:"thermostatSetpoint"]], 																	],
		setHeatingSetpoint			: [ n: "Set heating point...",			d: "Set heating point at {0}{T}",				a: "thermostatHeatingSetpoint",								p: [[n:"Desired temperature", t:"thermostatSetpoint"]], 																	],
		setHue						: [ n: "Set hue...",				i: 'barcode',	d: "Set hue to {0}°{1}",			a: "hue",													p: [[n:"Hue", t:"hue"], [n:"Only if switch is...", t:"enum",o:["on","off"], d:" if already {v}"]], 								],
		setInfraredLevel			: [ n: "Set infrared level...",		i: 'signal',	d: "Set infrared level to {0}%{1}",	a: "infraredLevel",											p: [[n:"Level",t:"infraredLevel"], [n:"Only if switch is...", t:"enum",o:["on","off"], d:" if already {v}"]], 					],
		setLevel					: [ n: "Set level...",				i: 'signal',	d: "Set level to {0}%{1}",			a: "level",													p: [[n:"Level",t:"level"], [n:"Only if switch is...", t:"enum",o:["on","off"], d:" if already {v}"]], 							],
		setSaturation				: [ n: "Set saturation...",				d: "Set saturation to {0}{1}",					a: "saturation",											p: [[n:"Saturation", t:"saturation"], [n:"Only if switch is...", t:"enum",o:["on","off"], d:" if already {v}"]],					],
		setSchedule					: [ n: "Set thermostat schedule...",	d: "Set schedule to {0}",						a: "schedule",												p: [[n:"Schedule", t:"object"]],																							],
		setThermostatFanMode		: [ n: "Set fan mode...",				d: "Set fan mode to {0}",						a: "thermostatFanMode",										p: [[n:"Fan mode", t:"thermostatFanMode"]],																					],
		setThermostatMode			: [ n: "Set thermostat mode...",		d: "Set thermostat mode to {0}",				a: "thermostatMode",										p: [[n:"Thermostat mode",t:"thermostatMode"]],																				],
		setTimeRemaining			: [ n: "Set remaining time...",			d: "Set remaining time to {0}s",				a: "timeRemaining",											p: [[n:"Remaining time [seconds]", t:"number"]],																						],
		setTrack					: [ n: "Set track...",					d: "Set track to <uri>{0}</uri>",																			p: [[n:"Track URL",t:"url"]], 																								],
		siren						: [ n: "Siren",																			a: "alarm",							v: "siren",																																			],
		speak						: [ n: "Speak...",						d: "Speak \"{0}\"",																							p: [[n:"Message", t:"string"]],																								],
		start						: [ n: "Start",																																																																	],
		startActivity				: [ n: "Start activity...",				d: "Start activity \"{0}\"",																				p: [[n:"Activity", t:"string"]],																							],
		stop						: [ n: "Stop",																																																																	],
		strobe						: [ n: "Strobe",																		a: "alarm",							v: "strobe",																																		],
		take						: [ n: "Take a picture",																																																														],
		unlock						: [ n: "Unlock",					i: 'unlock-alt',									a: "lock",							v: "unlocked",																																		],
		unmute						: [ n: "Unmute",					i: 'volume-up',										a: "mute",							v: "unmuted",																																		],
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
		setAdjustedColor			: [ n: "Transition to color...",		d: "Transition to color {0} in {1}{2}",																		p: [[n:"Color", t:"color"], [n:"Duration",t:"duration"],[n:"Only if switch is...", t:"enum",o:["on","off"], d:" if already {v}"]],																	],
		setAdjustedHSLColor			: [ n: "Transition to HSL color...",		d: "Transition to color H:{0}° / S:{1}% / L:{2}% in {3}{4}",												p: [[n:"Hue", t:"hue"],[n:"Saturation", t:"saturation"],[n:"Level", t:"level"],[n:"Duration",t:"duration"],[n:"Only if switch is...", t:"enum",o:["on","off"], d:" if already {v}"]],																	],
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
		noop						: [	n: "No operation",				a: true,	i: 'circle',				d: "No operation",																										],
		wait						: [	n: "Wait...", 					a: true,	i: "clock-o",				d: "Wait {0}",															p: [[n:"Duration", t:"duration"]],				],
		waitRandom					: [ n: "Wait randomly...",			a: true,	i: "clock-o",				d: "Wait randomly between {0} and {1}",									p: [[n:"At least", t:"duration"],[n:"At most", t:"duration"]],	],
		waitForTime					: [ n: "Wait for time...",			a: true,	i: "clock-o",				d: "Wait for {0}",														p: [[n:"Time", t:"time"]],	],
		waitForDateTime				: [ n: "Wait for date & time...",	a: true,	i: "clock-o",				d: "Wait for {0}",														p: [[n:"Date & Time", t:"datetime"]],	],
		executePiston				: [ n: "Execute piston...",			a: true,	i: "clock-o",				d: "Execute piston \"{0}\"",											p: [[n:"Piston", t:"piston"], [n:"Arguments", t:"variables", d:" with arguments {v}"]],	],
		pausePiston					: [ n: "Pause piston...",			a: true,	i: "clock-o",				d: "Pause piston \"{0}\"",												p: [[n:"Piston", t:"piston"]],	],
		resumePiston				: [ n: "Resume piston...",			a: true,	i: "clock-o",				d: "Resume piston \"{0}\"",												p: [[n:"Piston", t:"piston"]],	],
		executeRoutine				: [ n: "Execute routine...",		a: true,	i: "clock-o",				d: "Execute routine \"{0}\"",											p: [[n:"Routine", t:"routine"]],	],
		toggle						: [ n: "Toggle", r: ["on", "off"], 				i: "toggle-on"																				],
		setHSLColor					: [ n: "Set color... (hsl)", 					i: "barcode",			d: "Set color to H:{0}° / S:{1}% / L%:{2}{3}",				r: ["setColor"],				p: [[n:"Hue",t:"hue"], [n:"Saturation",t:"saturation"], [n:"Level",t:"level"], [n:"Only if switch is...", t:"enum",o:["on","off"], d:" if already {v}"]],  							],
		toggleLevel					: [ n: "Toggle level...", 						i: "toggle-off",			d: "Toggle level between 0% and {0}%",	r: ["on", "off", "setLevel"],	p: [[n:"Level", t:"level"]],																																	],
		sendNotification			: [ n: "Send notification...",		a: true,	i: "commenting-o",			d: "Send notification \"{0}\"",											p: [[n:"Message", t:"string"]],												],
		sendPushNotification		: [ n: "Send PUSH notification...",	a: true,	i: "commenting-o",			d: "Send PUSH notification \"{0}\"{1}",									p: [[n:"Message", t:"string"],[n:"Store in Messages", t:"boolean", d:" and store in Messages", s:1]],	],
		sendSMSNotification			: [ n: "Send SMS notification...",	a: true,	i: "commenting-o",			d: "Send SMS notification \"{0}\" to {1}{2}",							p: [[n:"Message", t:"string"],[n:"Phone number",t:"phone"],[n:"Store in Messages", t:"boolean", d:" and store in Messages", s:1]],	],
		sendNotificationToContacts	: [ n: "Send notification to contacts...",a: true,i: "commenting-o",		d: "Send notification \"{0}\" to {1}{2}",								p: [[n:"Message", t:"string"],[n:"Contacts",t:"contacts"],[n:"Store in Messages", t:"boolean", d:" and store in Messages", s:1]],	],
		log							: [ n: "Log to console...",			a: true,	i: "bug",					d: "Log {0} \"{1}\"",													p: [[n:"Log type", t:"enum", o:["info","trace","debug","warn","error"]],[n:"Message",t:"string"],[n:"Store in Messages", t:"boolean", d:" and store in Messages", s:1]],	],
		httpRequest					: [ n: "Make a web request",		a: true, 	i: "anchor",				d: "Make a {1} request to {0} with type {2}{3}",				        p: [[n:"URL", t:"string"],[n:"Method", t:"enum", o:["GET","POST","PUT","DELETE","HEAD"]],[n:"Content Type", t:"enum", o:["JSON","FORM"]],[n:"Send variables", t:"variables", d:" and data {v}"]],	],
        setVariable					: [ n: "Set variable...",			a: true,	i: "superscript",			d: "Set variable {0} = {1}",											p: [[n:"Variable",t:"variable"],[n:"Value", t:"dynamic"]],	],
        setState					: [ n: "Set piston state...",		a: true,	i: "superscript",			d: "Set piston state to \"{0}\"",										p: [[n:"State",t:"string"]],	],
		setLocationMode				: [ n: "Set location mode...",		a: true,	i: "", 						d: "Set location mode to {0}", 											p: [[n:"Mode",t:"mode"]],																														],
		setAlarmSystemStatus		: [ n: "Set Smart Home Monitor status...",	a: true, i: "",					d: "Set Smart Home Monitor status to {0}",								p: [[n:"Status", t:"alarmSystemStatus"]],																										],
		sendEmail					: [ n: "Send email...",				a: true,	i: "envelope", 				d: "Send email with subject \"{1}\" to {0}", 							p: [[n:"Recipient",t:"email"],[n:"Subject",t:"string"],[n:"Message body",t:"string"]],																							],
        wolRequest					: [ n: "Wake a LAN device", 		a: true,	i: "", 						d: "Wake LAN device at address {0}{1}",									p: [[n:"MAC address",t:"string"],[n:"Secure code",t:"string",d:" with secure code {v}"]],	],


/*		[ n: "waitState",											d: "Wait for piston state change",	p: ["Change to:enum[any,false,true]"],															i: true,	l: true,						dd: "Wait for {0} state"],
		[ n: "fadeLevelHW",			r: ["setLevel"], 				d: "Fade to level (hardware)",		p: ["Target level:level","Duration (ms):number[1..60000]"],																							dd: "Fade to {0}% in {1}ms",				],
		[ n: "fadeLevel",			r: ["setLevel"], 				d: "Fade to level",					p: ["?Start level (optional):level","Target level:level","Duration (seconds):number[1..600]"],															dd: "Fade level from {0}% to {1}% in {2}s",				],
		[ n: "adjustLevel",			r: ["setLevel"], 				d: "Adjust level",					p: ["Adjustment (+/-):number[-100..100]"],																												dd: "Adjust level by {0}%",	],
		[ n: "fadeSaturation",		r: ["setSaturation"],			d: "Fade to saturation",				p: ["?Start saturation (optional):saturation","Target saturation:saturation","Duration (seconds):number[1..600]"],											dd: "Fade saturation from {0}% to {1}% in {2}s",				],
		[ n: "adjustSaturation",		r: ["setSaturation"],		d: "Adjust saturation",				p: ["Adjustment (+/-):number[-100..100]"],																												dd: "Adjust saturation by {0}%",	],
		[ n: "fadeHue",				r: ["setHue"], 					d: "Fade to hue",						p: ["?Start hue (optional):hue","Target hue:hue","Duration (seconds):number[1..600]"],																dd: "Fade hue from {0}° to {1}° in {2}s",				],
		[ n: "adjustHue",			r: ["setHue"], 					d: "Adjust hue",						p: ["Adjustment (+/-):number[-360..360]"],																												dd: "Adjust hue by {0}°",	],
		[ n: "flash",				r: ["on", "off"], 				d: "Flash",							p: ["On interval (milliseconds):number[250..5000]","Off interval (milliseconds):number[250..5000]","Number of flashes:number[1..10]"],					dd: "Flash {0}ms/{1}ms for {2} time(s)",		],
		[ n: "saveState",		d: "Save state to variable",			p: ["Attributes:attributes","Aggregation:aggregation","?Convert to data t:dataType","Save to state variable:string"],			stateVarEntry: 3,	dd: "Save state of attributes {0} to variable |[{3}]|'",	aggregated: true,	],
		[ n: "saveStateLocally",	d: "Capture state to local store",	p: ["Attributes:attributes","?Only if state is empty:bool"],																															dd: "Capture state of attributes {0} to local store",		],
		[ n: "saveStateGlobally",d: "Capture state to global store",	p: ["Attributes:attributes","?Only if state is empty:bool"],																															dd: "Capture state of attributes {0} to global store",	],
		[ n: "loadState",		d: "Load state from variable",		p: ["Attributes:attributes","Load from state variable:stateVariable","Allow translations:bool","Negate translation:bool"],								dd: "Load state of attributes {0} from variable |[{1}]|"				],
		[ n: "loadStateLocally",	d: "Restore state from local store",	p: ["Attributes:attributes","?Empty the state:bool"],																															dd: "Restore state of attributes {0} from local store",			],
		[ n: "loadStateGlobally",d: "Restore state from global store",	p: ["Attributes:attributes","?Empty the state:bool"],																															dd: "Restore state of attributes {0} from global store",			],
		[ n: "queueAskAlexaMessage",d: "Queue AskAlexa message",			p: ["Message:text", "?Unit:text", "?Application:text"],																		l: true, dd: "Queue AskAlexa message '{0}' in unit {1}",aggregated: true,	],
		[ n: "deleteAskAlexaMessages",d: "Delete AskAlexa messages",			p: ["Unit:text", "?Application:text"],																	l: true, dd: "Delete AskAlexa messages in unit {1}",aggregated: true,	],
		[ n: "cancelPendingTasks",d: "Cancel pending tasks",			p: ["Scope:enum[Local,Global]"],																														dd: "Cancel all pending {0} tasks",		],
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
        	changed							: [ d: "changed",																	g:"bdis",						t: 1,	],
        	did_not_change					: [ d: "did not change",															g:"bdis",						t: 1,	],
    		is 								: [ d: "is",								dd: "are",								g:"bs",		p: 1						],
    		is_not	 						: [ d: "is not",							dd: "are not",							g:"bs",		p: 1						],
    		is_any_of 						: [ d: "is any of",							dd: "are any of",						g:"s",		p: 1,	m: true,			],
    		is_not_any_of 					: [ d: "is not any of",						dd: "are not any of",					g:"s",		p: 1,	m: true,			],
    		is_equal_to						: [ d: "is equal to",						dd: "are equal to",						g:"di",		p: 1						],
    		is_different_than				: [ d: "is different than",					dd: "are different than",				g:"di",		p: 1						],
    		is_less_than					: [ d: "is less than",						dd: "are less than",					g:"di",		p: 1						],
    		is_less_than_or_equal_to		: [ d: "is less than or equal to",			dd: "are less than or equal to",		g:"di",		p: 1						],
    		is_greater_than					: [ d: "is greater than",					dd: "are greater than",					g:"di",		p: 1						],
    		is_greater_than_or_equal_to		: [ d: "is greater than or equal to",		dd: "are greater than or equal to",		g:"di",		p: 1						],
    		is_inside_of_range				: [ d: "is inside of range",				dd: "are inside of range",				g:"di",		p: 2						],
    		is_outside_of_range				: [ d: "is outside of range",				dd: "are outside of range",				g:"di",		p: 2						],
			is_even							: [ d: "is even",							dd: "are even",							g:"di",									],
			is_odd							: [ d: "is odd",							dd: "are odd",							g:"di",									],
    		was 							: [ d: "was",								dd: "were",								g:"bs",		p: 1,				t: 2,	],
    		was_not 						: [ d: "was not",							dd: "were not",							g:"bs",		p: 1,				t: 2,	],
    		was_any_of 						: [ d: "was any of",						dd: "were any of",						g:"s",		p: 1,	m: true,	t: 2,	],
    		was_not_any_of 					: [ d: "was not any of",					dd: "were not any of",					g:"s",		p: 1,	m: true,	t: 2,	],
			was_equal_to 					: [ d: "was equal to",						dd: "were equal to",					g:"di",		p: 1,				t: 2,	],
			was_different_than 				: [ d: "was different than",				dd: "were different than",				g:"di",		p: 1,				t: 2,	],
			was_less_than 					: [ d: "was less than",						dd: "were less than",					g:"di",		p: 1,				t: 2,	],
			was_less_than_or_equal_to 		: [ d: "was less than or equal to",			dd: "were less than or equal to",		g:"di",		p: 1,				t: 2,	],
			was_greater_than 				: [ d: "was greater than",					dd: "were greater than",				g:"di",		p: 1,				t: 2,	],
			was_greater_than_or_equal_to 	: [ d: "was greater than or equal to",		dd: "were greater than or equal to",	g:"di",		p: 1,				t: 2,	],
			was_inside_of_range 			: [ d: "was inside of range",				dd: "were inside of range",				g:"di",		p: 2,				t: 2,	],
			was_outside_of_range 			: [ d: "was outside of range",				dd: "were outside of range",			g:"di",		p: 2,				t: 2,	],
    		was_even						: [ d: "was even",							dd: "were even",						g:"di",							t: 2,	],
    		was_odd							: [ d: "was odd",							dd: "were odd",							g:"di",							t: 2,	],
			is_any							: [ d: "is any",																	g:"t",		p: 0						],
			is_before						: [ d: "is before",																	g:"t",		p: 1						],
			is_after						: [ d: "is after",																	g:"t",		p: 1						],
			is_between						: [ d: "is between",																g:"t",		p: 2						],
			is_not_between					: [ d: "is not between",															g:"t",		p: 2						],
    	],
        triggers: [
    		gets							: [ d: "gets",																		g:"m",		p: 1						],
			happens_daily_at				: [ d: "happens daily at",															g:"t",		p: 1						],
    		executes						: [ d: "executes",																	g:"v",		p: 1						],
    		changes 						: [ d: "changes",							dd: "change",							g:"bdis",								],
    		changes_to 						: [ d: "changes to",						dd: "change to",						g:"bdis",	p: 1,						],
    		changes_away_from 				: [ d: "changes away from",					dd: "change away from",					g:"bdis",	p: 1,						],
    		changes_to_any_of 				: [ d: "changes to any of",					dd: "change to any of",					g:"dis",	p: 1,	m: true,			],
    		changes_away_from_any_of 		: [ d: "changes away from any of",			dd: "change away from any of",			g:"dis",	p: 1,	m: true,			],
            drops							: [ d: "drops",								dd: "drop",								g:"di",									],
            does_not_drop					: [ d: "does not drop",						dd: "do not drop",						g:"di",									],
            drops_below						: [ d: "drops below",						dd: "drop below",						g:"di",		p: 1,						],
            drops_to_or_below				: [ d: "drops to or below",					dd: "drop to or below",					g:"di",		p: 1,						],
            remains_below					: [ d: "remains below",						dd: "remains below",					g:"di",		p: 1,						],
            remains_below_or_equal_to		: [ d: "remains below or equal to",			dd: "remains below or equal to",		g:"di",		p: 1,						],
            rises							: [ d: "rises",								dd: "rise",								g:"di",									],
            does_not_rise					: [ d: "does not rise",						dd: "do not rise",						g:"di",									],
            rises_above						: [ d: "rises above",						dd: "rise above",						g:"di",		p: 1,						],
            rises_to_or_above				: [ d: "rises to or above",					dd: "rise to or above",					g:"di",		p: 1,						],
            remains_above					: [ d: "remains above",						dd: "remains above",					g:"di",		p: 1,						],
            remains_above_or_equal_to		: [ d: "remains above or equal to",			dd: "remains above or equal to",		g:"di",		p: 1,						],
            enters_range					: [ d: "enters range",						dd: "enter range",						g:"di",		p: 2,						],
            remains_outside_of_range		: [ d: "remains outside of range",			dd: "remain outside of range",			g:"di",		p: 2,						],
            exits_range						: [ d: "exits range",						dd: "exit range",						g:"di",		p: 2,						],
            remains_inside_of_range			: [ d: "remains inside of range",			dd: "remain inside of range",			g:"di",		p: 2,						],
			becomes_even					: [ d: "becomes even",						dd: "become even",						g:"di",									],
			remains_even					: [ d: "remains even",						dd: "remain even",						g:"di",									],
			becomes_odd						: [ d: "becomes odd",						dd: "become odd",						g:"di",									],
			remains_odd						: [ d: "remains odd",						dd: "remain odd",						g:"di",									],
    		stays_unchanged					: [ d: "stays unchanged",					dd: "stay unchanged",					g:"bdis",						t: 1,	],
    		stays	 						: [ d: "stays",								dd: "stay",								g:"bdis",	p: 1,				t: 1,	],
    		stays_away_from					: [ d: "stays away from",					dd: "stay away from",					g:"bdis",	p: 1,				t: 1,	],
    		stays_any_of					: [ d: "stays any of",						dd: "stay any of",						g:"dis",	p: 1,	m: true,	t: 1,	],
    		stays_away_from_any_of			: [ d: "stays away from any of",			dd: "stay away from any of",			g:"bdis",	p: 1,	m: true,	t: 1,	],
			stays_equal_to 					: [ d: "stays equal to",					dd: "stay equal to",					g:"di",		p: 1,				t: 1,	],
			stays_different_than			: [ d: "stays different than",				dd: "stay different than",				g:"di",		p: 1,				t: 1,	],
			stays_less_than 				: [ d: "stays less than",					dd: "stay less than",					g:"di",		p: 1,				t: 1,	],
			stays_less_than_or_equal_to 	: [ d: "stays less than or equal to",		dd: "stay less than or equal to",		g:"di",		p: 1,				t: 1,	],
			stays_greater_than 				: [ d: "stays greater than",				dd: "stay greater than",				g:"di",		p: 1,				t: 1,	],
			stays_greater_than_or_equal_to 	: [ d: "stays greater than or equal to",	dd: "stay greater than or equal to",	g:"di",		p: 1,				t: 1,	],
			stays_inside_of_range 			: [ d: "stays inside of range",				dd: "stay inside of range",				g:"di",		p: 2,				t: 1,	],
			stays_outside_of_range 			: [ d: "stays outside of range",			dd: "stay outside of range",			g:"di",		p: 2,				t: 1,	],
			stays_even						: [ d: "stays even",						dd: "stay even",						g:"di",							t: 1,	],
			stays_odd						: [ d: "stays odd",							dd: "stay odd",							g:"di",							t: 1,	],
        ]
	]
}

private static Map functions() {
	return [
      	age				: [ t: "integer",						],
      	previousage		: [ t: "integer",	d: "previousAge",	],
      	previousvalue	: [ t: "dynamic",	d: "previousValue",	],
      	newer			: [ t: "integer",						],
      	older			: [ t: "integer",						],
      	least			: [ t: "dynamic",						],
      	most			: [ t: "dynamic",						],
      	avg				: [ t: "decimal",						],
      	variance		: [ t: "decimal",						],
      	median			: [ t: "decimal",						],
      	stdev			: [ t: "decimal",						],
      	round			: [ t: "decimal",						],
      	ceil			: [ t: "decimal",						],
      	ceiling			: [ t: "decimal",						],
      	floor			: [ t: "decimal",						],
      	min				: [ t: "decimal",						],
      	max				: [ t: "decimal",						],
      	sum				: [ t: "decimal",						],
      	count			: [ t: "integer",						],
      	left			: [ t: "string",						],
      	right			: [ t: "string",						],
      	mid				: [ t: "string",						],
      	substring		: [ t: "string",						],
      	sprintf			: [ t: "string",						],
      	format			: [ t: "string",						],
      	string			: [ t: "string",						],
      	replace			: [ t: "string",						],
      	concat			: [ t: "string",						],
      	text			: [ t: "string",						],
      	lower			: [ t: "string",						],
      	upper			: [ t: "string",						],
      	title			: [ t: "string",						],
        int				: [ t: "integer",						],
        integer			: [ t: "integer",						],
        float			: [ t: "decimal",						],
        decimal			: [ t: "decimal",						],
        number			: [ t: "decimal",						],
        bool			: [ t: "boolean",						],
        boolean			: [ t: "boolean",						],
        power			: [ t: "decimal",						],
        sqr				: [ t: "decimal",						],
        sqrt			: [ t: "decimal",						],
        dewpoint		: [ t: "decimal",	d: "dewPoint",		],
        fahrenheit		: [ t: "decimal",						],
        celsius			: [ t: "decimal",						],
        dateAdd			: [ t: "time",		d: "dateAdd",		],
        startswith		: [ t: "boolean",	d: "startsWith",	],
        endswith		: [ t: "boolean",	d: "endsWith",		],
        contains		: [ t: "boolean",						],
        eq				: [ t: "boolean",						],
        lt				: [ t: "boolean",						],
        le				: [ t: "boolean",						],
        gt				: [ t: "boolean",						],
        ge				: [ t: "boolean",						],
        not				: [ t: "boolean",						],
        isempty			: [ t: "boolean",	d: "isEmpty",		],
        if				: [ t: "dynamic",						],
        date			: [ t: "date",							],
        time			: [ t: "time",							],
        addseconds		: [ t: "datetime",	d: "addSeconds"		],
        addminutes		: [ t: "datetime",	d: "addMinutes"		],
        addhours		: [ t: "datetime",	d: "addHours"		],
        adddays			: [ t: "datetime",	d: "addDays"		],
        addweeks		: [ t: "datetime",	d: "addWeeks"		],
        isbetween		: [ t: "boolean",	d: "isBetween"		],
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

private Map getLocationModeOptions() {
	def result = [:]
	for (mode in location.modes) {
		if (mode) result[hashId(mode.id)] = mode.name;
	}
	return result
}
private static Map getAlarmSystemStatusOptions() {
	return [
    	off:	"Disarmed",
        stay: 	"Armed/Stay",
        away:	"Armed/Away"
    ]
}

private Map getRoutineOptions() {
	def routines = location.helloHome?.getPhrases()
    def result = [:]
    for(routine in routines) {
    	if (routine && routine?.label) 
    		result[hashId(routine.id)] = routine.label
    }
    return result
}

private Map getAskAlexaOptions() {
	return atomicState.askAlexaMacros ?: [null:"AskAlexa not installed - please install or open AskAlexa"]
}

private Map getEchoSistantOptions() {
	return atomicState.echoSistantProfiles ?: [null:"EchoSistant not installed - please install or open EchoSistant"]
}

private Map virtualDevices() {
	return [
    	date:				[ n: 'Date',						t: 'date',		],
    	time:				[ n: 'Time',						t: 'time',		],
    	datetime:			[ n: 'Date & Time',					t: 'datetime',	],        
    	mode:				[ n: 'Location mode',				t: 'enum', 		o: getLocationModeOptions(),				x: true],
    	alarmSystemStatus:	[ n: 'Smart Home Monitor status',	t: 'enum',		o: getAlarmSystemStatusOptions(),			x: true],
        routine:			[ n: 'Routine',						t: 'enum',		o: getRoutineOptions(),						m: true],
        askAlexa:			[ n: 'Ask Alexa',					t: 'enum',		o: getAskAlexaOptions(),					m: true	],
        echoSistant:		[ n: 'EchoSistant',					t: 'enum',		o: getEchoSistantOptions(),					m: true	],
        ifttt:				[ n: 'IFTTT',						t: 'string',												m: true	],
    ]
}
