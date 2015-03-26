package com.qatrend.pomegranate.dataprovider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.composer.ComposerException;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * This class provides several methods to retrieve test data from yaml files. Users can get data returned in an Object
 * 2D array by loading the yaml file with Snakeyaml. If the entire yaml file is not needed then specific data entries
 * can be retrieved by indexes. If the yaml file is formatted to return a LinkedHashMap data type from Snakeyaml, user
 * can get an Object 2D array containing data for select keys or get the entire contents of the yaml file in a Hashtable
 * instead of an Object 2D array.
 * 
 */

public final class YamlDataProvider {

    private static Logger logger = Logger.getLogger(new Exception().getStackTrace()[0].getClassName());

    /**
     * Hiding constructor for class that contains only static methods
     */
    private YamlDataProvider() {
        super();
    }

    /**
     * Converts a yaml file into an Object 2D array for <a
     * href="http://testng.org/doc/documentation-main.html#parameters-dataproviders"> TestNG Dataprovider</a>
     * consumption. User-defined objects can be passed in to be added/mapped into the Snakeyaml constructor. <br>
     * <br>
     * YAML file example: Block List Of Strings
     * 
     * <pre>
     * -US - GB - AU
     * </pre>
     * 
     * Object array returned:
     * 
     * <pre>
     * Object[0][0] = US
     * Object[1][0] = GB
     * Object[2][0] = AU
     * </pre>
     * 
     * Test method signature example:
     * 
     * <pre>
     * public void testExample(String countryCode)
     * </pre>
     * 
     * <br>
     * <br>
     * YAML file example: Inline List Of Strings
     * 
     * <pre>
     * [US, GB, AU]
     * </pre>
     * 
     * Object array returned:
     * 
     * <pre>
     * Object[0][0] = US
     * Object[1][0] = GB
     * Object[2][0] = AU
     * </pre>
     * 
     * Test method signature example:
     * 
     * <pre>
     * public void testExample(String countryCode)
     * </pre>
     * 
     * <br>
     * <br>
     * YAML file example: Block List of Inline Associative Arrays
     * 
     * <pre>
     * - {name: 1, userEmail: user1@paypal.com, userId: 10686626}
     * - {name: 2, email: user2@paypal.com, userId: 10686627}
     * 
     * </pre>
     * 
     * Object array returned (LinkedHashMap):
     * 
     * <pre>
     * Object[0][0] = {name=1, email=user1@paypal.com, userId=10686626}
     * Object[1][0] = {name=2, email=user2@paypal.com, userId=10686627}
     * </pre>
     * 
     * Test method signature example:
     * 
     * <pre>
     * public void testExample(LinkedHashMap&lt;?, ?&gt; test)
     * </pre>
     * 
     * <br>
     * <br>
     * YAML file example: Block Associative Arrays of Associative Arrays
     * 
     * <pre>
     * test1: 
     *     name: 1
     *     email: user1@paypal.com
     *     userId: 10686626
     * test2: 
     *     name: 2
     *     email: user2@paypal.com
     *     userId: 10686627
     * </pre>
     * 
     * Object array returned (contains LinkedHashMap):
     * 
     * <pre>
     * Object[0][0] = {name=1, email=user1@paypal.com, userId=10686626}
     * Object[1][0] = {name=2, email=user2@paypal.com, userId=10686627}
     * </pre>
     * 
     * Test method signature example:
     * 
     * <pre>
     * public void testExample(LinkedHashMap&lt;?, ?&gt; test)
     * </pre>
     * 
     * <br>
     * <br>
     * YAML file example: Document separated Inline Associative Arrays
     * 
     * <pre>
     * ---
     * {name: 1, email: user1@paypal.com, userId: 10686626}
     * ---
     * {name: 2, email: user2@paypal.com, userId: 10686627}
     * </pre>
     * 
     * Object array returned (contains LinkedHashMap):
     * 
     * <pre>
     * Object[0][0] = {name=1, email=user1@paypal.com, userId=10686626}
     * Object[1][0] = {name=2, email=user2@paypal.com, userId=10686627}
     * </pre>
     * 
     * Test method signature example:
     * 
     * <pre>
     * public void testExample(LinkedHashMap&lt;?, ?&gt; test)
     * </pre>
     * 
     * <br>
     * <br>
     * <br>
     * <b>Abstract User-Defined Objects</b> <br>
     * <br>
     * User-defined objects can be passed into this method so the type can be mapped in the Snakeyaml constructor with a
     * new tag. Tag is automatically set to the simple name of the class. If there are multiple objects with the same
     * simple name, then the full path must be used in the yaml file to differentiate between the two. <br>
     * <br>
     * <br>
     * A proper <a href="https://code.google.com/p/snakeyaml/wiki/Documentation#JavaBeans">JavaBean</a> must be defined
     * for the user-defined object or else an exception will be thrown while attempting to load the yaml file. <br>
     * <br>
     * YAML file example: List of MyObject
     * 
     * <pre>
     * - !!com.paypal.test.resources.MyObject
     *     name: 1
     *     email: user1@paypal.com
     *     userId: 10686626
     * - !!com.paypal.test.resources.MyObject
     *     name: 2
     *     email: user2@paypal.com
     *     userId: 10686626
     * </pre>
     * 
     * <br>
     * YAML file example: List of MyObject mapped with tag "MyObject"
     * 
     * <pre>
     * - !MyObject
     *     name: 1
     *     email: user1@paypal.com
     *     userId: 10686626
     * - !MyObject
     *     name: 2
     *     email: user2@paypal.com
     *     userId: 10686626
     * </pre>
     * 
     * Object array returned:
     * 
     * <pre>
     * Object[1][0] = com.paypal.test.dataobject.MyObject@54bb7759
     * Object[2][0] = com.paypal.test.dataobject.MyObject@5f989f84
     * </pre>
     * 
     * Test method signature example:
     * 
     * <pre>
     * public void testExample(MyObject myObject)
     * </pre>
     * 
     * <br>
     * <br>
     * For sample yaml formats, use utility methods:
     * <ul>
     * <li>{@link #serializeObjectToYamlString(Object)}
     * <li>{@link #serializeObjectToYamlStringAsList(Object...)}
     * <li>{@link #serializeObjectToYamlStringAsMap(Object...)}
     * <li>{@link #serializeObjectToYamlStringAsDocuments(Object...)}
     * </ul>
     * <br>
     * <br>
     * 
     * @param resource file with parameters
     * @return Object[][] two dimensional object to be used with TestNG DataProvider
     * @throws IOException in case of errors
     * @throws YamlDataProviderException in case of errors
     */
    public static Object[][] getAllData(FileSystemResource resource) throws IOException, YamlDataProviderException {


        InputStream inputStream = resource.getInputStream();
        Yaml yaml = constructYaml(resource.getCls());

        Object yamlObject = null;

        // Mark the input stream in case multiple documents has been detected
        // so we can reset it.
        inputStream.mark(100);

        try {
            yamlObject = yaml.load(inputStream);
        } catch (ComposerException composerException) {
            if (composerException.getMessage().contains("expected a single document")) {
                inputStream.reset();
                yamlObject = loadDataFromDocuments(yaml, inputStream);
            } else {
                throw new YamlDataProviderException("Error reading YAML data", composerException);
            }
        }

        Object[][] objArray = convertToObjectArray(yamlObject);


        return objArray;
    }

