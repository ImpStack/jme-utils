package org.impstack.jme.shadow;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.Filter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.GeometryComparator;
import com.jme3.renderer.queue.GeometryList;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.shadow.ShadowUtil;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 *
 *  @author    Paul Speed
 */
public class DropShadowFilter extends Filter {

    private static final int VERTS_PER_SHADOW = 8; // one per box corner
    private static final int TRIS_PER_SHADOW = 12; // two per face
    private static final int INDEXES_PER_SHADOW = TRIS_PER_SHADOW * 3;

    private static final Vector3f[] BASE_CORNERS = new Vector3f[] {
            new Vector3f(-1, -1,  1),  // 0
            new Vector3f( 1, -1,  1),  // 1
            new Vector3f( 1, -1, -1),  // 2
            new Vector3f(-1, -1, -1),  // 3
            new Vector3f(-1,  1,  1),  // 4
            new Vector3f( 1,  1,  1),  // 5
            new Vector3f( 1,  1, -1),  // 6
            new Vector3f(-1,  1, -1)   // 7
    };

    private static final short[] BASE_INDEXES = new short[] {
            // top
            4, 5, 6, 4, 6, 7,
            // bottom
            3, 2, 1, 3, 1, 0,
            // +z
            0, 1, 5, 0, 5, 4,
            // -z
            2, 3, 7, 2, 7, 6,
            // -x
            3, 0, 4, 3, 4, 7,
            // +x
            1, 2, 6, 1, 6, 5
    };

    private Geometry shadowGeom;
    private Material shadowMaterial;
    private Mesh mesh;
    private int maxShadows;

    private ColorRGBA shadowColor = new ColorRGBA(0, 0, 0, 0.75f);

    private VertexBuffer vbPos;
    private VertexBuffer vbNormal;
    private VertexBuffer vbTexCoord;
    private VertexBuffer vbTexCoord2;
    private VertexBuffer vbIndex;

    private GeometryList casters;

    public DropShadowFilter() {
        this(500);
    }

    public DropShadowFilter( int maxShadows ) {
        this.maxShadows = maxShadows;
    }

    public void setShadowIntensity( float f ) {
        shadowColor.a = f;
    }

    public float getShadowIntensity() {
        return shadowColor.a;
    }

    @Override
    protected boolean isRequiresDepthTexture() {
        return true;
    }

    @Override
    protected void initFilter(AssetManager assets, RenderManager rm, ViewPort vp, int w, int h) {

        // Cheating... side effect of being lazy and using a filter
        // without actually needing to filter anything.
        material = new Material( assets, "MatDefs/Null.j3md" );

        mesh = new Mesh();

        // Setup the mesh for the max shadows size
        mesh.setBuffer( VertexBuffer.Type.Position, 3, BufferUtils.createVector3Buffer(maxShadows * VERTS_PER_SHADOW) );
        mesh.setBuffer( VertexBuffer.Type.Normal, 3, BufferUtils.createVector3Buffer(maxShadows * VERTS_PER_SHADOW) );
        mesh.setBuffer( VertexBuffer.Type.TexCoord, 3, BufferUtils.createVector3Buffer(maxShadows * VERTS_PER_SHADOW) );
        mesh.setBuffer( VertexBuffer.Type.TexCoord2, 3, BufferUtils.createVector3Buffer(maxShadows * VERTS_PER_SHADOW) );
        mesh.setBuffer( VertexBuffer.Type.Index, 3, BufferUtils.createShortBuffer(maxShadows * INDEXES_PER_SHADOW) );

        vbPos = mesh.getBuffer(VertexBuffer.Type.Position);
        vbNormal = mesh.getBuffer(VertexBuffer.Type.Normal);
        vbTexCoord = mesh.getBuffer(VertexBuffer.Type.TexCoord);
        vbTexCoord2 = mesh.getBuffer(VertexBuffer.Type.TexCoord2);
        vbIndex = mesh.getBuffer(VertexBuffer.Type.Index);


        shadowGeom = new Geometry("shadowVolumes", mesh);
        Material m = shadowMaterial = new Material( assets, "MatDefs/Shadows.j3md" );
        m.setColor( "ShadowColor", shadowColor );
        m.getAdditionalRenderState().setDepthWrite(false);
        m.getAdditionalRenderState().setDepthTest(false);
        m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        shadowGeom.setMaterial(m);
        shadowGeom.setLocalTranslation(0, 100, 0);

        shadowGeom.updateLogicalState(0.1f);
        shadowGeom.updateGeometricState();

        // Set our custom comparator for shadow casters
        casters = new GeometryList(new CasterComparator());
    }

