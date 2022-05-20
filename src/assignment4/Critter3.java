/*
 * CRITTERS Critter3.java
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
 * Lover, reproduces during a fight then runs away.
 */
public class Critter3 extends Critter {

    @Override
    public void doTimeStep() {
    }

    @Override
    public boolean fight(String opponent) {
        Critter baby = new Critter3();
        reproduce(baby, getRandomInt(8));
        run(getRandomInt(8));
        return false;
    }

    @Override
    public String toString() {
        return "3";
    }

}