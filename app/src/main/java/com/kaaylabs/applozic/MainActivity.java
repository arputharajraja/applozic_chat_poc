package com.kaaylabs.applozic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicommons.people.contact.Contact;
import com.google.firebase.messaging.RemoteMessage;
import com.kaaylabs.fcm.push.Config;
import com.kaaylabs.fcm.push.FcmManager;
import com.kaaylabs.fcm.push.interfaces.FcmListener;
import com.kaaylabs.fcm.push.util.Constants;

public class MainActivity extends AppCompatActivity implements FcmListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private PushNotificationTask pushNotificationTask = null;

    Button chatBtn;
    User user;
    UserLoginTask.TaskListener listener;
    String userId;
    String userName;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FcmManager.getInstance(getApplicationContext()).registerListener(this);
        chatBtn = (Button) findViewById(R.id.chatBtn);
        userId = "muruganandham.rajavelu";
        userName = "Muruganantham";
        userEmail = "muruganandham.rajavelu@kaaylabs.com";

       /* userId = "arputharaj.raja";
        userName = "Arputharaj Buyer";
        userEmail = "arputharaj.raja@kaaylabs.com";*/

        user = new User();
        user.setUserId(userId); //userId it can be any unique user identifier
        user.setDisplayName(userName); //displayName is the name of the user which will be shown in chat messages
        user.setEmail(userEmail); //optional
        user.setImageLink("");//optional,pass your image link

        listener = new UserLoginTask.TaskListener() {

            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                // After successful registration with Applozic server the callback will come here
                ApplozicSetting.getInstance(context).showStartNewButton();//To show contact list.

                /*Contact contact1 = new Contact();
                contact1.setUserId("muruganandham.rajavelu");
                contact1.setFullName("Muruganantham");
                contact1.setImageURL("R.drawable.applozic_ic_contact_picture_holo_light");
                contact1.setEmailId("muruganandham.rajavelu@kaaylabs.com");*/

                Contact contact1 = new Contact();
                contact1.setUserId("arputharaj.raja");
                contact1.setFullName("Arputharaj raja");
                contact1.setImageURL("R.drawable.applozic_ic_contact_picture_holo_light");
                contact1.setEmailId("arputharaj.raja@kaaylabs.com");

            /*    Contact contact2 = new Contact();
                contact2.setUserId("bajibabu.shaik");
                contact2.setFullName("Bajibabu shaik");
                contact2.setImageURL("R.drawable.applozic_ic_contact_picture_holo_light");
                contact2.setEmailId("bajibabu.shaik@kaaylabs.com");*/

                Context context1 = getApplicationContext();
                AppContactService appContactService = new AppContactService(context1);
                appContactService.add(contact1);
                //appContactService.add(contact2);
                //appContactService.add(contact4);
                Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
              /*  intent.putExtra(ConversationUIService.USER_ID, userId);
                intent.putExtra(ConversationUIService.DISPLAY_NAME, userName); //put it for displaying the title.
                intent.putExtra(ConversationUIService.TAKE_ORDER,true); //Skip chat list for showing on back press*/
                startActivity(intent);
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                // If any failure in registration the callback  will come here
            }
        };

        chatBtn.setOnClickListener(v -> new UserLoginTask(user, listener, getApplicationContext())
                .execute((Void) null));
    }


    @Override
    public void onDeviceRegistered(String deviceToken) {
        Log.e(TAG, "FCM device token: " + deviceToken);
        FcmManager.getInstance(getApplicationContext()).subscribeTopic();
    }

    @Override
    public void onMessage(RemoteMessage remoteMessage) {
        Log.e(TAG, " Activity From: " + remoteMessage.getFrom());
    }

    @Override
    public void onFcmMessage(String message) {
        Toast.makeText(getApplicationContext(), "Push notification: " + message,
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayFireBaseRegId();
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFireBaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString(Constants.SHARED_KEY_TOKEN, null);
        Log.d(TAG, "FCM reg id: " + regId);

        PushNotificationTask.TaskListener notificationListener = new PushNotificationTask.TaskListener() {

            @Override
            public void onSuccess(RegistrationResponse registrationResponse) {
                Log.d(TAG, "registration success ");
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                Log.d(TAG, "registration failure ");
            }
        };

        pushNotificationTask = new PushNotificationTask(regId, notificationListener, this);
        pushNotificationTask.execute((Void) null);
    }

}