    /**
     * Gets yaml data by key identifiers. Only compatible with a yaml file formatted to return a map. <br>
     * <br>
     * YAML file example:
     * 
     * <pre>
     * test1: 
     *     name: 1
     *     email: user1@paypal.com
     *     userId: 10686626
     * test2: 
     *     name: 2
     *     email: user2@paypal.com
     *     userId: 10686627
     * </pre>
     * 
     * @param resource file with params
     *            - A {@link FileSystemResource} object that represents the yaml resource to be read from.
     * @param keys keys
     *            - A String array that represents the keys.
     * 
     * @return Object[][] two dimensional object to be used with TestNG DataProvider
     */
    public static Object[][] getDataByKeys(FileSystemResource resource, String[] keys) {


        InputStream inputStream = resource.getInputStream();
        Yaml yaml = constructYaml(resource.getCls());

        HashMap<String, Object> requestedMap = new LinkedHashMap<String, Object>();
        LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) yaml.load(inputStream);

        for (String key : keys) {
            Object obj = map.get(key);
            if (obj == null) {
                throw new IllegalArgumentException("Key not found, returned null value: " + key);
            }
            requestedMap.put(key, obj);
        }

        Object[][] objArray = convertToObjectArray(requestedMap);


        return objArray;
    }

    /**
     * Gets yaml data and returns in a hashtable instead of an Object 2D array. Only compatible with a yaml file
     * formatted to return a map. <br>
     * <br>
     * YAML file example:
     * 
     * <pre>
     * test1: 
     *     name: 1
     *     email: user1@paypal.com
     *     userId: 10686626
     * test2: 
     *     name: 2
     *     email: user2@paypal.com
     *     userId: 10686627
     * </pre>
     * 
     * @param resource file with params
     * @return yaml data in form of a Hashtable.
     */
    public static Hashtable<String, Object> getDataAsHashtable(FileSystemResource resource) {


        InputStream inputStream = resource.getInputStream();
        Yaml yaml = constructYaml(resource.getCls());

        Hashtable<String, Object> yamlHashTable = new Hashtable<String, Object>();

        LinkedHashMap<?, ?> yamlObject = (LinkedHashMap<?, ?>) yaml.load(inputStream);

        for (Entry<?, ?> entry : yamlObject.entrySet()) {
            yamlHashTable.put((String) entry.getKey(), entry.getValue());
        }


        return yamlHashTable;
    }

    /**
     * Gets yaml data for requested indexes.
     * 
     * @param resource file with params
     * @param indexes indexes
     * 
     * @return Object[][] Two dimensional object to be used with TestNG DataProvider
     * @throws IOException in case of errors
     * @throws DataProviderException in case of errors
     */
    public static Object[][] getDataByIndex(FileSystemResource resource, String indexes) throws IOException,
            DataProviderException {

        List<Integer> arrayIndex = DataProviderHelper.parseIndexString(indexes);

        Object[][] yamlObj = getAllData(resource);
        Object[][] yamlObjRequested = new Object[arrayIndex.size()][yamlObj[0].length];

        int i = 0;
        for (Integer index : arrayIndex) {
            index--;
            yamlObjRequested[i] = yamlObj[index];
            i++;
        }


        return yamlObjRequested;
    }

    /**
     * Gets yaml data for tests that require multiple arguments. Saves a tester from needing to define another JavaBean
     * just to get multiple arguments passed in as one.
     * 
     * <br>
     * <br>
     * Example dataprovider:
     * 
     * <pre>
     * public static Object[][] dataProviderGetMultipleArguments() throws IOException {
     *     Object[][] data = null;
     *     List&lt;YamlResource&gt; yamlResources = new ArrayList&lt;YamlResource&gt;();
     *     yamlResources.add(new YamlResource(pathName, userDocuments, USER.class));
     *     yamlResources.add(new YamlResource(pathName, user2Documents, USER.class));
     * 
     *     data = new YamlDataProvider().getAllDataMultipleArgs(yamlResources);
     * 
     *     return data;
     * }
     * </pre>
     * 
     * Test method signature example:
     * 
     * <pre>
     * public void testExample(USER user1, USER user2)
     * </pre>
     * 
     * @param resources file with params
     * @return Object[][] Two dimensional object to be used with TestNG DataProvider
     * @throws IOException in case of errors
     * @throws YamlDataProviderException in case of errors
     */
    public static Object[][] getAllDataMultipleArgs(List<FileSystemResource> resources) throws IOException,
            YamlDataProviderException {

        List<Object[][]> dataproviders = new ArrayList<Object[][]>();
        Object[][] data = null;

        for (FileSystemResource r : resources) {
            Object[][] resourceData = getAllData(r);
            dataproviders.add(resourceData);
        }

        int maxLength = 0;
        for (Object[][] d : dataproviders) {
            if (d.length > maxLength) {
                maxLength = d.length;
            }
        }

        data = new Object[maxLength][resources.size()];

        int i = 0;
        for (Object[][] d : dataproviders) {
            for (int j = 0; j < maxLength; j++) {
                try {
                    data[j][i] = d[j][0];
                } catch (ArrayIndexOutOfBoundsException ex) {
                    data[j][i] = null;
                }
            }
            i++;
        }


        return data;
    }

    /**
     * Converts a yaml file into an Object 2D array for <a
     * href="http://testng.org/doc/documentation-main.html#parameters-dataproviders"> TestNG Dataprovider</a>
     * consumption.
     * 
     * <br>
     * A proper <a href="https://code.google.com/p/snakeyaml/wiki/Documentation#JavaBeans">JavaBean</a> must be defined
     * or else an exception will be thrown while attempting to load the yaml file. <br>
     * <br>
     * YAML file example:
     * 
     * <pre>
     * ---
     * MyObject:
     *     name: 1
     *     email: user1@paypal.com
     *     userId: 10686626
     * ---
     * MyObject:
     *     name: 2
     *     email: user2@paypal.com
     *     userId: 10686626
     * </pre>
     * 
     * Object array returned:
     * 
     * <pre>
     * Object[1][0] = com.paypal.test.dataobject.MyObject@54bb7759
     * Object[2][0] = com.paypal.test.dataobject.MyObject@5f989f84
     * </pre>
     * 
     * Test method signature example:
     * 
     * <pre>
     * public void testExample(MyObject myObject)
     * </pre>
     * 
     * @param yaml yaml object
     * @param inputStream input stream
     * @return an List containing multiple yaml documents loaded by SnakeYaml
     */
    private static List<Object> loadDataFromDocuments(Yaml yaml, InputStream inputStream) {

        Iterator<?> documents = yaml.loadAll(inputStream).iterator();
        List<Object> objList = new ArrayList<Object>();

        while (documents.hasNext()) {
            objList.add(documents.next());
        }


        return objList;
    }

    private static Yaml constructYaml(Class<?> cls) {
        if (cls != null) {
            Constructor constructor = new Constructor();
            constructor.addTypeDescription(new TypeDescription(cls, "!" + cls.getSimpleName()));
            return new Yaml(constructor);
        }

        return new Yaml();
    }

    /**
     * 
     * 
     * @param object object
     * @return Object[][] two dimensional object to be used with TestNG DataProvider
     */
    private static Object[][] convertToObjectArray(Object object) {

        Object[][] objArray = new Object[][] { { object } };
        Class<?> rootClass = object.getClass();

        // Convert a LinkedHashMap (Yaml Associative Array) to an Object double array.
        if (rootClass.equals(LinkedHashMap.class)) {
            LinkedHashMap<?, ?> objAsLinkedHashMap = (LinkedHashMap<?, ?>) object;
            Collection<?> allValues = objAsLinkedHashMap.values();
            objArray = new Object[allValues.size()][1];
            int i = 0;
            for (Object eachValue : allValues) {
                objArray[i][0] = eachValue;
                i++;
            }
        }

        // Converts an ArrayList (Yaml List) to an Object double array.
        else if (rootClass.equals(ArrayList.class)) {
            ArrayList<?> objAsArrayList = (ArrayList<?>) object;
            objArray = new Object[objAsArrayList.size()][1];

            int i = 0;
            for (Object eachArrayListObject : objAsArrayList) {

                /*
                 * Yaml list of an associative array will return a LinkedHashMap nested in a LinkedHashMap. Yaml list of
                 * a list will return an ArrayList nested in a LinkedHashMap. This block removes the first mapping since
                 * that data serves as visual organization of data within a yaml. If the parent is a LinkedHashMap and
                 * the child is a LinkedHashMap or an ArrayList, then assign the child to the Object double array
                 * instead of the parent.
                 */
                objArray[i][0] = eachArrayListObject;
                if (eachArrayListObject.getClass().equals(LinkedHashMap.class)) {
                    LinkedHashMap<?, ?> eachArrayListObjectAsHashMap = (LinkedHashMap<?, ?>) eachArrayListObject;
                    for (Object eachEntry : eachArrayListObjectAsHashMap.values()) {
                        if (eachEntry.getClass().equals(LinkedHashMap.class)
                                || eachEntry.getClass().equals(ArrayList.class)) {
                            objArray[i][0] = eachEntry;
                        }
                    }
                }
                i++;
            }
        }


        return objArray;
    }

    /**
     * Use this utility method to print and return a yaml string to help serialize the object passed in.
     * 
     * @param object object
     * @return a yaml string representation of the object passed in
     */
    public static String serializeObjectToYamlString(Object object) {

        Yaml yaml = new Yaml();
        String output = yaml.dump(object);

        return output;
    }

    /**
     * Use this utility method to print and return a yaml string to help serialize the object passed in as an ArrayList.
     * 
     * @param objects objects vararg
     * @return a yaml string representation of the object(s) passed in
     */
    public static String serializeObjectToYamlStringAsList(Object... objects) {

        String output = serializeObjectToYamlString(Arrays.asList(objects).iterator());

        return output;
    }

    /**
     * Use this utility method to print and return a yaml string to help serialize the object passed in as a
     * LinkedHashMap.
     * 
     * @param objects objects vararg
     * @return a yaml string representation of the object(s) passed in
     */
    public static String serializeObjectToYamlStringAsDocuments(Object... objects) {

        Yaml yaml = new Yaml();
        String output = yaml.dumpAll(Arrays.asList(objects).iterator());

        return output;
    }

    /**
     * Use this utility method to print and return a yaml string to help serialize the object passed in as multiple
     * documents.
     * 
     * @param objects objects vararg
     * @return a yaml string representation of the object(s) passed in
     */
    public static String serializeObjectToYamlStringAsMap(Object... objects) {

        HashMap<String, Object> objMap = new LinkedHashMap<String, Object>();

        String key = null;
        int i = 0;
        for (Object obj : objects) {
            key = "uniqueKey" + Integer.toString(i);
            objMap.put(key, obj);
            i++;
        }

        String output = serializeObjectToYamlString(objMap);

        return output;
    }

}