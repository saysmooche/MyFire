package com.bb.myfire;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

//import com.google.firebase.quickstart.database.java.models.Post;
//import com.google.firebase.quickstart.database.java.models.User;

public class DatabaseActivity  extends BaseActivity{

        private static final String TAG = "DatabaseActivity";
        private static final String REQUIRED = "Required";

        // [START declare_database_ref]
        private DatabaseReference mDatabase;
        // [END declare_database_ref]

        private TextView mTitleField;
        private EditText mFNameField;
        private EditText mLNameField;
        private Button mSubmitButton;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // [START initialize_database_ref]
            mDatabase = FirebaseDatabase.getInstance().getReference();
            // [END initialize_database_ref]

            mTitleField = findViewById(R.id.Title_txt);
            mFNameField = findViewById(R.id.editText1);
            mLNameField = findViewById(R.id.editText2);
            mSubmitButton = findViewById(R.id.submit_button);

            mSubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    submitPost();

                    Intent sendIntent = new Intent(getApplicationContext(), PostDetailActivity.class);
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, (Parcelable) mFNameField);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, (Parcelable) mLNameField);
                    sendIntent.setType("text/plain");

                    if (sendIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(sendIntent);
                }
                }
            });
        }

        private void submitPost() {
            final String fname = mFNameField.getText().toString();
            final String lname = mLNameField.getText().toString();

            // Title is required
            if (TextUtils.isEmpty(fname)) {
                mFNameField.setError(REQUIRED);
                return;
            }

            // Body is required
            if (TextUtils.isEmpty(lname)) {
                mLNameField.setError(REQUIRED);
                return;
            }

            // Disable button so there are no multi-posts
            setEditingEnabled(false);
            Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

            // [START single_value_read]
            final String guestId = getUid();
            mDatabase.child("guests").child(guestId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            Guest guest = dataSnapshot.getValue(Guest.class);

                            // [START_EXCLUDE]
                            if (guest == null) {
                                // User is null, error out
                                Log.e(TAG, "Guest " + guestId + " is unexpectedly null");
                                Toast.makeText(DatabaseActivity.this,
                                        "Error: could not fetch guest.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // Write new post
                                writeNewPost(guestId, fname, lname);
                            }

                            // Finish this Activity, back to the stream
                            setEditingEnabled(true);
                            finish();
                            // [END_EXCLUDE]
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "getGuest:onCancelled", databaseError.toException());
                            // [START_EXCLUDE]
                            setEditingEnabled(true);
                            // [END_EXCLUDE]
                        }
                    });
            // [END single_value_read]
        }

        private void setEditingEnabled(boolean enabled) {
            mFNameField.setEnabled(enabled);
            mLNameField.setEnabled(enabled);
            if (enabled) {
                mSubmitButton.isShown();
            } else {
                mSubmitButton.willNotDraw();
            }
        }

        // [START write_fan_out]
        private void writeNewPost(String guestId, String fname, String lname) {

            String key = mDatabase.child("posts").push().getKey();
            Post post = new Post(guestId, fname, lname);
            Map<String, Object> postValues = post.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/posts/" + key, postValues);
            childUpdates.put("/guest-posts/" + guestId + "/" + key, postValues);

            mDatabase.updateChildren(childUpdates);
        }
    }

