package com.kits.kowsarapp.model.base;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Category extends ExpandableGroup<Product> {

    public int id;
    public int childno;
    public String name;
    public String imagestr;


    public Category(String title, List<Product> items, int id, int childno) {
        super(title, items);
        this.id = id;
        this.name = title;
        this.childno = childno;
    }


    //region $ getter setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getImagestr() {
        return imagestr;
    }

    public void setImagestr(String imagestr) {
        this.imagestr = imagestr;
    }
    //endregion

}
