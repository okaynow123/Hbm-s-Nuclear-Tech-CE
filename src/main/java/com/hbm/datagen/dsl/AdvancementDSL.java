package com.hbm.datagen.dsl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.FrameType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

public final class AdvancementDSL {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private AdvancementDSL() {
    }

    public static Batch batch(String namespace) {
        return new Batch(namespace);
    }

    public static Display display() {
        return new Display();
    }

    /**
     * create a fluent Builder for an advancement located at the given idPath
     */
    public static Builder builder(String idPath) {
        return new Builder(idPath);
    }

    /**
     * Build a completely custom criterion JSON with a writer lambda.
     */
    public static JsonObject customCriterion(String name, Consumer<JsonObject> writer) {
        JsonObject c = new JsonObject();
        writer.accept(c);
        return c;
    }

    /**
     * Quickly build a map for {@link Templates#anyOf}.
     */
    public static Map<String, JsonObject> criteria(Object... nameThenCriterionObjects) {
        if (nameThenCriterionObjects.length % 2 != 0) throw new IllegalArgumentException("criteria() expects name, JsonObject pairs");
        Map<String, JsonObject> map = new LinkedHashMap<>();
        for (int i = 0; i < nameThenCriterionObjects.length; i += 2) {
            String name = (String) nameThenCriterionObjects[i];
            JsonObject crit = (JsonObject) nameThenCriterionObjects[i + 1];
            map.put(name, crit);
        }
        return map;
    }

    public static JsonObject criterion(String trigger, @Nullable Consumer<JsonObject> conditionsWriter) {
        JsonObject obj = new JsonObject();
        obj.addProperty("trigger", trigger);
        if (conditionsWriter != null) {
            JsonObject cond = new JsonObject();
            conditionsWriter.accept(cond);
            obj.add("conditions", cond);
        }
        return obj;
    }

    static Path detectProjectRoot() {
        Path p = Paths.get("").toAbsolutePath();
        if (p.getFileName() != null && p.getFileName().toString().equalsIgnoreCase("run")) p = p.getParent();
        while (p != null) {
            if (Files.exists(p.resolve("build.gradle")) || Files.exists(p.resolve("build.gradle.kts")) || Files.exists(p.resolve(".git"))) {
                return p;
            }
            p = p.getParent();
        }
        return Paths.get("").toAbsolutePath();
    }

    public static final class Batch {
        private final String namespace;
        private final List<Entry> entries = new ArrayList<>();

        Batch(String namespace) {
            this.namespace = namespace;
        }

        public Batch add(Entry e) {
            entries.add(e);
            return this;
        }

        public Batch add(Entry... arr) {
            entries.addAll(Arrays.asList(arr));
            return this;
        }

        public void writeAll(Path resourcesRoot) {
            Path base = resourcesRoot.resolve("assets").resolve(namespace).resolve("advancements");
            for (Entry e : entries) {
                Path out = base.resolve(e.path + ".json");
                try {
                    Files.createDirectories(out.getParent());
                    try (OutputStreamWriter w = new OutputStreamWriter(Files.newOutputStream(out), StandardCharsets.UTF_8)) {
                        GSON.toJson(e.json, w);
                    }
                } catch (IOException io) {
                    throw new RuntimeException("Failed writing advancement JSON: " + out, io);
                }
            }
        }

        public Path writeAllToProject(Path projectRoot) {
            Path res = projectRoot.resolve("src/main/resources");
            writeAll(res);
            return res.resolve("assets").resolve(namespace).resolve("advancements");
        }

        public Path writeAllSmart() {
            Path projectRoot = AdvancementDSL.detectProjectRoot();
            Path out = writeAllToProject(projectRoot);
            System.out.println("[AdvancementDSL] Wrote advancements to: " + out.toAbsolutePath());
            return out;
        }
    }

    public static final class Entry {
        final String path;
        final JsonObject json;

        Entry(String path, JsonObject json) {
            this.path = path;
            this.json = json;
        }
    }

