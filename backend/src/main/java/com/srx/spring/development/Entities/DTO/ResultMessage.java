package com.srx.spring.development.Entities.DTO;

import com.srx.spring.development.Enum.ResultCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class ResultMessage implements Serializable {
    private ResultCode ResultCode;
    private Object Result;
    private String message;
    private Integer code;


    public ResultMessage(com.srx.spring.development.Enum.ResultCode resultCode, Object result) {
        ResultCode = resultCode;
        Result = result;
        this.message = this.ResultCode.getMessage();
        this.code = this.ResultCode.getCode();
    }

    public ResultCode getResultCode() {
        return this.ResultCode;
    }

    public Object getResult() {
        return this.Result;
    }

    public String getMessage() {
        return this.message;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setResultCode(ResultCode ResultCode) {
        this.ResultCode = ResultCode;
    }

    public void setResult(Object Result) {
        this.Result = Result;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ResultMessage)) return false;
        final ResultMessage other = (ResultMessage) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$ResultCode = this.getResultCode();
        final Object other$ResultCode = other.getResultCode();
        if (this$ResultCode == null ? other$ResultCode != null : !this$ResultCode.equals(other$ResultCode))
            return false;
        final Object this$Result = this.getResult();
        final Object other$Result = other.getResult();
        if (this$Result == null ? other$Result != null : !this$Result.equals(other$Result)) return false;
        final Object this$message = this.getMessage();
        final Object other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) return false;
        final Object this$code = this.getCode();
        final Object other$code = other.getCode();
        if (this$code == null ? other$code != null : !this$code.equals(other$code)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ResultMessage;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $ResultCode = this.getResultCode();
        result = result * PRIME + ($ResultCode == null ? 43 : $ResultCode.hashCode());
        final Object $Result = this.getResult();
        result = result * PRIME + ($Result == null ? 43 : $Result.hashCode());
        final Object $message = this.getMessage();
        result = result * PRIME + ($message == null ? 43 : $message.hashCode());
        final Object $code = this.getCode();
        result = result * PRIME + ($code == null ? 43 : $code.hashCode());
        return result;
    }

    public String toString() {
        return "ResultMessage(ResultCode=" + this.getResultCode() + ", Result=" + this.getResult() + ", message=" + this.getMessage() + ", code=" + this.getCode() + ")";
    }
}
