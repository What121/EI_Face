package com.bestom.ei_library.commons.beans;

public class UserInfoDBBean {

    String image_path;
    String name;
    String ID;
    int image_id;

    public UserInfoDBBean(String image_path, String name, String ID, int image_id){
        this.image_path = image_path;
        this.name = name;
        this.ID = ID;
        this.image_id = image_id;
    }


    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }


    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
