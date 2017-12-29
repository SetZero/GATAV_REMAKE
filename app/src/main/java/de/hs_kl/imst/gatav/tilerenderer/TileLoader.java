package de.hs_kl.imst.gatav.tilerenderer;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Bitmap[][] chunkArray;

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

    /*@Deprecated
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

    @Deprecated
    public Map<Integer, Bitmap> getTiles() {
        return tiles;
    }
    */
    public Map<String, List<Collidable>> getObjectGroups() {
        return objectGroups;
    }

    synchronized public boolean isFinishedLoading() {
        return finishedLoading;
    }

    synchronized public int getLoadingPercentage() {
        return loadingPercentage;
    }

    @Deprecated
    public Bitmap getSceneBitmap() {
        return sceneBitmap;
    }

    public Bitmap[][] getChunkArray() {
        return chunkArray;
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
                            tile.setxPos(x);
                            tile.setyPos(y);
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
                generateBitmaps(src, firstGID, usedTilesInMap);
            }

            loadingPercentage = 60;

            loadObjectGroups(doc);
            loadingPercentage = 90;


            generateGameBitmap();
            splitBitmapToChunk();

            tileWidth = tileWidth * ratioX;
            tileHeight = tileHeight * ratioY;
            loadingPercentage = 100;
            //width = width / (int) ScaleHelper.getRatioX();
            //height = height / (int) ScaleHelper.getRatioY();
            Log.d("TileLoader", "Height: " + height);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void generateBitmaps(String src, int firstGID, Set<Integer> usedTilesInTileset) {
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
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(getGraphicsStream(sourceImage), false);

            for (Integer i : usedTilesInTileset) {
                if (i < firstGID || i > firstGID + tiles) continue;
                int realPosInTileset = i - firstGID;
                int xPos = (realPosInTileset % columns) * tileWidth;
                int yPos = (realPosInTileset / columns) * tileHeight;
                Bitmap region = decoder.decodeRegion(new Rect(xPos, yPos, xPos + tileWidth, yPos + tileHeight), null);
                //region = Bitmap.createScaledBitmap(region, region.getWidth() * ratioX, region.getHeight() * ratioY, false);
                this.tiles.put(i, region);
            }
            Log.d("TileLoader", "Finished Generating Bitmaps " + this.tiles.size());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private void loadObjectGroups(Document doc) {
        Log.d("TileLoader", "Start Loading Hitboxes ");
        NodeList objectgroups = doc.getElementsByTagName("objectgroup");
        int groups = objectgroups.getLength();
        for (int group = 0; group < groups; group++) {
            loadingPercentage += (40 / groups) * group;

            Log.d("TileLoader", "Hitbox Layer: " + group);
            Element groupElement = (Element) objectgroups.item(group);
            String name = groupElement.getAttribute("name");
            objectGroups.put(name, new ArrayList<>());

            NodeList objects = groupElement.getElementsByTagName("object");
            int objectAmount = objects.getLength();

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
        Log.d("TileLoader", "Finished Hitboxes");
    }

    private void generateGameBitmap() {
        int w = width * tileWidth;
        int h = height * tileHeight;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(w, h, conf);
        Canvas canvas = new Canvas(bmp);

        for (List<TileInformation> currentLayerTiles : map) {
            for (TileInformation currentTile : currentLayerTiles) {
                Rect test = currentTile.getTileRect();
                Bitmap tmpBmp = tiles.get(currentTile.getTilesetPiece());
                if (tmpBmp != null)
                    canvas.drawBitmap(tmpBmp, test.left, test.top, null);
            }
        }
        //cleanup
        map.clear();
        this.map = null;
        tiles.clear();
        this.tiles = null;

        sceneBitmap = bmp;
    }

    private void splitBitmapToChunk() {
        int sizeX = (int) Math.ceil(1024f / ratioX);
        int sizeY = (int) Math.ceil(1024f / ratioY);
        int splitY = (int) Math.ceil(sceneBitmap.getHeight() / sizeY);
        int splitX = (int) Math.ceil(sceneBitmap.getWidth() / sizeX);
        chunkArray = new Bitmap[splitX + 1][splitY + 1];
        for (int x = 0; x <= splitX; x++) {
            for (int y = 0; y <= splitY; y++) {
                if ((x + 1) * sizeX <= sceneBitmap.getWidth() && (y + 1) * sizeY <= sceneBitmap.getHeight()) {
                    chunkArray[x][y] = Bitmap.createBitmap(sceneBitmap, x * sizeX, y * sizeY, sizeX, sizeY);
                } else {
                    chunkArray[x][y] = Bitmap.createBitmap(sceneBitmap, x * sizeX, y * sizeY, (sceneBitmap.getWidth() - (x) * sizeX), (sceneBitmap.getHeight() - (y) * sizeY));
                }
                chunkArray[x][y] = Bitmap.createScaledBitmap(chunkArray[x][y], chunkArray[x][y].getWidth() * ratioX, chunkArray[x][y].getHeight() * ratioY, false);
            }
        }
        //cleanup
        sceneBitmap = null;
    }

    private InputStream getGraphicsStream(String graphicsName) {
        try {
            return assetManager.open("levels/spritesheets/" + graphicsName);
        } catch (IOException e2) {
            return null;
        }
    }
}