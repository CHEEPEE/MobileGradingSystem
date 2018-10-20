package com.mobilegradingsystem.mobilegradingsystem.objectModel;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class SectionObjectModel {

    private String key;
    private String section;
    private String program;
    private String yearLevel;
    private String department;

    public SectionObjectModel(){

    }
    public SectionObjectModel(String key, String department, String program, String yearLevel,String section
    ){

        this.key = key;
        this.section = section;
        this.program = program;
        this.yearLevel = yearLevel;
        this.department = department;

    }

    public String getYearLevel() {
        return yearLevel;
    }

    public String getDepartment() {
        return department;
    }

    public String getProgram() {
        return program;
    }

    public String getKey() {
        return key;
    }

    public String getSection() {
        return section;
    }
}
