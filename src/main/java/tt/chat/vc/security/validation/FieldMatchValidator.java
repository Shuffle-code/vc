package tt.chat.vc.security.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;


public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    private String message;
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.firstFieldName();
        secondFieldName = constraintAnnotation.secondFieldName();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean result = false;

        Object firstObject = new BeanWrapperImpl(value).getPropertyValue(firstFieldName);
        Object secondObject = new BeanWrapperImpl(value).getPropertyValue(secondFieldName);

        result = firstObject == null && secondObject == null || firstObject != null && firstObject.equals(secondObject);

        if (!result) {
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(secondFieldName)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }
        return result;
    }
}
