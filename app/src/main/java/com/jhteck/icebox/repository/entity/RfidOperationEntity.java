package com.jhteck.icebox.repository.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName = "t_rfid_operation")
public class RfidOperationEntity implements Serializable {
    private final static Long serialVersionUID = -1L;
    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "user_log_id")
    @SerializedName("user_log_id")
    private String user_log_id;

    @ColumnInfo(name = "user_id")
    @SerializedName("user_id")
    private String user_id;

    @ColumnInfo(name = "operation")
    @SerializedName("operation")
    private Byte operation;

    @ColumnInfo(name = "log_at")
    @SerializedName("log_at")
    private String log_at;
    @ColumnInfo(name = "rfid")
    @SerializedName("rfid")
    private String rfid ;

    @ColumnInfo(name = "cell_number")
    @SerializedName("cell_number")
    private Integer cell_number ; //格子编码

    @ColumnInfo(name = "nick_name")
    private String nick_name;
    @ColumnInfo(name = "eas_material_number")
    private String eas_material_number; //编号

    @ColumnInfo(name = "eas_material_name")

    private String eas_material_name; //名称

     @ColumnInfo(name = "eas_unit_number")

    private String eas_unit_number; //规格

    @ColumnInfo(name = "eas_supplier_name")

    private String eas_supplier_name; //厂家

    @ColumnInfo(name = "eas_unit_name")

    private String eas_unit_name; //单位

    @ColumnInfo(name = "number")

    private String number; //数量

    @ColumnInfo(name = "eas_lot")

    private String eas_lot ; //批号

    @ColumnInfo(name = "hasUpload")
    private boolean hasUpload;
    @ColumnInfo(name = "eas_specs")
    public  String eas_specs;
    @ColumnInfo(name = "eas_manufacturer")
    public  String eas_manufacturer;
    @ColumnInfo(name = "role_id")
    private int role_id;

    @ColumnInfo(name = "isOfflineData")
    private boolean isOfflineData;

    public boolean isOfflineData() {
        return isOfflineData;
    }

    public void setOfflineData(boolean offlineData) {
        isOfflineData = offlineData;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public String getEas_manufacturer() {
        return eas_manufacturer;
    }

    public void setEas_manufacturer(String eas_manufacturer) {
        this.eas_manufacturer = eas_manufacturer;
    }

    public String getEas_specs() {
        return eas_specs;
    }

    public void setEas_specs(String eas_specs) {
        this.eas_specs = eas_specs;
    }

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

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
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

    public String getEas_material_number() {
        return eas_material_number;
    }

    public void setEas_material_number(String eas_material_number) {
        this.eas_material_number = eas_material_number;
    }

    public String getEas_material_name() {
        return eas_material_name;
    }

    public void setEas_material_name(String eas_material_name) {
        this.eas_material_name = eas_material_name;
    }

    public String getEas_unit_number() {
        return eas_unit_number;
    }

    public void setEas_unit_number(String eas_unit_number) {
        this.eas_unit_number = eas_unit_number;
    }

    public String getEas_supplier_name() {
        return eas_supplier_name;
    }

    public void setEas_supplier_name(String eas_supplier_name) {
        this.eas_supplier_name = eas_supplier_name;
    }

    public String getEas_unit_name() {
        return eas_unit_name;
    }

    public void setEas_unit_name(String eas_unit_name) {
        this.eas_unit_name = eas_unit_name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEas_lot() {
        return eas_lot;
    }

    public void setEas_lot(String eas_lot) {
        this.eas_lot = eas_lot;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public boolean isHasUpload() {
        return hasUpload;
    }

    public void setHasUpload(boolean hasUpload) {
        this.hasUpload = hasUpload;
    }

    public Integer getCell_number() {
        return cell_number;
    }

    public void setCell_number(Integer cell_number) {
        this.cell_number = cell_number;
    }

    @Override
    public String toString() {
        return "RfidOperationEntity{" +
                "id=" + id +
                ", user_log_id='" + user_log_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", operation=" + operation +
                ", log_at='" + log_at + '\'' +
                ", eas_material_number='" + eas_material_number + '\'' +
                ", eas_material_name='" + eas_material_name + '\'' +
                ", eas_unit_number='" + eas_unit_number + '\'' +
                ", eas_supplier_name='" + eas_supplier_name + '\'' +
                ", eas_unit_name='" + eas_unit_name + '\'' +
                ", number='" + number + '\'' +
                ", eas_lot='" + eas_lot + '\'' +
                ", rfid='" + rfid + '\'' +
                ", hasUpload=" + hasUpload +
                '}';
    }
}
