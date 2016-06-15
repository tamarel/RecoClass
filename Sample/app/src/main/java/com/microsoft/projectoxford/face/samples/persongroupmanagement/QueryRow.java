package com.microsoft.projectoxford.face.samples.persongroupmanagement;

import com.microsoft.projectoxford.face.samples.StudentListActivity;

/**
 * Created by tamarazu on 5/23/2016.
 */
public class QueryRow {
    public String name;
    public String id;
    public int number;

    public QueryRow(String name, String id, int number){
        this.name = name;
        this.id=id;
        this.number = number;
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int  number) {
        this.number = number;
    }
}
