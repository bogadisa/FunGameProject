package secondEngine.assetManagers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import secondEngine.components.Sprite;
import secondEngine.components.SpriteSheet;

public abstract class AssetManager {
    public String assetFolder;
    public String[] assetNames;

    protected HashMap<String, SpriteSheet> spriteSheets;

    protected AssetManager(String assetFolder) {
        this.spriteSheets = new HashMap<String, SpriteSheet>();
        this.assetFolder = assetFolder;
        try {
            String assetNames[] = getMetadata(assetFolder);
            this.assetNames = assetNames;

            for (int i = 0; i < assetNames.length; i++) {
                this.spriteSheets.put(
                        assetNames[i],
                        new SpriteSheet(assetFolder + assetNames[i]));
            }

        } catch (IOException e) {
            // TODO: handle exception
        }

    }

    protected String[] getMetadata(String pathToFolder) throws IOException {
        InputStream is = getClass().getResourceAsStream(pathToFolder + "metadata.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        int linesToRead = Integer.parseInt(br.readLine());
        String metadata[] = new String[linesToRead];

        String line;
        for (int i = 0; i < linesToRead; i++) {
            line = br.readLine();
            metadata[i] = line.strip();
        }
        br.close();

        return metadata;
    }

    public abstract Sprite getSprite(String assetName, int index, int variation);
}
