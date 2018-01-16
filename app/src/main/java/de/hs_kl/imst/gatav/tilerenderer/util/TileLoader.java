package de.hs_kl.imst.gatav.tilerenderer.util;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.events.EventContainer;

import static de.hs_kl.imst.gatav.tilerenderer.util.Constants.enableEyeCandy;

/**
 * Loads all game elements from tiled xml (save) file
 * Created by Sebastian on 2017-12-05.
 */
public class TileLoader extends Observable implements Runnable {
    private AssetManager assetManager;
    private Context context;

    private String filename;
    private int width = 0;
    private int height = 0;
    private int tileWidth = 0;
    private int tileHeight = 0;
    private int layers = 0;
    //private int[][][] map;
    private ArrayList<List<TileInformation>> map;
    private Map<Integer, Bitmap> tiles = new HashMap<>();
    private Map<String, List<Collidable>> objectGroups = new HashMap<>();

    private int ratioX = (int) ScaleHelper.getRatioX();
    private int ratioY = (int) ScaleHelper.getRatioY();

    private boolean finishedLoading = false;
    private int loadingPercentage = 0;

    private Bitmap sceneBitmap;
    private Bitmap backgroundBitmap;
    //private Bitmap[][] chunkArray;
    //private LruCache<Pair<Integer, Integer>, Bitmap> chunkArray;

    private List<EventContainer> audioEventList = new ArrayList<>();

    /**
     * Constructor of TileLoader, can only be initialized AFTER the scale helper was initialized
     *
     * @param context  currently unused (can be null)
     * @param filename the file to load (only filename without .tmx and folder)
     */
    public TileLoader(Context context, String filename) {
        if (ratioX <= 0) throw new AssertionError("Scale Helper never initialized!");
        if (ratioY <= 0) throw new AssertionError("Scale Helper never initialized!");

        Log.d("Tile Loader", "Ratio X: " + ratioX);

        this.context = context;
        this.assetManager = context.getAssets();
        this.filename = filename;
    }

    /**
     * Starts loading process, will notify observers when finished
     */
    @Override
    public void run() {
        xmlLoadMap();

        loadingPercentage = 100;
        finishedLoading = true;
        setChanged();
        notifyObservers(true);
        Log.d("TileLoader", "Finished!");
    }

    /**
     * @return level width (px, scaled)
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return level height (px, scaled)
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return tile width (px, scaled)
     */
    public int getTileWidth() {
        return tileWidth;
    }

    /**
     * @return tile height (px, scaled)
     */
    public int getTileHeight() {
        return tileHeight;
    }

    /**
     * @return number of layers
     */
    @Deprecated
    public int getLayers() {
        return layers;
    }

    /**
     * DON'T CALL, will return null!
     *
     * @return all map tiles sorted by layer
     */
    @Deprecated
    public List<List<TileInformation>> getMap() {
        return map;
    }

    /**
     * DON'T CALL, will throw NullPointerException
     *
     * @param layer the layer to get
     * @return tiles in layer "layer"
     */
    @Deprecated
    public List<TileInformation> getLayer(int layer) {
        return map.get(layer);
    }

    /**
     * DON'T CALL, will return null!
     *
     * @return All Tiles
     */
    @Deprecated
    public Map<Integer, Bitmap> getTiles() {
        return tiles;
    }

    /**
     * @return all object groups with group name as key and Rectangle as area
     */
    public Map<String, List<Collidable>> getObjectGroups() {
        return objectGroups;
    }

    /**
     * Call this to check if the TileLoader is fully finished with loading
     *
     * @return is finished?
     */
    synchronized public boolean isFinishedLoading() {
        return finishedLoading;
    }

    /**
     * returns the current State of loading, will return 100 if already finished
     *
     * @return percentage as int (n/100, with n as return)
     */
    synchronized public int getLoadingPercentage() {
        return loadingPercentage;
    }

    /**
     * @return the fully rendered map, will return null if not finished loading!
     */
    public Bitmap getSceneBitmap() {
        return sceneBitmap;
    }

