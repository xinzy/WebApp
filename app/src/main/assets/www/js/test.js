var app = {
    // Application Constructor
    initialize: function() {
        document.getElementById('test-1').onclick = function() {
            cordova.exec(app.success, app.fail, "Test", "Test", ["haha"]);
        }

        //通知
        document.getElementById('notify-beep').onclick = function() {
            cordova.exec(null, null, "Notification", "beep", []);
        }
        document.getElementById('notify-vibrator').onclick = function() {
            cordova.exec(null, null, "Notification", "vibrator", []);
        }
        document.getElementById('notify-alert').onclick = function() {
            cordova.exec(app.success, app.fail, "Notification", "alert", ['通知', '这是一个通知', '已悉知']);
        }
        document.getElementById('notify-confirm').onclick = function() {
            cordova.exec(app.success, app.fail, "Notification", "confirm", ['警告', '您确认这么操作么', ['确认', '取消']]);
        }
        document.getElementById('notify-toast').onclick = function() {
            cordova.exec(null, null, "Notification", "toast", ['这里是js传递过来的toast消息']);
        }

        //通用
        document.getElementById('common-console').onclick = function() {
            cordova.exec(null, null, "Common", "console", ['error', ['打印日志']]);
        }
        document.getElementById('common-enable-refresh').onclick = function() {
            cordova.exec(null, null, "Common", "refreshable", [true]);
        }
        document.getElementById('common-disable-refresh').onclick = function() {
            cordova.exec(null, null, "Common", "refreshable", [false]);
        }
        document.getElementById('common-open').onclick = function() {
            cordova.exec(null, null, "Common", "open", ['file:///android_asset/www/second.html', "你猜猜"]);
        }
        document.getElementById('common-close').onclick = function() {
            cordova.exec(null, null, "Common", "close", []);
        }
        document.getElementById('common-new').onclick = function() {
            cordova.exec(null, null, "Common", "open", ['file:///android_asset/www/test.html', "按钮"]);
            cordova.exec(null, null, "Common", "close", []);
        } 
        document.getElementById('common-loading').onclick = function() {
            cordova.exec(null, null, "Common", "loading", [true]);
            setTimeout(app.closeLoading, 5000);
        }
        document.getElementById('common-device').onclick = function() {
            cordova.exec(function(data) {
                alert(JSON.stringify(data));
            }, null, "Common", "deviceInfo", []);
        }
        document.getElementById('common-open-for-result').onclick = function() {
            cordova.exec(null, null, "Common", "openPageForResult", [{"requestCode": 1, "url":"file:///android_asset/www/redirect/second.html"}]);
        }
    },

    success: function(message){
        cordova.exec(null, null, "Common", "console", ['warn', [message]]);
    },

    fail: function(message){
        cordova.exec(null, null, "Common", "console", ['error', [message]]);
    },

    closeLoading: function() {
        cordova.exec(null, null, "Common", "loading", [false]);
    }
};

app.initialize();

function onPageResult(requestCode, resultCode, data) {
    console.log(data);
    alert(data);
    return '123123123';
}