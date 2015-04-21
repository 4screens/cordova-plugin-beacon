var exec = require('cordova/exec');

exports.status = function(success, error) {
    exec(success, error, "Beacon", "status", []);
};
