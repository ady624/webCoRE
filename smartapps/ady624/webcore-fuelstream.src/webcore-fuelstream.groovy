private static String handle() { return "webCoRE" }
definition(
    namespace:"ady624",
    name:"${handle()} Fuel Stream",
    description: "Local container for fuel streams",
    author:"jp0550",
    category:"My Apps",
	iconUrl: "https://cdn.rawgit.com/ady624/${handle()}/master/resources/icons/app-CoRE.png",
	iconX2Url: "https://cdn.rawgit.com/ady624/${handle()}/master/resources/icons/app-CoRE@2x.png",
	iconX3Url: "https://cdn.rawgit.com/ady624/${handle()}/master/resources/icons/app-CoRE@3x.png",
    parent: "ady624:webCoRE"
)

preferences {
    page(name: "settingsPage")      
}

def settingsPage(){
 	dynamicPage(name: "settingsPage", title: "Settings", uninstall: true, install: true){
        section(){
            input "maxSize", "number", title: "Max size of all fuelStream data in KB", defaultValue: 95  
            
            def storageSize = (int)(state.toString().size() / 1024.0)
            paragraph("Current memory usage is ${storageSize}KB")
        }
    }    
}

def installed(){
    log.debug "Installed with settings $settings"
    initialize()
}

def updated(){
    log.debug "Updated with settings $settings"
 	initialize()
}

def createStream(settings){
    state.fuelStream = [i: settings.id, c: (settings.canister ?: ""), n: settings.name, w: 1, t: getFormattedDate(new Date())]
}

def initialize(){
    unsubscribe()
    unschedule()    
     
    getFuelStreamData()
    
    cleanFuelStreams()
}

def getFuelStreamData(){
    if(!state.fuelStreamData){
     	state.fuelStreamData = []   
    }
    return state.fuelStreamData
}	

def cleanFuelStreams(){    
    //ensure max size is obeyed
    def storageSize = (int)(state.toString().size() / 1024.0)
    def max = (settings.maxSize ?: 95).toInteger()
    
    if(storageSize > max){
        log.debug "Trim down fuel stream"     
        def points = getFuelStreamData().size()
        def averageSize = points > 0 ? storageSize/(double)points : 0      

        def pointsToRemove = averageSize > 0 ? (int)((storageSize - max) / (double)averageSize) : 0
        pointsToRemove = pointsToRemove > 0 ? pointsToRemove : 0

        log.debug "Size ${storageSize}KB Points ${points} Avg $averageSize Remove $pointsToRemove"
        def toBeRemoved = getFuelStreamData().sort { it.i }.take(pointsToRemove)
        getFuelStreamData().removeAll(toBeRemoved)                
    }
}

def updateFuelStream(req){
    def canister = req.c ?: ""
    def name = req.n
    def data = req.d
    def instance = req.i
    def source = req.s
 
    getFuelStreamData().add([d: data, i: (new Date()).getTime()])
    
    cleanFuelStreams()
}

def getFormattedDate(date = new Date()){
    def format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    format.format(date)
}

def getFuelStream(){
    state.fuelStream
}

def listFuelStreamData(){
    getFuelStreamData().collect{ it << [t: getFormattedDate(new Date(it.i))]}
}

def uninstalled(){
	parent.resetFuelStreamList()
}
