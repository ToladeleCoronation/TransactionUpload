package com.coronation.upload.util;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 5 * 60 * 60;
    public static final String SIGNING_KEY = "devtoyin123r";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String AUTHORITIES_KEY = "scopes";

    public static final String ACCOUNT_RESPONSE_CODE = "00";
    //    public static final String OFFICE_ACCOUNT_RESPONSE_CODE = null;
    public static final String ACCOUNT_ACTIVE_STATUS = "A";
    public static final String TRANSFER_RESPONSE_CODE = "000";
    public static final String BANK_CODE = "559";
    public static final String AUTO_DEBIT = "AUTO_DEBIT";

    public static final String SAVED_PREFIX = "saved";
    public static final String DUPLICATE_PREFIX = "duplicate";
    public static final String SUCCESSFUL_TRXN = "successfulUpload";
    public static final String UNMATCHED_PREFIX = "unmatched";
    public static final String INVALID_PREFIX = "invalid";
    public static final String EXCEPTIONS_PREFIX = "exceptions";
    public static final String SMS_CHARGE_LIEN = "Sms charge";
    public static final String INSUFFICIENT_FUND = "Insufficient available balance";
    public static final String TRANX_LIEN = "LIEN";
    public static final String STATUS = "SUCCESS";

    public static final String STAFF_EMAIL_SUFFIX = "coronationmb.com";
    public static final String DEFAULT_BANK_NAME = "Coronation Merchant Bank";

    public static final String accountCode = "NGN";
    public static final String serialNum1 = "1";
    public static final String serialNum2 = "2";

    public static final String credit = "C";
    public static final String debit = "D";

    public static final String STAFF_ENTRUST_GROUP = "Coronation Group";
    public static final String UPLOAD_CREATED = "uploadCreated";
    public static final String SUCCESSFUL_UPLOAD = "successfulUpload";
    public static final String UPLOAD_SUBJECT = "SMS Portal - APPROVAL Request";
    public static String SUCCESS_SUBJECT = "SMS CHARGES DEBITED ON";
    public static final String SUCCESS_FILENAME = "debitCredit.xlsx";

    public static List<String> logHeader() {
        List<String> logHead = new ArrayList<>();
        logHead.add("Account Number");
        logHead.add("Phone Number");
        logHead.add("Count");
        logHead.add("Amount (₦)");
        logHead.add("Narration");
        logHead.add("Status");
        logHead.add("Remark");
        logHead.add("Date Debited");

        return logHead;
    }

}
