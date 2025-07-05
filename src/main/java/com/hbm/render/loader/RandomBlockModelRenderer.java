package com.hbm.render.loader;

//TODO: make you work someday
//public class RandomBlockModelRenderer extends BlockRendererDispatcher {
//
//    private final Map<Integer, IBakedModel> bakedModels = new HashMap<>();
//
//
//    public RandomBlockModelRenderer(BlockModelShapes p_i46577_1_, BlockColors p_i46577_2_) {
//        super(p_i46577_1_, p_i46577_2_);
//        loadModels();
//    }
//
//    private void loadModels(){
//        bakedModels.put(0, getModelForState())
//    }
//
//    @Override
//    public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, BufferBuilder bufferBuilderIn){
//
//        long l = (pos.getX() * 3129871L) ^ (long) pos.getY() * 116129781L ^ (long) pos.getZ();
//        l = l * l * 42317861L + l * 11L;
//        int i = (int) (l >> 16 & 3L);
//
//        IBakedModel model = bakedModels.get(Math.abs(i) % TEXTURE_VARIANTS);
//
//        renderModel(model, pos, buffer, packedLight, packedOverlay);
//    }
//}
