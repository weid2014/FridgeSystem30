package com.jhteck.icebox.repository.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;

@Entity(tableName = "t_account", indices = {@Index(value = {"nfc_id", "nick_name", "faceUrl"})})
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

}
