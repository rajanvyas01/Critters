/*
 * CRITTERS Critter4.java
 * EE422C Project 4 submission by
 * Rajan Vyas
 * rv23454
 * 16160
 * Slip days used: <2>
 * Fall 2020
 */

package assignment4;

import assignment4.Critter.TestCritter;
import java.util.List;

/**
 * Monk, always walks, never fights.
 */
public class Critter4 extends Critter {

    @Override
    public void doTimeStep() {walk(getRandomInt(8)); }

    @Override
    public boolean fight(String opponent) {
        walk(getRandomInt(8));
        return true;
    }

    @Override
    public String toString() {
        return "4";
    }

}