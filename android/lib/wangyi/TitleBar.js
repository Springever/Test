
import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  Image,
  TouchableOpacity,
} from 'react-native';

export default class TitleBar extends Component{

  onPress(position){
    this.props.onSelectItem&&this.props.onSelectItem(position);
  }

  render(){
    var select=this.props.onCurrentIndex;
    var image1;
    if(select===0){
    image1=  <Image  style={{width:60,height:60}}
      source={require('../../img/actionbar_discover_selected.png')}/>
    }else{
    image1=  <Image  style={{width:60,height:60}}
      source={require('../../img/actionbar_discover_normal.png')}/>
    }
    return(
      <View style={styles.layout}>
      <TouchableOpacity onPress={()=>this.onPress(1)}>
      <Image  style={styles.icon} source={require('../../img/actionbar_menu.png')}/>
      </TouchableOpacity>
      <View style={{flex:1,flexDirection:'row',alignItems:'center',justifyContent:'center'}}>
      <TouchableOpacity onPress={()=>this.onPress(2)}>
      {image1}
      </TouchableOpacity>
      <TouchableOpacity onPress={()=>this.onPress(3)}>
      <Image  style={{width:60,height:60}} source={select===1?require('../../img/actionbar_music_selected.png'):require('../../img/actionbar_music_normal.png')}/>
      </TouchableOpacity>
      <TouchableOpacity onPress={()=>this.onPress(4)}>
      <Image  style={{width:60,height:60}} source={select===2?require('../../img/actionbar_friends_selected.png'):require('../../img/actionbar_friends_normal.png')}/>
      </TouchableOpacity>
      </View>

      <View>
      <Image  style={styles.icon} source={require('../../img/actionbar_search.png')}/>
      </View>
      </View>
    );
  }


}

const styles=StyleSheet.create({
   layout:{
    height:56,
    backgroundColor:'#ce3d3a',
    flexDirection:'row',
    alignItems:'center',
    paddingLeft:15,
    paddingRight:15,
  },
  icon:{
    width:25,
    height:25,
  }
});
