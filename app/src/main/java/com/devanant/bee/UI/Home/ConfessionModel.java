package com.devanant.bee.UI.Home;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConfessionModel implements Serializable {
    private String Date, Desc, ID, DocumentID;
    private Integer Like, Dislike;
    private ArrayList<String> comments;

    public ConfessionModel(String DocumentID, String date, String desc, String ID, Integer like, Integer dislike, ArrayList<String> comments) {
        Date = date;
        Desc = desc;
        this.ID = ID;
        Like = like;
        Dislike = dislike;
        this.comments = comments;
        this.DocumentID=DocumentID;
    }

    public ConfessionModel() {
        //
    }


    public String getDocumentID() {
        return DocumentID;
    }

    public String getDate() {
        return Date;
    }

    public String getDesc() {
        return Desc;
    }

    public String getID() {
        return ID;
    }

    public Integer getLike() {
        return Like;
    }

    public Integer getDislike() {
        return Dislike;
    }


    public ArrayList<String> getComments() {
        return comments;
    }
}
