package evilcraft.core.helper;

import evilcraft.EvilCraft;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

/**
 * A helper for rendering.
 * @author rubensworks
 */
public class RenderHelpers {

    private static Map<ForgeDirection, String> METHODS_RENDERFACE = new HashMap<ForgeDirection, String>();
    static {
        METHODS_RENDERFACE.put(ForgeDirection.DOWN, "renderFaceYNeg");
        METHODS_RENDERFACE.put(ForgeDirection.UP, "renderFaceYPos");
        METHODS_RENDERFACE.put(ForgeDirection.NORTH, "renderFaceZPos");
        METHODS_RENDERFACE.put(ForgeDirection.EAST, "renderFaceXPos");
        METHODS_RENDERFACE.put(ForgeDirection.SOUTH, "renderFaceZNeg");
        METHODS_RENDERFACE.put(ForgeDirection.WEST, "renderFaceXNeg");
    }
    private static Map<ForgeDirection, String> FIELDS_UVROTATE = new HashMap<ForgeDirection, String>();
    static { // Note: the fields from the RenderBlock are INCORRECT! Very good read: http://greyminecraftcoder.blogspot.be/2013/07/rendering-non-standard-blocks.html
        FIELDS_UVROTATE.put(ForgeDirection.DOWN, "uvRotateBottom");
        FIELDS_UVROTATE.put(ForgeDirection.UP, "uvRotateTop");
        FIELDS_UVROTATE.put(ForgeDirection.NORTH, "uvRotateEast");
        FIELDS_UVROTATE.put(ForgeDirection.EAST, "uvRotateSouth");
        FIELDS_UVROTATE.put(ForgeDirection.SOUTH, "uvRotateWest");
        FIELDS_UVROTATE.put(ForgeDirection.WEST, "uvRotateNorth");
    }
    private static Map<ForgeDirection, String> METHODS_RENDERFACE_OBFUSICATED = new HashMap<ForgeDirection, String>();
    static {
        METHODS_RENDERFACE_OBFUSICATED.put(ForgeDirection.DOWN, "func_147768_a");
        METHODS_RENDERFACE_OBFUSICATED.put(ForgeDirection.UP, "func_147806_b");
        METHODS_RENDERFACE_OBFUSICATED.put(ForgeDirection.NORTH, "func_147734_d");
        METHODS_RENDERFACE_OBFUSICATED.put(ForgeDirection.EAST, "func_147764_f");
        METHODS_RENDERFACE_OBFUSICATED.put(ForgeDirection.SOUTH, "func_147761_c");
        METHODS_RENDERFACE_OBFUSICATED.put(ForgeDirection.WEST, "func_147798_e");
    }
    private static Map<ForgeDirection, String> FIELDS_UVROTATE_OBFUSICATED = new HashMap<ForgeDirection, String>();
    static { // Note: the fields from the RenderBlock are INCORRECT! Very good read: http://greyminecraftcoder.blogspot.be/2013/07/rendering-non-standard-blocks.html
        FIELDS_UVROTATE_OBFUSICATED.put(ForgeDirection.DOWN, "field_147865_v");
        FIELDS_UVROTATE_OBFUSICATED.put(ForgeDirection.UP, "field_147867_u");
        FIELDS_UVROTATE_OBFUSICATED.put(ForgeDirection.NORTH, "field_147875_q");
        FIELDS_UVROTATE_OBFUSICATED.put(ForgeDirection.EAST, "field_147871_s");
        FIELDS_UVROTATE_OBFUSICATED.put(ForgeDirection.SOUTH, "field_147873_r");
        FIELDS_UVROTATE_OBFUSICATED.put(ForgeDirection.WEST, "field_147869_t");
    }
    private static int[] ROTATE_UV_ROTATE = { 0, 1, 3, 2 }; // N, E, S, W -> N, E, W, S

    /**
     * An icon that contains to texture, useful for when you want to render nothing.
     */
    public static IIcon EMPTYICON;

