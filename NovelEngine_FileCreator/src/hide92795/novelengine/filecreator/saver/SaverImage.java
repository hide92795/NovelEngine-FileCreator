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

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.crypto.CipherOutputStream;

/**
 * 画像データを保存するセーバーです。
 * 
 * @author hide92795
 */
public class SaverImage extends Saver {
	/**
	 * この画像のIDを表します。
	 */
	private final int id;

	/**
	 * 画像データを保存するセーバーを生成します。
	 * 
	 * @param outputDir
	 *            出力先のディレクトリ
	 * @param src
	 *            必要なファイルが保管されているディレクトリ、もしくはファイル
	 * @param crypt
	 *            暗号化に関する情報を保存するプロパティ
	 * @param encoding
	 *            読み込み時に使用する文字コード
	 * @param id
	 *            この画像のID
	 */
	public SaverImage(File outputDir, File src, Properties crypt, String encoding, int id) {
		super(outputDir, src, crypt, encoding);
		this.id = id;
	}

	@Override
	public void pack() throws Exception {
		System.out.println(getSrc().getName());
		FileInputStream fis = new FileInputStream(getSrc());

		CipherOutputStream cos = createCipherInputStream(new File(getOutputDir(), id + ".nei"), getCryptProperties());

		byte[] a = new byte[8];
		int i = fis.read(a);

		while (i != -1) {
			cos.write(a, 0, i);
			i = fis.read(a);
		}

		cos.flush();
		cos.close();
		fis.close();
	}
}
