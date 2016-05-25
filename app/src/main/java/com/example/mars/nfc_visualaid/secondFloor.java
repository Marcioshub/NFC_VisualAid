package com.example.mars.nfc_visualaid;

/**
 * Created by Mars on 5/14/16.
 */
public class secondFloor {
    private String[] southSide;
    private String[] northSide;

    public secondFloor(){
        southSide = new String[9];
        northSide = new String [11];

        southSide[0] = "SA1";       // stairs
        southSide[1] = "W23";       // ~23 steps
        southSide[2] = "R2112";     // room 2112
        southSide[3] = "W4";        // ~4 steps
        southSide[4] = "R2114";     // room 2114
        southSide[5] = "W8";        // ~8 steps
        southSide[6] = "R2118";     // room 2118
        southSide[7] = "W11";       // ~11 steps
        southSide[8] = "R2122";     // room 2122


        //set north rooms
        northSide[0] = "R2105b";    // room 2105b
        northSide[1] = "W2";        // ~2 steps
        northSide[2] = "R2107";     // room 2107
        northSide[3] = "W6";        // ~6 steps
        northSide[4] = "R2109";     // room 2109
        northSide[5] = "W10";       // ~10 steps
        northSide[6] = "R2113";     // room 2113
        northSide[7] = "W9";        // ~9 steps
        northSide[8] = "R2117";     // room 2117
        northSide[9] = "W21";       // ~21 steps
        northSide[10] = "R2127";    // 2127
    }

}
