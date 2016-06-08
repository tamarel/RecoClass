package com.microsoft.projectoxford.face.samples.persongroupmanagement;

import com.microsoft.projectoxford.face.samples.StudentListActivity;

/**
 * Created by tamarazu on 5/23/2016.
 */
public class StudentProperties {
    public String name;
    public String id;

    public StudentProperties(String name, String id){
        this.name = name;
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