    public static final class Templates {
        @Contract("_, _, _, _ -> new")
        public static @NotNull Entry impossible(String idPath, @Nullable String parentRL, Display display, String criterionName) {
            JsonObject json = base(parentRL, display);
            JsonObject criteria = new JsonObject();
            criteria.add(criterionName, criterion(Triggers.IMPOSSIBLE, null));
            json.add("criteria", criteria);
            json.add("requirements", reqAll(criterionName));
            return new Entry(idPath, json);
        }

        /**
         * Obtain one of the given items via inventory_changed.
         */
        @Contract("_, _, _, _, _ -> new")
        public static @NotNull Entry obtainAnyItem(String idPath, @Nullable String parentRL, Display display, String criterionName, Item... items) {
            JsonObject json = base(parentRL, display);
            JsonObject conditions = new JsonObject();
            JsonArray arr = new JsonArray();
            for (Item it : items) arr.add(itemPredicate(it));
            conditions.add("items", arr);
            JsonObject criteria = new JsonObject();
            criteria.add(criterionName, criterion(Triggers.INVENTORY_CHANGED, x -> x.add("items", arr)));
            json.add("criteria", criteria);
            json.add("requirements", reqAll(criterionName));
            return new Entry(idPath, json);
        }

        @Contract("_, _, _, _, _ -> new")
        public static @NotNull Entry obtainAnyItemStack(String idPath, @Nullable String parentRL, Display display, String criterionName,
                                                        ItemStack... stacks) {
            JsonObject json = base(parentRL, display);

            JsonArray items = new JsonArray();
            for (ItemStack s : stacks) {
                if (s == null || s.isEmpty()) continue;
                items.add(itemPredicate(s));
            }

            JsonObject criteria = new JsonObject();
            criteria.add(criterionName, criterion(Triggers.INVENTORY_CHANGED, cond -> cond.add("items", items)));
            json.add("criteria", criteria);
            json.add("requirements", reqAll(criterionName));
            return new Entry(idPath, json);
        }

        /**
         * Kill an entity of type RL (e.g. minecraft:creeper).
         */
        @Contract("_, _, _, _, _ -> new")
        public static @NotNull Entry killEntity(String idPath, @Nullable String parentRL, Display display, String criterionName, String entityRL) {
            JsonObject json = base(parentRL, display);
            JsonObject ent = new JsonObject();
            ent.addProperty("type", entityRL);
            JsonObject criteria = new JsonObject();
            criteria.add(criterionName, criterion(Triggers.PLAYER_KILLED_ENTITY, c -> c.add("entity", ent)));
            json.add("criteria", criteria);
            json.add("requirements", reqAll(criterionName));
            return new Entry(idPath, json);
        }

        /**
         * Enter a dimension by id (e.g. 0, -1, 1).
         */
        @Contract("_, _, _, _, _ -> new")
        public static @NotNull Entry enterDimension(String idPath, @Nullable String parentRL, Display display, String criterionName, int dim) {
            JsonObject json = base(parentRL, display);
            JsonObject criteria = new JsonObject();
            criteria.add(criterionName, criterion(Triggers.CHANGED_DIMENSION, c -> c.addProperty("dimension", dim)));
            json.add("criteria", criteria);
            json.add("requirements", reqAll(criterionName));
            return new Entry(idPath, json);
        }

        /**
         * Enter block trigger.
         */
        @Contract("_, _, _, _, _ -> new")
        public static @NotNull Entry enterBlock(String idPath, @Nullable String parentRL, Display display, String criterionName, String blockRL) {
            JsonObject json = base(parentRL, display);
            JsonObject criteria = new JsonObject();
            criteria.add(criterionName, criterion(Triggers.ENTER_BLOCK, c -> c.addProperty("block", blockRL)));
            json.add("criteria", criteria);
            json.add("requirements", reqAll(criterionName));
            return new Entry(idPath, json);
        }

        /**
         * Build a JSON with multiple named criteria and OR requirements (any one).
         */
        @Contract("_, _, _, _ -> new")
        public static @NotNull Entry anyOf(String idPath, @Nullable String parentRL, Display display, Map<String, JsonObject> namedCriteria) {
            JsonObject json = base(parentRL, display);
            JsonObject crit = new JsonObject();
            for (Map.Entry<String, JsonObject> e : namedCriteria.entrySet()) crit.add(e.getKey(), e.getValue());
            json.add("criteria", crit);
            json.add("requirements", reqAny(namedCriteria.keySet()));
            return new Entry(idPath, json);
        }

