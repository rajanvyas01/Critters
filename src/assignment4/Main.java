/*
 * CRITTERS Main.java
 * EE422C Project 4 submission by
 * Rajan Vyas
 * rv23454
 * 16160
 * Slip days used: <2>
 * Fall 2020
 */

package assignment4;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Scanner;


/*
 * Usage: java <pkg name>.Main <input file> test input file is
 * optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */

public class Main {

    /* Scanner connected to keyboard input, or input file */
    static Scanner kb;

    /* Input file, used instead of keyboard input if specified */
    private static String inputFile;

    /* If test specified, holds all console output */
    static ByteArrayOutputStream testOutputString;

    /* Use it or not, as you wish! */
    private static boolean DEBUG = false;

    /* if you want to restore output to console */
    static PrintStream old = System.out;

    /* Gets the package name.  The usage assumes that Critter and its
       subclasses are all in the same package. */
    private static String myPackage; // package of Critter file.

    /* Critter cannot be in default pkg. */
    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    /**
     * Main method.
     *
     * @param args args can be empty.  If not empty, provide two
     *             parameters -- the first is a file name, and the
     *             second is test (for test output, where all output
     *             to be directed to a String), or nothing.
     */
    public static void main(String[] args) {
        if (args.length != 0) {
            try {
                inputFile = args[0];
                kb = new Scanner(new File(inputFile));
            } catch (FileNotFoundException e) {
                System.out.println("USAGE: java <pkg name>.Main OR java <pkg name>.Main <input file> <test output>");
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.out.println("USAGE: java <pkg name>.Main OR java <pkg name>.Main <input file> <test output>");
            }
            if (args.length >= 2) {
                /* If the word "test" is the second argument to java */
                if (args[1].equals("test")) {
                    /* Create a stream to hold the output */
                    testOutputString = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(testOutputString);
                    /* Save the old System.out. */
                    old = System.out;
                    /* Tell Java to use the special stream; all
                     * console output will be redirected here from
                     * now */
                    System.setOut(ps);
                }
            }
        } else { // If no arguments to main
            kb = new Scanner(System.in); // Use keyboard and console
        }
        commandInterpreter(kb);

        System.out.flush();
    }

    /* Do not alter the code above for your submission. */

    private static void commandInterpreter(Scanner kb) {

        boolean interpreter = true;
        String command = null;

        while(interpreter){
            try {
                command = kb.nextLine();
                if (command.contains("quit")) {
                    if (command.equals("quit")) {
                        interpreter = false;
                    }
                    else {
                        System.out.println("error processing: " + command);
                    }
                }

                else if (command.contains("show")) {
                    if (command.equals("show")) {
                        assignment4.Critter.displayWorld();
                    }
                    else {
                        System.out.println("error processing: " + command);
                    }
                }

                else if (command.contains("step")) {
                    String[] stepCount = command.split(" ");

                    if (command.equals("step")) {
                        assignment4.Critter.worldTimeStep();
                    }
                    else if (stepCount[0].equals("step") && stepCount.length == 2) {
                        int count = Integer.parseInt(stepCount[1]);
                        for (int i = 0; i < count; i++) {
                            assignment4.Critter.worldTimeStep();
                        }
                    }
                    else {
                        System.out.println("error processing: " + command);
                    }
                }

                else if (command.contains("seed")) {
                    String[] seedNum = command.split(" ");
                    if (seedNum.length == 2 && seedNum[0].equals("seed")) {
                        long seed = Long.parseLong(seedNum[1]);
                        assignment4.Critter.setSeed(seed);
                    }
                    else {
                        System.out.println("error processing: " + command);
                    }
                }

                else if (command.contains("create")) {
                    String[] createNameNum = command.split(" ");
                    if (createNameNum.length == 2 && createNameNum[0].equals("create")) {
                        assignment4.Critter.createCritter(createNameNum[1]);
                    }
                    else if (createNameNum.length == 3 && createNameNum[0].equals("create")) {
                        int num = Integer.parseInt(createNameNum[2]);
                        for (int i = 0; i < num; i++) {
                            assignment4.Critter.createCritter(createNameNum[1]);
                        }
                    }
                    else {
                        System.out.println("error processing: " + command);
                    }
                }

                else if (command.contains("stats")) {
                    String[] statsName = command.split(" ");
                    if (statsName.length == 2){
                            java.util.List<Critter> listStats = Critter.getInstances(statsName[1]);
                            Class critterType = Class.forName(myPackage + "." + statsName[1]);
                            Method runStats = critterType.getMethod("runStats", List.class);
                            runStats.invoke(listStats.getClass(), listStats);
                        }
                    else{
                        System.out.println("error processing: " + command);
                    }
                }

                else if (command.contains("clear")) {
                    if (command.equals("clear")) {
                        assignment4.Critter.clearWorld();
                    }
                    else {
                        System.out.println("error processing: " + command);
                    }
                }

                else {
                    System.out.println("invalid command: " + command);
                }
            }
            catch (Exception e){
                System.out.println("error processing: " + command);
            }
        }
    }
}
