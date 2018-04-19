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
    private Spawner spawner;

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
        spawner = new Spawner(monsterEmitter);
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
        map.update(dt);
        monsterEmitter.update(dt);
        turret.update(dt);
        spawner.update(dt);
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
