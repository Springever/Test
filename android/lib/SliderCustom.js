'use strict';

import React from 'react';
import {
  Slider,
  Text,
  StyleSheet,
  View,
} from 'react-native';

export var SliderCustom = React.createClass({
  getDefaultProps() {
    return {
      value: 0,
    }
  },

  getInitialState() {
    return {
      value: this.props.value,
    };
  },

  render() {
    return (
      <View>
        <Text style={styles.text} >
          {this.state.value && +this.state.value.toFixed(3)}
        </Text>
        <Slider
          {...this.props}
          onValueChange={(value) => this.setState({value: value})} />
      </View>
    );
  }
});

export var SlidingCustomComplete = React.createClass({
  getInitialState() {
    return {
      slideCompletionValue: 0,
      slideCompletionCount: 0,
    };
  },

  render() {
    return (
      <View>
        <SliderCustom
          {...this.props}
          onSlidingComplete={(value) => this.setState({
              slideCompletionValue: value,
              slideCompletionCount: this.state.slideCompletionCount + 1})} />
        <Text>
          Completions: {this.state.slideCompletionCount} Value: {this.state.slideCompletionValue}
        </Text>
      </View>
    );
  }
});

var styles = StyleSheet.create({
  slider: {
    height: 10,
    margin: 10,
  },
  text: {
    fontSize: 14,
    textAlign: 'center',
    fontWeight: '500',
    margin: 10,
  },
});

exports.title = '<Slider>';
exports.displayName = 'SliderCustom';
exports.description = 'Slider input for numeric values';
exports.sliderType = [
  {
    title: 'Default settings',
    render(): ReactElement<any> {
      return <SliderCustom />;
    }
  },
  {
    title: 'Initial value: 0.5',
    render(): ReactElement<any> {
      return <SliderCustom value={0.5} />;
    }
  },
  {
    title: 'minimumValue: -1, maximumValue: 2',
    render(): ReactElement<any> {
      return (
        <SliderCustom
          minimumValue={-1}
          maximumValue={2}
        />
      );
    }
  },
  {
    title: 'step: 0.25',
    render(): ReactElement<any> {
      return <SliderCustom step={0.25} />;
    }
  },
  {
    title: 'onSlidingComplete',
    render(): ReactElement<any> {
      return (
          <SlidingCustomComplete />
      );
    }
  },
  {
    title: 'Custom min/max track tint color',
    platform: 'ios',
    render(): ReactElement<any> {
      return (
        <SliderCustom
          minimumTrackTintColor={'red'}
          maximumTrackTintColor={'green'}
        />
      );
    }
  },
  /*
  {
    title: 'Custom thumb image',
    platform: 'ios',
    render(): ReactElement<any> {
      return <SliderCustom thumbImage={require('./uie_thumb_big.png')} />;
    }
  },
  {
    title: 'Custom track image',
    platform: 'ios',
    render(): ReactElement<any> {
      return <SliderCustom trackImage={require('./slider.png')} />;
    }
  },
  {
    title: 'Custom min/max track image',
    platform: 'ios',
    render(): ReactElement<any> {
      return (
        <SliderCustom
          minimumTrackImage={require('./slider-left.png')}
          maximumTrackImage={require('./slider-right.png')}
        />
      );
    }
  },
  */
];