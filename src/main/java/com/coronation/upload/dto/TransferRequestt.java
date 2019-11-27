package com.coronation.upload.dto;

import java.util.List;

public class TransferRequestt {
   private List<Recs> recs;
    private String reqUuid;

    public List<Recs> getRecs() {
        return recs;
    }

    public void setRecs(List<Recs> recs) {
        this.recs = recs;
    }

    public String getReqUuid() {
        return reqUuid;
    }

    public void setReqUuid(String reqUuid) {
        this.reqUuid = reqUuid;
    }
}
