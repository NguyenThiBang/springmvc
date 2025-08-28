package com.example.springmvc.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {
    
    private static final NumberFormat VND_FORMAT = NumberFormat.getInstance(new Locale("vi", "VN"));
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###");
    
    static {
        VND_FORMAT.setMaximumFractionDigits(0);
        VND_FORMAT.setMinimumFractionDigits(0);
    }
    
    /**
     * Formats a BigDecimal price to Vietnamese currency format
     * @param price The price to format
     * @return Formatted price string with VND symbol (e.g., "12.000.000 ₫")
     */
    public static String formatToVND(BigDecimal price) {
        if (price == null) {
            return "0 ₫";
        }
        return DECIMAL_FORMAT.format(price.longValue()).replace(",", ".") + " ₫";
    }
    
    /**
     * Formats a double price to Vietnamese currency format
     * @param price The price to format
     * @return Formatted price string with VND symbol
     */
    public static String formatToVND(double price) {
        return formatToVND(BigDecimal.valueOf(price));
    }
    
    /**
     * Formats a long price to Vietnamese currency format
     * @param price The price to format
     * @return Formatted price string with VND symbol
     */
    public static String formatToVND(long price) {
        return formatToVND(BigDecimal.valueOf(price));
    }
}
