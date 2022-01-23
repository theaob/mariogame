package util;

import renderer.Shader;
import renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static Map<String, Shader> shaderMap = new HashMap<>();
    private static Map<String, Texture> textureMap = new HashMap<>();

    public static Shader getShader(String resourceName)
    {
        File file = new File(resourceName);
        if(shaderMap.containsKey(file.getAbsolutePath())) {
            return shaderMap.get(file.getAbsolutePath());
        }
        else {
            Shader shader = new Shader(resourceName);
            shader.compile();
            shaderMap.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        if(textureMap.containsKey(file.getAbsolutePath())) {
            return textureMap.get(file.getAbsolutePath());
        }
        else {
            Texture texture = new Texture(resourceName);
            textureMap.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }
}
