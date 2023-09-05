package com.jhteck.icebox.bean;

import java.util.Objects;

/**
 * @author wade
 * @Description:(药品信息)
 * @date 2023/6/29 20:13
 */
public class DrugBean {

    private String epc;
    private int antId;
    private int rssi;

    public DrugBean(String epc, int antId, int rssi) {
        this.epc = epc;
        this.antId = antId;
        this.rssi = rssi;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public int getAntId() {
        return antId;
    }

    public void setAntId(int antId) {
        this.antId = antId;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DrugBean drugBean = (DrugBean) o;
        return  Objects.equals(epc, drugBean.epc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(epc, antId, rssi);
    }
}
