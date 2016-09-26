package edu.vesit.barclaysapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TwitterFragment extends Fragment {

    private View rootView;
    private Context mContext;
    private User user;

    @BindView(R.id.tvStatus) TextView mStatusView;
    @BindView(R.id.btnChangeStatus) Button mStatusButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_twitter, container, false);
        ButterKnife.bind(this, rootView);
        mContext = getActivity();
        user = PreferenceUtil.getUser(mContext);
        initializeTwitterFragment(user);
        EventBus.getDefault().register(this);
        return rootView;
    }

    private void initializeTwitterFragment(User user) {
        if(user.getTwitterToken() != null) {
            mStatusView.setText("You are logged in");
            mStatusButton.setText("Logout");
        }
        else {
            mStatusView.setText("You are not logged in");
            mStatusButton.setText("Login");
        }
    }

    @OnClick(R.id.btnChangeStatus)
    public void changeStatus() {
        if(user.getTwitterToken() != null) {

        }
        else {
            new GetTwitterOAuthUrlTask(mContext).execute();
        }
    }

    @Subscribe
    public void onEvent(ServerResponseEvent serverResponseEvent) {
        String oAuthUrl = serverResponseEvent.getServerResponse();
        if(oAuthUrl == null)
            Toast.makeText(mContext, "An error occurred", Toast.LENGTH_SHORT).show();
        else {
            DialogUtils.openTwitterLoginDialog(mContext, oAuthUrl);
        }
    };

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
