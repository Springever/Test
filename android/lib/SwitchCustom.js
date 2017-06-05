'use strict';

import React from 'react';
import {
  Text,
  View,
  Switch,
  StyleSheet,
} from 'react-native';

export var SwitchCustom = React.createClass({
  getInitialState() {
    return {
      trueSwitchIsOn: true,
      falseSwitchIsOn: false,
    };
  },
  render() {
    return (
      <View style={styles.containerSwitch}>
        <Text>
           Swtich实例
        </Text>
        <Switch
          onValueChange={(value) => this.setState({falseSwitchIsOn: value})}
          style={{marginBottom:10,marginTop:10}}
          value={this.state.falseSwitchIsOn} />
        <Switch
          onValueChange={(value) => this.setState({trueSwitchIsOn: value})}
          value={this.state.trueSwitchIsOn} />
      </View>
    );
  }
});

const styles = StyleSheet.create({
  containerSwitch: {
    //flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
});