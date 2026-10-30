package local.tv.quranidle;

import android.content.*;
import android.media.AudioManager;
import android.os.*;

/** A time-bounded guard: expires even when the overlay/service is interrupted. */
final class PrayerGuard {
 static final String CHANGED="local.tv.quranidle.GUARD_CHANGED";
 static boolean active(Context c){return c.getSharedPreferences("prayer_guard",0).getLong("until",0)>System.currentTimeMillis();}
 static void begin(Context c,long until){android.content.SharedPreferences p=c.getSharedPreferences("prayer_guard",0);AudioManager a=c.getSystemService(AudioManager.class);if(!p.getBoolean("saved",false)){boolean muted=a.isStreamMute(AudioManager.STREAM_MUSIC);p.edit().putBoolean("saved",true).putBoolean("muted",muted).putLong("until",until).commit();}else p.edit().putLong("until",until).apply();enforce(c);c.sendBroadcast(new Intent(CHANGED).setPackage(c.getPackageName()));}
 static void enforce(Context c){if(!active(c))return;AudioManager a=c.getSystemService(AudioManager.class);if(!a.isStreamMute(AudioManager.STREAM_MUSIC))a.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_MUTE,0);}
 static void end(Context c){android.content.SharedPreferences p=c.getSharedPreferences("prayer_guard",0);boolean saved=p.getBoolean("saved",false),muted=p.getBoolean("muted",false);p.edit().remove("until").remove("saved").remove("muted").commit();if(saved&&!muted)c.getSystemService(AudioManager.class).adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_UNMUTE,0);c.sendBroadcast(new Intent(CHANGED).setPackage(c.getPackageName()));}
}
