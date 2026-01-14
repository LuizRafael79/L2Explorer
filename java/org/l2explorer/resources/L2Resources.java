package org.l2explorer.resources;

import org.l2explorer.utils.crypt.L2Crypt;
import org.l2explorer.unreal.Environment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.l2explorer.io.L2DataInputStream;
import org.l2explorer.resources.textures.Img;
import org.l2explorer.resources.textures.MipMapInfo;
import org.l2explorer.resources.textures.Split9;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.ImageIcon;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class L2Resources {
    private final Environment environment;
    private final Map<Integer, String> strings = new HashMap<>();
    private final Map<String, BufferedImage> images = new HashMap<>();
    private final Map<String, Split9> imageSplit9Info = new HashMap<>();

    public L2Resources(Environment environment) {
        this.environment = environment;
        loadStrings();
    }

    public Environment getEnvironment() {
        return environment;
    }

    private void loadStrings() {
        Collection<File> files = FileUtils.listFiles(getEnvironment().getStartDir(), new WildcardFileFilter("SysString-*.dat"), null);
        if (!files.isEmpty()) {
            File file = files.iterator().next();
            try (L2DataInputStream is = new L2DataInputStream(L2Crypt.decrypt(new FileInputStream(file), file.getName()), Charset.forName("EUC-KR"))) {
                strings.clear();
                int count = is.readInt();
                for (int i = 0; i < count; i++) {
                    strings.put(is.readInt(), is.readLine());
                }
            } catch (Exception ignore) {
            }
        }
    }

    public String getSysString(int i) {
        if (environment == null)
            return null;
        return strings.get(i);
    }

    private void loadImage(String name) throws IOException {
        if (environment == null || images.containsKey(name))
            return;

        getEnvironment().getExportEntry(name, MipMapInfo::isTexture)
                .ifPresent(texture -> MipMapInfo.getInfo(texture)
                        .ifPresent(info -> {
                            imageSplit9Info.put(name, info.properties.getSplit9());
                            try {
                                byte[] raw = texture.getObjectRawDataExternally();
                                switch (info.properties.getFormat()) {
                                    case DXT1:
                                    case DXT3:
                                    case DXT5:
                                        images.put(name, Img.DDS.createFromData(raw, info).getMipMaps()[0]);
                                        break;
                                    case RGBA8:
                                        images.put(name, Img.TGA.createFromData(raw, info).getMipMaps()[0]);
                                        break;
                                    case G16:
                                        images.put(name, Img.G16.createFromData(raw, info).getMipMaps()[0]);
                                        break;
                                    case P8:
                                        images.put(name, Img.P8.createFromData(raw, info).getMipMaps()[0]);
                                        break;
								default:
									break;
                                }
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        }));
    }

    // Retorna BufferedImage diretamente (Swing usa isso em ImageIcons ou Paint)
    public BufferedImage getImage(String name) throws IOException {
        loadImage(name);
        return images.get(name);
    }

    // Adaptação do Border para Swing
    public Border getBorder(String name) throws IOException {
        loadImage(name);

        BufferedImage img = images.get(name);
        Split9 split9 = imageSplit9Info.get(name);

        if (img == null)
            return new EmptyBorder(0, 0, 0, 0);

        if (split9 != null && split9.isSplit9Texture()) {
            // No Swing, para simular o BorderImage (Split9), usamos MatteBorder com um Icon
            // Ou uma implementação de Border customizada que desenha as 9 fatias.
            // Para simplificar agora, retornamos as insets corretas:
            Insets insets = new Insets(
                    split9.getSplit9Y1(),
                    split9.getSplit9X1(),
                    img.getHeight() - split9.getSplit9Y2(),
                    img.getWidth() - split9.getSplit9X2()
            );
            
            // Criamos um MatteBorder que usa a imagem como textura
            return new MatteBorder(insets, new ImageIcon(img));
        }

        return new MatteBorder(0, 0, 0, 0, new ImageIcon(img));
    }
}