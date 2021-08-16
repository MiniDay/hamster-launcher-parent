# HTTP API

|请求参数|内容|
|:---|:---|
|`Method`|`GET`|

|响应参数|内容|
|:---|:---|
|`Content-Type`|`application/json; charset=utf-8`|

# 下载

## 启动器

指定版本: `/download/launcher/1.0.0-SNAPSHOT`  
最新版本: `/download/launcher/latest`

## 客户端

指定版本: `/download/client/1.0.0-SNAPSHOT`  
最新版本: `/download/client/latest`

## 资源文件

Path: `/assets/hash`  
例: `/assets/bdf48ef6b5d0d23bbb02e17d04865216179f510a`

> 在？为什么不用 [BMCL API](https://bmclapidoc.bangbang93.com/) ?
> 
> `https://bmclapi2.bangbang93.com/assets`

## 库文件

Path: `libraries/name`  
例: `/libraries/com/mojang/blocklist/1.0.5/blocklist-1.0.5.jar`

> 在？为什么不用 [BMCL API](https://bmclapidoc.bangbang93.com/) ?
> 
> `https://bmclapi2.bangbang93.com/maven`

# 版本信息

## 启动器版本

指定版本: `/version/launcher/1.0.0-SNAPSHOT`

|响应参数|内容|参数类型|示例|
|:---|:---|:---|:---|
|`version`|版本号|String|1.0.0-SNAPSHOT|
|`info`|更新信息|String|第一版启动器|
|`url`|下载地址|String|https://api.airgame.net/download/launcher/1.0.0-SNAPSHOT|

最新版本: `/version/launcher/latest`

|响应参数|内容|参数类型|示例|
|:---|:---|:---|:---|
|`version`|版本号|String|1.0.0-SNAPSHOT|
|`info`|更新信息|String|第一版启动器|
|`url`|下载地址|String|https://api.airgame.net/download/launcher/1.0.0-SNAPSHOT|

## 获取客户端版本

指定版本: `/version/launcher/1.0.0-SNAPSHOT`

|响应参数|内容|参数类型|示例|
|:---|:---|:---|:---|
|`version`|版本号|String|1.0.0-SNAPSHOT|
|`info`|更新信息|String|第一版客户端|
|`fileModifies`|文件更改|Array[FileModify]|[FileModify](../../launcher-common/src/main/java/cn/hamster3/application/launcher/common/FileModify.java)|

最新版本: `/version/launcher/latest`

|响应参数|内容|参数类型|示例|
|:---|:---|:---|:---|
|`version`|版本号|String|1.0.0-SNAPSHOT|
