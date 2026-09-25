package com.pangbai520.multi_page_chest_neoforge;

import com.pangbai520.multi_page_chest_neoforge.block.MultiPageChestBlock;
import com.pangbai520.multi_page_chest_neoforge.blockentity.MultiPageChestBlockEntity;
import com.pangbai520.multi_page_chest_neoforge.menu.MultiPageChestMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(MultiPageChestNeoForge.MODID)
public final class MultiPageChestNeoForge {
    public static final String MODID = "multipagechest";

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, MODID);

    public static final DeferredBlock<MultiPageChestBlock> MULTI_PAGE_CHEST = BLOCKS.registerBlock(
            "multi_page_chest",
            MultiPageChestBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.5F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()
                    .noOcclusion()
    );

    public static final DeferredItem<BlockItem> MULTI_PAGE_CHEST_ITEM = ITEMS.register(
            "multi_page_chest",
            () -> new BlockItem(MULTI_PAGE_CHEST.get(), new Item.Properties())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiPageChestBlockEntity>>
            MULTI_PAGE_CHEST_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
                    "multi_page_chest",
                    () -> BlockEntityType.Builder.of(MultiPageChestBlockEntity::new, MULTI_PAGE_CHEST.get()).build(null)
            );

    public static final DeferredHolder<MenuType<?>, MenuType<MultiPageChestMenu>> MULTI_PAGE_CHEST_MENU =
            MENU_TYPES.register(
                    "multi_page_chest",
                    () -> IMenuTypeExtension.create(MultiPageChestMenu::fromNetwork)
            );

    public MultiPageChestNeoForge(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        modEventBus.addListener(this::addCreativeTabContents);
    }

    private void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(MULTI_PAGE_CHEST_ITEM);
        }
    }
}
