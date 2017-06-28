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

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.feed.TopicDetail;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.async.ApplozicConversationCreateTask;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicommons.people.channel.Conversation;
import com.applozic.mobicommons.people.contact.Contact;
import com.google.firebase.messaging.RemoteMessage;
import com.kaaylabs.fcm.push.Config;
import com.kaaylabs.fcm.push.FcmManager;
import com.kaaylabs.fcm.push.interfaces.FcmListener;
import com.kaaylabs.fcm.push.util.Constants;

public class MainActivity extends AppCompatActivity implements FcmListener{

    private static final String TAG = MainActivity.class.getSimpleName();

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
        userId = "Anantharaj.Raja";
        userName = "Anantharaj";
        userEmail = "anantharaj.raja@kaaylabs.com";

//        userId = "thenmozhi.raja";
//        userName = "Arputharaj Buyer";
//        userEmail = "thenmozhi.raja@kaaylabs.com";

        user = new User();
        user.setUserId(userId); //userId it can be any unique user identifier
        user.setDisplayName(userName); //displayName is the name of the user which will be shown in chat messages
        user.setEmail(userEmail); //optional
        user.setImageLink("");//optional,pass your image link

        listener = new UserLoginTask.TaskListener() {

            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                // After successful registration with Applozic server the callback will come here
                //listChat(context);
                conversationChat(context);
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                // If any failure in registration the callback  will come here
                Log.e("exception", exception.getMessage() + "");
            }
        };

        chatBtn.setOnClickListener(v -> {
            new UserLoginTask(user, listener, getApplicationContext())
                    .execute((Void) null);
        });

    }

    private void listChat(Context context) {
        ApplozicSetting.getInstance(context).showStartNewButton();//To show contact list.

     /*   Contact contact1 = new Contact();
        contact1.setUserId("arputharaj.raja");
        contact1.setFullName("Arputharaj raja");
        contact1.setImageURL("R.drawable.applozic_ic_contact_picture_holo_light");
        contact1.setEmailId("arputharaj.raja@kaaylabs.com");

        Context context1 = getApplicationContext();
        AppContactService appContactService = new AppContactService(context1);
        appContactService.add(contact1);*/

        Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
        intent.putExtra(ConversationUIService.USER_ID, "Anantharaj.Raja");
        intent.putExtra(ConversationUIService.DISPLAY_NAME, "Anantharaj Buyer"); //put it for displaying the title.
        intent.putExtra(ConversationUIService.TAKE_ORDER, true); //Skip chat list for showing on back press
        //intent.putExtra(ConversationUIService.CONTEXT_BASED_CHAT, true);
        //intent.putExtra(ConversationUIService.CONVERSATION_ID, userId + "_2");
        startActivity(intent);


    }

    private void conversationChat(Context context) {
        ApplozicClient.getInstance(context).setContextBasedChat(true);
        ApplozicConversationCreateTask applozicConversationCreateTask = null;

        ApplozicConversationCreateTask.ConversationCreateListener conversationCreateListener =  new ApplozicConversationCreateTask.ConversationCreateListener() {
            @Override
            public void onSuccess(Integer conversationId, Context context) {

                //For launching the  one to one  chat
                Intent intent = new Intent(context, ConversationActivity.class);
                intent.putExtra("takeOrder", true);
                intent.putExtra(ConversationUIService.USER_ID, "thenmozhi.raja");//RECEIVER USERID
                intent.putExtra(ConversationUIService.DISPLAY_NAME, "Arputharaj Buyer");

//                intent.putExtra(ConversationUIService.USER_ID, "Anantharaj.Raja");//RECEIVER USERID
//                intent.putExtra(ConversationUIService.DISPLAY_NAME, "Anantharaj");

                intent.putExtra(ConversationUIService.DEFAULT_TEXT, "Hello I am interested in this car, Can we chat?");
                intent.putExtra(ConversationUIService.CONTEXT_BASED_CHAT, true);
                intent.putExtra(ConversationUIService.CONVERSATION_ID,conversationId);
                startActivity(intent);
            }

            @Override
            public void onFailure(Exception e, Context context) {

            }
        };
        Conversation conversation = buildConversation(); //From Step 1
        applozicConversationCreateTask = new ApplozicConversationCreateTask(this , conversationCreateListener ,conversation);
        applozicConversationCreateTask.execute((Void)null);
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

        PushNotificationTask pushNotificationTask = new PushNotificationTask(regId, notificationListener, this);
        pushNotificationTask.execute((Void) null);
    }

    private Conversation buildConversation() {

        //Title and subtitles are required if you are enabling the view for particular context.

        TopicDetail topic = new TopicDetail();
        topic.setTitle("Hyundai i20");//Your Topic title
        topic.setSubtitle("May be your car model");//Put Your Topic subtitle
        topic.setLink("Topic Image link if any");

        //You can set two Custom key-value pair which will appear on context view .

        topic.setKey1("Mileage  : ");
        topic.setValue1("18 kmpl");
        topic.setKey2("Price :");
        topic.setValue2("RS. 5.7 lakh");

        //Create Conversation.

        Conversation conversation = new Conversation();

        //SET UserId for which you want to launch chat or conversation

        conversation.setTopicId("2");
        conversation.setUserId("thenmozhi.raja");
        //conversation.setUserId("Anantharaj.Raja");
        //conversation.setTopicDetail(topic.getJson());
        return conversation;

    }

}
