package edu.vesit.barclaysapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.melnykov.fab.FloatingActionButton;
import com.moxtra.binder.sdk.InviteToChatCallback;
import com.moxtra.binder.sdk.MXException;
import com.moxtra.binder.sdk.OnEndMeetListener;
import com.moxtra.binder.ui.drawable.CircleTransform;
import com.moxtra.mxvideo.MXCPUMemoryUtil;
import com.moxtra.sdk.MXChatCustomizer;
import com.moxtra.sdk.MXChatManager;
import com.moxtra.sdk.MXGroupChatMember;
import com.moxtra.sdk.MXGroupChatSession;
import com.moxtra.sdk.MXGroupChatSessionCallback;
import com.moxtra.sdk.MXSDKException;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatFragment extends Fragment implements MXChatManager.OnCreateChatForFragmentListener, MXChatManager.OnOpenChatForFragmentListener {

    private View rootView;
    private Context mContext;

    private static final String TAG = "ChatList";

    @BindView(R.id.rvChatList) RecyclerView chatListRecyclerView;

    private ChatFragment.ChatListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private User selectedUser;
    private User currentlyLoggedInUser;
    private List<User> userLisToSelect;
    private DatabaseReference mDatabase;
    private CustomInterface delegate;

    public ChatFragment(CustomInterface delegate) {
        this.delegate = delegate;
    }

    public ChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, rootView);
        mContext = getActivity();
        currentlyLoggedInUser = PreferenceUtil.getUser(mContext);
        chatListRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        chatListRecyclerView.setLayoutManager(layoutManager);
        adapter = new ChatFragment.ChatListAdapter();
        chatListRecyclerView.setAdapter(adapter);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        loadAllAvailableUsers();

        MXChatCustomizer.setOnMeetEndListener(new OnEndMeetListener() {
            @Override
            public void onMeetEnded(String meetId) {
                adapter.refreshData();
            }

            @Override
            public void onMeetEndFailed(int errorCode, String errorMessage) {
                Log.e(TAG, "onMeetEndFailed() called with: " + "errorCode = [" + errorCode + "], errorMessage = [" + errorMessage + "]");
            }
        });

        MXChatManager.getInstance().setGroupChatSessionCallback(new MXGroupChatSessionCallback() {
            @Override
            public void onGroupChatSessionCreated(MXGroupChatSession session) {
                adapter.refreshData();
            }

            @Override
            public void onGroupChatSessionUpdated(MXGroupChatSession session) {
                adapter.refreshData();
            }

            @Override
            public void onGroupChatSessionDeleted(MXGroupChatSession session) {
                adapter.refreshData();
            }
        });

        MXChatCustomizer.setChatInviteCallback(new InviteToChatCallback() {
            @Override
            public void onInviteToChat(String binderID, Bundle extras) {
                Log.e(TAG, "Invite to binder: " + binderID);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.refreshData();
    }

    @OnClick(R.id.fabAdd)
    public void addChat() {
        new MaterialDialog.Builder(mContext)
                .title(R.string.selectUserTitle)
                .items(getUserList())
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        selectedUser = userLisToSelect.get(i);
                        String topic = getTopic();
                        try {
                            //MXChatManager.getInstance().createChatForFragment(topic, Arrays.asList(new String[]{selectedUser.getEmail()}), ChatFragment.this);
                            MXChatManager.getInstance().createChatForFragment(topic, ChatFragment.this);
                        } catch (MXException.AccountManagerIsNotValid accountManagerIsNotValid) {
                            Log.e(TAG, "Error when create chat", accountManagerIsNotValid);
                        }
                        catch (MXException.OpenChatEventListenerMissed openChatEventListenerMissed) {
                            Log.e(TAG, "Error when create chat", openChatEventListenerMissed);
                        }
                    }
                })
                .show();
    }

    private String getTopic() {
        return "";
    }

    private String[] getUserList() {
        List<String> users = new ArrayList<>(userLisToSelect.size());
        for (User user : userLisToSelect) {
            users.add(user.getFirstName() + " " + user.getLastName());
        }
        String[] userNames = new String[users.size()];
        users.toArray(userNames);
        return userNames;
    }

    private void loadAllAvailableUsers() {
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userLisToSelect = new ArrayList<User>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    if(!user.getEmail().equals(currentlyLoggedInUser.getEmail()))
                        userLisToSelect.add(postSnapshot.getValue(User.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreateChatForFragmentSuccess(String binderId, Fragment fragment) {
        Log.e(TAG, "Create Chat Success. The binderId = " + binderId);
        delegate.exchangeFragment(fragment);
    }

    @Override
    public void onCreateChatForFragmentFailed(int i, String s) {
        Log.e(TAG, "Failed to create chat with code: " + i + ", msg: " + s);
        Toast.makeText(mContext, "Failed to create chat: " + s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onOpenChatForFragmentSuccess(String binderID, Fragment fragment) {
        Log.e(TAG, "Open chat success.");
        delegate.exchangeFragment(fragment);

    }

    @Override
    public void onOpenChatForFragmentFailed(int i, String s) {
        Log.e(TAG, "Failed to open chat with code: " + i + ", msg: " + s);
        Toast.makeText(mContext, "Failed to open chat: " + s, Toast.LENGTH_LONG).show();
    }

    public class ChatListAdapter extends RecyclerView.Adapter {

        List<MXGroupChatSession> sessions;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            ImageView ivCover;
            TextView tvTopic, tvLastMessage, tvBadge;
            Button btnDelete, btnMeet;
            MXGroupChatSession session;
            View itemView;

            public ViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                ivCover = (ImageView) itemView.findViewById(R.id.iv_cover);
                tvTopic = (TextView) itemView.findViewById(R.id.tv_topic);
                tvLastMessage = (TextView) itemView.findViewById(R.id.tv_last_message);
                tvBadge = (TextView) itemView.findViewById(R.id.tv_badge);
                btnDelete = (Button) itemView.findViewById(R.id.btn_delete);
                btnMeet = (Button) itemView.findViewById(R.id.btn_meet);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (session.isAChat()) {
                            try {
                                //MXChatManager.getInstance().openChat(session.getSessionID(), session.getChatFragment.this);
                                MXChatManager.getInstance().openChatForFragment(session.getSessionID(), new Bundle(), ChatFragment.this);
                            } catch (MXException.AccountManagerIsNotValid accountManagerIsNotValid) {
                                Log.e(TAG, "Error when open chat", accountManagerIsNotValid);
                            }
                        } else if (session.isAMeet()) {
                            joinMeet();
                        }
                    }
                });
                btnDelete.setOnClickListener(this);
                btnMeet.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_meet) {
                    if (session.isAMeet()) {
                        joinMeet();
                    } else {
                        MXChatManager.getInstance().getChatMembers(session.getSessionID(), new MXChatManager.OnGetChatMembersListener() {
                            @Override
                            public void onGetChatMembersDone(ArrayList<MXGroupChatMember> arrayList) {
                                try {
                                    List<String> uniqueIds = new ArrayList<>();
                                    for (MXGroupChatMember member : arrayList) {
                                        uniqueIds.add(member.getUniqueId());
                                    }
                                    MXChatManager.getInstance().startMeet(currentlyLoggedInUser.getFirstName() + "'s meet", null,
                                            uniqueIds, new MXChatManager.OnStartMeetListener() {
                                                @Override
                                                public void onStartMeetDone(String meetId, String meetUrl) {
                                                    Log.e(TAG, "Meet started: " + meetId);
                                                }

                                                @Override
                                                public void onStartMeetFailed(int i, String s) {
                                                    Log.e(TAG, "onStartMeetFailed: " + s);
                                                }
                                            });
                                } catch (MXSDKException.Unauthorized unauthorized) {
                                    Log.e(TAG, "Error when start meet", unauthorized);
                                } catch (MXSDKException.MeetIsInProgress meetIsInProgress) {
                                    Log.e(TAG, "Error when start meet", meetIsInProgress);
                                }
                            }

                            @Override
                            public void onGetChatMembersFailed(int i, String s) {
                                Log.e(TAG, "onGetMembersFailed: " + s);
                            }
                        });
                    }
                } else if (v.getId() == R.id.btn_delete) {
                    new MaterialDialog.Builder(mContext)
                            .title(R.string.delete_confirm_title)
                            .content(R.string.delete_confirm)
                            .positiveText(android.R.string.yes)
                            .positiveColorRes(R.color.red_800)
                            .negativeColorRes(R.color.black)
                            .negativeText(android.R.string.no)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    MXChatManager.getInstance().deleteChat(session.getSessionID());
                                    refreshData();
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                }
                            })
                            .show();
                }
            }

            private void joinMeet() {
                if (!MXChatManager.getInstance().isAMeetingInProgress()) {
                    try {
                        MXChatManager.getInstance().joinMeet(session.getMeetID(), currentlyLoggedInUser.getFirstName(),
                                new MXChatManager.OnJoinMeetListener() {
                                    @Override
                                    public void onJoinMeetDone(String meetId, String meetUrl) {
                                        Log.e(TAG, "Joined meet: " + meetId);
                                    }

                                    @Override
                                    public void onJoinMeetFailed() {
                                        Log.e(TAG, "Unable to join meet.");
                                    }
                                });
                    } catch (MXSDKException.MeetIsInProgress meetIsInProgress) {
                        Log.e(TAG, "Error when join meet", meetIsInProgress);
                    }
                }
            }

        }

        public ChatListAdapter() {
            super();
        }

        private void sortData() {
            Collections.sort(sessions, new Comparator<MXGroupChatSession>() {
                @Override
                public int compare(MXGroupChatSession lhs, MXGroupChatSession rhs) {
                    if (lhs.isAMeet()) return -1;
                    if (rhs.isAMeet()) return 1;
                    if (lhs.getLastFeedTimeStamp() > rhs.getLastFeedTimeStamp()) return -1;
                    return 0;
                }
            });
        }

        public void refreshData() {
            sessions = MXChatManager.getInstance().getGroupChatSessions();
            sortData();
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_timeline, parent, false);
            RecyclerView.ViewHolder vh = new ChatFragment.ChatListAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ChatFragment.ChatListAdapter.ViewHolder theHolder = (ChatFragment.ChatListAdapter.ViewHolder) holder;
            MXGroupChatSession session = sessions.get(position);
            if (!TextUtils.isEmpty(session.getCoverImagePath())) {
                theHolder.ivCover.setImageURI(Uri.fromFile(new File(session.getCoverImagePath())));
            } else {
                // Get other member's avatar
                if (session.isAChat()) {
                    List<MXGroupChatMember> members = session.getMembers();
                    if (members != null && members.size() > 0) {
                        String avatarPath = members.get(0).getAvatarPath();
                        if (!TextUtils.isEmpty(avatarPath)) {
                            //Picasso.with(mContext).load(new File(avatarPath)).transform(new CircleTransform()).into(theHolder.ivCover);
                        }
                    }
                }
            }
            theHolder.tvTopic.setText(session.getTopic());
            theHolder.tvLastMessage.setText(session.getLastFeedContent());
            theHolder.session = session;
            if (session.isOwner()) {
                theHolder.btnDelete.setText(R.string.Delete);
            } else {
                theHolder.btnDelete.setText(R.string.Leave);
            }
            if (session.isAMeet()) {
                theHolder.tvTopic.setText(session.getMeetID());
                ((CardView) theHolder.itemView).setCardBackgroundColor(getResources().getColor(R.color.yellow_100));
                theHolder.btnDelete.setVisibility(View.GONE);
                if (MXChatManager.getInstance().isAMeetingInProgress()) {
                    theHolder.btnMeet.setVisibility(View.GONE);
                } else {
                    theHolder.btnMeet.setVisibility(View.VISIBLE);
                    theHolder.btnMeet.setText(R.string.Join);
                }
            } else {
                theHolder.btnDelete.setVisibility(View.VISIBLE);
                theHolder.btnMeet.setVisibility(View.VISIBLE);
                theHolder.btnMeet.setText(R.string.Meet);
                ((CardView) theHolder.itemView).setCardBackgroundColor(getResources().getColor(R.color.white));
            }
            if (session.getUnreadFeedCount() > 0) {
                theHolder.tvBadge.setText(String.valueOf(session.getUnreadFeedCount()));
                theHolder.tvBadge.setVisibility(View.VISIBLE);
            } else {
                theHolder.tvBadge.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return sessions.size();
        }
    }
}
