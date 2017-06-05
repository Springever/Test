'use strict';

import { PropTypes } from 'react';
import { requireNativeComponent, View } from 'react-native';

var iFace = {
  name: 'ExpandableTextView',
  /*
  propTypes: {
    src: PropTypes.string,
    borderRadius: PropTypes.number,
    resizeMode: PropTypes.oneOf(['cover', 'contain', 'stretch']),
    ...View.propTypes // 包含默认的View的属性
  },
  */
  propTypes: {
    layoutWidth:  PropTypes.number,
    layoutHeight: PropTypes.number,
    ...View.propTypes
  },
};

module.exports = requireNativeComponent('RCTExpandableListView', iFace);