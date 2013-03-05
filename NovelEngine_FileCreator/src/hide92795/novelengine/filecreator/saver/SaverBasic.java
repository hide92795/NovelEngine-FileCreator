package hide92795.novelengine.filecreator.saver;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;
import java.util.Properties;
import javax.imageio.ImageIO;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

public class SaverBasic extends Saver {
	private Properties property;
	private File path;

	public SaverBasic(Properties property, File output, Properties crypt, File path) {
		super(output, crypt);
		this.property = property;
		this.path = path;
	}

	public void pack() throws Exception {
		FileOutputStream fos = new FileOutputStream(new File(output, "basic.neb"));
		MessagePack messagepack = new MessagePack();
		Packer p = messagepack.createPacker(fos);

		String gamename = property.getProperty("Gamename");
		if (gamename == null) {
			throw new Exception("Gamename is null");
		}

		String version = property.getProperty("Version");
		if (version == null) {
			throw new Exception("Version is null");
		}

		int height = Integer.parseInt(property.getProperty("Height"));

		int width = Integer.parseInt(property.getProperty("Width"));

		boolean arrowResize = Boolean.parseBoolean(property.getProperty("AllowResize"));

		int[] aspectRatio = getAspectRatio(property.getProperty("AspectRatio"));

		ByteBuffer[] icons = createIconByteBuffer();

		p.write(gamename);
		p.write(version);
		p.write(height);
		p.write(width);
		p.write(arrowResize);
		p.write(aspectRatio[0]);
		p.write(aspectRatio[1]);
		p.write(icons.length);
		for (ByteBuffer byteBuffer : icons) {
			p.write(byteBuffer);
		}

		p.flush();
		p.close();
	}

	private int[] getAspectRatio(String ratio) {
		String[] string = ratio.split(":");
		int width = Integer.parseInt(string[0].trim());
		int height = Integer.parseInt(string[1].trim());
		return new int[] { width, height };
	}

	private ByteBuffer[] createIconByteBuffer() throws IOException {
		File file = new File(path, "Icon");
		File[] list = file.listFiles();
		ByteBuffer[] iconsBuffer = new ByteBuffer[list.length];
		for (int i = 0; i < list.length; i++) {
			File icon = list[i];
			BufferedImage img = ImageIO.read(icon);
			WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, img.getWidth(),
					img.getHeight(), 4, null);
			BufferedImage texImage = new BufferedImage(new ComponentColorModel(
					ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 }, true, false,
					ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE), raster, false,
					new Hashtable<Object, Object>());

			Graphics g = texImage.getGraphics();
			g.setColor(new Color(0f, 0f, 0f, 0f));
			g.fillRect(0, 0, texImage.getWidth(), texImage.getHeight());
			g.drawImage(img, 0, 0, null);
			g.dispose();
			byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();
			ByteBuffer iconByteBuffer = ByteBuffer.allocateDirect(data.length);
			iconByteBuffer.order(ByteOrder.nativeOrder());
			iconByteBuffer.put(data, 0, data.length);
			iconByteBuffer.flip();
			iconsBuffer[i] = iconByteBuffer;
		}
		return iconsBuffer;
	}

}
