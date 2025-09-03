package com.hbm.config;

import com.google.common.base.Optional;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.RecipesCommon;
import com.hbm.main.MainRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FalloutConfigJSON {

    public static final List<FalloutEntry> entries = new ArrayList();
    public static final Gson gson = new Gson();
    public static Random rand = new Random();
    public static HashBiMap<String, Material> matNames = HashBiMap.create();

    static {
        matNames.put("grass", Material.GRASS);
        matNames.put("ground", Material.GROUND);
        matNames.put("wood", Material.WOOD);
        matNames.put("rock", Material.ROCK);
        matNames.put("iron", Material.IRON);
        matNames.put("anvil", Material.ANVIL);
        matNames.put("water", Material.WATER);
        matNames.put("lava", Material.LAVA);
        matNames.put("leaves", Material.LEAVES);
        matNames.put("plants", Material.PLANTS);
        matNames.put("vine", Material.VINE);
        matNames.put("sponge", Material.SPONGE);
        matNames.put("cloth", Material.CLOTH);
        matNames.put("fire", Material.FIRE);
        matNames.put("sand", Material.SAND);
        matNames.put("circuits", Material.CIRCUITS);
        matNames.put("carpet", Material.CARPET);
        matNames.put("redstoneLight", Material.REDSTONE_LIGHT);
        matNames.put("tnt", Material.TNT);
        matNames.put("coral", Material.CORAL);
        matNames.put("ice", Material.ICE);
        matNames.put("packedIce", Material.PACKED_ICE);
        matNames.put("snow", Material.SNOW);
        matNames.put("craftedSnow", Material.CRAFTED_SNOW);
        matNames.put("cactus", Material.CACTUS);
        matNames.put("clay", Material.CLAY);
        matNames.put("gourd", Material.GOURD);
        matNames.put("dragonEgg", Material.DRAGON_EGG);
        matNames.put("portal", Material.PORTAL);
        matNames.put("cake", Material.CAKE);
        matNames.put("web", Material.WEB);
        matNames.put("glass", Material.GLASS);
        matNames.put("piston", Material.PISTON);

    }

    public static void initialize() {
        File folder = MainRegistry.configHbmDir;

        File config = new File(folder.getAbsolutePath() + File.separatorChar + "hbmFallout.json");
        File template = new File(folder.getAbsolutePath() + File.separatorChar + "_hbmFallout.json");

        initDefault();

        if (!config.exists()) {
            writeDefault(template);
        } else {
            List<FalloutEntry> conf = readConfig(config);

            if (conf != null) {
                entries.clear();
                entries.addAll(conf);
            }
        }
    }

    private static void initDefault() {

        double woodEffectRange = 65D;

        /* petrify all wooden things possible */
        entries.add(new FalloutEntry()
                .setBlockState(Blocks.LOG.getDefaultState())
                .primaryStates(new net.minecraft.util.Tuple(ModBlocks.waste_log.getDefaultState(), 1))
                .setMax(woodEffectRange));

        entries.add(new FalloutEntry()
                .setBlockState(Blocks.LOG2.getDefaultState())
                .primaryStates(new net.minecraft.util.Tuple(ModBlocks.waste_log.getDefaultState(), 1))
                .setMax(woodEffectRange));

        entries.add(new FalloutEntry()
                .setBlockState(Blocks.RED_MUSHROOM_BLOCK.getStateFromMeta(10))
                .shouldMatchState(true)
                .primaryStates(new net.minecraft.util.Tuple(ModBlocks.waste_log.getDefaultState(), 1))
                .setMax(woodEffectRange));

        entries.add(new FalloutEntry()
                .setBlockState(Blocks.BROWN_MUSHROOM_BLOCK.getStateFromMeta(10))
                .shouldMatchState(true)
                .primaryStates(new net.minecraft.util.Tuple(ModBlocks.waste_log.getDefaultState(), 1))
                .setMax(woodEffectRange));

        entries.add(new FalloutEntry()
                .setBlockState(Blocks.RED_MUSHROOM_BLOCK.getDefaultState())
                .primaryStates(new net.minecraft.util.Tuple(Blocks.AIR.getDefaultState(), 1))
                .setMax(woodEffectRange));

        entries.add(new FalloutEntry()
                .setBlockState(Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState())
                .primaryStates(new net.minecraft.util.Tuple(Blocks.AIR.getDefaultState(), 1))
                .setMax(woodEffectRange));

        entries.add(new FalloutEntry()
                .setBlockState(Blocks.SNOW.getDefaultState())
                .primaryStates(new net.minecraft.util.Tuple(Blocks.AIR.getDefaultState(), 1))
                .setMax(woodEffectRange));

        entries.add(new FalloutEntry()
                .setBlockState(Blocks.PLANKS.getDefaultState())
                .primaryStates(new net.minecraft.util.Tuple(ModBlocks.waste_planks.getDefaultState(), 1))
                .setMax(woodEffectRange));

        /* if it can't be petrified, destroy it */
        entries.add(new FalloutEntry()
                .setMatchingMaterial(Material.WOOD)
                .primaryStates(new net.minecraft.util.Tuple(Blocks.AIR.getDefaultState(), 1))
                .setMax(woodEffectRange));

        /* destroy all leaves within the radius, kill all leaves outside of it */
        entries.add(new FalloutEntry()
                .setMatchingMaterial(Material.LEAVES)
                .primaryStates(new net.minecraft.util.Tuple(Blocks.AIR.getDefaultState(), 1))
                .setMax(woodEffectRange));

        entries.add(new FalloutEntry()
                .setMatchingMaterial(Material.PLANTS)
                .primaryStates(new net.minecraft.util.Tuple(Blocks.AIR.getDefaultState(), 1))
                .setMax(woodEffectRange));

        entries.add(new FalloutEntry()
                .setMatchingMaterial(Material.VINE)
                .primaryStates(new net.minecraft.util.Tuple(Blocks.AIR.getDefaultState(), 1))
                .setMax(woodEffectRange));

        entries.add(new FalloutEntry()
                .setBlockState(ModBlocks.waste_leaves.getDefaultState())
                .primaryStates(new net.minecraft.util.Tuple(Blocks.AIR.getDefaultState(), 1))
                .setMax(woodEffectRange));

        entries.add(new FalloutEntry()
                .setBlockState(Blocks.LEAVES.getDefaultState())
                .primaryStates(new net.minecraft.util.Tuple(ModBlocks.waste_leaves.getDefaultState(), 1))
                .setMinDistance(woodEffectRange - 5D));

        entries.add(new FalloutEntry()
                .setBlockState(Blocks.LEAVES2.getDefaultState())
                .primaryStates(new net.minecraft.util.Tuple(ModBlocks.waste_leaves.getDefaultState(), 1))
                .setMinDistance(woodEffectRange - 5D));

        entries.add(new FalloutEntry()
                .setBlockState(Blocks.MOSSY_COBBLESTONE.getDefaultState())
                .primaryStates(new net.minecraft.util.Tuple(Blocks.COAL_ORE.getDefaultState(), 1)));

        entries.add(new FalloutEntry()
                .setBlockState(ModBlocks.ore_nether_uranium.getDefaultState())
                .primaryStates(
                        new net.minecraft.util.Tuple(ModBlocks.ore_nether_schrabidium.getDefaultState(), 1),
                        new net.minecraft.util.Tuple(ModBlocks.ore_nether_uranium_scorched.getDefaultState(), 99)
                ));


        for (int i = 1; i <= 10; i++) {
            int m = 10 - i;

            entries.add(new FalloutEntry()
                    .primaryStates(
                            new net.minecraft.util.Tuple(ModBlocks.ore_sellafield_diamond.getStateFromMeta(m), 3),
                            new net.minecraft.util.Tuple(ModBlocks.ore_sellafield_emerald.getStateFromMeta(m), 2))
                    .setPrimaryChance(0.5)
                    .setMax(i * 5)
                    .isSolid(true)
                    .setBlockState(Blocks.COAL_ORE.getDefaultState()));

            entries.add(new FalloutEntry()
                    .primaryStates(new net.minecraft.util.Tuple(ModBlocks.ore_sellafield_diamond.getStateFromMeta(m), 1))
                    .setPrimaryChance(0.2)
                    .setMax(i * 5)
                    .isSolid(true)
                    .setBlockState(ModBlocks.ore_lignite.getDefaultState()));

            entries.add(new FalloutEntry()
                    .primaryStates(new net.minecraft.util.Tuple(ModBlocks.ore_sellafield_emerald.getStateFromMeta(m), 1))
                    .setMax(i * 5)
                    .isSolid(true)
                    .setBlockState(ModBlocks.ore_beryllium.getDefaultState()));

            if (m > 4) {
                entries.add(new FalloutEntry()
                        .primaryStates(
                                new net.minecraft.util.Tuple(ModBlocks.ore_sellafield_schrabidium.getStateFromMeta(m), 1),
                                new net.minecraft.util.Tuple(ModBlocks.ore_sellafield_uranium_scorched.getStateFromMeta(m), 9))
                        .setMax(i * 5)
                        .isSolid(true)
                        .setBlockState(ModBlocks.ore_uranium.getDefaultState()));

                entries.add(new FalloutEntry()
                        .primaryStates(
                                new net.minecraft.util.Tuple(ModBlocks.ore_sellafield_schrabidium.getStateFromMeta(m), 1),
                                new net.minecraft.util.Tuple(ModBlocks.ore_sellafield_uranium_scorched.getStateFromMeta(m), 9))
                        .setMax(i * 5)
                        .isSolid(true)
                        .setBlockState(ModBlocks.ore_gneiss_uranium.getDefaultState()));
            }

            entries.add(new FalloutEntry()
                    .primaryStates(new net.minecraft.util.Tuple(ModBlocks.ore_sellafield_radgem.getStateFromMeta(m), 1))
                    .setMax(i * 5)
                    .isSolid(true)
                    .setBlockState(Blocks.DIAMOND_ORE.getDefaultState()));

            entries.add(new FalloutEntry()
                    .primaryStates(new net.minecraft.util.Tuple(ModBlocks.sellafield_bedrock.getStateFromMeta(m), 1))
                    .setMax(i * 5)
                    .isSolid(true)
                    .setBlockState(Blocks.BEDROCK.getDefaultState()));

            entries.add(new FalloutEntry()
                    .primaryStates(new net.minecraft.util.Tuple(ModBlocks.sellafield_bedrock.getStateFromMeta(m), 1))
                    .setMax(i * 5)
                    .isSolid(true)
                    .setBlockState(ModBlocks.ore_bedrock_block.getDefaultState()));

            entries.add(new FalloutEntry()
                    .primaryStates(new net.minecraft.util.Tuple(ModBlocks.sellafield_bedrock.getStateFromMeta(m), 1))
                    .setMax(i * 5)
                    .isSolid(true)
                    .setBlockState(ModBlocks.ore_bedrock_oil.getDefaultState()));

            entries.add(new FalloutEntry()
                    .primaryStates(new net.minecraft.util.Tuple(ModBlocks.sellafield_bedrock.getStateFromMeta(m), 1))
                    .setMax(i * 5)
                    .isSolid(true)
                    .setBlockState(ModBlocks.sellafield_bedrock.getDefaultState()));

            entries.add(new FalloutEntry()
                    .primaryStates(new net.minecraft.util.Tuple(ModBlocks.sellafield_slaked.getStateFromMeta(m), 1))
                    .setMax(i * 5)
                    .isSolid(true)
                    .setMatchingMaterial(Material.IRON));

            entries.add(new FalloutEntry()
                    .primaryStates(new net.minecraft.util.Tuple(ModBlocks.sellafield_slaked.getStateFromMeta(m), 1))
                    .setMax(i * 5)
                    .isSolid(true)
                    .setMatchingMaterial(Material.ROCK));

            entries.add(new FalloutEntry()
                    .primaryStates(new net.minecraft.util.Tuple(ModBlocks.sellafield_slaked.getStateFromMeta(m), 1))
                    .setMax(i * 5)
                    .isSolid(true)
                    .setMatchingMaterial(Material.SAND));

            entries.add(new FalloutEntry()
                    .primaryStates(new net.minecraft.util.Tuple(ModBlocks.sellafield_slaked.getStateFromMeta(m), 1))
                    .setMax(i * 5)
                    .isSolid(true)
                    .setMatchingMaterial(Material.GROUND));

            if (i <= 9) {
                entries.add(new FalloutEntry()
                        .primaryStates(new net.minecraft.util.Tuple(ModBlocks.sellafield_slaked.getStateFromMeta(m), 1))
                        .setMax(i * 5)
                        .isSolid(true)
                        .setMatchingMaterial(Material.GRASS));
            }
        }


        entries.add(new FalloutEntry()
                .setBlockState(Blocks.MYCELIUM)
                .primaryStates(new net.minecraft.util.Tuple(ModBlocks.waste_mycelium.getDefaultState(),  1)));
        entries.add(new FalloutEntry()
                .setBlockState(Blocks.SAND.getDefaultState())
                .primaryStates(new net.minecraft.util.Tuple(ModBlocks.waste_trinitite.getDefaultState(),  1))
                .setPrimaryChance(0.05));
        entries.add(new FalloutEntry()
                .setBlockState(Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND))
                .primaryStates(new net.minecraft.util.Tuple(ModBlocks.waste_trinitite_red.getDefaultState(),  1))
                .setPrimaryChance(0.05));
        entries.add(new FalloutEntry()
                .setBlockState(Blocks.CLAY)
                .primaryStates(new net.minecraft.util.Tuple(Blocks.HARDENED_CLAY.getDefaultState(),  1)));
    }

    private static void writeDefault(File file) {

        try {
            JsonWriter writer = new JsonWriter(new FileWriter(file));
            writer.setIndent("  ");                    //pretty formatting
            writer.beginObject();                    //initial '{'
            writer.name("entries").beginArray();    //all recipes are stored in an array called "entries"

            for (FalloutEntry entry : entries) {
                writer.beginObject();                //begin object for a single recipe
                entry.write(writer);                //serialize here
                writer.endObject();                    //end recipe object
            }

            writer.endArray();                        //end recipe array
            writer.endObject();                        //final '}'
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<FalloutEntry> readConfig(File config) {

        try {
            JsonObject json = gson.fromJson(new FileReader(config), JsonObject.class);
            JsonArray recipes = json.get("entries").getAsJsonArray();
            List<FalloutEntry> conf = new ArrayList();

            for (JsonElement recipe : recipes) {
                conf.add(FalloutEntry.readEntry(recipe));
            }
            return conf;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static class FalloutEntry {
        private IBlockState blockState = null;
        private Material matchesMaterial = null;
        private boolean matchState = true;
        private boolean matchesOpaque = false;

        //BlockState / Weight
        private net.minecraft.util.Tuple<IBlockState, Integer>[] primaryBlocks = null;
        private net.minecraft.util.Tuple<IBlockState, Integer>[] secondaryBlocks = null;
        private double primaryChance = 1.0D;
        private double minDist = 0.0D;
        private double maxDist = 100.0D;
        private double falloffStart = 0.9D;

        /**
         * Whether the depth value should be decremented when this block is converted
         */
        private boolean isSolid = false;

        private static <T extends Comparable<T>> IBlockState applyProperty(IBlockState state, IProperty<T> prop, Object value) {
            return state.withProperty(prop, (T) value);
        }

        private static String stateToString(IBlockState state) {
            StringBuilder builder = new StringBuilder();
            Block block = state.getBlock();

            builder.append(ForgeRegistries.BLOCKS.getKey(block));
            builder.append("[");

            ImmutableMap<IProperty<?>, Comparable<?>> properties = state.getProperties();
            Iterator<Map.Entry<IProperty<?>, Comparable<?>>> iterator =
                    properties.entrySet().stream()
                            .sorted(Comparator.comparing(e -> e.getKey().getName()))
                            .iterator();

            while (iterator.hasNext()) {
                Map.Entry<IProperty<?>, Comparable<?>> entry = iterator.next();
                builder.append(entry.getKey().getName())
                        .append("=")
                        .append(entry.getValue().toString());
                if (iterator.hasNext()) {
                    builder.append(", ");
                }
            }

            builder.append("]");
            return builder.toString();
        }

        private static FalloutEntry readEntry(JsonElement recipe) {
            FalloutEntry entry = new FalloutEntry();
            if (!recipe.isJsonObject()) return null;

            JsonObject obj = recipe.getAsJsonObject();

            if (obj.has("matchesBlock"))
                entry.setBlockState(entry.parseBlockState(obj.get("matchesBlock").getAsString()));
            if (obj.has("matchesState")) entry.shouldMatchState(obj.get("matchesState").getAsBoolean());
            if (obj.has("mustBeOpaque")) entry.shouldBeOpaque(obj.get("mustBeOpaque").getAsBoolean());
            if (obj.has("matchesMaterial"))
                entry.setMatchingMaterial(matNames.get(obj.get("mustBeOpaque").getAsString()));
            if (obj.has("restrictDepth")) entry.isSolid(obj.get("restrictDepth").getAsBoolean());

            if (obj.has("primarySubstitution")) entry.primaryStates(readMetaArray(obj.get("primarySubstitution")));
            if (obj.has("secondarySubstitutions"))
                entry.secondaryStates(readMetaArray(obj.get("secondarySubstitutions")));

            if (obj.has("chance")) entry.setPrimaryChance(obj.get("chance").getAsDouble());

            if (obj.has("minimumDistancePercent"))
                entry.setMinDistance(obj.get("minimumDistancePercent").getAsDouble());
            if (obj.has("maximumDistancePercent")) entry.setMax(obj.get("maximumDistancePercent").getAsDouble());
            if (obj.has("falloffStartFactor")) entry.setFallOffStart(obj.get("falloffStartFactor").getAsDouble());

            return entry;
        }

        private static void writeStateArray(JsonWriter writer, net.minecraft.util.Tuple<IBlockState, Integer>[] array) throws IOException {
            writer.beginArray();
            writer.setIndent("");

            for (net.minecraft.util.Tuple<IBlockState, Integer> state : array) {
                writer.beginArray();
                writer.value(stateToString(state.getFirst()));
                writer.endArray();
            }

            writer.endArray();
            writer.setIndent("  ");
        }

        private static net.minecraft.util.Tuple<IBlockState, Integer>[] readMetaArray(JsonElement jsonElement) {

            if (!jsonElement.isJsonArray()) return null;

            JsonArray array = jsonElement.getAsJsonArray();
            net.minecraft.util.Tuple<IBlockState, Integer>[] stateArray = new net.minecraft.util.Tuple[array.size()];

            for (int i = 0; i < stateArray.length; i++) {
                JsonElement metaBlock = array.get(i);

                if (!metaBlock.isJsonArray()) {
                    throw new IllegalStateException("Could not read meta block " + metaBlock.toString());
                }

                JsonArray mBArray = metaBlock.getAsJsonArray();

                stateArray[i] = new net.minecraft.util.Tuple(parseBlockState(mBArray.get(0).getAsString()), mBArray.get(1).getAsInt());
            }

            return stateArray;
        }

        private static IBlockState parseBlockState(String input) {
            String blockName;
            String propsPart = null;
            int idx = input.indexOf('[');
            if (idx != -1) {
                blockName = input.substring(0, idx);
                propsPart = input.substring(idx + 1, input.length() - 1);
            } else {
                //Assume no blockstate specified
                blockName = input;
            }

            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
            if (block == null) {
                throw new IllegalArgumentException("Unknown block: " + blockName); //TODO:Graceful handing
            }
            IBlockState state = block.getDefaultState();

            if (propsPart == null || propsPart.isEmpty()) {
                return state;
            }
            String[] props = propsPart.split(",");
            for (String prop : props) {
                String[] kv = prop.split("=");
                if (kv.length != 2) continue;

                String key = kv[0];
                String val = kv[1];

                IProperty<?> property = block.getBlockState().getProperty(key);
                if (property == null) {
                    throw new IllegalArgumentException("Unknown property " + key + " for block " + blockName);
                }

                Optional<?> parsed = property.parseValue(val);
                if (!parsed.isPresent()) {
                    throw new IllegalArgumentException("Invalid value " + val + " for property " + key);
                }

                state = applyProperty(state, property, parsed.get());
            }
            return state;

        }

        public FalloutEntry clone() {
            FalloutEntry entry = new FalloutEntry();
            entry.setBlockState(blockState);
            entry.setMatchingMaterial(matchesMaterial);
            entry.shouldBeOpaque(matchesOpaque);
            entry.primaryStates(primaryBlocks);
            entry.secondaryStates(secondaryBlocks);
            entry.setMinDistance(minDist);
            entry.setMax(maxDist);
            entry.setFallOffStart(falloffStart);
            entry.isSolid(isSolid);

            return entry;
        }

        public FalloutEntry setBlockState(IBlockState block) {
            this.blockState = block;
            return this;
        }
        public FalloutEntry setBlockState(Block block) {
            this.blockState = block.getDefaultState();
            this.matchState = false;
            return this;
        }


        public FalloutEntry setMatchingMaterial(Material mat) {
            this.matchesMaterial = mat;
            return this;
        }

        public FalloutEntry shouldBeOpaque(boolean opaque) {
            this.matchesOpaque = opaque;
            return this;
        }

        public FalloutEntry primaryStates(net.minecraft.util.Tuple<IBlockState, Integer>... blocks) {
            this.primaryBlocks = blocks;
            return this;
        }

        public FalloutEntry secondaryStates(net.minecraft.util.Tuple<IBlockState, Integer>... blocks) {
            this.secondaryBlocks = blocks;
            return this;
        }

        public FalloutEntry setPrimaryChance(double chance) {
            this.primaryChance = chance;
            return this;
        }

        public FalloutEntry setMinDistance(double min) {
            this.minDist = min;
            return this;
        }

        public FalloutEntry setMax(double max) {
            this.maxDist = max;
            return this;
        }

        public FalloutEntry shouldMatchState(boolean matchState) {
            this.matchState = matchState;
            return this;
        }

        public FalloutEntry setFallOffStart(double falloffStart) {
            this.falloffStart = falloffStart;
            return this;
        }

        public FalloutEntry isSolid(boolean solid) {
            this.isSolid = solid;
            return this;
        }

        public boolean eval(World world, BlockPos pos, IBlockState blockState, double dist, IBlockState originalBlockState) {
            if (dist > maxDist || dist < minDist) return false;

            if (this.blockState != null && blockState.getBlock() != this.blockState.getBlock()) return false;
            if (matchesMaterial != null && blockState.getMaterial() != matchesMaterial) return false;
            if (matchState && this.blockState != null && blockState.equals(this.blockState)) return false;
            if (matchesOpaque && !blockState.isOpaqueCube()) return false;

            if (dist > maxDist * falloffStart) {
                double t = (dist - maxDist * falloffStart) / (maxDist - maxDist * falloffStart);
                if (Math.abs(world.rand.nextGaussian()) < t * t * 3.0) {
                    return false;
                }
            }

            RecipesCommon.MetaBlock conversion =
                    chooseRandomOutcome((primaryChance == 1D || rand.nextDouble() < primaryChance) ? primaryBlocks : secondaryBlocks);

            if (conversion == null) return false;

            Block originalBlock = originalBlockState.getBlock();
            int originalMeta = originalBlock.getMetaFromState(originalBlockState);

            if (conversion.block == ModBlocks.sellafield_slaked &&
                    originalBlock == ModBlocks.sellafield_slaked &&
                    conversion.meta <= originalMeta) {
                return false;
            }

            if (conversion.block == ModBlocks.sellafield_bedrock &&
                    originalBlock == ModBlocks.sellafield_bedrock &&
                    conversion.meta <= originalMeta) {
                return false;
            }

            if (originalBlock == ModBlocks.sellafield_bedrock &&
                    conversion.block != ModBlocks.sellafield_bedrock) {
                return false;
            }

            if (pos.getY() == 0 && conversion.block != ModBlocks.sellafield_bedrock) {
                return false;
            }

            IBlockState newState = conversion.block.getStateFromMeta(conversion.meta);
            world.setBlockState(pos, newState, 3);
            return true;
        }

        private RecipesCommon.MetaBlock chooseRandomOutcome(net.minecraft.util.Tuple<IBlockState, Integer>[] blocks) {
            if (blocks == null) return null;

            int weight = 0;

            for (net.minecraft.util.Tuple<IBlockState, Integer> choice : blocks) {
                weight += choice.getSecond();
            }

            int r = rand.nextInt(weight);

            for (net.minecraft.util.Tuple<IBlockState, Integer> choice : blocks) {
                r -= choice.getSecond();

                if (r <= 0) {
                    return new RecipesCommon.MetaBlock(choice.getFirst().getBlock(), choice.getFirst().getBlock().getMetaFromState(choice.getFirst()));
                }
            }

            return new RecipesCommon.MetaBlock(blocks[0].getFirst().getBlock(), blocks[0].getFirst().getBlock().getMetaFromState(blocks[0].getFirst()));
        }

        public boolean isSolid() {
            return this.isSolid;
        }

        public void write(JsonWriter writer) throws IOException {
            if (blockState != null) {
                writer.name("matchesBlock").value(stateToString(blockState));
                writer.name("matchesState").value(matchState);
            }
            if (matchesOpaque) writer.name("mustBeOpaque").value(true);

            if (matchesMaterial != null) {
                String matName = matNames.inverse().get(matchesMaterial);
                if (matName != null) {
                    writer.name("matchesMaterial").value(matName);
                }
            }
            if (isSolid) writer.name("restrictDepth").value(true);

            if (primaryBlocks != null) {
                writer.name("primarySubstitution");
                writeStateArray(writer, primaryBlocks);
            }
            if (secondaryBlocks != null) {
                writer.name("secondarySubstitutions");
                writeStateArray(writer, secondaryBlocks);
            }

            if (primaryChance != 1D) writer.name("chance").value(primaryChance);

            if (minDist != 0.0D) writer.name("minimumDistancePercent").value(minDist);
            if (maxDist != 100.0D) writer.name("maximumDistancePercent").value(maxDist);
            if (falloffStart != 0.9D) writer.name("falloffStartFactor").value(falloffStart);
        }
    }
}
