'use strict';

import React from 'react';
import { PropTypes } from 'react';
import ReactNative from 'react-native';
import {
    ViewPropTypes,
    StyleSheet,
    UIManager,
    View,
    requireNativeComponent,
    DeviceEventEmitter,
} from 'react-native';

var RCT_EXPANDABLELISTVIEW_REF = 'expandableListView';

class ExpandableListView extends React.Component {
  static propTypes = {
    ...ViewPropTypes,
    layoutWidth:  PropTypes.number,
    layoutHeight: PropTypes.number,
  };

  render() {
    var expandableListViewStyles = [styles.container, this.props.style];
    var expandableListView =
      <RCTExpandableListView
        ref={RCT_EXPANDABLELISTVIEW_REF}
        key="expandableListViewKey"
        style={expandableListViewStyles}
        onExpandListViewClick={this.onExpandListViewClick}
      />;

    return (
      <View style={styles.container}>
        {expandableListView}
      </View>
    );
  }

  goExpand = () => {
    //console.log("1111");
    UIManager.dispatchViewManagerCommand(
      this.getExpandableListViewHandle(),
      UIManager.RCTExpandableListView.Commands.goExpand,
      null
    );
  };

  postMessageExpand = () => {
    UIManager.dispatchViewManagerCommand(
      this.getExpandableListViewHandle(),
      UIManager.RCTExpandableListView.Commands.postMessageExpand,
      null
    );
  };

  injectJavaScriptExpand = (data) => {
    UIManager.dispatchViewManagerCommand(
      this.getExpandableListViewHandle(),
      UIManager.RCTExpandableListView.Commands.injectJavaScriptExpand,
      [data]
    );
  };

  /**
   * We return an event with a bunch of fields including:
   *  url, title, loading, canGoBack, canGoForward
   */
  updateNavigationState = (event) => {
    if (this.props.onNavigationStateChange) {
      this.props.onNavigationStateChange(event.nativeEvent);
    }
  };

  getExpandableListViewHandle = () => {
    return ReactNative.findNodeHandle(this.refs[RCT_EXPANDABLELISTVIEW_REF]);
  };

  onExpandListViewClick = (event: Event) => {
    console.log("onExpandListViewClick------------->"+event.nativeEvent.url);
    var onExpandListViewClick = this.props.onExpandListViewClick;
    onExpandListViewClick && onExpandListViewClick(event);
    this.updateNavigationState(event);
  };
}

var RCTExpandableListView = requireNativeComponent('RCTExpandableListView', ExpandableListView, {});

var styles = StyleSheet.create({
  /*
  container: {
    flex: 1,
  },
  */
});

module.exports = ExpandableListView;