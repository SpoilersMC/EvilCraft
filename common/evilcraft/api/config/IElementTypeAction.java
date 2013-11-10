package evilcraft.api.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.Configuration;

/**
 * An action that can be used to register Configurables
 * @author Ruben Taelman
 *
 */
public abstract class IElementTypeAction {
    
    public static Map<ElementType, String> CATEGORIES = new HashMap<ElementType, String>();
    static {
        CATEGORIES.put(ElementType.ITEM, ConfigHandler.CATEGORY_ITEM);
        CATEGORIES.put(ElementType.BLOCK, ConfigHandler.CATEGORY_BLOCK);
        CATEGORIES.put(ElementType.BLOCKCONTAINER, ConfigHandler.CATEGORY_BLOCK);
        CATEGORIES.put(ElementType.MOB, ConfigHandler.CATEGORY_ENTITY);
        CATEGORIES.put(ElementType.ENTITY, ConfigHandler.CATEGORY_ENTITY);
        CATEGORIES.put(ElementType.LIQUID, ConfigHandler.CATEGORY_LIQUID);
    }
    
    public void commonRun(ExtendedConfig eConfig, Configuration config) {
        run(eConfig, config);
        
        // Optional common stuff
    }
    
    public abstract void run(ExtendedConfig eConfig, Configuration config);
}
