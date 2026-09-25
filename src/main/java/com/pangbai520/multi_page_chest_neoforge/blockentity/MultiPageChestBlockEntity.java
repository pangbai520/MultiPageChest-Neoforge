package com.pangbai520.multi_page_chest_neoforge.blockentity;

import com.pangbai520.multi_page_chest_neoforge.MultiPageChestNeoForge;
import com.pangbai520.multi_page_chest_neoforge.menu.MultiPageChestMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class MultiPageChestBlockEntity extends BaseContainerBlockEntity implements LidBlockEntity {
    public static final int PAGE_COUNT = 5;
    public static final int PAGE_COLUMNS = 13;
    public static final int PAGE_ROWS = 9;
    public static final int PAGE_SIZE = PAGE_COLUMNS * PAGE_ROWS;
    public static final int TOTAL_SIZE = PAGE_COUNT * PAGE_SIZE;

    private NonNullList<ItemStack> items = NonNullList.withSize(TOTAL_SIZE, ItemStack.EMPTY);
    private final ChestLidController lidController = new ChestLidController();
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            playSound(level, pos, SoundEvents.CHEST_OPEN);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            playSound(level, pos, SoundEvents.CHEST_CLOSE);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {
            signalOpenCount(level, pos, state, newCount);
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof MultiPageChestMenu menu
                    && menu.getBackingContainer() == MultiPageChestBlockEntity.this;
        }
    };

    public MultiPageChestBlockEntity(BlockPos pos, BlockState state) {
        super(MultiPageChestNeoForge.MULTI_PAGE_CHEST_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return TOTAL_SIZE;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.multipagechest.multi_page_chest");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(TOTAL_SIZE, ItemStack.EMPTY);
        ListTag list = tag.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag itemTag = list.getCompound(i);
            int slot = itemTag.getShort("Slot") & 0xFFFF;
            if (slot < TOTAL_SIZE) {
                items.set(slot, ItemStack.parse(registries, itemTag).orElse(ItemStack.EMPTY));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag list = new ListTag();
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putShort("Slot", (short) slot);
                list.add(stack.save(registries, itemTag));
            }
        }
        tag.put("Items", list);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        return new MultiPageChestMenu(containerId, playerInventory, this, worldPosition);
    }

    public static void lidAnimateTick(
            Level level, BlockPos pos, BlockState state, MultiPageChestBlockEntity blockEntity
    ) {
        blockEntity.lidController.tickLid();
    }

    @Override
    public boolean triggerEvent(int id, int value) {
        if (id == 1) {
            lidController.shouldBeOpen(value > 0);
            return true;
        }
        return super.triggerEvent(id, value);
    }

    @Override
    public void startOpen(Player player) {
        if (!remove && !player.isSpectator() && level != null) {
            openersCounter.incrementOpeners(player, level, worldPosition, getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!remove && !player.isSpectator() && level != null) {
            openersCounter.decrementOpeners(player, level, worldPosition, getBlockState());
        }
    }

    @Override
    public float getOpenNess(float partialTicks) {
        return lidController.getOpenness(partialTicks);
    }

    public void recheckOpen() {
        if (!remove && level != null) {
            openersCounter.recheckOpeners(level, worldPosition, getBlockState());
        }
    }

    private void signalOpenCount(Level level, BlockPos pos, BlockState state, int count) {
        Block block = state.getBlock();
        level.blockEvent(pos, block, 1, count);
    }

    private static void playSound(Level level, BlockPos pos, SoundEvent sound) {
        level.playSound(
                null,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                sound,
                SoundSource.BLOCKS,
                0.5F,
                level.random.nextFloat() * 0.1F + 0.9F
        );
    }
}
