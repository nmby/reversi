package xyz.hotchpotch.reversi.cui.common;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 標準入力から対話的にユーザ入力値を取得するためのクラスです。<br>
 * <br>
 * 求める形式とは異なる入力をユーザが行った場合、{@link ConsoleScanner} はユーザに何度も再入力を求めます。<br>
 * 正しい形式の入力値が得られたら、それを必要な形式（数値、クラス、列挙型等）に変換し、呼出し元に返却します。<br>
 * <br>
 * 次の例では、1～12の範囲の整数を標準入力から対話的に取得します。
 * <pre>
 *     int n = ConsoleScanner.intBuilder(1, 12).build().get();
 * </pre>
 * 次の例では、列挙型 {@code MyEnum} の要素の中のひとつを選択するようユーザに要求し、選択された要素を取得します。<br>
 * <pre>
 *     MyEnum selected = ConsoleScanner.enumBuilder(MyEnum.class).build().get();
 * </pre>
 * このほか、正規表現を指定して入力を求めることなども可能です。<br>
 * 標準出力に表示するプロンプトや、要求とは異なる形式の入力をユーザが行った場合に表示するエラーメッセージを
 * カスタマイズすることができます。<br>
 * 詳細は各メソッドの説明を参照してください。<br>
 * <br>
 * このクラスのオブジェクトはスレッドセーフではありません。
 * このクラスのオブジェクトを複数のスレッドから利用することは避けてください。<br>
 * ただし、このクラスのオブジェクトが {@link #get()} を実行しユーザからの入力を待機しているときに、
 * 他のスレッドから割り込みを行うことができます。<br>
 * {@link ConsoleScanner} オブジェクトは、割り込みを検知すると入力待機を解除し、標準では {@code null} を返却して速やかに終了します。<br>
 * 割り込みを検知した際の動作はカスタマイズすることが可能です。
 * 詳細は {@link Builder#emergencyMeasure(Function)} の説明を参照してください。<br>
 * 
 * @param <T> 最終的に呼出し元に返却されるデータの型
 * @author nmby
 */
public class ConsoleScanner<T> implements Supplier<T> {
    
    // [static members] ********************************************************
    
    private static final String BR = System.lineSeparator();
    
    private static Predicate<String> rangeJudge(long lower, long upper) {
        assert lower <= upper;
        
        return s -> {
            try {
                long n = Long.parseLong(s);
                return lower <= n && n <= upper;
            } catch (NumberFormatException e) {
                return false;
            }
        };
    }
    
    /**
     * {@link ConsoleScanner} オブジェクトを構築するためのビルダーです。
     * 
     * @param <T> 最終的に呼出し元に返却されるデータの型
     * @author nmby
     */
    public static class Builder<T> {
        
        // [static members] ----------------------------------------------------
        
        // [instance members] --------------------------------------------------
        
        private Predicate<String> judge;
        private Function<String, ? extends T> converter;
        private String prompt = "> ";
        private String complaint = "入力形式が不正です。再入力してください。" + BR;
        private Function<Exception, ? extends T> emergencyMeasure = e -> null;
        
        /**
         * ユーザ入力値が要求形式に合致するかを判定する {@link Predicate} を指定します。<br>
         * 
         * @param judge ユーザ入力値が要求形式に合致するかを判定する {@link Predicate}
         * @return この {@link Builder} オブジェクト
         * @throws NullPointerException {@code judge} が {@code null} の場合
         */
        public Builder<T> judge(Predicate<String> judge) {
            this.judge = Objects.requireNonNull(judge);
            return this;
        }
        
        /**
         * ユーザ入力文字列を {@code T} 型に変換するための {@link Function} を指定します。<br>
         * 
         * @param converter ユーザ入力文字列を {@code T} 型に変換するための {@link Function}
         * @return この {@link Builder} オブジェクト
         * @throws NullPointerException {@code converter} が {@code null} の場合
         */
        public Builder<T> converter(Function<String, ? extends T> converter) {
            this.converter = Objects.requireNonNull(converter);
            return this;
        }
        
        /**
         * 標準出力に表示するプロンプト文字列を指定します。<br>
         * 
         * @param prompt 標準出力に表示するプロンプト文字列
         * @return この {@link Builder} オブジェクト
         * @throws NullPointerException {@code prompt} が {@code null} の場合
         */
        public Builder<T> prompt(String prompt) {
            this.prompt = Objects.requireNonNull(prompt);
            return this;
        }
        
        /**
         * ユーザが要求とは異なる形式で入力した場合に標準出力に表示するエラー文字列を指定します。<br>
         * 
         * @param complaint 標準出力に表示するエラー文字列
         * @return この {@link Builder} オブジェクト
         * @throws NullPointerException {@code complaint} が {@code null} の場合
         */
        public Builder<T> complaint(String complaint) {
            this.complaint = Objects.requireNonNull(complaint);
            return this;
        }
        
        /**
         * 割り込みや入出力例外が発生した際の対処方法を指定します。<br>
         * {@link ConsoleScanner#get()} の実行中に入出力例外や他のスレッドからの割り込みが発生した場合、
         * 捕捉された例外オブジェクト {@code e} をパラメータとして {@code emergencyMeasure.apply(e)} が実行され、
         * その戻り値が {@link ConsoleScanner#get()} の呼び出し元に返されます。<br>
         * {@code emergencyMeasure.apply(e)} は何らかの値を返すこともできますし、
         * 実行時例外をスローすることもできます。<br>
         * 明示的に指定しない場合のデフォルトでは、{@code emergencyMeasure.apply(e)} は
         * 単に {@code null} を返します。<br>
         * 
         * @param emergencyMeasure 割り込みや入出力例外が発生した場合の対処方法
         * @return この {@link Builder} オブジェクト
         * @throws NullPointerException {@code emergencyMeasure} が {@code null} の場合
         */
        public Builder<T> emergencyMeasure(Function<Exception, ? extends T> emergencyMeasure) {
            this.emergencyMeasure = Objects.requireNonNull(emergencyMeasure);
            return this;
        }
        
        /**
         * {@link ConsoleScanner} オブジェクトを生成します。<br>
         * 
         * @return {@code ConsoleScanner} オブジェクト
         * @throws NullPointerException {@code judge}, {@code converter} のいずれかが未指定の場合
         */
        public ConsoleScanner<T> build() {
            Objects.requireNonNull(judge, "judge");
            Objects.requireNonNull(converter, "converter");
            
            assert prompt != null;
            assert complaint != null;
            assert emergencyMeasure != null;
            
            return new ConsoleScanner<>(
                    judge,
                    converter,
                    prompt,
                    complaint,
                    emergencyMeasure);
        }
    }
    
    /**
     * {@link ConsoleScanner} のビルダーを返します。<br>
     * 
     * @param <T> 最終的に呼出し元に返却されるデータの型
     * @return {@link Builder} オブジェクト
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
    
    /**
     * {@code String} 型の入力値を取得するための {@link ConsoleScanner} のビルダーを返します。<br>
     * 
     * @param judge ユーザ入力値が要求形式に合致するかを判定する {@link Predicate}
     * @return {@link Builder} オブジェクト
     * @throws NullPointerException {@code judge} が {@code null} の場合
     */
    public static Builder<String> stringBuilder(Predicate<String> judge) {
        Objects.requireNonNull(judge);
        
        return new Builder<String>()
                .judge(judge)
                .converter(Function.identity());
    }
    
    /**
     * {@link String} 型の入力値を取得するための {@link ConsoleScanner} のビルダーを返します。<br>
     * 
     * @param pattern ユーザ入力値が要求形式に合致するかを判定する {@link Pattern}
     * @return {@link Builder} オブジェクト
     * @throws NullPointerException {@code pattern} が {@code null} の場合
     */
    public static Builder<String> stringBuilder(Pattern pattern) {
        Objects.requireNonNull(pattern);
        
        return stringBuilder(pattern.asPredicate());
    }
    
    /**
     * {@code String} 型の入力値を取得するための {@link ConsoleScanner} のビルダーを返します。<br>
     * 
     * @param regex ユーザ入力値が要求形式に合致するかを判定する正規表現文字列
     * @return {@link Builder} オブジェクト
     * @throws NullPointerException {@code regex} が {@code null} の場合
     * @throws PatternSyntaxException {@code regex} を {@link Pattern} にコンパイルできない場合
     * @see Pattern#compile(String)
     */
    public static Builder<String> stringBuilder(String regex) {
        Objects.requireNonNull(regex);
        
        return stringBuilder(Pattern.compile(regex));
    }
    
    /**
     * {@link Integer} 型の入力値を取得するための {@link ConsoleScanner} のビルダーを返します。<br>
     * 
     * @param lower 要求する範囲の下限値（この値を範囲に含みます）
     * @param upper 要求する範囲の上限値（この値を範囲に含みます）
     * @return {@link Builder} オブジェクト
     * @throws IllegalArgumentException {@code upper} よりも {@code lower} が大きい場合
     */
    public static Builder<Integer> intBuilder(int lower, int upper) {
        if (upper < lower) {
            throw new IllegalArgumentException("lower=%d, upper=%d".formatted(lower, upper));
        }
        
        return new Builder<Integer>()
                .judge(rangeJudge(lower, upper))
                .converter(Integer::valueOf)
                .prompt("%d～%dの範囲の値を指定してください > ".formatted(lower, upper));
    }
    
    /**
     * {@link Long} 型の入力値を取得するための {@link ConsoleScanner} のビルダーを返します。<br>
     * 
     * @param lower 要求する範囲の下限値（この値を範囲に含みます）
     * @param upper 要求する範囲の上限値（この値を範囲に含みます）
     * @return {@link Builder} オブジェクト
     * @throws IllegalArgumentException {@code upper} よりも {@code lower} が大きい場合
     */
    public static Builder<Long> longBuilder(long lower, long upper) {
        if (upper < lower) {
            throw new IllegalArgumentException("lower=%d, upper=%d".formatted(lower, upper));
        }
        
        return new Builder<Long>()
                .judge(rangeJudge(lower, upper))
                .converter(Long::valueOf)
                .prompt("%d～%dの範囲の値を指定してください > ".formatted(lower, upper));
    }
    
    /**
     * {@link Boolean} 型の入力値を取得するための {@link ConsoleScanner} のビルダーを返します。<br>
     * 
     * @return {@link Builder} オブジェクト
     */
    public static Builder<Boolean> booleanBuilder() {
        return new Builder<Boolean>()
                .judge(s -> s.length() == 1 && "YyNn".contains(s))
                .converter(s -> "y".equals(s.toLowerCase()))
                .prompt("よろしいですか？(y/N) > ")
                .complaint("yかNで入力してください。")
                .emergencyMeasure(s -> false);
    }
    
    /**
     * リストの中から選択された要素を取得するための {@link ConsoleScanner} のビルダーを返します。<br>
     * 
     * @param <T> 最終的に呼出し元に返却されるデータの型
     * @param list 選択対象の要素が格納されたリスト
     * @return {@link Builder} オブジェクト
     * @throws NullPointerException {@code list} が {@code null} の場合
     * @throws IllegalArgumentException {@code list} の要素数が {@code 0} の場合
     */
    public static <T> Builder<T> listBuilder(List<? extends T> list) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            throw new IllegalArgumentException("list is empty.");
        }
        
        Function<String, T> converter = s -> {
            int idx = Integer.parseInt(s) - 1;
            return list.get(idx);
        };
        
        StringBuilder prompt = new StringBuilder();
        prompt.append("次の中から番号で指定してください。").append(BR);
        for (int i = 0; i < list.size(); i++) {
            prompt.append("\t%d : %s".formatted(i + 1, list.get(i))).append(BR);
        }
        prompt.append("> ");
        
        return new Builder<T>()
                .judge(rangeJudge(1, list.size()))
                .converter(converter)
                .prompt(prompt.toString());
    }
    
    /**
     * 列挙型の要素の中から選択された要素を取得するための {@link ConsoleScanner} のビルダーを返します。<br>
     * 
     * @param <E> 最終的に呼出し元に返却されるデータの型
     * @param type 列挙型クラス
     * @return {@link Builder} オブジェクト
     * @throws NullPointerException {@code type} が {@code null} の場合
     */
    public static <E extends Enum<E>> Builder<E> enumBuilder(Class<E> type) {
        Objects.requireNonNull(type);
        
        return listBuilder(Arrays.asList(type.getEnumConstants()));
    }
    
    /**
     * ユーザが確認するまで待機するための {@link ConsoleScanner} を生成します。<br>
     * 生成される {@code ConsoleScanner} の {@link #get()} メソッドは、
     * 標準出力に「{@code 何か入力すると続行します > }」と表示し、
     * ユーザが何らかの入力を行うとその入力値を返します。<br>
     * 
     * @return ユーザが確認するまで待機するための {@link ConsoleScanner}
     */
    public static ConsoleScanner<String> waiter() {
        return waiter("何か入力すると続行します > ");
    }
    
    /**
     * ユーザが確認するまで待機するための {@link ConsoleScanner} を生成します。<br>
     * 生成される {@code ConsoleScanner} の {@link #get()} メソッドは、
     * 標準出力にプロンプト文字列を表示し、
     * ユーザが何らかの入力を行うとその入力値を返します。<br>
     * 
     * @param prompt 標準出力に表示するプロンプト文字列
     * @return ユーザが確認するまで待機するための {@link ConsoleScanner}
     * @throws NullPointerException {@code prompt} が {@code null} の場合
     */
    public static ConsoleScanner<String> waiter(String prompt) {
        Objects.requireNonNull(prompt);
        
        return new Builder<String>()
                .judge(s -> true)
                .converter(Function.identity())
                .prompt(prompt)
                .build();
    }
    
    // [instance members] ******************************************************
    
    private final Predicate<String> judge;
    private final Function<String, ? extends T> converter;
    private final String prompt;
    private final String complaint;
    private final Function<Exception, ? extends T> emergencyMeasure;
    
    private ConsoleScanner(
            Predicate<String> judge,
            Function<String, ? extends T> converter,
            String prompt,
            String complaint,
            Function<Exception, ? extends T> emergencyMeasure) {
        
        this.judge = Objects.requireNonNull(judge, "judge");
        this.converter = Objects.requireNonNull(converter, "converter");
        this.prompt = Objects.requireNonNull(prompt, "prompt");
        this.complaint = Objects.requireNonNull(complaint, "complaint");
        this.emergencyMeasure = Objects.requireNonNull(emergencyMeasure, "emergencyMeasure");
    }
    
    /**
     * 標準入力から対話的にユーザ入力値を取得し、目的の型に変換して返します。<br>
     * 要求する形式の入力値が得られるまで、ユーザに何度も再入力を求めます。<br>
     * <br>
     * 入力待機中に割り込みを検知した場合は入力待機を解除し、
     * このオブジェクトの構築時に指定された方法に従って速やかに終了します。
     * （詳細は {@link Builder#emergencyMeasure(Function)} の説明を参照してください。）<br>
     * 
     * @return ユーザ入力値を変換した {@code T} 型の値
     */
    @Override
    public T get() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = null;
        
        try {
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
            
            boolean isFirst = true;
            String str;
            
            do {
                if (isFirst) {
                    isFirst = false;
                } else {
                    System.out.print(complaint);
                }
                System.out.print(prompt);
                
                future = executor.submit(() -> scanner.nextLine());
                str = future.get();
                
            } while (!judge.test(str));
            
            return converter.apply(str);
            
        } catch (InterruptedException | ExecutionException e) {
            future.cancel(true);
            return emergencyMeasure.apply(e);
            
        } finally {
            executor.shutdownNow();
        }
    }
}
