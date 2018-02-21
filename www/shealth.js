/*global cordova, module*/

module.exports = {
	greet: function (name, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SHealth", "greet", [name]);
    },
    connectToSHealth: function (name, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SHealth", "connectToSHealth", [name]);
    },
    callHealthPermissionManager: function (name, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SHealth", "callHealthPermissionManager", [name]);
    },
    getSleepData: function (name, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SHealth", "getSleepData", [name]);
    },
    getStepCountData: function (name, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SHealth", "getStepCountData", [name]);
    },
    getStepCountTrendData: function (name, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SHealth", "getStepCountTrendData", [name]);
    },
    startObserver: function(dataTypes, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SHealth", "startObserver", [dataTypes]);
    },
    checkPermission: function(name, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SHealth", "checkPermission", [name]);
    },
    stopObserver: function(name, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SHealth", "stopObserver", [name]);
    },
    disconnect: function(name, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "SHealth", "disconnect", [name]);
    }
};
