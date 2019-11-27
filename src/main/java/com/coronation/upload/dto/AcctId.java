package com.coronation.upload.dto;

public class AcctId {
    private String acctId;

    public String getAcctId ()
    {
        return acctId;
    }

    public void setAcctId (String acctId)
    {
        this.acctId = acctId;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [acctId = "+acctId+"]";
    }
}
