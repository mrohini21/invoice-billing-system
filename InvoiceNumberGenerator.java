package com.company.invoice.util;

import java.time.LocalDate;
import java.util.Random;

public class InvoiceNumberGenerator {

    private static final Random random = new Random();

    public static String generateInvoiceNumber() {
        LocalDate today = LocalDate.now();

        String datePart = today.toString().replace("-", ""); // yyyyMMdd
        int randomPart = 1000 + random.nextInt(9000); // 4-digit random

        return "INV-" + datePart + "-" + randomPart;
    }
}