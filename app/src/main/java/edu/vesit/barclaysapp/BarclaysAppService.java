package edu.vesit.barclaysapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;

public class BarclaysAppService extends Service implements TextToSpeech.OnInitListener  {

    private static final int NOTIFICATION_CODE = 8959;
    private static final String RECORD_BUTTON_CLICKED = "recordButtonClicked";
    private Context mContext;
    private SpeechRecognizer recognizer;
    private TextToSpeech tts;
    private User user;
    protected static final int RESULT_SPEECH = 1;
    private TweetEvent tweetEvent;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        tts = new TextToSpeech(mContext, this);
        user = PreferenceUtil.getUser(mContext);
        tweetEvent = null;
        recognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {
                Toast.makeText(mContext, "Error, please speak again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                String result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
                if(tweetEvent != null) {
                    tweetEvent.setMessage(result);
                    PythonServerUtils.requestTweet(mContext, tweetEvent);
                    tweetEvent = null;
                }
                else if(result.contains("transfer"))
                    DialogUtils.showEnterParameterDialog(mContext, "Paytm amount");
                else
                    PythonServerUtils.sendMessageToServer(mContext, result);
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                String result = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
                if(tweetEvent != null) {
                    tweetEvent.setMessage(result);
                    PythonServerUtils.requestTweet(mContext, tweetEvent);
                    tweetEvent = null;
                }
                else
                PythonServerUtils.sendMessageToServer(mContext, result);
            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        registerReceiver(handleRecordButtonClick, new IntentFilter(RECORD_BUTTON_CLICKED));
        EventBus.getDefault().register(this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(mContext, "Error while converting text to voice", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "Could not initialize TTS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(mContext, MainActivity.class);
        PendingIntent openBarclaysApp = PendingIntent.getActivity(mContext, NOTIFICATION_CODE, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent recordButtonIntent = new Intent(RECORD_BUTTON_CLICKED);
        PendingIntent recordButtonClicked = PendingIntent.getBroadcast(mContext, 1, recordButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setContentTitle("BarclaysApp")
                .setContentText("Running")
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(openBarclaysApp)
                .addAction(R.drawable.ic_record_voice_over_black_24px, "Record",recordButtonClicked);
        Notification n = builder.build();
        n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        startForeground(NOTIFICATION_CODE, n);
        return START_STICKY;
    }

    private final BroadcastReceiver handleRecordButtonClick = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent recognizeVoiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizer.startListening(recognizeVoiceIntent);
        }
    };

    @Override
    public void onDestroy() {
        stopForeground(false);
        unregisterReceiver(handleRecordButtonClick);
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(ServerResponseEvent serverResponseEvent) {
        speakOut(serverResponseEvent.getServerResponse());
        processResponse(serverResponseEvent.getServerResponse());
    };

    @Subscribe
    public void onEvent(TweetEvent tweetEvent) {
        Log.e("tweet event ", "received");
        this.tweetEvent = tweetEvent;
        speakOut("What do you wish to tweet ?");
        Intent recognizeVoiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizer.startListening(recognizeVoiceIntent);
    };

    private void speakOut(String response) {
        tts.speak(response, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void processResponse(String response) {
        if(response == null)
            Toast.makeText(mContext, "The server responded with an error", Toast.LENGTH_SHORT).show();
        else if(response.contains("account number"))
            DialogUtils.showEnterParameterDialog(mContext, "account_no");
        else if(response.contains("amount"))
            DialogUtils.showEnterParameterDialog(mContext, "amount");
        else if(response.contains("https")) {
            speakOut("Please sign in to your Twitter account");
            DialogUtils.openTwitterLoginDialog(mContext,response);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
