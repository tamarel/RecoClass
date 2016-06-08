package com.microsoft.projectoxford.face.samples.persongroupmanagement;

/**
 * Created by tamarazu on 5/18/2016.
 */
public class CourseProperties {
    String courseName;
    String courseId;

    public CourseProperties(String courseName , String courseId){
        this.courseId =courseId;
        this.courseName = courseName;
    }
    public String getCourseName() {return courseName;}
    public void setCourseName(String name) {this.courseName = name;}
    public String getCourseId() {return courseId;}
    public void setCourseId(String courseId) {this.courseId = courseId;}
}
