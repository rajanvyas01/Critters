/*
 * CRITTERS Critter2.java
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
 * Mama Bear, reproduces every doTimeStep and always fights in encounters
 */
public class Critter2 extends Critter {

    @Override
    public void doTimeStep() {
        Critter2 cub = new Critter2();
        reproduce(cub, getRandomInt(8));
    }

    @Override
    public boolean fight(String opponent) { return true; }

    @Override
    public String toString() {
        return "2";
    }

}