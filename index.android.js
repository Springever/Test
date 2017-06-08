'use strict';

import React from 'react';
import {
  AppRegistry,
  Text,
  View,
  Button,
  StyleSheet,
  Dimensions,
} from 'react-native';

import {
  StackNavigator,
  TabNavigator,
  DrawerNavigator,
} from 'react-navigation';

import CustomViewPager from './android/lib/CustomViewPager';
import ViewPagerInAndroid from './android/lib/ViewPagerInAndroid';
import ToastTest from './android/lib/ToastTest';

const PickerCustom = require('./android/lib/PickerCustom').PickerCustom;
const PickerWidget = require('./android/lib/PickerCustom').PickerWidget;
const ActivityIndicatorCustom = require('./android/lib/ActivityIndicatorCustom').ActivityIndicatorCustom;
const ListViewCustom = require('./android/lib/ListViewCustom').ListViewCustom;
const SwitchCustom = require('./android/lib/SwitchCustom').SwitchCustom;
const ModalCustom = require('./android/lib/ModalCustom').ModalCustom;
const RefreshControlCustom = require('./android/lib/RefreshControlCustom').RefreshControlCustom;
const SlidingCustomComplete = require('./android/lib/SliderCustom').SlidingCustomComplete;
const SlidingCustom = require('./android/lib/SliderCustom').SlidingCustom;
const StatusBarStaticAndroid = require('./android/lib/StatusBarCustom').StatusBarStaticAndroid;
const TokenizedText = require('./android/lib/TextInputCustom').TokenizedText;
const AutoExpandingTextInput = require('./android/lib/TextInputCustom').AutoExpandingTextInput;
const ViewPagerCustom = require('./android/lib/ViewPagerCustom').ViewPagerCustom;
const WebViewCustom = require('./android/lib/WebViewCustom').WebViewCustom;
var ViewPagerModule = require('./android/lib/ViewPagerModule');
var AlertCustom = require('./android/lib/AlertCustom').SimpleAlertExampleBlock;
var ExpandableListView = require('./android/lib/ExpandableListView');

var RCT_EXPANDABLELISTVIEW_REF = 'expandableListView';
const {width, height} = Dimensions.get('window');

class HomeScreen extends React.Component {
  static navigationOptions = {
    title: 'Welcome',
  };
  render() {
    const { navigate } = this.props.navigation;
    return (
      <View>
        <Text>Hello, Chat App!</Text>
        <Button
          onPress={() => navigate('Chat', { user: 'Lucy' })}
          title="Chat with Lucy"
        />
      </View>
    );
  }
}

class ChatScreen extends React.Component {
  // Nav options can be defined as a function of the screen's props:
  /*
  static navigationOptions = ({ navigation }) => ({
    title: `Chat with ${navigation.state.params.user}`,
    headerRight: <Button title="Info" />,
  });
  */
  static navigationOptions = ({ navigation }) => {
    const {state, setParams} = navigation;
    const isInfo = state.params.mode === 'info';
    const {user} = state.params;
    return {
      title: isInfo ? `${user}'s Contact Info` : `Chat with ${state.params.user}`,
      headerRight: (
        <Button
          title={isInfo ? 'Done' : `${user}'s info`}
          onPress={() => setParams({ mode: isInfo ? 'none' : 'info'})}
        />
      ),
    };
  };
  render() {
    // The screen's current route is passed in to `props.navigation.state`:
    const { params } = this.props.navigation.state;
    return (
      <View>
        <Text>Chat with {params.user}</Text>
      </View>
    );
  }
}

class RecentChatsScreen extends React.Component {
  render() {
    return (
       <View>
         <Text>List of recent chats</Text>
         <Button
            onPress={() => this.props.navigation.navigate('Chat', { user: 'Lucy' })}
            title="Chat with Lucy"
         />
       </View>
    );
  }
}

class AllContactsScreen extends React.Component {
  render() {
    return <Text>List of all contacts</Text>
  }
}

const MainScreenNavigator = TabNavigator({
  Recent: { screen: RecentChatsScreen },
  All: { screen: AllContactsScreen },
});

class MyHomeScreen extends React.Component {
  static navigationOptions = {
    tabBarLabel: 'Home',
    // Note: By default the icon is only shown on iOS. Search the showIcon option below.
    tabBarIcon: ({ tintColor }) => (
      <Image
        source={require('./android/img/chats-icon.png')}
        style={[styles.icon, {tintColor: tintColor}]}
      />
    ),
  };
  render() {
    //this.props.navigation.navigate('DrawerOpen');
    //console.disableYellowBox = true;//屏蔽警告（让客户端不显示）
    //console.warn('YellowBox is disabled.');
    return (
      <View>
        <Button
          onPress={() => this.props.navigation.navigate('Notifications')}
          title="Go to notifications"
        />
        <Button
           onPress={() => this.props.navigation.navigate('Chat', { user: 'Lucy' })}
           title="Chat with Lucy"
        />
       <Button
            title="Go to DrawerNavigator"
            onPress={() => this.props.navigation.navigate('DrawerOpen')}
       />
       <PickerCustom />
       <ActivityIndicatorCustom />
       <ListViewCustom />
       <SwitchCustom />
       <ModalCustom />
       <RefreshControlCustom />
      </View>
    );
  }
}
//       <ListViewSimpleExample />
//       <ModalExampleCommon />
// <View style={{flex:1,}}>
//       <PickerWidget options={()=>["android","ios","reactNative"]}/>
class MyNotificationsScreen extends React.Component {
  static navigationOptions = {
    tabBarLabel: 'Notifications',
    tabBarIcon: ({ tintColor }) => (
      <Image
        source={require('./android/img/notif-icon.png')}
        style={[styles.icon, {tintColor: tintColor}]}
      />
    ),
  };

