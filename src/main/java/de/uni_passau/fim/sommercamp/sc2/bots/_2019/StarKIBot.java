package de.uni_passau.fim.sommercamp.sc2.bots._2019;

        import com.github.ocraft.s2client.protocol.data.Units;
        import de.uni_passau.fim.sommercamp.sc2.bots.AbstractBot;
        import de.uni_passau.fim.sommercamp.sc2.bots.Unit;
        import de.uni_passau.fim.sommercamp.sc2.bots.util.Vec2;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.PrimitiveIterator;

/**
 * Empty bot for the Sommercamp SC2 interface.
 */
public class StarKIBot extends AbstractBot {

    String name;
    List<Unit> workers;
    Vec2 enemyLocation;
    Unit myScout;
    Boolean scouting;
    Boolean scoutNextToTeam;
    List<Integer> unitsWaitedForMajorUnitsToMove;

    /**
     * This constructor is called by the framework. Extend it with all necessary setup, other constructors won't work.
     */
    public StarKIBot() { name = "Empty Bot"; }

    /**
     * Get Unit methods
    */

    private List<Unit> getEnemyMedics() {
        List<Unit> medics = new ArrayList<>();
        for (Unit medic: getEnemyUnits()) {
            if (medic.canHeal() && medic.isAliveAndVisible()) {
                medics.add(medic);
            }
        }

        return medics;
    }

