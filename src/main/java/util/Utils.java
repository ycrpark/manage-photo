package util;

public class Utils {
    public static String lPad(String value, int padSize, String pad) {
        if (value == null) {
            return value;
        }

        if (pad == null) {
            pad = " ";
        }

        for (int i = value.length(); i < padSize; i++) {
            value = pad + value;
        }

        return value;
    }

    public static String skipDir(String dir, String skipDir, String alias) {
        if (dir == null || skipDir == null) {
            return dir;
        }

        return dir.replace(skipDir.replace("/", "\\"), alias != null ? alias : "")
                .replace(skipDir.replace("\\", "/"), alias != null ? alias : "");
    }

    public static boolean containsAny(String text, CharSequence... sequences) {
        if (text == null || sequences == null) {
            return false;
        }

        for (CharSequence sequence : sequences) {
            if (text.indexOf(sequence.toString()) > -1) {
                return true;
            }
        }

        return false;
    }

    public static boolean equalsAny(String text, String... anotherTexts) {
        if (text == null || anotherTexts == null) {
            return false;
        }

        for (String anotherText : anotherTexts) {
            if (text.equals(anotherText)) {
                return true;
            }
        }

        return false;
    }

    public static boolean validDateText(String text) {
        return text != null && !text.equals("0000:00:00 00:00:00");
    }
}
