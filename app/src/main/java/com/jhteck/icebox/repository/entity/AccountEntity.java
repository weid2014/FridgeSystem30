package com.jhteck.icebox.repository.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;

@Entity(tableName = "t_account", indices = {@Index(value = {"fridge_nfc_1", "user_name", "faceUrl"})})
public class AccountEntity {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String user_id;
    private String role_id;
    private String real_name;
    private String nick_name;
    private String nfc_id;
    private String password_digest;
    private int store_count;
    private int consume_count;
    private int deposit_count;
    private String created_by;
    private String created_time;
    private String login_time;
    private String km_user_id;
    private String updated_by;
    private String updated_time;
    private Boolean hasUpload;
    private String faceUrl;
    private int status;
    //v1版本
    /**
     * id - integer 用户ID
     * name - string 姓名
     * user_name - string 登录用户名
     * user_number - string 用户员工编号
     * role - integer 角色： 2 - 冰箱管理员; 3 - 冰箱普通用户;
     * regist_at - datetime 注册时间
     * last_login_at - datetime 注册时间
     * is_cancel - bool 是否注销
     * fridge_face_url - string 冰箱用户脸部识别图片
     * fridge_face_mask_url - string 冰箱用户脸部（戴口罩）识别图片
     * fridge_nfc_1 - string 冰箱用户NCF1卡值
     * fridge_nfc_2 - string 冰箱用户NCF2卡值
     * fridge_nfc_3 - string 冰箱用户NCF3卡值
     * fridge_nfc_4 - string 冰箱用户NCF4卡值
     * fridge_nfc_5 - string 冰箱用户NCF5卡值
     */
    private String name;
    private String user_name;
    private String user_number;
    private int role;
    private String regist_at;
    private String last_login_at;
    private Boolean is_cancel;
    private String fridge_face_url;
    private String fridge_face_mask_url;
    private String fridge_nfc_1;
    private String fridge_nfc_2;
    private String fridge_nfc_3;
    private String fridge_nfc_4;
    private String fridge_nfc_5;

    public List<FaceAccountEntity> getFaceAccount() {
        return faceAccount;
    }

    public void setFaceAccount(List<FaceAccountEntity> faceAccount) {
        this.faceAccount = faceAccount;
    }

    private transient List<FaceAccountEntity> faceAccount;



    public Boolean getHasUpload() {
        return hasUpload;
    }

    public void setHasUpload(Boolean hasUpload) {
        this.hasUpload = hasUpload;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getNfc_id() {
        return nfc_id;
    }

    public void setNfc_id(String nfc_id) {
        this.nfc_id = nfc_id;
    }

    public String getPassword_digest() {
        return password_digest;
    }

    public void setPassword_digest(String password_digest) {
        this.password_digest = password_digest;
    }

    public int getStore_count() {
        return store_count;
    }

    public void setStore_count(int store_count) {
        this.store_count = store_count;
    }

    public int getConsume_count() {
        return consume_count;
    }

    public void setConsume_count(int consume_count) {
        this.consume_count = consume_count;
    }

    public int getDeposit_count() {
        return deposit_count;
    }

    public void setDeposit_count(int deposit_count) {
        this.deposit_count = deposit_count;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getLogin_time() {
        return login_time;
    }

    public void setLogin_time(String login_time) {
        this.login_time = login_time;
    }

    public String getKm_user_id() {
        return km_user_id;
    }

    public void setKm_user_id(String km_user_id) {
        this.km_user_id = km_user_id;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public String getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(String updated_time) {
        this.updated_time = updated_time;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_number() {
        return user_number;
    }

    public void setUser_number(String user_number) {
        this.user_number = user_number;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getRegist_at() {
        return regist_at;
    }

    public void setRegist_at(String regist_at) {
        this.regist_at = regist_at;
    }

    public String getLast_login_at() {
        return last_login_at;
    }

    public void setLast_login_at(String last_login_at) {
        this.last_login_at = last_login_at;
    }

    public Boolean getIs_cancel() {
        return is_cancel;
    }

    public void setIs_cancel(Boolean is_cancel) {
        this.is_cancel = is_cancel;
    }

    public String getFridge_face_url() {
        return fridge_face_url;
    }

    public void setFridge_face_url(String fridge_face_url) {
        this.fridge_face_url = fridge_face_url;
    }

    public String getFridge_face_mask_url() {
        return fridge_face_mask_url;
    }

    public void setFridge_face_mask_url(String fridge_face_mask_url) {
        this.fridge_face_mask_url = fridge_face_mask_url;
    }

    public String getFridge_nfc_1() {
        return fridge_nfc_1;
    }

    public void setFridge_nfc_1(String fridge_nfc_1) {
        this.fridge_nfc_1 = fridge_nfc_1;
    }

    public String getFridge_nfc_2() {
        return fridge_nfc_2;
    }

    public void setFridge_nfc_2(String fridge_nfc_2) {
        this.fridge_nfc_2 = fridge_nfc_2;
    }

    public String getFridge_nfc_3() {
        return fridge_nfc_3;
    }

    public void setFridge_nfc_3(String fridge_nfc_3) {
        this.fridge_nfc_3 = fridge_nfc_3;
    }

    public String getFridge_nfc_4() {
        return fridge_nfc_4;
    }

    public void setFridge_nfc_4(String fridge_nfc_4) {
        this.fridge_nfc_4 = fridge_nfc_4;
    }

    public String getFridge_nfc_5() {
        return fridge_nfc_5;
    }

    public void setFridge_nfc_5(String fridge_nfc_5) {
        this.fridge_nfc_5 = fridge_nfc_5;
    }

    @Override
    public String toString() {
        return "AccountEntity{" +
                "id=" + id +
                ", user_id='" + user_id + '\'' +
                ", role_id='" + role_id + '\'' +
                ", real_name='" + real_name + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", nfc_id='" + nfc_id + '\'' +
                ", password_digest='" + password_digest + '\'' +
                ", store_count=" + store_count +
                ", consume_count=" + consume_count +
                ", deposit_count=" + deposit_count +
                ", created_by='" + created_by + '\'' +
                ", created_time='" + created_time + '\'' +
                ", login_time='" + login_time + '\'' +
                ", km_user_id='" + km_user_id + '\'' +
                ", updated_by='" + updated_by + '\'' +
                ", updated_time='" + updated_time + '\'' +
                ", hasUpload=" + hasUpload +
                ", faceUrl='" + faceUrl + '\'' +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", user_name='" + user_name + '\'' +
                ", user_number='" + user_number + '\'' +
                ", role=" + role +
                ", regist_at='" + regist_at + '\'' +
                ", last_login_at='" + last_login_at + '\'' +
                ", is_cancel=" + is_cancel +
                ", fridge_face_url='" + fridge_face_url + '\'' +
                ", fridge_face_mask_url='" + fridge_face_mask_url + '\'' +
                ", fridge_nfc_1='" + fridge_nfc_1 + '\'' +
                ", fridge_nfc_2='" + fridge_nfc_2 + '\'' +
                ", fridge_nfc_3='" + fridge_nfc_3 + '\'' +
                ", fridge_nfc_4='" + fridge_nfc_4 + '\'' +
                ", fridge_nfc_5='" + fridge_nfc_5 + '\'' +
                ", faceAccount=" + faceAccount +
                '}';
    }
}
