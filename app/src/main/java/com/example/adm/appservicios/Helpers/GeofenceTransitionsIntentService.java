package com.example.adm.appservicios.Helpers;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.adm.appservicios.Activity.MainActivity;
import com.example.adm.appservicios.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import static com.example.adm.appservicios.Activity.MapsActivity.CHANNEL_ID;
import static com.example.adm.appservicios.Activity.MapsActivity.notificationId;

public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService(){
        super("GeofenceService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()){
            Log.d("GeofenceService", "onHandleIntent: ERROR SERVICE");
            return;
        }
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String details = triggeringGeofences.get(0).getRequestId();
            Log.d("GeofenceService", "onHandleIntent: RequestId = " + details);
            notificar();
        }else{
            Log.d("GeofenceService", "onHandleIntent: ERROR");
        }
    }

    public void notificar(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.car)
                .setContentTitle("Llegaste a tu destino")
                .setContentText("Estás muy cerca de tu destino")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId, builder.build());
    }
}
