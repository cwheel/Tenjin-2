package com.scelos.roomlaunch;

import android.app.Activity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

public class TenjinRoom {
    private Activity activity;
    private RequestQueue queue;

    //Set to the default internal access IP
    private String srv = "http://10.0.1.8:3000/";

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

    public static final String audioAlarm = "audio";
    public static final String lightAlarm = "light-light";

    private HashMap<String, Integer> context;
    private Boolean isUsingProxy = false;

    private ClientConnectionManager mClientConnectionManager;
    private HttpParams mHttpParams;
    private ThreadSafeClientConnManager mThreadSafeClientConnManager;
    private HttpStack httpStack;

    //Connect to the light controller from the internal network
    public TenjinRoom(Activity activ) {
        activity = activ;
        queue = Volley.newRequestQueue(activ);
    }

    //Connect to the light controller from a remote network via ProxyRoute
    public TenjinRoom(final Activity activ, String server, final String username, final String password) {
        srv = server;
        activity = activ;
        isUsingProxy = true;

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        DefaultHttpClient mDefaultHttpClient = new DefaultHttpClient();

        mClientConnectionManager = mDefaultHttpClient.getConnectionManager();
        mHttpParams = mDefaultHttpClient.getParams();
        mThreadSafeClientConnManager = new ThreadSafeClientConnManager(mHttpParams, mClientConnectionManager.getSchemeRegistry());

        mDefaultHttpClient = new DefaultHttpClient(mThreadSafeClientConnManager, mHttpParams);

        httpStack = new HttpClientStack(mDefaultHttpClient);

        queue = Volley.newRequestQueue(activ, httpStack);

        StringRequest login = new StringRequest(Request.Method.POST, srv + "login", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("AUTH_SUCCESS")) {
                    ((TenjinRoomDelegate) activ).roomLightProxyAuthSuccess();
                } else {
                    ((TenjinRoomDelegate) activ).roomLightProxyAuthFailure();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity.getApplicationContext(), "Could not connect to the room", Toast.LENGTH_SHORT).show();
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

        sendRequest(light, "val", String.valueOf(val));
    }

    public void setRGBWLight(String light, int r, int g, int b, int w) throws Exception {
        if (!light.contains("rgbw")) {
            throw new Exception("Invalid light type, use changeLight() instead");
        }

        HashMap<String, String> prams = new HashMap<>();
        prams.put("r", String.valueOf(r));
        prams.put("g", String.valueOf(g));
        prams.put("b", String.valueOf(b));
        prams.put("w", String.valueOf(w));
        sendRequest(light, prams);
    }

    public void saveLightingContext() {
        sendRequest("lights/cxsave");
    }

    public void restoreLightingContext() {
        sendRequest("lights/cxrestore");
    }

    public void fetchRoomAlarms(final TenjinRoomDelegate delegate) {
        StringRequest req = new StringRequest(Request.Method.GET, srv + "alarms/list", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // GsonBuilder builder = new GsonBuilder();
                //Object o = builder.create().fromJson(response, Object.class);
                JSONObject json = null;
                try {
                    json  = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                delegate.roomAlarmsUpdate(json);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity.getApplicationContext(), "Could not connect to the room", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(req);
    }

    public void addRoomAlarm(String name, String timeStamp, String prettyDate, String type) {
        HashMap<String, String> prams = new HashMap<>();
        prams.put("name", name);
        prams.put("date", timeStamp);
        prams.put("prettyDate", prettyDate);
        prams.put("type", type);

        sendRequest("alarms/new", prams);
    }

    public void removeRoomAlarm(String name) {
        sendRequest("alarms/remove", "name", name);
    }

    public void invalidateAlarm(String name) {
        sendRequest("alarms/invalidate", "name", name);
    }

    public void validateAlarm(String name) {
        sendRequest("alarms/validate", "name", name);
    }

    public void alarmOff() { sendRequest("alarms/off"); }

    public void setAlarmType(String name, String type) {
        HashMap<String, String> prams = new HashMap<>();
        prams.put("name", name);
        prams.put("type", type);

        sendRequest("alarms/settype", prams);
    }

    public void fetchLightingContext(final TenjinRoomDelegate caller) {
        StringRequest req = new StringRequest(Request.Method.GET, srv + "lights/cxfetch", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("LC_unreachable")) {
                    Toast.makeText(activity.getApplicationContext(), "Could not contact the lighting controller", Toast.LENGTH_SHORT).show();
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

                    caller.roomLightContextUpdated(context);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity.getApplicationContext(), "Could not connect to the room controller", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(req);
    }

    private void sendRequest(String request) {
        StringRequest req = new StringRequest(Request.Method.GET, srv +  request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("LC_unreachable")) {
                    Toast.makeText(activity.getApplicationContext(), "Could not contact the lighting controller", Toast.LENGTH_SHORT).show();
                } else if (response.equals("alarm_deleted") || response.equals("alarm_stored") || response.equals("alarm_invalidated") || response.equals("alarm_validated")) {
                    fetchRoomAlarms((TenjinRoomDelegate) activity);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(req);
    }

    private void sendRequest(String request, HashMap<String, String> prams) {
        request = request + "?";

        for (String key : prams.keySet()) {
            request = request + key + "=" + prams.get(key) + "&";
        }

        request = request.substring(0, request.length() - 1);
        sendRequest(request);
    }

    private void sendRequest(String request, String property, String value) {
        sendRequest(request + "?" + property + "=" + value);
    }
}
