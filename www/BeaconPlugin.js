var exec = require('cordova/exec')
  , channel = require('cordova/channel');

exports.receiveBeaconAppeared = function(success, error) {
  exec(success, error, "BeaconPlugin", "receiveBeaconAppeared", []);
};

exports.clearBeaconAppeared = function(success, error) {
  exec(success, error, "BeaconPlugin", "clearBeaconAppeared", []);
};

exports._listener = {};

exports.fireEvent = function( event ) {
  var args = Array.apply( null, arguments ).slice(1)
    , listener = this._listener[event];

  if( !this._listener[event] ) {
    return;
  }

  for( var i = 0, j = this._listener[event].length; i < j; i++) {
    var fn    = this._listener[event][i][0],
        scope = this._listener[event][i][1];

    fn.apply( scope, args );
  }
};

exports.on = function( event, callback ) {
  this._listener[event] = this._listener[event] || [];
  this._listener[event].push([ callback, window ]);
};

exports.off = function( event, callback ) {
  if( !this._listener[event] )
    return;

  for( var i = 0, j = this._listener[event].length; i < j; i++ ) {
    if( this._listener[event][0] == callback ) {
      this._listener[event].slice( i, 1 );
      break;
    }
  }
};

channel.deviceready.subscribe(function () {
  setTimeout(function() {
    exec(null, null, 'Beacon', 'deviceready', []);
  }, 1000);
});
