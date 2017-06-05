'use strict';

import React from 'react';
import {
  Text,
  View,
  TouchableHighlight,
  Modal,
  Switch,
  StyleSheet,
} from 'react-native';

const ButtonHighlightCustom = require('./ButtonHighlightCustom').ButtonHighlightCustom;

export var ModalCustom = React.createClass({
  getInitialState() {
    return {
      animationType: 'none',
      modalVisible: false,
      transparent: false,
    };
  },

  _setModalVisible(visible) {
    this.setState({modalVisible: visible});
  },

  _setAnimationType(type) {
    this.setState({animationType: type});
  },

  _toggleTransparent() {
    this.setState({transparent: !this.state.transparent});
  },

  render() {
    var modalBackgroundStyle = {
      backgroundColor: this.state.transparent ? 'rgba(0, 0, 0, 0.5)' : '#f5fcff',
    };
    var innerContainerTransparentStyle = this.state.transparent
      ? {backgroundColor: '#fff', padding: 20}
      : null;
    var activeButtonStyle = {
      backgroundColor: '#ddd'
    };

    return (
      <View>
        <Text>
          Modal实例
        </Text>
        <Modal
          animationType={this.state.animationType}
          transparent={this.state.transparent}
          visible={this.state.modalVisible}
          onRequestClose={() => {this._setModalVisible(false)}}
          >
          <View style={[styles.container, modalBackgroundStyle]}>
            <View style={[styles.innerContainer, innerContainerTransparentStyle]}>
              <Text>This modal was presented {this.state.animationType === 'none' ? 'without' : 'with'} animation.</Text>
              <ButtonHighlightCustom
                onPress={this._setModalVisible.bind(this, false)}
                style={styles.modalButton}>
                Close
              </ButtonHighlightCustom>
            </View>
          </View>
        </Modal>
        <View style={styles.row}>
          <Text style={styles.rowTitle}>Animation Type</Text>
          <ButtonHighlightCustom onPress={this._setAnimationType.bind(this, 'none')} style={this.state.animationType === 'none' ? activeButtonStyle : {}}>
            none
          </ButtonHighlightCustom>
          <ButtonHighlightCustom onPress={this._setAnimationType.bind(this, 'slide')} style={this.state.animationType === 'slide' ? activeButtonStyle : {}}>
            slide
          </ButtonHighlightCustom>
          <ButtonHighlightCustom onPress={this._setAnimationType.bind(this, 'fade')} style={this.state.animationType === 'fade' ? activeButtonStyle : {}}>
            fade
          </ButtonHighlightCustom>
        </View>

        <View style={styles.rowModal}>
          <Text style={styles.rowTitle}>Transparent</Text>
          <Switch value={this.state.transparent} onValueChange={this._toggleTransparent} />
        </View>
        <View style={styles.rowModal}>
          <ButtonHighlightCustom onPress={this._setModalVisible.bind(this, true)}>
             Present
          </ButtonHighlightCustom>
        </View>
      </View>
    );
  },
});

export class ModalCustomCommon extends React.Component {

  constructor(props) {
    super(props);
    this.state = {modalVisible: false};
  }

  setModalVisible(visible) {
    this.setState({modalVisible: visible});
  }

  render() {
    return (
      <View style={{marginTop: 22}}>
        <Modal
          animationType={"slide"}
          transparent={false}
          visible={this.state.modalVisible}
          onRequestClose={() => {alert("Modal has been closed.")}}
          >
         <View style={{marginTop: 22}}>
          <View>
            <Text>Hello World!</Text>

            <TouchableHighlight onPress={() => {
              this.setModalVisible(!this.state.modalVisible)
            }}>
              <Text>Hide Modal</Text>
            </TouchableHighlight>

          </View>
         </View>
        </Modal>

        <TouchableHighlight onPress={() => {
          this.setModalVisible(true)
        }}>
          <Text>Show Modal</Text>
        </TouchableHighlight>

      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    padding: 20,
  },
  innerContainer: {
    borderRadius: 10,
    alignItems: 'center',
  },
  row: {
    alignItems: 'center',
    flex: 1,
    flexDirection: 'row',
    marginBottom: 20,
  },
  rowModal: {
    alignItems: 'center',
    //flex: 1,
    //fexDirection: 'row',
    marginBottom: 20,
    marginTop: 30,
  },
  rowTitle: {
    //flex: 1,
    fontWeight: 'bold',
  },
  button: {
    borderRadius: 5,
    //flex: 1,
    height: 44,
    alignSelf: 'stretch',
    justifyContent: 'center',
    overflow: 'hidden',
  },
  buttonText: {
    fontSize: 18,
    margin: 5,
    textAlign: 'center',
  },
  modalButton: {
    marginTop: 10,
  },
});