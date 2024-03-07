package org.ctf.client.data;

import java.awt.Color;

/**
 * Converts a string to a color
 * 
 * @author rsyed
 */
public class StringToColor {
    private static Color color;

    public StringToColor() {
        color = Color.BLACK;
    }

    /**
     * Get the color from a string name
     * 
     * @param c String representation of the Color
     * @return BLACK if no color is given, otherwise the requested Color
     */
    static Color getColor(String c) {
        switch (c.toLowerCase()) {
            case "red":
                color = Color.RED;
                break;
            case "green":
                color = Color.GREEN;
                break;
            case "yellow":
                color = Color.YELLOW;
                break;
            case "white":
                color = Color.WHITE;
                break;
            case "black":
                color = Color.BLACK;
                break;
            case "blue":
                color = Color.BLUE;
                break;
            case "cyan":
                color = Color.CYAN;
                break;
            case "darkgray":
                color = Color.DARK_GRAY;
                break;
            case "gray":
                color = Color.GRAY;
                break;

            case "lightgray":
                color = Color.LIGHT_GRAY;
                break;
            case "magneta":
                color = Color.MAGENTA;
                break;
            case "orange":
                color = Color.ORANGE;
                break;
            case "pink":
                color = Color.PINK;
                break;
            default:
                color = Color.BLACK;
        }
        return color;
    }
}
