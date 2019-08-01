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
            if (unit.isAliveAndVisible() && unit.getHealth() / unit.getMaxHealth() <= 0.50) {
                //Vec2 position = unit.getPosition();
                for (Unit medic : getMyMedics()) {
                    medic.queueHeal(unit);
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
        float x, y, length;
        float scale;
        diagonale = getMapSize().getB().scaled(0.5f);
        length = diagonale.getLength();
        x = diagonale.getX();
        y = diagonale.getY();
        diagonale = getMapSize().getB().normal();
        diagonale = diagonale.plus(getMapSize().getB().scaled(0.1f));

        if (!isTop()) {
            diagonale = diagonale.rotated(25, 'd');
            diagonale = diagonale.scaled(length / 3);
        } else {

            diagonale = diagonale.rotated(-50, 'd');
            diagonale = diagonale.scaled(length / 3.5f);
        }

        printDebugString("X: " + Float.toString(diagonale.getX()));
        printDebugString("Y: " + Float.toString(diagonale.getY()));
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
            myScout.move(diagonale());
        runDiagonale = true;
    }
    else

    {
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

        if (getMyMedics().size() > 0) {
            myScout.move(getMyMedics().get(0).getPosition());
        } else if (getMySoldiers().size() > 0) {
            myScout.move(getMySoldiers().get(getMySoldiers().size() - 1).getPosition());
        } else if (getMyTanks().size() > 0) {
            myScout.move(getMyTanks().get(getMyTanks().size() - 1).getPosition());
        } else if (getMyBigTanks().size() > 0) {
            myScout.move(getMyBigTanks().get(getMyBigTanks().size() - 1).getPosition());
        }

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
        printDebugString("Teamposition: (" + teamPosition.get(0) + "," + teamPosition.get(1));
        printDebugString("Scoutposition: (" + myScout.getPosition().getX() + "," + myScout.getPosition().getY());
        if (myScout.isAliveAndVisible()) {
            if (teamPosition.get(0) - myScout.getPosition().getX() < 1 && teamPosition.get(0) - myScout.getPosition().getX() > -1) {
                if (teamPosition.get(1) - myScout.getPosition().getY() < 1 && teamPosition.get(1) - myScout.getPosition().getY() > -1) {
                    printDebugString("Scout is near Team.");
                    nearTeam = true;
                }
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
                        if (!foundEnemy()) { bigTank.move(enemyLocation); }
                    }

                    for (Unit medic: getMyMedics()) {
                        if (!foundEnemy()) { medic.move(enemyLocation); }
                    }

                    unitsWaitedForMajorUnitsToMove.add(2, unitsWaitedForMajorUnitsToMove.get(2) + 1);

                } else if (unitsWaitedForMajorUnitsToMove.get(2) > 2 && unitsWaitedForMajorUnitsToMove.get(1) < 3) {
                    for (Unit Tank: getMyTanks()) {
                        if (!foundEnemy()) { Tank.move(enemyLocation); }
                    }
                    printDebugString("Tanks are moving.");
                    unitsWaitedForMajorUnitsToMove.add(1, unitsWaitedForMajorUnitsToMove.get(1) + 1);

                } else if (unitsWaitedForMajorUnitsToMove.get(1) > 2 && unitsWaitedForMajorUnitsToMove.get(0) < 200) {
                    for (Unit soldier: getMySoldiers()) {
                        if (!foundEnemy()) { soldier.move(enemyLocation); }
                    }

                    unitsWaitedForMajorUnitsToMove.add(0, unitsWaitedForMajorUnitsToMove.get(0) + 1);
                }
                break;
            /* case "backwards":
                if (!healerHealing()) {
                    if(getEnemyMedics().size() != 0 && getMyMedics().size() != 0) {

                        Vec2 OurPositionBeforeWithdrawal = getMyMedics().get(0).getPosition();
                        Vec2 EnemyPositionBeforeWithdrawal = getEnemyMedics().get(0).getPosition();
                        Vec2 DirectionOfRetreat = OurPositionBeforeWithdrawal.plus(EnemyPositionBeforeWithdrawal);

                        if (unitsWaitedForMajorUnitsToMove.get(2) < 3) {
                            for (Unit bigTank : getMyBigTanks()) {
                                bigTank.move(DirectionOfRetreat);
                            }

                            unitsWaitedForMajorUnitsToMove.add(2, unitsWaitedForMajorUnitsToMove.get(2) + 1);

                        } else if (unitsWaitedForMajorUnitsToMove.get(2) > 2 && unitsWaitedForMajorUnitsToMove.get(1) < 3) {
                            for (Unit Tank : getMyTanks()) {
                                Tank.move(DirectionOfRetreat);
                            }
                            printDebugString("Tanks are moving.");
                            unitsWaitedForMajorUnitsToMove.add(1, unitsWaitedForMajorUnitsToMove.get(1) + 1);

                        } else if (unitsWaitedForMajorUnitsToMove.get(1) > 2 && unitsWaitedForMajorUnitsToMove.get(0) < 2) {
                            for (Unit soldier : getMySoldiers()) {
                                soldier.move(DirectionOfRetreat);
                            }

                            for (Unit medic : getMyMedics()) {
                                medic.move(DirectionOfRetreat);
                            }

                            unitsWaitedForMajorUnitsToMove.add(0, unitsWaitedForMajorUnitsToMove.get(0) + 1);
                        }
                        printDebugString("Backwards !healerHealing, Units should withdraw/retreat!!!!");
                    }
                    else {
                        printDebugString("No enemy medics spotted yet / No enemy medics existent / All enemy medics succesfully eliminated");
                    }

                    printDebugString("Backwards !healerHealing, Units should withdraw/retreat");
                }
                else {
                    printDebugString("Backwards !healerHealing, Units should withdraw/retreat but somehow it's ELSE");
                }
                break; */
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

        if (myUnitX - enemyX > 5 || myUnitX - enemyX > -5) {
            if (myUnitY - enemyY > 5 || myUnitY - enemyY > -5) {
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
        boolean healerAreHealing = true;

        if (getMyMedics().size() > 0) {
            for (Unit medic: getMyMedics()) {
                if (medic.getEnergy() != medic.getMaxEnergy()) {
                    printDebugString("healerHealing is FALSE");
                    healerAreHealing = false;
                } else {
                    printDebugString("healerHealing is TRUE");
                }
            }
        }
        printDebugString("Healer are healing.");
        return healerAreHealing;
    }

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

        Boolean tanksAlive = true;
        Boolean soldiersAlive = true;
        Boolean medicsAlive = true;
        Boolean bigTanksAlive = true;

        List<Unit> myAttackers = new ArrayList<>();
        for (int i=0; i < getMyUnits().size(); i++) {
            if (getMyUnits().get(i).canAttack()) {
                myAttackers.add(getMyUnits().get(i));
            }
        }

        if (tanksAlive && getEnemyTanks().size() > 0) {
            // Tanks are alive
            for (Unit attacker : myAttackers) {
                attacker.stop();
                attacker.queueAttack(getEnemyTanks().get(0));
            }
        } else {
            tanksAlive = false;
        }

        if (!tanksAlive && getEnemySoldiers().size() > 0) {
            // Soldiers are alive
            for (Unit attacker : myAttackers) {
                attacker.stop();
                attacker.queueAttack(getEnemySoldiers().get(0));
            }
        } else {
            soldiersAlive = false;
        }

        if (!soldiersAlive && getEnemyMedics().size() > 0) {
            // Medics are still alive
            for (Unit attacker : myAttackers) {
                attacker.stop();
                attacker.queueAttack(getEnemyMedics().get(0));
            }
        } else {
            medicsAlive = false;
        }

        if (!medicsAlive && getEnemyBigTanks().size() > 0){
            for (Unit attacker : myAttackers) {
                attacker.stop();
                attacker.queueAttack(getEnemyBigTanks().get(0));
            }
        } else {
            bigTanksAlive = false;
        }

        /*
        boolean medicAlive = true;
        boolean soldierAlive = true;

        List<Unit> myAttackingUnits = new ArrayList<>();
        printDebugString("Created List myAttackingUnits.");
        for (Unit myUnit: getMyUnits()) {
            if (myUnit.canAttack()) {
                myAttackingUnits.add(myUnit);
            }
        }

        printDebugString("intellegentAttack wurde gecallt!");

        if (getEnemyTanks().size() > 0) {
            printDebugString("Tanks alive: " + Integer.toString(getEnemyTanks().size()));
            if (getEnemyTanks().size() > 0) {
                for (Unit myAttacker : myAttackingUnits) {
                    myAttacker.queueAttack(getEnemyTanks().get(0));
                }
            }
        } else {
            if (soldierAlive) {
                printDebugString("There are soldier's alive");
                if (getEnemySoldiers().size() > 1) {
                    int firstAttackersTeam = getMyUnits().size() / 2;
                    printDebugString("Team attacks enemySoldiers.");
                    printDebugString("First Team: " + Integer.toString(firstAttackersTeam));
                    for (int i = 0; i < firstAttackersTeam; i++) {
                        getMySoldiers().get(i).queueAttack(getEnemySoldiers().get(0));
                    }
                    printDebugString("Second Team: " + Integer.toString(myAttackingUnits.size() - firstAttackersTeam));
                    for (int i = firstAttackersTeam + 1; i < getMySoldiers().size(); i++) {
                        myAttackingUnits.get(i).queueAttack(getEnemySoldiers().get(1));
                    }

                } else if (getEnemySoldiers().size() == 1) {
                    for (Unit attackingUnit : myAttackingUnits) {
                        printDebugString("There should be only 1 hostile soldier left!");
                        attackingUnit.queueAttack(getEnemySoldiers().get(0));
                    }
                } else {
                    soldierAlive = false;
                }
            } else {
                if (getMySoldiers().size() > 0) {
                    for (Unit soldier : getMySoldiers()) {
                        soldier.queueAttack(getEnemySoldiers().get(0));
                    }
                }
            }

            if (!soldierAlive) {
                if (medicAlive) {
                    if (getEnemyMedics().size() > 0) {
                        for (Unit attackingUnit : myAttackingUnits) {
                            attackingUnit.queueAttack(getEnemyMedics().get(0));
                        }
                    } else {
                        printDebugString("All hostile medics are dead!");
                        medicAlive = false;
                    }
                } else {
                    if (getEnemyTanks().size() > 0) {
                        // Test
                        for (int i = 0; i < getMyTanks().size(); i++) {
                            getMyTanks().get(0).stop();
                        }
                        // Attack normal tanks
                        for (Unit myUnit : getMyUnits()) {
                            myUnit.queueAttack(getEnemyTanks().get(0));
                        }
                    } else {
                        // Attack bigTanks
                        for (Unit myUnit : getMyBigTanks()) {
                            myUnit.queueAttack(getEnemyBigTanks().get(0));
                        }
                    }
                }
            }
        }
        */
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

            unitsWaitedForMajorUnitsToMove = new ArrayList();
            for (int i=0; i < 3; i++) {
                unitsWaitedForMajorUnitsToMove.add(i, 0);
            }
        }

         /* boolean sfoi = healerHealing();
         if (!healerHealing()) {
            moveTeam("backwards");
        } */

        mapDetermination();

        if (getMyUnits().size() > 0) {
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
            printDebugString("Enemy is at: " + enemyLocation.getX() + "," + enemyLocation.getY());

                if (teamNextToEnemy()) {
                    printDebugString("Team is next to Enemy!");
                    intellegentAttack();
                }
            }
        }

        checkHP();
    }
}
