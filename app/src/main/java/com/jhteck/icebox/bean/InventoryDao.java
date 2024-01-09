package com.jhteck.icebox.bean;

/**
 * @author wade
 * @Description:(登录页面库存信息dao)
 * @date 2023/7/2 9:59
 */
public class InventoryDao {
    private String DrugNO;//批次
    private String DrugName;
    private float DrugNumber;
    private String UnitName;
    private String expired_at;

    public InventoryDao(String drugNO, String drugName, float drugNumber, String unitName,String expired_at) {
        DrugNO = drugNO;
        DrugName = drugName;
        DrugNumber = drugNumber;
        UnitName = unitName;
         this.expired_at=expired_at;
    }

    public String getDrugNO() {
        return DrugNO;
    }

    public void setDrugNO(String drugNO) {
        DrugNO = drugNO;
    }

    public String getDrugName() {
        return DrugName;
    }

    public void setDrugName(String drugName) {
        DrugName = drugName;
    }

    public float getDrugNumber() {
        return DrugNumber;
    }

    public void setDrugNumber(float drugNumber) {
        DrugNumber = drugNumber;
    }

    public String getUnitName() {
        return UnitName;
    }

    public void setUnitName(String unitName) {
        UnitName = unitName;
    }

    public String getExpired_at() {
        return expired_at;
    }

    public void setExpired_at(String expired_at) {
        this.expired_at = expired_at;
    }
}
