package evilcraft.core.recipe.xml;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import evilcraft.Configs;
import evilcraft.EvilCraft;
import evilcraft.Recipes;
import evilcraft.core.config.extendedconfig.ExtendedConfig;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An XML Recipe loader.
 * @author rubensworks
 */
public class XmlRecipeLoader {

    private static final Map<String, IRecipeTypeHandler> RECIPE_TYPE_HANDLERS = Maps.newHashMap();
    static {
        RECIPE_TYPE_HANDLERS.put("shaped", new ShapedRecipeTypeHandler());
        RECIPE_TYPE_HANDLERS.put("shapeless", new ShapelessRecipeTypeHandler());
        RECIPE_TYPE_HANDLERS.put("smelting", new SmeltingRecipeTypeHandler());
        RECIPE_TYPE_HANDLERS.put("evilcraft:bloodinfuser", new BloodInfuserRecipeTypeHandler());
        RECIPE_TYPE_HANDLERS.put("evilcraft:environmentalaccumulator", new EnvironmentalAccumulatorRecipeTypeHandler());
    }

    public static final Map<String, IRecipeConditionHandler> RECIPE_CONDITION_HANDLERS = Maps.newHashMap();
    static {
        RECIPE_CONDITION_HANDLERS.put("config", new ConfigRecipeConditionHandler());
        RECIPE_CONDITION_HANDLERS.put("predefined", new PredefinedRecipeConditionHandler());
        RECIPE_CONDITION_HANDLERS.put("mod", new ModRecipeConditionHandler());
    }

    private static final Map<String, ItemStack> PREDEFINED_ITEMS = Maps.newHashMap();
    private static final Set<String> PREDEFINED_VALUES = Sets.newHashSet();

    private StreamSource stream;
    private String fileName;
    private InputStream xsdIs = null;
    private Document doc = null;

    /**
     * Register a new recipe type handler.
     * @param type The type name.
     * @param handler The handler instance.
     */
    public static void registerRecipeTypeHandler(String type, IRecipeTypeHandler handler) {
        RECIPE_TYPE_HANDLERS.put(type, handler);
    }

    /**
     * Register a new recipe condition handler.
     * @param type The type name.
     * @param handler The handler instance.
     */
    public static void registerRecipeConditionHandler(String type, IRecipeConditionHandler handler) {
        RECIPE_CONDITION_HANDLERS.put(type, handler);
    }

    /**
     * Register a new predefined item.
     * @param key The key of the item.
     * @param item The item.
     */
    public static void registerPredefinedItem(String key, ItemStack item) {
        PREDEFINED_ITEMS.put(key, item);
    }

    /**
     * Get a predefined item by key.
     * @param key The key of the item.
     * @return The item or null.
     */
    public static ItemStack getPredefinedItem(String key) {
        return PREDEFINED_ITEMS.get(key);
    }

    /**
     * Register a new predefined value that can be used in {@link PredefinedRecipeConditionHandler}.
     * @param value The key to register.
     */
    public static void registerPredefinedValue(String value) {
        PREDEFINED_VALUES.add(value);
    }

    /**
     * Check if a value has been predefined.
     * @param value The key to check.
     * @return If it was predefined.
     */
    public static boolean isPredefinedValue(String value) {
        return PREDEFINED_VALUES.contains(value);
    }

    /**
     * Make a new loader for the given file.
     * @param is The stream containing xml recipes.
     * @param fileName The file name, used for debugging.
     */
    public XmlRecipeLoader(InputStream is, String fileName) {
        this.stream = new StreamSource(is);
        this.fileName = fileName;
    }

    /**
     * Set the XSD validator.
     * @param xsdIs The inputstream for the validator.
     */
    public void setValidator(InputStream xsdIs) {
        this.xsdIs = xsdIs;
    }

