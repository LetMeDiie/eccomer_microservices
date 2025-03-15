package kz.amihady.eccomerce.exceiption;

public class BusinessException extends RuntimeException{

    public BusinessException(String message) {
        super(message);
    }
}
