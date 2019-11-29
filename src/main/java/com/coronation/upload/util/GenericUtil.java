package com.coronation.upload.util;

import com.coronation.upload.domain.DataUpload;
import com.coronation.upload.domain.User;
import com.coronation.upload.services.UserService;
import org.apache.commons.io.IOUtils;
import org.springframework.util.Base64Utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by Toyin on 2/3/19.
 */
public class GenericUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter longFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);

    public static boolean dateBetween(LocalDateTime dateToCompare, LocalDateTime startDate, LocalDateTime endDate) {
        if ((dateToCompare.isAfter(startDate) && dateToCompare.isBefore(endDate))
                || dateToCompare.equals(startDate) || dateToCompare.equals(endDate)) {
            return true;
        } else {
            return false;
        }
    }

    public static LocalDateTime getLocalDateTimeFromString(String dateString) throws DateTimeParseException {
        return LocalDateTime.parse(dateString, formatter);
    }

    public static Map<String, Object> getUploadDetails(DataUpload dataUpload, User user) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("firstName", user.getFirstName());
        variables.put("fileName", dataUpload.getUploadFile());
        variables.put("reconFile", dataUpload.getUploadFile());
        variables.put("successRecord", dataUpload.getSuccess());
        variables.put("invalidRecords",dataUpload.getInvalid());
        variables.put("unmatchedRecord", dataUpload.getUnmatched());
        variables.put("duplicate", dataUpload.getDuplicate());
        variables.put("exception", dataUpload.getExceptions());
        return variables;
    }
    public static String localDateTimeToString(LocalDateTime localDateTime) {
        return formatter.format(localDateTime);
    }

    public static String localDateTimeToLongString(LocalDate localDate) {
        return longFormatter.format(localDate).replace(" AD", "");
    }

    public static LocalDateTime truncateTime(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.DAYS);
    }

    public static LocalDateTime ceilTime(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.DAYS).plusHours(24).minusNanos(1);
    }

    public static String bytesToBase64(byte[] bytes) {
        return Base64Utils.encodeToString(bytes);
    }

    public static LocalDateTime getFirstDayOfTheWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS);
    }

    public static LocalDateTime getFirstDayOfTheMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DATE, 1);
        return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS);
    }

    public static LocalDateTime getFirstDayOfTheYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS);
    }

    public static String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }

    public static String getOrigin(HttpServletRequest request) {
        return request.getHeader("origin");
    }

    public static String generateKey(int keyLen) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keyLen);
        SecretKey secretKey = keyGen.generateKey();
        byte[] encoded = secretKey.getEncoded();
        return DatatypeConverter.printHexBinary(encoded).toLowerCase();
    }

    public static String generateRandomString(int length) {
        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] randomChars = new char[length];
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            randomChars[i] = chars[random.nextInt(chars.length)];
        }

        return new String(randomChars);
    }

    public static boolean isStaffEmail(String email) {
        return email.toLowerCase().endsWith(Constants.STAFF_EMAIL_SUFFIX);
    }

    public static String generateRandomId() {
        return UUID.randomUUID().toString();
    }

    public static LocalDateTime[] getDateRange(LocalDateTime from, LocalDateTime to) {
        if (from == null) {
            from = LocalDateTime.now();
        }
        if (to == null || to.isBefore(from)) {
            to = from;
        }
        from = GenericUtil.truncateTime(from);
        to = GenericUtil.ceilTime(to);
        return new LocalDateTime[] {from, to};
    }

    public static User getUserFromRequest(HttpServletRequest req, UserService userService) {
        Principal principal = req.getUserPrincipal();
        return userService.findByEmail(principal.getName());
    }

    public static LocalDateTime dateTimeFromString(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return LocalDateTime.parse(dateStr, formatter);
    }

    public static String generateRandomDigits(int length) {
        SecureRandom random = new SecureRandom();
        String result = "";
        for (int i = 0; i < length; i++) {
            result += random.nextInt(10);
        }
        return result;
    }

    public static String formatBigDecimal(BigDecimal amount) {
        if (amount != null) {
            return String.format("%,.2f", amount.setScale(2, RoundingMode.DOWN));
        } else {
            return "";
        }
    }

    public static String getStoragePath() {
        String path = "";
        if (OSValidator.isWindows()) {
            path = GenericUtil.getWindowsDrive() + "\\ProgramData\\DATAUPLOAD\\";
        } else {
            path = "/etc/dataupload/";
        }
        return path;
    }

    public static String getWindowsDrive() {
        return System.getenv("SystemDrive");
    }

    public static byte[] pathToByteArray(String path) throws IOException {
        File file = Paths.get(path).toFile();
        return IOUtils.toByteArray(new FileInputStream(file));
    }

    public static String pathToFileName(String path) {
        path = path.replaceAll("\\\\", "/");
        int startIndex = path.lastIndexOf("/");
        path = path.substring(startIndex + 1);
        return path;
    }

    public static String appendDateToFileName(String fileName) {
        fileName = fileName.substring(0, fileName.lastIndexOf(".")) +
        System.currentTimeMillis() + fileName.substring(fileName.lastIndexOf("."));
        return fileName;
    }

    public static void writeBytesToFile(byte[] bytes, String fileDest) throws IOException {
        Path path = Paths.get(fileDest);
        Files.write(path, bytes);
    }
}