        private static JsonObject base(@Nullable String parentRL, Display display) {
            JsonObject json = new JsonObject();
            if (parentRL != null) json.addProperty("parent", parentRL);
            json.add("display", display.toJson());
            return json;
        }

        private static JsonArray reqAll(String... names) {
            JsonArray outer = new JsonArray();
            JsonArray inner = new JsonArray();
            for (String n : names) inner.add(n);
            if (inner.size() == 0) inner.add("__missing");
            outer.add(inner);
            return outer;
        }

        private static JsonArray reqAny(Collection<String> names) {
            JsonArray outer = new JsonArray();
            for (String n : names) {
                JsonArray inner = new JsonArray();
                inner.add(n);
                outer.add(inner);
            }
            if (outer.size() == 0) outer.add(reqAll("__missing").get(0));
            return outer;
        }

        private static JsonObject itemPredicate(Item item) {
            ResourceLocation rl = GameRegistry.findRegistry(Item.class).getKey(item);
            JsonObject obj = new JsonObject();
            obj.addProperty("item", rl == null ? "minecraft:stone" : rl.toString());
            return obj;
        }

        private static JsonObject itemPredicate(ItemStack stack) {
            ResourceLocation rl = GameRegistry.findRegistry(Item.class).getKey(stack.getItem());
            JsonObject obj = new JsonObject();
            obj.addProperty("item", rl == null ? "minecraft:stone" : rl.toString());
            int meta = stack.getMetadata();
            if (meta != 0) obj.addProperty("data", meta);
            if (stack.hasTagCompound() && stack.getTagCompound() != null && !stack.getTagCompound().isEmpty()) {
                obj.addProperty("nbt", stack.getTagCompound().toString());
            }
            return obj;
        }
    }

    public static final class Display {
        private Icon icon;
        private TitleDesc title;
        private TitleDesc desc;
        private FrameType frame = FrameType.TASK;
        private boolean showToast = true;
        private boolean announce = true;
        private boolean hidden = false;
        private String background;

        public Display icon(Item item) {
            this.icon = Icon.of(item);
            return this;
        }

        public Display icon(ItemStack stack) {
            this.icon = Icon.of(stack);
            return this;
        }

        public Display iconRL(String itemRL) {
            this.icon = Icon.of(itemRL);
            return this;
        }

        public Display key(String key) {
            this.title = TitleDesc.key("achievement." + key);
            this.desc = TitleDesc.key("achievement." + key + ".desc");
            return this;
        }

        public Display titleKey(String key) {
            this.title = TitleDesc.key(key);
            return this;
        }

        public Display titleLiteral(String text) {
            this.title = TitleDesc.literal(text);
            return this;
        }

        public Display descKey(String key) {
            this.desc = TitleDesc.key(key);
            return this;
        }

        public Display descLiteral(String text) {
            this.desc = TitleDesc.literal(text);
            return this;
        }

        public Display frame(FrameType t) {
            this.frame = t;
            return this;
        }

        public Display toast(boolean b) {
            this.showToast = b;
            return this;
        }

        public Display announce(boolean b) {
            this.announce = b;
            return this;
        }

        public Display hidden(boolean b) {
            this.hidden = b;
            return this;
        }

        public Display background(String texRL) {
            this.background = texRL;
            return this;
        }

        JsonObject toJson() {
            if (icon == null) throw new IllegalStateException("Display.icon not set");
            if (title == null) throw new IllegalStateException("Display.title not set");
            if (desc == null) throw new IllegalStateException("Display.description not set");
            JsonObject obj = new JsonObject();
            obj.add("icon", icon.toJson());
            obj.add("title", title.toJson());
            obj.add("description", desc.toJson());
            obj.addProperty("frame", frame.name().toLowerCase(Locale.ROOT));
            obj.addProperty("show_toast", showToast);
            obj.addProperty("announce_to_chat", announce);
            obj.addProperty("hidden", hidden);
            if (background != null) obj.addProperty("background", background);
            return obj;
        }
    }

