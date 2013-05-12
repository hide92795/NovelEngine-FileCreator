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
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import javax.crypto.CipherOutputStream;

/**
 * ボイスデータを保存するセーバーです。
 * 
 * @author hide92795
 */
public class SaverVoice extends Saver {
	/**
	 * ボイスデータを保存するセーバーを生成します。
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
	public SaverVoice(File outputDir, File src, Properties crypt, String encoding) {
		super(outputDir, src, crypt, encoding);
	}

	@Override
	public void pack() throws Exception {
		HashMap<String, Integer> voices = VarNumManager.VOICE.getMap();
		Set<String> voice_s = voices.keySet();

		HashMap<String, File> inputDirs = new HashMap<String, File>();
		HashMap<String, File> outputDirs = new HashMap<String, File>();

		for (String voice : voice_s) {
			int id = voices.get(voice);
			String[] voiceData = voice.split("\\.", 2);
			String characterName = voiceData[0];
			String voiceName = voiceData[1];
			File rawDataDir;
			if (inputDirs.containsKey(characterName)) {
				rawDataDir = inputDirs.get(characterName);
			} else {
				rawDataDir = new File(getSrc(), characterName);
				inputDirs.put(characterName, rawDataDir);
				int characterId = VarNumManager.CHARACTER.add(characterName);
				File outPutDir = new File(getOutputDir(), String.valueOf(characterId));
				if (!outPutDir.exists()) {
					outPutDir.mkdir();
				}
				outputDirs.put(characterName, outPutDir);
			}
			File src = new File(rawDataDir, voiceName + ".ogg");

			System.out.println(characterName + ":" + src.getName());
			FileInputStream fis = new FileInputStream(src);

			File outputDir = outputDirs.get(characterName);
			CipherOutputStream cos = createCipherInputStream(new File(outputDir, id + ".nev"), getCryptProperties());

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
}