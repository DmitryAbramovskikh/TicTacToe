import java.lang.reflect.Array;
import java.util.Arrays;
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

    /**
     * Ход ИИ. ИИ делает предположение, если за 2 хода выигрыша или проигрыша нет - ходит случайно,
     * иначе побеждает или блокирует победный ход человека
     */
    private static void aiTurn() {
        //Вспомогательное игровое поле на котором ИИ делает предположения
        char[][] helpField = new char[field.length][field[0].length];
        //Приведение вспомогательного игрового поля в состояние актуального для текущего состояния игры
        for (int i = 0; i < field.length; i++) {
            helpField[i] = field[i].clone();
        }
        /* Циклически для каждой пустой ячейки делаем предположения хода ИИ, если победа, то делаем победный ход,
        если победного хода нет, то циклически делаем предположение о следующем ходе человека, если следующих ход
        человека победный, то блокируем путем хода ИИ в эту клетку, если победного хода человека нет ходим рандомно.
         */
        for (int y = 0; y < helpField.length; y++) {
            for (int x = 0; x < helpField[y].length; x++) {
                //Проверяет пуста ли выбранная ячейка, делает предположение и проверяет на выигрыш
                if(helpField[y][x] == DOT_EMPTY){
                    helpField[y][x] = DOT_AI;
                    if (checkWin(DOT_AI, helpField.length, helpField)){
                        //Если ячейка выигрышная - делает ход и завершает метод
                        field[y][x] = DOT_AI;
                        return;
                    }
                    //Иначе делает предположение о следующем ходе человека
                    else {
                        for (int i = 0; i < field.length; i++) {
                            for (int j = 0; j < field[i].length; j++) {
                                if(helpField[i][j] == DOT_EMPTY ){
                                    helpField[i][j] = DOT_HUMAN;
                                    //Проверяет выигрывает ли человек на следующем ходу, если выигрывает, то блокирует ход и завершает метод
                                    if (checkWin(DOT_HUMAN, helpField.length, helpField)){
                                        field[i][j] = DOT_AI;
                                        return;
                                    }
                                    //Если проигрыша нет - снимает предположение
                                    helpField[i][j] = DOT_EMPTY;
                                }
                            }
                        }
                    }
                    //Если в первой итерации победы и проигрыша нет, снимает предположение
                    helpField[y][x] = DOT_EMPTY;
                }
            }
        }
        //Если после 2 ходов нет выигрыша и проигрыша то ходит случайно
        int x;
        int y;
        do {
            x = RANDOM.nextInt(fieldSizeX);
            y = RANDOM.nextInt(fieldSizeY);
        } while (!isCellEmpty(x, y));
        field[y][x] = DOT_AI;
    }

    /** Проверка выигрыша для любого размера игрового поля и любого необходимого количества отметок в ряд (необходимых
     * для победы). Проверка реализована путем перебора всех игровых ячеек. Ищет совпадения размещенных
     * на игровом поле отметок проверяемого игрока от каждой ячейки по горизонтали (вправо), вертикали (вниз) и
     * 2-м диагоналям (вправо вверх и вправо вниз) с учетом границ игрового поля.
     *
     * @param c отметка проверяемого игрока
     * @param rowSize количество отметок в ряд необходимых для победы
     * @param checkField Проверяемое игровое поле (необходимо для проверки не только основного поля игры, но и вспомогательного при построении предположений)
     * @return если победа возращает истина, иначе - ложь
     */
    private static boolean checkWin(char c, int rowSize, char[][] checkField) {
        //Количество отметок в проверяемом ряду (вспомогательная)
        int sum;
        // Перебор всех ячеек игрового поля
        for (int y = 0; y < checkField.length; y++) {
            for (int x = 0; x < checkField[y].length; x++) {

                //Проверка по горизонтали вправо (в условии выполняем проверку границы)
                if (checkField[y].length - x >= rowSize) {
                    sum = 0;
                    for (int i = 0; i < rowSize; i++) {
                        if(checkField[y][x + i] == c) sum++;
                    }
                    if (sum == rowSize) return true;
                }
                //Проверка по вертикали вниз (в условии выполняем проверку границы)
                if (checkField.length - y >= rowSize) {
                    sum = 0;
                    for (int i = 0; i < rowSize; i++) {
                        if(checkField[y + i][x] == c) sum++;
                    }
                    if (sum == rowSize) return true;
                }
                //Проверка 1 диагонали вправо вверх (в условии выполняем проверку границ)
                if (y  >= rowSize - 1 && checkField[y].length - x >= rowSize){
                    sum = 0;
                    for (int i = 0; i < rowSize; i++) {
                        if(checkField[y - i][x + i] == c) sum++;
                    }
                    if (sum == rowSize) return true;
                }
                //Проверка 2 диагонали вправо вниз (в условии выполняем проверку границ)
                if (checkField.length - y >= rowSize && checkField[y].length - x >= rowSize) {
                    sum = 0;
                    for (int i = 0; i < rowSize; i++) {
                        if(checkField[y + i][x + i] == c) sum++;
                    }
                    if (sum == rowSize) return true;
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
            if (!SCANNER.next().toLowerCase().equals("y")) break;
        }
    }

    private static boolean gameChecks(char dot, String s) {
        if (checkWin(dot, field.length, field)) {
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
