package com.jhteck.icebox.bean;

import com.jhteck.icebox.repository.entity.RfidOperationEntity;

public class ExceptionRecordBean extends RfidOperationEntity {
    private Integer remain;
    //    private rfid : String,
    private Integer error_code;

    public String getError_code_desc() {
        return error_code_desc;
    }

    public void setError_code_desc(String error_code_desc) {
        this.error_code_desc = error_code_desc;
    }

    private String error_code_desc;

    public Integer getRemain() {
        return remain;
    }

    public void setRemain(Integer remain) {
        this.remain = remain;
    }

    public Integer getError_code() {
        return error_code;
    }

    public void setError_code(Integer error_code) {
        this.error_code = error_code;
    }
//    private log_at : String,
//    private hasUpload: Boolean
}
