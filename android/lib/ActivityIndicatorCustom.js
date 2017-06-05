'use strict';

import React from 'react';
import {
  ActivityIndicator,
  StyleSheet,
} from 'react-native';

const TimerMixin = require('react-timer-mixin');

export const ActivityIndicatorCustom = React.createClass({
  mixins: [TimerMixin],

  getInitialState() {
    return {
      animating: true,
    };
  },

  setToggleTimeout() {
    this.setTimeout(() => {
      this.setState({animating: !this.state.animating});
      this.setToggleTimeout();
    }, 2000);
  },

  componentDidMount() {
    this.setToggleTimeout();
  },

  render() {
    return (
      <ActivityIndicator
        animating={this.state.animating}
        style={[styles.centering, {height: 80}]}
        size="large"
      />
    );
  }
});


const styles = StyleSheet.create({
  centering: {
    alignItems: 'center',
    justifyContent: 'center',
    padding: 8,
  },
});