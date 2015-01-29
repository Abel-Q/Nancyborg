package comm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import model.animation.Animation;

public class AnimationIO {
    public static Animation loadAnimation(File file) throws IOException, ClassNotFoundException {
        FileInputStream is = new FileInputStream(file);
        try {
            ObjectInputStream ois = new ObjectInputStream(is);
            Animation anim = (Animation) ois.readObject();
            ois.close();
            return anim;
        } finally {
            is.close();
        }
    }

    public static void saveAnimation(File file, Animation anim) throws IOException {
        FileOutputStream os = new FileOutputStream(file);

        try {
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(anim);
            oos.close();
        } finally {
            os.close();
        }
    }
}
