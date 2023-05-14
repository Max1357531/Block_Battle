import java.awt.*;
import java.util.Arrays;

public class Mapper {
    public static int xRef = -Game.hexRad;
    public static int yRef = -Game.hexRad * 3;
    public static double yScale = 0.6;

    public static double diceSize = (int)(25/30.0*Game.hexRad);
    public static double cubeAngle = Math.toRadians(30);
    int hexRad;

    public Mapper(int hexRad) {
        this.hexRad = hexRad;
    }

    //7 Points of the cube facing towards the player, starting with the bottom left point
    public double[][] getCube(int xcoord, int ycoord, int diceNumber) {
        double xfloor;
        double yfloor;

        //The far side hypotenuse of the cube wrt. perspective angle
        double d = diceSize * Math.pow(2, 1.0 / 2.0) * Math.sin(cubeAngle);
        //The side length wrt. perspective (not including height)
        double x = Math.pow((Math.pow(d / 2.0, 2) + Math.pow(diceSize, 2) / 2.0), 1.0 / 2.0);
        //The height of the cube wrt. perspective
        double h = diceSize * Math.cos(cubeAngle);
        //Interior angles wrt. perspective
        double angle = Math.asin(d / (x * 2.0));


        xfloor = xcoord * 2 * hexRad * Math.cos(Math.toRadians(30));
        xfloor += ycoord * hexRad * Math.cos(Math.toRadians(30));
        yfloor = ycoord * 1.5 * hexRad;
        if (diceNumber <= 4) {
            yfloor = yfloor - (diceNumber - 1) * h / yScale;
        } else {
            yfloor = yfloor + Math.sin(angle) * x / yScale - (diceNumber - 5) * h / yScale;
            xfloor = xfloor + Math.cos(angle) * x;
        }


        double[] xpoints = new double[7];
        double[] ypoints = new double[7];
        xpoints[0] = xfloor - Math.pow(1 / 1.2, 2) * diceSize;
        ypoints[0] = yfloor;
        for (int i = 1; i < 7; i++) {
            xpoints[i] = xpoints[i - 1];
            ypoints[i] = ypoints[i - 1];
            if (i == 1) {
                ypoints[i] -= h / yScale;
            }
            if (i == 2) {
                xpoints[i] += Math.cos(angle) * x;
                ypoints[i] -= Math.sin(angle) * x / yScale;
            }
            if (i == 3) {
                xpoints[i] += Math.cos(angle) * x;
                ypoints[i] += Math.sin(angle) * x / yScale;
            }
            if (i == 4) {
                ypoints[i] += h / yScale;
            }
            if (i == 5) {
                xpoints[i] -= Math.cos(angle) * x;
                ypoints[i] += Math.sin(angle) * x / yScale;
            }
            if (i == 6) {
                ypoints[i] -= h / yScale;
            }
        }
        for (int i = 0; i < 7; i++) {
            xpoints[i] = xpoints[i] - Mapper.xRef;
            ypoints[i] = (ypoints[i]) * yScale - Mapper.yRef;
        }

        return new double[][]{xpoints, ypoints};
    }

    public int[][] getDicePips(int xcentre, int ycentre, int diceSize, int diceVal){
        int[] xPips = new int[9];
        Arrays.fill(xPips, xcentre);
        int[] yPips = new int[9];
        Arrays.fill(yPips, ycentre);
        for(int i = 0; i < 9; i++){
            if (i <= 2){xPips[i] -= diceSize/3;}
            if (i >= 6){xPips[i] += diceSize/3;}
            if (i % 3 == 0){yPips[i] += diceSize/3;}
            if (i % 3 == 2){yPips[i] -= diceSize/3;}
        }
        int[][] diePipValues = new int[][]{{4},{2,6},{2,4,6},{0,2,6,8},{0,2,4,6,8},{0,2,3,5,6,8}};
        int[][] dicePips = new int[diceVal][2];
        for (int i = 0; i < diceVal; i++){
            dicePips[i] = new int[]{xPips[diePipValues[diceVal-1][i]],yPips[diePipValues[diceVal-1][i]]};
        }
        return (dicePips);

    }

    public double[][] getHexagon(int xcoord, int ycoord) {
        double xcentral;
        double ycentral;

        xcentral = xcoord * 2 * hexRad * Math.cos(Math.toRadians(30));
        xcentral += ycoord * hexRad * Math.cos(Math.toRadians(30));

        ycentral = ycoord * 1.5 * hexRad;


        double[] xpoints = new double[6];
        double[] ypoints = new double[6];
        for (int i = 0; i < 6; i++) {
            xpoints[i] = xcentral + hexRad * Math.cos(Math.toRadians(30 + i * 60)) - Mapper.xRef;
            ypoints[i] = (ycentral + hexRad * Math.sin(Math.toRadians(30 + i * 60))) * yScale - Mapper.yRef;
        }
        return new double[][]{xpoints, ypoints};
    }

    public int[][] getIntHexagon(int xcoord, int ycoord) {
        double[][] hexCoords = getHexagon(xcoord, ycoord);
        int[][] intHexCoords = new int[2][6];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 6; j++) {
                intHexCoords[i][j] = (int) Math.round(hexCoords[i][j]);
                if (i == 1 && (j <= 2)) {
                    intHexCoords[i][j] += 1;
                }
            }
        }
        return intHexCoords;
    }

    public Polygon getBorder(double x1, double y1, double x2, double y2, int angle, double borderSize) {
        int[] xpoints = new int[4];
        int[] ypoints = new int[4];

        xpoints[0] = (int) Math.round(x1 + hexRad / borderSize * Math.cos(Math.toRadians(angle)));
        ypoints[0] = (int) Math.round(y1 + (hexRad / borderSize * Math.sin(Math.toRadians(angle))));
        xpoints[1] = (int) Math.round(x1 - (hexRad / borderSize * Math.cos(Math.toRadians(angle))));
        ypoints[1] = (int) Math.round(y1 - (hexRad / borderSize * Math.sin(Math.toRadians(angle))));
        xpoints[2] = (int) Math.round(x2 - (hexRad / borderSize * Math.cos(Math.toRadians(angle))));
        ypoints[2] = (int) Math.round(y2 - (hexRad / borderSize * Math.sin(Math.toRadians(angle))));
        xpoints[3] = (int) Math.round(x2 + (hexRad / borderSize * Math.cos(Math.toRadians(angle))));
        ypoints[3] = (int) Math.round(y2 + (hexRad / borderSize * Math.sin(Math.toRadians(angle))));
        return new Polygon(xpoints, ypoints, 4);
    }

    public int[] clickToCoords(int xPos, int yPos) {
        double calcXPos = xPos + xRef;
        double calcYPos = yPos + yRef;
        calcXPos = xPos / (2 * hexRad * Math.cos(Math.toRadians(30)));

        int finalXCoord = (int) Math.round(calcXPos);
        int finalYCoord = 2;

        return new int[]{finalXCoord, 2};
    }

    public static Color reduceSaturation(Color original, float saturation, float brightness) {
        float[] hsbValues = Color.RGBtoHSB(original.getRed(), original.getGreen(), original.getBlue(), null);
        return Color.getHSBColor(hsbValues[0], saturation, brightness);

    }

}
