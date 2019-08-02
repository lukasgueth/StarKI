package de.uni_passau.fim.sommercamp.sc2.bots._2019;

import com.github.ocraft.s2client.protocol.data.Units;
import de.uni_passau.fim.sommercamp.sc2.bots.AbstractBot;
import de.uni_passau.fim.sommercamp.sc2.bots.Unit;
import de.uni_passau.fim.sommercamp.sc2.bots.util.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * MasterOfZerstoerung for the Sommercamp SC2 interface by Claudia, Lukas, Tim
 * Version 1.4
 */

public class MasterOfZerstoerung extends AbstractBot {

    private enum Map {
        MARINES_2V2, OTHER
    }

    private int stepCount = 0;
    private Map currentMap;

    /**
     * This constructor is called by the framework. Extend it with all necessary setup, other constructors won't work.
     */
    public MasterOfZerstoerung() {
    }

    /**
     * This method is called every step by the framework. The game loop consists of calling this method for every bot
     * and executing the invoked actions inside the game.
     */
    @Override
    protected void onStep() {

        if (stepCount == 0) {
            if (getMyUnits().size() == 2 && getMyUnits().get(0).getType() == Units.TERRAN_MARINE && getMyUnits().get(1).getType() == Units.TERRAN_MARINE)
                currentMap = Map.MARINES_2V2;
            else
                currentMap = Map.OTHER;
        }

        switch (currentMap) {

            case OTHER:
                update();
                break;

            case MARINES_2V2:
                if (getEnemyUnits().size() == 0) {
                    moveAllToRandomPos();
                } else {
                    Unit toAttack = getNearestUnitFromPoint(getEnemyUnits(), getCenter(getMyUnits()));
                    for (Unit u : getMyUnits())
                        u.attack(toAttack);
                }
                break;
        }

        ++stepCount;
    }

    private void update() {

        // No enemy found
        if (getEnemyUnits().size() == 0) {
            this.moveAllToRandomPos();
        }
        // Enemy found
        else {
            Vec2 myUnitsCenter = getCenter(getMyUnits());

            Unit toAttack = getNearestUnitFromPoint(getEnemyUnits(), myUnitsCenter);

            Unit mostDamaged = null;
            float minHealthPercentage = 0.8f;
            for (Unit myUnit : getMyUnits()) {
                if (minHealthPercentage > (myUnit.getHealth() / myUnit.getMaxHealth())) {
                    minHealthPercentage = (myUnit.getHealth() / myUnit.getMaxHealth());
                    mostDamaged = myUnit;
                }
            }

            float range;
            for (Unit u : getMyUnits()) {

                switch ((Units) u.getType()) {

                    case TERRAN_MEDIC:
                        // able to heal
                        if (mostDamaged != null)
                            u.heal(mostDamaged);
                        break;

                    case TERRAN_MARAUDER:
                    case TERRAN_FIREBAT:
                        tryToActOnPreferred(u, toAttack, myUnitsCenter, Unit::attack);
                        break;

                    case TERRAN_MARINE:
                        range = 1 + u.getWeapons().iterator().next().getRange();
                        controlAttackingUnit(u, toAttack, range);
                        break;

                    default:
                        printDebugString("Unknown unit! - simply attacking");
                        u.attack(toAttack);
                        break;
                }
            }
        }
    }

    private void moveAllToRandomPos() {
        boolean moving = false;
        for (Unit u : getMyUnits()) {
            if (!u.isIdle()) {
                moving = true;
            }
        }
        if (!moving) {
            Vec2 pos = getRandomPointOnMap();
            for (Unit u : getMyUnits()) {
                u.move(pos);
            }
        }
    }

    private static Vec2 getCenter(List<Unit> units) {
        Vec2 tmp = new Vec2(0, 0);
        int i = 0;
        for (; i < units.size(); ++i) {
            tmp = tmp.plus(units.get(i).getPosition());
        }
        return new Vec2(tmp.getX() / i, tmp.getY() / i);
    }

    private static Unit getNearestUnitFromPoint(List<Unit> units, Vec2 point) {

        if (units.size() == 1)
            return units.get(0);

        float minDist = 10000;
        Unit minDistUnit = null;
        for (Unit enemyUnit : units) {
            float currentDist = enemyUnit.getPosition().minus(point).getLength();
            if (currentDist < minDist) {
                minDistUnit = enemyUnit;
                minDist = currentDist;
            }
        }
        return minDistUnit;
    }

    private Vec2 movePointIntoMap(Vec2 point) {
        if (!isPointOnMap(point)) {
            float new_x = Math.max(point.getX(), getMapSize().getA().getX());
            new_x = Math.min(new_x, getMapSize().getB().getX());
            float new_y = Math.max(point.getY(), getMapSize().getA().getY());
            new_y = Math.min(new_y, getMapSize().getB().getY());
            return Vec2.of(new_x, new_y);
        } else {
            return point;
        }
    }

    private void controlAttackingUnit(Unit attackingUnit, Unit attackedUnit, float range) {
        if (attackingUnit.getWeaponCooldown() > 0) {
            Vec2 away = attackingUnit.getPosition().minus(getEnemyUnits().get(0).getPosition()).normalized().
                    scaled(range).plus(getEnemyUnits().get(0).getPosition());
            away = movePointIntoMap(away);

            attackingUnit.move(away);
            //attackingUnit.queueAttack(attackedUnit);
            tryToActOnPreferred(attackingUnit, attackedUnit, attackingUnit.getPosition(), Unit::queueAttack);
        } else {
            //attackingUnit.attack(attackedUnit);
            tryToActOnPreferred(attackingUnit, attackedUnit, attackingUnit.getPosition(), Unit::attack);
        }
    }

    private Units getPreferredUnitType(Units attackingUnit) {
        if (attackingUnit == Units.TERRAN_FIREBAT) {
            return Units.TERRAN_MARINE;
        } else {
            if (!getListOfTypes(getEnemyUnits(), Units.TERRAN_FIREBAT).isEmpty())
                return Units.TERRAN_FIREBAT;
            else if (!getListOfTypes(getEnemyUnits(), Units.TERRAN_MARAUDER).isEmpty())
                return Units.TERRAN_MARAUDER;
            else
                return Units.TERRAN_MARINE;
        }
    }

    private static List<Unit> getListOfTypes(List<Unit> units, Units type) {
        List<Unit> typeUnits = new ArrayList<>();
        for (Unit u : units) {
            if (u.getType() == type) {
                typeUnits.add(u);
            }
        }
        return typeUnits;
    }

    private void tryToActOnPreferred(Unit attackingUnit, Unit toAttack, Vec2 myUnitsCenter, BiConsumer<Unit, Unit> act) {
        Unit toAttackPreferred = MasterOfZerstoerung.getNearestUnitFromPoint(MasterOfZerstoerung.getListOfTypes(getEnemyUnits(),
                getPreferredUnitType((Units) attackingUnit.getType())), myUnitsCenter);

        var toAttackPreferredList = MasterOfZerstoerung.getListOfTypes(getEnemyUnits(), getPreferredUnitType((Units) attackingUnit.getType()));

        float minHealth = 1f;
        for (Unit enemyUnit : toAttackPreferredList) {
            if (enemyUnit.getHealth() / enemyUnit.getMaxHealth() < minHealth) {
                toAttackPreferred = enemyUnit;
                minHealth = enemyUnit.getHealth() / enemyUnit.getMaxHealth();
            }
        }

        if (toAttackPreferred == null)
            act.accept(attackingUnit, toAttack);
        else
            act.accept(attackingUnit, toAttackPreferred);
    }
}