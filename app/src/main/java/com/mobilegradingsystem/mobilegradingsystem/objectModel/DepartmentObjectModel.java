package com.mobilegradingsystem.mobilegradingsystem.objectModel;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class DepartmentObjectModel {
    private String department;

    private String key;


    public DepartmentObjectModel(){

    }
    public DepartmentObjectModel(String department, String key
    ){
        this.department = department;
        this.key = key;

    }


    public String getDepartment() {
        return department;
    }

    public String getKey() {
        return key;
    }
}
