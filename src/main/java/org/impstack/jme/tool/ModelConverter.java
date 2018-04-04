package org.impstack.jme.tool;

import com.jme3.asset.AssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.texture.image.ColorSpace;
import org.apache.commons.lang3.StringUtils;
import org.impstack.jme.scene.SpatialUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * A utility class that converts a blender model to a j3o binary.
 * <p>
 * The given model is the path starting from the assets folder. The generated model
 * is placed next to the original blend file.
 * <p>
 * You can either specify a material or a materialDef for the model. When a material is specified,
 * this material is set on the model. All extra supplied material parameters (colorspace, faceculling, ...) will be
 * discarded in favour of the material.
 * When you specify a materialDef, all parameters set on the original materialDef will be copied to the newly supplied
 * materialDef.
 * The materialDef is the path starting from the assets folder.
 * <p>
 * When a colorspace is specified, this colorspace will be set on the texture of the DiffuseMap or ColorMap
 * of the materialDef.
 * <p>
 * eg.
 * new ModelConverter("~/Projects/jme-template/assets"), assetManager).setModel("Models/test.blend").convert();
 */
public class ModelConverter {

    private static final Logger LOG = LoggerFactory.getLogger(ModelConverter.class);

    private final Path assetsFolder;
    private final AssetManager assetManager;
    private String model;
    private String materialDef;
    private Material material;
    private ColorSpace colorSpace;
    private RenderQueue.Bucket bucket;
    private RenderState.BlendMode blendMode;
    private RenderState.FaceCullMode faceCullMode;
    private RenderQueue.ShadowMode shadowMode;
    private Set<MatParam> materialDefParams;

    /**
     * @param assetsFolder the absolute path to the assets folder
     * @param assetManager the asset manager
     */
    public ModelConverter(Path assetsFolder, AssetManager assetManager) {
        this.assetsFolder = assetsFolder;
        this.assetManager = assetManager;
    }

    public void convert() {
        if (StringUtils.isEmpty(model)) {
            LOG.error("No model is specified!");
            return;
        }

        LOG.info("Loading asset {} in folder {}", model, assetsFolder);
        Spatial spatial = assetManager.loadModel(model);

        // set a new materialDef on the geometry. Try to copy all parameters of the old materialDef to the new materialDef.
        // if a colorspace is set, the texture of the diffusemap will be set to this colorspace
        if (!StringUtils.isEmpty(materialDef) && material == null) {
            SpatialUtils.getGeometry(spatial).ifPresent(geometry -> {
                Material oldMaterial = geometry.getMaterial();
                Material newMaterial = new Material(assetManager, materialDef);
                // setting old material parameters on the new material definition
                for (MatParam matParam : oldMaterial.getParams()) {
                    try {
                        newMaterial.setParam(matParam.getName(), matParam.getVarType(), matParam.getValue());
                        LOG.info("Setting {} -> {}", matParam.getName(), matParam.getValueAsString());
                    } catch (IllegalArgumentException e) {
                        LOG.warn("Unable to set {} on materialDef {}", matParam, materialDef);
                    }
                    if ((matParam.getName().equals("DiffuseMap") && colorSpace != null) ||
                            (matParam.getName().equals("ColorMap") && colorSpace != null)) {
                        LOG.info("Setting Colorspace {} on DiffuseMap/ColorMap", colorSpace);
                        ((Texture) matParam.getValue()).getImage().setColorSpace(colorSpace);
                    }
                }
                // setting additional specified material parameters on the new material definition
                if (materialDefParams != null) {
                    for (MatParam matParam : materialDefParams) {
                        try {
                            newMaterial.setParam(matParam.getName(), matParam.getVarType(), matParam.getValue());
                            LOG.info("Setting additional param {} -> {}", matParam.getName(), matParam.getValueAsString());
                        } catch (IllegalArgumentException e) {
                            LOG.warn("Unable to set additional param {} on materialDef {}", matParam, materialDef);
                        }
                    }
                }
                if (blendMode != null) {
                    newMaterial.getAdditionalRenderState().setBlendMode(blendMode);
                    LOG.info("Setting BlendMode {}", blendMode);
                }
                if (faceCullMode != null) {
                    newMaterial.getAdditionalRenderState().setFaceCullMode(faceCullMode);
                    LOG.info("Setting FaceCullMode {}", faceCullMode);
                }
                LOG.info("Setting {} on {}", materialDef, model);
                geometry.setMaterial(newMaterial);
            });
        } else if (material != null) {
            SpatialUtils.getGeometry(spatial).ifPresent(geometry -> {
                LOG.info("Setting {} on {}", material, model);
                geometry.setMaterial(material);
            });
        }

        if (bucket != null) {
            spatial.setQueueBucket(bucket);
            LOG.info("Setting QueueBucket {} on {}", bucket, model);
        }

        if (shadowMode != null) {
            spatial.setShadowMode(shadowMode);
            LOG.info("Setting ShadowMode {} on {}", shadowMode, model);
        }

        try {
            Path path = Paths.get(assetsFolder.toString(), getOutputName(model));
            BinaryExporter.getInstance().save(spatial, path.toFile());
            LOG.info("Saved to: {}", path);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public String getModel() {
        return model;
    }

    public ModelConverter setModel(String model) {
        this.model = model;
        return this;
    }

    public String getMaterialDef() {
        return materialDef;
    }

    public ModelConverter setMaterialDef(String materialDef) {
        this.materialDef = materialDef;
        return this;
    }

    public ColorSpace getColorSpace() {
        return colorSpace;
    }

    public ModelConverter setColorSpace(ColorSpace colorSpace) {
        this.colorSpace = colorSpace;
        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public ModelConverter setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public RenderQueue.Bucket getBucket() {
        return bucket;
    }

    public ModelConverter setBucket(RenderQueue.Bucket bucket) {
        this.bucket = bucket;
        return this;
    }

    public RenderState.BlendMode getBlendMode() {
        return blendMode;
    }

    public ModelConverter setBlendMode(RenderState.BlendMode blendMode) {
        this.blendMode = blendMode;
        return this;
    }

    public RenderState.FaceCullMode getFaceCullMode() {
        return faceCullMode;
    }

    public ModelConverter setFaceCullMode(RenderState.FaceCullMode faceCullMode) {
        this.faceCullMode = faceCullMode;
        return this;
    }

    public RenderQueue.ShadowMode getShadowMode() {
        return shadowMode;
    }

    public ModelConverter setShadowMode(RenderQueue.ShadowMode shadowMode) {
        this.shadowMode = shadowMode;
        return this;
    }

    public Set<MatParam> getMaterialDefParams() {
        return materialDefParams;
    }

    public ModelConverter setMatParam(MatParam materialParameter) {
        if (materialDefParams == null) {
            materialDefParams = new HashSet<>();
        }
        materialDefParams.add(materialParameter);
        return this;
    }

    private String getOutputName(String model) {
        String extension = model.substring(model.lastIndexOf("."));
        return model.replaceAll(extension, ".j3o");
    }
}