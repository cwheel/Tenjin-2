package com.scelos.tenjin;

import org.json.JSONObject;
import java.util.HashMap;

public interface TenjinRoomDelegate {
    public void roomLightContextUpdated(HashMap context);
    public void roomLightProxyAuthSuccess();
    public void roomLightProxyAuthFailure();
    public void roomAlarmsUpdate(JSONObject resp);
}
