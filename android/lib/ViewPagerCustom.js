'use strict';

import React from 'react';
import {
  StyleSheet,
  Text,
  TouchableWithoutFeedback,
  TouchableOpacity,
  View,
  ViewPagerAndroid,
  Image,
} from 'react-native';

import type { ViewPagerScrollState } from 'ViewPagerAndroid';

var PAGES = 5;
var BGCOLOR = ['#fdc08e', '#fff6b9', '#99d1b7', '#dde5fe', '#f79273'];
/*
var IMAGE_URIS = [
  'http://130.1.11.143:9080/hmbank/collectWill/download/zxfile.do?fileName=20141008tleBaldridge001h990.jpg',
  'http://130.1.11.143:9080/hmbank/collectWill/download/zxfile.do?fileName=volcanicpillar_vetter_960.jpg',
  'http://130.1.11.143:9080/hmbank/collectWill/download/zxfile.do?fileName=m27_snyder_960.jpg',
  'http://130.1.11.143:9080/hmbank/collectWill/download/zxfile.do?fileName=PupAmulti_rot0.jpg',
  'http://130.1.11.143:9080/hmbank/collectWill/download/zxfile.do?fileName=lunareclipse_27Sep_beletskycrop4.jpg',
];
*/
var IMAGE_URIS = [
  require('../img/20141008tleBaldridge001h990.jpg'),
  require('../img/volcanicpillar_vetter_960.jpg'),
  require('../img/m27_snyder_960.jpg'),
  require('../img/PupAmulti_rot0.jpg'),
  require('../img/lunareclipse_27Sep_beletskycrop4.jpg'),
];
var LikeCount = React.createClass({
  getInitialState: function() {
    return {
      likes: 7,
    };
  },
  onClick: function() {
    this.setState({likes: this.state.likes + 1});
  },
  render: function() {
    var thumbsUp = '\uD83D\uDC4D';
    return (
      <View style={styles.likeContainer}>
        <TouchableOpacity onPress={this.onClick} style={styles.likeButton}>
          <Text style={styles.likesText}>
            {thumbsUp + ' Like'}
          </Text>
        </TouchableOpacity>
        <Text style={styles.likesText}>
          {this.state.likes + ' likes'}
        </Text>
      </View>
    );
  },
});

var Button = React.createClass({
  _handlePress: function() {
    if (this.props.enabled && this.props.onPress) {
      this.props.onPress();
    }
  },
  render: function() {
    return (
      <TouchableWithoutFeedback onPress={this._handlePress}>
        <View style={[styles.button, this.props.enabled ? {} : styles.buttonDisabled]}>
          <Text style={styles.buttonText}>{this.props.text}</Text>
        </View>
      </TouchableWithoutFeedback>
    );
  }
});

var ProgressBar = React.createClass({
  render: function() {
    var fractionalPosition = (this.props.progress.position + this.props.progress.offset);
    var progressBarSize = (fractionalPosition / (PAGES - 1)) * this.props.size;
    return (
      <View style={[styles.progressBarContainer, {width: this.props.size}]}>
        <View style={[styles.progressBar, {width: progressBarSize}]}/>
      </View>
    );
  }
});

