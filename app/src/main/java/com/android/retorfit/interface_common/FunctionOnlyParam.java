package com.android.retorfit.interface_common;

public abstract  class FunctionOnlyParam<Param> extends Function {
    public FunctionOnlyParam(String functionName) {
        super(functionName);
    }
    public  abstract   void funtion(Param param);
}
