//
// NovelEngine Project
//
// Copyright (C) 2013 - hide92795
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package hide92795.novelengine.filecreator.saver;

import hide92795.novelengine.filecreator.Utils;
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

/**
 * 基本データを保存するセーバーです。
 * 
 * @author hide92795
 */
public class SaverBasic extends Saver {
	/**
	 * プロジェクトデータを保管するプロパティです。
	 */
	private Properties project;

	/**
	 * 基本データを保存するためのセーバーを生成します。
	 * 
	 * @param outputDir
	 *            出力先のディレクトリ
	 * @param src
	 *            必要なファイルが保管されているディレクトリ、もしくはファイル
	 * @param crypt
	 *            暗号化に関する情報を保存するプロパティ
	 * @param encoding
	 *            読み込み時に使用する文字コード
	 * @param project
	 *            プロジェクトデータ
	 */
	public SaverBasic(File outputDir, File src, Properties crypt, String encoding, Properties project) {
		super(outputDir, src, crypt, encoding);
		this.project = project;
	}

	@Override
	public void pack() throws Exception {
		FileOutputStream fos = new FileOutputStream(new File(getOutputDir(), "basic.neb"));
		MessagePack messagepack = new MessagePack();
		Packer p = messagepack.createPacker(fos);

		String gamename = project.getProperty("Gamename");
		if (gamename == null) {
			throw new Exception("Gamename is null");
		}

		String version = project.getProperty("Version");
		if (version == null) {
			throw new Exception("Version is null");
		}

		int height = Integer.parseInt(project.getProperty("Height"));

		int width = Integer.parseInt(project.getProperty("Width"));

		boolean arrowResize = Boolean.parseBoolean(project.getProperty("AllowResize"));

		int[] aspectRatio = getAspectRatio(width, height);

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

	/**
	 * ゲーム上でのアスペクト比を算出します。
	 * 
	 * @param width
	 *            画面の横幅
	 * @param height
	 *            画面の高さ
	 * @return アスペクト比
	 */
	private int[] getAspectRatio(int width, int height) {
		int gcd = Utils.gcd(width, height);
		int asp_width = width / gcd;
		int asp_height = height / gcd;
		return new int[] { asp_width, asp_height };
	}

	/**
	 * アイコンのデータを作成します。
	 * 
	 * @return アイコンのデータを保存したバイトバッファーの配列
	 * @throws IOException
	 *             何らかの入出力エラーが発生した場合
	 */
	private ByteBuffer[] createIconByteBuffer() throws IOException {
		File file = new File(getSrc(), "Icon");
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
