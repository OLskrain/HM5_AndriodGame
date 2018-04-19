package com.td.game;

        import com.badlogic.gdx.graphics.Texture;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;
        import com.badlogic.gdx.graphics.g2d.TextureAtlas;
        import com.badlogic.gdx.graphics.g2d.TextureRegion;
        import com.badlogic.gdx.math.MathUtils;
        import com.badlogic.gdx.math.Vector2;

public class Monster {
    private Map map;
    private TextureRegion texture;
    private TextureRegion textureHp;
    private Vector2 position;
    private Vector2 velocity;
    private float speed;
    private boolean active; //живой или не живой
    private Map.Route route;// есть ссылка на маршрут(routes). они все ссылаються на один и тот же маршрут
    private int routeCounter;// монстр знает сколько раз он повернул
    private int lastCellX, lastCellY;//где он повернул. та ячейка на которой мы впоследний раз повернули, чтобы 2 раза на 1-ой месте не поворачиваться
    private float offsetX, offsetY; //координаты смешения монстра в клетке
    private int hp; //здоровье монстра текушее
    private int hpMax; //максимаьное здоровье монстра

    public boolean isActive() {
        return active;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Monster(TextureAtlas atlas, Map map, int routeIndex) {//мы говорим что у тебя есть карта и ты стоиш на n-ом маршруте
        this.map = map;
        this.texture = atlas.findRegion("monster");//в случае с атласом. все они ссылаються на одну текстуру
        this.textureHp = atlas.findRegion("monsterHp");
        this.speed = 100.0f;
        this.route = map.getRoutes().get(routeIndex);//наш текуший маршрут равен.запросим у карты маршрут с номером route.запоминаем маршрут
        this.offsetX = MathUtils.random(10, 70);
        this.offsetY = MathUtils.random(10, 70);
        this.position = new Vector2(route.getStartX() * 80 + offsetX, route.getStartY() * 80 + offsetY);
        this.lastCellX = route.getStartX();//последняя ячейка на которой мы повернулись это наша стартовая позиция. потом эти переменные будут меняться
        this.lastCellY = route.getStartY();
        this.routeCounter = 0;// мы говорим что последний раз мы повернули на маршруте с индексом ноль.
        this.velocity = new Vector2(route.getDirections()[0].x * speed, route.getDirections()[0].y * speed);// у стартовой точки мы спрашиваем куда мы должны пойти
        this.hpMax = 5;
        this.hp = this.hpMax;
        this.active = false;//вначале они все мертвые
    }

    public boolean takeDamage(int dmg) { //метод получения урона
        hp -= dmg;
        if (hp <= 0) { //убит ли монстр
            active = false;
            return true;
        }
        return false;
    }

    public void activate(int routeIndex) { //метод активации монстра
        this.offsetX = MathUtils.random(10, 70);
        this.offsetY = MathUtils.random(10, 70);
        this.route = map.getRoutes().get(routeIndex);//выбор индекса маршрута
        this.position.set(route.getStartX() * 80 + offsetX, route.getStartY() * 80 + offsetY);//не центруем а смешаем
        this.lastCellX = route.getStartX();
        this.lastCellY = route.getStartY();
        this.routeCounter = 0;
        this.velocity.set(route.getDirections()[0].x * speed, route.getDirections()[0].y * speed);
        this.hpMax = 5;
        this.hp = this.hpMax;
        this.active = true;
        this.speed = MathUtils.random(80.0f, 120.0f); // немного раскидываем монстров по скорости
    }

    public void render(SpriteBatch batch) {
        //тут теперь запрашиваем texture.getRegionWidth() длину региона и высоту
        batch.draw(texture, position.x - texture.getRegionWidth() / 2, position.y - texture.getRegionHeight() / 2, 40, 40,80,80,0.8f,0.8f,0);
        //длинна и высота картинки здоровья ((float)hp / hpMax) * 80. с течением попаданий картинка уменьшаеться
        batch.draw(textureHp, position.x - 40, position.y + 60, ((float)hp / hpMax) * 80, 20);//отрисовка хп монстра
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);

        int cx = (int) (position.x / 80);//постоянно проверяем на какой клетке мы находимся
        int cy = (int) (position.y / 80);
//из за offsetX, offsetY теперь мы считаем дт не относительно центра клетки, а относительно offsetX, offsetY
        float dx = Math.abs(cx * 80 + offsetX - position.x);// смотрим на сколько далеко от центра клетки мы находимся?
        float dy = Math.abs(cy * 80 + offsetY - position.y);
        //если мы на развилке и растояние между ним и центром клетки меньше чем его удвоенная скорость, то тогда мы считаем что мы вошли в центр клетки
        //если это проверки не сделать, есть вероятность что монст проскочить перекресток.

        if (map.isCrossroad(cx, cy) && Vector2.dst(0, 0, dx, dy) < velocity.len() * dt * 2) {//проверяем дошли ли мы донтра точки
            if (!(lastCellX == cx && lastCellY == cy)) {//если дошли то проверяем а поворачивали мы на ней или нет
                position.set(cx * 80 + offsetX, cy * 80 + offsetY);//если мы еще не были на этой точке, то цинтрируемся на ней
                routeCounter++;// говорим что мы еще раз повернули
                lastCellX = cx;//говорим что эту клетку мы посетили
                lastCellY = cy;
                if (routeCounter > route.getDirections().length - 1) {// если бошле маршрутов(точек) нет то
                    velocity.set(0, 0);// скидиваем скорость
                    return;
                }
                //мы меняем вектор скорости и у route запрашиваем какой должен быть x,y на данном повороте для данного маршрута
                velocity.set(route.getDirections()[routeCounter].x * speed, route.getDirections()[routeCounter].y * speed);
            }
        }
    }
}
