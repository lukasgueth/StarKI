package de.uni_passau.fim.sommercamp.sc2.bots._2019;

import com.github.ocraft.s2client.protocol.data.Units;
import de.uni_passau.fim.sommercamp.sc2.bots.AbstractBot;
import de.uni_passau.fim.sommercamp.sc2.bots.Unit;
import de.uni_passau.fim.sommercamp.sc2.bots.util.Vec2;

import java.util.*;
import java.util.stream.Collectors;

public class TestBot2 extends AbstractBot {

    private Vec2 moveTo;
    private static float MIN_DIST = 1f;

    private PriorityQueue<Unit> enemies;

    @Override
    protected void onStep() {
        rankEnemies();
        moveUnits();
        engageEnemies();
        healWoundedAllies();
    }

    private void rankEnemies() {
        enemies = new PriorityQueue<>((a, b) -> Float.compare(getUnitScore(a), getUnitScore(b)));
        getEnemyUnits().forEach(it -> enemies.offer(it));
    }

    private int getUnitScore(Unit unit) {
        int score = 0;
        if (unit.getType() == Units.TERRAN_FIREBAT) {
            score += 9999;
        }
        if (unit.getType() == Units.TERRAN_FIREBAT) {
            score += 5000;
        }
        if (unit.getHealth() < unit.getMaxHealth()) {
            score += unit.getHealth();
        } else {
            score += unit.getHealth() * 2;
        }
        if (unit.canAttack()) {
            score -= unit.getWeapons().iterator().next().getDamage();
        }
        return score;
    }

    private void moveUnits() {
        if (enemies.isEmpty()) {
            if (moveTo == null) {
                moveToRandomPoint();
            }
            getMyUnits().stream()
                    .filter(unit -> unit.getPosition().minus(moveTo).getLength() < MIN_DIST).findAny()
                    .ifPresent(it -> moveToRandomPoint());
        }
    }

    private void moveToRandomPoint() {
        moveTo = getRandomPointOnMap();
        printDebugString("Moving to location " + moveTo.toString());
        getMyUnits().forEach(unit -> unit.move(moveTo));
    }

    private void engageEnemies() {
        Deque<Unit> attackers = getMyUnits().stream()
                .filter(unit -> unit.canAttack() && unit.getWeaponCooldown() == 0)
                .collect(Collectors.toCollection(ArrayDeque::new));

        Optional<Unit> maybeEnemy = getNextEnemy();
        float enemyHealth = 0;
        if (maybeEnemy.isPresent()) {
            enemyHealth = maybeEnemy.get().getHealth();
        }

        while (attackers.peek() != null && maybeEnemy.isPresent()) {
            Unit attacker = attackers.poll();
            Unit enemy = maybeEnemy.get();

            if (enemyHealth > 0) {
                printDebugString("Unit " + attacker.toString() + " attacking enemy unit " + enemy.toString());
                attacker.attack(enemy);
                enemyHealth -= attacker.getWeapons().iterator().next().getDamage();
            } else {
                maybeEnemy = getNextEnemy();
                if (maybeEnemy.isPresent()) {
                    enemyHealth = maybeEnemy.get().getHealth();
                }
            }
        }

        getMyUnits().stream()
                .filter(unit -> unit.getType() == Units.TERRAN_MARINE)
                .filter(it -> it.getWeaponCooldown() > 0)
                .forEach(this::kite);
    }

    private void kite(Unit unit) {
        unit.getEngagedTarget().ifPresent(target -> {
            if (target.isAliveAndVisible() && target.getType() == Units.TERRAN_FIREBAT) {
                unit.move(unit.getPosition().plus(unit.getPosition().minus(target.getPosition())));
            }
        });
    }

    private Optional<Unit> getNextEnemy() {
        return Optional.ofNullable(enemies.poll());
    }

    private void healWoundedAllies() {
        Deque<Unit> healers = getMyUnits().stream()
                .filter(Unit::canHeal)
                .collect(Collectors.toCollection(ArrayDeque::new));
        Optional<Unit> toHeal = getMyUnits().stream()
                .filter(it -> it.getHealth() < it.getMaxHealth())
                .min(Comparator.comparingDouble(Unit::getHealth));
        toHeal.ifPresentOrElse(
                unit -> healers.forEach(healer -> healer.queueHeal(unit)),
                () -> healers.forEach(healer -> healer.move(getMyUnits().get(0).getPosition())));
    }
}