Cordova 协议
====================

### 协议格式
App原生与H5间通讯采用Cordova协议，App提供对外接口，H5端需引入Cordova js，通过js 发送事件与原生进行交互。该App 采用Cordova 6.3.0版本

#### JS格式协议调用
JS端代码参考：`cordova.exec(SuccessCallback, FailCallback, Service, Action, Params);`

参数说明：

| 参数 | 备注 |
| --- |  --- |
| SuccessCallback |	JS 调用原生 成功后的回调方法，带一个string类型参数。有些协议原生是没有回调的，这时callback可以为空 |
| FailCallback | JS 调用原生 失败后的回调方法，带一个string类型参数。有些协议原生是没有回调的，这时callback可以为空 |
| Service | 调用原生的服务名 |
| Action | 调用原生的执行动作 （一个Service可能会有多个动作） |
| Params | JS调用时传递给原生的参数。Json Array 格式。即便不需要参数也请传递 `[]` |

#### URL 格式协议调用
URL格式协议适用于那些不需要返回值并且不需要回调的场景，调用方式比js方式简单便捷，只需要像超链接一样写在 a 标签的href中即可。

唯渡APP的URL格式：vdo://host/path?parameters 其中scheme必须是vdo，host和path根据不同的协议而定

### 通用协议

#### 页面刷新开关
说明： 该URL在App端是否可以下拉刷新。仅针对当前URL有效。针对任意URL，默认都是支持下拉刷新的，如果需禁用请在js中调用此方法。

注意：如在A url情况下，禁用刷新后，从A页面点击进入B页面，那么B页面依然是可以下拉刷新的。

| 参数 | 值 | 备注 |
| --- | --- | --- |
|Service | Common| |
|Action | refreshable| |
|Param | [boolean]| 只有一个boolean类型参数 true 启用刷新，false 禁用刷新 仅针对当前URL有效 |

```
cordova.exec(null, null, "Common", "refreshable", [true]);
```

#### 开启新的页面容器
说明：打开一个新的容器来打开指定URL连接

| 参数 | 值 | 备注 |
| --- | --- | --- |
|Service | Common| |
|Action | open| |
|Param | [url, title] | url：需要打开的URL链接，一定需要全路径，即包含http:// 或https:// 。<br /> title：可选，这个页面的默认标题 |

```
cordova.exec(null, null, "Common", "open", ['http://www.vip.com', "唯品会首页"]);
cordova.exec(null, null, "Common", "open", ['http://www.vip.com',]); //无需默认标题
```

#### 关闭当前容器
说明：关闭当前原生页面

| 参数 | 值 | 备注 |
| --- | --- | --- |
|Service | Common| |
|Action | close| |
|Param | []| 无参数 |

```
cordova.exec(null, null, "Common", "close", []);
```

#### 显示/隐藏App原生加载框
说明：显示/隐藏App原生加载框

| 参数 | 值 | 备注 |
| --- | --- | --- |
|Service | Common| |
|Action | loading| |
|Param | [true]	| 只有一个boolean类型参数 true显示，false关闭 |

```
cordova.exec(null, null, "Common", "loading", [true]);
```

#### 获取设备信息
说明：获取设备信息。

| 参数 | 值 | 备注 |
| --- | --- | --- |
|SuccessCallback | function| 回调并以JsonObject格式返回设备信息 |
|Service | Common| |
|Action | deviceInfo| |
|Param | []	| 无参数 |

回调参数的格式：

| key | 说明 |
| --- | --- |
|appVersion	| 当前APP的版本号 |
|deviceId | 设备id |
|ip	 | 当前的IP地址，可能为空 |

```
cordova.exec(function(json) {
    json.deviceId, // 设备id
    json.ip, // IP地址
    json.appVersion // APP版本号
}, null, "Common", "deviceInfo", []);
```

#### 显示原生标题栏开关
说明：该URL在App端是否可以显示标题栏。仅针对当前URL有效。针对任意URL，默认是显示原生标题栏的，如果需隐藏请在js中调用此方法。

