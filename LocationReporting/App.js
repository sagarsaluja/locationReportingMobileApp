import React, { useEffect } from 'react';
import { Text, TouchableOpacity, View, NativeModules, NativeEventEmitter } from 'react-native';
import { LogBox } from 'react-native';

LogBox.ignoreAllLogs();

const { LocationModule } = NativeModules;
//the name here should be same as the one returned by the getName() method in LocationModule.java
const locationServiceEmitter = new NativeEventEmitter(LocationModule);

const App = () => {
  useEffect(() => {
    const locationUpdateListener = locationServiceEmitter.addListener('LOCATION_UPDATE', (location) => {
      console.log("Received location", location, location.latitude, location.longitude);
      setCurrentLocation([location.latitude, location.longitude])
    });

    return () => {
      locationUpdateListener.remove();
    };
  }, []);


  const [isReporting, setIsReporting] = React.useState(false);
  const [currentLocation, setCurrentLocation] = React.useState([]);
  const ShowLocation = () => {
    return (
      <View style={{ marginBottom: 120 }}>
        {isReporting && currentLocation && <ShowLatLon />}
      </View>
    )
  }

  const ShowLatLon = () => {
    return (
      <View>
        <Text>{`Latitude : ${currentLocation[0]}`}</Text>
        <Text>{`Longitude : ${currentLocation[1]}`}</Text>
      </View>
    )
  }

  const ShowButton = ({ isReporting }) => {
    if (!isReporting) {
      return (
        <View style={{ flexDirection: "row", flex: 1, alignItems: 'center' }}>
          <TouchableOpacity
            style={{ backgroundColor: 'green', flex: 1, alignItems: 'center', margin: 10, padding: 10 }}
            onPress={startLocationReporting}
          >
            <Text>START REPORTING</Text>
          </TouchableOpacity>
        </View>)
    }
    else {
      return (
        <View style={{ flexDirection: "row", flex: 1, alignItems: 'center' }}>
          <TouchableOpacity
            style={{ backgroundColor: 'red', flex: 1, alignItems: 'center', margin: 10, padding: 10 }}
            onPress={stopLocationReporting}
          >
            <Text>STOP REPORTING</Text>
          </TouchableOpacity>
        </View>
      )
    }

  }
  const startLocationReporting = async () => {
    setIsReporting(true);
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
    setIsReporting(false);
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
      <View style={{ alignItems: 'center', flex: 0.1, flexDirection: "row", marginTop: 200 }}>
        <Text>This is a location reporting app</Text>
      </View>
      <ShowButton isReporting={isReporting} />
      <ShowLocation />
    </View>
  );
};

export default App;