    @Override
    protected Material getMaterial() {
        return material;
    }

    @Override
    protected void postFrame(RenderManager renderManager, ViewPort viewPort, FrameBuffer prevFilterBuffer, FrameBuffer sceneBuffer ) {

        RenderQueue rq = viewPort.getQueue();
        for (Spatial scene : viewPort.getScenes()) {
            ShadowUtil.getGeometriesInCamFrustum(scene, viewPort.getCamera(), RenderQueue.ShadowMode.Cast, casters);
        }
        if( casters.size() == 0 )
            return;

        Camera cam = viewPort.getCamera();
        BoundingSphere cullCheck = new BoundingSphere();
        Vector3f pos = new Vector3f();

        Texture frameTex = prevFilterBuffer.getColorBuffer().getTexture();
        Texture depthTex = prevFilterBuffer.getDepthBuffer().getTexture();
        shadowMaterial.setTexture("FrameTexture", frameTex);
        if( frameTex.getImage().getMultiSamples() > 1 ) {
            shadowMaterial.setInt("NumSamples", frameTex.getImage().getMultiSamples());
        } else {
            shadowMaterial.clearParam("NumSamples");
        }

        shadowMaterial.setTexture("DepthTexture", depthTex);
        if( depthTex.getImage().getMultiSamples() > 1 ) {
            shadowMaterial.setInt("NumSamplesDepth", depthTex.getImage().getMultiSamples());
        } else {
            shadowMaterial.clearParam("NumSamplesDepth");
        }

        int size = casters.size();
        if( size > maxShadows ) {
            // Give the shadows their best chance by sorting them.
            casters.setCamera(cam);
            casters.sort();
        }

        FloatBuffer bPos = (FloatBuffer)vbPos.getData().rewind();
        FloatBuffer bNormal = (FloatBuffer)vbNormal.getData().rewind();
        FloatBuffer bTexCoord = (FloatBuffer)vbTexCoord.getData().rewind();
        FloatBuffer bTexCoord2 = (FloatBuffer)vbTexCoord2.getData().rewind();
        ShortBuffer bIndex = (ShortBuffer)vbIndex.getData().rewind();


        Matrix4f viewMatrix = cam.getViewMatrix();
        Matrix4f worldMatrix = new Matrix4f();
        Matrix4f worldViewMatrix = new Matrix4f();
        float[] angles = new float[3];
        Vector3f vTemp = new Vector3f();
        Vector3f vert = new Vector3f();
        Vector3f viewDir = new Vector3f();
        Vector3f boxScale = new Vector3f();

        int rendered = 0;
        for( int i = 0; i < size; i++ ) {
            Geometry g = casters.get(i);

            // Use the geometry bounds.  We assumg it is still y-up
            // and merely rotated.  It's a decent enough approximiation
            // in many cases and will produce better shadows for oblong
            // objects than a simple round radius would.
            BoundingBox bounds = (BoundingBox)g.getModelBound();

            float scale = g.getWorldScale().x;
            float xEx = bounds.getXExtent() * scale;
            float yEx = bounds.getYExtent() * scale;
            float zEx = bounds.getZExtent() * scale;
            float volumeHeight = Math.max(yEx, Math.min(xEx,zEx));

            float xOffset = bounds.getCenter().x * scale;
            float yOffset = bounds.getCenter().y * scale;
            float zOffset = bounds.getCenter().z * scale;

            yOffset -= yEx;
            yOffset -= volumeHeight * 0.5f;
            yOffset += 0.01f;

            pos.set(g.getWorldTranslation());
            pos.addLocal(xOffset, yOffset, zOffset);

            // A conservative approximation that works because our shadow volume
            // is really just a round blob
            float radius = Math.max(xEx, Math.max(yEx, zEx));
            cullCheck.setCenter(pos);
            cullCheck.setRadius(radius);

            int save = cam.getPlaneState();
            cam.setPlaneState(0);
            Camera.FrustumIntersect intersect = cam.contains(cullCheck);
            cam.setPlaneState(save);

            if( intersect == Camera.FrustumIntersect.Outside ) {
                continue;
            }

            boxScale.set(0.5f/xEx, 0.5f/volumeHeight, 0.5f/zEx);

            Quaternion quat = g.getWorldRotation();
            angles = quat.toAngles(angles);

            Quaternion rotation = new Quaternion().fromAngles(0, angles[1], 0);
            Quaternion invRotation = rotation.inverse();
            worldMatrix.setTranslation(pos);
            worldMatrix.setRotationQuaternion(rotation);

            worldViewMatrix.set(viewMatrix);
            worldViewMatrix.multLocal(worldMatrix);

            // Setup the vertexes for each corner
            for( int j = 0; j < VERTS_PER_SHADOW; j++ ) {
                vTemp.set(BASE_CORNERS[j].x * xEx,
                        BASE_CORNERS[j].y * volumeHeight,
                        BASE_CORNERS[j].z * zEx);

                // Get the transformed coordinate in world space
                vert = worldMatrix.mult(vTemp, vert);
                bPos.put(vert.x).put(vert.y).put(vert.z);

                // Now calculate the view direction
                vert = vert.subtractLocal(cam.getLocation());
                vert.normalizeLocal();
                viewDir = invRotation.mult(vert, viewDir);
                bNormal.put(viewDir.x).put(viewDir.y).put(viewDir.z);

                // Model space is easy to calculate
                bTexCoord.put(BASE_CORNERS[j].x * xEx + xEx);
                bTexCoord.put(BASE_CORNERS[j].y * volumeHeight + volumeHeight);
                bTexCoord.put(BASE_CORNERS[j].z * zEx + zEx);

                // And so is the scale... since it's always the same
                bTexCoord2.put(boxScale.x).put(boxScale.y).put(boxScale.z);
            }

            // Fill in the index buffer
            for( int j = 0; j < INDEXES_PER_SHADOW; j++ ) {
                bIndex.put( (short)(BASE_INDEXES[j] + rendered * VERTS_PER_SHADOW) );
            }

            rendered++;
            if( rendered >= maxShadows ) {
                break;
            }
        }

        if( rendered > 0 ) {
            // Need to zero out the left-overs
            for( int i = rendered; i < maxShadows; i++ ) {
                for( int j = 0; j < INDEXES_PER_SHADOW; j++ ) {
                    bIndex.put((short)0);
                }
            }

            // Update the buffers
            bPos.rewind();
            bNormal.rewind();
            bTexCoord.rewind();
            bTexCoord2.rewind();
            bIndex.rewind();

            vbPos.updateData(bPos);
            vbNormal.updateData(bNormal);
            vbTexCoord.updateData(bTexCoord);
            vbTexCoord2.updateData(bTexCoord2);
            vbIndex.updateData(bIndex);

            shadowGeom.updateGeometricState();
            renderManager.renderGeometry(shadowGeom);
        }

        casters.clear();
    }

