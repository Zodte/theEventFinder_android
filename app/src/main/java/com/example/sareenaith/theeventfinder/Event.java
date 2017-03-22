package com.example.sareenaith.theeventfinder;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by SareenAith on 12/2/2017.
 */

public class Event {

    private int id;

    private String name;
    private String description;
    private int ageMin;
    private int ageMax;
    private boolean genderRestriction;
    private ArrayList<Integer> attendees;
    private float lgt;
    private float lat;
    //private ArrayList<String> type
    private int creatorId;
    private Timestamp startDate;
    private Timestamp endDate;


    // Notice the empty constructor, because we need to be able to create an empty Event to add
    // to our model so we can use it with our form
    public Event() {

    }

    public Event(int id, String name, String description, int ageMin, int ageMax, boolean genderRestriction,
                 float lat, float lgt, int creatorId, Timestamp startDate, Timestamp endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.genderRestriction = genderRestriction;
        this.lat = lat;
        this.lgt = lgt;
        this.creatorId = creatorId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // public ArrayList<String>  getType() { return type; }

    // public void setType(ArrayList<String>  type) { this.type = type; }

    public int getAgeMin() { return ageMin; }

    public void setAgeMin(int ageMin) { if(ageMin >= 18) this.ageMin = ageMin; }

    public int getAgeMax() { return ageMax; }

    public void setAgeMax(int ageMax) { if(ageMax >= 18) this.ageMax = ageMax; }

    public boolean getGenderRestriction() { return genderRestriction; }

    public void setGenderRestriction(boolean genderRestriction) { this.genderRestriction = genderRestriction; }

    public float getLgt(){return this.lgt;}

    public void setLgt(float lgt){this.lgt = lgt;}

    public float getLat(){return this.lat;}

    public void setLat(float lat){this.lat = lat;}


    public void setAttendee(Integer userID) {
        if(this.attendees == null){
            this.attendees = new ArrayList<Integer>();
        }
        if(this.attendees.contains(userID)) return;
        else {
            this.attendees.add(userID);
        }
    }

    public void setAttendees(ArrayList<Integer> attendees) { this.attendees = attendees; }

    public ArrayList<Integer> getAttendees() {
        if(this.attendees == null){
            this.attendees = new ArrayList<Integer>();
        }
        return this.attendees;
    }


    public Timestamp getStartDate() { return startDate; }

    public void setStartDate(Timestamp date) { this.startDate = date; }

    public Timestamp getEndDate() { return endDate; }

    public void setEndDate(Timestamp date) { this.endDate = date; }

    public int getCreatorId() { return this.creatorId; }

    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }
}
