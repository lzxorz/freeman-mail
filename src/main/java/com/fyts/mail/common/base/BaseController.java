package com.fyts.mail.common.base;

import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import sun.security.validator.ValidatorException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author: 刘志新
 * @email: lzxorz@163.com
 */
public class BaseController {

    @Autowired
    protected Validator validator;

    public <T> void validate(T obj) throws ValidatorException {
        Set<ConstraintViolation<T>> constraintViolations = this.validator.validate(obj, new Class[0]);
        if (constraintViolations != null && constraintViolations.size() > 0) {
            StringBuffer sb = new StringBuffer();
            Iterator var4 = constraintViolations.iterator();

            while(var4.hasNext()) {
                ConstraintViolation constraint = (ConstraintViolation)var4.next();
                sb.append(constraint.getMessage());
            }

            throw new ValidatorException(sb.toString());
        }
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            public void setAsText(String text) {
                this.setValue(DateUtil.parseDateTime(text));
            }
        });
    }


}
