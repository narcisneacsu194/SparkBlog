package com.teamtreehouse.blog.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {
    private String authorName;
    private String commentText;
    private String date;

    public Comment(String authorName, String commentText){
        this.authorName = authorName;
        this.commentText = commentText;
        this.date = getDateTime();
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy -- hh:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getDate() {
        return date;
    }

}
