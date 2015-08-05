package com.scelos.roomlaunch;

import android.app.Activity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

public class LightController {
    private Activity activity;
    private RequestQueue queue;

    //Set to the default internal access IP
    private String srv = "http://192.168.1.126:3000/";

    public static final String red1 = "lights/r1";
    public static final String green1 = "lights/g1";
    public static final String blue1 = "lights/b1";
    public static final String white1 = "lights/w1";
    public static final String red2 = "lights/r2";
    public static final String green2 = "lights/g2";
    public static final String blue2 = "lights/b2";
    public static final String white2 = "lights/w2";
    public static final String whiteMain = "lights/w3";
    public static final String red = "lights/r1and2";
    public static final String green = "lights/g1and2";
    public static final String blue = "lights/b1and2";
    public static final String whiteBeds = "lights/w1and2";
    public static final String superWhite1 = "lights/sw1";
    public static final String superWhite2 = "lights/sw2";
    public static final String superWhiteBeds = "lights/sw1and2";
    public static final String superWhiteAll = "lights/sw1and2and3";

    public static final String rgbw1 = "lights/rgbw1";
    public static final String rgbw2 = "lights/rgbw2";
    public static final String rgbw = "lights/rgbw1and2";

    private HashMap<String, Integer> context;
    private Boolean isUsingProxy = false;

    //Connect to the light controller from the internal network
    public LightController(Activity activ) {
        activity = activ;
        queue = Volley.newRequestQueue(activ);
    }

    //Connect to the light controller from a remote network via ProxyRoute
    public LightController(final Activity activ, String server, final String username, final String password) {
        srv = server;
        activity = activ;
        isUsingProxy = true;
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        queue = Volley.newRequestQueue(activ);

        StringRequest login = new StringRequest(Request.Method.POST, srv + "login", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                if (response.equals("AUTH_SUCCESS")) {
                    ((LightControllerDelegate) activ).lightControllerProxyAuthSuccess();
                } else {
                    ((LightControllerDelegate) activ).lightControllerProxyAuthFailure();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity.getApplicationContext(), "Could not connect to the lighting proxy", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            };
        };

        queue.add(login);
    }

    public void setLight(String light, int val) throws Exception {
        if (light.contains("rgbw")) {
            throw new Exception("Invalid light type, use changeRGBWLight() instead");
        }

        sendRequest(light + "?val=" + String.valueOf(val));
    }

    public void setRGBWLight(String light, int r, int g, int b, int w) throws Exception {
        if (!light.contains("rgbw")) {
            throw new Exception("Invalid light type, use changeLight() instead");
        }

        sendRequest(light + "?r=" + String.valueOf(r) + "&g=" + String.valueOf(g) + "&b=" + String.valueOf(b) + "&w=" + String.valueOf(w));
    }

    public void saveContext() {
        sendRequest("lights/cxsave");
    }

    public void restoreContext() {
        sendRequest("lights/cxrestore");
    }

    public void fetchContext(final LightControllerDelegate caller) {
        StringRequest req = new StringRequest(Request.Method.GET, srv + "lights/cxfetch", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("LC_unreachable")) {
                    Toast.makeText(activity.getApplicationContext(), "Could not contact the lights controller", Toast.LENGTH_SHORT).show();
                } else {
                    context = new HashMap<String, Integer>();
                    String[] vals = response.split(",");

                    context.put("white1", Integer.parseInt(vals[0]));
                    context.put("red1", Integer.parseInt(vals[1]));
                    context.put("green1", Integer.parseInt(vals[2]));
                    context.put("blue1", Integer.parseInt(vals[3]));
                    context.put("white2", Integer.parseInt(vals[4]));
                    context.put("red2", Integer.parseInt(vals[5]));
                    context.put("green2", Integer.parseInt(vals[6]));
                    context.put("blue2", Integer.parseInt(vals[7]));
                    context.put("white3", Integer.parseInt(vals[8]));

                    caller.lightControllerContextUpdated(context);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity.getApplicationContext(), "Could not connect to the lighting server", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(req);
    }

    private void sendRequest(String request) {
        StringRequest req = new StringRequest(Request.Method.GET, srv +  request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("LC_unreachable")) {
                    Toast.makeText(activity.getApplicationContext(), "Could not contact the lights controller", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity.getApplicationContext(), "Could not connect to the lighting server", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(req);
    }
}
