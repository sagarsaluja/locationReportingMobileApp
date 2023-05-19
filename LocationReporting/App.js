import React from 'react';
import { Text, TouchableOpacity, View, NativeModules } from 'react-native';

const { LocationModule } = NativeModules;

const App = () => {
  const startLocationService = async () => {
    try {
      console.log("Starting location reporting...");
      // await LocationModule.startLocationUpdates();
      await LocationModule.startLocationUpdates();

      console.log("Location reporting started.");
    } catch (error) {
      console.error("Error starting location reporting:", error);
    }
  };

  return (
    <View style={{ flex: 1 }}>
      <View style={{ alignItems: 'center', flex: 1 }}>
        <Text>This is a location reporting app</Text>
      </View>
      <TouchableOpacity
        style={{ backgroundColor: 'green', flex: 1, alignItems: 'center' }}
        onPress={startLocationService}
      >
        <Text>START REPORTING</Text>
      </TouchableOpacity>
    </View>
  );
};

export default App;