    /**
     * Validate the xml file.
     * @throws XmlRecipeException If the file was invalid.
     */
    public void validate() throws XmlRecipeException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            if(xsdIs != null) {
                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                factory.setErrorHandler(new ErrorHandler() {
                    @Override
                    public void warning(SAXParseException exception) throws SAXException {
                        EvilCraft.log("[" + fileName + "]: " + exception.getMessage(), Level.WARN);
                    }
                    @Override
                    public void fatalError(SAXParseException exception) throws SAXException {
                        EvilCraft.log("[" + fileName + "]: " + exception.getMessage(), Level.FATAL);
                    }
                    @Override
                    public void error(SAXParseException exception) throws SAXException {
                        EvilCraft.log("[" + fileName + "]: " + exception.getMessage(), Level.ERROR);
                    }
                });
                Schema schema = factory.newSchema(new StreamSource(xsdIs));
                dbFactory.setSchema(schema);
            }
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(stream.getInputStream());
        } catch(SAXException e) {
            throw new XmlRecipeException(e);
        } catch(IOException e) {
            throw new XmlRecipeException(e);
        } catch(ParserConfigurationException e) {
            throw new XmlRecipeException(e);
        }
    }

    /**
     * Load the recipes for this instance.
     * @param crashOnInvalidRecipe If the loader should crash when an invalid recipe has been found. Will skip recipe otherwise.
     * @throws XmlRecipeException If something went wrong.
     */
    public void loadRecipes(boolean crashOnInvalidRecipe) throws XmlRecipeException {
        if(doc == null) {
            validate();
        }

        NodeList recipes = doc.getElementsByTagName("recipe");
        for(int i = 0; i < recipes.getLength(); i++) {
            Element recipe = (Element)recipes.item(i);
            if(isRecipeEnabled(recipe)) {
                try {
                    handleRecipe(recipe);
                } catch(XmlRecipeException e) {
                    if(crashOnInvalidRecipe) {
                        throw e;
                    } else {
                        EvilCraft.log(e.getMessage(), Level.ERROR);
                    }
                }
            }
        }
    }

    private boolean isRecipeEnabled(Element recipe) {
        boolean enable = true;
        NodeList conditions = recipe.getElementsByTagName("condition");
        int j = 0;
        while(j < conditions.getLength() && enable) {
            Node condition = conditions.item(j);
            String conditionType = condition.getAttributes().getNamedItem("type").getTextContent();
            IRecipeConditionHandler handler = RECIPE_CONDITION_HANDLERS.get(conditionType);
            if(handler == null) {
                throw new XmlRecipeException(String.format("Could not find a recipe condition handler of type '%s'", conditionType));
            }
            String param = condition.getTextContent();
            enable = enable && handler.isSatisfied(param);
            j++;
        }
        return enable;
    }

    private void handleRecipe(Element recipe) throws XmlRecipeException {
        String type = recipe.getAttributes().getNamedItem("type").getTextContent();
        IRecipeTypeHandler handler = RECIPE_TYPE_HANDLERS.get(type);
        if(handler == null) {
            throw new XmlRecipeException(String.format("Could not find a recipe type handler of type '%s'", type));
        }
        ItemStack output = handler.loadRecipe(recipe);

        ExtendedConfig<?> config = Configs.getConfigFromItem(output.getItem());
        for(String tag : getTags(recipe)) {
            Recipes.taggedOutput.put(tag, output);
            Recipes.taggedConfigurablesOutput.put(tag, config);
        }
    }

    private List<String> getTags(Element recipe) {
        NodeList tagNodes = recipe.getElementsByTagName("tag");
        List<String> tags = Lists.newArrayListWithCapacity(tagNodes.getLength());
        for(int i = 0; i < tagNodes.getLength(); i++) {
            Element tag = (Element)tagNodes.item(i);
            tags.add(tag.getTextContent());
        }
        return tags;
    }

    /**
     * Error that can occur while reading xml recipes.
     * @author rubensworks
     */
    public static class XmlRecipeException extends RuntimeException {

        /**
         * Make a new instance.
         * @param message The message.
         */
        public XmlRecipeException(String message) {
            super(message);
        }

        /**
         * Make a new instance.
         * @param e The exception with a message.
         */
        public XmlRecipeException(Exception e) {
            super(e.getMessage());
        }
    }
}