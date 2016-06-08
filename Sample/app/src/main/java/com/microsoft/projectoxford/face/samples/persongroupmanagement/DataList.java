package com.microsoft.projectoxford.face.samples.persongroupmanagement;

import android.provider.ContactsContract;

/**
 * Created by tamarazu on 5/19/2016.
 */
public class DataList {

    ContactsContract.Data date;
    int trainingNum;

    public DataList(int trainingNum, ContactsContract.Data date){
        this.trainingNum =trainingNum;
        this.date = date;
    }

    public ContactsContract.Data getDate() {
        return date;
    }

    public void setDate(ContactsContract.Data date) {
        this.date = date;
    }

    public int getTrainingNum() {
        return trainingNum;
    }

    public void setTrainingNum(int trainingNum) {
        this.trainingNum = trainingNum;
    }
}
