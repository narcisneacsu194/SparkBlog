package com.teamtreehouse.blog.model;

import com.github.slugify.Slugify;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlogEntry {
    private String title;
    private String date;
    private String content;
    private String slug;
    private List<Comment> commentList;

    public BlogEntry(String title, String content){
        this.title = title;
        this.date = getDateTime();
        this.content = content;
        commentList = new ArrayList<>();

        try{
            Slugify slugify = new Slugify();
            slug = slugify.slugify(title);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy -- hh:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String getSlug() {
        return slug;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean addComment(Comment comment) {
        return commentList.add(comment);
    }

}
