package com.tokbox.testrecycler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener, SubscriberKit.SubscriberListener {

    private static final String API_KEY = "";
    private static final String SESSION_ID = "";
    private static final String TOKEN = "";


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Publisher pub;
    Subscriber sub;
    Session sess;

    private ArrayList<View> mDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sess = new Session(this, API_KEY, SESSION_ID);
        sess.setSessionListener(this);

        pub = new Publisher(this, "pub");
        pub.setPublisherListener(this);
        sess.connect(TOKEN);

        mDataSet = new ArrayList<>();
        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(mDataSet);
        mRecyclerView.setAdapter(mAdapter);


    }

    @Override
    public void onConnected(Session session) {
        sess.publish(pub);
    }

    @Override
    public void onDisconnected(Session session) {

    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        sub = new Subscriber(this, stream);
        session.subscribe(sub);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDataSet.add(sub.getView());
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {

    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDataSet.add(pub.getView());
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    @Override
    public void onConnected(SubscriberKit subscriberKit) {

    }

    @Override
    public void onDisconnected(SubscriberKit subscriberKit) {

    }

    @Override
    public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {

    }
}

class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<View> mDataset;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mTextView;
        public LinearLayout mLayout;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            mTextView = (TextView) v.findViewById(R.id.text_view);
            mLayout = (LinearLayout) v.findViewById(R.id.row_layout);
        }
    }

    public MyAdapter(ArrayList<View> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mLayout.removeAllViews();
        holder.mTextView.setText(String.valueOf(position));
        holder.mLayout.addView(holder.mTextView,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(500, 500);

        holder.mLayout.addView(mDataset.get(position), params);
    }
    
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
