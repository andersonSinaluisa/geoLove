package com.today.geolove.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.today.geolove.Main.PrincipalActivity;
import com.today.geolove.Main.ui.home.HomeFragment;
import com.today.geolove.MainActivity;
import com.today.geolove.R;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.common.GooglePlayServicesUtil.getErrorString;
import static com.today.geolove.Preference.MainPreferences.id_geo;

public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();
    public static final int GEOFENCE_NOTIFICATION_ID = 0;

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        System.out.println("Servicio Iniciado");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if ( geofencingEvent.hasError() ) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode() );
            Log.e( TAG, errorMsg );
            return;
        }
        // Retrieve GeofenceTrasition
      //  System.out.println( geofencingEvent.getTriggeringLocation()+ " GEOLOCALIZACION");

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ) {
            // Get the geofence that were triggered
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            // Create a detail message with Geofences received

            boolean geofenceIDvalidation = getGeofenceTrasitionDetails(geofencingEvent.getTriggeringLocation(), triggeringGeofences );
            // Send notification details as a String
            int status = estado(geoFenceTransition);

            if(geofenceIDvalidation && status>0 ){
                sendNotification("no es tu radio");
            }else{
                sendNotification(" es tu radio");
            }
        }

    }


    // Create a detail message with Geofences received
    private boolean getGeofenceTrasitionDetails(Location triggeringLocation, List<Geofence> triggeringGeofences) {
        // get the ID of each geofence triggered
        boolean status = false;
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for ( Geofence geofence : triggeringGeofences ) {
            System.out.println("Mi geolocalizacion es ==>"+triggeringLocation+"  y el id del egeovalllado es ==>"+geofence.getRequestId());
            if(!triggeringLocation.equals(geofence.getRequestId())){

                status=true;
            }
            System.out.println(geofence.getRequestId()+"ID");

            triggeringGeofencesList.add( geofence.getRequestId() );
        }
        return status;
    }




    private int estado(int status){
        int estadoFinal = 0;
        if ( status == Geofence.GEOFENCE_TRANSITION_ENTER )
            estadoFinal= 1;
        else if ( status == Geofence.GEOFENCE_TRANSITION_EXIT )
            estadoFinal = -1;
        return estadoFinal;
    }

    // Send a notification
    private void sendNotification( String msg ) {
        Log.i(TAG, "sendNotification: " + msg );

        // Intent to start the main Activity
        Intent notificationIntent = new Intent(getApplicationContext(),PrincipalActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(PrincipalActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Creating and sending Notification
        NotificationManager notificatioMng =
                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificatioMng.notify(
                GEOFENCE_NOTIFICATION_ID,
                createNotification(msg, notificationPendingIntent));
    }

    // Create a notification
    private Notification createNotification(String msg, PendingIntent notificationPendingIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.RED)
                .setContentTitle(msg)
                .setContentText("Geofence Notification!")
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }

    // Handle errors
    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }
}
