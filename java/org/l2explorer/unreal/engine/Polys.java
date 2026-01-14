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

import java.io.IOException;

import org.l2explorer.io.ObjectInput;
import org.l2explorer.io.annotation.ReadMethod;
import org.l2explorer.unreal.UnrealRuntimeContext;
import org.l2explorer.unreal.UnrealSerializerFactory;
import org.l2explorer.unreal.core.Object;

/**
 * Represents a collection of Polygons in the Unreal Engine.
 * <p>Polys are typically used for Brush geometry (BSP) in levels and 
 * collision primitives.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization & Java 25 Port)
 * @since 13-01-2026
 */
public class Polys extends Object {
    private Polygon[] polygons;

    public Polys() {
    }

    /**
     * Reads the collection of polygons from the input stream.
     *
     * @param input The object input stream.
     * @throws IOException 
     */
    @ReadMethod
    public final void readPolys(ObjectInput<UnrealRuntimeContext> input) throws IOException {
        // Skip unknown int (usually 0 or version related in BSP)
        input.readInt();
        
        int count = input.readInt();
        this.polygons = new Polygon[count];
        for (int i = 0; i < count; i++) {
            input.getSerializerFactory()
                 .forClass(Polygon.class)
                 .readObject(this.polygons[i] = new Polygon(), input);
        }
    }

    // --- Getters and Setters ---

    public Polygon[] getPolygons() {
        return polygons;
    }

    public void setPolygons(Polygon[] polygons) {
        this.polygons = polygons;
    }

    /**
     * Represents a single Polygon with its geometric and material properties.
     */
    public static class Polygon {
        private Vector origin;
        private Vector normal;
        @SuppressWarnings("unused")
		private Vector textureU;
        @SuppressWarnings("unused")
		private Vector textureV;
        private Vector[] vertexes;
        @SuppressWarnings("unused")
		private int flags;
        @SuppressWarnings("unused")
		private Object actor;
        private Object texture;
        private String itemName;
        @SuppressWarnings("unused")
		private int iLink;
        @SuppressWarnings("unused")
		private int iBrushPoly;
        @SuppressWarnings("unused")
		private float unk0;
        @SuppressWarnings("unused")
		private int unk1;

        /**
         * Custom read method for individual polygons.
         *
         * @param input The object input stream.
         * @throws IOException 
         */
        @ReadMethod
        public final void readPolygon(ObjectInput<UnrealRuntimeContext> input) throws IOException {
            int vertexCount = input.readCompactInt();
            this.vertexes = new Vector[vertexCount];

            UnrealSerializerFactory factory = (UnrealSerializerFactory) input.getSerializerFactory();

            factory.forClass(Vector.class).readObject(this.origin = new Vector(), input);
            factory.forClass(Vector.class).readObject(this.normal = new Vector(), input);
            factory.forClass(Vector.class).readObject(this.textureU = new Vector(), input);
            factory.forClass(Vector.class).readObject(this.textureV = new Vector(), input);

            for (int i = 0; i < vertexCount; i++) {
                factory.forClass(Vector.class).readObject(this.vertexes[i] = new Vector(), input);
            }

            this.flags = input.readInt();
            this.actor = factory.getOrCreateObject(input.getContext().getUnrealPackage().objectReference(input.readCompactInt()));
            this.texture = factory.getOrCreateObject(input.getContext().getUnrealPackage().objectReference(input.readCompactInt()));
            this.itemName = (String) input.getContext().getUnrealPackage().nameReference(input.readCompactInt());
            this.iLink = input.readCompactInt();
            this.iBrushPoly = input.readCompactInt();
            this.unk0 = input.readFloat();

            // Handling version specific 'unk1' field
            switch (input.getContext().getUnrealPackage().getVersion()) {
                case 0x060076:
                case 0x10007b:
                case 0x12007b:
                case 0x13007b:
                case 0x14007b:
                    break;
                default:
                    this.unk1 = input.readInt();
            }
        }

        // --- Inner Getters and Setters ---

        public Vector getOrigin() { return origin; }
        public void setOrigin(Vector origin) { this.origin = origin; }
        public Vector getNormal() { return normal; }
        public void setNormal(Vector normal) { this.normal = normal; }
        public Vector[] getVertexes() { return vertexes; }
        public void setVertexes(Vector[] vertexes) { this.vertexes = vertexes; }
        public Object getTexture() { return texture; }
        public void setTexture(Object texture) { this.texture = texture; }
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
    }
}