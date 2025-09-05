package com.hbm.inventory.control_panel.controls;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.nodes.NodeBoolean;
import com.hbm.inventory.control_panel.nodes.NodeGetVar;
import com.hbm.inventory.control_panel.nodes.NodeSetVar;
import com.hbm.main.ClientProxy;
import com.hbm.main.ResourceManager;
import com.hbm.render.amlfrom1710.IModelCustom;
import com.hbm.render.amlfrom1710.Tessellator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.Collections;
import java.util.List;

public class SwitchRotaryToggle extends Control {

    public SwitchRotaryToggle(String name, ControlPanel panel) {
        super(name, panel);
        vars.put("isOn", new DataValueFloat(0));
    }

    @Override
    public ControlType getControlType() {
        return ControlType.SWITCH;
    }

    @Override
    public float[] getSize() {
        return new float[] {1, 1, .68F};
    }

    @Override
    public void render() {
        boolean isFlipped = getVar("isOn").getBoolean();

        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.ctrl_switch_rotary_toggle_tex);

        IModelCustom model = getModel();

        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, 0F, posY);
        GlStateManager.color(1F, 1F, 1F, 1F);
        model.renderPart("base");
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        if (isFlipped) {
            Matrix4f rot = new Matrix4f().rotate((float) Math.toRadians(-90), new Vector3f(0, 1, 0));
            Matrix4f trans = new Matrix4f().translate(new Vector3f(posX, 0, posY));
            Matrix4f mat = new Matrix4f();
            Matrix4f.mul(trans, rot, mat);
            mat.store(ClientProxy.AUX_GL_BUFFER);
            ClientProxy.AUX_GL_BUFFER.rewind();
            GlStateManager.multMatrix(ClientProxy.AUX_GL_BUFFER);
        } else {
            GlStateManager.translate(posX, 0F, posY);
        }
        GlStateManager.color(1F, 1F, 1F, 1F);
        model.renderPart("lever");
        GlStateManager.popMatrix();

        GlStateManager.shadeModel(GL11.GL_FLAT);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IModelCustom getModel() {
        return ResourceManager.ctrl_switch_rotary_toggle;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getGuiTexture() {
        return ResourceManager.ctrl_switch_rotary_toggle_gui_tex;
    }

    @Override
    public List<String> getOutEvents() {
        return Collections.singletonList("ctrl_press");
    }

    @Override
    public void populateDefaultNodes(List<ControlEvent> receiveEvents) {
        NodeSystem ctrl_press = new NodeSystem(this);
        {
            NodeGetVar node0 = new NodeGetVar(170, 100, this).setData("isOn", false);
            ctrl_press.addNode(node0);
            NodeBoolean node1 = new NodeBoolean(230, 120).setData(NodeBoolean.BoolOperation.NOT);
            node1.inputs.get(0).setData(node0, 0, true);
            ctrl_press.addNode(node1);
            NodeSetVar node2 = new NodeSetVar(290, 140, this).setData("isOn", false);
            node2.inputs.get(0).setData(node1, 0, true);
            ctrl_press.addNode(node2);
        }
        receiveNodeMap.put("ctrl_press", ctrl_press);
    }

    @Override
    public Control newControl(ControlPanel panel) {
        return new SwitchRotaryToggle(name, panel);
    }
}
