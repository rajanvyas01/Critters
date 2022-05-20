/*
 * CRITTERS Critter.java
 * EE422C Project 4 submission by
 * Rajan Vyas
 * rv23454
 * 16160
 * Slip days used: <2>
 * Fall 2020
 */

package assignment4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/* 
 * See the PDF for descriptions of the methods and fields in this
 * class. 
 * You may add fields, methods or inner classes to Critter ONLY
 * if you make your additions private; no new public, protected or
 * default-package code or data can be added to Critter.
 */

public abstract class Critter {

    private int energy = 0;

    //Two added private variables for functionality
    private boolean hasMoved = false;
    private boolean isFighting = false;

    private int x_coord;
    private int y_coord;

    private static List<Critter> population = new ArrayList<Critter>();
    private static List<Critter> babies = new ArrayList<Critter>();

    /* Gets the package name.  This assumes that Critter and its
     * subclasses are all in the same package. */
    private static String myPackage;

    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    private static Random rand = new Random();

    public static int getRandomInt(int max) {
        return rand.nextInt(max);
    }

    public static void setSeed(long new_seed) {
        rand = new Random(new_seed);
    }

    /**
     * create and initialize a Critter subclass.
     * critter_class_name must be the unqualified name of a concrete
     * subclass of Critter, if not, an InvalidCritterException must be
     * thrown.
     *
     * @param critter_class_name
     * @throws InvalidCritterException
     */
    public static void createCritter(String critter_class_name)
            throws InvalidCritterException {
        try{
            Class critType = Class.forName(myPackage + "." + critter_class_name);
            Critter newCritter = (Critter) critType.newInstance();
            newCritter.x_coord = getRandomInt(Params.WORLD_WIDTH);
            newCritter.y_coord = getRandomInt(Params.WORLD_HEIGHT);
            newCritter.energy = Params.START_ENERGY;
            population.add(newCritter);
        }
        catch(Exception e){
            throw new InvalidCritterException(critter_class_name);
        }
    }

    /**
     * Gets a list of critters of a specific type.
     *
     * @param critter_class_name What kind of Critter is to be listed.
     *        Unqualified class name.
     * @return List of Critters.
     * @throws InvalidCritterException
     */
    public static List<Critter> getInstances(String critter_class_name)
            throws InvalidCritterException {
        List<Critter> list = new java.util.ArrayList<Critter>();
        try {
            Class critterType = Class.forName(myPackage + "." + critter_class_name);
            for(Critter x : population) {
                if(critterType.isInstance(x)){
                    list.add(x);
                }
            }
        }
        catch (Exception e) {
            throw new InvalidCritterException(critter_class_name);
        }
        return list;
    }

    /**
     * Clear the world of all critters, dead and alive
     */
    public static void clearWorld() {
        population.clear();
        babies.clear();
    }

    /**
     * Completes a worldTimeStep;
     * Does a TimeStep for every critter that is alive,
     * Runs all encounters between critters,
     * Applies energy deduction then clears all dead critters from the population,
     * Adds all new babies to the population,
     * Refreshes Clovers.
     */
    public static void worldTimeStep() {
        List<Critter> dead = new ArrayList<Critter>();
        for(Critter crit : population){
            crit.doTimeStep();
            if(crit.energy <= 0){
                dead.add(crit);
            }
        }
        population.removeAll(dead);
        dead.clear();

        doEncounters();

        //Apply resting energy deduction, reset hasMoved flag, then clear all dead critters
        for(Critter crit : population){
            crit.energy -= 1;
            crit.hasMoved = false;
            if(crit.energy <= 0){
                dead.add(crit);
            }
        }
        population.removeAll(dead);
        dead.clear();

        //Add new babies to the population
        population.addAll(babies);
        babies.clear();

        //Refresh Clovers
        for(int i = 0; i < Params.REFRESH_CLOVER_COUNT; i++){
            Clover clove = new Clover();
            clove.setEnergy(Params.START_ENERGY);
            clove.setX_coord(getRandomInt(Params.WORLD_WIDTH));
            clove.setY_coord(getRandomInt(Params.WORLD_HEIGHT));
            population.add(clove);
        }
    }

