package de.uni_passau.fim.sommercamp.sc2.bots;

import com.github.ocraft.s2client.api.S2Client;
import de.uni_passau.fim.sommercamp.sc2.BaseBot;

/**
 * Empty bot for the Sommercamp SC2 interface.
 */
public class EmptyBot extends BaseBot {

    /* Example
     private String name;
    //*/

    /**
     * This constructor is called by the framework. Extend it with all necessary setup, other constructors won't work.
     *
     * @param client The connection to the game.
     */
    public EmptyBot(S2Client client) {
        super(client);

        /* Example
         this.name = "FooBot";
        //*/
    }

    /**
     * This method is call every step by the framework. The game loop consists of calling this method for every bot and
     * executing the invoked actions inside the game.
     */
    @Override
    protected void onStep() {
        /* Example
        for (BotUnit u: getObservation().getDiedInLastStep()) {
            if (u.isMine()) {
                System.out.println("Oops, not so good :(");
            } else {
                System.out.println("Yeah, one less!");
            }
        }

        BotUnit worker = getObservation().getUnits().get(0);
        if (worker.isAliveAndVisible()) {
            return;
        }

        for (BotUnit botUnit: getObservation().getUnits()) {
            if (botUnit.isEnemy()) {
                worker.attack(botUnit);
                break;
            }
        }

        if (worker.getOrders().isEmpty()) {
            worker.move(Point2d.of(getInfo().getMapData().getMapSize().getX(),1));
        }
        //*/
    }
}
