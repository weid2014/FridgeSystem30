package com.jhteck.icebox.bean;

/**
 * @author wade
 * @Description:(登录页面库存信息dao)
 * @date 2023/7/2 9:59
 */
public class InventoryDao {
    private String DrugNO;//批次
    private String DrugName;
    private int DrugNumber;
    private int CellNumber;

    public InventoryDao(String drugNO, String drugName, int drugNumber, int cellNumber) {
        DrugNO = drugNO;
        DrugName = drugName;
        DrugNumber = drugNumber;
        CellNumber = cellNumber;
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

    public int getDrugNumber() {
        return DrugNumber;
    }

    public void setDrugNumber(int drugNumber) {
        DrugNumber = drugNumber;
    }

    public int getCellNumber() {
        return CellNumber;
    }

    public void setCellNumber(int cellNumber) {
        CellNumber = cellNumber;
    }
}