    /**
     * Prints the current iteration of the world
     */
    public static void displayWorld() {
        Critter[][] map = new Critter[Params.WORLD_HEIGHT][Params.WORLD_WIDTH];

        for(Critter crit : population){
            map[crit.y_coord][crit.x_coord] = crit;
        }

        //Prints top border
        System.out.println();
        System.out.print('+');
        for(int i = 0; i < Params.WORLD_WIDTH; i++){
            System.out.print('-');
        }
        System.out.print('+');
        System.out.println();

        //Prints map of Critters
        for(int i = 0; i < Params.WORLD_HEIGHT; i++){
            System.out.print('|');
            for(int j = 0; j < Params.WORLD_WIDTH; j++){
                if(map[i][j] != null) {
                    System.out.print(map[i][j].toString());
                } else {
                    System.out.print(' ');
                }
            }
            System.out.print('|');
            System.out.println();
        }

        //Prints bottom border
        System.out.print('+');
        for(int i = 0; i < Params.WORLD_WIDTH; i++){
            System.out.print('-');
        }
        System.out.print('+');
        System.out.println();

    }

    /**
     * Prints out how many Critters of each type there are on the
     * board.
     *
     * @param critters List of Critters.
     */
    public static void runStats(List<Critter> critters) {
        System.out.print("" + critters.size() + " critters as follows -- ");
        Map<String, Integer> critter_count = new HashMap<String, Integer>();
        for (Critter crit : critters) {
            String crit_string = crit.toString();
            critter_count.put(crit_string,
                    critter_count.getOrDefault(crit_string, 0) + 1);
        }
        String prefix = "";
        for (String s : critter_count.keySet()) {
            System.out.print(prefix + s + ":" + critter_count.get(s));
            prefix = ", ";
        }
        System.out.println();
    }

    public abstract void doTimeStep();

    public abstract boolean fight(String opponent);

    /* a one-character long string that visually depicts your critter
     * in the ASCII interface */
    public String toString() {
        return "";
    }

    protected int getEnergy() {
        return energy;
    }

