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
public static String version() { return "v0.1.0a3.20170509" }
/*
 *	05/09/2017 >>> v0.1.0a3.20170509 - BETA M1 - DO NOT INSTALL THIS UNLESS ASKED TO - IT WILL BREAK YOUR ENVIRONMENT - IF YOU DID INSTALL IT, DO NOT GO BACK TO A PREVIOUS VERSION
 *	05/07/2017 >>> v0.1.0a2.20170507 - BETA M1 - Added the random() expression function.
 *	05/06/2017 >>> v0.1.0a1.20170506 - BETA M1 - Kill switch was a killer. Killed it.
 *	05/05/2017 >>> v0.1.0a0.20170505 - BETA M1 - Happy Cinco de Mayo
 *	05/03/2017 >>> v0.1.09e.20170503 - BETA M1 - Added the formatDuration function, added volume to playText, playTextAndResume, and playTextAndRestore
 *	05/03/2017 >>> v0.1.09d.20170503 - BETA M1 - Fixed a problem where async blocks inside async blocks were not working correctly.
 *	05/03/2017 >>> v0.1.09c.20170503 - BETA M1 - Fixes for race conditions where a second almost simultaneous event would miss cache updates from the first event, also improvements on timeout recovery
 *	05/02/2017 >>> v0.1.09b.20170502 - BETA M1 - Fixes for async elements as well as setColor hue inconsistencies
 *	05/01/2017 >>> v0.1.09a.20170501 - BETA M1 - Some visual UI fixes, added ternary operator support in expressions ( condition ? trueValue : falseValue ) - even with Groovy-style support for ( object ?: falseValue)
 *	05/01/2017 >>> v0.1.099.20170501 - BETA M1 - Lots of fixes and improvements - expressions now accept more logical operators like !, !!, ==, !=, <, >, <=, >= and some new math operators like \ (integer division) and % (modulo)
 *	04/30/2017 >>> v0.1.098.20170430 - BETA M1 - Minor bug fixes
 *	04/29/2017 >>> v0.1.097.20170429 - BETA M1 - First Beta Milestone 1!
 *	04/29/2017 >>> v0.0.096.20170429 - ALPHA - Various bug fixes, added options to disable certain statements, as per @eibyer's original idea and @RobinWinbourne's annoying persistance :)
 *	04/29/2017 >>> v0.0.095.20170429 - ALPHA - Fully implemented the on event statements
 *	04/28/2017 >>> v0.0.094.20170428 - ALPHA - Fixed a bug preventing timers from scheduling properly. Added the on statement and the do statement
 *	04/28/2017 >>> v0.0.093.20170428 - ALPHA - Fixed bugs (piston state issues, time condition schedules ignored offsets). Implemented more virtual commands (the fade suite)
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
	page(name: "pageSelectContacts")
}


/******************************************************************************/
/*** 																		***/
/*** CONFIGURATION PAGES													***/
/*** 																		***/
/******************************************************************************/

/******************************************************************************/
/*** COMMON PAGES															***/
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
                if (parent) href "", title: "More information ${parent.getWikiUrl()}", style: "external", url: parent.getWikiUrl(), image: "https://cdn.rawgit.com/ady624/webCoRE/master/resources/icons/app-CoRE.png", required: false
            }
        } else {
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

private pageSelectContacts() {
	parent.refreshDevices()
	dynamicPage(name: "pageSelectContacts", title: "") {
		section() {
			paragraph "Select the contacts you want ${handle()} to have access to."
        }
        section () {
			input "contacts", "contact", multiple: true, title: "Which contacts", required: false, submitOnChange: true
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
    if (contacts) {
    	app.updateSetting('contacts', [type: 'contact', value: contacts.collect{ it.id }])
    }
}

def Map listAvailableDevices(raw = false) {
	//long t = now()
	if (raw) {
    	return settings.findAll{ it.key.startsWith("dev:") }.collect{ it.value }.flatten().collectEntries{ dev -> [(hashId(dev.id)): dev]}
    } else {
    	return settings.findAll{ it.key.startsWith("dev:") }.collect{ it.value }.flatten().collectEntries{ dev -> [(hashId(dev.id)): dev]}.collectEntries{ id, dev -> [ (id): [ n: dev.getDisplayName(), cn: dev.getCapabilities()*.name, a: dev.getSupportedAttributes().unique{ it.name }.collect{def x = [n: it.name, t: it.getDataType(), o: it.getValues()]; try {x.v = dev.currentValue(x.n);} catch(all) {}; x}, c: dev.getSupportedCommands().unique{ it.getName() }.collect{[n: it.getName(), p: it.getArguments()]} ]]}
	}
}

def Map listAvailableContacts(raw = false) {
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


/******************************************************************************/
/*** 																		***/
/*** PUBLIC METHODS															***/
/*** 																		***/
/******************************************************************************/
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
