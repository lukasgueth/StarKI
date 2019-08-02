package de.uni_passau.fim.sommercamp.sc2.bots._2019;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.ocraft.s2client.protocol.data.UnitAttribute;
import com.github.ocraft.s2client.protocol.data.Units;
import de.uni_passau.fim.sommercamp.sc2.bots.AbstractBot;
import de.uni_passau.fim.sommercamp.sc2.bots.Unit;
import de.uni_passau.fim.sommercamp.sc2.bots.util.Vec2;

/**
 * Empty bot for the Sommercamp SC2 interface.
 */
public class FrankTheTank extends AbstractBot {

    String name;
    Unit unitToHeal;

    /**
     * This constructor is called by the framework. Extend it with all necessary setup, other constructors won't work.
     */
    public FrankTheTank() {
        name = "FrankTheTank";
    }

    /**
     * This method is called every step by the framework. The game loop consists of calling this method for every bot
     * and executing the invoked actions inside the game.
     */
    @Override
    protected void onStep() {

        unitToHeal = null;

        //falls die Einheiten, nicht richtig stehen, sollen sie zuerst positioniert werden, bevor sie etwas anderes tun
        if (!checkTogether(0,1)) {
            positioniere();
        }

        else {
            printDebugString("Phase1");
            //Phase 1 - Worker sucht nach den Gegnern
            if (!found()) {
                //printDebugString("Phase 1");

                walkaround();
            }

            //Alle Units greifen zusammen an
            else{
                attackTogether();
            }
        }
    }

    protected void positioniere(){
        printDebugString("Positionieren");
        //Wenn die erste Einheit nicht richtig steht (2|28-anzahl*2), wird diese positioniert bevor ale anderen positioniert werden
        if (getAliveUnits()[0].getPosition().getX() < 16){
            if (getAliveUnits()[0].getPosition().getX() != 4 || getAliveUnits()[0].getPosition().getY() != 28-numberOfAliveUnits()){ //Vec2.of(2,28-numberOfAliveUnits()) ){//!getAliveUnits()[0].getPosition().equals(Vec2.of(2,28-numberOfAliveUnits()*2))) {
                getAliveUnits()[0].move(Vec2.of(4, 28 - numberOfAliveUnits()));
            }
            else {
                for (int i = 1; i < numberOfAliveUnits(); i++) {
                    call(i, 0, i); //ruft die i-te Einheit zu zwei Feldern über der i-1ten Einheit
                }
            }
        }
        else{
            if (getAliveUnits()[0].getPosition().getX() != 27 || getAliveUnits()[0].getPosition().getY() != 4){ //Vec2.of(2,28-numberOfAliveUnits()) ){//!getAliveUnits()[0].getPosition().equals(Vec2.of(2,28-numberOfAliveUnits()*2))) {
                getAliveUnits()[0].move(Vec2.of(27, 4));
            }
            else {
                for (int i = 1; i < numberOfAliveUnits(); i++) {
                    call(i, 0, i); //ruft die i-te Einheit zu zwei Feldern über der i-1ten Einheit
                }
            }
        }

    }

    protected void goRight(){
        printPosition(getAliveUnits()[0]);
        printDebugString("rechts");
        moveTogether(1,0);
    }

    protected void goLeft(){
        printPosition(getAliveUnits()[0]);
        printDebugString("links");
        moveTogether(-1,0);
    }

    protected void goUp(){
        printPosition(getAliveUnits()[0]);
        printDebugString("oben");
        moveTogether(0,1);
    }

    protected void goDown(){
        printPosition(getAliveUnits()[0]);
        printDebugString("unten");
        moveTogether(0,-1);
    }

    protected void walkaround(){
        printDebugString("Walkaround-Modus");
        //Je nachdem wo die Einheiten gerade stehen, werden sie dem Rand entlang weiter eschickt
        if (getAliveUnits()[0].getPosition().getX() <= 4 && getAliveUnits()[0].getPosition().getY() >= 4){
            goDown();
        }
        if (getAliveUnits()[0].getPosition().getX() <= 27 && getAliveUnits()[0].getPosition().getY() <= 4){
            goRight();
        }
        if (getAliveUnits()[0].getPosition().getX()>= 27 && getAliveUnits()[0].getPosition().getY() <= 27){
            goUp();
        }
        if (getAliveUnits()[numberOfAliveUnits()-1].getPosition().getX()>=4 && getAliveUnits()[numberOfAliveUnits()-1].getPosition().getY() >= 27){
            goLeft();
            printDebugString("Left");
        }
    }

