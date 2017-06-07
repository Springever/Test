package com.example.test.widget.reactnative.expandableListView;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

/**
 * Created by Springever on 2017/6/6.
 */

public class ReactExpandListViewEvent extends Event<ReactExpandListViewEvent> {

    public static final String EVENT_NAME = "topMessage";

    private WritableMap mEventData;

    public ReactExpandListViewEvent(int viewId, WritableMap eventData) {
        super(viewId);
        mEventData = eventData;
    }

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), mEventData);
    }
}
