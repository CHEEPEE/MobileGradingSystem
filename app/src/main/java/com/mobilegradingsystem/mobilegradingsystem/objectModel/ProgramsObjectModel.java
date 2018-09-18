package com.mobilegradingsystem.mobilegradingsystem.objectModel;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class ProgramsObjectModel {
    private String department;
    private String key;
    private String program;


    public ProgramsObjectModel(){

    }
    public ProgramsObjectModel(String department, String key,String program
    ){
        this.department = department;
        this.key = key;
        this.program = program;

    }


    public String getDepartment() {
        return department;
    }

    public String getKey() {
        return key;
    }

    public String getProgram() {
        return program;
    }
}
