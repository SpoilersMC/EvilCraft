package evilcraft.api.config.elementtypeaction;

import static evilcraft.api.config.IElementTypeAction.CATEGORIES;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import evilcraft.EvilCraftTab;
import evilcraft.api.config.ExtendedConfig;
import evilcraft.api.config.IElementTypeAction;

public class ItemAction extends IElementTypeAction{

    @Override
    public void run(ExtendedConfig eConfig, Configuration config) {
     // Get property in config file and set comment
        Property property = config.getItem(CATEGORIES.get(eConfig.getHolderType()), eConfig.NAME, eConfig.ID);
        property.comment = eConfig.COMMENT;
        
        // Update the ID, it could've changed
        eConfig.ID = property.getInt();
        
        // Save the config inside the correct element
        eConfig.save();
        
        Item item = (Item) eConfig.getSubInstance();
        
        // Register
        GameRegistry.registerItem(
                item,
                eConfig.getSubUniqueName()
        );
        
        // Set creative tab
        item.setCreativeTab(EvilCraftTab.getInstance());
        
        // Add I18N
        LanguageRegistry.addName(eConfig.getSubInstance(), eConfig.NAME);
    }

}
