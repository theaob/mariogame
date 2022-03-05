package util;

import jade.Spritesheet;
import jade.Sound;
import renderer.Shader;
import renderer.Texture;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaderMap = new HashMap<>();
    private static Map<String, Texture> textureMap = new HashMap<>();
    private static Map<String, Spritesheet> spritesheetMap = new HashMap<>();
    private static Map<String, Sound> soundMap = new HashMap<>();

    public static Shader getShader(String resourceName) {
        File file = new File(resourceName);
        if (shaderMap.containsKey(file.getAbsolutePath())) {
            return shaderMap.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(resourceName);
            shader.compile();
            shaderMap.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        if (textureMap.containsKey(file.getAbsolutePath())) {
            return textureMap.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture();
            texture.init(resourceName);
            textureMap.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    public static void addSpriteSheet(String resourceName, Spritesheet spritesheet) {
        File file = new File(resourceName);
        if (!spritesheetMap.containsKey(file.getAbsolutePath())) {
            spritesheetMap.put(file.getAbsolutePath(), spritesheet);
        }
    }

    public static Spritesheet getSpritesheet(String resourceName) {
        File file = new File("assets/images/" + resourceName);
        if (!spritesheetMap.containsKey(file.getAbsolutePath())) {
            assert false : "Error: Tried to access spritesheet " + resourceName + " and it has not been added to pool";
        }
        return spritesheetMap.getOrDefault(file.getAbsolutePath(), null);
    }

    public static Sound getSound(String soundFile) {
        File file = new File("assets/sounds/"+soundFile);
        if (!soundMap.containsKey(file.getAbsolutePath())) {
            assert false : "Error: Tried to access sound " + soundFile + " and it has not been added to pool";
        }

        return soundMap.getOrDefault(file.getAbsolutePath(), null);
    }

    public static Sound addSound(String soundFile, boolean loops) {
        File file = new File(soundFile);
        if (soundMap.containsKey(file.getAbsolutePath())) {
            return soundMap.get(file.getAbsolutePath());
        } else {
            Sound s = new Sound(file.getAbsolutePath(), loops);
            soundMap.put(file.getAbsolutePath(), s);
            return s;
        }
    }

    public static Collection<Sound> getAllSounds() {
        return soundMap.values();
    }
}
