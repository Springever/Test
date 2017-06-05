
import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  Image,
  ScrollView,
  TouchableOpacity,
  TouchableHighlight,
} from 'react-native';

import Util from './util';
/**
音乐
**/
export default class MusicView extends Component{

  renderItem(imgSource,title){
    return(
      <TouchableHighlight underlayColor='#bec0c0' onPress={()=>console.log('...')}>
      <View style={styles.itemLayout} >
      <Image style={{width:45,height:45,}} source={imgSource}/>
      <Text style={{color:'#333333',fontSize:15}}>{title}</Text>
      </View>
      </TouchableHighlight>
    );
  }

  renderItem2(imgSource,title){
    return(
      <TouchableHighlight underlayColor='#bec0c0' onPress={()=>console.log('...')}>
      <View style={styles.itemLayout2} >
      <Image style={{width:25,height:25,}} source={imgSource}/>
      <Text style={{color:'#333333',fontSize:15,paddingLeft:15,}}>{title}</Text>
      </View>
      </TouchableHighlight>
    );
  }

  render(){
    return(
      <ScrollView style={styles.container}>
      {this.renderItem(require('../../img/music_icn_local.png'),'本地音乐')}
      {this.renderItem(require('../../img/music_icn_recent.png'),'最近播放')}
      {this.renderItem(require('../../img/music_icn_dld.png'),'下载管理')}
      {this.renderItem(require('../../img/music_icn_artist.png'),'我的歌手')}
      {this.renderItem2(require('../../img/music_icn_shoucang.png'),'我的收藏')}
      </ScrollView>
    );
  }
}

const styles=StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5FCFF',
  },
  itemLayout:{
    flex:1,
    height:55,
    padding:5,
    flexDirection:'row',
    alignItems:'center',
    borderBottomColor:'#ddd',
    borderBottomWidth:Util.pixel,
  },
  itemLayout2:{
    flex:1,
    height:55,
    padding:5,
    paddingLeft:15,
    flexDirection:'row',
    alignItems:'center',
    borderBottomColor:'#ddd',
    borderBottomWidth:Util.pixel,
  }

});
