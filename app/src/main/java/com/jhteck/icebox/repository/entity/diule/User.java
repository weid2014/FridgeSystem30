package com.jhteck.icebox.repository.entity.diule;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息
 */
@Entity(tableName = "t_user", indices ={@Index(value = {"nfc_id","nick_name"})})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @PrimaryKey(autoGenerate = true)
    @JsonProperty(value = "user_id", required = true)
    @ColumnInfo(name = "user_id")
    private Integer userId;
    @JsonProperty(value = "km_user_id")
    @ColumnInfo(name = "km_user_id")
    private String kmUserId;
    @JsonProperty(value = "role_id")
    @ColumnInfo(name = "role_id")
    private String roleId;
    @JsonProperty(value = "real_name")
    @ColumnInfo(name = "real_name")
    private String realName;
    @JsonProperty(value = "nick_name")
    @ColumnInfo(name = "nick_name")
    private String nickName;
    @JsonProperty(value = "nfc_id")
    @ColumnInfo(name = "nfc_id")
    private String nfcId;
    @JsonProperty("password_digest")
    @ColumnInfo(name = "user_pass")
    private String userPass;
    @ColumnInfo(name = "change_10")
    private Integer change10;
    @ColumnInfo(name = "change_20")
    private Integer change20;
    @ColumnInfo(name = "change_30")
    private String change30;
    @ColumnInfo(name = "status")
    private int status;
    @JsonProperty("create_by")
    @ColumnInfo(name = "create_by")
    private String createBy;
    @JsonProperty("create_time")
    @ColumnInfo(name = "create_time")
    private String createTime;
    @JsonProperty("update_by")
    @ColumnInfo(name = "update_by")
    private String updateBy;
    @JsonProperty("update_time")
    @ColumnInfo(name = "update_time")
    private String updateTime;
    @ColumnInfo(name = "login_time")
    @JsonProperty("login_time")
    private String loginTime;
    @ColumnInfo(name = "has_upload")
    private boolean hasUpload;



    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getKmUserId() {
        return kmUserId;
    }

    public void setKmUserId(String kmUserId) {
        this.kmUserId = kmUserId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNfcId() {
        return nfcId;
    }

    public void setNfcId(String nfcId) {
        this.nfcId = nfcId;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public Integer getChange10() {
        return change10;
    }

    public void setChange10(Integer change10) {
        this.change10 = change10;
    }

    public Integer getChange20() {
        return change20;
    }

    public void setChange20(Integer change20) {
        this.change20 = change20;
    }

    public String getChange30() {
        return change30;
    }

    public void setChange30(String change30) {
        this.change30 = change30;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public boolean isHasUpload() {
        return hasUpload;
    }

    public void setHasUpload(boolean hasUpload) {
        this.hasUpload = hasUpload;
    }
}

