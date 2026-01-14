/*
 * Copyright (c) 2021 acmi
 * Copyright (c) 2026 Galagard/L2Explorer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.l2explorer.unreal.engine;

import org.l2explorer.io.ByteUtil;
import org.l2explorer.io.ObjectInput;
import org.l2explorer.io.ObjectOutput;
import org.l2explorer.io.annotation.Compact;
import org.l2explorer.io.annotation.Length;
import org.l2explorer.io.annotation.ReadMethod;
import org.l2explorer.io.annotation.UShort;
import org.l2explorer.io.annotation.WriteMethod;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.UnrealSerializerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

/**
 * Represents a Static Mesh in the Unreal Engine.
 * <p>Static Meshes are highly optimized non-animated geometry. In Lineage II, 
 * this class handles complex versioning for vertex streams, UV maps, 
 * and chronicle-specific metadata across different licenses.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization & Java 25 Port)
 * @since 13-01-2026
 */
public class StaticMesh extends Primitive {
    private StaticMeshSection[] sections;
    private Box boundingBox2;
    private StaticMeshVertexStream vertexStream;
    private RawColorStream colorStream1;
    private RawColorStream colorStream2;
    private StaticMeshUVStream[] uvStream;
    private RawIndexBuffer indexStream1;
    private RawIndexBuffer indexStream2;
    
    @Compact
    private int u0;
    @SuppressWarnings("unused")
	private int offsetAfterU1;
    private U1[] u1;
    @SuppressWarnings("unused")
	private int offsetAfterU2;
    private U2[] u2;
    private int u;
    
    @Compact
    private int ref1;
    @Compact
    private int ref2;
    
    private float one11, one12, one13, one1, one2;
    private byte[] zeros12;
    private int u00, u01;
    @SuppressWarnings("unused")
	private int offsetAfterU3;
    private U3[] u3;
    private int i1;
    
    @Compact
    private int i2;
    private int i3;
    private U4[] u4;
    private int i4, i5, i6, i7;

    public StaticMesh() {
    }

    /**
     * Reads StaticMesh data according to the package license and version.
     *
     * @param input The object input stream.
     * @throws IOException 
     */
    @ReadMethod
    public final void readStaticMesh(ObjectInput<UnrealRuntimeContext> input) throws IOException {
        int license = input.getContext().getUnrealPackage().getLicense();
        int version = input.getContext().getUnrealPackage().getVersion();
        UnrealSerializerFactory factory = (UnrealSerializerFactory) input.getSerializerFactory();

        sections = new StaticMeshSection[input.readCompactInt()];
        for (int i = 0; i < sections.length; i++) {
            factory.forClass(StaticMeshSection.class).readObject(sections[i] = new StaticMeshSection(), input);
        }

        factory.forClass(Box.class).readObject(boundingBox2 = new Box(), input);
        factory.forClass(StaticMeshVertexStream.class).readObject(vertexStream = new StaticMeshVertexStream(), input);
        factory.forClass(RawColorStream.class).readObject(colorStream1 = new RawColorStream(), input);
        factory.forClass(RawColorStream.class).readObject(colorStream2 = new RawColorStream(), input);

        uvStream = new StaticMeshUVStream[input.readCompactInt()];
        for (int i = 0; i < uvStream.length; i++) {
            factory.forClass(StaticMeshUVStream.class).readObject(uvStream[i] = new StaticMeshUVStream(), input);
        }

        factory.forClass(RawIndexBuffer.class).readObject(indexStream1 = new RawIndexBuffer(), input);
        factory.forClass(RawIndexBuffer.class).readObject(indexStream2 = new RawIndexBuffer(), input);

        u0 = input.readCompactInt();
        if (license >= 18) {
            offsetAfterU1 = input.readInt();
        }

        u1 = new U1[input.readCompactInt()];
        for (int i = 0; i < u1.length; i++) {
            factory.forClass(U1.class).readObject(u1[i] = new U1(), input);
        }

        if (license >= 18) {
            offsetAfterU2 = input.readInt();
        }

        u2 = new U2[input.readCompactInt()];
        for (int i = 0; i < u2.length; i++) {
            factory.forClass(U2.class).readObject(u2[i] = new U2(), input);
        }

        if (license >= 11) {
            u = input.readInt();
            ref1 = input.readCompactInt();
            ref2 = input.readCompactInt();
            one11 = input.readFloat();
            one12 = input.readFloat();
            one13 = input.readFloat();
            one1 = input.readFloat();
            one2 = input.readFloat();
        }

        if (license >= 14) {
            zeros12 = new byte[12];
            input.readFully(zeros12);
        }

        if (license >= 15) {
            u00 = input.readInt();
        }

        if (license >= 37) {
            u01 = input.readInt();
        }

        offsetAfterU3 = input.readInt();
        u3 = new U3[input.readCompactInt()];
        for (int i = 0; i < u3.length; i++) {
            factory.forClass(U3.class).readObject(u3[i] = new U3(), input);
        }

        i1 = input.readInt();
        i2 = input.readCompactInt();

        if (version >= 123) {
            i3 = input.readInt();
        }

        if (version >= 125 && license >= 38) {
            u4 = new U4[input.readCompactInt()];
            for (int i = 0; i < u4.length; i++) {
                factory.forClass(U4.class).readObject(u4[i] = new U4(), input);
            }
            i4 = input.readInt();
            i5 = input.readInt();
            i6 = input.readInt();
            i7 = input.readInt();
        }
    }

