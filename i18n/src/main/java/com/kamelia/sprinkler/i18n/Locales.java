package com.kamelia.sprinkler.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Provides static methods to obtain {@link Locale} instances for various languages and countries.
 */
public final class Locales {

    //region Languages

    /**
     * Returns a locale for the Spanish language.
     *
     * @return a locale for the Spanish language
     */
    public static @NotNull Locale spanish() {
        if (spanish == null) spanish = new Locale("es");
        return spanish;
    }

    /**
     * Returns a locale for the Portuguese language.
     *
     * @return a locale for the Portuguese language
     */
    public static @NotNull Locale portuguese() {
        if (portuguese == null) portuguese = new Locale("pt");
        return portuguese;
    }

    /**
     * Returns a locale for the Russian language.
     *
     * @return a locale for the Russian language
     */
    public static @NotNull Locale russian() {
        if (russian == null) russian = new Locale("ru");
        return russian;
    }

    /**
     * Returns a locale for the Thai language.
     *
     * @return a locale for the Thai language
     */
    public static @NotNull Locale thai() {
        if (thai == null) thai = new Locale("th");
        return thai;
    }

    /**
     * Returns a locale for the Vietnamese language.
     *
     * @return a locale for the Vietnamese language
     */
    public static @NotNull Locale vietnamese() {
        if (vietnamese == null) vietnamese = new Locale("vi");
        return vietnamese;
    }

    /**
     * Returns a locale for the Greek language.
     *
     * @return a locale for the Greek language
     */
    public static @NotNull Locale greek() {
        if (greek == null) greek = new Locale("el");
        return greek;
    }

    /**
     * Returns a locale for the Hindi language.
     *
     * @return a locale for the Hindi language
     */
    public static @NotNull Locale hindi() {
        if (hindi == null) hindi = new Locale("hi");
        return hindi;
    }

    //endregion

    //region Countries

    /**
     * Returns a locale for the Spanish language in Spain.
     *
     * @return a locale for the Spanish language in Spain
     */
    public static @NotNull Locale spain() {
        if (spain == null) spain = new Locale("es", "ES");
        return spain;
    }

    /**
     * Returns a locale for the Portuguese language in Portugal.
     *
     * @return a locale for the Portuguese language in Portugal
     */
    public static @NotNull Locale portugal() {
        if (portugal == null) portugal = new Locale("pt", "PT");
        return portugal;
    }

    /**
     * Returns a locale for the Portuguese language in Brazil.
     *
     * @return a locale for the Portuguese language in Brazil
     */
    public static @NotNull Locale brazil() {
        if (brazil == null) brazil = new Locale("pt", "BR");
        return brazil;
    }

    /**
     * Returns a locale for the Russian language in Russia.
     *
     * @return a locale for the Russian language in Russia
     */
    public static @NotNull Locale russia() {
        if (russia == null) russia = new Locale("ru", "RU");
        return russia;
    }

    /**
     * Returns a locale for the Thai language in Thailand.
     *
     * @return a locale for the Thai language in Thailand
     */
    public static @NotNull Locale thailand() {
        if (thailand == null) thailand = new Locale("th", "TH");
        return thailand;
    }

    /**
     * Returns a locale for the Vietnamese language in Vietnam.
     *
     * @return a locale for the Vietnamese language in Vietnam
     */
    public static @NotNull Locale vietnam() {
        if (vietnam == null) vietnam = new Locale("vi", "VN");
        return vietnam;
    }

    /**
     * Returns a locale for the Greek language in Greece.
     *
     * @return a locale for the Greek language in Greece
     */
    public static @NotNull Locale greece() {
        if (greece == null) greece = new Locale("el", "GR");
        return greece;
    }

    /**
     * Returns a locale for the Hindi language in India.
     *
     * @return a locale for the Hindi language in India
     */
    public static @NotNull Locale india() {
        if (india == null) india = new Locale("hi", "IN");
        return india;
    }

    //endregion

    private static Locale spanish;
    private static Locale portuguese;
    private static Locale russian;
    private static Locale thai;
    private static Locale vietnamese;
    private static Locale greek;
    private static Locale hindi;

    private static Locale spain;
    private static Locale portugal;
    private static Locale brazil;
    private static Locale russia;
    private static Locale thailand;
    private static Locale vietnam;
    private static Locale greece;
    private static Locale india;

    private Locales() {
        throw new AssertionError();
    }

}
