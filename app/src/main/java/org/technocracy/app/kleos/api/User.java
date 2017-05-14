package org.technocracy.app.kleos.api;


public class User{

    private int uid;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String branch;
    private int semester;
    private String college;
    private String rollno;
    private String accesstoken;
    private String createdAt;

    public User(){}

    public User(int uid, String name, String username, String email,
                String phone, String branch, int semester, String college,
                String rollno, String accesstoken, String createdAt){
        this.uid = uid;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.branch = branch;
        this.semester = semester;
        this.college = college;
        this.rollno = rollno;
        this.accesstoken = accesstoken;
        this.createdAt = createdAt;
    }

    public int getUid(){
        return uid;
    }

    public void setUid(int uid){
        this.uid = uid;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPhone(){
        return phone;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getBranch(){
        return branch;
    }

    public void setBranch(String branch){
        this.branch = branch;
    }

    public int getSemester(){
        return semester;
    }

    public void setSemester(int semester){
        this.semester = semester;
    }

    public String getCollege(){
        return college;
    }

    public void setCollege(String college){
        this.college = college;
    }

    public String getRollno(){
        return rollno;
    }

    public void setRollno(String rollno){
        this.rollno = rollno;
    }

    public String getAccesstoken(){
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken){
        this.accesstoken = accesstoken;
    }

    public String getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
    }
}
