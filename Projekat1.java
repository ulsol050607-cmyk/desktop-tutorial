import java.util.ArrayList;
import java.util.List;

/*
Projekat 1 - UDG 
Članovi grupe: [Uljana Solodovnik 25/144 , Nikola Bulatovic 25/110]
Opis: Klase Player, Enemy i Game sa detekcijom sudara i vođenjem evidencije događaja.
*/

class Player {
    private String name;
    private int x;
    private int y;
    private int width;
    private int height;
    private int health;

    public Player(String name, int x, int y, int width, int height, int health) {
        setName(name);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        setHealth(health);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Greška: Ime ne smije biti prazno.");
            return;
        }

        // Uklanjanje razmaka sa početka i kraja
        String cistoIme = name.trim();

        // Jednostavno formatiranje: prvo slovo svake riječi u veliko
        String[] rijeci = cistoIme.split(" ");
        String rez = "";

        for (int i = 0; i < rijeci.length; i++) {
            String r = rijeci[i];
            if (!r.isEmpty()) {
                String obradjenaRijec = r.substring(0, 1).toUpperCase() + r.substring(1).toLowerCase();
                rez += obradjenaRijec + " ";
            }
        }

        this.name = rez.trim();
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public int getHealth() { return health; }

    public void setHealth(int health) {
        if (health < 0) {
            this.health = 0;
        } else if (health > 100) {
            this.health = 100;
        } else {
            this.health = health;
        }
    }
    public String toString() {
        return "Player[" + name + "] @ (" + x + "," + y + ") " + width + "x" + height + " HP=" + health;
    }
}

class Enemy {
    private String type;
    private int x;
    private int y;
    private int width;
    private int height;
    private int damage;

    public Enemy(String type, int x, int y, int width, int height, int damage) {
        setType(type);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        setDamage(damage);
    }

    // Parsiranje iz stringa formata "Goblin;12,5;16x16;20"
    public static Enemy parseString(String str) {
        String[] delovi = str.split(";");
        String type = delovi[0];

        String[] koordinate = delovi[1].split(",");
        int x = Integer.parseInt(koordinate[0]);
        int y = Integer.parseInt(koordinate[1]);

        String[] dimenzije = delovi[2].split("x");
        int width = Integer.parseInt(dimenzije[0]);
        int height = Integer.parseInt(dimenzije[1]);

        int damage = Integer.parseInt(delovi[3]);

        return new Enemy(type, x, y, width, height, damage);
    }

    public String getType() { return type; }

    public void setType(String type) {
        if (type == null || type.trim().isEmpty()) {
            System.out.println("Greška: Tip ne smije biti prazan.");
            return;
        }
        this.type = type.trim();
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public int getDamage() { return damage; }

    public void setDamage(int damage) {
        if (damage < 0) {
            this.damage = 0;
        } else if (damage > 100) {
            this.damage = 100;
        } else {
            this.damage = damage;
        }
    }

    public String toString() {
        return "Enemy[" + type + "] @ (" + x + "," + y + ") " + width + "x" + height + " DMG=" + damage;
    }
}

public class Game {
    private Player player;
    private List<Enemy> enemies;
    private List<String> eventLog;

    public Game(Player player) {
        this.player = player;
        this.enemies = new ArrayList<>();
        this.eventLog = new ArrayList<>();
    }

    public boolean checkCollision(Player p, Enemy e) {
        return p.getX() < e.getX() + e.getWidth() &&
               p.getX() + p.getWidth() > e.getX() &&
               p.getY() < e.getY() + e.getHeight() &&
               p.getY() + p.getHeight() > e.getY();
    }

    public void decreaseHealth(Player p, Enemy e) {
        int staroZdravlje = p.getHealth();
        p.setHealth(staroZdravlje - e.getDamage());

        String poruka = "HIT: Player by " + e.getType() + " for " + e.getDamage() +
                        " -> HP " + staroZdravlje + "-> " + p.getHealth();
        eventLog.add(poruka);
    }

    public void addEnemy(Enemy e) {
        enemies.add(e);
        eventLog.add("ADDED: " + e.toString());
    }

    public List<Enemy> findByType(String query) {
        List<Enemy> rezultat = new ArrayList<>();
        for (Enemy e : enemies) {
            if (e.getType().toLowerCase().contains(query.toLowerCase())) {
                rezultat.add(e);
            }
        }
        return rezultat;
    }

    public List<Enemy> collidingWithPlayer() {
        List<Enemy> rezultat = new ArrayList<>();
        for (Enemy e : enemies) {
            if (checkCollision(player, e)) {
                rezultat.add(e);
            }
        }
        return rezultat;
    }

    public void resolveCollisions() {
        List<Enemy> sudareni = collidingWithPlayer();
        for (Enemy e : sudareni) {
            decreaseHealth(player, e);
        }
    }

    public List<String> getEventLog() {
        return eventLog;
    }

    public static void main(String[] args) {
        // 1. Kreiranje igrača
        Player player = new Player("player 1", 10, 5, 32, 32, 85);
        Game game = new Game(player);

        // 2. Dodavanje dva neprijatelja (jedan ručno, jedan iz stringa)
        Enemy e1 = new Enemy("Orc", 50, 50, 20, 20, 15);
        Enemy e2 = Enemy.parseString("Goblin;12,5;16x16;20");

        game.addEnemy(e1);
        game.addEnemy(e2);

        // 3. Pretraga po tipu "gob"
        System.out.println("=== Pretraga neprijatelja ('gob') ===");
        List<Enemy> nadjeni = game.findByType("gob");
        for (Enemy e : nadjeni) {
            System.out.println(e);
        }

        // 4. Ispis stanja igrača prije i poslije obrade sudara
        System.out.println("\nStanje igrača PRIJE obrade sudara:");
        System.out.println(player);

        game.resolveCollisions();

        System.out.println("\nStanje igrača POSLIJE obrade sudara:");
        System.out.println(player);

        // 5. Ispis celog eventLog-a
        System.out.println("\n=== Event Log ===");
        for (String log : game.getEventLog()) {
            System.out.println(log);
        }
    }
}
