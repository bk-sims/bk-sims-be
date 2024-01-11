package com.dalv.bksims.validations;

import com.dalv.bksims.exceptions.FieldBlankException;
import com.dalv.bksims.exceptions.InvalidDateFormatException;
import com.dalv.bksims.exceptions.InvalidDateRangeException;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class DateValidator {
    public static void validateStartDateAndEndDate(String startDate, String endDate) {
        validateNotBlank(startDate, "startDate");
        validateNotBlank(endDate, "endDate");
        validateDateFormat(startDate, "startDate");
        validateDateFormat(endDate, "endDate");
        validateStartDateBeforeEndDate(startDate, endDate);
    }

    public static void validateNotBlank(String date, String fieldName) throws FieldBlankException {
        if (StringUtils.isBlank(date)) {
            throw new FieldBlankException(fieldName + " cannot be blank");
        }
    }

    public static void validateDateFormat(String date, String fieldName) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException("Invalid date format of field " + fieldName + " with value " + date);
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false); // Disable lenient mode
            sdf.parse(date);
        } catch (ParseException e) {
            throw new InvalidDateFormatException("Invalid date format of field " + fieldName + " with value " + date);
        }

    }

    public static void validateStartDateBeforeEndDate(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        if (!start.isBefore(end)) {
            throw new InvalidDateRangeException("Start date must be before end date");
        }
    }
}
