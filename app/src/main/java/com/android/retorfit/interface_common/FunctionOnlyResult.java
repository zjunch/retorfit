package com.android.retorfit.interface_common;

public abstract class FunctionOnlyResult extends Function {
    public FunctionOnlyResult(String functionName) {
        super(functionName);
    }

    public abstract <Result> Result funtion();
}
