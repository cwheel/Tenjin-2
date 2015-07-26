package com.scelos.roomlaunch;

import java.util.HashMap;

public interface LightControllerDelegate {
    public void lightControllerContextUpdated(HashMap context);
    public void lightControllerProxyAuthSuccess();
    public void lightControllerProxyAuthFailure();
}
