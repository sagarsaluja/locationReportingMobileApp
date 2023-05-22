import React from 'react';
import { Text, TouchableOpacity, View, NativeModules } from 'react-native';

const { LocationModule } = NativeModules;
//the name here should be same as the one returned by the getName() method in LocationModule.java

const App = () => {
  const startLocationReporting = async () => {
    try {
      console.log("Starting location reporting...");
      // await LocationModule.startLocationUpdates();
      await LocationModule.startLocationUpdates2();

      console.log("Location reporting started.");
    } catch (error) {
      console.error("Error starting location reporting:", error);
    }
  };
  const stopLocationReporting = async () => {
    try {
      console.log("Stopping location reporting...");
      // await LocationModule.startLocationUpdates();
      await LocationModule.stopLocationUpdates2();

      console.log("Location reporting stopped.");
    } catch (error) {
      console.error("Error starting location reporting:", error);
    }

  }

  return (
    <View style={{ flex: 1, alignItems: "center", }}>
      <View style={{ alignItems: 'center', flex: 0.1, flexDirection: "row", }}>
        <Text>This is a location reporting app</Text>
      </View>
      <View style={{ flexDirection: "row", flex: 2, alignItems: 'center' }}>

        <TouchableOpacity
          style={{ backgroundColor: 'green', flex: 1, alignItems: 'center', margin: 10, padding: 10 }}
          onPress={startLocationReporting}
        >
          <Text>START REPORTING</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={{ backgroundColor: 'red', flex: 1, alignItems: 'center', margin: 10, padding: 10 }}
          onPress={stopLocationReporting}
        >
          <Text>STOP REPORTING</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

export default App;
