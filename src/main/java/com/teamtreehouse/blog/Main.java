package com.teamtreehouse.blog;

/**
 * Created by narcis on 11/19/2016.
 */
import com.teamtreehouse.blog.dao.SimpleBlogDao;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args){
        staticFileLocation("/public");
        SimpleBlogDao dao = new SimpleBlogDao();

        //This filter creates a password attribute from the cookie with the same name, if it is not null.
        before((req, res) ->{
           if(req.cookie("password") != null){
               req.attribute("password", req.cookie("password"));
           }
        });

        //This filter redirects you to the password page if you try to create a new entry and no password attribute exists.
        before("/new", (req, res) ->{
            if(req.attribute("password") == null){
                res.redirect("/password");
                halt();
            }
        });

        //This filter redirects you to the password page if you try to edit an entry and no password attribute exists.
        before("/edit/:slug", (req, res) ->{
            if(req.attribute("password") == null){
                res.redirect("/password");
                halt();
            }
        });

        //This get request takes you to the main page and lists all the available entries, with some details included.
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entries", dao.findAllEntries());
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        //This get request is called when you try to create a new entry.
        get("/new", (req, res) ->{

            return new ModelAndView(null, "new.hbs");
        }, new HandlebarsTemplateEngine());

        //This get request is called when you try to edit an existing entry.
        get("/edit/:slug", (req, res) ->{

            Map<String, Object> model = new HashMap<>();
            BlogEntry blogEntry  = dao.findEntryBySlug(req.params("slug"));
            model.put("entry", blogEntry);
            return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());

        //This get request is called when you want to see the content of a specific entry.
        get("/detail/:slug", (req, res) ->{
            Map<String, Object> model = new HashMap<>();
            BlogEntry blogEntry = dao.findEntryBySlug(req.params("slug"));
            List<Comment> commentList = blogEntry.getCommentList();
            model.put("entry", blogEntry);
            model.put("comments", commentList);
            return new ModelAndView(model, "detail.hbs");
        }, new HandlebarsTemplateEngine());

        //This get request is called when you are being redirected by filters, if no password attribute exists.
        get("/password", (req, res) ->{
            return new ModelAndView(null, "password.hbs");
        }, new HandlebarsTemplateEngine());

        //This post request is called after you submit the information provided in the /new URI (create a new entry).
        post("/publish", (req, res) ->{
            String title = req.queryParams("title");
            String entry = req.queryParams("entry");
            if(title.equals("") || entry.equals("")){
                res.redirect("/new");
                return null;
            }
            BlogEntry blogEntry = new BlogEntry(title, entry);
            dao.addEntry(blogEntry);
            res.redirect("/");
            return null;
        });

        //This post request is called after you submit the new information provided in the /edit/:slug URI (edit an existing entry).
        post("/carry-out-edit/:slug", (req, res) ->{
            String title = req.queryParams("title");
            String entry = req.queryParams("entry");
            if(title.equals("") || entry.equals("")){
                res.redirect("/edit/" + req.params("slug"));
                return null;
            }
            BlogEntry blogEntry = dao.findEntryBySlug(req.params("slug"));
            blogEntry.setTitle(title);
            blogEntry.setContent(entry);
            blogEntry.setSlug();
            res.redirect("/detail/" + blogEntry.getSlug());
            return null;
        });

        //This post request is called after you submit the information provided in the /detail/:slug URI, at the comments section.
        post("/detail/:slug/post-comment", (req, res) ->{
            BlogEntry blogEntry = dao.findEntryBySlug(req.params("slug"));
            String name = req.queryParams("name");
            String comment = req.queryParams("comment");
            if(name.equals("") || comment.equals("")){
                res.redirect("/detail/" + req.params("slug"));
                return null;
            }
            Comment entryComment = new Comment(name, comment);
            blogEntry.addComment(entryComment);
            res.redirect("/detail/" + req.params("slug"));
            return null;
        });

        //This post request is called after you submit the password you wrote, verifies if it is valid or not, and will take appropriate action.
        post("/password-verification", (req, res) ->{
            String password = req.queryParams("password");
            if(password.equals("admin")){
                res.cookie("password", "admin");
                res.redirect("/");
                return null;
            }

            res.redirect("/password");
            return null;
        });

        //This post request is called if you press the Delete button inside the /detail/:slug URI (details of a specific entry).
        post("/delete/:slug", (req, res) ->{
            BlogEntry blogEntry = dao.findEntryBySlug(req.params("slug"));
            dao.delete(blogEntry);
            res.redirect("/");
            return null;
        });

    }
}
