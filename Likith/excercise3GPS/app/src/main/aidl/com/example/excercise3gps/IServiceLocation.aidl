// IServiceLocation.aidl
package com.example.excercise3gps;

// Declare any non-default types here with import statements

interface IServiceLocation {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    double rpc_getLatitude( );
    double rpc_getLongitude( );
    double rpc_getDistance( );
    double rpc_getAverageSpeed( );
}