export var ViewPagerCustom = React.createClass({
  statics: {
    title: '<ViewPagerAndroid>',
    description: 'Container that allows to flip left and right between child views.'
  },
  getInitialState: function() {
    return {
      page: 0,
      animationsAreEnabled: true,
      scrollEnabled: true,
      progress: {
        position: 0,
        offset: 0,
      },
    };
  },

  onPageSelected: function(e) {
    this.setState({page: e.nativeEvent.position});
  },

  onPageScroll: function(e) {
    this.setState({progress: e.nativeEvent});
  },

  onPageScrollStateChanged: function(state : ViewPagerScrollState) {
    this.setState({scrollState: state});
  },

  move: function(delta) {
    var page = this.state.page + delta;
    this.go(page);
  },

  go: function(page) {
    if (this.state.animationsAreEnabled) {
      this.viewPager.setPage(page);
    } else {
      this.viewPager.setPageWithoutAnimation(page);
    }

    this.setState({page});
  },

  render: function() {
    var pages = [];
    for (var i = 0; i < PAGES; i++) {
      var pageStyle = {
        backgroundColor: BGCOLOR[i % BGCOLOR.length],
        alignItems: 'stretch',
        padding: 20,
      };
      /*
      pages.push(
        <View key={i} style={pageStyle} collapsable={false}>
          <Image
            style={styles.image}
            source={{uri: IMAGE_URIS[i % BGCOLOR.length],  headers: {
                                                             Pragma: 'no-cache'
                                                           }}}
          />
          <LikeCount />
       </View>
      );
      */
      pages.push(
        <View key={i} style={pageStyle} collapsable={false}>
          <Image
            style={styles.image}
            resizeMode={'cover'}
            source={IMAGE_URIS[i % BGCOLOR.length]}
          />
          <LikeCount />
       </View>
      );
    }
    var { page, animationsAreEnabled } = this.state;
    return (
      <View style={styles.container}>
        <ViewPagerAndroid
          style={styles.viewPager}
          scrollEnabled={this.state.scrollEnabled}
          onPageScroll={this.onPageScroll}
          onPageSelected={this.onPageSelected}
          onPageScrollStateChanged={this.onPageScrollStateChanged}
          ref={viewPager => { this.viewPager = viewPager; }}>
          {pages}
        </ViewPagerAndroid>
        <View style={styles.buttons}>
          <Button
            enabled={true}
            text={this.state.scrollEnabled ? 'Scroll Enabled' : 'Scroll Disabled'}
            onPress={() => this.setState({scrollEnabled: !this.state.scrollEnabled})}
          />
        </View>
        <View style={styles.buttons}>
          { animationsAreEnabled ?
            <Button
              text="Turn off animations"
              enabled={true}
              onPress={() => this.setState({animationsAreEnabled: false})}
            /> :
            <Button
              text="Turn animations back on"
              enabled={true}
              onPress={() => this.setState({animationsAreEnabled: true})}
            /> }
          <Text style={styles.scrollStateText}>ScrollState[ {this.state.scrollState} ]</Text>
        </View>
        <View style={styles.buttons}>
          <Button text="Start" enabled={page > 0} onPress={() => this.go(0)}/>
          <Button text="Prev" enabled={page > 0} onPress={() => this.move(-1)}/>
          <Text style={styles.buttonText}>Page {page + 1} / {PAGES}</Text>
          <ProgressBar size={100} progress={this.state.progress}/>
          <Button text="Next" enabled={page < PAGES - 1} onPress={() => this.move(1)}/>
          <Button text="Last" enabled={page < PAGES - 1} onPress={() => this.go(PAGES - 1)}/>
        </View>
      </View>
    );
  },
});

var styles = StyleSheet.create({
  buttons: {
    flexDirection: 'row',
    height: 30,
    backgroundColor: 'black',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  button: {
    flex: 1,
    width: 0,
    margin: 5,
    borderColor: 'gray',
    borderWidth: 1,
    backgroundColor: 'gray',
  },
  buttonDisabled: {
    backgroundColor: 'black',
    opacity: 0.5,
  },
  buttonText: {
    color: 'white',
  },
  scrollStateText: {
    color: '#99d1b7',
  },
  container: {
    flex: 1,
    //backgroundColor: 'white',
    //width: 800,
    //height: 900,
  },
  image: {
    //width: 200,
    //height: 600,
    flex: 1,
    padding: 20,
  },
  likeButton: {
    backgroundColor: 'rgba(0, 0, 0, 0.1)',
    borderColor: '#333333',
    borderWidth: 1,
    borderRadius: 5,
    flex: 1,
    margin: 8,
    padding: 8,
  },
  likeContainer: {
    flexDirection: 'row',
  },
  likesText: {
    flex: 1,
    fontSize: 18,
    alignSelf: 'center',
  },
  progressBarContainer: {
    height: 10,
    margin: 10,
    borderColor: '#eeeeee',
    borderWidth: 2,
  },
  progressBar: {
    alignSelf: 'flex-start',
    flex: 1,
    backgroundColor: '#eeeeee',
  },
  viewPager: {
    flex: 1,
    //flexDirection: 'row',
    //alignItems: 'flex-start',
  },
});