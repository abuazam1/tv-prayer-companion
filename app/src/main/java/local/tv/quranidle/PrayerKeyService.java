package local.tv.quranidle;

import android.accessibilityservice.*;
import android.content.*;
import android.os.*;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.util.Log;

/** Filters remote keys during the timed notice; never reads window contents. */
public class PrayerKeyService extends AccessibilityService {
 private final Handler h=new Handler(Looper.getMainLooper());
 private final BroadcastReceiver receiver=new BroadcastReceiver(){public void onReceive(Context c,Intent i){update();}};
 protected void onServiceConnected(){IntentFilter f=new IntentFilter(PrayerGuard.CHANGED);registerReceiver(receiver,f,Context.RECEIVER_NOT_EXPORTED);update();Log.i("PrayerKeys","connected");}
 private void update(){AccessibilityServiceInfo info=getServiceInfo();if(info==null)return;boolean active=PrayerGuard.active(this);if(active)info.flags|=AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;else info.flags&=~AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;setServiceInfo(info);h.removeCallbacksAndMessages(null);if(active)h.postDelayed(this::update,1000);Log.i("PrayerKeys",active?"guard-on":"guard-off");}
 protected boolean onKeyEvent(KeyEvent event){if(!PrayerGuard.active(this))return false;if(event.getKeyCode()==KeyEvent.KEYCODE_POWER||event.getKeyCode()==KeyEvent.KEYCODE_SLEEP||event.getKeyCode()==KeyEvent.KEYCODE_WAKEUP)return false;if(event.getAction()==KeyEvent.ACTION_DOWN)Log.i("PrayerKeys","blocked-key-"+event.getKeyCode());PrayerGuard.enforce(this);return true;}
 public void onAccessibilityEvent(AccessibilityEvent event){} public void onInterrupt(){}
 public void onDestroy(){try{unregisterReceiver(receiver);}catch(Exception ignored){}h.removeCallbacksAndMessages(null);super.onDestroy();}
}
