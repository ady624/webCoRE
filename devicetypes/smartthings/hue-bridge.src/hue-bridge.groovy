/**
 *  Hue Bridge
 *
 *  Author: SmartThings
 */
// for the UI
metadata {
    // Automatically generated. Make future change here.
    definition (name: "Hue Bridge", namespace: "smartthings", author: "SmartThings") {
        attribute "serialNumber", "string"
        attribute "status", "string"
        attribute "networkAddress", "string"
    }

    simulator {
        // TODO: define status and reply messages here
    }
 
   tiles(scale: 2) {
       multiAttributeTile(name: "rich-control") {
           tileAttribute("device.status", key: "PRIMARY_CONTROL") {
               attributeState "Offline", label: '${currentValue}', action: "", icon: "st.Lighting.light99-hue", backgroundColor: "#ffffff"
               attributeState "Online", label: '${currentValue}', action: "", icon: "st.Lighting.light99-hue", backgroundColor: "#79b821"
           }
       }
       valueTile("doNotRemove", "v", decoration: "flat", height: 2, width: 6, inactiveLabel: false) {
           state "default", label: 'If removed, Hue lights will not work properly'
       }
       valueTile("serialNumber", "device.serialNumber", decoration: "flat", height: 2, width: 6, inactiveLabel: false) {
           state "default", label: 'SN: ${currentValue}'
       }
       valueTile("networkAddress", "device.networkAddress", decoration: "flat", height: 2, width: 6, inactiveLabel: false) {
           state "default", label: 'IP: ${currentValue}'
       }

       main(["rich-control"])
       details(["rich-control", "doNotRemove", "serialNumber", "networkAddress"])
   }
}

// parse events into attributes
def parse(description) {
    log.debug "Parsing '${description}'"
    def results = []
    def result = parent.parse(this, description)
    if (result instanceof physicalgraph.device.HubAction){
        log.trace "HUE BRIDGE HubAction received -- DOES THIS EVER HAPPEN?"
        results << result
    } else if (description == "updated") {
        //do nothing
        log.trace "HUE BRIDGE was updated"
    } else {
        def map = description
        if (description instanceof String)  {
            map = stringToMap(description)
        }
        if (map?.name && map?.value) {
            log.trace "HUE BRIDGE, GENERATING EVENT: $map.name: $map.value"
            results << createEvent(name: "${map.name}", value: "${map.value}")
        } else {
            log.trace "Parsing description"
            def msg = parseLanMessage(description)
            if (msg.body) {
                def contentType = msg.headers["Content-Type"]
                if (contentType?.contains("json")) {
                    def response = new groovy.json.JsonSlurper().parseText(msg.body)
                    log.trace "Bridge response: $msg.body"
                    if (response instanceof List)
                    {
                        response.each{
                            if (it?.success?."/groups/0/action/scene")
                            {
                                log.trace "Scene with id ${it?.success?."/groups/0/action/scene"} has been triggered"
                                parent.timedRefresh()
                            }
                        }
                    }
                    else if (response instanceof Map)
                    {
                        if (parent.state.inItemDiscovery)
                            log.trace "Get state for ${response.keySet()}"
                            log.info parent.itemListHandler(device.hub.id, msg.body)

                    } 
                }
                else if (contentType?.contains("xml")) {
                    log.debug "HUE BRIDGE ALREADY PRESENT"
                }
            }
        }
    }
    results
}