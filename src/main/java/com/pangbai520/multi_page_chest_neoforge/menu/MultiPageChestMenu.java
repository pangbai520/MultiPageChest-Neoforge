package com.pangbai520.multi_page_chest_neoforge.menu;

import com.pangbai520.multi_page_chest_neoforge.MultiPageChestNeoForge;
import com.pangbai520.multi_page_chest_neoforge.blockentity.MultiPageChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class MultiPageChestMenu extends AbstractContainerMenu {
    public static final int PREVIOUS_PAGE_BUTTON = 0;
    public static final int NEXT_PAGE_BUTTON = 1;

    private static final int CHEST_SLOT_COUNT = MultiPageChestBlockEntity.PAGE_SIZE;
    private static final int PLAYER_SLOT_START = CHEST_SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36;

    private final Container backingContainer;
    private final PageContainer pageContainer;
    private int page;

    public static MultiPageChestMenu fromNetwork(
            int containerId, Inventory playerInventory, RegistryFriendlyByteBuf data
    ) {
        BlockPos pos = data.readBlockPos();
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(pos);
        Container container = blockEntity instanceof MultiPageChestBlockEntity chest
                ? chest
                : new SimpleContainer(MultiPageChestBlockEntity.TOTAL_SIZE);
        return new MultiPageChestMenu(containerId, playerInventory, container, pos);
    }

    public MultiPageChestMenu(int containerId, Inventory playerInventory, Container container, BlockPos pos) {
        super(MultiPageChestNeoForge.MULTI_PAGE_CHEST_MENU.get(), containerId);
        checkContainerSize(container, MultiPageChestBlockEntity.TOTAL_SIZE);
        backingContainer = container;
        pageContainer = new PageContainer(container);
        container.startOpen(playerInventory.player);

        for (int row = 0; row < MultiPageChestBlockEntity.PAGE_ROWS; row++) {
            for (int column = 0; column < MultiPageChestBlockEntity.PAGE_COLUMNS; column++) {
                addSlot(new Slot(pageContainer, column + row * MultiPageChestBlockEntity.PAGE_COLUMNS,
                        12 + column * 18, 8 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 48 + column * 18, 174 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 48 + column * 18, 232));
        }

        addSlot(new Slot(new VoidContainer(), 0, 228, 232));

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return page;
            }

            @Override
            public void set(int value) {
                setPage(value);
            }
        });
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.floorMod(page, MultiPageChestBlockEntity.PAGE_COUNT);
        pageContainer.setPage(this.page);
    }

    public Container getBackingContainer() {
        return backingContainer;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == PREVIOUS_PAGE_BUTTON) {
            setPage(page - 1);
        } else if (id == NEXT_PAGE_BUTTON) {
            setPage(page + 1);
        } else {
            return false;
        }
        broadcastFullState();
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return backingContainer.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack source = slot.getItem();
            result = source.copy();
            if (index < CHEST_SLOT_COUNT) {
                if (!moveItemStackTo(source, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < PLAYER_SLOT_END) {
                if (!moveItemStackTo(source, 0, CHEST_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }

            if (source.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        backingContainer.stopOpen(player);
    }

    private static final class PageContainer implements Container {
        private final Container backing;
        private int page;

        private PageContainer(Container backing) {
            this.backing = backing;
        }

        private void setPage(int page) {
            this.page = page;
        }

        private int backingSlot(int slot) {
            return page * MultiPageChestBlockEntity.PAGE_SIZE + slot;
        }

        @Override
        public int getContainerSize() {
            return MultiPageChestBlockEntity.PAGE_SIZE;
        }

        @Override
        public boolean isEmpty() {
            for (int slot = 0; slot < getContainerSize(); slot++) {
                if (!getItem(slot).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return backing.getItem(backingSlot(slot));
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return backing.removeItem(backingSlot(slot), amount);
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return backing.removeItemNoUpdate(backingSlot(slot));
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            backing.setItem(backingSlot(slot), stack);
        }

        @Override
        public int getMaxStackSize() {
            return backing.getMaxStackSize();
        }

        @Override
        public void setChanged() {
            backing.setChanged();
        }

        @Override
        public boolean stillValid(Player player) {
            return backing.stillValid(player);
        }

        @Override
        public void clearContent() {
            for (int slot = 0; slot < getContainerSize(); slot++) {
                setItem(slot, ItemStack.EMPTY);
            }
        }
    }

    private static final class VoidContainer implements Container {
        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {
        }
    }
}