| 参数 | 值 | 备注 |
| --- | --- | --- |
|Service | Common| |
|Action | showTitle| |
|Param | [true]	| 只有一个boolean类型参数 true 显示标题栏，false 隐藏标题栏 仅针对当前URL有效 |

```
cordova.exec(null, null, "Common", "showTitle", [false]);
```

#### Console Debug
说明：控制台debug。H5与App联调时可作为App端控制台输出信息。帮助快速定位信息。

| 参数 | 值 | 备注 |
| --- | --- | --- |
|Service | Common| |
|Action | debug | |
|Param | [logLevel, [logTag, message]] | logLevel： 日志打印级别 error / warn / debug / info  <br /> logTag: 日志标签。String类型。可选  <br /> message：日志信息 |

```
cordova.exec(null, null, "Common", "console", ['error', ['LOG_TAG', '这里是Android App打印出来的日志']]);
// 或
cordova.exec(null, null, "Common", "console", ['error', ['这里是Android App打印出来的日志']]);
```

#### 启动新页面并带参数返回 openPageForResult / closePageForResult
说明：该协议需相互配合使用。类似startActivityForResult，H5通过调用该协议，打开新的H5页面，然后在新页面做完处理后，调用下面的协议返回时，在该页面能执行js脚本，从而进行一些处理逻辑

场景举例：比如A页面点击某按钮，跳转到B表单页面，在表单页面提交了一部分信息后，用户返回到A页面，这时A页面的部分信息已经修改了，那么需要刷新该页面才能得到正确的显示，如果这里的跳转普通的url跳转，那么用户返回时并不会刷新A页面，造成显示错误。

这时就可以借助这两个协议来操作，用户在A页面点击按钮后，通过 openPageForResult 开启新页面打开B页面，在B页面完成表单提交后，调用 closePageForResult 关闭B页面，这样其实就返回到了A页面，但不同的是 A页面能收到相应的通知，Android原生会调用 `onPageResult` 的js方法通知给A页面，这样A页面可以在该方法中做相应的展示，比如刷新此页面等。通过这样的流程保证了业务逻辑的正确性

Tips：调用方一定需要一个名为 `onPageResult(requestCode, resultCode, jsonData)` 的js方法。
参数说明：requestCode：在调用openPageForResult时传递的 requestCode。resultCode：-1 调用closePageForResult方法后返回, 0 用户直接点击返回键或左上角返回按钮返回。jsonData：调用closePageForResult协议时传递的json参数，以同样的数据作为该方法的参数

openPageForResult数据格式：

| 参数 | 值 | 备注 |
| --- | --- | --- |
|Service | Common| |
|Action | openPageForResult| |
|Param | [json]	| json: JSON格式的参数. 如下：<br /> requestCode: 不大于10000的正整数。 <br /> url：跳转后的url地址 |

closePageForResult 格式：

| 参数 | 值 | 备注 |
| --- | --- | --- |
|Service | Common| |
|Action | closePageForResult| |
|Param | [json]	| json: JSON格式的参数. 该参数会作为onPageResult的一个参数回调给js |

```
// 页面A
cordova.exec(null, null, "Common", "openPageForResult", [{"requestCode": 1, "url":"http://BBBBBBBB"}]);
function onPageResult(requestCode, resultCode, json) {
    if (requestCode == 1) {
        if (resultCode == -1) { //B页面通过调用closePageForResult 协议返回的
            console.log(json);  //此例子中json的数据就是 {"success": "true"}
            ......              //其他逻辑处理
        } else if (resultCode == 0) {   //用户点击返回键或左上角返回按钮返回的。此时json数据为空
            console.log('用户返回');
        }
    }
}
// 页面B
cordova.exec(null, null, "Common", "closePageForResult", [{"success": "true"}]);
```

### 通知
通知协议的Service 是 “Notification”，根据Action的不同来区分不同的执行命令

#### Alert
说明：类JS的alert。弹出一个警告框

| 参数 | 值 | 备注 |
| --- | --- | --- |
|SuccessCallback | function| 用户点击确定后的回调方法 |
|Service | Notification| |
|Action | alert| |
|Param | [title, message, buttonLabel]	| title： 弹窗标题。<br /> message: 弹窗内容。<br /> buttonLabel： 确认按钮文字，可选，默认值 OK |

