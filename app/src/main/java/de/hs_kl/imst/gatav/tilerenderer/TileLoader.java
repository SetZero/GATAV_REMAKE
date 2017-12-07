
package de.hs_kl.imst.gatav.tilerenderer;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.TileInformation;

public class TileLoader {
    private AssetManager assetManager;
    private Context context;

    private String filename;
    private int width = 0;
    private int height = 0;
    private int tileWidth = 0;
    private int tileHeight = 0;
    private int layers = 0;
    //private int[][][] map;
    private ArrayList<ArrayList<TileInformation>> map;
    private Map<Integer, Bitmap> tiles = new HashMap<>();
    private Map<String, List<Collidable>> objectGroups = new HashMap<>();

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

    public int getLayers() {
        return layers;
    }

    public ArrayList<ArrayList<TileInformation>> getMap() {
        return map;
    }

    public ArrayList<TileInformation> getLayer(int layer) {
        return map.get(layer);
    }

    public Map<Integer, Bitmap> getTiles() {
        return tiles;
    }

    public Map<String, List<Collidable>> getObjectGroups() {
        return objectGroups;
    }


    public TileLoader(Context context, String filename) {
        this.context = context;
        this.assetManager = context.getAssets();
        this.filename = filename;
        xmlLoadMap();
    }

    private void xmlLoadMap() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream fis = assetManager.open("levels/" + filename + ".tmx");
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
            Set<Integer> usedTilesInMap = new LinkedHashSet<>();

            Log.d("TileLoader", "Layers: " + this.map.size());
            for (int layer = 0; layer < layers; layer++) {
                Element layerElement = (Element) layerList.item(layer);
                String layerString = layerElement.getElementsByTagName("data").item(0).getTextContent();
                List<String> items = Arrays.asList(layerString.split("\\s*,\\s*"));
                this.map.add(new ArrayList<TileInformation>());

                Log.d("TileLoader", "Start Loading Tiles");
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int tileID = Integer.parseInt(items.get(x + (y * width)).trim());
                        if (tileID != 0) {
                            TileInformation tile = new TileInformation();
                            tile.setxPos(x);
                            tile.setyPos(y);
                            tile.setTilesetPiece(tileID);
                            usedTilesInMap.add(tileID);
                            this.map.get(layer).add(tile);
                        }
                    }
                }

                Log.d("TileLoader", "Finished Loading Tiles");
            }

            NodeList tilesets = doc.getElementsByTagName("tileset");
            int tileAmount = tilesets.getLength();
            for (int i = 0; i < tileAmount; i++) {
                Element tileElement = (Element) tilesets.item(i);
                String src = tileElement.getAttribute("source");
                int firstGID = Integer.parseInt(tileElement.getAttribute("firstgid"));
                generateBitmaps(src, firstGID, usedTilesInMap);
            }

            loadObjectGroups(doc);

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

            Log.d("TileLoader", "Start Splitting " + usedTilesInTileset.size() + " Tiles");
            //TODO: List to Map?!
            /*usedTilesInTileset.parallelStream().filter(i -> (i<firstGID || i > firstGID+tiles)).map(
                    i -> {
                        int realPosInTileset = i - firstGID;
                        int xPos = (realPosInTileset % columns) * tileWidth;
                        int yPos = (realPosInTileset / columns) * tileHeight;
                        Bitmap region = decoder.decodeRegion(new Rect( xPos, yPos, xPos+tileWidth, yPos+tileHeight), null);
                        return region;
                    }
            ).collect(Collectors.toMap())*/
            for (Integer i : usedTilesInTileset) {
                if (i < firstGID || i > firstGID + tiles) continue;
                int realPosInTileset = i - firstGID;
                int xPos = (realPosInTileset % columns) * tileWidth;
                int yPos = (realPosInTileset / columns) * tileHeight;
                Bitmap region = decoder.decodeRegion(new Rect(xPos, yPos, xPos + tileWidth, yPos + tileHeight), null);
                this.tiles.put(i, region);
            }
            Log.d("TileLoader", "Finished Generating Bitmaps");

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
            Log.d("TileLoader", "Hitbox Layer: " + group);
            Element groupElement = (Element) objectgroups.item(group);
            String name = groupElement.getAttribute("name");
            objectGroups.put(name, new ArrayList<Collidable>());

            NodeList objects = groupElement.getElementsByTagName("object");
            int objectAmount = objects.getLength();

            for (int i = 0; i < objectAmount; i++) {
                Element objectElement = (Element) objects.item(i);
                int id = Integer.parseInt(objectElement.getAttribute("id"));
                int x = (int) Double.parseDouble(objectElement.getAttribute("x"));
                int y = (int) Double.parseDouble(objectElement.getAttribute("y"));

                //Rect
                String width = objectElement.getAttribute("width");
                String height = objectElement.getAttribute("height");
                if (width != null && height != null && !width.isEmpty() && !height.isEmpty()) {
                    Rectangle tmpRect = new Rectangle(x, y, (int) Double.parseDouble(width), (int) Double.parseDouble(height));
                    objectGroups.get(name).add(tmpRect);
                }
            }
        }
        Log.d("TileLoader", "Finished Hitboxes");
    }

    private InputStream getGraphicsStream(String graphicsName) {
        try {
            return assetManager.open("levels/spritesheets/" + graphicsName);
        } catch (IOException e2) {
            return null;
        }
    }
}