package us.potatoboy.htm.mixin;

import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import us.potatoboy.htm.HTMContainerLock;
import us.potatoboy.htm.LockableObject;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("MixinInnerClass")
@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin extends AbstractChestBlock<ChestBlockEntity> {
    protected ChestBlockMixin(Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityTypeSupplier) {
        super(settings, blockEntityTypeSupplier);
    }

    private final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<HTMContainerLock>> LOCK_RETRIEVER =
            new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<HTMContainerLock>>() {
                @Override
                public Optional<HTMContainerLock> getFromBoth(ChestBlockEntity first, ChestBlockEntity second) {
                    if (((LockableObject) first).getLock() != null) {
                        return Optional.of(((LockableObject) first).getLock());
                    }

                    if (((LockableObject) second).getLock() != null) {
                        return Optional.of(((LockableObject) second).getLock());
                    }

                    return Optional.empty();
                }

                @Override
                public Optional<HTMContainerLock> getFrom(ChestBlockEntity single) {
                    if (((LockableObject) single).getLock() != null) {
                        return Optional.of(((LockableObject) single).getLock());
                    }

                    return Optional.empty();
                }

                @Override
                public Optional<HTMContainerLock> getFallback() {
                    return Optional.empty();
                }
            };
}
