package com.jhteck.icebox.repository.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "t_account_operation")
public class AccountOperationEntity implements Serializable {
    private final static Long serialVersionUID=-1L;
    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "account_id")
    @SerializedName("account_id")
    private Integer account_id;

    @ColumnInfo(name = "account_log_id")
    @SerializedName("account_log_id")
    private String account_log_id;

    @ColumnInfo(name="user_log_id")
    @SerializedName("user_log_id")
    private String user_log_id;

    @ColumnInfo(name="user_id")
    @SerializedName("user_id")
    private String user_id;

    @ColumnInfo(name="operation")
    @SerializedName("operation")
    private Byte operation;

    @ColumnInfo(name="log_at")
    @SerializedName("log_at")
    private String log_at;

    @ColumnInfo(name="temperature")
    @SerializedName("temperature")
    private Integer temperature;

    /**
     * 是否已经上传
     */
    @ColumnInfo(name="has_uploaded")
    private Boolean hasUploaded;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser_log_id() {
        return user_log_id;
    }

    public void setUser_log_id(String user_log_id) {
        this.user_log_id = user_log_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Byte getOperation() {
        return operation;
    }

    public void setOperation(Byte operation) {
        this.operation = operation;
    }

    public String getLog_at() {
        return log_at;
    }

    public void setLog_at(String log_at) {
        this.log_at = log_at;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Boolean getHasUploaded() {
        return hasUploaded;
    }

    public void setHasUploaded(Boolean hasUploaded) {
        this.hasUploaded = hasUploaded;
    }

    public Integer getAccount_id() {
        return account_id;
    }

    public void setAccount_id(Integer account_id) {
        this.account_id = account_id;
    }

    public String getAccount_log_id() {
        return account_log_id;
    }

    public void setAccount_log_id(String account_log_id) {
        this.account_log_id = account_log_id;
    }

    @Override
    public String toString() {
        return "AccountOperationEntity{" +
                "id=" + id +
                ", account_id=" + account_id +
                ", account_log_id='" + account_log_id + '\'' +
                ", user_log_id='" + user_log_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", operation=" + operation +
                ", log_at='" + log_at + '\'' +
                ", temperature=" + temperature +
                ", hasUploaded=" + hasUploaded +
                '}';
    }
}
