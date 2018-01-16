var app = {
    // Application Constructor
    initialize: function() {
        document.getElementById('common-close').onclick = function() {
            cordova.exec(null, null, "Common", "close", []);
        }
    },

    success: function(message){
        cordova.exec(null, null, "Common", "console", ['warn', [message]]);
    },

    fail: function(message){
        cordova.exec(null, null, "Common", "console", ['error', [message]]);
    }
};

app.initialize();

window.onload = function() {
    setTimeout(onloadsuccess, 3000);
}

function onloadsuccess() {
    console.log("load success ");
//    cordova.exec(null, null, "Common", "refreshable", [false]);
    cordova.exec(null, null, "Common", "console", ['error', ['该页面禁用刷新']]);
    cordova.exec(null, null, "Common", "showTitle", [false]);
}