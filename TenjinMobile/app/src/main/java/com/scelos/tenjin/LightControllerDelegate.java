package com.scelos.tenjin;

import java.util.HashMap;

public interface LightControllerDelegate {
    public void lightControllerContextUpdated(HashMap context);
    public void lightControllerProxyAuthSuccess();
    public void lightControllerProxyAuthFailure();
}
