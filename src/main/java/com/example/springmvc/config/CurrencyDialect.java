package com.example.springmvc.config;

import com.example.springmvc.util.CurrencyUtil;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

public class CurrencyDialect extends AbstractDialect implements IExpressionObjectDialect {

    public CurrencyDialect() {
        super("currency");
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return new IExpressionObjectFactory() {
            @Override
            public Set<String> getAllExpressionObjectNames() {
                return Collections.singleton("currency");
            }

            @Override
            public Object buildObject(IExpressionContext context, String expressionObjectName) {
                if ("currency".equals(expressionObjectName)) {
                    return new CurrencyExpressionObject();
                }
                return null;
            }

            @Override
            public boolean isCacheable(String expressionObjectName) {
                return true;
            }
        };
    }

    public static class CurrencyExpressionObject {
        public String formatVND(BigDecimal price) {
            return CurrencyUtil.formatToVND(price);
        }
        
        public String formatVND(Double price) {
            return CurrencyUtil.formatToVND(price);
        }
        
        public String formatVND(Long price) {
            return CurrencyUtil.formatToVND(price);
        }
    }
}
