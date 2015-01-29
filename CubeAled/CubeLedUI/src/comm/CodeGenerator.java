package comm;

import java.util.Formatter;
import java.util.Random;

import model.animation.Animation;
import model.animation.Frame;
import model.cube.Coord;
import model.cube.Led;

public class CodeGenerator {

    /**
     * Génère le code exécutable associé à l'animation.
     * <p>
     * Ce code peut être compilé dans le programme du cube afin d'y être
     * affiché.
     * <p>
     * Le code est constitué d'une séquence de mots codés sur 16 bits dont la
     * structure est la suivante :
     * <table border=1>
     * <tr>
     * <tr>
     * <td></td>
     * <td>15</td>
     * <td>14</td>
     * <td>13</td>
     * <td>12</td>
     * <td>11</td>
     * <td>10</td>
     * <td>9</td>
     * <td>8</td>
     * <td>7</td>
     * <td>6</td>
     * <td>5</td>
     * <td>4</td>
     * <td>3</td>
     * <td>2</td>
     * <td>1</td>
     * <td>0</td>
     * </tr>
     * <tr>
     * <th>Changer led</th>
     * <td>0</td>
     * <td>0</td>
     * <td colspan=2>z</td>
     * <td colspan=2>y</td>
     * <td colspan=2>x</td>
     * <td colspan=8>Luminosité (0 - 255)</td>
     * </tr>
     * <tr>
     * <th>Tout éteindre</th>
     * <td>1</td>
     * <td>0</td>
     * <td colspan=14>-</td>
     * </tr>
     * <tr>
     * <th>Attente</th>
     * <td>1</td>
     * <td>1</td>
     * <td colspan=14>Délai en millisecondes</td>
     * </tr>
     * </table>
     */
    public static String generateCode(Animation anim) {
        StringBuffer gen = new StringBuffer();
        Formatter format = new Formatter(gen);

        format.format("// Animation : %s%n", anim.getName());

        int n = 1;

        Frame prevFrame = null;

        for (Frame frame : anim.getFrames()) {
            format.format("%n// Frame %d%n", n++);

            for (Led led : frame.getCube()) {
                Coord coord = led.getCoord();
                Led prevLed = prevFrame != null ? prevFrame.getLed(coord) : null;

                if (!led.equals(prevLed)) {
                    int code = generateLedChange(led);
                    format.format("%0#6x, ", code);
                }

                if (gen.charAt(gen.length() - 1) != '\n' && coord.getX() == (anim.getCubeSize() - 1)) {
                    format.format("// y=%d, z=%d %n", coord.getY(), coord.getZ());
                }
            }

            format.format("%#06x, // Delay = %d ms%n", frame.getDuration() | 0x8000, frame.getDuration());

            prevFrame = frame;
        }

        format.close();
        return gen.toString();
    }

    public static int generateLedChange(Led led) {
        int code = led.getBrightness();
        code |= led.getCoord().getX() << 8;
        code |= led.getCoord().getY() << 10;
        code |= led.getCoord().getZ() << 12;
        return code;
    }

    public static void main(String[] args) {
        Random random = new Random();
        Animation a = new Animation("test", 4);

        Frame f1 = a.newFrame();
        f1.setDuration(16);

        for (Led l : f1.getCube()) {
            l.setBrightness(random.nextInt(5));
        }

        Frame f2 = a.newFrame();
        f2.setDuration(32);

        for (Led l : f1.getCube()) {
            l.setBrightness(random.nextInt(5));
        }

        System.out.println(CodeGenerator.generateCode(a));
    }
}