    /**
     * Writes StaticMesh data according to the package license and version.
     *
     * @param output The object output stream.
     * @throws IOException 
     */
    @WriteMethod
    public final void writeStaticMesh(ObjectOutput<UnrealRuntimeContext> output) throws IOException {
        int license = output.getContext().getUnrealPackage().getLicense();
        int version = output.getContext().getUnrealPackage().getVersion();
        UnrealSerializerFactory factory = (UnrealSerializerFactory) output.getSerializerFactory();

        output.writeCompactInt(sections.length);
        for (StaticMeshSection section : sections) {
            factory.forClass(StaticMeshSection.class).writeObject(section, output);
        }

        factory.forClass(Box.class).writeObject(boundingBox2, output);
        factory.forClass(StaticMeshVertexStream.class).writeObject(vertexStream, output);
        factory.forClass(RawColorStream.class).writeObject(colorStream1, output);
        factory.forClass(RawColorStream.class).writeObject(colorStream2, output);

        output.writeCompactInt(uvStream.length);
        for (StaticMeshUVStream stream : uvStream) {
            factory.forClass(StaticMeshUVStream.class).writeObject(stream, output);
        }

        factory.forClass(RawIndexBuffer.class).writeObject(indexStream1, output);
        factory.forClass(RawIndexBuffer.class).writeObject(indexStream2, output);

        output.writeCompactInt(u0);

        if (license >= 18) {
            output.writeInt(output.getPosition() + 4 + ByteUtil.sizeOfCompactInt(u1.length) + Arrays.stream(u1).mapToInt(U1::getSize).sum());
        }

        output.writeCompactInt(u1.length);
        for (U1 entry : u1) {
            factory.forClass(U1.class).writeObject(entry, output);
        }

        if (license >= 18) {
            output.writeInt(output.getPosition() + 4 + ByteUtil.sizeOfCompactInt(u2.length) + Arrays.stream(u2).mapToInt(U2::getSize).sum());
        }

        output.writeCompactInt(u2.length);
        for (U2 entry : u2) {
            factory.forClass(U2.class).writeObject(entry, output);
        }

        if (license >= 11) {
            output.writeInt(u);
            output.writeCompactInt(ref1);
            output.writeCompactInt(ref2);
            output.writeFloat(one11);
            output.writeFloat(one12);
            output.writeFloat(one13);
            output.writeFloat(one1);
            output.writeFloat(one2);
        }

        if (license >= 14) {
            output.writeBytes(zeros12);
        }

        if (license >= 15) {
            output.writeInt(u00);
        }

        if (license >= 37) {
            output.writeInt(u01);
        }

        int nextOffset = output.getPosition() + 4 + ByteUtil.sizeOfCompactInt(u3.length) + Arrays.stream(u3).mapToInt(U3::getSize).sum();
        output.writeInt(nextOffset);
        output.writeCompactInt(u3.length);
        for (U3 entry : u3) {
            factory.forClass(U3.class).writeObject(entry, output);
        }

        output.writeInt(i1);
        output.writeCompactInt(i2);

        if (version >= 123) {
            output.writeInt(i3);
        }

        if (version >= 125 && license >= 38) {
            output.writeCompactInt(u4.length);
            for (U4 entry : u4) {
                factory.forClass(U4.class).writeObject(entry, output);
            }
            output.writeInt(i4);
            output.writeInt(i5);
            output.writeInt(i6);
            output.writeInt(i7);
        }
    }

    // --- Getters and Setters ---

    public StaticMeshSection[] getSections() { return sections; }
    public void setSections(StaticMeshSection[] sections) { this.sections = sections; }
    public Box getBoundingBox2() { return boundingBox2; }
    public void setBoundingBox2(Box boundingBox2) { this.boundingBox2 = boundingBox2; }
    public StaticMeshVertexStream getVertexStream() { return vertexStream; }
    public void setVertexStream(StaticMeshVertexStream vertexStream) { this.vertexStream = vertexStream; }
    public RawColorStream getColorStream1() { return colorStream1; }
    public void setColorStream1(RawColorStream colorStream1) { this.colorStream1 = colorStream1; }
    public RawColorStream getColorStream2() { return colorStream2; }
    public void setColorStream2(RawColorStream colorStream2) { this.colorStream2 = colorStream2; }
    public StaticMeshUVStream[] getUvStream() { return uvStream; }
    public void setUvStream(StaticMeshUVStream[] uvStream) { this.uvStream = uvStream; }
    public RawIndexBuffer getIndexStream1() { return indexStream1; }
    public void setIndexStream1(RawIndexBuffer indexStream1) { this.indexStream1 = indexStream1; }
    public RawIndexBuffer getIndexStream2() { return indexStream2; }
    public void setIndexStream2(RawIndexBuffer indexStream2) { this.indexStream2 = indexStream2; }
    public int getU0() { return u0; }
    public void setU0(int u0) { this.u0 = u0; }
    public U1[] getU1() { return u1; }
    public void setU1(U1[] u1) { this.u1 = u1; }
    public U2[] getU2() { return u2; }
    public void setU2(U2[] u2) { this.u2 = u2; }
    public U3[] getU3() { return u3; }
    public void setU3(U3[] u3) { this.u3 = u3; }
    public U4[] getU4() { return u4; }
    public void setU4(U4[] u4) { this.u4 = u4; }

