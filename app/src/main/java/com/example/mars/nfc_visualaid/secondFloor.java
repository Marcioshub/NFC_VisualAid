package com.example.mars.nfc_visualaid;

import android.util.Log;

/**
 * Created by Mars on 5/14/16.
 */
public class secondFloor {
    private String[] southSide;
    private String[] northSide;
    private int steps;

    public secondFloor() {
        southSide = new String[11];
        northSide = new String[11];

        /*
            First char of string means the type of room it is
            S = Stairs
            W = Walking steps
            R = Room

            The last char means if its on the north or south side
            N = North
            S = South
        */

        //set south rooms
        //southSide[0] = "SA1";       // stairs SA1
        //southSide[1] = "10";       // ~10 steps
        //southSide[2] = "10";       // ~10 steps
        //southSide[3] = "3";        // ~3 steps
        southSide[0] = "R2112S";     // room 2112
        southSide[1] = "4";        // ~4 steps
        southSide[2] = "R2114S";     // room 2114
        southSide[3] = "8";        // ~8 steps
        southSide[4] = "R2118S";     // room 2118
        southSide[5] = "11";       // ~11 steps
        southSide[6] = "R2122S";     // room 2122

        //set north rooms
        northSide[0] = "R2105N";    // room 2105b
        northSide[1] = "2";        // ~2 steps
        northSide[2] = "R2107N";     // room 2107
        northSide[3] = "6";        // ~6 steps
        northSide[4] = "R2109N";     // room 2109
        northSide[5] = "10";       // ~10 steps
        northSide[6] = "R2113N";     // room 2113

        //northSide[7] = "9";        // ~9 steps
        //northSide[8] = "R2117N";     // room 2117
        //northSide[9] = "21";       // ~21 steps
        //northSide[10] = "R2127N";    // 2127
    }


    /**
     * Depending if the curr
     *
     *
     * @param currLocation - Is your current room location
     * @param destination - The room you want to go to
     * @return - This returns a string which will direct you
     * in the right direction
     */
    public String movementHelper(String currLocation, String destination){
        steps = 0;
        int index = 0;
        String text = "";

        //convert the string into integer to compare values
        int curr = Integer.parseInt(currLocation.substring(1, 4));
        int des = Integer.parseInt(destination.substring(1, 4));

        //find the index of current location
        for(int i = 0; i < 7; i++)
            if(southSide[i].equals(currLocation)){
                index = i;
                break;
            }

        if(currLocation.charAt(5) == 'S' && destination.charAt(5) == 'S') {

            //figures out which direction to go
            if (curr < des) {
                for (int i = index; i < 7; i++)
                    if (southSide[i].charAt(0) != 'R')
                        steps += Integer.parseInt(southSide[i]);

                text += "Go right and take about ";
            } else {
                for (int i = index; i > -1; i--)
                    if (southSide[i].charAt(0) != 'R')
                        steps += Integer.parseInt(southSide[i]);

                text += "Go left and take about ";
            }
        }
        else if(currLocation.charAt(5) == 'N' && destination.charAt(5) == 'N'){
            //figures out which direction to go
            if (curr < des) {
                for (int i = index; i < 7; i++)
                    if (northSide[i].charAt(0) != 'R')
                        steps += Integer.parseInt(northSide[i]);

                text += "Go left and take about ";
            } else {
                for (int i = index; i > -1; i--)
                    if (northSide[i].charAt(0) != 'R')
                        steps += Integer.parseInt(northSide[i]);

                text += "Go right and take about ";
            }
        }
        else if(currLocation.charAt(5) == 'S' && destination.charAt(5) == 'N'){
            //figures out which direction to go
            if (curr < des) {
                for (int i = index; i < 7; i++)
                    if (northSide[i].charAt(0) != 'R')
                        steps += Integer.parseInt(northSide[i]);

                text += "Slowly turn around and take about 4 steps across the other wall " +
                        " and go left and take about ";
            } else {
                for (int i = index; i > -1; i--)
                    if (northSide[i].charAt(0) != 'R')
                        steps += Integer.parseInt(northSide[i]);

                text += "Slowly turn around and take about 4 steps across the other wall " +
                        " and go right and take about ";
            }
        }
        else if(currLocation.charAt(5) == 'N' && destination.charAt(5) == 'S'){
            //figures out which direction to go
            if (curr < des) {
                for (int i = index; i < 7; i++)
                    if (southSide[i].charAt(0) != 'R')
                        steps += Integer.parseInt(southSide[i]);

                text += "Slowly turn around and take about 4 steps across the other " +
                        "wall parallel to this one and go right and take about ";
            } else {
                for (int i = index; i > -1; i--)
                    if (southSide[i].charAt(0) != 'R')
                        steps += Integer.parseInt(southSide[i]);

                text += "Slowly turn around and take about 4 steps across the other " +
                        "wall parallel to this one and go left and take about ";
            }
        }
        else{
            return "Error";
        }

        text += steps + " number of steps";

        return text;
    }
}