    private class CasterComparator implements GeometryComparator {

        private Camera cam;
        private final Vector3f tempVec  = new Vector3f();
        private final Vector3f tempVec2 = new Vector3f();

        public void setCamera( Camera cam ) {
            this.cam = cam;
        }

        public float distanceToCam( Geometry spat ) {
            if( spat == null ) {
                return Float.NEGATIVE_INFINITY;
            }

            if( spat.queueDistance != Float.NEGATIVE_INFINITY ) {
                return spat.queueDistance;
            }

            Vector3f camPosition = cam.getLocation();
            Vector3f viewVector = cam.getDirection(tempVec2);
            Vector3f spatPosition;

            if( spat.getWorldBound() != null ) {
                spatPosition = spat.getWorldBound().getCenter();
            } else {
                spatPosition = spat.getWorldTranslation();
            }

            spatPosition.subtract(camPosition, tempVec);
            spat.queueDistance = tempVec.dot(viewVector);

            return spat.queueDistance;
        }

        public int compare( Geometry o1, Geometry o2 ) {
            // Front to back sort
            float d1 = distanceToCam(o1);
            float d2 = distanceToCam(o2);

            if( d1 == d2 ) {
                return 0;
            } else if( d1 < d2 ) {
                return -1;
            } else {
                return 1;
            }
        }
    }


}