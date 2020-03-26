package com.bb.myfire;

import android.widget.TextView;

import com.google.firebase.database.IgnoreExtraProperties;

    @IgnoreExtraProperties
    public class Comment {

        public String commentid;
        public String fname;
        public String lname;

        public Comment(TextView mFNameView, TextView mLNameView, String commentText) {
            // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
        }

        public String getCommentid() {
            return commentid;
        }

        public void setCommentid(String commentid) {
            this.commentid = commentid;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public String getLname() {
            return lname;
        }

        public void setLname(String lname) {
            this.lname = lname;
        }

        public Comment(String commentid, String fname, String lname) {
            this.commentid = commentid;
            this.fname = fname;
            this.lname = lname;
        }

    }
// [END comment_class]
