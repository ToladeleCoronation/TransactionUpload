package com.coronation.upload.dto;

public class TransferResponses {
    private String tranId;

    private String tranDateTime;

    private String responseDescription;

    private String responseCode;

    public String getTranId ()
    {
        return tranId;
    }

    public void setTranId (String tranId)
    {
        this.tranId = tranId;
    }

    public String getTranDateTime ()
    {
        return tranDateTime;
    }

    public void setTranDateTime (String tranDateTime)
    {
        this.tranDateTime = tranDateTime;
    }

    public String getResponseDescription ()
    {
        return responseDescription;
    }

    public void setResponseDescription (String responseDescription)
    {
        this.responseDescription = responseDescription;
    }

    public String getResponseCode ()
    {
        return responseCode;
    }

    public void setResponseCode (String responseCode)
    {
        this.responseCode = responseCode;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [tranId = "+tranId+", tranDateTime = "+tranDateTime+", responseDescription = "+responseDescription+", responseCode = "+responseCode+"]";
    }
}
