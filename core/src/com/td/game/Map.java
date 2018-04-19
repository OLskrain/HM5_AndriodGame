package com.td.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//построение карты идет по нижнему левому углу
public class Map {
    public class Route {//класс для построения карты
        private int startX, startY;//стартовая клетка
        private Vector2[] directions;// массив направлений

        public int getStartX() {
            return startX;
        }

        public int getStartY() {
            return startY;
        }

        public Vector2[] getDirections() {
            return directions;
        }

        public Route(String str) {//маршруты
            String[] data = str.split(",");
            startX = Integer.parseInt(data[0]);//вытащили х. тут счет в клетках
            startY = Integer.parseInt(data[1]);
            directions = new Vector2[data[2].length()];//из записи 15,6,LDLULDLU выташили только буквы
            for (int i = 0; i < data[2].length(); i++) {//проходим по надписи
                if (data[2].charAt(i) == 'L') {
                    directions[i] = new Vector2(-1, 0);//задаем направление в ключевых точках
                }
                if (data[2].charAt(i) == 'R') {
                    directions[i] = new Vector2(1, 0);
                }
                if (data[2].charAt(i) == 'U') {
                    directions[i] = new Vector2(0, 1);
                }
                if (data[2].charAt(i) == 'D') {
                    directions[i] = new Vector2(0, -1);
                }
            }
        }
    }

    private int width;//длинна карты
    private int height;//высота карты

    private byte[][] data;
    private List<Route> routes;//набор маршрутов
    private TextureRegion textureGrass;//теперь это не текстура а сслыка на кусочек текстуры из атласа
    private TextureRegion textureRoad;
    private TextureRegion textureCursor;

    private int cursorCX, cursorCY; // координаты курсора(для желтого квадрата)

    public List<Route> getRoutes() {
        return routes;
    }

    public Map(TextureAtlas atlas) {// чтобы иметь доступ к атласу
        textureGrass = atlas.findRegion("grass");// мы будем ссылать на атлас и запрашивать файл с именем земля
        textureRoad = atlas.findRegion("road");
        textureCursor = atlas.findRegion("cursor");
        loadMap("map1");//передаем имя карты
    }

    public void loadMap(String mapName) {// когда мы создаем карту то указываем имя карты
        BufferedReader br = null;//построчно может чиать и работает с текстом. лучше проиниализировать переменную, потому что
        //мы не знаем что будет в try. и может возникнуть ошибка в finally;
        List<String> lines = new ArrayList<String>();//список строк

        try {
            br = Gdx.files.internal(mapName + ".dat").reader(8192);//специальная запись для чтения в либ
            String str;
            while ((str = br.readLine()) != null) {//построчно считываем файл с картой
                lines.add(str);//закидываем результат в лист
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();//закрываем файл
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < lines.size(); i++) {//проходим по списку строк и когда мы утыкаемся в routes(1)
            if (lines.get(i).equals("routes")) {
                height = i;//(1) то высота равна i - количуству строк
                break;
            }
        }
        width = lines.get(0).split(",").length;//по первой строчке ориентируемся сколько у нас столбцов(длина карты). так как разбили строку по ","
        data = new byte[width][height];//создаем байтовый массив соответствующего размера
        for (int i = 0; i < height; i++) {//пробегаемся по строчкам
            String[] arr = lines.get(i).split(",");//бъем их по запятым
            for (int j = 0; j < width; j++) {
                if (arr[j].equals("0")) { //если там 0
                    data[j][height - i - 1] = 0;//здесь именно такие параметры, чтобы была единая система координат для карты и отрисовки
                }
                if (arr[j].equals("1")) {
                    data[j][height - i - 1] = 1;
                }
                if (arr[j].equals("2")) {
                    data[j][height - i - 1] = 2;
                }
            }
        }
        routes = new ArrayList<Route>();//создаем список наших путей
        for (int i = height + 1; i < lines.size(); i++) {//пробегаемся по тем строчкам, которые идут после слова routes в map.dat
            routes.add(new Route(lines.get(i)));//.строим маршруты.добавляем в лист маршрутов
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (data[i][j] == 0) {//если 0 то трава
                    batch.draw(textureGrass, i * 80, j * 80);
                }
                if (data[i][j] == 1 || data[i][j] == 2) {// если 1 или 2 то дорога
                    batch.draw(textureRoad, i * 80, j * 80);
                }
            }
        }
        batch.setColor(1, 1, 1, 0.4f); //меняем прозрачность курсора(желтого квадрата)
        batch.draw(textureCursor, cursorCX * 80, cursorCY * 80);//(2). отрисовка курсора
        batch.setColor(1, 1, 1, 1f);//цвет основного поля.так как у нас 0.4 то сквозь курсор видим поле
    }

    public void update(float dt) { // метод для определения координат мышки
        cursorCX = (int)(Gdx.input.getX() / 80); //определяються и отрисовываеться всегда(2)
        cursorCY = (int)((720 - Gdx.input.getY()) / 80);
    }

    public boolean isCrossroad(int cx, int cy) {//проверка являеться ли в этой клетке перекресток
        return data[cx][cy] == 2;
    }

    public boolean isCellEmpty(int cx, int cy) { //метод для проверки земля это или нет, чтобы поствить туррель
        return data[cx][cy] == 0;
    }
}
