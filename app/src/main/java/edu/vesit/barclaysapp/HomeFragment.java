package edu.vesit.barclaysapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.ButterKnife;

public class HomeFragment extends Fragment {

    private View rootView;
    private Context mContext;
    private TextView statusText;
    private EditText messageText;
    private Button sendButton, recordButton, loginButton;
    protected static final int RESULT_SPEECH = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(rootView);
        mContext = getActivity();
        statusText = (TextView) rootView.findViewById(R.id.tvStatus);
        statusText.setText("Enter a message or press the record button");
        messageText = (EditText) rootView.findViewById(R.id.etMessage);
        sendButton = (Button) rootView.findViewById(R.id.btnSend);
        recordButton = (Button) rootView.findViewById(R.id.btnRecord);
        loginButton = (Button) rootView.findViewById(R.id.btnLogin);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    statusText.setText("Converting...");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(mContext, "Your device doesn't support Speech to Text", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, messageText.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(mContext, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    messageText.setText(text.get(0));
                    statusText.setText("Voice converted to speech successfully");
                }
                else {
                    messageText.setText("");
                    statusText.setText("Could not convert voice to text");
                }
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        mContext.stopService(new Intent(mContext, BarclaysAppService.class));
        super.onDestroy();
    }
}
