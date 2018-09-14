package com.filebox.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;

public class ModelUtil {
	/**
     * 获取前端传来的数组对象并响应成Model列表
     *
     * @param modelClass
     * @param modelName
     * @return
     */
    public static <T> List<T> getModels(Controller con,Class<T> modelClass, String modelName) {
        List<String> nos = getModelsNoList(con,modelName);
        List<T> list =new ArrayList<T>();
        for (String no : nos) {
            T m = con.getModel(modelClass, modelName + "[" + no + "]");
            if (m != null) {
                list.add(m);
            }
        }
        return list;
    }
    /**
     * 提取model对象数组的标号
     *
     * @param modelName
     * @return
     */
    protected static List<String> getModelsNoList(Controller con,String modelName) {
        // 提取下标
        List<String> list = new ArrayList<String>();
        String modelNameAndLeft = modelName + "[";
        Map<String, String[]> parasMap = con.getRequest().getParameterMap();
        for (Map.Entry<String, String[]> e : parasMap.entrySet()) {
            String paraKey = e.getKey();
            if (paraKey.startsWith(modelNameAndLeft)) {
                String no = paraKey.substring(paraKey.indexOf("[") + 1,
                        paraKey.indexOf("]"));
                if (!list.contains(no)) {
                    list.add(no);
                }
            }
        }
        Collections.sort(list);
        return list;
    }

}
