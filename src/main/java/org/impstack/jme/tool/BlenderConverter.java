package org.impstack.jme.tool;

import com.jme3.asset.AssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
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
 * When an additional material is specified, this material will be set on the model.
 * All parameters set on the original material will be copied to the new material.
 * The material is the path starting from the assets folder.
 *
 * When a colorspace is specified, this colorspace will be set on the texture of the DiffuseMap
 * of the material.
 *
 * eg.
 * new BlenderConverter("~/Projects/jme-template/assets"), assetManager).setModel("Models/test.blend").convert();
 */
public class BlenderConverter {

    private static final Logger LOG = LoggerFactory.getLogger(BlenderConverter.class);

    private final Path assetsFolder;
    private final AssetManager assetManager;
    private String model;
    private String material;
    private ColorSpace colorSpace;

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

        // set a new material on the geometry. Try to copy all parameters of the old material to the new material.
        // if a colorspace is set, the texture of the diffusemap will be set to this colorspace
        if (!StringUtils.isEmpty(material)) {
            SpatialUtils.getGeometry(spatial).ifPresent(geometry -> {
                Material oldMaterial = geometry.getMaterial();
                Material newMaterial = assetManager.loadMaterial(material);
                for (MatParam matParam : oldMaterial.getParams()) {
                    newMaterial.setParam(matParam.getName(), matParam.getVarType(), matParam.getValue());
                    LOG.info("Setting {} -> {}", matParam.getName(), matParam.getValueAsString());
                    if (matParam.getName().equals("DiffuseMap") && colorSpace != null) {
                        LOG.info("Setting Colorspace {} on DiffuseMap", colorSpace);
                        ((Texture) matParam.getValue()).getImage().setColorSpace(colorSpace);
                    }
                }
                LOG.info("Setting {} on {}", material, model);
                geometry.setMaterial(newMaterial);
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

    public String getMaterial() {
        return material;
    }

    public BlenderConverter setMaterial(String material) {
        this.material = material;
        return this;
    }

    public ColorSpace getColorSpace() {
        return colorSpace;
    }

    public BlenderConverter setColorSpace(ColorSpace colorSpace) {
        this.colorSpace = colorSpace;
        return this;
    }

    private String getOutputName(String model) {
        String extension = model.substring(model.lastIndexOf("."));
        return model.replaceAll(extension, ".j3o");
    }
}