    // --- Static Inner Classes ---

    public static class StaticMeshSection {
        private int f4;
        @UShort private int firstIndex;
        @UShort private int firstVertex;
        @UShort private int lastVertex;
        @UShort private int fE;
        @UShort private int numFaces;

        public int getF4() { return f4; }
        public void setF4(int f4) { this.f4 = f4; }
        public int getFirstIndex() { return firstIndex; }
        public void setFirstIndex(int firstIndex) { this.firstIndex = firstIndex; }
        public int getNumFaces() { return numFaces; }
        public void setNumFaces(int numFaces) { this.numFaces = numFaces; }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "[%d,%d,%d,%d,%d,%d]", f4, firstIndex, firstVertex, lastVertex, fE, numFaces);
        }
    }

    public static class StaticMeshVertexStream {
        private StaticMeshVertex[] vert;
        private int revision;

        public StaticMeshVertex[] getVert() { return vert; }
        public void setVert(StaticMeshVertex[] vert) { this.vert = vert; }
        public int getRevision() { return revision; }
        public void setRevision(int revision) { this.revision = revision; }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "[%d,%s]", revision, Arrays.toString(vert));
        }
    }

    public static class StaticMeshVertex {
        private Vector pos;
        private Vector normal;

        public Vector getPos() { return pos; }
        public void setPos(Vector pos) { this.pos = pos; }
        public Vector getNormal() { return normal; }
        public void setNormal(Vector normal) { this.normal = normal; }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "[%s,%s]", pos, normal);
        }
    }

    public static class RawColorStream {
        private Color[] color;
        private int revision;

        public Color[] getColor() { return color; }
        public void setColor(Color[] color) { this.color = color; }
        public int getRevision() { return revision; }
        public void setRevision(int revision) { this.revision = revision; }
    }

    public static class StaticMeshUVStream {
        private MeshUVFloat[] data;
        @SuppressWarnings("unused")
		private int f10;
        @SuppressWarnings("unused")
		private int f1C;

        public MeshUVFloat[] getData() { return data; }
        public void setData(MeshUVFloat[] data) { this.data = data; }
    }

    public static class MeshUVFloat {
        private float u;
        private float v;

        public float getU() { return u; }
        public void setU(float u) { this.u = u; }
        public float getV() { return v; }
        public void setV(float v) { this.v = v; }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "[%f,%f]", u, v);
        }
    }

    public static class RawIndexBuffer {
        @UShort private int[] indices;
        @SuppressWarnings("unused")
		private int revision;

        public int[] getIndices() { return indices; }
        public void setIndices(int[] indices) { this.indices = indices; }
    }

    public static class U1 {
        @SuppressWarnings("unused")
		private float u1, u2, u3, u4, u5, u6, u7, u8, u9, u10, u11, u12, u13, u14, u15, u16;
        @Compact private int u17, u18, u19, u20;

        int getSize() {
            return 16 * 4 + ByteUtil.sizeOfCompactInt(u17) + ByteUtil.sizeOfCompactInt(u18) +
                    ByteUtil.sizeOfCompactInt(u19) + ByteUtil.sizeOfCompactInt(u20);
        }
    }

    public static class U2 {
        @Compact private int u1, u2, u3, u4;
        @SuppressWarnings("unused")
		private Box u5;

        int getSize() {
            return ByteUtil.sizeOfCompactInt(u1) + ByteUtil.sizeOfCompactInt(u2) +
                    ByteUtil.sizeOfCompactInt(u3) + ByteUtil.sizeOfCompactInt(u4) + 25;
        }
    }

    public static class U3 {
        @SuppressWarnings("unused")
		private float u1, u2, u3, u4, u5, u6, u7, u8, u9;
        @Length(Length.Type.INT) private U3_1[] u10;
        @SuppressWarnings("unused")
		private int u11, u12, u13, u14, u15;

        public static class U3_1 {
            @SuppressWarnings("unused")
			private float u1, u2, u3, u4, u5, u6;
            int getSize() { return 6 * 4; }
        }

        int getSize() {
            return 9 * 4 + 4 + Arrays.stream(u10).mapToInt(U3_1::getSize).sum() + 5 * 4;
        }
    }

    public static class U4 {
        @SuppressWarnings("unused")
		private float u1, u2;
    }
}