'use strict';

import React from 'react';
import {
  Text,
  View,
  ListView,
  TouchableOpacity,
} from 'react-native';


export class ListViewDemo extends React.Component {
  constructor(props) {
    super(props);
    var ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
    this.state = {
        dataSource: ds.cloneWithRows(['row 1', 'row 2']),
    };
  }
  render() {
    return (
    <ListView
      dataSource={this.state.dataSource}
      renderRow={(rowData) => <Text>{rowData}</Text>}
    />
    );
  }
}

export class ListViewCustom extends React.Component {
    constructor(props){
        super(props);
        var ds = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});
        this.state = {
            dataSource: ds.cloneWithRows(this._genRows()),
        };
    }

    _genRows(){
        const dataBlob = [];
        for(let i = 0 ; i< 10 ; i ++ ){
            dataBlob.push("row"+i);
        }
        return dataBlob;
    }

    _pressRow(rowID){
        alert("selected"+rowID);
    }

    _renderRow(rowData, sectionID, rowID){
        return (
            <TouchableOpacity onPress={()=>this._pressRow(rowID)}>
                <View>
                <Text>{"rowData:"+rowData+"   rowId:"+rowID}</Text>
                </View>
            </TouchableOpacity>
            );
    }


    render(){
        return (
            <View style={{marginTop:20,}}>
                <Text>ListView实例</Text>
                <ListView dataSource={this.state.dataSource} renderRow={this._renderRow.bind(this)}/>
            </View>
            );
    }
};