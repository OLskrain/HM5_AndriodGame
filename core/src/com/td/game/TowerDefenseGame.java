package com.td.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class TowerDefenseGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Map map;
    private Turret turret;
    private MonsterEmitter monsterEmitter;
    private TextureAtlas atlas; //атлас текстур
    private float monsterSpawnTimer;
    private int lastRoute; // для определения индекса

    public MonsterEmitter getMonsterEmitter() {
        return monsterEmitter;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        atlas = new TextureAtlas(Gdx.files.internal("game.pack"));
        map = new Map(atlas);
        turret = new Turret(atlas, this, map, 0, 0);
        monsterEmitter = new MonsterEmitter(atlas, map, 60);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        update(dt);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        map.render(batch);
        turret.render(batch);
        monsterEmitter.render(batch);
        batch.end();
    }

    public void update(float dt) {
        monsterSpawnTimer += dt; //увеличиваем время сначала игры
        if (monsterSpawnTimer > 4.0f) { // если вдруг прошло больше 4 секунд
            monsterSpawnTimer = 0.0f;//обнуляем
            for (int i = 0; i < 8; i++) {// запрашиваем 8 монстров
                monsterEmitter.createMonster(lastRoute % 2);
                lastRoute++;
            }
        }

        map.update(dt);
        monsterEmitter.update(dt);
        turret.update(dt);
        if (Gdx.input.justTouched()) {//если мы нажали кннопку
            int cx = (int) (Gdx.input.getX() / 80);
            int cy = (int) ((720 - Gdx.input.getY()) / 80);
            turret.setTurretToCell(cx, cy);//передаем кооординаты нажатия для пушки
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
