package evilcraft;

/**
 * Class that can hold basic static things that are better not hard-coded like mod details, texture paths, ID's...
 * @author rubensworks
 */
public class Reference {

    // Mod info
    public static final String MOD_ID = "evilcraft";
    public static final String MOD_NAME = "EvilCraft";
    public static final String MOD_VERSION = "@VERSION@";
    public static final String MOD_BUILD_NUMBER = "@BUILD_NUMBER@";
    public static final String MOD_CHANNEL = MOD_ID;
    public static final String MOD_MC_VERSION = "@MC_VERSION@";

    // IMC keys
    public static final String IMC_BLACKLIST_VENGEANCESPIRIT = "blacklistVengeanceSpirit";
    public static final String IMC_OVERRIDE_SPIRITFURNACE_DROPS = "overrideSpiritFurnaceDrops";

    // Paths
    public static final String TEXTURE_PATH_GUI = "textures/gui/";
    public static final String TEXTURE_PATH_SKINS = "textures/skins/";
    public static final String TEXTURE_PATH_MODELS = "textures/models/";
    public static final String TEXTURE_PATH_ENTITIES = "textures/entities/";
    public static final String TEXTURE_PATH_GUIBACKGROUNDS = "textures/gui/title/background/";
    public static final String TEXTURE_PATH_ITEMS = "textures/items/";
    public static final String TEXTURE_PATH_PARTICLES = "textures/particles/";
    public static final String MODEL_PATH = "models/";

    // External locations
    public static final String URL_VERSIONSTATS = "http://rubensworks.net/evilcraft-versionstats/";

    // Enchantment ID's
    public static final int ENCHANTMENT_BREAKING = 101;
    public static final int ENCHANTMENT_LIFESTEALING = 102;
    public static final int ENCHANTMENT_UNUSING = 103;
    public static final int ENCHANTMENT_POISON_TIP = 104;

    // Potion effect ID's
    public static final int POTION_PALING = 121;

    // Biome ID's
    public static final int BIOME_DEGRADED = 105;

    // Villager ID's
    public static final int VILLAGER_WEREWOLF = 66666;

    // OREDICT NAMES
    public static final String DICT_MATERIALPOISONOUS = "materialPoisonous";
    public static final String DICT_BLOCKGLASS = "blockGlass";
    public static final String DICT_MATERIALLEATHER = "materialLeather";
    public static final String DICT_MATERIALBONE = "materialBone";
    public static final String DICT_COBBLESTONE = "cobblestone";
    public static final String DICT_BLOCKSTONE = "blockStone";
    public static final String DICT_OREDARK = "oreDark";
    public static final String DICT_WOODPLANK = "plankWood";
    public static final String DICT_WOODLOG = "logWood";
    public static final String DICT_WOODSTICK = "stickWood";
    public static final String DICT_SAPLINGTREE = "treeSapling";
    public static final String DICT_GEMDARK = "gemDark";
    public static final String DICT_DYERED = "dyeRed";
    public static final String DICT_GEMDARKCRUSHED = "gemDarkCrushed";
    public static final String DICT_MATERIALSPIKE = "materialSpike";
    public static final String DICT_ITEMSKULL = "itemSkull";
    public static final String DICT_DUSTDULL = "dustDull";
    public static final String DICT_FLESH = "materialFlesh";

    // MOD ID's
    public static final String MOD_FORGE = "Forge";
    public static final String MOD_THERMALEXPANSION = "ThermalExpansion";
    public static final String MOD_WAILA = "Waila";
    public static final String MOD_FMP = "ForgeMultipart";
    public static final String MOD_FORESTRY = "Forestry";
    public static final String MOD_TCONSTRUCT = "TConstruct";
    public static final String MOD_VERSION_CHECKER = "VersionChecker";
    public static final String MOD_BAUBLES = "Baubles";
    public static final String MOD_NEI = "NotEnoughItems";
    public static final String MOD_BLOODMAGIC = "AWWayofTime";
    public static final String MOD_THAUMCRAFT = "Thaumcraft";
    public static final String MOD_EE3 = "EE3";
    public static final String MOD_IC2 = "IC2";

    // Dependencies
    public static final String MOD_DEPENDENCIES = "required-after:Forge@[10.13.3.1360,);" // See the Forge changelog
            + "after:" + Reference.MOD_THERMALEXPANSION;
}