    /**
     * @return the Background Image, only call if eyecandy enabled, otherwise it'll be null
     */
    public Bitmap getBackgroundBitmap() {
        return backgroundBitmap;
    }

    /**
     * Main loading process, will call all substeps.
     * 1. Start loading the Map file
     * 2. Start XML Parser
     * 3. Start loading all tile layers in order, mark all used tiles and save the tiles in a list
     * 4. load all bitmaps with the tiles
     * 5.load all object groups (rectangles, hitboxes, event areas...)
     * 6. generate full background bitmap
     */
    private void xmlLoadMap() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream fis = assetManager.open(Constants.worldSaveLocation + "/" + filename + ".tmx");
            InputSource is = new InputSource(fis);
            Document doc = builder.parse(is);
            Node map = doc.getElementsByTagName("map").item(0);
            Element mapElement = (Element) map;

            width = Integer.parseInt(mapElement.getAttribute("width"));
            height = Integer.parseInt(mapElement.getAttribute("height"));
            tileHeight = Integer.parseInt(mapElement.getAttribute("tileheight"));
            tileWidth = Integer.parseInt(mapElement.getAttribute("tilewidth"));


            NodeList layerList = doc.getElementsByTagName("layer");
            layers = layerList.getLength();

            //ArrayList<List<TileInformation>> fullMap;

            this.map = new ArrayList<>(layers);//new int[layers][width][height];
            Set<Integer> usedTilesInMap = new ArraySet<>();

            Log.d("TileLoader", "Layers: " + this.map.size());
            loadingPercentage = 10;
            for (int layer = 0; layer < layers; layer++) {
                loadingPercentage += 20 / layers;
                Element layerElement = (Element) layerList.item(layer);
                String layerString = layerElement.getElementsByTagName("data").item(0).getTextContent();
                //List<String> items = Arrays.asList(layerString.split("\\s*,\\s*"));
                this.map.add(new ArrayList<>());
                String[] elements = layerString.split(",");
                Log.d("TileLoader", "Start Loading Tiles");
                final int tmpLayer = layer;
                IntStream.range(0, elements.length)
                        .mapToObj(i -> new Pair<>(i, Integer.parseInt(elements[i].trim())))
                        .filter(i -> i.second != 0)
                        .map(i -> {
                            int x = i.first % width;
                            int y = i.first / width;
                            TileInformation tile = new TileInformation();
                            tile.setxPos(x * ratioX);
                            tile.setyPos(y * ratioY);
                            tile.setWidth(tileWidth);
                            tile.setHeight(tileHeight);
                            tile.generateRect();
                            tile.setTilesetPiece(i.second);
                            usedTilesInMap.add(i.second);
                            return tile;
                        }).collect(Collectors.toCollection(() -> this.map.get(tmpLayer)));


                Log.d("TileLoader", "Finished Loading Tiles");
            }
            loadingPercentage = 30;

            NodeList tilesets = doc.getElementsByTagName("tileset");
            int tileAmount = tilesets.getLength();
            for (int i = 0; i < tileAmount; i++) {
                loadingPercentage += 30 / tileAmount;
                Element tileElement = (Element) tilesets.item(i);
                String src = tileElement.getAttribute("source");
                int firstGID = Integer.parseInt(tileElement.getAttribute("firstgid"));
                Log.d("TileLoader", "Loading: " + src);
                this.tiles.putAll(generateBitmaps(src, firstGID, usedTilesInMap));
            }

            loadingPercentage = 60;

            loadObjectGroups(doc);
            loadingPercentage = 90;


