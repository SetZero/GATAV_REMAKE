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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TileLoader {
    private AssetManager assetManager;
    private Context context;

    private String filename;
    private int width = 0;
    private int height = 0;
    private int tileWidth = 0;
    private int tileHeight = 0;
    private int layers = 0;
    private int[][][] map;

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

    public int[][][] getMap() {
        return map;
    }

    public Map<Integer, Bitmap> getTiles() {
        return tiles;
    }

    private Map<Integer, Bitmap> tiles = new HashMap<Integer, Bitmap>();


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
            Element mapElement = (Element)map;

            width = Integer.parseInt(mapElement.getAttribute("width"));
            height = Integer.parseInt(mapElement.getAttribute("height"));
            tileHeight = Integer.parseInt(mapElement.getAttribute("tileheight"));
            tileWidth = Integer.parseInt(mapElement.getAttribute("tilewidth"));

            NodeList tilesets = doc.getElementsByTagName("tileset");
            int tileAmount = tilesets.getLength();
            for (int i = 0; i < tileAmount; i++) {
                Element tileElement = (Element) tilesets.item(i);
                String src = tileElement.getAttribute("source");
                int firstGID = Integer.parseInt(tileElement.getAttribute("firstgid"));
                generateBitmaps(src, firstGID);
            }


            NodeList layerList = doc.getElementsByTagName("layer");
            layers = layerList.getLength();
            this.map = new int[layers][width][height];
            System.out.println("Length: " + this.map.length);

            for (int layer = 0; layer < layers; layer++) {
                Element layerElement = (Element) layerList.item(layer);
                String layerString = layerElement.getElementsByTagName("data").item(0).getTextContent();
                List<String> items = Arrays.asList(layerString.split("\\s*,\\s*"));

                for(int y = 0; y < height; y++) {
                    for(int x = 0; x < width; x++) {
                        this.map[layer][x][y] = Integer.parseInt(items.get(x + (y*width)).trim());
                    }
                }
            }

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

    private void generateBitmaps(String src, int firstGID) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream fis =  assetManager.open("levels/spritesheets/" + src);
            InputSource is = new InputSource(fis);
            Document doc = builder.parse(is);
            Node tileset = doc.getElementsByTagName("tileset").item(0);
            Element tilesetElement = (Element)tileset;
            tileWidth = Integer.parseInt(tilesetElement.getAttribute("tilewidth"));
            tileHeight = Integer.parseInt(tilesetElement.getAttribute("tileheight"));
            int tiles = Integer.parseInt(tilesetElement.getAttribute("tilecount"));
            int columns = Integer.parseInt(tilesetElement.getAttribute("columns"));

            Node image = doc.getElementsByTagName("image").item(0);
            Element imageElement = (Element)image;
            String sourceImage = imageElement.getAttribute("source");
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(getGraphicsStream(sourceImage), false);

            for(int i = 0; i < tiles; i++) {
                int xPos = (i % columns) * tileWidth;
                int yPos = ( i / columns) * tileHeight;
                Bitmap region = decoder.decodeRegion(new Rect( xPos, yPos, xPos+tileWidth, yPos+tileHeight), null);
                this.tiles.put(firstGID+i, region);
            }

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

    private InputStream getGraphicsStream(String graphicsName) {
        try {
            return assetManager.open("levels/spritesheets/" + graphicsName);
        }catch(IOException e2){
            return null;
        }
    }
}
