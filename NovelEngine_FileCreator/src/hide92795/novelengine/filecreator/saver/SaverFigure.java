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

import hide92795.novelengine.filecreator.VarNumManager;
import hide92795.novelengine.filecreator.saver.figure.FigureSaver;
import hide92795.novelengine.filecreator.saver.figure.FigureSaverCircle;
import hide92795.novelengine.filecreator.saver.figure.FigureSaverPolygon;
import hide92795.novelengine.filecreator.saver.figure.FigureSaverQuadrangle;
import hide92795.novelengine.filecreator.saver.figure.FigureSaverTriangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.crypto.CipherOutputStream;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.yaml.snakeyaml.Yaml;

/**
 * フィギュアデータを保存するセーバーです。
 * 
 * @author hide92795
 */
public class SaverFigure extends Saver {
	/**
	 * 各フィギュアのセーバーを保存するマップです。
	 */
	private static HashMap<String, FigureSaver> figureSavers;

	static {
		figureSavers = new HashMap<String, FigureSaver>();
		figureSavers.put("三角形", new FigureSaverTriangle());
		figureSavers.put("四角形", new FigureSaverQuadrangle());
		figureSavers.put("多角形", new FigureSaverPolygon());
		figureSavers.put("円形", new FigureSaverCircle());
	}

	/**
	 * 画面全体を範囲とするフィギュアを表します。
	 */
	public static final byte FIGURE_ENTIRE_SCREEN = 0;
	/**
	 * 三角形を範囲とするフィギュアを表します。
	 */
	public static final byte FIGURE_TRIANGLE = 1;
	/**
	 * 四角形を範囲とするフィギュアを表します。
	 */
	public static final byte FIGURE_QUADANGLE = 2;
	/**
	 * 多角形を範囲とするフィギュアを表します。
	 */
	public static final byte FIGURE_POLYGON = 3;
	/**
	 * 円形を範囲とするフィギュアを表します。
	 */
	public static final byte FIGURE_CIRCLE = 4;

	/**
	 * フィギュアデータを保存するセーバーを生成します。
	 * 
	 * @param outputDir
	 *            出力先のディレクトリ
	 * @param src
	 *            必要なファイルが保管されているディレクトリ、もしくはファイル
	 * @param crypt
	 *            暗号化に関する情報を保存するプロパティ
	 * @param encoding
	 *            読み込み時に使用する文字コード
	 */
	public SaverFigure(File outputDir, File src, Properties crypt, String encoding) {
		super(outputDir, src, crypt, encoding);
	}

	@Override
	public void pack() throws Exception {
		CipherOutputStream cos = createCipherInputStream(new File(getOutputDir(), "figure.neo"), getCryptProperties());

		MessagePack msgpack = new MessagePack();
		Packer p = msgpack.createPacker(cos);
		Yaml yaml = new Yaml();

		HashMap<String, Integer> figures_map = VarNumManager.FIGURE.getMap();
		Set<String> figures_ks = figures_map.keySet();

		p.write(figures_ks.size());

		for (String figures_s : figures_ks) {
			p.write(figures_map.get(figures_s));

			File character = new File(getSrc(), figures_s + ".yml");
			BufferedReader reader_f = new BufferedReader(new InputStreamReader(new FileInputStream(character),
					getEncoding()));
			Map<?, ?> map_f = (Map<?, ?>) yaml.load(reader_f);

			String type = map_f.get("Type").toString();
			if (type != null) {
				FigureSaver figureSaver = SaverFigure.figureSavers.get(type);
				figureSaver.save(map_f, p);
			}
			reader_f.close();
		}

		p.flush();
		p.close();
	}

}