    /**
     * Call the correct face renderer on the renderer depending on the given renderDirection.
     * @param renderDirection direction to call the renderer method for.
     * @param renderer Renderer to call the face renderer on.
     * @param block To be passed to renderer.
     * @param x To be passed to renderer.
     * @param y To be passed to renderer.
     * @param z To be passed to renderer.
     * @param blockIconFromSideAndMetadata To be passed to renderer.
     */
    public static void renderFaceDirection(ForgeDirection renderDirection, RenderBlocks renderer, Block block, double x, double y, double z, IIcon blockIconFromSideAndMetadata) {
        switch(renderDirection) {
        case DOWN: renderer.renderFaceYNeg(block, x, y, z, blockIconFromSideAndMetadata); break;
        case UP: renderer.renderFaceYPos(block, x, y, z, blockIconFromSideAndMetadata); break;
        case NORTH: renderer.renderFaceZPos(block, x, y, z, blockIconFromSideAndMetadata);  break;
        case EAST: renderer.renderFaceXPos(block, x, y, z, blockIconFromSideAndMetadata); break;
        case SOUTH: renderer.renderFaceZNeg(block, x, y, z, blockIconFromSideAndMetadata); break;
        case WEST: renderer.renderFaceXNeg(block, x, y, z, blockIconFromSideAndMetadata); break;
        default:
            EvilCraft.log("Rendering Failure- Invalid ForgeDirection: " + renderDirection.toString(), Level.ERROR);
            break;
        }
    }

    /**
     * Set the correct rotation of the given renderer given a {@link ForgeDirection}.
     * It will use reflection to set the correct field in the {@link RenderBlocks}.
     * @param renderer The renderer to set the rotation at.
     * @param side The {@link ForgeDirection} to set a rotation for.
     * @param rotation The rotation to set.
     * @see RenderBlocks
     */
    public static void setRenderBlocksUVRotation(RenderBlocks renderer, ForgeDirection side, int rotation) {
        switch(side) {
        case DOWN: renderer.uvRotateBottom = ROTATE_UV_ROTATE[rotation]; break;
        case UP: renderer.uvRotateTop = ROTATE_UV_ROTATE[rotation]; break;
        case NORTH: renderer.uvRotateEast = ROTATE_UV_ROTATE[rotation]; break;
        case EAST: renderer.uvRotateSouth = ROTATE_UV_ROTATE[rotation]; break;
        case SOUTH: renderer.uvRotateWest = ROTATE_UV_ROTATE[rotation]; break;
        case WEST: renderer.uvRotateNorth = ROTATE_UV_ROTATE[rotation]; break;
        default:
            EvilCraft.log("Rendering Failure- Invalid ForgeDirection: " + side.toString(), Level.ERROR);
            break;
        }
    }

