package it.polimi.ingsw.Utilities;

/**
 * The ConsoleColors enum provides ANSI escape codes for coloring console output.
 * Those codes are used to color the output in the {@link it.polimi.ingsw.View.TextualUI}.
 */
@SuppressWarnings("ALL")
public enum ConsoleColors {
    /**
     * Reset ANSI escape code.
     */
    RESET("\033[m"),

    /**
     * Black text color.
     */
    BLACK("\033[0;30m"),

    /**
     * Green text color.
     */
    GREEN("\033[0;32m"),

    /**
     * Yellow text color.
     */
    YELLOW("\033[0;33m"),

    /**
     * Blue text color.
     */
    BLUE("\033[0;34m"),

    /**
     * Purple text color.
     */
    PURPLE("\033[0;35m"),

    /**
     * Cyan text color.
     */
    CYAN("\033[0;36m"),

    /**
     * White text color.
     */
    WHITE("\033[0;37m"),

    /**
     * Green background color.
     */
    GREEN_BACKGROUND("\033[0;42m"),

    /**
     * Yellow background color.
     */
    YELLOW_BACKGROUND("\u001B[48;2;242;232;99m"),

    /**
     * Light brown background color.
     */
    LIGHTBROWN_BACKGROUND("\u001B[48;2;139;98;32m"),

    /**
     * Brown background color.
     */
    BROWN_BACKGROUND("\u001B[48;2;69;5;12m"),

    /**
     * Blue background color.
     */
    BLUE_BACKGROUND("\033[0;44m"),

    /**
     * Purple background color.
     */
    PURPLE_BACKGROUND("\033[0;45m"),

    /**
     * Cyan background color.
     */
    CYAN_BACKGROUND("\033[0;46m"),

    /**
     * White background color.
     */
    WHITE_BACKGROUND("\033[0;47m"),

    /**
     * Bright green background color.
     */
    GREEN_BACKGROUND_BRIGHT("\033[0;102m"),

    /**
     * Bright yellow background color.
     */
    YELLOW_BACKGROUND_BRIGHT("\033[0;103m"),

    /**
     * Bright blue background color.
     */
    BLUE_BACKGROUND_BRIGHT("\033[0;104m"),

    /**
     * Bright purple background color.
     */
    PURPLE_BACKGROUND_BRIGHT("\033[0;105m"),

    /**
     * Bright cyan background color.
     */
    CYAN_BACKGROUND_BRIGHT("\033[0;106m"),

    /**
     * Bright white background color.
     */
    WHITE_BACKGROUND_BRIGHT("\033[0;107m");

    private final String code;

    /**
     * Constructs a new {@code ConsoleColors} with the specified ANSI escape code.
     *
     * @param code the ANSI escape code
     */
    ConsoleColors(String code) {
        this.code = code;
    }

    /**
     * Returns the ANSI escape code of this {@code ConsoleColors}.
     *
     * @return the ANSI escape code
     */
    public String getCode() {
        return code;
    }
}
