package com.coronation.upload.repo.predicate;

import com.coronation.upload.domain.enums.GenericStatus;
import com.coronation.upload.domain.enums.PaymentStatus;
import com.coronation.upload.domain.enums.RoleType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * Created by Toyin on 2/17/19.
 */
public class EnumBooleanExpression {
    public static  <T> BooleanExpression getExpression(String key, Object value, PathBuilder<T> entityPath) {
        switch (key) {
            case "status":
                return entityPath.getEnum(key, GenericStatus.class).eq((GenericStatus) value);
            case "paymentStatus":
                return entityPath.getEnum(key, PaymentStatus.class).eq((PaymentStatus) value);
            case "role.name":
                return entityPath.getEnum(key, RoleType.class).eq((RoleType) value);
        }
        return null;
    }
}
