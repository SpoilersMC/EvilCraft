package evilcraft.core.client.render.block;

import net.minecraft.client.renderer.texture.TextureAtlasSpriteRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

/**
 * A component to be used in Blocks. Depending on the amount of alternateIcons,
 * this component will make sure that depending on the location of the blockState, different
 * icons will be displayed. Icons must be available in the following format:
 * 'textureNameBase_{0-(alternateIcons-1)}'
 * 
 * To use this the following methods should be called:
 * <ul>
 * <li>Constructor should only be called once in Block.</li>
 * <li>registerIcons should be called from registerIcons in the Block, super call is not needed.</li>
 * <li>getAlternateIcon should be called from getBlockTexture in Block.</li>
 * <li>getBaseIcon should be called from getIcon in Block, since this call is now only used by inventory blocks.</li>
 *  </ul>
 * @author rubensworks
 *
 */
public class AlternatingBlockIconComponent {
    
    private TextureAtlasSprite[] alternateIcons;
    private Random random = new Random();
    
    /**
     * Make a new instance.
     * @param alternateIcons The amount of icons to alternate.
     */
    public AlternatingBlockIconComponent(int alternateIcons) {
        this.alternateIcons =  new TextureAtlasSprite[alternateIcons];
    }
    
    /**
     * Register icons
     * @param textureNameBase The base texture name.
     * @param iconRegister The {@link TextureAtlasSpriteRegister}.
     */
    public void registerIcons(String textureNameBase, TextureAtlasSpriteRegister iconRegister) {
        for(int i = 0; i < getAlternateIcons().length; i++) {
            alternateIcons[i] = iconRegister.registerIcon(textureNameBase + "_" + i);
        }
    }

    /**
     * The array of alternate icons.
     * @return The icon array.
     */
    public TextureAtlasSprite[] getAlternateIcons() {
        return alternateIcons;
    }
    
    /**
     * Get one from the alternate icons depending on the coordinates and side of the blockState.
     * @param world The world.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @param side The side of the blockState that will be rendered.
     * @return The icon to render.
     */
    public TextureAtlasSprite getAlternateIcon(IBlockAccess world, BlockPos blockPos, EnumFacing side) {
        random.setSeed(String.format("%s:%s:%s:%s", x, y, z, side).hashCode());
        int randomIndex = random.nextInt(getAlternateIcons().length);
        return alternateIcons[randomIndex];
    }

    /**
     * Get the first/base icon.
     * @return The base icon.
     */
    public TextureAtlasSprite getBaseIcon() {
        return alternateIcons[0];
    }
}
