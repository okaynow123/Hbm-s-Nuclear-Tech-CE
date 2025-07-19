package com.hbm.core;

import static com.hbm.core.HbmCorePlugin.runtimeDeobfEnabled;

/**
 * Copied from MekanismCoreTransformer in Mek: CE Unofficial
 * @author sddsd2332
 */
class ObfSafeName {
    public final String mcp, srg;

    public ObfSafeName(String mcp, String srg) {
        this.mcp = mcp;
        this.srg = srg;
    }

    public String getName() {
        return runtimeDeobfEnabled ? srg : mcp;
    }

    public boolean matches(String name) {
        return mcp.equals(name) || srg.equals(name);
    }
}
