import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String castle = "\uD83C\uDFF0";
        Scanner sc = new Scanner(System.in);
        System.out.println("Введите размер доски (3-10):");
        int sizeBoard = sc.nextInt();
        sc.nextLine();
        if (sizeBoard < 3 || sizeBoard > 10) {
            System.out.println("Недопустимый размер! Используем 5.");
            sizeBoard = 5;
        }
        System.out.println("Размер доски: " + sizeBoard + "x" + sizeBoard);

        Person person = new Person(sizeBoard);

        int step = 0;

        String[][] board = new String[sizeBoard][sizeBoard];
        for (int y = 0; y < sizeBoard; y++) {
            for (int x = 0; x < sizeBoard; x++) {
                board[y][x] = "  ";
            }
        }

        int countMonster = (int)(sizeBoard * sizeBoard * 0.3);
        Random r = new Random();

        Monster[] arrMonster = new Monster[countMonster + 1];
        int count = 0;
        Monster test;
        while (count <= countMonster){
            if (r.nextBoolean()) {
                test = new Monster(sizeBoard);
            }else {
                test = new BigMonster(sizeBoard);
            }
            if (board[test.getY()][test.getX()].equals("  ")){
                board[test.getY()][test.getX()] = test.getImage();
                arrMonster[count] = test;
                count++;
            }
        }

        System.out.println("Привет! Ты готов начать играть в игру? (Напиши: ДА или НЕТ)");

        String answer = sc.nextLine();
        System.out.println("Ваш ответ:	" + answer);

        switch (answer) {
            case "ДА" -> {
                System.out.println("Выбери сложность игры(от 1 до 5):");
                int difficultGame = sc.nextInt();
                sc.nextLine();
                System.out.println("Выбранная сложность: " + difficultGame);

                int cookieCount = 5 - difficultGame;
                Cookie[] arrCookie = new Cookie[cookieCount];
                System.out.println("Ожидаемое кол-во печенек: " + cookieCount);

                int placedCount = 0;

                for (int i = 0; i < cookieCount; i++) {
                    int attempts = 0;
                    Cookie cookie;
                    boolean isFree;
                    boolean notNearOtherCookies;
                    do {
                        cookie = new Cookie(sizeBoard);
                        attempts++;

                        isFree = board[cookie.getY()][cookie.getX()].equals("  ");

                        notNearOtherCookies = true;
                        for (int j = 0; j < placedCount; j++) {
                            Cookie other = arrCookie[j];
                            if (other != null &&
                                    ((cookie.getX() == other.getX() && Math.abs(cookie.getY() - other.getY()) == 1) ||
                                            (cookie.getY() == other.getY() && Math.abs(cookie.getX() - other.getX()) == 1))) {
                                notNearOtherCookies = false;
                                break;
                            }
                        }

                    } while ((!isFree || !notNearOtherCookies) && attempts < 1000);

                    if (attempts < 1000) {
                        board[cookie.getY()][cookie.getX()] = cookie.getImage();
                        arrCookie[i] = cookie;
                        placedCount++;
                    } else {
                        System.out.println("Не удалось разместить печеньку #" + (i+1));
                    }
                }
                System.out.println("Фактически размещено печенек: " + placedCount);

                int castleX = r.nextInt(sizeBoard);
                int castleY = 0;
                int castleAttempts = 0;
                while (!board[castleY][castleX].equals("  ") && castleAttempts < 100000) {
                    castleX = r.nextInt(sizeBoard);
                    castleAttempts++;
                }
                board[castleY][castleX] = castle;

                while (true) {
                    board[person.getY() - 1][person.getX() - 1] = person.getImage();
                    outputBoard(board, person.getLive());
                    System.out.println("Введите куда будет ходить персонаж(ход возможен только по вертикали и горизонтали на одну клетку;" +
                            "\nКоординаты персонажа - (x: " + person.getX() + ", y: " + person.getY() + "))");
                    int x = sc.nextInt();
                    int y = sc.nextInt();
                    sc.nextLine();

                    if (person.moveCorrect(x, y)) {
                        String next = board[y - 1][x - 1];
                        if (next.equals("  ")) {
                            board[person.getY() - 1][person.getX() - 1] = "  ";
                            person.move(x, y);
                            step++;
                            System.out.println("Ход корректный; Новые координаты: " + person.getX() + ", " + person.getY() +
                                    "\nХод номер: " + step);
                        } else if (next.equals(castle)) {
                            System.out.println("Вы прошли игру!");
                            break;
                        } else if (next.equals("🍪")) {
                            board[person.getY() - 1][person.getX() - 1] = "  ";
                            person.move(x, y);
                            person.upLive();
                            step++;
                            System.out.println("Вы получили +1 жизнь");
                            System.out.println("Текущее кол-во жизней: " + person.getLive());
                            outputBoard(board, person.getLive());
                            System.out.println("Введите куда будет ходить персонаж(ход возможен только по вертикали и горизонтали на одну клетку;" +
                                    "\nКоординаты персонажа - (x: " + person.getX() + ", y: " + person.getY() + "))");
                        } else {
                            for (Monster monster : arrMonster) {
                                if (monster.conflictPerson(x, y)) {
                                    if (monster.taskMonster(difficultGame)) {
                                        board[person.getY() - 1][person.getX() - 1] = "  ";
                                        person.move(x, y);
                                    } else {
                                        person.downLive();
                                    }
                                    break;
                                }
                            }
                        }
                    } else {
                        System.out.println("Неккоректный ход");
                    }
                }
            }
            case "НЕТ" -> System.out.println("Жаль, приходи еще!");
            default -> System.out.println("Данные введены неккоректно");
        }
    }

    static void outputBoard(String[][] board, int live) {
        String leftBlock = "| ";
        String rightBlock = "|";
        int sizeBoard = board.length;
        String wall = "+";
        for (int i = 0; i < sizeBoard; i++) {
            wall += " —— +";
        }

        for (String[] raw : board) {
            System.out.println(wall);
            for (String col : raw) {
                System.out.print(leftBlock + col + " ");
            }
            System.out.println(rightBlock);
        }
        System.out.println(wall);

        System.out.println("Количество жизней:	" + live + "\n");
    }
}
