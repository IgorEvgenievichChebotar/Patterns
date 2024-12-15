import java.util.ArrayList;
import java.util.List;

enum PlayerType {
    Warrior, Goblin, Sentinel
}

class PlayerBuilder {
    public int health;
    public int damage;
    public PlayerType type;

    public PlayerBuilder withHealth(int health) {
        this.health = Math.max(health, 0);
        return this;
    }

    public PlayerBuilder withDamage(int damage) {
        this.damage = Math.max(damage, 0);;
        return this;
    }

    public PlayerBuilder withType(PlayerType type) {
        this.type = type;
        return this;
    }

    public Player build() {
        return new Player(this);
    }
}

class Player {
    private int health;
    private int damage;
    private PlayerType type;
    private Player opponent;

    public Player() {
    }

    public Player(PlayerBuilder builder) {
        this.health = builder.health;
        this.damage = builder.damage;
        this.type = builder.type;
    }

    public void kick() {
        if (opponent != null) {
            opponent.setHealth(opponent.getHealth() - this.getDamage());
        }
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public PlayerType getType() {
        return type;
    }

    public void setType(PlayerType type) {
        this.type = type;
    }

    public Player getOpponent() {
        return opponent;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    @Override
    public String toString() {
        return "Player{" +
               "health=" + health +
               ", damage=" + damage +
               ", type=" + type +
               '}';
    }
}

// Декоратор для логирования: наследуется от Player
class LoggingDecorator extends Player {
    private final Player wrapped;

    public LoggingDecorator(Player wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void kick() {
        wrapped.kick();
        System.out.println(wrapped.getType() + " ударил " + wrapped.getOpponent().getType()
                           + ", оставив ему " + wrapped.getHealth() + " здоровья");
    }

    @Override
    public int getDamage() {
        return wrapped.getDamage();
    }

    @Override
    public void setDamage(int damage) {
        wrapped.setDamage(damage);
    }

    @Override
    public int getHealth() {
        return wrapped.getHealth();
    }

    @Override
    public void setHealth(int health) {
        wrapped.setHealth(health);
    }

    @Override
    public PlayerType getType() {
        return wrapped.getType();
    }

    @Override
    public void setType(PlayerType type) {
        wrapped.setType(type);
    }

    @Override
    public Player getOpponent() {
        return wrapped.getOpponent();
    }

    @Override
    public void setOpponent(Player opponent) {
        wrapped.setOpponent(opponent);
    }

    @Override
    public String toString() {
        return wrapped.toString();
    }
}

class GameSession {
    private Player player1;
    private Player player2;

    public void setPlayer1(Player player) {
        this.player1 = player;
    }

    public void setPlayer2(Player player) {
        this.player2 = player;
    }

    public void startBattle() {
        player1.setOpponent(player2);
        player2.setOpponent(player1);
    }

    public GameState saveState() {
        return new GameState(
                player1.getHealth(), player1.getDamage(), player1.getType(),
                player2.getHealth(), player2.getDamage(), player2.getType()
        );
    }

    public void restoreState(GameState state) {
        if (state == null) return;
        player1.setHealth(state.p1Health);
        player1.setDamage(state.p1Damage);
        player1.setType(state.p1Type);

        player2.setHealth(state.p2Health);
        player2.setDamage(state.p2Damage);
        player2.setType(state.p2Type);
    }

    public static class GameState {
        private final int p1Health;
        private final int p1Damage;
        private final PlayerType p1Type;

        private final int p2Health;
        private final int p2Damage;
        private final PlayerType p2Type;

        public GameState(int p1Health, int p1Damage, PlayerType p1Type,
                         int p2Health, int p2Damage, PlayerType p2Type) {
            this.p1Health = p1Health;
            this.p1Damage = p1Damage;
            this.p1Type = p1Type;
            this.p2Health = p2Health;
            this.p2Damage = p2Damage;
            this.p2Type = p2Type;
        }
    }

    @Override
    public String toString() {
        return "GameSession{" +
               "player1=" + player1 +
               ", player2=" + player2 +
               '}';
    }
}

class GameStateCaretaker {
    private final List<GameSession.GameState> states = new ArrayList<>();

    public void add(GameSession.GameState state) {
        states.add(state);
    }

    public GameSession.GameState getLastMemento() {
        if (states.isEmpty()) {
            return null;
        }
        return states.get(states.size() - 1);
    }
}

public class StrategicGame {
    public static void main(String[] args) {
        // Создание игроков через билдер
        Player player = new PlayerBuilder()
                .withHealth(100)
                .withDamage(40)
                .withType(PlayerType.Goblin)
                .build();

        Player opponent = new PlayerBuilder()
                .withHealth(200)
                .withDamage(10)
                .withType(PlayerType.Sentinel)
                .build();

        // Оборачиваем игроков в декоратор для логирования ударов
        player = new LoggingDecorator(player);
        opponent = new LoggingDecorator(opponent);

        var game = new GameSession();
        game.setPlayer1(player);
        game.setPlayer2(opponent);

        game.startBattle();

        System.out.println(game);

        // Игроки наносят удары
        player.kick();
        opponent.kick();

        System.out.println(game);

        // Сохраняем состояние
        var gameStateCaretaker = new GameStateCaretaker();
        var gameState = game.saveState();
        gameStateCaretaker.add(gameState);

        // Еще удар
        player.kick();
        System.out.println(game);

        // Восстанавливаем состояние до сохраненного
        game.restoreState(gameStateCaretaker.getLastMemento());
        System.out.println("После восстановления:");
        System.out.println(game);
    }
}
