package de.uni_passau.fim.sommercamp.sc2.bots._2019;

        import com.github.ocraft.s2client.protocol.data.Units;
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

    private List<Unit> getMyMedics() {
        List<Unit> medics = new ArrayList<>();
        for (Unit medic: getMyUnits()) {
            if (medic.canHeal() && medic.isAliveAndVisible()) {
                medics.add(medic);
            }
        }

        return medics;
    }

    private List<Unit> getMyTanks(){
        List<Unit> tanks = new ArrayList<>();
        for (Unit tank: getMyUnits()) {

            if(tank.getType() == Units.TERRAN_FIREBAT && tank.isAliveAndVisible()){
                tanks.add(tank);
        }
        }
        return tanks;
    }

    private List<Unit> getMyBigTanks(){
        List<Unit> bigTanks = new ArrayList<>();
        for (Unit tank: getMyUnits()) {

            if(tank.getType() == Units.TERRAN_MARAUDER &&  tank.isAliveAndVisible()){
                bigTanks.add(tank);
            }
        }
        return bigTanks;
    }

    private List<Unit> getMySoldiers(){
        List<Unit> soldiers = new ArrayList<>();
        for (Unit soldier: getMyUnits()) {

            if(soldier.getType() == Units.TERRAN_MARINE && soldier.isAliveAndVisible()){
                soldiers.add(soldier);
            }
        }
        return soldiers;
    }

    private void checkHP(){
        for(Unit unit : getMyUnits()){
            if(unit.isAliveAndVisible() && unit.getHealth()/unit.getMaxHealth() <= 0.50)
            {
                if(getMyMedics().size() > 0)
                {
                 Vec2 position = getMyMedics().get(0).getPosition();
                 unit.move(position);
                }
            }
        }
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

        if (getGameLoop() / 20 == 1) {
            scout();
        }
        checkHP();

    }
}
