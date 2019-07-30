package de.uni_passau.fim.sommercamp.sc2.bots._2019;

        import de.uni_passau.fim.sommercamp.sc2.bots.AbstractBot;
        import de.uni_passau.fim.sommercamp.sc2.bots.Unit;
        import de.uni_passau.fim.sommercamp.sc2.bots.util.Vec2;

        import java.util.ArrayList;
        import java.util.List;

/**
 * Empty bot for the Sommercamp SC2 interface.
 */
public class StarKIBot extends AbstractBot {

    String name;
    List<Unit> workers;
    List<Integer> enemyLocation;
    Unit myScout;

    /**
     * This constructor is called by the framework. Extend it with all necessary setup, other constructors won't work.
     */
    public StarKIBot() { name = "Empty Bot"; }

    private void attack(Unit myunit, Unit enemy) {

    }

    private Boolean foundEnemy() {
        // return getEnemyUnits().size() > 0 ? true : false;
        return false;
    }

    private List<Unit> getEnemyMedics() {
        List<Unit> medics = new ArrayList<>();
        for (Unit medic: getEnemyUnits()) {
            if (medic.canHeal() && medic.isAliveAndVisible()) {
                medics.add(medic);
            }
        }

        return medics;
    }

    private void pickScout() {
        myScout = workers.get(0);
    }

    private void scout() {
        pickScout();

        if (!foundEnemy()) {
            myScout.move(getRandomPointOnMap());
        }
    }

    /**
     * This method is called every step by the framework. The game loop consists of calling this method for every bot
     * and executing the invoked actions inside the game.
     */
    @Override
    protected void onStep() {

        // Get list of units and store list in "workers"
        // Only in the first GameLoop
        if (getGameLoop() == 1) {
            workers = getMyUnits();
        }

        if (getGameLoop() / 5 == 1) {
            scout();
        }

    }
}
