package it.polimi.ingsw.Model.Utilities;

public enum ConsoleColors {
    RESET("\033[m"),
    BLACK("\033[0;30m"),
    GREEN("\033[0;32m"),
    YELLOW("\033[0;33m"),
    BLUE("\033[0;34m"),
    PURPLE("\033[0;35m"),
    CYAN("\033[0;36m"),
    WHITE("\033[0;37m"),
    GREEN_BACKGROUND("\033[0;42m"),
    YELLOW_BACKGROUND("\u001B[48;2;242;232;99m"),
    LIGHTBROWN_BACKGROUND("\u001B[48;2;139;98;32m"),
    BROWN_BACKGROUND("\u001B[48;2;69;5;12m"),
    BLUE_BACKGROUND("\033[0;44m"),
    PURPLE_BACKGROUND("\033[0;45m"),
    CYAN_BACKGROUND("\033[0;46m"),
    WHITE_BACKGROUND("\033[0;47m"),

    GREEN_BACKGROUND_BRIGHT("\033[0;102m"),
    YELLOW_BACKGROUND_BRIGHT("\033[0;103m"),
    BLUE_BACKGROUND_BRIGHT("\033[0;104m"),
    PURPLE_BACKGROUND_BRIGHT("\033[0;105m"),
    CYAN_BACKGROUND_BRIGHT("\033[0;106m"),
    WHITE_BACKGROUND_BRIGHT("\033[0;107m");



    private final String code;

    ConsoleColors(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
