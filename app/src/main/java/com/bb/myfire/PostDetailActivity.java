package com.bb.myfire;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends BaseActivity implements View.OnClickListener {

        private static final String TAG = "PostDetailActivity";

        public static final String EXTRA_POST_KEY = "post_key";

        private DatabaseReference mPostReference;
        private DatabaseReference mCommentsReference;
        private ValueEventListener mPostListener;
        private String mPostKey;
        private CommentAdapter mAdapter;

        private TextView mFNameView;
        private TextView mLNameView;
        private EditText mCommentField;
        private Button mCommentButton;
        private RecyclerView mCommentsRecycler;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.recycle_layout);

            // Get post key from intent
            mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
            if (mPostKey == null) {
                throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
            }

            // Initialize Database
            mPostReference = FirebaseDatabase.getInstance().getReference()
                    .child("posts").child(mPostKey);
            mCommentsReference = FirebaseDatabase.getInstance().getReference()
                    .child("post-comments").child(mPostKey);

            // Initialize Views
            mFNameView = findViewById(R.id.fir_nam);
            mLNameView = findViewById(R.id.las_nam);
            mCommentField = findViewById(R.id.edit_comment);
            mCommentButton = findViewById(R.id.submit_btn);
            mCommentsRecycler = findViewById(R.id.recycleview);

            mCommentButton.setOnClickListener(this);
            mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));

            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();

        }

        @Override
        public void onStart() {
            super.onStart();

            // Add value event listener to the post
            // [START post_value_event_listener]
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    Post post = dataSnapshot.getValue(Post.class);
                    // [START_EXCLUDE]
                    mFNameView.setText(post.fname);
                    mLNameView.setText(post.lname);
                    // [END_EXCLUDE]
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // [START_EXCLUDE]
                    Toast.makeText(PostDetailActivity.this, "Failed to load post.",
                            Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
            };
            mPostReference.addValueEventListener(postListener);
            // [END post_value_event_listener]

            // Keep copy of post listener so we can remove it when app stops
            mPostListener = postListener;

            // Listen for comments
            mAdapter = new CommentAdapter(this, mCommentsReference);
            mCommentsRecycler.setAdapter(mAdapter);
        }

        @Override
        public void onStop() {
            super.onStop();

            // Remove post value event listener
            if (mPostListener != null) {
                mPostReference.removeEventListener(mPostListener);
            }

            // Clean up comments listener
            mAdapter.cleanupListener();
        }

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.comment_btn) {
                postComment();
            }
        }

        private void postComment() {
            final String commentid = getCommentid();
            FirebaseDatabase.getInstance().getReference().child("guests").child(commentid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user information
                            Guest guest = dataSnapshot.getValue(Guest.class);
                            String authorName = guest.guestName;

                            // Create new comment object
                            String commentText = mCommentField.getText().toString();
                            Comment comment = new Comment(mFNameView, mLNameView, commentText);

                            // Push the comment, it will appear in the list
                            mCommentsReference.push().setValue(comment);

                            // Clear the field
                            mCommentField.setText(null);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

    private String getCommentid() {
            return getCommentid();
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {

            public TextView fnameView;
            public TextView lnameView;

            public CommentViewHolder(View itemView) {
                super(itemView);

                fnameView = itemView.findViewById(R.id.comment1);
                lnameView = itemView.findViewById(R.id.comment2);
            }
        }

        private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

            private PostDetailActivity mContext;
            private DatabaseReference mDatabaseReference;
            private ChildEventListener mChildEventListener;

            private List<String> mCommentIds = new ArrayList<>();
            private List<Comment> mComments = new ArrayList<>();

            public CommentAdapter(final PostDetailActivity context, DatabaseReference ref) {
                mContext = context;
                mDatabaseReference = ref;

                // Create child event listener
                // [START child_event_listener_recycler]
                ChildEventListener childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                        Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                        // A new comment has been added, add it to the displayed list
                        Comment comment = dataSnapshot.getValue(Comment.class);

                        // [START_EXCLUDE]
                        // Update RecyclerView
                        mCommentIds.add(dataSnapshot.getKey());
                        mComments.add(comment);
                        notifyItemInserted(mComments.size() - 1);
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                        Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                        // A comment has changed, use the key to determine if we are displaying this
                        // comment and if so displayed the changed comment.
                        Comment newComment = dataSnapshot.getValue(Comment.class);
                        String commentKey = dataSnapshot.getKey();

                        // [START_EXCLUDE]
                        int commentIndex = mCommentIds.indexOf(commentKey);
                        if (commentIndex > -1) {
                            // Replace with the new data
                            mComments.set(commentIndex, newComment);

                            // Update the RecyclerView
                            notifyItemChanged(commentIndex);
                        } else {
                            Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                        }
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                        // A comment has changed, use the key to determine if we are displaying this
                        // comment and if so remove it.
                        String commentKey = dataSnapshot.getKey();

                        // [START_EXCLUDE]
                        int commentIndex = mCommentIds.indexOf(commentKey);
                        if (commentIndex > -1) {
                            // Remove data from the list
                            mCommentIds.remove(commentIndex);
                            mComments.remove(commentIndex);

                            // Update the RecyclerView
                            notifyItemRemoved(commentIndex);
                        } else {
                            Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                        }
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                        Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                        // A comment has changed position, use the key to determine if we are
                        // displaying this comment and if so move it.
                        Comment movedComment = dataSnapshot.getValue(Comment.class);
                        String commentKey = dataSnapshot.getKey();

                        // ...
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                        Toast.makeText(mContext, "Failed to load comments.", Toast.LENGTH_SHORT).show();
                    }
                };
                ref.addChildEventListener(childEventListener);
                // [END child_event_listener_recycler]

                // Store reference to listener so it can be removed on app stop
                mChildEventListener = childEventListener;
            }

            @Override
            public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View view = inflater.inflate(R.layout.guest_fragment, parent, false);
                return new CommentViewHolder(view);
            }

            @Override
            public void onBindViewHolder(CommentViewHolder holder, int position) {
                Comment comment = mComments.get(position);
                holder.fnameView.setText(comment.fname);
                holder.lnameView.setText(comment.lname);
            }

            @Override
            public int getItemCount() {
                return mComments.size();
            }

            public void cleanupListener() {
                if (mChildEventListener != null) {
                    mDatabaseReference.removeEventListener(mChildEventListener);
                }
            }

        }
    }
