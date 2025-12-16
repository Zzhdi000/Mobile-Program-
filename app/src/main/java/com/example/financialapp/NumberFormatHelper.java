package com.example.financialapp;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormatHelper {

    public static String formatCurrency(String currency, long amount) {

        Locale locale;

        switch (currency) {
            case "IDR":
                locale = new Locale("id", "ID");
                break;

            case "USD":
                locale = Locale.US;
                break;

            case "MYR":
                locale = new Locale("ms", "MY");
                break;

            case "LKR":
            default:
                locale = new Locale("en", "LK");
                break;
        }

        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        return nf.format(amount);

    }

    public static String getCurrencySymbol(String currency) {

        switch (currency) {

            case "IDR":
                return "Rp";   // Rupiah

            case "USD":
                return "$";    // Dollar

            case "MYR":
                return "RM";   // Ringgit Malaysia

            case "LKR":
                return "රු";   // Sri Lanka Rupee

            default:
                return currency;
        }
    }
}