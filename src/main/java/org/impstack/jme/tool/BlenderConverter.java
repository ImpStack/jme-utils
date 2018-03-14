package org.impstack.jme.tool;

import com.jme3.asset.AssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
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

/**
 * A utility class that converts a blender model to a j3o binary.
 *
 * The given model is the path starting from the assets folder. The generated model
 * is placed next to the original blend file.
 *
 * When an additional materialDef is specified, this materialDef will be set on the model.
 * All parameters set on the original materialDef will be copied to the new materialDef.
 * The materialDef is the path starting from the assets folder.
 *
 * When a colorspace is specified, this colorspace will be set on the texture of the DiffuseMap
 * of the materialDef.
 *
 * eg.
 * new BlenderConverter("~/Projects/jme-template/assets"), assetManager).setModel("Models/test.blend").convert();
 */
public class BlenderConverter {

    private static final Logger LOG = LoggerFactory.getLogger(BlenderConverter.class);

    private final Path assetsFolder;
    private final AssetManager assetManager;
    private String model;
    private String materialDef;
    private Material material;
    private ColorSpace colorSpace;
    private RenderQueue.Bucket bucket;

    /**
     * @param assetsFolder the absolute path to the assets folder
     * @param assetManager the asset manager
     */
    public BlenderConverter(Path assetsFolder, AssetManager assetManager) {
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
                for (MatParam matParam : oldMaterial.getParams()) {
                    try {
                        newMaterial.setParam(matParam.getName(), matParam.getVarType(), matParam.getValue());
                        LOG.info("Setting {} -> {}", matParam.getName(), matParam.getValueAsString());
                    } catch (IllegalArgumentException e) {
                        LOG.warn("Unable to set {} on materialDef {}", matParam, materialDef);
                    }
                    if (matParam.getName().equals("DiffuseMap") && colorSpace != null) {
                        LOG.info("Setting Colorspace {} on DiffuseMap", colorSpace);
                        ((Texture) matParam.getValue()).getImage().setColorSpace(colorSpace);
                    }
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
            SpatialUtils.getGeometry(spatial).ifPresent(geometry -> {
                geometry.setQueueBucket(bucket);
                LOG.info("Setting QueueBucket {} on {}", bucket, model);
            });
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

    public BlenderConverter setModel(String model) {
        this.model = model;
        return this;
    }

    public String getMaterialDef() {
        return materialDef;
    }

    public BlenderConverter setMaterialDef(String materialDef) {
        this.materialDef = materialDef;
        return this;
    }

    public ColorSpace getColorSpace() {
        return colorSpace;
    }

    public BlenderConverter setColorSpace(ColorSpace colorSpace) {
        this.colorSpace = colorSpace;
        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public BlenderConverter setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public RenderQueue.Bucket getBucket() {
        return bucket;
    }

    public BlenderConverter setBucket(RenderQueue.Bucket bucket) {
        this.bucket = bucket;
        return this;
    }

    private String getOutputName(String model) {
        String extension = model.substring(model.lastIndexOf("."));
        return model.replaceAll(extension, ".j3o");
    }
}