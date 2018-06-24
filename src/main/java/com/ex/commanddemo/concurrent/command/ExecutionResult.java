package com.ex.commanddemo.concurrent.command;

/**
 * Created by edison
 * On 2018/4/11 17:31
 */
public class ExecutionResult {

	private Code resultCode;
	private String msg;

	public enum Code{
		SUCCESS, TIMEOUT, FAILED
	}

	public ExecutionResult(Code code) {
		resultCode = code;
	}

    public ExecutionResult(Code resultCode, String msg) {
        this.resultCode = resultCode;
        this.msg = msg;
    }

    public Code getResultCode() {
		return resultCode;
	}

	public void setResultCode(Code resultCode) {
		this.resultCode = resultCode;
	}

    public void setResultCode(Code resultCode, String msg) {
        this.resultCode = resultCode;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static ExecutionResult successResult(){
		return new ExecutionResult(Code.SUCCESS);
	}

	public static ExecutionResult failedResult(){
		return new ExecutionResult(Code.FAILED);
	}

    public static ExecutionResult failedResult(String msg){
        return new ExecutionResult(Code.FAILED, msg);
    }

	public static ExecutionResult timeoutResult(){
		return new ExecutionResult(Code.TIMEOUT, "请求已超时，请重试");
	}
}
