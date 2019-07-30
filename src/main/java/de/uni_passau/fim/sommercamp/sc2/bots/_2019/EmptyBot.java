package de.uni_passau.fim.sommercamp.sc2.bots._2019;

import de.uni_passau.fim.sommercamp.sc2.bots.AbstractBot;
import de.uni_passau.fim.sommercamp.sc2.bots.Unit;
import de.uni_passau.fim.sommercamp.sc2.bots.util.Vec2;

/**
 * Empty bot for the Sommercamp SC2 interface.
 */
public class EmptyBot extends AbstractBot {

    String name;
    Vec2 position;
    Unit[] units;
    int anzahl;
    boolean escape,enemySpotted, noMedic;

    /**
     * This constructor is called by the framework. Extend it with all necessary setup, other constructors won't work.
     */
    public EmptyBot() {
        name = "Empty Bot";
    }

    /**
     * This method is called every step by the framework. The game loop consists of calling this method for every bot
     * and executing the invoked actions inside the game.
     */
    @Override
    /*
    Idea: Some Units are  scouting, number is based on how many Units are available. When an enemy unit has been spotted => Sent current Location
    Attacker are going to this spot and attack enemy units.
    If attackers HP drops low => Try to escape from fight and try to reach a medic

     */
    protected void onStep() {

    }

    public Vec2 sendPosition(Unit worker)
    {
        position = worker.getPosition();
        return position;
    }

}
