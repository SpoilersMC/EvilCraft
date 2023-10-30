package evilcraft.block;

import evilcraft.client.render.block.RenderPurifier;
import evilcraft.client.render.tileentity.RenderTileEntityPurifier;
import evilcraft.core.config.extendedconfig.BlockContainerConfig;
import evilcraft.core.helper.MinecraftHelpers;
import evilcraft.proxy.ClientProxy;
import evilcraft.tileentity.TilePurifier;

/**
 * Config for the {@link Purifier}.
 * @author rubensworks
 */
public class PurifierConfig extends BlockContainerConfig {

    /**
     * The unique instance.
     */
    public static PurifierConfig _instance;

    /**
     * Make a new instance.
     */
    public PurifierConfig() {
        super(true, "purifier", null, Purifier.class);
    }

    @Override
    public void onRegistered() {
        if(MinecraftHelpers.isClientSide()) {
            ClientProxy.BLOCK_RENDERERS.add(new RenderPurifier());
            ClientProxy.TILE_ENTITY_RENDERERS.put(TilePurifier.class, new RenderTileEntityPurifier());
        }
    }
}