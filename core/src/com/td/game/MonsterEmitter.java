package com.td.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class MonsterEmitter { // класс который генерирует что то, и/или чем то управляет.отвечает за всех монстров
    private Monster[] monsters;

    public Monster[] getMonsters() {
        return monsters;
    }
//maxSize - максимальное кол-во монстров
    public MonsterEmitter(TextureAtlas atlas, Map map, int maxSize) {
        this.monsters = new Monster[maxSize]; //создаем монстров по махсайз
        for (int i = 0; i < monsters.length; i++) {
            this.monsters[i] = new Monster(atlas, map, 0);
        }
    }

    public void createMonster(int routeIndex) {//если нам нужен активировать монстра
        for (int i = 0; i < monsters.length; i++) {
            if (!monsters[i].isActive()) { //эмитер пробегаеться по монстрам
                monsters[i].activate(routeIndex);//активирует его и задает маршрут
                break;//обязательно делаем брейк, чтобы не создать всех монстров разом
            }
        }
    }

    public void render(SpriteBatch batch) {//отрисовка активных монстров
        for (int i = 0; i < monsters.length; i++) {
            if (monsters[i].isActive()) {
                monsters[i].render(batch);
            }
        }
    }

    public void update(float dt) {//апдет только активных монстров
        for (int i = 0; i < monsters.length; i++) {
            if (monsters[i].isActive()) {
                monsters[i].update(dt);
            }
        }
    }
}
