package de.hs_kl.imst.gatav.tilerenderer;


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

import de.hs_kl.imst.gatav.tilerenderer.util.Constants;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.ScaleHelper;
import de.hs_kl.imst.gatav.tilerenderer.util.TileInformation;

import static de.hs_kl.imst.gatav.tilerenderer.util.Constants.enableEyeCandy;

/**
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

    public TileLoader(Context context, String filename) {
        assert (ratioX > 0) : "Scale Helper never initialized!";
        assert (ratioY > 0) : "Scale Helper never initialized!";

        Log.d("Tile Loader", "Ratio X: " + ratioX);

        this.context = context;
        this.assetManager = context.getAssets();
        this.filename = filename;
    }

    @Override
    public void run() {
        xmlLoadMap();

        loadingPercentage = 100;
        finishedLoading = true;
        setChanged();
        notifyObservers(true);
        Log.d("TileLoader", "Finished!");
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    @Deprecated
    public int getLayers() {
        return layers;
    }

    @Deprecated
    public List<List<TileInformation>> getMap() {
        return map;
    }

    @Deprecated
    public List<TileInformation> getLayer(int layer) {
        return map.get(layer);
    }

    public Map<Integer, Bitmap> getTiles() {
        return tiles;
    }

    public Map<String, List<Collidable>> getObjectGroups() {
        return objectGroups;
    }

    synchronized public boolean isFinishedLoading() {
        return finishedLoading;
    }

    synchronized public int getLoadingPercentage() {
        return loadingPercentage;
    }

    public Bitmap getSceneBitmap() {
        return sceneBitmap;
    }

    public Bitmap getBackgroundBitmap() {
        return backgroundBitmap;
    }

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
            if(enableEyeCandy) {
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
            /*for (Integer i : usedTilesInTileset) {
                if (i < firstGID || i >= firstGID + tiles) continue;
                int realPosInTileset = i - firstGID;
                int xPos = (realPosInTileset % columns) * tileWidth;
                int yPos = (realPosInTileset / columns) * tileHeight;
                Bitmap region = decoder.decodeRegion(new Rect(xPos, yPos, xPos + tileWidth, yPos + tileHeight), null);
                region = Bitmap.createScaledBitmap(region, region.getWidth() * ratioX, region.getHeight() * ratioY, false);
                this.tiles.put(i, region);
            } */
            //Log.d("TileLoader", "Finished Generating Bitmaps " + this.tiles.size());

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    private void loadObjectGroups(Document doc) {
        Log.d("TileLoader", "Start Loading Hitboxes ");
        NodeList objectgroups = doc.getElementsByTagName("objectgroup");
        int groups = objectgroups.getLength();

        objectGroups = IntStream.range(0, groups).mapToObj(i -> {
            Element groupElement = (Element) objectgroups.item(i);
            String name = groupElement.getAttribute("name");

            NodeList objects = groupElement.getElementsByTagName("object");
            int objectAmount = objects.getLength();

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
                    tmpWidth *= ratioX;
                    tmpHeight *= ratioY;
                    Rectangle tmpRect = new Rectangle(x, y, tmpWidth, tmpHeight);
                    tmpRect.setId(id);
                    return tmpRect;
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            return new Pair<>(name, list);
        }).collect(Collectors.toMap(i -> i.first, i -> i.second));

        /*for (int group = 0; group < groups; group++) {
            //loadingPercentage += (40 / groups) * group;

            Log.d("TileLoader", "Hitbox Layer: " + group);
            Element groupElement = (Element) objectgroups.item(group);
            String name = groupElement.getAttribute("name");

            NodeList objects = groupElement.getElementsByTagName("object");
            int objectAmount = objects.getLength();
            objectGroups.put(name, new ArrayList<>(objectAmount));

            Log.d("TileLoader", "Adding " + objectAmount + " Objects!");
            for (int i = 0; i < objectAmount; i++) {
                Element objectElement = (Element) objects.item(i);
                int id = Integer.parseInt(objectElement.getAttribute("id"));
                //If it's stupid but it works it isn't stupid
                int x = (int) (Double.parseDouble(objectElement.getAttribute("x")) * ratioX);
                int y = (int) (Double.parseDouble(objectElement.getAttribute("y")) * ratioY);

                //Rect
                String width = objectElement.getAttribute("width");
                String height = objectElement.getAttribute("height");
                if (width != null && height != null && !width.isEmpty() && !height.isEmpty()) {
                    int tmpWidth = (int) Double.parseDouble(width);
                    int tmpHeight = (int) Double.parseDouble(height);
                    tmpWidth *= ratioX;
                    tmpHeight *= ratioY;
                    Rectangle tmpRect = new Rectangle(x, y, tmpWidth, tmpHeight);
                    tmpRect.setId(id);
                    objectGroups.get(name).add(tmpRect);
                }
            }
        }
        Log.d("TileLoader", "Finished Hitboxes");*/
    }

    private String loadBackgroundImageString(Document doc) {
        NodeList backgroundImage = doc.getElementsByTagName("imagelayer");
        int amount = backgroundImage.getLength();
        assert amount < 2 : "There can only be a maximum of ONE background image!";
        if(amount > 0) {
            Element backgroundElement = (Element) backgroundImage.item(0);
            NodeList image = backgroundElement.getElementsByTagName("image");
            if(image.getLength() > 0) {
                Element imageElement = (Element) image.item(0);
                String imageName = imageElement.getAttribute("source");
                return imageName;
            }
        }
        return null;
    }

    private Bitmap loadStaticBackgroundImage(Document doc) {
        if(enableEyeCandy) return null;
        String name = loadBackgroundImageString(doc);
        return (name != null ? BitmapFactory.decodeStream(getGraphicsStream(name, "levels/backgrounds/")) : null);
    }

    private Bitmap generateGameBitmap(ArrayList<List<TileInformation>> map, Bitmap backgroundImage) {
        int w = width * tileWidth;
        int h = height * tileHeight;
        Bitmap.Config conf;
        if(enableEyeCandy) {
            conf = Bitmap.Config.ARGB_8888;
        }else {
           conf = Bitmap.Config.RGB_565;
        }
        Bitmap backgroundBMP = null;
        if(backgroundImage != null) {
            double scale = h / backgroundImage.getHeight();
            backgroundBMP = Bitmap.createScaledBitmap(backgroundImage, (int)(backgroundImage.getWidth()*scale), (int)(backgroundImage.getHeight()*scale), false);
            backgroundImage = null;
        }
        Bitmap bmp = Bitmap.createBitmap(w, h, conf);
        Canvas canvas = new Canvas(bmp);

        if(!enableEyeCandy && backgroundBMP == null)
            canvas.drawARGB(255, 109, 165, 255);
        else if(!enableEyeCandy) {
            for(int i=0;i<Math.ceil(w / backgroundBMP.getWidth()); i++) {
                canvas.drawBitmap(backgroundBMP, backgroundBMP.getWidth()*i, 0, null);
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

    private Bitmap generateBackground(Document doc) {
        Bitmap.Config conf = Bitmap.Config.RGB_565;
        String name = loadBackgroundImageString(doc);
        Log.d("TileLoader", "Name: " + name);
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

    private InputStream getGraphicsStream(String graphicsName, String folder) {
        try {
            return assetManager.open(folder + graphicsName);
        } catch (IOException e2) {
            return null;
        }
    }

    private InputStream getSpriteGraphicsStream(String graphicsName) {
        return getGraphicsStream(graphicsName, "levels/spritesheets/");
    }

    public void cleanup() {
        sceneBitmap = null;
    }
}