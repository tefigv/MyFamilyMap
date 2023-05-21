package com.bignerdranch.android.myfamilymap;

import java.util.*;

import model.*;

class DataCache {
    private static DataCache instance = new DataCache();

    public static DataCache getInstance(){
        return instance;
    }

    List<Person> people;
    List<Event> events;


}
