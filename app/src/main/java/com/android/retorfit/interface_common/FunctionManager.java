package com.android.retorfit.interface_common;

import android.text.TextUtils;

import java.util.HashMap;

public class FunctionManager {
    private HashMap<String, FunctionOnlyParam> mOnlyPramMap ;
    private HashMap<String, FunctionNoParamNoResult> mNoneMap ;
    private HashMap<String, FunctionOnlyResult> mOlyResultMap ;
    private HashMap<String, FunctionBoth> mBothMap;
    static FunctionManager functionManager;

    public FunctionManager() {
        mOnlyPramMap = new HashMap<>();
        mNoneMap=new HashMap<>();
        mOlyResultMap=new HashMap<>();
        mBothMap=new HashMap<>();
    }

    public static FunctionManager getInstance() {
        if (functionManager == null) {
            functionManager = new FunctionManager();
        }
        return functionManager;
    }


    public  FunctionManager  addFunction(FunctionNoParamNoResult funtionNone){
        mNoneMap.put(funtionNone.mFunctionName,funtionNone);
        return  this;
    }

    public  FunctionManager  addFunction(FunctionOnlyParam funtionOnlyParam){
        mOnlyPramMap.put(funtionOnlyParam.mFunctionName,funtionOnlyParam);
        return  this;
    }

    public  FunctionManager  addFunction(FunctionOnlyResult functionOnlyResult){
        mOlyResultMap.put(functionOnlyResult.mFunctionName,functionOnlyResult);
        return  this;
    }


    public FunctionManager addFunction(FunctionBoth functionBoth) {
        mBothMap.put(functionBoth.mFunctionName, functionBoth);
        return this;
    }

    public void  invokeNoneFunc(String funName){
        if(TextUtils.isEmpty(funName)||mNoneMap==null||mNoneMap.size()==0){
            return;
        }
        FunctionNoParamNoResult f= mNoneMap.get(funName);
        if(f==null){
            return;
        }
        f.funtion();
    }
    public <Param> void  invokeOnlyPramFunc(String funName, Param param){
        if(TextUtils.isEmpty(funName)||mOnlyPramMap==null||mOnlyPramMap.size()==0){
            return;
        }
        FunctionOnlyParam f= mOnlyPramMap.get(funName);
        if(f==null){
            return;
        }
        f.funtion(param);
    }
    public <Result> Result invokeOlyResultFunc(String funName) {
        Result result;
        if (TextUtils.isEmpty(funName) || mOlyResultMap == null || mOlyResultMap.size() == 0) {
            return null;
        }
        FunctionOnlyResult f = mOlyResultMap.get(funName);
        if (f == null) {
            return null;
        }
        result = f.funtion();
        return result;
    }
    public  <Result,Param> Result invokeBothFunc(String funName, Param param) {
        Result result;
        if (TextUtils.isEmpty(funName) || mBothMap == null || mBothMap.size() == 0) {
            return null;
        }
        FunctionBoth f = mBothMap.get(funName);
        if (f == null) {
            return null;
        }
        result = (Result) f.funtion(param);
        return result;
    }

}