    public static final class Icon {
        private String itemRL;
        private int meta = 0; // include if != 0

        public static Icon of(ItemStack stack) {
            Item item = stack.getItem();
            Icon i = of(item);
            i.meta = stack.getMetadata();
            return i;
        }

        public static Icon of(Item item) {
            ResourceLocation rl = GameRegistry.findRegistry(Item.class).getKey(item);
            Icon i = new Icon();
            i.itemRL = rl == null ? "minecraft:stone" : rl.toString();
            return i;
        }

        public static Icon of(String itemRL) {
            Icon i = new Icon();
            i.itemRL = itemRL;
            return i;
        }

        JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("item", itemRL);
            if (meta != 0) obj.addProperty("data", meta);
            return obj;
        }
    }

    public static final class TitleDesc {
        private final Kind kind;
        private final String value;

        private TitleDesc(Kind k, String v) {
            this.kind = k;
            this.value = v;
        }

        public static TitleDesc key(String key) {
            return new TitleDesc(Kind.KEY, key);
        }

        public static TitleDesc literal(String text) {
            return new TitleDesc(Kind.LITERAL, text);
        }

        JsonObject toJson() {
            JsonObject obj = new JsonObject();
            if (kind == Kind.KEY) obj.addProperty("translate", value);
            else obj.addProperty("text", value);
            return obj;
        }

        private enum Kind {
            KEY,
            LITERAL
        }
    }

    public static final class Builder {
        private final String idPath;
        private final LinkedHashMap<String, JsonObject> criteria = new LinkedHashMap<>();
        private @Nullable String parentRL;
        private @Nullable Display display;
        private @Nullable Rewards rewards;
        private @Nullable List<List<String>> requirements;

        public Builder(String idPath) {
            if (idPath == null || idPath.isEmpty()) throw new IllegalArgumentException("idPath must not be empty");
            this.idPath = idPath;
        }

        /**
         * Set "parent": full RL string ("hbm:root").
         */
        public Builder parent(@Nullable String parentRL) {
            this.parentRL = parentRL;
            return this;
        }

        /**
         * Set display block.
         */
        public Builder display(Display display) {
            this.display = display;
            return this;
        }

        /**
         * Set rewards.
         */
        public Builder rewards(Rewards rewards) {
            this.rewards = rewards;
            return this;
        }

        /**
         * Add an arbitrary prebuilt criterion JSON.
         */
        public Builder criterion(String name, JsonObject criterion) {
            if (name == null || name.isEmpty()) throw new IllegalArgumentException("criterion name must not be empty");
            this.criteria.put(name, criterion);
            return this;
        }

        /**
         * Add a criterion with a trigger and optional conditions writer.
         */
        public Builder criterion(String name, String trigger, @Nullable Consumer<JsonObject> conditionsWriter) {
            return criterion(name, AdvancementDSL.criterion(trigger, conditionsWriter));
        }

        /**
         * inventory_changed: any of these items present in inventory.
         */
        public Builder inventoryHasAny(String name, Item... items) {
            return criterion(name, Triggers.INVENTORY_CHANGED, cond -> {
                JsonArray arr = new JsonArray();
                for (Item it : items) {
                    ResourceLocation rl = GameRegistry.findRegistry(Item.class).getKey(it);
                    JsonObject pred = new JsonObject();
                    pred.addProperty("item", rl == null ? "minecraft:stone" : rl.toString());
                    arr.add(pred);
                }
                cond.add("items", arr);
            });
        }

        /**
         * inventory_changed: any of these ItemStacks present in inventory (respects metadata and optional NBT).
         */
        public Builder inventoryHasAnyStacks(ItemStack... stacks) {
            return criterion("has_items", Triggers.INVENTORY_CHANGED, cond -> {
                com.google.gson.JsonArray arr = new com.google.gson.JsonArray();
                for (ItemStack s : stacks) {
                    if (s == null || s.isEmpty()) continue;
                    // reuse Templates' helper via a tiny bridge
                    arr.add(AdvancementDSL.Templates.itemPredicate(s));
                }
                cond.add("items", arr);
            });
        }

        /**
         * player_killed_entity with an entity type RL (e.g., minecraft:creeper).
         */
        public Builder playerKilledEntity(String name, String entityRL) {
            return criterion(name, Triggers.PLAYER_KILLED_ENTITY, cond -> {
                JsonObject ent = new JsonObject();
                ent.addProperty("type", entityRL);
                cond.add("entity", ent);
            });
        }

        /**
         * changed_dimension to specific dimension id.
         */
        public Builder changedDimension(String name, int dim) {
            return criterion(name, Triggers.CHANGED_DIMENSION, cond -> cond.addProperty("dimension", dim));
        }

        /**
         * enter_block with a block RL.
         */
        public Builder enterBlock(String name, String blockRL) {
            return criterion(name, Triggers.ENTER_BLOCK, cond -> cond.addProperty("block", blockRL));
        }

        /**
         * consume_item for specific items (any of).
         */
        public Builder consumeAnyItem(String name, Item... items) {
            return criterion(name, Triggers.CONSUME_ITEM, cond -> {
                JsonArray arr = new JsonArray();
                for (Item it : items) {
                    ResourceLocation rl = GameRegistry.findRegistry(Item.class).getKey(it);
                    JsonObject pred = new JsonObject();
                    pred.addProperty("item", rl == null ? "minecraft:stone" : rl.toString());
                    arr.add(pred);
                }
                cond.add("items", arr);
            });
        }

        /**
         * placed_block with block RL.
         */
        public Builder placedBlock(String name, String blockRL) {
            return criterion(name, Triggers.PLACED_BLOCK, cond -> cond.addProperty("block", blockRL));
        }

        /**
         * recipe_unlocked for a specific recipe RL.
         */
        public Builder recipeUnlocked(String name, String recipeRL) {
            return criterion(name, Triggers.RECIPE_UNLOCKED, cond -> cond.addProperty("recipe", recipeRL));
        }

        /**
         * Make requirements be "all of" the provided criterion names (AND in one inner array).
         */
        public Builder requireAll(String... names) {
            List<List<String>> matrix = new ArrayList<>(1);
            List<String> row = new ArrayList<>();
            Collections.addAll(row, names);
            matrix.add(row);
            this.requirements = matrix;
            return this;
        }

        /**
         * Make requirements be "any of" the provided criterion names (OR across separate inner arrays).
         */
        public Builder requireAny(String... names) {
            List<List<String>> matrix = new ArrayList<>();
            for (String n : names) matrix.add(Collections.singletonList(n));
            this.requirements = matrix;
            return this;
        }

        /**
         * Set a custom requirements matrix. Each inner list is an AND group; the outer list is OR across groups.
         */
        public Builder requirementsMatrix(List<List<String>> matrix) {
            this.requirements = matrix;
            return this;
        }

        /**
         * Auto "all-of" current criteria. Called if requirements were not explicitly set.
         */
        private JsonArray autoRequirementsFromCriteria() {
            JsonArray outer = new JsonArray();
            JsonArray inner = new JsonArray();
            for (String n : criteria.keySet()) inner.add(n);
            if (inner.size() == 0) inner.add("__missing");
            outer.add(inner);
            return outer;
        }

        private JsonArray toJsonRequirements(List<List<String>> matrix) {
            JsonArray outer = new JsonArray();
            for (List<String> group : matrix) {
                JsonArray inner = new JsonArray();
                for (String n : group) inner.add(n);
                outer.add(inner);
            }
            if (outer.size() == 0) {
                outer.add(autoRequirementsFromCriteria().get(0));
            }
            return outer;
        }

        /**
         * Finalize into an {@link Entry}.
         */
        public Entry build() {
            if (display == null) throw new IllegalStateException("display must be set");
            if (criteria.isEmpty()) throw new IllegalStateException("at least one criterion must be added");

            JsonObject json = new JsonObject();
            if (parentRL != null) json.addProperty("parent", parentRL);
            json.add("display", display.toJson());

            if (rewards != null) json.add("rewards", rewards.toJson());
            JsonObject crit = new JsonObject();
            for (Map.Entry<String, JsonObject> e : criteria.entrySet()) {
                crit.add(e.getKey(), e.getValue());
            }
            json.add("criteria", crit);
            if (requirements == null) {
                json.add("requirements", autoRequirementsFromCriteria());
            } else {
                json.add("requirements", toJsonRequirements(requirements));
            }

            return new Entry(idPath, json);
        }
    }

    public static final class Rewards {
        private final List<String> loot = new ArrayList<>();
        private final List<String> recipes = new ArrayList<>();
        private int experience;
        private @Nullable String function;

        public static Rewards create() {
            return new Rewards();
        }

        public Rewards experience(int xp) {
            this.experience = xp;
            return this;
        }

        /**
         * Add loot table RLs.
         */
        public Rewards loot(String... lootRLs) {
            Collections.addAll(this.loot, lootRLs);
            return this;
        }

        /**
         * Add recipe RLs.
         */
        public Rewards recipes(String... recipeRLs) {
            Collections.addAll(this.recipes, recipeRLs);
            return this;
        }

        /**
         * Set command function RL.
         */
        public Rewards function(@Nullable String functionRL) {
            this.function = functionRL;
            return this;
        }

        JsonObject toJson() {
            JsonObject obj = new JsonObject();
            if (experience != 0) obj.addProperty("experience", experience);

            if (!loot.isEmpty()) {
                JsonArray arr = new JsonArray();
                for (String s : loot) arr.add(s);
                obj.add("loot", arr);
            }
            if (!recipes.isEmpty()) {
                JsonArray arr = new JsonArray();
                for (String s : recipes) arr.add(s);
                obj.add("recipes", arr);
            }
            if (function != null && !function.isEmpty()) {
                obj.addProperty("function", function);
            }
            return obj;
        }
    }

    public static final class Triggers {
        public static final String BRED_ANIMALS = "minecraft:bred_animals";
        public static final String BREWED_POTION = "minecraft:brewed_potion";
        public static final String CHANGED_DIMENSION = "minecraft:changed_dimension";
        public static final String CONSTRUCT_BEACON = "minecraft:construct_beacon";
        public static final String CONSUME_ITEM = "minecraft:consume_item";
        public static final String CURED_ZOMBIE_VILLAGER = "minecraft:cured_zombie_villager";
        public static final String EFFECTS_CHANGED = "minecraft:effects_changed";
        public static final String ENCHANTED_ITEM = "minecraft:enchanted_item";
        public static final String ENTER_BLOCK = "minecraft:enter_block";
        public static final String ENTITY_HURT_PLAYER = "minecraft:entity_hurt_player";
        public static final String IMPOSSIBLE = "minecraft:impossible";
        public static final String INVENTORY_CHANGED = "minecraft:inventory_changed";
        public static final String ITEM_DURABILITY_CHANGED = "minecraft:item_durability_changed";
        public static final String PLAYER_KILLED_ENTITY = "minecraft:player_killed_entity";
        public static final String ENTITY_KILLED_PLAYER = "minecraft:entity_killed_player";
        public static final String LEVITATION = "minecraft:levitation";
        public static final String NETHER_TRAVEL = "minecraft:nether_travel";
        public static final String PLACED_BLOCK = "minecraft:placed_block";
        public static final String PLAYER_HURT_ENTITY = "minecraft:player_hurt_entity";
        public static final String LOCATION = "minecraft:location"; // PositionTrigger
        public static final String RECIPE_UNLOCKED = "minecraft:recipe_unlocked";
        public static final String SUMMONED_ENTITY = "minecraft:summoned_entity";
        public static final String TAME_ANIMAL = "minecraft:tame_animal";
        public static final String TICK = "minecraft:tick";
        public static final String USED_ENDER_EYE = "minecraft:used_ender_eye";
        public static final String USED_TOTEM = "minecraft:used_totem";
        public static final String VILLAGER_TRADE = "minecraft:villager_trade";
    }
}
