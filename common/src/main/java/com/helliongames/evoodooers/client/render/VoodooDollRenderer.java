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

/**
 * VoodooDollRenderer 类
 * <p> 用于渲染 VoodooDoll 块实体的模型, 根据块实体的属性和玩家皮肤信息, 绘制出对应的玩家模型.
 * 支持宽版和瘦版玩家模型的切换, 并根据块实体的旋转角度进行相应的姿态调整.
 * @author QiuHaiTangHong
 * @version 1.0.0
 * @date 2026.01.01
 * @since 1.0.0
 */
public class VoodooDollRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/block/voodoo_doll.png");
    /**
     * 玩家模型
     */
    private final PlayerModel<LivingEntity> wideModel;
    /** 玩家的瘦模型, 用于表示玩家的瘦形态模型数据 */
    private final PlayerModel<LivingEntity> slimModel;

    /**
     * 初始化 VoodooDollRenderer
     * <p> 根据提供的渲染上下文初始化宽版和瘦版玩家模型, 用于渲染幽灵娃娃
     * @param context 渲染上下文, 用于获取模型层和烘焙模型
     */
    public VoodooDollRenderer(BlockEntityRendererProvider.Context context) {
        this.wideModel = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false);
        this.slimModel = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
    }

    /**
     * 渲染诅咒娃娃方块实体
     * <p> 根据传入的方块实体和渲染参数, 渲染诅咒娃娃的模型, 包括娃娃本体和皮肤 (如果存在)
     * @param blockEntity       方块实体对象, 用于获取方块状态和娃娃信息
     * @param v                 渲染参数, 用于控制渲染效果
     * @param poseStack         姿势堆栈, 用于保存和恢复渲染姿势
     * @param multiBufferSource 多缓冲源, 用于获取渲染缓冲区
     * @param packedLight       光照参数, 用于控制渲染光照效果
     * @param packedOverlay     叠加参数, 用于控制渲染叠加效果
     */
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
