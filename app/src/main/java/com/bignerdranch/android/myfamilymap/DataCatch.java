package com.bignerdranch.android.myfamilymap;

import java.util.*;

import model.*;

//DataCache is used for anything that I will need to keep track.

class DataCache {

    private DataCache(){}
    private static DataCache instance = new DataCache();

    public static DataCache getInstance(){
        return instance;
    }

    //All the strings represent an ID.
    Map<String,Person> people;
    Map<String,Event> events;
    Map<String,List<Event>> familyEvents;
    Set<String> maternalTree;
    Set<String> paternalTree;

    //There will be a class called settings, data cache will help storing those settings;
    //Settings settings;

    //Class Methods: Person getPersonByID(String PersonID){}, Event getEventByID(String EventID){},
    //List<Event> getPersonEvents(String PersonID)

}
