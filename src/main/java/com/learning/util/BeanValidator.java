package com.learning.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.learning.exception.ParamException;
import org.apache.commons.collections.MapUtils;

import javax.validation.*;
import java.util.*;

public class BeanValidator {

    public static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    public static <T> Map<String, String> validate(T t, Class... groups){
        Validator validator = validatorFactory.getValidator();
        Set validateResult = validator.validate(t,groups);
        if(validateResult.isEmpty()){
            return Collections.emptyMap();
        } else {
            LinkedHashMap errors = Maps.newLinkedHashMap();
            Iterator iterator = validateResult.iterator();
            while(iterator.hasNext()){
                ConstraintViolation constraintViolation = (ConstraintViolation) iterator.next();
                errors.put(constraintViolation.getPropertyPath().toString(),constraintViolation.getMessage());
            }
            return errors;
        }
    }

    public static Map<String,String> validateList(Collection<?> collection){
        Preconditions.checkNotNull(collection);
        Iterator iterator = collection.iterator();
        Map errors;

        do{
            if(!iterator.hasNext()){
                return Collections.emptyMap();
            }
            Object object = iterator.next();
            errors = validate(object,new Class[0]);
        }while(errors.isEmpty());

        return errors;
    }

    public static Map<String, String> validateObject(Object first, Object... objects) {
        if(objects!=null && objects.length > 0 ){
            return validateList(Lists.asList(first,objects));
        }else{
            return validate(first, new Class[0]);
        }
    }

    public static void check(Object param) {
        Map<String, String> map = BeanValidator.validateObject(param);
        if(MapUtils.isNotEmpty(map)){
            throw new ParamException(map.toString());
        }
    }


}
