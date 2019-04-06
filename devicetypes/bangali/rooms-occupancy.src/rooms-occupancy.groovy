/***********************************************************************************************************************
*
*  A SmartThings device handler to allow handling rooms as devices which have states for occupancy.
*
*  Copyright (C) 2017 bangali
*
*  License:
*  This program is free software: you can redistribute it and/or modify it under the terms of the GNU
*  General Public License as published by the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
*  implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
*  for more details.
*
*  You should have received a copy of the GNU General Public License along with this program.
*  If not, see <http://www.gnu.org/licenses/>.
*
*  Name: Rooms Occupancy
*  Source: https://github.com/adey/bangali/blob/master/devicetypes/bangali/rooms-occupancy.src/rooms-occupancy.groovy
*
***********************************************************************************************************************/

public static String version()		{  return "v0.95.0"  }
private static boolean isDebug()	{  return false  }

final String _SmartThings()	{ return 'ST' }
final String _Hubitat()		{ return 'HU' }

metadata {
	definition (
		name: "rooms occupancy",
		namespace: "bangali",
		author: "bangali")		{
		capability "Actuator"
// for hubitat comment the next line and uncomment the one after that is currently commented
		capability "Button"
//		capability "PushableButton"		// hubitat changed `Button` to `PushableButton`  2018-04-20
		capability "Sensor"
		capability "Switch"
		capability "Beacon"
		capability "Health Check"
// for hubitat comment the next line since this capability is not supported
//		capability "Lock Only"
		attribute "occupancy", "enum", ['occupied', 'checking', 'vacant', 'locked', 'reserved', 'kaput', 'donotdisturb', 'asleep', 'engaged']
// for hubitat uncomment the next few lines ONLY if you want to use the icons on dashboard
//		attribute "occupancyIconS", "String"
//		attribute "occupancyIconM", "String"
//		attribute "occupancyIconL", "String"
//		attribute "occupancyIconXL", "String"
//		attribute "occupancyIconXXL", "String"
		attribute "occupancyIconURL", "String"
		attribute "alarmEnabled", "boolean"
		attribute "alarmTime", "String"
		attribute "alarmDayOfWeek", "String"
		attribute "alarmRepeat", "number"
		attribute "alarmSound", "String"
		attribute "countdown", "String"
		command "occupied"
		command "checking"
		command "vacant"
		command "locked"
		command "reserved"
		command "kaput"
		command "donotdisturb"
		command "asleep"
		command "engaged"
// for hubitat uncomment the next line
//		command "push"		// for use with hubitat useful with dashbooard 2018-04-24
		command "turnOnAndOffSwitches"
		command "turnSwitchesAllOn"
		command "turnSwitchesAllOff"
		command "turnNightSwitchesAllOn"
		command "turnNightSwitchesAllOff"
		command "alarmOffAction"
	}

	simulator	{
	}

	preferences		{
		section("Alarm settings:")		{
			input "alarmDisabled", "bool", title: "Disable alarm?", required: true, defaultValue: true
			input "alarmTime", "time", title: "Alarm Time?", required: false
			input "alarmVolume", "number", title: "Volume?", description: "0-100%", required: (alarmTime ? true : false), range: "1..100"
			input "alarmSound", "enum", title:"Sound?", required: (alarmTime ? true : false), multiple: false, defaultValue: null,
								options: ["0":"Bell 1", "1":"Bell 2", "2":"Dogs Barking", "3":"Fire Alarm", "4":"Piano", "5":"Lightsaber"]
			input "alarmRepeat", "number", title: "Repeat?", description: "1-999", required: (alarmTime ? true : false), range: "1..999"
			input "alarmDayOfWeek", "enum", title: "Which days of the week?", required: false, multiple: false, defaultValue: null,
								options: ["ADW0":"All Days of Week","ADW8":"Monday to Friday","ADW9":"Saturday & Sunday","ADW2":"Monday", \
										  "ADW3":"Tuesday","ADW4":"Wednesday","ADW5":"Thursday","ADW6":"Friday","ADW7":"Saturday","ADW1":"Sunday"]
		}
	}

	//
	// REMOVE THE FOLLOWING FOR HUBITAT		<<<<<
	//

	tiles(scale: 2)		{
		standardTile("occupancy", "device.occupancy", width: 2, height: 2, canChangeBackground: true)		{
			state "alarm", label: 'Alarm!', icon:"st.alarm.beep.beep", action:"alarmOffAction", backgroundColor:"#ff8c00"
			state "occupied", label: 'Occupied', icon:"st.Health & Wellness.health12", backgroundColor:"#90af89"
			state "checking", label: 'Checking', icon:"st.Health & Wellness.health9", backgroundColor:"#616969"
			state "vacant", label: 'Vacant', icon:"st.Home.home18", backgroundColor:"#32b399"
			state "donotdisturb", label: 'DnD', icon:"st.Seasonal Winter.seasonal-winter-011", backgroundColor:"#009cb2"
			state "reserved", label: 'Reserved', icon:"st.Office.office7", backgroundColor:"#ccac00"
			state "asleep", label: 'Asleep', icon:"st.Bedroom.bedroom2", backgroundColor:"#6879af"
			state "locked", label: 'Locked', icon:"st.locks.lock.locked", backgroundColor:"#c079a3"
			state "engaged", label: 'Engaged', icon:"st.locks.lock.unlocked", backgroundColor:"#ff6666"
			state "kaput", label: 'Kaput', icon:"st.Outdoor.outdoor18", backgroundColor:"#95623d"
		}
		valueTile("status", "device.status", inactiveLabel: false, width: 4, height: 1, decoration: "flat", wordWrap: false)	{
			state "status", label:'${currentValue}', backgroundColor:"#ffffff", defaultState: false
		}
		valueTile("timer", "device.timer", inactiveLabel: false, width: 1, height: 1, decoration: "flat")	{
			state "timer", label:'${currentValue}', action: "turnOnAndOffSwitches", backgroundColor:"#ffffff"
		}
		valueTile("timeInd", "device.timeInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("timeFT", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("motionInd", "device.motionInd", width: 1, height: 1, canChangeIcon: true)	{
			state("inactive", label:'${name}', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
			state("active", label:'${name}', icon:"st.motion.motion.active", backgroundColor:"#00A0DC")
			state("none", label:'${name}', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
		}
		valueTile("luxInd", "device.luxInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("lux", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("humidityInd", "device.humidityInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("humidity", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("contactInd", "device.contactInd", width: 1, height: 1, canChangeIcon: true)	{
			state("closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00A0DC")
			state("open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13")
			state("none", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#ffffff")
		}
		standardTile("switchInd", "device.switchInd", width: 1, height: 1, canChangeIcon: true)	{
			state("off", label: '${name}', action: "turnSwitchesAllOn", icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', action: "turnSwitchesAllOff", icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("presenceInd", "device.presenceInd", width: 1, height: 1, canChangeIcon: true)	{
			state("absent", label:'${name}', icon:"st.presence.tile.not-present", backgroundColor:"#ffffff")
			state("present", label:'${name}', icon:"st.presence.tile.present", backgroundColor:"#00A0DC")
			state("none", label:'${name}', icon:"st.presence.tile.not-present", backgroundColor:"#ffffff")
		}
		valueTile("presenceActionInd", "device.presenceActionInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("presenceAction", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("musicInd", "device.musicInd", width: 1, height: 1, canChangeIcon: true)	{
			state("none", label:'none', icon:"st.Electronics.electronics12", backgroundColor:"#ffffff")
			state("pause", action: "playMusic", icon: "st.sonos.play-btn", backgroundColor: "#ffffff")
			state("play", action: "pauseMusic", icon: "st.sonos.pause-btn", backgroundColor: "#00A0DC")
		}
		valueTile("dowInd", "device.dowInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("dow", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("powerInd", "device.powerInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("power", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("pauseInd", "device.pauseInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat", wordWrap: true)	{
			state("pause", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("temperatureInd", "device.temperatureInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("temperature", label:'${currentValue}', unit:'', backgroundColors: [
/*                														// Celsius Color Range
																		[value:  0, color: "#153591"],
																		[value:  7, color: "#1E9CBB"],
																		[value: 15, color: "#90D2A7"],
																		[value: 23, color: "#44B621"],
																		[value: 29, color: "#F1D801"],
																		[value: 33, color: "#D04E00"],
																		[value: 36, color: "#BC2323"],*/
																		// Fahrenheit Color Range
																		[value: 32, color: "#153591"],
																		[value: 45, color: "#1E9CBB"],
																		[value: 59, color: "#90D2A7"],
																		[value: 73, color: "#44B621"],
																		[value: 84, color: "#F1D801"],
																		[value: 91, color: "#D04E00"],
																		[value: 97, color: "#BC2323"]])
		}
		valueTile("maintainInd", "device.maintainInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("temperature", label:'${currentValue}', unit:'', backgroundColors: [
/*                														// Celsius Color Range
																		[[value:  0, color: "#153591"],
																		[value:  7, color: "#1E9CBB"],
																		[value: 15, color: "#90D2A7"],
																		[value: 23, color: "#44B621"],
																		[value: 29, color: "#F1D801"],
																		[value: 33, color: "#D04E00"],
																		[value: 36, color: "#BC2323"],*/
																		// Fahrenheit Color Range
																		[value: 32, color: "#153591"],
																		[value: 45, color: "#1E9CBB"],
																		[value: 59, color: "#90D2A7"],
																		[value: 73, color: "#44B621"],
																		[value: 84, color: "#F1D801"],
																		[value: 91, color: "#D04E00"],
																		[value: 97, color: "#BC2323"]])
		}
		valueTile("outTempInd", "device.outTempInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("temperature", label:'${currentValue}', unit:'', backgroundColors: [
/*                														// Celsius Color Range
																		[value:  0, color: "#153591"],
																		[value:  7, color: "#1E9CBB"],
																		[value: 15, color: "#90D2A7"],
																		[value: 23, color: "#44B621"],
																		[value: 29, color: "#F1D801"],
																		[value: 33, color: "#D04E00"],
																		[value: 36, color: "#BC2323"],*/
																		// Fahrenheit Color Range
																		[value: 32, color: "#153591"],
																		[value: 45, color: "#1E9CBB"],
																		[value: 59, color: "#90D2A7"],
																		[value: 73, color: "#44B621"],
																		[value: 84, color: "#F1D801"],
																		[value: 91, color: "#D04E00"],
																		[value: 97, color: "#BC2323"]])
		}
		standardTile("ventInd", "device.ventInd", width: 1, height: 1, canChangeIcon: true)	{
			state("none", label:'none', icon:"st.vents.vent", backgroundColor:"#ffffff")
			state("closed", label:'closed', icon:"st.vents.vent-closed", backgroundColor:"#00A0DC")
			state("open", label:'${currentValue}', icon:"st.vents.vent-open", backgroundColor:"#e86d13")
		}
		standardTile("thermostatInd", "device.thermostatInd", width:1, height:1, canChangeIcon: true)	{
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", icon: "st.thermostat.heating-cooling-off", backgroundColor: "#ffffff")
			state("auto", icon: "st.thermostat.auto", backgroundColor: "#ffffff")
			state("autoCool", icon: "st.thermostat.auto-cool", backgroundColor: "#ffffff")
			state("autoHeat", icon: "st.thermostat.heat", backgroundColor: "#ffffff")
			state("cooling", icon: "st.thermostat.cooling", backgroundColor: "#1E9CBB")
			state("heating", icon: "st.thermostat.heating", backgroundColor: "#D04E00")
		}
		valueTile("thermoOverrideInd", "device.thermoOverrideInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("thermoOverride", label:'${currentValue}', backgroundColor: "#ffffff")
		}
		standardTile("fanInd", "device.fanInd", width:1, height:1, canChangeIcon: true)		{
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", label:'${currentValue}', icon: "st.Lighting.light24", backgroundColor: "#ffffff")
			state("low", label:'${currentValue}', icon: "st.Lighting.light24", backgroundColor: "#90D2A7")
			state("medium", label:'${currentValue}', icon: "st.Lighting.light24", backgroundColor: "#F1D801")
			state("high", label:'${currentValue}', icon: "st.Lighting.light24", backgroundColor: "#D04E00")
		}
		standardTile("contactRTInd", "device.contactRTInd", width: 1, height: 1, canChangeIcon: true)	{
			state("closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00A0DC")
			state("open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13")
			state("none", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#ffffff")
		}
		valueTile("rulesInd", "device.rulesInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("rules", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("lastRuleInd", "device.lastRuleInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("lastRule", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("eSwitchInd", "device.eSwitchInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", label: '${name}', icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
		}
		standardTile("oSwitchInd", "device.oSwitchInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", label: '${name}', icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
		}
		standardTile("aSwitchInd", "device.aSwitchInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", label: '${name}', icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
		}
		valueTile("aRoomInd", "device.aRoomInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat", wordWrap: true)	{
			state("rooms", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("presenceEngagedInd", "device.presenceEngagedInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("presenceEngaged", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("busyEngagedInd", "device.busyEngagedInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("busyEngaged", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("lSwitchInd", "device.lSwitchInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", label: '${name}', icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
		}
		standardTile("nSwitchInd", "device.nSwitchInd", width: 1, height: 1, canChangeIcon: true)	{
			state("none", label:'${currentValue}', backgroundColor:"#ffffff")
			state("off", label: '${name}', action: "turnNightSwitchesAllOn", icon: "st.switches.switch.off", backgroundColor: "#ffffff")
			state("on", label: '${name}', action: "turnNightSwitchesAllOff", icon: "st.switches.switch.on", backgroundColor: "#00A0DC")
		}
		valueTile("wSSInd", "device.wSSInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("wSS", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("noMotionInd", "device.noMotionInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("noMotion", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("dimTimerInd", "device.dimTimerInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("dimTimer", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("noMotionEngagedInd", "device.noMotionEngagedInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("noMotionEngaged", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("noMotionAsleepInd", "device.noMotionAsleepInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("noMotionAsleep", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("turnAllOffInd", "device.turnAllOffInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("turnAllOff", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("dimByLevelInd", "device.dimByLevelInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("dimByLevel", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("eWattsInd", "device.eWattsInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("eWatts", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("aWattsInd", "device.aWattsInd", width: 1, height: 1, canChangeIcon: true, decoration: "flat")	{
			state("aWatts", label:'${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("aMotionInd", "device.aMotionInd", width: 1, height: 1, canChangeIcon: true)	{
			state("none", label:'${name}', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
			state("inactive", label:'${name}', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
			state("active", label:'${name}', icon:"st.motion.motion.active", backgroundColor:"#00A0DC")
		}
		valueTile("deviceList1", "device.deviceList1", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList1", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList2", "device.deviceList2", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList2", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList3", "device.deviceList3", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList3", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList4", "device.deviceList4", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList4", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList5", "device.deviceList5", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList5", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList6", "device.deviceList6", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList6", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList7", "device.deviceList7", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList7", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList8", "device.deviceList8", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList8", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList9", "device.deviceList9", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList9", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList10", "device.deviceList10", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList10", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList11", "device.deviceList11", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList11", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		valueTile("deviceList12", "device.deviceList12", inactiveLabel: false, width: 3, height: 1, decoration: "flat", wordWrap: true)	{
			state "deviceList12", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		standardTile("engaged", "device.engaged", width: 2, height: 2, canChangeIcon: true)	{
			state "engaged", label:"Engaged", icon: "st.locks.lock.unlocked", action: "engaged", backgroundColor:"#ffffff", nextState:"toEngaged"
			state "toEngaged", label:"Updating", icon: "st.locks.lock.unlocked", backgroundColor:"#ff6666"
		}
		standardTile("vacant", "device.vacant", width: 2, height: 2, canChangeIcon: true)	{
			state "vacant", label:"Vacant", icon: "st.Home.home18", action: "vacant", backgroundColor:"#ffffff", nextState:"toVacant"
			state "toVacant", label:"Updating", icon: "st.Home.home18", backgroundColor:"#32b399"
		}
		standardTile("occupied", "device.occupied", width: 2, height: 2, canChangeIcon: true)	{
			state "occupied", label:"Occupied", icon: "st.Health & Wellness.health12", action: "occupied", backgroundColor:"#ffffff", nextState:"toOccupied"
			state "toOccupied", label:"Updating", icon:"st.Health & Wellness.health12", backgroundColor:"#90af89"
		}
		standardTile("donotdisturb", "device.donotdisturb", width: 2, height: 2, canChangeIcon: true)	{
			state "donotdisturb", label:"DnD", icon: "st.Seasonal Winter.seasonal-winter-011", action: "donotdisturb", backgroundColor:"#ffffff", nextState:"toDoNotDisturb"
			state "toDoNotDisturb", label:"Updating", icon: "st.Seasonal Winter.seasonal-winter-011", backgroundColor:"#009cb2"
		}
		standardTile("reserved", "device.reserved", width: 2, height: 2, canChangeIcon: true)	{
			state "reserved", label:"Reserved", icon: "st.Office.office7", action: "reserved", backgroundColor:"#ffffff", nextState:"toReserved"
			state "toReserved", label:"Updating", icon: "st.Office.office7", backgroundColor:"#ccac00"
		}
		standardTile("asleep", "device.asleep", width: 2, height: 2, canChangeIcon: true)	{
			state "asleep", label:"Asleep", icon: "st.Bedroom.bedroom2", action: "asleep", backgroundColor:"#ffffff", nextState:"toAsleep"
			state "toAsleep", label:"Updating", icon: "st.Bedroom.bedroom2", backgroundColor:"#6879af"
		}
		standardTile("locked", "device.locked", width: 2, height: 2, canChangeIcon: true)	{
			state "locked", label:"Locked", icon: "st.locks.lock.locked", action: "locked", backgroundColor:"#ffffff", nextState:"toLocked"
			state "toLocked", label:"Updating", icon: "st.locks.lock.locked", backgroundColor:"#c079a3"
		}
		standardTile("kaput", "device.kaput", width: 2, height: 2, canChangeIcon: true)	{
			state "kaput", label:"Kaput", icon: "st.Outdoor.outdoor18", action: "kaput", backgroundColor:"#ffffff", nextState:"toKaput"
			state "toKaput", label:"Updating", icon: "st.Outdoor.outdoor18", backgroundColor:"#95623d"
		}
		valueTile("blankL", "device.blankL", width: 1, height: 1, decoration: "flat")					{ state "blankL", label:'\n' }
		valueTile("timerL", "device.timerL", width: 1, height: 1, decoration: "flat")					{ state "timerL", label:'timer' }
		valueTile("roomMotionL", "device.roomMotionL", width: 1, height: 1, decoration: "flat")			{ state "roomMotionL", label:'room\nmotion' }
		valueTile("adjRoomMotionL", "device.adjRoomMotionL", width: 1, height: 1, decoration: "flat")	{ state "adjRoomMotionL", label:'adjacent\nroom\nmotion' }
		valueTile("luxL", "device.luxL", width: 1, height: 1, decoration: "flat")						{ state "luxL", label:'room\nlux' }
		valueTile("roomContactL", "device.roomContactL", width: 1, height: 1, decoration: "flat")		{ state "roomContactL", label:'room\ncontact' }
		valueTile("presenceL", "device.presenceL", width: 1, height: 1, decoration: "flat")				{ state "presenceL", label:'presence' }
		valueTile("presenceActionL", "device.presenceActionL", width: 1, height: 1, decoration: "flat")	{ state "presenceActionL", label:'presence\naction' }
		valueTile("musicL", "device.musicL", width: 1, height: 1, decoration: "flat")					{ state "musicL", label:'music' }
		valueTile("dowL", "device.dowL", width: 1, height: 1, decoration: "flat")						{ state "dowL", label:'day of\nweek' }
		valueTile("timeL", "device.timeL", width: 1, height: 1, decoration: "flat")						{ state "timeL", label:'time\nschedule' }
		valueTile("oSwitchL", "device.oSwitchL", width: 1, height: 1, decoration: "flat")				{ state "oSwitchL", label:'occupied\nswitches' }
		valueTile("eSwitchL", "device.eSwitchL", width: 1, height: 1, decoration: "flat")				{ state "eSwitchL", label:'engaged\nswitches' }
		valueTile("aSwitchL", "device.aSwitchL", width: 1, height: 1, decoration: "flat")				{ state "aSwitchL", label:'asleep\nswitches' }
		valueTile("presenceEngagedL", "device.presenceEngagedL", width: 1, height: 1, decoration: "flat")	{ state "presenceEngagedL", label:'presence\nengaged' }
		valueTile("engagedWithBusyL", "device.engagedWithBusyL", width: 1, height: 1, decoration: "flat")	{ state "engagedWithBusyL", label:'engaged\nwith busy' }
		valueTile("lSwitchL", "device.lSwitchL", width: 1, height: 1, decoration: "flat")				{ state "lSwitchL", label:'locked\nswitch' }
		valueTile("oTimerL", "device.oTimerL", width: 1, height: 1, decoration: "flat")					{ state "oTimerL", label:'occupied\ntimer' }
		valueTile("cTimerL", "device.cTimerL", width: 1, height: 1, decoration: "flat")					{ state "cTimerL", label:'checking\ntimer' }
		valueTile("eTimerL", "device.eTimerL", width: 1, height: 1, decoration: "flat")					{ state "eTimerL", label:'engaged\ntimer' }
		valueTile("aTimerL", "device.aTimerL", width: 1, height: 1, decoration: "flat")					{ state "aTimerL", label:'asleep\ntimer' }
		valueTile("turnAllOffL", "device.turnAllOffL", width: 1, height: 1, decoration: "flat")			{ state "turnAllOffL", label:'turn\nall off' }
		valueTile("dimByL", "device.dimByL", width: 1, height: 1, decoration: "flat")					{ state "dimByL", label:'dim\nby / to\nlevel' }
		valueTile("switchL", "device.switchL", width: 1, height: 1, decoration: "flat")					{ state "switchL", label:'room\nswitches' }
		valueTile("nSwitchL", "device.nSwitchL", width: 1, height: 1, decoration: "flat")				{ state "nSwitchL", label:'night\nswitches' }
		valueTile("shadeL", "device.shadeL", width: 1, height: 1, decoration: "flat")					{ state "shadeL", label:'window\nshades' }
		valueTile("powerL", "device.powerL", width: 1, height: 1, decoration: "flat")					{ state "powerL", label:'power\nwatts' }
		valueTile("eWattsL", "device.eWattsL", width: 1, height: 1, decoration: "flat")					{ state "eWattsL", label:'engaged\nwatts' }
		valueTile("aWattsL", "device.aWattsL", width: 1, height: 1, decoration: "flat")					{ state "aWattsL", label:'asleep\nwatts' }
		valueTile("temperatureL", "device.temperatureL", width: 1, height: 1, decoration: "flat")		{ state "temperatureL", label:'room\ntemp' }
		valueTile("thermostatL", "device.thermostatL", width: 1, height: 1, decoration: "flat")			{ state "thermostatL", label:'heat /\ncool' }
		valueTile("maintainL", "device.maintainL", width: 1, height: 1, decoration: "flat")				{ state "maintainL", label:'maintain\ntemp' }
		valueTile("outTempL", "device.outTempeL", width: 1, height: 1, decoration: "flat")				{ state "outTempL", label:'outside\ntemp' }
		valueTile("ventL", "device.ventL", width: 1, height: 1, decoration: "flat")						{ state "ventL", label:'vent\nlevel' }
		valueTile("fanL", "device.fanL", width: 1, height: 1, decoration: "flat")						{ state "fanL", label:'fan\nspeed' }
		valueTile("roomWindowsL", "device.roomWindowsL", width: 1, height: 1, decoration: "flat")		{ state "roomWindowsL", label:'room\nwindow' }
		valueTile("thermoOverrideL", "device.thermoOverrideL", width: 1, height: 1, decoration: "flat")	{ state "thermoOverrideL", label:'thermo\noverride' }
		valueTile("humidityL", "device.humidityL", width: 1, height: 1, decoration: "flat")				{ state "humidityL", label:'humidity' }
		valueTile("reservedL", "device.reservedL", width: 2, height: 1, decoration: "flat")				{ state "reservedL", label:'reserved' }
		valueTile("rulesL", "device.rulesL", width: 1, height: 1, decoration: "flat")					{ state "rulesL", label:'# of\nrules' }
		valueTile("lastRuleL", "device.lastRuleL", width: 1, height: 1, decoration: "flat")				{ state "lastRuleL", label:'last\nrules' }
		valueTile("adjRoomsL", "device.adjRoomsL", width: 1, height: 1, decoration: "flat")				{ state "adjRoomsL", label:'adjacent\nrooms' }

		main (["occupancy"])

		details ([	"occupancy", "occupied", "engaged",
					"vacant", "asleep", "locked",
					"status", "timerL", "timer",
					"roomMotionL", "motionInd", "adjRoomMotionL", "aMotionInd", "luxL", "luxInd",
					"roomContactL", "contactInd", "presenceL", "presenceInd", "presenceActionL", "presenceActionInd",
					"musicL", "musicInd", "dowL", "dowInd", "timeL", "timeInd",
					"oSwitchL", "oSwitchInd", "eSwitchL", "eSwitchInd", "aSwitchL", "aSwitchInd",
					"presenceEngagedL", "presenceEngagedInd", "engagedWithBusyL", "busyEngagedInd",  "lSwitchL", "lSwitchInd",
					"oTimerL", "noMotionInd", "cTimerL", "dimTimerInd", "eTimerL", "noMotionEngagedInd",
					"turnAllOffL", "turnAllOffInd", "dimByL", "dimByLevelInd", "aTimerL", "noMotionAsleepInd",
					"switchL", "switchInd", "nSwitchL", "nSwitchInd", "shadeL", "wSSInd",
					"powerL", "powerInd", "eWattsL", "eWattsInd", "aWattsL", "aWattsInd",
					"temperatureL", "temperatureInd", "thermostatL", "thermostatInd", "maintainL", "maintainInd",
					"outTempL", "outTempInd", "ventL", "ventInd", "fanL", "fanInd",
					"roomWindowsL", "contactRTInd", "thermoOverrideL", "thermoOverrideInd", "humidityL", "humidityInd",
					"rulesL", "rulesInd", "lastRuleL", "lastRuleInd", "adjRoomsL", "aRoomInd"])
	}

	// REMOVE TILL HERE FOR HUBITAT		<<<<<

}

def parse(String description)	{  ifDebug("parse: $description")  }

def installed()		{  initialize()  }

def updated()		{  initialize()  }

def	initialize()	{
	unschedule()
	sendEvent(name: "numberOfButtons", value: 9, descriptionText: "set number of buttons to 9.", isStateChange: true, displayed: true)
	state.timer = 0
	setupAlarmC()
	sendEvent(name: "countdown", value: '0s', descriptionText: "countdown timer: 0s", isStateChange: true, displayed: true)
	if (getHubType() == _SmartThings)		{
		sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
		sendEvent(name: "healthStatus", value: "online")
		sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
	}
}

def getHubType()	{
	if (!state.hubId)	state.hubId = location.hubs[0].id.toString()
	return (state.hubId.length() > 5 ? _SmartThings() : _Hubitat())
}

def setupAlarmC()	{
	if (parent)		parent.setupAlarmP(alarmDisabled, alarmTime, alarmVolume, alarmSound, alarmRepeat, alarmDayOfWeek);
	if (alarmDayOfWeek != 'ADW0')	{
		state.alarmDayOfWeek = []
		switch(alarmDayOfWeek)	{
			case 'ADW1':	state.alarmDayOfWeek << 'Mon';		break
			case 'ADW2':	state.alarmDayOfWeek << 'Tue';		break
			case 'ADW3':	state.alarmDayOfWeek << 'Wed';		break
			case 'ADW4':	state.alarmDayOfWeek << 'Thu';		break
			case 'ADW5':	state.alarmDayOfWeek << 'Fri';		break
			case 'ADW6':	state.alarmDayOfWeek << 'Sat';		break
			case 'ADW7':	state.alarmDayOfWeek << 'Sun';		break
			case 'ADW8':   	state.alarmDayOfWeek = state.alarmDayOfWeek + ['Mon','Tue','Wed','Thu','Fri'];	break
			case 'ADW9':   	state.alarmDayOfWeek = state.alarmDayOfWeek + ['Sat','Sun'];					break
			default:  		state.alarmDayOfWeek = null;		break
		}
	}
	else
		state.alarmDayOfWeek = ''
	state.alarmSound = (alarmSound ? ["Bell 1", "Bell 2", "Dogs Barking", "Fire Alarm", "Piano", "Lightsaber"][alarmSound as Integer] : '')
	sendEvent(name: "alarmEnabled", value: ((alarmDisabled || !alarmTime) ? 'No' : 'Yes'), descriptionText: "alarm enabled is ${(!alarmDisabled)}", isStateChange: true, displayed: true)
	sendEvent(name: "alarmTime", value: "${(alarmTime ? timeToday(alarmTime, location.timeZone).format("HH:mm", location.timeZone) : '')}", descriptionText: "alarm time is ${alarmTime}", isStateChange: true, displayed: true)
	sendEvent(name: "alarmDayOfWeek", value: "$state.alarmDayOfWeek", descriptionText: "alarm days of week is $state.alarmDayOfWeek", isStateChange: true, displayed: true)
	sendEvent(name: "alarmSound", value: "$state.alarmSound", descriptionText: "alarm sound is $state.alarmSound", isStateChange: true, displayed: true)
	sendEvent(name: "alarmRepeat", value: alarmRepeat, descriptionText: "alarm sounds is repeated $alarmRepeat times", isStateChange: true, displayed: true)
}

def on()	{
	if (!state.onState)		state.onState = parent?.roomDeviceSwitchOnP().toString();
	switch(state.onState ?: 'occupied')		{
		case 'occupied':	occupied();		break
		case 'engaged':		engaged();		break
		case 'locked':		locked();		break
		case 'asleep':		asleep();		break
		default:							break
	}
	sendEvent(name: "switch", value: "on", descriptionText: "$device.displayName is on", isStateChange: true, displayed: true)
}

def setOnStateC(e)		{  state.onState = (e ? e.toString() : 'occupied')  }

def	off()		{
	vacant()
	sendEvent(name: "switch", value: "off", descriptionText: "$device.displayName is off", isStateChange: true, displayed: true)
}

def push(buton)		{
	ifDebug("$buton")
	def hT = getHubType()
	switch(buton)		{
		case 1:		occupied();		break
		case 3:		vacant();		break
		case 4:		locked();		break
		case 8:		asleep();		break
		case 9:		engaged();		break
		default:
			if (hT != _Hubitat())
				sendEvent(name: "button", value: "pushed", data: [buttonNumber: "$buton"], descriptionText: "$device.displayName button $buton was pushed", isStateChange: true, displayed: true)
			else
				sendEvent(name: "pushableButton", value: buton, descriptionText: "$device.displayName button $buton was pushed", isStateChange: true, displayed: true)
			break
	}
}

def lock()		{  locked() }

def unlock()	{  vacant()  }

def occupied(handleSwitches = true)			{ runIn(0, stateUpdate, [data: [newState:'occupied', handleSwitches:handleSwitches]]) }

def checking(handleSwitches = true)			{ runIn(0, stateUpdate, [data: [newState:'checking', handleSwitches:handleSwitches]]) }

def vacant(handleSwitches = true)			{ runIn(0, stateUpdate, [data: [newState:'vacant', handleSwitches:handleSwitches]]) }

def donotdisturb(handleSwitches = true)		{ runIn(0, stateUpdate, [data: [newState:'donotdisturb', handleSwitches:handleSwitches]]) }

def reserved(handleSwitches = true)			{ runIn(0, stateUpdate, [data: [newState:'reserved', handleSwitches:handleSwitches]]) }

def asleep(handleSwitches = true)			{ runIn(0, stateUpdate, [data: [newState:'asleep', handleSwitches:handleSwitches]]) }

def locked(handleSwitches = true)			{ runIn(0, stateUpdate, [data: [newState:'locked', handleSwitches:handleSwitches]]) }

def engaged(handleSwitches = true)			{ runIn(0, stateUpdate, [data: [newState:'engaged', handleSwitches:handleSwitches]]) }

def kaput(handleSwitches = true)			{ runIn(0, stateUpdate, [data: [newState:'kaput', handleSwitches:handleSwitches]]) }

def	stateUpdate(data)		{
	if (!data)		return;
	def newState = data.newState
	def handleSwitches = data.handleSwitches
	if (state.oldState != newState)		{
        if (handleSwitches && parent)
			setupTimer((int) (parent.handleSwitches(state.oldState, newState, true) ?: 0))
		updateOccupancy(state.oldState, newState)
		state.oldState = newState
	}
	resetTile(newState)
}

def updateOccupancy(oldOcc, newOcc) 	{
	newOcc = newOcc?.toLowerCase()
	def hT = getHubType()
	def buttonMap = ['occupied':1, 'locked':4, 'vacant':3, 'reserved':5, 'checking':2, 'kaput':6, 'donotdisturb':7, 'asleep':8, 'engaged':9]
	if (!newOcc || !(buttonMap.containsKey(newOcc)))	{
		ifDebug("Missing or invalid parameter room occupancy: $newOcc")
		return
	}
	sendEvent(name: "occupancy", value: newOcc, descriptionText: "$device.displayName changed to $newOcc", isStateChange: true, displayed: true)
	if (hT == _Hubitat())		{
		def img = "https://cdn.rawgit.com/adey/bangali/master/resources/icons/rooms${newOcc?.capitalize()}State.png"
		sendEvent(name: "occupancyIconS", value: "<img src=$img height=25 width=25>", descriptionText: "$device.displayName $newOcc icon small", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconM", value: "<img src=$img height=50 width=50>", descriptionText: "$device.displayName $newOcc icon medium", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconL", value: "<img src=$img height=75 width=75>", descriptionText: "$device.displayName $newOcc icon large", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconXL", value: "<img src=$img height=100 width=100>", descriptionText: "$device.displayName $newOcc icon extra large", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconXXL", value: "<img src=$img height=150 width=150>", descriptionText: "$device.displayName $newOcc icon extra extra large", isStateChange: true, displayed: true)
		sendEvent(name: "occupancyIconURL", value: img, descriptionText: "$device.displayName $newOcc icon URL", isStateChange: true, displayed: true)
	}
	def button = buttonMap[newOcc]
	if (hT == _SmartThings())
		sendEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed.", isStateChange: true)
	else
		sendEvent(name:"pushed", value:button, descriptionText: "$device.displayName button $button was pushed.", isStateChange: true)

	updateRoomStatusMsg()
}

def alarmOn()	{
	sendEvent(name: "occupancy", value: 'alarm', descriptionText: "$device.displayName alarm is on", isStateChange: true, displayed: true)
	runIn(2, alarmOff)
}

def alarmOff(endLoop = false)	{
	if (device.currentValue('occupancy') == 'alarm' || endLoop)
		sendEvent(name: "occupancy", value: "$state.oldState", descriptionText: "$device.displayName alarm is off", isStateChange: true, displayed: true)
	(endLoop ? unschedule() : runIn(1, alarmOn))
}

def alarmOffAction()	{
	ifDebug("alarmOffAction")
	unschedule()
	if (parent)		parent.ringAlarm(true);
	alarmOff(true)
}

private updateRoomStatusMsg()		{
	def formatter = new java.text.SimpleDateFormat("EEE, MMM d yyyy @ h:mm:ss a z")
	formatter.setTimeZone(location.timeZone)
	state.statusMsg = formatter.format(now())
	sendEvent(name: "status", value: state.statusMsg, isStateChange: true, displayed: false)
}

private	resetTile(occupancy)	{
	sendEvent(name: occupancy, value: occupancy, descriptionText: "$device.displayName reset tile $occupancy", isStateChange: true, displayed: false)
}

def turnSwitchesAllOn()		{
	if (parent)		{
		parent.turnSwitchesAllOnOrOff(true)
		if (getHubType() != _Hubitat())		updateSwitchInd(1);
	}
}

def turnSwitchesAllOff()		{
	if (parent)		{
		parent.turnSwitchesAllOnOrOff(false)
		if (getHubType() != _Hubitat())		updateSwitchInd(0);
	}
}

def turnNightSwitchesAllOn()	{
 	ifDebug("turnNightSwitchesAllOn")
	if (parent)	{
		parent.dimNightLights()
		if (getHubType() != _Hubitat())		updateNSwitchInd(1)
	}
}

def turnNightSwitchesAllOff()	{
	ifDebug("turnNightSwitchesAllOff")
	if (parent)		{
		parent.nightSwitchesOff()
		if (getHubType() != _Hubitat())		updateNSwitchInd(0)
	}
}

def	turnOnAndOffSwitches()	{
	if (parent)		parent.switchesOnOrOff();
	setupTimer(-1)
}

def setupTimer(int timer)	{
	if (timer != -1)	state.timerLeft = timer;
	timerNext()
}

def timerNext()		{
	int timerUpdate = (state.timerLeft > 60 ? 60 : (state.timerLeft < 5 ? state.timerLeft : 5))
	def timerInd = (state.timerLeft > 3600 ? (state.timerLeft / 3600f).round(1) + 'h' : (state.timerLeft > 60 ? (state.timerLeft / 60f).round(1) + 'm' : state.timerLeft + 's')).replace(".0","")
	if (getHubType() != _Hubitat())
		sendEvent(name: "timer", value: (timerInd ?: '--'), isStateChange: true, displayed: false)
	else
		sendEvent(name: "countdown", value: timerInd, descriptionText: "countdown timer: $timerInd", isStateChange: true, displayed: true)
	state.timerLeft = state.timerLeft - timerUpdate
	(state.timerLeft > 0 ? runIn(timerUpdate, timerNext) : unschedule('timerNext'))
}

private ifDebug(msg = null, level = null)	{  if (msg && (isDebug() || level == 'error'))	log."${level ?: 'debug'}" " $device.displayName device: " + msg  }

//
// REMOVE THE FOLLOWING FOR HUBITAT		<<<<<
//

def updateMotionInd(motionOn)		{
	def vV = 'none'
	def dD = "indicate no motion sensor"
	switch(motionOn)	{
		case 1:		vV = 'active';		dD = "indicate motion active";		break
		case 0:		vV = 'inactive';	dD = "indicate motion inactive";	break
	}
	sendEvent(name: 'motionInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateLuxInd(lux)		{
	sendEvent(name: 'luxInd', value: (lux == -1 ? '--' : (lux <= 100 ? lux : formatNumber(lux))), descriptionText: (lux == -1 ? "indicate no lux sensor" : "indicate lux value"), isStateChange: true, displayed: false)
}

def updateHumidityInd(humidity)		{
	sendEvent(name: 'humidityInd', value: (humidity == -1 ? '--' : humidity.toString() + '%'), descriptionText: (humidity == -1 ? "indicate no humidity sensor" : "indicate humidity value"), isStateChange: true, displayed: false)
}
def updateContactInd(contactClosed)		{
	def vV = 'none'
	def dD = "indicate no contact sensor"
	switch(contactClosed)	{
		case 1:		vV = 'closed';	dD = "indicate contact closed";		break
		case 0:		vV = 'open';	dD = "indicate contact open";		break
	}
	sendEvent(name: 'contactInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateContactRTIndC(contactStatus)		{
	def vV = 'none'
	def dD = "indicate no contact sensor"
	switch(contactStatus)	{
		case 1:		vV = 'closed';	dD = "indicate contact closed";		break
		case 0:		vV = 'open';	dD = "indicate contact open";		break
	}
	sendEvent(name: 'contactRTInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateSwitchInd(switchOn)		{
	def vV = '--'
	def dD = "indicate no switches to turn on in room"
	switch(switchOn)	{
		case 1:		vV = 'on';	dD = "indicate at least one switch in room is on";	break
		case 0:		vV = 'off';	dD = "indicate all switches in room is off";		break
	}
	sendEvent(name: 'switchInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updatePresenceInd(presencePresent)		{
	def vV = 'none'
	def dD = "indicate no presence sensor"
	switch(presencePresent)		{
		case 1:		vV = 'present';	dD = "indicate presence present";		break
		case 0:		vV = 'absent';	dD = "indicate presence not present";	break
	}
	sendEvent(name: 'presenceInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updatePresenceActionInd(presenceAction)		{
	def vV = '--'
	def dD = "indicate no presence sensor"
	switch(presenceAction)	{
		case '1':		vV = 'Arrival';		dD = "indicate arrival action when present";						break
		case '2':		vV = 'Departure';	dD = "indicate departure action when not present";					break
		case '3':		vV = 'Both';		dD = "indicate both arrival and depature action with presence";		break
		case '4':		vV = 'Neither';		dD = "indicate no action with with present";						break
	}
	sendEvent(name: 'presenceActionInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updatePresenceEngagedInd(presenceEngaged)		{
	sendEvent(name: 'presenceEngagedInd', value: (presenceEngaged == -1 ? '--' : presenceEngaged), descriptionText: (presenceEngaged == -1 ? "indicate no presence sensor" : "indicate if presence action continuous"), isStateChange: true, displayed: false)
}

def updateBusyEngagedInd(busyEngaged)		{
	sendEvent(name: 'busyEngagedInd', value: (busyEngaged == -1 ? '--' : "$busyEngaged\ntraffic"), descriptionText: (busyEngaged == -1 ? "indicate no presence sensor" : "indicate traffic check"), isStateChange: true, displayed: false)
}

def updateDoWInd(dow)		{
	def		vV
	switch(dow)	{
		case '1':	vV = 'Monday';		break
		case '2':	vV = 'Tuesday';		break
		case '3':	vV = 'Wednesday';	break
		case '4':	vV = 'Thursday';	break
		case '5':	vV = 'Friday';		break
		case '6':	vV = 'Saturday';	break
		case '7':	vV = 'Sunday';		break
		case '8':	vV = 'M - F';		break
		case '9':	vV = 'S & S';		break
		default:	vV = 'Everyday';	break
	}
	sendEvent(name: 'dowInd', value: vV, descriptionText: "indicate run on only these days of the week: $vV", isStateChange: true, displayed: false)
}

def updateTimeInd(timeFromTo)		{
	sendEvent(name: 'timeInd', value: timeFromTo, descriptionText: "indicate time from to", isStateChange: true, displayed: false)
}

def updateTemperatureInd(temp)		{
	def tS = (location.temperatureScale ?: 'F')
	sendEvent(name: 'temperatureInd', value: (temp == -1 ? '--' : temp + '°' + tS), unit: tS, descriptionText: (temp == -1 ? "indicate no temperature sensor" : "indicate temperature value"), isStateChange: true, displayed: false)
}

def updateOutTempIndC(temp)		{
	def tS = (location.temperatureScale ?: 'F')
	sendEvent(name: 'outTempInd', value: (temp == -1 ? '--' : temp + '°' + tS), unit: tS, descriptionText: (temp == -1 ? "indicate no temperature sensor" : "indicate temperature value"), isStateChange: true, displayed: false)
}

def updateVentIndC(vent)		{
	ifDebug("updateVentIndC: $vent")
	def vL, dD
	switch(vent)	{
		case -1:	vL = 'none';		dD = "indicate no vents";		break
		case 0:		vL = 'closed';		dD = "indicate vents closed";	break
		default:	vL = 'open';		dD = "indicate vent open";		break
	}
//	sendEvent(name: 'ventInd', value: (vL == 'open' ? formatNumber(vent) : vL), descriptionText: dD, isStateChange: true, displayed: false)
	sendEvent(name: 'ventInd', value: vL, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateMaintainIndC(temp)		{
	def tS = (location.temperatureScale ?: 'F')
	sendEvent(name: 'maintainInd', value: (temp == -1 ? '--' : temp + '°' + tS), unit: tS, descriptionText: (temp == -1 ? "indicate no maintain temperature" : "indicate maintain temperature value"), isStateChange: true, displayed: false)
}

def updateThermostatIndC(thermo)		{
	def vV = '--'
	def dD = "indicate no thermostat setting"
	switch(thermo)	{
		case 0:		vV = 'off';			dD = "indicate thermostat not auto";	break
		case 1:		vV = 'auto';		dD = "indicate thermostat auto";		break
		case 2:		vV = 'autoCool';	dD = "indicate thermostat auto cool";	break
		case 3:		vV = 'autoHeat';	dD = "indicate thermostat auto heat";	break
		case 4:		vV = 'cooling';		dD = "indicate thermostat cooling";		break
		case 5:		vV = 'heating';		dD = "indicate thermostat heating";		break
	}
	sendEvent(name: 'thermostatInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateThermoOverrideIndC(thermoOverride)		{
	sendEvent(name: 'thermoOverrideInd', value: thermoOverride, descriptionText: "indicate thermo override minutes", isStateChange: true, displayed: false)
}

def updateFanIndC(fan)		{
	def vV = '--'
	def dD = "indicate no fan setting"
	switch(fan)	{
		case 0:		vV = 'off';		dD = "indicate fan off";				break
		case 1:		vV = 'low';		dD = "indicate fan on at low speed";	break
		case 2:		vV = 'medium';	dD = "indicate fan on at medium speed";	break
		case 3:		vV = 'high';	dD = "indicate fan on at high speed";	break
	}
	sendEvent(name: 'fanInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateRulesInd(rules)		{
	sendEvent(name: 'rulesInd', value: (rules == -1 ? '0' : rules), descriptionText: (rules == -1 ? "indicate no rules" : "indicate rules count"), isStateChange: true, displayed: false)
}

def updateLastRuleInd(rule)		{
	sendEvent(name: 'lastRuleInd', value: (rule == -1 ? '--' : rule), descriptionText: (rule == -1 ? "indicate no rule executed" : "indicate rule number last executed"), isStateChange: true, displayed: false)
}

def updatePauseInd(pMode)		{
	sendEvent(name: 'pauseInd', value: (pMode == -1 ? '--' : pMode), descriptionText: (pMode == -1 ? "indicate no pause modes" : "indicate pause modes"), isStateChange: true, displayed: false)
}

def updatePowerInd(power)		{
	sendEvent(name: 'powerInd', value: (power == -1 ? '--' : (power <= 100 ? power : formatNumber(power))), descriptionText: (power == -1 ? "indicate no power sensor" : "indicate power value"), isStateChange: true, displayed: false)
}

def updateEWattsInd(eWatts)		{
	sendEvent(name: 'eWattsInd', value: (eWatts == -1 ? '--' : (eWatts <= 100 ? eWatts : formatNumber(eWatts))), descriptionText: (eWatts == -1 ? "indicate no engaged watts" : "indicate engaged watts value"), isStateChange: true, displayed: false)
}

def updateAWattsInd(aWatts)		{
	sendEvent(name: 'aWattsInd', value: (aWatts == -1 ? '--' : (aWatts <= 100 ? aWatts : formatNumber(aWatts))),
				descriptionText: (aWatts == -1 ? "indicate no asleep watts" : "indicate asleep watts value"), isStateChange: true, displayed: false)
}

def updateESwitchInd(switchOn)		{
	def vV = '--'
	def dD = "indicate no engaged switch"
	switch(switchOn)	{
		case 1:		vV = 'on';	dD = "indicate engaged switch is on";	break
		case 0:		vV = 'off';	dD = "indicate engaged switch is off";	break
	}
	sendEvent(name: 'eSwitchInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateTimersInd(noMotion, dimTimer, noMotionEngaged, noMotionAsleep)		{
	sendEvent(name: 'noMotionInd', value: (noMotion ? formatNumber(noMotion) : '--'), descriptionText: (noMotion ? "indicate motion timer for occupied state" : "indicate no motion timer for occupied state"), isStateChange: true, displayed: false)
	sendEvent(name: 'dimTimerInd', value: (dimTimer ? formatNumber(dimTimer) : '--'), descriptionText: (dimTimer ? "indicate timer for checking state" : "indicate no timer for checking state"), isStateChange: true, displayed: false)
	sendEvent(name: 'noMotionEngagedInd', value: (noMotionEngaged ? formatNumber(noMotionEngaged) : '--'), descriptionText: (noMotionEngaged ? "indicate motion timer for engaged state" : "indicate no motion timer for engaged state"), isStateChange: true, displayed: false)
	sendEvent(name: 'noMotionAsleepInd', value: (noMotionAsleep ? formatNumber(noMotionAsleep) : '--'), descriptionText: (noMotionAsleep ? "indicate motion timer for asleep state" : "indicate no motion timer for asleep state"), isStateChange: true, displayed: false)
}

def updateOSwitchInd(switchOn)		{
	def vV = '--'
	def dD = "indicate no occupied switches"
	switch(switchOn)	{
		case 1:		vV = 'on';	dD = "indicate at least one occupied switch is on";		break
		case 0:		vV = 'off';	dD = "indicate all occupied switches is off";			break
	}
	sendEvent(name: 'oSwitchInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateASwitchInd(switchOn)		{
	def vV = '--'
	def dD = "indicate no asleep switches"
	switch(switchOn)	{
		case 1:		vV = 'on';	dD = "indicate at least one asleep switch is on";	break
		case 0:		vV = 'off';	dD = "indicate all asleep switches are off";		break
	}
	sendEvent(name: 'aSwitchInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateNSwitchInd(switchOn)		{
	def vV = '--'
	def dD = "indicate no night switches"
	switch(switchOn)	{
		case 1:		vV = 'on';	dD = "indicate at least one night switch is on";	break
		case 0:		vV = 'off';	dD = "indicate all night switches are off";			break
	}
	sendEvent(name: 'nSwitchInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateLSwitchInd(switchOn)		{
	def vV = '--'
	def dD = "indicate no locked switch"
	switch(switchOn)	{
		case 1:		vV = 'on';	dD = "indicate locked switch is on";	break
		case 0:		vV = 'off';	dD = "indicate locked switch is off";	break
	}
	sendEvent(name: 'lSwitchInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

def updateTurnAllOffInd(turnOff)		{
	sendEvent(name: 'turnAllOffInd', value: turnOff, descriptionText: "indicate if all switches should be turned off when no rules match", isStateChange: true, displayed: false)
}

def updateDimByLevelInd(dimBy, dimTo)		{
	sendEvent(name: 'dimByLevelInd', value: (dimBy == -1 && dimTo == -1 ? '-- / --' : "${(dimBy == -1 ? '--' : dimBy + '%')} /\n${(dimTo == -1 ? '--' : dimTo + '% ')}"), descriptionText: (dimBy == -1 && dimTo == -1 ? "indicate no dimming" : "indicate dimming by / to level"), isStateChange: true, displayed: false)
}

def updateAdjRoomsInd(aRooms)		{
	sendEvent(name: 'aRoomInd', value: (aRooms == -1 ? '--' : aRooms + '\nrooms'), descriptionText: (aRooms == -1 ? "indicate no adjacent rooms" : "indicate how many adjacent rooms"), isStateChange: true, displayed: false)
}

def updateWSSInd(wSS)		{
	sendEvent(name: 'wSSInd', value: (wSS == -1 ? '--' : wSS), descriptionText: (wSS == -1 ? "indicate no window shades" : "indicate window shade position"), isStateChange: true, displayed: false)
}

def updateAdjMotionInd(motionOn)		{
	def vV = 'none'
	def dD = "indicate no adjacent motion sensor"
	switch(motionOn)	{
		case 1:		vV = 'active';		dD = "indicate adjacent motion active";		break
		case 0:		vV = 'inactive';	dD = "indicate adjacent motion inactive";	break
	}
	sendEvent(name: 'aMotionInd', value: vV, descriptionText: dD, isStateChange: true, displayed: false)
}

private formatNumber(number)	{
	int n = number as Integer
	return (n > 0 ? String.format("%,d", n) : '')
}

// REMOVE TILL HERE FOR HUBITAT		<<<<<