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

import static org.l2explorer.io.ByteUtil.sizeOfCompactInt;

import org.l2explorer.io.L2DataOutput;
import org.l2explorer.io.annotation.UByte;
import org.l2explorer.io.annotation.WriteMethod;

import java.io.IOException;

/**
 * Represents a Texture resource in the Unreal Engine.
 * <p>Textures contain pixel data organized in MipMaps, which are 
 * pre-calculated, optimized sequences of images at progressively 
 * lower resolutions.</p>
 *
 * @author acmi (Original Code)
 * @author Galagard (L2Explorer Modernization & Java 25 Port)
 * @since 13-01-2026
 */
public class Texture extends Material {
    /**
     * Array of MipMap levels for this texture.
     */
    private MipMapData[] mipMaps;

    public Texture() {
    }

    /**
     * Gets the mipmap levels of this texture.
     *
     * @return Array of MipMapData.
     */
    public MipMapData[] getMipMaps() {
        return mipMaps;
    }

    /**
     * Sets the mipmap levels for this texture.
     *
     * @param mipMaps Array of MipMapData to set.
     */
    public void setMipMaps(MipMapData[] mipMaps) {
        this.mipMaps = mipMaps;
    }

    /**
     * Inner class representing a single MipMap level's raw data and dimensions.
     */
    public static class MipMapData {
        private int offset;
        private byte[] data;
        private int width;
        private int height;
        @UByte
        private int widthBits;
        @UByte
        private int heightBits;

        /**
         * Writes mipmap data including the offset to the next mip level.
         *
         * @param output The data output stream.
         * @throws IOException If an I/O error occurs.
         */
        @WriteMethod
        public void writeMipMapData(L2DataOutput output) throws IOException {
            // Calculation of the pointer to the next mipmap level
            int nextMipOffset = output.getPosition() + 4 + sizeOfCompactInt(data.length) + data.length;
            output.writeInt(nextMipOffset);
            
            output.writeByteArray(data);
            output.writeInt(width);
            output.writeInt(height);
            output.writeByte(widthBits);
            output.writeByte(heightBits);
        }

        // --- Inner Getters and Setters ---

        public int getOffset() { return offset; }
        public void setOffset(int offset) { this.offset = offset; }

        public byte[] getData() { return data; }
        public void setData(byte[] data) { this.data = data; }

        public int getWidth() { return width; }
        public void setWidth(int width) { this.width = width; }

        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }

        public int getWidthBits() { return widthBits; }
        public void setWidthBits(int widthBits) { this.widthBits = widthBits; }

        public int getHeightBits() { return heightBits; }
        public void setHeightBits(int heightBits) { this.heightBits = heightBits; }
    }
}