    private List<Unit> getEnemySoldiers() {
        List<Unit> soldiers = new ArrayList<>();
        for (Unit enemyUnit: getEnemyUnits()) {
            if (enemyUnit.getType().equals(Units.TERRAN_MARINE) && enemyUnit.isAliveAndVisible()) {
                soldiers.add(enemyUnit);
            }
        }

        return soldiers;
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

    // Returns whether an enemy has been seen or not
    private Boolean foundEnemy() {
        return getEnemyUnits().size() > 0 ? true : false;
    }

    /**
     * Scout methods
     */

    // When a Unit`s HP drops below 50%, the unit asks a medic for its position and move towards it
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

    // Picks one unit to go scouting
    private void pickScout() {
        // Check if at least one bigTank is alive and pick it as a scout
        // Else if pick a normal tank
        // Else if pick a marine
        if (getMyBigTanks().size() > 0) {
            myScout = getMyBigTanks().get(0);
        } else if (getMyTanks().size() > 0) {
            myScout = getMyTanks().get(0);
        } else {
            myScout = getMySoldiers().get(0);
        }
    }

    // Generates Map-Diagonale
    private Vec2 diagonale()
    {
        Vec2 diagonale;
        float x,y, length;
        float scale;
        diagonale = getMapSize().getB().scaled(0.5f);
        length = diagonale.getLength();
        x = diagonale.getX();
        y = diagonale.getY();
        diagonale = getMapSize().getB().normal();
        diagonale = diagonale.plus(getMapSize().getB().scaled(0.1f));

       if(!isTop()) {
           diagonale = diagonale.rotated(25,'d');
           diagonale = diagonale.scaled(length/3);
        }
       else{

           diagonale = diagonale.rotated(-55,'d');
           diagonale = diagonale.scaled(length/3f);
       }

        printDebugString("X: "+Float.toString(diagonale.getX()));
        printDebugString("Y: "+Float.toString(diagonale.getY()));
        printDebugString("Vector has been found");
        return diagonale;
    }

    private boolean isTop(){
        boolean result;
        Unit unit = getMyUnits().get(0);
        if(unit.getPosition().getY() <10){
            result = false;
            printDebugString("I am bottom!");
        }
        else
        {
            result = true;
            printDebugString("I am top!");
        }
        return result;
    }

    /*
    *   Scout moving around
    * */

    // Scout moving around
    private void scout() {

        printDebugString("Is running");
        pickScout();
        myScout.move(diagonale());
        //myScout.move(getRandomPointOnMap());
        scouting = true;
    }

    private void returnScoutToTeam() {
        printDebugString("Scout returns to Team.");

        // Clear order queue of scout
        myScout.stop();

        printDebugString("Cleared orders of Scout.");

        myScout.move(getMyMedics().get(0).getPosition());

        scoutNextToTeam = false;
    }

    private boolean scoutNearTeam() {
        Boolean nearTeam = false;

        List<Float> teamPosition = new ArrayList();
        if (getMyMedics().size() > 0) {
            teamPosition.add(0, getMyMedics().get(0).getPosition().getX());
            teamPosition.add(1, getMyMedics().get(0).getPosition().getY());
        } else if (getMySoldiers().size() > 0) {
            teamPosition.add(0, getMySoldiers().get(0).getPosition().getX());
            teamPosition.add(1, getMySoldiers().get(0).getPosition().getY());
        } else if (getMyTanks().size() > 0) {
            teamPosition.add(0, getMyTanks().get(0).getPosition().getX());
            teamPosition.add(1, getMyTanks().get(0).getPosition().getY());
        } else if (getMyBigTanks().size() > 0) {
            teamPosition.add(0, getMyBigTanks().get(0).getPosition().getX());
            teamPosition.add(1, getMyBigTanks().get(0).getPosition().getY());
        }

        if (teamPosition.get(0) - myScout.getPosition().getX() < 2.5 || teamPosition.get(0) - myScout.getPosition().getX() > -2.5) {
            if (teamPosition.get(1) - myScout.getPosition().getY() < 2.5 || teamPosition.get(1) - myScout.getPosition().getY() > -2.5) {
                printDebugString("Scout is near Team.");
                return true;
            }
        }

        return nearTeam;
    }

    /* */

    /**
     * Team methods
     */
    private void moveTeam(String mode) {
        moveTeam(mode, Vec2.of(0,0));
    }

    private void moveTeam(String mode, Vec2 target) {
        switch (mode) {
            case "towardsEnemy":
                // Check if BigTanks are already moving towards enemy
                if (unitsWaitedForMajorUnitsToMove.get(2) < 3) {
                    for (Unit bigTank: getMyBigTanks()) {
                        bigTank.move(enemyLocation);
                    }

                    unitsWaitedForMajorUnitsToMove.add(2, unitsWaitedForMajorUnitsToMove.get(2) + 1);

                } else if (unitsWaitedForMajorUnitsToMove.get(2) > 2 && unitsWaitedForMajorUnitsToMove.get(1) < 3) {
                    for (Unit Tank: getMyTanks()) {
                        Tank.move(enemyLocation);
                    }
                    printDebugString("Tanks are moving.");
                    unitsWaitedForMajorUnitsToMove.add(1, unitsWaitedForMajorUnitsToMove.get(1) + 1);

                } else if (unitsWaitedForMajorUnitsToMove.get(1) > 2 && unitsWaitedForMajorUnitsToMove.get(0) < 2) {
                    for (Unit soldier: getMySoldiers()) {
                        soldier.move(enemyLocation);
                    }

                    for (Unit medic: getMyMedics()) {
                        medic.move(enemyLocation);
                    }

                    unitsWaitedForMajorUnitsToMove.add(0, unitsWaitedForMajorUnitsToMove.get(0) + 1);
                }
                break;
            case "backwards":
                if (!healerHealing()) {
                }
                break;
        }
    }

    private boolean teamNextToEnemy() {
        float medicX = getMyMedics().get(0).getPosition().getX();
        float medicY = getMyMedics().get(0).getPosition().getY();
        float enemyX = enemyLocation.getX();
        float enemyY = enemyLocation.getY();

        if (medicX - enemyX > 5 || medicX - enemyX > -5) {
            if (medicY - enemyY > 5 || medicY - enemyY > -5) {
                return true;
            }
        }

        return false;
    }

    /* */

    /**
     * Healer methods
     */

    private boolean healerHealing() {
        return true;
    }

    /* */

    /**
     * Attack methods
     */

    private void intellegentAttack() {
        boolean medicAlive = true;
        boolean soldierAlive = true;

        if (getMyBigTanks().size() > 2) {
            if (getEnemyMedics().get(0).isAliveAndVisible()) {
                for (Unit bigTank: getMyBigTanks()) {
                    printDebugString("Bigtanks attack medics.");
                    bigTank.attack(getEnemyMedics().get(0));
                }
            } else {
                medicAlive = false;
            }
        }

        if (!medicAlive) {
            List<Unit> myAttackingUnits = new ArrayList<>();

            for (Unit myUnit: getMyUnits()) {
                if (myUnit.isAliveAndVisible() && myUnit.canAttack()) {
                    myAttackingUnits.add(myUnit);
                }
            }

            if (soldierAlive) {
                if (myAttackingUnits.size() >= 4) {
                    if (getEnemySoldiers().size() > 1) {
                        int firstAttackersTeam = myAttackingUnits.size() / 2;
                        printDebugString("Team attacks enemySoldiers.");
                        for (int i=0; i < firstAttackersTeam; i++) {
                            myAttackingUnits.get(i).queueAttack(getEnemySoldiers().get(0));
                        }
                        for (int i=firstAttackersTeam + 1; i < myAttackingUnits.size(); i++) {
                            myAttackingUnits.get(i).queueAttack(getEnemySoldiers().get(1));
                        }
                    } else {
                        for (Unit attackingUnit: myAttackingUnits) {
                            attackingUnit.queueAttack(getEnemySoldiers().get(0));
                        }
                    }
                }
            }
        }
    }

    /* */

    /**
     * This method is called every step by the framework. The game loop consists of calling this method for every bot
     * and executing the invoked actions inside the game.
     */
    @Override
    protected void onStep() {

        // Get list of units and store list in "workers"
        // Only in the first GameLoop
        printDebugString("onStep triggered");
        if (getGameLoop() == 1) {
            workers = getMyUnits();
            printDebugString("Gameloop 1 found!");
            scoutNextToTeam = true;

            unitsWaitedForMajorUnitsToMove = new ArrayList();
            for (int i=0; i < 3; i++) {
                unitsWaitedForMajorUnitsToMove.add(i, 0);
            }
        }

        if (!foundEnemy() && scoutNextToTeam == true) {
            printDebugString("No enemy found!");
            if (getGameLoop() % 100 == 1) {
                scout();
            }
        } else {
            if (scouting) {
                scouting = false;

                // Push enemyCoordinates
                enemyLocation = myScout.getPosition();

                returnScoutToTeam();
            }

            if (scoutNearTeam() && !teamNextToEnemy()) {
                printDebugString("Scout is back Home!");

                moveTeam("towardsEnemy");
            }

            printDebugString("Medic is at: " + getMyMedics().get(0).getPosition().getX() + "," + getMyMedics().get(0).getPosition().getY());
            printDebugString("Enemy is at: " + enemyLocation.getX() + "," + enemyLocation.getY());

            if (teamNextToEnemy()) {
                printDebugString("Team is next to Enemy!");
                intellegentAttack();
            }
        }
        checkHP();

        Boolean egal = scoutNearTeam();
    }
}
