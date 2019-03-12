/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package translation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;


public class PropertiesControl2 {

    private Properties props = new Properties();

    private List<String> keyList = new ArrayList<>();
    private List<String> valueList = new ArrayList<>();
    private Map<String, String> kvMap = new HashMap<>();

    public PropertiesControl2(InputStream is) {
        try {
            if (null != is) {
                props.load(is);
                is.close();
                setKeysAndValues();
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCount() {
        return props.size();
    }

    private void setKeysAndValues() {
        for (Entry<Object, Object> entry : props.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            keyList.add(key);
            valueList.add(value);
            kvMap.put(key, value);
        }
    }

    public List<String> getKeyList() {
        return keyList;
    }

    public List<String> getValueList() {
        return valueList;
    }

    public Map<String, String> getKeysAndValuesMap() {
        return kvMap;
    }

    public void write2JarFile(File original, String tempFileName, String configPath, byte[] values) {
        write2JarFile(original, tempFileName, configPath, values, 0);
    }

    public void write2JarFile(File original, String tempFileName, String configPath, byte[] values, int ctrl) {
        String originalPath = original.getAbsolutePath();
        String tempPath;
        if (tempFileName == null) {
            tempPath = originalPath.substring(0, originalPath.lastIndexOf(".")) + "_temp" + originalPath.substring(originalPath.lastIndexOf("."));
        } else {
            tempPath = original.getParent() + File.separator + tempFileName;
        }

        System.out.println(tempPath);
        JarFile originalJar = null;
        try {
            originalJar = new JarFile(originalPath);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        List<JarEntry> lists = new LinkedList<>();
        assert originalJar != null;
        for (Enumeration<JarEntry> entrys = originalJar.entries(); entrys.hasMoreElements(); ) {
            JarEntry jarEntry = entrys.nextElement();
            lists.add(jarEntry);
        }
        File handled = new File(tempPath);
        JarOutputStream jos;
        try {
            FileOutputStream fos = new FileOutputStream(handled);
            jos = new JarOutputStream(fos);
            for (JarEntry je : lists) {
                JarEntry newEntry = new JarEntry(je.getName());

                jos.putNextEntry(newEntry);

                if (je.getName().equals(configPath)) {
                    jos.write(values);
                    continue;
                }

                InputStream is = originalJar.getInputStream(je);
                byte[] bytes = inputStream2byteArray(is);
                is.close();

                jos.write(bytes);
            }
            jos.close();
            fos.close();

            switch (ctrl) {
                case 0:

                    break;
                case 1:
                    new File(originalPath).delete();
                    break;
                case 2:

                    System.out.println(originalPath);
                    copyFile(tempPath, originalPath);
                    handled.delete();
                    break;
                default:

                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public byte[] inputStream2byteArray(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        try {
            while ((i = is.read()) != -1) {
                baos.write(i);
            }
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    private static void copyFile(String oldPath,
                                 String newPath)
            throws Exception {

        int byteread;

        try (FileInputStream inPutStream = new FileInputStream(oldPath); FileOutputStream outPutStream = new FileOutputStream(newPath)) {

            byte[] buffer = new byte[4096];

            while ((byteread = inPutStream.read(buffer)) != -1) {

                outPutStream.write(buffer, 0, byteread);
            }
        }

    }
}