    /**
     * Bind a texture to the rendering engine.
     * @param texture The texture to bind.
     */
    public static void bindTexture(ResourceLocation texture) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
    }

    /**
     * Convert r, g and b colors to an integer representation.
     * @param r red
     * @param g green
     * @param b blue
     * @return integer representation of the color.
     */
    public static int RGBToInt(int r, int g, int b) {
        return (int)r << 16 | (int)g << 8 | (int)b;
    }

    /**
     * Convert a color in integer representation to separated r, g and b colors.
     * @param color The color in integer representation.
     * @return The separated r, g and b colors.
     */
    public static Triple<Float, Float, Float> intToRGB(int color) {
        float red, green, blue;
        red = (float)(color >> 16 & 255) / 255.0F;
        green = (float)(color >> 8 & 255) / 255.0F;
        blue = (float)(color & 255) / 255.0F;
        // this.alpha = (float)(color >> 24 & 255) / 255.0F;
        return Triple.of(red, green, blue);
    }

    /**
     * Inverts the block bounds for the given block inside the given renderer.
     * Useful for rendering the inverted sides of a block (which are normally not rendered) after the normal sides.
     * @param renderer The renderer.
     * @param block The block.
     */
    public static void setInvertedRenderBounds(RenderBlocks renderer, Block block) {
        block.setBlockBoundsForItemRender();
        if(!renderer.lockBlockBounds) { // Code based on RenderBlocks#setRenderBoundsFromBlock
            renderer.renderMinX = block.getBlockBoundsMaxX();
            renderer.renderMaxX = block.getBlockBoundsMinX();
            renderer.renderMinY = block.getBlockBoundsMaxY();
            renderer.renderMaxY = block.getBlockBoundsMinY();
            renderer.renderMinZ = block.getBlockBoundsMaxZ();
            renderer.renderMaxZ = block.getBlockBoundsMinZ();
            renderer.partialRenderBounds = renderer.minecraftRB.gameSettings.ambientOcclusion >= 2
                    && (renderer.renderMinX > 0.0D || renderer.renderMaxX < 1.0D
                    || renderer.renderMinY > 0.0D || renderer.renderMaxY < 1.0D
                    || renderer.renderMinZ > 0.0D || renderer.renderMaxZ < 1.0D);
        }
    }

    /**
     * Get the icon of a fluid for a side in a safe way.
     * @param fluid The fluid stack.
     * @param side The side to get the icon from, UP if null.
     * @return The icon.
     */
    public static IIcon getFluidIcon(FluidStack fluid, ForgeDirection side) {
        Block defaultBlock = Blocks.water;
        Block block = defaultBlock;
        if(fluid.getFluid().getBlock() != null) {
            block = fluid.getFluid().getBlock();
        }
        if(side == null) side = ForgeDirection.UP;

        IIcon icon = fluid.getFluid().getFlowingIcon();
        if(icon == null || (side == ForgeDirection.UP || side == ForgeDirection.DOWN)) {
            icon = fluid.getFluid().getStillIcon();
        }
        if(icon == null) {
            icon = block.getIcon(side.ordinal(), 0);
            if(icon == null) {
                icon = defaultBlock.getIcon(side.ordinal(), 0);
            }
        }
        return icon;
    }

    /**
     * Prepare a GL context for rendering fluids with alpha rendering enabled.
     * @param fluid The fluid stack.
     * @param x X
     * @param y Y
     * @param z Z
     * @param render The actual fluid renderer.
     */
    public static void renderFluidContext(FluidStack fluid, double x, double y, double z, IFluidContextRender render) {
        renderFluidContext(fluid, x, y, z, true, render);
    }

    /**
     * Prepare a GL context for rendering fluids.
     * @param fluid The fluid stack.
     * @param x X
     * @param y Y
     * @param z Z
     * @param enableAlpha If alpha rendering should be enabled.
     * @param render The actual fluid renderer.
     */
    public static void renderFluidContext(FluidStack fluid, double x, double y, double z, boolean enableAlpha, IFluidContextRender render) {
        if(fluid != null && fluid.amount > 0) {
            GL11.glPushMatrix();

            // Make sure both sides are rendered
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_CULL_FACE);

            // Correct color & lighting
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glDisable(GL11.GL_LIGHTING);
            if(enableAlpha) {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            // Set to current relative player location
            GL11.glTranslated(x, y, z);

            // Set block textures
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

            render.renderFluid(fluid);

            // GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            // GL11.glDepthMask(false);
            GL11.glPopMatrix();
        }
    }

    /**
     * Prepare a GL context for rendering fluids for tile entities with alpha rendering enabled.
     * @param fluid The fluid stack.
     * @param x X
     * @param y Y
     * @param z Z
     * @param tile The tile.
     * @param render The actual fluid renderer.
     */
    public static void renderTileFluidContext(final FluidStack fluid, final double x, final double y, final double z, final TileEntity tile, final IFluidContextRender render) {
        renderTileFluidContext(fluid, x, y, z, true, tile, render);
    }

    /**
     * Prepare a GL context for rendering fluids for tile entities.
     * @param fluid The fluid stack.
     * @param x X
     * @param y Y
     * @param z Z
     * @param enableAlpha If alpha rendering should be enabled.
     * @param tile The tile.
     * @param render The actual fluid renderer.
     */
    public static void renderTileFluidContext(final FluidStack fluid, final double x, final double y, final double z, boolean enableAlpha, final TileEntity tile, final IFluidContextRender render) {
        renderFluidContext(fluid, x, y, z, enableAlpha, new IFluidContextRender() {

            @Override
            public void renderFluid(FluidStack fluid) {
                // Make sure our lighting is correct, otherwise everything will be black -_-
                Block block = tile.getWorldObj().getBlock((int)x, (int)y, (int)z);
                Tessellator.instance.setBrightness(2 * block.getMixedBrightnessForBlock(tile.getWorldObj(), (int)x, (int)y, (int)z));

                // Call the actual render.
                render.renderFluid(fluid);
            }
        });
    }

    /**
     * Runnable for {@link RenderHelpers#renderFluidContext(FluidStack, double, double, double, boolean, IFluidContextRender)}.
     * @author rubensworks
     */
    public static interface IFluidContextRender {

        /**
         * Render the fluid.
         * @param fluid The fluid stack.
         */
        public void renderFluid(FluidStack fluid);
    }
}