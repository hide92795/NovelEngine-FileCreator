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

import hide92795.novelengine.filecreator.NovelEngineFileCreator;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 各種リソースを保存するための処理を記述する抽象クラスです。
 * 
 * @author hide92795
 */
public abstract class Saver {
	/**
	 * 出力先のディレクトリを表します。
	 */
	private final File outputDir;
	/**
	 * 必要なファイルが保管されているディレクトリ、もしくはファイルを表します。
	 */
	private final File src;
	/**
	 * 暗号化に関する情報を保存するプロパティです。
	 */
	private final Properties crypt;
	/**
	 * 読み込み時に使用する文字コードです。
	 */
	private final String encoding;

	/**
	 * 出力時に必要な情報からサーバーを生成します。
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
	public Saver(File outputDir, File src, Properties crypt, String encoding) {
		this.outputDir = outputDir;
		this.src = src;
		this.crypt = crypt;
		this.encoding = encoding;
	}

	/**
	 * 各種リソースをファイルに対してパッケージングした上で出力します。
	 * 
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	public abstract void pack() throws Exception;

	/**
	 * ファイル名から拡張子を取り除きます。
	 * 
	 * @param filename
	 *            ファイル名
	 * @return 拡張子を取り除いたファイル名
	 */
	protected static String removeFileExtension(String filename) {
		int lastDotPos = filename.lastIndexOf('.');

		if (lastDotPos == -1) {
			return filename;
		} else if (lastDotPos == 0) {
			return filename;
		} else {
			return filename.substring(0, lastDotPos);
		}
	}

	/**
	 * 出力先のディレクトリを取得します。
	 * 
	 * @return 出力先のディレクトリ
	 */
	protected File getOutputDir() {
		return outputDir;
	}

	/**
	 * 必要なファイルが保管されているディレクトリ、もしくはファイルを取得します。
	 * 
	 * @return 必要なファイルが保管されているディレクトリ、もしくはファイル
	 */
	protected File getSrc() {
		return src;
	}

	/**
	 * 暗号化に関する情報を保存するプロパティを取得します。
	 * 
	 * @return 暗号化に関する情報を保存するプロパティ
	 */
	protected Properties getCryptProperties() {
		return crypt;
	}

	/**
	 * 読み込み時に使用する文字コードを取得します。
	 * 
	 * @return 読み込み時に使用する文字コード
	 */
	protected String getEncoding() {
		return encoding;
	}

	/**
	 * 指定されたファイルに対して暗号化を行いながら出力するためのストリームを生成します。
	 * 
	 * @param file
	 *            出力先のファイル
	 * @param crypt
	 *            暗号化に関する情報を保存するプロパティ
	 * @return 暗号化を行うストリーム
	 * @throws Exception
	 *             何らかのエラーが発生した場合
	 */
	protected static CipherOutputStream createCipherInputStream(File file, Properties crypt) throws Exception {
		SecretKeySpec key = new SecretKeySpec(crypt.getProperty("key").getBytes(), "AES");
		FileOutputStream fos = new FileOutputStream(file);

		Cipher cipher = Cipher.getInstance("AES/PCBC/PKCS5Padding");

		String iv = crypt.getProperty("iv");
		if (iv != null && iv.length() != 0) {
			IvParameterSpec ivspec = new IvParameterSpec(crypt.getProperty("iv").getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
		} else {
			cipher.init(Cipher.ENCRYPT_MODE, key);
		}

		if (NovelEngineFileCreator.DEBUG) {
			System.out.print("IV: ");
			byte[] b_iv = cipher.getIV();
			for (int i = 0; i < b_iv.length; i++) {
				System.out.print(Integer.toHexString(b_iv[i] & 0xff) + " ");
			}
			System.out.println();
		}


		CipherOutputStream cos = new CipherOutputStream(fos, cipher);

		fos.write(cipher.getIV());

		return cos;
	}
}
