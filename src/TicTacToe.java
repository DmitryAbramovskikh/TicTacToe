import java.util.Random;
import java.util.Scanner;

public class TicTacToe {

    private static final char DOT_HUMAN = 'X';
    private static final char DOT_AI = 'O';
    private static final char DOT_EMPTY = '.';
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Random RANDOM = new Random();
    private static char[][] field;
    private static int fieldSizeX;
    private static int fieldSizeY;

    //init field
    private static void initField() {
        fieldSizeY = 3;
        fieldSizeX = 3;
        field = new char[fieldSizeY][fieldSizeX];
        for (int y = 0; y < fieldSizeY; y++) {
            for (int x = 0; x < fieldSizeX; x++) {
                field[y][x] = DOT_EMPTY;
            }
        }
    }

    // printField
    private static void printField() {
        System.out.print("+");
        for (int i = 0; i < fieldSizeX * 2 + 1; i++)
            System.out.print((i % 2 == 0) ? "-" : i / 2 + 1);
        System.out.println();

        for (int i = 0; i < fieldSizeY; i++) {
            System.out.print(i + 1 + "|");
            for (int j = 0; j < fieldSizeX; j++)
                System.out.print(field[i][j] + "|");
            System.out.println();
        }

        for (int i = 0; i <= fieldSizeX * 2 + 1; i++)
            System.out.print("-");
        System.out.println();
    }

    // humanTurn
    private static void humanTurn() {
        int x;
        int y;
        do {
            System.out.print("Введите координаты X и Y (от 1 до 3) через пробел >>> ");
            x = SCANNER.nextInt() - 1;
            y = SCANNER.nextInt() - 1;
        } while (!isCellValid(x, y) || !isCellEmpty(x, y));
        field[y][x] = DOT_HUMAN;
    }

    private static boolean isCellEmpty(int x, int y) {
        return field[y][x] == DOT_EMPTY;
    }

    private static boolean isCellValid(int x, int y) {
        return x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY;
    }

    // aiTurn
    private static void aiTurn() {
        int x;
        int y;
        do {
            x = RANDOM.nextInt(fieldSizeX);
            y = RANDOM.nextInt(fieldSizeY);
        } while (!isCellEmpty(x, y));
        field[y][x] = DOT_AI;
    }

    /** Проверка победы реализована путем перебора всех игровых ячеек. Считаем сумму совпадений размещенных
     * на игровом поле отметок от каждой ячейки по горизонтали (вправо), вертикали (вниз) и 2-м диагоналям (вправо вверх
     * и вправо вниз) с учетом границ игрового поля.
     *
     * @param c отметка проверяемого игрока
     * @return если победа возращаем истина, иначе - ложь
     */
    private static boolean checkWin(char c) {

        // Перебор всех ячеек игрового поля
        for (int y = 0; y < field.length; y++) {
            for (int x = 0; x < field[y].length; x++) {
                //Проверка по горизонтали вправо (в условии выполняем проверку границы)
                if (field[y].length - x > 2) {
                    if (field[y][x] == c && field[y][x+1] == c && field[y][x+2] == c) return true;
                }
                //Проверка по вертикали вниз (в условии выполняем проверку границы)
                if (field.length - y > 2) {
                    if (field[y][x] == c && field[y+1][x] == c && field[y+2][x] == c) return true;
                }
                //Проверка 1 диагонали вправо вверх (в условии выполняем проверку границ)
                if (y  > 1 && field[y].length - x > 2){
                    if (field[y][x] == c && field[y-1][x+1] == c && field[y-2][x+2] == c) return true;
                }
                //Проверка 2 диагонали вправо вниз (в условии выполняем проверку границ)
                if (field.length - y > 2 && field[y].length - x > 2) {
                    if (field[y][x] == c && field[y+1][x+1] == c && field[y+2][x+2] == c) return true;
                }
            }
        }
        return false;

    }

    //checkDraw
    private static boolean checkDraw() {
        for (int y = 0; y < fieldSizeY; y++) {
            for (int x = 0; x < fieldSizeX; x++) {
                if (isCellEmpty(x, y)) return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        while (true) {
            initField();
            printField();
            while (true) {
                humanTurn();
                printField();
                if (gameChecks(DOT_HUMAN, "Human win")) break;
                aiTurn();
                printField();
                if (gameChecks(DOT_AI, "Computer win")) break;
            }
            System.out.println("Play again?");
            if (!SCANNER.next().equals("Y".toLowerCase()))
                break;
        }
    }

    private static boolean gameChecks(char dot, String s) {
        if (checkWin(dot)) {
            System.out.println(s);
            return true;
        }
        if (checkDraw()) {
            System.out.println("draw!");
            return true;
        }
        return false;
    }
}