            //splitBitmapToChunk(tmpMap);
            tileWidth = tileWidth * ratioX;
            tileHeight = tileHeight * ratioY;
            sceneBitmap = generateGameBitmap(this.map, loadStaticBackgroundImage(doc));
            if (enableEyeCandy) {
                backgroundBitmap = generateBackground(doc);
            }
            loadingPercentage = 100;
            //width = width / (int) ScaleHelper.getRatioX();
            //height = height / (int) ScaleHelper.getRatioY();
            Log.d("TileLoader", "Height: " + height);
            fis.close();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Splits a tilemap in single bitmaps
     *
     * @param src                file name to the tileset, (must be saved in levels/spritesheets)
     * @param firstGID           the start id (offset to start spliting)
     * @param usedTilesInTileset a set of all used tiles to split, others will be ignored
     * @return a map with the tile id and the bitmap
     */
    private Map<Integer, Bitmap> generateBitmaps(String src, int firstGID, Set<Integer> usedTilesInTileset) {
        Log.d("TileLoader", "Start Generating Bitmaps");
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream fis = assetManager.open("levels/spritesheets/" + src);
            InputSource is = new InputSource(fis);
            Document doc = builder.parse(is);
            Node tileset = doc.getElementsByTagName("tileset").item(0);

            Element tilesetElement = (Element) tileset;
            tileWidth = Integer.parseInt(tilesetElement.getAttribute("tilewidth"));
            tileHeight = Integer.parseInt(tilesetElement.getAttribute("tileheight"));
            int tiles = Integer.parseInt(tilesetElement.getAttribute("tilecount"));
            int columns = Integer.parseInt(tilesetElement.getAttribute("columns"));

            Node image = doc.getElementsByTagName("image").item(0);
            Element imageElement = (Element) image;
            String sourceImage = imageElement.getAttribute("source");
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(getSpriteGraphicsStream(sourceImage), false);
            return usedTilesInTileset.parallelStream()
                    .filter(i -> i >= firstGID && i < firstGID + tiles)
                    .map(i -> {
                        int realPosInTileset = i - firstGID;
                        int xPos = (realPosInTileset % columns) * tileWidth;
                        int yPos = (realPosInTileset / columns) * tileHeight;
                        Bitmap region = decoder.decodeRegion(new Rect(xPos, yPos, xPos + tileWidth, yPos + tileHeight), null);
                        region = Bitmap.createScaledBitmap(region, region.getWidth() * ratioX, region.getHeight() * ratioY, false);
                        return new Pair<Integer, Bitmap>(i, region);
                    }).collect(Collectors.toMap(i -> i.first, i -> i.second));


        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    /**
     * loads all object groups (hitboxes, events, coins, enemys, all as area rectangle)
     *
     * @param doc XML doc
     */
    private void loadObjectGroups(Document doc) {
        Log.d("TileLoader", "Start Loading Hitboxes ");
        NodeList objectgroups = doc.getElementsByTagName("objectgroup");
        int groups = objectgroups.getLength();

        objectGroups = IntStream.range(0, groups).mapToObj(i -> {
            Element groupElement = (Element) objectgroups.item(i);
            String name = groupElement.getAttribute("name");

            NodeList objects = groupElement.getElementsByTagName("object");
            int objectAmount = objects.getLength();

            if (!Objects.equals(name, Constants.musicObjectGroupString)) {
                List<Collidable> list = IntStream.range(0, objectAmount).parallel().mapToObj(obj -> {
                    Element objectElement = (Element) objects.item(obj);
                    int id = Integer.parseInt(objectElement.getAttribute("id"));
                    int x = (int) (Double.parseDouble(objectElement.getAttribute("x")) * ratioX);
                    int y = (int) (Double.parseDouble(objectElement.getAttribute("y")) * ratioY);
                    String width = objectElement.getAttribute("width");
                    String height = objectElement.getAttribute("height");
                    if (width != null && height != null && !width.isEmpty() && !height.isEmpty()) {
                        int tmpWidth = (int) Double.parseDouble(width);
                        int tmpHeight = (int) Double.parseDouble(height);
                        String type = objectElement.getAttribute("type");
                        tmpWidth *= ratioX;
                        tmpHeight *= ratioY;
                        Rectangle tmpRect = new Rectangle(x, y, tmpWidth, tmpHeight, type);
                        tmpRect.setId(id);
                        return tmpRect;
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());
                return new Pair<>(name, list);
            } else {
                audioEventList = IntStream.range(0, objectAmount).parallel().mapToObj(obj -> {
                    Element objectElement = (Element) objects.item(obj);
                    int x = (int) (Double.parseDouble(objectElement.getAttribute("x")) * ratioX);
                    int y = (int) (Double.parseDouble(objectElement.getAttribute("y")) * ratioY);
                    String type = objectElement.getAttribute("type");
                    if (type != null && type.equals("music")) {
                        String event = objectElement.getAttribute("name");
                        if (event != null) {
                            try {
                                Class<?> myAudioEvent = Class.forName("de.hs_kl.imst.gatav.tilerenderer.util.audio.events." + event);
                                return new EventContainer(myAudioEvent, new Vector2(x, y));
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toMap(i -> i.first, i -> i.second));
    }

    /**
     * Loads the background image name, but only one!
     *
     * @param doc the xml file to load from
     * @return the name of the file
     */
    private String loadBackgroundImageString(Document doc) {
        NodeList backgroundImage = doc.getElementsByTagName("imagelayer");
        int amount = backgroundImage.getLength();
        if (amount >= 2)
            throw new AssertionError("There can only be a maximum of ONE background image!");
        if (amount > 0) {
            Element backgroundElement = (Element) backgroundImage.item(0);
            NodeList image = backgroundElement.getElementsByTagName("image");
            if (image.getLength() > 0) {
                Element imageElement = (Element) image.item(0);
                return imageElement.getAttribute("source");
            }
        }
        return null;
    }

    /**
     * Loads a static background image, if eyecandy is disabled
     *
     * @param doc the xml file to load from
     * @return the bitmap file with the background image
     */
    private Bitmap loadStaticBackgroundImage(Document doc) {
        if (enableEyeCandy) return null;
        String name = loadBackgroundImageString(doc);
        return (name != null ? BitmapFactory.decodeStream(getGraphicsStream(name, "levels/backgrounds/")) : null);
    }

    /**
     * generates the full map
     * 1. Check if eyecandy is enabled and choose if transparency is needed
     * 2. place the background image
     * 3. draw background, if there is none make it blue, if eyecandy is disabled make it the static background
     * if eyecandy is enabled make it transparent
     * 4. Load all tiles in the game
     *
     * @param map
     * @param backgroundImage
     * @return
     */
    private Bitmap generateGameBitmap(ArrayList<List<TileInformation>> map, Bitmap backgroundImage) {
        int w = width * tileWidth;
        int h = height * tileHeight;
        Bitmap.Config conf;
        if (enableEyeCandy) {
            conf = Bitmap.Config.ARGB_8888;
        } else {
            conf = Bitmap.Config.RGB_565;
        }
        Bitmap backgroundBMP = null;
        if (backgroundImage != null) {
            double scale = Math.ceil(h / (double) backgroundImage.getHeight());
            backgroundBMP = Bitmap.createScaledBitmap(backgroundImage, (int) (backgroundImage.getWidth() * scale), (int) (backgroundImage.getHeight() * scale), false);
            backgroundImage = null;
        }
        Log.d("TileLoader", w + ", " + h + " | " + conf.toString() + "(" + width + "x" + height + ")" + " - " + tileWidth);
        Bitmap bmp;
        try {
            bmp = Bitmap.createBitmap(w, h, conf);
        } catch (OutOfMemoryError e) {
            conf = Bitmap.Config.RGB_565;
            bmp = Bitmap.createBitmap(w, h, conf);
            enableEyeCandy = false;
        }
        Canvas canvas = new Canvas(bmp);

        if (!enableEyeCandy && backgroundBMP == null)
            canvas.drawARGB(255, 109, 165, 255);
        else if (!enableEyeCandy && backgroundBMP != null) {
            Log.d("TileLoader", "Adding " + Math.ceil(w / (double) backgroundBMP.getWidth()) + "x Background");
            for (int i = 0; i < Math.ceil(w / (double) backgroundBMP.getWidth()); i++) {
                canvas.drawBitmap(backgroundBMP, backgroundBMP.getWidth() * i, 0, null);
            }
            backgroundBMP = null;
        }

        for (List<TileInformation> currentLayerTiles : map) {
            for (TileInformation currentTile : currentLayerTiles) {
                Rect test = currentTile.getTileRect();
                Bitmap tmpBmp = tiles.get(currentTile.getTilesetPiece());
                if (tmpBmp != null)
                    canvas.drawBitmap(tmpBmp, test.left, test.top, null);
            }
        }
        //cleanup
        tiles.clear();
        this.tiles = null;
        this.map = null;

        return bmp;
    }

    /**
     * generate the eyecandy background
     *
     * @param doc the xml file to load from
     * @return bitmap with the background image
     */
    private Bitmap generateBackground(Document doc) {
        Bitmap.Config conf = Bitmap.Config.RGB_565;
        String name = loadBackgroundImageString(doc);
        if (name == null) return null;
        Bitmap bmp = BitmapFactory.decodeStream(getGraphicsStream(name, "levels/backgrounds/"));
        //bmp.setConfig(conf);
        return Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * ScaleHelper.getRatioX()), (int) (bmp.getHeight() * ScaleHelper.getRatioY()), false);
    }

    /*private void splitBitmapToChunk(Bitmap sceneBitmap) {
        int sizeX = (int) Math.ceil(1024f / ratioX);
        int sizeY = (int) Math.ceil(1024f / ratioY);
        int splitY = (int) Math.ceil(sceneBitmap.getHeight() / sizeY);
        int splitX = (int) Math.ceil(sceneBitmap.getWidth() / sizeX);
        //chunkArray = new Bitmap[splitX + 1][splitY + 1];
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 2;
        chunkArray =  new LruCache<Pair<Integer, Integer>, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Pair<Integer, Integer> key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        for (int x = 0; x <= splitX; x++) {
            for (int y = 0; y <= splitY; y++) {
                Bitmap tmp;
                if ((x + 1) * sizeX <= sceneBitmap.getWidth() && (y + 1) * sizeY <= sceneBitmap.getHeight()) {
                    tmp = Bitmap.createBitmap(sceneBitmap, x * sizeX, y * sizeY, sizeX, sizeY);
                } else {
                    tmp = Bitmap.createBitmap(sceneBitmap, x * sizeX, y * sizeY, (sceneBitmap.getWidth() - (x) * sizeX), (sceneBitmap.getHeight() - (y) * sizeY));
                }
                //chunkArray[x][y] = Bitmap.createScaledBitmap(chunkArray[x][y], chunkArray[x][y].getWidth() * ratioX, chunkArray[x][y].getHeight() * ratioY, false);
                chunkArray.put(new Pair<>(x, y), Bitmap.createScaledBitmap(tmp, tmp.getWidth() * ratioX, tmp.getHeight() * ratioY, false));
            }
        }
    }*/

    /**
     * returns a graphics stream with the given parameters
     *
     * @param graphicsName the name of the file to load
     * @param folder       the folder of the file to load
     * @return
     */
    private InputStream getGraphicsStream(String graphicsName, String folder) {
        try {
            return assetManager.open(folder + graphicsName);
        } catch (IOException e2) {
            return null;
        }
    }

    /**
     * Returns a spritesheet / tileset
     *
     * @param graphicsName name of the element (saved in /levels/spritesheets)
     * @return the stream of the file
     */
    private InputStream getSpriteGraphicsStream(String graphicsName) {
        return getGraphicsStream(graphicsName, "levels/spritesheets/");
    }

    /**
     * cleans up mess after execution, currently only the scene Bitmap
     */
    public void cleanup() {
        sceneBitmap = null;
    }

    /**
     * @return all AudioEvents
     */
    public List<EventContainer> getAudioEventList() {
        return audioEventList;
    }
}