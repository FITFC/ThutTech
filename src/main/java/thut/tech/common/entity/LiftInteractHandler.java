package thut.tech.common.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import thut.api.entity.blockentity.BlockEntityInteractHandler;
import thut.core.common.ThutCore;
import thut.tech.common.TechCore;

public class LiftInteractHandler extends BlockEntityInteractHandler
{
    public static boolean DROPSPARTS = true;

    final EntityLift lift;

    public LiftInteractHandler(final EntityLift lift)
    {
        super(lift);
        this.lift = lift;
    }

    @Override
    public ActionResultType interactInternal(final PlayerEntity player, final BlockPos pos, final ItemStack stack,
            final Hand hand)
    {
        return ActionResultType.PASS;
    }

    @Override
    public boolean processInitialInteract(final PlayerEntity player, @Nullable ItemStack stack, final Hand hand)
    {

        final boolean isElevatorItemOrStick = stack.getItem() == Items.STICK || stack.getItem() == TechCore.LIFT;
        final boolean isLinker = stack.getItem() == TechCore.LINKER;

        final boolean canEdit = this.lift.owner != null && player.getUniqueID().equals(this.lift.owner)
                || player.abilities.isCreativeMode;

        final boolean shouldLinkLift = player.isShiftKeyDown() && isLinker && canEdit;
        final boolean shouldKillLiftUnowned = this.lift.owner == null;
        final boolean shouldDisplayOwner = isLinker && canEdit;
        final boolean shouldKillLiftOwned = player.isShiftKeyDown() && isElevatorItemOrStick && canEdit;

        if (shouldKillLiftUnowned)
        {
            ThutCore.LOGGER.error("Killing unowned Lift: " + this.lift);
            if (!this.lift.getEntityWorld().isRemote)
            {
                final String message = "msg.lift.killed";
                player.sendMessage(new TranslationTextComponent(message));
                if (LiftInteractHandler.DROPSPARTS)
                {
                    final BlockPos max = this.lift.boundMax;
                    final BlockPos min = this.lift.boundMin;
                    final int dw = Math.max(max.getX() - min.getX(), max.getZ() - min.getZ());
                    final int num = (dw + 1) * (max.getY() - min.getY() + 1);
                    stack = new ItemStack(TechCore.LIFT);
                    stack.setCount(num);
                    player.dropItem(stack, false, true);
                }
                this.lift.remove();
            }
            return true;
        }
        else if (shouldLinkLift)
        {
            if (stack.getTag() == null) stack.setTag(new CompoundNBT());
            stack.getTag().putString("lift", this.lift.getCachedUniqueIdString());

            final String message = "msg.liftSet";

            if (!this.lift.getEntityWorld().isRemote) player.sendMessage(new TranslationTextComponent(message));
            return true;
        }
        else if (shouldDisplayOwner)
        {
            if (!this.lift.getEntityWorld().isRemote && this.lift.owner != null)
            {
                final Entity ownerentity = this.lift.getEntityWorld().getPlayerByUuid(this.lift.owner);
                final String message = "msg.lift.owner";

                player.sendMessage(new TranslationTextComponent(message, ownerentity.getName()));
            }
            return true;
        }
        else if (shouldKillLiftOwned)
        {
            if (!this.lift.getEntityWorld().isRemote)
            {
                final String message = "msg.lift.killed";
                player.sendMessage(new TranslationTextComponent(message));
                if (LiftInteractHandler.DROPSPARTS)
                {
                    final BlockPos max = this.lift.boundMax;
                    final BlockPos min = this.lift.boundMin;
                    final int dw = Math.max(max.getX() - min.getX(), max.getZ() - min.getZ());
                    final int num = (dw + 1) * (max.getY() - min.getY() + 1);
                    stack = new ItemStack(TechCore.LIFT);
                    stack.setCount(num);
                    player.dropItem(stack, false, true);
                }
                this.lift.remove();
            }
            return true;
        }
        return false;
    }
}
