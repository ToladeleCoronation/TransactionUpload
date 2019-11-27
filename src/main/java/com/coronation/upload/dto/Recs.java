package com.coronation.upload.dto;

public class Recs {
    private AcctId acctId;
    private String creditDebitFlg;
    private String serialNum;
    private TrnAmt trnAmt;
    private String trnParticulars;

    public AcctId getAcctId() {
        return acctId;
    }

    public void setAcctId(AcctId acctId) {
        this.acctId = acctId;
    }

    public String getCreditDebitFlg() {
        return creditDebitFlg;
    }

    public void setCreditDebitFlg(String creditDebitFlg) {
        this.creditDebitFlg = creditDebitFlg;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public TrnAmt getTrnAmt() {
        return trnAmt;
    }

    public void setTrnAmt(TrnAmt trnAmt) {
        this.trnAmt = trnAmt;
    }

    public String getTrnParticulars() {
        return trnParticulars;
    }

    public void setTrnParticulars(String trnParticulars) {
        this.trnParticulars = trnParticulars;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [trnParticulars = "+trnParticulars+", serialNum = "+serialNum+", trnAmt = "+trnAmt+", creditDebitFlg = "+creditDebitFlg+", acctId = "+acctId+"]";
    }
}
