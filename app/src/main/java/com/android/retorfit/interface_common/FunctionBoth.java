package com.android.retorfit.interface_common;

public abstract class FunctionBoth <Param>extends Function {
    public FunctionBoth(String functionName) {
        super(functionName);
    }

    public abstract <Result> Result funtion(Param param);
}
