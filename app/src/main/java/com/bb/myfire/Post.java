package com.bb.myfire;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

    @IgnoreExtraProperties
    public class Post {

        public String guestid;
        public String fname;
        public String lname;
        public int starCount = 0;
        public Map<String, Boolean> stars = new HashMap<>();

        public Post() {
            // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        }

        public Post(String guestid, String fname, String lname) {
            this.guestid = guestid;
            this.fname = fname;
            this.lname = lname;
        }

        // [START post_to_map]
        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("guestid", guestid);
            result.put("first name", fname);
            result.put("last name", lname);
            result.put("starCount", starCount);

            return result;
        }
        // [END post_to_map]

    }
// [END post_class]
