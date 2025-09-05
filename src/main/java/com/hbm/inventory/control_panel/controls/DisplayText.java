package com.hbm.inventory.control_panel.controls;

import com.hbm.inventory.control_panel.*;
import com.hbm.main.ResourceManager;
import com.hbm.render.amlfrom1710.IModelCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

import java.util.List;
import java.util.Map;

public class DisplayText extends Control {

    private int scale = 25;
    private float width = 20;
    private float textWidth = 0; //TODO: stop all-too-long text
    private float height = 0;

    public DisplayText(String name, ControlPanel panel) {
        super(name, panel);
        vars.put("isLit", new DataValueFloat(0));
        vars.put("text", new DataValueString("text"));
        vars.put("color", new DataValueEnum<>(EnumDyeColor.WHITE));
        configMap.put("scale", new DataValueFloat(scale));
        configMap.put("width", new DataValueFloat(width));
    }

    @Override
    public ControlType getControlType() {
        return ControlType.DISPLAY;
    }

    @Override
    public float[] getSize() {
        return new float[] {0, 0, 0};
    }

    @Override
    public float[] getBox() {
        float d = .1F;
        return new float[] {posX-d, posY-d, posX + (width*1.5F*scale/500F)+d, posY + (height*scale/500F)+d};
    }

    @Override
    public void applyConfigs(Map<String, DataValue> configs) {
        super.applyConfigs(configs);

        for (Map.Entry<String, DataValue> e : configMap.entrySet()) {
            switch (e.getKey()) {
                case "scale": {
                    scale = (int) e.getValue().getNumber();
                    break;
                }
                case "width": {
                    width = (int) e.getValue().getNumber();
                    break;
                }
            }
        }
    }

    @Override
    public void render() {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;

        String text = getVar("text").toString();
        boolean isLit = getVar("isLit").getBoolean();
        EnumDyeColor dyeColor = getVar("color").getEnum(EnumDyeColor.class);
        int color = dyeColor.getColorValue();

        textWidth = font.getStringWidth(text);
        height = font.FONT_HEIGHT;
        float s = scale / 500F;

        float lX = OpenGlHelper.lastBrightnessX;
        float lY = OpenGlHelper.lastBrightnessY;

        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, 0, posY);
        GlStateManager.translate(0, 0.03F, 0);
        GlStateManager.scale(s, -s, s);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
        GlStateManager.rotate(90, 1, 0, 0);

        if (isLit) OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        GlStateManager.disableLighting();
        font.drawString(text, 0, 0, color, false);
        GlStateManager.enableLighting();
        if (isLit) OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lX, lY);

        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.translate(0, 0, -0.01F);

        GlStateManager.disableTexture2D();

        float[] box = getBox();

        GL11.glColor4f(0F, 0F, 0F, 1F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3f(box[0], box[1], -0.01F);
        GL11.glVertex3f(box[0], box[3], -0.01F);
        GL11.glVertex3f(box[2], box[3], -0.01F);
        GL11.glVertex3f(box[2], box[1], -0.01F);
        GL11.glEnd();
        float d = 0.05F;
        GL11.glColor4f(0.3F, 0.3F, 0.3F, 1F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3f(box[0] - d, box[1] - d, 0F);
        GL11.glVertex3f(box[0] - d, box[3] + d, 0F);
        GL11.glVertex3f(box[2] + d, box[3] + d, 0F);
        GL11.glVertex3f(box[2] + d, box[1] - d, 0F);
        GL11.glEnd();

        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();

        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    @Override
    public IModelCustom getModel() {
        return ResourceManager.ctrl_display_seven_seg;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return ResourceManager.ctrl_display_seven_seg_gui_tex;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public Control newControl(ControlPanel panel) {
        return new DisplayText(name, panel);
    }

    @Override
    public void populateDefaultNodes(List<ControlEvent> receiveEvents) {

    }

}
