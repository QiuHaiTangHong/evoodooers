package com.helliongames.evoodooers.client.render;

import com.helliongames.evoodooers.Constants;
import com.helliongames.evoodooers.block.VoodooDollBlock;
import com.helliongames.evoodooers.entity.block.VoodooDollBlockEntity;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class VoodooDollRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/block/voodoo_doll.png");
    /**
     * 玩家模型
     */
    private final PlayerModel<LivingEntity> wideModel;
    private final PlayerModel<LivingEntity> slimModel;

    public VoodooDollRenderer(BlockEntityRendererProvider.Context context) {
        this.wideModel = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false);
        this.slimModel = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
    }

    @Override
    public void render(@NotNull T blockEntity, float v, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        if (!(blockEntity instanceof VoodooDollBlockEntity voodooDoll)) return;
        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof VoodooDollBlock dollBlock)) return;
        GameProfile profile = voodooDoll.getOwnerProfile() != null ? voodooDoll.getOwnerProfile() : null;

        Minecraft mc = Minecraft.getInstance();
        SkinManager skinManager = mc.getSkinManager();
        PlayerSkin skin = null;
        if (profile != null) {
            skin = skinManager.getInsecureSkin(profile);
        }
        boolean isSlim = skin != null && skin.model() == PlayerSkin.Model.SLIM;;
        PlayerModel<LivingEntity> model = isSlim ? slimModel : wideModel;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        float rotation = dollBlock.getYRotationDegrees(state);
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotation + 180.0f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        poseStack.translate(0.0, -1.503f, 0.0);
        poseStack.scale(1.25f, 1.25f, 1.25f);
        model.setAllVisible(true);

        float armX = 0.325F;
        float armY = 0.1625F;
        float armZ = 0.65F;
        float legHorizontal = -1.5708F;

        model.head.zRot = -0.3927F;
        model.hat.copyFrom(model.head);

        model.rightArm.xRot = -armX;
        model.rightArm.yRot = armY;
        model.rightArm.zRot = armZ;
        model.rightSleeve.copyFrom(model.rightArm);

        model.leftArm.xRot = -armX;
        model.leftArm.yRot = -armY;
        model.leftArm.zRot = -armZ;
        model.leftSleeve.copyFrom(model.leftArm);

        model.rightLeg.xRot = legHorizontal;
        model.rightLeg.yRot = 0.3927F;
        model.rightPants.copyFrom(model.rightLeg);

        model.leftLeg.xRot = legHorizontal;
        model.leftLeg.yRot = -0.3927F;
        model.leftPants.copyFrom(model.leftLeg);

        VertexConsumer voodooConsumer = multiBufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        model.renderToBuffer(poseStack, voodooConsumer, packedLight, packedOverlay);

        if (skin != null) {
            VertexConsumer skinConsumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(skin.texture()));
            model.renderToBuffer(poseStack, skinConsumer, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }
}
