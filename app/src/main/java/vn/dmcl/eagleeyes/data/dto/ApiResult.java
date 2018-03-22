package vn.dmcl.eagleeyes.data.dto;

public class ApiResult<T> {
    private boolean Result;
    private int Code;
    private T Data;
    private String Message;

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isResult() {
        return Result;
    }

    public void setResult(boolean result) {
        Result = result;
    }
}