    /**
     * Deducts energy cost for walking, then moves a critter to an adjacent space
     * on the board.
     * Only moves the critter if it hasn't already moved this worldTimeStep.
     *
     * @param direction The integer value from 0 to 7 that corresponds to the direction
     *                  of movement.
     */
    protected final void walk(int direction) {
        this.energy = this.energy - Params.WALK_ENERGY_COST;
        int origX = this.x_coord;
        int origY = this.y_coord;
        if(!this.hasMoved) {
            switch (direction) {
                case 0:
                    if (this.x_coord == Params.WORLD_WIDTH - 1) {
                        this.x_coord = 0;
                    } else {
                        this.x_coord += 1;
                    }
                    break;
                case 1:
                    if (this.x_coord == Params.WORLD_WIDTH - 1) {
                        this.x_coord = 0;
                    } else {
                        this.x_coord += 1;
                    }
                    if (this.y_coord == 0) {
                        this.y_coord = Params.WORLD_HEIGHT - 1;
                    } else {
                        this.y_coord -= 1;
                    }
                    break;
                case 2:
                    if (this.y_coord == 0) {
                        this.y_coord = Params.WORLD_HEIGHT - 1;
                    } else {
                        this.y_coord -= 1;
                    }
                    break;
                case 3:
                    if (this.x_coord == 0) {
                        this.x_coord = Params.WORLD_WIDTH - 1;
                    } else {
                        this.x_coord -= 1;
                    }
                    if (this.y_coord == 0) {
                        this.y_coord = Params.WORLD_HEIGHT - 1;
                    } else {
                        this.y_coord -= 1;
                    }
                    break;
                case 4:
                    if (this.x_coord == 0) {
                        this.x_coord = Params.WORLD_WIDTH - 1;
                    } else {
                        this.x_coord -= 1;
                    }
                    break;
                case 5:
                    if (this.x_coord == 0) {
                        this.x_coord = Params.WORLD_WIDTH - 1;
                    } else {
                        this.x_coord -= 1;
                    }
                    if (this.y_coord == Params.WORLD_HEIGHT - 1) {
                        this.y_coord = 0;
                    } else {
                        this.y_coord += 1;
                    }
                    break;
                case 6:
                    if (this.y_coord == Params.WORLD_HEIGHT - 1) {
                        this.y_coord = 0;
                    } else {
                        this.y_coord += 1;
                    }
                    break;
                case 7:
                    if (this.x_coord == Params.WORLD_WIDTH - 1) {
                        this.x_coord = 0;
                    } else {
                        this.x_coord += 1;
                    }
                    if (this.y_coord == Params.WORLD_HEIGHT - 1) {
                        this.y_coord = 0;
                    } else {
                        this.y_coord += 1;
                    }
                    break;
            }
            this.hasMoved = true;
            if(isFighting){
                for(Critter crit : population){
                    if (!crit.isFighting && crit.x_coord == this.x_coord && crit.y_coord == this.y_coord) {
                        this.x_coord = origX;
                        this.y_coord = origY;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Deducts energy cost for running, then moves a critter to a space
     * two spaces away in the given direction.
     * Only moves the critter if it hasn't already moved this worldTimeStep.
     *
     * @param direction The integer value from 0 to 7 that corresponds to the direction
     *                  of movement.
     */
    protected final void run(int direction) {
        this.energy = this.energy - Params.RUN_ENERGY_COST;
        int origX = this.x_coord;
        int origY = this.y_coord;
        if(!this.hasMoved) {
            switch (direction) {
                case 0:
                    if (this.x_coord >= Params.WORLD_WIDTH - 2) {
                        this.x_coord = (this.x_coord + 2) - Params.WORLD_WIDTH;
                    } else {
                        this.x_coord += 2;
                    }
                    break;
                case 1:
                    if (this.x_coord >= Params.WORLD_WIDTH - 2) {
                        this.x_coord = (this.x_coord + 2) - Params.WORLD_WIDTH;
                    } else {
                        this.x_coord += 2;
                    }
                    if (this.y_coord <= 1) {
                        this.y_coord = this.y_coord + Params.WORLD_HEIGHT - 2;
                    } else {
                        this.y_coord -= 2;
                    }
                    break;
                case 2:
                    if (this.y_coord <= 1) {
                        this.y_coord = this.y_coord + Params.WORLD_HEIGHT - 2;
                    } else {
                        this.y_coord -= 2;
                    }
                    break;
                case 3:
                    if (this.x_coord <= 1) {
                        this.x_coord = this.x_coord + Params.WORLD_WIDTH - 2;
                    } else {
                        this.x_coord -= 2;
                    }
                    if (this.y_coord <= 1) {
                        this.y_coord = this.y_coord + Params.WORLD_HEIGHT - 2;
                    } else {
                        this.y_coord -= 2;
                    }
                    break;
                case 4:
                    if (this.x_coord <= 1) {
                        this.x_coord = this.x_coord + Params.WORLD_WIDTH - 2;
                    } else {
                        this.x_coord -= 2;
                    }
                    break;
                case 5:
                    if (this.x_coord <= 1) {
                        this.x_coord = this.x_coord + Params.WORLD_WIDTH - 2;
                    } else {
                        this.x_coord -= 2;
                    }
                    if (this.y_coord >= Params.WORLD_HEIGHT - 2) {
                        this.y_coord = (this.y_coord + 2) - Params.WORLD_HEIGHT;
                    } else {
                        this.y_coord += 2;
                    }
                    break;
                case 6:
                    if (this.y_coord >= Params.WORLD_HEIGHT - 2) {
                        this.y_coord = (this.y_coord + 2) - Params.WORLD_HEIGHT;
                    } else {
                        this.y_coord += 2;
                    }
                    break;
                case 7:
                    if (this.x_coord >= Params.WORLD_WIDTH - 2) {
                        this.x_coord = (this.x_coord + 2) - Params.WORLD_WIDTH;
                    } else {
                        this.x_coord += 2;
                    }
                    if (this.y_coord >= Params.WORLD_HEIGHT - 2) {
                        this.y_coord = (this.y_coord + 2) - Params.WORLD_HEIGHT;
                    } else {
                        this.y_coord += 2;
                    }
                    break;
            }
            this.hasMoved = true;
            if(isFighting){
                for(Critter crit : population){
                    if (!crit.isFighting && crit.x_coord == this.x_coord && crit.y_coord == this.y_coord) {
                        this.x_coord = origX;
                        this.y_coord = origY;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Sets the parameters of the offspring based off the direction, the parents current position and
     * the parents energy.
     * Returns immediately if parent Critter lacks the minimum reproduction energy.
     * If offspring is valid, then after setting its parameters it is added to the babies collection.
     *
     * @param offspring The newly created Critter object that still needs its parameters set,
     *                  and to be added to the babies collection.
     * @param direction The integer value from 0 to 7 that corresponds to the direction
     *                  in relation to the parent that offspring will be placed.
     *
     */
    protected final void reproduce(Critter offspring, int direction) {
        if(getEnergy() < Params.MIN_REPRODUCE_ENERGY){
            return;
        }
        offspring.energy = Math.floorDiv(getEnergy(),2);
        this.energy = (int) Math.ceil((float) getEnergy() / 2);
        switch (direction) {
            case 0:
                if (this.x_coord == Params.WORLD_WIDTH - 1) {
                    offspring.x_coord = 0;
                } else {
                    offspring.x_coord = this.x_coord + 1;
                }
                break;
            case 1:
                if (this.x_coord == Params.WORLD_WIDTH - 1) {
                    offspring.x_coord = 0;
                } else {
                    offspring.x_coord = this.x_coord + 1;
                }
                if (this.y_coord == 0) {
                    offspring.y_coord = Params.WORLD_HEIGHT - 1;
                } else {
                    offspring.y_coord = this.y_coord - 1;
                }
                break;
            case 2:
                if (this.y_coord == 0) {
                    offspring.y_coord = Params.WORLD_HEIGHT - 1;
                } else {
                    offspring.y_coord = this.y_coord - 1;
                }
                break;
            case 3:
                if (this.x_coord == 0) {
                    offspring.x_coord = Params.WORLD_WIDTH - 1;
                } else {
                    offspring.x_coord = this.x_coord - 1;
                }
                if (this.y_coord == 0) {
                    offspring.y_coord = Params.WORLD_HEIGHT - 1;
                } else {
                    offspring.y_coord = this.y_coord - 1;
                }
                break;
            case 4:
                if (this.x_coord == 0) {
                    offspring.x_coord = Params.WORLD_WIDTH - 1;
                } else {
                    offspring.x_coord = this.x_coord - 1;
                }
                break;
            case 5:
                if (this.x_coord == 0) {
                    offspring.x_coord = Params.WORLD_WIDTH - 1;
                } else {
                    offspring.x_coord = this.x_coord - 1;
                }
                if (this.y_coord == Params.WORLD_HEIGHT - 1) {
                    offspring.y_coord = 0;
                } else {
                    offspring.y_coord = this.y_coord + 1;
                }
                break;
            case 6:
                if (this.y_coord == Params.WORLD_HEIGHT - 1) {
                    offspring.y_coord = 0;
                } else {
                    offspring.y_coord = this.y_coord + 1;
                }
                break;
            case 7:
                if (this.x_coord == Params.WORLD_WIDTH - 1) {
                    offspring.x_coord = 0;
                } else {
                    offspring.x_coord = this.x_coord + 1;
                }
                if (this.y_coord == Params.WORLD_HEIGHT - 1) {
                    offspring.y_coord = 0;
                } else {
                    offspring.y_coord = this.y_coord + 1;
                }
                break;
        }
        babies.add(offspring);
    }

    /**
     * Completes all encounters between all critters that are sharing a
     * space on the board.
     */
    private static void doEncounters(){

        for(int a = 0; a < population.size(); a++){
            Critter critA = population.get(a);
            if(critA.energy <= 0){continue;}

            for(int b = a+1; b < population.size(); b++){
                Critter critB = population.get(b);
                if(critB.energy <= 0){continue;}

                //If in the same location, do encounter
                if(critA.x_coord == critB.x_coord && critA.y_coord == critB.y_coord){
                    int battleX = critA.x_coord;
                    int battleY = critA.y_coord;
                    critA.isFighting = true;
                    critB.isFighting = true;
                    boolean fightA = critA.fight(critB.toString());
                    boolean fightB = critB.fight(critA.toString());

                    //If still alive and in the same spot, then conduct fight
                    if(critA.getEnergy()>0 && critB.getEnergy()>0 && critA.x_coord==battleX && critA.y_coord==battleY &&
                       critB.x_coord==battleX && critB.y_coord==battleY) {
                        int rollA, rollB;
                        if (fightA) {
                            rollA = getRandomInt(critA.getEnergy());
                        } else {
                            rollA = 0;
                        }
                        if (fightB) {
                            rollB = getRandomInt(critB.getEnergy());
                        } else {
                            rollB = 0;
                        }
                        if (rollA > rollB) {
                            critA.energy += critB.getEnergy() / 2;
                            critB.energy = 0;
                        } else {
                            if (rollA < rollB) {
                                critB.energy += critA.getEnergy() / 2;
                                critA.energy = 0;
                            } else {
                                critA.energy += critB.getEnergy() / 2;
                                critB.energy = 0;
                            }
                        }
                    } else if(critA.getEnergy()>0 && critB.getEnergy()>0 && critA.x_coord!=battleX &&
                            critA.y_coord!=battleY && critB.x_coord== critA.x_coord && critB.y_coord==critA.y_coord) {
                        critB.x_coord = battleX;
                        critB.y_coord = battleY;
                    }
                    critA.isFighting = false;
                    critB.isFighting = false;
                }


            }

        }


    }

    /**
     * The TestCritter class allows some critters to "cheat". If you
     * want to create tests of your Critter model, you can create
     * subclasses of this class and then use the setter functions
     * contained here.
     * <p>
     * NOTE: you must make sure that the setter functions work with
     * your implementation of Critter. That means, if you're recording
     * the positions of your critters using some sort of external grid
     * or some other data structure in addition to the x_coord and
     * y_coord functions, then you MUST update these setter functions
     * so that they correctly update your grid/data structure.
     */
    static abstract class TestCritter extends Critter {

        protected void setEnergy(int new_energy_value) {
            super.energy = new_energy_value;
        }

        protected void setX_coord(int new_x_coord) {
            super.x_coord = new_x_coord;
        }

        protected void setY_coord(int new_y_coord) {
            super.y_coord = new_y_coord;
        }

        protected int getX_coord() {
            return super.x_coord;
        }

        protected int getY_coord() {
            return super.y_coord;
        }

        /**
         * This method getPopulation has to be modified by you if you
         * are not using the population ArrayList that has been
         * provided in the starter code.  In any case, it has to be
         * implemented for grading tests to work.
         */
        protected static List<Critter> getPopulation() {
            return population;
        }

        /**
         * This method getBabies has to be modified by you if you are
         * not using the babies ArrayList that has been provided in
         * the starter code.  In any case, it has to be implemented
         * for grading tests to work.  Babies should be added to the
         * general population at either the beginning OR the end of
         * every timestep.
         */
        protected static List<Critter> getBabies() {
            return babies;
        }
    }
}
