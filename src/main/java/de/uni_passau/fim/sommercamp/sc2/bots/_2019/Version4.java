package de.uni_passau.fim.sommercamp.sc2.bots._2019;

import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.data.Units;
import de.uni_passau.fim.sommercamp.sc2.bots.AbstractBot;
import de.uni_passau.fim.sommercamp.sc2.bots.Unit;
import de.uni_passau.fim.sommercamp.sc2.bots.util.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Empty bot for the Sommercamp SC2 interface.
 */
public class Version4 extends AbstractBot {

    String name;
    Unit worker;

    /**
     * This constructor is called by the framework. Extend it with all necessary setup, other constructors won't work.
     */
    public Version4() {
        name = "Empty Bot";
    }

    /**
     * This method is called every step by the framework. The game loop consists of calling this method for every bot
     * and executing the invoked actions inside the game.
     */
    @Override
    protected void onStep() {
        Unit opfer = null;
        if (opfer == null) {
            opfer = nextEnemy();
        }



        for (Unit unit : getEnemyUnits()) {
            attackAll(opfer);
            if(marine()!=null) {
                attackFlamentypen(specialEnemy(Units.TERRAN_MARINE));
            }
        }
        if (getMyUnits().get(0).getOrders().isEmpty()) {
            getMyUnits().get(0).move(getRandomPointOnMap());
            moveAll(getMyUnits().get(0).getPosition());
        }
    }


    public void moveAll (Vec2 place){
        for (int i = 0; i < getMyUnits().size(); i++) {
            getMyUnits().get(i).move(place);
        }
    }

    public void attackAll (Unit ziel){
        for (int i = 0; i < getMyUnits().size(); i++) {
            if (getMyUnits().get(i).getType() != Units.TERRAN_FIREBAT) {
                getMyUnits().get(i).attack(ziel);
            }
        }
    }

    public Unit medic () {
        for (int i = 0; i < getEnemyUnits().size(); i++) {
            if (getEnemyUnits().get(i).getType() == Units.TERRAN_MEDIC) {
                return getEnemyUnits().get(i);
            }
        }
        return null;
    }

    public Unit marine () {
        for (int i = 0; i < getEnemyUnits().size(); i++) {
            if (getEnemyUnits().get(i).getType() == Units.TERRAN_MARINE) {
                return getEnemyUnits().get(i);
            }
        }
        return null;
    }

    public Unit firebat () {
        for (int i = 0; i < getEnemyUnits().size(); i++) {
            if (getEnemyUnits().get(i).getType() == Units.TERRAN_FIREBAT) {
                return getEnemyUnits().get(i);
            }
        }
        return null;
    }


    public void attackFlamentypen (Unit ziel){
        List<Unit> list = new ArrayList<>();
        for (Unit unit : getMyUnits()) {
            if (unit.getType() == Units.TERRAN_FIREBAT) {
                list.add(unit);
            }
        }
        for (Unit unit : list) {
            unit.attack(ziel);
        }
    }

    public void attackMarines (Unit ziel){
        List<Unit> list = new ArrayList<>();
        for (Unit unit : getMyUnits()) {
            if (unit.getType() == Units.TERRAN_MARINE) {
                list.add(unit);
            }
        }
        for (Unit unit : list) {
            if (taktischerRueckzug() == false) {
                unit.attack(ziel);
            }
        }
    }

    public Unit specialEnemy (UnitType type){
        int size = getEnemyUnits().size();
        float lowestDistance = 100000;
        Unit nextEnemy = null;
        Vec2 myPosition = getMyUnits().get(0).getPosition();

        for (int i = 0; i < size; i++) {
            Vec2 enemyPosition = getEnemyUnits().get(i).getPosition();
            float distance = enemyPosition.minus(myPosition).getLength();

            if (distance < lowestDistance && getEnemyUnits().get(i).getType() == type) {
                nextEnemy = getEnemyUnits().get(i);
                lowestDistance = distance;
            }
        }
        return nextEnemy;
    }

    public Unit nextEnemy () {
        int size = getEnemyUnits().size();
        float lowestDistance = 100000;
        Unit nextEnemy = null;
        Vec2 myPosition = getMyUnits().get(0).getPosition();

        for (int i = 0; i < size; i++) {
            Vec2 enemyPosition = getEnemyUnits().get(i).getPosition();
            float distance = enemyPosition.minus(myPosition).getLength();

            if (distance < lowestDistance) {
                nextEnemy = getEnemyUnits().get(i);
                lowestDistance = distance;
            }
        }
        return nextEnemy;
    }

    public boolean taktischerRueckzug () {
        boolean first = true;
        Unit friend = null;
        for (int i = 0; i < getMyUnits().size(); i++) {
            if (getMyUnits().get(i).getType() == Units.TERRAN_MARINE) {
                if (getMyUnits().get(i).getHealth() < 44) {
                    Unit me = getMyUnits().get(i);
                    Vec2 my = me.getPosition();
                    Vec2 enemy = nextEnemy().getPosition();

                    Vec2 x = enemy.minus(my).negated().scaled(2 / 3);
                    Vec2 ziel = x.plus(my);
                    me.move(ziel);
                    return true;
                }
            }
        }
        return false;
    }

}