    protected void call(int i, int x, int y){ //ruft eie Einheit auf eine Position verglichen zu Einheit 1
        printDebugString("rufen");
        getAliveUnits()[i].move(Vec2.of(getAliveUnits()[0].getPosition().getX()+x, getAliveUnits()[0].getPosition().getY()+y));
    }

    protected void moveTogether(int dx, int dy){ //Alle Einheiten bewegen sich parallel in eine Richtung
        printDebugString("Laufen in der Gruppe");
        for (int i = 0; i< numberOfAliveUnits(); i++) {
            getAliveUnits()[i].move(Vec2.of(getAliveUnits()[i].getPosition().getX()+dx, getAliveUnits()[i].getPosition().getY()+dy));
        }
    }

    protected boolean found(){ //prüft ob Gegner in der Nhe sind
        if (getEnemyUnits().isEmpty()){
            return false;
        }
        else{
            return true;
        }
    }

    protected boolean checkTogether(int x, int y) { //Prüft ob zwei Einheiten im Abstand x|y zu einander stehen
        boolean together = false;
        if (found()){
            return true;
        }
        if (numberOfAliveUnits() > 1) {
            for (int i = 1; i < numberOfAliveUnits(); i++) {
                if (getAliveUnits()[i - 1].getPosition().getX() + x == getAliveUnits()[i].getPosition().getX() && getAliveUnits()[i - 1].getPosition().getY() + y == getAliveUnits()[i].getPosition().getY()) {
                    together = true;
                } else {
                    return false;
                }
            }
            return together;
        }
        else {
            return true;
        }
    }

    //all Units attack together
    protected void attackTogether(){
        for (int i = 0; i<numberOfAliveUnits(); i++)  {
            if (getAliveUnits()[i].getType() == Units.TERRAN_MEDIC){
                if (getAliveUnits()[i].canHeal()) {
                    getAliveUnits()[i].heal(getUnitToHeal());
                    printDebugString(getAliveUnits()[i].toString());
                }
            }
            else {
                getAliveUnits()[i].attack(getEnemyUnits().get(getTarget()));
            }
        }
    }
    protected void printPosition(Unit unit) {
        printDebugString("X: " + unit.getPosition().getX() + ", Y: " + unit.getPosition().getY());
    }

    protected Unit[] getAliveUnits() {
        Unit aliveUnits[] = new Unit[getMyUnits().size()];
        int j = 0;
        for (int i = 0; i<getMyUnits().size(); i++){
            if (getMyUnits().get(i).isAliveAndVisible()){
                aliveUnits[j] = getMyUnits().get(i);
                j++;
            }
        }
        return aliveUnits;
    }

    protected int numberOfAliveUnits(){
        Unit aliveUnits[] = new Unit[getMyUnits().size()];
        int j = 0;
        for (int i = 0; i<getMyUnits().size(); i++){
            if (getMyUnits().get(i).isAliveAndVisible()){
                aliveUnits[j] = getMyUnits().get(i);
                j++;
            }
        }
        return j;
    }

    protected int getTarget(){
        float sdistanceX;
        float sdistanceY;
        double distance = 100;
        int j = 0;
        for (int i = 0; i<getEnemyUnits().size(); i++){
            sdistanceX = getEnemyUnits().get(i).getPosition().getX() - getAliveUnits()[0].getPosition().getX();
            sdistanceY = getEnemyUnits().get(i).getPosition().getY() - getAliveUnits()[0].getPosition().getY();
            if (distance > Math.sqrt(sdistanceX*sdistanceX + sdistanceY*sdistanceY)) {
                distance = Math.sqrt(sdistanceX * sdistanceX + sdistanceY * sdistanceY);
                j = i;
            }
        }
        return j;
    }

    protected Unit getUnitToHeal(){
        float HP = 100000000;
        int j = 0;
        for (int i = 0; i<numberOfAliveUnits(); i++){
            if (unitToHeal == getAliveUnits()[i]){
                continue;
            }
            if (getAliveUnits()[i].getType() == Units.TERRAN_MEDIC){
                continue;
            }
            else if (getAliveUnits()[i].getHealth() < HP){
                if (getAliveUnits()[i].getMaxHealth()==getAliveUnits()[i].getHealth()){
                    continue;
                }
                else {
                    HP = getAliveUnits()[i].getHealth();
                    j = i;
                }
            }
        }
        printDebugString(getAliveUnits()[j].toString());
        unitToHeal = getAliveUnits()[j];
        return getAliveUnits()[j];
    }
}