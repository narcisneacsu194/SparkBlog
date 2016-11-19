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

        before((req, res) ->{
           if(req.cookie("password") != null){
               req.attribute("password", req.cookie("password"));
           }
        });

        before("/new", (req, res) ->{
            if(req.attribute("password") == null){
                res.redirect("/password");
                halt();
            }
        });

        before("/edit/:slug", (req, res) ->{
            if(req.attribute("password") == null){
                res.redirect("/password");
                halt();
            }
        });

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entries", dao.findAllEntries());
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        get("/new", (req, res) ->{

            return new ModelAndView(null, "new.hbs");
        }, new HandlebarsTemplateEngine());

        get("/edit/:slug", (req, res) ->{

            Map<String, Object> model = new HashMap<>();
            BlogEntry blogEntry  = dao.findEntryBySlug(req.params("slug"));
            model.put("entry", blogEntry);
            return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());

        get("/detail/:slug", (req, res) ->{
            Map<String, Object> model = new HashMap<>();
            BlogEntry blogEntry = dao.findEntryBySlug(req.params("slug"));
            List<Comment> commentList = blogEntry.getCommentList();
            model.put("entry", blogEntry);
            model.put("comments", commentList);
            return new ModelAndView(model, "detail.hbs");
        }, new HandlebarsTemplateEngine());

        get("/password", (req, res) ->{
            return new ModelAndView(null, "password.hbs");
        }, new HandlebarsTemplateEngine());

        post("/publish", (req, res) ->{
            String title = req.queryParams("title");
            String entry = req.queryParams("entry");
            BlogEntry blogEntry = new BlogEntry(title, entry);
            dao.addEntry(blogEntry);
            res.redirect("/");
            return null;
        });

        post("/carryOutEdit/:slug", (req, res) ->{
            String title = req.queryParams("title");
            String entry = req.queryParams("entry");
            BlogEntry blogEntry = dao.findEntryBySlug(req.params("slug"));
            blogEntry.setTitle(title);
            blogEntry.setContent(entry);
            res.redirect("/detail/" + req.params("slug"));
            return null;
        });

        post("/detail/:slug/postComment", (req, res) ->{
            BlogEntry blogEntry = dao.findEntryBySlug(req.params("slug"));
            String name = req.queryParams("name");
            String comment = req.queryParams("comment");
            Comment entryComment = new Comment(name, comment);
            blogEntry.addComment(entryComment);
            res.redirect("/detail/" + req.params("slug"));
            return null;
        });

        post("/passwordVerification", (req, res) ->{
            String password = req.queryParams("password");
            if(password.equals("admin")){
                res.cookie("password", "admin");
                res.redirect("/");
                return null;
            }

            res.redirect("/password");
            return null;
        });

        post("/delete/:slug", (req, res) ->{
            BlogEntry blogEntry = dao.findEntryBySlug(req.params("slug"));
            dao.delete(blogEntry);
            res.redirect("/");
            return null;
        });

    }
}
