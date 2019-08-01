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
    boolean runDiagonale;
    String queuedTarget = "enemyTanks";
    List<Float> teamPosition = new ArrayList();


    /**
     * This constructor is called by the framework. Extend it with all necessary setup, other constructors won't work.
     */
    public StarKIBot() {
        name = "Empty Bot";
        runDiagonale = false;
    }

    /**
     * Get Unit methods
     */

    private List<Unit> getEnemyMedics() {
        List<Unit> medics = new ArrayList<>();
        for (Unit medic : getEnemyUnits()) {
            if (medic.canHeal() && medic.isAliveAndVisible()) {
                medics.add(medic);
            }
        }

        return medics;
    }

    private List<Unit> getEnemySoldiers() {
        List<Unit> soldiers = new ArrayList<>();
        for (Unit enemyUnit : getEnemyUnits()) {
            if (enemyUnit.getType().equals(Units.TERRAN_MARINE) && enemyUnit.isAliveAndVisible()) {
                soldiers.add(enemyUnit);
            }
        }

        return soldiers;
    }

    private List<Unit> getEnemyTanks() {
        List<Unit> tanks = new ArrayList<>();
        for (Unit enemyUnit : getEnemyUnits()) {
            if (enemyUnit.getType().equals(Units.TERRAN_FIREBAT) && enemyUnit.isAliveAndVisible()) {
                tanks.add(enemyUnit);
            }
        }

        return tanks;
    }

    private List<Unit> getEnemyBigTanks() {
        List<Unit> bigTanks = new ArrayList<>();
        for (Unit enemyUnit : getEnemyUnits()) {
            if (enemyUnit.getType().equals(Units.TERRAN_MARAUDER) && enemyUnit.isAliveAndVisible()) {
                bigTanks.add(enemyUnit);
            }
        }

        return bigTanks;
    }

    private List<Unit> getMyMedics() {
        List<Unit> medics = new ArrayList<>();
        for (Unit medic : getMyUnits()) {
            if (medic.canHeal() && medic.isAliveAndVisible()) {
                medics.add(medic);
            }
        }

        return medics;
    }

    private List<Unit> getMyTanks() {
        List<Unit> tanks = new ArrayList<>();
        for (Unit tank : getMyUnits()) {

            if (tank.getType() == Units.TERRAN_FIREBAT && tank.isAliveAndVisible()) {
                tanks.add(tank);
            }
        }
        return tanks;
    }

    private List<Unit> getMyBigTanks() {
        List<Unit> bigTanks = new ArrayList<>();
        for (Unit tank : getMyUnits()) {

            if (tank.getType() == Units.TERRAN_MARAUDER && tank.isAliveAndVisible()) {
                bigTanks.add(tank);
            }
        }
        return bigTanks;
    }

    private List<Unit> getMySoldiers() {
        List<Unit> soldiers = new ArrayList<>();
        for (Unit soldier : getMyUnits()) {

            if (soldier.getType() == Units.TERRAN_MARINE && soldier.isAliveAndVisible()) {
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
    private void checkHP() {
        for (Unit unit : getMyUnits()) {
            if (unit.isAliveAndVisible() && unit.getHealth() / unit.getMaxHealth() <= 0.70) {
                //Vec2 position = unit.getPosition();
                for (Unit medic : getMyMedics()) {


                    medic.queueHeal(unit);
                }
            }
            else{

                List<Unit> medics = new ArrayList<>();
                for (Unit medic: getMyMedics()){
                    int i = 0;
                    medics.add(i,medic);
                    i++;
                }
                if(getMyBigTanks().size()>0)
                {
                    for(Unit follower: getMyBigTanks()){
                        if(!follower.getType().equals(myScout)){

                            medics.get(0).move(follower.getPosition().scaled(0.96f));
                            medics.get(1).move(follower.getPosition().scaled(0.93f));
                        }
                    }
                }
                else if(getMyTanks().size()>0)
                {
                    for(Unit follower: getMyTanks()){
                        if(!follower.getType().equals(myScout)){
                            medics.get(0).move(follower.getPosition().scaled(0.96f));
                            medics.get(1).move(follower.getPosition().scaled(0.93f));
                        }
                    }
                }
                else if(getMySoldiers().size()>0)
                {
                    for(Unit follower: getMySoldiers()){
                        if(!follower.getType().equals(myScout)){
                            medics.get(0).move(follower.getPosition().scaled(0.96f));
                            medics.get(1).move(follower.getPosition().scaled(0.93f));
                        }
                    }
                }
                else{
                    medics.get(0).move(getRandomPointOnMap());
                    medics.get(1).move(getRandomPointOnMap());
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
            if (getMySoldiers().size() > 0) {
                myScout = getMySoldiers().get(0);
            } else {
                myScout = getMyMedics().get(0);
            }
        }
    }

    // Generates Map-Diagonale
    private Vec2 diagonale() {
        Vec2 diagonale;
        float length;
        diagonale = getMapSize().getB().scaled(0.5f);
        length = diagonale.getLength();
        diagonale = getMapSize().getB().normal();
        diagonale = diagonale.plus(getMapSize().getB().scaled(0.1f));

        if (!isTop()) {
            diagonale = diagonale.rotated(25, 'd');
            diagonale = diagonale.scaled(length / 3);
        } else {

            diagonale = diagonale.rotated(-50, 'd');
            diagonale = diagonale.scaled(length / 3.5f);
        }

        printDebugString("X: " + diagonale.getX());
        printDebugString("Y: " + diagonale.getY());
        printDebugString("Vector has been found");
        return diagonale;
    }

    private boolean isTop() {
        boolean result;
        Unit unit = getMyUnits().get(0);
        if (unit.getPosition().getY() < 10) {
            result = false;
            printDebugString("I am bottom!");
        } else {
            result = true;
            printDebugString("I am top!");
        }
        return result;
    }

    // Scout moving around
    private void scout() {
        printDebugString("Is running");
        if (!runDiagonale){
            Vec2 diagonale = diagonale();
            myScout.move(diagonale);
            if (diagonale == myScout.getPosition()) {
                runDiagonale = true;
            }
        } else {
            myScout.move(getRandomPointOnMap());
        }
        //myScout.move(getRandomPointOnMap());
        scouting = true;
    }

    private void returnScoutToTeam() {
        printDebugString("Scout returns to Team.");

        // Clear order queue of scout
        myScout.stop();

        printDebugString("Cleared orders of Scout.");

         if (getMySoldiers().size() > 0) {
            myScout.move(getMySoldiers().get(getMySoldiers().size() - 1).getPosition());
        } else if (getMyTanks().size() > 0) {
            myScout.move(getMyTanks().get(getMyTanks().size() - 1).getPosition());
        } else if (getMyBigTanks().size() > 0) {
            myScout.move(getMyBigTanks().get(getMyBigTanks().size() - 1).getPosition());
        } else if (getMyMedics().size() > 0) {
             myScout.move(getMyMedics().get(0).getPosition());
         }

        scoutNextToTeam = false;
    }

    private boolean scoutNearTeam() {
        scoutNextToTeam = false;

        if (getMySoldiers().size() > 0) {
            teamPosition.add(0, getMySoldiers().get(0).getPosition().getX());
            teamPosition.add(1, getMySoldiers().get(0).getPosition().getY());
        } else if (getMyTanks().size() > 0) {
            teamPosition.add(0, getMyTanks().get(0).getPosition().getX());
            teamPosition.add(1, getMyTanks().get(0).getPosition().getY());
        } else if (getMyBigTanks().size() > 0) {
            teamPosition.add(0, getMyBigTanks().get(0).getPosition().getX());
            teamPosition.add(1, getMyBigTanks().get(0).getPosition().getY());
        } else if (getMyMedics().size() > 0) {
            teamPosition.add(0, getMyMedics().get(0).getPosition().getX());
            teamPosition.add(1, getMyMedics().get(0).getPosition().getY());
        }

        printDebugString("Teamposition: (" + teamPosition.get(0) + "," + teamPosition.get(1));
        printDebugString("Scoutposition: (" + myScout.getPosition().getX() + "," + myScout.getPosition().getY());
        if (myScout.isAliveAndVisible()) {
            if (teamPosition.get(0) - myScout.getPosition().getX() < 1.5 && teamPosition.get(0) - myScout.getPosition().getX() > -1.5) {
                if (teamPosition.get(1) - myScout.getPosition().getY() < 1.5 && teamPosition.get(1) - myScout.getPosition().getY() > -1.5) {
                    printDebugString("Scout is near Team.");
                    scoutNextToTeam = true;
                }
            }
        }

        return scoutNextToTeam;
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
                printDebugString("moveTeam() wurde gecallt.");
                if (unitsWaitedForMajorUnitsToMove.get(0) == 0) {
                    for (Unit myUnit : getMyUnits()) {
                        if (myUnit.getType().equals(Units.TERRAN_MEDIC) || myUnit.getType().equals(Units.TERRAN_MARAUDER)) {
                            if (!myUnit.equals(myScout)) {
                                myUnit.move(enemyLocation);
                            }

                            if (myUnit.getType().equals(Units.TERRAN_MEDIC)) {
                                printDebugString("I am a meidc!");
                            }
                        }
                    }
                    printDebugString("Medics and Marauder start moving.");
                    unitsWaitedForMajorUnitsToMove.add(0, unitsWaitedForMajorUnitsToMove.get(0) + 1);
                }

                if (unitsWaitedForMajorUnitsToMove.get(1) < 2 && unitsWaitedForMajorUnitsToMove.get(0) > 0) {
                    for (Unit myUnit : getMyUnits()) {
                        if (myUnit.getType().equals(Units.TERRAN_FIREBAT)) {
                            if (!myUnit.equals(myScout)) {
                                myUnit.move(enemyLocation);
                            }
                        }
                    }
                    printDebugString("Firebat starts moving.");
                    unitsWaitedForMajorUnitsToMove.add(1, unitsWaitedForMajorUnitsToMove.get(1) + 1);
                }

                if (unitsWaitedForMajorUnitsToMove.get(1) == 1) {
                    for (Unit myUnit : getMyUnits()) {
                        if (myUnit.getType().equals(Units.TERRAN_MARINE)) {
                            if (!myUnit.equals(myScout)) {
                                myUnit.move(enemyLocation);
                            }
                        }
                    }
                }

                if (scoutNearTeam()) {
                    printDebugString("Scout ist near team.");
                    myScout.move(enemyLocation);
                }

                break;
            }
        }

    private boolean teamNextToEnemy() {
        float myUnitX = 0;
        float myUnitY = 0;
        float enemyX = enemyLocation.getX();
        float enemyY = enemyLocation.getY();

        if(getMyMedics().size() > 0) {
            myUnitX = getMyMedics().get(0).getPosition().getX();
            myUnitY = getMyMedics().get(0).getPosition().getY();;
        } else if (getMySoldiers().size() > 0) {
            myUnitX = getMySoldiers().get(getMySoldiers().size() - 1).getPosition().getX();
            myUnitY = getMySoldiers().get(getMySoldiers().size() - 1).getPosition().getY();
        }

        if (myUnitX - enemyX > 2.5 || myUnitX - enemyX > -2.5) {
            if (myUnitY - enemyY > 2.5 || myUnitY - enemyY > -2.5) {
                return true;
            }
        }

        return false;
    }

    /* */

    /**
     * Healer methods
     */

    /* */

    /**
     * Attack methods
     */

    private void mapDetermination(){

        if (getMyUnits().size() == getMySoldiers().size()) {
            marinesAttack();
            printDebugString("This fight takes place on a Marines-Map!");
        }
        else {
            intellegentAttack();
            printDebugString("This fight doesn't take place on a Marines-Map!");
        }
    }

    private void marinesAttack() {
        printDebugString("This is marinesAttack!");

        List<Unit> marines = new ArrayList<>();

        for (int i = 0; i < getMySoldiers().size(); i++) {
            marines.add(getMySoldiers().get(i));
        }

        for (Unit attacker: marines) {
            if (getEnemySoldiers().size() > 0) {
                attacker.queueAttack(getEnemySoldiers().get(0));
                printDebugString("Attacking enemySoldier_0 on Marines-Map!");
            } else {
                printDebugString("All enemy marines are either not yet discovered or dead!");
            }
        }
    }

    private void intellegentAttack() {
        if (getEnemyTanks().size() > 0) {
            printDebugString("Now attacking their tanks.");
            for (Unit attacker: getMyUnits()) {
                if (!queuedTarget.equals("enemyTanks")) {
                    attacker.stop();
                }
                attacker.queueAttack(getEnemyTanks().get(0));
                printDebugString("Attacking enemy TANKS!");
            }
            queuedTarget = "enemyTanks";
        } else if (getEnemySoldiers().size() > 0) {
            for (Unit attacker: getMyUnits()) {
                if (getEnemyTanks().size() == 0 && !queuedTarget.equals("enemySoldiers")) {
                    attacker.stop();
                }
                attacker.queueAttack(getEnemySoldiers().get(0));
                printDebugString("Attacking enemy SOLDIERS!");
            }
            queuedTarget = "enemySoldiers";
        } else if (getEnemyMedics().size() > 0) {
            for (Unit attacker: getMyUnits()) {
                if (getEnemySoldiers().size() == 0 && !queuedTarget.equals("enemyMedics")) {
                    attacker.stop();
                }
                attacker.queueAttack((getEnemyMedics().get(0)));
                printDebugString("Attacking enemy MEDICS!");
            }
            queuedTarget = "enemyMedics";
        } else if (getEnemyBigTanks().size() > 0) {
            for (Unit attacker: getMyUnits()) {
                attacker.queueAttack(getEnemyBigTanks().get(0));
                printDebugString("Attacking enemy BIGTANKS!");
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
        pickScout();
        // Get list of units and store list in "workers"
        // Only in the first GameLoop
        printDebugString("onStep triggered");
        if (getGameLoop() == 1) {
            workers = getMyUnits();
            printDebugString("Gameloop 1 found!");
            scoutNextToTeam = true;

            unitsWaitedForMajorUnitsToMove = new ArrayList<>();
            for (int i=0; i < 4; i++) {
                unitsWaitedForMajorUnitsToMove.add(i, 0);
            }
        }

        if (getMyUnits().size() > 0) {
            if (!foundEnemy() && scoutNextToTeam) {
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

                moveTeam("towardsEnemy");

                printDebugString("Enemy is at: " + enemyLocation.getX() + "," + enemyLocation.getY());

                if (teamNextToEnemy()) {
                    printDebugString("Team is next to Enemy!");
                    intellegentAttack();
                    mapDetermination();
                }
            }
        }

        checkHP();
    }
}
