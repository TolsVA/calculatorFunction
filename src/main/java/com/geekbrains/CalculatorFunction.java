package com.geekbrains;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import static java.lang.String.*;

public class CalculatorFunction {
    public static void main(String[] args) {

        Double c = 0.06 - 0.07;
        BigDecimal decima = new BigDecimal(c);
        String n = format(String.valueOf(c),"#.######");
        c = Double.valueOf(format(String.valueOf(c),"#.######"));
        
        System.out.println(decima.setScale(16, BigDecimal.ROUND_HALF_DOWN));


        String str = "Java / ProgLang";
        // Выводим на экран строку str без символа "v", который находится во второй позиции или индексе 2.
        // Напоминаем, что в Java отсчет начинается с нуля.
        System.out.println(removeCharAt(str, 1));
        System.out.println(str.substring(0, str.length()-1));
        format("Привет %h!",1);
        System.out.printf("Привет %h!",1);
        System.out.printf("%nПривет %nПривет %n");






        /*------------------------------------------------------
         * PARSER RULES
         *------------------------------------------------------*/

//        expr : plusminus* EOF ;
//
//        plusminus: multdiv ( ( '+' | '-' ) multdiv )* ;
//
//        multdiv : factor ( ( '*' | '/' ) factor )* ;
//
//        factor : NUMBER | '(' expr ')' ;

//        String expressionText = "122 - 34 - 3 * (55 + 5 * (3 - 2)) * 2";
        for (int j = 0; j < 100; j++) {
            System.out.println("Введите выражение для вычисления.");
            Scanner in = new Scanner(System.in);
            String expText = in.nextLine();
            int l = 0;
            int r = 0;
            for (int i = 0; i < expText.length(); i++) {
                if (expText.charAt(i) == '(') {
                    l++;
                }
                if (expText.charAt(i) == ')') {
                    r++;
                }
            }
            if (l > r) {
                for (int i = 0; i < l - r; i++) {
                    expText += ')';
                }
            }
            List<Lexeme> lexemes = lexAnalyze(expText);
            LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);
            System.out.println(expr(lexemeBuffer));
        }
    }


    public static String removeCharAt(String s, int pos) {
        // Возвращаем подстроку s, которая начиная с нулевой позиции переданной строки (0)
        // и заканчивается позицией символа (pos), который мы хотим удалить, соединенную с
        // другой подстрокой s, которая начинается со следующей позиции после позиции символа (pos + 1),
        // который мы удаляем, и заканчивается последней позицией переданной строки.
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    public enum LexemeType {
        LEFT_BRACKET, RIGHT_BRACKET,
        OP_PLUS, OP_MINUS, OP_MUL, OP_DIV,
        NUMBER,
        EOF
    }

    public static class Lexeme {
        LexemeType type;
        String value;
        public Lexeme(LexemeType type, String value) {
            this.type = type;
            this.value = value;
        }

        public Lexeme(LexemeType type, Character value) {
            this.type = type;
            this.value = value.toString();
        }

        @Override
        public String toString() {
            return "Lexeme{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static class LexemeBuffer{

        private int pos;

        public List<Lexeme> lexemes;

        public LexemeBuffer(List<Lexeme> lexemes) {
            this.lexemes = lexemes;
        }

        public Lexeme next() {
            return lexemes.get(pos++);
        }

        public void back() {
            pos--;
        }

        public int getPos() {
            return pos;
        }
    }

    public static List<Lexeme> lexAnalyze(String expText) {
        ArrayList<Lexeme> lexemes = new ArrayList<>();
        int pos = 0;
        StringBuilder expTextBuilder = new StringBuilder(expText);
        while (pos < expTextBuilder.length()) {
            char c = expTextBuilder.charAt(pos);
            switch (c) {
                case '(':
                    lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                    pos++;
                    continue;
                case ')':
                    lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                    pos++;
                    continue;
                case '+':
                    lexemes.add(new Lexeme(LexemeType.OP_PLUS, c));
                    pos++;
                    continue;
                case '-':
                    lexemes.add(new Lexeme(LexemeType.OP_MINUS, c));
                    pos++;
                    continue;
                case '*':
                    lexemes.add(new Lexeme(LexemeType.OP_MUL, c));
                    pos++;
                    continue;
                case '/':
                    lexemes.add(new Lexeme(LexemeType.OP_DIV, c));
                    pos++;
                    continue;
                default:
                    if (c <= '9' && c >= '0' || c == ',') {
                        StringBuilder sb = new StringBuilder();
                        do {
                            if (c == ',') {
                                sb.append('.');
                            } else {
                                sb.append(c);
                            }
                            pos++;
                            if (pos >= expTextBuilder.length()) {
                                break;
                            }
                            c = expTextBuilder.charAt(pos);
                        } while (c <= '9' && c >= '0' || c == ',');
                        lexemes.add(new Lexeme(LexemeType.NUMBER, sb.toString()));
                    } else {
                        if (c != ' ') {
                            throw new RuntimeException("Unexpected character: " + c);
                        }
                        pos++;
                    }
            }
        }
        lexemes.add(new Lexeme(LexemeType.EOF,""));
        return lexemes;
    }

    public static Double expr(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        if (lexeme.type == LexemeType.EOF) {
            return (double) 0;
        } else {
            lexemes.back();
            return plusminus(lexemes);
        }
    }

    public static Double plusminus(LexemeBuffer lexemes) {
        double value = multdiv(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_PLUS:
                    value += multdiv(lexemes);
                    break;
                case OP_MINUS:
                    value -= multdiv(lexemes);
                    break;
                default:
                    lexemes.back();
                    return value;
            }
        }
    }

    public static Double multdiv(LexemeBuffer lexemes) {
        double value = factor(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_MUL:
                    value *= factor(lexemes);
                    break;
                case OP_DIV:
                    value /= factor(lexemes);
                    break;
                default:
                    lexemes.back();
                    return value;
            }
        }
    }

    public static Double factor(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.type) {
            case NUMBER:
                return Double.valueOf(lexeme.value);
            case LEFT_BRACKET:
                double value = expr(lexemes);
                lexeme = lexemes.next();
                if (lexeme.type != LexemeType.RIGHT_BRACKET) {
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
                }
                return value;
            default:
                throw new RuntimeException("Unexpected token: " + lexeme.value
                        + " at position: " + lexemes.getPos());
        }
    }
}
