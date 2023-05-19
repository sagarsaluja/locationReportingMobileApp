import React, { useEffect, } from 'react';


import {
  Text,
  TouchableOpacity,
  View,
} from 'react-native';

import { NativeModules } from 'react-native';

const { LocationModule } = NativeModules;
const App = () => {
  return (
    <View style={{ flex: 1, }}>
      <View style={{ alignItems: 'center', flex: 1 }}>
        <Text>{"This is a location reporting app"}</Text>
      </View>
      <TouchableOpacity
        style={{ backgroundColor: "green", flex: 1, alignItems: 'center' }}
        onPress={async () => {
          console.log("starting location reporting", LocationModule, JSON.stringify(LocationModule));
          await LocationModule.startLocationService();
          await LocationModule.getConstants();

        }}
      >
        <Text>{"START REPORTING"}</Text>
      </TouchableOpacity>
    </View>
  )
}
export default App;




