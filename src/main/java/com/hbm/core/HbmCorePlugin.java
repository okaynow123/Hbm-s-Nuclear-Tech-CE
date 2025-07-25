package com.hbm.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({"com.hbm.core"})
@IFMLLoadingPlugin.SortingIndex(2077) // mlbv: this shit must be greater than 1000, after the srg transformer
public class HbmCorePlugin implements IFMLLoadingPlugin {

    static final Logger coreLogger = LogManager.getLogger("HBM CoreMod");
    static boolean runtimeDeobfEnabled = false;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"com.hbm.core.GlStateManagerTransformer", "com.hbm.core.EntityPlayerMPTransformer", "com.hbm.core" +
				".InventoryPlayerTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return "com.hbm.core.HbmCoreModContainer";
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        runtimeDeobfEnabled = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
