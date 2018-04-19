package com.td.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Turret {
    private TowerDefenseGame game;
    private Map map;
    private TextureRegion texture;
    private Vector2 position;
    private float angle; //угол поворота пушки
    private float range; //радиус действия пушки
    private float fireDelay; //скорость выстрелов
    private float fireTimer; //сколько прошло с последнего выстрела
    private float rotationSpeed; // скорость поворота
    private Monster target; //наша цель монстр

    private Vector2 tmpVector; // вспомогательный вектор

    public Turret(TextureAtlas atlas, TowerDefenseGame game, Map map, float cellX, float cellY) {
        this.texture = atlas.findRegion("turret");
        this.game = game;
        this.map = map;
        this.range = 100;
        this.rotationSpeed = 270.0f;
        this.fireDelay = 1.0f;
        this.position = new Vector2(cellX * 80 + 40, cellY * 80 + 40);
        this.angle = 0;
        this.tmpVector = new Vector2(0, 0);
    }

    public void setTurretToCell(int cellX, int cellY) {// метод для того чтобы поставить туррель в центр конкретной клетки
        if (map.isCellEmpty(cellX, cellY)) { //если там  трава
            position.set(cellX * 80 + 40, cellY * 80 + 40);//ставим турель
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 40, position.y - 40, 40, 40, 80, 80, 1, 1, angle);
    }

    public boolean checkMonsterInRange(Monster monster) { //метод для проверки являеться ли монст в радиусе поражения пушки
        //метод dst позволяет выяснить расстояние от точки до точки(от пушки до монстра)
        return Vector2.dst(position.x, position.y, monster.getPosition().x, monster.getPosition().y) < range;
    }

    public void update(float dt) {
        //если наша цель не нулл(цель захвачена) и эта мешень вышла из радиусе действия и монстр неактивен
        if (target != null && (!checkMonsterInRange(target) || !target.isActive())) {
            target = null;//то цеь сбрасываем. у нас ее больше нет
        }
        if (target == null) { //если цель нулл(нет ее). нам нужно ее найти
            //так писать можно. потому что массив упадет в стек и после завершения метода исчезнет
            Monster[] monsters = game.getMonsterEmitter().getMonsters();//массив активных монстров
            for (int i = 0; i < monsters.length; i++) { // перебираем всех активных монстров
                if (monsters[i].isActive() && checkMonsterInRange(monsters[i])) {// и если вдруг монстр активен и в радиусе
                    target = monsters[i];//то захватываем его в цель
                    break;
                }
            }
        }
        checkRotation(dt);
        tryToFire(dt); //метод стрельбы
    }

    public float getAngleToTarget() { //метод определения угла между монстром и пушкой
        //загнав координаты цели в спомогательный вектор, мы избавились от лишнего копирования. его не жалко переписать
        return tmpVector.set(target.getPosition()).sub(position).angle();
    }

    public void checkRotation(float dt) { //метод для поворота пушки
        if (target != null) { //если есть цель
            float angleTo = getAngleToTarget();
            if (angle > angleTo) { // если наш угол больше угла, куда надо повернуть
                if (Math.abs(angle - angleTo) <= 180.0f) {//(1) если тебе надо преодолеть меньше 180 то все ок
                    angle -= rotationSpeed * dt; //скорость поворота пушки
                } else { // если больше то поворачивайся через другую сторону
                    angle += rotationSpeed * dt;
                }
            }
            //чтобы пушка не поворачивалиь через спину долгим пуьем (1)
            if (angle < angleTo) {
                if (Math.abs(angle - angleTo) <= 180.0f) {//(1)
                    angle += rotationSpeed * dt; //(1) если тебе надо преодолеть меньше 180 то все ок
                } else {// если больше то поворачивайся через другую сторону
                    angle -= rotationSpeed * dt;
                }
            }
            //чтобы держать пушку в определенном угле
            if (angle < 0.0f) { //если на ш угол меньше нуля
                angle += 360.0f; //чтобы держать всегда пушку в диапозоне угла 360 angleTo
            }
            if (angle > 360.0f) {
                angle -= 360.0f;
            }
        }
    }

    public void tryToFire(float dt) { //метод для стрельбы пушки
        fireTimer += dt; // всегда должен увеличиваться, чтобы даже когда нету монстров пушка заряжалась
        //если у нас  мешень есть и пушка заряжена (fireTimer >= fireDelay).
        // а так же угол между пушкой и целью менбше 15 градусов Math.abs(angle - getAngleToTarget()) < 15)
        //чтобы пушка не стреляла спиной
        if (target != null && fireTimer >= fireDelay && Math.abs(angle - getAngleToTarget()) < 15) {//
            fireTimer = 0.0f;
            target.takeDamage(1);//домаг монстру 1
        }
    }
}
