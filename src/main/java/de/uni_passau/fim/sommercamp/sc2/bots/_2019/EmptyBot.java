package de.uni_passau.fim.sommercamp.sc2.bots._2019;

import SC2APIProtocol.Common;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import de.uni_passau.fim.sommercamp.sc2.bots.AbstractBot;
import de.uni_passau.fim.sommercamp.sc2.bots.Unit;
import de.uni_passau.fim.sommercamp.sc2.bots.util.Vec2;

/**
 * Empty bot for the Sommercamp SC2 interface.
 */
public class EmptyBot extends AbstractBot {

    String name;
    Unit worker;

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
    protected void onStep() {
        printDebugString(name + " is doing nothing yet, program something!");


        if (worker == null || !worker.isAliveAndVisible()) {
            worker = getMyUnits().get(1);
        }

        for (Unit unit: getEnemyUnits()) {
            worker.attack(unit);
            break;
        }

        if (worker.getOrders().isEmpty()) {
            worker.move(ran);
        }

    }
}
