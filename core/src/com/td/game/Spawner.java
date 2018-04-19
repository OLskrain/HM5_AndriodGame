package com.td.game;

import com.badlogic.gdx.Gdx;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Spawner {
    public class Waves{
        int time;
        int rou;
        int cou;
        int [] datawaves = new int[width];

        public Waves(int time, int rou, int cou) {
            this.time = time;
            this.rou = rou;
            this.cou = cou;
            add();
        }
        public void add(){
            datawaves[0] = this.time;
            datawaves[1] = this.rou;
            datawaves[2] = this.cou;
        }

        public int[] getDatawaves() {
            return datawaves;
        }
    }

    private int width; //кол-во столбцов в массиве(если файл это двумерный массив)
    private int height;//кол-во строк в массиве
    private int monsterSpawnTimer;
    private float startTimer;
    private int route;
    private int count;
    private MonsterEmitter monsterEmitter;
    private List<Waves> waves;
    private int waveCounter = 0; //для переключения волн


    public Spawner(MonsterEmitter monsterEmitter) {
        scriptReading("scenario1");
        this.monsterEmitter = monsterEmitter;
    }

    public void scriptReading(String scenarioName) {
        BufferedReader br = null;
        List<String> lines = new ArrayList<String>();

        try {
            br = Gdx.files.internal(scenarioName + ".dat").reader(8192);
            String str;
            while ((str = br.readLine()) != null) {
                lines.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < lines.size(); i++) {
            height = i + 1 ;
        }
        width = lines.get(0).split(",").length;

        waves = new ArrayList<Waves>();
        for (int i = 1; i < height; i++) {//пробегаемся по строчкам
            String[] arr = lines.get(i).split(",");//бъем их по запятым
            monsterSpawnTimer = Integer.parseInt(arr[0]);
            route = Integer.parseInt(arr[1]);
            count = Integer.parseInt(arr[2]);
            waves.add(new Waves(monsterSpawnTimer,route,count));

        }
    }
    public void update(float dt){
        startTimer += dt; //увеличиваем время сначала игры
        if(waveCounter < waves.size()){//если счетчик меньше массива объектов "Волн"
        if (startTimer > waves.get(waveCounter).getDatawaves()[0]) { //смотрим, если время с начала игры прошло больше чем в первой волне
            for (int i = 0; i < waves.get(waveCounter).getDatawaves()[2]; i++) {//заказываем кол-во вмонстров из 1-ой волны
                monsterEmitter.createMonster(waves.get(waveCounter).getDatawaves()[1]);//передаем им маршрут
            }waveCounter++;//меняем счетчик,чтобы переключиться на вторую волну и повторяем все действия пока в массиве не останеться волн
        }
        }
    }
}