```
cordova.exec(successFuction, null, "Notification", "alert", ['通知', '您已经被我监控了，淡定的接受吧', '已悉知']);
// 或
cordova.exec(successFuction, null, "Notification", "alert", ['通知', '您已经被我监控了，淡定的接受吧']);
successFuction = function(message) {
    //用户点击了确认按钮后的操作
}
```

#### Confirm
说明：类JS的confirm。弹出一个确认框

| 参数 | 值 | 备注 |
| --- | --- | --- |
|SuccessCallback | function| 用户点击确定或取消按钮后的回调方法，根据回调参数不同来区分是确认（‘ok’）还是取消（‘cancel’）的点击。 |
|Service | Notification| |
|Action | confirm| |
|Param | [title, message, [okButtonLabe, cancelButtonLable]] | title： 弹窗标题。<br /> message: 弹窗内容。<br /> 第三个参数是一个json array， okButtonLabe：确认按钮文字，默认值 OK； cancelButtonLable：取消按钮文字，默认值 CANCEL。 |

```
cordova.exec(successFuction, null, "Notification", "confirm", ['警告', '您已经被我监控了，淡定的接受吧', ['淡定接受', '强烈拒绝']]);
// 或
cordova.exec(successFuction, null, "Notification", "confirm", ['警告', '您已经被我监控了，淡定的接受吧', []]); //按默认按钮文字 OK和CANCEL展示

successFuction = function(message) {
    if (messag == 'ok') {
        //用户点击了确认按钮后的操作
    } else if (message == 'cancel') {
        //用户点击了取消按钮后的操作
    }
}
```

#### Toast
说明：Toast样式的提示

| 参数 | 值 | 备注 |
| --- | --- | --- |
|Service | Notification| |
|Action | toast| |
|Param | [message] | message：提示文字 |

```
cordova.exec(null, null, "Notification", "toast", ['您已经被我监控了，哈哈哈']);
```

#### Beep 系统提示音
说明：手机系统提示音

| 参数 | 值 | 备注 |
| --- | --- | --- |
|Service | Notification| |
|Action | beep| |
|Param | [] |无参数 |

```
cordova.exec(null, null, "Notification", "beep", []);
```

### URL格式协议

#### 打开新页面
说明：通过新开容器打开新的url
格式： vdo://view/open?url=http://xxxxxxx
其中url是必须参数，是开启新容器需要打开的url路径，需要url encode

```
<a href="vdo://view/open?url=http://xxxxxxx">新页面打开</a>
```

#### 关闭当前容器
说明：关闭当前容器
格式： vdo://view/close
无参数

```
<a href="vdo://view/close">关闭页面</a>
```

#### 启动新页面并带参数返回 openPageForResult / closePageForResult
说明：该协议类似于Common的启动新页面并带参数返回协议，只不过通过URL的方式调用

openPageForResult：  
格式：vdo://view/openPageForResult?url=xxxx&requestCode=1
其中url是必须参数，是开启新容器需要打开的url路径，需要url encode; requestCode是请求的code。

closePageForResult：  
格式：vdo://view/closePageForResult?parameters
parameters 参数会以json格式作为onPageResult 的参数

```
// A 页面
<a href="vdo://view/openPageForResult?url=http%3a%2f%2f10.107.70.41%2fcordova%2fschema%2fsecond.html&requestCode=1">跳转并带参数返回</a>
<script type="text/javascript">
    function onPageResult(requestCode, resultCode, data) {
        if (requestCode == 1) {
            if (resultCode == -1) { //B页面通过调用closePageForResult 协议返回的
                console.log(json);  //此例子中json的数据就是 {"test": "1", "name":"hahh"}
                ......              //其他逻辑处理
            } else if (resultCode == 0) {   //用户点击返回键或左上角返回按钮返回的。此时json数据为空
                console.log('用户返回');
            }
        }
    }
</script>

// B页面
<a href="vdo://view/closePageForResult?test=1&name=hahh">关闭并带参数返回</a>
```