  render() {
    //const TextInputCustom = textInputCustomType.map((title, render) => {
   //     return render;
    //});
    return (
        <View>
          <Button
            onPress={() => this.props.navigation.goBack()}
            title="Go back home"
          />
          <View>
            <Text>Sliding例子</Text>
            <SlidingCustomComplete />
          </View>
          <View>
            <Text>StatusBar例子</Text>
            <StatusBarStaticAndroid />
          </View>
          <View>
            <Text>TextInput例子</Text>
            <AutoExpandingTextInput
                placeholder="height increases with content"
                enablesReturnKeyAutomatically={true}
                returnKeyType="done"
            />
            <TokenizedText />
          </View>
        </View>
    );
  }
}

class WebViewScreen extends React.Component {
  static navigationOptions = {
    tabBarLabel: 'WebViewScreen',
    tabBarIcon: ({ tintColor }) => (
      <Image
        source={require('./android/img/notif-icon.png')}
        style={[styles.icon, {tintColor: tintColor}]}
      />
    ),
  };

  render() {
    return (
        <WebViewCustom />
    );
  }
}

class SubScreen extends React.Component {
  static navigationOptions = {
    tabBarLabel: 'SubScreen',
    tabBarIcon: ({ tintColor }) => (
      <Image
        source={require('./android/img/notif-icon.png')}
        style={[styles.icon, {tintColor: tintColor}]}
      />
    ),
  };
  goExpand = () => {
    this.refs[RCT_EXPANDABLELISTVIEW_REF].goExpand();
  };

  postMessageExpand = () => {
    this.refs[RCT_EXPANDABLELISTVIEW_REF].postMessageExpand();
  };

  injectJavaScriptExpand = () => {
    this.refs[RCT_EXPANDABLELISTVIEW_REF].injectJavaScriptExpand();
  };

  showToastTest = ()=>{
    ToastTest.show('Hello ToastTest', ToastTest.SHORT);
  };

  onNavigationStateChange = (navState) => {
    this.setState({
      target: navState.target,
      width: navState.width,
      url: navState.url,
      status: navState.title,
      loading: navState.loading,
    });
  };

  render() {
    return (
        <View>
            <Text onPress={this.showToastTest}>Alert测试</Text>
            <AlertCustom style={{flex:1,}}/>
            <Text onPress={this.goExpand}>ExpandableListView测试</Text>
            <ExpandableListView
                ref={RCT_EXPANDABLELISTVIEW_REF}
                style={{width:width,height:height-500,alignItems:"flex-end"}}
                layoutWidth={900}
                layoutHeight={900}
                onNavigationStateChange={this.onNavigationStateChange}
            />
        </View>
    );
  }
}

const MyApp = TabNavigator({
  Home: {
    screen: MyHomeScreen,
  },
  Notifications: {
    screen: MyNotificationsScreen,
  },
  Sub: {
    screen: SubScreen,
  },
  WebView: {
    screen: WebViewScreen,
  },
}, {
  tabBarOptions: {
    activeTintColor: '#e91e63',
  },
});

const MyApp_draw = DrawerNavigator({
  Home: {
    screen: MyHomeScreen,
  },
  Notifications: {
    screen: MyNotificationsScreen,
  },
});

MainScreenNavigator.navigationOptions = {
  title: 'My Chats',
};

MyApp.navigationOptions = ({ navigation }) => ({
  title: 'My Chats',
  headerRight: (
       <Button
            title="Go to DrawerNavigator"
            onPress={() => navigation.navigate('DrawerOpen')}
       />
  ),
});

const ReactNativePage = StackNavigator({
  //Home: { screen: MainScreenNavigator },
  Home: { screen: MyApp },
  Chat: { screen: ChatScreen },
  Draw: { screen: MyApp_draw },
});

class WangYi extends React.Component {

  render(){
    return (
      <ViewPagerInAndroid/>
    );
  }
}

class ViewPageForAndroid extends React.Component {

  render(){
    return (
      <ViewPagerCustom />
    );
  }
}

const styles = StyleSheet.create({
  icon: {
    width: 26,
    height: 26,
  },
  gray: {
    backgroundColor: '#cccccc',
  },
  horizontal: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    padding: 8,
  },
  thumb: {
    width: 64,
    height: 64,
  },
  text: {
    flex: 1,
  },
});

//AppRegistry.registerComponent('ReactNativeActivity', () => WangYi);
//AppRegistry.registerComponent('ReactNativeActivity', () => ViewPageForAndroid);
AppRegistry.registerComponent('ReactNativeActivity', () => ReactNativePage);